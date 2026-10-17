// port-lint: source src/int.rs
package io.github.kotlinmania.numtraits

import io.github.kotlinmania.numtraits.ops.CheckedAdd
import io.github.kotlinmania.numtraits.ops.CheckedDiv
import io.github.kotlinmania.numtraits.ops.CheckedMul
import io.github.kotlinmania.numtraits.ops.CheckedSub
import io.github.kotlinmania.numtraits.ops.Saturating

/**
 * Generic interface for primitive integers.
 */
public interface PrimInt<T> :
    Num<T>,
    NumCast<T>,
    Bounded<T>,
    CheckedAdd<T>,
    CheckedSub<T>,
    CheckedMul<T>,
    CheckedDiv<T>,
    Saturating<T> {
    /**
     * Bitwise NOT (`!value`).
     */
    public fun inv(value: T): T

    /**
     * Bitwise AND (`a and b`).
     */
    public fun bitAnd(
        a: T,
        b: T,
    ): T

    /**
     * Bitwise OR (`a or b`).
     */
    public fun bitOr(
        a: T,
        b: T,
    ): T

    /**
     * Bitwise XOR (`a xor b`).
     */
    public fun bitXor(
        a: T,
        b: T,
    ): T

    /**
     * Bitwise shift left (`a shl n`).
     */
    public fun shl(
        a: T,
        n: Int,
    ): T

    /**
     * Bitwise shift right (`a shr n`).
     */
    public fun shr(
        a: T,
        n: Int,
    ): T

    /**
     * Returns the number of ones in the binary representation of `value`.
     */
    public fun countOnes(value: T): UInt

    /**
     * Returns the number of zeros in the binary representation of `value`.
     */
    public fun countZeros(value: T): UInt

    /**
     * Returns the number of leading ones in the binary representation of `value`.
     */
    public fun leadingOnes(value: T): UInt = leadingZeros(inv(value))

    /**
     * Returns the number of leading zeros in the binary representation of `value`.
     */
    public fun leadingZeros(value: T): UInt

    /**
     * Returns the number of trailing ones in the binary representation of `value`.
     */
    public fun trailingOnes(value: T): UInt = trailingZeros(inv(value))

    /**
     * Returns the number of trailing zeros in the binary representation of `value`.
     */
    public fun trailingZeros(value: T): UInt

    /**
     * Shifts the bits to the left by a specified amount, `n`, wrapping
     * the truncated bits to the end of the resulting integer.
     */
    public fun rotateLeft(
        value: T,
        n: UInt,
    ): T

    /**
     * Shifts the bits to the right by a specified amount, `n`, wrapping
     * the truncated bits to the beginning of the resulting integer.
     */
    public fun rotateRight(
        value: T,
        n: UInt,
    ): T

    /**
     * Shifts the bits to the left by a specified amount, `n`, filling
     * zeros in the least significant bits.
     */
    public fun signedShl(
        value: T,
        n: UInt,
    ): T

    /**
     * Shifts the bits to the right by a specified amount, `n`, copying
     * the sign bit in the most significant bits even for unsigned types.
     */
    public fun signedShr(
        value: T,
        n: UInt,
    ): T

    /**
     * Shifts the bits to the left by a specified amount, `n`, filling
     * zeros in the least significant bits.
     */
    public fun unsignedShl(
        value: T,
        n: UInt,
    ): T

    /**
     * Shifts the bits to the right by a specified amount, `n`, filling
     * zeros in the most significant bits.
     */
    public fun unsignedShr(
        value: T,
        n: UInt,
    ): T

    /**
     * Reverses the byte order of the integer.
     */
    public fun swapBytes(value: T): T

    /**
     * Reverses the order of bits in the integer.
     */
    public fun reverseBits(value: T): T = reverseBitsFallback(this, value)

    /**
     * Convert an integer from big endian to the target's endianness.
     */
    public fun fromBe(x: T): T = swapBytes(x)

    /**
     * Convert an integer from little endian to the target's endianness.
     */
    public fun fromLe(x: T): T = x

    /**
     * Convert `value` to big endian from the target's endianness.
     */
    public fun toBe(value: T): T = swapBytes(value)

    /**
     * Convert `value` to little endian from the target's endianness.
     */
    public fun toLe(value: T): T = value

    /**
     * Raises `value` to the power of `exp`, using exponentiation by squaring.
     */
    public fun pow(
        value: T,
        exp: UInt,
    ): T = pow(this, value, exp.toInt())
}

internal fun <P> onePerByte(type: PrimInt<P>): P {
    var ret = type.one()
    var shift = 8
    var b = type.countZeros(ret) shr 3
    while (b != 0u) {
        ret = type.bitOr(type.shl(ret, shift), ret)
        shift = shift shl 1
        b = b shr 1
    }
    return ret
}

internal fun <P> reverseBitsFallback(
    type: PrimInt<P>,
    i: P,
): P {
    val rep01 = onePerByte(type)
    val rep03 = type.bitOr(type.shl(rep01, 1), rep01)
    val rep05 = type.bitOr(type.shl(rep01, 2), rep01)
    val rep0f = type.bitOr(type.shl(rep03, 2), rep03)
    val rep33 = type.bitOr(type.shl(rep03, 4), rep03)
    val rep55 = type.bitOr(type.shl(rep05, 4), rep05)

    var ret = type.swapBytes(i)
    ret = type.bitOr(type.shl(type.bitAnd(ret, rep0f), 4), type.bitAnd(type.unsignedShr(ret, 4u), rep0f))
    ret = type.bitOr(type.shl(type.bitAnd(ret, rep33), 2), type.bitAnd(type.unsignedShr(ret, 2u), rep33))
    ret = type.bitOr(type.shl(type.bitAnd(ret, rep55), 1), type.bitAnd(type.unsignedShr(ret, 1u), rep55))
    return ret
}

public fun Byte.swapBytes(): Byte = this

public fun UByte.swapBytes(): UByte = this

public fun Short.swapBytes(): Short {
    val u = this.toInt() and 0xFFFF
    return (((u and 0xFF) shl 8) or ((u ushr 8) and 0xFF)).toShort()
}

public fun UShort.swapBytes(): UShort = this.toShort().swapBytes().toUShort()

public fun Int.swapBytes(): Int =
    ((this and 0xFF) shl 24) or
        ((this and 0xFF00) shl 8) or
        ((this ushr 8) and 0xFF00) or
        ((this ushr 24) and 0xFF)

public fun UInt.swapBytes(): UInt = this.toInt().swapBytes().toUInt()

public fun Long.swapBytes(): Long =
    ((this and 0xFFL) shl 56) or
        ((this and 0xFF00L) shl 40) or
        ((this and 0xFF0000L) shl 24) or
        ((this and 0xFF000000L) shl 8) or
        ((this ushr 8) and 0xFF000000L) or
        ((this ushr 24) and 0xFF0000L) or
        ((this ushr 40) and 0xFF00L) or
        ((this ushr 56) and 0xFFL)

public fun ULong.swapBytes(): ULong = this.toLong().swapBytes().toULong()

public fun Byte.reverseBits(): Byte = I8Num.reverseBits(this)

public fun UByte.reverseBits(): UByte = U8Num.reverseBits(this)

public fun Short.reverseBits(): Short = I16Num.reverseBits(this)

public fun UShort.reverseBits(): UShort = U16Num.reverseBits(this)

public fun Int.reverseBits(): Int = I32Num.reverseBits(this)

public fun UInt.reverseBits(): UInt = U32Num.reverseBits(this)

public fun Long.reverseBits(): Long = I64Num.reverseBits(this)

public fun ULong.reverseBits(): ULong = U64Num.reverseBits(this)
