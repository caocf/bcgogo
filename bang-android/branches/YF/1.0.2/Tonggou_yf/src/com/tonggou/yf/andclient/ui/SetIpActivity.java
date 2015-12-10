package com.tonggou.yf.andclient.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.tonggou.yf.andclient.App;
import com.tonggou.yf.andclient.AppConfig;
import com.tonggou.yf.andclient.R;
import com.tonggou.yf.andclient.util.ContextUtil;

public class SetIpActivity extends BaseActivity {

	EditText mBaseUrlEdit;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_set_ip);
		
		mBaseUrlEdit = myFindViewById(R.id.base_url_edit);
	}
	
	public void onOkBtnClick(View view) {
		String url = mBaseUrlEdit.getText().toString();
		if( TextUtils.isEmpty(url) ) {
			App.showShortToast("请输入完整 base url");
			return;
		}
		AppConfig.setHost(url);
		ContextUtil.startActivity(this, LoginActivity.class);
		finish();
	}
	
}
