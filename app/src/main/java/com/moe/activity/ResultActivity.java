package com.moe.activity;
import android.app.Activity;
import android.os.Bundle;
import com.moe.sego.R;
import com.moe.fragment.PostsFragment;
import com.moe.model.SimpleLoader;

public class ResultActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reault_view);
		PostsFragment postf=(PostsFragment) getFragmentManager().findFragmentById(R.id.postfragment);
		postf.setLoader(new SimpleLoader(getIntent().getDataString()));
	}
	
}
