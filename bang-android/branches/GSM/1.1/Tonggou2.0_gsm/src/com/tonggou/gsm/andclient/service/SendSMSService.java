package com.tonggou.gsm.andclient.service;

import java.util.List;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;

import com.tonggou.gsm.andclient.App;
import com.tonggou.gsm.andclient.R;

public class SendSMSService {

	private final static String SENT_SMS_ACTION = "SENT_SMS_ACTION";

	public static void send(Context context, String destPhoneNo, String content) {
		registerReceiver(context);
		// 直接调用短信接口发短信
		SmsManager smsManager = SmsManager.getDefault();
		List<String> divideContents = smsManager.divideMessage(content);
		for (String text : divideContents) {
			smsManager.sendTextMessage(destPhoneNo, null, text,
					getSentIntent(context), null);
		}
	}

	private static PendingIntent getSentIntent(Context context) {
		Intent sentIntent = new Intent(SENT_SMS_ACTION);
		return PendingIntent.getBroadcast(context, 0, sentIntent, 0);
	}

	private static void registerReceiver(final Context context) {
		context.registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context _context, Intent _intent) {
				switch (getResultCode()) {
					case Activity.RESULT_OK:
						App.showShortToast(context.getString(R.string.info_sent_success));
						break;
					case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
						break;
					case SmsManager.RESULT_ERROR_RADIO_OFF:
						break;
					case SmsManager.RESULT_ERROR_NULL_PDU:
						break;
				}
			}
		}, new IntentFilter(SENT_SMS_ACTION));
	}

}
