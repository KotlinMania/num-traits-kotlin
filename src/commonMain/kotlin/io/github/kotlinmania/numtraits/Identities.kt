// port-lint: source src/identities.rs
package io.github.kotlinmania.numtraits

/**
 * Defines an additive identity element for [T].
 */
public interface Zero<T> {
    /**
     * Returns the additive identity element of [T], `0`.
     */
    public fun zero(): T

    /**
     * Returns `true` if `value` is equal to the additive identity.
     */
    public fun isZero(value: T): Boolean

    /**
     * Returns the additive identity after resetting a value.
     */
    public fun setZero(): T = zero()
}

/**
 * Defines a compile-time style constant additive identity element for [T].
 */
public interface ConstZero<T> : Zero<T> {
    /**
     * The additive identity constant `0`.
     */
    public val zeroConstant: T
}

/**
 * Defines a multiplicative identity element for [T].
 */
public interface One<T> {
    /**
     * Returns the multiplicative identity element of [T], `1`.
     */
    public fun one(): T

    /**
     * Returns `true` if `value` is equal to the multiplicative identity.
     */
    public fun isOne(value: T): Boolean = value == one()

    /**
     * Returns the multiplicative identity after resetting a value.
     */
    public fun setOne(): T = one()
}

/**
 * Defines a compile-time style constant multiplicative identity element for [T].
 */
public interface ConstOne<T> : One<T> {
    /**
     * The multiplicative identity constant `1`.
     */
    public val oneConstant: T
}

/**
 * Returns the additive identity, `0`, using the provided [Zero] instance.
 */
public fun <T> zero(type: Zero<T>): T = type.zero()

/**
 * Returns the multiplicative identity, `1`, using the provided [One] instance.
 */
public fun <T> one(type: One<T>): T = type.one()

public fun Byte.isZero(): Boolean = this == 0.toByte()

public fun Short.isZero(): Boolean = this == 0.toShort()

public fun Int.isZero(): Boolean = this == 0

public fun Long.isZero(): Boolean = this == 0L

public fun UByte.isZero(): Boolean = this == 0.toUByte()

public fun UShort.isZero(): Boolean = this == 0.toUShort()

public fun UInt.isZero(): Boolean = this == 0u

public fun ULong.isZero(): Boolean = this == 0uL

public fun Float.isZero(): Boolean = this == 0.0f

public fun Double.isZero(): Boolean = this == 0.0

public fun Byte.isOne(): Boolean = this == 1.toByte()

public fun Short.isOne(): Boolean = this == 1.toShort()

public fun Int.isOne(): Boolean = this == 1

public fun Long.isOne(): Boolean = this == 1L

public fun UByte.isOne(): Boolean = this == 1.toUByte()

public fun UShort.isOne(): Boolean = this == 1.toUShort()

public fun UInt.isOne(): Boolean = this == 1u

public fun ULong.isOne(): Boolean = this == 1uL

public fun Float.isOne(): Boolean = this == 1.0f

public fun Double.isOne(): Boolean = this == 1.0
