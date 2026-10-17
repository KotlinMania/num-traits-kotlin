// port-lint: source src/ops/euclid.rs
package io.github.kotlinmania.numtraits.ops

import kotlin.math.abs
import kotlin.math.truncate

public data class DivRemByte(
    public val div: Byte,
    public val rem: Byte,
)

public data class DivRemShort(
    public val div: Short,
    public val rem: Short,
)

public data class DivRemInt(
    public val div: Int,
    public val rem: Int,
)

public data class DivRemLong(
    public val div: Long,
    public val rem: Long,
)

public data class DivRemUByte(
    public val div: UByte,
    public val rem: UByte,
)

public data class DivRemUShort(
    public val div: UShort,
    public val rem: UShort,
)

public data class DivRemUInt(
    public val div: UInt,
    public val rem: UInt,
)

public data class DivRemULong(
    public val div: ULong,
    public val rem: ULong,
)

public data class DivRemFloat(
    public val div: Float,
    public val rem: Float,
)

public data class DivRemDouble(
    public val div: Double,
    public val rem: Double,
)

/**
 * Euclidean division and remainder.
 */
public interface Euclid<T, R> {
    /**
     * Calculates Euclidean division, the matching method for [remEuclid].
     */
    public fun divEuclid(
        a: T,
        v: T,
    ): T

    /**
     * Calculates the least nonnegative remainder of `a (mod v)`.
     */
    public fun remEuclid(
        a: T,
        v: T,
    ): T

    /**
     * Returns both the quotient and remainder from Euclidean division.
     */
    public fun divRemEuclid(
        a: T,
        v: T,
    ): R
}

/**
 * Checked Euclidean division and remainder.
 */
public interface CheckedEuclid<T, R> : Euclid<T, R> {
    public fun checkedDivEuclid(
        a: T,
        v: T,
    ): T?

    public fun checkedRemEuclid(
        a: T,
        v: T,
    ): T?

    public fun checkedDivRemEuclid(
        a: T,
        v: T,
    ): R?
}

public fun Byte.divEuclid(v: Byte): Byte {
    val q = (this / v).toByte()
    if (this % v < 0) {
        return if (v > 0) (q - 1).toByte() else (q + 1).toByte()
    }
    return q
}

public fun Byte.remEuclid(v: Byte): Byte {
    val r = (this % v).toByte()
    return if (r < 0) {
        if (v < 0) (r - v).toByte() else (r + v).toByte()
    } else {
        r
    }
}

public fun Byte.divRemEuclid(v: Byte): DivRemByte = DivRemByte(divEuclid(v), remEuclid(v))

public fun Byte.checkedDivEuclid(v: Byte): Byte? =
    if (v == 0.toByte() || (this == Byte.MIN_VALUE && v == (-1).toByte())) null else divEuclid(v)

public fun Byte.checkedRemEuclid(v: Byte): Byte? =
    if (v == 0.toByte() || (this == Byte.MIN_VALUE && v == (-1).toByte())) null else remEuclid(v)

public fun Byte.checkedDivRemEuclid(v: Byte): DivRemByte? =
    if (v == 0.toByte() || (this == Byte.MIN_VALUE && v == (-1).toByte())) null else divRemEuclid(v)

public fun Short.divEuclid(v: Short): Short {
    val q = (this / v).toShort()
    if (this % v < 0) {
        return if (v > 0) (q - 1).toShort() else (q + 1).toShort()
    }
    return q
}

public fun Short.remEuclid(v: Short): Short {
    val r = (this % v).toShort()
    return if (r < 0) {
        if (v < 0) (r - v).toShort() else (r + v).toShort()
    } else {
        r
    }
}

public fun Short.divRemEuclid(v: Short): DivRemShort = DivRemShort(divEuclid(v), remEuclid(v))

public fun Short.checkedDivEuclid(v: Short): Short? =
    if (v == 0.toShort() || (this == Short.MIN_VALUE && v == (-1).toShort())) null else divEuclid(v)

public fun Short.checkedRemEuclid(v: Short): Short? =
    if (v == 0.toShort() || (this == Short.MIN_VALUE && v == (-1).toShort())) null else remEuclid(v)

public fun Short.checkedDivRemEuclid(v: Short): DivRemShort? =
    if (v == 0.toShort() || (this == Short.MIN_VALUE && v == (-1).toShort())) null else divRemEuclid(v)

public fun Int.divEuclid(v: Int): Int {
    val q = this / v
    if (this % v < 0) {
        return if (v > 0) q - 1 else q + 1
    }
    return q
}

public fun Int.remEuclid(v: Int): Int {
    val r = this % v
    return if (r < 0) {
        if (v < 0) r - v else r + v
    } else {
        r
    }
}

public fun Int.divRemEuclid(v: Int): DivRemInt = DivRemInt(divEuclid(v), remEuclid(v))

public fun Int.checkedDivEuclid(v: Int): Int? =
    if (v == 0 || (this == Int.MIN_VALUE && v == -1)) null else divEuclid(v)

public fun Int.checkedRemEuclid(v: Int): Int? =
    if (v == 0 || (this == Int.MIN_VALUE && v == -1)) null else remEuclid(v)

public fun Int.checkedDivRemEuclid(v: Int): DivRemInt? =
    if (v == 0 || (this == Int.MIN_VALUE && v == -1)) null else divRemEuclid(v)

public fun Long.divEuclid(v: Long): Long {
    val q = this / v
    if (this % v < 0L) {
        return if (v > 0L) q - 1L else q + 1L
    }
    return q
}

public fun Long.remEuclid(v: Long): Long {
    val r = this % v
    return if (r < 0L) {
        if (v < 0L) r - v else r + v
    } else {
        r
    }
}

public fun Long.divRemEuclid(v: Long): DivRemLong = DivRemLong(divEuclid(v), remEuclid(v))

public fun Long.checkedDivEuclid(v: Long): Long? =
    if (v == 0L || (this == Long.MIN_VALUE && v == -1L)) null else divEuclid(v)

public fun Long.checkedRemEuclid(v: Long): Long? =
    if (v == 0L || (this == Long.MIN_VALUE && v == -1L)) null else remEuclid(v)

public fun Long.checkedDivRemEuclid(v: Long): DivRemLong? =
    if (v == 0L || (this == Long.MIN_VALUE && v == -1L)) null else divRemEuclid(v)

public fun UByte.divEuclid(v: UByte): UByte = (this / v).toUByte()

public fun UByte.remEuclid(v: UByte): UByte = (this % v).toUByte()

public fun UByte.divRemEuclid(v: UByte): DivRemUByte = DivRemUByte(divEuclid(v), remEuclid(v))

public fun UByte.checkedDivEuclid(v: UByte): UByte? = if (v == 0.toUByte()) null else divEuclid(v)

public fun UByte.checkedRemEuclid(v: UByte): UByte? = if (v == 0.toUByte()) null else remEuclid(v)

public fun UByte.checkedDivRemEuclid(v: UByte): DivRemUByte? = if (v == 0.toUByte()) null else divRemEuclid(v)

public fun UShort.divEuclid(v: UShort): UShort = (this / v).toUShort()

public fun UShort.remEuclid(v: UShort): UShort = (this % v).toUShort()

public fun UShort.divRemEuclid(v: UShort): DivRemUShort = DivRemUShort(divEuclid(v), remEuclid(v))

public fun UShort.checkedDivEuclid(v: UShort): UShort? = if (v == 0.toUShort()) null else divEuclid(v)

public fun UShort.checkedRemEuclid(v: UShort): UShort? = if (v == 0.toUShort()) null else remEuclid(v)

public fun UShort.checkedDivRemEuclid(v: UShort): DivRemUShort? = if (v == 0.toUShort()) null else divRemEuclid(v)

public fun UInt.divEuclid(v: UInt): UInt = this / v

public fun UInt.remEuclid(v: UInt): UInt = this % v

public fun UInt.divRemEuclid(v: UInt): DivRemUInt = DivRemUInt(divEuclid(v), remEuclid(v))

public fun UInt.checkedDivEuclid(v: UInt): UInt? = if (v == 0u) null else divEuclid(v)

public fun UInt.checkedRemEuclid(v: UInt): UInt? = if (v == 0u) null else remEuclid(v)

public fun UInt.checkedDivRemEuclid(v: UInt): DivRemUInt? = if (v == 0u) null else divRemEuclid(v)

public fun ULong.divEuclid(v: ULong): ULong = this / v

public fun ULong.remEuclid(v: ULong): ULong = this % v

public fun ULong.divRemEuclid(v: ULong): DivRemULong = DivRemULong(divEuclid(v), remEuclid(v))

public fun ULong.checkedDivEuclid(v: ULong): ULong? = if (v == 0uL) null else divEuclid(v)

public fun ULong.checkedRemEuclid(v: ULong): ULong? = if (v == 0uL) null else remEuclid(v)

public fun ULong.checkedDivRemEuclid(v: ULong): DivRemULong? = if (v == 0uL) null else divRemEuclid(v)

public fun Float.divEuclid(v: Float): Float {
    val q = truncate(this / v)
    if (this % v < 0.0f) {
        return if (v > 0.0f) q - 1.0f else q + 1.0f
    }
    return q
}

public fun Float.remEuclid(v: Float): Float {
    val r = this % v
    return if (r < 0.0f) {
        r + abs(v)
    } else {
        r
    }
}

public fun Float.divRemEuclid(v: Float): DivRemFloat = DivRemFloat(divEuclid(v), remEuclid(v))

public fun Double.divEuclid(v: Double): Double {
    val q = truncate(this / v)
    if (this % v < 0.0) {
        return if (v > 0.0) q - 1.0 else q + 1.0
    }
    return q
}

public fun Double.remEuclid(v: Double): Double {
    val r = this % v
    return if (r < 0.0) {
        r + abs(v)
    } else {
        r
    }
}

public fun Double.divRemEuclid(v: Double): DivRemDouble = DivRemDouble(divEuclid(v), remEuclid(v))
