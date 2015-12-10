package com.tonggou.yf.andclient.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;

import com.tonggou.yf.andclient.R;
import com.tonggou.yf.andclient.bean.UserScope;
import com.tonggou.yf.andclient.bean.type.TodoType;
import com.tonggou.yf.andclient.service.NewVersionBroadcastReceiver;
import com.tonggou.yf.andclient.service.NewVersionBroadcastReceiver.OnGotNewVersionListener;
import com.tonggou.yf.andclient.service.UpdateService;
import com.tonggou.yf.andclient.ui.fragment.TodoPagerFragment;
import com.tonggou.yf.andclient.widget.view.SimpleTitleBar;

@SuppressLint("UseSparseArrays")
public class MainActivity extends AbsDoubleBackPressedExitActivity implements OnGotNewVersionListener {
	
	public static final String EXTRA_USER_SCOPE = "extra_user_scope";
	
	UserScope mUserScope;
	DrawerLayout mDrawerLayout;
	
	private HashMap<Integer, WeakReference<Fragment>> mActivityResultObservers;
	
	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setContentView(R.layout.activity_main);
		if( !restoreArgs(getIntent()) ) {
			restoreArgs(savedInstance);
		}
		initActivityResultObservers();
		
		mDrawerLayout = myFindViewById(R.id.drawer_layout);
		afterViews();
	}
	
	void initActivityResultObservers() {
		mActivityResultObservers = new HashMap<Integer, WeakReference<Fragment>>(2);
	}
	
	boolean restoreArgs(Intent intent) {
		if( intent != null) {
			return restoreArgs(intent.getExtras());
		}
		return false;
	}
	
	boolean restoreArgs(Bundle args) {
		if( args != null && args.containsKey(EXTRA_USER_SCOPE) ) {
			mUserScope = (UserScope) args.getSerializable(EXTRA_USER_SCOPE);
			return true;
		}
		return false;
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(EXTRA_USER_SCOPE, mUserScope);
		super.onSaveInstanceState(outState);
	}
 	
	@Override
	protected void onTitleBarCreated(SimpleTitleBar titleBar) {
		super.onTitleBarCreated(titleBar);
		titleBar.setTitle(R.string.title_main);
		titleBar.setLeftButton(R.drawable.ic_titlebar_menu);
		titleBar.setOnLeftButtonClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				toggleShowDrawerLayout();
			}
		});
	}
	
	void toggleShowDrawerLayout() {
		if(!mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
			mDrawerLayout.openDrawer(Gravity.LEFT);
		} else {
			mDrawerLayout.closeDrawers();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if( keyCode == KeyEvent.KEYCODE_MENU ) {
			toggleShowDrawerLayout();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	void afterViews() {
		Fragment fragment = TodoPagerFragment.newInstance(getTypesByUserScope(mUserScope));
		
		getSupportFragmentManager().beginTransaction()
			.replace(R.id.fragment_container, fragment).commit();
		
		NewVersionBroadcastReceiver.register(this, this);
		UpdateService.update(this, true, null);
	}
	
	private TodoType[] getTypesByUserScope(UserScope userScope) {
		ArrayList<TodoType> types = new ArrayList<TodoType>();
		if( userScope.isDTC() ) {
			types.add(TodoType.DTC);
		}
		if( userScope.isAppoint() ) {
			types.add(TodoType.APPOINTMENT);
		}
		if( userScope.isMaintain() ) {
			types.add(TodoType.MAINTAIN);
		}
		TodoType[] typeArr = new TodoType[types.size()];
		return types.toArray(typeArr);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Fragment activityResultObserver = mActivityResultObservers.get(requestCode).get();
		if( activityResultObserver != null ) {
			activityResultObserver.onActivityResult(requestCode, resultCode, data);
		}
	}
	
	public void registerActivityResultObserver(int requestCode, Fragment fragment) {
		mActivityResultObservers.put(requestCode, new WeakReference<Fragment>(fragment));
	}
	
	@Override
	boolean isCanExit() {
		return true;
	}
	
	@Override
	protected void onDestroy() {
		NewVersionBroadcastReceiver.unregister(this);
		super.onDestroy();
	}

	@Override
	public void onGotNewVersion() {
		// do nothing
	}
}
