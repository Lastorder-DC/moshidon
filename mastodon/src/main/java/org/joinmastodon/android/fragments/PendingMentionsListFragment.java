package org.joinmastodon.android.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import org.joinmastodon.android.GlobalUserPreferences;
import org.joinmastodon.android.R;
import org.joinmastodon.android.api.requests.notifications.DismissPendingMention;
import org.joinmastodon.android.api.requests.notifications.GetPendingMentions;
import org.joinmastodon.android.model.HeaderPaginationList;
import org.joinmastodon.android.model.Notification;
import org.joinmastodon.android.ui.displayitems.StatusDisplayItem;
import org.joinmastodon.android.ui.utils.UiUtils;
import org.parceler.Parcels;

import java.util.Collections;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import me.grishka.appkit.api.Callback;
import me.grishka.appkit.api.ErrorResponse;
import me.grishka.appkit.api.SimpleCallback;

public class PendingMentionsListFragment extends BaseStatusListFragment<Notification>{
	private String maxID;

	@Override
	protected boolean wantsComposeButton(){
		return false;
	}

	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		setTitle(R.string.sk_pending_mentions);
	}

	@Override
	protected List<StatusDisplayItem> buildDisplayItems(Notification n){
		if(n.status==null)
			return Collections.emptyList();
		int flags=0;
		if(GlobalUserPreferences.spectatorMode)
			flags|=StatusDisplayItem.FLAG_NO_FOOTER;
		if(!GlobalUserPreferences.showMediaPreview)
			flags|=StatusDisplayItem.FLAG_NO_MEDIA_PREVIEW;
		return StatusDisplayItem.buildItems(this, n.status, accountID, n, knownAccounts, null, flags);
	}

	@Override
	protected void addAccountToKnown(Notification n){
		if(!knownAccounts.containsKey(n.account.id))
			knownAccounts.put(n.account.id, n.account);
		if(n.status!=null && !knownAccounts.containsKey(n.status.account.id))
			knownAccounts.put(n.status.account.id, n.status.account);
	}

	@Override
	protected void doLoadData(int offset, int count){
		currentRequest=new GetPendingMentions(offset>0 ? maxID : null, count)
				.setCallback(new SimpleCallback<>(this){
					@Override
					public void onSuccess(HeaderPaginationList<Notification> result){
						if(getActivity()==null)
							return;
						maxID=result.nextPageUri!=null ? result.nextPageUri.getQueryParameter("max_id") : null;
						onDataLoaded(result, maxID!=null);
					}
				})
				.exec(accountID);
	}

	@Override
	public void onItemClick(String id){
		Notification n=getNotificationByID(id);
		if(n==null)
			return;
		Bundle args=new Bundle();
		if(n.status!=null && n.status.inReplyToAccountId!=null && knownAccounts.containsKey(n.status.inReplyToAccountId))
			args.putParcelable("inReplyToAccount", Parcels.wrap(knownAccounts.get(n.status.inReplyToAccountId)));
		UiUtils.showFragmentForNotification(getContext(), n, accountID, args);
	}

	private Notification getNotificationByID(String id){
		for(Notification n:data){
			if(n.id.equals(id))
				return n;
		}
		for(Notification n:preloadedData){
			if(n.id.equals(id))
				return n;
		}
		return null;
	}

	public void dismissPendingMention(Notification n){
		new DismissPendingMention(n.id).setCallback(new Callback<>(){
			@Override
			public void onSuccess(Void result){
				if(getActivity()==null)
					return;
				removeNotification(n);
			}

			@Override
			public void onError(ErrorResponse error){
				if(getActivity()==null)
					return;
				error.showToast(getActivity());
			}
		}).exec(accountID);
	}

	public void removeNotification(Notification n){
		data.remove(n);
		preloadedData.remove(n);
		int index=-1;
		for(int i=0;i<displayItems.size();i++){
			if(n.id.equals(displayItems.get(i).parentID)){
				index=i;
				break;
			}
		}
		if(index==-1)
			return;
		int lastIndex;
		for(lastIndex=index;lastIndex<displayItems.size();lastIndex++){
			if(!displayItems.get(lastIndex).parentID.equals(n.id))
				break;
		}
		displayItems.subList(index, lastIndex).clear();
		adapter.notifyItemRangeRemoved(index, lastIndex-index);
	}

	@Override
	protected boolean needDividerForExtraItem(View child, View bottomSibling, RecyclerView.ViewHolder holder, RecyclerView.ViewHolder siblingHolder){
		return super.needDividerForExtraItem(child, bottomSibling, holder, siblingHolder) || (siblingHolder!=null && siblingHolder.getAbsoluteAdapterPosition()>=adapter.getItemCount());
	}

	@Override
	public Uri getWebUri(Uri.Builder base){
		return base.path("/pending-mentions").build();
	}
}
