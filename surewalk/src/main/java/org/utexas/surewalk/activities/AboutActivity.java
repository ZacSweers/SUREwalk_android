package org.utexas.surewalk.activities;


import android.os.Bundle;

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



}
