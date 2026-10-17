// port-lint: source src/cast.rs
package io.github.kotlinmania.numtraits

/**
 * A generic interface for converting a value to a primitive numeric value.
 *
 * A value can be represented by the target type when it lies within the
 * bounds `[minValue, maxValue]` of that target type. When the value lies
 * outside those bounds or is not finite, `null` is returned.
 */
public interface ToPrimitive<T> {
    public fun toI8(value: T): Byte? = toI64(value)?.let { if (it in Byte.MIN_VALUE.toLong()..Byte.MAX_VALUE.toLong()) it.toByte() else null }

    public fun toI16(value: T): Short? =
        toI64(value)?.let {
            if (it in Short.MIN_VALUE.toLong()..Short.MAX_VALUE.toLong()) it.toShort() else null
        }

    public fun toI32(value: T): Int? = toI64(value)?.let { if (it in Int.MIN_VALUE.toLong()..Int.MAX_VALUE.toLong()) it.toInt() else null }

    public fun toI64(value: T): Long?

    public fun toIsize(value: T): Long? = toI64(value)

    public fun toU8(value: T): UByte? = toU64(value)?.let { if (it <= UByte.MAX_VALUE.toULong()) it.toUByte() else null }

    public fun toU16(value: T): UShort? = toU64(value)?.let { if (it <= UShort.MAX_VALUE.toULong()) it.toUShort() else null }

    public fun toU32(value: T): UInt? = toU64(value)?.let { if (it <= UInt.MAX_VALUE.toULong()) it.toUInt() else null }

    public fun toU64(value: T): ULong?

    public fun toUsize(value: T): ULong? = toU64(value)

    public fun toF32(value: T): Float? =
        toF64(value)?.let {
            when {
                it > Float.MAX_VALUE.toDouble() -> Float.POSITIVE_INFINITY
                it < -Float.MAX_VALUE.toDouble() -> Float.NEGATIVE_INFINITY
                else -> it.toFloat()
            }
        }

    public fun toF64(value: T): Double? =
        toI64(value)?.toDouble() ?: toU64(value)?.toDouble()
}

/**
 * A generic interface for converting a primitive numeric value to [T].
 */
public interface FromPrimitive<T> {
    public fun fromI8(n: Byte): T? = fromI64(n.toLong())

    public fun fromI16(n: Short): T? = fromI64(n.toLong())

    public fun fromI32(n: Int): T? = fromI64(n.toLong())

    public fun fromI64(n: Long): T?

    public fun fromIsize(n: Long): T? = fromI64(n)

    public fun fromU8(n: UByte): T? = fromU64(n.toULong())

    public fun fromU16(n: UShort): T? = fromU64(n.toULong())

    public fun fromU32(n: UInt): T? = fromU64(n.toULong())

    public fun fromU64(n: ULong): T?

    public fun fromUsize(n: ULong): T? = fromU64(n)

    public fun fromF32(n: Float): T? = fromF64(n.toDouble())

    public fun fromF64(n: Double): T? =
        if (!n.isFinite()) {
            null
        } else if (n >= 0.0) {
            doubleToU64Checked(n)?.let { fromU64(it) }
        } else {
            doubleToI64Checked(n)?.let { fromI64(it) }
        }
}

/**
 * An interface for numeric casting between machine types.
 */
public interface NumCast<T> :
    ToPrimitive<T>,
    FromPrimitive<T> {
    public fun <U> from(
        sourceType: ToPrimitive<U>,
        n: U,
    ): T?

    override fun fromI64(n: Long): T? = from(I64Num, n)

    override fun fromU64(n: ULong): T? = from(U64Num, n)

    override fun fromF32(n: Float): T? = from(F32Num, n)

    override fun fromF64(n: Double): T? = from(F64Num, n)
}

/**
 * A generic interface for raw `as`-style primitive conversion that may truncate or wrap.
 */
public interface AsPrimitive<T, U> {
    public fun asPrimitive(value: T): U
}

/**
 * Cast from one machine scalar to another, returning `null` if the value cannot be represented.
 */
public fun <T, U> cast(
    sourceType: ToPrimitive<T>,
    targetType: NumCast<U>,
    n: T,
): U? = targetType.from(sourceType, n)

internal fun floatToI8Checked(f: Float): Byte? {
    if (!f.isFinite()) return null
    val tr = if (f < 0.0f) kotlin.math.ceil(f) else kotlin.math.floor(f)
    if (tr < Byte.MIN_VALUE.toFloat() || tr > Byte.MAX_VALUE.toFloat()) return null
    return tr.toInt().toByte()
}

internal fun floatToI16Checked(f: Float): Short? {
    if (!f.isFinite()) return null
    val tr = if (f < 0.0f) kotlin.math.ceil(f) else kotlin.math.floor(f)
    if (tr < Short.MIN_VALUE.toFloat() || tr > Short.MAX_VALUE.toFloat()) return null
    return tr.toInt().toShort()
}

internal fun floatToI32Checked(f: Float): Int? {
    if (!f.isFinite()) return null
    val tr = if (f < 0.0f) kotlin.math.ceil(f) else kotlin.math.floor(f)
    // Int.MAX_VALUE (2147483647) rounds up to 2147483648.0f in Float!
    if (tr < Int.MIN_VALUE.toFloat() || tr >= 2147483648.0f) return null
    return tr.toInt()
}

internal fun floatToI64Checked(f: Float): Long? {
    if (!f.isFinite()) return null
    val tr = if (f < 0.0f) kotlin.math.ceil(f) else kotlin.math.floor(f)
    // Long.MAX_VALUE rounds up to 9223372036854775808.0f (2^63) in Float!
    if (tr < -9223372036854775808.0f || tr >= 9223372036854775808.0f) return null
    return tr.toLong()
}

internal fun floatToU8Checked(f: Float): UByte? {
    if (!f.isFinite()) return null
    val tr = if (f < 0.0f) kotlin.math.ceil(f) else kotlin.math.floor(f)
    if (tr < 0.0f || tr > UByte.MAX_VALUE.toFloat()) return null
    return tr.toInt().toUByte()
}

internal fun floatToU16Checked(f: Float): UShort? {
    if (!f.isFinite()) return null
    val tr = if (f < 0.0f) kotlin.math.ceil(f) else kotlin.math.floor(f)
    if (tr < 0.0f || tr > UShort.MAX_VALUE.toFloat()) return null
    return tr.toInt().toUShort()
}

internal fun floatToU32Checked(f: Float): UInt? {
    if (!f.isFinite()) return null
    val tr = if (f < 0.0f) kotlin.math.ceil(f) else kotlin.math.floor(f)
    if (tr < 0.0f || tr >= 4294967296.0f) return null
    return tr.toDouble().toULong().toUInt()
}

internal fun floatToU64Checked(f: Float): ULong? {
    if (!f.isFinite()) return null
    val tr = if (f < 0.0f) kotlin.math.ceil(f) else kotlin.math.floor(f)
    if (tr < 0.0f || tr >= 18446744073709551616.0f) return null
    val decoded = f.integerDecode()
    if (decoded.exponent >= 0) {
        return decoded.mantissa shl decoded.exponent.toInt()
    }
    val shift = -decoded.exponent.toInt()
    if (shift >= 64) return 0uL
    return decoded.mantissa shr shift
}

internal fun doubleToI8Checked(d: Double): Byte? {
    if (!d.isFinite()) return null
    val tr = kotlin.math.truncate(d)
    if (tr < Byte.MIN_VALUE.toDouble() || tr > Byte.MAX_VALUE.toDouble()) return null
    return tr.toInt().toByte()
}

internal fun doubleToI16Checked(d: Double): Short? {
    if (!d.isFinite()) return null
    val tr = kotlin.math.truncate(d)
    if (tr < Short.MIN_VALUE.toDouble() || tr > Short.MAX_VALUE.toDouble()) return null
    return tr.toInt().toShort()
}

internal fun doubleToI32Checked(d: Double): Int? {
    if (!d.isFinite()) return null
    val tr = kotlin.math.truncate(d)
    if (tr < Int.MIN_VALUE.toDouble() || tr > Int.MAX_VALUE.toDouble()) return null
    return tr.toInt()
}

internal fun doubleToI64Checked(d: Double): Long? {
    if (!d.isFinite()) return null
    val tr = kotlin.math.truncate(d)
    if (tr < -9223372036854775808.0 || tr >= 9223372036854775808.0) return null
    return tr.toLong()
}

internal fun doubleToU8Checked(d: Double): UByte? {
    if (!d.isFinite()) return null
    val tr = kotlin.math.truncate(d)
    if (tr < 0.0 || tr > UByte.MAX_VALUE.toDouble()) return null
    return tr.toInt().toUByte()
}

internal fun doubleToU16Checked(d: Double): UShort? {
    if (!d.isFinite()) return null
    val tr = kotlin.math.truncate(d)
    if (tr < 0.0 || tr > UShort.MAX_VALUE.toDouble()) return null
    return tr.toInt().toUShort()
}

internal fun doubleToU32Checked(d: Double): UInt? {
    if (!d.isFinite()) return null
    val tr = kotlin.math.truncate(d)
    if (tr < 0.0 || tr > UInt.MAX_VALUE.toDouble()) return null
    return tr.toLong().toUInt()
}

internal fun doubleToU64Checked(d: Double): ULong? {
    if (!d.isFinite()) return null
    val tr = kotlin.math.truncate(d)
    if (tr < 0.0 || tr >= 18446744073709551616.0) return null
    val decoded = d.integerDecode()
    if (decoded.exponent >= 0) {
        return decoded.mantissa shl decoded.exponent.toInt()
    }
    val shift = -decoded.exponent.toInt()
    if (shift >= 64) return 0uL
    return decoded.mantissa shr shift
}
