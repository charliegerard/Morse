package com.example.charliegerard.morse;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 8675309;
    //    public static final String EXTRA_MESSAGE = "com.example.morse.MESSAGE";

    ImageButton imageButton;
    Camera camera;
    Camera.Parameters parameters;
    boolean isFlash = false;
    boolean isOn = false;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageButton = (ImageButton) findViewById(R.id.imageButton2);

        this.activate();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void activate()
    {

        if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            startFlash();
        } else {
            String[] permissionRequest = {Manifest.permission.CAMERA};
            requestPermissions(permissionRequest, CAMERA_PERMISSION_REQUEST_CODE);
        }


//        if(getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)){
//            camera = Camera.open();
//            parameters = camera.getParameters();
////            camera = null;
////            try{
////                camera = Camera.open();
////                parameters = camera.getParameters();
////            }
////            catch(Exception e){
////                Log.d("here", "camera not working");
////            }
//
//            isFlash = true;
//
//        }

//        imageButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v){
//                if(isFlash){
//                    if(!isOn){
////                        imageButton.setImageResource(R.id.imageButton3);
//                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
//                        camera.setParameters(parameters);
//                        camera.startPreview();
//                        isOn = true;
//                    } else {
////                        imageButton.setImageResource(R.id.imageButton2);
//                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
//                        camera.setParameters(parameters);
//                        camera.stopPreview();
//                        isOn = false;
//                    }
//
//                } else {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                    builder.setTitle("Error");
//                    builder.setMessage("Flash is not available on this device");
//                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener(){
//                        @Override
//                        public void onClick(DialogInterface dialog, int which){
//                            dialog.dismiss();
//                            finish();
//                        }
//                    });
//                    AlertDialog alert = builder.create();
//                    alert.show();
//                }
//            }
//        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == CAMERA_PERMISSION_REQUEST_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startFlash();
            }
        }
    }

    private void startFlash() {
            camera = Camera.open();
            parameters = camera.getParameters();

            isFlash = true;

            imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if(isFlash){
                    if(!isOn){
//                        imageButton.setImageResource(R.id.imageButton3);
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        camera.setParameters(parameters);
                        camera.startPreview();
                        isOn = true;
                    } else {
//                        imageButton.setImageResource(R.id.imageButton2);
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        camera.setParameters(parameters);
                        camera.stopPreview();
                        isOn = false;
                    }

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Error");
                    builder.setMessage("Flash is not available on this device");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which){
                            dialog.dismiss();
                            finish();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });

    }


    @Override
    protected void onStop(){
        super.onStop();

        if(camera != null){
            camera.release();
            camera = null;
        }

    }

//    public void logText(String message){
//        Log.d("there", message);
//        //TO DO:
//        // Translate message to morse code.
//        // Test to turn on flashlight.
//        // Turn on flashlight for 1 word.
//    }
}
