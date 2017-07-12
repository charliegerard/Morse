package com.example.charliegerard.morse;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.util.Log;

/**
 * Created by charliegerard on 10/07/2017.
 */

class MarshmallowCamera {
    private CameraManager manager;
    private String cameraId;
    private Context context;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    MarshmallowCamera(Context ctx){
        context = ctx;
        manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);

        try{
            final String[] list = manager.getCameraIdList();
            cameraId = list[0];
        } catch(CameraAccessException ignored){
            Log.e("ERR", "access to camera " + ignored.getMessage());
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void toggleFlashlight(boolean toggleFlag) {
        try{
            manager.setTorchMode(cameraId, toggleFlag);
        } catch(CameraAccessException e){
            Log.e("ERR", "toggle flashlight " + e.getMessage());
        }
    }
}
