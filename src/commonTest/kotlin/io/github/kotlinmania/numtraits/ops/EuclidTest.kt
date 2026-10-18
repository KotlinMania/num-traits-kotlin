// port-lint: tests src/ops/euclid.rs
package io.github.kotlinmania.numtraits.ops

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class EuclidTest {
    @Test
    fun euclidUnsigned() {
        val x = 7u
        val y = 4u
        assertEquals(1u, x.divEuclid(y))
        assertEquals(3u, x.remEuclid(y))
        assertEquals(DivRemUInt(1u, 3u), x.divRemEuclid(y))
        assertNull(x.checkedDivEuclid(0u))
        assertNull(x.checkedRemEuclid(0u))
        assertNull(x.checkedDivRemEuclid(0u))
    }

    @Test
    fun euclidSigned() {
        val a = 7
        val b = 4
        assertEquals(1, a.divEuclid(b))
        assertEquals(-1, a.divEuclid(-b))
        assertEquals(-2, (-a).divEuclid(b))
        assertEquals(2, (-a).divEuclid(-b))

        assertEquals(3, a.remEuclid(b))
        assertEquals(3, a.remEuclid(-b))
        assertEquals(1, (-a).remEuclid(b))
        assertEquals(1, (-a).remEuclid(-b))

        assertEquals(DivRemInt(-2, 1), (-a).divRemEuclid(b))
        assertNull(Int.MIN_VALUE.checkedDivEuclid(-1))
        assertNull(Int.MIN_VALUE.checkedRemEuclid(-1))
        assertNull(Int.MIN_VALUE.checkedDivRemEuclid(-1))
    }

    @Test
    fun euclidFloat() {
        val a = 7.0
        val b = 4.0
        assertEquals(1.0, a.divEuclid(b))
        assertEquals(-1.0, a.divEuclid(-b))
        assertEquals(-2.0, (-a).divEuclid(b))
        assertEquals(2.0, (-a).divEuclid(-b))

        assertEquals(3.0, a.remEuclid(b))
        assertEquals(3.0, a.remEuclid(-b))
        assertEquals(1.0, (-a).remEuclid(b))
        assertEquals(1.0, (-a).remEuclid(-b))
    }

    @Test
    fun euclidChecked() {
        assertNull(Byte.MIN_VALUE.checkedDivEuclid((-1).toByte()))
        assertNull(Byte.MIN_VALUE.checkedRemEuclid((-1).toByte()))
        assertNull(1.toByte().checkedDivEuclid(0.toByte()))
        assertNull(1.toByte().checkedRemEuclid(0.toByte()))

        assertNull(Short.MIN_VALUE.checkedDivEuclid((-1).toShort()))
        assertNull(Short.MIN_VALUE.checkedRemEuclid((-1).toShort()))
        assertNull(1.toShort().checkedDivEuclid(0.toShort()))
        assertNull(1.toShort().checkedRemEuclid(0.toShort()))

        assertNull(Int.MIN_VALUE.checkedDivEuclid(-1))
        assertNull(Int.MIN_VALUE.checkedRemEuclid(-1))
        assertNull(1.checkedDivEuclid(0))
        assertNull(1.checkedRemEuclid(0))

        assertNull(Long.MIN_VALUE.checkedDivEuclid(-1L))
        assertNull(Long.MIN_VALUE.checkedRemEuclid(-1L))
        assertNull(1L.checkedDivEuclid(0L))
        assertNull(1L.checkedRemEuclid(0L))
    }
}
