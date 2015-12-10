package com.tonggou.andclient;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.tonggou.andclient.util.SomeUtil;

public class DrivingJournalPullLayout extends LinearLayout {
	private static final String TAG = "DrivingJournalPullLayout";
	private static final int ACTION_DURATION = 300;
	private static final int DATAS_DURATION_DOWN = 250;
	private static final int DATAS_DURATION_UP = 350;
	private static final int CALENDAR_DURATION_IN = 750;
	private static final int CALENDAR_DURATION_OUT = 150;
	private static final int DP_SCOPE = 12;
	private Context mContext;
	private LinearLayout mPullLayout, mLlSuper, mLlDatas, mLlOhters, mLlCalendar;
	private FrameLayout mFlDrag;
	private LayoutParams mSuperLayoutParams;
	private DrivingJournalCalendarLayout mDjclCalendar;
	private int mSuperHeight, mDatasHeight, mDragHeight, mStartSupertop, mStartSuperBottom, mCurrSupertop,
			mCurrSuperBottom, mStartX, mStartY;
	private Scroller mScroller;
	private boolean isOthersOnTouching;
	private int clickScope;

	public enum PullLayoutStatus {
		ALL_COLLAPSED, CALENDAR_COLLAPSED, ALL_EXPANDED
	}

	private PullLayoutStatus mCurrStatus = PullLayoutStatus.CALENDAR_COLLAPSED;

	public PullLayoutStatus getStatus() {
		return mCurrStatus;
	}

	public void changeStatus() {
		switch (mCurrStatus) {
		case ALL_COLLAPSED:
			expandDatas();
			break;
		case CALENDAR_COLLAPSED:
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
		mScroller = new Scroller(mContext, new AccelerateInterpolator(1.0f));
	}

	private void initSelf() {
		mPullLayout = (LinearLayout) View.inflate(mContext, R.layout.driving_journal_pull_layout, this);
		initViews();
		initParams();
	}

	private void initViews() {
		mLlSuper = (LinearLayout) mPullLayout.findViewById(R.id.ll_driving_journal_pull_layout_super);
		mLlDatas = (LinearLayout) mPullLayout.findViewById(R.id.ll_driving_journal_datas);
		mFlDrag = (FrameLayout) mPullLayout.findViewById(R.id.fl_driving_journal_drag);
		mLlCalendar = (LinearLayout) mPullLayout.findViewById(R.id.ll_driving_journal_calendar);
		mDjclCalendar = (DrivingJournalCalendarLayout) mPullLayout
				.findViewById(R.id.djcl_driving_journal_calendar);
		mLlOhters = (LinearLayout) findViewById(R.id.ll_driving_journal_others);

		mPullLayout.findViewById(R.id.fl_driving_journal_drag).setOnClickListener(mOnClickListener);
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
		mDragHeight = mFlDrag.getHeight();
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
			isOthersOnTouching = (mCurrStatus == PullLayoutStatus.ALL_EXPANDED)
					&& (event.getY() > mDatasHeight + mDragHeight) ? true : false;
			break;
		case MotionEvent.ACTION_MOVE:
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
		if (isOthersOnTouching) {
			return true;
		}

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
			}

			break;
		case MotionEvent.ACTION_UP:
			movedX = (int) event.getRawX() - mStartX;
			movedY = (int) event.getRawY() - mStartY;

			mCurrSupertop = mStartSupertop + movedY;
			mCurrSuperBottom = mStartSuperBottom + movedY;

			if (mCurrSupertop >= 0 || mCurrSupertop <= -mDatasHeight) {
				return true;
			}
			Log.d(TAG, "CurrStatus Before£º" + mCurrStatus.toString());
			if (mSuperHeight - mCurrSuperBottom >= (mDatasHeight / 2)) {
				mCurrStatus = PullLayoutStatus.ALL_COLLAPSED;
				mScroller.startScroll(0, mCurrSuperBottom, 0,
						((mSuperHeight - mDatasHeight) - mCurrSuperBottom), ACTION_DURATION);
			} else {
				if (mCurrStatus != PullLayoutStatus.ALL_EXPANDED) {
					mCurrStatus = PullLayoutStatus.CALENDAR_COLLAPSED;
				}
				mScroller.startScroll(0, mCurrSuperBottom, 0, mSuperHeight - mCurrSuperBottom,
						ACTION_DURATION);
			}
			Log.d(TAG, "CurrStatus After£º" + mCurrStatus.toString());
			break;
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
			// Log.d(TAG, "computeScroll£º" + bottom);
			if (mCurrStatus == PullLayoutStatus.ALL_COLLAPSED && bottom == mSuperHeight - mDatasHeight) {
				collapseAll();
			}
		}
		postInvalidate();
	}

	private void expandDatas() {
		if (mDatasHeight == 0) {
			getCommonParams();
		}
		mCurrStatus = PullLayoutStatus.CALENDAR_COLLAPSED;
		mScroller.startScroll(0, mSuperHeight - mDatasHeight, 0, mDatasHeight, DATAS_DURATION_DOWN);
	}

	private void expandCalendar() {
		mLlSuper.post(new Runnable() {
			@Override
			public void run() {
				changeOthersParams(false);
				mLlCalendar.setVisibility(View.VISIBLE);
				mDjclCalendar.scrollToEnd();
				postInvalidate();
				startExpandCalendarAnim();
				mCurrStatus = PullLayoutStatus.ALL_EXPANDED;
			}
		});
	}

	private void startExpandCalendarAnim() {
		AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
		alphaAnimation.setDuration(CALENDAR_DURATION_IN);
		mLlCalendar.setAnimation(alphaAnimation);
		alphaAnimation.start();
	}

	private void collapseCalendar() {
		mLlSuper.post(new Runnable() {
			@Override
			public void run() {
				changeOthersParams(true);
				startCollapseCalendarAnim();
			}
		});
	}

	private void startCollapseCalendarAnim() {
		AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0f);
		alphaAnimation.setDuration(CALENDAR_DURATION_OUT);
		alphaAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mLlCalendar.setVisibility(View.GONE);
				postInvalidate();
				mCurrStatus = PullLayoutStatus.CALENDAR_COLLAPSED;
			}
		});
		mLlCalendar.setAnimation(alphaAnimation);
		alphaAnimation.start();
	}

	private void collapseAll() {
		if (mDatasHeight == 0) {
			getCommonParams();
		}
		mLlSuper.post(new Runnable() {
			@Override
			public void run() {
				changeOthersParams(true);
				mLlCalendar.setVisibility(View.GONE);
				setSuperMargins(0, -mDatasHeight, 0, 0);
				postInvalidate();
			}
		});
	}

	public void collapseDatas() {
		if (mDatasHeight == 0) {
			getCommonParams();
		}
		mCurrStatus = PullLayoutStatus.ALL_COLLAPSED;
		mScroller.startScroll(0, mSuperHeight, 0, -mDatasHeight, DATAS_DURATION_UP);
	}

	private OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.fl_driving_journal_drag:
				changeStatus();
				break;
			}
		}
	};

	private void changeOthersParams(boolean changeToInit) {
		android.widget.FrameLayout.LayoutParams pullrParams = (android.widget.FrameLayout.LayoutParams) mPullLayout
				.getLayoutParams();
		if (changeToInit) {
			pullrParams.height = LayoutParams.WRAP_CONTENT;
			mPullLayout.setLayoutParams(pullrParams);
			mLlOhters.setBackgroundColor(Color.parseColor("#00000000"));
		} else {
			pullrParams.height = LayoutParams.MATCH_PARENT;
			mPullLayout.setLayoutParams(pullrParams);
			mLlOhters.setBackgroundColor(Color.BLACK);
			mLlOhters.getBackground().setAlpha(200);
//			 mLlOhters.setBackgroundResource(R.drawable.aaa);
		}
	}
}
