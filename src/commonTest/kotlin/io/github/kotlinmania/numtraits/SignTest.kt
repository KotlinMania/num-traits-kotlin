// port-lint: tests src/sign.rs
package io.github.kotlinmania.numtraits

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SignTest {
    @Test
    fun unsignedWrappingIsUnsigned() {
        fun <T> requireUnsigned(type: Unsigned<T>) {
            assertTrue(type.isZero(type.zero()))
        }
        requireUnsigned(U8Num)
        requireUnsigned(U16Num)
        requireUnsigned(U32Num)
        requireUnsigned(U64Num)
    }

    @Test
    fun signedWrappingIsSigned() {
        assertEquals(42, abs(I32Num, -42))
        assertEquals(20, absSub(I32Num, 42, 22))
        assertEquals(0, absSub(I32Num, 22, 42))
        assertEquals(-1, signum(I32Num, -42))
        assertEquals(1, signum(I32Num, 42))
        assertEquals(0, signum(I32Num, 0))
        assertTrue(I32Num.isPositive(42))
        assertFalse(I32Num.isPositive(-42))
        assertFalse(I32Num.isPositive(0))
        assertTrue(I32Num.isNegative(-42))
        assertFalse(I32Num.isNegative(42))
        assertFalse(I32Num.isNegative(0))
    }

    @Test
    fun signedFloat() {
        assertTrue(F32Num.abs(Float.NaN).isNaN())
        assertTrue(F64Num.abs(Double.NaN).isNaN())
        assertTrue(F32Num.absSub(Float.NaN, 1.0f).isNaN())
        assertTrue(F32Num.absSub(1.0f, Float.NaN).isNaN())
        assertTrue(F64Num.absSub(Double.NaN, 1.0).isNaN())
        assertTrue(F64Num.absSub(1.0, Double.NaN).isNaN())
        assertEquals(2.0f, F32Num.absSub(3.0f, 1.0f))
        assertEquals(0.0f, F32Num.absSub(1.0f, 3.0f))
        assertEquals(2.0, F64Num.absSub(3.0, 1.0))
        assertEquals(0.0, F64Num.absSub(1.0, 3.0))
        assertTrue(F32Num.isPositive(0.0f))
        assertTrue(F32Num.isNegative(-0.0f))
        assertTrue(F64Num.isPositive(0.0))
        assertTrue(F64Num.isNegative(-0.0))
    }
}
