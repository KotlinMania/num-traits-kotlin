// port-lint: source src/bounds.rs
package io.github.kotlinmania.numtraits

/**
 * Numbers which have lower bounds.
 */
public interface LowerBounded<T> {
    /**
     * Returns the smallest finite number this type can represent.
     */
    public fun minValue(): T
}

/**
 * Numbers which have upper bounds.
 */
public interface UpperBounded<T> {
    /**
     * Returns the largest finite number this type can represent.
     */
    public fun maxValue(): T
}

/**
 * Numbers which have upper and lower bounds.
 */
public interface Bounded<T> :
    LowerBounded<T>,
    UpperBounded<T>

public object ByteBounds : Bounded<Byte> {
    override fun minValue(): Byte = Byte.MIN_VALUE

    override fun maxValue(): Byte = Byte.MAX_VALUE
}

public object ShortBounds : Bounded<Short> {
    override fun minValue(): Short = Short.MIN_VALUE

    override fun maxValue(): Short = Short.MAX_VALUE
}

public object IntBounds : Bounded<Int> {
    override fun minValue(): Int = Int.MIN_VALUE

    override fun maxValue(): Int = Int.MAX_VALUE
}

public object LongBounds : Bounded<Long> {
    override fun minValue(): Long = Long.MIN_VALUE

    override fun maxValue(): Long = Long.MAX_VALUE
}

public object UByteBounds : Bounded<UByte> {
    override fun minValue(): UByte = UByte.MIN_VALUE

    override fun maxValue(): UByte = UByte.MAX_VALUE
}

public object UShortBounds : Bounded<UShort> {
    override fun minValue(): UShort = UShort.MIN_VALUE

    override fun maxValue(): UShort = UShort.MAX_VALUE
}

public object UIntBounds : Bounded<UInt> {
    override fun minValue(): UInt = UInt.MIN_VALUE

    override fun maxValue(): UInt = UInt.MAX_VALUE
}

public object ULongBounds : Bounded<ULong> {
    override fun minValue(): ULong = ULong.MIN_VALUE

    override fun maxValue(): ULong = ULong.MAX_VALUE
}

public object FloatBounds : Bounded<Float> {
    override fun minValue(): Float = -Float.MAX_VALUE

    override fun maxValue(): Float = Float.MAX_VALUE
}

public object DoubleBounds : Bounded<Double> {
    override fun minValue(): Double = -Double.MAX_VALUE

    override fun maxValue(): Double = Double.MAX_VALUE
}

public object UnitBounds : Bounded<Unit> {
    override fun minValue() {}

    override fun maxValue() {}
}
