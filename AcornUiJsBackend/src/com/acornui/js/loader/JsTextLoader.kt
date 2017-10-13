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

package com.acornui.js.loader

/**
 * @author nbilyk
 */
import com.acornui.core.UserInfo
import com.acornui.core.assets.AssetLoader
import com.acornui.core.assets.AssetType
import com.acornui.core.assets.AssetTypes
import com.acornui.core.request.Request
import com.acornui.core.request.UrlRequestData
import com.acornui.js.io.JsTextRequest

/**
 * An asset loader for text.
 * @author nbilyk
 */
class JsTextLoader(
		override val path: String,
		override val estimatedBytesTotal: Int = 0,
		private val request: Request<String> = JsTextRequest(UrlRequestData(path))
) : AssetLoader<String> {

	override val type: AssetType<String> = AssetTypes.TEXT

	override val secondsLoaded: Float
		get() = request.secondsLoaded

	override val secondsTotal: Float
		get() = if (request.secondsTotal <= 0f) estimatedBytesTotal * UserInfo.downBpsInv else request.secondsTotal

	suspend override fun await(): String = request.await()

	override fun cancel() = request.cancel()
}