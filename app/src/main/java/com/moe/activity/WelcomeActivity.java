package com.moe.activity;
import android.app.Activity;
import android.os.Bundle;
import com.moe.sego.R;
import android.support.design.widget.NavigationView;
import android.view.MenuItem;
import android.app.Fragment;
import com.moe.fragment.PostsFragment;
import com.moe.model.IndexLoader;
import android.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import com.moe.model.VideosLoader;
import com.moe.model.SimpleLoader;
import com.moe.internal.Api;
import android.view.Menu;
import android.widget.EditText;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.widget.Toolbar;
import com.moe.fragment.TagsFragment;
import com.moe.fragment.CategoriesFragment;

public class WelcomeActivity extends Activity implements NavigationView.OnNavigationItemSelectedListener
{
private Fragment current;
private DrawerLayout mDrawerLayout;
private NavigationView mNavigationView;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mDrawerLayout=(DrawerLayout) findViewById(R.id.drawerLayout);
		mNavigationView=(NavigationView) findViewById(R.id.navigationView);
		mNavigationView.setNavigationItemSelectedListener(this);
		onNavigationItemSelected(mNavigationView.getMenu().findItem(R.id.index));
		setActionBar((Toolbar)findViewById(R.id.toolbar));
	}

	@Override
	public boolean onNavigationItemSelected(MenuItem item)
	{
		switch(item.getItemId()){
			case R.id.index:
			{
				Fragment f=getFragmentManager().findFragmentByTag(String.valueOf(item.getItemId()));
				if(f==null){
					f=new PostsFragment();
					((PostsFragment)f).setLoader(new IndexLoader());
				}
				FragmentTransaction ft=getFragmentManager().beginTransaction();
				if(current!=null)
					ft.hide(current);
				if(f.isAdded())
					ft.show(f);
					else
					ft.add(R.id.fragment,f,String.valueOf(item.getItemId()));
					ft.commitAllowingStateLoss();
					current=f;
			}
				break;
			case R.id.videos:
				{
					Fragment f=getFragmentManager().findFragmentByTag(String.valueOf(item.getItemId()));
					if(f==null){
						f=new PostsFragment();
						((PostsFragment)f).setLoader(new VideosLoader());
					}
					FragmentTransaction ft=getFragmentManager().beginTransaction();
					if(current!=null)
						ft.hide(current);
					if(f.isAdded())
						ft.show(f);
					else
						ft.add(R.id.fragment,f,String.valueOf(item.getItemId()));
					ft.commitAllowingStateLoss();
					current=f;
				}
				break;
			case R.id.hd:
				{
					Fragment f=getFragmentManager().findFragmentByTag(String.valueOf(item.getItemId()));
					if(f==null){
						f=new PostsFragment();
						((PostsFragment)f).setLoader(new SimpleLoader(Api.getHD(this)));
					}
					FragmentTransaction ft=getFragmentManager().beginTransaction();
					if(current!=null)
						ft.hide(current);
					if(f.isAdded())
						ft.show(f);
					else
						ft.add(R.id.fragment,f,String.valueOf(item.getItemId()));
					ft.commitAllowingStateLoss();
					current=f;
				}
				break;
			case R.id.tags:
				{
					Fragment f=getFragmentManager().findFragmentByTag(String.valueOf(item.getItemId()));
					if(f==null){
						f=new TagsFragment();
						}
					FragmentTransaction ft=getFragmentManager().beginTransaction();
					if(current!=null)
						ft.hide(current);
					if(f.isAdded())
						ft.show(f);
					else
						ft.add(R.id.fragment,f,String.valueOf(item.getItemId()));
					ft.commitAllowingStateLoss();
					current=f;
				}
				break;
			case R.id.class_:
				{
					Fragment f=getFragmentManager().findFragmentByTag(String.valueOf(item.getItemId()));
					if(f==null){
						f=new CategoriesFragment();
						}
					FragmentTransaction ft=getFragmentManager().beginTransaction();
					if(current!=null)
						ft.hide(current);
					if(f.isAdded())
						ft.show(f);
					else
						ft.add(R.id.fragment,f,String.valueOf(item.getItemId()));
					ft.commitAllowingStateLoss();
					current=f;
				}
				break;
		}
		mDrawerLayout.closeDrawer(Gravity.START);
		return true;
	}

	@Override
	public void onBackPressed()
	{
		if(current!=null&&!current.getTag().equals(String.valueOf(R.id.index))){
			getFragmentManager().beginTransaction().hide(current).show(current=getFragmentManager().findFragmentByTag(String.valueOf(R.id.index))).commitAllowingStateLoss();
			mNavigationView.getMenu().findItem(R.id.index).setChecked(true);
		}else
		moveTaskToBack(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu,menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId()){
			case R.id.search:
				final EditText search_key=new EditText(this);
				search_key.setSingleLine();
				new AlertDialog.Builder(this).setTitle("Search").setView(search_key).setPositiveButton(android.R.string.search_go, new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface p1, int p2)
						{
							startActivity(new Intent(getApplicationContext(),ResultActivity.class).setData(Uri.parse(Api.getHost(getApplicationContext()).concat("/search/videos?search_query=").concat(search_key.getText().toString()))));
						}
					}).setNeutralButton(android.R.string.cancel, null).show();
				break;
		}
		return true;
	}
	
	
}
