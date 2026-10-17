// port-lint: source src/ops/checked.rs
package io.github.kotlinmania.numtraits.ops

/**
 * Performs addition that returns `null` instead of wrapping around on overflow.
 */
public interface CheckedAdd<T> {
    public fun checkedAdd(
        a: T,
        b: T,
    ): T?
}

/**
 * Performs subtraction that returns `null` instead of wrapping around on underflow.
 */
public interface CheckedSub<T> {
    public fun checkedSub(
        a: T,
        b: T,
    ): T?
}

/**
 * Performs multiplication that returns `null` instead of wrapping around on overflow.
 */
public interface CheckedMul<T> {
    public fun checkedMul(
        a: T,
        b: T,
    ): T?
}

/**
 * Performs division that returns `null` instead of panicking on division by zero
 * and instead of wrapping around on underflow and overflow.
 */
public interface CheckedDiv<T> {
    public fun checkedDiv(
        a: T,
        b: T,
    ): T?
}

/**
 * Performs an integral remainder that returns `null` instead of panicking on division by zero
 * and instead of wrapping around on underflow and overflow.
 */
public interface CheckedRem<T> {
    public fun checkedRem(
        a: T,
        b: T,
    ): T?
}

/**
 * Performs negation that returns `null` if the result can't be represented.
 */
public interface CheckedNeg<T> {
    public fun checkedNeg(a: T): T?
}

/**
 * Performs a left shift that returns `null` on shifts larger than or equal to the type width.
 */
public interface CheckedShl<T> {
    public fun checkedShl(
        a: T,
        rhs: UInt,
    ): T?
}

/**
 * Performs a right shift that returns `null` on shifts larger than or equal to the type width.
 */
public interface CheckedShr<T> {
    public fun checkedShr(
        a: T,
        rhs: UInt,
    ): T?
}
