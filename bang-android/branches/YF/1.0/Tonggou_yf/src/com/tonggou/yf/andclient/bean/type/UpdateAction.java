package com.tonggou.yf.andclient.bean.type;

/**
 * 更新动作
 * @author lwz
 *
 */
public enum UpdateAction {
	/**
	 * 正常状态，不更新
	 */
	normal,
	/**
	 * 提示。有新版本，可以更新
	 */
	alert,
	/**
	 * 强制更新
	 */
	force
	
}
