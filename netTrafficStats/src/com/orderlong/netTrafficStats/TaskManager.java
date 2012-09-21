package com.orderlong.netTrafficStats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.ContentObservable;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class TaskManager {
	private Context context = null;
	private boolean isRunning = false;
	// private long totalRxBytes;
	// private long lastTotalRxBytes=-100000L;
	protected static final int MSG_BYTES_CHANGED = 0x101;
	// MyFileObserver myFileObserver;

	private boolean AppListLoaded=false;
	List<AppInfoItem> allAppItem;
	ContentObservable mContentObservable = new ContentObservable();

	public static TaskManager instance;
	
	public static synchronized TaskManager getInstance(Context context)
	{
		if(instance==null)
		{
			instance=new TaskManager(context);
		}
		return instance;
	}
	public TaskManager(Context context) {
		this.context = context;
		// myFileObserver=new MyFileObserver("/sys/class/net/");
		// myFileObserver.startWatching();
		// mHandler.post(new CheckChangeTread());
		Log.d("TaskManager", "TaskManager init");
		//allAppItem=getAppInfoList();
	}
	
	public void loadAppList()
	{
		Log.d("TaskManager", "TaskManager LoadAppList");
		allAppItem=getAppInfoList();
	}

	public void checkchange() {
		if (!isRunning) {
			isRunning = true;
			new Thread(new CheckChangeTread()).start();
		}
	}

	public boolean IsLoadedAppList()
	{
		return this.AppListLoaded;
	}
	public void registerContentObserver(ContentObserver observer) {
		mContentObservable.unregisterAll();
		mContentObservable.registerObserver(observer);
	}

	public void unregisterContentObserver() {
		mContentObservable.unregisterAll();
	}

	public List<AppInfoItem> updateList()
	{
		return updateList(allAppItem);
	}
	public List<AppInfoItem> updateList(List<AppInfoItem> appList) {
		List<AppInfoItem> newappList=new ArrayList<AppInfoItem>();
		for (AppInfoItem item : appList) {
			item.setRxBytes(NetTraffic.getUidRxBytes(item.getUid()));
			item.setTxBytes(NetTraffic.getUidTxBytes(item.getUid()));
			newappList.add(item);
		}
		Collections.sort(newappList);
		return newappList;
	}

	public List<AppInfoItem> getAppInfoList() {

		List<AppInfoItem> appList = new ArrayList<AppInfoItem>();

		final PackageManager packageManager=context.getPackageManager();
		//List<ResolveInfo> mApps = context.getPackageManager()
			//	.queryIntentActivities(intent, 0);

		final List<ApplicationInfo> mApps=packageManager.getInstalledApplications(0);
		//for (ResolveInfo info : mApps) {
		for (ApplicationInfo info : mApps) {

				if(context.getPackageManager().checkPermission("android.permission.INTERNET", info.packageName)==0){
						//AppInfoItem item = new AppInfoItem();
						AppInfoItem item;
						item=getAppInfoItembyUid(info.uid,appList);
						if(item!=null)
						{
							//item.setName(item.getName()+" | "+info.loadLabel(packageManager).toString());
							AppInfoItem subitem=new AppInfoItem();
							subitem.setIcon(info.loadIcon(context.getPackageManager()));
							subitem.setName(info
									.loadLabel(context.getPackageManager())
									.toString());
							subitem.setUid(info.uid);
							item.addSubItems(subitem);
						}
						else
						{
							item=new AppInfoItem();
							item.setIcon(info.loadIcon(context.getPackageManager()));
							item.setName(info
									.loadLabel(context.getPackageManager())
									.toString());
							item.setUid(info.uid);
							appList.add(item);
						}
						
				}
		}
		//add special app
		appList.add(new AppInfoItem(android.os.Process.getUidForName("root"),"(root)权限运行的程序"));
		appList.add(new AppInfoItem(android.os.Process.getUidForName("media"),"Media sever"));
		appList.add(new AppInfoItem(android.os.Process.getUidForName("vpn"),"VPN网络"));
		appList.add(new AppInfoItem(android.os.Process.getUidForName("shell"),"Linux shell"));
		checkchange();
		this.AppListLoaded=true;
		return appList;
	}
	
	public AppInfoItem getAppInfoItembyUid(int uid,List<AppInfoItem> applist)
	{
		if(applist!=null)
		{
			for(AppInfoItem app :applist)
			{
				if(app.getUid()==uid)
					return app;
			}
		}
		return null;
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_BYTES_CHANGED:
				mContentObservable.notifyChange(true);
				break;
			}
			super.handleMessage(msg);
		}
	};

	final private class CheckChangeTread implements Runnable {

		private long totalRxBytes;
		private long lastTotalRxBytes = -100000L;

		public void run() {
			while (true) {
				try {
					//Log.d("TaskManager", "changeThread running");
					Thread.sleep(1000);
					totalRxBytes = NetTraffic.getTotalRxBytes();
					if (totalRxBytes > lastTotalRxBytes) {
						lastTotalRxBytes = totalRxBytes;
						Message msg = new Message();
						msg.what = MSG_BYTES_CHANGED;
						mHandler.sendMessage(msg);
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}

	}

}
