package org.joinmastodon.android.api.requests.dm;

import org.joinmastodon.android.api.MastodonAPIRequest;
import org.joinmastodon.android.model.dm.DmMessage;

import java.util.List;

public class SendDmMessage extends MastodonAPIRequest<DmMessage>{
	public SendDmMessage(String roomID, String content, String inReplyToId){
		this(roomID, content, inReplyToId, null);
	}

	public SendDmMessage(String roomID, String content, String inReplyToId, List<String> mediaIds){
		super(HttpMethod.POST, "/dm/chat_rooms/"+roomID+"/messages", DmMessage.class);
		setRequestBody(new Request(content, inReplyToId, mediaIds));
	}

	private static class Request{
		public String content;
		public String inReplyToId;
		public List<String> mediaIds;

		public Request(String content, String inReplyToId, List<String> mediaIds){
			this.content=content;
			this.inReplyToId=inReplyToId;
			this.mediaIds=mediaIds;
		}
	}
}
