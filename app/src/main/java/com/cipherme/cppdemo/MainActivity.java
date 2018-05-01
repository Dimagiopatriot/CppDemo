package com.cipherme.cppdemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.annotation.UiThread;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCamera2View;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    // Used to load the 'native-lib' library on application startup.

    private static final String TAG = "MainActivity";
    private static final int CAMERA_REQUEST_CODE = 128;
    protected Handler handler;
    protected Bitmap mBitmap;

    protected Mat mMainFrame;
    protected Mat mQrCode;

    static {
        System.loadLibrary("native-lib");
        System.loadLibrary("opencv_java3");
    }

    protected JavaCameraView mJavaCamera2View;
    protected ImageView mQrResult;

    protected BaseLoaderCallback mBaseLoaderCallback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mQrResult = findViewById(R.id.result);

        mBaseLoaderCallback = new BaseLoaderCallback(this) {
            @Override
            public void onManagerConnected(int status) {
                switch (status) {
                    case BaseLoaderCallback.SUCCESS:
                        if (mJavaCamera2View != null) {
                            mJavaCamera2View.enableView();
                        }
                        else {
                            break;
                        }
                        break;
                    default:
                        super.onManagerConnected(status);
                        break;
                }
            }
        };

        mJavaCamera2View = findViewById(R.id.getFromCamera);
        mJavaCamera2View.setVisibility(View.VISIBLE);
        mJavaCamera2View.setCvCameraViewListener(this);

        mBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (OpenCVLoader.initDebug()) {
            mBaseLoaderCallback.onManagerConnected(BaseLoaderCallback.SUCCESS);

        }
        else {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mBaseLoaderCallback);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mJavaCamera2View != null)
            mJavaCamera2View.disableView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(null);
        if (mJavaCamera2View != null)
            mJavaCamera2View.disableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mMainFrame = new Mat(height, width, CvType.CV_8UC4);
        mQrCode = new Mat(100, 100, CvType.CV_8UC1);
    }

    @Override
    public void onCameraViewStopped() {
        mMainFrame.release();
        mQrCode.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mMainFrame = inputFrame.rgba();
        calcQR(mMainFrame.nativeObj, mQrCode.nativeObj);
        Utils.matToBitmap(mQrCode, mBitmap);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setBitmap(mBitmap);
            }
        });
        return mMainFrame;
    }

    @UiThread
    protected void setBitmap(final Bitmap bitmap) {
        if (mQrResult != null)
            mQrResult.setImageBitmap(bitmap);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (mJavaCamera2View != null && !mJavaCamera2View.isEnabled())
                        mJavaCamera2View.enableView();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native int calcQR(long matRes, long matQr);
}
