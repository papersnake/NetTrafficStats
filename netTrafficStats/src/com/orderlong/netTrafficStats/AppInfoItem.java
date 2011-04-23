package com.orderlong.netTrafficStats;


import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.Drawable;

public class AppInfoItem implements Comparable<AppInfoItem>{

	private Drawable icon;
	private String name;
	private String packagename;
	private int uid;
	private long rxBytes;
	private long txBytes;
	private long oldrxBytes;
	private ArrayList<AppInfoItem> subItems=new ArrayList<AppInfoItem>();
	
	public AppInfoItem(){
		
	}
	
	public AppInfoItem(int muid,String appname)
	{
		this.uid=muid;
		this.name=appname;
	}
	
	public Drawable getIcon()
	{
		return this.icon;
	}
	
	public void setIcon(Drawable drawable)
	{
		this.icon=drawable;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public void setName(String appName)
	{
		this.name=appName;
	}
	
	public String getPackageName()
	{
		return this.packagename;
	}
	
	public void setPackageName(String appPackageName)
	{
		this.packagename=appPackageName;
	}
	
	public int getUid()
	{
		return this.uid;
	}
	
	public void setUid(int appUid)
	{
		this.uid=appUid;
	}
	
	public long getRxBytes()
	{
		return this.rxBytes;
	}
	
	public void setRxBytes(long appRxBytes)
	{
		if(appRxBytes==-1)
			appRxBytes=0;
		oldrxBytes=this.rxBytes;
		this.rxBytes=appRxBytes;
	}
	
	public long getTxBytes()
	{
		return this.txBytes;
	}
	
	public long getSpeed()
	{
		return this.rxBytes-oldrxBytes;
	}
	
	public void setTxBytes(long appTxBytes)
	{
		this.txBytes=appTxBytes;
	}
	
	public void addSubItems(AppInfoItem item)
	{
		this.subItems.add(item);
	}
	
	public List<AppInfoItem> getSubItem()
	{
		return this.subItems;
	}
	public String getTxBytesFormat()
	{
		return formatBytes(this.txBytes);
	}
	public String getRxBytesFormat()
	{
		return formatBytes(this.rxBytes);
	}

	private String formatBytes(long bytes) {
		if(bytes>1048576)
		{
			double result=(double)bytes/1048576;
			return String.format("%.2fM", result);
			//return "0K";
		} else if (bytes>0)
		{
			double result=(double)bytes/1024;
			return String.format("%.2fK", result);
		}else {
			return "0K";
		}
	}

	public int compareTo(AppInfoItem another) {
		if(this.getSpeed()!=0)
		{
			if(this.getSpeed()>another.getSpeed())
			{
				return -1;
			}
			else
			{
				return 1;
			}
		}
		if(this.rxBytes>=another.rxBytes){
			return -1;
		}
		else
		{
			return 1;
		}
	}
}
