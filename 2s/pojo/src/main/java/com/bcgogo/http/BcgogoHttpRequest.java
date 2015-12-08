package com.bcgogo.http;

import com.bcgogo.utils.StringUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Vector;

/**
 * httpRequest封装
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-7-24
 * Time: 下午11:27
 * To change this template use File | Settings | File Templates.
 */
public class BcgogoHttpRequest {
  private String defaultContentEncoding;
  private String cookies;

  public BcgogoHttpRequest() {
//    this.defaultContentEncoding = Charset.defaultCharset().name();
    this.defaultContentEncoding ="UTF-8";
  }

  public String getCookies() {
    return cookies;
  }

  public void setCookies(String cookies) {
    this.cookies = cookies;
  }

  /**
   * 发送GET请求
   *
   * @param urlString URL地址
   * @return 响应对象
   * @throws java.io.IOException
   */
  public BcgogoHttpResponse sendGet(String urlString) throws IOException {
    return this.send(urlString, "GET", null, null);
  }

  /**
   * 发送GET请求
   *
   * @param urlString URL地址
   * @param params    参数集合
   * @return 响应对象
   * @throws java.io.IOException
   */
  public BcgogoHttpResponse sendGet(String urlString, Map<String, String> params)
      throws IOException {
    return this.send(urlString, "GET", params, null);
  }

  /**
   * 发送GET请求
   *
   * @param urlString URL地址
   * @param params    参数集合
   * @param propertys 请求属性
   * @return 响应对象
   * @throws java.io.IOException
   */
  public BcgogoHttpResponse sendGet(String urlString, Map<String, String> params,
                             Map<String, String> propertys) throws IOException {
    return this.send(urlString, "GET", params, propertys);
  }

  /**
   * 发送POST请求
   *
   * @param urlString URL地址
   * @return 响应对象
   * @throws java.io.IOException
   */
  public BcgogoHttpResponse sendPost(String urlString) throws IOException {
    return this.send(urlString, "POST", null, null);
  }

  /**
   * 发送POST请求
   *
   * @param urlString URL地址
   * @param params    参数集合
   * @return 响应对象
   * @throws java.io.IOException
   */
  public BcgogoHttpResponse sendPost(String urlString, Map<String, String> params)
      throws IOException {
    return this.send(urlString, "POST", params, null);
  }

  /**
   * 发送POST请求
   *
   * @param urlString URL地址
   * @param params    参数集合
   * @param propertys 请求属性
   * @return 响应对象
   * @throws java.io.IOException
   */
  public BcgogoHttpResponse sendPost(String urlString, Map<String, String> params,
                              Map<String, String> propertys) throws IOException {
    return this.send(urlString, "POST", params, propertys);
  }

  /**
   * 发送HTTP请求
   *
   * @param urlString
   * @return 响映对象
   * @throws java.io.IOException
   */
  private BcgogoHttpResponse send(String urlString, String method,
                           Map<String, String> parameters, Map<String, String> propertys)
      throws IOException {
    HttpURLConnection urlConnection = null;

    if (method.equalsIgnoreCase("GET") && parameters != null) {
      StringBuffer param = new StringBuffer();
      int i = 0;
      for (String key : parameters.keySet()) {
        if (i == 0)
          param.append("?");
        else
          param.append("&");
        param.append(key).append("=").append(parameters.get(key));
        i++;
      }
      urlString += param;
    }
    URL url = new URL(urlString);
    urlConnection = (HttpURLConnection) url.openConnection();
    if (StringUtil.isNotEmpty(cookies)) {
      urlConnection.setRequestProperty("Cookie", cookies);
    }
    urlConnection.setRequestMethod(method);
    urlConnection.setDoOutput(true);
    urlConnection.setDoInput(true);
    urlConnection.setUseCaches(false);
    urlConnection.setConnectTimeout(40000);
    urlConnection.setReadTimeout(30000);

    if (propertys != null) {
      for (String key : propertys.keySet()) {
        urlConnection.addRequestProperty(key, propertys.get(key));
      }
    }

    if (method.equalsIgnoreCase("POST") && parameters != null) {
      StringBuffer param = new StringBuffer();
      for (String key : parameters.keySet()) {
        param.append("&");
        param.append(key).append("=").append(parameters.get(key));
      }
      urlConnection.getOutputStream().write(param.toString().getBytes());
      urlConnection.getOutputStream().flush();
      urlConnection.getOutputStream().close();
    }

    return this.makeContent(urlString, urlConnection);
  }

  /**
   * 得到响应对象
   *
   * @param urlConnection
   * @return 响应对象
   * @throws java.io.IOException
   */
  private BcgogoHttpResponse makeContent(String urlString,
                                  HttpURLConnection urlConnection) throws IOException {
    BcgogoHttpResponse bcgogoHttpResponse = new BcgogoHttpResponse();
    try {
      String ecod = urlConnection.getContentEncoding();
      if (ecod == null)
        ecod = this.defaultContentEncoding;
      bcgogoHttpResponse.setContentCollection(new Vector<String>());
      StringBuffer temp = new StringBuffer();
      BufferedReader bufferedReader=null;
      try{
        InputStream in = urlConnection.getInputStream();
        bufferedReader = new BufferedReader(new InputStreamReader(in,ecod));
        String line = bufferedReader.readLine();
        while (line != null) {
          bcgogoHttpResponse.getContentCollection().add(line);
          temp.append(line).append("\r\n");
          line = bufferedReader.readLine();
        }
      }finally {
        if(bufferedReader!=null)
          bufferedReader.close();
      }

      bcgogoHttpResponse.setUrlString(urlString);

      bcgogoHttpResponse.setDefaultPort(urlConnection.getURL().getDefaultPort());
      bcgogoHttpResponse.setFile(urlConnection.getURL().getFile());
      bcgogoHttpResponse.setHost(urlConnection.getURL().getHost());
      bcgogoHttpResponse.setPath(urlConnection.getURL().getPath());
      bcgogoHttpResponse.setPort(urlConnection.getURL().getPort());
      bcgogoHttpResponse.setProtocol(urlConnection.getURL().getProtocol());
      bcgogoHttpResponse.setQuery(urlConnection.getURL().getQuery());
      bcgogoHttpResponse.setRef(urlConnection.getURL().getRef());
      bcgogoHttpResponse.setUserInfo(urlConnection.getURL().getUserInfo());
      bcgogoHttpResponse.setContent(new String(temp.toString().getBytes()));
      bcgogoHttpResponse.setContentEncoding(ecod);
      bcgogoHttpResponse.setCode(urlConnection.getResponseCode());
      bcgogoHttpResponse.setMessage(urlConnection.getResponseMessage());
      bcgogoHttpResponse.setContentType(urlConnection.getContentType());
      bcgogoHttpResponse.setMethod(urlConnection.getRequestMethod());
      bcgogoHttpResponse.setConnectTimeout(urlConnection.getConnectTimeout());
      bcgogoHttpResponse.setReadTimeout(urlConnection.getReadTimeout());

      return bcgogoHttpResponse;
    } catch (IOException e) {
      throw e;
    } finally {
      if (urlConnection != null)
        urlConnection.disconnect();
    }
  }

  /**
   * 默认的响应字符集
   */
  public String getDefaultContentEncoding() {
    return this.defaultContentEncoding;
  }

  /**
   * 设置默认的响应字符集
   */
  public void setDefaultContentEncoding(String defaultContentEncoding) {
    this.defaultContentEncoding = defaultContentEncoding;
  }
}
