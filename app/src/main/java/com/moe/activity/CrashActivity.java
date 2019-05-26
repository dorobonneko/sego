package com.moe.activity;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.content.Intent;
import android.text.method.ArrowKeyMovementMethod;
import android.net.Uri;
import android.content.ClipboardManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;
import com.moe.sego.R;

public class CrashActivity extends Activity
{

	StringBuilder sb;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		TextView tv=new TextView(this);
		tv.setText(getIntent().getStringExtra(Intent.EXTRA_TEXT));
		sb= new StringBuilder(tv.getText());
		tv.setMovementMethod(new ArrowKeyMovementMethod());
		tv.setFitsSystemWindows(true);
		setContentView(tv);
		tv.setTextIsSelectable(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuItem item=menu.add(0,0,0,"发送给开发者");
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		((ClipboardManager)getSystemService(CLIPBOARD_SERVICE)).setText(sb.insert(0, "@千羽樱 ").toString());
		Intent intent=new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("market://details?id="+getPackageName()));
		try{startActivity(Intent.createChooser(intent,"chooser"));}catch(Exception e){
			Toast.makeText(getBaseContext(),"Not Activity Found",Toast.LENGTH_SHORT).show();
		}
		return true;
	}
	
}
