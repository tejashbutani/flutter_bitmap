package com.example.flutter_bitmap

import android.content.Context
import android.graphics.Bitmap

object RenderUtils {
    private const val TAG = "RenderUtils"

    fun initRendLib() {
        android.util.Log.d(TAG, "Initializing render library")
        // Initialize your rendering library here
    }

    fun getDeviceNativeResolution(context: Context): IntArray {
        // Return the device's native resolution
        val resolution = intArrayOf(1080, 1920) // Example resolution
        android.util.Log.d(TAG, "Device resolution: ${resolution[0]}x${resolution[1]}")
        return resolution
    }

    fun getAccelerateBitmap(width: Int, height: Int): Bitmap {
        android.util.Log.d(TAG, "Creating accelerated bitmap: ${width}x${height}")
        // Create and return an accelerated bitmap
        return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    }

    fun clearBitmapContent() {
        android.util.Log.d(TAG, "Clearing bitmap content")
        // Clear bitmap content if necessary
    }
} 