package io.github.kotlinmania.numtraits

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class NumTest {
    @Test
    fun fromStrRadixUnwrap() {
        val i = I32Num.fromStrRadix("0", 10u)
        assertEquals(0, i)

        val f = F32Num.fromStrRadix("0.0", 10u)
        assertEquals(0.0f, f)
    }

    @Test
    fun fromStrRadixMultiByteFail() {
        assertNull(F32Num.fromStrRadix("0.2™", 10u))
        assertNull(F32Num.fromStrRadix("0.2E™", 10u))
    }

    @Test
    fun fromStrRadixIgnoreCase() {
        assertEquals(Float.POSITIVE_INFINITY, F32Num.fromStrRadix("InF", 16u))
        assertEquals(Float.POSITIVE_INFINITY, F32Num.fromStrRadix("InfinitY", 16u))
        assertEquals(Float.NEGATIVE_INFINITY, F32Num.fromStrRadix("-InF", 16u))
        assertEquals(Float.NEGATIVE_INFINITY, F32Num.fromStrRadix("-InfinitY", 16u))
        assertTrue(F32Num.fromStrRadix("nAn", 16u)?.isNaN() == true)
        assertTrue(F32Num.fromStrRadix("-nAn", 16u)?.isNaN() == true)
    }

    @Test
    fun clampTests() {
        assertEquals(1, clamp(1, -1, 2))
        assertEquals(-1, clamp(-2, -1, 2))
        assertEquals(2, clamp(3, -1, 2))
        assertEquals(1, clampMin(1, -1))
        assertEquals(-1, clampMin(-2, -1))
        assertEquals(-1, clampMax(1, -1))
        assertEquals(-2, clampMax(-2, -1))

        assertEquals(1.0f, clamp(1.0f, -1.0f, 2.0f))
        assertEquals(-1.0f, clamp(-2.0f, -1.0f, 2.0f))
        assertEquals(2.0f, clamp(3.0f, -1.0f, 2.0f))
        assertTrue(clamp(Float.NaN, -1.0f, 1.0f).isNaN())
    }

    @Test
    fun clampShouldFailOnInvalidBounds() {
        assertFailsWith<IllegalArgumentException> {
            clamp(1, 2, -1)
        }
        assertFailsWith<IllegalArgumentException> {
            clamp(1.0f, 2.0f, -1.0f)
        }
        assertFailsWith<IllegalArgumentException> {
            clamp(1.0f, Float.NaN, 1.0f)
        }
        assertFailsWith<IllegalArgumentException> {
            clamp(1.0f, -1.0f, Float.NaN)
        }
        assertFailsWith<IllegalArgumentException> {
            clampMin(1.0f, Float.NaN)
        }
        assertFailsWith<IllegalArgumentException> {
            clampMax(1.0f, Float.NaN)
        }
    }
}
