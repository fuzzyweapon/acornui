/*
 * Copyright 2018 Poly Forest
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

package com.acornui.component.text

import com.acornui.core.mvc.Command
import com.acornui.core.mvc.CommandGroup
import com.acornui.core.mvc.CommandType
import com.acornui.core.mvc.StateCommand

class ReplaceTextRangeCommand(
		val target: Any?,
		val startIndex: Int,
		val oldText: String,
		val newText: String,
		override val group: CommandGroup?
) : StateCommand {

	override val type = Companion

	val endIndex: Int
		get() = startIndex + oldText.length

	override fun reverse(): Command {
		return ReplaceTextRangeCommand(target, startIndex, newText, oldText, group)
	}

	companion object : CommandType<ReplaceTextRangeCommand>
}
