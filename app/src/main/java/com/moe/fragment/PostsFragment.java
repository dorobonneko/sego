package com.moe.fragment;
import android.view.*;

import android.app.Fragment;
import android.os.Bundle;
import com.moe.sego.R;
import com.moe.model.PostLoader;
import android.support.v4.widget.SwipeRefreshLayout;
import java.util.List;
import com.moe.entry.Post;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.GridLayoutManager;
import com.moe.adapter.PostAdapter;
import java.util.ArrayList;
import com.moe.internal.ItemDecoration;
import android.util.TypedValue;
import com.moe.adapter.PostAdapter.ViewHolder;
import android.content.Intent;
import com.moe.activity.VideoActivity;
import android.net.Uri;
import android.support.v4.app.ActivityOptionsCompat;
import android.widget.Toast;

public class PostsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,PostLoader.Callback,PostAdapter.OnClickListener
{
	private PostLoader loader;
	private SwipeRefreshLayout refresh;
	private PostAdapter mPostAdapter;
	private List<Post> list;
	private ScrollListener mScroll=new ScrollListener();
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.list_view,container,false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onViewCreated(view, savedInstanceState);
		refresh=(SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
		refresh.setOnRefreshListener(this);
		RecyclerView recyclerView=(RecyclerView) view.findViewById(R.id.recyclerView);
		recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(),2));
		recyclerView.setAdapter(mPostAdapter=new PostAdapter(list=new ArrayList<>()));
		recyclerView.addItemDecoration(new ItemDecoration((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,10,view.getResources().getDisplayMetrics())));
		mPostAdapter.setOnClickListener(this);
		recyclerView.addOnScrollListener(mScroll);
	}

	@Override
	public boolean contains(Post post)
	{
		if(list!=null)
			return list.contains(post);
		return false;
	}

	
	public void setLoader(PostLoader loader){
		this.loader=loader;
		if(isVisible()){
			if(loader==null){
				refresh.setEnabled(false);
			}else{
				refresh.setRefreshing(true);
				onRefresh();
			}
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		if(loader==null){
			refresh.setEnabled(false);
		}else{
			refresh.setRefreshing(true);
			onRefresh();
		}
	}

	@Override
	public void onRefresh()
	{
		loader.reset();
		loader.load(this);
	}

	@Override
	public void onReceived(List<Post> data,boolean clearFix)
	{
		refresh.setRefreshing(false);
		int size=list.size();
		if(clearFix){
			list.clear();
			mPostAdapter.notifyItemRangeRemoved(0,size);
			size=0;
		}
		list.addAll(data);
		mPostAdapter.notifyItemRangeInserted(size,list.size()-size);
	}

	@Override
	public void onError(Exception e)
	{
		refresh.setRefreshing(false);
		Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_SHORT).show();
	}


	@Override
	public void onClick(PostAdapter pa, PostAdapter.ViewHolder vh)
	{
		ActivityOptionsCompat aoc=ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),vh.itemView.findViewById(R.id.preview),"preview");
		startActivity(new Intent(getActivity(),VideoActivity.class).setData(Uri.parse(list.get(vh.getAdapterPosition()).href)).putExtra("preview",list.get(vh.getAdapterPosition()).img),aoc.toBundle());
	}


	private class ScrollListener extends RecyclerView.OnScrollListener
	{

		@Override
		public void onScrolled(RecyclerView recyclerView, int dx, int dy)
		{
			GridLayoutManager glm=(GridLayoutManager) recyclerView.getLayoutManager();
			if(dy>0&&loader.canLoadMore()&&!refresh.isRefreshing()&&glm.findLastVisibleItemPosition()>list.size()-4){
				refresh.setRefreshing(true);
				loader.load(PostsFragment.this);
			}
		}
	
}
	
}
