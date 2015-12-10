package com.tonggou.gsm.andclient.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.tonggou.gsm.andclient.App;
import com.tonggou.gsm.andclient.util.StringUtil;
import com.umeng.analytics.MobclickAgent;

public class BaseFragment extends Fragment {
	
	public final String TAG = getClass().getSimpleName();
	
	protected Activity mActivity;
	private View mRootView;
	
	/**
	 * 取得 bundle 中存储的值.
	 * <p>NOTE : 若要使用该方法，应该在子类中覆写此方法
	 * @param extra 
	 * @return true 成功取值  | false 取值不成功
	 */
	protected boolean restoreExtras(final Bundle extra) {
		return false;
	}
	
	public void onAttach(Activity activity) {
		App.debug(TAG, "onAttach");
		super.onAttach(activity);
		mActivity = activity;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		App.debug(TAG, "onViewCreated");
		view.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
		mRootView = view;
		super.onViewCreated(view, savedInstanceState);
	}
	
	@SuppressWarnings("unchecked")
	protected <T extends View> T findViewById(int id) {
		return (T)mRootView.findViewById(id);
	}
	
	public void onResume() {
		App.debug(TAG, "onResume");
		super.onResume();
		MobclickAgent.onPageStart(TAG);
	}

	@Override
	public void onPause() {
		App.debug(TAG, "onPause");
		super.onPause();
		MobclickAgent.onPageEnd(TAG);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		App.debug(TAG, "onActivityCreated");
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		App.debug(TAG, "onCreate");
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		App.debug(TAG, "onCreateView");
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onDestroy() {
		App.debug(TAG, "onDestroy");
		super.onDestroy();
	}

	@Override
	public void onDestroyView() {
		App.debug(TAG, "onDestroyView");
		super.onDestroyView();
	}

	@Override
	public void onDetach() {
		App.debug(TAG, "onDetach");
		super.onDetach();
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		App.debug(TAG, "onHiddenChanged  " + hidden);
		super.onHiddenChanged(hidden);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		App.debug(TAG, "onSaveInstanceState");
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onStart() {
		App.debug(TAG, "onStart");
		super.onStart();
	}

	@Override
	public void onStop() {
		App.debug(TAG, "onStop");
		super.onStop();
	}

	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		App.debug(TAG, "onViewStateRestored");
		super.onViewStateRestored(savedInstanceState);
	}

	/**
	 * {@link StringUtil #formatHourMinute(long)}
	 * @param during
	 * @return
	 */
	protected String formatHourMinute(long during) {
		return StringUtil.formatHourMinute(during);
	}
	
	/**
	 * {@link StringUtil #formatFloat1(float) }
	 * @param oilWear
	 * @return
	 */
	protected String formatFloat1(float oilWear) {
		return StringUtil.formatFloat1(oilWear);
	}
}
