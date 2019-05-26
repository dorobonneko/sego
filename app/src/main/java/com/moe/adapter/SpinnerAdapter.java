package com.moe.adapter;
import android.widget.BaseAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.util.TypedValue;

public class SpinnerAdapter extends BaseAdapter
{
	private String[] data;
	public SpinnerAdapter(String... data){
		this.data=data;
	}
	@Override
	public int getCount()
	{
		return data.length;
	}

	@Override
	public Object getItem(int p1)
	{
		return data[p1];
	}

	@Override
	public long getItemId(int p1)
	{
		// TODO: Implement this method
		return p1;
	}

	@Override
	public View getView(int p1, View p2, ViewGroup p3)
	{
		if(p2==null)
			p2=new TextView(p3.getContext());
			((TextView)p2).setText(data[p1]);
			((TextView)p2).setTextColor(0xffffffff);
		return p2;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent)
	{
		if(convertView==null)
			convertView=new TextView(parent.getContext());
		((TextView)convertView).setText(data[position]);
		int padding=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,8,parent.getResources().getDisplayMetrics());
		convertView.setPadding(padding,padding,padding,padding);
			return convertView;
	}
	
}
