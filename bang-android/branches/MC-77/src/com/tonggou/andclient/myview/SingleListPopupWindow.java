package com.tonggou.andclient.myview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.tonggou.andclient.R;
import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.util.ScreenSizeUtil;

/**
 * 单列表的弹出框
 * <p>ContentView 是单个 ListView 的PopupWindow
 * @author lwz
 *
 */
public class SingleListPopupWindow implements OnItemClickListener {
	private Context mContext;
	private PopupWindow mPopupWindow;
	private ListView mListView;
	private ListAdapter mAdapter;
	
	private ScreenSizeUtil screenSizeUtil;
	private OnItemClickListener mOnItemClickListener;
	private int mBackgroundRes = R.drawable.shopschooseback2;
	private int mItemWidth;
	private int mItemHeight;
	
	public SingleListPopupWindow(Context context, ListAdapter adapter) {
		mContext = context;
		mAdapter = adapter;
		screenSizeUtil = new ScreenSizeUtil(context);
		init();
	}
	
	private void init() {
		View contentView = View.inflate(mContext, R.layout.popup_single_list, null);
		mListView = (ListView) contentView.findViewById(R.id.single_list);
		mListView.setEmptyView( contentView.findViewById(R.id.loading_indicator) );
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
		
		mPopupWindow = new PopupWindow(contentView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		setBackground(R.drawable.shopschooseback2);
		if( mBackgroundRes > 0 ) {
			mPopupWindow.setBackgroundDrawable(mContext.getResources().getDrawable(mBackgroundRes));
		}
		mPopupWindow.setFocusable(true);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.update();
	}
	
	/**
	 * 设置背景图片
	 * @param resId
	 */
	public void setBackground(int resId) {
		mBackgroundRes = resId;
	}
	
	/**
	 * 设置 ListView item 的宽高，popupWindow 将以此为标准显示
	 * @param width
	 * @param height
	 */
	public void setListItemWidthAndHeight(int width, int height) {
		mItemWidth = width;
		mItemHeight = height;
	}
	
	/**
	 * 获得 ListItem 的宽度，该值有默认值
	 * @return
	 */
	public int getListItemWidth() {
		if( mItemWidth <= 0 ) {
			mItemWidth = mContext.getResources().getDimensionPixelSize(R.dimen.popup_list_width);
		} 
		return mItemWidth;
	}
	
	/**
	 * 获得 ListItem 的高度，该值有默认值
	 * @return
	 */
	public int getListItemHeight() {
		if( mItemHeight <= 0 ) {
			mItemHeight = mContext.getResources().getDimensionPixelSize(R.dimen.popup_list_child_height);
		} 
		return mItemHeight;
	}
	
	/**
	 * 在锚点的下方显示弹出框
	 * @param anchor 锚点
	 */
	public void showAsDropDown(View anchor) {
		if( mPopupWindow == null ) {
			init();
		}
		
		int[] location = new int[]{0, 0};
		anchor.getLocationInWindow(location);
		int listViewWidth = getListItemWidth();
		int listViewHeight = getMeasureHeight(mListView);
		TongGouApplication.showLog(screenSizeUtil.getScreenHeight() - location[1] + "  " + listViewHeight);
		if( screenSizeUtil.getScreenHeight() - location[1] > listViewHeight ) {
			mPopupWindow.setHeight(listViewHeight);
		}
		if( screenSizeUtil.getScreenWidth() > listViewWidth ) {
			mPopupWindow.setWidth(listViewWidth);
		}
		mPopupWindow.showAsDropDown(anchor);
	}
	
	/**
	 * 计算 listView 的高度
	 * @param list
	 * @return
	 */
	private int getMeasureHeight(ListView list) {
        ListAdapter adapter = list.getAdapter();
        int childCount = adapter.getCount();
        int listDividerHeight = list.getDividerHeight();
        return (getListItemHeight() + listDividerHeight) * ( childCount + 1 );
	}
	
	/**
	 * 设置 listView 的点击监听
	 * @param l
	 */
	public void setOnListItemClickListener(final OnItemClickListener l) {
		mOnItemClickListener = l;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
		mPopupWindow.dismiss();
		mPopupWindow = null;
		if( mOnItemClickListener != null ) {
			mOnItemClickListener.onItemClick(arg0, arg1, pos, arg3);
		}
	}

	public void dismiss() {
		if( mPopupWindow != null && mPopupWindow.isShowing() ) {
			mPopupWindow.dismiss();
		}
	}
}
