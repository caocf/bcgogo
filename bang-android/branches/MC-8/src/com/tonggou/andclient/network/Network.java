
package com.tonggou.andclient.network;



import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.net.UnknownServiceException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.zip.GZIPInputStream;
import javax.net.ssl.SSLHandshakeException;
import javax.xml.parsers.FactoryConfigurationError;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerPNames;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.tonggou.andclient.BaseActivity;
import com.tonggou.andclient.LoginActivity;
import com.tonggou.andclient.app.TongGouApplication;
import com.tonggou.andclient.app.TongGouService;
import com.tonggou.andclient.parse.JSONParseInterface;
import com.tonggou.andclient.util.INFO;


public class Network {
	private static Network network = null;
	static DefaultHttpClient defaultHttpClient = null;
	private static Context appContext;
	private static String username;
	private static String password;	
	public static String JSESSIONIDStr;
	private static HttpRequestInterceptor preemptiveAuth = new HttpRequestInterceptor() {

		public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {	    	
			Locale locale = Locale.getDefault();
			request.setHeader("Accept-Language", (locale.getLanguage()+"-"+locale.getCountry()).toLowerCase());
			request.setHeader("Accept-Encoding", "gzip");
			AuthState authState = (AuthState) context.getAttribute(ClientContext.TARGET_AUTH_STATE);
			//HttpHost targetHost = (HttpHost) context.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
			if(true/*(INFO.HOST_IP.equals(targetHost.getHostName())) && (INFO.HOST_PORT == targetHost.getPort())*/) {
				authState.setAuthScheme(new BasicScheme());

				/* username = "26";
                password = "ac7ee4f33fc663a2445586f27ec3fdfc";*/
				if(username == null){
					//            		username = appContext.getSharedPreferences(BaseActivity.SETTING_INFOS, Context.MODE_PRIVATE).getString(BaseActivity.NAME, null);
					//            		password = appContext.getSharedPreferences(BaseActivity.SETTING_INFOS, Context.MODE_PRIVATE).getString(BaseActivity.PASSWORD, null);
				}
				//INFO.Log("username", username+"");
				if(username != null){
					authState.setCredentials( new UsernamePasswordCredentials(username, password));
				}


			}
		}	    
	};


	private static void httpInit(){
		try{
			HttpParams params = new BasicHttpParams();
			params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, Integer.valueOf(30000));     
			params.setParameter(CoreConnectionPNames.SO_TIMEOUT, Integer.valueOf(50000));	         //设置20秒socket超时
			params.setLongParameter(ConnManagerPNames.TIMEOUT, 30000);
			params.setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);    
			params.setParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, Integer.valueOf(8192*3));	 //默认是8192 byte socket buffers
			ConnManagerParams.setMaxTotalConnections(params, 200);
			ConnPerRouteBean connPerRoute = new ConnPerRouteBean(20);
			HttpHost localhost = new HttpHost(INFO.HOST_IP, INFO.HOST_PORT);
			connPerRoute.setMaxForRoute(new HttpRoute(localhost), 50);
			ConnManagerParams.setMaxConnectionsPerRoute(params, connPerRoute);


			////////////////////
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());  
			trustStore.load(null, null);        
			SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);  
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);  
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);  
			////////////////////////////////////

			SchemeRegistry schemeRegistry = new SchemeRegistry();
			schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			schemeRegistry.register(new Scheme("https",sf, 443));
			ClientConnectionManager clientManager = new ThreadSafeClientConnManager(params, schemeRegistry);

			defaultHttpClient = new DefaultHttpClient(clientManager,params);
			defaultHttpClient.setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy(){
				public long getKeepAliveDuration(HttpResponse response, HttpContext context){
					long keepAlive = super.getKeepAliveDuration(response, context);
					if (keepAlive == -1) {
						keepAlive = 5000;
					}

					return keepAlive;
				}
			});
			defaultHttpClient.addRequestInterceptor(preemptiveAuth, 0);
			defaultHttpClient.setHttpRequestRetryHandler(new HttpRequestRetryHandler() {	
				public boolean retryRequest(IOException exception, int executionCount,HttpContext context) {		    	
					//INFO.Log("NETWORKTEST>><<",exception.toString()+"##"+executionCount);
					if (executionCount >= 2) {
						return false;
					}
					if (exception instanceof NoHttpResponseException) {
						// 服务停掉则重新尝试连接
						//INFO.Log("NETWORKTEST>><<","port:"+INFO.HOST_PORT);
						return true;
					}		        
					if (exception instanceof UnknownHostException) {		        	 
						INFO.HOST_PORT = 8080;
						//INFO.Log("NETWORKTEST>><<","port:"+INFO.HOST_PORT);
						return true;
					}	
					if (exception instanceof UnknownServiceException) {		        	 
						INFO.HOST_PORT = 8080;
						//INFO.Log("NETWORKTEST>><<","Service:"+INFO.HOST_PORT);
						return true;
					}
					if (exception instanceof SocketException) {
						//INFO.Log("NETWORKTEST>><<","SocketException");
						return true;
					}		        
					if (exception instanceof SSLHandshakeException) {
						return false;
					}
					HttpRequest request = (HttpRequest) context.getAttribute(ExecutionContext.HTTP_REQUEST);
					boolean idempotent = !(request instanceof HttpEntityEnclosingRequest); 
					if (idempotent) {
						return true;
					}
					return false;
				}
			});

		}catch(Exception ex){
			ex.printStackTrace();
		}

		//ssl

		//FileInputStream instream = new FileInputStream(new File("my.keystore"));  
		//		InputStream instream = appContext.getResources().openRawResource(R.drawable.adcd);  //读证书
		//		try {  
		//			KeyStore trustStore  = KeyStore.getInstance(KeyStore.getDefaultType());   //KeyStore.getDefaultType()  android为 BKS
		//		    trustStore.load(instream, "nopassword".toCharArray());  
		//		    SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore);  
		//			Scheme sch = new Scheme("https", socketFactory, 443);                     //443端口
		//			defaultHttpClient.getConnectionManager().getSchemeRegistry().register(sch);
		//		}catch(Exception e){
		//			e.printStackTrace();
		//		}finally {  
		//		    try {
		//				instream.close();
		//			} catch (IOException e) {
		//				e.printStackTrace();
		//			}  
		//		}   


	}






	public static Network getNetwork(Context context){
		appContext = context;
		if(network == null){
			network = new Network();			
		}
		if(defaultHttpClient == null){
			JSESSIONIDStr = appContext.getSharedPreferences(BaseActivity.SETTING_INFOS, Context.MODE_PRIVATE).getString(BaseActivity.COOKIES_STR, null);
			httpInit();
			//conManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		}
		return network;
	}


	public NetworkState httpGetUpdateString(String uri, JSONParseInterface jsonParser){
		NetworkState netState = new NetworkState();
		int stateCode;		
		HttpGet httpGet = new HttpGet(uri);		
		httpGet.setHeader("Accept-Language", (Locale.getDefault().getLanguage()+"-"+Locale.getDefault().getCountry()).toLowerCase());
		if(JSESSIONIDStr!=null){
			httpGet.setHeader("Cookie", "JSESSIONID=" + JSESSIONIDStr);   
		}
		httpGet.setHeader("Content-Type", "application/json;charset=UTF-8");
		try {

			HttpResponse httpResponse = defaultHttpClient.execute(httpGet);		
			stateCode = httpResponse.getStatusLine().getStatusCode();
			if(stateCode != 200){
				netState.setNetworkState(false);
				netState.setErrorMessage(NetworkState.ERROR_SERVER_ERROR_MESSAGE);
				//Log.d("NETT", NetworkState.ERROR_SERVER_ERROR_MESSAGE+":"+stateCode+uri);
				return netState;
			}

			getCookie(defaultHttpClient);

			HttpEntity httpEntity = httpResponse.getEntity();
			InputStream is = null;
			if(httpResponse.getEntity().getContentEncoding() == null || httpResponse.getEntity().getContentEncoding().getValue().toLowerCase().indexOf("gzip")<0){				
				is = new ByteArrayInputStream(EntityUtils.toByteArray(httpEntity));				
			}else{
				is = new GZIPInputStream(new ByteArrayInputStream(EntityUtils.toByteArray(httpEntity)));
			}

			byte[] con = readInStream(is);
			String str = new String(con);
			//Log.d("NETT", str+"::::>"+uri);
			
			if(str!=null&&str.indexOf("登录过期")!=-1&&str.indexOf("-202")!=-1){	
				//Log.d("RRRR", "RRRRRRRRRRRRRRRRRRRRRRRR登录过期");
				if(appContext instanceof BaseActivity){
					BaseActivity ba = ((BaseActivity)appContext);					
					//////////////////////////////////////
					ba.exit();
					ba.deInit();
					///////////////////////////////////////
					new Thread(){
						public void run(){
							//停掉连接obd
							Intent intent = new Intent();
							intent.setAction(TongGouService.TONGGOU_ACTION_START);
					        intent.putExtra("com.tonggou.server","STOP");
					        appContext.sendBroadcast(intent);
						}
					}.start();
					
					Intent toLogin = new Intent(appContext, LoginActivity.class);
					toLogin.putExtra("tonggou.loginExpire", "登录过期，请重新登录。");
					toLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					appContext.startActivity(toLogin);
				}
				/*if(reloging){
					return netState;
				}
				reloging = true;
				while(!login()){
					count++;
					if(count>=5){
						jsonParser.parsing(str);    //解析
						netState.setNetworkState(true);
						count = 0;
						return netState;
					}
				}
				reloging = false;
				httpGetUpdateString(uri,jsonParser);*/
			}
				jsonParser.parsing(str);    //解析
				//System.out.println(str);
				netState.setNetworkState(true);
				return netState;
			

		} catch(IOException ie){
			netState.setNetworkState(false);
			ie.printStackTrace();
			if(ie instanceof SocketTimeoutException){
				if("en".equals(Locale.getDefault().getLanguage().toLowerCase())){

					netState.setErrorMessage(NetworkState.ERROR_CLIENT_ERROR_SOCKETTIMEOUT_MESSAGE_EN);
				}else{
					netState.setErrorMessage(NetworkState.ERROR_CLIENT_ERROR_SOCKETTIMEOUT_MESSAGE);

				}
				return netState;
			}			
			if("en".equals(Locale.getDefault().getLanguage().toLowerCase())){
				netState.setErrorMessage(NetworkState.ERROR_CLIENT_ERROR_TIMEOUT_MESSAGE_EN);
			}else{
				netState.setErrorMessage(NetworkState.ERROR_CLIENT_ERROR_TIMEOUT_MESSAGE);
			}

		}catch (FactoryConfigurationError e) {
			netState.setNetworkState(false);
			netState.setErrorMessage(NetworkState.ERROR_RESPONSE_ERROR_MESSAGE);
		} catch (NullPointerException ex) {
			netState.setNetworkState(false);
			netState.setErrorMessage(NetworkState.ERROR_RESPONSE_ERROR_MESSAGE);
		}
		return  netState;
	}

	public NetworkState httpPostUpdateString(String uri,ArrayList<NameValuePair> nameValuePairs,JSONParseInterface jsonParser){
		NetworkState netState = new NetworkState();
//		if(wapIf()){
//			uri="http://"+CMWAP_GATEWAY+uri.substring(uri.indexOf("/", 8));
//		}
		int stateCode;		
		HttpPost httpPost = new HttpPost(uri);		
		httpPost.setHeader("Accept-Language", (Locale.getDefault().getLanguage()+"-"+Locale.getDefault().getCountry()).toLowerCase());
		httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
		if(JSESSIONIDStr!=null){
			httpPost.setHeader("Cookie", "JSESSIONID=" + JSESSIONIDStr); 
		}
		//httpPost.setHeader("Content-Type", "application/json");

		try {
			
			//UrlEncodedFormEntity urf = new UrlEncodedFormEntity(nameValuePairs,"UTF-8");
			//byte[] bb = new byte[1024];
			//urf.getContent().read(bb);
			
			//String strs =new String(bb,"UTF-8");
			//Header[] headers =  httpPost.getHeaders("Accept-Language");
			
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
			//Header[] headers1 =  httpPost.getHeaders("Content-Type");

			HttpResponse httpResponse = defaultHttpClient.execute(httpPost);	
			
			
			stateCode = httpResponse.getStatusLine().getStatusCode();
			if(stateCode != 200){
				netState.setNetworkState(false);
				netState.setErrorMessage(NetworkState.ERROR_SERVER_ERROR_MESSAGE);
				//Log.d("NETT", NetworkState.ERROR_SERVER_ERROR_MESSAGE+":"+stateCode+uri);
				return netState;
			}

			getCookie(defaultHttpClient);

			HttpEntity httpEntity = httpResponse.getEntity();
			InputStream is = null;
			if(httpResponse.getEntity().getContentEncoding() == null || httpResponse.getEntity().getContentEncoding().getValue().toLowerCase().indexOf("gzip")<0){				
				is = new ByteArrayInputStream(EntityUtils.toByteArray(httpEntity));				
			}else{
				is = new GZIPInputStream(new ByteArrayInputStream(EntityUtils.toByteArray(httpEntity)));
			}

			byte[] con = readInStream(is);
			String str = new String(con);
			//Log.d("NETT", str);
			if(str!=null&&str.indexOf("登录过期")!=-1&&str.indexOf("-202")!=-1){	
				if(appContext instanceof BaseActivity){
					BaseActivity ba = ((BaseActivity)appContext);					
					//////////////////////////////////////
					ba.exit();
					ba.deInit();
					///////////////////////////////////////
					new Thread(){
						public void run(){
							//停掉连接obd
							Intent intent = new Intent();
							intent.setAction(TongGouService.TONGGOU_ACTION_START);
					        intent.putExtra("com.tonggou.server","STOP");
					        appContext.sendBroadcast(intent);
						}
					}.start();
					
					Intent toLogin = new Intent(appContext, LoginActivity.class);
					toLogin.putExtra("tonggou.loginExpire", "登录过期，请重新登录。");
					toLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					appContext.startActivity(toLogin);
				}
			}

			jsonParser.parsing(str);    //解析
			netState.setNetworkState(true);
			
			return netState;

		} catch(IOException ie){
			netState.setNetworkState(false);
			ie.printStackTrace();
			if(ie instanceof SocketTimeoutException){ 
				if("en".equals(Locale.getDefault().getLanguage().toLowerCase())){

					netState.setErrorMessage(NetworkState.ERROR_CLIENT_ERROR_SOCKETTIMEOUT_MESSAGE_EN);
				}else{
					netState.setErrorMessage(NetworkState.ERROR_CLIENT_ERROR_SOCKETTIMEOUT_MESSAGE);

				}
				return netState;
			}			
			if("en".equals(Locale.getDefault().getLanguage().toLowerCase())){
				netState.setErrorMessage(NetworkState.ERROR_CLIENT_ERROR_TIMEOUT_MESSAGE_EN);
			}else{
				netState.setErrorMessage(NetworkState.ERROR_CLIENT_ERROR_TIMEOUT_MESSAGE);
			}

		}catch (FactoryConfigurationError e) {
			netState.setNetworkState(false);
			netState.setErrorMessage(NetworkState.ERROR_RESPONSE_ERROR_MESSAGE);
		} catch (NullPointerException ex) {
			netState.setNetworkState(false);
			netState.setErrorMessage(NetworkState.ERROR_RESPONSE_ERROR_MESSAGE);
		}
		return  netState;
	}




	public NetworkState httpPutUpdateString(String uri, byte[] valuePairs,JSONParseInterface jsonParser){
		//测试代码/////////////////////////////////////////////
		//String jsonData = "[{\"id\":\"22\",\"content\":内容1},{\"id\":\"33\",\"content\":内容2}]";
		//String jsonData = "[{id:22,content:内容1},{id:33,content:内容2}]";
		//jsonParser.parsing(jsonData);    //解析
		////////////////////////////////////////////////////////
		NetworkState netState = new NetworkState();
//		if(wapIf()){
//			uri="http://"+CMWAP_GATEWAY+uri.substring(uri.indexOf("/", 8));
//		}
		int stateCode;		
		HttpPut httpPut = new HttpPut(uri);		
		httpPut.setHeader("Accept-Language", (Locale.getDefault().getLanguage()+"-"+Locale.getDefault().getCountry()).toLowerCase());
		if(JSESSIONIDStr!=null){
			httpPut.setHeader("Cookie", "JSESSIONID=" + JSESSIONIDStr); 
		}
		httpPut.setHeader("Content-Type", "application/json;charset=UTF-8");

		try {
			httpPut.setEntity(new ByteArrayEntity(valuePairs));

			HttpResponse httpResponse = defaultHttpClient.execute(httpPut);		
			stateCode = httpResponse.getStatusLine().getStatusCode();
			if(stateCode != 200){
				netState.setNetworkState(false);
				netState.setErrorMessage(NetworkState.ERROR_SERVER_ERROR_MESSAGE);
				//Log.d("NETT", NetworkState.ERROR_SERVER_ERROR_MESSAGE+":"+stateCode+uri);
				return netState;
			}

			getCookie(defaultHttpClient);

			HttpEntity httpEntity = httpResponse.getEntity();
			InputStream is = null;
			if(httpResponse.getEntity().getContentEncoding() == null || httpResponse.getEntity().getContentEncoding().getValue().toLowerCase().indexOf("gzip")<0){				
				is = new ByteArrayInputStream(EntityUtils.toByteArray(httpEntity));				
			}else{
				is = new GZIPInputStream(new ByteArrayInputStream(EntityUtils.toByteArray(httpEntity)));
			}

			byte[] con = readInStream(is);
			String str = new String(con);
			if(str!=null&&str.indexOf("登录过期")!=-1&&str.indexOf("-202")!=-1){	
				if(appContext instanceof BaseActivity){
					BaseActivity ba = ((BaseActivity)appContext);					
					//////////////////////////////////////
					ba.exit();
					ba.deInit();
					///////////////////////////////////////
					new Thread(){
						public void run(){
							//停掉连接obd
							Intent intent = new Intent();
							intent.setAction(TongGouService.TONGGOU_ACTION_START);
					        intent.putExtra("com.tonggou.server","STOP");
					        appContext.sendBroadcast(intent);
						}
					}.start();
					
					Intent toLogin = new Intent(appContext, LoginActivity.class);
					toLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					toLogin.putExtra("tonggou.loginExpire", "登录过期，请重新登录。");
					appContext.startActivity(toLogin);
				}
			}
			TongGouApplication.showLog(str);
			//Log.d("NETT", str);
			jsonParser.parsing(str);    //解析
			netState.setNetworkState(true);
		
			return netState;

		} catch(IOException ie){
			netState.setNetworkState(false);
			ie.printStackTrace();
			if(ie instanceof SocketTimeoutException){
				if("en".equals(Locale.getDefault().getLanguage().toLowerCase())){

					netState.setErrorMessage(NetworkState.ERROR_CLIENT_ERROR_SOCKETTIMEOUT_MESSAGE_EN);
				}else{
					netState.setErrorMessage(NetworkState.ERROR_CLIENT_ERROR_SOCKETTIMEOUT_MESSAGE);

				}
				return netState;
			}			
			if("en".equals(Locale.getDefault().getLanguage().toLowerCase())){
				netState.setErrorMessage(NetworkState.ERROR_CLIENT_ERROR_TIMEOUT_MESSAGE_EN);
			}else{
				netState.setErrorMessage(NetworkState.ERROR_CLIENT_ERROR_TIMEOUT_MESSAGE);
			}

		}catch (FactoryConfigurationError e) {
			netState.setNetworkState(false);
			netState.setErrorMessage(NetworkState.ERROR_RESPONSE_ERROR_MESSAGE);
		} catch (NullPointerException ex) {
			netState.setNetworkState(false);
			netState.setErrorMessage(NetworkState.ERROR_RESPONSE_ERROR_MESSAGE);
		}
		return  netState;
	}



	public NetworkState httpDeleteUpdateString(String uri,JSONParseInterface jsonParser){
		NetworkState netState = new NetworkState();
//		if(wapIf()){
//			uri="http://"+CMWAP_GATEWAY+uri.substring(uri.indexOf("/", 8));
//		}
		int stateCode;		
		HttpDelete httpDelete = new HttpDelete(uri);		
		httpDelete.setHeader("Accept-Language", (Locale.getDefault().getLanguage()+"-"+Locale.getDefault().getCountry()).toLowerCase());
		if(JSESSIONIDStr!=null){
			httpDelete.setHeader("Cookie", "JSESSIONID=" + JSESSIONIDStr); 
		}
		httpDelete.setHeader("Content-Type", "application/json");

		try {						
			HttpResponse httpResponse = defaultHttpClient.execute(httpDelete);		
			stateCode = httpResponse.getStatusLine().getStatusCode();
			if(stateCode != 200){
				netState.setNetworkState(false);
				netState.setErrorMessage(NetworkState.ERROR_SERVER_ERROR_MESSAGE);
				//Log.d("NETT", NetworkState.ERROR_SERVER_ERROR_MESSAGE+":"+stateCode+uri);
				return netState;
			}
			getCookie(defaultHttpClient);

			HttpEntity httpEntity = httpResponse.getEntity();
			InputStream is = null;
			if(httpResponse.getEntity().getContentEncoding() == null || httpResponse.getEntity().getContentEncoding().getValue().toLowerCase().indexOf("gzip")<0){				
				is = new ByteArrayInputStream(EntityUtils.toByteArray(httpEntity));				
			}else{
				is = new GZIPInputStream(new ByteArrayInputStream(EntityUtils.toByteArray(httpEntity)));
			}

			byte[] con = readInStream(is);
			String str = new String(con);
			if(str!=null&&str.indexOf("登录过期")!=-1&&str.indexOf("-202")!=-1){	
				if(appContext instanceof BaseActivity){
					BaseActivity ba = ((BaseActivity)appContext);					
					//////////////////////////////////////
					ba.exit();
					ba.deInit();
					///////////////////////////////////////
					new Thread(){
						public void run(){
							//停掉连接obd
							Intent intent = new Intent();
							intent.setAction(TongGouService.TONGGOU_ACTION_START);
					        intent.putExtra("com.tonggou.server","STOP");
					        appContext.sendBroadcast(intent);
						}
					}.start();
					
					Intent toLogin = new Intent(appContext, LoginActivity.class);
					toLogin.putExtra("tonggou.loginExpire", "登录过期，请重新登录。");
					toLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					appContext.startActivity(toLogin);
				}
			}
			jsonParser.parsing(str);    //解析
			netState.setNetworkState(true);
			return netState;

		} catch(IOException ie){
			netState.setNetworkState(false);
			ie.printStackTrace();
			if(ie instanceof SocketTimeoutException){
				if("en".equals(Locale.getDefault().getLanguage().toLowerCase())){

					netState.setErrorMessage(NetworkState.ERROR_CLIENT_ERROR_SOCKETTIMEOUT_MESSAGE_EN);
				}else{
					netState.setErrorMessage(NetworkState.ERROR_CLIENT_ERROR_SOCKETTIMEOUT_MESSAGE);

				}
				return netState;
			}			
			if("en".equals(Locale.getDefault().getLanguage().toLowerCase())){
				netState.setErrorMessage(NetworkState.ERROR_CLIENT_ERROR_TIMEOUT_MESSAGE_EN);
			}else{
				netState.setErrorMessage(NetworkState.ERROR_CLIENT_ERROR_TIMEOUT_MESSAGE);
			}

		}catch (FactoryConfigurationError e) {
			netState.setNetworkState(false);
			netState.setErrorMessage(NetworkState.ERROR_RESPONSE_ERROR_MESSAGE);
		} catch (NullPointerException ex) {
			netState.setNetworkState(false);
			netState.setErrorMessage(NetworkState.ERROR_RESPONSE_ERROR_MESSAGE);
		}
		return  netState;
	}



	private byte[] readInStream(InputStream inStream){
		ByteArrayOutputStream outStream = null;
		try {
			outStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024*30];
			int length = -1;
			while((length = inStream.read(buffer)) != -1 ){
				outStream.write(buffer, 0, length);
			}

			return outStream.toByteArray();
		}catch (IOException e){
		}catch (OutOfMemoryError e) {
		}finally{
			try {
				outStream.close();
				inStream.close();
			} catch (IOException e) {
			}
		}
		return null;
	}




	/**
	 * 扩展卡不可用时直接从网络获取图片
	 */
	public byte[] httpGetUrlAsByte(String url){
		try {
			int stateCode;
			HttpGet httpGet = new HttpGet(url);
			httpGet.setHeader("Accept-Language", (Locale.getDefault().getLanguage()+"-"+Locale.getDefault().getCountry()).toLowerCase());
			HttpResponse httpResponse = defaultHttpClient.execute(httpGet);
			stateCode = httpResponse.getStatusLine().getStatusCode();
			HttpEntity httpEntity = httpResponse.getEntity();			
			if(httpEntity != null && stateCode == 200){
				if(httpResponse.getEntity().getContentEncoding() == null || httpResponse.getEntity().getContentEncoding().getValue().toLowerCase().indexOf("gzip")<0){				
					InputStream is = httpEntity.getContent();
					byte[] byt = readInStream(is);
					return byt;				
				}else{
					InputStream is = new GZIPInputStream(new ByteArrayInputStream(EntityUtils.toByteArray(httpEntity)));				
					byte[] byt = readInStream(is);
					return byt;
				}
			}
		} catch (IOException e) {
		} catch (NullPointerException ex) {
		}
		return null;
	}






	/**
	 * 通过网络去取图片
	 * @param strurl
	 * @param str
	 * @return
	 */
	public File readPicFromNetwork(String strurl,String str){
		//Log.d("NNN", "NNNNNNNNNNNN");
		String url = strurl;
		
		HttpURLConnection  photoConn = null;
		InputStream photoIs = null;
		FileOutputStream fileOS = null;
		try {	
			URL urlOb = new URL(url);		
			        	
				photoConn = (HttpURLConnection)urlOb.openConnection();
					
			photoConn.setDoInput(true);	
		
			if(photoConn!=null){
				int rscode = photoConn.getResponseCode();

				photoIs = photoConn.getInputStream();		//如果没有输入流会抛出io异常!!!!!!!!!!!	
				if(photoIs!=null){
					String s4 = URLEncoder.encode(str, "UTF-8");
					File kaikaiPath = new File(android.os.Environment.getExternalStorageDirectory() + "/.tonggou");
					if (!kaikaiPath.exists()) { //扩展卡有问题时，这个会抛出异常
						kaikaiPath.mkdir();
						File kaiCachePath = new File(android.os.Environment.getExternalStorageDirectory() + "/.tonggou/cache");
						if (!kaiCachePath.exists()) {
							kaiCachePath.mkdir();
						}
					}else{
						File kaiCachePath = new File(android.os.Environment.getExternalStorageDirectory() + "/.tonggou/cache");
						if (!kaiCachePath.exists()) {
							kaiCachePath.mkdir();
						}						
					}
					// 写
					File f2 = new File(android.os.Environment.getExternalStorageDirectory() + "/.tonggou/cache/" + s4 + "k");
					fileOS = new FileOutputStream(f2);
					byte[] buffer = new byte[1024];
					int length = -1;
					while((length = photoIs.read(buffer)) != -1 ){
						fileOS.write(buffer, 0, length);
					}

					fileOS.close();
					photoIs.close();

					return f2;
				}
			}

		} catch (FactoryConfigurationError e) {
		} catch (MalformedURLException e) {
		} catch (IOException e) {			
		}finally{
			try {
				if(fileOS!=null){
					fileOS.close();
				}
				if(photoIs!=null){
					photoIs.close();
				}
			} catch (IOException e){
			}
			if(photoConn!=null){
				photoConn.disconnect();
				photoConn = null;
			}
		}
		return null;
	}






	public static void setUsername(String username) {
		Network.username = username;
	}
	public static void setPassword(String password) {
		Network.password = password;
	}






	/**
	 * 获取标准 Cookie ，并存储
	 * @param httpClient
	 */
	private void getCookie(DefaultHttpClient httpClient) {
		List<Cookie> cookies = httpClient.getCookieStore().getCookies();
		//StringBuffer sb = new StringBuffer();
		for (int i = 0; i < cookies.size(); i++) {
			Cookie cookie = cookies.get(i);
			String cookieName = cookie.getName();
			String cookieValue = cookie.getValue();
			if (!TextUtils.isEmpty(cookieName)&& !TextUtils.isEmpty(cookieValue)) {
				if("JSESSIONID".equalsIgnoreCase(cookieName)){
					JSESSIONIDStr = cookieValue;
				}
			}
		}
		//Log. e( "cookie", sb.toString());

		if(JSESSIONIDStr!=null&&!"".equals(JSESSIONIDStr)){
			appContext.getSharedPreferences(BaseActivity.SETTING_INFOS, 0).edit().putString(BaseActivity.COOKIES_STR, JSESSIONIDStr).commit();
		}
	}



}
