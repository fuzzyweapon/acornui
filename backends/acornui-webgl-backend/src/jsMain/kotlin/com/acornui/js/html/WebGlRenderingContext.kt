/*
 * Derived from com.google.gwt.webgl.client.WebGLRenderingContext by Nicholas Bilyk 2015
 * Copyright 2010 Google Inc.
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
 *
 * Documentation derived from http://www.javascripture.com/WebGLRenderingContext
 *     http://creativecommons.org/licenses/by-sa/2.5/
 */

package com.acornui.js.html

import org.khronos.webgl.WebGLContextAttributes
import org.khronos.webgl.WebGLRenderingContext
import org.w3c.dom.HTMLCanvasElement

object WebGl {

	/**
	 * Returns a WebGLRenderingContext for the given canvas element. Returns null if no 3d context is available.
	 */
	fun getContext(canvas: HTMLCanvasElement, attributes: WebGLContextAttributes = WebGLContextAttributes()): WebGLRenderingContext? {
		val names = arrayOf("webgl", "experimental-webgl", "moz-webgl", "webkit-webgl", "webkit-3d")
		for (i in 0..names.lastIndex) {
			val context = canvas.getContext(names[i], attributes)
			if (context != null) {
				return context as WebGLRenderingContext
			}
		}
		return null
	}

}