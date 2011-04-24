package com.orderlong.netTrafficStats;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class NetTrafficStats extends FragmentActivity
{
	public static final String tag="NetTrafficStats Main";
	
	//public AppListAdapter adapter;
	//public ListView appListView;
	
	
    /** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		// showLoading();
		// adapter=new AppListAdapter(NetTrafficStats.this);
		// initLoader();
		// bindListAdapter();

		if(savedInstanceState==null){
		FragmentManager fm = this.getSupportFragmentManager();
		//final AppInfoFragment fmInfo = new AppInfoFragment();
		//fm.beginTransaction().add(R.id.fmappinfo, fmInfo).commit();

		AppListFragment appList = new AppListFragment();
		fm.beginTransaction().add(R.id.fmapplist, appList).commit();
		}


	}
    
    
	@Override
	public boolean onCreatePanelMenu(int featureId, Menu menu) {
		MenuInflater inflater=this.getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return super.onCreatePanelMenu(featureId, menu);
	}
    
	
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

		switch(item.getItemId()){
		case R.id.menu_about:
			showAbout();
			return true;
		default:
			return super.onMenuItemSelected(featureId, item);
		}
	}



	private void showAbout() {
		AlertDialog builder=new AlertDialog.Builder(this).setIcon(R.drawable.about).setTitle(R.string.about).setMessage(R.string.about_content).show();
		
	}


	public static class AppListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<AppInfoItem>>
	{
		public AppListAdapter mAdapter;
		private ProgressDialog loadDialog;
		

		@Override
		public void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			setRetainInstance(true);
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onActivityCreated(savedInstanceState);
			showLoading();
			mAdapter=new AppListAdapter(this.getActivity());
			
			this.setListAdapter(mAdapter);
			
			getLoaderManager().initLoader(0, null, this);
		}


		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {

			AppInfoItem appInfo=(AppInfoItem)l.getItemAtPosition(position);
			/*
			if(this.itemSelectedListener!=null)
			{
				this.itemSelectedListener.onItemSelected(appInfo);
			}*/
			showAppInfo(appInfo);
		}
		private void showLoading()
	    {
	    	if(loadDialog==null)
	    	{
	    		loadDialog=new ProgressDialog(this.getActivity());
	    		loadDialog.setMessage("loading...");
	    		loadDialog.setIndeterminate(true);
	    		loadDialog.setCancelable(false);
	    		loadDialog.show();
	    	}
	    }
		void showAppInfo(AppInfoItem item)
		{
			if(item!=null)
			{
				
				AppInfoFragment fmAppInfo=AppInfoFragment.newInstance(item);
				FragmentManager fmManager=this.getFragmentManager();
				
				FragmentTransaction ft=fmManager.beginTransaction();
				ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
				if(fmManager.findFragmentById(R.id.fmappinfo)!=null)
				{
					AppInfoFragment fmold=(AppInfoFragment)fmManager.findFragmentById(R.id.fmappinfo);
					if(!fmold.isHidden()){
						ft.hide(fmold);
					}
				}
				ft.replace(R.id.fmappinfo, fmAppInfo);
				if(fmAppInfo.isHidden())
				{
					ft.show(fmAppInfo);
				}
				
				ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				ft.commit();
			}
		}



		public Loader<List<AppInfoItem>> onCreateLoader(int id, Bundle arg1) {
			Log.d(tag, "on loader created");
			
			AsyncAppInfoLoader asyncTaskLoader=new AsyncAppInfoLoader(this.getActivity());
			asyncTaskLoader.setUpdateThrottle(2000);
			return asyncTaskLoader;
		}

		public void onLoadFinished(Loader<List<AppInfoItem>> loader,
				List<AppInfoItem> list) {
			Log.d(tag, "on loader Finised");
			mAdapter.setList(list);
			mAdapter.notifyDataSetChanged();
			if(loadDialog!=null && loadDialog.isShowing())
			{
				loadDialog.dismiss();
			}
			
		}

		public void onLoaderReset(Loader<List<AppInfoItem>> loader) {
			Log.d(tag, "on loader Reset");
		}
		

	}
	
	public static class AppInfoFragment extends Fragment
	{
		private TextView txtAppName;
		private ImageView imgAppIcon;
		private TextView txtRxBytes;
		private TextView txtTxBytes;
		private TextView txtmoreapp;

		private AppInfoItem appItem;
		
		static AppInfoFragment newInstance(AppInfoItem item)
		{
			AppInfoFragment  appInfo=new AppInfoFragment();
			appInfo.setDdata(item);
			return appInfo;
		}
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			setRetainInstance(true);
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View v= inflater.inflate(R.layout.appinfo_layout, container,false);
			txtAppName=(TextView)v.findViewById(R.id.appinfo_name);
			imgAppIcon=(ImageView)v.findViewById(R.id.appinfo_icon);
			txtRxBytes=(TextView)v.findViewById(R.id.txt_rxbytes);
			txtTxBytes=(TextView)v.findViewById(R.id.txt_txbytes);
			txtmoreapp=(TextView)v.findViewById(R.id.moreapp);
			if(this.appItem!=null)
			{
				txtAppName.setText(appItem.getName());
				imgAppIcon.setImageDrawable(appItem.getIcon());
				txtRxBytes.setText("已接收:"+appItem.getRxBytesFormat());
				txtTxBytes.setText("已发送:"+appItem.getTxBytesFormat());
				if(appItem.getSubItem().size()>0)
				{
					ArrayList<AppInfoItem> moreapp=(ArrayList<AppInfoItem>) appItem.getSubItem();
					for(AppInfoItem subItem :moreapp)
					{
						txtmoreapp.append(subItem.getName()+"\n");
					}
				}
			}
			return v;
		}
		
		
		public void setDdata(AppInfoItem item)
		{
			this.appItem=item;
		}
		
		
		
		public void loadData(AppInfoItem appInfo)
		{
			this.txtAppName.setText(appInfo.getName());
			this.imgAppIcon.setImageDrawable(appInfo.getIcon());
		}
		
	}
	
}
