package com.tonggou.gsm.andclient.service;

import java.util.ArrayList;

import org.apache.http.Header;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.view.View;

import com.tonggou.gsm.andclient.App;
import com.tonggou.gsm.andclient.Constants;
import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.UserBaseInfo;
import com.tonggou.gsm.andclient.bean.TGMessage;
import com.tonggou.gsm.andclient.db.dao.TonggouMessageDao;
import com.tonggou.gsm.andclient.net.parser.AsyncJsonBaseResponseParseHandler;
import com.tonggou.gsm.andclient.net.request.QueryMessageRequest;
import com.tonggou.gsm.andclient.net.response.QueryMesageResponse;
import com.tonggou.gsm.andclient.ui.DTCManagerActivity;
import com.tonggou.gsm.andclient.ui.MessageManagerActivity;
import com.tonggou.gsm.andclient.ui.view.SingleButtonDialog;
import com.tonggou.gsm.andclient.util.ContextUtil;
import com.tonggou.gsm.andclient.util.HandlerTimer;

public class PollingMessageService extends BaseService implements HandlerTimer.OnHandleTimerListener {

	private final String TAG = this.getClass().getSimpleName();
	public static final String EXTRA_POLLING_MSG_STATUS = "extra_polling_msg_status";
	
	private final int TIMER_TOKEN_POLLING_MESSAGE = 0x111;
	private final int INTERVAL_POLLING_MESSAGE = Constants.APP_CONFIG.POLLING_MESSAGE_INTERVAL;	// 轮询消息间隔时间
	
	// notification
	private final int NOTIFICATION_PENDING_REQUEST_CODE = 0x888;
	private final int NOTIFICATION_ID = 0x123;
	
	private HandlerTimer mPollingTimer;		// 轮询计时器
	private SingleButtonDialog mDTCMessageDialog;
	
	public static enum Status {
		/** 开始轮询 */
		START,
		/** 停止轮询 */
		STOP
	}
	
	private static void startPollingService(Context context, Status status) {
		Intent service = new Intent(context, PollingMessageService.class);
		service.putExtra(PollingMessageService.EXTRA_POLLING_MSG_STATUS, status);
		context.startService(service);
	}
	
	public static void startPolling(Context context) {
		startPollingService(context, Status.START);
	}
	
	public static void stopPolling(Context context) {
		startPollingService(context, Status.STOP);
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		mPollingTimer = new HandlerTimer(TIMER_TOKEN_POLLING_MESSAGE, this);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if( intent != null && intent.hasExtra(EXTRA_POLLING_MSG_STATUS)) {
			Object status = intent.getSerializableExtra(EXTRA_POLLING_MSG_STATUS);
			if( status instanceof Status ) {
				if( status == Status.STOP ) {
					mPollingTimer.stop(); 
					stopSelf(); 
					App.debug(TAG, "stopPolling");
				} else {
					startPolling();
				}
			} else {
				startPolling();
				App.debug(TAG, "startPolling");
			}
		}
		return START_STICKY;
	}
	
	private void startPolling() {
		if( !mPollingTimer.hasMessages(TIMER_TOKEN_POLLING_MESSAGE) ) {
			mPollingTimer.start(5000, INTERVAL_POLLING_MESSAGE);
			App.debug(TAG, "startPolling");
		} else {
			App.debug(TAG, "polling running");
		}
	}

	@Override
	public void onHandleTimerMessage(int token, Message msg) {
		if( token == TIMER_TOKEN_POLLING_MESSAGE ) {
			requestData();
		}
	}

	private void requestData() {
		App.debug(TAG, "request message");
		QueryMessageRequest request = new QueryMessageRequest();
		final String userNo = UserBaseInfo.getUserInfo().getUserNo();
		request.setApiParams( userNo );
		request.doRequest(this, new AsyncJsonBaseResponseParseHandler<QueryMesageResponse>() {

			@Override
			public void onParseSuccess(QueryMesageResponse result, String originResult) {
				super.onParseSuccess(result, originResult);
				ArrayList<TGMessage> msgs = result.getMessageList();
				if( msgs == null ) {
					return;
				}
				storeMessageToDB( userNo, msgs );
				notifyMessage(result.getMessageList().size());
			}
			
			@Override
			public void onParseFailure(String errorCode, String errorMsg) {
				// 登录过期就停止轮询
				if( String.valueOf(Constants.NETWORK_STATUS_CODE.CODE_LOGIN_EXPIRE)
						.equalsIgnoreCase(errorCode) ) {
					stopSelf();
				}
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					String responseBody, Throwable error) {
				// do-nothing
			}
			
			@Override
			public Class<QueryMesageResponse> getTypeClass() {
				return QueryMesageResponse.class;
			}
		});
	}
	
	/**
	 * 存储消息到数据库
	 * @param msgs
	 */
	private void storeMessageToDB(String userNo, ArrayList<TGMessage> msgs) {
		int dtcMsgCount = TonggouMessageDao.insertMessages(this, userNo, msgs);
		MessageBroadcastReceiver.sendBroadcast(this);
		showDTCDialog( dtcMsgCount );
	}
	
	/**
	 * 通知有消息
	 * @param msgCount
	 */
	public void notifyMessage(int msgCount) {
		if( msgCount > 0 ) {
			showNotification(msgCount);
		}
	}
	
	/**
	 * 显示 故障码消息对话框
	 * <p>故障吗消息条数大于 0 时，显示对话框
	 * @param dtcMsgCount
	 */
	public void showDTCDialog(int dtcMsgCount) {
		if( dtcMsgCount <= 0 ) {
			return;
		}
		dismissDTCDialog();
		mDTCMessageDialog = new SingleButtonDialog(this);
		mDTCMessageDialog.showDialog(
				getString(R.string.dialog_dtc_message_content, dtcMsgCount), 
				getString(R.string.dialog_dtc_message_confirm), 
				new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						toDTCManagerActivity();
					}
				});
	}
	
	public void toDTCManagerActivity() {
		ContextUtil.startActivity(this, DTCManagerActivity.class, Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
	}
	
	public void dismissDTCDialog() {
		if( mDTCMessageDialog != null && mDTCMessageDialog.isShowing()) {
			mDTCMessageDialog.dismiss();
		}
		mDTCMessageDialog = null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		dismissDTCDialog();
		if( mPollingTimer != null ) {
			mPollingTimer.stop();
		}
		mPollingTimer = null;
		App.debug(TAG, "onDestroy");
	}
	
	
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private synchronized void showNotification(int msgCount) {
		
		Intent intent = new Intent(this, MessageManagerActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
		PendingIntent clickIntent = PendingIntent.getActivity(this, NOTIFICATION_PENDING_REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT); 
		
		Notification notification = null;
		String appNameStr = getString(R.string.app_name);
		String titleText = getString(R.string.content_title_notification, appNameStr);
		String contentText = getString(R.string.content_text_notification, msgCount);
		String tickerText = getString(R.string.content_ticker_notification, appNameStr);
		
		if( VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB ) {
			NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
				.setContentTitle(titleText)
				.setContentText(contentText)
				.setSmallIcon(R.drawable.ic_launcher) 
				.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
				.setContentIntent(clickIntent)
				.setTicker(tickerText)
				.setAutoCancel(true);
				builder.setDefaults(Notification.DEFAULT_SOUND);
			notification = builder.build();
			
		} else {
			notification = new Notification();
			notification.icon = R.drawable.ic_launcher;
			notification.defaults = Notification.DEFAULT_SOUND;
			notification.flags = Notification.FLAG_AUTO_CANCEL;
			notification.tickerText = tickerText;
			notification.setLatestEventInfo(this, titleText, contentText, clickIntent);
		}
		
		NotificationManager notificationManager = 
				(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.cancel(NOTIFICATION_ID);
		notificationManager.notify(NOTIFICATION_ID, notification);
	}
}
