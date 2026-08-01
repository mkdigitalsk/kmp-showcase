package com.mk.kmpshowcase.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NotificationChannelTest {

    @Test
    fun `GENERAL channel has correct properties`() {
        val channel = NotificationChannel.GENERAL
        assertEquals("001", channel.id)
        assertEquals("General", channel.channelName)
        assertEquals("General app notifications", channel.description)
    }

    @Test
    fun `REMINDERS channel has correct properties`() {
        val channel = NotificationChannel.REMINDERS
        assertEquals("002", channel.id)
        assertEquals("Reminders", channel.channelName)
        assertEquals("Calendar events and task reminders", channel.description)
    }

    @Test
    fun `PROMOTIONS channel has correct properties`() {
        val channel = NotificationChannel.PROMOTIONS
        assertEquals("003", channel.id)
        assertEquals("Promotions", channel.channelName)
        assertEquals("Deals, offers and updates", channel.description)
    }

    @Test
    fun `all channels have unique IDs`() {
        val ids = NotificationChannel.entries.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun `all channels have non-empty names`() {
        NotificationChannel.entries.forEach { channel ->
            assertTrue(channel.channelName.isNotBlank())
        }
    }

    @Test
    fun `all channels have non-empty descriptions`() {
        NotificationChannel.entries.forEach { channel ->
            assertTrue(channel.description.isNotBlank())
        }
    }
}
