package com.example.check_in_speaker

import org.junit.Test
import org.junit.Assert.*

class SafeNumberTest {
    @Test
    fun isValidSafeNumber(){
        val safeNumberFormat = Regex("^[가-힣][0-9]{2}[가-힣][0-9]{2}\$")
        assertEquals(false, "".matches(safeNumberFormat))
        assertEquals(true, "가12나34".matches(safeNumberFormat))
        assertEquals(false, "모AB나CD".matches(safeNumberFormat))
        assertEquals(true, "모69홁46".matches(safeNumberFormat))
        assertEquals(false, "바나2345".matches(safeNumberFormat))
        assertEquals(false, "동143바7".matches(safeNumberFormat))
        assertEquals(true, "마63마12".matches(safeNumberFormat))
        assertEquals(false, "ASDQWR".matches(safeNumberFormat))
        assertEquals(false, "asdsqwewrsw".matches(safeNumberFormat))
        assertEquals(false, "asd가w".matches(safeNumberFormat))
    }
}