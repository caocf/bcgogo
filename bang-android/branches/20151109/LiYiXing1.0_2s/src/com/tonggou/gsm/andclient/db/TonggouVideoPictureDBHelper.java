package com.tonggou.gsm.andclient.db;

import com.tonggou.gsm.andclient.bean.VideoPictureDTO;

import android.content.Context;
/**
 * VideoPictureDBHelper
 * @author peter
 *
 */
public class TonggouVideoPictureDBHelper extends AbsSingleTableDatebaseHelper {

	public TonggouVideoPictureDBHelper(Context context) {
		super(context, VideoPictureDTO.DB_NAME, VideoPictureDTO.DB_VERSION);
	}

	@Override
	Class<?> getTableClass() {
		return VideoPictureDTO.class;
	}
}