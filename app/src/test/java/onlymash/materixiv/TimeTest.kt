package onlymash.materixiv

import org.junit.Test

class TimeTest {

    @Test
    fun verifyTime() {
        val time1 = System.currentTimeMillis()
        Thread.sleep(3600L)
        val time2 = System.currentTimeMillis()
        val dt = time2 - time1
        println(dt)
        assert(dt == 3600L)
    }
}