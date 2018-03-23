## Microsoft Speech API Voice Recognition Demo App with LUIS

### The Sample

This sample demonstrates the following features:

* Speech-to-text conversion recognition using external microphone input
* Using converted text to compare to a string and run an intent
* Using LUIS’ structure of intent and entity to run a desired intent

This only works for speech with audio length less than 15 seconds.
This demonstration application also has no wake words and microphone has to be activated by clicking on any of the two buttons.
Client library has been added to this project.


#### Required keys:

* Speech API subscription key can be obtained from https://azure.microsoft.com/en-us/try/cognitive-services
* If LUIS is being used, sign up for LUIS at https://azure.microsoft.com/en-us/services/cognitive-services/language-understanding-intelligent-service to obtain Application ID, Subscription ID, and the endpoint URL.


#### LUIS Configuration:
LUIS is currently configured to have two intents: Navigate and Action, and two entities: Pages and Name.


#### Running the sample:

‘Start with LUIS’ will run the demonstration app with LUIS. When the button is clicked, if what is spoken matches the utterance configured in LUIS, it will pull out the intent and the entity as the result and display it in the TextView at the bottom of the screen (to mimic an intent).
Example of current utterances are: ‘View watch list’, ‘buy stocks’, ‘change password’, ‘see top gainers’.

‘Start’ will run the demonstration app without LUIS. When the button is clicked, if what is spoken matches the word being grouped in the ArrayList in IntentCompare.java, the intent with the name of the ArrayList (A or B) will be displayed (to mimic an intent).



### The Client Library

The Speech To Text client library is a client library for Microsoft Speech, Speech-to-text API.

The easiest way to consume the client library is to add the com.microsoft.projectoxford:speechrecognition package from Maven Central Repository. To find the latest version of client library, go to http://search.maven.org, and search for “g:com.microsoft.projectoxford”.

To add the client library dependency from build.gradle file, add the following line in dependencies.
```
dependencies {

	// Use the following line to include client library from Maven Central Repository
	// Change the version number from the search.maven.org result

	compile ‘com.microsoft.projectoxford:speechrecognition:1.2.2’

}
```


To add the client library dependency from Android Studio:

1. From Menu, Choose File > Project Structure.
2. Click on your app module.
3. Click on Dependencies tab.
4. Click “+” sign to add new dependency
5. Pick Library dependency from the drop-down list.
6. Type com.microsoft.projectoxford and hit the search icon from Choose Library Dependency dialog.
7. Pick the client library that you intend to use.
8. Click OK to add the new dependency
9. Download the appropriate JNI library libandroid_platform.so from https://github.com/Azure-Samples/Cognitive-Speech-STT-Android/tree/master/SpeechSDK/libs, and put it in your project’s directory app/src/main/jniLibs/armeabi/ or app/src/main/jniLibs/x86/.
