package com.bcgogo.config.service.UMPush;

import com.bcgogo.config.service.UMPush.UMPushConstant.UMPushMessageConstant;
import com.bcgogo.utils.JsonUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;

/**
 * Created by XinyuQiu on 14-4-29.
 */
public class UMUnicastPushService implements UMPushService {
  private static final Logger LOG = LoggerFactory.getLogger(UMUnicastPushService.class);

  private ExecutorService executor = null;
  private UMPushDelegate umPushDelegate = null;

  public UMUnicastPushService(ExecutorService executor, UMPushDelegate umPushDelegate) {
    this.executor = executor;
    this.umPushDelegate = umPushDelegate;
  }

  public UMUnicastPushService() {

  }

  @Override
  public void send(final UnicastUMNotification notification) {
    if (executor != null) {
      executor.execute(new Runnable() {

        @Override
        public void run() {
          BufferedReader bufferedReader = null;
          InputStream inputStream = null;
          OutputStream outputStream = null;
          HttpURLConnection urlConnection = null;
          try {
            URL url = new URL(UMPushMessageConstant.SEND_URL);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(UMPushMessageConstant.POST_METHOD);
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setConnectTimeout(30000);
            urlConnection.setReadTimeout(30000);
            urlConnection.setRequestProperty("Accept-Charset", "GBK");
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=GBK");
            outputStream = urlConnection.getOutputStream();
            outputStream.write(JsonUtil.objectToJson(notification).getBytes("GBK"));
            outputStream.flush();
            inputStream = urlConnection.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            StringBuilder temp = new StringBuilder();
            String line = bufferedReader.readLine();
            while (line != null) {
              if (temp.length() > 0) {
                temp.append("\r\n");
              }
              temp.append(line);
              line = bufferedReader.readLine();
            }
            UMSendResponse umPushResponse = null;
            if (StringUtils.isNotBlank(temp.toString())) {
              umPushResponse = JsonUtil.fromJson(temp.toString(), UMSendResponse.class);
            }
            if (LOG.isInfoEnabled()) {
              LOG.info("notification send :{},response:{}", JsonUtil.objectToJson(notification), JsonUtil.objectToJson(umPushResponse));
            }
            if (umPushDelegate != null) {
              umPushDelegate.messageSent(notification, umPushResponse);
            }
          } catch (Exception e) {
            if (umPushDelegate != null) {
              umPushDelegate.messageSendFailed(notification, e);
            }
            LOG.error(e.getMessage(), e);
          } finally {
            try {
              if (inputStream != null) {
                inputStream.close();
              }
              if (bufferedReader != null) {
                bufferedReader.close();
              }
              if (outputStream != null) {
                outputStream.close();
              }
              if(urlConnection != null){
                urlConnection.disconnect();
              }

            } catch (Exception e) {
              LOG.error(e.getMessage(), e);
            }
          }

        }
      });
    }
  }

}
