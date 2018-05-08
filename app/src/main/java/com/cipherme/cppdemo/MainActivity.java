package com.cipherme.cppdemo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.annotation.UiThread;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.cipherme.api.RetrofitProvider;
import com.cipherme.arch.MainScreenContractHolder;
import com.cipherme.entities.models.response.present.GetKey;
import com.cipherme.entities.models.response.present.Verify;
import com.cipherme.gpe.GPEReader;
import com.cipherme.util.Utils;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

public class MainActivity extends AppCompatActivity implements
        CameraBridgeViewBase.CvCameraViewListener2, MainScreenContractHolder.MainView {

    // Used to load the 'native-lib' library on application startup.

    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE_CAMERA_PERMISSION = 128;

    protected Bitmap mBitmap;
    protected GPEReader gpeReader;
    protected Switch mLight;
    protected MainPresenter mMainPresenter;

    protected Mat mMainFrame;
    protected Mat mQrCode;

    Rect rect;

    protected RetrofitProvider retrofitProvider = new RetrofitProvider();

    static {
        System.loadLibrary("native-lib");
        System.loadLibrary("opencv_java3");
    }

    protected JavaCameraView mJavaCamera2View;
    protected ImageView mQrResult;
    protected ProgressDialog dialog;

    protected BaseLoaderCallback mBaseLoaderCallback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        retrofitProvider.start();
        mMainPresenter = new MainPresenter(retrofitProvider.getRetrofit(), this);

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

        mBitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);
        gpeReader = new GPEReader();

        mLight = findViewById(R.id.light);
        mLight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mJavaCamera2View != null)
                    if (isChecked) {
                        mJavaCamera2View.turnOnTheFlash();
                    }
                    else {
                         mJavaCamera2View.turnOffTheFlash();
                    }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Utils.hasPermissions(this, Utils.permissions)) {
            mMainPresenter.attachView(this);
            showProgressDialog(this);
            mMainPresenter.getKey(Utils.getDeviceUUID(this));
        }
        else {
            attemptToPermissions();
        }
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
    protected void onStop() {
        mMainPresenter.detachView();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mJavaCamera2View != null)
            mJavaCamera2View.disableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mMainFrame = new Mat(height, width, CvType.CV_8UC4);
        mQrCode = new Mat(500, 500, CvType.CV_8UC3);
        mJavaCamera2View.setZoom(15);
    }

    @Override
    public void onCameraViewStopped() {
        mMainFrame.release();
        mQrCode.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        mMainFrame = inputFrame.rgba();
        mMainPresenter.computeGpe(inputFrame);
        return mMainFrame;
    }

    @Override
    public void onShowGPE(Mat mat) {
        if (mat != null) {
            final Bitmap bitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
            org.opencv.android.Utils.matToBitmap(mat, bitmap);
            runOnUiThread(() -> setBitmap(bitmap));
        }
    }

    @UiThread
    protected void setBitmap(final Bitmap bitmap) {
        if (mQrResult != null)
            mQrResult.setImageBitmap(bitmap);
    }

    @Override
    public void onSuccessfulGetKey(GetKey getKey) {
        mMainPresenter.auth(getKey.getResult().getAuthKey());
    }

    @Override
    public void onSuccessfulAuth(String token) {
        hideProgressDialog();
        mMainPresenter.prepareVerify(token);
    }

    @Override
    public void onSuccessfulQrGpe(String[] results, String token) {
        showProgressDialog(this);
        mMainPresenter.verify(results[1], results[0], token);
    }

    @Override
    public void onSuccessfulVerify(Verify verify) {
        hideProgressDialog();
        Toast.makeText(this, "Verify success:\n" + verify.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFailure(Throwable throwable) {
        hideProgressDialog();
        Toast.makeText(this, throwable.getLocalizedMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFailure(String message) {
        hideProgressDialog();
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void attemptToPermissions() {
        ActivityCompat.requestPermissions(this, com.cipherme.util.Utils.permissions, REQUEST_CODE_CAMERA_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_CAMERA_PERMISSION:
                if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    showProgressDialog(this);
                    mMainPresenter.getKey(com.cipherme.util.Utils.getDeviceUUID(this));
                }
                break;
            default:
                break;
        }
    }

    public void showProgressDialog(Context context) {
        dialog = ProgressDialog.show(context, "", "Please wait....");
    }

    public void hideProgressDialog() {
        if (dialog != null)
            dialog.dismiss();
    }
}
