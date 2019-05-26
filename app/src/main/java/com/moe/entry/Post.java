package com.moe.entry;

public class Post
{
	public String title,img,href,time,views,duration;

	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof Post)
			return ((Post)obj).href.equals(href);
		return super.equals(obj);
	}
	
}
