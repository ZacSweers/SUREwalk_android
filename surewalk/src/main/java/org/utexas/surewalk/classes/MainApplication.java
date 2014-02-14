package org.utexas.surewalk.classes;

import android.app.Application;

import com.parse.ParseInstallation;
import com.parse.PushService;

import org.utexas.surewalk.activities.MainActivity;
import org.utexas.surewalk.controllers.ParseHandler;


public class MainApplication extends Application {

    @Override
    public void onCreate(){
        new ParseHandler(this).initializeParse();
        PushService.setDefaultPushCallback(this, MainActivity.class);
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
}
