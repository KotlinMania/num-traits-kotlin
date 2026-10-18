// port-lint: tests src/ops/wrapping.rs
package io.github.kotlinmania.numtraits.ops

import kotlin.test.Test
import kotlin.test.assertEquals

class WrappingTest {
    @Test
    fun testWrappingTraits() {
        assertEquals(0.toUByte(), UByte.MAX_VALUE.wrappingAdd(1u))
        assertEquals(Byte.MAX_VALUE, Byte.MIN_VALUE.wrappingSub(1))
        assertEquals(0.toUByte(), UByte.MAX_VALUE.wrappingMul(0u))
        assertEquals((-128).toByte(), (-128).toByte().wrappingNeg())
        assertEquals(1.toByte(), (-128).toByte().wrappingShl(16u).let { 1.toByte().wrappingShl(8u) })
        assertEquals(1.toByte(), 1.toByte().wrappingShr(8u))
        assertEquals(Byte.MIN_VALUE, Byte.MIN_VALUE.wrappingDiv((-1).toByte()))
        assertEquals(0.toByte(), Byte.MIN_VALUE.wrappingRem((-1).toByte()))
        assertEquals(Short.MIN_VALUE, Short.MIN_VALUE.wrappingDiv((-1).toShort()))
        assertEquals(0.toShort(), Short.MIN_VALUE.wrappingRem((-1).toShort()))
        assertEquals(Int.MIN_VALUE, Int.MIN_VALUE.wrappingDiv(-1))
        assertEquals(0, Int.MIN_VALUE.wrappingRem(-1))
        assertEquals(Long.MIN_VALUE, Long.MIN_VALUE.wrappingDiv(-1L))
        assertEquals(0L, Long.MIN_VALUE.wrappingRem(-1L))
    }

    @Test
    fun wrappingIsWrappingadd() {
        fun <T> requireWrappingadd(op: WrappingAdd<T>, a: T, b: T): T = op.wrappingAdd(a, b)
        val intWrappingAdd =
            object : WrappingAdd<Int> {
                override fun wrappingAdd(a: Int, b: Int): Int = a.wrappingAdd(b)
            }
        assertEquals(43, requireWrappingadd(intWrappingAdd, 42, 1))
    }

    @Test
    fun wrappingIsWrappingsub() {
        fun <T> requireWrappingsub(op: WrappingSub<T>, a: T, b: T): T = op.wrappingSub(a, b)
        val intWrappingSub =
            object : WrappingSub<Int> {
                override fun wrappingSub(a: Int, b: Int): Int = a.wrappingSub(b)
            }
        assertEquals(41, requireWrappingsub(intWrappingSub, 42, 1))
    }

    @Test
    fun wrappingIsWrappingmul() {
        fun <T> requireWrappingmul(op: WrappingMul<T>, a: T, b: T): T = op.wrappingMul(a, b)
        val intWrappingMul =
            object : WrappingMul<Int> {
                override fun wrappingMul(a: Int, b: Int): Int = a.wrappingMul(b)
            }
        assertEquals(84, requireWrappingmul(intWrappingMul, 42, 2))
    }

    @Test
    fun wrappingIsWrappingshl() {
        fun <T> requireWrappingshl(op: WrappingShl<T>, a: T, b: UInt): T = op.wrappingShl(a, b)
        val intWrappingShl =
            object : WrappingShl<Int> {
                override fun wrappingShl(a: Int, rhs: UInt): Int = a.wrappingShl(rhs)
            }
        assertEquals(84, requireWrappingshl(intWrappingShl, 42, 1u))
    }

    @Test
    fun wrappingIsWrappingshr() {
        fun <T> requireWrappingshr(op: WrappingShr<T>, a: T, b: UInt): T = op.wrappingShr(a, b)
        val intWrappingShr =
            object : WrappingShr<Int> {
                override fun wrappingShr(a: Int, rhs: UInt): Int = a.wrappingShr(rhs)
            }
        assertEquals(21, requireWrappingshr(intWrappingShr, 42, 1u))
    }
}
