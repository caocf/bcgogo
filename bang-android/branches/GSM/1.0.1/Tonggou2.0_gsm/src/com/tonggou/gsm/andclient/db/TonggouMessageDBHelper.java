package com.tonggou.gsm.andclient.db;

import android.content.Context;

import com.tonggou.gsm.andclient.bean.TGMessage;

public class TonggouMessageDBHelper extends AbsSingleTableDatebaseHelper {

	public TonggouMessageDBHelper(Context context) {
		super(context, TGMessage.DB_NAME, TGMessage.DB_VERSION);
	}

	@Override
	Class<?> getTableClass() {
		return TGMessage.class;
	}

}
