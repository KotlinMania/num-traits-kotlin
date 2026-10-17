// port-lint: source src/ops/saturating.rs
package io.github.kotlinmania.numtraits.ops

import io.github.kotlinmania.numtraits.checkedAdd
import io.github.kotlinmania.numtraits.checkedMul
import io.github.kotlinmania.numtraits.checkedSub

/**
 * Saturating math operations (deprecated in favor of [SaturatingAdd], [SaturatingSub], [SaturatingMul]).
 */
public interface Saturating<T> {
    public fun saturatingAdd(
        a: T,
        b: T,
    ): T

    public fun saturatingSub(
        a: T,
        b: T,
    ): T
}

/**
 * Performs addition that saturates at the numeric bounds instead of overflowing.
 */
public interface SaturatingAdd<T> {
    public fun saturatingAdd(
        a: T,
        b: T,
    ): T
}

/**
 * Performs subtraction that saturates at the numeric bounds instead of underflowing.
 */
public interface SaturatingSub<T> {
    public fun saturatingSub(
        a: T,
        b: T,
    ): T
}

/**
 * Performs multiplication that saturates at the numeric bounds instead of overflowing.
 */
public interface SaturatingMul<T> {
    public fun saturatingMul(
        a: T,
        b: T,
    ): T
}

public fun Byte.saturatingAdd(other: Byte): Byte =
    (this.toInt() + other.toInt()).coerceIn(Byte.MIN_VALUE.toInt(), Byte.MAX_VALUE.toInt()).toByte()

public fun Short.saturatingAdd(other: Short): Short =
    (this.toInt() + other.toInt()).coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()

public fun Int.saturatingAdd(other: Int): Int =
    (this.toLong() + other.toLong()).coerceIn(Int.MIN_VALUE.toLong(), Int.MAX_VALUE.toLong()).toInt()

public fun Long.saturatingAdd(other: Long): Long =
    checkedAdd(other) ?: if (this >= 0L) Long.MAX_VALUE else Long.MIN_VALUE

public fun UByte.saturatingAdd(other: UByte): UByte =
    (this.toUInt() + other.toUInt()).coerceAtMost(UByte.MAX_VALUE.toUInt()).toUByte()

public fun UShort.saturatingAdd(other: UShort): UShort =
    (this.toUInt() + other.toUInt()).coerceAtMost(UShort.MAX_VALUE.toUInt()).toUShort()

public fun UInt.saturatingAdd(other: UInt): UInt =
    (this.toULong() + other.toULong()).coerceAtMost(UInt.MAX_VALUE.toULong()).toUInt()

public fun ULong.saturatingAdd(other: ULong): ULong = checkedAdd(other) ?: ULong.MAX_VALUE

public fun Byte.saturatingSub(other: Byte): Byte =
    (this.toInt() - other.toInt()).coerceIn(Byte.MIN_VALUE.toInt(), Byte.MAX_VALUE.toInt()).toByte()

public fun Short.saturatingSub(other: Short): Short =
    (this.toInt() - other.toInt()).coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()

public fun Int.saturatingSub(other: Int): Int =
    (this.toLong() - other.toLong()).coerceIn(Int.MIN_VALUE.toLong(), Int.MAX_VALUE.toLong()).toInt()

public fun Long.saturatingSub(other: Long): Long =
    checkedSub(other) ?: if (this >= 0L) Long.MAX_VALUE else Long.MIN_VALUE

public fun UByte.saturatingSub(other: UByte): UByte = if (this <= other) 0u else (this - other).toUByte()

public fun UShort.saturatingSub(other: UShort): UShort = if (this <= other) 0u else (this - other).toUShort()

public fun UInt.saturatingSub(other: UInt): UInt = if (this <= other) 0u else this - other

public fun ULong.saturatingSub(other: ULong): ULong = if (this <= other) 0uL else this - other

public fun Byte.saturatingMul(other: Byte): Byte =
    (this.toInt() * other.toInt()).coerceIn(Byte.MIN_VALUE.toInt(), Byte.MAX_VALUE.toInt()).toByte()

public fun Short.saturatingMul(other: Short): Short =
    (this.toInt() * other.toInt()).coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()

public fun Int.saturatingMul(other: Int): Int =
    (this.toLong() * other.toLong()).coerceIn(Int.MIN_VALUE.toLong(), Int.MAX_VALUE.toLong()).toInt()

public fun Long.saturatingMul(other: Long): Long =
    checkedMul(other) ?: if ((this > 0L) == (other > 0L)) Long.MAX_VALUE else Long.MIN_VALUE

public fun UByte.saturatingMul(other: UByte): UByte =
    (this.toUInt() * other.toUInt()).coerceAtMost(UByte.MAX_VALUE.toUInt()).toUByte()

public fun UShort.saturatingMul(other: UShort): UShort =
    (this.toUInt() * other.toUInt()).coerceAtMost(UShort.MAX_VALUE.toUInt()).toUShort()

public fun UInt.saturatingMul(other: UInt): UInt =
    (this.toULong() * other.toULong()).coerceAtMost(UInt.MAX_VALUE.toULong()).toUInt()

public fun ULong.saturatingMul(other: ULong): ULong = checkedMul(other) ?: ULong.MAX_VALUE
