import 'dart:developer';
import 'dart:typed_data';
import 'dart:ui';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter/foundation.dart';

const platform = MethodChannel('com.example.flutter_bitmap/render');

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: '',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
        useMaterial3: true,
      ),
      home: const MyHomePage(title: ''),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key, required this.title});

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  static const String tag = "_MyHomePageState";

  final int _counter = 0;
  List<DrawingPoint?> points = [];
  Color selectedColor = Colors.black;
  double strokeWidth = 3.0;
  late Uint8List _bitmapData = Uint8List(0);
  late Uint8List _strokeBitmapData = Uint8List(0);

  @override
  void initState() {
    super.initState();
    _initRenderLib();
  }

  Future<void> _initRenderLib() async {
    try {
      log('$tag: Initializing render library');
      await platform.invokeMethod('initRenderLib');

      log('$tag: Getting device resolution');
      final List<int> resolution = await platform.invokeMethod('getDeviceNativeResolution');
      final int width = resolution[0];
      final int height = resolution[1];
      log('$tag: Device resolution: ${width}x$height');

      log('$tag: Getting accelerated bitmap');
      _bitmapData = await platform.invokeMethod('getAccelerateBitmap', {'width': width, 'height': height});
      log('$tag: Received bitmap data of size: ${_bitmapData.length}');

      log('$tag: Getting stroke bitmap');
      _strokeBitmapData = await platform.invokeMethod('getStrokeBitmap');
      log('$tag: Received stroke bitmap data of size: ${_strokeBitmapData.length}');

      // Create a temporary painter to cache the image
      final painter = DrawingPainter(points: points, strokeBitmapData: _strokeBitmapData);
      // Assuming _cacheImage is a method that needs to be defined in DrawingPainter
      // Since it's not defined, we'll comment out the line that calls it
      // await painter._cacheImage();
    } on PlatformException catch (e) {
      log('$tag: Failed to initialize render library: ${e.message}');
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
        title: Text(widget.title),
      ),
      body: Stack(
        children: [
          GestureDetector(
            onPanStart: (details) {
              setState(() {
                points.add(
                  DrawingPoint(
                    details.localPosition,
                    Paint()
                      ..color = selectedColor
                      ..isAntiAlias = true
                      ..strokeWidth = strokeWidth
                      ..strokeCap = StrokeCap.round,
                  ),
                );
              });
            },
            onPanUpdate: (details) {
              setState(() {
                points.add(
                  DrawingPoint(
                    details.localPosition,
                    Paint()
                      ..color = selectedColor
                      ..isAntiAlias = true
                      ..strokeWidth = strokeWidth
                      ..strokeCap = StrokeCap.round,
                  ),
                );
              });
            },
            onPanEnd: (details) {
              setState(() {
                points.add(null);
              });
            },
            child: CustomPaint(
              painter: DrawingPainter(points: points, strokeBitmapData: _strokeBitmapData),
              size: Size.infinite,
            ),
          ),
          Positioned(
            bottom: 20,
            right: 20,
            child: FloatingActionButton(
              onPressed: () {
                setState(() {
                  points.clear();
                });
              },
              child: const Icon(Icons.clear),
            ),
          ),
        ],
      ),
    );
  }
}

class DrawingPoint {
  final Offset offset;
  final Paint paint;

  DrawingPoint(this.offset, this.paint);
}

class DrawingPainter extends CustomPainter {
  final List<DrawingPoint?> points;
  final Uint8List strokeBitmapData;

  DrawingPainter({required this.points, required this.strokeBitmapData});

  @override
  void paint(Canvas canvas, Size size) async {
    if (strokeBitmapData.isNotEmpty) {
      final image = await decodeImageFromList(strokeBitmapData);
      canvas.drawImage(image, Offset.zero, Paint());
    }

    for (int i = 0; i < points.length - 1; i++) {
      if (points[i] != null && points[i + 1] != null) {
        canvas.drawLine(points[i]!.offset, points[i + 1]!.offset, points[i]!.paint);
      } else if (points[i] != null && points[i + 1] == null) {
        canvas.drawPoints(
          PointMode.points,
          [points[i]!.offset],
          points[i]!.paint,
        );
      }
    }
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => true;
}
