package com.cipherme.gpe;

import android.support.annotation.Nullable;

import org.opencv.core.Point;

public class GZEMathematics {

    protected static double sqr(final double a) { return Math.pow(a, 2); }

    protected static void maxSwap(final double[] values) {
        if (values == null) { return; }
        if (values[0] < values[values.length - 1]) {
            double temp = values[0];
            values[values.length - 1] = values[0];
            values[0] = temp;
        }
    }

    protected static double lineLength(final double x1, final double y1,
                                       final double x2, final double y2) {
        return Math.sqrt(sqr(x2 - x1) + sqr(y2 - y1));
    }

    protected static double lineLength(final Point point1, final Point point2) {
        return lineLength(point1.x, point1.y, point2.x, point2.y);
    }

    @Nullable
    protected static Point perpendicular(final Point p1, final Point p2, final Point p3) {
        final Point result = new Point();
        if (p1.x == p2.x) {
            result.x = p1.x;
            result.y = p3.y;
        } else if (p1.y == p2.y) {
            result.x = p3.x;
            result.y = p1.y;
        } else {
            result.x = (p1.x * sqr(p2.y - p1.y) + p3.x * sqr(p2.x - p1.x) + (p2.x - p1.x) * (p2.y - p1.y) * (p3.y - p1.y))
                    / (sqr(p2.y - p1.y) + sqr(p2.x - p1.x));
            result.y = ((p2.x - p1.x) * (p3.x - result.x)) / (p2.y - p1.y) + p3.y;
        }
        return result;
    }
}
