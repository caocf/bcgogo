package com.bcgogo.schedule.index.stat;

import com.bcgogo.config.dto.ShopOperationTaskDTO;
import com.bcgogo.config.service.IShopService;
import com.bcgogo.enums.ExeStatus;
import com.bcgogo.enums.shop.ShopOperateTaskScene;
import com.bcgogo.schedule.BcgogoQuartzJobBean;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.solr.IProductSolrWriterService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-2-22
 * Time: 上午11:46
 * To change this template use File | Settings | File Templates.
 */
public class ShopOperateSchedule extends BcgogoQuartzJobBean {

  private static final Logger LOG = LoggerFactory.getLogger(ShopOperateSchedule.class);
  private static boolean lock = false;

  private static synchronized boolean isLock() {
    if (lock) {
      return true;
    }
    lock = true;
    return false;
  }

  @Override
  protected void executeJob(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    if (isLock()) {
      return;
    }
    ShopOperationTaskDTO taskDTO = null;
    try {
      IShopService shopService = ServiceManager.getService(IShopService.class);
      ISearchService searchService = ServiceManager.getService(ISearchService.class);
      IProductSolrWriterService productSolrWriterService = ServiceManager.getService(IProductSolrWriterService.class);
      do{
        taskDTO = shopService.getFirstReadyShopOperationTaskDTO();
        if(taskDTO == null){
          break;
        }
        taskDTO.setExecuteTime(System.currentTimeMillis());
        if (taskDTO.getScene() != null && taskDTO.getShopId() != null) {
          taskDTO.setExeStatus(ExeStatus.START);
          shopService.updateShopOperationTaskDTO(taskDTO);
          if (ShopOperateTaskScene.DISABLE_REGISTERED_PAID_SHOP.equals(taskDTO.getScene())) {
            //删除该shop product solr
            StringBuffer query = new StringBuffer("product_name:*");
            query.append(" AND shop_id:").append(taskDTO.getShopId());
            searchService.deleteByQuery(query.toString(), "product");
          } else if (ShopOperateTaskScene.ENABLE_REGISTERED_PAID_SHOP.equals(taskDTO.getScene())) {
            productSolrWriterService.reCreateProductSolrIndex(taskDTO.getShopId(), 2000);
          }
          taskDTO.setExeStatus(ExeStatus.FINISHED);
          shopService.updateShopOperationTaskDTO(taskDTO);
        }else if(taskDTO.getId() != null){
          taskDTO.setExeStatus(ExeStatus.EXCEPTION);
          shopService.updateShopOperationTaskDTO(taskDTO);
        }
      }while (taskDTO!=null);
    } catch (Exception e) {
      LOG.error("ShopOperateSchedule:出错"+e.getMessage(), e);
      if(taskDTO != null && taskDTO.getId() != null){
        taskDTO.setExeStatus(ExeStatus.EXCEPTION);
        ServiceManager.getService(IShopService.class).updateShopOperationTaskDTO(taskDTO);
      }
    } finally {
      lock = false;
    }
  }
}
