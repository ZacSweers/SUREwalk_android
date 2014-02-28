package org.utexas.surewalk.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.joda.time.DateTime;
import org.utexas.surewalk.R;
import org.utexas.surewalk.activities.RequestWalkActivity;
import org.utexas.surewalk.data.SureCalendar;

import java.util.Locale;

import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class DashboardFragment extends SherlockFragment {	

    private CalendarClient cc = new CalendarClient();
    private boolean isOpen = false;
    private static Typeface mFont;
    private static final String FONTAWESOME = "fontawesome-webfont.ttf";
    
	private static final String SURE_NUMBER = "512-232-9255";
    private static final String UTPD_NUMBER = "512-471-4441";
    
    private View rootView;

    public static DashboardFragment newInstance(String title) {
    	DashboardFragment df = new DashboardFragment();
    	Bundle args = new Bundle();
    	args.putString("title", title);
    	df.setArguments(args);
    	return df;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_dashboard, null);
        View v = rootView;
        AbsoluteSizeSpan iconSizeSpan = new AbsoluteSizeSpan(95, true);
        ForegroundColorSpan blackSpan = new ForegroundColorSpan(getResources().getColor(android.R.color.black));
        
        TextView requestBtn = (TextView) v.findViewById(R.id.request_btn);
        TextView callUtpdBtn = (TextView) v.findViewById(R.id.contact_utpd_btn);
        TextView callSureWalkBtn = (TextView) v.findViewById(R.id.contact_sure_walk_btn);
        TextView safetyTipsBtn = (TextView) v.findViewById(R.id.safety_btn);
        
        requestBtn.setTypeface(getTypeface(getActivity(), FONTAWESOME));
        callUtpdBtn.setTypeface(getTypeface(getActivity(), FONTAWESOME));
        callSureWalkBtn.setTypeface(getTypeface(getActivity(), FONTAWESOME));
        safetyTipsBtn.setTypeface(getTypeface(getActivity(), FONTAWESOME));
        
        SpannableString requestIcon = new SpannableString(getString(R.string.icon_request_walk));
        SpannableString callUtpdIcon = new SpannableString(getString(R.string.icon_call_utpd));
        SpannableString callSureWalkIcon = new SpannableString(getString(R.string.icon_call_surewalk));
        SpannableString safetyTipsIcon = new SpannableString(getString(R.string.icon_safety_tips));
        
        SpannableString requestText = new SpannableString(requestBtn.getText());
        SpannableString callUtpdText = new SpannableString(callUtpdBtn.getText());
        SpannableString callSureWalkText = new SpannableString(callSureWalkBtn.getText());
        SpannableString safetyTipsText = new SpannableString(safetyTipsBtn.getText());
        
        requestText.setSpan(blackSpan, 0, requestText.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        callUtpdText.setSpan(blackSpan, 0, callUtpdText.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        callSureWalkText.setSpan(blackSpan, 0, callSureWalkText.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        safetyTipsText.setSpan(blackSpan, 0, safetyTipsText.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        
        requestIcon.setSpan(iconSizeSpan, 0, requestIcon.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        callUtpdIcon.setSpan(iconSizeSpan, 0, callUtpdIcon.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        callSureWalkIcon.setSpan(iconSizeSpan, 0, callSureWalkIcon.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        safetyTipsIcon.setSpan(iconSizeSpan, 0, safetyTipsIcon.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        
        requestBtn.setText(TextUtils.concat(requestIcon, requestText));
        callUtpdBtn.setText(TextUtils.concat(callUtpdIcon, callUtpdText));
        callSureWalkBtn.setText(TextUtils.concat(callSureWalkIcon, callSureWalkText));
        safetyTipsBtn.setText(TextUtils.concat(safetyTipsIcon, safetyTipsText));
        
        requestBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
                if (!isNetworkAvailable()) {
                    Toast.makeText(getActivity(), "Please connect to the internet first!", Toast.LENGTH_LONG).show();
                } else if (!isOpen) {
                    Toast.makeText(getActivity(), "SUREwalk isn't open right now :(", Toast.LENGTH_LONG).show();
                } else {
                    Intent i = new Intent(getActivity(), RequestWalkActivity.class);
                    startActivity(i);
                }

			}
		});   
        callSureWalkBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Attention")
                        .setMessage("Call SUREwalk?")
                        .setPositiveButton("Ok, call SUREwalk", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Intent.ACTION_CALL);
                                intent.setData(Uri.parse("tel:" + SURE_NUMBER));
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //nothing happens
                            }
                        })
                        .show();
            }
        });
        callUtpdBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //insert dialog here
                new AlertDialog.Builder(getActivity())
                        .setTitle("Attention")
                        .setMessage("In the event of an emergency, exit this app and call 911.")
                        .setPositiveButton("Ok, call UTPD", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Intent.ACTION_CALL);
                                intent.setData(Uri.parse("tel:" + UTPD_NUMBER));
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                              //nothing happens
                           }
                        })
                        .show();
            }
        });
        safetyTipsBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Safety Tips")
                        .setMessage(R.string.safety_tips)
                        .setNegativeButton("Done", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //nothing happens
                            }
                        })
                        .show();
            }
        });
        return v;
    }
	
	//@return true if network is available -> an internet connection
    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return (info != null);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        updateCalendar();
        
    }

    public Typeface getTypeface(Context context, String typeface) {
	    if (mFont == null) {
	        mFont = Typeface.createFromAsset(context.getAssets(), typeface);
	    }
	    return mFont;
	}
    
	// /////////////////////////
	// / Calendar Manager ///
	// /////////////////////////
	public void updateCalendar() {
		cc.call();
	}

	// A simple class that calls the Google calendar, parses the response, and
	// determines if Sure walk is open
	private class CalendarClient {

		private static final String CAL_ID = "surewalk.utsg%40gmail.com";
		private static final String PARAMS = "full?alt=json&orderby=starttime&max-results=1&singleevents=true&sortorder=descending&futureevents=false";
		private static final String URL = "http://www.google.com/calendar/feeds/" + CAL_ID + "/public/" + PARAMS;
		
		public Gson gson = new Gson();

		public void call() {
			AsyncHttpClient client = new AsyncHttpClient();

			client.get(URL, new AsyncHttpResponseHandler() {
				@SuppressLint("ResourceAsColor")
                @Override
				public void onSuccess(String response) {
					SureCalendar sureCalendar = gson.fromJson(response, SureCalendar.class);

                    SureCalendar.Entry entry = sureCalendar.feed.entry.get(0);

					String status = entry.title.get("$t");
					String details = entry.content.get("$t");
                    String startTime = entry.gd$when.get(0).get("startTime");
                    String endTime = entry.gd$when.get(0).get("endTime");

                    DateTime start = new DateTime(startTime);
                    DateTime end = new DateTime(endTime);

                    DateTime now = new DateTime();

					isOpen = status.toLowerCase(Locale.US).contains("open");
                    boolean isCurrent = false;

                    // If the current date/time isn't in the range of the open hours, then assume closed
                    if (!(now.compareTo(start) >= 0 && now.compareTo(end) <= 0)) {
                        isOpen = false;
                    } else {
                        isCurrent = true;
                    }

					ProgressBar pb = (ProgressBar) rootView.findViewById(R.id.open_status_progress);
					pb.setVisibility(View.GONE);

                    TextView tv2 = (TextView) rootView.findViewById(R.id.et_open_status_details);

                    Crouton.makeText(getActivity(),
                            "SUREwalk is " + (isOpen ? "OPEN" : "CLOSED"),
                            new Style.Builder()
                                    .setTextSize(22)
                                    .setBackgroundColor((isOpen ? R.color.lightGreen : R.color.lightRed))
                                    .build(),
                            (ViewGroup) tv2.getParent())
                            .setConfiguration(new Configuration.Builder()
                                    .setDuration(Configuration.DURATION_INFINITE)
                                    .build())
                            .setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    updateCalendar();
                                }
                            })
                            .show();

                    // Only show details textview if there are any details to display
                    if (details.length() > 0 && isCurrent) {
                        tv2.setVisibility(View.VISIBLE);
                        tv2.setText(details);
                        tv2.setSelected(true);
                    }
				}
			});
		}
	}
}
