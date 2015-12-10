package com.tonggou.gsm.andclient.ui;

import org.apache.http.Header;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.bean.VideoPictureDTO;
import com.tonggou.gsm.andclient.net.response.QueryVideoRecordResponse;
import com.tonggou.gsm.andclient.ui.view.SimpleTitleBar;
import com.tonggou.gsm.andclient.util.ImageFileCache;

public class MonitorPictureActivity extends BackableTitleBarActivity {
	public static final String EXTRA_PCITURE_PLAY = "extra_picture_play";
	private ImageView ivBitmap;

	private TextView tvPositionBitmap;
	private TextView tvTotalBitmap;

	private GestureDetector mGestureDetector;

	private SimpleTitleBar titleBar;
	private boolean isTitleBarHide;

	private ImageFileCache imgFileCache;
	private	String[] picPathArray;
	private int position;

	private QueryVideoRecordResponse mDataResponse;
	private VideoPictureDTO mData;

	@Override
	protected boolean restoreExtras(Bundle extra) {
		if( extra != null && extra.containsKey(EXTRA_PCITURE_PLAY) ) {
			mDataResponse = (QueryVideoRecordResponse) extra.getSerializable(EXTRA_PCITURE_PLAY);
			return true;
		}
		return false;
	}

	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setContentView(R.layout.activity_monitor_picture_view);
		tvPositionBitmap = (TextView)findViewById(R.id.positionTxt);
		ivBitmap = (ImageView)findViewById(R.id.photographview);

		if( !restoreExtras(getIntent()) ) {
			restoreExtras(savedInstance);
		}

		mData = mDataResponse.getVideoRecordDTOs().get(0);
		picPathArray = mData.getPictureArray();

		tvTotalBitmap = (TextView)findViewById(R.id.totalTxt);
		if ((picPathArray != null) && (picPathArray.length > 0)) {
			tvTotalBitmap.setText(picPathArray.length + "");
			position = 0;
			showLoadingDialog();
			getImag(picPathArray[position]);
		}

		mGestureDetector = new GestureDetector(this, new MyOnGestureListener());

		ivBitmap.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mGestureDetector.onTouchEvent(event);
				return true;
			}

		});
	}

	public void getImag(final String path) {
		if (null == path) {
			dismissLoadingDialog();
			Toast.makeText(this, "图片地址为null", Toast.LENGTH_SHORT).show();

			return;
		}

		imgFileCache = new ImageFileCache();
		if (null != imgFileCache.getBitmap(path)) {
			ivBitmap.setImageBitmap(imgFileCache.getBitmap(path));
			tvPositionBitmap.setText(position + 1 + "");
			dismissLoadingDialog();
			return;
		}

		AsyncHttpClient client = new AsyncHttpClient();
		client.setTimeout(5000);
		client.get(path, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody) {
				if (statusCode == 200) {
					Bitmap bitmap = BitmapFactory.decodeByteArray(responseBody, 0, responseBody.length);

					ivBitmap.setImageBitmap(rotateBitmap(readPicDegree(bitmap) , bitmap));
					imgFileCache.saveBitmap(bitmap, path);
					tvPositionBitmap.setText(position + 1 + "");
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable error) {
				error.printStackTrace();
				dismissLoadingDialog();
			}

		});
	}

	public static Bitmap rotateBitmap(int degree, Bitmap bitmap) {
		Matrix matrix = new Matrix();
		matrix.postRotate(degree);

		Bitmap bm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
			bitmap.getHeight(), matrix, true);
		return bm;
	}

	public static int readPicDegree(Bitmap map) {
		int degree;

		if (map.getHeight() < map.getWidth())
			return degree = 0;
		else
			degree = 90;

		return degree;
	}

	@Override
	protected void onTitleBarCreated(SimpleTitleBar titleBar) {
		super.onTitleBarCreated(titleBar);
		titleBar.setTitle(R.string.title_query_photograph);
		this.titleBar = titleBar;
		isTitleBarHide = false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	class MyOnGestureListener extends SimpleOnGestureListener {

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			if (e1.getX() - e2.getX() > 120) {
				if ((position < (picPathArray.length-1)) && (position >= 0) && (null != picPathArray[position + 1])) {
					position++;
					showLoadingDialog();
					getImag(picPathArray[position]);
					return true;
				}

			} else if (e1.getX() - e2.getX() < -120) {

				if ((position > 0) && (picPathArray.length > 1) && (position < picPathArray.length)) {
					position--;
					showLoadingDialog();
					getImag(picPathArray[position]);
					return true;
				}
			}

			return false;
		}

		@Override
		public boolean onDoubleTapEvent(MotionEvent e) {
			Log.i(getClass().getName(), "onDoubleTapEvent");
			showLoadingDialog();
			getImag(picPathArray[position]);
			return false;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			Log.i(getClass().getName(), "onSingleTapConfirmed-----");
			if (!isTitleBarHide) {
				titleBar.setVisibility(View.GONE);
				isTitleBarHide = true;
			} else {
				titleBar.setVisibility(View.VISIBLE);
				isTitleBarHide = false;
			}
			return false;
		}
	}
}