package com.tonggou.andclient;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tonggou.andclient.myview.AbsViewHolderAdapter;
import com.tonggou.andclient.util.DJDatabase;
import com.tonggou.andclient.util.SomeUtil;
import com.tonggou.andclient.vo.DrivingJournalItem;

public class DrivingJournalCalendarLayout extends HorizontalScrollView {
	private HorizontalScrollView mCalendarLayout;
	private ArrayList<String>[] mArrCalendarDatas;
	private ArrayList<String> mCalendarDatas;
	private CalendarAdapter mCalendarAdapter;
	private DJCalendarListener mDJCalendarListener;
	private int mDataSize;
	private GridView mGridView;
	private Context mContext;
	private Resources mResources;
	private int mParamWidth;
	private int mSelectedPostion;
	private String mSelectedData;
	private int mCurrType;

	private static final int COLUMN_WIDTH_DP = 40;
	private static final int HORIZONTAL_SPACING = 5;
	private static final float ITEM_FONT_SIZE_NORMAL = 16;
	private static final float ITEM_FONT_SIZE_SELECTED = 20;
	private static final float ITEM_FONT_SIZE_SELECTED2 = 18;
	private static final int ITEM_COLOR_NORMAL = android.R.color.darker_gray;
	private static final int ITEM_COLOR_SELECTED = R.color.calendar_item_selected;

	public DrivingJournalCalendarLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	public DrivingJournalCalendarLayout(Context context) {
		super(context);
		mContext = context;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		initSelf();
	}

	private void initSelf() {
		mResources = mContext.getResources();
		mCalendarLayout = (HorizontalScrollView) View.inflate(mContext, R.layout.driving_journal_calendar,
				this);
		mGridView = (GridView) mCalendarLayout.findViewById(R.id.gv_driving_journal_calendar);
		setCalendarParams();
	}

	public void scrollToEnd() {
		mGridView.post(new Runnable() {
			@Override
			public void run() {
				scrollTo(mParamWidth, getHeight());
			}
		});
	}

	private void setGridViewParams() {
		mParamWidth = (SomeUtil.Dp2Px(mContext, COLUMN_WIDTH_DP + HORIZONTAL_SPACING)) * (mDataSize);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mParamWidth,
				LinearLayout.LayoutParams.MATCH_PARENT);
		mGridView.setLayoutParams(params);
		mGridView.setColumnWidth(SomeUtil.Dp2Px(mContext, COLUMN_WIDTH_DP));
		mGridView.setHorizontalSpacing(SomeUtil.Dp2Px(mContext, HORIZONTAL_SPACING));
		mGridView.setNumColumns(mDataSize);
		setSelected(mDataSize - 1);
		scrollToEnd();
	}

	private class CalendarAdapter extends AbsViewHolderAdapter<String> {

		public CalendarAdapter(Context context, List<String> data, int layoutRes) {
			super(context, data, layoutRes);
		}

		@Override
		protected void setData(int pos, View convertView, String itemData) {
			LinearLayout llItem = getViewFromHolder(convertView, R.id.ll_driving_journal_calendar_item);
			TextView tvMonth = getViewFromHolder(convertView, R.id.txt_driving_journal_calendar_item_month);
			TextView tvDay = getViewFromHolder(convertView, R.id.txt_driving_journal_calendar_item_day);
			TextView tvMonth2 = getViewFromHolder(convertView, R.id.txt_driving_journal_calendar_item_month2);

			if (mCurrType == SomeUtil.TYPE_MONTH) {
				llItem.setVisibility(View.GONE);
				tvMonth2.setVisibility(View.VISIBLE);
				if (pos == mSelectedPostion) {
					tvMonth2.setTextColor(mResources.getColor(ITEM_COLOR_SELECTED));
					tvMonth2.setTextSize(ITEM_FONT_SIZE_SELECTED2);
				} else {
					tvMonth2.setTextColor(mResources.getColor(ITEM_COLOR_NORMAL));
					tvMonth2.setTextSize(ITEM_FONT_SIZE_NORMAL);
				}
				tvMonth2.setText(SomeUtil.getMonth(itemData));
			} else {
				llItem.setVisibility(View.VISIBLE);
				tvMonth2.setVisibility(View.GONE);
				if (pos == mSelectedPostion) {
					tvDay.setTextSize(ITEM_FONT_SIZE_SELECTED);
					tvDay.setTextColor(mResources.getColor(ITEM_COLOR_SELECTED));
					tvMonth.setTextColor(mResources.getColor(ITEM_COLOR_SELECTED));
				} else {
					tvDay.setTextSize(ITEM_FONT_SIZE_NORMAL);
					tvDay.setTextColor(mResources.getColor(ITEM_COLOR_NORMAL));
					tvMonth.setTextColor(mResources.getColor(ITEM_COLOR_NORMAL));
				}
				tvDay.setText(SomeUtil.getDay(itemData));
				tvMonth.setText(SomeUtil.getMonth(itemData));
			}
		}
	}

	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			setSelected(position);
			mCalendarAdapter.notifyDataSetChanged();
			mDJCalendarListener.onDateSelected();
		}
	};

	private void setSelected(int position) {
		mSelectedPostion = position;
		mSelectedData = mCalendarAdapter.getItem(position);
	}

	public String getSelectedData() {
		return mSelectedData;
	}

	public int getSelectedType() {
		return mCurrType;
	}

	private void setCalendarParams() {
		prepareDatas();
		if (mCalendarDatas != null) {
			mCalendarAdapter = new CalendarAdapter(mContext, mCalendarDatas,
					R.layout.driving_journal_calendar_item);
			mGridView.setAdapter(mCalendarAdapter);
			mGridView.setOnItemClickListener(mOnItemClickListener);
			setGridViewParams();
		}
	}

	private void prepareDatas() {
		DJDatabase djDatabase = DJDatabase.getInstance(mContext);
		ArrayList<DrivingJournalItem> djItems = djDatabase.queryDJItems();
		mArrCalendarDatas = SomeUtil.getCalendarDatas(djItems);
		updateDatas(SomeUtil.TYPE_DAY);
	}

	public void updateDatas(int type) {
		if (mArrCalendarDatas == null) {
			mCalendarDatas = null;
		} else {
			mCurrType = type;
			mCalendarDatas = mArrCalendarDatas[type];
			mDataSize = mCalendarDatas.size();
			if (mCalendarAdapter != null) {
				mCalendarAdapter.update(mCalendarDatas);
				setGridViewParams();
			}
		}
	}

	/**
	 * 下面是测试数据，300多个数据，加载大约需要1.5秒钟
	 */
	private ArrayList<String> list1, list2, list3;

	private void getTestDatas(int type) {
		if (type == SomeUtil.TYPE_DAY) {
			if (list1 == null) {
				list1 = new ArrayList<String>();
				String s1 = "2014-";
				for (int i = 1; i <= 12; i++) {
					String s2 = new DecimalFormat("00").format(i) + "-";
					for (int j = 1; j <= 28; j++) {
						String s3 = new DecimalFormat("00").format(j);
						list1.add(s1 + s2 + s3);
					}
				}
			}
			mCalendarDatas = list1;
		} else if (type == SomeUtil.TYPE_WEEK) {
			if (list2 == null) {
				list2 = new ArrayList<String>();
				String s1 = "2014-";
				for (int i = 3; i <= 7; i++) {
					String s2 = new DecimalFormat("00").format(i) + "-";
					for (int j = 1; j <= 28; j++) {
						String s3 = new DecimalFormat("00").format(j);
						list2.add(s1 + s2 + s3);
					}
				}
			}
			mCalendarDatas = list2;
		} else if (type == SomeUtil.TYPE_MONTH) {
			if (list3 == null) {
				list3 = new ArrayList<String>();
				String s1 = "2014-";
				for (int i = 1; i <= 12; i++) {
					String s2 = new DecimalFormat("00").format(i);
					list3.add(s1 + s2);
				}
			}
			mCalendarDatas = list3;
		}
	}

	public interface DJCalendarListener {
		void onDateSelected();
	}

	public void setDJCalendarListener(DJCalendarListener dJCalendarListener) {
		mDJCalendarListener = dJCalendarListener;
	}
}
