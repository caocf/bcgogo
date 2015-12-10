package com.tonggou.gsm.andclient.ui;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import android.text.TextUtils;

import com.tonggou.gsm.andclient.App;
import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.ui.view.DateTimePickerDialog;
import com.tonggou.gsm.andclient.ui.view.DateTimePickerDialog.OnDateTimePickedListener;
import com.tonggou.gsm.andclient.ui.view.IndicatorTextView;
import com.tonggou.gsm.andclient.util.StringUtil;

public class DateTimePickerBackableActivity extends BackableTitleBarActivity {

	DateTimePickerDialog mDatePickerDialog;
	
	void showDatePickerDialog(final IndicatorTextView indicatorTextView) {
		dismissDatePickerDialog();
		
		String dateStr = indicatorTextView.getTextValue();
		DateTime dateTime = new DateTime();
		if( !TextUtils.isEmpty( dateStr) ) {
			dateTime = formatDateTimeYYYYHHddHHmm(dateStr);
		}
		
		mDatePickerDialog = new DateTimePickerDialog(this);
		
		if( !isFinishing() ) {
			mDatePickerDialog.showDialog(dateTime, new OnDateTimePickedListener() {
				
				@Override
				public void onDateTimePicked(DateTime pickedDateTime) {
					DateTime currentDate = new DateTime();
					if( currentDate.isAfter(pickedDateTime.getMillis()) ) {
						App.showLongToast(getString(R.string.txt_info_picked_date_time_must_after_now));
						return;
					}
					String picketDateStr = pickedDateTime.toString(StringUtil.DATE_FORMAT_DATE_TIME_YYYY_MM_dd_HH_mm);
					indicatorTextView.setTextValue(picketDateStr);
				}
			});
		}
	}
	
	void dismissDatePickerDialog() {
		if( mDatePickerDialog != null && mDatePickerDialog.isShowing() ) {
			mDatePickerDialog.dismiss();
		}
		mDatePickerDialog = null;
	}
	
	@Override
	protected void onDestroy() {
		dismissDatePickerDialog();
		super.onDestroy();
	}	
	
	public DateTime formatDateTimeYYYYHHddHHmm(String dateTimeStr) {
		DateTimeFormatter formatter = DateTimeFormat.forPattern(StringUtil.DATE_FORMAT_DATE_TIME_YYYY_MM_dd_HH_mm);
		return DateTime.parse(dateTimeStr, formatter);
	}
}
