package com.tonggou.gsm.andclient.ui.view;

import org.joda.time.DateTime;
import org.joda.time.DateTime.Property;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tonggou.gsm.andclient.R;

/**
 * 行车轨迹切换器
 * @author lwz
 *
 */
public class DrivingTrackDateSwitcher extends FrameLayout implements OnClickListener {

	private final String DATE_TIME_FROMAT = "MM/dd";

	public static interface OnSwitchDateRangeListener {

		/**
		 * 切换日期的范围
		 * @param minDate 最小的日期
		 * @param maxDate 最大的日期
		 */
		public void onSwitchDateRange(long minDate, long maxDate);
	}

	private TextView mContent;
	private ImageButton mLeftBtn;
	private ImageButton mRightBtn;
	private DateTime mLastDateTime;
	private OnSwitchDateRangeListener mListener;

	public DrivingTrackDateSwitcher(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public DrivingTrackDateSwitcher(Context context) {
		super(context);
		init();
	}

	private void init() {
		View rootView = View.inflate(getContext(), R.layout.widget_driving_track_switcher, null);
		addView(rootView);
		mLeftBtn = (ImageButton)rootView.findViewById(R.id.left_arrow_img_btn);
		mRightBtn = (ImageButton)rootView.findViewById(R.id.right_arrow_img_btn);
		mContent = (TextView)rootView.findViewById(R.id.content);

		mLeftBtn.setOnClickListener(this);
		mRightBtn.setOnClickListener(this);

		mLastDateTime = new DateTime();
		updateContentText(getResources().getString(R.string.current_week_statistics));
		mRightBtn.setEnabled(false);
	}

	/**
	 * 设置切换日期范围监听
	 * @param listener
	 */
	public void setOnSwitchDateRangeListener(OnSwitchDateRangeListener listener) {
		mListener = listener;
	}

	@Override
	public void onClick(View v) {
		final int viewId = v.getId();
		if( viewId == R.id.left_arrow_img_btn ) {
			switchToPreviousWeek();
		} else {
			switchToNextWeek();
		}
	}

	/**
	 * 切换到下一周
	 */
	private void switchToNextWeek() {
		mLastDateTime = mLastDateTime.plusWeeks(1);
		DateTime now = new DateTime();
		if( mLastDateTime.dayOfWeek().withMaximumValue().withTimeAtStartOfDay().getMillis()
				>= now.dayOfWeek().withMaximumValue().withTimeAtStartOfDay().getMillis()) {
			// 本周统计
			mLastDateTime = now;
			mRightBtn.setEnabled(false);
			updateContentText(getResources().getString(R.string.current_week_statistics));
		} else {
			// 显示时间区间
			updateContentText(mLastDateTime);
		}
		performSwitchDateRangeListener();
	}

	public void performSwitchDateRangeListener() {
		if( mListener != null ) {
			Property property = mLastDateTime.dayOfWeek();
			mListener.onSwitchDateRange(
					// 一周开始的时间
					property.withMinimumValue().withTimeAtStartOfDay().getMillis(),
					// 一周结束的时间
					property.withMaximumValue().withTime(23, 59, 59, 0).getMillis());
		}
	}

	/**
	 * 切换到上一周
	 */
	private void switchToPreviousWeek() {
		mRightBtn.setEnabled(true);
		mLastDateTime = mLastDateTime.minusWeeks(1);
		updateContentText(mLastDateTime);
		performSwitchDateRangeListener();
	}

	/**
	 * 显示 指定日期所在自然周的第一天和最后一天.{@link #updateContentText(DateTime, DateTime)}
	 * @param dateTime
	 */
	private void updateContentText(DateTime dateTime) {
		updateContentText(dateTime.dayOfWeek().withMinimumValue(), dateTime.dayOfWeek().withMaximumValue() );
	}

	/**
	 * display string
	 * @param text
	 */
	private void updateContentText(String text) {
		mContent.setText(text);
	}

	/**
	 * display string like "minDate - maxDate"
	 * @param minDate
	 * @param maxDate
	 */
	private void updateContentText(String minDate, String maxDate) {
		updateContentText( minDate + "	—  " + maxDate);
	}

	/**
	 * format dateTime and display it. {@link #updateContentText(String, String)}
	 * @param minDateTime
	 * @param maxDateTime
	 */
	private void updateContentText(DateTime minDateTime, DateTime maxDateTime) {
		updateContentText( fromatDateTime(minDateTime), fromatDateTime(maxDateTime) );
	}

	private String fromatDateTime(DateTime dateTime) {
		return dateTime.toString(DATE_TIME_FROMAT);
	}
}