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

    MicrophoneRecognitionClient micClient = null;
    SpeechRecognitionMode speechMode = SpeechRecognitionMode.ShortPhrase;
    final private String locale = "en-us";
    String query;
    String topIntent;
    String topEntity;
    TextView transcriptResult;
    TextView intentResult;
    Button startButton;
    Button startButtonwithLUIS;
    boolean isLUIS = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final MainActivity This = this;

        this.transcriptResult = (TextView) findViewById(R.id.transciptResult);
        this.startButton = (Button) findViewById(R.id.startButton);
        this.startButtonwithLUIS = (Button) findViewById(R.id.startButtonwithLUIS);
        this.intentResult = (TextView) findViewById(R.id.intentResult);

        this.startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                This.startButton_Click(arg0);

            }
        });

        this.startButtonwithLUIS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                This.startButtonwithLUIS_Click(arg0);
            }
        });
    }

    // Handles click event of startButtonwithLUIS control
    private void startButtonwithLUIS_Click(View arg0) {

        this.LogRecognitionStart();

        this.micClient = SpeechRecognitionServiceFactory.createMicrophoneClientWithIntent(
                this,
                locale, this, getPrimaryKey(),
                this.getLuisAppID(),
                this.getLuisSubscriptionID()
        );
        isLUIS = true;
        this.micClient.startMicAndRecognition();

    }

    // Handles click event of startButton control
    private void startButton_Click(View arg0) {

        this.LogRecognitionStart();

        // Microphone client is created and started
        this.micClient = SpeechRecognitionServiceFactory.createMicrophoneClient(this, speechMode, locale, this, this.getPrimaryKey());
        isLUIS = false;
        this.micClient.startMicAndRecognition();

    }


    // Obtain Primary Subscription Key for Bing Speech API
    public String getPrimaryKey() {
        return this.getString(R.string.primaryKey);
    }

    // Obtain LUIS AppID
    public String getLuisAppID() { return this.getString(R.string.LuisAppID); }

    // Obtain LUIS SubscriptionID
    public String getLuisSubscriptionID() { return this.getString(R.string.LuisSubscriptionID); }

    // Obtain LUIS Endpoint Key
    public String getLuisEndpointKey() { return this.getString(R.string.LuisAPI); }

    private void LogRecognitionStart() {
        Log.d("RecStart", "Start speech recognition");
    }


    // Interface methods
    public void onPartialResponseReceived(final String response) {

    }

    public void onIntentReceived(final String payload) {
        Log.d("intent", payload);

    }

    public void onError(final int errorCode, final String response) {
        Log.e("onError()", "ErrorCode: "+Integer.toString(errorCode) + response );

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

                // Top few results transcribed from speech
                for (int i = 0; i < response.Results.length; i++) {
                    Log.d("phrase" + Integer.toString(i), response.Results[i].DisplayText);
                }

                // Display the highest confidence result transcribed from speech
                this.transcriptResult.setText(response.Results[0].DisplayText);

                // If 'Start With LUIS' button is clicked
                if(isLUIS) {

                    // LUIS will return a JSON from this query URL
                    query = this.getLuisEndpointKey() + response.Results[0].DisplayText.replaceAll("\\p{P}", "");

                    new AsyncTask<String, Void, JSONObject>() {
                        @Override
                        protected JSONObject doInBackground(String... params) {
                            return JSONParser.getJSONFromUrl(params[0]);
                        }

                        @Override
                        protected void onPostExecute(JSONObject result) {
                            try {
                                JSONArray values = result.getJSONArray("intents");
                                topIntent = values.getJSONObject(0).getString("intent");
                                JSONArray entities = result.getJSONArray("entities");
                                topEntity = entities.getJSONObject(0).getString("entity");
                                Log.d("result", topIntent);
                            } catch (JSONException je) {
                                Log.e("FRR JSON", "JSON Exception: " + je);
                            } catch (Exception e) {
                                Log.e("FRR", "Exception: "+e);
                            }
                            intentResult.setText(executeIntent(topIntent, topEntity));
                        }
                    }.execute(query);
                }

                else {
                    IntentCompare intentcompare = new IntentCompare();
                    String receivedResult = intentcompare.compareAndRunIntent(this, response.Results[0].DisplayText.replaceAll("\\p{P}", ""));
                    intentResult.setText(receivedResult);
                }

            } catch (Exception e) {
                Log.e("Invalid Response", e.toString());
                transcriptResult.setText(R.string.invalidResponseFeedback);
            }
    }
    // End of Interface Methods


    // Execute next step based on intent name which matches translated text best
    public String executeIntent(String topIntent, String topEntity) {

        String intendedText;

        switch (topIntent) {
            case "Action": intendedText = topEntity + " " +topIntent+" has been called";
                break;
            case "Navigate": intendedText = topIntent+": Proceed to " + topEntity;
                break;
            default: intendedText = "Build in LUIS";
                break;
        }
        return intendedText;
    }
}