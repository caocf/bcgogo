package com.bcgogo.schedule.bean.recommend;

import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.txn.preBuyOrder.BusinessChanceType;
import com.bcgogo.enums.txn.preBuyOrder.PreBuyOrderValidDate;
import com.bcgogo.enums.txn.pushMessage.PushMessageScene;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.schedule.BcgogoQuartzJobBean;
import com.bcgogo.search.dto.PreBuyOrderSearchCondition;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.PreBuyOrderDTO;
import com.bcgogo.txn.dto.PreBuyOrderItemDTO;
import com.bcgogo.txn.dto.pushMessage.PushMessageBuildTaskDTO;
import com.bcgogo.txn.service.IPreBuyOrderService;
import com.bcgogo.txn.service.pushMessage.ITradePushMessageService;
import com.bcgogo.txn.service.solr.IOrderSolrWriterService;
import com.bcgogo.utils.ArrayUtil;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.DateUtil;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 14-8-13
 * Time: 下午5:13
 * To change this template use File | Settings | File Templates.
 */
public class LackAutoPreBuySchedule extends BcgogoQuartzJobBean {

  private static final Logger LOG = LoggerFactory.getLogger(LackAutoPreBuySchedule.class);
  private static boolean lock = false;


  private static synchronized boolean isLock() {
    if (lock) {
      return true;
    }
    lock = true;
    return false;
  }

  protected void executeJob(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    if (isLock()) {
      return;
    }
    lock = true;
    try{
      IPreBuyOrderService preBuyOrderService=ServiceManager.getService(IPreBuyOrderService.class);
      preBuyOrderService.processLackAutoPreBuy();
    } catch (Exception e) {
      LOG.error(e.getMessage(),e);
    } finally {
      lock = false;
    }
  }


}
