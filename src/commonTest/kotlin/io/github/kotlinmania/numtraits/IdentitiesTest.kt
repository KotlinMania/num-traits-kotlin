// port-lint: tests src/identities.rs
package io.github.kotlinmania.numtraits

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class IdentitiesTest {
    @Test
    fun zeroAndOnePrimitives() {
        assertEquals(0.toByte(), zero(I8Num))
        assertEquals(1.toByte(), one(I8Num))
        assertTrue(0.toByte().isZero())
        assertTrue(1.toByte().isOne())
        assertFalse(1.toByte().isZero())

        assertEquals(0.toShort(), zero(I16Num))
        assertEquals(1.toShort(), one(I16Num))
        assertTrue(0.toShort().isZero())
        assertTrue(1.toShort().isOne())

        assertEquals(0, zero(I32Num))
        assertEquals(1, one(I32Num))
        assertTrue(0.isZero())
        assertTrue(1.isOne())

        assertEquals(0L, zero(I64Num))
        assertEquals(1L, one(I64Num))
        assertTrue(0L.isZero())
        assertTrue(1L.isOne())

        assertEquals(0.toUByte(), zero(U8Num))
        assertEquals(1.toUByte(), one(U8Num))
        assertTrue(0.toUByte().isZero())
        assertTrue(1.toUByte().isOne())

        assertEquals(0.toUShort(), zero(U16Num))
        assertEquals(1.toUShort(), one(U16Num))
        assertTrue(0.toUShort().isZero())
        assertTrue(1.toUShort().isOne())

        assertEquals(0u, zero(U32Num))
        assertEquals(1u, one(U32Num))
        assertTrue(0u.isZero())
        assertTrue(1u.isOne())

        assertEquals(0uL, zero(U64Num))
        assertEquals(1uL, one(U64Num))
        assertTrue(0uL.isZero())
        assertTrue(1uL.isOne())

        assertEquals(0.0f, zero(F32Num))
        assertEquals(1.0f, one(F32Num))
        assertTrue(0.0f.isZero())
        assertTrue(1.0f.isOne())

        assertEquals(0.0, zero(F64Num))
        assertEquals(1.0, one(F64Num))
        assertTrue(0.0.isZero())
        assertTrue(1.0.isOne())
    }

    @Test
    fun constIdentities() {
        assertEquals(0, I32Num.zeroConstant)
        assertEquals(1, I32Num.oneConstant)
        assertEquals(0L, I64Num.zeroConstant)
        assertEquals(1L, I64Num.oneConstant)
        assertEquals(0.0f, F32Num.zeroConstant)
        assertEquals(1.0f, F32Num.oneConstant)
        assertEquals(0.0, F64Num.zeroConstant)
        assertEquals(1.0, F64Num.oneConstant)
    }
}
