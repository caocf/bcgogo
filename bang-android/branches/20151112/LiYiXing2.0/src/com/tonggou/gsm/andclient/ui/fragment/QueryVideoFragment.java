package com.tonggou.gsm.andclient.ui.fragment;

import java.util.ArrayList;
import org.joda.time.DateTime;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.UserBaseInfo;
import com.tonggou.gsm.andclient.bean.VideoPictureDTO;
import com.tonggou.gsm.andclient.bean.type.PollingStatus;
import com.tonggou.gsm.andclient.bean.type.VideoDTOType;
import com.tonggou.gsm.andclient.db.dao.TonggouVideoPictureDao;
import com.tonggou.gsm.andclient.net.parser.AsyncJsonBaseResponseParseHandler;
import com.tonggou.gsm.andclient.net.request.QueryVideoPictureRequest;
import com.tonggou.gsm.andclient.net.response.QueryVideoRecordResponse;
import com.tonggou.gsm.andclient.ui.MonitorPictureActivity;
import com.tonggou.gsm.andclient.ui.QueryVideoPlayActivity;
import com.tonggou.gsm.andclient.ui.view.AbsEmptyViewAdapter;
import com.tonggou.gsm.andclient.ui.view.VideoRefreshViewLoadMoreProxy;
import com.tonggou.gsm.andclient.ui.view.VideoRefreshViewLoadMoreProxy.OnLoadDataActionListener;
import com.tonggou.gsm.andclient.util.ContextUtil;

/**
 * 视频查询 Fragment
 * @author peter
 *
 */
public class QueryVideoFragment extends BaseFragment
		implements OnItemClickListener, OnLoadDataActionListener{

	private final static int GET_COUNT = 5;
	private PullToRefreshListView mQueryVideoList;
	private VideoRefreshViewLoadMoreProxy mLoadMoreProxy;
	private QueryVideoAdapter mAdapter;
	private ArrayList<VideoPictureDTO> arryVideoList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_query_video, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		arryVideoList = new ArrayList<VideoPictureDTO>();

		mQueryVideoList = findViewById(R.id.query_video_list);
		mQueryVideoList.getRefreshableView().setDividerHeight(0);
		mQueryVideoList.getRefreshableView().setSelector(new ColorDrawable(Color.TRANSPARENT));

		mAdapter = new QueryVideoAdapter(mActivity, R.layout.item_list_video);
		mQueryVideoList.setAdapter(mAdapter);

		mLoadMoreProxy = new VideoRefreshViewLoadMoreProxy(mQueryVideoList);
		mLoadMoreProxy.setOnLoadDataActionListener(this);
		mQueryVideoList.getRefreshableView().setOnItemClickListener(
				new AbsEmptyViewAdapter.OnItemClickListenerWrapper(this));

		mLoadMoreProxy.refreshing();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		if (mLoadMoreProxy.isPullingDownMode() != PollingStatus.stop)
			return;

		// 去掉 header 的位置
		final int headerViewCount = mQueryVideoList.getRefreshableView().getHeaderViewsCount();
		if (position < headerViewCount) return;
		final int pos = position - headerViewCount;

		// 跳转到播放界面
		if (null != mAdapter.getItem(pos))
			if (mAdapter.getItem(pos).getUploadProgress() == VideoDTOType.VIDEO_IS_UPLOADED) {
				Bundle args = new Bundle(1);

				QueryVideoRecordResponse videoPictureResponse = new QueryVideoRecordResponse();
				videoPictureResponse.addSingleDriveLog(mAdapter.getItem(pos));

				if (mAdapter.getItem(pos).getVideoType() == VideoDTOType.VIDEO_IS_PICTURE) {

					args.putSerializable(MonitorPictureActivity.EXTRA_PCITURE_PLAY, videoPictureResponse);
					ContextUtil.startActivity(getActivity(), MonitorPictureActivity.class, args);
				} else {

					args.putSerializable(QueryVideoPlayActivity.EXTRA_VIDEO_PLAY, videoPictureResponse);
					ContextUtil.startActivity(getActivity(), QueryVideoPlayActivity.class, args);
				}
			} else {
				Toast.makeText(mActivity, "正在上传！请刷新", Toast.LENGTH_SHORT).show();
			}
	}

	@Override
	public void onRefresh(int page) {
		mAdapter.clear();
		queryFromDB();

		doQueryVideoRecordRequest(UserBaseInfo.getVehicleInfo().getVehicleId(), System.currentTimeMillis(), GET_COUNT);

		return;
	}

	@Override
	public void onLoadMore(int page) {
		queryFromDB();

		if ((mAdapter != null) && (mAdapter.getCount() > 1)) {
			doQueryVideoRecordRequest(UserBaseInfo.getVehicleInfo().getVehicleId(), mAdapter.getItem(mAdapter.getCount() - 1).getRecordDate(), GET_COUNT);
		} else {
			doQueryVideoRecordRequest(UserBaseInfo.getVehicleInfo().getVehicleId(), System.currentTimeMillis(), GET_COUNT);
		}

		return;
	}

	private void queryFromDB() {
		arryVideoList = TonggouVideoPictureDao.queryAll(mActivity);
		mAdapter.update(arryVideoList);
	}

	public VideoRefreshViewLoadMoreProxy getVideoRefreshViewLoadMoreProxy() {
		return mLoadMoreProxy;
	}

	public void doQueryVideoRecordRequest(String vehicleId, long date, int count) {

		QueryVideoPictureRequest request = new QueryVideoPictureRequest();
		request.setApiParams(vehicleId, date, count);
		request.doRequest(mActivity, new AsyncJsonBaseResponseParseHandler<QueryVideoRecordResponse>() {

			@Override
			public void onParseSuccess(QueryVideoRecordResponse result, String originResult) {
				super.onParseSuccess(result, originResult);

				TonggouVideoPictureDao.clearAll(mActivity);
		
				if (mLoadMoreProxy.isPullingDownMode() == PollingStatus.down) {
					arryVideoList = result.getVideoRecordDTOs();
				} else if (mLoadMoreProxy.isPullingDownMode() == PollingStatus.up) {
					arryVideoList.addAll(result.getVideoRecordDTOs());
				}

				mAdapter.update(arryVideoList);
				TonggouVideoPictureDao.insertVideo(mActivity, arryVideoList);
			}

			@Override
			public void onFinish() {
				super.onFinish();
				mLoadMoreProxy.loadDataActionComplete(true);
			}

			@Override
			public Class<QueryVideoRecordResponse> getTypeClass() {
				return QueryVideoRecordResponse.class;
			}
		});
	}

	class QueryVideoAdapter extends AbsEmptyViewAdapter<VideoPictureDTO> {
		int iconId[] = { R.drawable.ic_video_uploading, R.drawable.ic_video_playable};

		public QueryVideoAdapter(Context context, int layoutRes) {
			super(context, layoutRes);
		}

		@Override
		protected void bindData(int pos, VideoPictureDTO itemData) {
			TextView movieTypeText = getViewFromHolder(R.id.movie_type);
			TextView movieTimeText = getViewFromHolder(R.id.movie_time);
			TextView movieAddrText = getViewFromHolder(R.id.movie_addr);
			TextView movieStatusText = getViewFromHolder(R.id.movie_status);
			ImageView screenshot = (ImageView)getViewFromHolder(R.id.movie_screen_shot);

			String movieType = getType(itemData.getVideoType());
			movieTypeText.setText(movieType);

			String movieTime = getFrormatDateTime(itemData.getRecordDate());
			movieTimeText.setText(movieTime);

			String movieAddr = itemData.getRecordPlace();
			movieAddrText.setText(movieAddr);

			String movieUploadProgress = getUploadStatus(itemData.getUploadProgress());
			movieStatusText.setText(movieUploadProgress);

			int id = getDrawId(itemData.getUploadProgress());
			screenshot.setBackgroundResource(id);
		}

		private String getType(int typeId) {
			String type = "";

			if (typeId == 0) {
				type = "停车监控";
			} else if (typeId == 1) {
				type  = "碰撞视频";
			} else if (typeId == 2) {
				type = "主动拍照";
			}

			return type;
		}

		private String getUploadStatus(int progress) {
			String status = "";

			if (progress == 1) {
				status = "上传完成";
			} else {
				status	= "上传中";
			}

			return status;
		}

		private int getDrawId(int progress) {
			int id;

			if (progress != 1) {
				id = iconId[0];
			} else {
				id = iconId[1];
			}

			return id;
		}

		String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";
		protected String getFrormatDateTime(long millis) {
			return new DateTime(millis).toString(dateTimeFormat);
		}
	}
}