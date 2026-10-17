// port-lint: tests src/ops/overflowing.rs
package io.github.kotlinmania.numtraits.ops

import kotlin.test.Test
import kotlin.test.assertEquals

class OverflowingTest {
    @Test
    fun testOverflowingTraits() {
        assertEquals(OverflowingByte(Byte.MIN_VALUE, true), Byte.MAX_VALUE.overflowingAdd(1))
        assertEquals(OverflowingByte(5, false), 2.toByte().overflowingAdd(3))
        assertEquals(OverflowingByte(Byte.MAX_VALUE, true), Byte.MIN_VALUE.overflowingSub(1))
        assertEquals(OverflowingByte(2, false), 5.toByte().overflowingSub(3))
        assertEquals(OverflowingByte((-2).toByte(), true), Byte.MAX_VALUE.overflowingMul(2))
        assertEquals(OverflowingByte(6, false), 2.toByte().overflowingMul(3))
        assertEquals(OverflowingUByte(0u, true), UByte.MAX_VALUE.overflowingAdd(1u))
        assertEquals(OverflowingUByte(UByte.MAX_VALUE, true), 0.toUByte().overflowingSub(1u))
        assertEquals(OverflowingInt(Int.MIN_VALUE, true), Int.MAX_VALUE.overflowingAdd(1))
        assertEquals(OverflowingLong(Long.MIN_VALUE, true), Long.MAX_VALUE.overflowingAdd(1L))
    }
}
