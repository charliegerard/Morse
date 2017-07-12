package com.example.charliegerard.morse;

import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import org.w3c.dom.Text;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.switchBtn) Button toggleBtnOff;
    @BindView(R.id.translateBtn) Button translateButton;
    @BindView(R.id.inputText) EditText inputField;

    HashMap<String, String> morseMap = new HashMap<String, String>();

    private MyCameraImpl cameraImpl;
    public static boolean isFlashlightOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setupMorseMap();
    }

    private void setupMorseMap(){
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

    @OnClick(R.id.translateBtn)
    public void translate(){
        String message = inputField.getText().toString();

        for(int item = 0; item < message.length(); item++){
            String character = String.valueOf(message.charAt(item));

            if(morseMap.containsKey(character)){
                Log.d("text", morseMap.get(character));
            }
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
