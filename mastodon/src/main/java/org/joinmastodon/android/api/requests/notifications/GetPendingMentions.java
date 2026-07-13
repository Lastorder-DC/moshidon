package org.joinmastodon.android.api.requests.notifications;

import com.google.gson.reflect.TypeToken;

import org.joinmastodon.android.api.requests.HeaderPaginationRequest;
import org.joinmastodon.android.model.Notification;

public class GetPendingMentions extends HeaderPaginationRequest<Notification>{
	public GetPendingMentions(String maxID, int limit){
		super(HttpMethod.GET, "/pending_mentions", new TypeToken<>(){});
		if(maxID!=null)
			addQueryParameter("max_id", maxID);
		if(limit>0)
			addQueryParameter("limit", ""+limit);
		removeUnsupportedItems=true;
	}
}
