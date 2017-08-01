package com.example.charliegerard.morse;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.os.Handler;
import android.widget.TextView;

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
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    boolean previous = false;
    boolean isAtCenter = false;

    //Timer variables
    long init = 0;
    long now,time,paused;
    Handler handler;

    // Timer light off variables.
    long initOff = 0;
    long nowOff, timeOff, pausedOff;
    Handler handlerOff;

    private int oneTimeUnit = 240;
    private int dotUnitDuration = oneTimeUnit;
    private int dashUnitDuration = oneTimeUnit * 3;
    private int gapInCharacter = oneTimeUnit;
    private int gapBetweenLetters = oneTimeUnit * 3;
    private int gapBetweenWords = oneTimeUnit * 7;

    String morseMessage = "";
    String character = "";
//    String letter = "";
    String word = "";

    HashMap<String, String> morseMap = new HashMap<String, String>();

    TextView text;

    public void setupMorseMap(){
        morseMap.put("a", ". -");
        morseMap.put("b", "- . . .");
        morseMap.put("c", "- . - .");
        morseMap.put("d", "- . .");
        morseMap.put("e", ".");
        morseMap.put("f", ". . - .");
        morseMap.put("g", "- - .");
        morseMap.put("h", ". . . .");
        morseMap.put("i", ". .");
        morseMap.put("j", ". - - -");
        morseMap.put("k", "- . -");
        morseMap.put("l", ". - . .");
        morseMap.put("m", "- -");
        morseMap.put("n", "- .");
        morseMap.put("o", "- - -");
        morseMap.put("p", ". - - .");
        morseMap.put("q", "- - . -");
        morseMap.put("r", ". - .");
        morseMap.put("s", ". . .");
        morseMap.put("t", "-");
        morseMap.put("u", ". . -");
        morseMap.put("v", ". . . -");
        morseMap.put("w", ". - -");
        morseMap.put("x", "- . . -");
        morseMap.put("y", "- . - -");
        morseMap.put("z", "- - . .");
    }

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

        text = (TextView) findViewById(R.id.text_view);

        cameraPreview = (CameraBridgeViewBase) findViewById(R.id.sample_test_camera_view);

        cameraPreview.setCvCameraViewListener(this);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setupMorseMap();

        handler = new Handler();
        handlerOff = new Handler();
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

                return mRgba;
            }
        };
        cameraPreview.setCvCameraViewListener(camListener);
    }

    private boolean checkIfBlobAtCenter(Rect centerRect, Rect blobBoundary) {
        previous = isAtCenter;

        if(blobBoundary.x > centerRect.x &&
                blobBoundary.x + blobBoundary.width < centerRect.x + centerRect.width &&
                blobBoundary.y > centerRect.y && blobBoundary.y + blobBoundary.height < centerRect.y + centerRect.height){

            isAtCenter = true;

            if(previous != isAtCenter){

                if(init == 0){
                    init = System.currentTimeMillis();
                } else {
                    init += System.currentTimeMillis() - pausedOff;
                }

                handler.post(updater);

                // Pause the counter of NO light.
                pausedOff = System.currentTimeMillis();

                if(timeOff <= (gapInCharacter * 2) && timeOff > 0){
                    Log.d("symbol: ", "gap in character");
                    character += " ";

                } else if(timeOff > (gapInCharacter * 2) && timeOff >= gapBetweenLetters && timeOff < gapBetweenWords){

                    for(Map.Entry map : morseMap.entrySet()) {
                        if (character.equals(map.getValue())) {
                            word += map.getKey();
                        }
                    }
                    // Reset character for new one;
                    character = "";

                } else if(timeOff >= gapBetweenWords){
//                    Log.d("symbol: ", "gap between words");
                    morseMessage += word + " ";
                    word = "";
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        appendText(word);
                    }
                });
            }

        } else { // Light off.

            isAtCenter = false;

            if(previous != isAtCenter){
                if(initOff == 0){
                    initOff = System.currentTimeMillis();
                } else {
                    initOff += System.currentTimeMillis() - paused;
                }
                handlerOff.post(offTimer);

                // Pause the counter of the ON light.
                paused = System.currentTimeMillis();

                // duration of light on before turning off ==> time
                if(time <= dotUnitDuration){
                    character += ".";
                } else if(time > dotUnitDuration * 2 ){
                    character += "-";
                }

            }
        }

        return isAtCenter;
    }

    private void appendText(String word) {
        text.setText(word);
    }

    final Runnable updater = new Runnable() {
        @Override
        public void run() {
            now = System.currentTimeMillis();
            time = now - init;
//            handler.postDelayed(this, 30);
            handler.post(this);
        }
    };

    final Runnable offTimer = new Runnable() {
        @Override
        public void run() {
            nowOff = System.currentTimeMillis();
            timeOff = nowOff - initOff;
            handlerOff.post(this);
        }
    };
}

