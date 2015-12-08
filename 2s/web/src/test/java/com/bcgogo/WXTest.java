package com.bcgogo;

import com.bcgogo.api.response.HttpResponse;
import com.bcgogo.utils.HttpUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-10-22
 * Time: 下午5:34
 */
public class WXTest {
          private static final Logger LOG = LoggerFactory.getLogger(WXTest.class);

   @Test
  public void test2() throws IOException {
    String url = "http://127.0.0.1:8080/web/test2";
    HttpResponse response = HttpUtils.sendGet(url);
    LOG.info("httpResponse result:{}", response.getContent());
  }

}
