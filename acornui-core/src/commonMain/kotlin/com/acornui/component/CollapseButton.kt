/*
 * Copyright 2017 Nicholas Bilyk
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

package com.acornui.component

import com.acornui.component.style.StyleTag
import com.acornui.core.di.Owned

open class CollapseButton(
		owner: Owned
) : Button(owner) {

	init {
		styleTags.add(CollapseButton)
		toggleOnClick = true
	}

	companion object : StyleTag
}

fun Owned.collapseButton(init: ComponentInit<CollapseButton> = {}): CollapseButton {
	val c = CollapseButton(this)
	c.init()
	return c
}

fun Owned.collapseButton(label: String, init: ComponentInit<CollapseButton> = {}): CollapseButton {
	val b = CollapseButton(this)
	b.label = label
	b.init()
	return b
}