// port-lint: source src/ops/mul_add.rs
package io.github.kotlinmania.numtraits.ops

/**
 * Fused multiply-add. Computes `(value * a) + b` with only one rounding
 * error on floating-point types, or `(value * a) + b` on integer types.
 */
public interface MulAdd<T> {
    public fun mulAdd(
        value: T,
        a: T,
        b: T,
    ): T
}

/**
 * Interface for types supporting fused multiply-add assignment.
 */
public interface MulAddAssign<T> : MulAdd<T>

public fun Byte.mulAdd(
    a: Byte,
    b: Byte,
): Byte = ((this * a) + b).toByte()

public fun Short.mulAdd(
    a: Short,
    b: Short,
): Short = ((this * a) + b).toShort()

public fun Int.mulAdd(
    a: Int,
    b: Int,
): Int = (this * a) + b

public fun Long.mulAdd(
    a: Long,
    b: Long,
): Long = (this * a) + b

public fun UByte.mulAdd(
    a: UByte,
    b: UByte,
): UByte = ((this * a) + b).toUByte()

public fun UShort.mulAdd(
    a: UShort,
    b: UShort,
): UShort = ((this * a) + b).toUShort()

public fun UInt.mulAdd(
    a: UInt,
    b: UInt,
): UInt = (this * a) + b

public fun ULong.mulAdd(
    a: ULong,
    b: ULong,
): ULong = (this * a) + b

public fun Float.mulAdd(
    a: Float,
    b: Float,
): Float = (this.toDouble() * a.toDouble() + b.toDouble()).toFloat()

public fun Double.mulAdd(
    a: Double,
    b: Double,
): Double = this * a + b
