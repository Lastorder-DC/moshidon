package org.joinmastodon.android.api.requests.dm;

import org.joinmastodon.android.api.MastodonAPIRequest;
import org.joinmastodon.android.model.dm.DmChatRoom;

public class MarkDmChatRoomRead extends MastodonAPIRequest<DmChatRoom>{
	public MarkDmChatRoomRead(String roomID){
		super(HttpMethod.POST, "/dm/chat_rooms/"+roomID+"/read", DmChatRoom.class);
		// The server doesn't read any params here, but OkHttp requires POST/PUT/PATCH requests
		// to carry a body - an empty object serializes to "{}", which is body enough.
		setRequestBody(new Object());
	}
}
