package com.bcgogo.web.init;

import com.bcgogo.common.Result;
import com.bcgogo.config.cache.AreaCacheManager;
import com.bcgogo.config.dto.AreaDTO;
import com.bcgogo.config.model.ConfigDaoManager;
import com.bcgogo.config.model.ConfigWriter;
import com.bcgogo.config.model.Shop;
import com.bcgogo.config.service.IShopService;
import com.bcgogo.enums.app.ObdUserVehicleStatus;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.model.UserDaoManager;
import com.bcgogo.user.model.UserWriter;
import com.bcgogo.user.model.app.AppUserShopVehicle;
import com.bcgogo.user.model.app.ObdUserVehicle;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * User: ZhangJuntao
 * Date: 13-8-28
 * Time: 下午1:25
 * 店铺初始化 controller
 */
@Controller
@RequestMapping("/shopInit.do")
public class ShopInitController {
  private static final Logger LOG = LoggerFactory.getLogger(ShopInitController.class);

  @RequestMapping(params = "method=initShopGeocodeCityCode")
  @ResponseBody
  public Object initShopGeocodeCityCode() throws Exception {
    try {
      IShopService shopService = ServiceManager.getService(IShopService.class);
      List<Long> longList = shopService.getAllShopIds();
      if (CollectionUtil.isNotEmpty(longList)) {
        for (Long id : longList) {
          shopService.updateShopCoordinate(id);
        }
      }
      return new Result("初始化成功", true);
    } catch (Exception e) {
      LOG.error("method=initShopGeocodeCityCode" + e.getMessage(), e);
      return new Result("初始化失败", false);
    }
  }

  @RequestMapping(params = "method=initAddressDetail")
  @ResponseBody
  public Object initAddressDetail() throws Exception {
    try {

      ConfigDaoManager configDaoManager = ServiceManager.getService(ConfigDaoManager.class);
      ConfigWriter writer = configDaoManager.getWriter();
      List<Shop> shops = writer.getShop();
      AreaDTO areaDTO;
      String[] strings;
      Object status = writer.begin();
      try {
        for (Shop shop : shops) {
          if (StringUtil.isNotEmpty(shop.getDetailAddress())) continue;
          if (shop.getId() <= 100) {
            shop.setDetailAddress("漕湖科技园C栋12F");
            writer.update(shop);
            continue;
          }
          if (shop.getAreaId() < 1000) {
            continue;
          }
          areaDTO = AreaCacheManager.getAreaDTOByNo(shop.getProvince());
          String province = (areaDTO != null && StringUtil.isNotEmpty(areaDTO.getName())) ? areaDTO.getName() : "";
          areaDTO = AreaCacheManager.getAreaDTOByNo(shop.getCity());
          String city = (areaDTO != null && StringUtil.isNotEmpty(areaDTO.getName())) ? areaDTO.getName() : "";
          areaDTO = AreaCacheManager.getAreaDTOByNo(shop.getRegion());
          String region = (areaDTO != null && StringUtil.isNotEmpty(areaDTO.getName())) ? areaDTO.getName() : "";
          if (StringUtil.isEmpty(shop.getAddress())) continue;
          String detailAddress = shop.getAddress();
          LOG.info(detailAddress);
          if (StringUtil.isNotEmpty(province)) {
            detailAddress = detailAddress.replace(province, "");
          }
          if (StringUtil.isEmpty(detailAddress)) continue;
          if (detailAddress.startsWith("-")) {
            detailAddress = detailAddress.substring(1, detailAddress.length());
          }
          if (StringUtil.isEmpty(detailAddress)) continue;
          if (StringUtil.isNotEmpty(city)) {
            detailAddress = detailAddress.replace(city, "");
          }
          if (StringUtil.isEmpty(detailAddress)) continue;
          if (detailAddress.startsWith("-")) {
            detailAddress = detailAddress.substring(1, detailAddress.length());
          }
          if (StringUtil.isEmpty(detailAddress)) continue;
          if (StringUtil.isNotEmpty(region)) {
            detailAddress = detailAddress.replace(region, "");
          }
          if (StringUtil.isEmpty(detailAddress)) continue;
          shop.setDetailAddress(detailAddress);
          writer.update(shop);
        }
        writer.commit(status);
      } finally {
        writer.rollback(status);
      }
      return new Result("初始化成功", true);
    } catch (Exception e) {
      LOG.error("method=initAddressDetail" + e.getMessage(), e);
      return new Result("初始化失败", false);
    }
  }


  @RequestMapping(params = "method=initBinding")
  @ResponseBody
  public Object initBinding() throws Exception {
    try {
      UserDaoManager userDaoManager = ServiceManager.getService(UserDaoManager.class);
      UserWriter writer = userDaoManager.getWriter();
      Object status = writer.begin();
      try {
        Map<Long, Long> obdMap = writer.getOBDIdSellShopMap();
        List<ObdUserVehicle> list = writer.getOBDUserVehicle(obdMap.keySet());
        for (ObdUserVehicle entity : list) {
          Long sellShopId = obdMap.get(entity.getObdId());
          if (sellShopId == null) continue;
          AppUserShopVehicle ausv = new AppUserShopVehicle();
          ausv.setShopId(sellShopId);
          ausv.setAppVehicleId(entity.getAppVehicleId());
          ausv.setAppUserNo(entity.getAppUserNo());
          ausv.setObdId(entity.getObdId());
          ausv.setStatus(ObdUserVehicleStatus.BUNDLING);
          writer.save(ausv);
        }
        writer.commit(status);
      } finally {
        writer.rollback(status);
      }
      return new Result("初始化成功", true);
    } catch (Exception e) {
      LOG.error("method=initBinding" + e.getMessage(), e);
      return new Result("初始化失败", false);
    }
  }

}
