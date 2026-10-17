// port-lint: source tests/cast.rs
package io.github.kotlinmania.numtraits

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CastTest {
    @Test
    fun toPrimitiveFloat() {
        val f32TooLarge = 1e39
        assertEquals(Float.POSITIVE_INFINITY, F64Num.toF32(f32TooLarge))
        assertEquals(Float.NEGATIVE_INFINITY, F64Num.toF32(-f32TooLarge))
        assertEquals(Float.MAX_VALUE, F64Num.toF32(Float.MAX_VALUE.toDouble()))
        assertEquals(-Float.MAX_VALUE, F64Num.toF32(-Float.MAX_VALUE.toDouble()))
        assertEquals(Float.POSITIVE_INFINITY, F64Num.toF32(Double.POSITIVE_INFINITY))
        assertEquals(Float.NEGATIVE_INFINITY, F64Num.toF32(Double.NEGATIVE_INFINITY))
        assertTrue(F64Num.toF32(Double.NaN).isNaN())
    }

    @Test
    fun wrappingToPrimitive() {
        val i = 0
        assertEquals(0.toUByte(), I32Num.toU8(i))
        assertEquals(0.toUShort(), I32Num.toU16(i))
        assertEquals(0u, I32Num.toU32(i))
        assertEquals(0uL, I32Num.toU64(i))
        assertEquals(0uL, I32Num.toUsize(i))
        assertEquals(0.toByte(), I32Num.toI8(i))
        assertEquals(0.toShort(), I32Num.toI16(i))
        assertEquals(0, I32Num.toI32(i))
        assertEquals(0L, I32Num.toI64(i))
        assertEquals(0L, I32Num.toIsize(i))
        assertEquals(0.0f, I32Num.toF32(i))
        assertEquals(0.0, I32Num.toF64(i))
    }

    @Test
    fun wrappingIsToprimitive() {
        val type: ToPrimitive<Int> = I32Num
        assertEquals(42, type.toI32(42))
    }

    @Test
    fun wrappingIsFromprimitive() {
        val type: FromPrimitive<Int> = I32Num
        assertEquals(42, type.fromI32(42))
    }

    @Test
    fun wrappingIsNumcast() {
        val type: NumCast<Int> = I32Num
        assertEquals(42, type.from(I32Num, 42))
    }

    @Test
    fun asPrimitive() {
        val f64AsF32 =
            object : AsPrimitive<Double, Float> {
                override fun asPrimitive(value: Double): Float = value.toFloat()
            }
        val i16AsU8 =
            object : AsPrimitive<Short, UByte> {
                override fun asPrimitive(value: Short): UByte = value.toUByte()
            }
        assertEquals(1.625f, f64AsF32.asPrimitive(1.625))
        assertEquals(0.toUByte(), i16AsU8.asPrimitive(768.toShort()))
    }

    @Test
    fun floatToIntegerChecksOverflow() {
        val source = 1.0e123
        assertNull(cast(F64Num, I32Num, source))
    }

    @Test
    fun castToIntChecksOverflow() {
        val bigF = 1.0e123
        val normalF = 1.0
        val smallF = -1.0e123

        assertNull(cast(F64Num, I8Num, bigF))
        assertNull(cast(F64Num, I16Num, bigF))
        assertNull(cast(F64Num, I32Num, bigF))
        assertNull(cast(F64Num, I64Num, bigF))

        assertEquals(1.toByte(), cast(F64Num, I8Num, normalF))
        assertEquals(1.toShort(), cast(F64Num, I16Num, normalF))
        assertEquals(1, cast(F64Num, I32Num, normalF))
        assertEquals(1L, cast(F64Num, I64Num, normalF))

        assertNull(cast(F64Num, I8Num, smallF))
        assertNull(cast(F64Num, I16Num, smallF))
        assertNull(cast(F64Num, I32Num, smallF))
        assertNull(cast(F64Num, I64Num, smallF))
    }

    @Test
    fun castToUnsignedIntChecksOverflow() {
        val bigF = 1.0e123
        val normalF = 1.0
        val smallF = -1.0e123

        assertNull(cast(F64Num, U8Num, bigF))
        assertNull(cast(F64Num, U16Num, bigF))
        assertNull(cast(F64Num, U32Num, bigF))
        assertNull(cast(F64Num, U64Num, bigF))

        assertEquals(1.toUByte(), cast(F64Num, U8Num, normalF))
        assertEquals(1.toUShort(), cast(F64Num, U16Num, normalF))
        assertEquals(1u, cast(F64Num, U32Num, normalF))
        assertEquals(1uL, cast(F64Num, U64Num, normalF))

        assertNull(cast(F64Num, U8Num, smallF))
        assertNull(cast(F64Num, U16Num, smallF))
        assertNull(cast(F64Num, U32Num, smallF))
        assertNull(cast(F64Num, U64Num, smallF))
    }

    // castToI128ChecksOverflow omitted because Kotlin has no primitive 128-bit integer types.

    @Test
    fun castFloatToIntEdgeCases() {
        assertEquals(Byte.MIN_VALUE, cast(F32Num, I8Num, Byte.MIN_VALUE.toFloat()))
        assertNull(cast(F32Num, I8Num, Byte.MIN_VALUE.toFloat() - 1.0f))
        assertEquals(Byte.MAX_VALUE, cast(F32Num, I8Num, Byte.MAX_VALUE.toFloat()))
        assertNull(cast(F32Num, I8Num, Byte.MAX_VALUE.toFloat() + 1.0f))

        assertEquals(Int.MIN_VALUE, cast(F64Num, I32Num, Int.MIN_VALUE.toDouble()))
        assertNull(cast(F64Num, I32Num, Int.MIN_VALUE.toDouble() - 1.0))
        assertEquals(Int.MAX_VALUE, cast(F64Num, I32Num, Int.MAX_VALUE.toDouble()))
        assertNull(cast(F64Num, I32Num, Int.MAX_VALUE.toDouble() + 1.0))

        assertNull(cast(F32Num, I32Num, Float.NaN))
        assertNull(cast(F32Num, I32Num, Float.POSITIVE_INFINITY))
        assertNull(cast(F32Num, I32Num, Float.NEGATIVE_INFINITY))
        assertNull(cast(F64Num, I64Num, Double.NaN))
        assertNull(cast(F64Num, I64Num, Double.POSITIVE_INFINITY))
        assertNull(cast(F64Num, I64Num, Double.NEGATIVE_INFINITY))
    }

    // castFloatToI128EdgeCases omitted because Kotlin has no primitive 128-bit integer types.

    @Test
    fun castIntToIntEdgeCases() {
        assertEquals(Byte.MIN_VALUE, cast(I32Num, I8Num, Byte.MIN_VALUE.toInt()))
        assertNull(cast(I32Num, I8Num, Byte.MIN_VALUE.toInt() - 1))
        assertEquals(Byte.MAX_VALUE, cast(I32Num, I8Num, Byte.MAX_VALUE.toInt()))
        assertNull(cast(I32Num, I8Num, Byte.MAX_VALUE.toInt() + 1))

        assertEquals(0.toUByte(), cast(I32Num, U8Num, 0))
        assertNull(cast(I32Num, U8Num, -1))
        assertEquals(UByte.MAX_VALUE, cast(I32Num, U8Num, UByte.MAX_VALUE.toInt()))
        assertNull(cast(I32Num, U8Num, UByte.MAX_VALUE.toInt() + 1))
    }

    // castIntTo128EdgeCases omitted because Kotlin has no primitive 128-bit integer types.

    @Test
    fun newtypeFromPrimitive() {
        val customFrom =
            object : FromPrimitive<Int> {
                override fun fromI64(n: Long): Int? = I32Num.fromI64(n)

                override fun fromU64(n: ULong): Int? = I32Num.fromU64(n)
            }
        assertEquals(I32Num.fromI8(Byte.MIN_VALUE), customFrom.fromI8(Byte.MIN_VALUE))
        assertEquals(I32Num.fromI8(Byte.MAX_VALUE), customFrom.fromI8(Byte.MAX_VALUE))
        assertEquals(I32Num.fromU8(UByte.MIN_VALUE), customFrom.fromU8(UByte.MIN_VALUE))
        assertEquals(I32Num.fromU8(UByte.MAX_VALUE), customFrom.fromU8(UByte.MAX_VALUE))
        assertEquals(I32Num.fromF32(10.0f), customFrom.fromF32(10.0f))
        assertEquals(I32Num.fromF64(10.0), customFrom.fromF64(10.0))
    }

    @Test
    fun newtypeToPrimitive() {
        val customTo =
            object : ToPrimitive<Int> {
                override fun toI64(value: Int): Long? = I32Num.toI64(value)

                override fun toU64(value: Int): ULong? = I32Num.toU64(value)
            }
        assertEquals(I32Num.toI32(Int.MIN_VALUE), customTo.toI32(Int.MIN_VALUE))
        assertEquals(I32Num.toI32(Int.MAX_VALUE), customTo.toI32(Int.MAX_VALUE))
        assertEquals(I32Num.toU32(Int.MAX_VALUE), customTo.toU32(Int.MAX_VALUE))
        assertEquals(I32Num.toF64(Int.MAX_VALUE), customTo.toF64(Int.MAX_VALUE))
    }
}
