/*
 * Copyright 2015 Nicholas Bilyk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.acornui.component.layout.algorithm

import com.acornui.collection.sortTo
import com.acornui.component.ComponentInit
import com.acornui.component.layout.*
import com.acornui.component.style.StyleBase
import com.acornui.component.style.StyleType
import com.acornui.core.di.Owned
import com.acornui.math.Bounds
import com.acornui.math.MathUtils
import com.acornui.math.Pad
import com.acornui.math.PadRo
import kotlin.math.floor

class HorizontalLayout : LayoutAlgorithm<HorizontalLayoutStyle, HorizontalLayoutData> {

	override val style = HorizontalLayoutStyle()

	private val orderedElements = ArrayList<LayoutElement>()

	override fun calculateSizeConstraints(elements: List<LayoutElementRo>, out: SizeConstraints) {
		val padding = style.padding
		val gap = style.gap

		var minHeight = 0f
		var minWidth = 0f
		for (i in 0..elements.lastIndex) {
			val element = elements[i]
			val sC = element.sizeConstraints
			val iMinHeight = sC.height.min
			if (iMinHeight != null) minHeight = maxOf(iMinHeight, minHeight)
			val iMinWidth = sC.width.min
			if (iMinWidth != null) minWidth += iMinWidth
		}
		minHeight += padding.bottom + padding.top
		minWidth += gap * elements.lastIndex + padding.left + padding.right
		out.height.min = minHeight
		out.width.min = minWidth
	}

	override fun layout(explicitWidth: Float?, explicitHeight: Float?, elements: List<LayoutElement>, out: Bounds) {
		val padding = style.padding
		val gap = style.gap
		val allowRelativeSizing = style.allowRelativeSizing

		val childAvailableWidth: Float? = padding.reduceWidth(explicitWidth)
		val childAvailableHeight: Float? = padding.reduceHeight(explicitHeight)

		if (allowRelativeSizing) elements.sortTo(orderedElements, true, sizeOrderComparator)
		else orderedElements.addAll(elements)

		// Following the sizing precedence, size the children, maxing the maxHeight by the measured height if
		// allowRelativeSizing is true.
		var maxHeight = childAvailableHeight
		var inflexibleWidth = 0f
		var flexibleWidth = 0f
		for (i in 0..orderedElements.lastIndex) {
			val element = orderedElements[i]
			val layoutData = element.layoutDataCast
			if (childAvailableWidth == null || layoutData?.widthPercent == null) {
				val w = layoutData?.getPreferredWidth(childAvailableWidth)
				val h = layoutData?.getPreferredHeight(maxHeight)
				element.setSize(w, h)
				inflexibleWidth += element.width
				if (allowRelativeSizing && (maxHeight == null || element.height > maxHeight))
					maxHeight = element.height
			} else {
				flexibleWidth += layoutData.widthPercent!! * childAvailableWidth
			}
			inflexibleWidth += gap
		}
		inflexibleWidth -= gap

		// Size flexible elements within the remaining space.
		if (childAvailableWidth != null) {
			val scale = if (flexibleWidth > 0) MathUtils.clamp((childAvailableWidth - inflexibleWidth) / flexibleWidth, 0f, 1f) else 1f
			for (i in 0..orderedElements.lastIndex) {
				val element = orderedElements[i]
				val layoutData = element.layoutDataCast
				if (layoutData?.widthPercent != null) {
					val h = layoutData.getPreferredHeight(maxHeight)
					val w = scale * layoutData.widthPercent!! * childAvailableWidth
					element.setSize(w, h)
					if (allowRelativeSizing && (maxHeight == null || element.height > maxHeight))
						maxHeight = element.height
				}
			}
		}

		if (maxHeight == null)
			maxHeight = 0f
		orderedElements.clear()

		// Position
		var x = padding.left
		if (childAvailableWidth != null && style.horizontalAlign != HAlign.LEFT) {
			val d = childAvailableWidth - (inflexibleWidth + flexibleWidth)
			if (d > 0f) {
				if (style.horizontalAlign == HAlign.RIGHT) {
					x += d
				} else if (style.horizontalAlign == HAlign.CENTER) {
					x += floor(d * 0.5f).toInt()
				}
			}
		}
		for (i in 0..elements.lastIndex) {
			val element = elements[i]
			val layoutData = element.layoutDataCast
			val y = when (layoutData?.verticalAlign ?: style.verticalAlign) {
				VAlign.TOP -> padding.top
				VAlign.MIDDLE -> (maxHeight - element.height) * 0.5f + padding.top
				VAlign.BOTTOM -> maxHeight - element.height + padding.top
				else -> throw Exception()
			}
			element.moveTo(x, y)
			x += element.width + gap
		}
		x += padding.right - gap
		out.set(x, maxHeight + padding.bottom + padding.top)
	}

	override fun createLayoutData() = HorizontalLayoutData()

	companion object {
		private val sizeOrderComparator = {
			o1: LayoutElement, o2: LayoutElement ->
			val layoutData1 = o1.layoutData as HorizontalLayoutData?
			val layoutData2 = o2.layoutData as HorizontalLayoutData?
			val r1 = (layoutData1?.priority ?: 0f).compareTo(layoutData2?.priority ?: 0f)
			if (r1 == 0) {
				(layoutData1?.heightPercent == null).compareTo(layoutData2?.heightPercent == null)
			} else r1
		}
	}

}

class HorizontalLayoutStyle : StyleBase() {

	override val type: StyleType<HorizontalLayoutStyle> = Companion

	/**
	 * The horizontal gap between elements.
	 */
	var gap by prop(5f)

	/**
	 * The Padding object with left, bottom, top, and right padding.
	 */
	var padding: PadRo by prop(Pad())

	/**
	 * The horizontal alignment of the entire row within the explicit width.
	 * If the explicit width is null, this will have no effect.
	 */
	var horizontalAlign by prop(HAlign.LEFT)

	/**
	 * The vertical alignment of each element within the measured height.
	 */
	var verticalAlign by prop(VAlign.BOTTOM)

	/**
	 * If true, the actual size of an element can expand the bounds of the layout for the remaining elements, based
	 * on priority rules.
	 *
	 * Example:
	 *
	 * ```
	 * hGroup {
	 *    +rect() layout { heightPercent = 1f }   // height = if (allowRelativeSizing == true) 200f else 100f.
	 *    +rect() layout { height = 200f }
	 * } layout { height = 100f }
	 * ```
	 *
	 * @see HorizontalLayoutData.priority
	 */
	var allowRelativeSizing by prop(true)

	companion object : StyleType<HorizontalLayoutStyle>

}

class HorizontalLayoutData : BasicLayoutData() {

	/**
	 * If set, the vertical alignment for this item overrides the vertical layout's verticalAlign.
	 */
	var verticalAlign: HAlign? by bindable(null)

	/**
	 * The order of sizing precedence is as follows:
	 * - widthPercent null (inflexible width before flexible width)
	 * - priority value (higher values before lower values)
	 * - heightPercent null (inflexible height before flexible height)
	 */
	var priority: Float by bindable(0f)
}

open class HorizontalLayoutContainer(owner: Owned) : ElementLayoutContainerImpl<HorizontalLayoutStyle, HorizontalLayoutData>(owner, HorizontalLayout())

fun Owned.hGroup(init: ComponentInit<HorizontalLayoutContainer> = {}): HorizontalLayoutContainer {
	val horizontalGroup = HorizontalLayoutContainer(this)
	horizontalGroup.init()
	return horizontalGroup
}