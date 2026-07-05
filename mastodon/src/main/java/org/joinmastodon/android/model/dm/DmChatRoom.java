package org.joinmastodon.android.model.dm;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import org.joinmastodon.android.api.RequiredField;
import org.joinmastodon.android.model.Account;
import org.joinmastodon.android.model.BaseModel;
import org.parceler.Parcel;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Parcel
public class DmChatRoom extends BaseModel{
	@RequiredField
	public String id;
	@RequiredField
	public String uuid;
	@RequiredField
	public RoomType roomType;
	public String title;
	public Instant lastMessageAt;
	public boolean unread;
	public boolean accepted;
	@RequiredField
	public Instant createdAt;
	public List<ReadReceipt> readReceipts;
	public Account owner;
	public List<Account> participants;
	public DmMessage lastMessage;

	/**
	 * Title to show to a specific viewer: the room's own title if set, otherwise the other
	 * participants' display names (falling back to username), joined together.
	 */
	public String displayTitle(String myAccountId){
		if(!TextUtils.isEmpty(title))
			return title;
		if(participants==null || participants.isEmpty())
			return "";
		return participants.stream()
				.filter(a->!a.id.equals(myAccountId))
				.map(a->!TextUtils.isEmpty(a.displayName) ? a.displayName : a.username)
				.collect(Collectors.joining(", "));
	}

	@NonNull
	@Override
	public String toString(){
		return "DmChatRoom{"+
				"id='"+id+'\''+
				", uuid='"+uuid+'\''+
				", roomType="+roomType+
				", title='"+title+'\''+
				'}';
	}

	@Parcel
	public static class ReadReceipt{
		public String accountId;
		public String lastReadMessageId;

		public ReadReceipt(){}
	}

	public enum RoomType{
		@SerializedName("direct")
		DIRECT,
		@SerializedName("group_chat")
		GROUP_CHAT
	}
}
