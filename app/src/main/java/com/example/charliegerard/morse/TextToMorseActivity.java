package com.example.charliegerard.morse;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TextToMorseActivity extends AppCompatActivity {

    @BindView(R.id.translateBtn) Button translateButton;
    @BindView(R.id.inputText) EditText inputField;
    @BindView(R.id.switchBtn) Button toggleBtnOff;

    private int oneTimeUnit = 240;
    private int dotUnitDuration = oneTimeUnit;
    private int dashUnitDuration = oneTimeUnit * 3;
    private int gapInCharacter = oneTimeUnit;
    private int gapBetweenLetters = oneTimeUnit * 3;
    private int getGapBetweenWords = oneTimeUnit * 7;

    HashMap<String, String> morseMap = new HashMap<String, String>();

    private MyCameraImpl cameraImpl;

    Thread newThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_to_morse);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ButterKnife.bind(this);

        setupMorseMap();
    }

    private void setupCameraImpl(){
        cameraImpl = new MyCameraImpl(this);
    }

    public void setupMorseMap(){
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


    @OnClick(R.id.translateBtn)
    public void translate(){
        changeStateIcon(true);
        newThread = new Thread(morse);
        newThread.start();
    }

    private void changeStateIcon(boolean stateBtn){
        if(stateBtn == true){
            toggleBtnOff.setBackgroundResource(R.drawable.on);
        } else {
            toggleBtnOff.setBackgroundResource(R.drawable.off);
        }
    }


    Runnable morse = new Runnable(){

        private void mapMessageToMorse(){

            String message = inputField.getText().toString();
            String[] words = message.split("\\s+");

            // for each word in the message.
            for(String word: words){
                // for each character in the word.
                for(int index = 0; index < word.length(); index++){
                    String character = String.valueOf(word.charAt(index));
                    String characterLowercase = character.toLowerCase();
                    if(morseMap.containsKey(characterLowercase)){
                        String morseValue = morseMap.get(characterLowercase);

                        for(int elementIndex = 0; elementIndex < morseValue.length(); elementIndex++){
                            String element = String.valueOf(morseValue.charAt(elementIndex));

                            if(element.equals(".")){
                                executeMorseLights(dotUnitDuration, true);
                            } else if(element.equals("-")){
                                executeMorseLights(dashUnitDuration, true);
                            } else if(element.equals(" ")){
                                executeMorseLights(gapInCharacter, false);
                            }
                        }
                        // Pause the light between letters in a word.
                        executeMorseLights(gapBetweenLetters, false);
                    }
                }
                // Pause the light between words.
                executeMorseLights(getGapBetweenWords, false);
            }

        }

        private void executeMorseLights(int duration, boolean state){
            try{
                cameraImpl.toggleFlashlight(state);
                Thread.sleep(duration);
            } catch(InterruptedException e){
                Log.e("Err", "Interruption exception " + e.getMessage());
            }
        }

        @Override
        public void run() {
            mapMessageToMorse();
            //Run following action on UI thread rather than background thread because it is changing an icon.
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    changeStateIcon(false);
                }
            });
        }
    };

    @Override
    protected void onStart(){
        super.onStart();
        if(cameraImpl == null){
            setupCameraImpl();
        }
    }
}
