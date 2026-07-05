package org.joinmastodon.android.updater;

import android.app.Activity;
import android.content.Intent;


public abstract class GithubSelfUpdater{
	private static GithubSelfUpdater instance;
	public static boolean forceUpdate;

	public static GithubSelfUpdater getInstance(){
		if(instance==null){
			try{
				Class<?> c=Class.forName("org.joinmastodon.android.updater.GithubSelfUpdaterImpl");
				instance=(GithubSelfUpdater) c.newInstance();
			}catch(IllegalAccessException|InstantiationException|ClassNotFoundException ignored){
			}
		}
		return instance;
	}

	public static boolean needSelfUpdating(){
		// Disabled: this checked LucasGGamerM/moshidon(-nightly) upstream releases, which are a
		// different app entirely and always "newer" than our own version dates - always showing a
		// bogus update prompt. Re-enable once this fork has its own release pipeline to point at,
		// or leave disabled for good once distributed via Play Store (which handles updates itself).
		return false;
	}

	public abstract void checkForUpdates();

	public abstract void maybeCheckForUpdates();

	public abstract GithubSelfUpdater.UpdateState getState();

	public abstract GithubSelfUpdater.UpdateInfo getUpdateInfo();

	public abstract void downloadUpdate();

	public abstract void installUpdate(Activity activity);

	public abstract float getDownloadProgress();

	public abstract void cancelDownload();

	public abstract void handleIntentFromInstaller(Intent intent, Activity activity);

	public abstract void reset();

	public enum UpdateState{
		NO_UPDATE,
		CHECKING,
		UPDATE_AVAILABLE,
		DOWNLOADING,
		DOWNLOADED
	}

	public static class UpdateInfo{
		public String changelog;
		public String version;
		public long size;
	}
}
