package com.tonggou.andclient;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.tonggou.andclient.util.DJDatabase;
import com.tonggou.andclient.vo.DrivingJournalItem;

public class VehicleConditionAdjustActivity extends BaseActivity {
	public static final String EXTRA_DJITEM = "extra_dj_item";
	private DJDatabase mDjDatabase;
	private DrivingJournalItem mDjItem;
	private InputMethodManager mImm;
	private EditText mEtPrice, mEtWear, mEtCost, mEtDistance;

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
			setEtData(mEtPrice, String.valueOf(mDjItem.getOilPrice()));
			setEtData(mEtWear, String.valueOf(mDjItem.getOilWear()));
			setEtData(mEtCost, String.valueOf(mDjItem.getTotalOilMoney()));
			setEtData(mEtDistance, String.valueOf(mDjItem.getDistance()));
			mEtPrice.requestFocus();
			mEtPrice.postDelayed(new Runnable() {
				@Override
				public void run() {
					mImm = (InputMethodManager) getApplicationContext().getSystemService(
							Context.INPUT_METHOD_SERVICE);
					mImm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
				}
			}, 500);

		}
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
			mDjItem.setOilPrice(getEtData(mEtPrice));
			mDjItem.setOilWear(getEtData(mEtWear));
			mDjItem.setTotalOilMoney(getEtData(mEtCost));
			mDjItem.setDistance(getEtData(mEtDistance));
		}
	}

	private void initViews() {
		RelativeLayout rlBack = (RelativeLayout) findViewById(R.id.rl_vehicle_condition_adjust_back);
		mEtPrice = (EditText) findViewById(R.id.et_vehicle_condition_adjust_oil_price);
		mEtWear = (EditText) findViewById(R.id.et_vehicle_condition_adjust_oil_wear);
		mEtCost = (EditText) findViewById(R.id.et_vehicle_condition_adjust_oil_cost);
		mEtDistance = (EditText) findViewById(R.id.et_vehicle_condition_adjust_distance);
		ImageView ivCommit = (ImageView) findViewById(R.id.iv_vehicle_condition_adjust_commit);

		rlBack.setOnClickListener(mOnClickListener);
		ivCommit.setOnClickListener(mOnClickListener);
	}

	private OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.rl_vehicle_condition_adjust_back:
				if (mImm != null) {
					mImm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
				}
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
