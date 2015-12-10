package com.tonggou.gsm.andclient.util;

import java.lang.reflect.Array;

import android.os.Parcelable;

public class ParcelableUtil {
	
	@SuppressWarnings( "unchecked" )
	public static <T extends Parcelable> T[] castParcelableArray(Class<T> clazz, Parcelable[] parcelableArray) {
	    final int length = parcelableArray.length;
	    final T[] array = (T[]) Array.newInstance(clazz, length);
	    for (int i = 0; i < length; i++) {
	        array[i] = (T) parcelableArray[i];
	    }
	    return array;
	}
}
