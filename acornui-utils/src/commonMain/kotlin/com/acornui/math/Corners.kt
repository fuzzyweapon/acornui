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

package com.acornui.math

import com.acornui.recycle.Clearable
import com.acornui.serialization.*

interface CornersRo {

	val topLeft: Vector2Ro
	val topRight: Vector2Ro
	val bottomRight: Vector2Ro
	val bottomLeft: Vector2Ro

	fun isEmpty(): Boolean {
		return topLeft.isZero() && topRight.isZero() && bottomRight.isZero() && bottomLeft.isZero()
	}

	fun copy(topLeft: Vector2Ro = this.topLeft, topRight: Vector2Ro = this.topRight, bottomRight: Vector2Ro = this.bottomRight, bottomLeft: Vector2Ro = this.bottomLeft): Corners {
		return Corners(topLeft.copy(), topRight.copy(), bottomRight.copy(), bottomLeft.copy())
	}

}

/**
 * A representation of corner radii.
 *
 * @author nbilyk
 */
class Corners() : CornersRo, Clearable {

	override val topLeft = Vector2()
	override val topRight = Vector2()
	override val bottomRight = Vector2()
	override val bottomLeft = Vector2()

	constructor(topLeft: Vector2Ro = Vector2(),
				topRight: Vector2Ro = Vector2(),
				bottomRight: Vector2Ro = Vector2(),
				bottomLeft: Vector2Ro = Vector2()) : this() {
		set(topLeft, topRight, bottomRight, bottomLeft)
	}

	constructor(all: Float) : this() {
		set(all)
	}

	constructor(topLeft: Float, topRight: Float, bottomRight: Float, bottomLeft: Float) : this() {
		set(topLeft, topRight, bottomRight, bottomLeft)
	}

	fun set(all: Float): Corners {
		topLeft.set(all, all)
		topRight.set(all, all)
		bottomRight.set(all, all)
		bottomLeft.set(all, all)
		return this
	}

	fun set(other: CornersRo): Corners {
		topLeft.set(other.topLeft)
		topRight.set(other.topRight)
		bottomRight.set(other.bottomRight)
		bottomLeft.set(other.bottomLeft)
		return this
	}

	fun set(topLeft: Float = 0f, topRight: Float = 0f, bottomRight: Float = 0f, bottomLeft: Float = 0f): Corners {
		this.topLeft.set(topLeft, topLeft)
		this.topRight.set(topRight, topRight)
		this.bottomRight.set(bottomRight, bottomRight)
		this.bottomLeft.set(bottomLeft, bottomLeft)
		return this
	}

	fun set(topLeft: Vector2Ro, topRight: Vector2Ro, bottomRight: Vector2Ro, bottomLeft: Vector2Ro): Corners {
		this.topLeft.set(topLeft)
		this.topRight.set(topRight)
		this.bottomRight.set(bottomRight)
		this.bottomLeft.set(bottomLeft)
		return this
	}

	/**
	 * Decreases the corner radius by the given padding.
	 */
	fun deflate(pad: PadRo): Corners = inflate(-pad.left, -pad.top, -pad.right, -pad.bottom)
	fun deflate(left: Float, top: Float, right: Float, bottom: Float): Corners = inflate(-left, -top, -right, -bottom)
	fun deflate(all: Float): Corners = inflate(-all, -all, -all, -all)

	/**
	 * Increases the corner radius by the given padding.
	 */
	fun inflate(pad: PadRo): Corners = inflate(pad.left, pad.top, pad.right, pad.bottom)

	/**
	 * Increases all dimensions of the corner radius by the given amount.
	 */
	fun inflate(all: Float): Corners = inflate(all, all, all, all)

	fun inflate(left: Float, top: Float, right: Float, bottom: Float): Corners {
		topLeft.x += left
		topLeft.y += top
		topRight.x += right
		topRight.y += top
		bottomRight.x += right
		bottomRight.y += bottom
		bottomLeft.x += left
		bottomLeft.y += bottom
		return this
	}

	override fun clear() {
		set(0f)
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is CornersRo) return false
		if (topLeft != other.topLeft) return false
		if (topRight != other.topRight) return false
		if (bottomRight != other.bottomRight) return false
		if (bottomLeft != other.bottomLeft) return false

		return true
	}

	override fun hashCode(): Int {
		var result = topLeft.hashCode()
		result = 31 * result + topRight.hashCode()
		result = 31 * result + bottomRight.hashCode()
		result = 31 * result + bottomLeft.hashCode()
		return result
	}

	override fun toString(): String {
		return "Corners(topLeft=$topLeft, topRight=$topRight, bottomRight=$bottomRight, bottomLeft=$bottomLeft)"
	}


}

object CornersSerializer : To<CornersRo>, From<Corners> {
	override fun read(reader: Reader): Corners {
		val c = Corners(
				topLeft = reader.vector2("topLeft")!!,
				topRight = reader.vector2("topRight")!!,
				bottomRight = reader.vector2("bottomRight")!!,
				bottomLeft = reader.vector2("bottomLeft")!!
		)
		return c
	}

	override fun CornersRo.write(writer: Writer) {
		writer.vector2("topLeft", topLeft)
		writer.vector2("topRight", topRight)
		writer.vector2("bottomRight", bottomRight)
		writer.vector2("bottomLeft", bottomLeft)
	}
}