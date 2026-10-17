package io.github.kotlinmania.numtraits

/**
 * A value bounded by a minimum and a maximum.
 *
 * If input is less than min then this returns min.
 * If input is greater than max then this returns max.
 * Otherwise this returns input.
 *
 * Throws [IllegalArgumentException] if `min > max`.
 */
internal fun <T : Comparable<T>> clampComparable(
    input: T,
    min: T,
    max: T,
): T {
    require(min <= max) { "min must be less than or equal to max" }
    return when {
        input < min -> min
        input > max -> max
        else -> input
    }
}

public fun clamp(
    input: Byte,
    min: Byte,
    max: Byte,
): Byte = clampComparable(input, min, max)

public fun clamp(
    input: Short,
    min: Short,
    max: Short,
): Short = clampComparable(input, min, max)

public fun clamp(
    input: Int,
    min: Int,
    max: Int,
): Int = clampComparable(input, min, max)

public fun clamp(
    input: Long,
    min: Long,
    max: Long,
): Long = clampComparable(input, min, max)

public fun clamp(
    input: UByte,
    min: UByte,
    max: UByte,
): UByte = clampComparable(input, min, max)

public fun clamp(
    input: UShort,
    min: UShort,
    max: UShort,
): UShort = clampComparable(input, min, max)

public fun clamp(
    input: UInt,
    min: UInt,
    max: UInt,
): UInt = clampComparable(input, min, max)

public fun clamp(
    input: ULong,
    min: ULong,
    max: ULong,
): ULong = clampComparable(input, min, max)

public fun clamp(
    input: Float,
    min: Float,
    max: Float,
): Float {
    require(min <= max) { "min must be less than or equal to max" }
    return when {
        input.isNaN() -> input
        input < min -> min
        input > max -> max
        else -> input
    }
}

public fun clamp(
    input: Double,
    min: Double,
    max: Double,
): Double {
    require(min <= max) { "min must be less than or equal to max" }
    return when {
        input.isNaN() -> input
        input < min -> min
        input > max -> max
        else -> input
    }
}

/**
 * A value bounded by a minimum value.
 *
 * If input is less than min then this returns min.
 * Otherwise this returns input.
 */
internal fun <T : Comparable<T>> clampMinComparable(
    input: T,
    min: T,
): T = if (input < min) min else input

public fun clampMin(
    input: Byte,
    min: Byte,
): Byte = clampMinComparable(input, min)

public fun clampMin(
    input: Short,
    min: Short,
): Short = clampMinComparable(input, min)

public fun clampMin(
    input: Int,
    min: Int,
): Int = clampMinComparable(input, min)

public fun clampMin(
    input: Long,
    min: Long,
): Long = clampMinComparable(input, min)

public fun clampMin(
    input: UByte,
    min: UByte,
): UByte = clampMinComparable(input, min)

public fun clampMin(
    input: UShort,
    min: UShort,
): UShort = clampMinComparable(input, min)

public fun clampMin(
    input: UInt,
    min: UInt,
): UInt = clampMinComparable(input, min)

public fun clampMin(
    input: ULong,
    min: ULong,
): ULong = clampMinComparable(input, min)

public fun clampMin(
    input: Float,
    min: Float,
): Float {
    require(!min.isNaN()) { "min must not be NaN" }
    return if (input < min) min else input
}

public fun clampMin(
    input: Double,
    min: Double,
): Double {
    require(!min.isNaN()) { "min must not be NaN" }
    return if (input < min) min else input
}

/**
 * A value bounded by a maximum value.
 *
 * If input is greater than max then this returns max.
 * Otherwise this returns input.
 */
internal fun <T : Comparable<T>> clampMaxComparable(
    input: T,
    max: T,
): T = if (input > max) max else input

public fun clampMax(
    input: Byte,
    max: Byte,
): Byte = clampMaxComparable(input, max)

public fun clampMax(
    input: Short,
    max: Short,
): Short = clampMaxComparable(input, max)

public fun clampMax(
    input: Int,
    max: Int,
): Int = clampMaxComparable(input, max)

public fun clampMax(
    input: Long,
    max: Long,
): Long = clampMaxComparable(input, max)

public fun clampMax(
    input: UByte,
    max: UByte,
): UByte = clampMaxComparable(input, max)

public fun clampMax(
    input: UShort,
    max: UShort,
): UShort = clampMaxComparable(input, max)

public fun clampMax(
    input: UInt,
    max: UInt,
): UInt = clampMaxComparable(input, max)

public fun clampMax(
    input: ULong,
    max: ULong,
): ULong = clampMaxComparable(input, max)

public fun clampMax(
    input: Float,
    max: Float,
): Float {
    require(!max.isNaN()) { "max must not be NaN" }
    return if (input > max) max else input
}

public fun clampMax(
    input: Double,
    max: Double,
): Double {
    require(!max.isNaN()) { "max must not be NaN" }
    return if (input > max) max else input
}
