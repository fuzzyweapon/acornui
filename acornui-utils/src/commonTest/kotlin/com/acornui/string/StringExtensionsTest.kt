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

package com.acornui.string

import com.acornui.core.replaceTokens
import com.acornui.core.split2
import org.junit.*
import kotlin.test.*
import com.acornui.test.*

/**
 * @author nbilyk
 */
class StringExtensionsTest {

	@Test fun testTokenReplace() {
		assertEquals("alpha, beta, gamma", "{0}, {1}, {2}".replaceTokens("alpha", "beta", "gamma"))
		assertEquals("beta, alpha, alpha, beta, gamma", "{1}, {0}, {0}, {1}, {2}".replaceTokens("alpha", "beta", "gamma"))
	}

	@Test fun testSplit2() {
		assertListEquals(arrayOf("one", "two", "three"), "one,two,three".split2(','))
		assertListEquals(arrayOf("one", "two", "three"), """one\two\three""".split2("\\"))
		assertListEquals(arrayOf("one", "", "two", "", "three"), """one\\two\\three""".split2("\\"))
		assertListEquals(arrayOf("one", "two", "bbthree"), """oneaabbtwoaabbbbthree""".split2("aabb"))
	}
}