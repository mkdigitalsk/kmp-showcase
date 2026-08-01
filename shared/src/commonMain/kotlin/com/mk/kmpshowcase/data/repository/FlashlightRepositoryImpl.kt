package com.mk.kmpshowcase.data.repository

import com.mk.kmpshowcase.data.client.FlashlightClient
import com.mk.kmpshowcase.domain.repository.FlashlightRepository

class FlashlightRepositoryImpl(
    private val flashlightClient: FlashlightClient
) : FlashlightRepository {

    override fun isAvailable(): Boolean = flashlightClient.isAvailable()

    override fun turnOn(): Boolean = flashlightClient.turnOn()

    override fun turnOff(): Boolean = flashlightClient.turnOff()

    override fun toggle(): Boolean = flashlightClient.toggle()
}
