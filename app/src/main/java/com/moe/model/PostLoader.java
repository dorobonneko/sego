package com.moe.model;
import java.util.List;
import com.moe.entry.Post;
import android.content.Context;

public interface PostLoader
{
	public void onAttachContext(Context context);
	public void reset();
	public void cancel();
	public void load(Callback callback);
	public boolean canLoadMore();
	public String getUrl();
	public Context getContext();
	public interface Callback{
		void onReceived(List<Post> data,boolean clearFix);
		void onError(Exception e);
		 boolean contains(Post post);
	}
}
