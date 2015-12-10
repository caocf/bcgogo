package com.tonggou.gsm.andclient.ui;

import java.util.Arrays;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.tonggou.gsm.andclient.App;
import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.ui.view.AbsViewHolderAdapter;
import com.tonggou.gsm.andclient.ui.view.AllCapTransformationMethod;
import com.tonggou.gsm.andclient.ui.view.LettersDigitsKeyListener;
import com.tonggou.gsm.andclient.ui.view.SimpleTitleBar;
import com.tonggou.gsm.andclient.util.StringUtil;

public class EditVehicleNoActivity extends BackableTitleBarActivity implements OnItemClickListener {

	public static final String EXTRA_VEHICLE_NO = "extra_vehicle_no";
	public static final String EXTRA_RESULT_DATA_VEHICLE_NO = "extra_result_data_vehicle_no";
	
	TextView mVehicleNoPrefixText;
	EditText mVehicleNoEdit;
	GridView mVehicleNoPrefixGrid;
	VehicleNoPrefixAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setContentView(R.layout.activity_edit_vehicle_no);
		
		mVehicleNoPrefixText = (TextView) findViewById(R.id.vehicle_no_prefix_text);
		mVehicleNoEdit = (EditText) findViewById(R.id.vehicle_no_editText);
		mVehicleNoPrefixGrid = (GridView) findViewById(R.id.vehicle_no_prefix_grid);
		
		// 输入框只能输入字母和数字，并且字母大写
		mVehicleNoEdit.setKeyListener(new LettersDigitsKeyListener());
		mVehicleNoEdit.setTransformationMethod(new AllCapTransformationMethod());
		
		String[] vehicleNoPrefixArr = getResources().getStringArray(R.array.vehicle_no_prefix);
		mAdapter = new VehicleNoPrefixAdapter(this, vehicleNoPrefixArr, R.layout.item_grid_vehicle_no_prefix);
		mVehicleNoPrefixGrid.setAdapter(mAdapter);
		mVehicleNoPrefixGrid.setOnItemClickListener(this);
		
		if( !restoreExtras(getIntent()) ) {
			restoreExtras(savedInstance);
		}
		
		// 初始化
		if( TextUtils.isEmpty( mVehicleNoPrefixText.getText() ) ) {
			mVehicleNoPrefixText.setText(vehicleNoPrefixArr[0]);
		}
	}
	
	@Override
	protected boolean restoreExtras(Bundle extra) {
		if( extra != null && extra.containsKey(EXTRA_VEHICLE_NO) ) {
			String vehicleNo = extra.getString(EXTRA_VEHICLE_NO);
			if( StringUtil.isVehicleNo(vehicleNo) ) {
				mVehicleNoPrefixText.setText( vehicleNo.substring(0, 1) );
				mVehicleNoEdit.setText( vehicleNo.substring(1) );
				mVehicleNoEdit.setSelection(mVehicleNoEdit.length());
			}
			return true;
		}
		return false;
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString(EXTRA_VEHICLE_NO, getVehicleNo());
		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onTitleBarCreated(SimpleTitleBar titleBar) {
		super.onTitleBarCreated(titleBar);
		titleBar.setTitle(R.string.title_edit_vehicle_no);
		titleBar.setRightButtonText(getString(R.string.btn_confirm));
		titleBar.setOnRightButtonClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				doConfirm();
			}
		});
	}
	
	private void doConfirm() {
		String vehicleNo = getVehicleNo();
		if( StringUtil.isVehicleNo(vehicleNo) ) {
			Intent data = new Intent();
			data.putExtra(EXTRA_RESULT_DATA_VEHICLE_NO, vehicleNo);
			setResult(RESULT_OK, data);
			finish();
		} else {
			App.showShortToast(getString(R.string.info_vehicle_no_incorrect));
		}
	}
	
	private String getVehicleNo() {
		return mVehicleNoPrefixText.getText() + mVehicleNoEdit.getEditableText().toString().toUpperCase(Locale.getDefault());
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		mVehicleNoPrefixText.setText(mAdapter.getData().get(position));
	}
	
	class VehicleNoPrefixAdapter extends AbsViewHolderAdapter<String> {

		public VehicleNoPrefixAdapter(Context context, String[] data, int layoutRes) {
			super(context, Arrays.asList(data), layoutRes);
		}

		@Override
		protected void bindData(int pos, String itemData) {
			TextView content = getViewFromHolder(android.R.id.text1);
			content.setText(itemData);
		}
	}

}
