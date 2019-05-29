package com.moe.internal;
import android.content.Context;

public class Api
{
	public static final String HOST_1="https://www.qipa20.com";
	public static String getHost(Context context){
		return HOST_1;
	}
	public static String getVideo(Context context){
		return getHost(context).concat("/videos");
	}
	public static String getHD(Context context){
		return getHost(context).concat("/hd");
	}
	public static String getTags(Context context){
		return getHost(context).concat("/tags");
	}
	public static String getCategories(Context context){
		return getHost(context).concat("/categories");
	}
}
