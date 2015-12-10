package com.tonggou.andclient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DrivingJournalPullLayout extends LinearLayout {
	private Context mContext;
	private Resources mResources;
	private LinearLayout mPullLayout;
	private GridView mGvCalendar;
	private TextView mTxtDistance;
	private TextView mTxtTime;
	private TextView mTxtOilUsed;
	private TextView mTxtOilCost;
	private ImageView mImgViewArrow;
	private ImageView mImgViewDay;
	private ImageView mImgViewWeek;
	private ImageView mImgViewMonth;

	private ArrayList<Integer> mCalendarDatas;

	public DrivingJournalPullLayout(Context context) {
		super(context);
		initSelf(context);
	}

	public DrivingJournalPullLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		initSelf(context);
	}

	private void initSelf(Context context) {
		mContext = context;
		mResources = context.getResources();
		mPullLayout = (LinearLayout) View.inflate(context, R.layout.driving_journal_pull_layout,
				this);
		initViews();
		initCalendar();
	}

	private void initViews() {
		mTxtDistance = (TextView) mPullLayout.findViewById(R.id.txt_driving_distance);
		mTxtTime = (TextView) mPullLayout.findViewById(R.id.txt_driving_time);
		mTxtOilUsed = (TextView) mPullLayout.findViewById(R.id.txt_driving_oil_used);
		mTxtOilCost = (TextView) mPullLayout.findViewById(R.id.txt_driving_oil_cost);
		mGvCalendar = (GridView) mPullLayout.findViewById(R.id.gv_driving_journal_calendar);
		mImgViewArrow = (ImageView) mPullLayout.findViewById(R.id.imgView_driving_journal_arrow);
		mImgViewDay = (ImageView) mPullLayout
				.findViewById(R.id.imgView_driving_journal_calendar_day);
		mImgViewWeek = (ImageView) mPullLayout
				.findViewById(R.id.imgView_driving_journal_calendar_week);
		mImgViewMonth = (ImageView) mPullLayout
				.findViewById(R.id.imgView_driving_journal_calendar_month);

		mPullLayout.findViewById(R.id.ll_driving_journal_arrow)
				.setOnClickListener(mOnClickListener);
		mImgViewDay.setOnClickListener(mOnClickListener);
		mImgViewWeek.setOnClickListener(mOnClickListener);
		mImgViewMonth.setOnClickListener(mOnClickListener);
	}

	private void initCalendar() {
		mCalendarDatas = new ArrayList<Integer>();
		for (int i = 0; i < getNowDay(); i++) {
			mCalendarDatas.add(i + 1);
		}
		mGvCalendar.setAdapter(mAdapter);
//		mGvCalendar.setNumColumns(mCalendarDatas.size());
	}

	private BaseAdapter mAdapter = new BaseAdapter() {
		@Override
		public int getCount() {
			return mCalendarDatas.size();
		}

		@Override
		public Object getItem(int position) {
			return mCalendarDatas.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			GvItemViewHoloder viewHoloder;
			if (convertView == null) {
				convertView = View.inflate(mContext, R.layout.driving_journal_calendar_item, null);
				viewHoloder = new GvItemViewHoloder();
				setViewHoloder(viewHoloder, convertView);
			} else {
				viewHoloder = (GvItemViewHoloder) convertView.getTag();
			}
			String data = mCalendarDatas.get(position) + "";
			setData(position, viewHoloder, data);
			return convertView;
		}

		private void setData(int position, GvItemViewHoloder viewHoloder, String data) {
			if (position == mCalendarDatas.size() - 1) {
				viewHoloder.txtData.setTextSize(16);
				viewHoloder.txtData.setTextColor(mResources.getColor(android.R.color.black));
			}
			viewHoloder.txtData.setText(data);
		}

		private void setViewHoloder(GvItemViewHoloder viewHoloder, View convertView) {
			viewHoloder.imgViewLeft = (ImageView) convertView
					.findViewById(R.id.imgView_driving_journal_calendar_item_left);
			viewHoloder.imgViewRight = (ImageView) convertView
					.findViewById(R.id.imgView_driving_journal_calendar_item_right);
			viewHoloder.txtData = (TextView) convertView
					.findViewById(R.id.txt_driving_journal_calendar_item);
			convertView.setTag(viewHoloder);
		}
	};

	private class GvItemViewHoloder {
		public ImageView imgViewLeft;
		public ImageView imgViewRight;
		public TextView txtData;
	}

	private int getNowDay() {
		String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		return Integer.parseInt(date.substring(date.lastIndexOf("-") + 1));
	}

	private OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.ll_driving_journal_arrow:

				break;
			case R.id.imgView_driving_journal_calendar_day:

				break;
			case R.id.imgView_driving_journal_calendar_week:

				break;
			case R.id.imgView_driving_journal_calendar_month:

				break;
			}
		}
	};

}
