package com.tonggou.andclient;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.tonggou.andclient.util.SomeUtil;

public class DrivingJournalPullLayout extends LinearLayout {
	private static final String TAG = "DrivingJournalPullLayout";
	private static final boolean DEBUG = false;
	private static final int DURATION = 500;
	private static final int DP_SCOPE = 12;
	private Context mContext;
	private LinearLayout mPullLayout, mLlSuper, mLlDatas, mLlCalendar;
	private LayoutParams mSuperLayoutParams;
	private DrivingJournalCalendarLayout mDjclCalendar;
	private ImageView mImgViewArrow;
	private int mSuperHeight, mDatasHeight, mCalendarHeight, mStartSupertop, mStartSuperBottom,
			mCurrSupertop, mCurrSuperBottom, mStartX, mStartY;
	private Scroller mScroller;
	private boolean isCalendarOnTouching;
	private int clickScope;

	public enum PullLayoutStatus {
		ALL_COLLAPSED, ONLY_CALENDAR_COLLAPSED, ONLY_DATAS_COLLAPSED, ALL_EXPANDED
	}

	private PullLayoutStatus mCurrStatus = PullLayoutStatus.ONLY_CALENDAR_COLLAPSED;

	public PullLayoutStatus getStatus() {
		return mCurrStatus;
	}

	public void changeStatus() {
		switch (mCurrStatus) {
		case ALL_COLLAPSED:
			expandDatas();
			break;
		case ONLY_DATAS_COLLAPSED:
			collapseAll();
			break;
		case ONLY_CALENDAR_COLLAPSED:
			expandCalendar();
			break;
		case ALL_EXPANDED:
			collapseCalendar();
			break;
		}
	}

	public DrivingJournalPullLayout(Context context) {
		super(context);
		mContext = context;
	}

	public DrivingJournalPullLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		initSelf();
		mScroller = new Scroller(mContext, new DecelerateInterpolator(2.0f));
	}

	private void initSelf() {
		mPullLayout = (LinearLayout) View.inflate(mContext, R.layout.driving_journal_pull_layout, this);
		initViews();
		initParams();
	}

	private void initViews() {
		mLlSuper = (LinearLayout) mPullLayout.findViewById(R.id.ll_driving_journal_pull_layout_super);
		mLlDatas = (LinearLayout) mPullLayout.findViewById(R.id.ll_driving_journal_datas);
		mLlCalendar = (LinearLayout) mPullLayout.findViewById(R.id.ll_driving_journal_calendar);
		mDjclCalendar = (DrivingJournalCalendarLayout) mPullLayout
				.findViewById(R.id.djcl_driving_journal_calendar);
		mImgViewArrow = (ImageView) mPullLayout.findViewById(R.id.imgView_driving_journal_arrow);

		mPullLayout.findViewById(R.id.ll_driving_journal_arrow).setOnClickListener(mOnClickListener);
	}

	public void initParams() {
		mPullLayout.post(new Runnable() {
			@Override
			public void run() {
				clickScope = SomeUtil.Dp2Px(mContext, DP_SCOPE);
				mSuperLayoutParams = (LayoutParams) mLlSuper.getLayoutParams();
				collapseCalendar();
			}
		});
	}

	private void getCommonParams() {
		mSuperHeight = mLlSuper.getHeight();
		mDatasHeight = mLlDatas.getHeight();
		mCalendarHeight = mDjclCalendar.getHeight();
		mStartSupertop = mLlSuper.getTop();
		mStartSuperBottom = mLlSuper.getBottom();
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		int movedX, movedY;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mStartX = (int) event.getRawX();
			mStartY = (int) event.getRawY();
			getCommonParams();
			isCalendarOnTouching = mCurrStatus == PullLayoutStatus.ALL_EXPANDED
					&& (event.getY() > mSuperHeight - mCalendarHeight) ? true : false;
			break;
		case MotionEvent.ACTION_MOVE:
			if (isCalendarOnTouching) {
				return false;
			}
			movedX = (int) event.getRawX() - mStartX;
			movedY = (int) event.getRawY() - mStartY;
			if (Math.abs(movedX) < clickScope && Math.abs(movedY) < clickScope) {
				return false;
			} else {
				return true;
			}
		}
		return super.onInterceptTouchEvent(event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int movedX, movedY;
		switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE:
			movedX = (int) event.getRawX() - mStartX;
			movedY = (int) event.getRawY() - mStartY;
			mCurrSupertop = mStartSupertop + movedY;
			mCurrSuperBottom = mStartSuperBottom + movedY;
			if (mCurrSuperBottom >= (mSuperHeight - mDatasHeight)) {
				if (mCurrSuperBottom <= mSuperHeight) {
					setSuperMargins(0, mCurrSupertop, 0, 0);
					postInvalidate();
				} else {
					mCurrSuperBottom = mSuperHeight;
				}
			} else {
				mCurrSuperBottom = mSuperHeight - mDatasHeight;
				if (mCurrStatus == PullLayoutStatus.ONLY_DATAS_COLLAPSED) {
					collapseAll();
				}
			}

			break;
		case MotionEvent.ACTION_UP:
			movedX = (int) event.getRawX() - mStartX;
			movedY = (int) event.getRawY() - mStartY;
			if (mSuperHeight - mCurrSuperBottom >= (mDatasHeight / 2)) {
				mScroller.startScroll(0, mCurrSuperBottom, 0,
						((mSuperHeight - mDatasHeight) - mCurrSuperBottom), DURATION);
				if (DEBUG)
					Log.d(TAG, "1£º" + mCurrStatus.toString());
				if (mCurrStatus == PullLayoutStatus.ONLY_CALENDAR_COLLAPSED) {
					mCurrStatus = PullLayoutStatus.ALL_COLLAPSED;
				} else if (mCurrStatus == PullLayoutStatus.ALL_EXPANDED) {
					mCurrStatus = PullLayoutStatus.ONLY_DATAS_COLLAPSED;
				}
			} else {
				mScroller.startScroll(0, mCurrSuperBottom, 0, mSuperHeight - mCurrSuperBottom, DURATION);
				if (DEBUG)
					Log.d(TAG, "2£º" + mCurrStatus.toString());
				if (mCurrStatus == PullLayoutStatus.ALL_COLLAPSED) {
					mCurrStatus = PullLayoutStatus.ONLY_CALENDAR_COLLAPSED;
				} else if (mCurrStatus == PullLayoutStatus.ONLY_DATAS_COLLAPSED) {
					mCurrStatus = PullLayoutStatus.ALL_EXPANDED;
				}
			}
			if (DEBUG)
				Log.d(TAG, "curr£º" + mCurrStatus.toString());
		}
		return true;
	}

	private void setSuperMargins(int left, int top, int right, int bottom) {
		mSuperLayoutParams.setMargins(0, top, 0, 0);
		mLlSuper.setLayoutParams(mSuperLayoutParams);
		postInvalidate();
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			int bottom = mScroller.getCurrY();
			setSuperMargins(0, bottom - mSuperHeight, 0, 0);
			if (DEBUG)
				Log.d(TAG, "computeScroll£º" + mScroller.getCurrY());
		}
		postInvalidate();
	}

	private OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.ll_driving_journal_arrow:
				changeStatus();
				break;
			}
		}
	};

	private void expandDatas() {
		mLlSuper.post(new Runnable() {
			@Override
			public void run() {
				mImgViewArrow.setImageResource(R.drawable.icon_arrow1_down);
				setSuperMargins(0, 0, 0, 0);
				mCurrStatus = PullLayoutStatus.ONLY_CALENDAR_COLLAPSED;
			}
		});
	}

	public void collapseAll() {
		if (mDatasHeight == 0) {
			getCommonParams();
		}
		mLlSuper.post(new Runnable() {
			@Override
			public void run() {
				mImgViewArrow.setImageResource(R.drawable.icon_arrow1_down);
				mLlCalendar.setVisibility(View.GONE);
				setSuperMargins(0, -mDatasHeight, 0, 0);
				mCurrStatus = PullLayoutStatus.ALL_COLLAPSED;
			}
		});
	}

	private void expandCalendar() {
		mLlSuper.post(new Runnable() {
			@Override
			public void run() {
				mImgViewArrow.setImageResource(R.drawable.icon_arrow1_up);
				mLlCalendar.setVisibility(View.VISIBLE);
				mDjclCalendar.scrollToEnd();
				postInvalidate();
				mCurrStatus = PullLayoutStatus.ALL_EXPANDED;
			}
		});
	}

	private void collapseCalendar() {
		mLlSuper.post(new Runnable() {
			@Override
			public void run() {
				mImgViewArrow.setImageResource(R.drawable.icon_arrow1_down);
				mLlCalendar.setVisibility(View.GONE);
				postInvalidate();
				mCurrStatus = PullLayoutStatus.ONLY_CALENDAR_COLLAPSED;
			}
		});
	}
}
