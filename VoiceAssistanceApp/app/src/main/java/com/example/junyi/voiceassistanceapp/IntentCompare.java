package com.example.junyi.voiceassistanceapp;

import android.content.Context;
import android.content.res.Resources;

import java.util.ArrayList;
import java.util.Arrays;

import static com.example.junyi.voiceassistanceapp.R.id.intentResult;

public class IntentCompare extends MainActivity {

    static String receivedresult;

    static ArrayList<String> A = new ArrayList<>(Arrays.asList(
            "Top Gainers",
            "Top Losers",
            "Market Index",
            "Securities",
            "Market"));

    static ArrayList<String> B = new ArrayList<>(Arrays.asList(
            "Watch List",
            "Watchlist",
            "Status",
            "Transactions",
            "Notifications",
            "Profile",
            "Testing"));

    protected String compareAndRunIntent(Context context, String result) {
        if (A.contains(result)) {
            receivedresult = context.getString(R.string.intentA);
            //intentResult.setText(getString(R.string.intentA));
        }
        else if (B.contains(result)){
            receivedresult = context.getString(R.string.intentB);
            //intentResult.setText(getString(R.string.intentB));
        }
        else {
            receivedresult = context.getString(R.string.noMatch);
            //intentResult.setText(getString(R.string.noMatch));
        }
        return receivedresult;
    }

}
