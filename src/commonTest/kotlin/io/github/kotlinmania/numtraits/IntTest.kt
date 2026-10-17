// port-lint: tests src/int.rs
package io.github.kotlinmania.numtraits

import kotlin.test.Test
import kotlin.test.assertEquals

class IntTest {
    @Test
    fun reverseBits() {
        assertEquals(
            0xf7b3_d591_e6a2_c480uL,
            U64Num.reverseBits(0x0123_4567_89ab_cdefuL),
        )

        assertEquals(0.toByte(), I8Num.reverseBits(0.toByte()))
        assertEquals((-1).toByte(), I8Num.reverseBits((-1).toByte()))
        assertEquals(Byte.MIN_VALUE, I8Num.reverseBits(1.toByte()))
        assertEquals(1.toByte(), I8Num.reverseBits(Byte.MIN_VALUE))
        assertEquals(Byte.MAX_VALUE, I8Num.reverseBits((-2).toByte()))
        assertEquals((-2).toByte(), I8Num.reverseBits(Byte.MAX_VALUE))

        assertEquals(0.toShort(), I16Num.reverseBits(0.toShort()))
        assertEquals((-1).toShort(), I16Num.reverseBits((-1).toShort()))
        assertEquals(Short.MIN_VALUE, I16Num.reverseBits(1.toShort()))
        assertEquals(1.toShort(), I16Num.reverseBits(Short.MIN_VALUE))
        assertEquals(Short.MAX_VALUE, I16Num.reverseBits((-2).toShort()))
        assertEquals((-2).toShort(), I16Num.reverseBits(Short.MAX_VALUE))

        assertEquals(0, I32Num.reverseBits(0))
        assertEquals(-1, I32Num.reverseBits(-1))
        assertEquals(Int.MIN_VALUE, I32Num.reverseBits(1))
        assertEquals(1, I32Num.reverseBits(Int.MIN_VALUE))
        assertEquals(Int.MAX_VALUE, I32Num.reverseBits(-2))
        assertEquals(-2, I32Num.reverseBits(Int.MAX_VALUE))

        assertEquals(0L, I64Num.reverseBits(0L))
        assertEquals(-1L, I64Num.reverseBits(-1L))
        assertEquals(Long.MIN_VALUE, I64Num.reverseBits(1L))
        assertEquals(1L, I64Num.reverseBits(Long.MIN_VALUE))
        assertEquals(Long.MAX_VALUE, I64Num.reverseBits(-2L))
        assertEquals(-2L, I64Num.reverseBits(Long.MAX_VALUE))
    }
}
