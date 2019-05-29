package com.moe.adapter;
import android.view.*;

import android.support.v7.widget.RecyclerView;
import com.moe.entry.Category;
import com.moe.sego.R;
import java.util.List;
import com.moe.widget.ImageView;
import android.widget.TextView;
import com.moe.tinyimage.Pussy;
import com.moe.tinyimage.CropTransForm;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder>
{
	private List<Category> list;
	private OnClickListener ocl;
	public CategoriesAdapter(List<Category> list){
		this.list=list;
	}
	@Override
	public CategoriesAdapter.ViewHolder onCreateViewHolder(ViewGroup p1, int p2)
	{
		// TODO: Implement this method
		return new ViewHolder(LayoutInflater.from(p1.getContext()).inflate(R.layout.category_view,p1,false));
	}

	@Override
	public void onBindViewHolder(CategoriesAdapter.ViewHolder p1, int p2)
	{
		Category category=list.get(p1.getAdapterPosition());
		Pussy.get(p1.itemView.getContext()).load(category.preview).transForm(new CropTransForm(Gravity.CENTER)).into(p1.preview);
		p1.title.setText(category.title);
		p1.videos.setText(category.videos);
	}

	@Override
	public int getItemCount()
	{
		// TODO: Implement this method
		return list.size();
	}
	
	public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
		ImageView preview;
		TextView title,videos;
		public ViewHolder(View v){
			super(v);
			preview=v.findViewById(R.id.preview);
			title=v.findViewById(R.id.title);
			videos=v.findViewById(R.id.videos);
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
