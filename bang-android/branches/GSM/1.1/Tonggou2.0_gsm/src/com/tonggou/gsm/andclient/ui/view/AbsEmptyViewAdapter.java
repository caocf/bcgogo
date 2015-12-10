package com.tonggou.gsm.andclient.ui.view;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.WrapperListAdapter;

import com.tonggou.gsm.andclient.R;

public abstract class AbsEmptyViewAdapter<T> extends AbsViewHolderAdapter<T> {

	public AbsEmptyViewAdapter(Context context, int layoutRes) {
		super(context, layoutRes);
	}

	public AbsEmptyViewAdapter(Context context, List<T> data, int layoutRes) {
		super(context, data, layoutRes);
	}

	@Override
	public T getItem(int position) {
		if( isDataEmpty() ) {
			return null;
		}
		return super.getItem(position);
	}

	@Override
	public int getCount() {
		return super.getCount() == 0 ? 1 : super.getCount();
	}
	
	public boolean isDataEmpty() {
		return mData == null || mData.isEmpty();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (isDataEmpty()) {
			return mLayoutInflater.inflate(getNodataLayoutRes(), parent, false);
		}
		if( convertView == null || convertView.getTag() == null ) {
			convertView = View.inflate(mContext, mLayoutRes, null);
		}
		mCurrentConvertView = convertView;
		bindData(position, getItem(position));
		
		return convertView;
	}
	
	public int getNodataLayoutRes() {
		return R.layout.widget_no_data_view;
	}
	
	/**
	 * 当使用 AbsEmptyViewAdapter 适配器时，应当使用该点击监听保护类。
	 * 该类会自动判断当前数据源属否为 空，防止点击 emptyView 引发的问题 
	 * @author lwz
	 *
	 */
	public static class OnItemClickListenerWrapper implements OnItemClickListener {

		private OnItemClickListener mListener;
		
		public OnItemClickListenerWrapper(OnItemClickListener l) {
			mListener = l;
		}
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Adapter adapter = parent.getAdapter();
			if( adapter instanceof WrapperListAdapter ) {
				ListAdapter listAdapter = ((WrapperListAdapter)adapter).getWrappedAdapter();
				if( listAdapter instanceof AbsEmptyViewAdapter<?>) {
					if( ((AbsEmptyViewAdapter<?>)listAdapter).isDataEmpty()) {
						return;
					}
				}
			} 
			mListener.onItemClick(parent, view, position, id);
			
			
		}
		
	}
	
}
