/*
 * Copyright (C) 2011 Make Ramen, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tonggou.yf.andclient.widget.view;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.tonggou.yf.andclient.R;

public class SegmentedRadioGroup extends RadioGroup {

	private int mSingleChildBackground;
	private int mLeftChildBackground;
	private int mCenterChildBackground;
	private int mRightChildBackground;
	
	ArrayList<Integer> mRadioButtonIndexList;
	
	public SegmentedRadioGroup(Context context) {
		this(context, null);
	}

	public SegmentedRadioGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		mRadioButtonIndexList = new ArrayList<Integer>();
		
		if( attrs != null ) {
			TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SegmentedRadioGroup);
			mSingleChildBackground = a.getResourceId(R.styleable.SegmentedRadioGroup_singleChildBackground, Color.TRANSPARENT);
			mLeftChildBackground = a.getResourceId(R.styleable.SegmentedRadioGroup_leftChildBackground, Color.TRANSPARENT);
			mCenterChildBackground = a.getResourceId(R.styleable.SegmentedRadioGroup_centerChildBackground, Color.TRANSPARENT);
			mRightChildBackground = a.getResourceId(R.styleable.SegmentedRadioGroup_rightChildBackground, Color.TRANSPARENT);
			a.recycle();
		}
	}
	
	public void setChildrenBackground(int left, int center, int right, int single) {
		mLeftChildBackground = left;
		mCenterChildBackground = center;
		mRightChildBackground = right;
		mSingleChildBackground = single;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		changeButtonsStyle();
	}
	
	protected void changeButtonsStyle() {
		final int COUNT = getChildCount();
		mRadioButtonIndexList.clear();
		for( int i=0; i<COUNT; i++ ) {
			if( getChildAt(i) instanceof RadioButton)
				mRadioButtonIndexList.add(i);
		}
			
		if( mRadioButtonIndexList.isEmpty() ) {
			return;
		}
		
		final int SIZE = mRadioButtonIndexList.size();
		if( SIZE == 1 ) {
			getChildAt(mRadioButtonIndexList.get(0)).setBackgroundResource(mSingleChildBackground);
		} else {
			getChildAt(mRadioButtonIndexList.get(0)).setBackgroundResource(mLeftChildBackground);
			for (int i = 1; i < SIZE - 1; i++) {
				getChildAt(mRadioButtonIndexList.get(i)).setBackgroundResource(mCenterChildBackground);
			}
			getChildAt(mRadioButtonIndexList.get(SIZE - 1)).setBackgroundResource(mRightChildBackground);
		}
	}
	
}