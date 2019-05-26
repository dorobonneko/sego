package com.moe.widget;
import android.graphics.*;
import android.os.*;
import android.view.*;
import android.widget.*;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import com.moe.adapter.SpinnerAdapter;
import com.moe.sego.R;
import java.io.IOException;
import android.media.AudioManager;
import android.support.v4.view.ViewCompat;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

public class MediaController extends FrameLayout implements View.OnClickListener,Handler.Callback,SeekBar.OnSeekBarChangeListener,MediaPlayer.OnInfoListener,MediaPlayer.OnVideoSizeChangedListener,MediaPlayer.OnBufferingUpdateListener,TextureView.SurfaceTextureListener,MediaPlayer.OnCompletionListener,MediaPlayer.OnPreparedListener,GestureDetector.OnGestureListener,GestureDetector.OnDoubleTapListener,PopupMenu.OnMenuItemClickListener
{
	private android.widget.ImageView play_pause,full,tips_img;
	private SeekBar mSeekBar;
	private TextView time,quality_display,tips_progress;
	private Handler mHandler;
	private View visible,progressbar,tips_message;
	private MediaPlayer mMediaPlayer;
	private final static int HIDE=0;
	private final static int PROGRESS=1;
	private TextureView mTextureView;
	private boolean fullscreen=false;
	private int orientation,duration;
	private boolean prepared,playing;
	//换成popupmenu
	private PopupMenu quality_popup;
	private String[][] quality;
	private int currentPosition;
	private GestureDetector mGestureDetector;
	private int scrollFlag=-1;
	private final static int SCROLL_VOLUME=0;
	private final static int SCROLL_LIGHT=1;
	private final static int SCROLL_PROGRESS=2;
	private AudioManager mAudioManager;
	private int data;
	private ProgressBar tips_progressbar;
	private boolean intercept;
	public MediaController(Context context,AttributeSet attrs){
		super(context,attrs);
		mAudioManager=(AudioManager) context.getSystemService(context.AUDIO_SERVICE);
		mHandler=new Handler(this);
		LayoutInflater.from(context).inflate(R.layout.media_control_view,this,true);
		visible=findViewById(R.id.control_view);
		play_pause=(android.widget.ImageView) findViewById(R.id.play_pause);
		progressbar=findViewById(R.id.progressbar);
		play_pause.setOnClickListener(this);
		full=(android.widget.ImageView) findViewById(R.id.full);
		full.setOnClickListener(this);
		mSeekBar=(SeekBar) findViewById(R.id.seekbar);
		time=(TextView) findViewById(R.id.time);
		mSeekBar.setOnSeekBarChangeListener(this);
		quality_display=(TextView) findViewById(R.id.quality);
		quality_display.setOnClickListener(this);
		quality_popup=new PopupMenu(context,quality_display);
		quality_popup.setOnMenuItemClickListener(this);
		mGestureDetector=new GestureDetector(context,this);
		mGestureDetector.setIsLongpressEnabled(true);
		mGestureDetector.setOnDoubleTapListener(this);
		tips_message=findViewById(R.id.tips_message);
		tips_img=(android.widget.ImageView) findViewById(R.id.tips_img);
		tips_progress=(TextView) findViewById(R.id.tips_progress);
		tips_progressbar=(ProgressBar) findViewById(R.id.tips_progressbar);
		ViewCompat.setNestedScrollingEnabled(this,false);
	}

	public void setDisplay(TextureView mVideoView)
	{
		this.mTextureView=mVideoView;
		mVideoView.setSurfaceTextureListener(this);
	}
	public void setQuality(String[][] quality){
		this.quality=quality;
		Menu menu=quality_popup.getMenu();
		menu.clear();
		for(int i=0;i<quality.length;i++){
			menu.add(0,i,i,quality[i][1]).setTitleCondensed(quality[i][0]);
		}
		menu.setGroupCheckable(0,true,true);
	}
	public MediaPlayer getMediaPlayer()
	{
		if(mMediaPlayer==null){
			mMediaPlayer=new MediaPlayer();
		mMediaPlayer.setOnInfoListener(this);
		mMediaPlayer.setOnVideoSizeChangedListener(this);
		mMediaPlayer.setOnBufferingUpdateListener(this);
		mMediaPlayer.setOnCompletionListener(this);
		mMediaPlayer.setOnPreparedListener(this);
		}
		return mMediaPlayer;
	}
	public int getDuration(){
		return duration;
	}
	public void prepare(int quality,boolean start){
		if(this.quality==null)throw new IllegalStateException("数据源未初始化");
		try
		{
			quality_display.setText(this.quality[quality][1]);
			quality_popup.getMenu().getItem(quality).setChecked(true);
			setDataSource(this.quality[quality][0]);
			getMediaPlayer().prepareAsync();
		}
		catch (IOException e)
		{}
		catch (IllegalArgumentException e)
		{}
		catch (SecurityException e)
		{}
		catch (IllegalStateException e)
		{}

	}
private void setDataSource(String path) throws SecurityException, IllegalArgumentException, IOException, IllegalStateException{
	getMediaPlayer().setDataSource(path);
}

	@Override
	public void onClick(View p1)
	{
		switch(p1.getId()){
			case R.id.play_pause:
				if(mMediaPlayer.isPlaying())
					pause();
					else
					start();
				show();
				break;
			case R.id.full:
				ViewGroup content=(ViewGroup) getRootView().findViewById(android.R.id.content);
				if(fullscreen){
					hideView();
				}
				else{
					ViewGroup parent=(ViewGroup)(getParent().getParent());
				setTag(parent);
				View group=parent.getChildAt(0);
				parent.removeView(group);
				content.addView(group);
				orientation=((Activity)getContext()).getRequestedOrientation();
				((Activity)getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
				full.setImageResource(R.drawable.fullscreen_exit);
				fullscreen=true;
				}
				hide();
				break;
			case R.id.quality:
				quality_popup.show();
				show();
			break;
		}
	}
	public void show(){
		show(3000);
	}
	public void show(long time){
		visible.setVisibility(VISIBLE);
		Window window=((Activity)getContext()).getWindow();
		window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		mHandler.removeMessages(HIDE);
		mHandler.sendEmptyMessageDelayed(HIDE,time);
		mHandler.sendEmptyMessage(PROGRESS);
	}
	public void hide(){
		show(0);
	}
	public boolean isShown(){
		return visible.getVisibility()==VISIBLE;
	}
	@Override
	public boolean handleMessage(Message p1)
	{
		switch(p1.what){
			case HIDE:
				visible.setVisibility(GONE);
				if(playing){
				Window window=((Activity)getContext()).getWindow();
				window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
				window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
				}
				mHandler.removeMessages(PROGRESS);
				break;
			case PROGRESS:
				try{
				mSeekBar.setProgress(mMediaPlayer.getCurrentPosition());
				invalidateTime();}catch(IllegalStateException e){}
				mHandler.sendEmptyMessageDelayed(PROGRESS,1000);
				break;
		}
		return true;
	}

	@Override
	public void onStartTrackingTouch(SeekBar p1)
	{
		mHandler.removeMessages(HIDE);
		mHandler.removeMessages(PROGRESS);
	}

	@Override
	public void onStopTrackingTouch(SeekBar p1)
	{
		show();
		mMediaPlayer.seekTo(p1.getProgress());
		mHandler.sendEmptyMessage(PROGRESS);
	}

	@Override
	public void onProgressChanged(SeekBar p1, int p2, boolean p3)
	{
		if(p3){
		invalidateTime(p2);
		}
	}
	public void pause(){
		mMediaPlayer.pause();
		Window window=((Activity)getContext()).getWindow();
		window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		playing=false;
		play_pause.setImageResource(R.drawable.play);
	}
public void start(){
	Window window=((Activity)getContext()).getWindow();
	try{
	if(mMediaPlayer.getCurrentPosition()!=0)
	{
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
	}
	}catch(Exception e){}
	if(prepared)
	mMediaPlayer.start();
	playing=true;
	window.getDecorView().setSystemUiVisibility(window.getDecorView().getSystemUiVisibility()|(~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR));
	play_pause.setImageResource(R.drawable.pause);
	if(mMediaPlayer.isPlaying()&&!isShown()){
		window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
	}
}
	@Override
	public boolean onInfo(MediaPlayer p1, int p2, int p3)
	{
		play_pause.setImageResource(p1.isPlaying()?R.drawable.pause:R.drawable.play);
		switch(p2){
			case p1.MEDIA_INFO_BUFFERING_START:
				progressbar.setVisibility(View.VISIBLE);
				break;
			case p1.MEDIA_INFO_BUFFERING_END:
				progressbar.setVisibility(View.INVISIBLE);
				break;
			case p1.MEDIA_INFO_METADATA_UPDATE:
				break;
			case p1.MEDIA_INFO_VIDEO_RENDERING_START:
				//开始渲染第一帧
				Window window=((Activity)getContext()).getWindow();
				if(!isShown()){
				window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
				window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
				}
				window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
				View toolbar=window.findViewById(R.id.toolbar_layout);
				AppBarLayout.LayoutParams params=(AppBarLayout.LayoutParams) toolbar.getLayoutParams();
				params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP);
				toolbar.setLayoutParams(params);
				android.support.design.widget.AppBarLayout appbar=(AppBarLayout) window.findViewById(R.id.app_bar);
				appbar.setExpanded(true,true);
				mSeekBar.setMax(p1.getDuration());
				invalidateTime();
				intercept=true;
				break;
		}
		return true;
	}

	@Override
	public void onVideoSizeChanged(MediaPlayer p1, int mVideoWidth, int mVideoHeight)
	{
		int width=mTextureView.getWidth();
		int height=mTextureView.getHeight();
		float sx = (float) width / (float) mVideoWidth;
        float sy = (float) height / (float) mVideoHeight;
		Matrix matrix = new Matrix();

        //第1步:把视频区移动到View区,使两者中心点重合.
        matrix.preTranslate((width- mVideoWidth) / 2, (height - mVideoHeight) / 2);

        //第2步:因为默认视频是fitXY的形式显示的,所以首先要缩放还原回来.
        matrix.preScale(mVideoWidth / (float) width, mVideoHeight / (float) height);

        //第3步,等比例放大或缩小,直到视频区的一边和View一边相等.如果另一边和view的一边不相等，则留下空隙
        if (sx >= sy){
            matrix.postScale(sy, sy, width/ 2, height / 2);
        }else{
            matrix.postScale(sx, sx, width / 2, height / 2);
        }

        mTextureView.setTransform(matrix);
		
	}

	
private void invalidateTime(int currentPosition){
	time.setText(getTime(currentPosition)+"/"+getTime(getDuration()));
}
private void invalidateTime(){
	invalidateTime(mMediaPlayer.getCurrentPosition());
}
	private String getTime(int time){
		time=Math.abs(time);
		if(time==0)return "00:00";
		time/=1000;
		if(time<60)
			return "00:".concat(getFormat(time));
		int second=time%60;
		time/=60;
		if(time<60){
			return getFormat(time).concat(":").concat(getFormat(second));
		}
		int minute=time%60;
		return getFormat(time/60).concat(":").concat(getFormat(minute)).concat(":").concat(getFormat(second));
		
	}
	private String getFormat(int time){
		String time_=String.valueOf(time);
		if(time_.length()==1)
			return "0"+time_;
			return time_;
	}

	@Override
	public void onBufferingUpdate(MediaPlayer p1, int p2)
	{
		mSeekBar.setSecondaryProgress(p2);
	}

	@Override
	public void onSurfaceTextureSizeChanged(SurfaceTexture p1, int width, int height)
	{
		int mVideoWidth=mMediaPlayer.getVideoWidth();
		int mVideoHeight=mMediaPlayer.getVideoHeight();
		float sx = (float) width / (float) mVideoWidth;
        float sy = (float) height / (float) mVideoHeight;
		Matrix matrix = new Matrix();

        //第1步:把视频区移动到View区,使两者中心点重合.
        matrix.preTranslate((width- mVideoWidth) / 2, (height - mVideoHeight) / 2);

        //第2步:因为默认视频是fitXY的形式显示的,所以首先要缩放还原回来.
        matrix.preScale(mVideoWidth / (float) width, mVideoHeight / (float) height);

        //第3步,等比例放大或缩小,直到视频区的一边和View一边相等.如果另一边和view的一边不相等，则留下空隙
        if (sx >= sy){
            matrix.postScale(sy, sy, width/ 2, height / 2);
        }else{
            matrix.postScale(sx, sx, width / 2, height / 2);
        }

        mTextureView.setTransform(matrix);
		
	}

	@Override
	public boolean onSurfaceTextureDestroyed(SurfaceTexture p1)
	{
		return false;
	}

	@Override
	public void onSurfaceTextureAvailable(SurfaceTexture p1, int p2, int p3)
	{
		try{getMediaPlayer().setSurface(new Surface(p1));}catch(Exception e){}
		onSurfaceTextureSizeChanged(p1,p2,p3);
	}

	@Override
	public void onSurfaceTextureUpdated(SurfaceTexture p1)
	{
		// TODO: Implement this method
	}
	public boolean hideView(){
		if(fullscreen){
			((ViewGroup)getRootView().findViewById(android.R.id.content)).removeView((View)getParent());
			((ViewGroup)getTag()).addView((View)getParent());
			((Activity)getContext()).setRequestedOrientation(orientation);
			fullscreen=false;
			full.setImageResource(R.drawable.fullscreen);
			return true;
		}
		return false;
	}

	@Override
	public void onCompletion(MediaPlayer p1)
	{
		playing=false;
		play_pause.setImageResource(R.drawable.play);
		Window window=((Activity)getContext()).getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
	}

	public interface OnStateListener{
		void onStateChanged();
	}

	@Override
	public void onPrepared(MediaPlayer p1)
	{
		duration=p1.getDuration();
		prepared=true;
		getMediaPlayer().seekTo(currentPosition);
		if(playing)
			start();
	}

	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		// TODO: Implement this method
		boolean flag=mGestureDetector.onTouchEvent(event);
		if(event.getAction()==event.ACTION_CANCEL||event.getAction()==event.ACTION_UP){
			tips_message.setVisibility(INVISIBLE);
			tips_progress.setVisibility(INVISIBLE);
			if(scrollFlag==SCROLL_PROGRESS){
				mMediaPlayer.seekTo(mSeekBar.getProgress());
				if(isShown())
					mHandler.sendEmptyMessage(PROGRESS);
			}
		}
		return flag;
	}

	@Override
	public boolean onDown(MotionEvent p1)
	{
		getParent().requestDisallowInterceptTouchEvent(intercept);
		scrollFlag=-1;
		return true;
	}

	@Override
	public boolean onScroll(MotionEvent p1, MotionEvent p2, float dx, float dy)
	{
		if(scrollFlag==-1){
			if(p1.getY()<getResources().getDimensionPixelSize(getResources().getIdentifier("status_bar_height","dimen","android"))) return false;
			if(Math.abs(dx)>Math.abs(dy)){
				//拖动进度条
				scrollFlag=SCROLL_PROGRESS;
				data=mMediaPlayer.getCurrentPosition();
				tips_progress.setVisibility(View.VISIBLE);
				mHandler.removeMessages(PROGRESS);
			}else{
				if(p1.getX()<getWidth()/4){
					//亮度
				scrollFlag=SCROLL_LIGHT;
				data=(int)(((Activity)getContext()).getWindow().getAttributes().screenBrightness*100);
					try
					{
						if (data == -100)
							data =(int)(Settings.System.getInt(getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS)/255f*100f);
					}
					catch (Settings.SettingNotFoundException e)
					{}
					tips_message.setVisibility(VISIBLE);
				tips_img.setImageResource(R.drawable.brightness_6);
				}else if(p1.getX()>getWidth()/4*3){
					scrollFlag=SCROLL_VOLUME;
					data=mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
				tips_message.setVisibility(VISIBLE);
				}else{
					scrollFlag=SCROLL_PROGRESS;
					data=mMediaPlayer.getCurrentPosition();
					tips_progress.setVisibility(VISIBLE);
					mHandler.removeMessages(PROGRESS);
				}
			}
		}
		switch(scrollFlag){
			case SCROLL_VOLUME:{
				int max=mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
				int volume=(int)((p1.getY()-p2.getY())/(getHeight()/2)*max);
				volume+=data;
				volume=volume>max?max:volume<0?0:volume;
				mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,volume,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
				tips_img.setImageResource(volume==0?R.drawable.volume_off:R.drawable.volume_high);
				tips_progressbar.setMax(max);
				tips_progressbar.setProgress(volume);
				}break;
			case SCROLL_LIGHT:{
				int max=100;
				int light=(int)((p1.getY()-p2.getY())/getHeight()*max);
				light+=data;
				light=light>max?max:light<0?0:light;
				Window window=((Activity)getContext()).getWindow();
				WindowManager.LayoutParams wl=window.getAttributes();
				wl.screenBrightness=light/(float)max;
				window.setAttributes(wl);
				tips_progressbar.setMax(max);
				tips_progressbar.setProgress(light);
				}break;
			case SCROLL_PROGRESS:
			{
				int max=100000;
				int progress=(int)((p2.getX()-p1.getX())/getWidth()*max);
				progress+=data;
				progress=progress<0?0:progress>getDuration()?getDuration():progress;
				String distance=(dx>0?"-":"+").concat(getTime(progress-data));
				invalidateTime(progress);
				mSeekBar.setProgress(progress);
				tips_progress.setText(time.getText()+"\r\n"+distance);
			}
				break;
		}
		return true;
	}

	@Override
	public void onShowPress(MotionEvent p1)
	{
	}

	@Override
	public boolean onSingleTapUp(MotionEvent p1)
	{
		return true;
	}

	@Override
	public void onLongPress(MotionEvent p1)
	{
		// TODO: Implement this method
	}

	@Override
	public boolean onFling(MotionEvent p1, MotionEvent p2, float p3, float p4)
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public boolean onDoubleTap(MotionEvent p1)
	{
		if(mMediaPlayer.isPlaying())
			pause();
			else
			start();
		return true;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent p1)
	{
		if(isShown())hide();else show();
		return true;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent p1)
	{
		// TODO: Implement this method
		return true;
	}

	@Override
	public boolean onMenuItemClick(MenuItem p1)
	{
		show();
		try{
			currentPosition=getMediaPlayer().getCurrentPosition();
			getMediaPlayer().reset();
			prepare(p1.getItemId(),playing);
			
		}
		
		catch (IllegalArgumentException e)
		{}
		catch (SecurityException e)
		{}
		catch (IllegalStateException e)
		{}
		return true;
	}

	



	
	
}
