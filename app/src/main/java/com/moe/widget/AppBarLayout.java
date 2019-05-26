package com.moe.widget;
import android.support.design.widget.AppBarLayout;
import android.util.AttributeSet;
import android.content.Context;

public class AppBarLayout extends AppBarLayout
{
	public AppBarLayout(Context context,AttributeSet attrs){
		super(context,attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		int width=MeasureSpec.getSize(widthMeasureSpec);
		int height=(int)(width/16d*9);
		super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height,MeasureSpec.EXACTLY));
	}
	
}
