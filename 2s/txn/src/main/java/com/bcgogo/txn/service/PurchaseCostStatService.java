package com.bcgogo.txn.service;

import com.bcgogo.enums.OrderStatus;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.dto.ProductLocalInfoDTO;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.stat.dto.PurchaseInventoryStatDTO;
import com.bcgogo.stat.dto.SalesStatCondition;
import com.bcgogo.stat.dto.SupplierTranStatDTO;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.model.*;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * 采购统计Service
 * User: Jimuchen
 * Date: 12-10-24
 * Time: 下午5:57
 */
@Component
public class PurchaseCostStatService implements IPurchaseCostStatService {
  private static final Logger LOG = LoggerFactory.getLogger(PurchaseCostStatService.class);

  @Autowired
  private TxnDaoManager txnDaoManager;

  @Override
  public void purchaseCostStat(PurchaseInventoryDTO purchaseInventoryDTO, boolean isRepeal) {
    if (purchaseInventoryDTO == null) {
      return;
    }
    if (purchaseInventoryDTO.getVestDate() == null || ArrayUtils.isEmpty(purchaseInventoryDTO.getItemDTOs()) || purchaseInventoryDTO.getShopId() == null) {
      LOG.error("成本统计时数据异常. PurchaseCostStatService.purchaseCostStat(). purchaseInventoryDTO:{}", purchaseInventoryDTO.toString());
      return;
    }
    Long currentTime = System.currentTimeMillis();
    String vestDateStr = DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY, purchaseInventoryDTO.getVestDate());
    String todayStr = DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY, currentTime);

    saveSupplierTranMonthStat(purchaseInventoryDTO, isRepeal);
    savePurchaseInventoryMonthStat(purchaseInventoryDTO, isRepeal);
    if(!isRepeal && vestDateStr.equals(todayStr)){   //今天的入库单
      savePurchaseInventoryStat(purchaseInventoryDTO);
      saveSupplierTranStat(purchaseInventoryDTO);
    }else{    //不是今天的入库单或者是作废单，放入change
      savePurchaseInventoryStatChange(purchaseInventoryDTO, isRepeal);
      saveSupplierTranStatChange(purchaseInventoryDTO, isRepeal);
    }
  }

  private void savePurchaseInventoryMonthStat(PurchaseInventoryDTO purchaseInventoryDTO, boolean isRepeal) {
    if(ArrayUtil.isEmpty(purchaseInventoryDTO.getItemDTOs())){
         return;
    }
    Long shopId = purchaseInventoryDTO.getShopId();
    TxnWriter writer = txnDaoManager.getWriter();
    int year = DateUtil.getYear(purchaseInventoryDTO.getVestDate());
    int month = DateUtil.getMonth(purchaseInventoryDTO.getVestDate());
    IProductService productService = ServiceManager.getService(IProductService.class);
    Object status = writer.begin();
    try{
      for(PurchaseInventoryItemDTO itemDTO : purchaseInventoryDTO.getItemDTOs()){
        PurchaseInventoryMonthStat stat = writer.getPurchaseInventoryMonthStat(shopId, itemDTO, year, month);
        double total = itemDTO.getTotal();
        double amount = itemDTO.getAmount();

        ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoById(itemDTO.getProductId(), shopId);
        if (UnitUtil.isStorageUnit(itemDTO.getUnit(), productLocalInfoDTO)) {
          amount = amount * productLocalInfoDTO.getRate();
        }

        int times = 1;
        if(isRepeal){
          total = -total;
          amount = -amount;
          times = -times;
        }
        if(stat == null){     //当月统计不存在
          PurchaseInventoryMonthStat purchaseInventoryMonthStat = new PurchaseInventoryMonthStat();
          purchaseInventoryMonthStat.setShopId(shopId);
          purchaseInventoryMonthStat.setStatYear(DateUtil.getCurrentYear());
          purchaseInventoryMonthStat.setStatMonth(DateUtil.getCurrentMonth());
          purchaseInventoryMonthStat.setProductName(StringUtils.defaultIfEmpty(itemDTO.getProductName(), ""));
          purchaseInventoryMonthStat.setProductBrand(StringUtils.defaultIfEmpty(itemDTO.getBrand(), ""));
          purchaseInventoryMonthStat.setVehicleBrand(StringUtils.defaultIfEmpty(itemDTO.getVehicleBrand(), ""));
          purchaseInventoryMonthStat.setVehicleModel(StringUtils.defaultIfEmpty(itemDTO.getVehicleModel(), ""));
          purchaseInventoryMonthStat.setAmount(amount);
          purchaseInventoryMonthStat.setTotal(total);
          purchaseInventoryMonthStat.setTimes(times);
          writer.save(purchaseInventoryMonthStat);
        }else{
          //更新当月的统计记录
          stat.setAmount(stat.getAmount() + amount);
          stat.setTotal(stat.getTotal() + total);
          stat.setTimes(stat.getTimes() + times);
          writer.update(stat);
        }
      }
      writer.commit(status);
    }catch(Exception e){
      LOG.error("保存入库时月成本统计出错. PurchaseCostStatService.savePurchaseInventoryMonthStat. purchaseInventoryDTO:{}", purchaseInventoryDTO);
      LOG.error(e.getMessage(), e);
    }finally{
      writer.rollback(status);
    }
  }

  private void saveSupplierTranMonthStat(PurchaseInventoryDTO purchaseInventoryDTO, boolean repeal) {
    Long shopId = purchaseInventoryDTO.getShopId();
    TxnWriter writer = txnDaoManager.getWriter();
    int year = DateUtil.getYear(purchaseInventoryDTO.getVestDate());
    int month = DateUtil.getMonth(purchaseInventoryDTO.getVestDate());

    Object status = writer.begin();
    double total = purchaseInventoryDTO.getTotal();
    int times = 1;
    if(repeal){
      total = -total;
      times = -times;
    }
    try{
      SupplierTranMonthStat stat = writer.getSupplierTranMonthStat(shopId, purchaseInventoryDTO.getSupplierId(), year, month);
      if(stat == null){     //当月的统计不存在
        SupplierTranMonthStat supplierTranMonthStat = new SupplierTranMonthStat();
        supplierTranMonthStat.setShopId(shopId);
        supplierTranMonthStat.setSupplierId(purchaseInventoryDTO.getSupplierId());
        supplierTranMonthStat.setStatYear(DateUtil.getCurrentYear());
        supplierTranMonthStat.setStatMonth(DateUtil.getCurrentMonth());
        supplierTranMonthStat.setTotal(total);
        supplierTranMonthStat.setTimes(times);
        writer.save(supplierTranMonthStat);
      }else{
        //更新当月的统计记录
        stat.setTotal(stat.getTotal() + total);
        stat.setTimes(stat.getTimes() + times);
        writer.update(stat);
      }
      writer.commit(status);
    }catch(Exception e){
      LOG.error("保存入库时成本统计出错. PurchaseCostStatService.saveSupplierTranStat. purchaseInventoryDTO:{}", purchaseInventoryDTO);
      LOG.error(e.getMessage(), e);
    }finally{
      writer.rollback(status);
    }
  }

  private void savePurchaseInventoryStat(PurchaseInventoryDTO purchaseInventoryDTO) {
    Long shopId = purchaseInventoryDTO.getShopId();
    IProductService productService = ServiceManager.getService(IProductService.class);
    TxnWriter writer = txnDaoManager.getWriter();
    int year = DateUtil.getYear(purchaseInventoryDTO.getVestDate());
    int month = DateUtil.getMonth(purchaseInventoryDTO.getVestDate());
    int day = DateUtil.getDay(purchaseInventoryDTO.getVestDate());

    Object status = writer.begin();
    try {
      for (PurchaseInventoryItemDTO itemDTO : purchaseInventoryDTO.getItemDTOs()) {

        Double amount = itemDTO.getAmount() == null ? 0 : itemDTO.getAmount();
        ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoById(itemDTO.getProductId(), shopId);
        if (UnitUtil.isStorageUnit(itemDTO.getUnit(), productLocalInfoDTO)) {
          amount = amount * productLocalInfoDTO.getRate();
        }

        PurchaseInventoryStat stat = writer.getPurchaseInventoryStat(shopId, itemDTO.getProductId(), year, month, day);
        if (stat == null) {     //今天的统计不存在
          stat = writer.getLatestPurchaseInventoryStatBeforeTime(shopId, itemDTO.getProductId(), DateUtil.getStartTimeOfTimeDay(purchaseInventoryDTO.getVestDate()));
          //根据之前的统计记录生成今天的记录
          PurchaseInventoryStat purchaseInventoryStat = new PurchaseInventoryStat();
          purchaseInventoryStat.setShopId(shopId);
          purchaseInventoryStat.setProductId(itemDTO.getProductId());
          purchaseInventoryStat.setStatYear(DateUtil.getCurrentYear());
          purchaseInventoryStat.setStatMonth(DateUtil.getCurrentMonth());
          purchaseInventoryStat.setStatDay(DateUtil.getCurrentDay());
          purchaseInventoryStat.setStatTime(DateUtil.get6clock(purchaseInventoryDTO.getVestDate()));
          if (stat == null) {
            purchaseInventoryStat.setAmount(amount);
            purchaseInventoryStat.setTotal(itemDTO.getTotal());
            purchaseInventoryStat.setTimes(1);
          } else {
            purchaseInventoryStat.setAmount(stat.getAmount() + amount);
            purchaseInventoryStat.setTotal(stat.getTotal() + itemDTO.getTotal());
            purchaseInventoryStat.setTimes(stat.getTimes() + 1);
          }
          writer.save(purchaseInventoryStat);
        } else {
          //更新今天的统计记录
          stat.setAmount(amount + stat.getAmount());
          stat.setTotal(stat.getTotal() + itemDTO.getTotal());
          stat.setTimes(stat.getTimes() + 1);
          writer.update(stat);
        }
      }
      writer.commit(status);
    } catch (Exception e) {
      LOG.error("保存入库时成本统计出错. PurchaseCostStatService.savePurchaseInventoryStat. purchaseInventoryDTO:{}", purchaseInventoryDTO);
      LOG.error(e.getMessage(), e);
    }finally{
      writer.rollback(status);
    }
  }

  private void savePurchaseInventoryStatChange(PurchaseInventoryDTO purchaseInventoryDTO, boolean isRepeal) {
    Long shopId = purchaseInventoryDTO.getShopId();
    TxnWriter writer = txnDaoManager.getWriter();
    IProductService productService = ServiceManager.getService(IProductService.class);
    int year = DateUtil.getYear(purchaseInventoryDTO.getVestDate());
    int month = DateUtil.getMonth(purchaseInventoryDTO.getVestDate());
    int day = DateUtil.getDay(purchaseInventoryDTO.getVestDate());

    Object status = writer.begin();
    try {
      for (PurchaseInventoryItemDTO itemDTO : purchaseInventoryDTO.getItemDTOs()) {
        PurchaseInventoryStatChange statChange = writer.getPurchaseInventoryStatChange(shopId, itemDTO.getProductId(), year, month, day);
        Long statTime = DateUtil.get6clock(purchaseInventoryDTO.getVestDate());
        double amount = itemDTO.getAmount();
        ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoById(itemDTO.getProductId(), shopId);
        if (UnitUtil.isStorageUnit(itemDTO.getUnit(), productLocalInfoDTO)) {
          amount = amount * productLocalInfoDTO.getRate();
        }

        double total = itemDTO.getTotal();
        int times = 1;
        if(isRepeal){
          amount = -amount;
          total = -total;
          times = -times;
        }
        if(statChange == null){
          PurchaseInventoryStatChange purchaseInventoryStatChange = new PurchaseInventoryStatChange();
          purchaseInventoryStatChange.setShopId(shopId);
          purchaseInventoryStatChange.setProductId(itemDTO.getProductId());
          purchaseInventoryStatChange.setStatYear(DateUtil.getYear(statTime));
          purchaseInventoryStatChange.setStatMonth(DateUtil.getMonth(statTime));
          purchaseInventoryStatChange.setStatDay(DateUtil.getDay(statTime));
          purchaseInventoryStatChange.setStatTime(statTime);

          purchaseInventoryStatChange.setAmount(amount);
          purchaseInventoryStatChange.setTotal(total);
          purchaseInventoryStatChange.setTimes(times);
          writer.save(purchaseInventoryStatChange);
        } else {
          statChange.setAmount(statChange.getAmount() + amount);
          statChange.setTotal(statChange.getTotal() + total);
          statChange.setTimes(statChange.getTimes() + times);
          writer.update(statChange);
        }
      }
      writer.commit(status);
    } catch (Exception e) {
      LOG.error("保存入库时成本统计出错. PurchaseCostStatService.savePurchaseInventoryStatChange. purchaseInventoryDTO:{}", purchaseInventoryDTO);
      LOG.error(e.getMessage(), e);
    }finally{
      writer.rollback(status);
    }
  }

  private void saveSupplierTranStat(PurchaseInventoryDTO purchaseInventoryDTO) {
    Long shopId = purchaseInventoryDTO.getShopId();
    TxnWriter writer = txnDaoManager.getWriter();
    int year = DateUtil.getYear(purchaseInventoryDTO.getVestDate());
    int month = DateUtil.getMonth(purchaseInventoryDTO.getVestDate());
    int day = DateUtil.getDay(purchaseInventoryDTO.getVestDate());

    Object status = writer.begin();
    try {
      SupplierTranStat stat = writer.getSupplierTranStat(shopId, purchaseInventoryDTO.getSupplierId(), year, month, day);
      if (stat == null) {     //今天的统计不存在
        stat = writer.getLatestSupplierTranStatBeforeTime(shopId, purchaseInventoryDTO.getSupplierId(), DateUtil.getStartTimeOfTimeDay(purchaseInventoryDTO.getVestDate()));
        //根据之前的统计记录生成今天的记录
        SupplierTranStat supplierTranStat = new SupplierTranStat();
        supplierTranStat.setShopId(shopId);
        supplierTranStat.setSupplierId(purchaseInventoryDTO.getSupplierId());
        supplierTranStat.setStatYear(DateUtil.getCurrentYear());
        supplierTranStat.setStatMonth(DateUtil.getCurrentMonth());
        supplierTranStat.setStatDay(DateUtil.getCurrentDay());
        supplierTranStat.setStatTime(DateUtil.get6clock(purchaseInventoryDTO.getVestDate()));
        if (stat == null) {
          supplierTranStat.setTotal(purchaseInventoryDTO.getTotal());
          supplierTranStat.setTimes(1);
        } else {
          supplierTranStat.setTotal(stat.getTotal() + purchaseInventoryDTO.getTotal());
          supplierTranStat.setTimes(stat.getTimes() + 1);
        }
        writer.save(supplierTranStat);
      } else {
        //更新今天的统计记录
        stat.setTotal(stat.getTotal() + purchaseInventoryDTO.getTotal());
        stat.setTimes(stat.getTimes() + 1);
        writer.update(stat);
      }
      writer.commit(status);
    } catch (Exception e) {
      LOG.error("保存入库时成本统计出错. PurchaseCostStatService.saveSupplierTranStat. purchaseInventoryDTO:{}", purchaseInventoryDTO);
      LOG.error(e.getMessage(), e);
    }finally{
      writer.rollback(status);
    }
  }

  private void saveSupplierTranStatChange(PurchaseInventoryDTO purchaseInventoryDTO, boolean isRepeal) {
    Long shopId = purchaseInventoryDTO.getShopId();
    TxnWriter writer = txnDaoManager.getWriter();
    int year = DateUtil.getYear(purchaseInventoryDTO.getVestDate());
    int month = DateUtil.getMonth(purchaseInventoryDTO.getVestDate());
    int day = DateUtil.getDay(purchaseInventoryDTO.getVestDate());

    Object status = writer.begin();
    try {
      SupplierTranStatChange statChange = writer.getSupplierTranStatChange(shopId, purchaseInventoryDTO.getSupplierId(), year, month, day);
      Long statTime = DateUtil.get6clock(purchaseInventoryDTO.getVestDate());
      double total = purchaseInventoryDTO.getTotal();
      int times = 1;
      if(isRepeal){
        total = -total;
        times = -times;
      }

      if(statChange == null){
        SupplierTranStatChange newStatChange = new SupplierTranStatChange();
        newStatChange.setShopId(shopId);
        newStatChange.setSupplierId(purchaseInventoryDTO.getSupplierId());
        newStatChange.setTotal(total);
        newStatChange.setTimes(times);
        newStatChange.setStatYear(DateUtil.getYear(statTime));
        newStatChange.setStatMonth(DateUtil.getMonth(statTime));
        newStatChange.setStatDay(DateUtil.getDay(statTime));
        newStatChange.setStatTime(statTime);
        writer.save(newStatChange);
      } else {
        statChange.setTotal(statChange.getTotal() + total);
        statChange.setTimes(statChange.getTimes() + times);
        writer.update(statChange);
      }
      writer.commit(status);
    } catch (Exception e) {
      LOG.error("保存入库时成本统计出错. PurchaseCostStatService.updateTodaySupplierTranStat. purchaseInventoryDTO:{}", purchaseInventoryDTO);
      LOG.error(e.getMessage(), e);
    }finally{
      writer.rollback(status);
    }
  }

  @Override
  public void purchaseReturnStat(PurchaseReturnDTO purchaseReturnDTO,boolean isRepeal) {
    if (purchaseReturnDTO == null) {
      return;
    }
    if (purchaseReturnDTO.getVestDate() == null || ArrayUtils.isEmpty(purchaseReturnDTO.getItemDTOs()) || purchaseReturnDTO.getShopId() == null) {
      LOG.error("成本统计时数据异常，PurchaseCostStatService.purchaseReturnStat(). purchaseReturnDTO:{}", purchaseReturnDTO.toString());
      return;
    }

    //退货时更新supplier_tran_month_stat表，供应商交易月统计表    不再减去
//    newOrUpdateSupplierTranMonthStatForReturn(purchaseReturnDTO);

    //退货时更新puchase_inventory_month_stat表，采购成本月统计表    不再减去
//    newOrUpdatePurchaseInventoryMonthStatForReturn(purchaseReturnDTO);

    savePurchaseReturnStat(purchaseReturnDTO,isRepeal);

    //退货记录记入入库统计change表中，数值为负    不再减去
//    newOrUpdatePurchaseInventoryStatChangeForReturn(purchaseReturnDTO);

    //退货记录记入供应商统计change表中，数值为负    不再减去
//    newOrUpdateSupplierTranStatChangeForReturn(purchaseReturnDTO);
  }

  private void newOrUpdatePurchaseInventoryMonthStatForReturn(PurchaseReturnDTO purchaseReturnDTO) {
    TxnWriter writer = txnDaoManager.getWriter();
    IProductService productService = ServiceManager.getService(IProductService.class);
    Long statTime = purchaseReturnDTO.getVestDate();
    if(statTime == null){
      statTime = purchaseReturnDTO.getCreationDate();
    }
    int year = DateUtil.getYear(statTime);
    int month = DateUtil.getMonth(statTime);
    Long shopId = purchaseReturnDTO.getShopId();
    Object status = writer.begin();
    try{
      for(PurchaseReturnItemDTO itemDTO: purchaseReturnDTO.getItemDTOs()){

        Double amount = itemDTO.getAmount()==null ? 0 : itemDTO.getAmount();
        ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoById(itemDTO.getProductId(), shopId);
        if (UnitUtil.isStorageUnit(itemDTO.getUnit(), productLocalInfoDTO)) {
          amount = amount * productLocalInfoDTO.getRate();
        }
        
        PurchaseInventoryMonthStat purchaseInventoryMonthStat = writer.getPurchaseInventoryMonthStat(shopId, itemDTO, year, month);
        if(purchaseInventoryMonthStat == null){
          purchaseInventoryMonthStat = new PurchaseInventoryMonthStat();
          purchaseInventoryMonthStat.setShopId(shopId);
          purchaseInventoryMonthStat.setAmount(-amount);
          purchaseInventoryMonthStat.setTotal(-itemDTO.getTotal());
          purchaseInventoryMonthStat.setTimes(-1);
          purchaseInventoryMonthStat.setStatYear(DateUtil.getYear(statTime));
          purchaseInventoryMonthStat.setStatMonth(DateUtil.getMonth(statTime));
          purchaseInventoryMonthStat.setProductName(StringUtils.defaultIfEmpty(itemDTO.getProductName(), ""));
          purchaseInventoryMonthStat.setProductBrand(StringUtils.defaultIfEmpty(itemDTO.getBrand(), ""));
          purchaseInventoryMonthStat.setVehicleBrand(StringUtils.defaultIfEmpty(itemDTO.getVehicleBrand(), ""));
          purchaseInventoryMonthStat.setVehicleModel(StringUtils.defaultIfEmpty(itemDTO.getVehicleModel(), ""));
          writer.save(purchaseInventoryMonthStat);
        }else{
          purchaseInventoryMonthStat.setAmount(purchaseInventoryMonthStat.getAmount() - amount);
          purchaseInventoryMonthStat.setTotal(purchaseInventoryMonthStat.getTotal() - itemDTO.getTotal());
          purchaseInventoryMonthStat.setTimes(purchaseInventoryMonthStat.getTimes() - 1);
          writer.update(purchaseInventoryMonthStat);
        }
      }
      writer.commit(status);
    }catch (Exception e){
      LOG.error("退货时更新入库统计表出错。");
      LOG.error(e.getMessage(), e);
    }finally{
      writer.rollback(status);
    }
  }

  /**
   * 保存退货单统计表 包括天统计 月统计
   * @param purchaseReturnDTO
   * @param isRepeal 是否作废
   */
  private void savePurchaseReturnStat(PurchaseReturnDTO purchaseReturnDTO,boolean isRepeal) {
    Long shopId = purchaseReturnDTO.getShopId();
    TxnWriter writer = txnDaoManager.getWriter();
    IProductService productService = ServiceManager.getService(IProductService.class);
    int year = DateUtil.getYear(purchaseReturnDTO.getVestDate());
    int month = DateUtil.getMonth(purchaseReturnDTO.getVestDate());
    int day = DateUtil.getDay(purchaseReturnDTO.getVestDate());

    Object status = writer.begin();
    try {
      for (PurchaseReturnItemDTO itemDTO : purchaseReturnDTO.getItemDTOs()) {
        if(itemDTO == null || itemDTO.getProductId() == null) {
          LOG.error("productCostService.java savePurchaseReturnStat itemDTO.productId为空" + itemDTO.toString());
          continue;
        }

        Double amount = itemDTO.getAmount() == null ? 0 : itemDTO.getAmount();
        Double total = NumberUtil.toReserve(itemDTO.getTotal(),NumberUtil.MONEY_PRECISION);
        ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoById(itemDTO.getProductId(), shopId);
        if(productLocalInfoDTO == null){
          continue;
        }
        if (UnitUtil.isStorageUnit(itemDTO.getUnit(), productLocalInfoDTO)) {
          amount = amount * productLocalInfoDTO.getRate();
        }

        int times = 1;//每次统计次数
        //如果作废 减去相应的统计额
        if(isRepeal){
          amount = 0 - amount;
          total = 0 - total;
          times = -1;
        }

        //保存月退货统计表
        PurchaseReturnMonthStat stat = writer.getPurchaseReturnMonthStat(shopId, purchaseReturnDTO.getSupplierId(), itemDTO.getProductId(), year, month);
        if (stat == null) {     //这个月的统计不存在
          PurchaseReturnMonthStat monthStat = new PurchaseReturnMonthStat();
          monthStat.setShopId(purchaseReturnDTO.getShopId());
          monthStat.setProductId(itemDTO.getProductId());
          monthStat.setSupplierId(purchaseReturnDTO.getSupplierId());
          monthStat.setStatYear(DateUtil.getYear(purchaseReturnDTO.getVestDate()));
          monthStat.setStatMonth(DateUtil.getMonth(purchaseReturnDTO.getVestDate()));
          monthStat.setAmount(amount);
          monthStat.setTotal(total);
          monthStat.setTimes(times);
          writer.save(monthStat);
        } else {
          //这个月的统计值存在
          stat.setAmount(stat.getAmount() + amount);
          stat.setTotal(stat.getTotal() + total);
          stat.setTimes(stat.getTimes() + times);
          if (stat.getAmount() <= 0) {
            writer.delete(stat);
          } else {
            writer.update(stat);
          }
        }

        //保存天退货统计表
        if(DateUtil.isSameDay(System.currentTimeMillis(),purchaseReturnDTO.getVestDate())) {
          PurchaseReturnStat purchaseReturnStat = writer.getPurchaseReturnStat(shopId, purchaseReturnDTO.getSupplierId(), itemDTO.getProductId(), year, month, day);
          if (purchaseReturnStat == null) {
            purchaseReturnStat = writer.getLatestPurchaseReturnStatBeforeTime(shopId, purchaseReturnDTO.getSupplierId(), itemDTO.getProductId(), DateUtil.getStartTimeOfTimeDay(purchaseReturnDTO.getVestDate()));

            PurchaseReturnStat returnStat = new PurchaseReturnStat();
            returnStat.setShopId(purchaseReturnDTO.getShopId());
            returnStat.setSupplierId(purchaseReturnDTO.getSupplierId());
            returnStat.setProductId(itemDTO.getProductId());
            returnStat.setStatYear(year);
            returnStat.setStatMonth(month);
            returnStat.setStatDay(day);
            returnStat.setStatTime(DateUtil.get6clock(purchaseReturnDTO.getVestDate()));

            if (purchaseReturnStat == null) {
              returnStat.setTimes(times);
              returnStat.setAmount(amount);
              returnStat.setTotal(total);
            } else {
              returnStat.setAmount(purchaseReturnStat.getAmount() + amount);
              returnStat.setTotal(purchaseReturnStat.getTotal() + total);
              returnStat.setTimes(purchaseReturnStat.getTimes() + times);
            }
            writer.save(returnStat);
          } else {
            purchaseReturnStat.setAmount(purchaseReturnStat.getAmount() + amount);
            purchaseReturnStat.setTotal(purchaseReturnStat.getTotal() + total);
            purchaseReturnStat.setTimes(purchaseReturnStat.getTimes() + times);
            writer.saveOrUpdate(purchaseReturnStat);
          }
        }else {
          PurchaseReturnStatChange statChange = writer.getPurchaseReturnStatChange(shopId, purchaseReturnDTO.getSupplierId(), itemDTO.getProductId(), year, month, day);
          itemDTO.setTotal(total);
          itemDTO.setAmount(amount);
          statChange = newOrUpdatePurchaseReturnStatChange(statChange, itemDTO, shopId, purchaseReturnDTO.getSupplierId(), DateUtil.get6clock(purchaseReturnDTO.getVestDate()),times);
          writer.saveOrUpdate(statChange);
        }
      }
      writer.commit(status);
    } catch (Exception e) {
      LOG.error("保存退货单时成本统计出错. PurchaseCostStatService.savePurchaseReturnStat. purchaseReturnDTO:{}", purchaseReturnDTO);
      LOG.error(e.getMessage(), e);
      LOG.error("purchaseReturnDTO :" + purchaseReturnDTO.toString());
    }finally{
      writer.rollback(status);
    }
  }

  private void savePurchaseReturnMonthStat(PurchaseReturnDTO purchaseReturnDTO,boolean isRepeal) {
    Long shopId = purchaseReturnDTO.getShopId();
    TxnWriter writer = txnDaoManager.getWriter();
    IProductService productService = ServiceManager.getService(IProductService.class);
    int year = DateUtil.getYear(purchaseReturnDTO.getVestDate());
    int month = DateUtil.getMonth(purchaseReturnDTO.getVestDate());
    int day = DateUtil.getDay(purchaseReturnDTO.getVestDate());

    Object status = writer.begin();
    try {
      for (PurchaseReturnItemDTO itemDTO : purchaseReturnDTO.getItemDTOs()) {
        if(itemDTO.getProductId() == null) {
          LOG.error("productCostService.java savePurchaseReturnStat itemDTO.productId为空" + itemDTO.toString());
          continue;
        }

        Double amount = itemDTO.getAmount() == null ? 0 : itemDTO.getAmount();
        Double total = NumberUtil.toReserve(itemDTO.getTotal(),NumberUtil.MONEY_PRECISION);
        ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoById(itemDTO.getProductId(), shopId);
        if (UnitUtil.isStorageUnit(itemDTO.getUnit(), productLocalInfoDTO)) {
          amount = amount * productLocalInfoDTO.getRate();
        }

        //如果作废 减去相应的统计额
        if(isRepeal){
          amount = 0 - amount;
          total = 0 - total;
        }

        PurchaseReturnMonthStat stat = writer.getPurchaseReturnMonthStat(shopId, purchaseReturnDTO.getSupplierId(), itemDTO.getProductId(), year, month);
        if (stat == null) {     //这个月的统计不存在
          PurchaseReturnMonthStat monthStat = new PurchaseReturnMonthStat();
          monthStat.setShopId(purchaseReturnDTO.getShopId());
          monthStat.setProductId(itemDTO.getProductId());
          monthStat.setSupplierId(purchaseReturnDTO.getSupplierId());
          monthStat.setStatYear(DateUtil.getYear(purchaseReturnDTO.getVestDate()));
          monthStat.setStatMonth(DateUtil.getMonth(purchaseReturnDTO.getVestDate()));
          monthStat.setAmount(amount);
          monthStat.setTotal(total);
          monthStat.setTimes(1);
          writer.save(monthStat);
        } else {
          //更新今天的统计记录
          stat.setAmount(stat.getAmount() + amount);
          stat.setTotal(stat.getTotal() + total);
          stat.setTimes(stat.getTimes() + 1);
          writer.update(stat);
        }
      }
      writer.commit(status);
    } catch (Exception e) {
      LOG.error("保存退货单时成本统计出错. PurchaseCostStatService.savePurchaseReturnStat. purchaseReturnDTO:{}", purchaseReturnDTO);
      LOG.error(e.getMessage(), e);
    }finally{
      writer.rollback(status);
    }
  }


  //不是今天的退货单，放入change
  private void savePurchaseReturnStatChange(PurchaseReturnDTO purchaseReturnDTO) {
    Long shopId = purchaseReturnDTO.getShopId();
    TxnWriter writer = txnDaoManager.getWriter();
    int year = DateUtil.getYear(purchaseReturnDTO.getVestDate());
    int month = DateUtil.getMonth(purchaseReturnDTO.getVestDate());
    int day = DateUtil.getDay(purchaseReturnDTO.getVestDate());

    Object status = writer.begin();
    try {
      for (PurchaseReturnItemDTO itemDTO : purchaseReturnDTO.getItemDTOs()) {
        PurchaseReturnStatChange statChange = writer.getPurchaseReturnStatChange(shopId, purchaseReturnDTO.getSupplierId(), itemDTO.getProductId(), year, month, day);
        statChange = newOrUpdatePurchaseReturnStatChange(statChange, itemDTO, shopId, purchaseReturnDTO.getSupplierId(), DateUtil.get6clock(purchaseReturnDTO.getVestDate()),1);
        writer.saveOrUpdate(statChange);
      }
      writer.commit(status);
    } catch (Exception e) {
      LOG.error("保存退货单时成本统计出错. PurchaseCostStatService.savePurchaseReturnStatChange. purchaseReturnDTO:{}", purchaseReturnDTO);
      LOG.error(e.getMessage(), e);
    }finally{
      writer.rollback(status);
    }
  }

  //根据当天的change记录新建或更新
  private PurchaseReturnStatChange newOrUpdatePurchaseReturnStatChange(PurchaseReturnStatChange existStatChange, PurchaseReturnItemDTO itemDTO, Long shopId, Long supplierId, Long statTime,int times) {
    if (existStatChange == null) {
      PurchaseReturnStatChange changeRec = new PurchaseReturnStatChange();
      changeRec.setShopId(shopId);
      changeRec.setSupplierId(supplierId);
      changeRec.setProductId(itemDTO.getProductId());
      changeRec.setAmount(itemDTO.getAmount());
      changeRec.setTotal(itemDTO.getTotal());
      changeRec.setTimes(times);
      changeRec.setStatYear(DateUtil.getYear(statTime));
      changeRec.setStatMonth(DateUtil.getMonth(statTime));
      changeRec.setStatDay(DateUtil.getDay(statTime));
      changeRec.setStatTime(statTime);
      return changeRec;
    } else {
      existStatChange.setAmount(itemDTO.getAmount() + existStatChange.getAmount());
      existStatChange.setTotal(existStatChange.getTotal() + itemDTO.getTotal());
      existStatChange.setTimes(existStatChange.getTimes() + times);
      return existStatChange;
    }
  }


  //退货记录记入入库统计change表中，数值为负
  private void newOrUpdatePurchaseInventoryStatChangeForReturn(PurchaseReturnDTO purchaseReturnDTO) {
    TxnWriter writer = txnDaoManager.getWriter();
    IProductService productService = ServiceManager.getService(IProductService.class);
    Long statTime = DateUtil.get6clock(purchaseReturnDTO.getVestDate());
    if(statTime == null){
      statTime = DateUtil.get6clock(purchaseReturnDTO.getCreationDate());
    }
    int year = DateUtil.getYear(statTime);
    int month = DateUtil.getMonth(statTime);
    int day = DateUtil.getDay(statTime);
    Long shopId = purchaseReturnDTO.getShopId();
    for(PurchaseReturnItemDTO itemDTO: purchaseReturnDTO.getItemDTOs()){
      Double amount = itemDTO.getAmount() == null ? 0 : itemDTO.getAmount();
      ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoById(itemDTO.getProductId(), shopId);
      if (UnitUtil.isStorageUnit(itemDTO.getUnit(), productLocalInfoDTO)) {
        amount = amount * productLocalInfoDTO.getRate();
      }

      PurchaseInventoryStatChange existStatChange = writer.getPurchaseInventoryStatChange(shopId, itemDTO.getProductId(), year, month, day);
      if(existStatChange == null){
        PurchaseInventoryStatChange changeRec = new PurchaseInventoryStatChange();
        changeRec.setShopId(shopId);
        changeRec.setProductId(itemDTO.getProductId());
        changeRec.setAmount(-amount);
        changeRec.setTotal(-itemDTO.getTotal());
        changeRec.setTimes(-1);
        changeRec.setStatYear(DateUtil.getYear(statTime));
        changeRec.setStatMonth(DateUtil.getMonth(statTime));
        changeRec.setStatDay(DateUtil.getDay(statTime));
        changeRec.setStatTime(statTime);
        writer.save(changeRec);
      }else{
        existStatChange.setAmount(existStatChange.getAmount() - amount);
        existStatChange.setTotal(existStatChange.getTotal() - itemDTO.getTotal());
        existStatChange.setTimes(existStatChange.getTimes() - 1);
        writer.update(existStatChange);
      }
    }
  }

  //退货时更新supplier_tran_month_stat表，供应商交易月统计表
  private void newOrUpdateSupplierTranMonthStatForReturn(PurchaseReturnDTO purchaseReturnDTO){
    Long shopId = purchaseReturnDTO.getShopId();
    Long statTime = DateUtil.get6clock(purchaseReturnDTO.getVestDate());
    if(statTime == null){
      statTime = DateUtil.get6clock(purchaseReturnDTO.getCreationDate());
    }
    int year = DateUtil.getYear(statTime);
    int month = DateUtil.getMonth(statTime);
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try{
      SupplierTranMonthStat existStatChange = writer.getSupplierTranMonthStat(shopId, purchaseReturnDTO.getSupplierId(), year, month);
      if (existStatChange == null) {
        SupplierTranMonthStat monthStat = new SupplierTranMonthStat();
        monthStat.setShopId(shopId);
        monthStat.setSupplierId(purchaseReturnDTO.getSupplierId());
        monthStat.setTotal(-purchaseReturnDTO.getTotal());
        monthStat.setTimes(-1);
        monthStat.setStatYear(DateUtil.getYear(statTime));
        monthStat.setStatMonth(DateUtil.getMonth(statTime));
        writer.save(monthStat);
      } else {
        existStatChange.setTotal(existStatChange.getTotal() - purchaseReturnDTO.getTotal());
        existStatChange.setTimes(existStatChange.getTimes() - 1);
        writer.update(existStatChange);
      }
      writer.commit(status);
    }catch (Exception e){
      LOG.error("退货时更新供应商交易统计表出错。", e.getMessage());
      LOG.error(e.getMessage(), e);
    }finally{
      writer.rollback(status);
    }
  }

  //退货记录记入供应商统计change表中，数值为负
  private void newOrUpdateSupplierTranStatChangeForReturn(PurchaseReturnDTO purchaseReturnDTO) {
    Long shopId = purchaseReturnDTO.getShopId();
    Long statTime = DateUtil.get6clock(purchaseReturnDTO.getVestDate());
    if(statTime == null){
      statTime = DateUtil.get6clock(purchaseReturnDTO.getCreationDate());
    }
    int year = DateUtil.getYear(statTime);
    int month = DateUtil.getMonth(statTime);
    int day = DateUtil.getDay(statTime);
    TxnWriter writer = txnDaoManager.getWriter();
    SupplierTranStatChange existStatChange = writer.getSupplierTranStatChange(shopId, purchaseReturnDTO.getSupplierId(), year, month, day);
    if (existStatChange == null) {
      SupplierTranStatChange changeRec = new SupplierTranStatChange();
      changeRec.setShopId(shopId);
      changeRec.setSupplierId(purchaseReturnDTO.getSupplierId());
      changeRec.setTotal(-purchaseReturnDTO.getTotal());
      changeRec.setTimes(-1);
      changeRec.setStatYear(DateUtil.getYear(statTime));
      changeRec.setStatMonth(DateUtil.getMonth(statTime));
      changeRec.setStatDay(DateUtil.getDay(statTime));
      changeRec.setStatTime(statTime);
      writer.save(changeRec);
    } else {
      existStatChange.setTotal(existStatChange.getTotal() - purchaseReturnDTO.getTotal());
      existStatChange.setTimes(existStatChange.getTimes() - 1);
      writer.update(existStatChange);
    }
  }

  @Override
  public void salesStat(BcgogoOrderDto bcgogoOrderDto, OrderStatus orderStatus) {
    if (bcgogoOrderDto == null || ArrayUtils.isEmpty(bcgogoOrderDto.getItemDTOs())) {
      return;
    }
    if (bcgogoOrderDto.getVestDate() == null ||  bcgogoOrderDto.getShopId() == null) {
      LOG.error("销售统计时数据异常，PurchaseCostStatService.salesStat(). bcgogoOrderDto:{}", bcgogoOrderDto.toString());
      return;
    }
    Long currentTime = System.currentTimeMillis();
    String vestDateStr = DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY, bcgogoOrderDto.getVestDate());
    String todayStr = DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY, currentTime);

    boolean isRepeal = false;
    if (orderStatus == OrderStatus.REPAIR_REPEAL || orderStatus == OrderStatus.SALE_REPEAL) {
      isRepeal = true;
    }
    if (vestDateStr.equals(todayStr) && !isRepeal) {   //今天的销售单
      saveSalesStat(bcgogoOrderDto);
    } else {    //不是今天的销售单，放入change
      saveSalesStatChange(bcgogoOrderDto, isRepeal);
    }

    //保存月销售单记录
    saveSalesMonthStat(bcgogoOrderDto, isRepeal);
  }

  private void saveSalesStat(BcgogoOrderDto bcgogoOrderDto) {
    Long shopId = bcgogoOrderDto.getShopId();
    TxnWriter writer = txnDaoManager.getWriter();
    IProductService productService = ServiceManager.getService(IProductService.class);
    int year = DateUtil.getYear(bcgogoOrderDto.getVestDate());
    int month = DateUtil.getMonth(bcgogoOrderDto.getVestDate());
    int day = DateUtil.getDay(bcgogoOrderDto.getVestDate());

    int times = 1;//默认增加一次
    Object status = writer.begin();
    try {
      for (BcgogoOrderItemDto itemDTO : bcgogoOrderDto.getItemDTOs()) {
        if (itemDTO == null) {
          continue;
        }
        double amount = 0d;
        double total = 0d;
        ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoById(itemDTO.getProductId(), shopId);
        if (productLocalInfoDTO == null) {
          continue;
        }

        if (itemDTO instanceof SalesOrderItemDTO) {
          SalesOrderItemDTO salesItemDTO = (SalesOrderItemDTO) itemDTO;
          amount = salesItemDTO.getAmount();
          if (UnitUtil.isStorageUnit(itemDTO.getUnit(), productLocalInfoDTO)) {
            amount = amount * productLocalInfoDTO.getRate();
          }
          total = salesItemDTO.getTotal();
        } else if (itemDTO instanceof RepairOrderItemDTO) {
          RepairOrderItemDTO repairItemDTO = (RepairOrderItemDTO) itemDTO;
          amount = repairItemDTO.getAmount();
          if (UnitUtil.isStorageUnit(itemDTO.getUnit(), productLocalInfoDTO)) {
            amount = amount * productLocalInfoDTO.getRate();
          }
          total = repairItemDTO.getTotal();
        }
        SalesStat stat = writer.getSalesStat(shopId, itemDTO.getProductId(), year, month, day);
        if (stat == null) {     //今天的统计不存在
          stat = writer.getLatestSalesStatBeforeTime(shopId, itemDTO.getProductId(), DateUtil.getStartTimeOfTimeDay(bcgogoOrderDto.getVestDate()));
          //根据之前的统计记录生成今天的记录
          SalesStat salesStat = new SalesStat();
          salesStat.setShopId(shopId);
          salesStat.setProductId(itemDTO.getProductId());
          salesStat.setStatYear(DateUtil.getCurrentYear());
          salesStat.setStatMonth(DateUtil.getCurrentMonth());
          salesStat.setStatDay(DateUtil.getCurrentDay());
          salesStat.setStatTime(DateUtil.get6clock(bcgogoOrderDto.getVestDate()));
          if (stat == null) {
            salesStat.setAmount(amount);
            salesStat.setTotal(total);
            salesStat.setTimes(times);
          } else {
            salesStat.setAmount(stat.getAmount() + amount);
            salesStat.setTotal(stat.getTotal() + total);
            salesStat.setTimes(stat.getTimes() + times);
          }
          writer.saveOrUpdate(salesStat);
        } else {
          //更新今天的统计记录
          stat.setAmount(stat.getAmount() + amount);
          stat.setTotal(stat.getTotal() + total);
          stat.setTimes(stat.getTimes() + times);
          writer.saveOrUpdate(stat);
        }
      }
      writer.commit(status);
    } catch (Exception e) {
      LOG.error("保存销售单时成本统计出错. PurchaseCostStatService.saveSalesStat. bcgogoOrderDto:{}", bcgogoOrderDto);
      LOG.error(e.getMessage(), e);
    }finally{
      writer.rollback(status);
    }
  }

  private void saveSalesStatChange(BcgogoOrderDto bcgogoOrderDto, boolean isRepeal) {
    Long shopId = bcgogoOrderDto.getShopId();
    TxnWriter writer = txnDaoManager.getWriter();
    IProductService productService = ServiceManager.getService(IProductService.class);
    int year = DateUtil.getYear(bcgogoOrderDto.getVestDate());
    int month = DateUtil.getMonth(bcgogoOrderDto.getVestDate());
    int day = DateUtil.getDay(bcgogoOrderDto.getVestDate());

    int times = 1;//默认增加一次
    Object status = writer.begin();
    try {
      for (BcgogoOrderItemDto itemDTO : bcgogoOrderDto.getItemDTOs()) {
        if(itemDTO == null){
          continue;
        }
        double amount = 0d;
        double total = 0d;
        ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoById(itemDTO.getProductId(), shopId);
        if(productLocalInfoDTO == null){
          continue;
        }
        if (itemDTO instanceof SalesOrderItemDTO) {
          SalesOrderItemDTO salesItemDTO = (SalesOrderItemDTO) itemDTO;
          if (isRepeal) {
            times = -1;
            amount = 0 - salesItemDTO.getAmount();
            if (UnitUtil.isStorageUnit(itemDTO.getUnit(), productLocalInfoDTO)) {
              amount = amount * productLocalInfoDTO.getRate();
            }
            total = 0 - salesItemDTO.getTotal();
          } else {
            amount = salesItemDTO.getAmount();
            if (UnitUtil.isStorageUnit(itemDTO.getUnit(), productLocalInfoDTO)) {
              amount = amount * productLocalInfoDTO.getRate();
            }
            total = salesItemDTO.getTotal();
          }
        } else if (itemDTO instanceof RepairOrderItemDTO) {
          RepairOrderItemDTO repairItemDTO = (RepairOrderItemDTO) itemDTO;
          if (isRepeal) {
            times = -1;
            amount = 0 - repairItemDTO.getAmount();
            if (UnitUtil.isStorageUnit(itemDTO.getUnit(), productLocalInfoDTO)) {
              amount = amount * productLocalInfoDTO.getRate();
            }
            total = 0 - repairItemDTO.getTotal();
          } else {
            amount = repairItemDTO.getAmount();
            if (UnitUtil.isStorageUnit(itemDTO.getUnit(), productLocalInfoDTO)) {
              amount = amount * productLocalInfoDTO.getRate();
            }
            total = repairItemDTO.getTotal();
          }
        }
        SalesStatChange existStatChange = writer.getSalesStatChange(shopId, itemDTO.getProductId(), year, month, day);
        Long statTime = DateUtil.get6clock(bcgogoOrderDto.getVestDate());
        if (existStatChange == null) {
          SalesStatChange changeRec = new SalesStatChange();
          changeRec.setShopId(shopId);
          changeRec.setProductId(itemDTO.getProductId());
          changeRec.setAmount(amount);
          changeRec.setTotal(total);
          changeRec.setTimes(times);
          changeRec.setStatYear(DateUtil.getYear(statTime));
          changeRec.setStatMonth(DateUtil.getMonth(statTime));
          changeRec.setStatDay(DateUtil.getDay(statTime));
          changeRec.setStatTime(statTime);
          writer.save(changeRec);
        } else {
          existStatChange.setAmount(amount + existStatChange.getAmount());
          existStatChange.setTotal(existStatChange.getTotal() + total);
          existStatChange.setTimes(existStatChange.getTimes() + times);
          writer.update(existStatChange);
        }
      }
      writer.commit(status);
    } catch (Exception e) {
      LOG.error("保存销售单时成本统计出错. PurchaseCostStatService.saveSalesStatChange. bcgogoOrderDto:{}", bcgogoOrderDto);
      LOG.error(e.getMessage(), e);
    }finally{
      writer.rollback(status);
    }
  }

  @Override
  public PurchaseInventoryStatDTO queryCostStat(Long shopId, int year, int month, boolean allYear, PurchaseInventoryStatDTO purchaseInventoryStatDTO) {
    List<Long> ids = purchaseInventoryStatDTO.getProductIds();
    long fromTime = 0;
    long endTime = 0;
    if(allYear){
      Calendar calendar = Calendar.getInstance();
      calendar.clear();
      calendar.set(year, 0, 1, 0, 0, 0);
      fromTime = calendar.getTimeInMillis();
      calendar.add(Calendar.YEAR, 1);
      endTime = calendar.getTimeInMillis();
    }else{
      Calendar calendar = Calendar.getInstance();
      calendar.clear();
      calendar.set(year, month-1, 1, 0, 0, 0);
      fromTime = calendar.getTimeInMillis();
      calendar.add(Calendar.MONTH, 1);
      endTime = calendar.getTimeInMillis();
    }

    for(Long productId : ids){
      PurchaseInventoryStat stat = getCostStat(shopId, productId, fromTime, endTime);
      PurchaseInventoryStatChange statChange = getCostStatChange(shopId, productId, fromTime, endTime);

      double amount = purchaseInventoryStatDTO.getAmount() + (stat==null?0:stat.getAmount()) + (statChange==null?0:statChange.getAmount());
      purchaseInventoryStatDTO.setAmount(NumberUtil.round(amount, 1));
      double total = purchaseInventoryStatDTO.getTotal() + (stat==null?0:stat.getTotal()) + (statChange==null?0:statChange.getTotal());
      purchaseInventoryStatDTO.setTotal(NumberUtil.round(total, NumberUtil.MONEY_PRECISION));
    }

    return purchaseInventoryStatDTO;
  }

  public PurchaseInventoryStat getCostStat(Long shopId, Long productId, long fromTime, long endTime) {
    TxnWriter writer = txnDaoManager.getWriter();
    PurchaseInventoryStat stat = writer.getLatestPurchaseInventoryStatInRange(shopId, productId, fromTime, endTime);
    if(stat == null){
      stat = new PurchaseInventoryStat();
      stat.setAmount(0);
      stat.setTimes(0);
      stat.setTotal(0);
      return stat;
    }
    PurchaseInventoryStat previousStat = writer.getLatestInventoryStatBeforeTime(shopId, productId, fromTime);
    if(previousStat == null){
      return stat;
    }else{
      PurchaseInventoryStat result = new PurchaseInventoryStat();
      result.setAmount(stat.getAmount()-previousStat.getAmount());
      result.setTimes(stat.getTimes() - previousStat.getTimes());
      result.setTotal(stat.getTotal() - previousStat.getTotal());
      return result;
    }
  }

  public PurchaseInventoryStatChange getCostStatChange(Long shopId, Long productId, long fromTime, long endTime) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getPurchaseInventoryStatChangeInRange(shopId, productId, fromTime, endTime);
  }

  @Override
  public SupplierTranStatDTO querySupplierTranStat(Long shopId, int year, int month, boolean allYear, Long supplierId) {
    long fromTime = 0;
    long endTime = 0;
    if(allYear){
      Calendar calendar = Calendar.getInstance();
      calendar.clear();
      calendar.set(year, 0, 1, 0, 0, 0);
      fromTime = calendar.getTimeInMillis();
      calendar.add(Calendar.YEAR, 1);
      endTime = calendar.getTimeInMillis();
    }else{
      Calendar calendar = Calendar.getInstance();
      calendar.clear();
      calendar.set(year, month-1, 1, 0, 0, 0);
      fromTime = calendar.getTimeInMillis();
      calendar.add(Calendar.MONTH, 1);
      endTime = calendar.getTimeInMillis();
    }
    SupplierTranStatDTO supplierTranStatDTO = new SupplierTranStatDTO();

    SupplierTranStat stat = getSupplierTranStat(shopId, supplierId, fromTime, endTime);
    SupplierTranStatChange statChange = getSupplierTranStatChange(shopId, supplierId, fromTime, endTime);

    double total = 0d;
    int times = 0;

    times = times + (stat==null?0:stat.getTimes()) + (statChange==null?0:statChange.getTimes());
    supplierTranStatDTO.setTimes(times);
    total = total + (stat==null?0:stat.getTotal()) + (statChange==null?0:statChange.getTotal());
    supplierTranStatDTO.setTotal(NumberUtil.round(total, NumberUtil.MONEY_PRECISION));

    return supplierTranStatDTO;
  }

  private SupplierTranStat getSupplierTranStat(Long shopId, Long supplierId, long fromTime, long endTime) {
    TxnWriter writer = txnDaoManager.getWriter();
    SupplierTranStat stat = writer.getLatestSupplierTranStatInRange(shopId, supplierId, fromTime, endTime);
    if(stat == null){
      stat = new SupplierTranStat();
      stat.setTimes(0);
      stat.setTotal(0);
      return stat;
    }
    SupplierTranStat previousStat = writer.getLatestSupplierTranStatBeforeTime(shopId, supplierId, fromTime);
    if(previousStat == null){
      return stat;
    }else{
      SupplierTranStat result = new SupplierTranStat();
      result.setTimes(stat.getTimes() - previousStat.getTimes());
      result.setTotal(stat.getTotal() - previousStat.getTotal());
      return result;
    }
  }

  private SupplierTranStatChange getSupplierTranStatChange(Long shopId, Long supplierId, long fromTime, long endTime) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getSupplierTranStatChangeInRange(shopId, supplierId, fromTime, endTime);
  }

  @Override
  public List<SupplierTranStatDTO> queryTopSupplierTranMonthStat(Long shopId, int year, int month, boolean allYear, int topLimit) {
    TxnWriter writer = txnDaoManager.getWriter();
    IUserService userService = ServiceManager.getService(IUserService.class);
    List<SupplierTranMonthStat> monthStats = writer.queryTopSupplierTranMonthStat(shopId, year, month, allYear, topLimit);
    if(CollectionUtils.isEmpty(monthStats)){
      return new ArrayList<SupplierTranStatDTO>();
    }
    List<SupplierTranStatDTO> result = new ArrayList<SupplierTranStatDTO>();
    for(SupplierTranMonthStat stat : monthStats){
      SupplierDTO supplierDTO = userService.getSupplierById(stat.getSupplierId());
      SupplierTranStatDTO statDTO = new SupplierTranStatDTO();
      statDTO.setSupplierName(supplierDTO.getName());
      statDTO.setTimes(stat.getTimes());
      statDTO.setTotal(stat.getTotal());
      result.add(statDTO);
    }
    return result;
  }

  @Override
  public double querySupplierTranTotal(Long shopId, int year, int month, boolean allYear) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.querySupplierTranTotal(shopId, year, month, allYear);
  }

  @Override
  public List<PurchaseInventoryStatDTO> queryTopPurchaseInventoryMonthStat(Long shopId, int year, int month, boolean allYear, String[] queryFields, int topLimit) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<PurchaseInventoryMonthStat> monthStats = writer.queryTopPurchaseInventoryMonthStat(shopId, year, month, allYear, queryFields, topLimit);
    if(CollectionUtils.isEmpty(monthStats)){
      return new ArrayList<PurchaseInventoryStatDTO>();
    }
    List<PurchaseInventoryStatDTO> result = new ArrayList<PurchaseInventoryStatDTO>();
    for(PurchaseInventoryMonthStat stat : monthStats){
      result.add(stat.toDTO());
    }
    return result;
  }

  @Override
  public double queryPurchaseInventoryTotal(Long shopId, int year, int month, boolean allYear) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.queryPurchaseInventoryTotal(shopId, year, month, allYear);
  }

  @Override
  public List<PurchaseInventoryMonthStat> getPurchaseInventoryMonthStatByProperties(Long shopId, String name, String brand, String vehicleBrand, String vehicleModel) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getPurchaseInventoryMonthStatByProperties(shopId, name, brand, vehicleBrand, vehicleModel);
  }

  @Override
  public List<PurchaseInventoryMonthStat> purchaseCostStatForProductInRange(Long shopId, Long productId, long begin, long end) throws Exception{
    TxnWriter writer = txnDaoManager.getWriter();
    List<PurchaseInventoryDTO> dtos = ServiceManager.getService(ITxnService.class).getPurchaseInventoryDTOByCreationDate(shopId, begin, end);

    if(CollectionUtils.isEmpty(dtos)){
      return null;
    }
    Map<String, PurchaseInventoryMonthStat> yearMonthStatsMap = new HashMap<String, PurchaseInventoryMonthStat>();

    for(PurchaseInventoryDTO purchaseInventoryDTO: dtos){
      if(ArrayUtils.isEmpty(purchaseInventoryDTO.getItemDTOs())){
        continue;
      }

      for(PurchaseInventoryItemDTO itemDTO : purchaseInventoryDTO.getItemDTOs()){
        ProductDTO product = ServiceManager.getService(IProductService.class).getProductByProductLocalInfoId(productId, shopId);
        if(product == null || !productId.equals(itemDTO.getProductId())){
          continue;
        }
        Double amount = itemDTO.getAmount()==null?0:itemDTO.getAmount();
        ProductLocalInfoDTO productLocalInfoDTO = ServiceManager.getService(IProductService.class).getProductLocalInfoById(itemDTO.getProductId(), shopId);
        if (UnitUtil.isStorageUnit(itemDTO.getUnit(), productLocalInfoDTO)) {
          amount = amount * productLocalInfoDTO.getRate();
        }
        int year = DateUtil.getYear(purchaseInventoryDTO.getVestDate());
        int month = DateUtil.getMonth(purchaseInventoryDTO.getVestDate());
        String key = String.valueOf(year)+"_"+String.valueOf(month);
        PurchaseInventoryMonthStat purchaseInventoryMonthStat = yearMonthStatsMap.get(key);
        if(purchaseInventoryMonthStat == null){
          purchaseInventoryMonthStat = new PurchaseInventoryMonthStat();
          purchaseInventoryMonthStat.setShopId(shopId);
          purchaseInventoryMonthStat.setProductName(StringUtils.defaultIfEmpty(product.getName(), ""));
          purchaseInventoryMonthStat.setProductBrand(StringUtils.defaultIfEmpty(product.getBrand(), ""));
          purchaseInventoryMonthStat.setVehicleBrand(StringUtils.defaultIfEmpty(product.getVehicleBrand(), ""));
          purchaseInventoryMonthStat.setVehicleModel(StringUtils.defaultIfEmpty(product.getVehicleModel(), ""));
          purchaseInventoryMonthStat.setStatYear(DateUtil.getYear(purchaseInventoryDTO.getVestDate()));
          purchaseInventoryMonthStat.setStatMonth(DateUtil.getMonth(purchaseInventoryDTO.getVestDate()));
          purchaseInventoryMonthStat.setAmount(amount);
          purchaseInventoryMonthStat.setTotal(itemDTO.getTotal());
          purchaseInventoryMonthStat.setTimes(1);
          yearMonthStatsMap.put(key, purchaseInventoryMonthStat);
        }else{
          purchaseInventoryMonthStat.setAmount(purchaseInventoryMonthStat.getAmount() + amount);
          purchaseInventoryMonthStat.setTotal(purchaseInventoryMonthStat.getTotal() + itemDTO.getTotal());
          purchaseInventoryMonthStat.setTimes(purchaseInventoryMonthStat.getTimes() + 1);
        }
      }
    }

    Set<String> keySet = yearMonthStatsMap.keySet();
    List<PurchaseInventoryMonthStat> list = new ArrayList<PurchaseInventoryMonthStat>();
    for(String key:keySet){
      PurchaseInventoryMonthStat purchaseInventoryMonthStat = yearMonthStatsMap.get(key);
      list.add(purchaseInventoryMonthStat);
    }
    return list;
  }

  @Override
  public void batchSaveOrUpdateInventoryCostMonthStat(List<PurchaseInventoryMonthStat> stats) {
    if(CollectionUtils.isEmpty(stats)){
      return;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try{
      for(PurchaseInventoryMonthStat stat : stats){
        writer.saveOrUpdate(stat);
      }
      writer.commit(status);
    }catch(Exception e){
      writer.rollback(status);
    }finally{
      writer.rollback(status);
    }
  }

  private void saveSalesMonthStat(BcgogoOrderDto bcgogoOrderDto, boolean isRepeal) {

    Long shopId = bcgogoOrderDto.getShopId();
    TxnWriter writer = txnDaoManager.getWriter();
    IProductService productService = ServiceManager.getService(IProductService.class);
    int year = DateUtil.getYear(bcgogoOrderDto.getVestDate());
    int month = DateUtil.getMonth(bcgogoOrderDto.getVestDate());
    int day = DateUtil.getDay(bcgogoOrderDto.getVestDate());

    int times = 1;
    if (isRepeal) {
      times = -1;
    }

    Object status = writer.begin();
    try {
      for (BcgogoOrderItemDto itemDTO : bcgogoOrderDto.getItemDTOs()) {
        if(itemDTO == null){
          continue;
        }
        double amount = 0d;
        double total = 0d;
        ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoById(itemDTO.getProductId(), shopId);
        if(productLocalInfoDTO == null){
          continue;
        }
        if (itemDTO instanceof SalesOrderItemDTO) {
          SalesOrderItemDTO salesItemDTO = (SalesOrderItemDTO) itemDTO;
          if (isRepeal) {
            amount = 0 - salesItemDTO.getAmount();
            if (UnitUtil.isStorageUnit(itemDTO.getUnit(), productLocalInfoDTO)) {
              amount = amount * productLocalInfoDTO.getRate();
            }
            total = 0 - salesItemDTO.getTotal();
          } else {
            amount = salesItemDTO.getAmount();
            if (UnitUtil.isStorageUnit(itemDTO.getUnit(), productLocalInfoDTO)) {
              amount = amount * productLocalInfoDTO.getRate();
            }
            total = salesItemDTO.getTotal();
          }
        } else if (itemDTO instanceof RepairOrderItemDTO) {
          RepairOrderItemDTO repairItemDTO = (RepairOrderItemDTO) itemDTO;
          if (isRepeal) {
            amount = 0 - repairItemDTO.getAmount();
            if (UnitUtil.isStorageUnit(itemDTO.getUnit(), productLocalInfoDTO)) {
              amount = amount * productLocalInfoDTO.getRate();
            }
            total = 0 - repairItemDTO.getTotal();
          } else {
            amount = repairItemDTO.getAmount();
            if (UnitUtil.isStorageUnit(itemDTO.getUnit(), productLocalInfoDTO)) {
              amount = amount * productLocalInfoDTO.getRate();
            }
            total = repairItemDTO.getTotal();
          }
        }
        List<SalesMonthStat> salesMonthStatList = writer.getSalesMonthStatByYearMonth(shopId, itemDTO.getProductId(), year, month);
        Long statTime = DateUtil.get6clock(bcgogoOrderDto.getVestDate());

        if (CollectionUtils.isEmpty(salesMonthStatList)) {
          SalesMonthStat salesMonthStat = new SalesMonthStat();
          salesMonthStat.setShopId(shopId);
          salesMonthStat.setProductId(itemDTO.getProductId());
          salesMonthStat.setAmount(amount);
          salesMonthStat.setTotal(total);
          salesMonthStat.setTimes(times);
          salesMonthStat.setStatYear(DateUtil.getYear(statTime));
          salesMonthStat.setStatMonth(DateUtil.getMonth(statTime));
          salesMonthStat.setStatDay(DateUtil.getDay(statTime));
          salesMonthStat.setStatTime(statTime);
          writer.save(salesMonthStat);
        } else {
          SalesMonthStat salesMonthStat = salesMonthStatList.get(0);
          salesMonthStat.setAmount(amount + salesMonthStat.getAmount());
          salesMonthStat.setTotal(salesMonthStat.getTotal() + total);
          salesMonthStat.setTimes(salesMonthStat.getTimes() + times);
          if (salesMonthStat.getAmount() <= 0) {
            writer.delete(salesMonthStat);
          } else {
            writer.update(salesMonthStat);
          }
          if (salesMonthStatList.size() > 1) {
            LOG.error("PurchaseCostStatService.java method=saveSalesMonthStat ");
            LOG.error(" shopID:" + bcgogoOrderDto.getShopId() + "year:" + year + "month:" + month + "product:" + itemDTO.getProductId() + "在 salesMonthStat 表有一条以上记录");
          }
        }
      }
      writer.commit(status);
    } catch (Exception e) {
      LOG.error("保存销售单畅销商品出错. PurchaseCostStatService.saveSalesStatChange. bcgogoOrderDto:{}", bcgogoOrderDto);
      LOG.error(e.getMessage(), e);
    }finally{
      writer.rollback(status);
    }

  }

  @Override
  public List<ProductDTO> querySalesStatByCondition(Long shopId, SalesStatCondition salesStatCondition) {
    if (shopId == null || salesStatCondition == null) {
      return null;
    }

    TxnWriter txnWriter = txnDaoManager.getWriter();
    List<ProductDTO> dtoList = txnWriter.querySalesStatByCondition(shopId, salesStatCondition);
    if (CollectionUtils.isEmpty(dtoList)) {
      return null;
    }
    return dtoList;
  }

  public List<String> countSalesStatByCondition(Long shopId,SalesStatCondition salesStatCondition) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.countSalesStatByCondition(shopId, salesStatCondition);
  }


  @Override
  public List<ProductDTO> queryBadSalesStatByCondition(Long shopId, SalesStatCondition salesStatCondition) {
    if (shopId == null || salesStatCondition == null) {
      return null;
    }
    IProductService productService = ServiceManager.getService(IProductService.class);

    TxnWriter txnWriter = txnDaoManager.getWriter();
    List<ProductDTO> dtoList = txnWriter.queryBadSalesStatByCondition(shopId, salesStatCondition);
    if (CollectionUtils.isEmpty(dtoList)) {
      return null;
    }
    return dtoList;
  }

  @Override
  public PurchaseInventoryMonthStat purchaseReturnCostStatForProductInRange(Long shopId, Long productId, long begin, long end) throws Exception{
    TxnWriter writer = txnDaoManager.getWriter();
    IProductService productService = ServiceManager.getService(IProductService.class);
    List<PurchaseReturnDTO> dtos = ServiceManager.getService(ITxnService.class).getPurchaseReturnDTOByVestDate(shopId, begin, end);
    PurchaseInventoryMonthStat purchaseInventoryMonthStat = new PurchaseInventoryMonthStat();
    purchaseInventoryMonthStat.setShopId(shopId);
    purchaseInventoryMonthStat.setAmount(0);
    purchaseInventoryMonthStat.setTotal(0);
    purchaseInventoryMonthStat.setTimes(0);

    if(CollectionUtils.isEmpty(dtos)){
      return null;
    }

    for(PurchaseReturnDTO purchaseReturnDTO: dtos){
      if(ArrayUtils.isEmpty(purchaseReturnDTO.getItemDTOs())){
        continue;
      }

      for(PurchaseReturnItemDTO itemDTO : purchaseReturnDTO.getItemDTOs()){
        ProductDTO product = ServiceManager.getService(IProductService.class).getProductByProductLocalInfoId(productId, shopId);
        if(product == null || !productId.equals(itemDTO.getProductId())){
          continue;
        }

        Double amount = itemDTO.getAmount() == null ? 0 : itemDTO.getAmount();
        ProductLocalInfoDTO productLocalInfoDTO = productService.getProductLocalInfoById(itemDTO.getProductId(), shopId);
        if (UnitUtil.isStorageUnit(itemDTO.getUnit(), productLocalInfoDTO)) {
          amount = amount * productLocalInfoDTO.getRate();
        }
        purchaseInventoryMonthStat.setProductName(StringUtils.defaultIfEmpty(product.getName(), ""));
        purchaseInventoryMonthStat.setProductBrand(StringUtils.defaultIfEmpty(product.getBrand(), ""));
        purchaseInventoryMonthStat.setVehicleBrand(StringUtils.defaultIfEmpty(product.getVehicleBrand(), ""));
        purchaseInventoryMonthStat.setVehicleModel(StringUtils.defaultIfEmpty(product.getVehicleModel(), ""));
        purchaseInventoryMonthStat.setStatYear(DateUtil.getYear(purchaseReturnDTO.getCreationDate()));
        purchaseInventoryMonthStat.setStatMonth(DateUtil.getMonth(purchaseReturnDTO.getCreationDate()));
        purchaseInventoryMonthStat.setAmount(purchaseInventoryMonthStat.getAmount() + amount);
        purchaseInventoryMonthStat.setTotal(purchaseInventoryMonthStat.getTotal() + itemDTO.getTotal());
        purchaseInventoryMonthStat.setTimes(purchaseInventoryMonthStat.getTimes() + 1);
      }
    }
    return purchaseInventoryMonthStat;
  }

  @Override
  public List<String> countBadSalesStatByCondition(Long shopId,SalesStatCondition salesStatCondition) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.countBadSalesStatByCondition(shopId, salesStatCondition);
  }


  @Override
  public  List<PriceFluctuationStatDTO> queryTopPurchaseInventoryLastTwelveMonthStat(Long shopId, int limit){
    TxnWriter writer = txnDaoManager.getWriter();
    List<PriceFluctuationStatDTO> objectList = writer.queryTopPurchaseInventoryLastTwelveMonthStat(shopId, limit);
    return objectList;
  }

  public List<String> countTotalReturnByCondition(Long shopId,SalesStatCondition salesStatCondition) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.countTotalReturnByCondition(shopId, salesStatCondition);
  }

  @Override
  public void saveOrUpdatePurchaseInventoryStatChange(Long shopId, Long productId, Double addAmount, Long vestDate){
    PurchaseInventoryStatChange statChange = new PurchaseInventoryStatChange();
    TxnWriter writer = txnDaoManager.getWriter();

    int year = DateUtil.getYear(vestDate);
    int month = DateUtil.getMonth(vestDate);
    int day = DateUtil.getDay(vestDate);
    Calendar c = Calendar.getInstance();
    c.clear();
    c.set(year,month-1,day,6,0,0);
    Long statTime = c.getTimeInMillis();

    Object status = writer.begin();
    try{
      statChange.setAmount(addAmount);
      statChange.setStatTime(statTime);
      statChange.setStatYear(year);
      statChange.setStatMonth(month);
      statChange.setStatDay(day);
      statChange.setTimes(0);
      statChange.setTotal(0);
      statChange.setShopId(shopId);
      statChange.setProductId(productId);

      writer.save(statChange);
      writer.commit(status);
    }catch(Exception e){
      LOG.error("保存成本统计时出错.");
      LOG.error(e.getMessage(), e);
    }finally{
      writer.rollback(status);
    }
  }

  @Override
  public List<Object[]> queryAllProductPriceFluctuation(Long startTime, Long endTime){
    TxnWriter writer = txnDaoManager.getWriter();
    List<Object[]> objectList = writer.queryAllProductPriceFluctuation(startTime, endTime);
    return objectList;
  }

  @Override
  public void savePriceFluctuationStat(List<Object[]> list) {
    TxnWriter writer = txnDaoManager.getWriter();
    //清空PriceFluctuationStat表
    LOG.info("开始清空PriceFluctuationStat表");
    writer.emptyPriceFluctuationStat();
    LOG.info("成功清空PriceFluctuationStat表");
    //将新的统计结果保存到PriceFluctuationStat表
    Object status = writer.begin();
    LOG.info("开始保存PriceFluctuationStat表");
    try{
      for(int i=0;i<list.size();i++){
        PriceFluctuationStat entity = new PriceFluctuationStat();
        entity.setShopId(list.get(i)[0] == null ? 0 : Long.parseLong(list.get(i)[0].toString()));
        entity.setProductId(list.get(i)[1] == null ? 0 : Long.parseLong(list.get(i)[1].toString()));
        entity.setTotal(list.get(i)[2] == null ? 0 : Double.parseDouble(list.get(i)[2].toString()));
        entity.setAmount(list.get(i)[3] == null ? 0 : Double.parseDouble(list.get(i)[3].toString()));
        entity.setTimes(list.get(i)[4] == null ? 0 : Integer.parseInt(list.get(i)[4].toString()));
        entity.setStatTime(new Date().getTime());
        if(entity.getAmount()>=0 && entity.getTotal()>=0){
          writer.save(entity);
        }
      }
      writer.commit(status);
      LOG.info("成功保存riceFluctuationStat表");
    }catch (Exception e) {
      LOG.error("保存价格波动统计结果出错， savePriceFluctuationStat");
      LOG.error(e.getMessage(), e);
    }finally{
      writer.rollback(status);
    }
  }

//  @Override
//  public Map<String, Object> getPriceFluctuationLineChartData(Long shopId, Long productId, List<Long> timePointList) {
//    Map<String, Object> result = new HashMap<String, Object>();
//    LOG.info("长度"+timePointList.size()+"，第一个时间:"+new Date(timePointList.get(0)).toLocaleString()+"，最后一个时间:"+new Date(timePointList.get(timePointList.size()-1)).toLocaleString());
//    TxnWriter writer = txnDaoManager.getWriter();
//    List<Object> dataList = new ArrayList<Object>();
//    for(int i=0;i<timePointList.size()-1;i++){
//      Double avgPrice = writer.getPriceFluctuationLineChartData(shopId,productId,timePointList.get(i),timePointList.get(i+1));
//      BigDecimal temp = new BigDecimal(avgPrice);
//      temp = temp.setScale(2, BigDecimal.ROUND_HALF_UP);
//      List<Object> itemList = new ArrayList<Object>();
//      itemList.add(new Date(timePointList.get(i+1)).getTime());
//      itemList.add(temp.doubleValue());
//      dataList.add(itemList);
//    }
//    timePointList.remove(timePointList.size()-1);
//    List<String> timeList = new ArrayList<String>();
//    int nextMonth = new Date(timePointList.get(0)).getMonth()+1;
//    timeList.add(nextMonth+"月");
//    for(int i=0;i<12;i++){
//      nextMonth = nextMonth + 1;
//      if(nextMonth!=13){
//        timeList.add(nextMonth+"月");
//      }else{
//        nextMonth = 1;
//        timeList.add(nextMonth+"月");
//      }
//    }
//    result.put("timeList",timeList);  //横坐标
//    result.put("dataList",dataList);  //纵坐标
//    return result;
//  }

  @Override
  public List<List<Object>> getPriceFluctuationLineChartData(Long shopId, Long productId, Long startTime, Long endTime) {
    List<List<Object>> result = new ArrayList<List<Object>>();
    TxnWriter writer = txnDaoManager.getWriter();
    //根据productId和时间范围，取得purchase_inventory_stat表和_stat_change表 的全部记录
    List<Object[]> statList = new ArrayList<Object[]>();
    List<Object[]> changeList = new ArrayList<Object[]>();
    Map<String,Object> map = new HashMap<String,Object>();

    map = writer.getPriceFluctuationLineChartData(shopId,productId,startTime,endTime);
    statList = (List<Object[]>)map.get("statList");
    changeList = (List<Object[]>)map.get("changeList");
    
    //两个列数据合并
    for(int i=0;i<statList.size();i++){
      //该天的stat数据
      Double cur_amount = ((BigDecimal)statList.get(i)[0]).doubleValue();
      Double cur_total = ((BigDecimal)statList.get(i)[1]).doubleValue();
      Long cur_time = ((BigInteger)statList.get(i)[2]).longValue();
      //该天之前的数据，如果有，则取最近一次的数据，相减
      Double pre_amount = null;
      Double pre_total = null;
      for(int j=i-1;j>=0;j--){
        if(((BigDecimal)statList.get(j)[1]).doubleValue()>0){
          pre_amount = ((BigDecimal)statList.get(j)[0]).doubleValue();
          pre_total = ((BigDecimal)statList.get(j)[1]).doubleValue();
          break;
        }
      }
      //该天的change数据
      Double change_amount = 0d;
      Double change_total = 0d;
      for(int k=0;k<changeList.size();k++){
        if(cur_time==((BigInteger)changeList.get(k)[2]).longValue()){
          change_amount = ((BigDecimal)changeList.get(k)[0]).doubleValue() + change_amount;
          change_total = ((BigDecimal)changeList.get(k)[1]).doubleValue() + change_total;
        }
      }
      if(pre_amount==null){
        pre_amount = 0D;
      }
      if(pre_total==null){
        pre_total = 0D;
      }
      if(change_amount==null){
        change_amount = 0D;
      }
      if(change_total==null){
        change_total = 0D;
      }
      cur_amount = cur_amount - pre_amount + change_amount;
      cur_total = cur_total - pre_total + change_total;
      //求四舍五入后的日均价
      BigDecimal avgPrice = cur_amount==0d ? new BigDecimal(0d) : new BigDecimal(cur_total/cur_amount);
      avgPrice = avgPrice.setScale(2, BigDecimal.ROUND_HALF_UP);

      List<Object> itemList = new ArrayList<Object>();
      //时间转换为0点时刻
//      Calendar cal = Calendar.getInstance();
//      cal.setTimeInMillis(cur_time);
//      cal.set(Calendar.HOUR_OF_DAY,0);
//      cur_time = cal.getTimeInMillis();
      itemList.add(cur_time.toString());
      itemList.add(avgPrice.doubleValue());

      result.add(itemList);
    }
    return result;
  }

  public List<PurchaseReturnMonthStatDTO> queryPurchaseReturnByCondition(Long shopId, SalesStatCondition salesStatCondition) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.queryPurchaseReturnByCondition(shopId, salesStatCondition);
  }

  /**
   * 退货统计 获得退货信息
   * @param model
   * @param purchaseReturnMonthStatDTOList
   * @param salesStatCondition
   * @param total
   * @param totalAmount
   * @param size
   */
  public void getReturnInfo(Model model,List<PurchaseReturnMonthStatDTO> purchaseReturnMonthStatDTOList,SalesStatCondition salesStatCondition,double total,double totalAmount,int size) {
    IUserService userService = ServiceManager.getService(IUserService.class);
    if (salesStatCondition.getProductOrSupplier().equals(StatConstant.QUERY_BY_PRODUCT)) {
      List<ProductDTO> productDTOList = new ArrayList<ProductDTO>();
      Long shopId = null;

      ProductDTO other = new ProductDTO();
      double topTotal = 0;
      double topAmount = 0;

      for (PurchaseReturnMonthStatDTO purchaseReturnMonthStatDTO : purchaseReturnMonthStatDTOList) {
        Long productId = purchaseReturnMonthStatDTO.getSupplierIdOrdProductId();
        ProductDTO dto = new ProductDTO();
        dto.setId(productId);
        shopId = purchaseReturnMonthStatDTO.getShopId();
        dto.setSalesAmount(NumberUtil.toReserve(purchaseReturnMonthStatDTO.getAmount(), 1));
        dto.setSalesTotal(NumberUtil.toReserve(purchaseReturnMonthStatDTO.getTotal(), NumberUtil.MONEY_PRECISION));
        dto.setReturnTimes(purchaseReturnMonthStatDTO.getTimes());
        topTotal += dto.getSalesTotal();
        topAmount += dto.getSalesAmount();
        dto.setQueryResult(dto.getSalesTotal());
        productDTOList.add(dto);
      }
      productDTOList = getProductInfo(productDTOList,shopId);

      other.setSalesTotal(NumberUtil.toReserve(total - topTotal, NumberUtil.MONEY_PRECISION));
      other.setSalesAmount(NumberUtil.toReserve(totalAmount - topAmount,1));
      other.setQueryResult(other.getSalesTotal());
      other.setQueryResultStr("其他商品");
      List<ProductDTO> otherList = new ArrayList<ProductDTO>();
      if (productDTOList.size() > StatConstant.QUERY_SIZE) {
        otherList.add(other);
      }
      model.addAttribute("other", otherList);
      model.addAttribute("itemDTOs", productDTOList);
    } else {
      List<SupplierDTO> supplierDTOList = new ArrayList<SupplierDTO>();

      SupplierDTO other = new SupplierDTO();
      double topTotal = 0;
      double topAmount = 0;

      for (PurchaseReturnMonthStatDTO purchaseReturnMonthStatDTO : purchaseReturnMonthStatDTOList) {
        Long supplierId = purchaseReturnMonthStatDTO.getSupplierIdOrdProductId();

        SupplierDTO supplierDTO = userService.getSupplierById(supplierId);
        if (supplierDTO == null) {
          LOG.error("supplierId:" + supplierId + "在 supplier 找不到" + "shopId:" + purchaseReturnMonthStatDTO.getShopId());
          continue;
        }
        supplierDTO.setReturnProductCategories(purchaseReturnMonthStatDTO.getReturnProductCategories());
        supplierDTO.setReturnAmount(NumberUtil.toReserve(purchaseReturnMonthStatDTO.getAmount(), 1));
        supplierDTO.setReturnTotal(NumberUtil.toReserve(purchaseReturnMonthStatDTO.getTotal(), NumberUtil.MONEY_PRECISION));
        supplierDTO.setReturnTimes(purchaseReturnMonthStatDTO.getTimes());
        supplierDTO.setQueryResult(supplierDTO.getReturnTotal());
        supplierDTO.setQueryResultStr(supplierDTO.getName());
        topTotal += supplierDTO.getReturnTotal();
        topAmount += supplierDTO.getReturnAmount();
        supplierDTOList.add(supplierDTO);
      }
      other.setReturnTotal(NumberUtil.toReserve(total - topTotal, NumberUtil.MONEY_PRECISION));
      other.setReturnAmount(NumberUtil.toReserve(totalAmount - topAmount, 1));
      other.setQueryResult(other.getReturnTotal());
      other.setQueryResultStr("其他供应商");

      List<SupplierDTO> otherList = new ArrayList<SupplierDTO>();
      if (size > StatConstant.QUERY_SIZE) {
        otherList.add(other);
      }
      model.addAttribute("other", otherList);
      model.addAttribute("itemDTOs", supplierDTOList);
    }
  }


  /**
   * 获得商品的属性
   * @param productDTOList
   * @param shopId
   * @return
   */
   public List<ProductDTO> getProductInfo(List<ProductDTO> productDTOList,Long shopId) {

     List<ProductDTO> returnDTOList = new ArrayList<ProductDTO>();
     IProductService productService = ServiceManager.getService(IProductService.class);

     Set<Long> productSet = new HashSet<Long>();
     for (ProductDTO productDTO : productDTOList) {
       if (productDTO != null && productDTO.getId() != null) {
         productSet.add(productDTO.getId());
       }
     }

     if (!productSet.isEmpty()) {
       Map<Long, ProductDTO> productDTOMap = productService.getProductDTOMapByProductLocalInfoIds(productSet);
       returnDTOList = new ArrayList<ProductDTO>(productDTOMap.values());
       if (CollectionUtils.isNotEmpty(returnDTOList)) {

         for (ProductDTO productDTO : productDTOList) {
           ProductDTO dto = productDTOMap.get(productDTO.getId());
           productDTO.setCommodityCode(StringUtils.defaultIfEmpty(dto.getCommodityCode(), "--").replaceAll("\n", "").replaceAll("\r", ""));
           productDTO.setName(StringUtils.defaultIfEmpty(dto.getName(), "--").replaceAll("\n", "").replaceAll("\r", ""));
           productDTO.setBrand(StringUtils.defaultIfEmpty(dto.getBrand(), "--").replaceAll("\n", "").replaceAll("\r", ""));
           productDTO.setSpec(StringUtils.defaultIfEmpty(dto.getSpec(), "--").replaceAll("\n", "").replaceAll("\r", ""));
           productDTO.setModel(StringUtils.defaultIfEmpty(dto.getModel(), "--").replaceAll("\n", "").replaceAll("\r", ""));
           productDTO.setProductVehicleModel(StringUtils.defaultIfEmpty(dto.getProductVehicleModel(), "--").replaceAll("\n", "").replaceAll("\r", ""));
           productDTO.setProductVehicleBrand(StringUtils.defaultIfEmpty(dto.getProductVehicleBrand(), "--").replaceAll("\n", "").replaceAll("\r", ""));
           productDTO.setQueryResultStr(productDTO.getName().replaceAll("--", "") + " " + productDTO.getBrand().replaceAll("--", "") + " " + productDTO.getSpec().replaceAll("--", "")
               + " " + productDTO.getModel().replaceAll("--", "") + " " + productDTO.getProductVehicleModel().replaceAll("--", ""));
           productDTO.setQueryResultStr(StringUtil.getShortStringByNum(productDTO.getQueryResultStr(),0,25));
           int lastIndexOfSpace = productDTO.getQueryResultStr().lastIndexOf(" ");
           if (lastIndexOfSpace > 0) {
             productDTO.setQueryResultStr(StringUtil.getShortStringByNum(productDTO.getQueryResultStr(), 0, lastIndexOfSpace));
           }
         }
       }
     }
     return productDTOList;

   }

  @Override
  public PurchaseInventoryMonthStat getPurchaseInventoryMonthStatByPropertiesYearMonth(Long shopId, String name, String brand, String vehicleBrand, String vehicleModel, int statYear, int statMonth) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getPurchaseInventoryMonthStatByPropertiesYearMonth(shopId, name, brand, vehicleBrand, vehicleModel, statYear, statMonth);
  }


}
