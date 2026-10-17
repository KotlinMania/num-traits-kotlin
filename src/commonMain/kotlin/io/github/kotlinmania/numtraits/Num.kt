package io.github.kotlinmania.numtraits

import io.github.kotlinmania.numtraits.ops.mulAdd
import io.github.kotlinmania.numtraits.ops.saturatingAdd
import io.github.kotlinmania.numtraits.ops.saturatingSub
import kotlin.math.E
import kotlin.math.PI
import kotlin.math.expm1
import kotlin.math.pow
import kotlin.math.truncate

/**
 * The interface for types implementing basic numeric operations (`+`, `-`, `*`, `/`, `%`).
 */
public interface NumOps<T> {
    public fun add(
        a: T,
        b: T,
    ): T

    public fun sub(
        a: T,
        b: T,
    ): T

    public fun mul(
        a: T,
        b: T,
    ): T

    public fun div(
        a: T,
        b: T,
    ): T

    public fun rem(
        a: T,
        b: T,
    ): T
}

/**
 * The base interface for numeric types, built on [Zero], [One], and [NumOps].
 */
public interface Num<T> :
    Zero<T>,
    One<T>,
    NumOps<T> {
    /**
     * Convert from a string and radix in `2..36`, returning `null` if parsing fails.
     */
    public fun fromStrRadix(
        str: String,
        radix: UInt,
    ): T?
}

/**
 * The interface for `Num` types which also implement assignment operators.
 */
public interface NumAssignOps<T> : NumOps<T>

/**
 * The interface for `Num` types which also implement assignment operators.
 */
public interface NumAssign<T> :
    Num<T>,
    NumAssignOps<T>

/**
 * The interface for `NumAssign` types which also implement assignment operations taking
 * the second operand by reference.
 */
public interface NumAssignRef<T> : NumAssign<T>

/**
 * The interface for `Num` types which also implement numeric operations taking the second
 * operand by reference.
 */
public interface NumRef<T> : Num<T>

/**
 * The interface for `Num` references which implement numeric operations.
 */
public interface RefNum<T> : NumOps<T>

/**
 * Error kind returned when parsing a floating-point number with a custom radix fails.
 */
public enum class FloatErrorKind {
    Empty,
    Invalid,
}

/**
 * Error value describing why parsing a floating-point number from a string failed.
 */
public data class ParseFloatError(
    public val kind: FloatErrorKind,
) {
    override fun toString(): String =
        when (kind) {
            FloatErrorKind.Empty -> "cannot parse float from empty string"
            FloatErrorKind.Invalid -> "invalid float literal"
        }
}

internal fun strToAsciiLowerEq(
    a: String,
    b: String,
): Boolean =
    a.length == b.length &&
        a.indices.all { idx ->
            val ca = a[idx]
            val cb = b[idx]
            val lowerA = if (ca in 'A'..'Z') (ca.code + 32).toChar() else ca
            lowerA == cb
        }

/**
 * Parse a floating-point value of type [T] from `src` in base `radix` (`2..36`).
 */
public fun <T> floatFromStrRadix(
    type: FloatCore<T>,
    src: String,
    radix: UInt,
): T? {
    require(radix in 2u..36u) {
        "fromStrRadix: must lie in the range [2, 36] - found $radix"
    }

    if (radix == 10u) {
        val parsed = src.toDoubleOrNull()
        if (parsed != null) {
            return type.from(F64Num, parsed)
        }
    }

    if (strToAsciiLowerEq(src, "inf") || strToAsciiLowerEq(src, "infinity")) {
        return type.infinity()
    } else if (strToAsciiLowerEq(src, "-inf") || strToAsciiLowerEq(src, "-infinity")) {
        return type.negInfinity()
    } else if (strToAsciiLowerEq(src, "nan")) {
        return type.nan()
    } else if (strToAsciiLowerEq(src, "-nan")) {
        return type.neg(type.nan())
    }

    var isPositive = true
    val slice =
        if (src.startsWith('-')) {
            isPositive = false
            val rest = src.substring(1)
            if (strToAsciiLowerEq(rest, "inf") || strToAsciiLowerEq(rest, "infinity")) {
                return type.negInfinity()
            } else if (strToAsciiLowerEq(rest, "nan")) {
                return type.neg(type.nan())
            }
            rest
        } else if (src.startsWith('+')) {
            val rest = src.substring(1)
            if (strToAsciiLowerEq(rest, "inf") || strToAsciiLowerEq(rest, "infinity")) {
                return type.infinity()
            } else if (strToAsciiLowerEq(rest, "nan")) {
                return type.nan()
            }
            rest
        } else {
            src
        }

    if (slice.isEmpty()) {
        return null
    }

    var sig = if (isPositive) type.zero() else type.negZero()
    var prevSig = sig
    var csIdx = 0
    val radixT = type.from(U32Num, radix) ?: return null

    var sawDigit = false
    while (csIdx < slice.length) {
        val c = slice[csIdx]
        csIdx += 1
        val digit = c.digitToIntOrNull(radix.toInt())
        if (digit != null) {
            sawDigit = true
            sig = type.mul(sig, radixT)
            val dVal = type.from(I32Num, digit) ?: return null
            sig = if (isPositive) type.add(sig, dVal) else type.sub(sig, dVal)
            if (type.isInfinite(sig)) {
                while (csIdx < slice.length) {
                    val remChar = slice[csIdx]
                    csIdx += 1
                    if (remChar.digitToIntOrNull(radix.toInt()) == null) {
                        return if (remChar == '.') {
                            val rest = slice.substring(csIdx)
                            var hasExp = false
                            for (rc in rest) {
                                if (rc.digitToIntOrNull(radix.toInt()) == null) {
                                    hasExp = true
                                    break
                                }
                            }
                            if (hasExp) null else sig
                        } else {
                            null
                        }
                    }
                }
                return sig
            }
            prevSig = sig
        } else {
            when (c) {
                'e', 'E', 'p', 'P' -> return parseFloatExponent(type, sig, isPositive, sawDigit, false, 0, c, slice.substring(csIdx), radix)
                '.' -> return parseFloatFraction(type, sig, prevSig, isPositive, sawDigit, slice.substring(csIdx), radix, radixT)
                else -> return null
            }
        }
    }

    return sig
}

private fun <T> parseFloatFraction(
    type: FloatCore<T>,
    initialSig: T,
    initialPrevSig: T,
    isPositive: Boolean,
    initialSawDigit: Boolean,
    rest: String,
    radix: UInt,
    radixT: T,
): T? {
    var sig = initialSig
    var prevSig = initialPrevSig
    var sawDigit = initialSawDigit
    var power = type.one()
    var csIdx = 0
    while (csIdx < rest.length) {
        val c = rest[csIdx]
        csIdx += 1
        val digit = c.digitToIntOrNull(radix.toInt())
        if (digit != null) {
            sawDigit = true
            val dVal = type.from(I32Num, digit) ?: return null
            power = type.div(power, radixT)
            sig =
                if (isPositive) {
                    type.add(sig, type.mul(dVal, power))
                } else {
                    type.sub(sig, type.mul(dVal, power))
                }
            if (type.isInfinite(sig)) {
                for (remIdx in csIdx until rest.length) {
                    if (rest[remIdx].digitToIntOrNull(radix.toInt()) == null) {
                        return null
                    }
                }
                return sig
            }
            prevSig = sig
        } else {
            return when (c) {
                'e', 'E', 'p', 'P' -> parseFloatExponent(type, sig, isPositive, sawDigit, true, 0, c, rest.substring(csIdx), radix)
                else -> null
            }
        }
    }
    return if (sawDigit) sig else null
}

private fun <T> parseFloatExponent(
    type: FloatCore<T>,
    initialSig: T,
    isPositive: Boolean,
    sawDigit: Boolean,
    hasDecPoint: Boolean,
    unprocessedDigits: Int,
    expChar: Char,
    expSliceRaw: String,
    radix: UInt,
): T? {
    if (!sawDigit) return null
    val base =
        when {
            (expChar == 'e' || expChar == 'E') && radix <= 10u -> type.from(I32Num, 10) ?: return null
            (expChar == 'p' || expChar == 'P') && radix <= 25u -> type.from(I32Num, 2) ?: return null
            else -> return null
        }

    var expIsPositive = true
    val expSlice =
        if (expSliceRaw.startsWith('-')) {
            expIsPositive = false
            expSliceRaw.substring(1)
        } else if (expSliceRaw.startsWith('+')) {
            expSliceRaw.substring(1)
        } else {
            expSliceRaw
        }

    if (expSlice.isEmpty()) return null
    var parsedExp = 0L
    for (c in expSlice) {
        val d = c.digitToIntOrNull(radix.toInt()) ?: return null
        parsedExp = parsedExp * radix.toLong() + d.toLong()
        if (parsedExp > Int.MAX_VALUE.toLong()) {
            return if (expIsPositive) {
                when {
                    type.isZero(initialSig) -> initialSig
                    isPositive -> type.infinity()
                    else -> type.negInfinity()
                }
            } else {
                if (isPositive) type.zero() else type.negZero()
            }
        }
    }

    val finalExp = (if (expIsPositive) parsedExp else -parsedExp) + unprocessedDigits.toLong()
    return if (finalExp >= 0L) {
        type.mul(initialSig, type.powi(base, finalExp.toInt()))
    } else {
        type.div(initialSig, type.powi(base, (-finalExp).toInt()))
    }
}

public object I8Num :
    PrimInt<Byte>,
    Signed<Byte>,
    ConstZero<Byte>,
    ConstOne<Byte>,
    NumAssign<Byte> {
    override val zeroConstant: Byte = 0
    override val oneConstant: Byte = 1

    override fun zero(): Byte = 0

    override fun isZero(value: Byte): Boolean = value == 0.toByte()

    override fun one(): Byte = 1

    override fun isOne(value: Byte): Boolean = value == 1.toByte()

    override fun minValue(): Byte = Byte.MIN_VALUE

    override fun maxValue(): Byte = Byte.MAX_VALUE

    override fun add(
        a: Byte,
        b: Byte,
    ): Byte = (a + b).toByte()

    override fun sub(
        a: Byte,
        b: Byte,
    ): Byte = (a - b).toByte()

    override fun mul(
        a: Byte,
        b: Byte,
    ): Byte = (a * b).toByte()

    override fun div(
        a: Byte,
        b: Byte,
    ): Byte = (a / b).toByte()

    override fun rem(
        a: Byte,
        b: Byte,
    ): Byte = (a % b).toByte()

    override fun fromStrRadix(
        str: String,
        radix: UInt,
    ): Byte? {
        require(radix in 2u..36u) { "fromStrRadix: radix must lie in [2, 36]" }
        return str.toByteOrNull(radix.toInt())
    }

    override fun abs(value: Byte): Byte = value.absVal()

    override fun absSub(
        value: Byte,
        other: Byte,
    ): Byte = value.absSub(other)

    override fun signum(value: Byte): Byte = value.signumVal()

    override fun isPositive(value: Byte): Boolean = value > 0

    override fun isNegative(value: Byte): Boolean = value < 0

    override fun toI64(value: Byte): Long = value.toLong()

    override fun toU64(value: Byte): ULong? = if (value < 0) null else value.toULong()

    override fun <U> from(
        sourceType: ToPrimitive<U>,
        n: U,
    ): Byte? = sourceType.toI8(n)

    override fun checkedAdd(
        a: Byte,
        b: Byte,
    ): Byte? = a.checkedAdd(b)

    override fun checkedSub(
        a: Byte,
        b: Byte,
    ): Byte? = a.checkedSub(b)

    override fun checkedMul(
        a: Byte,
        b: Byte,
    ): Byte? = a.checkedMul(b)

    override fun checkedDiv(
        a: Byte,
        b: Byte,
    ): Byte? = a.checkedDiv(b)

    override fun saturatingAdd(
        a: Byte,
        b: Byte,
    ): Byte = a.saturatingAdd(b)

    override fun saturatingSub(
        a: Byte,
        b: Byte,
    ): Byte = a.saturatingSub(b)

    override fun inv(value: Byte): Byte = value.toInt().inv().toByte()

    override fun bitAnd(
        a: Byte,
        b: Byte,
    ): Byte = (a.toInt() and b.toInt()).toByte()

    override fun bitOr(
        a: Byte,
        b: Byte,
    ): Byte = (a.toInt() or b.toInt()).toByte()

    override fun bitXor(
        a: Byte,
        b: Byte,
    ): Byte = (a.toInt() xor b.toInt()).toByte()

    override fun shl(
        a: Byte,
        n: Int,
    ): Byte = (a.toInt() shl n).toByte()

    override fun shr(
        a: Byte,
        n: Int,
    ): Byte = (a.toInt() shr n).toByte()

    override fun countOnes(value: Byte): UInt = value.countOneBits().toUInt()

    override fun countZeros(value: Byte): UInt = (8 - value.countOneBits()).toUInt()

    override fun leadingZeros(value: Byte): UInt = value.countLeadingZeroBits().toUInt()

    override fun trailingZeros(value: Byte): UInt = value.countTrailingZeroBits().toUInt()

    override fun rotateLeft(
        value: Byte,
        n: UInt,
    ): Byte = value.rotateLeft(n.toInt())

    override fun rotateRight(
        value: Byte,
        n: UInt,
    ): Byte = value.rotateRight(n.toInt())

    override fun signedShl(
        value: Byte,
        n: UInt,
    ): Byte = (value.toInt() shl (n.toInt() and 7)).toByte()

    override fun signedShr(
        value: Byte,
        n: UInt,
    ): Byte = (value.toInt() shr (n.toInt() and 7)).toByte()

    override fun unsignedShl(
        value: Byte,
        n: UInt,
    ): Byte = ((value.toInt() and 0xFF) shl (n.toInt() and 7)).toByte()

    override fun unsignedShr(
        value: Byte,
        n: UInt,
    ): Byte = ((value.toInt() and 0xFF) ushr (n.toInt() and 7)).toByte()

    override fun swapBytes(value: Byte): Byte = value
}

public object I16Num :
    PrimInt<Short>,
    Signed<Short>,
    ConstZero<Short>,
    ConstOne<Short>,
    NumAssign<Short> {
    override val zeroConstant: Short = 0
    override val oneConstant: Short = 1

    override fun zero(): Short = 0

    override fun isZero(value: Short): Boolean = value == 0.toShort()

    override fun one(): Short = 1

    override fun isOne(value: Short): Boolean = value == 1.toShort()

    override fun minValue(): Short = Short.MIN_VALUE

    override fun maxValue(): Short = Short.MAX_VALUE

    override fun add(
        a: Short,
        b: Short,
    ): Short = (a + b).toShort()

    override fun sub(
        a: Short,
        b: Short,
    ): Short = (a - b).toShort()

    override fun mul(
        a: Short,
        b: Short,
    ): Short = (a * b).toShort()

    override fun div(
        a: Short,
        b: Short,
    ): Short = (a / b).toShort()

    override fun rem(
        a: Short,
        b: Short,
    ): Short = (a % b).toShort()

    override fun fromStrRadix(
        str: String,
        radix: UInt,
    ): Short? {
        require(radix in 2u..36u) { "fromStrRadix: radix must lie in [2, 36]" }
        return str.toShortOrNull(radix.toInt())
    }

    override fun abs(value: Short): Short = value.absVal()

    override fun absSub(
        value: Short,
        other: Short,
    ): Short = value.absSub(other)

    override fun signum(value: Short): Short = value.signumVal()

    override fun isPositive(value: Short): Boolean = value > 0

    override fun isNegative(value: Short): Boolean = value < 0

    override fun toI64(value: Short): Long = value.toLong()

    override fun toU64(value: Short): ULong? = if (value < 0) null else value.toULong()

    override fun <U> from(
        sourceType: ToPrimitive<U>,
        n: U,
    ): Short? = sourceType.toI16(n)

    override fun checkedAdd(
        a: Short,
        b: Short,
    ): Short? = a.checkedAdd(b)

    override fun checkedSub(
        a: Short,
        b: Short,
    ): Short? = a.checkedSub(b)

    override fun checkedMul(
        a: Short,
        b: Short,
    ): Short? = a.checkedMul(b)

    override fun checkedDiv(
        a: Short,
        b: Short,
    ): Short? = a.checkedDiv(b)

    override fun saturatingAdd(
        a: Short,
        b: Short,
    ): Short = a.saturatingAdd(b)

    override fun saturatingSub(
        a: Short,
        b: Short,
    ): Short = a.saturatingSub(b)

    override fun inv(value: Short): Short = value.toInt().inv().toShort()

    override fun bitAnd(
        a: Short,
        b: Short,
    ): Short = (a.toInt() and b.toInt()).toShort()

    override fun bitOr(
        a: Short,
        b: Short,
    ): Short = (a.toInt() or b.toInt()).toShort()

    override fun bitXor(
        a: Short,
        b: Short,
    ): Short = (a.toInt() xor b.toInt()).toShort()

    override fun shl(
        a: Short,
        n: Int,
    ): Short = (a.toInt() shl n).toShort()

    override fun shr(
        a: Short,
        n: Int,
    ): Short = (a.toInt() shr n).toShort()

    override fun countOnes(value: Short): UInt = value.countOneBits().toUInt()

    override fun countZeros(value: Short): UInt = (16 - value.countOneBits()).toUInt()

    override fun leadingZeros(value: Short): UInt = value.countLeadingZeroBits().toUInt()

    override fun trailingZeros(value: Short): UInt = value.countTrailingZeroBits().toUInt()

    override fun rotateLeft(
        value: Short,
        n: UInt,
    ): Short = value.rotateLeft(n.toInt())

    override fun rotateRight(
        value: Short,
        n: UInt,
    ): Short = value.rotateRight(n.toInt())

    override fun signedShl(
        value: Short,
        n: UInt,
    ): Short = (value.toInt() shl (n.toInt() and 15)).toShort()

    override fun signedShr(
        value: Short,
        n: UInt,
    ): Short = (value.toInt() shr (n.toInt() and 15)).toShort()

    override fun unsignedShl(
        value: Short,
        n: UInt,
    ): Short = ((value.toInt() and 0xFFFF) shl (n.toInt() and 15)).toShort()

    override fun unsignedShr(
        value: Short,
        n: UInt,
    ): Short = ((value.toInt() and 0xFFFF) ushr (n.toInt() and 15)).toShort()

    override fun swapBytes(value: Short): Short = value.swapBytes()
}

public object I32Num :
    PrimInt<Int>,
    Signed<Int>,
    ConstZero<Int>,
    ConstOne<Int>,
    NumAssign<Int> {
    override val zeroConstant: Int = 0
    override val oneConstant: Int = 1

    override fun zero(): Int = 0

    override fun isZero(value: Int): Boolean = value == 0

    override fun one(): Int = 1

    override fun isOne(value: Int): Boolean = value == 1

    override fun minValue(): Int = Int.MIN_VALUE

    override fun maxValue(): Int = Int.MAX_VALUE

    override fun add(
        a: Int,
        b: Int,
    ): Int = a + b

    override fun sub(
        a: Int,
        b: Int,
    ): Int = a - b

    override fun mul(
        a: Int,
        b: Int,
    ): Int = a * b

    override fun div(
        a: Int,
        b: Int,
    ): Int = a / b

    override fun rem(
        a: Int,
        b: Int,
    ): Int = a % b

    override fun fromStrRadix(
        str: String,
        radix: UInt,
    ): Int? {
        require(radix in 2u..36u) { "fromStrRadix: radix must lie in [2, 36]" }
        return str.toIntOrNull(radix.toInt())
    }

    override fun abs(value: Int): Int = value.absVal()

    override fun absSub(
        value: Int,
        other: Int,
    ): Int = value.absSub(other)

    override fun signum(value: Int): Int = value.signumVal()

    override fun isPositive(value: Int): Boolean = value > 0

    override fun isNegative(value: Int): Boolean = value < 0

    override fun toI64(value: Int): Long = value.toLong()

    override fun toU64(value: Int): ULong? = if (value < 0) null else value.toULong()

    override fun <U> from(
        sourceType: ToPrimitive<U>,
        n: U,
    ): Int? = sourceType.toI32(n)

    override fun checkedAdd(
        a: Int,
        b: Int,
    ): Int? = a.checkedAdd(b)

    override fun checkedSub(
        a: Int,
        b: Int,
    ): Int? = a.checkedSub(b)

    override fun checkedMul(
        a: Int,
        b: Int,
    ): Int? = a.checkedMul(b)

    override fun checkedDiv(
        a: Int,
        b: Int,
    ): Int? = a.checkedDiv(b)

    override fun saturatingAdd(
        a: Int,
        b: Int,
    ): Int = a.saturatingAdd(b)

    override fun saturatingSub(
        a: Int,
        b: Int,
    ): Int = a.saturatingSub(b)

    override fun inv(value: Int): Int = value.inv()

    override fun bitAnd(
        a: Int,
        b: Int,
    ): Int = a and b

    override fun bitOr(
        a: Int,
        b: Int,
    ): Int = a or b

    override fun bitXor(
        a: Int,
        b: Int,
    ): Int = a xor b

    override fun shl(
        a: Int,
        n: Int,
    ): Int = a shl n

    override fun shr(
        a: Int,
        n: Int,
    ): Int = a shr n

    override fun countOnes(value: Int): UInt = value.countOneBits().toUInt()

    override fun countZeros(value: Int): UInt = (32 - value.countOneBits()).toUInt()

    override fun leadingZeros(value: Int): UInt = value.countLeadingZeroBits().toUInt()

    override fun trailingZeros(value: Int): UInt = value.countTrailingZeroBits().toUInt()

    override fun rotateLeft(
        value: Int,
        n: UInt,
    ): Int = value.rotateLeft(n.toInt())

    override fun rotateRight(
        value: Int,
        n: UInt,
    ): Int = value.rotateRight(n.toInt())

    override fun signedShl(
        value: Int,
        n: UInt,
    ): Int = value shl (n.toInt() and 31)

    override fun signedShr(
        value: Int,
        n: UInt,
    ): Int = value shr (n.toInt() and 31)

    override fun unsignedShl(
        value: Int,
        n: UInt,
    ): Int = value shl (n.toInt() and 31)

    override fun unsignedShr(
        value: Int,
        n: UInt,
    ): Int = value ushr (n.toInt() and 31)

    override fun swapBytes(value: Int): Int = value.swapBytes()
}

public object I64Num :
    PrimInt<Long>,
    Signed<Long>,
    ConstZero<Long>,
    ConstOne<Long>,
    NumAssign<Long> {
    override val zeroConstant: Long = 0L
    override val oneConstant: Long = 1L

    override fun zero(): Long = 0L

    override fun isZero(value: Long): Boolean = value == 0L

    override fun one(): Long = 1L

    override fun isOne(value: Long): Boolean = value == 1L

    override fun minValue(): Long = Long.MIN_VALUE

    override fun maxValue(): Long = Long.MAX_VALUE

    override fun add(
        a: Long,
        b: Long,
    ): Long = a + b

    override fun sub(
        a: Long,
        b: Long,
    ): Long = a - b

    override fun mul(
        a: Long,
        b: Long,
    ): Long = a * b

    override fun div(
        a: Long,
        b: Long,
    ): Long = a / b

    override fun rem(
        a: Long,
        b: Long,
    ): Long = a % b

    override fun fromStrRadix(
        str: String,
        radix: UInt,
    ): Long? {
        require(radix in 2u..36u) { "fromStrRadix: radix must lie in [2, 36]" }
        return str.toLongOrNull(radix.toInt())
    }

    override fun abs(value: Long): Long = value.absVal()

    override fun absSub(
        value: Long,
        other: Long,
    ): Long = value.absSub(other)

    override fun signum(value: Long): Long = value.signumVal()

    override fun isPositive(value: Long): Boolean = value > 0L

    override fun isNegative(value: Long): Boolean = value < 0L

    override fun toI64(value: Long): Long = value

    override fun toU64(value: Long): ULong? = if (value < 0L) null else value.toULong()

    override fun toF32(value: Long): Float = value.toFloat()

    override fun toF64(value: Long): Double = value.toDouble()

    override fun <U> from(
        sourceType: ToPrimitive<U>,
        n: U,
    ): Long? = sourceType.toI64(n)

    override fun checkedAdd(
        a: Long,
        b: Long,
    ): Long? = a.checkedAdd(b)

    override fun checkedSub(
        a: Long,
        b: Long,
    ): Long? = a.checkedSub(b)

    override fun checkedMul(
        a: Long,
        b: Long,
    ): Long? = a.checkedMul(b)

    override fun checkedDiv(
        a: Long,
        b: Long,
    ): Long? = a.checkedDiv(b)

    override fun saturatingAdd(
        a: Long,
        b: Long,
    ): Long = a.saturatingAdd(b)

    override fun saturatingSub(
        a: Long,
        b: Long,
    ): Long = a.saturatingSub(b)

    override fun inv(value: Long): Long = value.inv()

    override fun bitAnd(
        a: Long,
        b: Long,
    ): Long = a and b

    override fun bitOr(
        a: Long,
        b: Long,
    ): Long = a or b

    override fun bitXor(
        a: Long,
        b: Long,
    ): Long = a xor b

    override fun shl(
        a: Long,
        n: Int,
    ): Long = a shl n

    override fun shr(
        a: Long,
        n: Int,
    ): Long = a shr n

    override fun countOnes(value: Long): UInt = value.countOneBits().toUInt()

    override fun countZeros(value: Long): UInt = (64 - value.countOneBits()).toUInt()

    override fun leadingZeros(value: Long): UInt = value.countLeadingZeroBits().toUInt()

    override fun trailingZeros(value: Long): UInt = value.countTrailingZeroBits().toUInt()

    override fun rotateLeft(
        value: Long,
        n: UInt,
    ): Long = value.rotateLeft(n.toInt())

    override fun rotateRight(
        value: Long,
        n: UInt,
    ): Long = value.rotateRight(n.toInt())

    override fun signedShl(
        value: Long,
        n: UInt,
    ): Long = value shl (n.toInt() and 63)

    override fun signedShr(
        value: Long,
        n: UInt,
    ): Long = value shr (n.toInt() and 63)

    override fun unsignedShl(
        value: Long,
        n: UInt,
    ): Long = value shl (n.toInt() and 63)

    override fun unsignedShr(
        value: Long,
        n: UInt,
    ): Long = value ushr (n.toInt() and 63)

    override fun swapBytes(value: Long): Long = value.swapBytes()
}

public object U8Num :
    PrimInt<UByte>,
    Unsigned<UByte>,
    ConstZero<UByte>,
    ConstOne<UByte>,
    NumAssign<UByte> {
    override val zeroConstant: UByte = 0u
    override val oneConstant: UByte = 1u

    override fun zero(): UByte = 0u

    override fun isZero(value: UByte): Boolean = value == 0.toUByte()

    override fun one(): UByte = 1u

    override fun isOne(value: UByte): Boolean = value == 1.toUByte()

    override fun minValue(): UByte = UByte.MIN_VALUE

    override fun maxValue(): UByte = UByte.MAX_VALUE

    override fun add(
        a: UByte,
        b: UByte,
    ): UByte = (a + b).toUByte()

    override fun sub(
        a: UByte,
        b: UByte,
    ): UByte = (a - b).toUByte()

    override fun mul(
        a: UByte,
        b: UByte,
    ): UByte = (a * b).toUByte()

    override fun div(
        a: UByte,
        b: UByte,
    ): UByte = (a / b).toUByte()

    override fun rem(
        a: UByte,
        b: UByte,
    ): UByte = (a % b).toUByte()

    override fun fromStrRadix(
        str: String,
        radix: UInt,
    ): UByte? {
        require(radix in 2u..36u) { "fromStrRadix: radix must lie in [2, 36]" }
        return str.toUByteOrNull(radix.toInt())
    }

    override fun toI64(value: UByte): Long = value.toLong()

    override fun toU64(value: UByte): ULong = value.toULong()

    override fun <U> from(
        sourceType: ToPrimitive<U>,
        n: U,
    ): UByte? = sourceType.toU8(n)

    override fun checkedAdd(
        a: UByte,
        b: UByte,
    ): UByte? = a.checkedAdd(b)

    override fun checkedSub(
        a: UByte,
        b: UByte,
    ): UByte? = a.checkedSub(b)

    override fun checkedMul(
        a: UByte,
        b: UByte,
    ): UByte? = a.checkedMul(b)

    override fun checkedDiv(
        a: UByte,
        b: UByte,
    ): UByte? = a.checkedDiv(b)

    override fun saturatingAdd(
        a: UByte,
        b: UByte,
    ): UByte = a.saturatingAdd(b)

    override fun saturatingSub(
        a: UByte,
        b: UByte,
    ): UByte = a.saturatingSub(b)

    override fun inv(value: UByte): UByte = value.inv()

    override fun bitAnd(
        a: UByte,
        b: UByte,
    ): UByte = a and b

    override fun bitOr(
        a: UByte,
        b: UByte,
    ): UByte = a or b

    override fun bitXor(
        a: UByte,
        b: UByte,
    ): UByte = a xor b

    override fun shl(
        a: UByte,
        n: Int,
    ): UByte = (a.toUInt() shl n).toUByte()

    override fun shr(
        a: UByte,
        n: Int,
    ): UByte = (a.toUInt() shr n).toUByte()

    override fun countOnes(value: UByte): UInt = value.countOneBits().toUInt()

    override fun countZeros(value: UByte): UInt = (8 - value.countOneBits()).toUInt()

    override fun leadingZeros(value: UByte): UInt = value.countLeadingZeroBits().toUInt()

    override fun trailingZeros(value: UByte): UInt = value.countTrailingZeroBits().toUInt()

    override fun rotateLeft(
        value: UByte,
        n: UInt,
    ): UByte = value.rotateLeft(n.toInt())

    override fun rotateRight(
        value: UByte,
        n: UInt,
    ): UByte = value.rotateRight(n.toInt())

    override fun signedShl(
        value: UByte,
        n: UInt,
    ): UByte = (value.toByte().toInt() shl (n.toInt() and 7)).toUByte()

    override fun signedShr(
        value: UByte,
        n: UInt,
    ): UByte = (value.toByte().toInt() shr (n.toInt() and 7)).toUByte()

    override fun unsignedShl(
        value: UByte,
        n: UInt,
    ): UByte = (value.toUInt() shl (n.toInt() and 7)).toUByte()

    override fun unsignedShr(
        value: UByte,
        n: UInt,
    ): UByte = (value.toUInt() shr (n.toInt() and 7)).toUByte()

    override fun swapBytes(value: UByte): UByte = value
}

public object U16Num :
    PrimInt<UShort>,
    Unsigned<UShort>,
    ConstZero<UShort>,
    ConstOne<UShort>,
    NumAssign<UShort> {
    override val zeroConstant: UShort = 0u
    override val oneConstant: UShort = 1u

    override fun zero(): UShort = 0u

    override fun isZero(value: UShort): Boolean = value == 0.toUShort()

    override fun one(): UShort = 1u

    override fun isOne(value: UShort): Boolean = value == 1.toUShort()

    override fun minValue(): UShort = UShort.MIN_VALUE

    override fun maxValue(): UShort = UShort.MAX_VALUE

    override fun add(
        a: UShort,
        b: UShort,
    ): UShort = (a + b).toUShort()

    override fun sub(
        a: UShort,
        b: UShort,
    ): UShort = (a - b).toUShort()

    override fun mul(
        a: UShort,
        b: UShort,
    ): UShort = (a * b).toUShort()

    override fun div(
        a: UShort,
        b: UShort,
    ): UShort = (a / b).toUShort()

    override fun rem(
        a: UShort,
        b: UShort,
    ): UShort = (a % b).toUShort()

    override fun fromStrRadix(
        str: String,
        radix: UInt,
    ): UShort? {
        require(radix in 2u..36u) { "fromStrRadix: radix must lie in [2, 36]" }
        return str.toUShortOrNull(radix.toInt())
    }

    override fun toI64(value: UShort): Long = value.toLong()

    override fun toU64(value: UShort): ULong = value.toULong()

    override fun <U> from(
        sourceType: ToPrimitive<U>,
        n: U,
    ): UShort? = sourceType.toU16(n)

    override fun checkedAdd(
        a: UShort,
        b: UShort,
    ): UShort? = a.checkedAdd(b)

    override fun checkedSub(
        a: UShort,
        b: UShort,
    ): UShort? = a.checkedSub(b)

    override fun checkedMul(
        a: UShort,
        b: UShort,
    ): UShort? = a.checkedMul(b)

    override fun checkedDiv(
        a: UShort,
        b: UShort,
    ): UShort? = a.checkedDiv(b)

    override fun saturatingAdd(
        a: UShort,
        b: UShort,
    ): UShort = a.saturatingAdd(b)

    override fun saturatingSub(
        a: UShort,
        b: UShort,
    ): UShort = a.saturatingSub(b)

    override fun inv(value: UShort): UShort = value.inv()

    override fun bitAnd(
        a: UShort,
        b: UShort,
    ): UShort = a and b

    override fun bitOr(
        a: UShort,
        b: UShort,
    ): UShort = a or b

    override fun bitXor(
        a: UShort,
        b: UShort,
    ): UShort = a xor b

    override fun shl(
        a: UShort,
        n: Int,
    ): UShort = (a.toUInt() shl n).toUShort()

    override fun shr(
        a: UShort,
        n: Int,
    ): UShort = (a.toUInt() shr n).toUShort()

    override fun countOnes(value: UShort): UInt = value.countOneBits().toUInt()

    override fun countZeros(value: UShort): UInt = (16 - value.countOneBits()).toUInt()

    override fun leadingZeros(value: UShort): UInt = value.countLeadingZeroBits().toUInt()

    override fun trailingZeros(value: UShort): UInt = value.countTrailingZeroBits().toUInt()

    override fun rotateLeft(
        value: UShort,
        n: UInt,
    ): UShort = value.rotateLeft(n.toInt())

    override fun rotateRight(
        value: UShort,
        n: UInt,
    ): UShort = value.rotateRight(n.toInt())

    override fun signedShl(
        value: UShort,
        n: UInt,
    ): UShort = (value.toShort().toInt() shl (n.toInt() and 15)).toUShort()

    override fun signedShr(
        value: UShort,
        n: UInt,
    ): UShort = (value.toShort().toInt() shr (n.toInt() and 15)).toUShort()

    override fun unsignedShl(
        value: UShort,
        n: UInt,
    ): UShort = (value.toUInt() shl (n.toInt() and 15)).toUShort()

    override fun unsignedShr(
        value: UShort,
        n: UInt,
    ): UShort = (value.toUInt() shr (n.toInt() and 15)).toUShort()

    override fun swapBytes(value: UShort): UShort = value.swapBytes()
}

public object U32Num :
    PrimInt<UInt>,
    Unsigned<UInt>,
    ConstZero<UInt>,
    ConstOne<UInt>,
    NumAssign<UInt> {
    override val zeroConstant: UInt = 0u
    override val oneConstant: UInt = 1u

    override fun zero(): UInt = 0u

    override fun isZero(value: UInt): Boolean = value == 0u

    override fun one(): UInt = 1u

    override fun isOne(value: UInt): Boolean = value == 1u

    override fun minValue(): UInt = UInt.MIN_VALUE

    override fun maxValue(): UInt = UInt.MAX_VALUE

    override fun add(
        a: UInt,
        b: UInt,
    ): UInt = a + b

    override fun sub(
        a: UInt,
        b: UInt,
    ): UInt = a - b

    override fun mul(
        a: UInt,
        b: UInt,
    ): UInt = a * b

    override fun div(
        a: UInt,
        b: UInt,
    ): UInt = a / b

    override fun rem(
        a: UInt,
        b: UInt,
    ): UInt = a % b

    override fun fromStrRadix(
        str: String,
        radix: UInt,
    ): UInt? {
        require(radix in 2u..36u) { "fromStrRadix: radix must lie in [2, 36]" }
        return str.toUIntOrNull(radix.toInt())
    }

    override fun toI64(value: UInt): Long = value.toLong()

    override fun toU64(value: UInt): ULong = value.toULong()

    override fun <U> from(
        sourceType: ToPrimitive<U>,
        n: U,
    ): UInt? = sourceType.toU32(n)

    override fun checkedAdd(
        a: UInt,
        b: UInt,
    ): UInt? = a.checkedAdd(b)

    override fun checkedSub(
        a: UInt,
        b: UInt,
    ): UInt? = a.checkedSub(b)

    override fun checkedMul(
        a: UInt,
        b: UInt,
    ): UInt? = a.checkedMul(b)

    override fun checkedDiv(
        a: UInt,
        b: UInt,
    ): UInt? = a.checkedDiv(b)

    override fun saturatingAdd(
        a: UInt,
        b: UInt,
    ): UInt = a.saturatingAdd(b)

    override fun saturatingSub(
        a: UInt,
        b: UInt,
    ): UInt = a.saturatingSub(b)

    override fun inv(value: UInt): UInt = value.inv()

    override fun bitAnd(
        a: UInt,
        b: UInt,
    ): UInt = a and b

    override fun bitOr(
        a: UInt,
        b: UInt,
    ): UInt = a or b

    override fun bitXor(
        a: UInt,
        b: UInt,
    ): UInt = a xor b

    override fun shl(
        a: UInt,
        n: Int,
    ): UInt = a shl n

    override fun shr(
        a: UInt,
        n: Int,
    ): UInt = a shr n

    override fun countOnes(value: UInt): UInt = value.countOneBits().toUInt()

    override fun countZeros(value: UInt): UInt = (32 - value.countOneBits()).toUInt()

    override fun leadingZeros(value: UInt): UInt = value.countLeadingZeroBits().toUInt()

    override fun trailingZeros(value: UInt): UInt = value.countTrailingZeroBits().toUInt()

    override fun rotateLeft(
        value: UInt,
        n: UInt,
    ): UInt = value.rotateLeft(n.toInt())

    override fun rotateRight(
        value: UInt,
        n: UInt,
    ): UInt = value.rotateRight(n.toInt())

    override fun signedShl(
        value: UInt,
        n: UInt,
    ): UInt = (value.toInt() shl (n.toInt() and 31)).toUInt()

    override fun signedShr(
        value: UInt,
        n: UInt,
    ): UInt = (value.toInt() shr (n.toInt() and 31)).toUInt()

    override fun unsignedShl(
        value: UInt,
        n: UInt,
    ): UInt = value shl (n.toInt() and 31)

    override fun unsignedShr(
        value: UInt,
        n: UInt,
    ): UInt = value shr (n.toInt() and 31)

    override fun swapBytes(value: UInt): UInt = value.swapBytes()
}

public object U64Num :
    PrimInt<ULong>,
    Unsigned<ULong>,
    ConstZero<ULong>,
    ConstOne<ULong>,
    NumAssign<ULong> {
    override val zeroConstant: ULong = 0uL
    override val oneConstant: ULong = 1uL

    override fun zero(): ULong = 0uL

    override fun isZero(value: ULong): Boolean = value == 0uL

    override fun one(): ULong = 1uL

    override fun isOne(value: ULong): Boolean = value == 1uL

    override fun minValue(): ULong = ULong.MIN_VALUE

    override fun maxValue(): ULong = ULong.MAX_VALUE

    override fun add(
        a: ULong,
        b: ULong,
    ): ULong = a + b

    override fun sub(
        a: ULong,
        b: ULong,
    ): ULong = a - b

    override fun mul(
        a: ULong,
        b: ULong,
    ): ULong = a * b

    override fun div(
        a: ULong,
        b: ULong,
    ): ULong = a / b

    override fun rem(
        a: ULong,
        b: ULong,
    ): ULong = a % b

    override fun fromStrRadix(
        str: String,
        radix: UInt,
    ): ULong? {
        require(radix in 2u..36u) { "fromStrRadix: radix must lie in [2, 36]" }
        return str.toULongOrNull(radix.toInt())
    }

    override fun toI64(value: ULong): Long? = if (value > Long.MAX_VALUE.toULong()) null else value.toLong()

    override fun toU64(value: ULong): ULong = value

    override fun toF32(value: ULong): Float = value.toFloat()

    override fun toF64(value: ULong): Double = value.toDouble()

    override fun <U> from(
        sourceType: ToPrimitive<U>,
        n: U,
    ): ULong? = sourceType.toU64(n)

    override fun checkedAdd(
        a: ULong,
        b: ULong,
    ): ULong? = a.checkedAdd(b)

    override fun checkedSub(
        a: ULong,
        b: ULong,
    ): ULong? = a.checkedSub(b)

    override fun checkedMul(
        a: ULong,
        b: ULong,
    ): ULong? = a.checkedMul(b)

    override fun checkedDiv(
        a: ULong,
        b: ULong,
    ): ULong? = a.checkedDiv(b)

    override fun saturatingAdd(
        a: ULong,
        b: ULong,
    ): ULong = a.saturatingAdd(b)

    override fun saturatingSub(
        a: ULong,
        b: ULong,
    ): ULong = a.saturatingSub(b)

    override fun inv(value: ULong): ULong = value.inv()

    override fun bitAnd(
        a: ULong,
        b: ULong,
    ): ULong = a and b

    override fun bitOr(
        a: ULong,
        b: ULong,
    ): ULong = a or b

    override fun bitXor(
        a: ULong,
        b: ULong,
    ): ULong = a xor b

    override fun shl(
        a: ULong,
        n: Int,
    ): ULong = a shl n

    override fun shr(
        a: ULong,
        n: Int,
    ): ULong = a shr n

    override fun countOnes(value: ULong): UInt = value.countOneBits().toUInt()

    override fun countZeros(value: ULong): UInt = (64 - value.countOneBits()).toUInt()

    override fun leadingZeros(value: ULong): UInt = value.countLeadingZeroBits().toUInt()

    override fun trailingZeros(value: ULong): UInt = value.countTrailingZeroBits().toUInt()

    override fun rotateLeft(
        value: ULong,
        n: UInt,
    ): ULong = value.rotateLeft(n.toInt())

    override fun rotateRight(
        value: ULong,
        n: UInt,
    ): ULong = value.rotateRight(n.toInt())

    override fun signedShl(
        value: ULong,
        n: UInt,
    ): ULong = (value.toLong() shl (n.toInt() and 63)).toULong()

    override fun signedShr(
        value: ULong,
        n: UInt,
    ): ULong = (value.toLong() shr (n.toInt() and 63)).toULong()

    override fun unsignedShl(
        value: ULong,
        n: UInt,
    ): ULong = value shl (n.toInt() and 63)

    override fun unsignedShr(
        value: ULong,
        n: UInt,
    ): ULong = value shr (n.toInt() and 63)

    override fun swapBytes(value: ULong): ULong = value.swapBytes()
}

public object F32Num :
    NumFloat<Float>,
    Real<Float>,
    Signed<Float>,
    Bounded<Float>,
    FloatConst<Float>,
    ConstZero<Float>,
    ConstOne<Float>,
    NumAssign<Float> {
    override val zeroConstant: Float = 0.0f
    override val oneConstant: Float = 1.0f

    override fun zero(): Float = 0.0f

    override fun isZero(value: Float): Boolean = value == 0.0f

    override fun one(): Float = 1.0f

    override fun isOne(value: Float): Boolean = value == 1.0f

    override fun add(
        a: Float,
        b: Float,
    ): Float = a + b

    override fun sub(
        a: Float,
        b: Float,
    ): Float = a - b

    override fun mul(
        a: Float,
        b: Float,
    ): Float = a * b

    override fun div(
        a: Float,
        b: Float,
    ): Float = a / b

    override fun rem(
        a: Float,
        b: Float,
    ): Float = a % b

    override fun fromStrRadix(
        str: String,
        radix: UInt,
    ): Float? = floatFromStrRadix(this, str, radix)

    override fun infinity(): Float = Float.POSITIVE_INFINITY

    override fun negInfinity(): Float = Float.NEGATIVE_INFINITY

    override fun nan(): Float = Float.NaN

    override fun negZero(): Float = -0.0f

    override fun minValue(): Float = -Float.MAX_VALUE

    override fun minPositiveValue(): Float = Float.fromBits(0x00800000)

    override fun epsilon(): Float = 1.1920929e-7f

    override fun maxValue(): Float = Float.MAX_VALUE

    override fun neg(value: Float): Float = -value

    override fun compare(
        a: Float,
        b: Float,
    ): Int = a.compareTo(b)

    override fun isNan(value: Float): Boolean = value.isNaN()

    override fun isInfinite(value: Float): Boolean = value.isInfinite()

    override fun isFinite(value: Float): Boolean = value.isFinite()

    override fun isNormal(value: Float): Boolean = value.isNormal()

    override fun classify(value: Float): FpCategory = value.classify()

    override fun floor(value: Float): Float = kotlin.math.floor(value)

    override fun ceil(value: Float): Float = kotlin.math.ceil(value)

    override fun round(value: Float): Float = value.roundHalfAwayFromZero()

    override fun trunc(value: Float): Float = truncate(value)

    override fun fract(value: Float): Float = value.fract()

    override fun abs(value: Float): Float = value.absVal()

    override fun absSub(
        value: Float,
        other: Float,
    ): Float = value.absSub(other)

    override fun signum(value: Float): Float = value.signumVal()

    override fun isPositive(value: Float): Boolean = value.isPositive()

    override fun isNegative(value: Float): Boolean = value.isNegative()

    override fun isSignPositive(value: Float): Boolean = value.isSignPositive()

    override fun isSignNegative(value: Float): Boolean = value.isSignNegative()

    override fun min(
        a: Float,
        b: Float,
    ): Float =
        when {
            a.isNaN() -> b
            b.isNaN() -> a
            else -> kotlin.math.min(a, b)
        }

    override fun max(
        a: Float,
        b: Float,
    ): Float =
        when {
            a.isNaN() -> b
            b.isNaN() -> a
            else -> kotlin.math.max(a, b)
        }

    override fun recip(value: Float): Float = 1.0f / value

    override fun toDegrees(value: Float): Float = value.toDegrees()

    override fun toRadians(value: Float): Float = value.toRadians()

    override fun integerDecode(value: Float): DecodedFloat = integerDecodeF32(value)

    override fun mulAdd(
        value: Float,
        a: Float,
        b: Float,
    ): Float = value.mulAdd(a, b)

    override fun powf(
        value: Float,
        n: Float,
    ): Float = value.pow(n)

    override fun sqrt(value: Float): Float = kotlin.math.sqrt(value)

    override fun exp(value: Float): Float = kotlin.math.exp(value)

    override fun exp2(value: Float): Float = 2.0f.pow(value)

    override fun ln(value: Float): Float = kotlin.math.ln(value)

    override fun log(
        value: Float,
        base: Float,
    ): Float = kotlin.math.log(value, base)

    override fun log2(value: Float): Float = kotlin.math.log2(value)

    override fun log10(value: Float): Float = kotlin.math.log10(value)

    override fun cbrt(value: Float): Float = kotlin.math.cbrt(value)

    override fun hypot(
        value: Float,
        other: Float,
    ): Float = kotlin.math.hypot(value, other)

    override fun sin(value: Float): Float = kotlin.math.sin(value)

    override fun cos(value: Float): Float = kotlin.math.cos(value)

    override fun tan(value: Float): Float = kotlin.math.tan(value)

    override fun asin(value: Float): Float = kotlin.math.asin(value)

    override fun acos(value: Float): Float = kotlin.math.acos(value)

    override fun atan(value: Float): Float = kotlin.math.atan(value)

    override fun atan2(
        value: Float,
        other: Float,
    ): Float = kotlin.math.atan2(value, other)

    override fun expM1(value: Float): Float = expm1(value)

    override fun ln1p(value: Float): Float = kotlin.math.ln1p(value)

    override fun sinh(value: Float): Float = kotlin.math.sinh(value)

    override fun cosh(value: Float): Float = kotlin.math.cosh(value)

    override fun tanh(value: Float): Float = kotlin.math.tanh(value)

    override fun asinh(value: Float): Float = kotlin.math.asinh(value)

    override fun acosh(value: Float): Float = kotlin.math.acosh(value)

    override fun atanh(value: Float): Float = kotlin.math.atanh(value)

    override fun copysign(
        value: Float,
        sign: Float,
    ): Float = value.copysign(sign)

    override fun toI8(value: Float): Byte? = floatToI8Checked(value)

    override fun toI16(value: Float): Short? = floatToI16Checked(value)

    override fun toI32(value: Float): Int? = floatToI32Checked(value)

    override fun toI64(value: Float): Long? = floatToI64Checked(value)

    override fun toU8(value: Float): UByte? = floatToU8Checked(value)

    override fun toU16(value: Float): UShort? = floatToU16Checked(value)

    override fun toU32(value: Float): UInt? = floatToU32Checked(value)

    override fun toU64(value: Float): ULong? = floatToU64Checked(value)

    override fun toF32(value: Float): Float = value

    override fun toF64(value: Float): Double = value.toDouble()

    override fun <U> from(
        sourceType: ToPrimitive<U>,
        n: U,
    ): Float? = sourceType.toF32(n)

    override fun e(): Float = E.toFloat()

    override fun frac1Pi(): Float = (1.0 / PI).toFloat()

    override fun frac1Sqrt2(): Float = (1.0 / kotlin.math.sqrt(2.0)).toFloat()

    override fun frac2Pi(): Float = (2.0 / PI).toFloat()

    override fun frac2SqrtPi(): Float = (2.0 / kotlin.math.sqrt(PI)).toFloat()

    override fun fracPi2(): Float = (PI / 2.0).toFloat()

    override fun fracPi3(): Float = (PI / 3.0).toFloat()

    override fun fracPi4(): Float = (PI / 4.0).toFloat()

    override fun fracPi6(): Float = (PI / 6.0).toFloat()

    override fun fracPi8(): Float = (PI / 8.0).toFloat()

    override fun ln10(): Float = kotlin.math.ln(10.0).toFloat()

    override fun ln2(): Float = kotlin.math.ln(2.0).toFloat()

    override fun log10E(): Float = kotlin.math.log10(E).toFloat()

    override fun log2E(): Float = kotlin.math.log2(E).toFloat()

    override fun pi(): Float = PI.toFloat()

    override fun sqrt2(): Float = kotlin.math.sqrt(2.0).toFloat()

    override fun tau(): Float = (2.0 * PI).toFloat()

    override fun log102(): Float = 0.301029995663981195213738894724493027f

    override fun log210(): Float = 3.32192809488736234787031942948939018f
}

public object F64Num :
    NumFloat<Double>,
    Real<Double>,
    Signed<Double>,
    Bounded<Double>,
    FloatConst<Double>,
    ConstZero<Double>,
    ConstOne<Double>,
    NumAssign<Double> {
    override val zeroConstant: Double = 0.0
    override val oneConstant: Double = 1.0

    override fun zero(): Double = 0.0

    override fun isZero(value: Double): Boolean = value == 0.0

    override fun one(): Double = 1.0

    override fun isOne(value: Double): Boolean = value == 1.0

    override fun add(
        a: Double,
        b: Double,
    ): Double = a + b

    override fun sub(
        a: Double,
        b: Double,
    ): Double = a - b

    override fun mul(
        a: Double,
        b: Double,
    ): Double = a * b

    override fun div(
        a: Double,
        b: Double,
    ): Double = a / b

    override fun rem(
        a: Double,
        b: Double,
    ): Double = a % b

    override fun fromStrRadix(
        str: String,
        radix: UInt,
    ): Double? = floatFromStrRadix(this, str, radix)

    override fun infinity(): Double = Double.POSITIVE_INFINITY

    override fun negInfinity(): Double = Double.NEGATIVE_INFINITY

    override fun nan(): Double = Double.NaN

    override fun negZero(): Double = -0.0

    override fun minValue(): Double = -Double.MAX_VALUE

    override fun minPositiveValue(): Double = Double.fromBits(0x0010000000000000L)

    override fun epsilon(): Double = 2.220446049250313e-16

    override fun maxValue(): Double = Double.MAX_VALUE

    override fun neg(value: Double): Double = -value

    override fun compare(
        a: Double,
        b: Double,
    ): Int = a.compareTo(b)

    override fun isNan(value: Double): Boolean = value.isNaN()

    override fun isInfinite(value: Double): Boolean = value.isInfinite()

    override fun isFinite(value: Double): Boolean = value.isFinite()

    override fun isNormal(value: Double): Boolean = value.isNormal()

    override fun classify(value: Double): FpCategory = value.classify()

    override fun floor(value: Double): Double = kotlin.math.floor(value)

    override fun ceil(value: Double): Double = kotlin.math.ceil(value)

    override fun round(value: Double): Double = value.roundHalfAwayFromZero()

    override fun trunc(value: Double): Double = truncate(value)

    override fun fract(value: Double): Double = value.fract()

    override fun abs(value: Double): Double = value.absVal()

    override fun absSub(
        value: Double,
        other: Double,
    ): Double = value.absSub(other)

    override fun signum(value: Double): Double = value.signumVal()

    override fun isPositive(value: Double): Boolean = value.isPositive()

    override fun isNegative(value: Double): Boolean = value.isNegative()

    override fun isSignPositive(value: Double): Boolean = value.isSignPositive()

    override fun isSignNegative(value: Double): Boolean = value.isSignNegative()

    override fun min(
        a: Double,
        b: Double,
    ): Double =
        when {
            a.isNaN() -> b
            b.isNaN() -> a
            else -> kotlin.math.min(a, b)
        }

    override fun max(
        a: Double,
        b: Double,
    ): Double =
        when {
            a.isNaN() -> b
            b.isNaN() -> a
            else -> kotlin.math.max(a, b)
        }

    override fun recip(value: Double): Double = 1.0 / value

    override fun toDegrees(value: Double): Double = value.toDegrees()

    override fun toRadians(value: Double): Double = value.toRadians()

    override fun integerDecode(value: Double): DecodedFloat = integerDecodeF64(value)

    override fun mulAdd(
        value: Double,
        a: Double,
        b: Double,
    ): Double = value.mulAdd(a, b)

    override fun powf(
        value: Double,
        n: Double,
    ): Double = value.pow(n)

    override fun sqrt(value: Double): Double = kotlin.math.sqrt(value)

    override fun exp(value: Double): Double = kotlin.math.exp(value)

    override fun exp2(value: Double): Double = 2.0.pow(value)

    override fun ln(value: Double): Double = kotlin.math.ln(value)

    override fun log(
        value: Double,
        base: Double,
    ): Double = kotlin.math.log(value, base)

    override fun log2(value: Double): Double = kotlin.math.log2(value)

    override fun log10(value: Double): Double = kotlin.math.log10(value)

    override fun cbrt(value: Double): Double = kotlin.math.cbrt(value)

    override fun hypot(
        value: Double,
        other: Double,
    ): Double = kotlin.math.hypot(value, other)

    override fun sin(value: Double): Double = kotlin.math.sin(value)

    override fun cos(value: Double): Double = kotlin.math.cos(value)

    override fun tan(value: Double): Double = kotlin.math.tan(value)

    override fun asin(value: Double): Double = kotlin.math.asin(value)

    override fun acos(value: Double): Double = kotlin.math.acos(value)

    override fun atan(value: Double): Double = kotlin.math.atan(value)

    override fun atan2(
        value: Double,
        other: Double,
    ): Double = kotlin.math.atan2(value, other)

    override fun expM1(value: Double): Double = expm1(value)

    override fun ln1p(value: Double): Double = kotlin.math.ln1p(value)

    override fun sinh(value: Double): Double = kotlin.math.sinh(value)

    override fun cosh(value: Double): Double = kotlin.math.cosh(value)

    override fun tanh(value: Double): Double = kotlin.math.tanh(value)

    override fun asinh(value: Double): Double = kotlin.math.asinh(value)

    override fun acosh(value: Double): Double = kotlin.math.acosh(value)

    override fun atanh(value: Double): Double = kotlin.math.atanh(value)

    override fun copysign(
        value: Double,
        sign: Double,
    ): Double = value.copysign(sign)

    override fun toI8(value: Double): Byte? = doubleToI8Checked(value)

    override fun toI16(value: Double): Short? = doubleToI16Checked(value)

    override fun toI32(value: Double): Int? = doubleToI32Checked(value)

    override fun toI64(value: Double): Long? = doubleToI64Checked(value)

    override fun toU8(value: Double): UByte? = doubleToU8Checked(value)

    override fun toU16(value: Double): UShort? = doubleToU16Checked(value)

    override fun toU32(value: Double): UInt? = doubleToU32Checked(value)

    override fun toU64(value: Double): ULong? = doubleToU64Checked(value)

    override fun toF32(value: Double): Float =
        when {
            value > Float.MAX_VALUE.toDouble() -> Float.POSITIVE_INFINITY
            value < -Float.MAX_VALUE.toDouble() -> Float.NEGATIVE_INFINITY
            else -> value.toFloat()
        }

    override fun toF64(value: Double): Double = value

    override fun <U> from(
        sourceType: ToPrimitive<U>,
        n: U,
    ): Double? = sourceType.toF64(n)

    override fun e(): Double = E

    override fun frac1Pi(): Double = 1.0 / PI

    override fun frac1Sqrt2(): Double = 1.0 / kotlin.math.sqrt(2.0)

    override fun frac2Pi(): Double = 2.0 / PI

    override fun frac2SqrtPi(): Double = 2.0 / kotlin.math.sqrt(PI)

    override fun fracPi2(): Double = PI / 2.0

    override fun fracPi3(): Double = PI / 3.0

    override fun fracPi4(): Double = PI / 4.0

    override fun fracPi6(): Double = PI / 6.0

    override fun fracPi8(): Double = PI / 8.0

    override fun ln10(): Double = kotlin.math.ln(10.0)

    override fun ln2(): Double = kotlin.math.ln(2.0)

    override fun log10E(): Double = kotlin.math.log10(E)

    override fun log2E(): Double = kotlin.math.log2(E)

    override fun pi(): Double = PI

    override fun sqrt2(): Double = kotlin.math.sqrt(2.0)

    override fun tau(): Double = 2.0 * PI

    override fun log102(): Double = 0.301029995663981195213738894724493027

    override fun log210(): Double = 3.32192809488736234787031942948939018
}

public fun Byte.checkedAdd(other: Byte): Byte? {
    val r = this.toInt() + other.toInt()
    return if (r in Byte.MIN_VALUE..Byte.MAX_VALUE) r.toByte() else null
}

public fun Short.checkedAdd(other: Short): Short? {
    val r = this.toInt() + other.toInt()
    return if (r in Short.MIN_VALUE..Short.MAX_VALUE) r.toShort() else null
}

public fun Int.checkedAdd(other: Int): Int? {
    val r = this.toLong() + other.toLong()
    return if (r in Int.MIN_VALUE.toLong()..Int.MAX_VALUE.toLong()) r.toInt() else null
}

public fun Long.checkedAdd(other: Long): Long? {
    val r = this + other
    return if (((this xor r) and (other xor r)) < 0L) null else r
}

public fun UByte.checkedAdd(other: UByte): UByte? {
    val r = this.toUInt() + other.toUInt()
    return if (r <= UByte.MAX_VALUE.toUInt()) r.toUByte() else null
}

public fun UShort.checkedAdd(other: UShort): UShort? {
    val r = this.toUInt() + other.toUInt()
    return if (r <= UShort.MAX_VALUE.toUInt()) r.toUShort() else null
}

public fun UInt.checkedAdd(other: UInt): UInt? {
    val r = this.toULong() + other.toULong()
    return if (r <= UInt.MAX_VALUE.toULong()) r.toUInt() else null
}

public fun ULong.checkedAdd(other: ULong): ULong? {
    val r = this + other
    return if (r < this) null else r
}

public fun Byte.checkedSub(other: Byte): Byte? {
    val r = this.toInt() - other.toInt()
    return if (r in Byte.MIN_VALUE..Byte.MAX_VALUE) r.toByte() else null
}

public fun Short.checkedSub(other: Short): Short? {
    val r = this.toInt() - other.toInt()
    return if (r in Short.MIN_VALUE..Short.MAX_VALUE) r.toShort() else null
}

public fun Int.checkedSub(other: Int): Int? {
    val r = this.toLong() - other.toLong()
    return if (r in Int.MIN_VALUE.toLong()..Int.MAX_VALUE.toLong()) r.toInt() else null
}

public fun Long.checkedSub(other: Long): Long? {
    val r = this - other
    return if (((this xor other) and (this xor r)) < 0L) null else r
}

public fun UByte.checkedSub(other: UByte): UByte? = if (this < other) null else (this - other).toUByte()

public fun UShort.checkedSub(other: UShort): UShort? = if (this < other) null else (this - other).toUShort()

public fun UInt.checkedSub(other: UInt): UInt? = if (this < other) null else this - other

public fun ULong.checkedSub(other: ULong): ULong? = if (this < other) null else this - other

public fun Byte.checkedMul(other: Byte): Byte? {
    val r = this.toInt() * other.toInt()
    return if (r in Byte.MIN_VALUE..Byte.MAX_VALUE) r.toByte() else null
}

public fun Short.checkedMul(other: Short): Short? {
    val r = this.toInt() * other.toInt()
    return if (r in Short.MIN_VALUE..Short.MAX_VALUE) r.toShort() else null
}

public fun Int.checkedMul(other: Int): Int? {
    val r = this.toLong() * other.toLong()
    return if (r in Int.MIN_VALUE.toLong()..Int.MAX_VALUE.toLong()) r.toInt() else null
}

public fun Long.checkedMul(other: Long): Long? {
    if (this == 0L || other == 0L) return 0L
    if (this == Long.MIN_VALUE && other == -1L) return null
    if (other == Long.MIN_VALUE && this == -1L) return null
    val r = this * other
    return if (r / other == this) r else null
}

public fun UByte.checkedMul(other: UByte): UByte? {
    val r = this.toUInt() * other.toUInt()
    return if (r <= UByte.MAX_VALUE.toUInt()) r.toUByte() else null
}

public fun UShort.checkedMul(other: UShort): UShort? {
    val r = this.toUInt() * other.toUInt()
    return if (r <= UShort.MAX_VALUE.toUInt()) r.toUShort() else null
}

public fun UInt.checkedMul(other: UInt): UInt? {
    val r = this.toULong() * other.toULong()
    return if (r <= UInt.MAX_VALUE.toULong()) r.toUInt() else null
}

public fun ULong.checkedMul(other: ULong): ULong? {
    if (this == 0uL || other == 0uL) return 0uL
    val r = this * other
    return if (r / other == this) r else null
}

public fun Byte.checkedDiv(other: Byte): Byte? =
    if (other == 0.toByte() || (this == Byte.MIN_VALUE && other == (-1).toByte())) null else (this / other).toByte()

public fun Short.checkedDiv(other: Short): Short? =
    if (other == 0.toShort() || (this == Short.MIN_VALUE && other == (-1).toShort())) null else (this / other).toShort()

public fun Int.checkedDiv(other: Int): Int? =
    if (other == 0 || (this == Int.MIN_VALUE && other == -1)) null else this / other

public fun Long.checkedDiv(other: Long): Long? =
    if (other == 0L || (this == Long.MIN_VALUE && other == -1L)) null else this / other

public fun UByte.checkedDiv(other: UByte): UByte? = if (other == 0.toUByte()) null else (this / other).toUByte()

public fun UShort.checkedDiv(other: UShort): UShort? = if (other == 0.toUShort()) null else (this / other).toUShort()

public fun UInt.checkedDiv(other: UInt): UInt? = if (other == 0u) null else this / other

public fun ULong.checkedDiv(other: ULong): ULong? = if (other == 0uL) null else this / other

public fun Byte.checkedRem(other: Byte): Byte? =
    if (other == 0.toByte() || (this == Byte.MIN_VALUE && other == (-1).toByte())) null else (this % other).toByte()

public fun Short.checkedRem(other: Short): Short? =
    if (other == 0.toShort() || (this == Short.MIN_VALUE && other == (-1).toShort())) null else (this % other).toShort()

public fun Int.checkedRem(other: Int): Int? =
    if (other == 0 || (this == Int.MIN_VALUE && other == -1)) null else this % other

public fun Long.checkedRem(other: Long): Long? =
    if (other == 0L || (this == Long.MIN_VALUE && other == -1L)) null else this % other

public fun UByte.checkedRem(other: UByte): UByte? = if (other == 0.toUByte()) null else (this % other).toUByte()

public fun UShort.checkedRem(other: UShort): UShort? = if (other == 0.toUShort()) null else (this % other).toUShort()

public fun UInt.checkedRem(other: UInt): UInt? = if (other == 0u) null else this % other

public fun ULong.checkedRem(other: ULong): ULong? = if (other == 0uL) null else this % other

public fun Byte.checkedNeg(): Byte? = if (this == Byte.MIN_VALUE) null else (-this.toInt()).toByte()

public fun Short.checkedNeg(): Short? = if (this == Short.MIN_VALUE) null else (-this.toInt()).toShort()

public fun Int.checkedNeg(): Int? = if (this == Int.MIN_VALUE) null else -this

public fun Long.checkedNeg(): Long? = if (this == Long.MIN_VALUE) null else -this

public fun UByte.checkedNeg(): UByte? = if (this == 0.toUByte()) 0.toUByte() else null

public fun UShort.checkedNeg(): UShort? = if (this == 0.toUShort()) 0.toUShort() else null

public fun UInt.checkedNeg(): UInt? = if (this == 0u) 0u else null

public fun ULong.checkedNeg(): ULong? = if (this == 0uL) 0uL else null

public fun Byte.checkedShl(rhs: UInt): Byte? = if (rhs >= 8u) null else (this.toInt() shl rhs.toInt()).toByte()

public fun Short.checkedShl(rhs: UInt): Short? = if (rhs >= 16u) null else (this.toInt() shl rhs.toInt()).toShort()

public fun Int.checkedShl(rhs: UInt): Int? = if (rhs >= 32u) null else this shl rhs.toInt()

public fun Long.checkedShl(rhs: UInt): Long? = if (rhs >= 64u) null else this shl rhs.toInt()

public fun UByte.checkedShl(rhs: UInt): UByte? = if (rhs >= 8u) null else (this.toUInt() shl rhs.toInt()).toUByte()

public fun UShort.checkedShl(rhs: UInt): UShort? = if (rhs >= 16u) null else (this.toUInt() shl rhs.toInt()).toUShort()

public fun UInt.checkedShl(rhs: UInt): UInt? = if (rhs >= 32u) null else this shl rhs.toInt()

public fun ULong.checkedShl(rhs: UInt): ULong? = if (rhs >= 64u) null else this shl rhs.toInt()

public fun Byte.checkedShr(rhs: UInt): Byte? = if (rhs >= 8u) null else (this.toInt() shr rhs.toInt()).toByte()

public fun Short.checkedShr(rhs: UInt): Short? = if (rhs >= 16u) null else (this.toInt() shr rhs.toInt()).toShort()

public fun Int.checkedShr(rhs: UInt): Int? = if (rhs >= 32u) null else this shr rhs.toInt()

public fun Long.checkedShr(rhs: UInt): Long? = if (rhs >= 64u) null else this shr rhs.toInt()

public fun UByte.checkedShr(rhs: UInt): UByte? = if (rhs >= 8u) null else (this.toUInt() shr rhs.toInt()).toUByte()

public fun UShort.checkedShr(rhs: UInt): UShort? = if (rhs >= 16u) null else (this.toUInt() shr rhs.toInt()).toUShort()

public fun UInt.checkedShr(rhs: UInt): UInt? = if (rhs >= 32u) null else this shr rhs.toInt()

public fun ULong.checkedShr(rhs: UInt): ULong? = if (rhs >= 64u) null else this shr rhs.toInt()
