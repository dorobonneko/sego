package com.moe.internal;
import android.support.v7.widget.RecyclerView;
import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView.State;
import android.graphics.Rect;
import android.view.View;

public class ItemDecoration extends RecyclerView.ItemDecoration
{
	private int size;
	public ItemDecoration(int size){
		this.size=size;
	}
	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)
	{
		if((parent.getLayoutManager().getPosition(view)+1)%2==0){
			outRect.right=size;
			outRect.left=size/2;
		}else{
			outRect.left=size;
			outRect.right=size/2;
			}
			outRect.top=size;
	}
	
}
