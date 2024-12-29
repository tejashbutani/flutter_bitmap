package com.example.flutter_bitmap

import android.content.Context
import android.graphics.Color
import android.text.method.Touch.onTouchEvent
import android.view.MotionEvent
import android.view.View
import io.flutter.plugin.platform.PlatformView

class NativeDrawingView(context: Context) : PlatformView {
    private val rendLibView: RendLibSurfaceView = RendLibSurfaceView(context)
    private var lastX: Float? = null
    private var lastY: Float? = null
    private var currentColor: Int = Color.BLACK
    private var currentStrokeWidth: Float = 3f

    override fun getView(): View {
        return rendLibView
    }

    fun drawPoint(x: Float, y: Float, color: Int, strokeWidth: Float) {
        rendLibView.setColor(color)
        rendLibView.setStrokeWidth(strokeWidth)

        if (lastX != null && lastY != null) {
            // Create motion events for both the previous and current points
            val downTime = System.currentTimeMillis()
            
            // Move to the last point
            val moveEvent = MotionEvent.obtain(
                downTime, downTime,
                MotionEvent.ACTION_MOVE,
                lastX!!, lastY!!,
                0
            )
            rendLibView.onTouchEvent(moveEvent)
            moveEvent.recycle()

            // Draw to the current point
            val moveEvent2 = MotionEvent.obtain(
                downTime, downTime,
                MotionEvent.ACTION_MOVE,
                x, y,
                0
            )
            rendLibView.onTouchEvent(moveEvent2)
            moveEvent2.recycle()
        } else {
            // First point in the stroke
            val downEvent = MotionEvent.obtain(
                System.currentTimeMillis(),
                System.currentTimeMillis(),
                MotionEvent.ACTION_DOWN,
                x, y,
                0
            )
            rendLibView.onTouchEvent(downEvent)
            downEvent.recycle()
        }

        lastX = x
        lastY = y
    }

    fun clear() {
        lastX = null
        lastY = null
        rendLibView.surfaceCreated(rendLibView.holder)
    }

    override fun dispose() {
        rendLibView.surfaceDestroyed(rendLibView.holder)
    }
}