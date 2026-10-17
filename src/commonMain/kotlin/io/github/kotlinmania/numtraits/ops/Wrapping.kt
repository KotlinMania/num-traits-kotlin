// port-lint: source src/ops/wrapping.rs
package io.github.kotlinmania.numtraits.ops

/**
 * Performs addition that wraps around on overflow.
 */
public interface WrappingAdd<T> {
    public fun wrappingAdd(
        a: T,
        b: T,
    ): T
}

/**
 * Performs subtraction that wraps around on underflow.
 */
public interface WrappingSub<T> {
    public fun wrappingSub(
        a: T,
        b: T,
    ): T
}

/**
 * Performs multiplication that wraps around on overflow.
 */
public interface WrappingMul<T> {
    public fun wrappingMul(
        a: T,
        b: T,
    ): T
}

/**
 * Performs division that wraps around on overflow.
 */
public interface WrappingDiv<T> {
    public fun wrappingDiv(
        a: T,
        b: T,
    ): T
}

/**
 * Performs an integral remainder that wraps around on overflow.
 */
public interface WrappingRem<T> {
    public fun wrappingRem(
        a: T,
        b: T,
    ): T
}

/**
 * Performs negation that wraps around on overflow.
 */
public interface WrappingNeg<T> {
    public fun wrappingNeg(a: T): T
}

/**
 * Performs a left shift that does not panic.
 */
public interface WrappingShl<T> {
    public fun wrappingShl(
        a: T,
        rhs: UInt,
    ): T
}

/**
 * Performs a right shift that does not panic.
 */
public interface WrappingShr<T> {
    public fun wrappingShr(
        a: T,
        rhs: UInt,
    ): T
}

public fun Byte.wrappingAdd(other: Byte): Byte = (this.toInt() + other.toInt()).toByte()

public fun Short.wrappingAdd(other: Short): Short = (this.toInt() + other.toInt()).toShort()

public fun Int.wrappingAdd(other: Int): Int = this + other

public fun Long.wrappingAdd(other: Long): Long = this + other

public fun UByte.wrappingAdd(other: UByte): UByte = (this.toUInt() + other.toUInt()).toUByte()

public fun UShort.wrappingAdd(other: UShort): UShort = (this.toUInt() + other.toUInt()).toUShort()

public fun UInt.wrappingAdd(other: UInt): UInt = this + other

public fun ULong.wrappingAdd(other: ULong): ULong = this + other

public fun Byte.wrappingSub(other: Byte): Byte = (this.toInt() - other.toInt()).toByte()

public fun Short.wrappingSub(other: Short): Short = (this.toInt() - other.toInt()).toShort()

public fun Int.wrappingSub(other: Int): Int = this - other

public fun Long.wrappingSub(other: Long): Long = this - other

public fun UByte.wrappingSub(other: UByte): UByte = (this.toUInt() - other.toUInt()).toUByte()

public fun UShort.wrappingSub(other: UShort): UShort = (this.toUInt() - other.toUInt()).toUShort()

public fun UInt.wrappingSub(other: UInt): UInt = this - other

public fun ULong.wrappingSub(other: ULong): ULong = this - other

public fun Byte.wrappingMul(other: Byte): Byte = (this.toInt() * other.toInt()).toByte()

public fun Short.wrappingMul(other: Short): Short = (this.toInt() * other.toInt()).toShort()

public fun Int.wrappingMul(other: Int): Int = this * other

public fun Long.wrappingMul(other: Long): Long = this * other

public fun UByte.wrappingMul(other: UByte): UByte = (this.toUInt() * other.toUInt()).toUByte()

public fun UShort.wrappingMul(other: UShort): UShort = (this.toUInt() * other.toUInt()).toUShort()

public fun UInt.wrappingMul(other: UInt): UInt = this * other

public fun ULong.wrappingMul(other: ULong): ULong = this * other

public fun Byte.wrappingDiv(other: Byte): Byte =
    if (this == Byte.MIN_VALUE && other == (-1).toByte()) Byte.MIN_VALUE else (this / other).toByte()

public fun Short.wrappingDiv(other: Short): Short =
    if (this == Short.MIN_VALUE && other == (-1).toShort()) Short.MIN_VALUE else (this / other).toShort()

public fun Int.wrappingDiv(other: Int): Int =
    if (this == Int.MIN_VALUE && other == -1) Int.MIN_VALUE else this / other

public fun Long.wrappingDiv(other: Long): Long =
    if (this == Long.MIN_VALUE && other == -1L) Long.MIN_VALUE else this / other

public fun UByte.wrappingDiv(other: UByte): UByte = (this / other).toUByte()

public fun UShort.wrappingDiv(other: UShort): UShort = (this / other).toUShort()

public fun UInt.wrappingDiv(other: UInt): UInt = this / other

public fun ULong.wrappingDiv(other: ULong): ULong = this / other

public fun Byte.wrappingRem(other: Byte): Byte =
    if (this == Byte.MIN_VALUE && other == (-1).toByte()) 0 else (this % other).toByte()

public fun Short.wrappingRem(other: Short): Short =
    if (this == Short.MIN_VALUE && other == (-1).toShort()) 0 else (this % other).toShort()

public fun Int.wrappingRem(other: Int): Int =
    if (this == Int.MIN_VALUE && other == -1) 0 else this % other

public fun Long.wrappingRem(other: Long): Long =
    if (this == Long.MIN_VALUE && other == -1L) 0L else this % other

public fun UByte.wrappingRem(other: UByte): UByte = (this % other).toUByte()

public fun UShort.wrappingRem(other: UShort): UShort = (this % other).toUShort()

public fun UInt.wrappingRem(other: UInt): UInt = this % other

public fun ULong.wrappingRem(other: ULong): ULong = this % other

public fun Byte.wrappingNeg(): Byte = (-this.toInt()).toByte()

public fun Short.wrappingNeg(): Short = (-this.toInt()).toShort()

public fun Int.wrappingNeg(): Int = -this

public fun Long.wrappingNeg(): Long = -this

public fun UByte.wrappingNeg(): UByte = (0u - this.toUInt()).toUByte()

public fun UShort.wrappingNeg(): UShort = (0u - this.toUInt()).toUShort()

public fun UInt.wrappingNeg(): UInt = 0u - this

public fun ULong.wrappingNeg(): ULong = 0uL - this

public fun Byte.wrappingShl(rhs: UInt): Byte = (this.toInt() shl (rhs.toInt() and 7)).toByte()

public fun Short.wrappingShl(rhs: UInt): Short = (this.toInt() shl (rhs.toInt() and 15)).toShort()

public fun Int.wrappingShl(rhs: UInt): Int = this shl (rhs.toInt() and 31)

public fun Long.wrappingShl(rhs: UInt): Long = this shl (rhs.toInt() and 63)

public fun UByte.wrappingShl(rhs: UInt): UByte = (this.toUInt() shl (rhs.toInt() and 7)).toUByte()

public fun UShort.wrappingShl(rhs: UInt): UShort = (this.toUInt() shl (rhs.toInt() and 15)).toUShort()

public fun UInt.wrappingShl(rhs: UInt): UInt = this shl (rhs.toInt() and 31)

public fun ULong.wrappingShl(rhs: UInt): ULong = this shl (rhs.toInt() and 63)

public fun Byte.wrappingShr(rhs: UInt): Byte = (this.toInt() shr (rhs.toInt() and 7)).toByte()

public fun Short.wrappingShr(rhs: UInt): Short = (this.toInt() shr (rhs.toInt() and 15)).toShort()

public fun Int.wrappingShr(rhs: UInt): Int = this shr (rhs.toInt() and 31)

public fun Long.wrappingShr(rhs: UInt): Long = this shr (rhs.toInt() and 63)

public fun UByte.wrappingShr(rhs: UInt): UByte = (this.toUInt() shr (rhs.toInt() and 7)).toUByte()

public fun UShort.wrappingShr(rhs: UInt): UShort = (this.toUInt() shr (rhs.toInt() and 15)).toUShort()

public fun UInt.wrappingShr(rhs: UInt): UInt = this shr (rhs.toInt() and 31)

public fun ULong.wrappingShr(rhs: UInt): ULong = this shr (rhs.toInt() and 63)
