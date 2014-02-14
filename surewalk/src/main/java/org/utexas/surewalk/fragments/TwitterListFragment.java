package org.utexas.surewalk.fragments;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;

import org.utexas.surewalk.data.Tweet;
import org.utexas.surewalk.data.TweetAdapter;

import java.util.ArrayList;

import twitter4j.AsyncTwitter;
import twitter4j.AsyncTwitterFactory;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterException;
import twitter4j.TwitterListener;
import twitter4j.TwitterMethod;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterListFragment extends SherlockListFragment {

	// /////////////////////////////////////////////////////////////////////////////////////////////////
	// / TWITTER AUTH INFO ///
	// /////////////////////////////////////////////////////////////////////////////////////////////////
	private final static String CONSUMER_KEY = "G7TG9wmWqsYiXEduXEzJXQ"; // /
	private final static String CONSUMER_SECRET = "Gel2C5unJakDfKiJCeFIh3OVpbqO3lFS04xqBgbU04"; // /
	private final static String ACCESS_TOKEN = "517041256-ZJVIqkv3q2m1pSyJaRrdncahiyihv3mIbgrAoj8B";// /
	private final static String ACCESS_SECRET = "nVrY5w3dQ9QJKfmrWwi3rRRpQrdeDI5Nett4Xk8kpqU"; // /
	// /////////////////////////////////////////////////////////////////////////////////////////////////
	private static final long SUREWALK_TWITTER_ID = 193517698;
	
	public static TwitterListFragment newInstance(String title) {
    	TwitterListFragment tlf = new TwitterListFragment();
    	Bundle args = new Bundle();
    	args.putString("title", title);
    	tlf.setArguments(args);
    	return tlf;
    }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {    
            updateTwitter();
        } catch (TwitterException e) {
            e.printStackTrace();
        }
	}
	
    @Override
    public void onListItemClick(ListView lv, View view, int position, long id) {
        Tweet twt = (Tweet) lv.getItemAtPosition(position);
        String uriToParse = "http://twitter.com/" + twt.getName() + "/status/" + twt.getId();;

        if (checkTwitterInstall()) {
            uriToParse = "twitter://status?status_id=" + twt.getId();
        }

        Intent viewTweet = new Intent(Intent.ACTION_VIEW, Uri.parse(uriToParse));
        startActivity(viewTweet);
    }

	private void updateTwitter() throws TwitterException {

		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		.setOAuthConsumerKey(CONSUMER_KEY)
		.setOAuthConsumerSecret(CONSUMER_SECRET)
		.setOAuthAccessToken(ACCESS_TOKEN)
		.setOAuthAccessTokenSecret(ACCESS_SECRET);
		
		TwitterListener listener = new TwitterAdapter() {
			@Override
			public void gotUserTimeline(ResponseList<Status> statuses) {
				String msg = " Sure Walk Twitter:" + statuses.size();

				ArrayList<Tweet> tweetList = new ArrayList<Tweet>();
				for(Status st : statuses) {
					tweetList.add(new Tweet((st)));
				}

				displayTwitter(tweetList);
			}

			@Override
			public void onException(TwitterException e, TwitterMethod method) {
			    if(method == TwitterMethod.HOME_TIMELINE) {
					e.printStackTrace();
				} else {
				    e.printStackTrace();
				}
			}
		};
		// The factory instance is re-usable and thread safe.
		AsyncTwitterFactory factory = new AsyncTwitterFactory(cb.build());
		AsyncTwitter asyncTwitter = factory.getInstance();
		asyncTwitter.addListener(listener);
		asyncTwitter.getUserTimeline(SUREWALK_TWITTER_ID);

	}
	private void displayTwitter(final ArrayList<Tweet> tweetList) {

	      final TweetAdapter<Tweet> adapter = new TweetAdapter<Tweet>(getActivity(), tweetList);
	      
	      getActivity().runOnUiThread(new Runnable() {
	         @Override
	         public void run() {
	        	 setListAdapter(adapter);
	         }
	      });
	}

    /**
     * Check to see if user has the official twitter app installed
     * @return true if installed, false if not
     */
    private boolean checkTwitterInstall() {
        try{
            ApplicationInfo info = getActivity().getPackageManager().getApplicationInfo("com.twitter.android", 0);
            return true;
        } catch( PackageManager.NameNotFoundException e ){
            return false;
        }
    }
}
