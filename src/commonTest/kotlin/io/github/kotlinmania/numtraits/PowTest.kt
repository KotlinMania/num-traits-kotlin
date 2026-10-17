// port-lint: tests src/pow.rs
package io.github.kotlinmania.numtraits

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PowTest {
    @Test
    fun testPow() {
        assertEquals(1, pow(I32Num, 0, 0))
        assertEquals(0, pow(I32Num, 0, 1))
        assertEquals(0, pow(I32Num, 0, 5))
        assertEquals(1, pow(I32Num, 2, 0))
        assertEquals(2, pow(I32Num, 2, 1))
        assertEquals(4, pow(I32Num, 2, 2))
        assertEquals(8, pow(I32Num, 2, 3))
        assertEquals(16, pow(I32Num, 2, 4))
        assertEquals(32, pow(I32Num, 2, 5))
        assertEquals(1024, pow(I32Num, 2, 10))
        assertEquals(-27, pow(I32Num, -3, 3))
        assertEquals(81, pow(I32Num, -3, 4))
    }

    @Test
    fun testCheckedPow() {
        assertEquals(1, checkedPow(I32Num, I32Num, 2, 0))
        assertEquals(1024, checkedPow(I32Num, I32Num, 2, 10))
        assertNull(checkedPow(I8Num, I8Num, 2.toByte(), 7))
        assertEquals(64.toByte(), checkedPow(I8Num, I8Num, 2.toByte(), 6))
        assertNull(checkedPow(U8Num, U8Num, 2.toUByte(), 8))
        assertEquals(128.toUByte(), checkedPow(U8Num, U8Num, 2.toUByte(), 7))
    }
}
