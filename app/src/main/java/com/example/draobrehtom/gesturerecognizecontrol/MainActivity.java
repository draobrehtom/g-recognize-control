package com.example.draobrehtom.gesturerecognizecontrol;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";


    public static String currentIP;
    public static int    currentPort;

    TextView textResponse;
    EditText editTextAddress, editTextPort;
    Button buttonConnect, buttonClear;


    static {
        System.loadLibrary("MyLibs");
        Log.d(TAG, NativeClass.getMessageFromJNI());
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCV not Loaded");
        } else {
            Log.d(TAG, "OpenCV Loaded");
        }
    }

    //define callback function
    public void tcpHandler(String ip, int port, String query, Client.MyCallbackInterface callback) {
        new Client(ip, port, query, callback).execute();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setContentView(R.layout.activity_main);
        editTextAddress = (EditText) findViewById(R.id.ipText);
        editTextPort = (EditText) findViewById(R.id.portTxt);
        buttonConnect = (Button) findViewById(R.id.connectBtn);
        buttonClear = (Button) findViewById(R.id.clearBtn);
        textResponse = (TextView) findViewById(R.id.responseLbl);

    }

    public void connectClick(View v) {
        currentIP = editTextAddress.getText().toString();
        if (!currentIP.isEmpty()) {
            currentPort = Integer.parseInt(editTextPort.getText().toString());
            tcpHandler(currentIP, currentPort, "authqwerty", new Client.MyCallbackInterface() {
                @Override
                public void tcpHandler(String response) {
                        Intent htrackIntent = new Intent(MainActivity.this, HandTrackingActivity.class);
                        startActivity(htrackIntent);
                        Toast.makeText(getApplicationContext(), "You are connected.", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(getApplicationContext(), "Unable to connect...", Toast.LENGTH_SHORT).show();
//                    }
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Fill IP/port data.", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClearClick(View v) {
        editTextAddress.setText("");
        editTextPort.setText("");

    }
}
