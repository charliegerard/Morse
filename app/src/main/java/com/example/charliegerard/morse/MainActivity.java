package com.example.charliegerard.morse;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {
//    public static final String EXTRA_MESSAGE = "com.example.morse.MESSAGE";

    ImageButton imageButton;
    Camera camera;
    Camera.Parameters parameters;
    boolean isFlash = false;
    boolean isOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageButton = (ImageButton) findViewById(R.id.imageButton2);

        this.activate();
    }

    public void activate()
    {
        if(getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)){
            camera = Camera.open();
            parameters = camera.getParameters();
//            camera = null;
//            try{
//                camera = Camera.open();
//                parameters = camera.getParameters();
//            }
//            catch(Exception e){
//                Log.d("here", "camera not working");
//            }

            isFlash = true;

        }

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
//    @Override
//    protected void onStop(){
//        super.onStop();
//
//        if(camera != null){
//            camera.release();
//            camera = null;
//        }
//
//    }

//    public void logText(String message){
//        Log.d("there", message);
//        //TO DO:
//        // Translate message to morse code.
//        // Test to turn on flashlight.
//        // Turn on flashlight for 1 word.
//    }
}
