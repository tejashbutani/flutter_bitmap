package com.example.flutter_bitmap

import android.content.Context
import android.graphics.Color
import android.text.method.Touch.onTouchEvent
import android.util.Log
import android.view.MotionEvent
import android.view.View
import io.flutter.plugin.platform.PlatformView

class NativeDrawingView(context: Context) : PlatformView {
    private val TAG = "NativeDrawingView"
    private val rendLibView: RendLibSurfaceView = RendLibSurfaceView(context)
    private var lastX: Float? = null
    private var lastY: Float? = null
    private var isDrawing = false

    override fun getView(): View {
        return rendLibView
    }

    fun drawPoint(x: Float, y: Float, color: Int, strokeWidth: Float) {
        rendLibView.setColor(color)
        rendLibView.setStrokeWidth(strokeWidth)

        if (!isDrawing) {
            // First point - send ACTION_DOWN
            Log.d(TAG, "Starting new stroke at ($x, $y)")
            val downEvent = MotionEvent.obtain(
                System.currentTimeMillis(),
                System.currentTimeMillis(),
                MotionEvent.ACTION_DOWN,
                x, y,
                0
            )
            rendLibView.onTouchEvent(downEvent)
            downEvent.recycle()
            isDrawing = true
        } else {
            // Continuing stroke - send ACTION_MOVE
            Log.d(TAG, "Continuing stroke to ($x, $y)")
            val moveEvent = MotionEvent.obtain(
                System.currentTimeMillis(),
                System.currentTimeMillis(),
                MotionEvent.ACTION_MOVE,
                x, y,
                0
            )
            rendLibView.onTouchEvent(moveEvent)
            moveEvent.recycle()
        }

        lastX = x
        lastY = y
    }

    fun endStroke() {
        if (isDrawing && lastX != null && lastY != null) {
            Log.d(TAG, "Ending stroke at ($lastX, $lastY)")
            val upEvent = MotionEvent.obtain(
                System.currentTimeMillis(),
                System.currentTimeMillis(),
                MotionEvent.ACTION_UP,
                lastX!!, lastY!!,
                0
            )
            rendLibView.onTouchEvent(upEvent)
            upEvent.recycle()
            isDrawing = false
        }
    }

    fun clear() {
        Log.d(TAG, "clear called")
        lastX = null
        lastY = null
        isDrawing = false
        rendLibView.surfaceCreated(rendLibView.holder)
    }

    override fun dispose() {
        Log.d(TAG, "dispose called")
        rendLibView.surfaceDestroyed(rendLibView.holder)
    }
}