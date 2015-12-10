package com.tonggou.gsm.andclient.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;

import android.content.Context;
import android.util.Xml.Encoding;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.ResponseHandlerInterface;
import com.tonggou.gsm.andclient.App;
import com.tonggou.gsm.andclient.util.PreferenceUtil;

public class HttpRequestClient {
	public static final String TAG = "HttpRequestClient";

	private static final String PREF_NAME_COOKIE = "pref_cookie";
	private static final String PREF_KEY_COOKIE = "JSESSIONID";

	public static final String PREF_NAME_USER_INFO = "pref_user_info";
	public static final String PREF_KEY_USER_INFO = "user_info";

	private static final int DEFAULT_TETRY_TIMES = 1;			// 最大的重试次数
	private static final int DEFAULT_TIMEOUT = 20 * 1000;		// 连接超时时间
	private static final int DEFAULT_MAX_CONNECTIONS = 10;		// 最大连接数

	private static final String HEADER_KEY_COOKIE = "Cookie";

	private Context mContext;
	private static AsyncHttpClient msAsyncHttpClient;

	public HttpRequestClient(Context context) {
		mContext = context;
	}

	/**
	 * GET 请求
	 * @param url			请求的 URL
	 * @param params		参数，可为 null
	 * @param responseHandler	响应处理器
	 */
	public void get(String url, HttpRequestParams params, ResponseHandlerInterface responseHandler) {
		AsyncHttpClient client = getDefaultClient();
		client.get(mContext, url, HttpRequestParams.convert2RequestParams(params), responseHandler);
	}

	/**
	 * POST 请求
	 * @param url				请求的 URL
	 * @param params			参数，可为 null
	 * @param responseHandler	响应处理器
	 */
	public void post(String url, HttpRequestParams params, ResponseHandlerInterface responseHandler) {
		AsyncHttpClient client = getDefaultClient();
		client.post(mContext, url, HttpRequestParams.convert2RequestParams(params), responseHandler);
	}

	/**
	 * PUT 请求
	 * @param url				请求的 URL
	 * @param params			参数，可为 null
	 * @param responseHandler	响应处理器
	 */
	public void put(String url, HttpRequestParams params, ResponseHandlerInterface responseHandler) {
		AsyncHttpClient client = getDefaultClient();
		try {
			HttpEntity entity = new StringEntity(params.toString(), Encoding.UTF_8.name());
			final String CONTENT_TYPE = "application/json";
			client.put(mContext, url, entity, CONTENT_TYPE, responseHandler);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * DELETE 请求
	 * @param url				请求的 URL
	 * @param params			参数，可为 null
	 * @param responseHandler	响应处理器
	 */
	public void delete(String url, ResponseHandlerInterface responseHandler) {
		AsyncHttpClient client = getDefaultClient();
		client.delete(mContext, url, responseHandler);
	}

	public AsyncHttpClient getDefaultClient() {
		if( msAsyncHttpClient == null ) {
			AsyncHttpClient client = new AsyncHttpClient(80, 443);
			Locale locale = Locale.getDefault();
			client.addHeader("Accept-Language", (locale.getLanguage()+"-"+locale.getCountry()).toLowerCase(locale));
			client.addHeader("Accept-Encoding", "gzip");

			// Basic Auth
//			String userInfoStr = PreferenceUtil.getString(mContext, PREF_NAME_USER_INFO, PREF_KEY_USER_INFO);
//			if( !TextUtils.isEmpty(userInfoStr) ) {
//				UserInfo user = new Gson().fromJson(userInfoStr, UserInfo.class);
//				if( user != null && !TextUtils.isEmpty(user.getUserNo()) && !TextUtils.isEmpty(user.getPassword())) {
//					client.setBasicAuth(user.getUserNo(), user.getPassword());
//				}
//			}

			client.setTimeout(DEFAULT_TIMEOUT);
			client.setMaxRetriesAndTimeout(DEFAULT_TETRY_TIMES, DEFAULT_TIMEOUT);
			client.setMaxConnections(DEFAULT_MAX_CONNECTIONS);
			client.setCookieStore( getCookieStore(mContext) );
			client.setSSLSocketFactory( getSSLSocketFactory() );
			msAsyncHttpClient = client;
		}
		msAsyncHttpClient.addHeader(HEADER_KEY_COOKIE, PREF_KEY_COOKIE + "=" + restoreSessionId(mContext));
		return msAsyncHttpClient;
	}

	private static synchronized CookieStore getCookieStore(final Context context) {
		PersistentCookieStore myCookieStore = new PersistentCookieStore(context) {

			@Override
			public void addCookie(Cookie cookie) {
				if( PREF_KEY_COOKIE.equals( cookie.getName() ) ) {
					storeSessionId(context, cookie.getValue());
				}
				super.addCookie(cookie);
			}

		};
		return myCookieStore;
	}

	private SSLSocketFactory getSSLSocketFactory() {
		SSLSocketFactory sf = null;
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);
			sf = new SSLSocketFactoryEx(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sf;
	}

	/**
	 * 存储 sessionId
	 * @param context
	 * @param sessionId
	 */
	public synchronized static void storeSessionId(Context context, String sessionId) {
		PreferenceUtil.putString(context, PREF_NAME_COOKIE, PREF_KEY_COOKIE, sessionId);
	}

	/**
	 * 获取 本地存储的 sessionId
	 * @param context
	 * @return
	 */
	private String restoreSessionId(Context context) {
		return PreferenceUtil.getString(context, PREF_NAME_COOKIE, PREF_KEY_COOKIE);
	}

	/**
	 * 在 基础API 上加上参数后的新 API
	 * @param api		基础 API
	 * @param params	参数
	 * @return
	 */
	public static String getAPIWithQueryParams(String api, APIQueryParam params) {
		if( params == null ) {
			params = new APIQueryParam();
		}
		api = api.trim();
		if( api.endsWith("/") ) {
			api = api.substring(0, api.length() - 1);
		}
		App.debug(TAG, api + params.toString());
		return api + params.toString();
	}

	public static void cancelRequest(final Context context, final boolean mayInterruptIfRunning) {
		Thread cancelThread = new Thread() {

			@Override
			public void run() {
				HttpRequestClient client = new HttpRequestClient(context);
				client.getDefaultClient().cancelRequests(context, mayInterruptIfRunning);
			}
		};
		cancelThread.setPriority(Thread.MAX_PRIORITY);
		cancelThread.start();
	}
}