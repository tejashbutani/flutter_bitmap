import 'package:flutter/services.dart';
import 'package:flutter/material.dart';

class NativeDrawingView extends StatefulWidget {
  const NativeDrawingView({super.key});

  @override
  State<NativeDrawingView> createState() => _NativeDrawingViewState();
}

class _NativeDrawingViewState extends State<NativeDrawingView> {
  static const MethodChannel _channel = MethodChannel('drawing_channel');
  bool isDrawing = false;

  Future<void> _handleDrawPoint(Offset point, Color color, double strokeWidth, {bool isEndStroke = false}) async {
    try {
      await _channel.invokeMethod('drawStroke', {
        'points': [
          {'x': point.dx, 'y': point.dy}
        ],
        'color': (color.value & 0xFFFFFFFF).toInt(),
        'strokeWidth': strokeWidth,
        'isEndStroke': isEndStroke,
      });
    } catch (e) {
      debugPrint('Error sending point to native: $e');
    }
  }

  @override
  Widget build(BuildContext context) {
    return Stack(
      children: [
        const AndroidView(
          viewType: 'native-drawing-view',
          creationParams: <String, dynamic>{},
          creationParamsCodec: StandardMessageCodec(),
        ),
        Listener(
          behavior: HitTestBehavior.opaque,
          onPointerDown: (details) {
            isDrawing = true;
            _handleDrawPoint(
              details.localPosition,
              Colors.black,
              3.0,
            );
          },
          onPointerMove: (details) {
            if (isDrawing) {
              _handleDrawPoint(
                details.localPosition,
                Colors.black,
                3.0,
              );
            }
          },
          onPointerUp: (details) {
            if (isDrawing) {
              _handleDrawPoint(
                details.localPosition,
                Colors.black,
                3.0,
                isEndStroke: true,
              );
              isDrawing = false;
            }
          },
          onPointerCancel: (details) {
            if (isDrawing) {
              _handleDrawPoint(
                details.localPosition,
                Colors.black,
                3.0,
                isEndStroke: true,
              );
              isDrawing = false;
            }
          },
        ),
      ],
    );
  }
}
