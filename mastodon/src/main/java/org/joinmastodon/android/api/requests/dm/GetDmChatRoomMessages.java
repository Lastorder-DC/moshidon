package org.joinmastodon.android.api.requests.dm;

import com.google.gson.reflect.TypeToken;

import org.joinmastodon.android.api.requests.HeaderPaginationRequest;
import org.joinmastodon.android.model.dm.DmMessage;

public class GetDmChatRoomMessages extends HeaderPaginationRequest<DmMessage>{
	public GetDmChatRoomMessages(String roomID, String maxID, int limit){
		super(HttpMethod.GET, "/dm/chat_rooms/"+roomID+"/messages", new TypeToken<>(){});
		if(maxID!=null)
			addQueryParameter("max_id", maxID);
		if(limit>0)
			addQueryParameter("limit", ""+limit);
	}
}
