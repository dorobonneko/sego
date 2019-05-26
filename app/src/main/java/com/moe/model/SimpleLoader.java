package com.moe.model;

public class SimpleLoader extends AbstractLoader
{
	private String url;
	public SimpleLoader(String url){
		this.url=url;
	}

	@Override
	public String getUrl()
	{
		// TODO: Implement this method
		return url;
	}

	
}
