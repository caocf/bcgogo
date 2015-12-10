package com.tonggou.gsm.andclient.ui;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tonggou.gsm.andclient.App;
import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.UserBaseInfo;
import com.tonggou.gsm.andclient.bean.TGMessage;
import com.tonggou.gsm.andclient.bean.type.MessageType;
import com.tonggou.gsm.andclient.db.dao.TonggouMessageDao;
import com.tonggou.gsm.andclient.service.MessageBroadcastReceiver;
import com.tonggou.gsm.andclient.service.MessageBroadcastReceiver.OnGotNewMessageListener;
import com.tonggou.gsm.andclient.ui.view.AbsViewHolderAdapter;
import com.tonggou.gsm.andclient.ui.view.DoubleButtonDialog;
import com.tonggou.gsm.andclient.ui.view.RefreshViewItemClickWrapper;
import com.tonggou.gsm.andclient.ui.view.RefreshViewLoadMoreProxy;
import com.tonggou.gsm.andclient.ui.view.RefreshViewLoadMoreProxy.OnLoadDataActionListener;
import com.tonggou.gsm.andclient.ui.view.SimpleTitleBar;
import com.tonggou.gsm.andclient.util.ContextUtil;
import com.tonggou.gsm.andclient.util.StringUtil;

public class MessageManagerActivity extends BackableTitleBarActivity 
					implements OnGotNewMessageListener, OnItemClickListener, 
						OnLoadDataActionListener, OnItemLongClickListener {
	
	
	PullToRefreshListView mMessageListView;
	RefreshViewLoadMoreProxy mLoadMoreProxy;
	MessageAdapter mAdapter;
	DoubleButtonDialog mDeleteMessageDialog;
	
	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setContentView(R.layout.activity_message_manager);
		
		mMessageListView = (PullToRefreshListView)findViewById(R.id.message_ptr_list);
		mMessageListView.setOnItemClickListener(new RefreshViewItemClickWrapper.OnItemClickListener(this));
		mMessageListView.getRefreshableView().setOnItemLongClickListener(
				new RefreshViewItemClickWrapper.OnItemLongClickListener(this));
		mLoadMoreProxy = new RefreshViewLoadMoreProxy(mMessageListView, Mode.PULL_FROM_END, 1, 1);
		mLoadMoreProxy.setOnLoadDataActionListener(this);
		mAdapter = new MessageAdapter(this, R.layout.item_list_message);
		mMessageListView.setAdapter(mAdapter);
		
		MessageBroadcastReceiver.register(this, this);
		updateMessageList(mLoadMoreProxy.resetPage(), true);
	}
	
	private void updateMessageList(final int pageNo, boolean isRefresh) {
		ArrayList<TGMessage> data = TonggouMessageDao.queryAllMessageAndUpdateToRead(this, UserBaseInfo.getUserInfo().getUserNo(), pageNo);
		mLoadMoreProxy.loadDataActionComplete(false);
		if( data == null || data.isEmpty() ) {
			mLoadMoreProxy.resetPage();
			App.showShortToast(getString(R.string.no_more_data));
		} else {
			if( isRefresh ) {
				mAdapter.update(data);
			} else {
				mAdapter.append(data);
			}
		}
	}
	
	@Override
	protected void onTitleBarCreated(SimpleTitleBar titleBar) {
		super.onTitleBarCreated(titleBar);
		titleBar.setTitle(R.string.title_message_manager);
	}
	
	class MessageAdapter extends AbsViewHolderAdapter<TGMessage> {

		public MessageAdapter(Context context, int layoutRes) {
			super(context, layoutRes);
		}

		@Override
		protected void bindData(int pos, TGMessage itemData) {
			TextView titleText = getViewFromHolder(R.id.title_text);
			TextView timestampText = getViewFromHolder(R.id.timestamp_text);
			TextView contentText = getViewFromHolder(R.id.content_text);
			ImageView clicableIndicator = getViewFromHolder(R.id.item_clickable_indicator);
			
			titleText.setText(itemData.getTitle());
			timestampText.setText(StringUtil.formatDateYYYYMMdd(itemData.getTimestamp()));
			contentText.setText(itemData.getContent());
			if( App.DBG ) {
				contentText.setText(itemData.getContent() + " \n " + itemData.getType());
			}
			clicableIndicator.setVisibility( showIndicator(itemData.getType()) ? View.VISIBLE : View.INVISIBLE);
		}
		
		/**
		 * 根据消息类型来判断是否显示小箭头指示器
		 * @param msgType	
		 * @return true 显示 | false 不显示
		 */
		private boolean showIndicator(MessageType msgType) {
			return msgType == MessageType.OVERDUE_APPOINT_TO_APP
					|| msgType == MessageType.APP_VEHICLE_MAINTAIN_MILEAGE
					|| msgType == MessageType.APP_VEHICLE_MAINTAIN_TIME
					|| msgType == MessageType.VEHICLE_FAULT_2_APP
					|| msgType == MessageType.SHOP_CHANGE_APPOINT
					|| msgType == MessageType.SHOP_ACCEPT_APPOINT
					|| msgType == MessageType.SHOP_FINISH_APPOINT
					|| msgType == MessageType.SHOP_ADVERT_TO_APP
					|| msgType == MessageType.VIOLATE_REGULATION_RECORD_2_APP;
		}
		
	}

	@Override
	public void onGotNewMessage() {
		updateMessageList(mLoadMoreProxy.resetPage(), true);
	}
	
	@Override
	protected void onDestroy() {
		MessageBroadcastReceiver.unregister(this);
		dismissDeleteDialog();
		super.onDestroy();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		TGMessage message = mAdapter.getData().get(position);
		MessageType msgType = message.getType();
		if( msgType == MessageType.OVERDUE_APPOINT_TO_APP
				|| msgType == MessageType.APP_VEHICLE_MAINTAIN_MILEAGE
				|| msgType == MessageType.APP_VEHICLE_MAINTAIN_TIME) {
			// 去预约界面
			ContextUtil.startActivity(this, AppointmentActivity.class);
			
		} else if( msgType == MessageType.VEHICLE_FAULT_2_APP ) {
			// 去故障码界面
			ContextUtil.startActivity(this, DTCManagerActivity.class);
			
		} else if( msgType == MessageType.SHOP_CHANGE_APPOINT
				|| msgType == MessageType.SHOP_ACCEPT_APPOINT) {
			// 去账单列表界面
			ContextUtil.startActivity(this, PayListManagerActivity.class);
			
		} else if( msgType == MessageType.SHOP_FINISH_APPOINT ) {
			// 去详情界面
			String serviceHistoryId =  getServiceHistoryId(message.getParams());
			if( TextUtils.isEmpty(serviceHistoryId) ) {
				return;
			}
			Bundle args = new Bundle();
			args.putString(PaymentDetailActivity.EXTRA_SERVICE_HISTORY_ID, serviceHistoryId);
			ContextUtil.startActivity(this, PaymentDetailActivity.class, args);
			
		} else if( msgType == MessageType.SHOP_ADVERT_TO_APP ) {
			// 去店铺公告列表界面
			ContextUtil.startActivity(this, ShopNoticeActivity.class);
			
		} else if( msgType == MessageType.VIOLATE_REGULATION_RECORD_2_APP ) {
			// 去违章消息列表界面
			ContextUtil.startActivity(this, ViolationQueryActivity.class);
		}
		// else 不做任何操作
	}
	
	/**
	 * 获取服务历史的 id
	 * @param paramsStr
	 * @return
	 */
	private String getServiceHistoryId(String paramsStr) {
		if( TextUtils.isEmpty(paramsStr) ) {
			return null;
		}
		String[] ids = paramsStr.split(",");
		// params 参数格式: 预约单Id,shopId,服务单Id
		if( ids == null || ids.length < 3 ) {
			return null;
		}
		return ids[2];
	}

	@Override
	public void onRefresh(int page) {
		// do-nothing
	}

	@Override
	public void onLoadMore(int page) {
		updateMessageList(page, false);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		showDeleteDialog(position);
		return true;
	}
	
	private void showDeleteDialog(final int pos) {
		// 防止对话框显示了，但是用户没有即使操作，此时列表又更新了，导致的删除消息错位的问题。
		// 所以先把消息的 id 取出
		final String messageId = mAdapter.getData().get(pos).getId();
		dismissDeleteDialog();
		mDeleteMessageDialog = new DoubleButtonDialog(this);
		mDeleteMessageDialog.showDialog(getString(R.string.info_is_delete_message), new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				deleteMessageByMessageId(messageId);
			}
		});
	}
	
	private void deleteMessageByMessageId(String messageId) {
		TonggouMessageDao.deleteMessage(this, messageId);
		updateMessageList(mLoadMoreProxy.resetPage(), true);
	}
	
	private void dismissDeleteDialog() {
		if( mDeleteMessageDialog != null && mDeleteMessageDialog.isShowing() ) {
			mDeleteMessageDialog.dismiss();
		}
		mDeleteMessageDialog = null;
	}
	
}
