package com.mk.kmpshowcase.data.client

interface FlashlightClient {
    fun isAvailable(): Boolean
    fun turnOn(): Boolean
    fun turnOff(): Boolean
    fun toggle(): Boolean
}

expect class FlashlightClientImpl : FlashlightClient {
    override fun isAvailable(): Boolean
    override fun turnOn(): Boolean
    override fun turnOff(): Boolean
    override fun toggle(): Boolean
}
