package com.cipherme.cppdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.

    private static final String TAG = "MainActivity";

    static {
        System.loadLibrary("native-lib");
        System.loadLibrary("opencv_java3");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());

        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCV doesn't loaded");
            Toast.makeText(this, "OpenCV doesn't loaded", Toast.LENGTH_LONG).show();
        }
        else {
            Log.d(TAG, "OpenCV loaded!!!");
            Toast.makeText(this, "OpenCV loaded!!!", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
