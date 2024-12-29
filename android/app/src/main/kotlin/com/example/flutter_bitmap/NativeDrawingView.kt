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
    private var currentColor: Int = Color.BLACK
    private var currentStrokeWidth: Float = 3f

    override fun getView(): View {
        Log.d(TAG, "getView called")
        return rendLibView
    }

    fun drawPoint(x: Float, y: Float, color: Int, strokeWidth: Float) {
        Log.d(TAG, "drawPoint called with x=$x, y=$y, color=$color, strokeWidth=$strokeWidth")
        rendLibView.setColor(color)
        rendLibView.setStrokeWidth(strokeWidth)

        if (lastX != null && lastY != null) {
            Log.d(TAG, "Drawing line from ($lastX, $lastY) to ($x, $y)")
            val downTime = System.currentTimeMillis()
            
            val moveEvent = MotionEvent.obtain(
                downTime, downTime,
                MotionEvent.ACTION_MOVE,
                lastX!!, lastY!!,
                0
            )
            rendLibView.onTouchEvent(moveEvent)
            moveEvent.recycle()

            val moveEvent2 = MotionEvent.obtain(
                downTime, downTime,
                MotionEvent.ACTION_MOVE,
                x, y,
                0
            )
            rendLibView.onTouchEvent(moveEvent2)
            moveEvent2.recycle()
        } else {
            Log.d(TAG, "Drawing first point at ($x, $y)")
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
        Log.d(TAG, "clear called")
        lastX = null
        lastY = null
        rendLibView.surfaceCreated(rendLibView.holder)
    }

    override fun dispose() {
        Log.d(TAG, "dispose called")
        rendLibView.surfaceDestroyed(rendLibView.holder)
    }
}