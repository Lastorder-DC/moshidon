package org.joinmastodon.android.fragments.onboarding;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.recyclerview.widget.RecyclerView;

import org.joinmastodon.android.R;
import org.joinmastodon.android.api.session.AccountSessionManager;
import org.joinmastodon.android.model.Instance;
import org.joinmastodon.android.ui.BetterItemAnimator;
import org.joinmastodon.android.ui.utils.UiUtils;

import me.grishka.appkit.utils.MergeRecyclerAdapter;
import me.grishka.appkit.utils.SingleViewRecyclerAdapter;
import me.grishka.appkit.utils.V;
import me.grishka.appkit.views.UsableRecyclerView;

public class CustomWelcomeFragment extends InstanceCatalogFragment {
	/** This build only ever talks to our own server, so there's nothing to search or choose. */
	private static final String OCCM_DOMAIN="occm.cc";

	@Override
	public void onAttach(Context context){
		super.onAttach(context);
		setRefreshEnabled(false);
	}

	public CustomWelcomeFragment() {
		super(R.layout.fragment_welcome_custom, 1);
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		// There's no search box in this build to ever set this, but
		// InstanceCatalogFragment.getCurrentSearchQuery()/loadInstanceInfo() unconditionally
		// dereference it, so it has to be non-null from the start.
		currentSearchQuery="";
		dataLoaded();
	}

	@Override
	protected void onUpdateToolbar(){
		super.onUpdateToolbar();

		if (!canGoBack()) {
			TextView toolbarLogo=new TextView(getActivity());
			toolbarLogo.setText(R.string.mo_app_name);
			toolbarLogo.setTextAppearance(R.style.m3_title_medium);
			toolbarLogo.setTextColor(UiUtils.getThemeColor(getActivity(), android.R.attr.textColorPrimary));

			FrameLayout logoWrap=new FrameLayout(getActivity());
			FrameLayout.LayoutParams logoParams=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
			logoParams.setMargins(0, V.dp(2), 0, 0);
			logoWrap.addView(toolbarLogo, logoParams);

			getToolbar().addView(logoWrap, new Toolbar.LayoutParams(Gravity.CENTER));
		} else {
			setTitle(R.string.add_account);
		}
	}

	@Override
	protected void proceedWithAuthOrSignup(Instance instance) {
		AccountSessionManager.getInstance().authenticate(getActivity(), instance);
	}

	@Override
	protected void updateFilteredList(){
		// No search box in this build - nothing to filter.
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		view.setBackgroundColor(UiUtils.getThemeColor(getActivity(), R.attr.colorM3Surface));
		list.setItemAnimator(new BetterItemAnimator());
		((UsableRecyclerView) list).setSelector(null);
		nextButton.setText(R.string.log_in);
		nextButton.setEnabled(true);
		((me.grishka.appkit.FragmentStackActivity) getActivity()).invalidateSystemBarColors(this);
	}

	@Override
	protected void onNextClick(View v){
		showProgressDialog();
		loadInstanceInfo(OCCM_DOMAIN, false);
	}

	@Override
	protected void doLoadData(int offset, int count) {}

	@Override
	protected RecyclerView.Adapter<?> getAdapter(){
		View headerView=getActivity().getLayoutInflater().inflate(R.layout.header_welcome_custom, list, false);

		mergeAdapter=new MergeRecyclerAdapter();
		mergeAdapter.addAdapter(new SingleViewRecyclerAdapter(headerView));
		// InstanceCatalogFragment.loadInstanceInfo()'s success callback can call
		// adapter.notifyItem*() directly (normally the instance-list sub-adapter); there's no
		// such list in this build, so point it at something real to avoid an NPE if that ever runs.
		adapter=mergeAdapter;
		return mergeAdapter;
	}
}
