package com.mk.kmpshowcase.data.client

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager

actual class FlashlightClientImpl(
    context: Context,
) : FlashlightClient {

    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private var isOn = false
    private var cameraId: String? = null

    init {
        cameraId = findCameraWithFlash()
    }

    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    private fun findCameraWithFlash(): String? {
        return try {
            cameraManager.cameraIdList.firstOrNull { id ->
                val characteristics = cameraManager.getCameraCharacteristics(id)
                characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
            }
        } catch (e: Exception) {
            null
        }
    }

    actual override fun isAvailable(): Boolean {
        return cameraId != null
    }

    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    actual override fun turnOn(): Boolean {
        val id = cameraId ?: return false
        return try {
            cameraManager.setTorchMode(id, true)
            isOn = true
            true
        } catch (e: Exception) {
            false
        }
    }

    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    actual override fun turnOff(): Boolean {
        val id = cameraId ?: return false
        return try {
            cameraManager.setTorchMode(id, false)
            isOn = false
            true
        } catch (e: Exception) {
            false
        }
    }

    actual override fun toggle(): Boolean {
        return if (isOn) turnOff() else turnOn()
    }
}
