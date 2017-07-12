package com.example.charliegerard.morse;

import android.media.Image;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.switchBtn) Button toggleBtnOff;
    @BindView(R.id.translateBtn) Button translateButton;
    @BindView(R.id.inputText) EditText inputField;

    HashMap<String, String> morseMap = new HashMap<String, String>();

    private Handler handler;
    private int interval = 1000;

    private int oneTimeUnit = 500;
    private int dotUnitDuration = oneTimeUnit;
    private int dashUnitDuration = oneTimeUnit * 3;
    private int gapInCharacter = oneTimeUnit;
    private int gapBetweenLetters = oneTimeUnit * 3;
    private int getGapBetweenWords = oneTimeUnit * 7;

    private MyCameraImpl cameraImpl;
    public static boolean isFlashlightOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        handler = new Handler();

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
        Log.d("here?", "boo");
        for(int item = 0; item < message.length(); item++){
            String character = String.valueOf(message.charAt(item));

            if(morseMap.containsKey(character)){
                Log.d("text", morseMap.get(character));
                String morseValue = morseMap.get(character);
                for(int index = 0; index < morseValue.length(); index++){
//                    Log.d("value", morseValue.valueOf(index));
                }
//                startTranslating();
            }
        }
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            Log.d("test", "testing loop");
            handler.postDelayed(mStatusChecker, interval);
        }
    };

    public void startTranslating(){
        mStatusChecker.run();
    }

    public void stopRepeatingTask(){
        handler.removeCallbacks(mStatusChecker);
    }

    @Override
    protected void onStart(){
        super.onStart();
        if(cameraImpl == null){
            setupCameraImpl();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        stopRepeatingTask();
    }

}
