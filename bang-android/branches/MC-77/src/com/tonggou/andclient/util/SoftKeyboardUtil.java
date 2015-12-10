package com.tonggou.andclient.util;

import android.content.Context;
import android.os.IBinder;
import android.view.inputmethod.InputMethodManager;

public class SoftKeyboardUtil {
	
	/**
	 * Òþ²ØÈí¼þ¼üÅÌ
	 * 
	 * @param context
	 * @param windowToken The token of the window that is making the request, as returned by View.getWindowToken().
	 */
	public static void hide(Context context, IBinder windowToken) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE); 
		imm.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_IMPLICIT_ONLY);
	}
}
