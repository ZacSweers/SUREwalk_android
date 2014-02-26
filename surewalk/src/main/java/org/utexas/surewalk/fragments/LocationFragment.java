package org.utexas.surewalk.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.utexas.surewalk.R;
import org.utexas.surewalk.activities.RequestWalkActivity;
import org.utexas.surewalk.classes.OnFragmentReadyListener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;


public class LocationFragment extends SherlockFragment {

    private LocationManager locationManager;
    private LocationListener gpsLocationListener;
    private LocationListener networkLocationListener;

    private boolean mLocationFound = false;

    private ProgressBar mProgressBar;
    private TextView mSearching, mCoordinates;
    private LinearLayout mStartCard, mDoneCard;
    private String mLat;
    private String mLon;
    public boolean mDone;
    private Button mGPSButton;
    private Button mAddressButton;
    private Button mBuildingButton;
    private Button mRedoButton;
    private String mEnteredAddress;
    private String mAddressError;

    public static LocationFragment newInstance(String title) {
    	LocationFragment lf = new LocationFragment();
    	Bundle args = new Bundle();
    	args.putString("title", title);
    	lf.setArguments(args);
    	return lf;
    }
    
    @Override
    public void onCreate(Bundle bundle) {
    	super.onCreate(bundle);
        mDone = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location, null);

        //location information
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        networkLocationListener = getGpsLocationListener();
        gpsLocationListener = getGpsLocationListener();
        mProgressBar = (ProgressBar) view.findViewById(R.id.location_progressBar);
        mSearching = (TextView) view.findViewById(R.id.finding_location);

        mCoordinates = (TextView) view.findViewById(R.id.coordinates);
        mStartCard = (LinearLayout) view.findViewById(R.id.location_info_card);
        mDoneCard = (LinearLayout) view.findViewById(R.id.location_done_card);

        mGPSButton = (Button) view.findViewById(R.id.bt_start_searching_GPS);
        mAddressButton = (Button) view.findViewById(R.id.bt_start_searching_address);
        mRedoButton = (Button) view.findViewById(R.id.bt_redo);
        mBuildingButton = (Button) view.findViewById(R.id.bt_start_searching_building);

        setupListeners(view);

        return view;
    }

    private void setupListeners(View v) {
        mGPSButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLocating();
                toggleStartView(false);
            }
        });

        mAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEnteredAddress = null;
                enterAddress();
                toggleStartView(false);
            }
        });
        
        mRedoButton.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                resetAllViews();
                mDone = false;
                ((OnFragmentReadyListener) getActivity()).onFragmentReady(false);
            }
        });

        mBuildingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Coming soon", Toast.LENGTH_SHORT).show();
//                buildingSearch();
            }
        });

    }

    public void startLocating(){

        toggleSearchingViews(true);

        //Initially check if GPS is on, offer to turn on if it's off
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, gpsLocationListener);
        }
        else{
            showGPSDisabledAlertToUser();
        }


        // set Network Coordinate
        if (isNetworkAvailable())
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, networkLocationListener);

        new Handler().postDelayed(new Thread(){

            @Override
            public void run(){
                if (!mLocationFound) {
                    locationNotFound();
                }
            }
        }, 10000);

    }

    private void locationNotFound() {
        toggleSearchingViews(false);

        locationManager.removeUpdates(gpsLocationListener);
        locationManager.removeUpdates(networkLocationListener);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
        .setPositiveButton("Enter address",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // enter manually
                        mEnteredAddress = null;
                        enterAddress();
                    }
                })
        .setMessage("Could not pinpoint location, enter manually?")
        .setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                resetAllViews();
            }
        });
                
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void enterAddress() {
        final EditText address = new EditText(getActivity());
        if (mEnteredAddress != null) {
            address.setText(mEnteredAddress);
            address.setError(mAddressError != null ? mAddressError : "Invalid address or out of range");
        }
        address.setHint("e.g. 1 Tower Way, Austin, TX 78705");
        address.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        address.setImeOptions(EditorInfo.IME_ACTION_GO);

        // Show keyboard
        address.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                address.post(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(address, InputMethodManager.SHOW_IMPLICIT);
                    }
                });
            }
        });
        
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
        .setPositiveButton("Find",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // enter get coordinates
                        mEnteredAddress = address.getText().toString();
                        new GetCoordinatesTask().execute(mEnteredAddress);
                    }
        })
        .setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                mEnteredAddress = null;
                resetAllViews();
            }
        })
        .setView(address)
        .setTitle("Address")
        .setMessage("Please enter the address that you would like the volunteers to meet you at.");
        final AlertDialog alert = alertDialogBuilder.create();

        // Go starts search
        address.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    alert.getButton(DialogInterface.BUTTON_POSITIVE).performClick();
                    return true;
                }
                return false;
            }
        });

        alert.show();
        address.requestFocus();
        address.selectAll();
    }

    public void buildingSearch() {

    }

    private void toggleSearchingViews(boolean on) {
        if (on) {
            mStartCard.setVisibility(View.GONE);
            mCoordinates.setVisibility(View.GONE);
            mRedoButton.setVisibility(View.GONE);
            mDoneCard.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
            mSearching.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mSearching.setVisibility(View.GONE);
        }
    }
    
    /**
     * Toggle method for setting the visibility of the start view
     * @param on boolean for whether to turn the view on or off
     */
    private void toggleStartView(boolean on) {
        if (on) {
            mAddressButton.setVisibility(View.VISIBLE);
            mGPSButton.setVisibility(View.VISIBLE);
            mStartCard.setVisibility(View.VISIBLE);
        } else {
            mAddressButton.setVisibility(View.GONE);
            mGPSButton.setVisibility(View.GONE);
            mStartCard.setVisibility(View.GONE);
        }
    }

    private LocationListener getGpsLocationListener() {
        return new LocationListener() {
            @Override
            public void onLocationChanged(Location fix) {
                double latitude = fix.getLatitude();
                double longitude = fix.getLongitude();
                mLat = Double.toString(latitude);
                mLon = Double.toString(longitude);
                locationManager.removeUpdates(gpsLocationListener);
                locationManager.removeUpdates(networkLocationListener);
                mLocationFound = true;
                done();
            }
            @Override
            public void onProviderDisabled(String provider) {
                // required for interface, not used
            }
            @Override
            public void onProviderEnabled(String provider) {
                // required for interface, not used
            }
            @Override
            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
                // required for interface, not used
            }
        };
    }

    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
        .setMessage("GPS is disabled. Would you like to enable it?")
        .setPositiveButton("Enable",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Intent callGPSSettingIntent = new Intent(
                                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(callGPSSettingIntent);
                    }
                })
        .setOnCancelListener(new DialogInterface.OnCancelListener() {
            
            @Override
            public void onCancel(DialogInterface dialog) {
                resetAllViews();
            }
        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    //@return true if network is available -> an internet connection
    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return (info != null);
    }

    /**
     * AsyncTask for getting the coordinates from Google's Geodecoder Service
     * Takes in a String representation of the address and sends it to Google
     * Parses the JSON response, retrieves the coordinates, and then sends them
     * to the done() method.
     */
    private class GetCoordinatesTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        public void onPreExecute(){

        }

        @Override
        public JSONObject doInBackground(String... params){

            String urlString = "";
            for (String s : Arrays.asList(params[0].split(" "))) {
                urlString += "+" + s;
            }

            try {
                HttpParams httpParams = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
                HttpConnectionParams.setSoTimeout(httpParams, 5000);

                DefaultHttpClient client = new DefaultHttpClient(httpParams);
                HttpPost post = new HttpPost("http://maps.googleapis.com/maps/api/geocode/json?address=" + urlString + "&sensor=true");

                HttpResponse response = client.execute(post);

                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                StringBuilder builder = new StringBuilder();
                for (String line; (line = reader.readLine()) != null;) {
                    builder.append(line).append("\n");
                }

                return new JSONObject(new JSONTokener(builder.toString()));
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public void onPostExecute(JSONObject result) {
            try {
                if (result != null && result.has("status") && result.get("status").equals("OK")) {
                    JSONArray coordinates = result.getJSONArray("results");
                    JSONObject actualresults = coordinates.getJSONObject(0);
                    JSONObject geometry = actualresults.getJSONObject("geometry");
                    JSONObject location = geometry.getJSONObject("location");
                    mLat = location.getString("lat");
                    mLon = location.getString("lng");

                    double distance = haversine(30.28768, -97.74039, Double.parseDouble(mLat),
                            Double.parseDouble(mLon));

                    if (distance > 3.0) {

                        // Not an address in range
                        mLon = null;
                        mLat = null;
                        mAddressError = "Address out of range";
                    }

                } else {
                    mLon = null;
                    mLat = null;

                    if (mEnteredAddress.equals("")) {
                        mAddressError = "Cannot be blank";
                    } else {
                        mAddressError = "Invalid address";
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            done();
        }
    }
    
    private void resetAllViews() {
        toggleSearchingViews(false);
        toggleStartView(true);
        mDoneCard.setVisibility(View.GONE);
        mRedoButton.setVisibility(View.GONE);
        
    }

    public void done() {
        String coordinateString;
        if (mLat != null && mLon != null) {
            coordinateString = "(" + mLat + ", " + mLon + ")";
            toggleSearchingViews(false);
            mDoneCard.setVisibility(View.VISIBLE);
            mCoordinates.setText(coordinateString);
            mCoordinates.setVisibility(View.VISIBLE);
            mRedoButton.setVisibility(View.VISIBLE);
            mDone = true;
            ((OnFragmentReadyListener) getActivity()).onFragmentReady(true);
            if (isAdded()) {
                ViewPager pager = ((RequestWalkActivity) getActivity()).getPager();
                FragmentPagerAdapter adapter = (FragmentPagerAdapter) pager.getAdapter();
                final RequestMapFragment nextFrag = (RequestMapFragment) adapter.getItem(2);
                nextFrag.setStartLoc(Double.parseDouble(mLat), Double.parseDouble(mLon));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((RequestWalkActivity) getActivity()).getNextButton().performClick();
                        if (!nextFrag.containsPoint(nextFrag.getHole(), nextFrag.getStartLoc()))
                            nextFrag.createAlertDialog(false);
                    }
                }, 150);
            }
        } else {
            enterAddress();
        }
    }

    public double[] getCoordinates(){
        if (mLat != null && mLon != null) {
            return new double[]{Double.parseDouble(mLat), Double.parseDouble(mLon)};
        } else {
            return null;
        }
    }

    public double haversine(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6372.8; // In kilometers
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c;
    }
}