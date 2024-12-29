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
class RendLibSurfaceView : SurfaceView, SurfaceHolder.Callback {
    private var mHolder: SurfaceHolder? = null

    private var mBitmap: Bitmap? = null


    private var mScreenWidth = 0

    private var mScreenHeight = 0

    /**
     * Drawing Camvas
     */
    private var mPaintCanvas: Canvas? = null


    private var mPaint: Paint? = null


    private var Prex = 0.0f
    private var Prey = 0.0f
    private val mPath = Path()


    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context)
    }

    private fun init(context: Context) {
        // Library resources need to be loaded before use

        RenderUtils.initRendLib()
        // getScreenSize
        val resolution: IntArray = RenderUtils.getDeviceNativeResolution(context)
        mScreenWidth = resolution[0]
        mScreenHeight = resolution[1]
        mBitmap = RenderUtils.getAccelerateBitmap(3840, 2160)

        holder.addCallback(this)


        mPath.moveTo(0f, 100f)


        // init paint
        mPaint = Paint()
        mPaint!!.color = Color.RED
        mPaint!!.strokeWidth = 4.0f
        mPaint!!.style = Paint.Style.STROKE
        mPaint!!.strokeCap = Paint.Cap.ROUND
        mPaint!!.isAntiAlias = true
        mPaintCanvas = Canvas()
        mPaintCanvas!!.setBitmap(mBitmap)
    }

    override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
        if (surfaceHolder != null) {
            mHolder = surfaceHolder
            val canvas = mHolder!!.lockCanvas()
            // Set the background of the acceleration bitmap to transparent
            canvas.drawColor(Color.WHITE)
            mHolder!!.setFormat(PixelFormat.TRANSPARENT)
            mHolder!!.unlockCanvasAndPost(canvas)
        } else {
            Log.w("TestMXW", "surfaceHolder is nulll !!!")
        }
    }

    override fun surfaceChanged(surfaceHolder: SurfaceHolder, i: Int, i1: Int, i2: Int) {
    }

    override fun surfaceDestroyed(surfaceHolder: SurfaceHolder) {
        RenderUtils.clearBitmapContent()
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (MotionEvent.ACTION_DOWN == event.action) {
            Prex = event.x
            Prey = event.y
            mPath.moveTo(event.x, event.y)
            mPaintCanvas!!.drawPoint(Prex, Prey, mPaint!!)
        } else if (MotionEvent.ACTION_UP == event.action) {
            mPaintCanvas!!.drawPoint(event.x, event.y, mPaint!!)
        } else if (MotionEvent.ACTION_MOVE == event.action) {
            mPath.quadTo(Prex, Prey, event.x, event.y)
            Prex = event.x
            Prey = event.y
            mPaintCanvas!!.drawPath(mPath, mPaint!!)
        }
        return true
    }
}
