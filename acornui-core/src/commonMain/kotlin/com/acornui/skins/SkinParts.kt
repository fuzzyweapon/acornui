/*
 * Copyright 2019 Poly Forest, LLC
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

package com.acornui.skins

import com.acornui.component.*
import com.acornui.component.layout.algorithm.CanvasLayoutContainer
import com.acornui.component.layout.algorithm.canvas
import com.acornui.core.di.Owned
import com.acornui.core.di.inject
import com.acornui.graphic.Color
import com.acornui.math.*

interface SkinPartFactory {

	fun iconButtonSkin(buttonState: ButtonState, icon: String, padding: PadRo = Pad(5f), hGap: Float = 4f): Owned.() -> UiComponent
	fun labelButtonSkin(theme: Theme, buttonState: ButtonState): Owned.() -> UiComponent
	fun tabButtonSkin(theme: Theme, buttonState: ButtonState): Owned.() -> UiComponent

	/**
	 * A convenience function to create a button skin part.
	 */
	fun iconButtonSkin(theme: Theme, buttonState: ButtonState): Owned.() -> UiComponent

	fun checkboxNoLabelSkin(theme: Theme, buttonState: ButtonState): Owned.() -> UiComponent

	/**
	 * A checkbox skin part.
	 */
	fun checkboxSkin(theme: Theme, buttonState: ButtonState): Owned.() -> UiComponent

	/**
	 * A checkbox skin part.
	 */
	fun collapseButtonSkin(theme: Theme, buttonState: ButtonState): Owned.() -> UiComponent

	/**
	 * A convenience function to create a radio button skin part.
	 */
	fun radioButtonSkin(theme: Theme, buttonState: ButtonState): Owned.() -> UiComponent

	fun Owned.buttonTexture(buttonState: ButtonState): UiComponent
	fun Owned.buttonTexture(buttonState: ButtonState, borderRadius: CornersRo, borderThickness: PadRo, isTab: Boolean = false): CanvasLayoutContainer

}

open class BasicSkinPartFactory : SkinPartFactory {

	override fun iconButtonSkin(buttonState: ButtonState, icon: String, padding: PadRo, hGap: Float): Owned.() -> UiComponent = {
		val texture = buttonTexture(buttonState)
		val skinPart = IconButtonSkinPart(this, texture, padding, hGap)
		val theme = inject(Theme)
		skinPart.contentsAtlas(theme.atlasPath, icon)
		skinPart
	}

	override fun labelButtonSkin(theme: Theme, buttonState: ButtonState): Owned.() -> UiComponent = {
		val texture = buttonTexture(buttonState)
		LabelButtonSkinPart(this, texture, theme.buttonPad)
	}

	override fun tabButtonSkin(theme: Theme, buttonState: ButtonState): Owned.() -> UiComponent = {
		val texture = buttonTexture(buttonState, Corners(topLeft = theme.borderRadius, topRight = theme.borderRadius, bottomLeft = 0f, bottomRight = 0f), Pad(theme.strokeThickness), isTab = true)
		IconButtonSkinPart(this, texture, theme.buttonPad, theme.iconButtonGap)
	}

	/**
	 * A convenience function to create a button skin part.
	 */
	override fun iconButtonSkin(theme: Theme, buttonState: ButtonState): Owned.() -> UiComponent = {
		val texture = buttonTexture(buttonState)
		IconButtonSkinPart(this, texture, theme.buttonPad, theme.iconButtonGap)
	}

	override fun checkboxNoLabelSkin(theme: Theme, buttonState: ButtonState): Owned.() -> CheckboxSkinPart = {
		val s = checkboxSkin(theme, buttonState)()
		val lD = s.createLayoutData()
		lD.widthPercent = 1f
		lD.heightPercent = 1f
		s.box.layoutData = lD
		s
	}

	/**
	 * A checkbox skin part.
	 */
	override fun checkboxSkin(theme: Theme, buttonState: ButtonState): Owned.() -> CheckboxSkinPart = {
		val box = buttonTexture(buttonState, borderRadius = Corners(), borderThickness = Pad(theme.strokeThickness))
		if (buttonState.toggled) {
			box.addElement(rect {
				style.backgroundColor = theme.iconColor
				style.margin = Pad(1f + theme.strokeThickness)
				layoutData = box.createLayoutData().apply {
					fill()
				}
			})
		} else if (buttonState == ButtonState.DOWN) {
			box.addElement(rect {
				style.backgroundColor = theme.iconColor
				style.margin = Pad(2f + theme.strokeThickness)
				layoutData = box.createLayoutData().apply {
					fill()
				}
			})
		}
		CheckboxSkinPart(
				this,
				box
		).apply {
			box layout {
				width = 18f
				height = 18f
			}
		}
	}

	/**
	 * A checkbox skin part.
	 */
	override fun collapseButtonSkin(theme: Theme, buttonState: ButtonState): Owned.() -> CheckboxSkinPart = {
		val box = atlas(theme.atlasPath, if (buttonState.toggled) "CollapseSelected" else "CollapseUnselected")
		CheckboxSkinPart(
				this,
				box
		)
	}

	/**
	 * A convenience function to create a radio button skin part.
	 */
	override fun radioButtonSkin(theme: Theme, buttonState: ButtonState): Owned.() -> CheckboxSkinPart = {
		val radio = buttonTexture(buttonState, borderRadius = Corners(1000f), borderThickness = Pad(theme.strokeThickness))
		if (buttonState.toggled) {
			val filledCircle = rect {
				style.margin = Pad(4f)
				style.borderRadii = Corners(1000f)
				style.backgroundColor = Color.DARK_GRAY.copy()
				layoutData = radio.createLayoutData().apply {
					fill()
				}
			}
			radio.addElement(filledCircle)
		}

		CheckboxSkinPart(
				this,
				radio
		).apply {
			radio layout {
				width = 18f
				height = 18f
			}
		}
	}

	override fun Owned.buttonTexture(buttonState: ButtonState): UiComponent = stack {
		val theme = inject(Theme)
		val fillRegion = when (buttonState) {
			ButtonState.TOGGLED_UP, ButtonState.UP -> "Button_up"
			ButtonState.TOGGLED_OVER, ButtonState.OVER -> "Button_over"
			ButtonState.TOGGLED_DOWN, ButtonState.DOWN -> "Button_down"
			ButtonState.DISABLED -> "Button_disabled"
		}
		+atlas(theme.atlasPath, fillRegion) {
			colorTint = when (buttonState) {
				ButtonState.DISABLED -> theme.fillDisabled
				ButtonState.UP, ButtonState.TOGGLED_UP -> theme.fill
				ButtonState.OVER, ButtonState.TOGGLED_OVER -> theme.fillHighlight
				ButtonState.DOWN, ButtonState.TOGGLED_DOWN -> theme.fill
			}

		} layout { fill() }
		+atlas(theme.atlasPath, "CurvedStroke") {
			colorTint = if (buttonState == ButtonState.DISABLED) theme.strokeDisabled else if (buttonState.toggled) theme.strokeToggled else theme.stroke
		} layout { fill() }
	}

	override fun Owned.buttonTexture(buttonState: ButtonState, borderRadius: CornersRo, borderThickness: PadRo, isTab: Boolean): CanvasLayoutContainer = canvas {
		val theme = inject(Theme)
		+rect {
			style.apply {
				backgroundColor = theme.getButtonFillColor(buttonState)
				borderColors = BorderColors(theme.getButtonStrokeColor(buttonState))
				val bT = borderThickness.copy()
				if (isTab && buttonState.toggled) {
					bT.bottom = 0f
				}
				this.borderThicknesses = bT
				this.borderRadii = borderRadius
			}
		} layout { fill() }
		when (buttonState) {
			ButtonState.UP,
			ButtonState.OVER,
			ButtonState.TOGGLED_UP,
			ButtonState.TOGGLED_OVER -> {
				+rect {
					style.apply {
						margin = Pad(top = borderThickness.top, right = borderThickness.right, bottom = 0f, left = borderThickness.left)
						backgroundColor = theme.fillShine
						this.borderRadii = Corners(
								topLeft = Vector2(borderRadius.topLeft.x - borderThickness.left, borderRadius.topLeft.y - borderThickness.top),
								topRight = Vector2(borderRadius.topRight.x - borderThickness.right, borderRadius.topRight.y - borderThickness.top),
								bottomLeft = Vector2(), bottomRight = Vector2()
						)
					}
				} layout {
					widthPercent = 1f
					heightPercent = 0.5f
				}
			}
			ButtonState.DISABLED -> {
			}
			else -> {
				+rect {
					style.apply {
						margin = Pad(top = 0f, right = borderThickness.right, bottom = borderThickness.bottom, left = borderThickness.left)
						backgroundColor = theme.fillShine
						this.borderRadii = Corners(
								topLeft = Vector2(), topRight = Vector2(),
								bottomLeft = Vector2(borderRadius.bottomLeft.x - borderThickness.left, borderRadius.bottomLeft.y - borderThickness.bottom),
								bottomRight = Vector2(borderRadius.bottomRight.x - borderThickness.right, borderRadius.bottomRight.y - borderThickness.bottom)
						)
					}
				} layout {
					widthPercent = 1f
					verticalCenter = 0f
					bottom = 0f
				}
			}
		}
	}

}

