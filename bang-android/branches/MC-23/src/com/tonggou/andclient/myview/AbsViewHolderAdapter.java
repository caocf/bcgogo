package com.tonggou.andclient.myview;

import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 对象适配器 抽象类
 * 
 * @author lwz
 *
 * @param <T>
 */
public abstract class AbsViewHolderAdapter<T> extends BaseAdapter {
	
	private Context mContext;
	private List<T> mData;
	private int mLayoutRes;
	
	public AbsViewHolderAdapter(Context context, List<T> data, int layoutRes) {
		mContext = context;
		mData = data;
		mLayoutRes = layoutRes;
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public T getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public List<T> getData() {
		return mData;
	}
	
	/**
	 * 用新数据替换所有的旧数据
	 * @param newData
	 */
	public synchronized void update(Collection<? extends T> newData) {
		mData.clear();
		if( newData != null ) {
			mData.addAll(newData);
		}
		notifyDataSetChanged();
	}
	
	/**
	 * 直接将mData指向newData对象
	 * @param newData
	 */
	public void quickUpdate (Collection<? extends T> newData) {
		mData=(List<T>) newData;
		notifyDataSetChanged();
	}
	
	
	/**
	 * 在原有数据的基础上再添加数据
	 * @param appendData
	 */
	public synchronized void append(Collection<? extends T> appendData) {
		if( appendData == null || appendData.isEmpty() ) {
			return;
		}
		mData.addAll(appendData);
		notifyDataSetChanged();
	}
	
	/**
	 * 添加一个数据
	 * @param item
	 */
	public synchronized void add(T item) {
		mData.add(item);
		notifyDataSetChanged();
	}
	
	/**
	 * 清空所有数据
	 */
	public void clear() {
		mData.clear();
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if( convertView == null ) {
			convertView = View.inflate(mContext, mLayoutRes, null);
		}
		
		setData(position, convertView, getItem(position));
		
		return convertView;
	}
	
	abstract protected void setData(int pos, View convertView, T itemData);

	public <K extends View> K getViewFromHolder( View convertView, int id ) {
		return ViewHolder.getView(convertView, id);
	}
}
