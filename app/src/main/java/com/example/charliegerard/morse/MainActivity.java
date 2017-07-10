package com.example.charliegerard.morse;

import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.imageButton2) ImageView toggleBtnOff;
    @BindView(R.id.imageButton3) ImageView toggleBtnOn;

    private MyCameraImpl cameraImpl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    private void setupCameraImpl(){
        cameraImpl = new MyCameraImpl(this);
        cameraImpl.enableFlashlight();
    }

    @OnClick(R.id.imageButton2)
    public void toggleFlashlight(){
        cameraImpl.toggleFlashlight(true);
    }

    @Override
    protected void onStart(){
        super.onStart();
        if(cameraImpl == null){
            setupCameraImpl();
        }
    }
}
