package com.bcgogo.txn.service;

import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.stat.dto.VehicleServeMonthStatDTO;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.model.*;
import com.bcgogo.user.dto.CustomerVehicleDTO;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.utils.BcgogoShopLogicResourceUtils;
import com.bcgogo.utils.DateUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 12-11-13
 * Time: 上午11:17
 */
@Component
public class VehicleStatService implements IVehicleStatService {
  private static final Logger LOG = LoggerFactory.getLogger(VehicleStatService.class);

  @Autowired
  private TxnDaoManager txnDaoManager;

  @Override
  public void vehicleServeStat(BcgogoOrderDto bcgogoOrderDto, boolean repeal) {
    if(bcgogoOrderDto == null){
      return;
    }
    if(bcgogoOrderDto.getVestDate() == null || bcgogoOrderDto.getShopId()== null){
      LOG.error("车型服务统计时数据异常，VehicleStatService.vehicleServeStat(). bcgogoOrderDto:{}", bcgogoOrderDto.toString());
      return;
    }
    if(repeal && bcgogoOrderDto.getVestDate()==0 && bcgogoOrderDto instanceof RepairOrderDTO){    // 未结算的施工单作废
      return;
    }
    saveVehicleServeMonthStat(bcgogoOrderDto, repeal);
  }

  private void saveVehicleServeMonthStat(BcgogoOrderDto bcgogoOrderDto, boolean repeal) {
    Long shopId = bcgogoOrderDto.getShopId();
    TxnWriter writer = txnDaoManager.getWriter();
    int year = DateUtil.getYear(bcgogoOrderDto.getVestDate());
    int month = DateUtil.getMonth(bcgogoOrderDto.getVestDate());

    Object status = writer.begin();
    try {
      double repairTotal = 0;
      double washTotal = 0;
      int repairTimes = 0;
      int washTimes = 0;
      String brand = "";
      String model = "";
      if (bcgogoOrderDto instanceof RepairOrderDTO) {
        RepairOrderDTO repairOrderDTO = (RepairOrderDTO) bcgogoOrderDto;
        model = StringUtils.isBlank(repairOrderDTO.getModel())?"":repairOrderDTO.getModel();
        brand = StringUtils.isBlank(repairOrderDTO.getBrand())?"":repairOrderDTO.getBrand();
        repairTotal = repairOrderDTO.getTotal();
        repairTimes = 1;
      } else if (bcgogoOrderDto instanceof WashBeautyOrderDTO) {
        WashBeautyOrderDTO washBeautyOrderDTO = (WashBeautyOrderDTO) bcgogoOrderDto;
        model = StringUtils.isBlank(washBeautyOrderDTO.getModel())?"":washBeautyOrderDTO.getModel();
        brand = StringUtils.isBlank(washBeautyOrderDTO.getBrand())?"":washBeautyOrderDTO.getBrand();
        washTotal = washBeautyOrderDTO.getTotal();
        washTimes = 1;
      }

      if(repeal){
        repairTotal = -repairTotal;
        repairTimes = -repairTimes;
        washTimes = -washTimes;
        washTotal = -washTotal;
      }

      VehicleServeMonthStat monthStat = writer.getVehicleServeMonthStat(shopId, brand, model, year, month);
      if (monthStat == null) {     //当月的统计不存在
        VehicleServeMonthStat vehicleServeMonthStat = new VehicleServeMonthStat();
        vehicleServeMonthStat.setShopId(shopId);
        vehicleServeMonthStat.setStatYear(year);
        vehicleServeMonthStat.setStatMonth(month);
        vehicleServeMonthStat.setBrand(brand);
        vehicleServeMonthStat.setModel(model);
        vehicleServeMonthStat.setRepairTimes(repairTimes);
        vehicleServeMonthStat.setRepairTotal(repairTotal);
        vehicleServeMonthStat.setWashTimes(washTimes);
        vehicleServeMonthStat.setWashTotal(washTotal);
        vehicleServeMonthStat.setTotalTimes(repairTimes+washTimes);
        vehicleServeMonthStat.setTotalConsume(repairTotal + washTotal);
        writer.save(vehicleServeMonthStat);
      } else {
        //更新当月的统计记录
        monthStat.setRepairTimes(monthStat.getRepairTimes() + repairTimes);
        monthStat.setRepairTotal(monthStat.getRepairTotal() + repairTotal);
        monthStat.setWashTimes(monthStat.getWashTimes() + washTimes);
        monthStat.setWashTotal(monthStat.getWashTotal() + washTotal);
        monthStat.setTotalConsume(monthStat.getWashTotal() + monthStat.getRepairTotal());
        monthStat.setTotalTimes(monthStat.getRepairTimes() + monthStat.getWashTimes());
        writer.update(monthStat);
      }
      writer.commit(status);
    } catch (Exception e){
      LOG.error("保存洗车/施工单时成本统计出错. VehicleStatService.saveVehicleServeMonthStat. bcgogoOrderDto:{}", bcgogoOrderDto);
      LOG.error(e.getMessage(), e);
      writer.rollback(status);
    }finally{
      writer.rollback(status);
    }
  }

  @Override
  public List<VehicleServeMonthStatDTO> queryTopVehicleServeMonthStat(Long shopId, int year, int month, boolean allYear, int topLimit) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<VehicleServeMonthStat> monthStats = writer.queryTopVehicleServeMonthStat(shopId, year, month, allYear, topLimit);
    if(CollectionUtils.isEmpty(monthStats)){
      return new ArrayList<VehicleServeMonthStatDTO>();
    }
    List<VehicleServeMonthStatDTO> result = new ArrayList<VehicleServeMonthStatDTO>();
    for(VehicleServeMonthStat stat : monthStats){
      VehicleServeMonthStatDTO statDTO = stat.toDTO();
      result.add(statDTO);
    }
    return result;
  }

  @Override
  public int queryVehicleServeTotal(Long shopId, int year, int month, boolean allYear) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.queryVehicleServeTotal(shopId, year, month, allYear);
  }

  @Override
  public List<VehicleServeMonthStat> getVehicleServeMonthStatByBrandModel(Long shopId, String brand, String model) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getVehicleServeMonthStatByBrandModel(shopId, brand, model);
  }

  @Override
  public List<VehicleServeMonthStat> vehicleServeMonthStatForVehicleInRange(Long shopId, Long vehicleId, long begin, long end) {
    TxnWriter txnWriter = txnDaoManager.getWriter();

    Map<String, VehicleServeMonthStat> yearMonthStatsMap = new HashMap<String, VehicleServeMonthStat>();
    List<RepairOrder> repairOrders = txnWriter.getRepairOrderListByCreationDate(shopId, begin, end);
    if(CollectionUtils.isNotEmpty(repairOrders)){
      for(RepairOrder repairOrder : repairOrders){
        if(repairOrder.getStatusEnum() != OrderStatus.REPAIR_SETTLED || repairOrder.getVechicleId() == null || !repairOrder.getVechicleId().equals(vehicleId)){
          continue;
        }
        int year = DateUtil.getYear(repairOrder.getVestDate()==null?repairOrder.getCreationDate():repairOrder.getVestDate());
        int month = DateUtil.getMonth(repairOrder.getVestDate()==null?repairOrder.getCreationDate():repairOrder.getVestDate());
        String key = String.valueOf(year) + "_" + String.valueOf(month);
        VehicleServeMonthStat vehicleServeMonthStat = yearMonthStatsMap.get(key);
        if(vehicleServeMonthStat==null){
          vehicleServeMonthStat = new VehicleServeMonthStat();
          vehicleServeMonthStat.setShopId(shopId);
          vehicleServeMonthStat.setStatYear(year);
          vehicleServeMonthStat.setStatMonth(month);
          vehicleServeMonthStat.setRepairTimes(1);
          vehicleServeMonthStat.setRepairTotal(repairOrder.getTotal());
          yearMonthStatsMap.put(key, vehicleServeMonthStat);
        }else{
          vehicleServeMonthStat.setRepairTimes(vehicleServeMonthStat.getRepairTimes() +1);
          vehicleServeMonthStat.setRepairTotal(vehicleServeMonthStat.getRepairTotal() + repairOrder.getTotal());
        }
      }
    }

    List<WashBeautyOrder> washBeautyOrders = txnWriter.getWashBeautyOrderByCreationDate(shopId, begin, end);
    if(CollectionUtils.isNotEmpty(washBeautyOrders)){
      for(WashBeautyOrder washBeautyOrder : washBeautyOrders){
        if(washBeautyOrder.getStatus()!=OrderStatus.WASH_SETTLED || washBeautyOrder.getVechicleId() == null || !washBeautyOrder.getVechicleId().equals(vehicleId)){
          continue;
        }
        int year = DateUtil.getYear(washBeautyOrder.getVestDate()==null?washBeautyOrder.getCreationDate():washBeautyOrder.getVestDate());
        int month = DateUtil.getMonth(washBeautyOrder.getVestDate()==null?washBeautyOrder.getCreationDate():washBeautyOrder.getVestDate());
        String key = String.valueOf(year) + "_" + String.valueOf(month);
        VehicleServeMonthStat vehicleServeMonthStat = yearMonthStatsMap.get(key);

        if(vehicleServeMonthStat==null){
          vehicleServeMonthStat = new VehicleServeMonthStat();
          vehicleServeMonthStat.setShopId(shopId);
          vehicleServeMonthStat.setStatYear(year);
          vehicleServeMonthStat.setStatMonth(month);
          vehicleServeMonthStat.setWashTimes(1);
          vehicleServeMonthStat.setWashTotal(washBeautyOrder.getTotal());
          yearMonthStatsMap.put(key, vehicleServeMonthStat);
        }else{
          vehicleServeMonthStat.setWashTimes(vehicleServeMonthStat.getWashTimes() + 1);
          vehicleServeMonthStat.setWashTotal(vehicleServeMonthStat.getWashTotal() + washBeautyOrder.getTotal());
        }
      }
    }
    Set<String> keySet = yearMonthStatsMap.keySet();
    List<VehicleServeMonthStat> list = new ArrayList<VehicleServeMonthStat>();
    for(String key:keySet){
      VehicleServeMonthStat vehicleServeMonthStat = yearMonthStatsMap.get(key);
      vehicleServeMonthStat.setTotalConsume(vehicleServeMonthStat.getRepairTotal() + vehicleServeMonthStat.getWashTotal());
      vehicleServeMonthStat.setTotalTimes(vehicleServeMonthStat.getRepairTimes() + vehicleServeMonthStat.getWashTimes());
      list.add(vehicleServeMonthStat);
    }
    return list;
  }

  @Override
  public void batchSaveOrUpdateVehicleServeMonthStat(List<VehicleServeMonthStat> stats) {
    if(CollectionUtils.isEmpty(stats)){
      return;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try{
      for(VehicleServeMonthStat stat : stats){
        writer.saveOrUpdate(stat);
      }
      writer.commit(status);
    }catch(Exception e){
      writer.rollback(status);
    }finally{
      writer.rollback(status);
    }
  }

  @Override
  public VehicleServeMonthStat getVehicleServeMonthStatByBrandModelYearMonth(Long shopId, String brand, String model, int year, int month) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getVehicleServeMonthStatByBrandModelYearMonth(shopId, brand, model, year, month);
  }

  /**
   * 客户车辆消费统计
   * @param bcgogoOrderDto
   * @param orderType
   * @param isRepeal
   */
  public void customerVehicleConsumeStat(BcgogoOrderDto bcgogoOrderDto,OrderTypes orderType, boolean isRepeal,ReceivableDTO receivableDTO) {
    Long orderId = null;
    Long shopId = null;
    Long vehicleId = null;
    Long customerId = null;
    if (orderType == OrderTypes.WASH_BEAUTY) {
      WashBeautyOrderDTO washBeautyOrderDTO = (WashBeautyOrderDTO) bcgogoOrderDto;
      orderId = washBeautyOrderDTO.getId();
      shopId = washBeautyOrderDTO.getShopId();
      vehicleId = washBeautyOrderDTO.getVechicleId();
      customerId = washBeautyOrderDTO.getCustomerId();

    } else if (orderType == OrderTypes.REPAIR) {
      RepairOrderDTO repairOrderDTO = (RepairOrderDTO) bcgogoOrderDto;
      orderId = repairOrderDTO.getId();
      shopId = repairOrderDTO.getShopId();
      vehicleId = repairOrderDTO.getVechicleId();
      customerId = repairOrderDTO.getCustomerId();
    } else if (orderType == OrderTypes.SALE) {
      SalesOrderDTO salesOrderDTO = (SalesOrderDTO) bcgogoOrderDto;
      orderId = salesOrderDTO.getId();
      shopId = salesOrderDTO.getShopId();
      vehicleId = salesOrderDTO.getVehicleId();
      customerId = salesOrderDTO.getCustomerId();
    } else {
      return;
    }

    if (shopId == null || orderId == null || customerId == null || vehicleId == null) {
      return;
    }

    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    if (receivableDTO == null) {
      receivableDTO = txnService.getReceivableDTOByShopIdAndOrderId(shopId, orderId);
    }
    if (receivableDTO == null) {
      LOG.error("vehicleStatService.customerVehicleConsumeStat:shopId" + shopId + ",orderId:" + orderId + "receivable is null");
      return;
    }
    IUserService userService = ServiceManager.getService(IUserService.class);
    CustomerVehicleDTO customerVehicleDTO = userService.getCustomerVehicleDTOByVehicleIdAndCustomerId(vehicleId, customerId);
    if (customerVehicleDTO == null) {
      LOG.error("customerVehicleDTO is null,customerId" + customerId + ",vehicleId:" + vehicleId);
      return;
    }

    customerVehicleDTO.calculateVehicleConsume(receivableDTO,isRepeal);
    List<CustomerVehicleDTO> customerVehicleDTOs = new ArrayList<CustomerVehicleDTO>();
    customerVehicleDTOs.add(customerVehicleDTO);
    userService.saveOrUpdateCustomerVehicle(customerVehicleDTOs);

  }

}
