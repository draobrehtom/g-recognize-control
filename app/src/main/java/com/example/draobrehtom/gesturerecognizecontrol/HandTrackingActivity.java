package com.example.draobrehtom.gesturerecognizecontrol;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;

public class HandTrackingActivity extends Activity implements OnTouchListener, CvCameraViewListener2 {
    private static final String TAG = "OCVSample::Activity";
    private int Height = 720;
    private int Width  = 1280;

    private final double alpha = 2.0;
    private final double beta = 0.0;

    private final int KERNEL_SIZE = 5;
    Mat kernel;
    private Scalar CONTOUR_COLOR;

    private CameraBridgeViewBase mOpenCvCameraView;

    public boolean haveRange = false;
    Mat mRgba;
    Mat mHsv;
    Mat mOutput;
    Mat threshold;
    Mat mHierarchy;
    Point[] squares;
    Point touched;
    int[][] min_range;
    int[][] max_range;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_hand_tracking);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.handtracking_activity_java_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.setOnTouchListener(HandTrackingActivity.this);
        mOpenCvCameraView.setFocusable(false);
    }

    public void onCameraViewStarted(int width, int height) {
        Log.i(TAG, "-----------Widht: " +width+" Height: "+height);
        Height = height;
        Width = width;
        mRgba = new Mat(height, width, CvType.CV_8UC3);
        mHsv = new Mat(); //(height, width, CvType.CV_8UC3);
        threshold = new Mat(); //(height, width, CvType.CV_8UC3);
        kernel = Mat.zeros(KERNEL_SIZE, KERNEL_SIZE, CvType.CV_8UC1);
        CONTOUR_COLOR = new Scalar(255,0,0);
        mHierarchy = new Mat();
        squares = getSquares(new Point(mRgba.width()/2,  mRgba.height()/2), (int)(Width*20)/1280);
        max_range = new int[squares.length][3];
        min_range = new int[squares.length][3];
        for(int i=0; i<squares.length; i++) {
            for(int j=0; j<3; j++) {
                min_range[i][j]=256;
            }
        }
    }

    public void onCameraViewStopped() {
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        Mat blurredImage = new Mat();
        Mat hsvImage = new Mat();
        Mat mask = new Mat();
        Mat morphOutput = new Mat();
        Mat frame = inputFrame.rgba();

        // remove some noise
        Imgproc.blur(frame, blurredImage, new Size(7, 7));

        // convert the frame to HSV
        Imgproc.cvtColor(blurredImage, hsvImage, Imgproc.COLOR_BGR2HSV);

        // morphological operators
        // dilate with large element, erode with small ones
        Mat dilateElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(24, 24));
        Mat erodeElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(12, 12));

        Imgproc.erode(mask, morphOutput, erodeElement);
        Imgproc.erode(mask, morphOutput, erodeElement);

        Imgproc.dilate(mask, morphOutput, dilateElement);
        Imgproc.dilate(mask, morphOutput, dilateElement);

        // show the partial output
        //this.onFXThread(this.morphProp, this.mat2Image(morphOutput));

        return morphOutput;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(haveRange) {
            haveRange=false;
            return false;
        }
        haveRange = true;
        //mRgba.convertTo(mRgba, -1, alpha, beta);
        Imgproc.cvtColor(mRgba, mHsv, Imgproc.COLOR_RGB2HSV);
        List<Mat> channels = new ArrayList<Mat>();
        Core.split(mHsv, channels);

        for (int c=0; c<3;c++){
            Mat ch = channels.get(c);
            for(int s=0; s < squares.length; s+=2) {
                Mat subMat = ch.submat((int)squares[s].y, (int)squares[s+1].y, (int)squares[s].x, (int)squares[s+1].x);
                MinMaxLocResult result = Core.minMaxLoc(subMat);
                min_range[s][c] = (int) result.minVal;
                max_range[s][c] = (int) result.maxVal;
            }
        }

        return false;
    }

    Point[] getSquares(Point center, int side){
        Point[] squares = new Point[10*2];
        int step = (Width*100)/1280;
        int left = -step, right = step, up = -step, down = step;

        squares[0]  = new Point(center.x - side, center.y-side);
        squares[1]  = new Point(center.x + side, center.y+side);
        squares[2]  = new Point(center.x + left - side, center.y - side);
        squares[3]  = new Point(center.x + left + side, center.y + side);
        squares[4]  = new Point(center.x + right - side, center.y - side);
        squares[5]  = new Point(center.x + right + side, center.y + side);
        squares[6]  = new Point(center.x - side, center.y + up - side);
        squares[7]  = new Point(center.x + side, center.y + up + side);
        squares[8]  = new Point(center.x - side, center.y + down - side);
        squares[9]  = new Point(center.x + side, center.y + down + side);
        squares[10] = new Point(center.x + left - side, center.y + up - side);
        squares[11] = new Point(center.x + left + side, center.y + up + side);
        squares[12] = new Point(center.x + right - side, center.y + up - side);
        squares[13] = new Point(center.x + right  + side, center.y + up + side);
        squares[14] = new Point(center.x + left - side, center.y + down - side);
        squares[15] = new Point(center.x + left + side, center.y + down + side);
        squares[16] = new Point(center.x + right - side, center.y + down - side);
        squares[17] = new Point(center.x + right  + side, center.y + down + side);
        squares[18] = new Point(center.x - side, center.y + up + up - side);
        squares[19] = new Point(center.x + side, center.y + up + up + side);

        return squares;
    }
    /***************************************************************************
     * *************************************************************************/
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };


    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

}
