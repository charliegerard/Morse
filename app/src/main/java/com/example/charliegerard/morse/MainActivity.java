package com.example.charliegerard.morse;

import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.switchBtn) Button toggleBtnOff;

    private MyCameraImpl cameraImpl;
    public static boolean isFlashlightOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    private void setupCameraImpl(){
        cameraImpl = new MyCameraImpl(this);
        // The following line was triggering the flashlight by default when the app opens.
        // cameraImpl.enableFlashlight();
    }

    @OnClick(R.id.switchBtn)
    public void changeStateFlashlight(){
        isFlashlightOn = !isFlashlightOn;
        cameraImpl.toggleFlashlight(isFlashlightOn);

        if(isFlashlightOn){
            toggleBtnOff.setBackgroundResource(R.drawable.on);
        } else {
            toggleBtnOff.setBackgroundResource(R.drawable.off);
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        if(cameraImpl == null){
            setupCameraImpl();
        }
    }
}
