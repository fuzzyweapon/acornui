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

package com.acornui.js.text

import com.acornui.collection.copy
import com.acornui.core.i18n.I18n
import com.acornui.core.i18n.Locale
import com.acornui.core.text.NumberFormatType
import com.acornui.core.text.NumberFormatter
import com.acornui.core.userInfo
import com.acornui.reflect.observable
import kotlin.properties.ReadWriteProperty

class NumberFormatterImpl() : NumberFormatter {

	override var type by watched(NumberFormatType.NUMBER)
	override var locales: List<Locale>? by watched(null)
	override var minIntegerDigits: Int by watched(1)
	override var maxIntegerDigits: Int by watched(40)
	override var minFractionDigits: Int by watched(0)
	override var maxFractionDigits: Int by watched(3)
	override var useGrouping: Boolean by watched(true)

	override var currencyCode: String by watched("USD")

	private var lastLocales: List<Locale> = listOf()
	private var formatter: dynamic = null

	override fun format(value: Number?): String {
		if (value == null) return ""
		if (locales == null && lastLocales != userInfo.currentLocale.value) {
			formatter = null
			lastLocales = userInfo.currentLocale.value.copy()
		}
		if (formatter == null) {
			val locales = (locales ?: lastLocales).map { it.value }
			val JsNumberFormat = js("Intl.NumberFormat")
			val options = js("({})")

			options.minimumIntegerDigits = minIntegerDigits
			options.maximumIntegerDigits = maxIntegerDigits
			options.minimumFractionDigits = minFractionDigits
			options.maximumFractionDigits = maxFractionDigits
			options.useGrouping = useGrouping
			if (type == NumberFormatType.CURRENCY) {
				options.style = "currency"
				options.currency = currencyCode
			} else if (type == NumberFormatType.PERCENT) {
				options.style = "percent"
			}

			formatter = JsNumberFormat(locales.toTypedArray(), options)
		}
		return formatter!!.format(value)
	}

	private fun <T> watched(initial: T): ReadWriteProperty<Any?, T> {
		return observable(initial) {
			formatter = null
		}
	}
}