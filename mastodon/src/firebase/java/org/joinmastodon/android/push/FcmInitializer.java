package org.joinmastodon.android.push;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;

import org.joinmastodon.android.api.PushSubscriptionManager;

/**
 * Bridge used by {@link PushSubscriptionManager#tryRegisterFCM()} through reflection, so that
 * shared code (compiled into every build variant, including fdroid/github builds that never
 * have Firebase on their classpath) doesn't need to reference Firebase types directly.
 */
public class FcmInitializer{
	private static final String TAG="FcmInitializer";

	public static void requestToken(){
		FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task->{
			if(task.isSuccessful()){
				PushSubscriptionManager.onNewFcmToken(task.getResult());
			}else{
				Log.w(TAG, "requestToken: failed to fetch FCM token", task.getException());
			}
		});
	}
}
