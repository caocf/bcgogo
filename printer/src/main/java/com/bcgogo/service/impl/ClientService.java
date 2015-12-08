package com.bcgogo.service.impl;

import com.bcgogo.pojo.Constants;
import com.bcgogo.pojo.response.HttpResponse;
import com.bcgogo.service.IClientService;
import com.bcgogo.util.ConfigUtil;
import com.bcgogo.util.HttpUtils;
import com.bcgogo.util.IOUtil;
import com.bcgogo.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-3-5
 * Time: 15:44
 */
public class ClientService implements IClientService {
  private static final Logger logger = LoggerFactory.getLogger(ClientService.class);

  /**
   * 注册序列号
   * 第一次打开输入并验证序列号
   *
   * @throws java.io.IOException
   */
  @Override
  public boolean validateSerialNo() throws Exception {
    String serialNo = ConfigUtil.readSerialNo();
    if (!StringUtil.isEmpty(serialNo)) {
      return true;
    }
    serialNo = ConfigUtil.readOutPropertyFile("CAMERA.SERIAL.NO");
    if (StringUtil.isEmpty(serialNo)) {
      throw new Exception("请配置摄像头序列号");
    }
    String printer_serial_no = null;
    if ("TEST".equals(ConfigUtil.read("ENV.MODE"))) {
      printer_serial_no = Constants.TEST_PRINTER_SERIAL_NO;
    } else {
      String url = Constants.URL_GET_PRINTER_SERIAL_NO + serialNo;
      HttpResponse response = HttpUtils.sendPost(url);
      printer_serial_no = response.getContent();
    }
    if (StringUtil.isEmpty(printer_serial_no)) {
      throw new Exception("未查询到客户端序列号，请联系一发客服0512-66733331");
    }
    ConfigUtil.saveSerialNo(printer_serial_no);
    logger.info("序列号验证成功。");
    return true;
  }


  /**
   * 下载网络文件
   *
   * @param url
   * @throws java.io.IOException
   */
  @Override
  public void download(String url, String path) throws IOException {
    if (StringUtil.isEmpty(url)) return;
    InputStream in = new URL(url).openStream();
    try {
      OutputStream out = new FileOutputStream(path);
      IOUtil.copy(in, out);
    } finally {
      IOUtil.closeQuietly(in);
    }
  }

  public static void main(String[] args) throws IOException {
    ClientService handler = new ClientService();
    String url = "http://www.bcgogo.com/file/printer-2.0.jar";
    String path = ConfigUtil.read("SETUP_PATH") + "printer-2.0.jar";
    handler.download(url, path);
  }

}


