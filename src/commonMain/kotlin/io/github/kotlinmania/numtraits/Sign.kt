// port-lint: source src/sign.rs
package io.github.kotlinmania.numtraits

import kotlin.math.sign
import kotlin.math.abs as mathAbs

/**
 * Useful functions for signed numbers (i.e. numbers that can be negative).
 */
public interface Signed<T> : Num<T> {
    /**
     * Computes the absolute value of a number.
     *
     * For signed integers, `minValue().abs()` overflows in two's complement and wraps to `minValue()`.
     */
    public fun abs(value: T): T

    /**
     * The positive difference of two numbers.
     *
     * Returns `zero` if the number is less than or equal to `other`, otherwise the difference
     * between `value` and `other` is returned.
     */
    public fun absSub(
        value: T,
        other: T,
    ): T

    /**
     * Returns the sign of the number.
     *
     * - `0` if the number is zero
     * - `1` if the number is positive
     * - `-1` if the number is negative
     */
    public fun signum(value: T): T

    /**
     * Returns true if the number is positive and false if the number is zero or negative.
     */
    public fun isPositive(value: T): Boolean

    /**
     * Returns true if the number is negative and false if the number is zero or positive.
     */
    public fun isNegative(value: T): Boolean
}

/**
 * Computes the absolute value of a number using its [Signed] instance.
 */
public fun <T> abs(
    type: Signed<T>,
    value: T,
): T = type.abs(value)

/**
 * The positive difference of two numbers using their [Signed] instance.
 */
public fun <T> absSub(
    type: Signed<T>,
    value: T,
    other: T,
): T = type.absSub(value, other)

/**
 * Returns the sign of a number using its [Signed] instance.
 */
public fun <T> signum(
    type: Signed<T>,
    value: T,
): T = type.signum(value)

/**
 * A marker interface for unsigned types (i.e. numbers that cannot be negative).
 */
public interface Unsigned<T> : Num<T>

public fun Byte.absVal(): Byte =
    when {
        this == Byte.MIN_VALUE -> Byte.MIN_VALUE
        this < 0 -> (-this.toInt()).toByte()
        else -> this
    }

public fun Short.absVal(): Short =
    when {
        this == Short.MIN_VALUE -> Short.MIN_VALUE
        this < 0 -> (-this.toInt()).toShort()
        else -> this
    }

public fun Int.absVal(): Int = if (this == Int.MIN_VALUE) Int.MIN_VALUE else mathAbs(this)

public fun Long.absVal(): Long = if (this == Long.MIN_VALUE) Long.MIN_VALUE else mathAbs(this)

public fun Float.absVal(): Float = mathAbs(this)

public fun Double.absVal(): Double = mathAbs(this)

public fun Byte.absSub(other: Byte): Byte = if (this <= other) 0 else (this - other).toByte()

public fun Short.absSub(other: Short): Short = if (this <= other) 0 else (this - other).toShort()

public fun Int.absSub(other: Int): Int = if (this <= other) 0 else this - other

public fun Long.absSub(other: Long): Long = if (this <= other) 0L else this - other

public fun Float.absSub(other: Float): Float =
    if (this.isNaN() || other.isNaN()) {
        Float.NaN
    } else if (this <= other) {
        0.0f
    } else {
        this - other
    }

public fun Double.absSub(other: Double): Double =
    if (this.isNaN() || other.isNaN()) {
        Double.NaN
    } else if (this <= other) {
        0.0
    } else {
        this - other
    }

public fun Byte.signumVal(): Byte =
    when {
        this > 0 -> 1
        this < 0 -> -1
        else -> 0
    }

public fun Short.signumVal(): Short =
    when {
        this > 0 -> 1
        this < 0 -> -1
        else -> 0
    }

public fun Int.signumVal(): Int =
    when {
        this > 0 -> 1
        this < 0 -> -1
        else -> 0
    }

public fun Long.signumVal(): Long =
    when {
        this > 0L -> 1L
        this < 0L -> -1L
        else -> 0L
    }

public fun Float.signumVal(): Float =
    when {
        this.isNaN() -> Float.NaN
        this == 0.0f -> if (1.0f / this < 0.0f) -1.0f else 1.0f
        else -> sign(this)
    }

public fun Double.signumVal(): Double =
    when {
        this.isNaN() -> Double.NaN
        this == 0.0 -> if (1.0 / this < 0.0) -1.0 else 1.0
        else -> sign(this)
    }

public fun Byte.isPositive(): Boolean = this > 0

public fun Short.isPositive(): Boolean = this > 0

public fun Int.isPositive(): Boolean = this > 0

public fun Long.isPositive(): Boolean = this > 0L

public fun Float.isPositive(): Boolean = !this.isNegative()

public fun Double.isPositive(): Boolean = !this.isNegative()

public fun Byte.isNegative(): Boolean = this < 0

public fun Short.isNegative(): Boolean = this < 0

public fun Int.isNegative(): Boolean = this < 0

public fun Long.isNegative(): Boolean = this < 0L

public fun Float.isNegative(): Boolean = (this.toRawBits() and (1 shl 31)) != 0

public fun Double.isNegative(): Boolean = (this.toRawBits() and (1L shl 63)) != 0L
