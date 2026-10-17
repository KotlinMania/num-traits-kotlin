// port-lint: source src/pow.rs
package io.github.kotlinmania.numtraits

import io.github.kotlinmania.numtraits.ops.CheckedMul

/**
 * Binary operator for raising a value to a power.
 */
public interface Pow<T, Rhs, Output> {
    /**
     * Returns `base` raised to the power `exp`.
     */
    public fun pow(
        base: T,
        exp: Rhs,
    ): Output
}

/**
 * Raises a value to the power of `exp`, using exponentiation by squaring.
 */
public fun <T> pow(
    type: Num<T>,
    baseValue: T,
    expValue: Int,
): T {
    require(expValue >= 0) { "exponent must be non-negative" }
    var exp = expValue
    if (exp == 0) return type.one()
    var base = baseValue
    while ((exp and 1) == 0) {
        base = type.mul(base, base)
        exp = exp ushr 1
    }
    if (exp == 1) return base
    var acc = base
    while (exp > 1) {
        exp = exp ushr 1
        base = type.mul(base, base)
        if ((exp and 1) == 1) {
            acc = type.mul(acc, base)
        }
    }
    return acc
}

/**
 * Raises a value to the power of `exp`, returning `null` if an overflow occurred.
 */
public fun <T> checkedPow(
    oneType: One<T>,
    checkedMulType: CheckedMul<T>,
    baseValue: T,
    expValue: Int,
): T? {
    require(expValue >= 0) { "exponent must be non-negative" }
    var exp = expValue
    if (exp == 0) return oneType.one()
    var base = baseValue
    while ((exp and 1) == 0) {
        base = checkedMulType.checkedMul(base, base) ?: return null
        exp = exp ushr 1
    }
    if (exp == 1) return base
    var acc = base
    while (exp > 1) {
        exp = exp ushr 1
        base = checkedMulType.checkedMul(base, base) ?: return null
        if ((exp and 1) == 1) {
            acc = checkedMulType.checkedMul(acc, base) ?: return null
        }
    }
    return acc
}
