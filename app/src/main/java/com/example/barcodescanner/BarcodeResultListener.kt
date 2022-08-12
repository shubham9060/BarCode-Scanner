package com.example.barcodescanner

import android.graphics.Rect
import android.util.Log
import java.io.IOException

class BarcodeScannerProcessor(
    private val view: GraphicOverlay,
    private val listener: BarcodeResultListener
) : BaseImageAnalyzer<List<Barcode>>() {

    private val options = BarcodeScannerOptions.Builder().build()
    private val scanner = BarcodeScanning.getClient(options)
    private var mListener: BarcodeResultListener? = null

    companion object {
        private const val TAG = "BarcodeScanProcessor"
    }

    interface BarcodeResultListener {
        fun onScanned(barcode: Barcode)
        fun onScanError(errorMessage: String?)
    }

    override val graphicOverlay: GraphicOverlay
        get() = view

    @androidx.camera.core.ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        super.analyze(imageProxy)
        mListener = listener
    }

    override fun detectInImage(image: InputImage): Task<List<Barcode>> {
        return scanner.process(image)
    }

    override fun stop() {
        try {
            scanner.close()
        } catch (e: IOException) {
            Log.e(TAG, "Exception thrown while trying to close Barcode Scanner: $e")
        }
    }

    override fun onSuccess(
        results: List<Barcode>,
        graphicOverlay: GraphicOverlay,
        rect: Rect
    ) {
        if (!results.isNullOrEmpty()) {
            mListener?.onScanned(results.first())
        }
    }

    override fun onFailure(e: Exception) {
        mListener?.onScanError(e.message)
        Log.w(TAG, "Barcode Scan failed.$e")
    }
}