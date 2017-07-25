package com.example.charliegerard.morse;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.graphics.BitmapCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import static org.opencv.imgproc.Imgproc.CHAIN_APPROX_SIMPLE;
import static org.opencv.imgproc.Imgproc.RETR_EXTERNAL;
import static org.opencv.imgproc.Imgproc.RETR_LIST;
import static org.opencv.imgproc.Imgproc.boundingRect;
import static org.opencv.imgproc.Imgproc.dilate;

public class MorseToTextActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2, View.OnTouchListener{

    public String TAG = "Camera ";
    protected CameraBridgeViewBase cameraPreview;
    protected Mat mRgba;
    public CameraBridgeViewBase.CvCameraViewListener2 camListener;

    protected BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
//                    mOpenCvCameraView.enableView();
//                    mOpenCvCameraView.setOnTouchListener(ColorRegionDetectionActivity.this);

                    cameraPreview.enableView();
                    run();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_morse_to_text);

        cameraPreview = (CameraBridgeViewBase) findViewById(R.id.sample_test_camera_view);

        cameraPreview.setCvCameraViewListener(this);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if(cameraPreview != null){
            cameraPreview.disableView();
        }
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if(cameraPreview != null){
            cameraPreview.disableView();
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if(!OpenCVLoader.initDebug()){
            Log.d(TAG, "Internal OpenCV library not found.");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Loading it");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }

    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        // TODO Auto-generated method stub
        mRgba =  new Mat(height, width, CvType.CV_8UC4);
    }

    @Override
    public void onCameraViewStopped() {
        // TODO Auto-generated method stub
        mRgba.release();

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        // TODO Auto-generated method stub
        mRgba = inputFrame.rgba();

        return mRgba;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // TODO Auto-generated method stub
        return false;
    }

    private void run(){
        camListener = new CameraBridgeViewBase.CvCameraViewListener2() {
            @Override
            public void onCameraViewStarted(int width, int height) {

            }

            @Override
            public void onCameraViewStopped() {

            }

            @Override
            public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
                mRgba = inputFrame.rgba();
                Mat gray = new Mat();
                Mat bwImg = new Mat();

                List<MatOfPoint> contours = new ArrayList<>();

                Imgproc.cvtColor(mRgba, gray,Imgproc.COLOR_RGB2GRAY);
                Imgproc.GaussianBlur(gray, gray, new Size(5,5), 0);
//                Imgproc.Canny(gray, gray, 80,100);
                Imgproc.threshold(gray, bwImg, 200, 255, Imgproc.THRESH_BINARY);

//                Imgproc.erode(gray, gray, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(10,10)));
//                Imgproc.dilate(gray, gray, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(10,10)));

                Imgproc.findContours(bwImg, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0,0));

                double maxVal = 0;
                int maxValId = 0;

                for(int i=0; i < contours.size(); i++){
                    double contourArea = Imgproc.contourArea(contours.get(i));
                    if(maxVal < contourArea){
                        maxVal = contourArea;
                        maxValId = i;
                    }
                }
                Rect rect = new Rect();
                rect.width = mRgba.width();
                rect.height = mRgba.height();
                // Rectangle at the center of the camera preview.
                Imgproc.rectangle(mRgba, new Point(rect.width/4 * 1, rect.height/4 * 1), new Point(rect.width/4 * 3, rect.height/4 * 3), new Scalar(255,255,255), 5);
                // Contour of the largest blob.
                Imgproc.drawContours(mRgba, contours, maxValId, new Scalar(255,0,0,255), 5);

                // Bounding rectangle of the largest blob.
                MatOfPoint2f approxCurve = new MatOfPoint2f();
                MatOfPoint2f contour2f = new MatOfPoint2f(contours.get(maxValId).toArray());
                double approxDistance = Imgproc.arcLength(contour2f, true) * 0.02;
                Imgproc.approxPolyDP(contour2f,approxCurve, approxDistance, true);
                MatOfPoint points = new MatOfPoint(approxCurve.toArray());
                Rect lightBoundary = Imgproc.boundingRect(points);
                Imgproc.rectangle(mRgba, new Point(lightBoundary.x, lightBoundary.y), new Point(lightBoundary.x + lightBoundary.width, lightBoundary.y + lightBoundary.height), new Scalar(255,0,0,255), 3);

                if(checkIfBlobAtCenter(rect, lightBoundary)){

                };

                return mRgba;
            }
        };
        cameraPreview.setCvCameraViewListener(camListener);
    }

    private boolean checkIfBlobAtCenter(Rect centerRect, Rect blobBoundary) {
        boolean isAtCenter = false;
        if(blobBoundary.x > centerRect.x &&
                blobBoundary.x + blobBoundary.width < centerRect.x + centerRect.width &&
                blobBoundary.y > centerRect.y && blobBoundary.y + blobBoundary.height < centerRect.y + centerRect.height){

            isAtCenter = true;
        }
        return isAtCenter;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        int x = (int)event.getX();
        int y = (int)event.getY();
        Log.d("touch", String.valueOf(x));

        return false;
    }
}

