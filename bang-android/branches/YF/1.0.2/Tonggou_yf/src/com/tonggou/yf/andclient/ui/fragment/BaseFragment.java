package com.tonggou.yf.andclient.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.tonggou.yf.andclient.ui.BaseActivity;
import com.umeng.analytics.MobclickAgent;

public abstract class BaseFragment extends Fragment {

	public final String TAG = getClass().getSimpleName();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		int layoutRes = getLayoutRes();
		if( layoutRes <= 0 ) {
			return super.onCreateView(inflater, container, savedInstanceState);
		}
		return inflater.inflate(layoutRes, container, false);
	}
	
	public abstract int getLayoutRes();
	
	@SuppressWarnings("unchecked")
	public <T extends View> T  findViewById(int id) {
		return (T) getView().findViewById(id);
	}
	
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(TAG);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(TAG);
	}

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
	
	public void showLoadingDialog() {
		showLoadingDialog(0);
	}
	
	public void showLoadingDialog(int msgRes) {
		(getBaseActivity()).showLoadingDialog(msgRes);
	}
	
	public void dismissLoadingDialog() {
		(getBaseActivity()).dismissLoadingDialog();
	}
	
	public BaseActivity getBaseActivity() {
		return (BaseActivity) getActivity();
	}
	
}
