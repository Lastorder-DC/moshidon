package org.joinmastodon.android.api.requests.notifications;

import org.joinmastodon.android.api.ResultlessMastodonAPIRequest;

public class DismissPendingMention extends ResultlessMastodonAPIRequest{
	public DismissPendingMention(String id){
		super(HttpMethod.DELETE, "/pending_mentions/"+id);
	}
}
