package com.bcgogo.util;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.api.response.HttpResponse;
import com.bcgogo.enums.app.MessageCode;
import com.bcgogo.utils.HttpUtils;
import com.bcgogo.utils.JsonUtil;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-9-1
 * Time: 上午11:56
 */
public class ApixTest {
  public static final Logger LOG = LoggerFactory.getLogger(ApixTest.class);

   private static String domain_apix="http://42.121.98.170:8080";

  @BeforeClass
  public static void setUp() throws Exception {
    LOG.info("setUp");
  }

   @Test
  public void apixTest() throws IOException {
    String url = domain_apix + "/apix/connect/test";
    HttpResponse response = HttpUtils.sendGet(url);
    ApiResponse apiResponse = (ApiResponse) JsonUtil.jsonToObject(response.getContent(), ApiResponse.class);
    Assert.assertEquals(MessageCode.SUCCESS.getCode(), apiResponse.getMsgCode());
  }


}
