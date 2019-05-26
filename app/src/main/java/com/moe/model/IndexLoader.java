package com.moe.model;
import com.moe.model.PostLoader.Callback;
import org.jsoup.Jsoup;
import com.moe.internal.Api;
import android.content.Context;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import java.io.IOException;
import org.jsoup.select.Elements;
import java.util.ArrayList;
import com.moe.entry.Post;
import org.jsoup.nodes.Element;
import android.os.Handler;
import android.os.Looper;

public class IndexLoader extends AbstractLoader
{
	@Override
	public String getUrl()
	{
		// TODO: Implement this method
		return Api.getHost(getContext());
	}

	@Override
	public boolean canLoadMore()
	{
		// TODO: Implement this method
		return false;
	}

	@Override
	public void load(PostLoader.Callback callback)
	{
		reset();
		super.load(callback);
	}


}
