package com.cipherme.gpe;

import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;

public class GPEReader {

    private static final String TAG = "GPEReader";

    public boolean findGPE(final Mat image, float deleterMaxWidth, float deleterMinWidth, float deleterMaxHeight,
                           float deleterMinHeight, float centerPercentX, float centerPercentY, final Mat gpe, double thresDeleter) {
        Mat roi = Mat.zeros(500, 500, CvType.CV_8UC1);
        Imgproc.cvtColor(image, roi, Imgproc.COLOR_RGB2GRAY);
        Core.MinMaxLocResult maxLocResult = Core.minMaxLoc(roi);

        Imgproc.threshold(roi, roi, maxLocResult.maxVal / thresDeleter, 255, Imgproc.THRESH_BINARY_INV);
        List<MatOfPoint> needCont = Utils.findAndNormCont(roi, false);

        float imageCenterX = (float) roi.cols() * 0.5f;
        float imageCenterY = (float) roi.rows() * 0.5f;

        int mindw, mindh, maxdw, maxdh;
        int mindw2, mindh2, maxdw2, maxdh2;
        {
            float a = (float) roi.cols() / deleterMinWidth;
            mindw = (int) a;
            mindw2 = (int) (a - (a / 100.0f * 4.0f));
            a = (float) roi.rows() / deleterMinHeight;
            mindh = (int) a;
            mindh2 = (int) (a - (a / 100.0f * 4.0f));
            a = (float) roi.cols() / deleterMaxWidth;
            maxdw = (int) a;
            maxdw2 = (int) (a - (a / 100.0f * 4.0f));
            a = (float) roi.rows() / deleterMaxHeight;
            maxdh = (int) a;
            maxdh2 = (int) (a - (a / 100.0f * 4.0f));
        }

        boolean firstObj = true;
        for (int i = 0; i < needCont.size(); ++i) {
            final MatOfPoint2f tempNeedContItem = new MatOfPoint2f(needCont.get(i).toArray());
            final RotatedRect rectGze = Imgproc.minAreaRect(tempNeedContItem);
            double a = rectGze.size.width;
            double b = rectGze.size.height;

            double[] swap = new double[]{a, b};
            GZEMathematics.maxSwap(swap);

            a = swap[0];
            b = swap[1];

            if (PercentCompare.isEqual(a / b, 1.0f, 5.0f)) {
                if (firstObj) {
                    if (rectGze.size.height > maxdh || rectGze.size.height < mindh
                            || rectGze.size.width > maxdw || rectGze.size.width < mindw)
                        continue;
                } else {
                    if (rectGze.size.height > maxdh2 || rectGze.size.height < mindh2
                            || rectGze.size.width > maxdw2 || rectGze.size.width < mindw2)
                        continue;
                }

                if (!PercentCompare.isEqual(rectGze.center.x, imageCenterX, centerPercentX)
                        || !PercentCompare.isEqual(rectGze.center.y, imageCenterY, centerPercentY))
                    continue;
                if (firstObj) {
                    firstObj = false;
                    continue;
                }
                Point[] coos = new Point[4];
                Point[] listData = new Point[4];
                rectGze.points(listData);
                final Point[][] data = Utils.fillDataArray(roi);
                {
                    Point[] tempListData = Arrays.copyOf(listData, listData.length);
                    for (int j = 0; j < listData.length; ++j) {
                        listData[Utils.nearestBoard(tempListData[j], data)] = tempListData[j];
                    }
                    for (Point item : listData) {
                        item.x = Math.round(item.x);
                        item.y = Math.round(item.y);
                    }
                }

                int length = (int) Math.max(0.0f, GZEMathematics.lineLength(listData[0], listData[1]));
                length = (int) Math.max((float) length, GZEMathematics.lineLength(listData[1], listData[2]));
                length = (int) Math.max((float) length, GZEMathematics.lineLength(listData[2], listData[3]));
                length = (int) Math.max((float) length, GZEMathematics.lineLength(listData[0], listData[3]));

                final Mat mat = new Mat(length, length, roi.type());
                coos[0] = new Point();
                coos[1] = new Point(mat.cols(), 0);
                coos[2] = new Point(mat.cols(), mat.rows());
                coos[3] = new Point(0, mat.rows());

                Mat matOfPoint2f = new MatOfPoint2f(listData);
                Mat matOfPoint2f1 = new MatOfPoint2f(coos);

                Imgproc.warpPerspective(image, gpe, Imgproc.getPerspectiveTransform(
                        matOfPoint2f, matOfPoint2f1),
                        mat.size(), Imgproc.INTER_NEAREST, Core.BORDER_DEFAULT, new Scalar(new double[]{}));

                Mat weirdGpe = gpe.clone();
                unsharpMasking(weirdGpe.nativeObj, gpe.nativeObj);

                Mat temp = gpe.clone();
                laplacianMode(temp.nativeObj);
                temp.convertTo(gpe, -1, 1.1, 47);

                double laplacianMode = laplacianMode(gpe.nativeObj);

                Log.d(TAG, "Laplacian GPE : " + String.valueOf(laplacianMode));


                return true;
            }
        }
        return false;
    }

    private native void unsharpMasking(long matSrc, long matDest);

    private native double laplacianMode(long matSrc);

}
