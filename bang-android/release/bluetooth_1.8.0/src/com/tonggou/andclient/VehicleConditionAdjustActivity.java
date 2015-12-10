package com.tonggou.andclient;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
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

	@Override
	protected void onResume() {
		super.onResume();
		mEtPrice.addTextChangedListener(mTextWatcher);
		mEtDistance.addTextChangedListener(mTextWatcher);
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
			setEtData(mEtWear,
					SomeUtil.doubleToString(SomeUtil.getOilWear(mAvgOilWear, mDjItem.getDistance()))); // 油耗(根据平均油耗来算)
			setEtData(mEtCost, SomeUtil.doubleToString(mDjItem.getTotalOilMoney())); // 油费
			mEtPrice.requestFocus();
		}
	}

	private void setEtData(EditText et, String data) {
		if (data != null) {
			et.setText(data);
			et.setSelection(data.length());
		}
	}

	private double getDoubleEtData(EditText et) {
		return SomeUtil.fmtDouble(getStrEtData(et));
	}

	private String getStrEtData(EditText et) {
		return et.getText().toString().trim();
	}

	private boolean isTextQualified(EditText et) {
		return SomeUtil.isNumeric2(getStrEtData(et));
	}

	private boolean isAllQualified() {
		return isTextQualified(mEtPrice) && isTextQualified(mEtDistance);
	}

	private void setDJItem() {
		if (mDjItem != null) {
			if (!isAllQualified()) {
				return;
			}
			double oilPrice = getDoubleEtData(mEtPrice);
			double distance = getDoubleEtData(mEtDistance);
			mDjItem.setOilWear(mAvgOilWear);
			mDjItem.setOilPrice(oilPrice);
			mDjItem.setDistance(distance);
			mDjItem.setTotalOilMoney(getDoubleEtData(mEtCost));
			if (oilPrice != SomeUtil.stringToDouble(mOrigOilPrice)
					|| distance != SomeUtil.stringToDouble(mOrigDistance)) {
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
	}

	private TextWatcher mTextWatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			if (isAllQualified()) {
				double oilPrice = getDoubleEtData(mEtPrice);
				double distance = getDoubleEtData(mEtDistance);
				mEtWear.setText(SomeUtil.doubleToString(SomeUtil.getOilWear(mAvgOilWear, distance)));
				mEtCost.setText(SomeUtil.doubleToString(SomeUtil.getOilCost(mAvgOilWear, oilPrice, distance)));
			}
		}
	};

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

}
