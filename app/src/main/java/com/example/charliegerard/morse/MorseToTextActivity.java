package com.example.charliegerard.morse;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.constraint.solver.widgets.Rectangle;
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
    int x = -1;
    int y = -1;
    double [] rgb;
    int counter = 0;
    int previousCounter = -1;
    boolean previous = false;
    boolean isAtCenter = false;


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
                if(x != -1 && y != -1){
                    rgb = mRgba.get(x,y); //get RGB values for the touch event.
                }

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

                // x, y, width, height;
                Rect centerRect = new Rect(rect.width/4 * 1, rect.height/4 * 1, rect.width/4 * 2, rect.height/4 * 2);

                // Rectangle at the center of the camera preview.
                Imgproc.rectangle(mRgba, new Point(rect.width/4 * 1, rect.height/4 * 1), new Point(rect.width/4 * 3, rect.height/4 * 3), new Scalar(255,255,255), 5);

                // Contour of the largest blob.
                Imgproc.drawContours(mRgba, contours, maxValId, new Scalar(255,0,0,255), 5);

                if(maxValId != 0){
                    // Bounding rectangle of the largest blob.
                    MatOfPoint2f approxCurve = new MatOfPoint2f();
                    MatOfPoint2f contour2f = new MatOfPoint2f(contours.get(maxValId).toArray());
                    double approxDistance = Imgproc.arcLength(contour2f, true) * 0.02;
                    Imgproc.approxPolyDP(contour2f,approxCurve, approxDistance, true);
                    MatOfPoint points = new MatOfPoint(approxCurve.toArray());
                    Rect lightBoundary = Imgproc.boundingRect(points);
                    Imgproc.rectangle(mRgba, new Point(lightBoundary.x, lightBoundary.y), new Point(lightBoundary.x + lightBoundary.width, lightBoundary.y + lightBoundary.height), new Scalar(255,0,0,255), 3);

                    checkIfBlobAtCenter(centerRect, lightBoundary);
                }

                //Original camera feed is rotated to appear correctly
                //So drawing a circle can't only be x,y.
//                Imgproc.circle(mRgba, new Point(y, rect.width - x), 50, new Scalar(255,255,255), 5);

                //Drawing line instead of circle to try and get pixel brightness.
                int horiLineX1 = y;
                int horiLineY1 = 0;
                int horiLineX2 = y;
                int horiLineY2 = rect.height;

                int vertLineX1 = 0;
                int vertLineY1 = rect.width - x;
                int vertLineX2 = rect.width;
                int vertLineY2 = rect.width - x;
                //Horizontal line.
                Imgproc.line(mRgba, new Point(horiLineX1,horiLineY1), new Point(horiLineX2, horiLineY2), new Scalar(255,0,0), 3);
                //Vertical line
                Imgproc.line(mRgba, new Point(vertLineX1, vertLineY1), new Point(vertLineX2, vertLineY2), new Scalar(255,0,0), 3);

//                Point intersection = calculateIntersectionPoint(horiLineX1, horiLineY1, horiLineX2, horiLineY2, vertLineX1, vertLineY1, vertLineX2, vertLineY2);

                return mRgba;
            }
        };
        cameraPreview.setCvCameraViewListener(camListener);
    }

    public Point calculateIntersectionPoint(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4) {
        // equation found here: https://stackoverflow.com/questions/30072854/android-opencv-retrieve-intersection-point
        int d = (x1-x2)*(y3-y4) - (y1-y2)*(x3-x4);
        if (d == 0) return null;

        int xi = ((x3-x4)*(x1*y2-y1*x2)-(x1-x2)*(x3*y4-y3*x4))/d;
        int yi = ((y3-y4)*(x1*y2-y1*x2)-(y1-y2)*(x3*y4-y3*x4))/d;

        return new Point(xi, yi);
    };

    private boolean checkIfBlobAtCenter(Rect centerRect, Rect blobBoundary) {
        previous = isAtCenter;

        if(blobBoundary.x > centerRect.x &&
                blobBoundary.x + blobBoundary.width < centerRect.x + centerRect.width &&
                blobBoundary.y > centerRect.y && blobBoundary.y + blobBoundary.height < centerRect.y + centerRect.height){

            isAtCenter = true;

//            Log.d("center", String.valueOf(blobBoundary.area()));
        } else {
            isAtCenter = false;
        }

        if(previous != isAtCenter && isAtCenter == true){
            counter += 1;
            Log.d("counter: ", String.valueOf(counter));
        }

        return isAtCenter;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub

        if(event.getAction() == MotionEvent.ACTION_DOWN) {

            x = (int) event.getX();
            y = (int) event.getY();

        }
//        if(rgb != null){

//            Log.d("Color: Red", String.valueOf(rgb[0]) + " Green: " + String.valueOf(rgb[1]) + " Blue: " + String.valueOf(rgb[2]));
//        }

        return super.onTouchEvent(event);
    }
}

