package com.tonggou.gsm.andclient.ui;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;

import com.tonggou.gsm.andclient.App;
import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.bean.ShopNotice;
import com.tonggou.gsm.andclient.net.parser.AsyncJsonBaseResponseParseHandler;
import com.tonggou.gsm.andclient.net.request.QueryShopNoticeDetailRequest;
import com.tonggou.gsm.andclient.net.response.QueryShopNoticeResponse;
import com.tonggou.gsm.andclient.ui.view.SimpleTitleBar;
import com.tonggou.gsm.andclient.util.StringUtil;

/**
 * 4S 店铺公告详情
 * @author lwz
 *
 */
public class ShopNoticeDetailActivity extends BackableTitleBarActivity {

	public static final String EXTRA_SHOP_NOTICE = "extra_shop_notice";
	private ShopNotice mShopNotice;
	private WebView mWebView;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		App.getInstance().InitDituSDK();
		setContentView(R.layout.activity_4s_notice_detail);

		if( !restoreExtras(getIntent()) ) {
			restoreExtras(savedInstance);
		}

		mWebView = (WebView) findViewById(R.id.webview);
		WebSettings settings = mWebView.getSettings();
		settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		settings.setJavaScriptEnabled(true);
		settings.setBuiltInZoomControls(false);

		requestNoticeDetail();
	}

	@Override
	protected boolean restoreExtras(Bundle extra) {
		if( extra != null && extra.containsKey(EXTRA_SHOP_NOTICE) ) {
			mShopNotice = extra.getParcelable(EXTRA_SHOP_NOTICE);
			return true;
		}
		return false;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putParcelable(EXTRA_SHOP_NOTICE, mShopNotice);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onTitleBarCreated(SimpleTitleBar titleBar) {
		super.onTitleBarCreated(titleBar);
		titleBar.setTitle(R.string.title_4s_shop_detail);
	}

	private void requestNoticeDetail() {
		showLoadingDialog();
		QueryShopNoticeDetailRequest request = new QueryShopNoticeDetailRequest();
		request.setApiParams(mShopNotice.getId());
		request.doRequest(this, new AsyncJsonBaseResponseParseHandler<QueryShopNoticeResponse>() {

			ShopNotice shopNotice = mShopNotice;

			@Override
			public void onStart() {
				super.onStart();
				shopNotice.setDescription("");
			}

			@Override
			public void onParseSuccess(QueryShopNoticeResponse result, String originResult) {
				super.onParseSuccess(result, originResult);
				ArrayList<ShopNotice> data = result.getAdvertDTOList();
				if( data != null && !data.isEmpty() ) {
					shopNotice = data.get(0);
				}
			}

			@Override
			public void onFinish() {
				super.onFinish();
				loadNoticeDatil(shopNotice);
				dismissLoadingDialog();
			}

			@Override
			public Class<QueryShopNoticeResponse> getTypeClass() {
				return QueryShopNoticeResponse.class;
			}
		});
	}

	private void loadNoticeDatil(final ShopNotice shopNotice) {
		mWebView.addJavascriptInterface(new Object() {

			@JavascriptInterface
			public String getTitle() {
				return shopNotice.getTitle();
			}

			@JavascriptInterface
			public String getTimestamp() {
				String beginDateStr = shopNotice.getBeginDateStr();
				beginDateStr = TextUtils.isEmpty(beginDateStr) ? StringUtil.formatDateYYYYMMdd(shopNotice.getTimestamp()) : beginDateStr;
				String endDateStr = shopNotice.getEndDateStr();
				endDateStr = TextUtils.isEmpty(shopNotice.getEndDateStr()) ? getString(R.string.txt_forever) : endDateStr;
				return getString(R.string.format_shop_notice_timestamp, beginDateStr, endDateStr);
			}

			@JavascriptInterface
			public String getContent() {
				return shopNotice.getDescription();
			}

		}, "injection_js");

		mWebView.loadUrl("file:///android_asset/www/shop_notice_template.html");
	}
}