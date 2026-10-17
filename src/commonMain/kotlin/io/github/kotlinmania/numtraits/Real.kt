// port-lint: source src/real.rs
package io.github.kotlinmania.numtraits

/**
 * An interface for real number types that do not necessarily have
 * floating-point-specific characteristics such as NaN and infinity.
 */
public interface Real<T> :
    Num<T>,
    NumCast<T> {
    public fun minValue(): T

    public fun minPositiveValue(): T

    public fun epsilon(): T

    public fun maxValue(): T

    public fun floor(value: T): T

    public fun ceil(value: T): T

    public fun round(value: T): T

    public fun trunc(value: T): T

    public fun fract(value: T): T

    public fun abs(value: T): T

    public fun signum(value: T): T

    public fun isSignPositive(value: T): Boolean

    public fun isSignNegative(value: T): Boolean

    public fun mulAdd(
        value: T,
        a: T,
        b: T,
    ): T

    public fun recip(value: T): T

    public fun powi(
        value: T,
        n: Int,
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

    public fun toDegrees(value: T): T

    public fun toRadians(value: T): T

    public fun max(
        a: T,
        b: T,
    ): T

    public fun min(
        a: T,
        b: T,
    ): T

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
}
