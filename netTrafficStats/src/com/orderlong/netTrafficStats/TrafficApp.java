package com.orderlong.netTrafficStats;

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import android.app.Application;
import android.content.SharedPreferences;

public class TrafficApp extends Application {

	
	private List<Integer> savedUids_wifi=new LinkedList<Integer>();
	private List<Integer> savedUids_3g=new LinkedList<Integer>();
	@Override
	public void onCreate()
	{
		final SharedPreferences prefs=this.getSharedPreferences(ofirewallApi.PREFS_NAME, 0);
		final String uids_wifi=prefs.getString(ofirewallApi.PREF_WIFI_UIDS, "");
		final String uids_3g=prefs.getString(ofirewallApi.PREF_3G_UIDS, "");
		
		if(uids_wifi.length()>0)
		{
			final StringTokenizer tok=new StringTokenizer(uids_wifi,"|");
			while(tok.hasMoreTokens())
			{
				final String uid=tok.nextToken();
				if(!uid.equals(""))
				{
					try
					{
					savedUids_wifi.add(Integer.parseInt(uid));
					}
					catch(Exception ex){
						
					}
				}
			}
		}
		
		if(uids_3g.length()>0)
		{
			final StringTokenizer tok=new StringTokenizer(uids_3g,"|");
			while(tok.hasMoreTokens())
			{
				final String uid=tok.nextToken();
				if(!uid.equals(""))
				{
					try
					{
						savedUids_3g.add(Integer.parseInt(uid));
					}
					catch(Exception ex){
						
					}
				}
			}
		}
	}
	
	
	public void addtowifiList(int uid)
	{
		if(savedUids_wifi.indexOf(uid)==-1)
		{
			savedUids_wifi.add(uid);
		}
	}
	
	public void removefromwifiList(int uid)
	{
		int index=savedUids_wifi.indexOf(uid);
		if(index!=-1)
		{
			savedUids_wifi.remove(index);
		}
	}
	public boolean isWifienable(int uid)
	{
		if(savedUids_wifi.indexOf(uid)==-1)
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
	
	public void addto3gList(int uid)
	{
		if(savedUids_3g.indexOf(uid)==-1)
		{
			savedUids_3g.add(uid);
		}
	}
	
	public void removefrom3gList(int uid)
	{
		int index=savedUids_3g.indexOf(uid);
		if(index!=-1)
		{
			savedUids_3g.remove(index);
		}
	}
	
	public boolean is3genable(int uid)
	{
		if(savedUids_3g.indexOf(uid)==-1)
		{
			return false;
		}
		else
		{
			return true;
		}
	}
	
	
	public List<Integer> getSavedUids_wifi()
	{
		return savedUids_wifi;
	}
	
	public List<Integer> getSavedUids_3g()
	{
		return savedUids_3g;
	}
	
}
