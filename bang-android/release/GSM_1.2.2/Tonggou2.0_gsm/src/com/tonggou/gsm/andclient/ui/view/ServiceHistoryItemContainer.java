package com.tonggou.gsm.andclient.ui.view;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.bean.ServiceItem;

/**
 * 服务历史明细
 * @author lwz
 *
 */
public class ServiceHistoryItemContainer extends LinearLayout {

	public ServiceHistoryItemContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ServiceHistoryItemContainer(Context context) {
		super(context);
		init();
	}
	
	private void init() {
		setOrientation(LinearLayout.VERTICAL);
		setVisibility(View.GONE);
	}
	
	public void setItemsValue(ArrayList<ServiceItem> services, ArrayList<ServiceItem> products ) {
		removeAllViews();
		addView( View.inflate(getContext(), R.layout.widget_payment_detail_title, null) );
		setServiceHistoryItems(getResources().getString(R.string.subtitle_payment_service), services);
		setServiceHistoryItems(getResources().getString(R.string.subtitle_payment_product), products);
		if( getChildCount() > 1 ) {
			getChildAt(getChildCount() - 1).setBackgroundColor(Color.TRANSPARENT);
		}
	}
	
	private void setServiceHistoryItems(String subtitle, ArrayList<ServiceItem> items) {
		
		if( items != null && !items.isEmpty() ) {
			setVisibility(View.VISIBLE);
			addServiceHistoryItemView(subtitle, null, true, subtitle.length());
			int maxIndaictorLength = 0;
			for(ServiceItem item : items) {
				maxIndaictorLength = Math.max( item.getContent().length() , maxIndaictorLength);
			}
			for(ServiceItem item : items  ) {
				addServiceHistoryItemView(item.getContent(), getResources().getString(
						R.string.format_paid_total_money, item.getAmount()), false, maxIndaictorLength);
			}
		}
	}
	
	private void addServiceHistoryItemView(CharSequence title, CharSequence value, boolean isSubtitle, int leftIndicatorTextlength) {
		final int rightIndicatorPadding = getResources().getDimensionPixelOffset(R.dimen.dimen_10dp);
		IndicatorTextView itv = (IndicatorTextView) View.inflate(getContext(), R.layout.widget_payment_detail_item, null);
		if( isSubtitle ) {
			itv.setIndicatorTextValues(title);
		} else {
			itv.setIndicatorTextValues(title, value);
			TextView rightIndicator = itv.getRightIndicators().get(0);
			rightIndicator.setTextColor(getResources().getColor(R.color.red));
			rightIndicator.setPadding(0, 0, rightIndicatorPadding, 0);
			TextView leftIndiactor = itv.getLeftIndicator();
			leftIndiactor.setTextColor(Color.BLACK);
			leftIndiactor.setGravity(Gravity.LEFT);
			itv.setLeftIndicatorLength(leftIndicatorTextlength);
		}
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		lp.leftMargin = getResources().getDimensionPixelOffset(R.dimen.dimen_1dp);
		lp.rightMargin = lp.leftMargin;
		addView(itv, lp);
	}
}
	
