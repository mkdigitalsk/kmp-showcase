package com.mk.kmpshowcase.data.client

import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVCaptureDeviceDiscoverySession
import platform.AVFoundation.AVCaptureDevicePositionBack
import platform.AVFoundation.AVCaptureDeviceTypeBuiltInWideAngleCamera
import platform.AVFoundation.AVCaptureTorchModeOff
import platform.AVFoundation.AVCaptureTorchModeOn
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.hasTorch
import platform.AVFoundation.isTorchAvailable
import platform.AVFoundation.setTorchMode

actual class FlashlightClientImpl : FlashlightClient {

    private var isOn = false

    private fun getDevice(): AVCaptureDevice? {
        val discoverySession = AVCaptureDeviceDiscoverySession.discoverySessionWithDeviceTypes(
            deviceTypes = listOf(AVCaptureDeviceTypeBuiltInWideAngleCamera),
            mediaType = AVMediaTypeVideo,
            position = AVCaptureDevicePositionBack
        )
        return discoverySession.devices.firstOrNull() as? AVCaptureDevice
    }

    actual override fun isAvailable(): Boolean {
        val device = getDevice() ?: return false
        return device.hasTorch && device.isTorchAvailable()
    }

    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    actual override fun turnOn(): Boolean {
        val device = getDevice() ?: return false
        if (!device.hasTorch || !device.isTorchAvailable()) return false
        return try {
            device.lockForConfiguration(null)
            device.setTorchMode(AVCaptureTorchModeOn)
            device.unlockForConfiguration()
            isOn = true
            true
        } catch (e: Exception) {
            false
        }
    }

    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    actual override fun turnOff(): Boolean {
        val device = getDevice() ?: return false
        if (!device.hasTorch) return false
        return try {
            device.lockForConfiguration(null)
            device.setTorchMode(AVCaptureTorchModeOff)
            device.unlockForConfiguration()
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
