package com.orderlong.netTrafficStats;

import java.util.List;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;

public class AsyncAppInfoLoader extends AsyncTaskLoader<List<AppInfoItem>>
{
	final Loader<List<AppInfoItem>>.ForceLoadContentObserver mObserver;
	TaskManager taskManager;
	//private Context mContext;
	List<AppInfoItem> mData;
	
	public AsyncAppInfoLoader(Context context) {
		super(context);
		//mContext=context;
		//taskManager=new TaskManager(context);
		taskManager=TaskManager.getInstance(context);
		this.mObserver=new Loader.ForceLoadContentObserver();
		
		forceLoad();
	}

	
	
	@Override
	public void deliverResult(List<AppInfoItem> data) {
		if(isReset())
		{
			if(data!=null)
			{
				data=null;
			}
			return;
		}
		
		List<AppInfoItem> oldData=this.mData;
		this.mData=data;
		if(isStarted())
		{
			super.deliverResult(data);
		}
		if((oldData!=null) && (oldData!=data)){
			oldData=null;
		}
	}






	@Override
	public List<AppInfoItem> loadInBackground() {
		//Log.d("AsyncAppInfoLoader","running");
		//taskManager=new TaskManager(mContext);
		//taskManager=TaskManager.getInstance(mContext);
		if(!taskManager.IsLoadedAppList())
		{
			taskManager.loadAppList();
		}
		taskManager.registerContentObserver(mObserver);
		return taskManager.updateList();
	}

	
	
	

	@Override
	public void onContentChanged() {
		//Log.d("AsyncAppInfoLoader", "onContentChanged");
		//forceLoad();
		super.onContentChanged();
	}

	
	
}
