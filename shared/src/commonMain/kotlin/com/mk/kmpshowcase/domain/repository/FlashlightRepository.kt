package com.mk.kmpshowcase.domain.repository

interface FlashlightRepository {
    fun isAvailable(): Boolean
    fun turnOn(): Boolean
    fun turnOff(): Boolean
    fun toggle(): Boolean
}
