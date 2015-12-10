package com.tonggou.gsm.andclient.ui.view;
import org.joda.time.DateTime;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import com.tonggou.gsm.andclient.R;


public class DateTimePickerDialog extends AbsCustomAlertDialog {

	private TextView mPickedWeekText;
	private DatePicker mDatePicker;
	private TimePicker mTimePicker;
	private DateTime mPickedDateTime;
	private Button mConfirmButton;
	
	public static interface OnDateTimePickedListener {
		public void onDateTimePicked( DateTime dateTime );
	}
	
	public DateTimePickerDialog(Context context) {
		super(context);
	}

	@Override
	protected View getCustomContentView(CharSequence msg) {
		View view = View.inflate(getContext(), R.layout.widget_dialog_date_time_picker, null);
		mPickedWeekText = (TextView) view.findViewById(R.id.picked_week);
		mTimePicker = (TimePicker) view.findViewById(R.id.time_picker);
		mTimePicker.setIs24HourView(true);
		mDatePicker = (DatePicker) view.findViewById(R.id.date_picker);
		mConfirmButton = (Button) view.findViewById(R.id.btn_confirm);
		return view;
	}

	@Override
	@Deprecated
	public void showDialog(CharSequence msg) {
		super.showDialog(msg);
	}

	@Override
	@Deprecated
	public void showDialog(int msgRes) {
		super.showDialog(msgRes);
	}
	
	public void showDialog( DateTime dateTime, final OnDateTimePickedListener onDateTimePickedListener  ) {
		super.showDialog("");
		mPickedDateTime = dateTime;
		mTimePicker.setCurrentHour(dateTime.getHourOfDay());
		mTimePicker.setCurrentMinute(dateTime.getMinuteOfHour());
		updateWeekText(mPickedDateTime);
		mTimePicker.setOnTimeChangedListener(new OnTimeChangedListener() {
			
			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				mPickedDateTime = mPickedDateTime.withHourOfDay(hourOfDay);
				mPickedDateTime = mPickedDateTime.withMinuteOfHour(minute);
				updateWeekText(mPickedDateTime);
			}
		});
		mDatePicker.init(dateTime.year().get(), dateTime.getMonthOfYear() - 1, dateTime.getDayOfMonth(), new OnDateChangedListener() {
			
			@Override
			public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				mPickedDateTime = mPickedDateTime.withYear(year);
				mPickedDateTime = mPickedDateTime.withMonthOfYear(monthOfYear + 1);
				mPickedDateTime = mPickedDateTime.withDayOfMonth(dayOfMonth);
				updateWeekText(mPickedDateTime);
			}
		});
		mConfirmButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
				if( onDateTimePickedListener != null ) {
					onDateTimePickedListener.onDateTimePicked(mPickedDateTime);
				}
			}
		});
	}

	private void updateWeekText( DateTime dateTime ) {
		mPickedWeekText.setText( dateTime.toString("EEEE") );
	} 
}
