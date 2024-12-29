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
    private val TAG = "RenderLibSurfaceView"
    private var mPaint: Paint? = null
    private var mBitmap: Bitmap? = null
    private var mHolder: SurfaceHolder? = null
    private var mPaintCanvas: Canvas? = null
    private var mPath: Path = Path()
    private var isDrawing = false

    init {
        Log.d(TAG, "Initializing RenderLibSurfaceView")
        holder.addCallback(this)
        mBitmap = Bitmap.createBitmap(3840, 2160, Bitmap.Config.ARGB_8888)
        
        mPaint = Paint().apply {
            strokeWidth = 4.0f
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
            isAntiAlias = true
            color = Color.BLACK
        }
        mPaintCanvas = Canvas(mBitmap!!)
        Log.d(TAG, "Initialization complete")
    }

    fun setColor(color: Int) {
        Log.d(TAG, "setColor called with color=$color")
        mPaint?.color = color
    }

    fun setStrokeWidth(width: Float) {
        Log.d(TAG, "setStrokeWidth called with width=$width")
        mPaint?.strokeWidth = width
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        Log.d(TAG, "onTouchEvent called with action=${event.action} at (${event.x}, ${event.y})")
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mPath.reset()
                mPath.moveTo(event.x, event.y)
                isDrawing = true
                Log.d(TAG, "Pen down at (${event.x}, ${event.y})")
            }
            MotionEvent.ACTION_MOVE -> {
                if (isDrawing) {
                    mPath.lineTo(event.x, event.y)
                    mPaintCanvas?.drawPath(mPath, mPaint!!)
                    Log.d(TAG, "Drawing line to (${event.x}, ${event.y})")
                }
            }
            MotionEvent.ACTION_UP -> {
                if (isDrawing) {
                    mPath.lineTo(event.x, event.y)
                    mPaintCanvas?.drawPath(mPath, mPaint!!)
                    mPath.reset()
                    isDrawing = false
                    Log.d(TAG, "Pen up at (${event.x}, ${event.y})")
                }
            }
        }
        updateSurface()
        return true
    }

    private fun updateSurface() {
        mHolder?.let { holder ->
            try {
                val canvas = holder.lockCanvas()
                synchronized(holder) {
                    canvas.drawColor(Color.WHITE)
                    mBitmap?.let {
                        canvas.drawBitmap(it, 0f, 0f, null)
                    }
                }
                holder.unlockCanvasAndPost(canvas)
                Log.d(TAG, "Surface updated")
            } catch (e: Exception) {
                Log.e(TAG, "Error updating surface: ${e.message}")
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        Log.d(TAG, "onDraw called")
        mBitmap?.let { 
            canvas.drawBitmap(it, 0f, 0f, null)
            Log.d(TAG, "Drew bitmap on canvas")
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        Log.d(TAG, "surfaceCreated called")
        mHolder = holder
        updateSurface()
        Log.d(TAG, "Surface initialized with white background")
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        Log.d(TAG, "surfaceChanged called with width=$width, height=$height")
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        Log.d(TAG, "surfaceDestroyed called")
    }
}
