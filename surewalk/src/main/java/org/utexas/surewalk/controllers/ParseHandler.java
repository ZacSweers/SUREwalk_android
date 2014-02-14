package org.utexas.surewalk.controllers;

import android.content.Context;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import org.utexas.surewalk.R;
import org.utexas.surewalk.data.WalkRequest;


public class ParseHandler {
    //surewalk Parse account keys
    private String APP_KEY;
    private String CLIENT_KEY;

    private static final Class[] PARSE_OBJECT_CLASSES = {WalkRequest.class};

    private Context context;
    private HandlerSaveCallback callback;

    private boolean isParseInit = false;
    private boolean isSaved = false;


    public ParseHandler(Context c) {
        if (c == null)
                throw new IllegalArgumentException("c" +
                        "may not be null.");

        context = c;
        APP_KEY = c.getString(R.string.parse_app_key);
        CLIENT_KEY = c.getString(R.string.parse_client_key);
        
        callback = new HandlerSaveCallback();
    }


    public void initializeParse() {
        isParseInit = true;
        for (Class subclass : PARSE_OBJECT_CLASSES)
                ParseObject.registerSubclass(subclass);
        Parse.initialize(context, APP_KEY, CLIENT_KEY);
    }


    public boolean saveWalkInfo(WalkRequest data) {
        if (!isParseInit)
            throw new IllegalStateException("Must initialize parse first.");
        if (data == null)
            throw new IllegalArgumentException("Data may not be null.");
        return saveInBackground(data);
    }


    private boolean saveInBackground(ParseObject data) {
        if (data == null)
            throw new IllegalArgumentException("Data may not be null.");
        isSaved = false;
        data.saveInBackground(callback);
        return isSaved;
    }


    private class HandlerSaveCallback extends SaveCallback {

        @Override
        public void done(ParseException e) {
            if (e == null) {
                ParseHandler.this.isSaved = true;

                Toast.makeText(ParseHandler.this.context, "Request sent! You'll get a response soon!", Toast.LENGTH_SHORT).show();
            } else {
                ParseHandler.this.isSaved = false;

                Toast.makeText(ParseHandler.this.context, "Unable to send request.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
