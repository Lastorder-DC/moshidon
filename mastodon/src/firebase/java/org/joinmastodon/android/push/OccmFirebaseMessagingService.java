package org.joinmastodon.android.push;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.joinmastodon.android.PushNotificationReceiver;
import org.joinmastodon.android.api.PushSubscriptionManager;

public class OccmFirebaseMessagingService extends FirebaseMessagingService{
	private static final String TAG="OccmFcmService";

	@Override
	public void onNewToken(@NonNull String token){
		Log.i(TAG, "onNewToken: got a new FCM token");
		PushSubscriptionManager.onNewFcmToken(token);
	}

	@Override
	public void onMessageReceived(@NonNull RemoteMessage message){
		// Sent by our server's FCM v1 API path (see Fcm::MessageSender on the server) - a plain
		// data message, not an encrypted Web Push payload. FCM tokens minted by the native SDK
		// aren't bound to a VAPID key the way a browser's push subscription is, so the server
		// can't address them with standard Web Push and sends a data message instead.
		String payload=message.getData().get("payload");
		String accountID=message.getData().get("account_id");
		if(payload==null || accountID==null){
			Log.w(TAG, "onMessageReceived: message is missing payload/account_id, ignoring");
			return;
		}
		PushNotificationReceiver.handleFcmPush(getApplicationContext(), accountID, payload);
	}
}
