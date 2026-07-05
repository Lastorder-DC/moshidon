package org.joinmastodon.android.api.requests.dm;

import org.joinmastodon.android.api.MastodonAPIRequest;
import org.joinmastodon.android.model.dm.DmChatRoom;

public class GetDmChatRoom extends MastodonAPIRequest<DmChatRoom>{
	public GetDmChatRoom(String roomID){
		super(HttpMethod.GET, "/dm/chat_rooms/"+roomID, DmChatRoom.class);
	}
}
