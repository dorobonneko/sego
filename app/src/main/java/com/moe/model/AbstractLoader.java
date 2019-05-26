package com.moe.model;

import android.os.*;
import org.jsoup.nodes.*;

import com.moe.entry.Post;
import com.moe.internal.Api;
import java.io.IOException;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import android.content.Context;
import android.net.Uri;

public abstract class AbstractLoader implements PostLoader
{
	private int page=1;
	private boolean canLoadMore=true;
	private Thread thread;
	private Context context;
	@Override
	public void reset()
	{
		page=1;
		canLoadMore=true;
	}

	@Override
	public void onAttachContext(Context context)
	{
		this.context=context;
		}

	@Override
	public void cancel()
	{
		if(thread!=null)
			thread.interrupt();
	}

	@Override
	public Context getContext()
	{
		// TODO: Implement this method
		return context;
	}


	@Override
	public boolean canLoadMore()
	{
		// TODO: Implement this method
		return canLoadMore;
	}

	
	@Override
	public void load(final PostLoader.Callback callback)
	{
		thread=new Thread(){
			public void run(){
				try
				{
					Document doc=Jsoup.connect(Uri.parse(getUrl()).buildUpon().appendQueryParameter("page",String.valueOf(page)).build().toString()).get();
					Elements posts=doc.select(".well-sm");
					final ArrayList<Post> list=new ArrayList<>(posts.size());
					for(int i=0;i<posts.size();i++){
						Post post=new Post();
						Element e=posts.get(i);
						Element link=e.child(0);
						if(!link.tagName().equalsIgnoreCase("a"))continue;
						post.href=link.absUrl("href");
						Element div=link.child(0);
						post.img=div.child(0).absUrl("src");
						post.duration=div.select(".duration").get(0).ownText();
						post.title=link.child(1).ownText();
						post.time=e.child(1).ownText();
						post.views=e.child(2).ownText();
						if(!callback.contains(post))
						list.add(post);
					}
					canLoadMore=list.size()>0;
					new Handler(Looper.getMainLooper()).post(new Runnable(){

							@Override
							public void run()
							{
								callback.onReceived(list,page==1);
								page++;
								
							}
						});
				}
				catch (final IOException e)
				{
					new Handler(Looper.getMainLooper()).post(new Runnable(){

							@Override
							public void run()
							{
								callback.onError(e);
							}
						});
				}
			}
		};
		thread.start();
	}
}
