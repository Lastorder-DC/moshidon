package org.joinmastodon.android.model.dm;

import androidx.annotation.NonNull;

import org.joinmastodon.android.api.RequiredField;
import org.joinmastodon.android.model.Account;
import org.joinmastodon.android.model.Attachment;
import org.joinmastodon.android.model.BaseModel;
import org.joinmastodon.android.model.Emoji;
import org.parceler.Parcel;

import java.time.Instant;
import java.util.List;

@Parcel
public class DmMessage extends BaseModel{
	@RequiredField
	public String id;
	@RequiredField
	public String dmChatRoomUuid;
	public String content;
	public String contentPlain;
	public String inReplyToId;
	@RequiredField
	public Instant createdAt;
	public String language;
	@RequiredField
	public Account account;
	public List<Attachment> attachments;
	public List<Emoji> emojis;

	@NonNull
	@Override
	public String toString(){
		return "DmMessage{"+
				"id='"+id+'\''+
				", dmChatRoomUuid='"+dmChatRoomUuid+'\''+
				", contentPlain='"+contentPlain+'\''+
				", createdAt="+createdAt+
				'}';
	}
}
