package com.bcgogo.schedule.stat;

import com.bcgogo.common.Sort;
import com.bcgogo.enums.*;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.dto.ProductLocalInfoDTO;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.schedule.BcgogoQuartzJobBean;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.model.*;
import com.bcgogo.txn.service.IPurchaseCostStatService;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.txn.service.IVehicleStatService;
import com.bcgogo.user.dto.VehicleDTO;
import com.bcgogo.user.dto.VehicleModifyLogDTO;
import com.bcgogo.user.model.VehicleModifyLog;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 成本统计分析监听。监听product_modify_log表，如果有修改则重新统计相关数据。
 * User: Jimuchen
 * Date: 12-11-1
 * Time: 下午4:02
 */
public class CostStatSchedule extends BcgogoQuartzJobBean {
  public static final Logger LOG = LoggerFactory.getLogger(CostStatSchedule.class);
  private static boolean lock = false;

  @Override
  protected void executeJob(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    if (lock) {
      return;
    }
    lock = true;
    LOG.info("后台成本统计监听开始运行.");
    try {
      productPropertyChangeProcess();
    } catch (Exception e) {
      LOG.error("/CostStatSchedule, method=productPropertyChangeProcess. 后台成本统计-产品属性修改监听运行失败");
      LOG.error(e.getMessage(), e);
    }
    try{
      sellUnitChangeProcess();
    } catch(Exception e){
      LOG.error("/CostStatSchedule, method=productPropertyChangeProcess. 后台成本统计-单位修改监听运行失败");
      LOG.error(e.getMessage(), e);
    }
    try{
      vehicleChangeProcess();
    } catch(Exception e){
      LOG.error("/CostStatSchedule, method=productPropertyChangeProcess. 后台成本统计-车辆属性修改监听运行失败");
      LOG.error(e.getMessage(), e);
    }
    LOG.info("后台成本统计监听运行结束");
    lock = false;
  }

  private void vehicleChangeProcess() throws Exception{
    LOG.info("车型服务统计 - 车辆品牌/型号变动监听 开始");
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    IVehicleStatService vehicleStatService = ServiceManager.getService(IVehicleStatService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    StatProcessStatus[] statProcessStatuses = {StatProcessStatus.NEW, StatProcessStatus.FAIL};
    List<VehicleModifyLog> toProcessLogs = customerService.getVehicleModifyLogByStatus(statProcessStatuses);
    if(CollectionUtils.isEmpty(toProcessLogs)){
      LOG.info("车型服务统计 - 车辆品牌/型号变动监听 结束");
      return;
    }
    Map<Long, VehicleModifyLogDTO> operationLogInfoMap = new LinkedHashMap<Long, VehicleModifyLogDTO>();
    for(VehicleModifyLog vehicleModifyLog : toProcessLogs){
      VehicleModifyLogDTO vehicleModifyLogDTO = operationLogInfoMap.get(vehicleModifyLog.getOperationId());
      //如果为空，记录老值新值
      if(vehicleModifyLogDTO == null){
        vehicleModifyLogDTO = new VehicleModifyLogDTO();
        vehicleModifyLogDTO.setShopId(vehicleModifyLog.getShopId());
        vehicleModifyLogDTO.setVehicleId(vehicleModifyLog.getVehicleId());
        vehicleModifyLogDTO.setOperationId(vehicleModifyLog.getOperationId());
        vehicleModifyLogDTO.setCreationDate(vehicleModifyLog.getCreationDate());
        if(vehicleModifyLog.getFieldName() == VehicleModifyFields.brand){
          vehicleModifyLogDTO.setOldBrand(vehicleModifyLog.getOldValue());
          vehicleModifyLogDTO.setNewBrand(vehicleModifyLog.getNewValue());
        }
        if(vehicleModifyLog.getFieldName() == VehicleModifyFields.model){
          vehicleModifyLogDTO.setOldModel(vehicleModifyLog.getOldValue());
          vehicleModifyLogDTO.setNewModel(vehicleModifyLog.getNewValue());
        }
      }else{
        //同一次操作，则更新其他信息
        if(vehicleModifyLog.getFieldName() == VehicleModifyFields.brand){
          vehicleModifyLogDTO.setOldBrand(vehicleModifyLog.getOldValue());
          vehicleModifyLogDTO.setNewBrand(vehicleModifyLog.getNewValue());
        }
        if(vehicleModifyLog.getFieldName() == VehicleModifyFields.model){
          vehicleModifyLogDTO.setOldModel(vehicleModifyLog.getOldValue());
          vehicleModifyLogDTO.setNewModel(vehicleModifyLog.getNewValue());
        }
      }
      operationLogInfoMap.put(vehicleModifyLog.getOperationId(), vehicleModifyLogDTO);
    }

    Set<Long> operationIdSet = operationLogInfoMap.keySet();
    for(Long operationId : operationIdSet){
      VehicleModifyLogDTO logInfo = operationLogInfoMap.get(operationId);
      VehicleDTO vehicle = userService.getVehicleById(logInfo.getVehicleId());
      if(vehicle == null){
        continue;
      }
      Long vehicleId = vehicle.getId();

      Long firstRepairOrderCreationTime = txnService.getFirstRepairOrderCreationTimeByVehicleId(logInfo.getShopId(), vehicleId);
      Long firstWashBeautyOrderCreationTime = txnService.getFirstWashBeautyOrderCreationTimeByVehicleId(logInfo.getShopId(), vehicleId);

      long firstOrderTime = 0;
      if(firstRepairOrderCreationTime == null && firstWashBeautyOrderCreationTime == null){
        continue;
      }else if(firstRepairOrderCreationTime != null){
        firstOrderTime = firstRepairOrderCreationTime;
      }else if(firstWashBeautyOrderCreationTime != null){
        firstOrderTime = firstWashBeautyOrderCreationTime;
      }else{
        firstOrderTime = firstRepairOrderCreationTime > firstWashBeautyOrderCreationTime ? firstWashBeautyOrderCreationTime: firstRepairOrderCreationTime;
      }

      int year = DateUtil.getYear(firstOrderTime);
      int month = DateUtil.getMonth(firstOrderTime);
      Calendar calendar = Calendar.getInstance();
      calendar.clear();
      calendar.set(year, month-1, 1, 0, 0, 0);

      long begin = calendar.getTimeInMillis();

      while(begin < logInfo.getCreationDate()){
        calendar.add(Calendar.MONTH, 1);
        year = DateUtil.getYear(begin);
        month = DateUtil.getMonth(begin);
        long end = calendar.getTimeInMillis();
        end = end<logInfo.getCreationDate()?end:logInfo.getCreationDate();

        List<VehicleServeMonthStat> changeStats = vehicleStatService.vehicleServeMonthStatForVehicleInRange(logInfo.getShopId(), vehicleId, begin, end);
        if(CollectionUtils.isEmpty(changeStats)){
          begin = end;
          continue;
        }
        List<VehicleServeMonthStat> toUpdateStats = new ArrayList<VehicleServeMonthStat>();

        for(VehicleServeMonthStat changeStat : changeStats){
          int statYear = changeStat.getStatYear();
          int statMonth = changeStat.getStatMonth();
          String oldBrand = StringUtils.isBlank(logInfo.getOldBrand())?"":logInfo.getOldBrand();
          String oldModel = StringUtils.isBlank(logInfo.getOldModel())?"":logInfo.getOldModel();
          String newBrand = StringUtils.isBlank(logInfo.getNewBrand())?"":logInfo.getNewBrand();
          String newModel = StringUtils.isBlank(logInfo.getNewModel())?"":logInfo.getNewModel();
          VehicleServeMonthStat oldStat = vehicleStatService.getVehicleServeMonthStatByBrandModelYearMonth(logInfo.getShopId(), oldBrand, oldModel, statYear, statMonth);
          VehicleServeMonthStat newStat = vehicleStatService.getVehicleServeMonthStatByBrandModelYearMonth(logInfo.getShopId(), newBrand, newModel, statYear, statMonth);
          if(oldStat != null){
            oldStat.setWashTimes(oldStat.getWashTimes() - changeStat.getWashTimes());
            oldStat.setWashTotal(oldStat.getWashTotal() - changeStat.getWashTotal());
            oldStat.setRepairTimes(oldStat.getRepairTimes() - changeStat.getRepairTimes());
            oldStat.setRepairTotal(oldStat.getRepairTotal() - changeStat.getRepairTotal());
            oldStat.setTotalTimes(oldStat.getWashTimes() + oldStat.getRepairTimes());
            oldStat.setTotalConsume(oldStat.getWashTotal() + oldStat.getRepairTotal());
            toUpdateStats.add(oldStat);
          }
          if(newStat != null){
            newStat.setWashTimes(newStat.getWashTimes() + changeStat.getWashTimes());
            newStat.setWashTotal(newStat.getWashTotal() + changeStat.getWashTotal());
            newStat.setRepairTimes(newStat.getRepairTimes() + changeStat.getRepairTimes());
            newStat.setRepairTotal(newStat.getRepairTotal() + changeStat.getRepairTotal());
            newStat.setTotalTimes(newStat.getWashTimes() + newStat.getRepairTimes());
            newStat.setTotalConsume(newStat.getWashTotal() + newStat.getRepairTotal());
            toUpdateStats.add(newStat);
          }else{
            VehicleServeMonthStat stat = new VehicleServeMonthStat();
            stat.setShopId(logInfo.getShopId());
            stat.setStatYear(statYear);
            stat.setStatMonth(statMonth);
            stat.setBrand(newBrand);
            stat.setModel(newModel);
            stat.setRepairTimes(changeStat.getRepairTimes());
            stat.setRepairTotal(changeStat.getRepairTotal());
            stat.setWashTimes(changeStat.getWashTimes());
            stat.setWashTotal(changeStat.getWashTotal());
            stat.setTotalConsume(changeStat.getRepairTotal() + changeStat.getWashTotal());
            stat.setTotalTimes(changeStat.getRepairTimes() + changeStat.getWashTimes());
            toUpdateStats.add(stat);
          }
          vehicleStatService.batchSaveOrUpdateVehicleServeMonthStat(toUpdateStats);
        }
        begin = end;
      }
    }
    customerService.batchUpdateVehicleModifyLogStatus(toProcessLogs, StatProcessStatus.DONE);

    LOG.info("车型服务统计 - 车辆品牌/型号变动监听 结束");
  }

  private void sellUnitChangeProcess() throws Exception{
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IPurchaseCostStatService purchaseCostStatService = ServiceManager.getService(IPurchaseCostStatService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);
    TxnDaoManager txnDaoManager = ServiceManager.getService(TxnDaoManager.class);
    TxnWriter writer = txnDaoManager.getWriter();
    StatProcessStatus[] statProcessStatuses = {StatProcessStatus.NEW, StatProcessStatus.FAIL};

    //从TXN的product_modify_log表中获取所有操作类型为TXN_SET_SECOND_UNIT、状态为NEW、修改单位为cellUnit的记录
    List<ProductModifyLogDTO> toProcessLogs = txnService.getProductModifyLogByStatus(ProductModifyOperations.TXN_SET_SECOND_UNIT, statProcessStatuses);   //以operation_id为升序排列，最先修改的在最前面

    if(CollectionUtils.isEmpty(toProcessLogs)){
      return;
    }
    for(ProductModifyLogDTO productModifyLogDTO : toProcessLogs){
      if(productModifyLogDTO.getFieldName() != ProductModifyFields.sellUnit){
        continue;
      }
      //此处的productId为单据所引用的product_local_info表的ID
      Long productId = productModifyLogDTO.getProductId();
      Long shopId = productModifyLogDTO.getShopId();
      ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoById(productId, shopId);
      ProductDTO productDTO = productService.getProductByProductLocalInfoId(productId, shopId);

      // 入库相关统计修改
      //根据productId获取引用到该商品的第一个单据的创建时间
      Long purchaseInventoryCreationDate = txnService.getFirstPurchaseInventoryCreationDateByProductIdShopId(shopId, productId);

      Object status = writer.begin();
      List<ProductModifyLogDTO> oneLog = new ArrayList<ProductModifyLogDTO>();
      StatProcessStatus finalStatus = null;
      try{
        if(purchaseInventoryCreationDate != null){
          int year = DateUtil.getYear(purchaseInventoryCreationDate);
          int month = DateUtil.getMonth(purchaseInventoryCreationDate);

          double monthStorageAmount = 0;

          Calendar calendar = Calendar.getInstance();
          calendar.clear();
          calendar.set(year, month-1, 1, 0, 0, 0);
          Long startTime = calendar.getTimeInMillis();

          //成本统计，按月度修改
          while(startTime < productModifyLogDTO.getCreationDate()){
            year = DateUtil.getYear(startTime);
            month = DateUtil.getMonth(startTime);
            calendar.add(Calendar.MONTH, 1);
            Long endTime = calendar.getTimeInMillis();
            endTime = endTime < productModifyLogDTO.getCreationDate() ? endTime : productModifyLogDTO.getCreationDate();
            List<PurchaseInventoryDTO> list = txnService.getPurchaseInventoryDTOByProductIdCreationDate(shopId, productId, startTime, endTime);
            if(CollectionUtils.isEmpty(list)){
              startTime = endTime;
              continue;
            }
            for(PurchaseInventoryDTO purchaseInventoryDTO : list){
              PurchaseInventoryItemDTO[] items = purchaseInventoryDTO.getItemDTOs();
              if(ArrayUtils.isEmpty(items)){
                continue;
              }
              for(PurchaseInventoryItemDTO itemDTO : items){
                if(!itemDTO.getProductId().equals(productId)){
                  continue;
                }
                monthStorageAmount += itemDTO.getAmount();
              }
            }

            PurchaseInventoryItemDTO itemDTO = new PurchaseInventoryItemDTO();
            itemDTO.setProductName(productDTO.getName());
            itemDTO.setBrand(productDTO.getBrand());
            itemDTO.setVehicleBrand(productDTO.getProductVehicleBrand());
            itemDTO.setVehicleModel(productDTO.getProductVehicleModel());
            PurchaseInventoryMonthStat monthStat = writer.getPurchaseInventoryMonthStat(shopId, itemDTO, year, month);
            if(monthStat!=null){
              double toAddAmount = monthStorageAmount * productLocalInfoDTO.getRate() - monthStorageAmount;
              monthStat.setAmount(monthStat.getAmount() + toAddAmount);
              writer.update(monthStat);
            }
            startTime = endTime;
          }

          //价格波动，修改所有大单位转小单位之前的统计数据
          startTime = purchaseInventoryCreationDate;
          //查询所有大单位转小单位之前的入库单记录
          List<PurchaseInventoryDTO> list = txnService.getPurchaseInventoryDTOByProductIdCreationDate(shopId, productId, startTime, productModifyLogDTO.getCreationDate());
          if(CollectionUtils.isEmpty(list)){
            break;
          }
          for(PurchaseInventoryDTO purchaseInventoryDTO : list){
            PurchaseInventoryItemDTO[] items = purchaseInventoryDTO.getItemDTOs();
            if(ArrayUtils.isEmpty(items)){
              continue;
            }
            //是大单位时，在purchase_inventory_stat_change表中补足数量，统计时间按照入库那天的6点整时刻
            Double itemStorageAmount = 0d;
            for(PurchaseInventoryItemDTO itemDTO : items){
              if(!itemDTO.getProductId().equals(productId)){
                continue;
              }
  //            if(!UnitUtil.isStorageUnit(itemDTO.getUnit(), productLocalInfoDTO)){
  //              continue;
  //            }
              itemStorageAmount = itemStorageAmount + itemDTO.getAmount();
            }
            Double addAmount = itemStorageAmount * productLocalInfoDTO.getRate() - itemStorageAmount;
            //在保存到purchase_inventory_stat_change
            if(addAmount>0){
              purchaseCostStatService.saveOrUpdatePurchaseInventoryStatChange(shopId,productId,addAmount,purchaseInventoryDTO.getVestDate());
            }
          }

        }

        //退货相关统计修改
        Long purchaseReturnCreationDate = txnService.getFirstPurchaseReturnCreationDateByProductIdShopId(shopId, productId);
        if(purchaseReturnCreationDate != null){
          int year = DateUtil.getYear(purchaseReturnCreationDate);
          int month = DateUtil.getMonth(purchaseReturnCreationDate);

          double returnMonthStorageAmount = 0;
          Calendar calendar = Calendar.getInstance();
          calendar.clear();
          calendar.set(year, month-1, 1, 0, 0, 0);
          long startTime = calendar.getTimeInMillis();

          while(startTime < productModifyLogDTO.getCreationDate()){
            year = DateUtil.getYear(startTime);
            month = DateUtil.getMonth(startTime);
            calendar.add(Calendar.MONTH, 1);
            Long endTime = calendar.getTimeInMillis();
            endTime = endTime < productModifyLogDTO.getCreationDate() ? endTime : productModifyLogDTO.getCreationDate();
            List<PurchaseReturnDTO> list = txnService.getPurchaseReturnDTOByProductIdCreationDate(shopId, productId, startTime, endTime);
            if(CollectionUtils.isEmpty(list)){
              startTime = endTime;
              continue;
            }

            for(PurchaseReturnDTO purchaseReturnDTO : list) {
              PurchaseReturnItemDTO[] items = purchaseReturnDTO.getItemDTOs();
              if (ArrayUtils.isEmpty(items)) {
                continue;
              }
              for (PurchaseReturnItemDTO itemDTO : items) {
                if (!itemDTO.getProductId().equals(productId)) {
                  continue;
                }
                returnMonthStorageAmount += itemDTO.getAmount();

                PurchaseReturnMonthStat purchaseReturnMonthStat = writer.getPurchaseReturnMonthStat(shopId, purchaseReturnDTO.getSupplierId(), itemDTO.getProductId(), year, month);
                if (purchaseReturnMonthStat != null) {
                  double returnAmount = returnMonthStorageAmount * productLocalInfoDTO.getRate() - returnMonthStorageAmount;
                  purchaseReturnMonthStat.setAmount(purchaseReturnMonthStat.getAmount() + returnAmount);
                  writer.update(purchaseReturnMonthStat);
                }
              }
            }
            startTime = endTime;
          }
        }

        //畅销 滞销品统计
        Sort sort = new Sort(" a.creationDate ", " asc ");
        Long salesCreationDate = NumberUtil.longValue(writer.getSalesVestDateByShopId(shopId, productId, sort));
        Long repairCreationDate = NumberUtil.longValue(writer.getRepairVestDateByShopId(shopId, productId, sort));
        Long date = 0L;
        if(salesCreationDate <= 0L || repairCreationDate <= 0L){
          date = salesCreationDate < repairCreationDate ? repairCreationDate : salesCreationDate;
        }else{
          date = salesCreationDate < repairCreationDate ? salesCreationDate : repairCreationDate;
        }
        if (date > 0) {
          int year = DateUtil.getYear(date);
          int month = DateUtil.getMonth(date);

          Calendar calendar = Calendar.getInstance();
          calendar.clear();
          calendar.set(year, month - 1, 1, 0, 0, 0);
          Long startTime = calendar.getTimeInMillis();

          while (startTime < productModifyLogDTO.getCreationDate()) {
            calendar.add(Calendar.MONTH, 1);
            year = DateUtil.getYear(startTime);
            month = DateUtil.getMonth(startTime);
            Long endTime = calendar.getTimeInMillis();
            endTime = endTime < productModifyLogDTO.getCreationDate() ? endTime : productModifyLogDTO.getCreationDate();

            double amount = 0D;
            List<RepairOrder> repairOrderList = writer.getRepairOrderByProductIdCreationDate(shopId, productId, startTime, endTime);

            if (CollectionUtils.isNotEmpty(repairOrderList)) {
              for (RepairOrder repairOrder : repairOrderList) {
                if (repairOrder.getStatusEnum() == OrderStatus.REPAIR_REPEAL) {
                  continue;
                }
                List<RepairOrderItem> repairOrderItemList = writer.getRepairOrderItemsByRepairOrderId(repairOrder.getId());
                if (CollectionUtils.isEmpty(repairOrderItemList)) {
                  continue;
                }
                for (RepairOrderItem repairOrderItem : repairOrderItemList) {
                  if (!repairOrderItem.getProductId().equals(productId)) {
                    continue;
                  }
                  amount += repairOrderItem.getAmount();
                }
              }
            }


            List<SalesOrder> salesOrderList = writer.getSalesOrderByProductIdCreationDate(shopId, productId, startTime, endTime);

            if (CollectionUtils.isNotEmpty(salesOrderList)) {
              for (SalesOrder salesOrder : salesOrderList) {
                if (salesOrder.getStatusEnum() == OrderStatus.SALE_REPEAL) {
                  continue;
                }
                List<SalesOrderItem> salesOrderItemList = writer.getSalesOrderItemsByOrderId(salesOrder.getId());
                if (CollectionUtils.isEmpty(salesOrderItemList)) {
                  continue;
                }
                for (SalesOrderItem salesOrderItem : salesOrderItemList) {
                  if (!salesOrderItem.getProductId().equals(productId)) {
                    continue;
                  }
                  amount += salesOrderItem.getAmount();
                }
              }
            }

            List<SalesMonthStat> salesMonthStatList = writer.getSalesMonthStatByYearMonth(shopId, productId, year, month);

            if (CollectionUtils.isEmpty(salesMonthStatList)) {
              LOG.error("shopId:" + shopId + "year:" + year + "month:" + month + "salesMonthStat无记录");
            } else {
              double returnAmount = amount * productLocalInfoDTO.getRate() - amount;
              SalesMonthStat salesMonthStat = salesMonthStatList.get(0);
              salesMonthStat.setAmount(NumberUtil.toReserve(returnAmount + salesMonthStat.getAmount(),1));
              if (salesMonthStat.getAmount() <= 0) {
                writer.delete(salesMonthStat);
              } else {
                writer.update(salesMonthStat);
              }
            }
            startTime = endTime;
          }
        }

        oneLog.add(productModifyLogDTO);

        writer.commit(status);
        finalStatus = StatProcessStatus.DONE;
      }catch(Exception e){
        writer.rollback(status);
        finalStatus = StatProcessStatus.FAIL;
        LOG.error("单位修改后成本统计数量修改出错(入库, 入库退货单)", e);
      }finally{
        writer.rollback(status);
        txnService.batchUpdateProductModifyLogStatus(oneLog, finalStatus);
      }
    }
  }

  private void productPropertyChangeProcess() throws Exception {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IPurchaseCostStatService purchaseCostStatService = ServiceManager.getService(IPurchaseCostStatService.class);
    StatProcessStatus[] status = {StatProcessStatus.NEW, StatProcessStatus.FAIL};
    List<ProductModifyLogDTO> toProcessLogs = txnService.getProductModifyLogByStatus(ProductModifyOperations.INVENTORY_INDEX_UPDATE, status);   //以operation_id为升序排列，最先修改的在最前面

    if(CollectionUtils.isEmpty(toProcessLogs)){
      return;
    }

    Map<Long, ProductModifyLogDTO> productLogMap = new LinkedHashMap<Long, ProductModifyLogDTO>();

    //得到所有改变了的商品的旧值新值（品名，品牌，车型，车品牌）
    for(ProductModifyLogDTO dto : toProcessLogs){
      Long operationId = dto.getOperationId();

      ProductModifyLogDTO productModifyLogDTO = productLogMap.get(operationId);
      if(productModifyLogDTO == null){
        productModifyLogDTO = new ProductModifyLogDTO();
        productModifyLogDTO.setProductId(dto.getProductId());
        productModifyLogDTO.setShopId(dto.getShopId());
        productModifyLogDTO.setOperationId(operationId);
        productModifyLogDTO.setCreationDate(dto.getCreationDate());
        productLogMap.put(operationId, productModifyLogDTO);
        if(dto.getFieldName() == ProductModifyFields.brand){
          productModifyLogDTO.setOldBrand(dto.getOldValue());
          productModifyLogDTO.setNewBrand(dto.getNewValue());
        }
        if(dto.getFieldName() == ProductModifyFields.name){
          productModifyLogDTO.setOldName(dto.getOldValue());
          productModifyLogDTO.setNewName(dto.getNewValue());
        }
        if(dto.getFieldName() == ProductModifyFields.productVehicleModel){
          productModifyLogDTO.setOldProductVehicleModel(dto.getOldValue());
          productModifyLogDTO.setNewProductVehicleModel(dto.getNewValue());
        }
        if(dto.getFieldName() == ProductModifyFields.productVehicleBrand){
          productModifyLogDTO.setOldProductVehicleBrand(dto.getOldValue());
          productModifyLogDTO.setNewProductVehicleBrand(dto.getNewValue());
        }
      }else{
        if(dto.getFieldName() == ProductModifyFields.brand){
          productModifyLogDTO.setOldBrand(dto.getOldValue());
          productModifyLogDTO.setNewBrand(dto.getNewValue());
        }
        if(dto.getFieldName() == ProductModifyFields.name){
          productModifyLogDTO.setOldName(dto.getOldValue());
          productModifyLogDTO.setNewName(dto.getNewValue());
        }
        if(dto.getFieldName() == ProductModifyFields.productVehicleModel){
          productModifyLogDTO.setOldProductVehicleModel(dto.getOldValue());
          productModifyLogDTO.setNewProductVehicleModel(dto.getNewValue());
        }
        if(dto.getFieldName() == ProductModifyFields.productVehicleBrand){
          productModifyLogDTO.setOldProductVehicleBrand(dto.getOldValue());
          productModifyLogDTO.setNewProductVehicleBrand(dto.getNewValue());
        }
      }
    }

    Set<Long> operationIdSet = productLogMap.keySet();

    //查出统计表中原属性值相应的统计值。相应的减去，并增加到新属性对应的记录上
    for(Long operationId : operationIdSet){
      ProductModifyLogDTO productModifyLog = productLogMap.get(operationId);

      Long firstPurchaseInventoryTime = txnService.getFirstPurchaseInventoryCreationDateByProductIdShopId(productModifyLog.getShopId(), productModifyLog.getProductId());
      if(firstPurchaseInventoryTime == null){
        continue;
      }

      int year = DateUtil.getYear(firstPurchaseInventoryTime);
      int month = DateUtil.getMonth(firstPurchaseInventoryTime);
      Calendar calendar = Calendar.getInstance();
      calendar.clear();
      calendar.set(year, month-1, 1, 0, 0, 0);
      long begin = calendar.getTimeInMillis();

      while(begin < productModifyLog.getCreationDate()){
        calendar.add(Calendar.MONTH, 1);
        year = DateUtil.getYear(begin);
        month = DateUtil.getMonth(begin);
        long end = calendar.getTimeInMillis();
        end = end<productModifyLog.getCreationDate()?end:productModifyLog.getCreationDate();

        //得到此商品在统计时间段内的相应统计值
        List<PurchaseInventoryMonthStat> changeStats = purchaseCostStatService.purchaseCostStatForProductInRange(productModifyLog.getShopId(), productModifyLog.getProductId(), begin, end);

        if(CollectionUtils.isEmpty(changeStats)){
          begin = end;
          continue;
        }

        List<PurchaseInventoryMonthStat> toUpdateStats = new ArrayList<PurchaseInventoryMonthStat>();
        for(PurchaseInventoryMonthStat changeStat : changeStats){
          int statYear = changeStat.getStatYear();
          int statMonth = changeStat.getStatMonth();

          PurchaseInventoryMonthStat oldStat = purchaseCostStatService.getPurchaseInventoryMonthStatByPropertiesYearMonth(productModifyLog.getShopId(),
              StringUtils.defaultIfEmpty(productModifyLog.getOldName(), ""), StringUtils.defaultIfEmpty(productModifyLog.getOldBrand(), ""),
              StringUtils.defaultIfEmpty(productModifyLog.getOldProductVehicleBrand(), ""), StringUtils.defaultIfEmpty(productModifyLog.getOldProductVehicleModel(), ""),
              statYear, statMonth);
          PurchaseInventoryMonthStat newStat = purchaseCostStatService.getPurchaseInventoryMonthStatByPropertiesYearMonth(productModifyLog.getShopId(),
              StringUtils.defaultIfEmpty(productModifyLog.getNewName(), ""), StringUtils.defaultIfEmpty(productModifyLog.getNewBrand(), ""),
              StringUtils.defaultIfEmpty(productModifyLog.getNewProductVehicleBrand(), ""), StringUtils.defaultIfEmpty(productModifyLog.getNewProductVehicleModel(), ""),
              statYear, statMonth);

          if(oldStat!=null){
            //旧统计记录减去相应值
            oldStat.setAmount(oldStat.getAmount() - changeStat.getAmount());
            oldStat.setTimes(oldStat.getTimes() - changeStat.getTimes());
            oldStat.setTotal(oldStat.getTotal() - changeStat.getTotal());
            toUpdateStats.add(oldStat);
          }

          if(newStat != null){
            newStat.setAmount(newStat.getAmount() + changeStat.getAmount());
            newStat.setTimes(newStat.getTimes() + changeStat.getTimes());
            newStat.setTotal(newStat.getTotal() + changeStat.getTotal());
            toUpdateStats.add(newStat);
          }else{
            PurchaseInventoryMonthStat stat = new PurchaseInventoryMonthStat();
            stat.setShopId(productModifyLog.getShopId());
            stat.setStatYear(statYear);
            stat.setStatMonth(statMonth);
            stat.setProductName(StringUtils.defaultIfEmpty(productModifyLog.getNewName(), ""));
            stat.setProductBrand(StringUtils.defaultIfEmpty(productModifyLog.getNewBrand(), ""));
            stat.setVehicleBrand(StringUtils.defaultIfEmpty(productModifyLog.getNewProductVehicleBrand(), ""));
            stat.setVehicleModel(StringUtils.defaultIfEmpty(productModifyLog.getNewProductVehicleModel(), ""));
            stat.setTimes(changeStat.getTimes());
            stat.setAmount(changeStat.getAmount());
            stat.setTotal(changeStat.getTotal());
            toUpdateStats.add(stat);
          }
          purchaseCostStatService.batchSaveOrUpdateInventoryCostMonthStat(toUpdateStats);
        }
        begin = end;
      }
    }
    txnService.batchUpdateProductModifyLogStatus(toProcessLogs, StatProcessStatus.DONE );
  }

}
