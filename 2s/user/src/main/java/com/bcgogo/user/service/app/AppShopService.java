package com.bcgogo.user.service.app;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.api.AppShopDTO;
import com.bcgogo.api.response.ApiShopResponse;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.app.MessageCode;
import com.bcgogo.enums.app.ValidateMsg;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.model.UserDaoManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * User: ZhangJuntao
 * Date: 13-9-3
 * Time: 上午9:34
 */
@Component
public class AppShopService implements IAppShopService {
  private static final Logger LOG = LoggerFactory.getLogger(AppShopService.class);
  @Autowired
  private UserDaoManager userDaoManager;

  public ApiResponse getShopDetail(Long shopId) {
    if (shopId == null) {
      return MessageCode.toApiResponse(MessageCode.OBTAIN_SHOP_DETAIL_FAIL, ValidateMsg.SHOP_ID_IS_NULL);
    } else {
      ApiShopResponse response = new ApiShopResponse(MessageCode.toApiResponse(MessageCode.OBTAIN_SHOP_DETAIL_SUCCESS));
      IConfigService configService = ServiceManager.getService(IConfigService.class);
      ShopDTO shopDTO = configService.getShopById(shopId);
      AppShopDTO appShopDTO = new AppShopDTO(shopDTO);
      response.setShop(appShopDTO);
      return response;
    }
  }


}
