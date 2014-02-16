package org.utexas.surewalk.controllers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceHandler {

    SharedPreferences mPrefs;

    public PreferenceHandler(Context c) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(c.getApplicationContext());
    }

    public String getUTEID() {
        return mPrefs.getString("uteid", "");
    }

    public void setUTEID(String uteid) {
        SharedPreferences.Editor prefEdit = mPrefs.edit();
        prefEdit.putString("uteid", uteid);
        prefEdit.commit();
    }

    public String getName() {
        return mPrefs.getString("name", "");
    }

    public void setName(String name) {
        SharedPreferences.Editor prefEdit = mPrefs.edit();
        prefEdit.putString("name", name);
        prefEdit.commit();
    }

    public String getPhoneNumber() {
        return mPrefs.getString("phone", "");
    }

    public void setPhoneNumber(String phone) {
        SharedPreferences.Editor prefEdit = mPrefs.edit();
        prefEdit.putString("phone", phone);
        prefEdit.commit();
    }

    public void setFirstRun() {
        SharedPreferences.Editor prefEdit = mPrefs.edit();
        prefEdit.putBoolean("firstrun", true);
        prefEdit.commit();
    }

    public boolean getFirstRun(){
        return mPrefs.getBoolean("firstrun", false);
    }

    public void setEmail(String email) {
        SharedPreferences.Editor prefEdit = mPrefs.edit();
        prefEdit.putString("email", email);
        prefEdit.commit();
    }

    public String getEmail() {
        return mPrefs.getString("email", "");
    }

    public void setAnalyticsOptIn(boolean val) {
        SharedPreferences.Editor prefEdit = mPrefs.edit();
        prefEdit.putBoolean("analytics", val);
        prefEdit.commit();
    }

    public boolean getAnalyticsOptIn() {
        return mPrefs.getBoolean("analytics", false);
    }

    public void setCROptIn(boolean val) {
        SharedPreferences.Editor prefEdit = mPrefs.edit();
        prefEdit.putBoolean("crashreports", val);
        prefEdit.commit();
    }

    public boolean getCROptIn() {
        return mPrefs.getBoolean("crashreports", false);
    }

    public void setKeyCheck(boolean val) {
        SharedPreferences.Editor prefEdit = mPrefs.edit();
        prefEdit.putBoolean("keycheck", val);
        prefEdit.commit();
    }

    public boolean getKeyCheck() {
        return mPrefs.getBoolean("keycheck", false);
    }

//    public String[] getInfo() {
//        String[] s = [mPrefs.getString("name", ""), mPrefs.getString("uteid", ""), mPrefs.getString("phone", "")];
//        return new String[]{mPrefs.getString("name", ""), mPrefs.getString("uteid", ""), mPrefs.getString("phone", "")};
//    }
}
