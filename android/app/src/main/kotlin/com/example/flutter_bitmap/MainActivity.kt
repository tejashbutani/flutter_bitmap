package com.example.flutter_bitmap

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.os.Bundle
import io.flutter.embedding.android.FlutterActivity
import io.flutter.plugin.common.MethodChannel
import java.io.ByteArrayOutputStream

class MainActivity: FlutterActivity() {
    private val CHANNEL = "com.example.flutter_bitmap/render"
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        android.util.Log.d(TAG, "onCreate: Initializing MethodChannel")
        
        MethodChannel(flutterEngine!!.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->
            android.util.Log.d(TAG, "Received method call: ${call.method}")
            when (call.method) {
                "initRenderLib" -> {
                    RenderUtils.initRendLib()
                    result.success(null)
                }
                "getDeviceNativeResolution" -> {
                    val resolution = RenderUtils.getDeviceNativeResolution(this)
                    android.util.Log.d(TAG, "Returning resolution: ${resolution[0]}x${resolution[1]}")
                    result.success(resolution)
                }
                "getAccelerateBitmap" -> {
                    val width = call.argument<Int>("width") ?: 0
                    val height = call.argument<Int>("height") ?: 0
                    android.util.Log.d(TAG, "Creating bitmap: ${width}x${height}")
                    
                    val bitmap = RenderUtils.getAccelerateBitmap(width, height)
                    val stream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    val byteArray = stream.toByteArray()
                    android.util.Log.d(TAG, "Returning bitmap byte array of size: ${byteArray.size}")
                    result.success(byteArray)
                }
                "getStrokeBitmap" -> {
                    val strokeBitmap = getStrokeBitmap()
                    val stream = ByteArrayOutputStream()
                    strokeBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    val byteArray = stream.toByteArray()
                    result.success(byteArray)
                }
                else -> {
                    android.util.Log.w(TAG, "Method not implemented: ${call.method}")
                    result.notImplemented()
                }
            }
        }
    }

    private fun getStrokeBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        // Draw your strokes here
        return bitmap
    }
}
