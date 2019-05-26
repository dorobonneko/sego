package com.moe.activity;
import android.content.*;
import android.media.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.net.*;
import org.json.*;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import com.moe.activity.VideoActivity;
import com.moe.internal.Api;
import com.moe.sego.R;
import com.moe.tinyimage.Pussy;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;
import com.moe.widget.MediaController;
import android.graphics.SurfaceTexture;

public class VideoActivity extends Activity implements View.OnClickListener,Toolbar.OnMenuItemClickListener
{
	private View play,play_button;
	private TextureView mVideoView;
	private AudioManager mAudioManager;
	private String[][] medias;
	private MediaController mMediaController;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		//View v=getWindow().getDecorView();
		//v.setSystemUiVisibility((~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)|v.getSystemUiVisibility());
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_view);
		Pussy.get(this).load(getIntent().getStringExtra("preview")).into((ImageView)findViewById(R.id.preview));
		Toolbar toolbar=((Toolbar)findViewById(R.id.toolbar));
		toolbar.setNavigationIcon(R.drawable.arrow_left);
		toolbar.setNavigationOnClickListener(this);
		toolbar.inflateMenu(R.menu.video_menu);
		toolbar.setOnMenuItemClickListener(this);
		play_button=findViewById(R.id.playButton);
		play_button.setOnClickListener(this);
		play=findViewById(R.id.play);
		play.setOnClickListener(this);
		play_button.setVisibility(View.INVISIBLE);
		play.setVisibility(View.INVISIBLE);
		mVideoView=(TextureView) findViewById(R.id.videoview);
		mMediaController=(MediaController) findViewById(R.id.mediacontroller);
		mMediaController.setDisplay(mVideoView);
		mAudioManager=(AudioManager) getSystemService(AUDIO_SERVICE);
		//mAudioManager.requestAudioFocus(new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).build());
		load(getIntent().getDataString());
	}
	private void load(final String url){
		new Thread(){
			public void run(){
				try
				{
					Document doc=Jsoup.connect(url).get();
					Elements sources=doc.select("video#vjsplayer>source");
					medias=new String[sources.size()][2];
					for(int i=0;i<medias.length;i++){
						Element source=sources.get(i);
						medias[i][0]=source.absUrl("src");
						medias[i][1]=source.attr("label");
					}
					runOnUiThread(new Runnable(){

							@Override
							public void run()
							{
								onLoadSuccess();
							}
						});
				}
				catch (IOException e)
				{}

			}
		}.start();
	}
	private void onLoadSuccess(){
		play.setVisibility(View.VISIBLE);
		play_button.setVisibility(View.VISIBLE);
		try
		{
			mMediaController.setQuality(medias);
			mMediaController.prepare(medias.length-1,false);
		}
		catch (SecurityException e)
		{}
		catch (IllegalStateException e)
		{}
		catch (IllegalArgumentException e)
		{}
		
	}
	@Override
	public void onClick(View p1)
	{
		switch(p1.getId()){
			case -1:
				finish();
				break;
			case R.id.play:
			case R.id.playButton:
				play.setVisibility(View.INVISIBLE);
				((View)mVideoView.getParent().getParent()).setVisibility(View.VISIBLE);
				mMediaController.start();
				break;
		}
	}

	

	@Override
	public void onBackPressed()
	{
		if(!mMediaController.hideView()){
		mVideoView.setVisibility(View.GONE);
		super.onBackPressed();
		}
	}

	@Override
	public boolean onMenuItemClick(MenuItem p1)
	{
		switch(p1.getItemId()){
			case R.id.third_play:
				if(medias==null){
					Toast.makeText(getApplicationContext(),"视频解析中...",Toast.LENGTH_SHORT).show();
				}else{
					String[] bands=new String[medias.length];
					for(int i=0;i<medias.length;i++){
						bands[i]=medias[i][1];
					}
					new AlertDialog.Builder(VideoActivity.this).setTitle("选择清晰度").setItems(bands, new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface p1, int p2)
							{
								try{startActivity(new Intent(Intent.ACTION_VIEW).setDataAndType(Uri.parse(medias[p2][0]),"video/*"));}catch(Exception e){Toast.makeText(getApplicationContext(),"没有可用的第三方查询",Toast.LENGTH_SHORT).show();}
							}
						}).show();
				}
				break;
		}
		return true;
	}

	
	@Override
	protected void onDestroy()
	{
		mMediaController.getMediaPlayer().release();
		super.onDestroy();
	}





	







	
	
}
