package com.moe.fragment;
import android.view.*;

import android.app.Fragment;
import android.os.Bundle;
import com.moe.sego.R;
import android.support.v7.widget.RecyclerView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.StaggeredGridLayoutManager;
import org.jsoup.Jsoup;
import com.moe.internal.Api;
import org.jsoup.nodes.Document;
import java.io.IOException;
import org.jsoup.select.Elements;
import java.util.ArrayList;
import android.widget.Toast;
import java.util.List;
import com.moe.adapter.TagsAdapter;
import com.moe.widget.WaterFullLayoutManager;
import com.moe.internal.ItemDecoration;
import android.util.TypedValue;
import android.content.Intent;
import com.moe.activity.ResultActivity;
import android.net.Uri;

public class TagsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,TagsAdapter.OnClickListener
{
	private SwipeRefreshLayout refresh;
	private List<String> list;
	private TagsAdapter mTagsAdapter;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// TODO: Implement this method
		return inflater.inflate(R.layout.list_view,container,false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onViewCreated(view, savedInstanceState);
		RecyclerView recyclerView=view.findViewById(R.id.recyclerView);
		refresh=view.findViewById(R.id.swipeRefreshLayout);
		refresh.setOnRefreshListener(this);
		recyclerView.setLayoutManager(new WaterFullLayoutManager());
		recyclerView.addItemDecoration(new ItemDecoration((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,15,recyclerView.getResources().getDisplayMetrics())));
		recyclerView.setAdapter(mTagsAdapter=new TagsAdapter(list=new ArrayList<>()));
		mTagsAdapter.setOnClickListener(this);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onActivityCreated(savedInstanceState);
		refresh.setRefreshing(true);
		onRefresh();
	}

	@Override
	public void onRefresh()
	{
		new Thread(){
			public void run(){
				try
				{
					Document doc=Jsoup.connect(Api.getTags(getActivity())).get();
					Elements tags=doc.select(".stag");
					final ArrayList<String> tags_list=new ArrayList<>(tags.size());
					for(int i=0;i<tags.size();i++){
						tags_list.add(tags.get(i).child(0).ownText());
					}
					if(getView()!=null)
						getView().post(new Runnable(){

								@Override
								public void run()
								{
									refresh.setRefreshing(false);
									int size=list.size();
									list.clear();
									mTagsAdapter.notifyItemRangeRemoved(0,size);
									list.addAll(tags_list);
									mTagsAdapter.notifyItemRangeInserted(0,list.size());
								}
							});
				}
				catch (final IOException e)
				{if(getView()!=null)
						getView().post(new Runnable(){

								@Override
								public void run()
								{
									refresh.setRefreshing(false);
									Toast.makeText(getView().getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
								}
							});}
			}
		}.start();
	}

	@Override
	public void onClick(int position)
	{
		startActivity(new Intent(getActivity(),ResultActivity.class).setData(Uri.parse(Api.getTags(getActivity()).concat("/").concat(list.get(position)))));
	}


	
}
