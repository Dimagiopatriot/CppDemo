package com.cipherme.gpe;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Utils {

    public static int nearestBoard(final Point point, Point[][] data) {
        final double[] perps = new double[4];
        for (int i = 0; i < data.length; ++i) {
            perps[i] = GZEMathematics.lineLength(GZEMathematics.perpendicular(data[i][0], data[i][1], point), point) +
                    GZEMathematics.lineLength(GZEMathematics.perpendicular(data[i][2], data[i][3], point), point);
        }

        int index = -1;
        double dist = Double.MAX_VALUE;
        for (int i = 0; i < perps.length; ++i) {
            if (dist > perps[i]) {
                dist = perps[i];
                index = i;
            }
        }
        return index;
    }

    public static Point[][] fillDataArray(final Mat mat) {
        final Point[][] data = new Point[4][4];
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                data[i][j] = new Point();
            }
        }
        data[0][0].x = 0;
        data[0][0].y = 0;
        data[0][1].x = 0;
        data[0][1].y = mat.rows();
        data[0][2].x = 0;
        data[0][2].y = 0;
        data[0][3].x = mat.cols();
        data[0][3].y = 0;

        data[1][0].x = 0;
        data[1][0].y = 0;
        data[1][1].x = mat.cols();
        data[1][1].y = 0;
        data[1][2].x = mat.cols();
        data[1][2].y = 0;
        data[1][3].x = mat.cols();
        data[1][3].y = mat.rows();

        data[2][0].x = mat.cols();
        data[2][0].y = 0;
        data[2][1].x = mat.cols();
        data[2][1].y = mat.rows();
        data[2][2].x = 0;
        data[2][2].y = mat.rows();
        data[2][3].x = mat.cols();
        data[2][3].y = mat.rows();

        data[3][0].x = 0;
        data[3][0].y = mat.rows();
        data[3][1].x = mat.cols();
        data[3][1].y = mat.rows();
        data[3][2].x = 0;
        data[3][2].y = 0;
        data[3][3].x = 0;
        data[3][3].y = mat.rows();

        return data;
    }

    public static List<MatOfPoint> findAndNormCont(final Mat mat, boolean doApprox) {
        List<MatOfPoint> needCont = new ArrayList<>();
        List<MatOfPoint> cont = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(mat, cont, hierarchy, Imgproc.RETR_LIST, Imgproc.CV_CHAIN_APPROX_SIMPLE, new Point());
        if (!doApprox) {
            needCont = cont;
        } else {
            for (MatOfPoint item : cont) {
                MatOfPoint2f contoursPoly = new MatOfPoint2f();
                MatOfPoint2f tempItem = new MatOfPoint2f(item.toArray());
                Imgproc.approxPolyDP(tempItem, contoursPoly, 3, true);
                needCont.add(new MatOfPoint(contoursPoly.toArray()));
            }
        }

        Collections.sort(needCont, new MatComparator());

        return needCont;
    }
}
