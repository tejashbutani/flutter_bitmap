package com.example.flutter_bitmap

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PixelFormat
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import display.interactive.renderlib.RenderUtils

/**
 * @ClassName: display.interactive.rendlibtools.view
 * @Description: 作用表述
 * @Author: maoxingwen
 * @Date: 2024/11/23
 */
class RendLibSurfaceView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {
    private var mPaint: Paint? = null
    private var mBitmap: Bitmap? = null
    private var mHolder: SurfaceHolder? = null
    private var mPaintCanvas: Canvas? = null

    init {
        holder.addCallback(this)
        // Create bitmap for drawing
        mBitmap = Bitmap.createBitmap(2000, 2000, Bitmap.Config.ARGB_8888)
        
        // init paint
        mPaint = Paint().apply {
            strokeWidth = 4.0f
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            isAntiAlias = true
        }
        mPaintCanvas = Canvas(mBitmap!!)
    }

    fun setColor(color: Int) {
        mPaint?.color = color
    }

    fun setStrokeWidth(width: Float) {
        mPaint?.strokeWidth = width
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mPaintCanvas?.drawPoint(event.x, event.y, mPaint!!)
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                mPaintCanvas?.drawPoint(event.x, event.y, mPaint!!)
                invalidate()
            }
        }
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mBitmap?.let { canvas.drawBitmap(it, 0f, 0f, null) }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        mHolder = holder
        val canvas = mHolder!!.lockCanvas()
        canvas.drawColor(Color.WHITE)
        mBitmap?.eraseColor(Color.WHITE)
        mHolder!!.unlockCanvasAndPost(canvas)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {}
}
