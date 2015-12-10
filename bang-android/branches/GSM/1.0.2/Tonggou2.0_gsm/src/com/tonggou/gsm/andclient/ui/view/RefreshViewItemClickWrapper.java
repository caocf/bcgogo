package com.tonggou.gsm.andclient.ui.view;

import android.view.View;
import android.widget.AdapterView;

public class RefreshViewItemClickWrapper {
	
	private RefreshViewItemClickWrapper() {
		
	}
	
	public static class OnItemClickListener implements AdapterView.OnItemClickListener {

		private AdapterView.OnItemClickListener mListener;
		
		public OnItemClickListener(AdapterView.OnItemClickListener l) {
			mListener = l;
		}
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if( mListener != null ) {
				mListener.onItemClick(parent, view, position - 1, id);
			}
		}
		
	}
	
	public static class OnItemLongClickListener implements AdapterView.OnItemLongClickListener {

		private AdapterView.OnItemLongClickListener mListener;
		
		public OnItemLongClickListener(AdapterView.OnItemLongClickListener l) {
			mListener = l;
		}

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			if( mListener != null ) {
				return mListener.onItemLongClick(parent, view, position - 1, id);
			}
			return false;
		}
	}
	
}


