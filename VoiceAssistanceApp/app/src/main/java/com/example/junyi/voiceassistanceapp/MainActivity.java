package com.example.junyi.voiceassistanceapp;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.microsoft.cognitiveservices.speechrecognition.ISpeechRecognitionServerEvents;
import com.microsoft.cognitiveservices.speechrecognition.MicrophoneRecognitionClient;
import com.microsoft.cognitiveservices.speechrecognition.RecognitionResult;
import com.microsoft.cognitiveservices.speechrecognition.SpeechRecognitionMode;
import com.microsoft.cognitiveservices.speechrecognition.SpeechRecognitionServiceFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity implements ISpeechRecognitionServerEvents {

    final String url = "https://westus.api.cognitive.microsoft.com/luis/v2.0/apps/6b721f64-39c5-43b7-8c4c-51482ca30470?subscription-key=39ab21617ede43b3a488716683aa7476&verbose=true&timezoneOffset=480&q=";

    MicrophoneRecognitionClient micClient = null;
    SpeechRecognitionMode speechMode = SpeechRecognitionMode.ShortPhrase;
    private String locale = "en-us";
    String query;
    TextView transcriptResult;
    TextView intentResult;
    Button startButton;
    Button startButtonwithIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final MainActivity This = this;

        this.transcriptResult = (TextView) findViewById(R.id.transciptResult);
        this.startButton = (Button) findViewById(R.id.startButton);
        this.startButtonwithIntent = (Button) findViewById(R.id.startButtonwithIntent);
        this.intentResult = (TextView) findViewById(R.id.intentResult);

        this.startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                This.startButton_Click(arg0);

            }
        });

        this.startButtonwithIntent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                This.startButtonwithIntent_Click(arg0);
            }
        });
    }

    // Handles click event of startButtonwithIntent control
    private void startButtonwithIntent_Click(View arg0) {

        this.LogRecognitionStart();

        this.micClient = SpeechRecognitionServiceFactory.createMicrophoneClientWithIntent(
                this,
                locale, this, getPrimaryKey(),
                this.getLuisAppID(),
                this.getLuisSubscriptionID()
        );
        this.micClient.startMicAndRecognition();

    }

    // Handles click event of startButton control
    private void startButton_Click(View arg0) {

        this.LogRecognitionStart();

        // Microphone client is created and started
        this.micClient = SpeechRecognitionServiceFactory.createMicrophoneClient(this, speechMode, locale, this, this.getPrimaryKey());
        this.micClient.startMicAndRecognition();

    }


    // Obtain Primary Subscription Key for Bing Speech API
    public String getPrimaryKey() {
        return this.getString(R.string.primaryKey);
    }

    // Obtain LUIS AppID
    public String getLuisAppID() {
        return this.getString(R.string.LuisAppID);
    }

    // Obtain LUIS SubscriptionID
    public String getLuisSubscriptionID() {
        return this.getString(R.string.LuisSubscriptionID);
    }

    private void LogRecognitionStart() {
        Log.d("Recstart", "Start speech recognition");
    }


    // Interface methods
    public void onPartialResponseReceived(final String response) {

    }

    public void onIntentReceived(final String payload) {
        Log.d("intent", payload);

    }

    public void onError(final int errorCode, final String response) {

    }

    public void onAudioEvent(boolean recording) {
        Log.d("onAudioEvent()", "--Microphone status change received by onAudioEvent()--");
        if (!recording) {
            this.micClient.endMicAndRecognition();
        }
    }

    public void onFinalResponseReceived(final RecognitionResult response) {
        Log.d("finish", "final response received");
        try {
            for (int i = 0; i < response.Results.length; i++) {
                Log.d("phrase" + Integer.toString(i), response.Results[i].DisplayText);
            }
            this.transcriptResult.setText(response.Results[0].DisplayText);

            query = url + response.Results[0].DisplayText.replaceAll("\\p{P}", "");

            new AsyncTask<String, Void, JSONObject>() {
                @Override
                protected JSONObject doInBackground(String... params) {
                    return JSONParser.getJSONFromUrl(params[0]);
                }

                @Override
                protected void onPostExecute(JSONObject result) {
                    try {
                        JSONArray values = result.getJSONArray("intents");
                        String topIntent = values.getJSONObject(0).getString("intent");
                        Log.d("result", topIntent);
                    } catch (JSONException je) {
                        Log.e("FRR", "JSON Exception: " + je);
                    }
                }
            }.execute(query);

            IntentCompare intentcompare = new IntentCompare();
            String receivedResult = intentcompare.compareAndRunIntent(this, response.Results[0].DisplayText.replaceAll("\\p{P}", ""));
            intentResult.setText(receivedResult);


        } catch (Exception e) {
            Log.e("Invalid Response", e.toString());
            transcriptResult.setText(R.string.invalidResponseFeedback);
        }


    }
    // End of Interface Methods
}
