package com.tonggou.gsm.andclient.test;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tonggou.gsm.andclient.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

public class TestPullToRefreshActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_activity_pulltorefresh);
		
		PullToRefreshListView refreshListView = (PullToRefreshListView) findViewById(R.id.refresh_listview);
		refreshListView.setAdapter( getAdapter() );
		refreshListView.setMode( Mode.BOTH );
	}
	
	private ListAdapter getAdapter() {
		return new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getData());
	}
	
	private String[] getData() {
		final int SIZE = 50;
		String[] data = new String[SIZE];
		for( int i=0; i<SIZE; i++ ) {
			data[i] = String.valueOf("ITEM " + i);
		}
		return data;
	}
}
