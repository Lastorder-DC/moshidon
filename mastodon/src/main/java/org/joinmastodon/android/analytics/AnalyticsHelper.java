package org.joinmastodon.android.analytics;

import android.content.Context;
import android.util.Log;

/**
 * Reports a handful of events to Firebase Analytics when it's available. Firebase is only
 * linked into build variants that ship a google-services.json (see mastodon/build.gradle),
 * so this reaches it through reflection into AnalyticsBridge (src/firebase) rather than
 * referencing Firebase types directly - this class is compiled into every variant, including
 * fdroid/github builds that never have Firebase on their classpath.
 */
public class AnalyticsHelper{
	private static final String TAG="AnalyticsHelper";

	public static void logLogin(Context context){
		try{
			Class<?> bridge=Class.forName("org.joinmastodon.android.analytics.AnalyticsBridge");
			bridge.getMethod("logLogin", Context.class).invoke(null, context);
		}catch(ClassNotFoundException x){
			// Firebase isn't included in this build variant
		}catch(Exception x){
			Log.w(TAG, "logLogin: failed to log analytics event", x);
		}
	}
}
