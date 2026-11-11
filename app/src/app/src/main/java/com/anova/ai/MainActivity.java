package com.anova.ai;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.RecognitionListener;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends Activity {

    TextView chat;
    EditText input;
    TextToSpeech tts;
    SpeechRecognizer speech;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        LinearLayout main = new LinearLayout(this);
        main.setOrientation(1);
        main.setPadding(20, 30, 20, 20);
        main.setBackgroundColor(Color.rgb(12, 18, 35));

        TextView title = new TextView(this);
        title.setText("ANOVA AI");
        title.setTextSize(30);
        title.setTextColor(Color.CYAN);
        title.setGravity(Gravity.CENTER);
        main.addView(title);

        chat = new TextView(this);
        chat.setText("السلام علیکم! میں ANOVA AI ہوں۔\nاپنا پیغام لکھیں یا مائیک دبائیں۔");
        chat.setTextColor(Color.WHITE);
        chat.setTextSize(18);
        chat.setPadding(10, 30, 10, 30);
        main.addView(chat);

        input = new EditText(this);
        input.setHint("اپنا سوال لکھیں...");
        input.setTextColor(Color.WHITE);
        input.setHintTextColor(Color.LTGRAY);
        main.addView(input);

        Button send = new Button(this);
        send.setText("Send");
        main.addView(send);

        Button mic = new Button(this);
        mic.setText("🎤 بول کر پوچھیں");
        main.addView(mic);

        setContentView(main);

        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(new Locale("ur", "PK"));
            }
        });

        send.setOnClickListener(v -> {
            String message = input.getText().toString().trim();
            if (!message.isEmpty()) {
                chat.setText("آپ: " + message +
                    "\n\nANOVA AI: ابھی AI سرور سے کنکشن تیار کیا جا رہا ہے۔");
                input.setText("");
            }
        });

        mic.setOnClickListener(v -> startVoice());
    }

    private void startVoice() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            Toast.makeText(this, "Voice recognition دستیاب نہیں",
                Toast.LENGTH_LONG).show();
            return;
        }

        speech = SpeechRecognizer.createSpeechRecognizer(this);

        speech.setRecognitionListener(new RecognitionListener() {
            public void onReadyForSpeech(Bundle b) {}
            public void onBeginningOfSpeech() {}
            public void onRmsChanged(float r) {}
            public void onBufferReceived(byte[] b) {}
            public void onEndOfSpeech() {}
            public void onError(int e) {
                Toast.makeText(MainActivity.this,
                    "آواز سمجھنے میں مسئلہ ہوا", Toast.LENGTH_SHORT).show();
            }
            public void onResults(Bundle b) {
                ArrayList<String> results =
                    b.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (results != null && !results.isEmpty()) {
                    input.setText(results.get(0));
                }
            }
            public void onPartialResults(Bundle b) {}
            public void onEvent(int a, Bundle b) {}
        });

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ur-PK");
        speech.startListening(intent);
    }

    @Override
    protected void onDestroy() {
        if (speech != null) speech.destroy();
        if (tts != null) tts.shutdown();
        super.onDestroy();
    }
}
