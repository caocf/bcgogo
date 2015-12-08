package com.bcgogo.config.service;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.api.response.HttpResponse;
import com.bcgogo.constant.MQConstant;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.utils.HttpUtils;
import com.bcgogo.utils.JsonUtil;
import com.bcgogo.utils.ShopConstant;
import com.bcgogo.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-8-31
 * Time: 下午5:25
 */
@Component
public class MQClientService implements IMQClientService {

  private static final Logger LOG = LoggerFactory.getLogger(MQClientService.class);

  @Override
  public boolean isOnLine(String name) throws IOException {
      String mq_ip = ServiceManager.getService(IConfigService.class).getConfig("MQ_IP_INTERNET", ShopConstant.BC_SHOP_ID);
      if (StringUtil.isEmpty(mq_ip)) {
        LOG.error("config MQ_IP_INTERNET is empty!");
        return false;
      }
      String url = (mq_ip + MQConstant.URL_MQ_HTTP_IS_ONLINE.replace("{USER_NAME}", name));
      HttpResponse response = HttpUtils.sendGet(url);
      String appVehicleDTOJson = response.getContent();
      ApiResponse apiResponse = JsonUtil.jsonToObj(appVehicleDTOJson, ApiResponse.class);
      if (apiResponse != null && "ONLINE".equals(apiResponse.getMessage())) {
        return true;
      } else {
        return false;
      }
  }


}
