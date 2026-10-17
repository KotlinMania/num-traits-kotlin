// port-lint: tests src/ops/mul_add.rs
package io.github.kotlinmania.numtraits.ops

import kotlin.test.Test
import kotlin.test.assertEquals

class MulAddTest {
    @Test
    fun mulAddInteger() {
        assertEquals(6.toByte(), 2.toByte().mulAdd(2.toByte(), 2.toByte()))
        assertEquals(6.toShort(), 2.toShort().mulAdd(2.toShort(), 2.toShort()))
        assertEquals(6, 2.mulAdd(2, 2))
        assertEquals(6L, 2L.mulAdd(2L, 2L))
        assertEquals(6.toUByte(), 2.toUByte().mulAdd(2.toUByte(), 2.toUByte()))
        assertEquals(6.toUShort(), 2.toUShort().mulAdd(2.toUShort(), 2.toUShort()))
        assertEquals(6u, 2u.mulAdd(2u, 2u))
        assertEquals(6uL, 2uL.mulAdd(2uL, 2uL))
    }

    @Test
    fun mulAddFloat() {
        val x = 10.0f
        val a = 4.0f
        val b = 60.0f
        assertEquals(100.0f, x.mulAdd(a, b))

        val xd = 10.0
        val ad = 4.0
        val bd = 60.0
        assertEquals(100.0, xd.mulAdd(ad, bd))
    }
}
