package com.example.junyi.voiceassistanceapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.microsoft.cognitiveservices.speechrecognition.DataRecognitionClient;
import com.microsoft.cognitiveservices.speechrecognition.ISpeechRecognitionServerEvents;
import com.microsoft.cognitiveservices.speechrecognition.MicrophoneRecognitionClient;
import com.microsoft.cognitiveservices.speechrecognition.SpeechRecognitionMode;
import com.microsoft.cognitiveservices.speechrecognition.SpeechRecognitionServiceFactory;

public class MainActivity extends Activity implements ISpeechRecognitionServerEvents {

    int m_waitSeconds = 0;
    DataRecognitionClient dataClient = null;
    MicrophoneRecognitionClient micClient = null;
    FinalResponseStatus isReceivedResponse = FinalResponseStatus.NotReceived;
    SpeechRecognitionMode speechMode = SpeechRecognitionMode.ShortPhrase;
    private String locale = "en-us";
    TextView transcriptResult;
    Button startButton;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final MainActivity This = this;

        this.transcriptResult = (TextView) findViewById(R.id.transciptResult);
        this.startButton = (Button) findViewById(R.id.startButton);
        this.startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                This.startButton_Click(arg0);

            }
        });
    }

    private void startButton_Click(View arg0) {
        this.m_waitSeconds = 20;

        this.LogRecognitionStart();

        this.micClient = SpeechRecognitionServiceFactory.createMicrophoneClient(this,speechMode,locale,this,this.getPrimaryKey());
        this.micClient.startMicAndRecognition();

    }

    public enum FinalResponseStatus { NotReceived, OK, Timeout }

    public String getPrimaryKey() {
        return this.getString(R.string.primaryKey);
    }

    private void LogRecognitionStart(){
        Log.d("Recstart","Start speech recognition");
    }

    public void onIntentReceived(final String response){

    }

    public void onPartialResponseReceived(final String response) {

    }

    public void onError(final int errorCode, final String response) {
        Log.e("Recognition error", Integer.toString(errorCode) + " " + response);
    }
}
