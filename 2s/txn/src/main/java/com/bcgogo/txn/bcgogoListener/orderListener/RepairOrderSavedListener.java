package com.bcgogo.txn.bcgogoListener.orderListener;

import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.RemindEventType;
import com.bcgogo.notification.service.ISmsService;
import com.bcgogo.product.service.IProductSolrService;
import com.bcgogo.search.dto.OrderIndexDTO;
import com.bcgogo.search.model.ItemIndex;
import com.bcgogo.search.service.IItemIndexService;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.bcgogoListener.orderEvent.RepairOrderSavedEvent;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.service.IPreBuyOrderService;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.txn.service.IVehicleStatService;
import com.bcgogo.txn.service.RepairOrderTemplateService;
import com.bcgogo.txn.service.sms.ISendSmsService;
import com.bcgogo.txn.service.solr.ICustomerOrSupplierSolrWriteService;
import com.bcgogo.txn.service.solr.IOrderSolrWriterService;
import com.bcgogo.txn.service.solr.IProductSolrWriterService;
import com.bcgogo.user.dto.ContactDTO;
import com.bcgogo.user.service.solr.IVehicleSolrWriterService;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.MemberDTO;
import com.bcgogo.user.dto.VehicleDTO;
import com.bcgogo.user.service.IMembersService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.app.IAppUserService;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-4-11
 * Time: 下午5:09
 */
public class RepairOrderSavedListener extends OrderSavedListener {
  private RepairOrderSavedEvent repairOrderSavedEvent;

  public RepairOrderSavedEvent getRepairOrderSavedEvent() {
    return repairOrderSavedEvent;
  }

  public void setRepairOrderSavedEvent(RepairOrderSavedEvent repairOrderSavedEvent) {
    this.repairOrderSavedEvent = repairOrderSavedEvent;
  }

  public RepairOrderSavedListener(RepairOrderSavedEvent repairOrderSavedEvent) {
    super();
    this.repairOrderSavedEvent = repairOrderSavedEvent;
  }

  public void run() {
    long begin = System.currentTimeMillis();
    RepairOrderDTO repairOrderDTO = this.repairOrderSavedEvent.getRepairOrderDTO();
    ReceivableDTO receivableDTO = null;
    ITxnService iTxnService = ServiceManager.getService(ITxnService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    ISmsService smsService = ServiceManager.getService(ISmsService.class);
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    IItemIndexService itemIndexService = ServiceManager.getService(IItemIndexService.class);
    IVehicleStatService vehicleStatService = ServiceManager.getService(IVehicleStatService.class);

    try {
      if (repairOrderDTO != null) {
        //欠款结算的时候更新memcache 统计数字
        if (OrderStatus.REPAIR_SETTLED.equals(repairOrderDTO.getStatus()) && repairOrderDTO.getDebt() > 0) {
          iTxnService.updateRemindCountInMemcacheByTypeAndShopId(RemindEventType.DEBT, repairOrderDTO.getShopId());
        }
        //如果是 派单 改单   自动发缺料 商机（求购）
        if (OrderStatus.REPAIR_DISPATCH.equals(repairOrderDTO.getStatus())) {
          List<PreBuyOrderDTO> preBuyOrderDTOs = ServiceManager.getService(IPreBuyOrderService.class).createPreBuyOrderByLackRepairOrderDTO(repairOrderDTO);
          if(CollectionUtil.isNotEmpty(preBuyOrderDTOs)){
            List<Long> orderIds=new ArrayList<Long>();
            for(PreBuyOrderDTO orderDTO:preBuyOrderDTOs){
              orderIds.add(orderDTO.getId());
            }
            ServiceManager.getService(IOrderSolrWriterService.class).reCreateOrderSolrIndex(ServiceManager.getService(IConfigService.class).getShopById(repairOrderDTO.getShopId()), OrderTypes.PRE_BUY_ORDER, ArrayUtil.toLongArr(orderIds));
          }
        }
        //提醒数量，更新到缓存
        ServiceManager.getService(ITxnService.class).updateRemindCountInMemcacheByTypeAndShopId(RemindEventType.CUSTOMER_SERVICE, repairOrderDTO.getShopId());
        ServiceManager.getService(ITxnService.class).updateRemindCountInMemcacheByTypeAndShopId(RemindEventType.REPAIR, repairOrderDTO.getShopId());

        repairOrderDTO.setCurrentUsedProductDTOList();
        repairOrderDTO.setCurrentUsedVehicleDTOList();
        if (repairOrderDTO.getStatus() == null) {
          LOG.error("/RepairOrderSavedListener, method=run, repairOrderDTO[{}] 状态为空 ", repairOrderDTO.toString());
        } else {

          receivableDTO = iTxnService.getReceivableDTOByShopIdAndOrderId(repairOrderDTO.getShopId(), repairOrderDTO.getId());
          MemberDTO memberDTO = ServiceManager.getService(IMembersService.class).getMemberByCustomerId(repairOrderDTO.getShopId(), repairOrderDTO.getCustomerId());
          if (memberDTO != null) {
            repairOrderDTO.setMemberNo(memberDTO.getMemberNo());
            repairOrderDTO.setMemberType(memberDTO.getType());
          }
          OrderIndexDTO orderIndexDTO = repairOrderDTO.toOrderIndexDTO();
          //服务人员
          String serviceWorker = "";
          if (!StringUtils.isBlank(repairOrderDTO.getServiceWorker())) {
            serviceWorker = repairOrderDTO.getServiceWorker();
          }
          if (!StringUtils.isBlank(repairOrderDTO.getProductSaler())) {
            if (StringUtils.isBlank(serviceWorker)) {
              serviceWorker = serviceWorker + repairOrderDTO.getProductSaler();
            } else {
              serviceWorker = serviceWorker + "," + repairOrderDTO.getProductSaler();
            }
          }
          if (StringUtils.isBlank(serviceWorker)) {
            serviceWorker = RfTxnConstant.ASSISTANT_NAME;
          }
          orderIndexDTO.setServiceWorker(serviceWorker);
          if (repairOrderDTO.getStatus() == OrderStatus.REPAIR_DISPATCH) {
            if (repairOrderDTO.getOrderStatus() == 1L) {
              searchService.saveOrUpdateOrderIndex(orderIndexDTO);
            } else if (repairOrderDTO.getOrderStatus() == 2L) {
              searchService.updateOrderIndex(orderIndexDTO);
            }
          } else if (repairOrderDTO.getStatus() == OrderStatus.REPAIR_DONE) {
            searchService.updateOrderIndex(orderIndexDTO);
          } else if (repairOrderDTO.getStatus() == OrderStatus.REPAIR_SETTLED) {
            searchService.updateOrderIndex(orderIndexDTO);
            if(StringUtils.isNotEmpty(repairOrderDTO.getAppUserNo())){
              ServiceManager.getService(IAppUserService.class)
                  .updateAppUserLastExpenseShopId(repairOrderDTO.getShopId(), repairOrderDTO.getAppUserNo());
            }
            //营业统计                                                                                                                                               .
            if (DateUtil.isCurrentTime(repairOrderDTO.getVestDate())) {
              businessStatByOrder(repairOrderDTO, false, true, NumberUtil.longValue(repairOrderDTO.getVestDate()));
            } else {
              businessStatByOrder(repairOrderDTO, false, false, NumberUtil.longValue(repairOrderDTO.getVestDate()));
              orderRunBusinessStatChange(repairOrderDTO);
            }

            vehicleServeStat(repairOrderDTO, false);

            try {
              vehicleStatService.customerVehicleConsumeStat(repairOrderDTO, OrderTypes.REPAIR,false,receivableDTO);
            } catch (Exception e) {
              LOG.error(e.getMessage(), e);
            }

          } else if (repairOrderDTO.getStatus() == OrderStatus.REPAIR_REPEAL) {
            itemIndexService.updateItemIndexPurchaseOrderStatus(repairOrderDTO.getShopId(), OrderTypes.REPAIR,repairOrderDTO.getId(), OrderStatus.REPAIR_REPEAL);
            searchService.updateOrderIndex(repairOrderDTO.getShopId(), repairOrderDTO.getId(), OrderTypes.REPAIR, OrderStatus.REPAIR_REPEAL);
            //作废单 把今天的营业额去掉
            //判断该单据是否已经结算
            if (NumberUtil.longValue(repairOrderDTO.getId()) > 0) {
              RepairOrderDTO newRepairOrderDTO = iTxnService.getRepairOrder(repairOrderDTO.getId());
              if (receivableDTO != null) {
                businessStatByOrder(repairOrderDTO, true, true, NumberUtil.longValue(newRepairOrderDTO.getVestDate()));

                try {
                  vehicleStatService.customerVehicleConsumeStat(repairOrderDTO, OrderTypes.REPAIR, true, receivableDTO);
                } catch (Exception e) {
                  LOG.error(e.getMessage(), e);
                }
              }
            }
            vehicleServeStat(repairOrderDTO, true);
          } else {
            LOG.error("/RepairOrderSavedListener");
            LOG.error("method=run");
            LOG.error(" repairOrderDTO 状态不正确 " + repairOrderDTO.toString());
          }
        }
        if (!OrderStatus.REPAIR_REPEAL.equals(repairOrderDTO.getStatus())) {

          //保存itemIndex
          saveItemIndex(repairOrderDTO);
          //更新维修单中使用过的单位顺序
          configService.updateOrderUnitSort(repairOrderDTO.getShopId(), repairOrderDTO);
          // 施工单模板使用计次
          Long repairOrderTemplateId = repairOrderDTO.getRepairOrderTemplateId();
          if (repairOrderTemplateId != null) {
            ServiceManager.getService(RepairOrderTemplateService.class).updateRepairOrderTemplateUsageCounter(repairOrderTemplateId);
          }
          //常用商品 add by zhangjuntao
          this.currentUsedSaved(repairOrderDTO);
        }

        //施工单完工短信，结算短信
        if (OrderStatus.REPAIR_DONE.equals(repairOrderDTO.getStatus())) {
          ShopDTO shopDTO = ServiceManager.getService(IConfigService.class).getShopById(repairOrderDTO.getShopId());
          //完工短信：给顾客发送完工短信
          smsService.sendFinishMsgToCustomer(repairOrderDTO, repairOrderDTO.getShopId(), shopDTO);
        } else if (OrderStatus.REPAIR_SETTLED.equals(repairOrderDTO.getStatus())) {
          ShopDTO shopDTO = ServiceManager.getService(IConfigService.class).getShopById(repairOrderDTO.getShopId());
          //更新会员信息, 并发送提醒短信给持卡人（如果勾上）
          VelocityContext context = repairOrderDTO.getMemberSmsVelocityContext();
          CustomerDTO cardOwner = ServiceManager.getService(IUserService.class).getCustomerWithMemberByMemberNoShopId(
              repairOrderDTO.getAccountMemberNo(), repairOrderDTO.getShopId());
          if (cardOwner != null && StringUtils.isNotEmpty(cardOwner.getMobile()) && repairOrderDTO.isSendMemberSms()) {
            String smsContent=smsService.sendMemberMsgToCardOwner(cardOwner, shopDTO, context);
            ContactDTO contactDTO=new ContactDTO();
            contactDTO.setMobile(cardOwner.getMobile());
            ServiceManager.getService(ISendSmsService.class).sendSms(repairOrderDTO.getShopId(),repairOrderDTO.getUserId(),smsContent,false,true,true,contactDTO);
          }
          String time = DateUtil.convertDateLongToDateString(RfTxnConstant.FORMAT_CHINESE_YEAR_MONTH_DATE, System.currentTimeMillis());
          //维修美容折扣短信：如果总计大于实收和欠款和的话，就代表打折了，要发送折扣短信给店老板.
          if (repairOrderDTO.getTotal() > repairOrderDTO.getSettledAmount() + repairOrderDTO.getDebt()) {
            smsService.sendCheapMsgToBoss(repairOrderDTO, repairOrderDTO.getShopId(), shopDTO, time);
          }
          //如果有欠款就要发送欠款备忘给店老板
          if (repairOrderDTO.getDebt() > 0) {
            smsService.sendDebtMsgToBoss(repairOrderDTO, repairOrderDTO.getShopId(), shopDTO, time);
          }
        }
        //单据已经被结算
        if (receivableDTO != null) {
          salesStat(repairOrderDTO, repairOrderDTO.getStatus());
        }
        //reindex solr
        reCreateSolrIndex(repairOrderDTO);
      } else {
        throw new Exception("施工单 repairOrderDTO 为空");
      }
    } catch (Exception e) {
      LOG.error("/RepairOrderSavedListener");
      LOG.error("method=run");
      LOG.error("施工单repairOrderDTO 放入 orderInder失败,repairOrderDTO.toString:" + repairOrderDTO.toString());
      LOG.error(e.getMessage(), e);
    } finally {
      LOG.warn("AOP_thread:RepairOrderSavedListener Run {}ms", System.currentTimeMillis() - begin);
      repairOrderSavedEvent.setOrderFlag(true);
    }
  }

  private void reCreateSolrIndex(RepairOrderDTO repairOrderDTO) throws Exception {
    ServiceManager.getService(IProductSolrWriterService.class).createProductSolrIndex(repairOrderDTO.getShopId(), repairOrderDTO.getProductIds());

    //reindex order solr
    ServiceManager.getService(IOrderSolrWriterService.class).reCreateOrderSolrIndex(ServiceManager.getService(IConfigService.class).getShopById(repairOrderDTO.getShopId()), OrderTypes.REPAIR, repairOrderDTO.getId());
    //reindex customer in solr
    ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerByCustomerId(repairOrderDTO.getCustomerId());

    if (!ArrayUtils.isEmpty(repairOrderDTO.getServiceDTOs())) {
      Set<Long> serviceIds = new HashSet<Long>();
      for (RepairOrderServiceDTO repairOrderServiceDTO : repairOrderDTO.getServiceDTOs()) {
        if (repairOrderServiceDTO.getServiceId() != null) {
          serviceIds.add(repairOrderServiceDTO.getServiceId());
        }
      }
      if (CollectionUtils.isNotEmpty(serviceIds)) {
        ServiceManager.getService(IOrderSolrWriterService.class).createRepairServiceSolrIndex(repairOrderDTO.getShopId(), serviceIds);
      }
    }
    //新增车辆信息添加到solr
    if (repairOrderDTO.isAddVehicleInfoToSolr()) {
      VehicleDTO vehicleDTOForSolr = new VehicleDTO(repairOrderDTO);
      if (vehicleDTOForSolr.getId() != null) {
        List<VehicleDTO> vehicleDTOs = new ArrayList<VehicleDTO>();
        vehicleDTOs.add(vehicleDTOForSolr);
        ServiceManager.getService(IProductSolrService.class).addVehicleForSearch(vehicleDTOs);
      }
    }
    ServiceManager.getService(IVehicleSolrWriterService.class).createVehicleSolrIndex(repairOrderDTO.getShopId(), repairOrderDTO.getVechicleId());

  }

  private void saveItemIndex(RepairOrderDTO repairOrderDTO) throws Exception {
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    List<ItemIndex> itemIndexList = new ArrayList<ItemIndex>();
    if (!ArrayUtils.isEmpty(repairOrderDTO.getServiceDTOs())) {
      for (RepairOrderServiceDTO repairOrderServiceDTO : repairOrderDTO.getServiceDTOs()) {
        if (StringUtils.isBlank(repairOrderServiceDTO.getService())) {
          continue;
        }
        ItemIndex itemIndex = new ItemIndex();
        itemIndex.setRepairOrderService(repairOrderDTO, repairOrderServiceDTO);
        itemIndexList.add(itemIndex);
      }
    }
    if (!ArrayUtils.isEmpty(repairOrderDTO.getItemDTOs())) {
      for (RepairOrderItemDTO repairOrderItemDTO : repairOrderDTO.getItemDTOs()) {
        if (StringUtils.isBlank(repairOrderItemDTO.getProductName())) {
          continue;
        }
        ItemIndex itemIndex = new ItemIndex();
        itemIndex.setRepairOrderItem(repairOrderDTO, repairOrderItemDTO);
        itemIndexList.add(itemIndex);
      }
    }

    if (CollectionUtils.isNotEmpty(repairOrderDTO.getOtherIncomeItemDTOList())) {
      for (RepairOrderOtherIncomeItemDTO repairOrderOtherIncomeItemDTO : repairOrderDTO.getOtherIncomeItemDTOList()) {
        if (repairOrderOtherIncomeItemDTO == null || repairOrderOtherIncomeItemDTO.getId() == null) {
          continue;
        }
        ItemIndex itemIndex = new ItemIndex();
        itemIndex.setRepairOrderOtherIncome(repairOrderDTO, repairOrderOtherIncomeItemDTO);
        itemIndexList.add(itemIndex);
      }
    }
    searchService.deleteItemIndex(repairOrderDTO.getShopId(), repairOrderDTO.getId());
    searchService.addItemIndexList(itemIndexList);
  }

}
