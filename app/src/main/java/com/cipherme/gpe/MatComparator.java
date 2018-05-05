package com.cipherme.gpe;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.Comparator;

public class MatComparator implements Comparator<Mat> {

    @Override
    public int compare(Mat o1, Mat o2) {
        double cArea1 = Imgproc.contourArea(o1);
        double cArea2 = Imgproc.contourArea(o2);
        return Double.compare(cArea1, cArea2);
    }
}
