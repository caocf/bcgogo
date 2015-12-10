package com.tonggou.yf.andclient.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.tonggou.lib.net.response.BaseResponse;
import com.tonggou.yf.andclient.App;
import com.tonggou.yf.andclient.R;
import com.tonggou.yf.andclient.bean.type.TodoType;
import com.tonggou.yf.andclient.net.TonggouResponseParseHandler;
import com.tonggou.yf.andclient.net.request.AcceptAppointRequest;
import com.tonggou.yf.andclient.net.request.QuerySmsTempletRequest;
import com.tonggou.yf.andclient.net.request.RemindHandleRequest;
import com.tonggou.yf.andclient.net.request.SendSmsRequest;
import com.tonggou.yf.andclient.net.response.SmsTempletResponse;
import com.tonggou.yf.andclient.ui.MainActivity;
import com.tonggou.yf.andclient.util.TelephonyUtil;
import com.tonggou.yf.andclient.widget.AbsTodoAdapter;

public abstract class AbsTodoFragment<T> extends AbsLazyLoadRefreshListFragment {

	ListView mListView;
	AbsTodoAdapter<T> mAdapter;
	AlertDialog mSendSmsDialog;
	
	@Override
	public int getLayoutRes() {
		return R.layout.fragment_refresh_list;
	}
	
	@Override
	int getPullToRefreshViewId() {
		return R.id.ptr_listView;
	}

	@Override
	void afterViews() {
		super.afterViews();
		mListView = mRefreshListView.getRefreshableView();
	}
	
	@Override
	abstract AbsTodoAdapter<T> createAdapter();
	
	@SuppressWarnings("unchecked")
	@Override
	final void afterAdapterCreated(ListAdapter adapter) {
		mAdapter = (AbsTodoAdapter<T>) adapter;
		releaseHandleBtnlock();
	}
	
	@Override
	public void onRefresh(int page) {
		super.onRefresh(page);
		releaseHandleBtnlock();
	}

	@Override
	public void onLoadMore(int page) {
		super.onLoadMore(page);
		releaseHandleBtnlock();
	}

	// TODO sync
	public void animateDismiss(final int pos) {
		final int actionPos = pos - mListView.getFirstVisiblePosition() + mListView.getHeaderViewsCount();
		final View view = mListView.getChildAt(actionPos);
		final ViewGroup.LayoutParams lp = view.getLayoutParams();
		final int originWidth = view.getWidth();
		final int originHeight = view.getHeight();
		
		Animator translationXAnim = ObjectAnimator.ofFloat(view, "x", 0, -originWidth);
		Animator alphaAnim = ObjectAnimator.ofFloat(view, "alpha", 1, 0);
		ValueAnimator layoutParamYAnim = ValueAnimator.ofInt(originHeight, 0);
		layoutParamYAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator anim) {
				lp.height = (Integer) anim.getAnimatedValue();
				view.setLayoutParams(lp);
			}
		});
		
		AnimatorSet animSet = new AnimatorSet();
		animSet.setDuration(1000);
		animSet.setTarget(view);
		animSet.setInterpolator(new AccelerateInterpolator());
		animSet.playTogether(translationXAnim, alphaAnim, layoutParamYAnim);
		layoutParamYAnim.setStartDelay(200);
		animSet.addListener(new Animator.AnimatorListener() {
			
			@Override
			public void onAnimationStart(Animator arg0) {}
			
			@Override
			public void onAnimationRepeat(Animator arg0) {}
			
			@Override
			public void onAnimationCancel(Animator arg0) {}
			
			@Override
			public void onAnimationEnd(Animator arg0) {
				// 还原 View 的状态
				lp.height = originHeight;
				view.setLayoutParams(lp);
				ViewHelper.setX(view, 0);
				ViewHelper.setAlpha(view, 1);
				
				mAdapter.getData().remove(pos);
				mAdapter.notifyDataSetChanged();
			}
			
		});
		animSet.start();
	}
	
	public void doLoadSmsTemplet(final TodoType type, final String idStr) {
		showLoadingDialog(R.string.info_loading_sms_templet);
		QuerySmsTempletRequest request = new QuerySmsTempletRequest();
		request.setApiParams(type, idStr);
		request.doRequest(getActivity(), new TonggouResponseParseHandler<SmsTempletResponse>() {

			@Override
			public void onParseSuccess(SmsTempletResponse result, String originResult) {
				super.onParseSuccess(result, originResult);
				App.debug(result.getMsgTemplet());
				showMsgDialog(type, idStr, result.getMsgTemplet());
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				onHandleFininsh();
			}
			
			@Override
			public Class<SmsTempletResponse> getTypeClass() {
				return SmsTempletResponse.class;
			}
		});
		
	}
	
	private void showMsgDialog(final TodoType type, final String idStr, final String msg) {
		dismissSendMsgDialog();
		mSendSmsDialog = new AlertDialog.Builder(getActivity())
			.setTitle(R.string.dialog_title_sms_templet)
			.setMessage(msg)
			.setPositiveButton(R.string.dialog_btn_send_sms, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					sendSms(type, idStr);
				}
			})
			.create();
		mSendSmsDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				releaseHandleBtnlock();
			}
		});
		if( getActivity() != null && !getActivity().isFinishing() ) {
			mSendSmsDialog.show();
		}
	}
	
	private void dismissSendMsgDialog() {
		if( mSendSmsDialog != null && mSendSmsDialog.isShowing() ) {
			mSendSmsDialog.dismiss();
		}
		mSendSmsDialog = null;
	}
	
	private void sendSms(final TodoType type, final String idStr) {
		showLoadingDialog(R.string.info_loading_send_sms);
		SendSmsRequest request = new SendSmsRequest();
		request.setRequestParams(type, idStr);
		request.doRequest(getActivity(), new TonggouResponseParseHandler<BaseResponse>() {

			@Override
			public void onParseSuccess(BaseResponse result, String originResult) {
				super.onParseSuccess(result, originResult);
				App.showShortToast(getString(R.string.info_send_sms_success));
			}
			
			@Override
			public void onParseFailure(String errorCode, String errorMsg) {
				super.onParseFailure(errorCode, errorMsg);
				App.showShortToast(getString(R.string.info_send_sms_failure));
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				onHandleFininsh();
			}
			
			@Override
			public Class<BaseResponse> getTypeClass() {
				return BaseResponse.class;
			}
		});
	}
	
	public void doRemindHandle(final TodoType type, final String idStr, final Runnable handleSuccessCallback) {
		showLoadingDialog(R.string.info_loading_handle);
		RemindHandleRequest request = new RemindHandleRequest();
		request.setRequestParams(type, idStr);
		request.doRequest(getActivity(), new TonggouResponseParseHandler<BaseResponse>() {

			@Override
			public void onParseSuccess(BaseResponse result, String originResult) {
				super.onParseSuccess(result, originResult);
				App.showLongToast(getString(R.string.info_handle_success));
				if( handleSuccessCallback != null ) {
					handleSuccessCallback.run();
				}
			}
			
			@Override
			public void onParseFailure(String errorCode, String errorMsg) {
				super.onParseFailure(errorCode, errorMsg);
				App.showShortToast(getString(R.string.info_handle_failure));
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				onHandleFininsh();
			}
			
			@Override
			public Class<BaseResponse> getTypeClass() {
				return BaseResponse.class;
			}
		});
	}
	
	public void doAcceptAppoint(final String idStr, final Runnable handleSuccessCallback) {
		showLoadingDialog(R.string.info_loading_handle);
		AcceptAppointRequest request = new AcceptAppointRequest();
		request.setRequestParams(idStr);
		request.doRequest(getActivity(), new TonggouResponseParseHandler<BaseResponse>() {

			@Override
			public void onParseSuccess(BaseResponse result, String originResult) {
				super.onParseSuccess(result, originResult);
				App.showLongToast(getString(R.string.info_accept_success));
				if( handleSuccessCallback != null ) {
					handleSuccessCallback.run();
				}
			}
			
			@Override
			public void onParseFailure(String errorCode, String errorMsg) {
				super.onParseFailure(errorCode, errorMsg);
				App.showShortToast(getString(R.string.info_accept_failure));
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				onHandleFininsh();
			}
			
			@Override
			public Class<BaseResponse> getTypeClass() {
				return BaseResponse.class;
			}
		});
	}
	
	public void doPhoneCall(final String mobile) {
		if( !TelephonyUtil.isSIMCardEnable(getActivity()) ) {
			App.showShortToast(getString(R.string.info_unfind_sim_card));
			return;
		}
		TelephonyUtil.phoneCall(getActivity(), mobile);
		releaseHandleBtnlock();
	}
	
	public MainActivity getMainActivity() {
		return (MainActivity)getActivity();
	}
	
	@Override
	void onHandleFininsh() {
		super.onHandleFininsh();
		dismissLoadingDialog();
		releaseHandleBtnlock();
	}
	
	void releaseHandleBtnlock() {
		if( mAdapter != null )
			mAdapter.releaseHandleBtnlock();
	}
}
