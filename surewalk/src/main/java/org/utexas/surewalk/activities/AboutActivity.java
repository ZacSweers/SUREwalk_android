package org.utexas.surewalk.activities;


import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import org.utexas.surewalk.R;

public class AboutActivity extends SherlockActivity {

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_about);
      getSupportActionBar().setDisplayShowTitleEnabled(false);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);

   }


   @Override
   public boolean onCreateOptionsMenu(Menu menu) {

      // Inflate the menu; this adds items to the action bar if it is present.
      //getMenuInflater().inflate(R.menu.about, menu);
      return true;
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
       int id = item.getItemId();
       switch(id) {
           case android.R.id.home:
               super.onBackPressed();
               break;
       }

      return super.onOptionsItemSelected(item);
   }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.social_fb:
                // Adapted slightly from here: http://stackoverflow.com/a/10213314/3034339
                if(checkFBInstall()) {
                    Toast.makeText(this, "This might take a second", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/116169851775344")));
                } else {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/116169851775344")));
                }
                break;
            case R.id.social_twitter:
                // Adapted slightly from here: http://stackoverflow.com/a/18695465/3034339
                if (checkTwitterInstall()) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=texassurewalk")));
                } else {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/texassurewalk")));
                }
                break;
            case R.id.about_source:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.github.com/pandanomic/SUREwalk_android")));
                break;
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
