package com.example.draobrehtom.gesturerecognizecontrol;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

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

    JavaCameraView javaCameraView;
    Mat mRgba, mGray;
    BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case BaseLoaderCallback.SUCCESS:
                    javaCameraView.enableView();
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    // Own implementation
    Mat backgroundFrame;

    Mat frame;
    Mat currentFrame;
    Mat previosFrame;
    Mat resultFrame;
    Mat v = new Mat();
    Long currentFrameLong;
    Scalar scalar1 = new Scalar(0,0,255);
    Scalar scalar2 = new Scalar(0,255,0);

    Size size = new Size(3,3);
    int index = 0;
    int sensivity = 75;
    double maxArea = 300;
    boolean started = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        javaCameraView = (JavaCameraView) findViewById(R.id.java_camera_view);
        javaCameraView.setVisibility(View.VISIBLE);
        javaCameraView.setCvCameraViewListener(this);
        Intent intent = new Intent(this, HandTrackingActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (javaCameraView != null) {
            javaCameraView.disableView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCV loaded successfully");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        } else {
            Log.d(TAG, "OpenCV not loaded");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
        }

        if (javaCameraView == null) {
            javaCameraView.enableView();
        }
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();
        if (javaCameraView != null) {
            javaCameraView.disableView();
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        backgroundFrame = new Mat(height, width, CvType.CV_8UC4);
        currentFrame = new Mat(height, width, CvType.CV_8UC4);
        previosFrame = new Mat(height, width, CvType.CV_8UC4);
        resultFrame = new Mat(height, width, CvType.CV_8UC4);

        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mGray = new Mat(height, width, CvType.CV_8UC1);
    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        if (!started) {
            started = true;
            backgroundFrame = inputFrame.rgba();
        }
        currentFrame = inputFrame.rgba();

        Core.absdiff(backgroundFrame, currentFrame, resultFrame);
        Imgproc.threshold(resultFrame, resultFrame, 80, 255, Imgproc.THRESH_BINARY);
        Imgproc.erode(resultFrame, resultFrame, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, size));

//        Imgproc.GaussianBlur(currentFrame,currentFrame,size,0);

//        if (index > 1) {
//            Core.subtract(previosFrame, currentFrame, resultFrame);
//            Imgproc.cvtColor(resultFrame, resultFrame, Imgproc.COLOR_BGR2GRAY);
//            Imgproc.threshold(resultFrame, resultFrame, sensivity, 255, Imgproc.THRESH_BINARY);
//            List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
//            Imgproc.findContours(resultFrame, contours, v, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
//            v.release();
//
//            boolean found = false;
//            for (int idx = 0; idx < contours.size(); idx++) {
//                Mat contour = contours.get(idx);
//                double contourArea = Imgproc.contourArea(contour);
//                if (contourArea > maxArea) {
//                    found = true;
//                    Rect r = Imgproc.boundingRect(contours.get(idx));
//                    Imgproc.drawContours(currentFrame, contours, idx, scalar1);
//                    Imgproc.rectangle(currentFrame, r.br(), r.tl(), scalar2, 1);
//                }
//                contour.release();
//            }
//
//            if (found) {
//                Log.d("MainActivity", "Moved");
//            }
//        }
//        index++;
//        previosFrame = currentFrame;
        return resultFrame;
    }
}
