package com.orderlong.netTrafficStats;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import android.widget.Toast;
import android.widget.ToggleButton;


import com.gfan.sdk.statistics.Collector;

public class NetTrafficStats extends FragmentActivity
{
	public static final String tag="NetTrafficStats Main";
	
	//public AppListAdapter adapter;
	//public ListView appListView;
	
	AppListFragment appList;
    /** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		checkPreferences();
		setContentView(R.layout.main);
		Collector.setAppClickCount("App Starting");
		if(savedInstanceState==null){
		FragmentManager fm = this.getSupportFragmentManager();
		appList=(AppListFragment)fm.findFragmentByTag("iswork");
		if(appList==null){
			appList = new AppListFragment();
			fm.beginTransaction().add(R.id.fmapplist, appList,"iswork").commit();
		}

		//AppListFragment appList = new AppListFragment();
		//fm.beginTransaction().add(R.id.fmapplist, appList).commit();
		}


	}
    
    
	@Override
	protected void onPause() {
		Collector.onPause(this);
		super.onPause();
	}


	@Override
	protected void onResume() {
		Collector.onResume(this);
		super.onResume();
	}


	@Override
	public boolean onCreatePanelMenu(int featureId, Menu menu) {
		MenuInflater inflater=this.getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return super.onCreatePanelMenu(featureId, menu);
	}
    
	
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		final MenuItem item_onoff=menu.findItem(R.id.menu_disable);
		//final MenuItem item_apply=menu.findItem(R.id.menu_apply);
		final boolean enabled=ofirewallApi.isEnabled(this);
		if(enabled)
		{
			item_onoff.setIcon(android.R.drawable.button_onoff_indicator_on);
			item_onoff.setTitle(R.string.menu_fw_enable);
			
		} else {
			item_onoff.setIcon(android.R.drawable.button_onoff_indicator_off);
			item_onoff.setTitle(R.string.menu_fw_disable);
		}
		return super.onPrepareOptionsMenu(menu);
	}


	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

		switch(item.getItemId()){
		case R.id.menu_about:
			showAbout();
			return true;
		case R.id.menu_disable:
			disableOrEnable();
			return true;
		case R.id.menu_apply:
			applyRules();
			return true;
		case R.id.menu_showrules:
			showRules();
			return true;
		default:
			return super.onMenuItemSelected(featureId, item);
		}
	}
	
	private void showRules() {
    	final Resources res = getResources();
		final ProgressDialog progress = ProgressDialog.show(this, res.getString(R.string.working), res.getString(R.string.working), true);
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
    			try {progress.dismiss();} catch(Exception ex){}
				if (!ofirewallApi.hasRootAccess(NetTrafficStats.this, true)) return;
				ofirewallApi.showIptablesRules(NetTrafficStats.this);
			}
		};
		handler.sendEmptyMessageDelayed(0, 100);
	}

	private void disableOrEnable()
	{
		final boolean enabled = !ofirewallApi.isEnabled(this);
		Log.d(tag, "放火墙状态："+enabled);
		ofirewallApi.setEnabled(this, enabled);
		if(enabled)
		{
			applyRules();
		}
		else
		{
			purgeRulles();
		}
	}
	
	private void applyRules(){
		final Resources res=getResources();
		final boolean enabled=ofirewallApi.isEnabled(this);
		final ProgressDialog progress=ProgressDialog.show(this, res.getString(R.string.working), res.getString(R.string.applyrules),true);
		
		final Handler handler=new Handler()
		{
			public void handleMessage(Message msg){
				try{progress.dismiss();}catch(Exception ex){}
				if(enabled)
				{
					Log.d(tag,"应用规则...");
					if(ofirewallApi.hasRootAccess(NetTrafficStats.this, true) && ofirewallApi.applyIptablesRules(NetTrafficStats.this,true)){
						Toast.makeText(NetTrafficStats.this, R.string.rulesapplied, Toast.LENGTH_SHORT).show();
					}
					else
					{
						ofirewallApi.setEnabled(NetTrafficStats.this, false);
					}
				}
			}
		};
		handler.sendEmptyMessageDelayed(0, 100);
	}
	
	private void purgeRulles()
	{
		final Resources res = getResources();
		final ProgressDialog progress = ProgressDialog.show(this, res.getString(R.string.working), res.getString(R.string.deletingrules), true);
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
    			try {progress.dismiss();} catch(Exception ex){}
				if (!ofirewallApi.hasRootAccess(NetTrafficStats.this, true)) return;
				if (ofirewallApi.purgeIptables(NetTrafficStats.this, true)) {
					Toast.makeText(NetTrafficStats.this, R.string.rulesdeleted, Toast.LENGTH_SHORT).show();
				}
			}
		};
		handler.sendEmptyMessageDelayed(0, 100);
	}
	
	private void checkPreferences()
	{
		final SharedPreferences prefs =this.getSharedPreferences(ofirewallApi.PREFS_NAME, 0);
		final Editor editor=prefs.edit();
		boolean changed=false;
		
		if(prefs.getString(ofirewallApi.PREF_MODE, "").length()==0)
		{
			editor.putString(ofirewallApi.PREF_MODE, ofirewallApi.MODE_BLACKLIST);
			changed=true;
		}
		if (prefs.contains("AllowedUids")) {
    		editor.remove("AllowedUids");
    		changed = true;
    	}
    	if (prefs.contains("Interfaces")) {
    		editor.remove("Interfaces");
    		changed = true;
    	}
    	if (changed) editor.commit();
	}


	private void toggleLogEnabled() {
		final SharedPreferences prefs = getSharedPreferences(ofirewallApi.PREFS_NAME, 0);
		final boolean enabled = !prefs.getBoolean(ofirewallApi.PREF_LOGENABLED, false);
		final Editor editor = prefs.edit();
		editor.putBoolean(ofirewallApi.PREF_LOGENABLED, enabled);
		editor.commit();
		if (ofirewallApi.isEnabled(this)) {
			ofirewallApi.applySavedIptablesRules(this, true);
		}
		Toast.makeText(NetTrafficStats.this, (enabled?R.string.log_was_enabled:R.string.log_was_disabled), Toast.LENGTH_SHORT).show();
	}
	
	
	private void showAbout() {
		new AlertDialog.Builder(this).setIcon(R.drawable.about).setTitle(R.string.about).setMessage(R.string.about_content).show();
		
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
			mAdapter=new AppListAdapter(this.getActivity());
			
			this.setListAdapter(mAdapter);
			
			getLoaderManager().initLoader(0, null, this);
			
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onActivityCreated(savedInstanceState);
			
			
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
			showLoading();
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
				try{
					loadDialog.dismiss();
				}
				catch(Exception ex){}
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
		private ToggleButton btnwifiswitch;
		private ToggleButton btn3gswitch;

		private TrafficApp app;
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
			app=(TrafficApp)this.getActivity().getApplication();
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
			btnwifiswitch=(ToggleButton)v.findViewById(R.id.btnwifiswitch);
			btn3gswitch=(ToggleButton)v.findViewById(R.id.btn3gswitch);
			
			
			
			
			
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
				
				boolean wifichecked=app.isWifienable(appItem.getUid());
				btnwifiswitch.setChecked(wifichecked);
				btnwifiswitch.setOnClickListener(new OnClickListener(){

					public void onClick(View v) {
						if(((ToggleButton)v).isChecked())
						{
							app.addtowifiList(appItem.getUid());
						}
						else
						{
							app.removefromwifiList(appItem.getUid());
						}
						
					}
					
				});
				
				
				boolean gchecked=app.is3genable(appItem.getUid());
				btn3gswitch.setChecked(gchecked);
				btn3gswitch.setOnClickListener(new OnClickListener(){

					public void onClick(View v) {
						if(((ToggleButton)v).isChecked())
						{
							app.addto3gList(appItem.getUid());
						}
						else
						{
							app.removefrom3gList(appItem.getUid());
						}
						
					}
					
				});
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
