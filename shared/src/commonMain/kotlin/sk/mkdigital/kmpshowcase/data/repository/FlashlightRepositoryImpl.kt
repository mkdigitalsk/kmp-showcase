package sk.mkdigital.kmpshowcase.data.repository

import sk.mkdigital.kmpshowcase.data.client.FlashlightClient
import sk.mkdigital.kmpshowcase.domain.repository.FlashlightRepository

class FlashlightRepositoryImpl(
    private val flashlightClient: FlashlightClient
) : FlashlightRepository {

    override fun isAvailable(): Boolean = flashlightClient.isAvailable()

    override fun turnOn(): Boolean = flashlightClient.turnOn()

    override fun turnOff(): Boolean = flashlightClient.turnOff()

    override fun toggle(): Boolean = flashlightClient.toggle()
}
