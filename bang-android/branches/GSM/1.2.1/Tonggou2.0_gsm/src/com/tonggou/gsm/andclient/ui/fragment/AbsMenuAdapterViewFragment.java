package com.tonggou.gsm.andclient.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;

import com.tonggou.gsm.andclient.ui.MainActivity;
import com.tonggou.gsm.andclient.ui.view.AbsViewHolderAdapter;

/**
 * 菜单 Fragment基类
 * @author lwz
 *
 */
public abstract class AbsMenuAdapterViewFragment extends BaseFragment implements OnItemClickListener {

	protected MenuAdapter mMenuAdapter;
	private Class<?>[] DEST_ACTIVITIES;
	private int mItemCount;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(getLayoutId(), container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		AdapterView<ListAdapter> menuList = findViewById(getAdapterViewId());
		mItemCount = getItemCount();
		DEST_ACTIVITIES = getDestActivitys();
		
		createAdapter();
		menuList.setAdapter(mMenuAdapter);
		menuList.setOnItemClickListener(this);
	}
	
	private void createAdapter() {
		String[] contents = getResources().getStringArray(getContentsArrayRes());
		TypedArray arr = getResources().obtainTypedArray(getIconsTypeArrayRes());
		ArrayList<MenuItem> items = new ArrayList<MenuItem>();
		for( int i=0; i<mItemCount; i++ ) {
			items.add(new MenuItem(contents[i], arr.getDrawable(i), null));
		}
		arr.recycle();
		mMenuAdapter = new MenuAdapter(mActivity, items, getAdapterItemLayoutId());
	}
	
	/**
	 * 子项的数目
	 * @return
	 */
	abstract int getItemCount();
	
	/**
	 * 布局 id
	 * @return
	 */
	abstract int getLayoutId();
	
	/**
	 * AdapterView 的 id,可以是 ListView、GridView 等 AdapterView
	 * @return
	 */
	abstract int getAdapterViewId();

	/**
	 * 适配器中子项的布局 id
	 * @return
	 */
	abstract int getAdapterItemLayoutId();
	
	/**
	 * 子项点击要跳转的 Activity
	 * @return
	 */
	abstract Class<?>[] getDestActivitys();
	
	/**
	 * 子项显示的文字内容
	 * @return
	 */
	abstract int getContentsArrayRes();
	
	/**
	 * 子项显示的图标
	 * @return
	 */
	abstract int getIconsTypeArrayRes();
	
	class MenuAdapter extends AbsViewHolderAdapter<MenuItem> {

		public MenuAdapter(Context context, List<MenuItem> data, int layoutRes) {
			super(context, data, layoutRes);
		}

		@Override
		protected void bindData(int pos, MenuItem itemData) {
			setMenuAdapterViewData(this, pos, itemData);
		}
	}
	
	/**
	 * 设置 适配器中子项的各个 View 的值
	 * @param adapter
	 * @param pos
	 * @param convertView
	 * @param itemData
	 */
	abstract void setMenuAdapterViewData(AbsViewHolderAdapter<MenuItem> adapter, int pos, MenuItem itemData);
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent(getActivity(), DEST_ACTIVITIES[position % mItemCount]);
		startActivity(intent);
		((MainActivity)mActivity).showCloseMenu();
	}
	
	/**
	 * 设置消息的条数
	 * @param count
	 */
	public void setItemIndicatorCount(int itemPos, String indicator) {
		mMenuAdapter.getData().get(itemPos).indicator = indicator;
		mMenuAdapter.notifyDataSetChanged();
	}
	
	class MenuItem {
		String content;
		Drawable icon;
		String indicator;
		
		MenuItem(String content, Drawable icon, String indicator) {
			super();
			this.content = content;
			this.icon = icon;
			this.indicator = indicator;
		}
	}
}
