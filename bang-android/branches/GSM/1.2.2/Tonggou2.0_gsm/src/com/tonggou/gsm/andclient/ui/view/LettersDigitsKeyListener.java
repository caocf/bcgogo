package com.tonggou.gsm.andclient.ui.view;

import android.text.InputType;
import android.text.method.NumberKeyListener;

public class LettersDigitsKeyListener extends NumberKeyListener {

	private final static char[] ACCEPTED_CHARS;
	static {
		final String ACCEPTED_STR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		ACCEPTED_CHARS = new char[ACCEPTED_STR.length()];
		ACCEPTED_STR.getChars(0, ACCEPTED_CHARS.length, ACCEPTED_CHARS, 0);
	}
	
	@Override
	protected char[] getAcceptedChars() {
		return ACCEPTED_CHARS;
	}

	@Override
	public int getInputType() {
		return InputType.TYPE_CLASS_TEXT;
	}

}
