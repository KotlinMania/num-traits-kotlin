// port-lint: tests src/float.rs
package io.github.kotlinmania.numtraits

import kotlin.math.PI
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FloatTest {
    private val degRadPairs =
        listOf(
            0.0 to 0.0,
            22.5 to PI / 8.0,
            30.0 to PI / 6.0,
            45.0 to PI / 4.0,
            60.0 to PI / 3.0,
            90.0 to PI / 2.0,
            180.0 to PI,
        )

    @Test
    fun convertDegRad() {
        for ((deg, rad) in degRadPairs) {
            assertTrue(abs(F64Num.toDegrees(rad) - deg) < 1e-6)
            assertTrue(abs(F64Num.toRadians(deg) - rad) < 1e-6)

            val degF = deg.toFloat()
            val radF = rad.toFloat()
            assertTrue(abs(F32Num.toDegrees(radF) - degF) < 1e-5f)
            assertTrue(abs(F32Num.toRadians(degF) - radF) < 1e-5f)
        }
    }

    @Test
    fun toDegreesRounding() {
        assertEquals(
            57.2957795130823208767981548141051703f,
            F32Num.toDegrees(1.0f),
        )
    }

    @Test
    fun extraLogs() {
        fun <T> check(
            type: NumFloat<T>,
            consts: FloatConst<T>,
            diff: T,
        ) {
            val two = type.from(F64Num, 2.0)!!
            assertTrue(type.compare(type.abs(type.sub(consts.log102(), type.log10(two))), diff) < 0)
            assertTrue(
                type.compare(
                    type.abs(type.sub(consts.log102(), type.div(consts.ln2(), consts.ln10()))),
                    diff,
                ) < 0,
            )

            val ten = type.from(F64Num, 10.0)!!
            assertTrue(type.compare(type.abs(type.sub(consts.log210(), type.log2(ten))), diff) < 0)
            assertTrue(
                type.compare(
                    type.abs(type.sub(consts.log210(), type.div(consts.ln10(), consts.ln2()))),
                    diff,
                ) < 0,
            )
        }

        check(F32Num, F32Num, 1e-6f)
        check(F64Num, F64Num, 1e-12)
    }

    @Test
    fun copysign() {
        testCopysignGeneric(F32Num, 2.0f, -2.0f, Float.NaN)
        testCopysignGeneric(F64Num, 2.0, -2.0, Double.NaN)
    }

    private fun <F> testCopysignGeneric(
        type: NumFloat<F>,
        p: F,
        n: F,
        nan: F,
    ) {
        assertTrue(type.isSignPositive(p))
        assertTrue(type.isSignNegative(n))
        assertTrue(type.isNan(nan))

        assertEquals(p, type.copysign(p, p))
        assertEquals(type.neg(p), type.copysign(p, n))

        assertEquals(n, type.copysign(n, n))
        assertEquals(type.neg(n), type.copysign(n, p))
    }
}
