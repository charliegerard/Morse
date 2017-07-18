package com.example.charliegerard.morse;

import android.content.Intent;
import android.hardware.Camera;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.textToMorsebutton) Button textToMorseButton;
    @BindView(R.id.morseToTextbutton) Button morseToTextButton;

    public static boolean isFlashlightOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick (R.id.textToMorsebutton)
    public void textToMorseView(View view){
        Intent intent = new Intent(this, TextToMorseActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.morseToTextbutton)
    public void morseToTextView(View view){
        Intent intent = new Intent(this, MorseToTextActivity.class);
        startActivity(intent);
    }
}
