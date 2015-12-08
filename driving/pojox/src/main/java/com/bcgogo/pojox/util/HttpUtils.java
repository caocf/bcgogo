package com.bcgogo.pojox.util;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 14-8-12
 * Time: 下午4:58
 * To change this template use File | Settings | File Templates.
 */

import com.bcgogo.pojox.api.response.HttpResponse;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class HttpUtils {

  public static final Logger LOG = LoggerFactory.getLogger(HttpUtils.class);
  private static String charsetName = "UTF-8";




  /**
   * 发送GET请求
   *
   * @param urlString URL地址
   * @return 响应对象
   * @throws java.io.IOException
   */
  public static HttpResponse sendGet(String urlString) throws IOException {
    return send(urlString, "GET", null, null);
  }

  /**
   * 发送GET请求
   *
   * @param urlString URL地址
   * @param params    参数集合
   * @return 响应对象
   * @throws java.io.IOException
   */
  public static HttpResponse sendGet(String urlString, Map<String, Object> params)
    throws IOException {
    return send(urlString, "GET", params, null);
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
  public static HttpResponse sendGet(String urlString, Map<String, Object> params,
                                     Map<String, String> propertys) throws IOException {
    return send(urlString, "GET", params, propertys);
  }


  /**
   * 发送POST请求
   *
   * @param urlString URL地址
   * @return 响应对象
   * @throws java.io.IOException
   */
  public static HttpResponse sendPost(String urlString) throws IOException {
    return send(urlString, "POST", null, null);
  }

  /**
   * 发送POST请求
   *
   * @param urlString URL地址
   * @param obj    参数集合
   * @return 响应对象
   * @throws java.io.IOException
   */
//  public static HttpResponse sendPost(String urlString,Object obj)
//    throws IOException, IllegalAccessException {
//    Map<String, Object> params=ReflectUtil.getFiledValMap(obj);
//    return send(urlString, "POST", params, null);
//  }

  /**
   * 发送POST请求
   *
   * @param urlString
   * @param obj
   * @return
   * @throws java.io.IOException
   */
  public static HttpResponse sendPost(String urlString, Object obj)
    throws IOException {
    return send(urlString, "POST", objectToMap(obj), null);
  }


  private static Map<String, Object> objectToMap(Object obj) {
    String json = JsonUtil.objectToJson(obj);
    Map<String, String> dMap = JsonUtil.jsonToStringMap(json);
    if (MapUtils.isNotEmpty(dMap)) {
      Map<String, Object> paramMap = new HashMap<String, Object>();
      for (String key : dMap.keySet()) {
        paramMap.put(key, dMap.get(key));
      }
      return paramMap;
    }
    return null;
  }


  public static HttpResponse sendPost(String urlString, Map<String, Object> params)
    throws IOException {
    return send(urlString, "POST", params, null);
  }

  /**
   * 发送PUT请求
   *
   * @param urlString URL地址
   * @return 响应对象
   * @throws java.io.IOException
   */
  public static HttpResponse sendPUT(String urlString) throws IOException {
    return sendPUT(urlString, null, null);
  }

  public static HttpResponse sendPUT(String urlString, Object obj)
    throws IOException, IllegalAccessException {
    return send(urlString, "PUT", objectToMap(obj), null);
  }

  public static HttpResponse sendPUT(String urlString, Map<String, Object> parameters) throws IOException {
    return sendPUT(urlString, parameters, null);
  }

  public static HttpResponse sendPUT(String urlString, Map<String, Object> parameters, Map<String, String> propertys) throws IOException {
    if (MapUtils.isEmpty(propertys)) {
      propertys = new HashMap<String, String>();
    }
    propertys.put("Content-Type", "application/json");
    return send(urlString, "PUT", parameters, propertys);
  }

  /**
   * 发送POST请求
   *
   * @param toUrl     URL地址
   * @param params    参数集合
   * @param propertys 请求属性
   * @return 响应对象
   * @throws java.io.IOException
   */
  public static HttpResponse sendPost(String toUrl, Map<String, Object> params,
                                      Map<String, String> propertys) throws IOException {
    return send(toUrl, "POST", params, propertys);
  }

  public static HttpResponse sendPost(String urlString, String dataStr) throws IOException {
    URL url = new URL(urlString);
    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
    LOG.info("http post request,,url is {},data is {},", urlString, dataStr);
    urlConnection.setRequestMethod("POST");
    urlConnection.setDoOutput(true);
    urlConnection.setDoInput(true);
    urlConnection.setUseCaches(false);
    if (StringUtil.isNotEmpty(dataStr)) {
      try {
        urlConnection.getOutputStream().write(dataStr.getBytes(getCharsetName()));
        urlConnection.getOutputStream().flush();
      } finally {
        urlConnection.getOutputStream().close();
      }
    }
    HttpResponse response = getContent(urlString, urlConnection);
    return response;
  }

  /**
   * @param toUrl
   * @param dataStr 不为空
   * @return
   * @throws java.io.IOException
   */
  public static String postData(String toUrl, String dataStr) throws IOException {
    if (StringUtil.isEmpty(dataStr)) return null;
    URL url = new URL(toUrl);
    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
    LOG.info("post request,,toUrl is {},data is {},", toUrl, dataStr);
    urlConnection.setRequestMethod("POST");
    urlConnection.setDoOutput(true);
    urlConnection.setDoInput(true);
    urlConnection.setUseCaches(false);
    try {
      urlConnection.getOutputStream().write(dataStr.getBytes(getCharsetName()));
      urlConnection.getOutputStream().flush();
    } finally {
      urlConnection.getOutputStream().close();
    }
    InputStream in = urlConnection.getInputStream();
    try {
      StringWriter writer = new StringWriter();
      IOUtils.copy(in, writer, "UTF-8");
      String content = writer.toString();
      LOG.info("httpUtils postData,resp data is {}", content);
      return content;
    } catch (IOException e) {
      throw e;
    } finally {
      if (urlConnection != null) {
        urlConnection.disconnect();
      }
      if (in != null) {
        in.close();
      }
    }
  }

//  /**
//   * 根据地址获得数据的字节流
//   * @param dataUrl 网络连接地址
//   * @return
//   */
//  public static byte[] getDataByUrl(String dataUrl) throws Exception {
//    HttpURLConnection conn=null;
//    InputStream inStream=null;
//    try {
//      URL url = new URL(dataUrl);
//      conn = (HttpURLConnection)url.openConnection();
//      conn.setRequestMethod("GET");
//      conn.setConnectTimeout(5 * 1000);
//      inStream = conn.getInputStream();//通过输入流获取图片数据
//      StringWriter writer = new StringWriter();
//            IOUtil.copy(inStream, writer, "UTF-8");
//            String content = writer.toString();
//      byte[] btImg =IOUtil.readFromStream(inStream);//得到图片的二进制数据
//      return btImg;
//    } catch (Exception e) {
//      throw e;
//    } finally {
////            if(conn!=null) conn.
//    }
//  }


  public static HttpResponse uploadFile(String urlString, File file) throws IOException {
    LOG.info("uploadFile...");
    URL urlObj = new URL(urlString);
    HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();
    con.setRequestMethod("POST"); // 以Post方式提交表单，默认get方式
    con.setDoInput(true);
    con.setDoOutput(true);
    con.setUseCaches(false); // post方式不能使用缓存
    // 设置请求头信息
    con.setRequestProperty("Connection", "Keep-Alive");
    con.setRequestProperty("Charset", "UTF-8");
    // 设置边界
    String BOUNDARY = "----------" + System.currentTimeMillis();
    con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
    // 请求正文信息
    StringBuilder sb = new StringBuilder();
    sb.append("--"); // 必须多两道线
    sb.append(BOUNDARY);
    sb.append("\r\n");
    sb.append("Content-Disposition: form-data;name=\"file\";filename=\"" + file.getName() + "\"\r\n");
    sb.append("Content-Type:application/octet-stream\r\n\r\n");
    byte[] head = sb.toString().getBytes("utf-8");
    OutputStream out = null;
    try {
      out = new DataOutputStream(con.getOutputStream());
      // 输出表头
      out.write(head);
      // 把文件已流文件的方式 推入到url中
      DataInputStream in = new DataInputStream(new FileInputStream(file));
      int bytes = 0;
      byte[] bufferOut = new byte[1024];
      while ((bytes = in.read(bufferOut)) != -1) {
        out.write(bufferOut, 0, bytes);
      }
      in.close();
      // 结尾部分
      byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("utf-8");// 定义最后数据分隔线
      out.write(foot);
    } finally {
      out.flush();
      out.close();
    }
    return getContent(urlString, con);
  }



  /**
   * 发送HTTP请求
   *
   * @param urlString
   * @return 响映对象
   * @throws java.io.IOException
   */
  private static HttpResponse send(String urlString, String method,
                                   Map<String, Object> parameters, Map<String, String> propertys) throws IOException {
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
    LOG.info("http request,method is " + method + ",url={},params={}", urlString, parameters);
    URL url = new URL(urlString);
    urlConnection = (HttpURLConnection) url.openConnection();

    urlConnection.setRequestMethod(method);
    urlConnection.setDoOutput(true);
    urlConnection.setDoInput(true);
    urlConnection.setUseCaches(false);
    urlConnection.setRequestProperty("Charset", getCharsetName());
    //设置propertys
    if (propertys != null) {
      for (String key : propertys.keySet()) {
        urlConnection.addRequestProperty(key, propertys.get(key));
      }
    }
    //设置请求 params
    if (parameters != null) {
      StringBuffer param = new StringBuffer();
      if (method.equalsIgnoreCase("POST")) {
        for (String key : parameters.keySet()) {
          param.append("&");
          param.append(key).append("=").append(parameters.get(key));
        }
      } else if (method.equalsIgnoreCase("PUT")) {
        param.append(JsonUtil.mapToJson(parameters));
//        param.append("{");
//        int count = 0;
//        for (String key : parameters.keySet()) {
//          param.append("\"").append(key).append("\"");
//          param.append(":").append("\"").append(parameters.get(key)).append("\"");
//          count++;
//          if (count < parameters.size()) {
//            param.append(",");
//          }
//        }
//        param.append("}");
      }
      urlConnection.getOutputStream().write(param.toString().getBytes("UTF-8"));
      urlConnection.getOutputStream().flush();
      urlConnection.getOutputStream().close();
    }
    return getContent(urlString, urlConnection);
  }

  /**
   * 得到响应对象
   *
   * @param urlConnection
   * @return 响应对象
   * @throws java.io.IOException
   */
  private static HttpResponse getContent(String urlString, HttpURLConnection urlConnection) throws IOException {
    HttpResponse httpResponse = new HttpResponse();
    try {
      InputStream in = urlConnection.getInputStream();
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, getCharsetName()));
      Vector<String> contentVector = new Vector<String>();
      httpResponse.setContentCollection(contentVector);
      StringBuffer temp = new StringBuffer();
      String line = bufferedReader.readLine();
      while (line != null) {
        contentVector.add(line);
        temp.append(line);
        line = bufferedReader.readLine();
      }
      bufferedReader.close();
      httpResponse.setUrlString(urlString);
      int port = urlConnection.getURL().getDefaultPort();
      httpResponse.setDefaultPort(port);
      httpResponse.setFile(urlConnection.getURL().getFile());
      httpResponse.setHost(urlConnection.getURL().getHost());
      httpResponse.setPath(urlConnection.getURL().getPath());
      httpResponse.setPort(urlConnection.getURL().getPort());
      httpResponse.setProtocol(urlConnection.getURL().getProtocol());
      httpResponse.setQuery(urlConnection.getURL().getQuery());
      httpResponse.setRef(urlConnection.getURL().getRef());
      httpResponse.setUserInfo(urlConnection.getURL().getUserInfo());
      httpResponse.setContent(temp.toString());
      httpResponse.setContentEncoding(getCharsetName());
      httpResponse.setCode(urlConnection.getResponseCode());
      httpResponse.setCookie(urlConnection.getHeaderField("set-cookie"));
      httpResponse.setMessage(urlConnection.getResponseMessage());
      httpResponse.setContentType(urlConnection.getContentType());
      httpResponse.setMethod(urlConnection.getRequestMethod());
      httpResponse.setConnectTimeout(urlConnection.getConnectTimeout());
      httpResponse.setReadTimeout(urlConnection.getReadTimeout());
      LOG.info("HttpUtils response:{}", httpResponse.getContent());
      return httpResponse;
    } catch (IOException e) {
      throw e;
    } finally {
      if (urlConnection != null) {
        urlConnection.disconnect();
      }
    }
  }

  public static String getCharsetName() {
    return charsetName;
  }

  public static void setCharsetName(String charsetName) {
    HttpUtils.charsetName = charsetName;
  }


}
