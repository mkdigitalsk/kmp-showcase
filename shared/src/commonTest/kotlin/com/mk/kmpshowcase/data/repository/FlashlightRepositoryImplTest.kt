package com.mk.kmpshowcase.data.repository

import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import dev.mokkery.verify
import com.mk.kmpshowcase.data.client.FlashlightClient
import com.mk.kmpshowcase.domain.BaseTest
import com.mk.kmpshowcase.domain.repository.FlashlightRepository
import kotlin.test.Test
import kotlin.test.assertEquals

class FlashlightRepositoryImplTest : BaseTest<FlashlightRepository>() {
    override lateinit var classUnderTest: FlashlightRepository

    private val flashlightClient: FlashlightClient = mock()

    override fun beforeEach() {
        classUnderTest = FlashlightRepositoryImpl(flashlightClient)
    }

    @Test
    fun `isAvailable returns true when client reports available`() {
        every { flashlightClient.isAvailable() } returns true

        val result = classUnderTest.isAvailable()

        assertEquals(true, result)
        verify { flashlightClient.isAvailable() }
    }

    @Test
    fun `isAvailable returns false when client reports not available`() {
        every { flashlightClient.isAvailable() } returns false

        val result = classUnderTest.isAvailable()

        assertEquals(false, result)
    }

    @Test
    fun `turnOn returns true when client succeeds`() {
        every { flashlightClient.turnOn() } returns true

        val result = classUnderTest.turnOn()

        assertEquals(true, result)
        verify { flashlightClient.turnOn() }
    }

    @Test
    fun `turnOn returns false when client fails`() {
        every { flashlightClient.turnOn() } returns false

        val result = classUnderTest.turnOn()

        assertEquals(false, result)
    }

    @Test
    fun `turnOff returns true when client succeeds`() {
        every { flashlightClient.turnOff() } returns true

        val result = classUnderTest.turnOff()

        assertEquals(true, result)
        verify { flashlightClient.turnOff() }
    }

    @Test
    fun `turnOff returns false when client fails`() {
        every { flashlightClient.turnOff() } returns false

        val result = classUnderTest.turnOff()

        assertEquals(false, result)
    }

    @Test
    fun `toggle returns true when client succeeds`() {
        every { flashlightClient.toggle() } returns true

        val result = classUnderTest.toggle()

        assertEquals(true, result)
        verify { flashlightClient.toggle() }
    }

    @Test
    fun `toggle returns false when client fails`() {
        every { flashlightClient.toggle() } returns false

        val result = classUnderTest.toggle()

        assertEquals(false, result)
    }
}
