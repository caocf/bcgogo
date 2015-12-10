package com.tonggou.gsm.andclient.db;

import com.tonggou.gsm.andclient.bean.DriveLogDTO;

import android.content.Context;

public class TonggouDriveLogDBHelper extends AbsSingleTableDatebaseHelper {

	public TonggouDriveLogDBHelper(Context context) {
		super(context, DriveLogDTO.DB_NAME, DriveLogDTO.DB_VERSION);
	}

	@Override
	Class<?> getTableClass() {
		return DriveLogDTO.class;
	}

}
