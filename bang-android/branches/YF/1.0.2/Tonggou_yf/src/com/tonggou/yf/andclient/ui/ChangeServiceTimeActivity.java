package com.tonggou.yf.andclient.ui;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import android.R.color;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.tonggou.yf.andclient.App;
import com.tonggou.yf.andclient.R;
import com.tonggou.yf.andclient.widget.view.SimpleTitleBar;

public class ChangeServiceTimeActivity extends BackableTitleBarActivity
		implements OnDateSetListener, OnTimeSetListener {
	
	public static final String EXTRA_DATETIME = "extra_datetime";
	
	Button mDatePickerBtn;
	Button mTimePickerBtn;
	DatePickerDialog mDatePickerDialog;
	TimePickerDialog mTimePickerDialog;
	Calendar mCalendar;
	
	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setContentView(R.layout.activity_change_service_time);
		
		mDatePickerBtn = myFindViewById(R.id.date_picker_btn);
		mTimePickerBtn = myFindViewById(R.id.time_picker_btn);
		afterViews();
	}
	
	@Override
	protected void onTitleBarCreated(SimpleTitleBar titleBar) {
		super.onTitleBarCreated(titleBar);
		
		titleBar.setTitle(R.string.btn_handled_change_time);
		titleBar.setRightButton(getString(R.string.btn_titlebar_right), color.transparent);
		titleBar.setOnRightButtonClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onChangeServiceTimeComplete();
			}
		});
	}
	
	void afterViews() {
		mCalendar = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT+8"), Locale.getDefault());
		updateDatePickerBtn();
		updateTimePickerBtn();
		mDatePickerBtn.post(new Runnable() {
			
			@Override
			public void run() {
				mDatePickerBtn.performClick();
			}
		});
	}
	
	private void onChangeServiceTimeComplete() {
		if(mCalendar.getTimeInMillis() < System.currentTimeMillis()  ) {
			App.showLongToast(getString(R.string.info_datetime_invalidate));
			return;
		}
		Intent data = new Intent();
		data.putExtra(EXTRA_DATETIME, mCalendar.getTimeInMillis());
		setResult(RESULT_OK, data);
		finish();
	}
	
	public void onDatePickerBtnClick(View view) {
		dismissDialog(mDatePickerDialog);
		mDatePickerDialog = new DatePickerDialog(this, this, 
				mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
		mDatePickerDialog.show();
	}
	
	public void onTimePickerBtnClick(View view) {
		dismissDialog(mTimePickerDialog);
		mTimePickerDialog = new TimePickerDialog(this, this, 
				mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), true);
		mTimePickerDialog.show();
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		mCalendar.set(year, monthOfYear, dayOfMonth);
		updateDatePickerBtn();
	}

	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
		mCalendar.set(Calendar.MINUTE, minute);
		updateTimePickerBtn();
	}
	
	private void dismissDialog(Dialog dialog) {
		if( dialog != null && dialog.isShowing() ) {
			dialog.dismiss();
		}
		dialog = null;
	}
	
	private void updateDatePickerBtn() {
		String formateStr = DateUtils.formatDateTime(this, mCalendar.getTimeInMillis(), 
				DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_WEEKDAY);
		mDatePickerBtn.setText(formateStr);
	}
	
	private void updateTimePickerBtn() {
		String formateStr = DateUtils.formatDateTime(this, mCalendar.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME);
		mTimePickerBtn.setText(formateStr);
	}
	
	@Override
	protected void onDestroy() {
		dismissDialog(mDatePickerDialog);
		dismissDialog(mTimePickerDialog);
		super.onDestroy();
	}
}
