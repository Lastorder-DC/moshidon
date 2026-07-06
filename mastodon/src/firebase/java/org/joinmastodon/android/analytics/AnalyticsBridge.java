package org.joinmastodon.android.analytics;

import android.content.Context;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Invoked by org.joinmastodon.android.analytics.AnalyticsHelper through reflection - see
 * that class for why this isn't called directly from shared code.
 */
public class AnalyticsBridge{
	public static void logLogin(Context context){
		FirebaseAnalytics.getInstance(context).logEvent(FirebaseAnalytics.Event.LOGIN, null);
	}
}
