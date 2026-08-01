package com.mk.kmpshowcase.util

import kotlin.test.Test
import kotlin.test.assertEquals

class StringFormatterTest {

    @Test
    fun formatDouble_withZeroDecimals_returnsWholeNumber() {
        val result = StringFormatter.formatDouble(123.456, 0)
        assertEquals("123", result)
    }

    @Test
    fun formatDouble_withTwoDecimals_roundsCorrectly() {
        val result = StringFormatter.formatDouble(123.456, 2)
        assertEquals("123.46", result)
    }

    @Test
    fun formatDouble_withSixDecimals_formatsCoordinates() {
        val result = StringFormatter.formatDouble(48.123456789, 6)
        assertEquals("48.123457", result)
    }

    @Test
    fun formatDouble_withWholeNumber_addsPadding() {
        val result = StringFormatter.formatDouble(48.0, 6)
        assertEquals("48.000000", result)
    }

    @Test
    fun formatDouble_withNegativeValue_formatsCorrectly() {
        val result = StringFormatter.formatDouble(-17.123456, 6)
        assertEquals("-17.123456", result)
    }

    @Test
    fun formatDouble_withZero_formatsCorrectly() {
        val result = StringFormatter.formatDouble(0.0, 6)
        assertEquals("0.000000", result)
    }

    @Test
    fun formatDouble_withSmallValue_formatsCorrectly() {
        val result = StringFormatter.formatDouble(0.000001, 6)
        assertEquals("0.000001", result)
    }

    @Test
    fun formatDouble_withLargeValue_formatsCorrectly() {
        val result = StringFormatter.formatDouble(1234567.89, 2)
        assertEquals("1234567.89", result)
    }

    @Test
    fun formatDouble_roundsUp() {
        val result = StringFormatter.formatDouble(1.999, 2)
        assertEquals("2.00", result)
    }

    @Test
    fun formatDouble_roundsDown() {
        val result = StringFormatter.formatDouble(1.994, 2)
        assertEquals("1.99", result)
    }
}
