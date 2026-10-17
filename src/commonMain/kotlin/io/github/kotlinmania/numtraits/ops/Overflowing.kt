// port-lint: source src/ops/overflowing.rs
package io.github.kotlinmania.numtraits.ops

import io.github.kotlinmania.numtraits.checkedAdd
import io.github.kotlinmania.numtraits.checkedMul
import io.github.kotlinmania.numtraits.checkedSub

public data class OverflowingByte(
    public val value: Byte,
    public val overflowed: Boolean,
)

public data class OverflowingShort(
    public val value: Short,
    public val overflowed: Boolean,
)

public data class OverflowingInt(
    public val value: Int,
    public val overflowed: Boolean,
)

public data class OverflowingLong(
    public val value: Long,
    public val overflowed: Boolean,
)

public data class OverflowingUByte(
    public val value: UByte,
    public val overflowed: Boolean,
)

public data class OverflowingUShort(
    public val value: UShort,
    public val overflowed: Boolean,
)

public data class OverflowingUInt(
    public val value: UInt,
    public val overflowed: Boolean,
)

public data class OverflowingULong(
    public val value: ULong,
    public val overflowed: Boolean,
)

/**
 * Performs addition with a flag for overflow.
 */
public interface OverflowingAdd<T, R> {
    public fun overflowingAdd(
        a: T,
        b: T,
    ): R
}

/**
 * Performs subtraction with a flag for overflow.
 */
public interface OverflowingSub<T, R> {
    public fun overflowingSub(
        a: T,
        b: T,
    ): R
}

/**
 * Performs multiplication with a flag for overflow.
 */
public interface OverflowingMul<T, R> {
    public fun overflowingMul(
        a: T,
        b: T,
    ): R
}

public fun Byte.overflowingAdd(other: Byte): OverflowingByte =
    OverflowingByte(wrappingAdd(other), checkedAdd(other) == null)

public fun Short.overflowingAdd(other: Short): OverflowingShort =
    OverflowingShort(wrappingAdd(other), checkedAdd(other) == null)

public fun Int.overflowingAdd(other: Int): OverflowingInt =
    OverflowingInt(wrappingAdd(other), checkedAdd(other) == null)

public fun Long.overflowingAdd(other: Long): OverflowingLong =
    OverflowingLong(wrappingAdd(other), checkedAdd(other) == null)

public fun UByte.overflowingAdd(other: UByte): OverflowingUByte =
    OverflowingUByte(wrappingAdd(other), checkedAdd(other) == null)

public fun UShort.overflowingAdd(other: UShort): OverflowingUShort =
    OverflowingUShort(wrappingAdd(other), checkedAdd(other) == null)

public fun UInt.overflowingAdd(other: UInt): OverflowingUInt =
    OverflowingUInt(wrappingAdd(other), checkedAdd(other) == null)

public fun ULong.overflowingAdd(other: ULong): OverflowingULong =
    OverflowingULong(wrappingAdd(other), checkedAdd(other) == null)

public fun Byte.overflowingSub(other: Byte): OverflowingByte =
    OverflowingByte(wrappingSub(other), checkedSub(other) == null)

public fun Short.overflowingSub(other: Short): OverflowingShort =
    OverflowingShort(wrappingSub(other), checkedSub(other) == null)

public fun Int.overflowingSub(other: Int): OverflowingInt =
    OverflowingInt(wrappingSub(other), checkedSub(other) == null)

public fun Long.overflowingSub(other: Long): OverflowingLong =
    OverflowingLong(wrappingSub(other), checkedSub(other) == null)

public fun UByte.overflowingSub(other: UByte): OverflowingUByte =
    OverflowingUByte(wrappingSub(other), checkedSub(other) == null)

public fun UShort.overflowingSub(other: UShort): OverflowingUShort =
    OverflowingUShort(wrappingSub(other), checkedSub(other) == null)

public fun UInt.overflowingSub(other: UInt): OverflowingUInt =
    OverflowingUInt(wrappingSub(other), checkedSub(other) == null)

public fun ULong.overflowingSub(other: ULong): OverflowingULong =
    OverflowingULong(wrappingSub(other), checkedSub(other) == null)

public fun Byte.overflowingMul(other: Byte): OverflowingByte =
    OverflowingByte(wrappingMul(other), checkedMul(other) == null)

public fun Short.overflowingMul(other: Short): OverflowingShort =
    OverflowingShort(wrappingMul(other), checkedMul(other) == null)

public fun Int.overflowingMul(other: Int): OverflowingInt =
    OverflowingInt(wrappingMul(other), checkedMul(other) == null)

public fun Long.overflowingMul(other: Long): OverflowingLong =
    OverflowingLong(wrappingMul(other), checkedMul(other) == null)

public fun UByte.overflowingMul(other: UByte): OverflowingUByte =
    OverflowingUByte(wrappingMul(other), checkedMul(other) == null)

public fun UShort.overflowingMul(other: UShort): OverflowingUShort =
    OverflowingUShort(wrappingMul(other), checkedMul(other) == null)

public fun UInt.overflowingMul(other: UInt): OverflowingUInt =
    OverflowingUInt(wrappingMul(other), checkedMul(other) == null)

public fun ULong.overflowingMul(other: ULong): OverflowingULong =
    OverflowingULong(wrappingMul(other), checkedMul(other) == null)
