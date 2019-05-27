package com.moe.adapter;
import android.view.*;

import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import com.moe.sego.R;
import java.util.List;
import android.util.TypedValue;

public class TagsAdapter extends RecyclerView.Adapter<TagsAdapter.ViewHolder>
{
	private List<String> list;
	private OnClickListener ocl;
	public TagsAdapter(List<String> list){
		this.list=list;
	}
	@Override
	public TagsAdapter.ViewHolder onCreateViewHolder(ViewGroup p1, int p2)
	{
		// TODO: Implement this method
		return new ViewHolder(new TextView(p1.getContext()));
	}

	@Override
	public void onBindViewHolder(TagsAdapter.ViewHolder vh, int p2)
	{
		vh.text.setText(list.get(vh.getAdapterPosition()));
	}

	@Override
	public int getItemCount()
	{
		// TODO: Implement this method
		return list.size();
	}
	
	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
		private TextView text;
		public ViewHolder(View v){
			super(v);
			text=(TextView) v;
			int size=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,4,v.getResources().getDisplayMetrics());
			v.setPadding(size,size,size,size);
			text.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
			v.setBackgroundResource(R.drawable.tags_background);
			v.setOnClickListener(this);
		}

		@Override
		public void onClick(View p1)
		{
			if(ocl!=null)
				ocl.onClick(getAdapterPosition());
		}

		
	}
	public void setOnClickListener(OnClickListener l){
		ocl=l;
	}
	public interface OnClickListener{
		void onClick(int position);
	}
}
