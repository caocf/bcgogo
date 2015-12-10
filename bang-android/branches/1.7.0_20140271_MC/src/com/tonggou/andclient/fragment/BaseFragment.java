package com.tonggou.andclient.fragment;

import com.tonggou.andclient.BaseActivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MotionEvent;
import android.view.View;

public class BaseFragment extends Fragment {
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		view.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
	}
	
	protected BaseActivity getBaseActivity() {
		return (BaseActivity)getActivity();
	}
	
}
