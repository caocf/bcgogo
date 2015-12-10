package com.tonggou.gsm.andclient.ui;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.view.View;

import com.baidu.mapapi.map.GraphicsOverlay;
import com.baidu.mapapi.map.MapView;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.bean.GeoPointParcel;
import com.tonggou.gsm.andclient.ui.view.SimpleTitleBar;
import com.tonggou.gsm.andclient.util.BMapUtil;
import com.tonggou.gsm.andclient.util.ParcelableUtil;

/**
 * 行车轨迹回放界面
 * @author lwz
 *
 */
public class DrivingTrackPlaybackActivity extends AbsBackableTitleBarMapActivity implements Handler.Callback {

	public static final String EXTRA_TRACK_POINTS = "extra_track_points";
	
	private final int MSG_DRAW_TRACK = 0x123;
	private final int DRAW_TRACK_INTRTVAL_MILLIS_SLOW = 100;
	private final int DRAW_TRACK_INTRTVAL_MILLIS_NORMAL = 50;
	private final int DRAW_TRACK_INTRTVAL_MILLIS_FAST = 10;
	
	
	private Handler mDrawTrackHandler;
	private GraphicsOverlay mGraphicsOverlay;
	private DrawTrackTask mDrawTrackTask;
	private GeoPointParcel[] mData;
	
	@Override
	protected void onCreate(Bundle savedInstance) {
		if( !restoreExtras(getIntent()) ) {
			restoreExtras(savedInstance);
		}
		super.onCreate(savedInstance);
	}
	
	@Override
	protected boolean restoreExtras(Bundle extra) {
		if( extra != null && extra.containsKey(EXTRA_TRACK_POINTS) ) {
			Parcelable[] parcelableArr = extra.getParcelableArray(EXTRA_TRACK_POINTS);
			mData = ParcelableUtil.castParcelableArray(GeoPointParcel.class, parcelableArr);
			return true;
		}
		return false;
	}
	
	@Override
	protected int getContentView() {
		return R.layout.activity_driving_track_playback;
	}
	
	protected int getMapFragmentId() {
		return R.id.driving_track_playback_map;
	}
	
	@Override
	protected void onTitleBarCreated(SimpleTitleBar titleBar) {
		super.onTitleBarCreated(titleBar);
		titleBar.setTitle(R.string.title_driving_track_playback);
		titleBar.setRightButton(getString(R.string.titlebar_right_btn_replay), Color.WHITE);
		titleBar.setOnRightButtonClickListener(new View.OnClickListener() {
				
			@Override
			public void onClick(View v) {
				drawTrack();
			}
		});
	}
	
	@Override
	protected void onMapCreated(final MapView mapView, Bundle savedInstance) {
		mDrawTrackHandler = new Handler(this);
		mGraphicsOverlay = new GraphicsOverlay(mapView);
		mapView.getOverlays().add(mGraphicsOverlay);
	}
	
	@Override
	public void onMapLoadFinish() {
		final Integer[] span = new Integer[2];
		final GeoPoint centerPoint = new GeoPoint(0, 0);
		BMapUtil.calculateSpanAndCenter(mData, span, centerPoint);
		zoomToSpanAndMoveToCenter(span[0], span[1], centerPoint);
		drawTrack();
	}
	
	private void drawTrack() {
		if( mDrawTrackTask != null && !mDrawTrackTask.isDrawFinished()) {
			return;
		}
		mGraphicsOverlay.removeAll();
		getMapView().refresh();
		mDrawTrackTask = new DrawTrackTask(this);
		mDrawTrackTask.execute(mData);
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		if( msg.what == MSG_DRAW_TRACK ) {
			GeoPoint[] linePoints = (GeoPoint[])msg.obj;
			mGraphicsOverlay.setData(BMapUtil.drawLine(linePoints));
			getMapView().refresh();
			return true;
		}
		return false;
	}
	
    @Override
	protected void onDestroy() {
		super.onDestroy();
		if( mDrawTrackTask != null ) {
			mDrawTrackTask.cancel(true);
		}
	}
    
    /**
     * 画行车轨迹延时子线程
     * @author lwz
     *
     */
    class DrawTrackTask extends AsyncTask<GeoPoint[], Void, Void> {

		private WeakReference<Activity> reference;
		private boolean isDrawFinished;
		
		public DrawTrackTask(Activity activity) {
			reference = new WeakReference<Activity>(activity);
			isDrawFinished = false;
		}
		
		@Override
		protected Void doInBackground(GeoPoint[]... params) {
			GeoPoint[] data = params[0];
	        final int SIZE = data.length - 1;
			for( int i=0;i<SIZE; i++ ) {
				if( isNeedBreak() ) break;
	        	Message msgMessage = mDrawTrackHandler.obtainMessage(MSG_DRAW_TRACK,
	        			new GeoPoint[]{data[i], data[i+1]});
	        	mDrawTrackHandler.sendMessage(msgMessage);
	        	try {
	        		// 延时
					Thread.sleep(getDrawSpeed(data.length));
				} catch (InterruptedException e) {
					break;
				}
	        }
			return null;
		}
		
		public int getDrawSpeed(int pointsSize) {
			if( pointsSize < 30 ) {
				return DRAW_TRACK_INTRTVAL_MILLIS_SLOW;
			} else if( pointsSize < 100 ) {
				return DRAW_TRACK_INTRTVAL_MILLIS_NORMAL;
			} else {
				return DRAW_TRACK_INTRTVAL_MILLIS_FAST;
			}
		}
		
		@Override
		protected void onPostExecute(Void result) {
			isDrawFinished = true;
		}
		
		public synchronized boolean isDrawFinished() {
			return isDrawFinished;
		}
		
		private boolean isNeedBreak() {
			Activity activity = reference.get();
			return isCancelled() || activity == null || activity.isFinishing();
		}
	}

}
