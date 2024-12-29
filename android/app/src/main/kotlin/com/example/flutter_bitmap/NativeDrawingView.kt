package com.example.flutter_bitmap

import android.content.Context
import android.text.method.Touch.onTouchEvent
import android.view.MotionEvent
import android.view.View
import io.flutter.plugin.platform.PlatformView

class NativeDrawingView(context: Context) : PlatformView {
    private val rendLibView: RendLibSurfaceView = RendLibSurfaceView(context)

    override fun getView(): View {
        return rendLibView
    }

    fun drawPoint(x: Float, y: Float, color: Int, strokeWidth: Float) {
        val downTime = System.currentTimeMillis()
        val eventTime = downTime
        val action = MotionEvent.ACTION_DOWN
        val metaState = 0
        val motionEvent = MotionEvent.obtain(
            downTime, 
            eventTime, 
            action, 
            x, 
            y, 
            metaState
        )
        rendLibView.onTouchEvent(motionEvent)
        motionEvent.recycle()
    }

    fun clear() {
        rendLibView.surfaceCreated(rendLibView.holder)
    }

    override fun dispose() {
        rendLibView.surfaceDestroyed(rendLibView.holder)
    }
}