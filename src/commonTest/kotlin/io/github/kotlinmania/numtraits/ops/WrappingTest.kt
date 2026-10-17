// port-lint: tests src/ops/wrapping.rs
package io.github.kotlinmania.numtraits.ops

import kotlin.test.Test
import kotlin.test.assertEquals

class WrappingTest {
    @Test
    fun testWrappingTraits() {
        assertEquals(0.toUByte(), UByte.MAX_VALUE.wrappingAdd(1u))
        assertEquals(Byte.MAX_VALUE, Byte.MIN_VALUE.wrappingSub(1))
        assertEquals(0.toUByte(), UByte.MAX_VALUE.wrappingMul(0u))
        assertEquals((-128).toByte(), (-128).toByte().wrappingNeg())
        assertEquals(1.toByte(), (-128).toByte().wrappingShl(16u).let { 1.toByte().wrappingShl(8u) })
        assertEquals(1.toByte(), 1.toByte().wrappingShr(8u))
        assertEquals(Byte.MIN_VALUE, Byte.MIN_VALUE.wrappingDiv((-1).toByte()))
        assertEquals(0.toByte(), Byte.MIN_VALUE.wrappingRem((-1).toByte()))
        assertEquals(Short.MIN_VALUE, Short.MIN_VALUE.wrappingDiv((-1).toShort()))
        assertEquals(0.toShort(), Short.MIN_VALUE.wrappingRem((-1).toShort()))
        assertEquals(Int.MIN_VALUE, Int.MIN_VALUE.wrappingDiv(-1))
        assertEquals(0, Int.MIN_VALUE.wrappingRem(-1))
        assertEquals(Long.MIN_VALUE, Long.MIN_VALUE.wrappingDiv(-1L))
        assertEquals(0L, Long.MIN_VALUE.wrappingRem(-1L))
    }
}
