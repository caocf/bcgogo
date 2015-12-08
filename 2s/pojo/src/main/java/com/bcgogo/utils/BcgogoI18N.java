package com.bcgogo.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.ResourceBundle;


/**
 * Created by IntelliJ IDEA.
 * User: zhuyinjia
 * Date: 11-12-30
 * Time: 上午10:07
 * To change this template use File | Settings | File Templates.
 */
public class BcgogoI18N {
  private static final Logger LOG = LoggerFactory.getLogger(BcgogoI18N.class);

	/*
	 * 根据Key获得资源包对应Value
	 */
	public static String getMessageByKey(String key) {
		Locale locale = Locale.getDefault();
		ResourceBundle bundle = ResourceBundle.getBundle("bcgogo", locale);
		String message = bundle.getString(key);
		return message;
	}

	// 根据SESSION LOCAL 获得资源文件
	public static String getMessageByKey(String key, Locale sessionLocale) {
		Locale locale;
		if (sessionLocale == null) {
			locale = Locale.getDefault();
		} else {
			locale = sessionLocale;
		}
		ResourceBundle bundle = ResourceBundle.getBundle("bcgogo", locale);
		String message = key;
		try {
			message = bundle.getString(key);
		} catch (java.util.MissingResourceException ex) {
			LOG.error("get resource error! key:"+ message, ex);
		}
		return message;
	}

	// 获得异常信息
	public static String getErrorMessage(String key, Locale sessionLocale) {
		String message = key;
		Locale locale;
		if (sessionLocale == null) {
			locale = Locale.getDefault();
		} else {
			locale = sessionLocale;
		}
		ResourceBundle bundle = ResourceBundle.getBundle("bcgogo_exception",locale);
		try {
			message = bundle.getString(key);
		} catch (java.util.MissingResourceException ex) {
			LOG.error("get resource error! key:"+ message, ex);
		}
		return message;
	}

}
