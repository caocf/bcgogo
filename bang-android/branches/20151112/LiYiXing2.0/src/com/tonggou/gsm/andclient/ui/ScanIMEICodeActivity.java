package com.tonggou.gsm.andclient.ui;

import net.sourceforge.zbar.Symbol;
import android.content.Intent;

import com.tonggou.gsm.andclient.App;
import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.ui.view.SimpleTitleBar;

public class ScanIMEICodeActivity extends AbsScanBarCodeActivity {

	@Override
	protected void onTitleBarCreated(SimpleTitleBar titleBar) {
		super.onTitleBarCreated(titleBar);
		titleBar.setTitle(R.string.title_scan_bar_code, R.color.black);
	}
	
	@Override
	int[] getSymbols() {
		return new int[]{
				Symbol.QRCODE, Symbol.CODABAR,
				Symbol.CODE128, Symbol.ISBN13, 
				Symbol.CODE39, Symbol.CODE93,
                Symbol.DATABAR, Symbol.DATABAR_EXP, 
                Symbol.EAN13, Symbol.EAN8, 
                Symbol.I25, Symbol.ISBN10 };
	}

	@Override
	void onScanSuccess(String result) {
		App.showShortToast(result);
		
		Intent data = new Intent();
		data.putExtra(EXTRA_SCAN_RESULT_STR, result);
		setResult(RESULT_OK, data);
		finish();
		// 模拟扫描不成功的情况
//		new Handler().postDelayed(new Runnable() {
//			
//			@Override
//			public void run() {
//				rescan();
//			}
//		}, 2000);
	}

}
