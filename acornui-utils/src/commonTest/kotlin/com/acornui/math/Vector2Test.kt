/*
 * Copyright 2018 Nicholas Bilyk
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

import com.acornui.test.assertClose
import org.junit.Test
import kotlin.math.sqrt
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class Vector2Test {

	@Test
	fun len() {
		assertClose(3f, Vector2(3f, 0f).len())
		assertClose(3f, Vector2(0f, -3f).len())
		assertClose(sqrt(18f), Vector2(3f, -3f).len())
	}

	@Test
	fun len2() {
		assertClose(9f, Vector2(3f, 0f).len2())
		assertClose(9f, Vector2(0f, -3f).len2())
		assertClose(18f, Vector2(3f, -3f).len2())
	}

	@Test
	fun set() {
		assertEquals(Vector2(2f, 3f), Vector2().set(2f, 3f))
	}

	@Test
	fun set1() {
		assertEquals(Vector2(2f, 3f), Vector2().set(Vector2(2f, 3f)))
	}

	@Test
	fun sub() {
		assertEquals(Vector2(2f, 3f), Vector2(4f, 6f).sub(Vector2(2f, 3f)))
	}

	@Test
	fun sub1() {
		assertEquals(Vector2(2f, 3f), Vector2(4f, 6f).sub(2f, 3f))
	}

	@Test
	fun nor() {
		assertEquals(Vector2(1f, 0f), Vector2(5f, 0f).nor())
	}

	@Test
	fun add() {
		assertEquals(Vector2(10f, 11f), Vector2(5f, 4f).add(5f, 7f))
	}

	@Test
	fun add1() {
		assertEquals(Vector2(10f, 11f), Vector2(5f, 4f).add(Vector2(5f, 7f)))
	}

	@Test
	fun dot() {
		assertEquals(1f, Vector2(1f, 0f).dot(1f, 0f))
		assertEquals(-1f, Vector2(1f, 0f).dot(-1f, 0f))
		assertEquals(0f, Vector2(1f, 0f).dot(0f, 1f))

//		println(Vector2(50f, 0f).crs(100f, 100f) / Vector2(50f, 0f).len())
//		println(Vector2(10f, 50f).crs(0f, 10f) / Vector2(50f, 10f).len())
//		println(Vector2(10f, 50f).crs(5f, 1f) / Vector2(50f, 10f).len())
	}

	@Test
	fun dot1() {
		assertEquals(1f, Vector2(1f, 0f).dot(Vector2(1f, 0f)))
	}

	@Test
	fun scl() {
		assertEquals(Vector2(6f, 6f), Vector2(3f, 2f).scl(2f, 3f))
	}

	@Test
	fun scl1() {
		assertEquals(Vector2(6f, 6f), Vector2(3f, 2f).scl(Vector2(2f, 3f)))
	}

	@Test
	fun scl2() {
		assertEquals(Vector2(6f, 4f), Vector2(3f, 2f).scl(2f))
	}

	@Test
	fun dst() {
		assertEquals(4f, Vector2(0f, 0f).dst(Vector2(4f, 0f)))
	}

	@Test
	fun dst1() {
		assertEquals(4f, Vector2(0f, 0f).dst(4f, 0f))
	}

	@Test
	fun dst2() {
		assertEquals(16f, Vector2(0f, 0f).dst2(4f, 0f))
	}

	@Test
	fun manhattanDst() {
		assertEquals(5f, Vector2(0f, 0f).manhattanDst(Vector2(4f, 1f)))
	}

	@Test
	fun limit() {
		assertEquals(Vector2(1f, 0f), Vector2(3f, 0f).limit(1f))
	}

	@Test
	fun clamp() {
		assertEquals(Vector2(3f, 0f), Vector2(4f, 0f).clamp(1f, 3f))
		assertEquals(Vector2(1f, 0f), Vector2(0.5f, 0f).clamp(1f, 3f))
	}

	@Test
	fun mul() {
	}

	@Test
	fun crs() {
	}

	@Test
	fun crs1() {
	}

	@Test
	fun angle() {
	}

	@Test
	fun setAngleRad() {
	}

	@Test
	fun rotateRad() {
	}

	@Test
	fun lerp() {
	}

	@Test
	fun lerp1() {
	}

	@Test
	fun interpolate() {
	}

	@Test
	fun epsilonEquals() {
	}

	@Test
	fun epsilonEquals1() {
	}

	@Test
	fun isUnit() {
	}

	@Test
	fun isUnit1() {
	}

	@Test
	fun isZero() {
	}

	@Test
	fun isZero1() {
	}

	@Test
	fun isOnLine() {
		assertTrue(Vector2(100f, 0f).isOnLine(Vector2(50f, 0f)))
		assertTrue(Vector2(100f, 110f).isOnLine(Vector2(50f, 55f)))
	}

	@Test
	fun isOnLine1() {
	}

	@Test
	fun isCollinear() {
	}

	@Test
	fun isCollinear1() {
	}

	@Test
	fun isCollinearOpposite() {
	}

	@Test
	fun isCollinearOpposite1() {
	}

	@Test
	fun isPerpendicular() {
	}

	@Test
	fun isPerpendicular1() {
	}

	@Test
	fun hasSameDirection() {
	}

	@Test
	fun hasOppositeDirection() {
	}

	@Test
	fun ext() {
	}

	@Test
	fun clear() {
	}

	@Test
	fun free() {
	}

	@Test
	fun equals() {
	}

	@Test
	fun hashCodeTest() {
	}

	@Test
	fun toStringTest() {
	}

	@Test
	fun getX() {
	}

	@Test
	fun setX() {
	}

	@Test
	fun getY() {
	}

	@Test
	fun setY() {
	}
}