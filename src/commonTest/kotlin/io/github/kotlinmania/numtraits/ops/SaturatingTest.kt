// port-lint: tests src/ops/saturating.rs
package io.github.kotlinmania.numtraits.ops

import kotlin.test.Test
import kotlin.test.assertEquals

class SaturatingTest {
    @Test
    fun testSaturatingTraits() {
        assertEquals(UByte.MAX_VALUE, UByte.MAX_VALUE.saturatingAdd(1u))
        assertEquals(Byte.MAX_VALUE, Byte.MAX_VALUE.saturatingAdd(1))
        assertEquals(Byte.MIN_VALUE, Byte.MIN_VALUE.saturatingSub(1))
        assertEquals(0.toUByte(), 0.toUByte().saturatingSub(1u))
        assertEquals(UByte.MAX_VALUE, UByte.MAX_VALUE.saturatingMul(2u))
        assertEquals(Byte.MAX_VALUE, 100.toByte().saturatingMul(2))
        assertEquals(Byte.MIN_VALUE, (-100).toByte().saturatingMul(2))
        assertEquals(Long.MAX_VALUE, Long.MAX_VALUE.saturatingAdd(1L))
        assertEquals(Long.MIN_VALUE, Long.MIN_VALUE.saturatingSub(1L))
        assertEquals(Long.MAX_VALUE, Long.MAX_VALUE.saturatingMul(2L))
        assertEquals(ULong.MAX_VALUE, ULong.MAX_VALUE.saturatingAdd(1uL))
        assertEquals(0uL, 0uL.saturatingSub(1uL))
        assertEquals(ULong.MAX_VALUE, ULong.MAX_VALUE.saturatingMul(2uL))
    }
}
