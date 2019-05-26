package com.moe.adapter;
import android.view.*;

import android.support.v7.widget.RecyclerView;
import com.moe.entry.Post;
import com.moe.sego.R;
import java.util.List;
import android.widget.ImageView;
import com.moe.tinyimage.Pussy;
import android.widget.TextView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>
{
	private List<Post> list;
	private OnClickListener l;
	public PostAdapter(List<Post> list){
		this.list=list;
	}
	@Override
	public PostAdapter.ViewHolder onCreateViewHolder(ViewGroup p1, int p2)
	{
		return new ViewHolder(LayoutInflater.from(p1.getContext()).inflate(R.layout.post_view,p1,false));
	}

	@Override
	public void onBindViewHolder(PostAdapter.ViewHolder vh, int p2)
	{
		Post post=list.get(vh.getAdapterPosition());
		Pussy.get(vh.itemView.getContext()).load(post.img).into(vh.preview);
		vh.title.setText(post.title);
		vh.duration.setText(post.duration);
		vh.time.setText(post.time);
		vh.views.setText(post.views);
	}

	@Override
	public int getItemCount()
	{
		return list.size();
	}
	
	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
		ImageView preview;
		TextView title,time,duration,views;
		public ViewHolder(View v){
			super(v);
			preview=(ImageView) v.findViewById(R.id.preview);
			title=(TextView) v.findViewById(R.id.title);
			time=(TextView) v.findViewById(R.id.time);
			duration=(TextView) v.findViewById(R.id.duration);
			views=(TextView) v.findViewById(R.id.views);
			v.setOnClickListener(this);
		}

		@Override
		public void onClick(View p1)
		{
			if(l!=null)
				l.onClick(PostAdapter.this,this);
		}

		
	}
	public void setOnClickListener(OnClickListener l){
		this.l=l;
	}
	public interface OnClickListener{
		void onClick(PostAdapter pa,ViewHolder vh);
	}
}
