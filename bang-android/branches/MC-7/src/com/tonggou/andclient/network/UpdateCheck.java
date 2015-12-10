package com.tonggou.andclient.network;

import android.content.Context;


public abstract class UpdateCheck {
	protected Context context;
	
	public UpdateCheck(Context context) {
		this.context = context;
	}
	/**
     * @return false 表示检查到强制更新,true表是正常走登陆流程，版本检查结束.
     */
	abstract public boolean checkUpgradeAction();
}
