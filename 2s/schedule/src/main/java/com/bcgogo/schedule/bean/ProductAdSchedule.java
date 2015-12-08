package com.bcgogo.schedule.bean;

import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.ProductAdStatus;
import com.bcgogo.enums.shop.ProductAdType;
import com.bcgogo.product.dto.ProductLocalInfoDTO;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.schedule.BcgogoQuartzJobBean;
import com.bcgogo.search.util.SolrUtil;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.solr.IProductSolrWriterService;
import com.bcgogo.utils.CollectionUtil;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 更新店铺商品广告状态
 * User: ndong
 * Date: 14-7-29
 * Time: 下午2:58
 * To change this template use File | Settings | File Templates.
 */
public class ProductAdSchedule extends BcgogoQuartzJobBean {
  private static final Logger LOG = LoggerFactory.getLogger(ShopAdSchedule.class);
  private static boolean lock = false;
  private static final int AD_PRODUCT_SIZE=3;
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
      LOG.info("ready to update ad shop's product");
      IConfigService configService= ServiceManager.getService(IConfigService.class);
      IProductService productService= ServiceManager.getService(IProductService.class);
      IProductSolrWriterService productSolrWriterService= ServiceManager.getService(IProductSolrWriterService.class);
      List<ShopDTO> shopDTOList=configService.getAdShops();
      if(CollectionUtil.isNotEmpty(shopDTOList)){
        LOG.info("ad shop is {}",shopDTOList.size());
        for(ShopDTO shopDTO: shopDTOList){
          List<ProductLocalInfoDTO> toUpdateDTOs=new ArrayList<ProductLocalInfoDTO>();
          Set<Long> productIdSet=new HashSet<Long>();
          List<ProductLocalInfoDTO> currentAdLocalInfoDTOs=productService.getShopAdProductLocalInfoDTO(shopDTO.getId());
          if(CollectionUtil.isNotEmpty(currentAdLocalInfoDTOs)){
            for(ProductLocalInfoDTO localInfoDTO:currentAdLocalInfoDTOs){
              localInfoDTO.setAdStatus(ProductAdStatus.DISABLED);
              toUpdateDTOs.add(localInfoDTO);
              if(!productIdSet.contains(localInfoDTO.getId())){
                productIdSet.add(localInfoDTO.getId());
              }
            }
          }
          List<ProductLocalInfoDTO> lastInSalesProducts=productService.getLastInSalesProductLocalInfo(shopDTO.getId(),AD_PRODUCT_SIZE);
          if(CollectionUtil.isNotEmpty(lastInSalesProducts)){
            for(ProductLocalInfoDTO localInfoDTO:lastInSalesProducts){
              localInfoDTO.setAdStatus(ProductAdStatus.ENABLED);
               toUpdateDTOs.add(localInfoDTO);
              if(!productIdSet.contains(localInfoDTO.getId())){
                productIdSet.add(localInfoDTO.getId());
              }
            }
          }
          productService.updateProductLocalInfo(toUpdateDTOs.toArray(new ProductLocalInfoDTO[toUpdateDTOs.size()]));
        }
      }

    }catch (Exception e){
      LOG.error(e.getMessage(),e);
    }finally {
      lock = false;
    }
  }
}
