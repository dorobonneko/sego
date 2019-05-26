package com.moe.model;
import android.os.*;
import org.jsoup.nodes.*;

import android.content.Context;
import com.moe.entry.Post;
import com.moe.internal.Api;
import java.io.IOException;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

public class VideosLoader extends AbstractLoader
{

	@Override
	public String getUrl()
	{
		// TODO: Implement this method
		return Api.getVideo(getContext());
	}

	

	
}
