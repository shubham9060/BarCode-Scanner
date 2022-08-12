package com.example.barcodescanner

import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity(), BarcodeScannerProcessor.BarcodeResultListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var cameraManager: CameraManager

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        createCameraManager()
        binding.apply {
            lifecycleOwner = this@MainActivity
        }
        if (allPermissionsGranted()) {
            cameraManager.startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                cameraManager.startCamera()
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT)
                    .show()
                //finish()
            }
        }
    }

    private fun createCameraManager() {
        cameraManager = CameraManager(
            this,
            binding.previewViewFinder,
            this,
            binding.graphicOverlayFinder,
            this
        )
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onScanned(barcode: Barcode) {
        cameraManager.stopCamera()
//        navigateToDestination(
//                R.id.barcodeResultFragment,
//                R.id.action_barcodeSearchFragment_to_barcodeResultFragment,
//                bundleOf(BarcodeResultFragment.KEY_BARCODE_RAW_VALUE to barcode.rawValue)
//        )
    }

    override fun onScanError(errorMessage: String?) {
//        context?.showShortToast("Error occurred. Please try again")
//        Timber.d(errorMessage)
    }
}