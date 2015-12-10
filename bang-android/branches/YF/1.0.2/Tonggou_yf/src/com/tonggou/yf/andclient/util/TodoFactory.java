package com.tonggou.yf.andclient.util;

import android.content.res.Resources;

import com.tonggou.yf.andclient.R;
import com.tonggou.yf.andclient.bean.type.TodoType;
import com.tonggou.yf.andclient.ui.fragment.AbsTodoFragment;
import com.tonggou.yf.andclient.ui.fragment.AppointTodoFragment;
import com.tonggou.yf.andclient.ui.fragment.DTCTodoFragment;
import com.tonggou.yf.andclient.ui.fragment.MaintainTodoFragment;

public class TodoFactory {
	
	private TodoFactory() {
	}
	
	public static AbsTodoFragment<?> getTodoFragment(TodoType type) {
		switch (type) {
			case APPOINTMENT:
				return new AppointTodoFragment();
			case MAINTAIN:
				return new MaintainTodoFragment();
			default:
				return new DTCTodoFragment();
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
