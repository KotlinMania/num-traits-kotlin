// port-lint: source src/float.rs
package io.github.kotlinmania.numtraits

import kotlin.math.PI
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.truncate
import kotlin.math.withSign

/**
 * Floating-point category classification.
 */
public enum class FpCategory {
    Nan,
    Infinite,
    Zero,
    Subnormal,
    Normal,
}

/**
 * Decoded floating-point components `(mantissa, exponent, sign)` where the original number
 * can be recovered by `sign * mantissa * 2 ^ exponent`.
 */
public data class DecodedFloat(
    public val mantissa: ULong,
    public val exponent: Short,
    public val sign: Byte,
)

/**
 * Simultaneously computed sine and cosine of a `Float` value.
 */
public data class SinCosF32(
    public val sin: Float,
    public val cos: Float,
)

/**
 * Simultaneously computed sine and cosine of a `Double` value.
 */
public data class SinCosF64(
    public val sin: Double,
    public val cos: Double,
)

/**
 * Generic interface for core floating-point numbers.
 */
public interface FloatCore<T> :
    Num<T>,
    NumCast<T> {
    public fun infinity(): T

    public fun negInfinity(): T

    public fun nan(): T

    public fun negZero(): T

    public fun minValue(): T

    public fun minPositiveValue(): T

    public fun epsilon(): T

    public fun maxValue(): T

    public fun neg(value: T): T

    public fun compare(
        a: T,
        b: T,
    ): Int

    public fun isNan(value: T): Boolean = value != value

    public fun isInfinite(value: T): Boolean = value == infinity() || value == negInfinity()

    public fun isFinite(value: T): Boolean = !(isNan(value) || isInfinite(value))

    public fun isNormal(value: T): Boolean = classify(value) == FpCategory.Normal

    public fun classify(value: T): FpCategory

    public fun floor(value: T): T {
        val f = fract(value)
        return if (isNan(f) || isZero(f)) {
            value
        } else if (compare(value, zero()) < 0) {
            sub(sub(value, f), one())
        } else {
            sub(value, f)
        }
    }

    public fun ceil(value: T): T {
        val f = fract(value)
        return if (isNan(f) || isZero(f)) {
            value
        } else if (compare(value, zero()) > 0) {
            add(sub(value, f), one())
        } else {
            sub(value, f)
        }
    }

    public fun round(value: T): T {
        val oneVal = one()
        val h = from(F64Num, 0.5) ?: error("Unable to cast from 0.5")
        val f = fract(value)
        return if (isNan(f) || isZero(f)) {
            value
        } else if (compare(value, zero()) > 0) {
            if (compare(f, h) < 0) {
                sub(value, f)
            } else {
                add(sub(value, f), oneVal)
            }
        } else {
            if (compare(neg(f), h) < 0) {
                sub(value, f)
            } else {
                sub(sub(value, f), oneVal)
            }
        }
    }

    public fun trunc(value: T): T {
        val f = fract(value)
        return if (isNan(f)) {
            value
        } else {
            sub(value, f)
        }
    }

    public fun fract(value: T): T =
        if (isZero(value)) {
            zero()
        } else {
            rem(value, one())
        }

    public fun abs(value: T): T {
        if (isSignPositive(value)) return value
        if (isSignNegative(value)) return neg(value)
        return nan()
    }

    public fun signum(value: T): T =
        if (isNan(value)) {
            nan()
        } else if (isSignNegative(value)) {
            neg(one())
        } else {
            one()
        }

    public fun isSignPositive(value: T): Boolean = !isSignNegative(value)

    public fun isSignNegative(value: T): Boolean {
        val decoded = integerDecode(value)
        return decoded.sign < 0
    }

    public fun min(
        a: T,
        b: T,
    ): T {
        if (isNan(a)) return b
        if (isNan(b)) return a
        return if (compare(a, b) < 0) a else b
    }

    public fun max(
        a: T,
        b: T,
    ): T {
        if (isNan(a)) return b
        if (isNan(b)) return a
        return if (compare(a, b) > 0) a else b
    }

    public fun recip(value: T): T = div(one(), value)

    public fun powi(
        value: T,
        n: Int,
    ): T {
        var b = value
        var e = n.toLong()
        if (e < 0L) {
            e = -e
            b = recip(b)
        }
        var acc = one()
        var cur = b
        var remExp = e
        while (remExp > 0L) {
            if ((remExp and 1L) == 1L) {
                acc = mul(acc, cur)
            }
            remExp = remExp ushr 1
            if (remExp > 0L) {
                cur = mul(cur, cur)
            }
        }
        return acc
    }

    public fun toDegrees(value: T): T

    public fun toRadians(value: T): T

    public fun integerDecode(value: T): DecodedFloat
}

/**
 * Generic interface for floating-point numbers with transcendental functions.
 */
public interface NumFloat<T> : FloatCore<T> {
    public fun mulAdd(
        value: T,
        a: T,
        b: T,
    ): T

    public fun powf(
        value: T,
        n: T,
    ): T

    public fun sqrt(value: T): T

    public fun exp(value: T): T

    public fun exp2(value: T): T

    public fun ln(value: T): T

    public fun log(
        value: T,
        base: T,
    ): T

    public fun log2(value: T): T

    public fun log10(value: T): T

    public fun absSub(
        value: T,
        other: T,
    ): T

    public fun cbrt(value: T): T

    public fun hypot(
        value: T,
        other: T,
    ): T

    public fun sin(value: T): T

    public fun cos(value: T): T

    public fun tan(value: T): T

    public fun asin(value: T): T

    public fun acos(value: T): T

    public fun atan(value: T): T

    public fun atan2(
        value: T,
        other: T,
    ): T

    public fun expM1(value: T): T

    public fun ln1p(value: T): T

    public fun sinh(value: T): T

    public fun cosh(value: T): T

    public fun tanh(value: T): T

    public fun asinh(value: T): T

    public fun acosh(value: T): T

    public fun atanh(value: T): T

    public fun copysign(
        value: T,
        sign: T,
    ): T =
        if (isSignNegative(value) == isSignNegative(sign)) {
            value
        } else {
            neg(value)
        }
}

/**
 * Interface for floating-point mathematical constants.
 */
public interface FloatConst<T> {
    public fun e(): T

    public fun frac1Pi(): T

    public fun frac1Sqrt2(): T

    public fun frac2Pi(): T

    public fun frac2SqrtPi(): T

    public fun fracPi2(): T

    public fun fracPi3(): T

    public fun fracPi4(): T

    public fun fracPi6(): T

    public fun fracPi8(): T

    public fun ln10(): T

    public fun ln2(): T

    public fun log10E(): T

    public fun log2E(): T

    public fun pi(): T

    public fun sqrt2(): T

    public fun tau(): T

    public fun log102(): T

    public fun log210(): T
}

public fun integerDecodeF32(f: Float): DecodedFloat {
    val bits = f.toRawBits().toUInt()
    val sign: Byte = if ((bits shr 31) == 0u) 1 else -1
    var exponent: Int = ((bits shr 23) and 0xffu).toInt()
    val mantissa: UInt =
        if (exponent == 0) {
            (bits and 0x7fffffu) shl 1
        } else {
            (bits and 0x7fffffu) or 0x800000u
        }
    exponent -= 127 + 23
    return DecodedFloat(mantissa.toULong(), exponent.toShort(), sign)
}

public fun integerDecodeF64(f: Double): DecodedFloat {
    val bits = f.toRawBits().toULong()
    val sign: Byte = if ((bits shr 63) == 0uL) 1 else -1
    var exponent: Int = ((bits shr 52) and 0x7ffuL).toInt()
    val mantissa: ULong =
        if (exponent == 0) {
            (bits and 0xfffffffffffffuL) shl 1
        } else {
            (bits and 0xfffffffffffffuL) or 0x10000000000000uL
        }
    exponent -= 1023 + 52
    return DecodedFloat(mantissa, exponent.toShort(), sign)
}

public fun Float.integerDecode(): DecodedFloat = integerDecodeF32(this)

public fun Double.integerDecode(): DecodedFloat = integerDecodeF64(this)

public fun Float.classify(): FpCategory {
    val bits = this.toRawBits().toUInt()
    val expMask = 0x7f800000u
    val manMask = 0x007fffffu
    val man = bits and manMask
    val exp = bits and expMask
    return when {
        man == 0u && exp == 0u -> FpCategory.Zero
        exp == 0u -> FpCategory.Subnormal
        man == 0u && exp == expMask -> FpCategory.Infinite
        exp == expMask -> FpCategory.Nan
        else -> FpCategory.Normal
    }
}

public fun Double.classify(): FpCategory {
    val bits = this.toRawBits().toULong()
    val expMask = 0x7ff0000000000000uL
    val manMask = 0x000fffffffffffffuL
    val man = bits and manMask
    val exp = bits and expMask
    return when {
        man == 0uL && exp == 0uL -> FpCategory.Zero
        exp == 0uL -> FpCategory.Subnormal
        man == 0uL && exp == expMask -> FpCategory.Infinite
        exp == expMask -> FpCategory.Nan
        else -> FpCategory.Normal
    }
}

public fun Float.isNormal(): Boolean = classify() == FpCategory.Normal

public fun Double.isNormal(): Boolean = classify() == FpCategory.Normal

public fun Float.isSignPositive(): Boolean = !isSignNegative()

public fun Double.isSignPositive(): Boolean = !isSignNegative()

public fun Float.isSignNegative(): Boolean = (this.toRawBits().toUInt() and 0x80000000u) != 0u

public fun Double.isSignNegative(): Boolean = (this.toRawBits().toULong() and 0x8000000000000000uL) != 0uL

public fun Float.toDegrees(): Float = this * 57.2957795130823208767981548141051703f

public fun Double.toDegrees(): Double = this * (180.0 / PI)

public fun Float.toRadians(): Float = this * (PI.toFloat() / 180.0f)

public fun Double.toRadians(): Double = this * (PI / 180.0)

public fun Float.fract(): Float = if (this == 0.0f) 0.0f else this - truncate(this)

public fun Double.fract(): Double = if (this == 0.0) 0.0 else this - truncate(this)

public fun Float.roundHalfAwayFromZero(): Float {
    if (!this.isFinite() || this == 0.0f) return this
    return if (this > 0.0f) floor(this + 0.5f) else ceil(this - 0.5f)
}

public fun Double.roundHalfAwayFromZero(): Double {
    if (!this.isFinite() || this == 0.0) return this
    return if (this > 0.0) floor(this + 0.5) else ceil(this - 0.5)
}

public fun Float.recip(): Float = 1.0f / this

public fun Double.recip(): Double = 1.0 / this

public fun Float.powi(n: Int): Float = F32Num.powi(this, n)

public fun Double.powi(n: Int): Double = F64Num.powi(this, n)

public fun Float.powf(n: Float): Float = this.pow(n)

public fun Double.powf(n: Double): Double = this.pow(n)

public fun Float.exp2(): Float = 2.0f.pow(this)

public fun Double.exp2(): Double = 2.0.pow(this)

public fun Float.sinCos(): SinCosF32 = SinCosF32(sin(this), cos(this))

public fun Double.sinCos(): SinCosF64 = SinCosF64(sin(this), cos(this))

public fun Float.copysign(sign: Float): Float = this.withSign(sign)

public fun Double.copysign(sign: Double): Double = this.withSign(sign)
