package org.joinmastodon.android.events;

/** Posted when a DM push notification arrives, so any currently-open DM screen can
 *  refresh immediately instead of waiting for its next poll tick. */
public class DmPushReceivedEvent{
	public final String accountID;
	public final String roomUuid;

	public DmPushReceivedEvent(String accountID, String roomUuid){
		this.accountID=accountID;
		this.roomUuid=roomUuid;
	}
}
