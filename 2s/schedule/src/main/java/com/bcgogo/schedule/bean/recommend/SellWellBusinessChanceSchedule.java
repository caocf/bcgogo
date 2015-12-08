package com.bcgogo.schedule.bean.recommend;

import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.schedule.BcgogoQuartzJobBean;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.PreBuyOrderDTO;
import com.bcgogo.txn.service.IPreBuyOrderService;
import com.bcgogo.txn.service.recommend.IRecommendService;
import com.bcgogo.txn.service.solr.IOrderSolrWriterService;
import com.bcgogo.txnRead.service.IRecommendReadService;
import com.bcgogo.utils.ArrayUtil;
import com.bcgogo.utils.CollectionUtil;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SellWellBusinessChanceSchedule extends BcgogoQuartzJobBean {
  private static final Logger LOG = LoggerFactory.getLogger(SellWellBusinessChanceSchedule.class);
  private static boolean lock = false;

  protected void executeJob(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    processSellWellBusinessChanceJobs();
  }

  private static synchronized boolean isLock() {
    if (lock) {
      return true;
    }
    lock = true;
    return false;
  }


  public void processSellWellBusinessChanceJobs() {
    if (isLock()) {
      LOG.warn("SellWellBusinessChanceSchedule isLock!");
      return;
    }
    try {
      if (LOG.isDebugEnabled()) LOG.debug("SellWellBusinessChanceSchedule start.............");
      long currentTime = System.currentTimeMillis();
//      List<ShopDTO> shopDTOList = ServiceManager.getService(IConfigService.class).getActiveShop();
//      for (ShopDTO shopDTO : shopDTOList) {
//        if(ConfigUtils.isWholesalerVersion(shopDTO.getShopVersionId())) continue;
//        List<ProductDTO> productDTOList = ServiceManager.getService(IRecommendReadService.class).getLastMonthTopTenSalesByShopId(shopDTO.getId());
//        List<PreBuyOrderDTO> preBuyOrderDTOs = ServiceManager.getService(IPreBuyOrderService.class).createPreBuyOrderByProductDTO(shopDTO.getId(),productDTOList.toArray(new ProductDTO[productDTOList.size()]));
//        if(CollectionUtil.isNotEmpty(preBuyOrderDTOs)){
//          List<Long> orderIds=new ArrayList<Long>();
//          for(PreBuyOrderDTO orderDTO:preBuyOrderDTOs){
//            orderIds.add(orderDTO.getId());
//          }
//          ServiceManager.getService(IOrderSolrWriterService.class).reCreateOrderSolrIndex(shopDTO, OrderTypes.PRE_BUY_ORDER, ArrayUtil.toLongArr(orderIds));
//        }
//      }
      if (LOG.isDebugEnabled()) LOG.debug((System.currentTimeMillis()-currentTime)+",SellWellBusinessChanceSchedule end.............");
    } catch (Exception e) {
      LOG.error("执行定时钟失败，失败原因:", e);
    } finally {
      debugResourceLeak();
      lock = false;
    }
  }

}
