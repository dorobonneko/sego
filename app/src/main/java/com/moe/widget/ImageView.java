package com.moe.widget;
import android.content.Context;
import android.util.AttributeSet;

public class ImageView extends android.widget.ImageView
{
	public ImageView(Context context,AttributeSet attrs){
		super(context,attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		int width=MeasureSpec.getSize(widthMeasureSpec);
		int height=(int)(width/16d*9);
		setMeasuredDimension(widthMeasureSpec,MeasureSpec.makeMeasureSpec(height,MeasureSpec.EXACTLY));
	}
	
}
