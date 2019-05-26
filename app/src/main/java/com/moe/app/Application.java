package com.moe.app;

import android.os.*;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.annotation.WorkerThread;
import com.moe.activity.CrashActivity;
import com.moe.sego.BuildConfig;
import java.io.File;
import java.net.HttpURLConnection;
import javax.net.ssl.HttpsURLConnection;

public class Application extends android.app.Application implements Thread.UncaughtExceptionHandler
{
	@Override
	public void uncaughtException(final Thread p1,final Throwable p2)
	{
		new Thread(){
			public void run(){
				if(p2!=null){
				StringBuilder sb=new StringBuilder();
				if(p2.getMessage()!=null)sb.append(p2.getMessage());
				try
				{
					sb.append("\n").append(getPackageManager().getPackageInfo(getPackageName(), 0).versionName).append("\n").append(Build.MODEL).append(" ").append(Build.VERSION.RELEASE).append("\n");
				}
				catch (PackageManager.NameNotFoundException e)
				{}
				for (StackTraceElement element:p2.getStackTrace())
					sb.append("\n").append(element.toString());
				Intent intent=new Intent(getBaseContext(),CrashActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra(Intent.EXTRA_TEXT,sb.toString());
				startActivity(intent);
				}
				android.os.Process.killProcess(android.os.Process.myPid());
			}
		}.start();
		
	}
	
	@Override
	public void onCreate()
	{
		Thread.setDefaultUncaughtExceptionHandler(this);
		super.onCreate();
	}

	
}
