package com.bcgogo;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.api.response.HttpResponse;
import com.bcgogo.enums.app.MessageCode;
import com.bcgogo.utils.HttpUtils;
import com.bcgogo.utils.JsonUtil;
import com.bcgogo.utils.StringUtil;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-11-13
 * Time: 下午1:57
 */
public class AppClientMocker {
  public static final Logger LOG = LoggerFactory.getLogger(AppClientMocker.class);

  private static String domain;

  static String mode = "www";

  static {
    if (StringUtil.isEmpty(mode)) {
      domain = "http://192.168.1.252:8080";
    } else {
      domain = "http://127.0.0.1:8080";
    }
  }

  private static String url_gsm_login = domain + "/api/gsm/login";

  @BeforeClass
  public static void setUp() throws Exception {
    Map<String, Object> params = new HashMap<String, Object>();
    params.put("appVersion", "1.1");
    params.put("userNo", "15151499443");
    params.put("password", "111111");
    params.put("imageVersion", "320X480");
    HttpResponse response = HttpUtils.sendPost(url_gsm_login, params);
    ApiResponse apiResponse = (ApiResponse) JsonUtil.jsonToObject(response.getContent(), ApiResponse.class);
    LOG.info("gsmLoginTest result:{}", response.getContent());
    Assert.assertEquals(MessageCode.LOGIN_SUCCESS.getCode(), apiResponse.getMsgCode());
  }

  @AfterClass
  public static void after() throws Exception {
    LOG.info("logout");
  }

  /**
   * 获取后视镜二维码
   *
   * @throws java.io.IOException
   */
  @Test
  public void gsmLoginTest() throws IOException {

  }

}
