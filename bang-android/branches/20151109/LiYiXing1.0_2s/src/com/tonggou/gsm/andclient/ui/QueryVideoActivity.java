package com.tonggou.gsm.andclient.ui;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.tonggou.gsm.andclient.App;
import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.UserBaseInfo;
import com.tonggou.gsm.andclient.net.parser.AsyncJsonBaseResponseParseHandler;
import com.tonggou.gsm.andclient.net.request.QueryMonitorPictureRequest;
import com.tonggou.gsm.andclient.net.response.QueryMonitorPictureResponse;
import com.tonggou.gsm.andclient.ui.fragment.QueryVideoFragment;
import com.tonggou.gsm.andclient.ui.view.SimpleTitleBar;

/**
 * 视频查看页面
 * @author peter
 *
 */
public class QueryVideoActivity extends BackableTitleBarActivity implements OnClickListener {
	QueryVideoFragment fragment;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_query_video_frame);
	}

	@Override
	protected void onStart() {
		super.onStart();
		fragment = (QueryVideoFragment) getSupportFragmentManager().findFragmentById(R.id.query_video_frame);
	}

	@Override
	protected void onTitleBarCreated(SimpleTitleBar titleBar) {
		super.onTitleBarCreated(titleBar);
		titleBar.setTitle(R.string.title_query_video);
		titleBar.setRightImageButton(R.drawable.ic_titlebar_userinfo, android.R.color.transparent)
			.setOnRightButtonClickListener(this);
	}

	@Override
	public void onClick(View v) {
		QueryMonitorPictureRequest request = new QueryMonitorPictureRequest();
		request.setApiParams(UserBaseInfo.getVehicleInfo().getVehicleId());
		request.doRequest(this, new AsyncJsonBaseResponseParseHandler<QueryMonitorPictureResponse>() {

			@Override
			public void onParseSuccess(QueryMonitorPictureResponse result, String originResult) {
				super.onParseSuccess(result, originResult);

				if (result.getData() == 1) {
					App.showShortToast(result.getMessage());
					fragment.getVideoRefreshViewLoadMoreProxy().refreshing();
				} else if (result.getData() == 0) {
					App.showShortToast(result.getMessage());
				}
			}

			@Override
			public void onFinish() {
				super.onFinish();
			}

			@Override
			public Class<QueryMonitorPictureResponse> getTypeClass() {
				return QueryMonitorPictureResponse.class;
			}
		});
	}
}