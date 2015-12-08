package com.bcgogo.schedule.index.stat;

import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.ExeStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.schedule.BcgogoQuartzJobBean;
import com.bcgogo.search.dto.OrderDTO;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.txn.service.solr.IOrderSolrWriterService;
import com.bcgogo.user.dto.CusOrSupOrderIndexScheduleDTO;
import com.bcgogo.user.service.IUserService;
import org.apache.commons.collections.CollectionUtils;
import org.omg.CORBA.PRIVATE_MEMBER;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: king
 * Date: 13-8-20
 * Time: 上午11:02
 * To change this template use File | Settings | File Templates.
 */
@Component
public class UpdateCusOrSupOrderIndexSchedule extends BcgogoQuartzJobBean {
  private static final Logger LOG = LoggerFactory.getLogger(UpdateCusOrSupOrderIndexSchedule.class);

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

    try {
      //得到需要开始的任务
      IUserService userService = ServiceManager.getService(IUserService.class);
      List<CusOrSupOrderIndexScheduleDTO> cusOrSupOrderIndexScheduleDTOs = userService.getCusOrSupOrderIndexScheduleDTOs();
      if (CollectionUtils.isEmpty(cusOrSupOrderIndexScheduleDTOs)) {
        return;
      }
      for (CusOrSupOrderIndexScheduleDTO cusOrSupOrderIndexScheduleDTO : cusOrSupOrderIndexScheduleDTOs) {
        try {
          userService.updateCusOrSupOrderIndexScheduleStatusById(cusOrSupOrderIndexScheduleDTO.getId(), ExeStatus.START);
          handleSchedule(cusOrSupOrderIndexScheduleDTO);
          userService.updateCusOrSupOrderIndexScheduleStatusById(cusOrSupOrderIndexScheduleDTO.getId(), ExeStatus.FINISHED);
        } catch (Exception e) {
          LOG.error(e.getMessage(),e);
          userService.updateCusOrSupOrderIndexScheduleStatusById(cusOrSupOrderIndexScheduleDTO.getId(), ExeStatus.EXCEPTION);
        }

      }
    } finally {
      lock = false;
    }
  }

  private void handleSchedule(CusOrSupOrderIndexScheduleDTO cusOrSupOrderIndexScheduleDTO) {
    if(cusOrSupOrderIndexScheduleDTO == null) return;
    //更新客户下所有单据
    if (cusOrSupOrderIndexScheduleDTO.getCustomerId() != null) {
      reindexCustomerOrders(cusOrSupOrderIndexScheduleDTO.getShopId(), cusOrSupOrderIndexScheduleDTO.getCustomerId());
    }
    //更新供应商下所有单据
    if (cusOrSupOrderIndexScheduleDTO.getSupplierId() != null) {
      reindexSupplierOrders(cusOrSupOrderIndexScheduleDTO.getShopId(), cusOrSupOrderIndexScheduleDTO.getSupplierId());
    }
  }

  private void reindexCustomerOrders(Long shopId, Long customerId) {
    try {
      List<OrderDTO> orderDTOList = ServiceManager.getService(ISearchService.class).getConsumeOrderHistory(customerId, shopId, null, null, null, null);
      Map<OrderTypes, List<Long>> orderTypeAndIdsMap = new HashMap<OrderTypes, List<Long>>();
      if (CollectionUtils.isNotEmpty(orderDTOList)) {
        for (OrderDTO orderDTO : orderDTOList) {
          if (orderTypeAndIdsMap.keySet().contains(orderDTO.getOrderType())) {
            orderTypeAndIdsMap.get(orderDTO.getOrderType()).add(orderDTO.getOrderId());
          } else {
            List<Long> orderIds = new ArrayList<Long>();
            orderIds.add(orderDTO.getOrderId());
            orderTypeAndIdsMap.put(orderDTO.getOrderType(), orderIds);
          }
        }
        for (OrderTypes orderType : orderTypeAndIdsMap.keySet()) {
          Long[] orderIds = new Long[orderTypeAndIdsMap.get(orderType).size()];
          orderTypeAndIdsMap.get(orderType).toArray(orderIds);
          ServiceManager.getService(IOrderSolrWriterService.class).reCreateOrderSolrIndex(ServiceManager.getService(IConfigService.class).getShopById(shopId), orderType, orderIds);
        }
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }

  private void reindexSupplierOrders(Long shopId, Long supplierId) {
    try {
      List<OrderTypes> orderTypeList = new ArrayList<OrderTypes>();
      orderTypeList.add(OrderTypes.INVENTORY);
      orderTypeList.add(OrderTypes.RETURN);
      List<Object[]> objectList = ServiceManager.getService(ITxnService.class).getSupplierHistoryOrderList(supplierId, shopId, null, null, orderTypeList, null);
      List<Long> inventoryOrderIds = new ArrayList<Long>();
      List<Long> returnOrderIds = new ArrayList<Long>();
      if (CollectionUtils.isNotEmpty(objectList)) {
        for (int i = 0; i < objectList.size(); i++) {
          Long orderId = ((BigInteger) objectList.get(i)[0]).longValue();
          String orderType = (String) objectList.get(i)[1];
          if (OrderTypes.INVENTORY.toString().equals(orderType)) {
            inventoryOrderIds.add(orderId);
          } else if (OrderTypes.RETURN.toString().equals(orderType)) {
            returnOrderIds.add(orderId);
          }

        }
        if (inventoryOrderIds.size() > 0) {
          Long[] inventoryOrderIdArray = new Long[inventoryOrderIds.size()];
          inventoryOrderIds.toArray(inventoryOrderIdArray);
          ServiceManager.getService(IOrderSolrWriterService.class).reCreateOrderSolrIndex(ServiceManager.getService(IConfigService.class).getShopById(shopId), OrderTypes.INVENTORY, inventoryOrderIdArray);
        }
        if (returnOrderIds.size() > 0) {
          Long[] returnOrderIdArray = new Long[returnOrderIds.size()];
          returnOrderIds.toArray(returnOrderIdArray);
          ServiceManager.getService(IOrderSolrWriterService.class).reCreateOrderSolrIndex(ServiceManager.getService(IConfigService.class).getShopById(shopId), OrderTypes.RETURN, returnOrderIdArray);
        }

      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }
}
