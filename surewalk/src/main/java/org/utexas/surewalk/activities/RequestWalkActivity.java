package org.utexas.surewalk.activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.andreabaccega.widget.FormEditText;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.maps.GoogleMapOptions;
import com.viewpagerindicator.TitlePageIndicator;

import org.utexas.surewalk.R;
import org.utexas.surewalk.classes.OnFragmentReadyListener;
import org.utexas.surewalk.controllers.FragmentAdapter;
import org.utexas.surewalk.controllers.ParseHandler;
import org.utexas.surewalk.controllers.PreferenceHandler;
import org.utexas.surewalk.data.WalkRequest;
import org.utexas.surewalk.fragments.InfoFragment;
import org.utexas.surewalk.fragments.LocationFragment;
import org.utexas.surewalk.fragments.RequestMapFragment;
import org.utexas.surewalk.fragments.ReviewFragment;


public class RequestWalkActivity extends SherlockFragmentActivity implements OnFragmentReadyListener {

    private FragmentAdapter mAdapter;
    private WalkRequest walkRequest;
    private ParseHandler mParseHandler;
    private ViewPager pager;
    private Button prevButton, nextButton;
    private TitlePageIndicator titleindicator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_request_walk);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        pager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new FragmentAdapter(this, pager);
        titleindicator = (TitlePageIndicator) findViewById(R.id.titles);
        prevButton = (Button) findViewById(R.id.prev_button);
        nextButton = (Button) findViewById(R.id.next_button);

        titleindicator.setViewPager(pager);
        titleindicator.setOnTouchListener(null);
        titleindicator.setOnClickListener(null);
        titleindicator.setOnPageChangeListener(mAdapter);

        prevButton.setEnabled(false);
        nextButton.setEnabled(false);
       
        mAdapter.addFragment(InfoFragment.newInstance("Information"));
        mAdapter.addFragment(LocationFragment.newInstance("Location"));
        mAdapter.addFragment(RequestMapFragment.newInstance(new GoogleMapOptions(), "Destination"));
        mAdapter.addFragment(ReviewFragment.newInstance("Review"));
        
        initializePageListeners(pager, prevButton, nextButton);
        pager.setOffscreenPageLimit(mAdapter.getCount() - 1);

        mParseHandler = new ParseHandler(this);
        mParseHandler.initializeParse();
        walkRequest = new WalkRequest();
    }

    private void initializePageListeners(final ViewPager pager, final Button prevButton, final Button nextButton) {
        nextButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                int pageNum = pager.getCurrentItem();
                switch (pageNum) {
                    case 0:
                        // Getting info
                        if (v.isEnabled() && validationCheck()) {
                            String[] info = ((InfoFragment) mAdapter.getItem(0)).getInfo();
                            walkRequest.setName(info[0]);
                            walkRequest.setUTEID(info[1]);
                            walkRequest.setPhoneNumber(info[2]);
                            walkRequest.setEmail(info[3]);
                            pager.setCurrentItem(1, true);
                            v.setEnabled(false);
                            prevButton.setEnabled(true);
                        }
                        break;
                    case 1:
                        // Getting location
                        LocationFragment locFrag = ((LocationFragment) mAdapter.getItem(1));
                        if(v.isEnabled() && locFrag.mDone) {
	                        double[] coordinates = locFrag.getCoordinates();
	                        
	                        walkRequest.setStartLocation(coordinates[0], coordinates[1]);
	                        pager.setCurrentItem(2, true);
                            showHelpToast();
	                        v.setEnabled(false);
	                        prevButton.setEnabled(true);
                        } else {
                            Toast.makeText(getBaseContext(), "Please select a start address first", Toast.LENGTH_SHORT).show();
                        }
                        
                        break;
                    case 2:
                        // Getting destination
                        RequestMapFragment destFrag = ((RequestMapFragment) mAdapter.getItem(2));
                        if (destFrag.mPointPicked) {
                            double[] coordinates2 = destFrag.getCoordinates();
                            walkRequest.setEndLocation(coordinates2[0], coordinates2[1]);
                            ReviewFragment commentFrag = ((ReviewFragment) mAdapter.getItem(3));
                            commentFrag.populateFields(walkRequest);
                            pager.setCurrentItem(3, true);
                            ((Button) v).setText("Submit");
                            v.setEnabled(true);
                            prevButton.setEnabled(true);

                        } else {
                            Toast.makeText(getBaseContext(), "Please select a point", Toast.LENGTH_LONG).show();
                        }
                        break;
                    case 3:
                        // Getting comments
                        ReviewFragment commentFrag = ((ReviewFragment) mAdapter.getItem(3));
                        walkRequest.setMessage(commentFrag.getComments());
                        mParseHandler.saveWalkInfo(walkRequest);
                        finish();

                    default:
                        break;
                }
            }
        });

        prevButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                int pageNum = pager.getCurrentItem();
                pager.setCurrentItem(pageNum - 1, true);
                switch (pageNum) {
                    case 1:
                        // Getting location
                        v.setEnabled(false);
                        nextButton.setEnabled(true);
                        break;
                    case 2:
                        // Getting destination
                        v.setEnabled(true);
                        nextButton.setEnabled(true);
                        break;
                    case 3:
                        // Getting comments
                        nextButton.setText("Next");
                        break;
                    default:
                        break;
                }
            }
        });
    }
    
    @Override
    public void onBackPressed() {
    	if(pager.getCurrentItem() > 0) {
    		prevButton.performClick();
    	}
    	else {
    		super.onBackPressed();
    	}
    }

    private boolean validationCheck() {
        FormEditText[] allFields = {(FormEditText) findViewById(R.id.et_name), (FormEditText) findViewById(R.id.et_eid), (FormEditText) findViewById(R.id.et_phone), (FormEditText) findViewById(R.id.et_email)};

        boolean allValid = true;
        for (FormEditText field: allFields) {
            allValid = field.testValidity() && allValid;
        }
        return allValid;
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
    
    public ViewPager getPager() {
        return pager;
    }
    
    public Button getNextButton() {
        return nextButton;
    }

    public void showHelpToast() {
        Toast.makeText(getBaseContext(), "Tap on your destination", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFragmentReady(boolean val) {
        nextButton.setEnabled(val);
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
}