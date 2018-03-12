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
import com.microsoft.cognitiveservices.speechrecognition.RecognitionResult;
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
            public void onClick(View arg0) {
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

    // Interface methods
    public void onPartialResponseReceived(final String response) {

    }

    public void onIntentReceived(final String payload) {

    }

    public void onError(final int errorCode, final String response) {

    }

    public void onAudioEvent(boolean recording){
        System.out.print("--Microphone status change received by onAudioEvent()--");
        if (!recording){
            this.micClient.endMicAndRecognition();
        }
    }

    public void onFinalResponseReceived(final RecognitionResult response){
        Log.d("finish","final response received");
        for (int i = 0; i < response.Results.length;i++){
            Log.d("phrase"+ Integer.toString(i), response.Results[i].DisplayText);
        }
        this.transcriptResult.setText(response.Results[0].DisplayText);

    }
    // End of Interface Methods

}
