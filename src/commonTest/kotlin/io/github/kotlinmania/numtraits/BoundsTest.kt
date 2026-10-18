// port-lint: tests src/bounds.rs
package io.github.kotlinmania.numtraits

import kotlin.test.Test
import kotlin.test.assertEquals

class BoundsTest {
    private fun <T> testWrappingBounds(
        bounds: Bounded<T>,
        expectedMin: T,
        expectedMax: T,
    ) {
        assertEquals(expectedMin, bounds.minValue())
        assertEquals(expectedMax, bounds.maxValue())
    }

    @Test
    fun wrappingBounded() {
        testWrappingBounds(ByteBounds, Byte.MIN_VALUE, Byte.MAX_VALUE)
        testWrappingBounds(ShortBounds, Short.MIN_VALUE, Short.MAX_VALUE)
        testWrappingBounds(IntBounds, Int.MIN_VALUE, Int.MAX_VALUE)
        testWrappingBounds(LongBounds, Long.MIN_VALUE, Long.MAX_VALUE)
        testWrappingBounds(UByteBounds, UByte.MIN_VALUE, UByte.MAX_VALUE)
        testWrappingBounds(UShortBounds, UShort.MIN_VALUE, UShort.MAX_VALUE)
        testWrappingBounds(UIntBounds, UInt.MIN_VALUE, UInt.MAX_VALUE)
        testWrappingBounds(ULongBounds, ULong.MIN_VALUE, ULong.MAX_VALUE)
        testWrappingBounds(FloatBounds, -Float.MAX_VALUE, Float.MAX_VALUE)
        testWrappingBounds(DoubleBounds, -Double.MAX_VALUE, Double.MAX_VALUE)
        testWrappingBounds(UnitBounds, Unit, Unit)
    }

    // wrapping_bounded_i128 unported: standard Kotlin Multiplatform primitives do not include native 128-bit integers.

    @Test
    fun wrappingIsBounded() {
        fun <T> requireBounded(bounds: Bounded<T>) {
            assertEquals(bounds.minValue(), bounds.minValue())
            assertEquals(bounds.maxValue(), bounds.maxValue())
        }
        requireBounded(UIntBounds)
        requireBounded(IntBounds)
    }
}
