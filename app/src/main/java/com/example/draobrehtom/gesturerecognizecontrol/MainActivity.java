package com.example.draobrehtom.gesturerecognizecontrol;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    static {
        System.loadLibrary("MyLibs");
        Log.d(TAG, NativeClass.getMessageFromJNI());
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCV not Loaded");
        } else {
            Log.d(TAG, "OpenCV Loaded");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
