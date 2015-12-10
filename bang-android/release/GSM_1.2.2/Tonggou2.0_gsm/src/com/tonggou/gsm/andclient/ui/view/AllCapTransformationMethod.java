package com.tonggou.gsm.andclient.ui.view;

import android.text.method.ReplacementTransformationMethod;

public class AllCapTransformationMethod extends ReplacementTransformationMethod {

	@Override
	protected char[] getOriginal() {
		char[] c = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
				'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
				'x', 'y', 'z' };
		return c;
	}

	@Override
	protected char[] getReplacement() {
		char[] c = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
				'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
				'X', 'Y', 'Z' };
		return c;
	}

}
