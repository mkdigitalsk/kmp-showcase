package com.mk.kmpshowcase.presentation.component.barcode

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import platform.AVFoundation.AVCaptureConnection
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVCaptureDeviceInput
import platform.AVFoundation.AVCaptureFocusModeContinuousAutoFocus
import platform.AVFoundation.AVCaptureMetadataOutput
import platform.AVFoundation.AVCaptureMetadataOutputObjectsDelegateProtocol
import platform.AVFoundation.AVCaptureOutput
import platform.AVFoundation.AVCaptureSession
import platform.AVFoundation.AVCaptureSessionPresetHigh
import platform.AVFoundation.AVCaptureVideoOrientationPortrait
import platform.AVFoundation.AVCaptureVideoPreviewLayer
import platform.AVFoundation.AVLayerVideoGravityResizeAspectFill
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.AVMetadataMachineReadableCodeObject
import platform.AVFoundation.AVMetadataObjectTypeCodabarCode
import platform.AVFoundation.AVMetadataObjectTypeCode128Code
import platform.AVFoundation.AVMetadataObjectTypeCode39Code
import platform.AVFoundation.AVMetadataObjectTypeCode93Code
import platform.AVFoundation.AVMetadataObjectTypeEAN13Code
import platform.AVFoundation.AVMetadataObjectTypeEAN8Code
import platform.AVFoundation.AVMetadataObjectTypeITF14Code
import platform.AVFoundation.AVMetadataObjectTypeQRCode
import platform.AVFoundation.focusMode
import platform.AVFoundation.isFocusModeSupported
import platform.CoreGraphics.CGRectMake
import platform.UIKit.UIColor
import platform.UIKit.UIScreen
import platform.UIKit.UIView
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_global_queue
import platform.darwin.dispatch_get_main_queue

@Composable
actual fun CodeScanner(
    onScanned: (String) -> Unit,
    onError: (String) -> Unit,
    modifier: Modifier
) {
    UIKitView(
        modifier = modifier.fillMaxSize(),
        factory = {
            val view = UIView(frame = UIScreen.mainScreen.bounds)
            view.backgroundColor = UIColor.blackColor()

            val session = AVCaptureSession().apply {
                sessionPreset = AVCaptureSessionPresetHigh
            }

            val videoDevice: AVCaptureDevice? =
                AVCaptureDevice.defaultDeviceWithMediaType(AVMediaTypeVideo)
            val videoInput = videoDevice?.let {
                AVCaptureDeviceInput.deviceInputWithDevice(it, error = null)
            }

            if (videoInput != null && session.canAddInput(videoInput)) {
                session.addInput(videoInput)
            } else {
                onError("No camera access")
                return@UIKitView view
            }

            // Autofocus configuration
            @Suppress("TooGenericExceptionCaught", "SwallowedException")
            try {
                videoDevice.lockForConfiguration(null)
                if (videoDevice.isFocusModeSupported(AVCaptureFocusModeContinuousAutoFocus)) {
                    videoDevice.focusMode = AVCaptureFocusModeContinuousAutoFocus
                }
                videoDevice.unlockForConfiguration()
            } catch (e: Exception) {
                // Autofocus configuration is optional, continue without it
            }

            val metadataOutput = AVCaptureMetadataOutput()
            val delegate = AVCaptureMetadataOutputObjectsDelegateProtocolImpl(
                session = session,
                onScanned = onScanned
            )
            if (session.canAddOutput(metadataOutput)) {
                session.addOutput(metadataOutput)
                metadataOutput.setMetadataObjectsDelegate(
                    objectsDelegate = delegate,
                    queue = dispatch_get_main_queue()
                )
                metadataOutput.metadataObjectTypes = listOf(
                    AVMetadataObjectTypeQRCode,
                    AVMetadataObjectTypeEAN8Code,
                    AVMetadataObjectTypeEAN13Code,
                    AVMetadataObjectTypeCode39Code,
                    AVMetadataObjectTypeCode93Code,
                    AVMetadataObjectTypeCode128Code,
                    AVMetadataObjectTypeITF14Code,
                    AVMetadataObjectTypeCodabarCode
                )
                metadataOutput.rectOfInterest = CGRectMake(0.0, 0.0, 1.0, 1.0)
            } else {
                onError("No metadata output")
                return@UIKitView view
            }

            val previewLayer = AVCaptureVideoPreviewLayer(session = session).apply {
                videoGravity = AVLayerVideoGravityResizeAspectFill
                frame = view.bounds
                connection?.videoOrientation = AVCaptureVideoOrientationPortrait
            }
            view.layer.addSublayer(previewLayer)

            dispatch_async(dispatch_get_global_queue(0, 0u)) {
                session.startRunning()
            }
            view
        }
    )
}

private class AVCaptureMetadataOutputObjectsDelegateProtocolImpl(
    val session: AVCaptureSession,
    val onScanned: (String) -> Unit
) : NSObject(), AVCaptureMetadataOutputObjectsDelegateProtocol {
    override fun captureOutput(
        output: AVCaptureOutput,
        didOutputMetadataObjects: List<*>,
        fromConnection: AVCaptureConnection
    ) {
        val metadataObj =
            didOutputMetadataObjects.firstOrNull() as? AVMetadataMachineReadableCodeObject
        val scannedCode = metadataObj?.stringValue

        if (scannedCode != null) {
            onScanned(scannedCode)
            session.stopRunning()
        }
    }
}
