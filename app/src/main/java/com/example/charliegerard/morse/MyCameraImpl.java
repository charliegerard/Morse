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

    private static boolean isFlashlightOn;

    public MyCameraImpl(Context ctx){
        context = ctx;

        handleCameraSetup();
        checkFlashlight();
    }

    private void checkFlashlight() {
        if(isFlashlightOn){
            enableFlashlight();
        } else {
            disableFlashlight();
        }
    }

    private void disableFlashlight() {
        isFlashlightOn = false;
        toggleFlashlight(false);
    }

    public void enableFlashlight() {
        isFlashlightOn = true;
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
