package com.example.charliegerard.morse;

import android.hardware.Camera;
import android.media.Image;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    private int currentDuration;
    List<Integer> durations = new ArrayList<Integer>();

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

        // At the moment, only working with messages made of 1 word.

        for(int item = 0; item < message.length(); item++){
            // Each character in message.
            String character = String.valueOf(message.charAt(item));

            if(morseMap.containsKey(character)){
                // Morse value for each character in message.
                String morseValue = morseMap.get(character);

                for(int index = 0; index < morseValue.length(); index++){
                    // Each character in morse value;
                    String singleCharacterInMorseValue = String.valueOf(morseValue.charAt(index));

                    if(singleCharacterInMorseValue.equals("-")){
                        // dashUnitDuration
//                        currentDuration = dashUnitDuration;

                        durations.add(dashUnitDuration);

                    } else if(singleCharacterInMorseValue.equals(".")){
                        // dotUnitDuration
//                        currentDuration = dotUnitDuration;
                        Log.d("there", "should be .");
                        durations.add(dotUnitDuration);

                    } else if(singleCharacterInMorseValue.equals(" ")){
                        // gapInCharacter
//                        currentDuration = gapInCharacter;

                        durations.add(gapInCharacter);
                    }
                }
            }
        }
        startTranslation(durations);
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {

            handler.postDelayed(mStatusChecker, currentDuration);
        }
    };

    public void startTranslating(){
        mStatusChecker.run();
    }

    public void stopRepeatingTask(){
        handler.removeCallbacks(mStatusChecker);
    }

    private void startTranslation(List durations){
//        for(int index = 0; index < durations.size(); index++ ){
//            Log.d("durations", String.valueOf(durations.get(index)));
//        }

        new Thread(morse).start();
    }

    private Runnable morse = new Runnable(){

        @Override
        public void run() {
            try{
                cameraImpl.toggleFlashlight(true);
                Thread.sleep(5000);
                cameraImpl.toggleFlashlight(false);
                Thread.sleep(1000);
            } catch(InterruptedException e){
                Log.e("Err", "Interruption exception " + e.getMessage());
            }
        }
    };

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
