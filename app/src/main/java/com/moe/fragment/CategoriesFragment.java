package com.moe.fragment;
import android.view.*;

import android.app.Fragment;
import android.os.Bundle;
import com.moe.sego.R;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import com.moe.adapter.CategoriesAdapter;
import java.util.List;
import com.moe.entry.Category;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import com.moe.internal.Api;
import org.jsoup.nodes.Document;
import java.io.IOException;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;
import android.widget.Toast;
import android.content.Intent;
import com.moe.activity.ResultActivity;
import android.net.Uri;

public class CategoriesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,CategoriesAdapter.OnClickListener
{
	private SwipeRefreshLayout refresh;
	private CategoriesAdapter mCategoriesAdapter;
	private List<Category> list;
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
		refresh=view.findViewById(R.id.swipeRefreshLayout);
		refresh.setOnRefreshListener(this);
		RecyclerView recyclerView=view.findViewById(R.id.recyclerView);
		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		recyclerView.setAdapter(mCategoriesAdapter=new CategoriesAdapter(list=new ArrayList<>()));
		mCategoriesAdapter.setOnClickListener(this);
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
					Document doc=Jsoup.connect(Api.getCategories(getActivity())).get();
					Elements categroies=doc.select(".thumb-overlay");
					final List<Category> temp_list=new ArrayList<>(categroies.size());
					for(int i=0;i<categroies.size();i++){
						Category category=new Category();
						Element item=categroies.get(i);
						category.preview=item.select(".img-responsive").get(0).absUrl("src");
						Element title=item.select(".category-title").get(0);
						category.title=title.child(0).ownText();
						category.videos=title.child(1).child(0).ownText();
						category.href=item.parent().absUrl("href");
						temp_list.add(category);
					}
					if(getView()!=null)
						getView().post(new Runnable(){

								@Override
								public void run()
								{
									refresh.setRefreshing(false);
									int size=list.size();
									list.clear();
									mCategoriesAdapter.notifyItemRangeRemoved(0,size);
									list.addAll(temp_list);
									mCategoriesAdapter.notifyItemRangeInserted(0,list.size());
								}
							});
				}
				catch (final IOException e)
				{
					if(getView()!=null)
						getView().post(new Runnable(){

								@Override
								public void run()
								{
									refresh.setRefreshing(false);
									Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_SHORT).show();
								}
							});
				}
			}
		}.start();
	}

	@Override
	public void onClick(int position)
	{
		getActivity().startActivity(new Intent(getActivity(),ResultActivity.class).setData(Uri.parse(list.get(position).href)));
	}


	
}
