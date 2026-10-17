// port-lint: source src/ops/inv.rs
package io.github.kotlinmania.numtraits.ops

/**
 * Unary operator for retrieving the multiplicative inverse, or reciprocal, of a value.
 */
public interface Inv<T> {
    /**
     * Returns the multiplicative inverse of `value`.
     */
    public fun inv(value: T): T
}

public object F32Inv : Inv<Float> {
    override fun inv(value: Float): Float = 1.0f / value
}

public object F64Inv : Inv<Double> {
    override fun inv(value: Double): Double = 1.0 / value
}

public fun Float.multiplicativeInv(): Float = 1.0f / this

public fun Double.multiplicativeInv(): Double = 1.0 / this
