package com.tonggou.yf.andclient.util;

import android.content.res.Resources;

import com.tonggou.yf.andclient.R;
import com.tonggou.yf.andclient.bean.type.TodoType;
import com.tonggou.yf.andclient.ui.fragment.AbsTodoFragment;
import com.tonggou.yf.andclient.ui.fragment.AppointTodoFragment_;
import com.tonggou.yf.andclient.ui.fragment.DTCTodoFragment_;
import com.tonggou.yf.andclient.ui.fragment.MaintainTodoFragment_;

public class TodoFactory {
	
	private TodoFactory() {
	}
	
	public static AbsTodoFragment<?> getTodoFragment(TodoType type) {
		switch (type) {
			case APPOINTMENT:
				return AppointTodoFragment_.builder().build();
			case MAINTAIN:
				return MaintainTodoFragment_.builder().build();
			default:
				return DTCTodoFragment_.builder().build();
			}
	}
	
	public static String getTodoTitle(Resources res, TodoType type) {
		switch (type) {
			case APPOINTMENT:
				return res.getString(R.string.title_appointment);
			case MAINTAIN:
				return res.getString(R.string.title_maintain);
			default:
				return res.getString(R.string.title_dtc);
			}
	}
	
	public static String[] getTodoTitles(Resources res, TodoType[] types) {
		String[] titles = new String[types.length];
		for( int i=0; i<types.length; i++ ) {
			titles[i] = getTodoTitle(res, types[i]);
		}
	 	return titles;
	}
}
