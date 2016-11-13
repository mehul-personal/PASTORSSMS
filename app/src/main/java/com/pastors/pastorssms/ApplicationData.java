package com.pastors.pastorssms;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.multidex.MultiDex;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import twitter4j.Twitter;

public class ApplicationData extends Application {
	public static final String serviceURL = "http://45.55.152.215/dev/pastorssms/services/";
	public static final String CONSUMER_KEY = "stV1FNg1csFtkBUtQdyvBaXIO";
	public static final String CONSUMER_SECRET = "6Hyj8TWUay1SOpF3idNTg9G1tsRgxm57q5lKGTP53oxHAEeZjN";
	public static final String CALLBACK_URL = "callback://pastorssms";
	//public static final String CALLBACK_URL1 = "callback1://SettingsFragment";
	//public static final String CALLBACK_URL2 = "callback2://journey";
	//public static final String APP_ID = "280698458805185";

	private RequestQueue mRequestQueue;
	private static ApplicationData mInstance;
	public static final String TAG = ApplicationData.class
			.getSimpleName();
	public static FragmentActivity BASEACTIVITY = null;
	private Twitter twitter;


	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mInstance = this;
	}

	public Twitter getTwitter() {
		return twitter;
	}

	public void setTwitter(Twitter twitter) {
		this.twitter = twitter;
	}

	private OAuthProvider provider;
	private CommonsHttpOAuthConsumer consumer;

	public void setProvider(OAuthProvider provider) {
		this.provider = provider;
	}

	public OAuthProvider getProvider() {
		return provider;
	}

	public void setConsumer(CommonsHttpOAuthConsumer consumer) {
		this.consumer = consumer;
	}

	public CommonsHttpOAuthConsumer getConsumer() {
		return consumer;
	}

	public boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		}

		return mRequestQueue;
	}
	public <T> void addToRequestQueue(Request<T> req, String tag) {
		// set the default tag if tag is empty
		req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
		getRequestQueue().add(req);
	}
	public static synchronized ApplicationData getInstance() {
		return mInstance;
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}
}
