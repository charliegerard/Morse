package com.example.charliegerard.morse;

import android.content.Context;
import android.hardware.Camera;

/**
 * Created by charliegerard on 10/07/2017.
 */

class MyCameraImpl {
    private static Camera camera;
    private static Camera.Parameters parameters;
    private MarshmallowCamera marshmallowCamera;
    private Context context;

    public MyCameraImpl(Context ctx){
        context = ctx;

        handleCameraSetup();
        checkFlashlight();
    }

    private void checkFlashlight() {
        if(MainActivity.isFlashlightOn){
            enableFlashlight();
        } else {
            disableFlashlight();
        }
    }

    private void disableFlashlight() {
        toggleFlashlight(false);
    }

    public void enableFlashlight() {
        toggleFlashlight(true);
    }

    public void toggleFlashlight(boolean toggleFlag) {
        marshmallowCamera.toggleFlashlight(toggleFlag);
    }

    private void handleCameraSetup() {
        setupMarshmallowCamera();
        checkFlashlight();
    }

    private void setupMarshmallowCamera() {
        if(marshmallowCamera == null){
            marshmallowCamera = new MarshmallowCamera(context);
        }
    }

}
