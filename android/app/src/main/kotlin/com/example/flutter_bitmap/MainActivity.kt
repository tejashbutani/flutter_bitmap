package com.example.flutter_bitmap

import android.graphics.Color
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity: FlutterActivity() {
    private var drawingView: NativeDrawingView? = null
    
    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        
        // Register platform view factory
        flutterEngine.platformViewsController.registry
            .registerViewFactory("native-drawing-view", NativeDrawingViewFactory(context))
        
        // Setup method channel
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, "drawing_channel")
            .setMethodCallHandler { call, result ->
                when (call.method) {
                    "drawStroke" -> {
                        val points = call.argument<List<Map<String, Double>>>("points")
                        val color = call.argument<Int>("color") ?: Color.BLACK
                        val strokeWidth = call.argument<Double>("strokeWidth")?.toFloat() ?: 3f
                        
                        points?.forEach { point ->
                            drawingView?.drawPoint(
                                point["x"]?.toFloat() ?: 0f,
                                point["y"]?.toFloat() ?: 0f,
                                color,
                                strokeWidth
                            )
                        }
                        result.success(null)
                    }
                    "clear" -> {
                        drawingView?.clear()
                        result.success(null)
                    }
                    else -> result.notImplemented()
                }
            }
    }
}