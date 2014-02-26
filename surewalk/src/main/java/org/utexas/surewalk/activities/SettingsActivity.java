package org.utexas.surewalk.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.telephony.PhoneNumberFormattingTextWatcher;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;

import org.utexas.surewalk.R;
import org.utexas.surewalk.controllers.PreferenceHandler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SettingsActivity extends SherlockPreferenceActivity implements Preference.OnPreferenceChangeListener {

    private Pattern mNamePattern;
    private Pattern mEidPattern;
    private Pattern mPhonePattern;
    private Pattern mEmailPattern;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        mNamePattern = Pattern.compile("[\\p{L}- ]+");
        mEidPattern = Pattern.compile("^[A-Za-z]+[0-9]+$");
        mPhonePattern = Pattern.compile("(\\+[0-9]+[\\- \\.]*)?"
                + "(\\([0-9]+\\)[\\- \\.]*)?"
                + "([0-9][0-9\\- \\.][0-9\\- \\.]+[0-9])");
        mEmailPattern = Pattern.compile("[a-zA-Z0-9\\+\\._%\\-\\+]{1,256}" +
                "@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        EditTextPreference uteid = (EditTextPreference) findPreference("uteid");
        uteid.setSummary(((EditTextPreference) findPreference("uteid")).getText());
        uteid.setOnPreferenceChangeListener(this);

        EditTextPreference name = (EditTextPreference) findPreference("name");
        name.setSummary(((EditTextPreference) findPreference("name")).getText());
        name.setOnPreferenceChangeListener(this);

        EditTextPreference phone = (EditTextPreference) findPreference("phone");
        phone.getEditText().addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        phone.setSummary(((EditTextPreference) findPreference("phone")).getText());
        phone.setOnPreferenceChangeListener(this);

        EditTextPreference email = (EditTextPreference) findPreference("email");
        email.setSummary(((EditTextPreference) findPreference("email")).getText());
        email.setOnPreferenceChangeListener(this);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case android.R.id.home:
                super.onBackPressed();
                break;
        }

        return true;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen screen, Preference pref) {
        if (pref.getKey().equals("about")) {
            startActivity(new Intent(this, AboutActivity.class));
        } else if (pref.getKey().equals("licenses")) {
            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setTitle("Licenses");
            b.setMessage(R.string.licenses);
            b.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            AlertDialog dialog = b.create();
            dialog.show();
        }

        if (pref instanceof CheckBoxPreference) {
            String key = pref.getKey();
            if (key.equals("crashreports")) {
                AlertDialog.Builder b = new AlertDialog.Builder(this);
                b.setTitle("Attention");
                b.setMessage("Requires application restart to take effect.");
                b.setNegativeButton("Later", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                    }
                })
                .setPositiveButton("Restart", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Borrowed from here: http://stackoverflow.com/a/15564838/3034339
                        Intent i = getBaseContext().getPackageManager()
                                .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }
                });
                AlertDialog dialog = b.create();
                dialog.show();
            }
        }

        return true;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newVal) {

        boolean allIsGood = false;

        if (preference instanceof EditTextPreference) {
            final EditTextPreference editTextPreference = (EditTextPreference) preference;

            String newPrefVal = (String) newVal;
            Matcher m;
            String problem = "";

            final String key = editTextPreference.getKey();
            if (key.equals("name")) {
                m = mNamePattern.matcher(newPrefVal);

                if (m.matches()) {
                    allIsGood = true;
                } else {
                    problem = "Not a valid name";
                }
            } else if (key.equals("uteid")) {
                m = mEidPattern.matcher(newPrefVal);

                allIsGood = true;
                if (m.matches()) {
                    allIsGood = true;
                } else {
                    problem = "Not a valid UT EID";
                }
            } else if (key.equals("phone")) {
                m = mPhonePattern.matcher(newPrefVal);

                if (m.matches()) {
                    allIsGood = true;
                } else {
                    problem = "Not a valid phone number";
                }
            } else {
                m = mEmailPattern.matcher(newPrefVal);

                if (m.matches()) {
                    allIsGood = true;
                } else {
                    problem = "Not a valid email";
                }
            }

            if (allIsGood) {
                editTextPreference.setSummary(editTextPreference.getEditText().getText());
            } else {
//                AlertDialog.Builder b = new AlertDialog.Builder(this);
//                b.setTitle("Invalid input");
//                b.setMessage(problem);
//                b.setNegativeButton("Close", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        //TODO
//                    }
//                });
//                AlertDialog dialog = b.create();
//                dialog.show();
            }
        }

        return allIsGood;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Google Analytics
        if (new PreferenceHandler(this).getAnalyticsOptIn()) {
            EasyTracker.getInstance(this).activityStart(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        // Google Analytics
        if (new PreferenceHandler(this).getAnalyticsOptIn()) {
            EasyTracker.getInstance(this).activityStop(this);
        }
    }

    /**
     * Check to see if user has the official twitter app installed
     * @return true if installed, false if not
     */
    private boolean checkTwitterInstall() {
        try{
            ApplicationInfo info = this.getPackageManager().getApplicationInfo("com.twitter.android", 0);
            return true;
        } catch( PackageManager.NameNotFoundException e ){
            return false;
        }
    }

    /**
     * Check to see if user has the official facebook app installed
     * @return true if installed, false if not
     */
    private boolean checkFBInstall() {
        try{
            ApplicationInfo info = this.getPackageManager().getApplicationInfo("com.facebook.katana", 0);
            return true;
        } catch( PackageManager.NameNotFoundException e ){
            return false;
        }
    }

}
