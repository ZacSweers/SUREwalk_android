package org.utexas.surewalk.activities;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.crashlytics.android.Crashlytics;
import com.google.analytics.tracking.android.EasyTracker;
import com.parse.ParseAnalytics;

import org.utexas.surewalk.R;
import org.utexas.surewalk.controllers.PreferenceHandler;
import org.utexas.surewalk.fragments.DashboardFragment;

import de.keyboardsurfer.android.widget.crouton.Crouton;


public class MainActivity extends SherlockFragmentActivity {

    private PreferenceHandler mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPrefs = new PreferenceHandler(this);
        ParseAnalytics.trackAppOpened(getIntent());

        // Only enable Crashlytics if opted in and manifest key isn't our placeholder
        if (mPrefs.getCROptIn() && hasCrashlyticsApiKey(this)) {
            Crashlytics.start(this);
        }

        // Set up our views
        setContentView(R.layout.activity_main);
        getSupportActionBar().setLogo(getResources().getDrawable(R.drawable.surewalkbanner));
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Set up our fragments. In this case we just have one: DashboardFragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container,  DashboardFragment.newInstance("Home"));
        }

        // If it's their first time running the app, show a dialog to set up info
        if (!mPrefs.getFirstRun()) {
            mPrefs.setFirstRun();
            firstRunDialog();
        }
    }

    private void firstRunDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        alertDialogBuilder.setMessage("Looks like this is your first time using the app! Would you like to enter your information now? It speeds up the process later when you request a walk!")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // enter manually
                                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }


   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      // Inflate the menu; this adds items to the action bar if it is present.
      getSupportMenuInflater().inflate(R.menu.main, menu);
      return true;
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      // Handle action bar item clicks here. The action bar will
      // automatically handle clicks on the Home/Up button, so long
      // as you specify a parent activity in AndroidManifest.xml.
      int id = item.getItemId();
      if (id == R.id.action_set) {
         startActivity(new Intent(this, SettingsActivity.class));
         return true;
      }
      return super.onOptionsItemSelected(item);
   }

    @Override
    public void onStart() {
        super.onStart();

        // Google Analytics
        if (mPrefs.getAnalyticsOptIn()) {
            EasyTracker.getInstance(this).activityStart(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        // Google Analytics
        if (mPrefs.getAnalyticsOptIn()) {
            EasyTracker.getInstance(this).activityStop(this);
        }
   }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Crouton.cancelAllCroutons();
    }

    /**
     * Crashlytics' API doesn't support string replacement, so we use this to check if the API key is real or our placeholder
     * before enabling it, to prevent Gradle from crashing
     * This code is courtesy of Michael Bonnell from Crashlytics, slightly adapted
     * @return true if the Crashlytics API key's value is declared in AndroidManifest.xml metadata, otherwise return false.
     */
    private boolean hasCrashlyticsApiKey(Context context) {
        boolean hasValidKey = false;
        try {
            Context appContext = context.getApplicationContext();
            ApplicationInfo ai = appContext.getPackageManager().getApplicationInfo(appContext.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            if (bundle != null) {
                Object apiKey = bundle.get("com.crashlytics.ApiKey");
                hasValidKey = apiKey != null && apiKey instanceof String;
            }
        } catch (PackageManager.NameNotFoundException e)  {
            Log.e("CrashlyticsCheck", "Unexpected NameNotFound.", e);
        }

        return hasValidKey;
    }
}