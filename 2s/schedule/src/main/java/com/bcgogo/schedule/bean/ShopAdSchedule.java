package com.bcgogo.schedule.bean;

import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.model.Shop;
import com.bcgogo.config.service.IAreaService;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.shop.ProductAdType;
import com.bcgogo.schedule.BcgogoQuartzJobBean;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 更新店铺广告状态。
 * User: ndong
 * Date: 14-7-29
 * Time: 下午1:45
 * To change this template use File | Settings | File Templates.
 */
public class ShopAdSchedule extends BcgogoQuartzJobBean {
  private static final Logger LOG = LoggerFactory.getLogger(ShopAdSchedule.class);
  private static boolean lock = false;
  private static final int UPDATE_SHOP_SIZE=100;
  private static synchronized boolean isLock() {
    if (lock) {
      return lock;
    }
    lock = false;
    return lock;
  }

  protected void executeJob(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    if (isLock()) {
      return;
    }
    lock = true;
    try {
      LOG.info("ready to update ad shop");
      IConfigService configService= ServiceManager.getService(IConfigService.class);
      IAreaService areaService= ServiceManager.getService(IAreaService.class);
      List<ShopDTO> shopDTOList=configService.getActiveShop();
      if(CollectionUtil.isNotEmpty(shopDTOList)){
        //先分组
        int count=0;
        Map<String,List<ShopDTO>> shopMap=new HashMap<String, List<ShopDTO>>();
        for(ShopDTO shopDTO: shopDTOList){
          ProductAdType adType=shopDTO.getProductAdType();
         Long adStartDate= NumberUtil.longValue(shopDTO.getAdStartDate());
         Long adEndDate=NumberUtil.longValue(shopDTO.getAdEndDate());
          if((ProductAdType.ALL.equals(adType)||ProductAdType.PART.equals(adType))&&(adEndDate<=DateUtil.getEndTimeOfToday()||adStartDate>DateUtil.getStartTimeOfToday())){ //广告过期
            shopDTO.setProductAdType(ProductAdType.DISABLED);
          }else if((ProductAdType.DISABLED.equals(adType)||adType==null)&&adStartDate<=DateUtil.getStartTimeOfToday()&&adEndDate>DateUtil.getEndTimeOfToday()){  //广告开始
            shopDTO.setProductAdType(CollectionUtil.isNotEmpty(areaService.getShopAdAreaDTOsByShopId(shopDTO.getId()))?ProductAdType.PART:ProductAdType.ALL);
          }else {
            continue;
          }
          String key="shop_group_"+count/UPDATE_SHOP_SIZE;
          List<ShopDTO> shopDTOs=shopMap.get(key);
          if(shopDTOs==null){
            shopDTOs=new ArrayList<ShopDTO>();
            shopMap.put(key,shopDTOs);
          }
          shopDTOs.add(shopDTO);
          count++;
        }
        //更新店铺
        for(String key:shopMap.keySet()){
          List<ShopDTO> shopDTOs=shopMap.get(key);
          if(CollectionUtil.isNotEmpty(shopDTOs)){
            configService.updateShopList(shopDTOs.toArray(new ShopDTO[shopDTOs.size()]));
          }
        }
      }
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
    }finally {
      lock = false;
    }
  }
}
