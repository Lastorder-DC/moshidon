package org.joinmastodon.android.fragments.dm;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.joinmastodon.android.E;
import org.joinmastodon.android.R;
import org.joinmastodon.android.api.MastodonAPIRequest;
import org.joinmastodon.android.api.requests.dm.GetDmChatRooms;
import org.joinmastodon.android.api.session.AccountSessionManager;
import org.joinmastodon.android.events.DmPushReceivedEvent;
import org.joinmastodon.android.fragments.MastodonRecyclerFragment;
import org.joinmastodon.android.model.Account;
import org.joinmastodon.android.model.HeaderPaginationList;
import org.joinmastodon.android.model.dm.DmChatRoom;
import org.joinmastodon.android.ui.OutlineProviders;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import com.squareup.otto.Subscribe;

import me.grishka.appkit.Nav;
import me.grishka.appkit.api.SimpleCallback;
import me.grishka.appkit.imageloader.ViewImageLoader;
import me.grishka.appkit.imageloader.requests.UrlImageLoaderRequest;
import me.grishka.appkit.utils.BindableViewHolder;
import me.grishka.appkit.utils.V;
import me.grishka.appkit.views.UsableRecyclerView;

public class DmChatRoomListFragment extends MastodonRecyclerFragment<DmChatRoom>{
	private static final long POLL_INTERVAL_MS=20_000;

	private String accountID;
	private String myAccountId;
	private String nextMaxID;
	private DmChatRoomAdapter adapter;
	private final Handler pollHandler=new Handler(Looper.getMainLooper());
	private final Runnable pollRunnable=this::pollRunnableTick;

	public DmChatRoomListFragment(){
		super(20);
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		E.register(this);
		accountID=getArguments().getString("account");
		myAccountId=AccountSessionManager.getInstance().getAccount(accountID).self.id;
		setTitle(R.string.dm_chat_rooms_title);
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		E.unregister(this);
	}

	@Subscribe
	public void onDmPushReceived(DmPushReceivedEvent ev){
		if(!ev.accountID.equals(accountID))
			return;
		if(getActivity()==null || dataLoading)
			return;
		// A push already told us something changed - no need to wait for the next poll tick.
		pollHandler.removeCallbacks(pollRunnable);
		pollHandler.postDelayed(pollRunnable, POLL_INTERVAL_MS);
		onRefresh();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState){
		super.onViewCreated(view, savedInstanceState);
		setRefreshEnabled(true);
	}

	@Override
	protected void onShown(){
		super.onShown();
		if(!getArguments().getBoolean("noAutoLoad") && !loaded && !dataLoading)
			loadData();
		pollHandler.removeCallbacks(pollRunnable);
		pollHandler.postDelayed(pollRunnable, POLL_INTERVAL_MS);
	}

	@Override
	protected void onHidden(){
		super.onHidden();
		pollHandler.removeCallbacks(pollRunnable);
	}

	@Override
	public void onDestroyView(){
		super.onDestroyView();
		pollHandler.removeCallbacks(pollRunnable);
	}

	/** Lightweight polling stand-in for real-time updates - there's no streaming channel for DMs. */
	private void pollRunnableTick(){
		if(getActivity()==null) return;
		if(!dataLoading)
			onRefresh();
		pollHandler.postDelayed(pollRunnable, POLL_INTERVAL_MS);
	}

	@Override
	protected void doLoadData(int offset, int count){
		MastodonAPIRequest<?> request=new GetDmChatRooms(offset==0 ? null : nextMaxID, count)
				.setCallback(new SimpleCallback<>(this){
					@Override
					public void onSuccess(HeaderPaginationList<DmChatRoom> result){
						if(getActivity()==null) return;
						nextMaxID=result.getNextPageMaxID();
						onDataLoaded(result, nextMaxID!=null);
					}
				});
		request.exec(accountID);
		currentRequest=request;
	}

	@Override
	protected RecyclerView.Adapter<DmChatRoomViewHolder> getAdapter(){
		return adapter=new DmChatRoomAdapter();
	}

	private class DmChatRoomAdapter extends RecyclerView.Adapter<DmChatRoomViewHolder>{
		@NonNull
		@Override
		public DmChatRoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
			return new DmChatRoomViewHolder();
		}

		@Override
		public void onBindViewHolder(@NonNull DmChatRoomViewHolder holder, int position){
			holder.bind(data.get(position));
		}

		@Override
		public int getItemCount(){
			return data.size();
		}
	}

	private class DmChatRoomViewHolder extends BindableViewHolder<DmChatRoom> implements UsableRecyclerView.Clickable{
		private final android.widget.ImageView avatar;
		private final TextView title, lastMessage, time;
		private final View unreadDot;

		public DmChatRoomViewHolder(){
			super(getActivity(), R.layout.item_dm_chat_room, list);
			avatar=findViewById(R.id.dm_room_avatar);
			avatar.setOutlineProvider(OutlineProviders.OVAL);
			avatar.setClipToOutline(true);
			title=findViewById(R.id.dm_room_title);
			lastMessage=findViewById(R.id.dm_room_last_message);
			time=findViewById(R.id.dm_room_time);
			unreadDot=findViewById(R.id.dm_room_unread_dot);
		}

		@Override
		public void onBind(DmChatRoom item){
			itemView.setAlpha(item.accepted ? 1f : 0.5f);

			String displayTitle=item.displayTitle(myAccountId);
			title.setText(!TextUtils.isEmpty(displayTitle) ? displayTitle : itemView.getContext().getString(R.string.mo_app_name));

			if(!item.accepted){
				lastMessage.setText(R.string.dm_pending_invite);
			}else if(item.lastMessage!=null){
				String preview=!TextUtils.isEmpty(item.lastMessage.contentPlain) ? item.lastMessage.contentPlain : item.lastMessage.content;
				lastMessage.setText(preview);
			}else{
				lastMessage.setText(R.string.dm_no_messages_yet);
			}

			if(item.lastMessageAt!=null){
				time.setText(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
						.withZone(ZoneId.systemDefault())
						.format(item.lastMessageAt));
			}else{
				time.setText("");
			}

			unreadDot.setVisibility(item.unread ? View.VISIBLE : View.GONE);

			Account avatarAccount=item.participants==null ? null : item.participants.stream()
					.filter(a->!a.id.equals(myAccountId))
					.findFirst()
					.orElse(item.owner);
			if(avatarAccount!=null)
				ViewImageLoader.loadWithoutAnimation(avatar, null, new UrlImageLoaderRequest(avatarAccount.avatar, V.dp(48), V.dp(48)));
		}

		@Override
		public void onClick(){
			Bundle args=new Bundle();
			args.putString("account", accountID);
			args.putString("roomUuid", item.uuid);
			Nav.go(getActivity(), DmChatRoomFragment.class, args);
		}
	}
}
