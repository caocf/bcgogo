package com.tonggou.andclient;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.tonggou.andclient.util.DJDatabase;
import com.tonggou.andclient.util.SomeUtil;
import com.tonggou.andclient.vo.DrivingJournalItem;
import com.tonggou.andclient.vo.type.DrivingJournalStatus;

public class VehicleConditionAdjustActivity extends BaseActivity {
	public static final String EXTRA_DJITEM = "extra_dj_item";
	private DJDatabase mDjDatabase;
	private DrivingJournalItem mDjItem;
	private String mOrigOilPrice, mOrigDistance;
	private EditText mEtPrice, mEtDistance, mEtWear, mEtCost;
	private double mAvgOilWear;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.vehicle_condition_adjust);
		mDjDatabase = DJDatabase.getInstance(this);
		initViews();
		showData();
	}

	private void showData() {
		Intent intent = getIntent();
		if (intent != null) {
			mDjItem = (DrivingJournalItem) intent.getSerializableExtra(EXTRA_DJITEM);
		}
		if (mDjItem != null) {
			mAvgOilWear = mDjItem.getOilWear(); // 平均油耗
			mOrigOilPrice = SomeUtil.doubleToString(mDjItem.getOilPrice());
			mOrigDistance = SomeUtil.doubleToString(mDjItem.getDistance());
			setEtData(mEtPrice, mOrigOilPrice); // 油价
			setEtData(mEtDistance, mOrigDistance); // 距离
			setEtData(mEtWear, SomeUtil.doubleToString(getOilWear(mDjItem.getDistance()))); // 油耗(根据平均油耗来算)
			setEtData(mEtCost, SomeUtil.doubleToString(mDjItem.getTotalOilMoney())); // 油费
			mEtPrice.requestFocus();
		}
	}

	private double getOilWear(double distance) {
		if (mAvgOilWear > 0 && distance > 0) {
			return SomeUtil.baseFmtDouble(mAvgOilWear * (distance / 100), 2);
		}
		return 0d;
	}

	private double getOilCost(double oilPrice, double distance) {
		if (mAvgOilWear > 0 && oilPrice > 0 && distance > 0) {
			return SomeUtil.baseFmtDouble(oilPrice * getOilWear(distance), 2);
		}
		return 0d;
	}

	private void setEtData(EditText et, String data) {
		if (data != null) {
			et.setText(data);
			et.setSelection(data.length());
		}
	}

	private double getEtData(EditText et) {
		return Double.parseDouble(et.getText().toString().trim());
	}

	private void setDJItem() {
		if (mDjItem != null) {
			double oilPrice = getEtData(mEtPrice);
			double distance = getEtData(mEtDistance);
			mDjItem.setOilPrice(oilPrice);
			mDjItem.setDistance(distance);
			mDjItem.setTotalOilMoney(getEtData(mEtCost));
			if (!SomeUtil.doubleToString(oilPrice).equals(mOrigOilPrice)
					|| !SomeUtil.doubleToString(distance).equals(mOrigDistance)) {
				mDjItem.setStatus(DrivingJournalStatus.NOT_UPLOAD.toString());
			}
		}
	}

	private void initViews() {
		RelativeLayout rlBack = (RelativeLayout) findViewById(R.id.rl_vehicle_condition_adjust_back);
		mEtPrice = (EditText) findViewById(R.id.et_vehicle_condition_adjust_oil_price);
		mEtDistance = (EditText) findViewById(R.id.et_vehicle_condition_adjust_distance);
		mEtWear = (EditText) findViewById(R.id.et_vehicle_condition_adjust_oil_wear);
		mEtCost = (EditText) findViewById(R.id.et_vehicle_condition_adjust_oil_cost);
		ImageView ivCommit = (ImageView) findViewById(R.id.iv_vehicle_condition_adjust_commit);

		rlBack.setOnClickListener(mOnClickListener);
		ivCommit.setOnClickListener(mOnClickListener);
		mEtPrice.setOnFocusChangeListener(mOnFocusChangeListener);
		mEtCost.setOnFocusChangeListener(mOnFocusChangeListener);
	}

	private OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.rl_vehicle_condition_adjust_back:
				finish();
				break;
			case R.id.iv_vehicle_condition_adjust_commit:
				setDJItem();
				mDjDatabase.updateVCAdjust(mDjItem);
				finish();
				break;
			}
		}
	};

	private OnFocusChangeListener mOnFocusChangeListener = new OnFocusChangeListener() {
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (v.getId() == R.id.et_vehicle_condition_adjust_oil_price
					|| v.getId() == R.id.et_vehicle_condition_adjust_distance) {
				double price = SomeUtil.stringToDouble(mEtPrice.getText().toString());
				double distance = SomeUtil.stringToDouble(mEtDistance.getText().toString());
				mEtWear.setText(SomeUtil.doubleToString(getOilWear(distance)));
				mEtCost.setText(SomeUtil.doubleToString(getOilCost(price, distance)));
			}
		}
	};

}
