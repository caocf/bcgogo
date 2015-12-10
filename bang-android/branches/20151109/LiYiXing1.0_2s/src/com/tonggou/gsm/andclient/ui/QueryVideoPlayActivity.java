package com.tonggou.gsm.andclient.ui;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.bean.VideoPictureDTO;
import com.tonggou.gsm.andclient.net.response.QueryVideoRecordResponse;
import com.tonggou.gsm.andclient.ui.view.SimpleTitleBar;

public class QueryVideoPlayActivity extends BackableTitleBarActivity {
	public static final String EXTRA_VIDEO_PLAY = "extra_video_play";
	private SimpleTitleBar titleBar;
	private boolean isTitleBarDisplay;
	private QueryVideoRecordResponse dataResponse;
	private VideoPictureDTO data;
	private VideoView video;
	private MediaController controller;
	private int total;
	private int pos;
	private boolean isFirstError = false;
	private boolean isSecondError = false;

	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		setContentView(R.layout.activity_videoview_play);

		if( !restoreExtras(getIntent()) ) {
			restoreExtras(savedInstance);
		}

		data = dataResponse.getVideoRecordDTOs().get(0);

		if (data == null || (null == data.getVideoPath())) {
			Toast.makeText(this, "视频数据为空！", Toast.LENGTH_SHORT).show();
			finish();
		} else {
			showLoadingDialog();
			total = data.getVideoPath().length;
			pos = 0;
			controller = new MediaController(this);

			video = (VideoView)findViewById(R.id.videoview);
			video.setMediaController(controller);
			video.setKeepScreenOn(true);
			video.setVideoURI(Uri.parse(data.getVideoPath()[pos]));
			video.setOnPreparedListener(new OnPreparedListener() {

				@Override
				public void onPrepared(MediaPlayer mp) {

					dismissLoadingDialog();
					video.start();
					video.requestFocus();
				}
			});

			video.setOnCompletionListener(new OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer mp) {
						video.seekTo(0);
						video.pause();
					}
			});

			video.setOnErrorListener(new OnErrorListener(){

					@Override
					public boolean onError(MediaPlayer mp, int what, int extra) {
						dismissLoadingDialog();
						if (pos == 0) {
							isFirstError = true;
						} else if (pos == 1) {
							isSecondError = true;
						}

						if (total == 1) {
							Log.e("video error" ,  what + " " + extra + "/n total : " + total);
							onBackPressed();
						} else {
							if ((pos == 0 && (isSecondError == true))  || ((pos == 1 && (isFirstError == true)))) {
								Log.e("Total video is error" ,	what + " " + extra);
								onBackPressed();
							} else {
								if (pos == 0) {
									pos++;
								} else if (pos == 1) {
									pos--;
								}
								video.stopPlayback();

								video.setVideoURI(Uri.parse(data.getVideoPath()[pos]));
							}


						}
						return false;
				}
			});

			video.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (isTitleBarDisplay) {
						titleBar.setVisibility(View.GONE);
						isTitleBarDisplay = false;
					} else {
						titleBar.setVisibility(View.VISIBLE);
						isTitleBarDisplay = true;
						hidBackTitleBar();
					}
					return false;
				}
			});

			video.setOnKeyListener(new OnKeyListener() {

				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if (isTitleBarDisplay && (keyCode == KeyEvent.KEYCODE_BACK)) {
						titleBar.setVisibility(View.GONE);
						isTitleBarDisplay = false;
					}
					return false;
				}

			});
		}
	}

	@Override
	protected boolean restoreExtras(Bundle extra) {
		if( extra != null && extra.containsKey(EXTRA_VIDEO_PLAY) ) {
			dataResponse = (QueryVideoRecordResponse) extra.getSerializable(EXTRA_VIDEO_PLAY);
			return true;
		}
		return false;
	}

	@Override
	protected void onTitleBarCreated(SimpleTitleBar titleBar) {
		super.onTitleBarCreated(titleBar);
		titleBar.setTitle(R.string.title_play_video);
		titleBar.setRightImageButton(R.drawable.ic_titlebar_userinfo, android.R.color.transparent)
		.setOnRightButtonClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (total == 1) {
					return;
				}

				if (pos == 0) {
					pos++;
				} else if (pos == 1) {
					pos--;
				}
				video.stopPlayback();
				video.setVideoURI(Uri.parse(data.getVideoPath()[pos]));
			}
		});

		this.titleBar = titleBar;
		isTitleBarDisplay = false;
		titleBar.setVisibility(View.GONE);
	}

	/*
	 * Start timer
	 */
	private void hidBackTitleBar() {
		hideBackTitleBarHandler.postDelayed(hideTitleBarRunnable, 3000);
	}

	/*
	 * Delay to display titleBar
	 */
	Handler hideBackTitleBarHandler = new Handler();
	Runnable hideTitleBarRunnable = new Runnable() {
		public void run() {
			if (isTitleBarDisplay) {
				titleBar.setVisibility(View.GONE);
				isTitleBarDisplay = false;
			}
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		hideBackTitleBarHandler.removeCallbacks(hideTitleBarRunnable);
		hideBackTitleBarHandler = null;
		dismissLoadingDialog();
	}
}