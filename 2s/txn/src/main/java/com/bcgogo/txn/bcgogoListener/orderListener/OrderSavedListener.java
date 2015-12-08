package com.bcgogo.txn.bcgogoListener.orderListener;

import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.listener.BcgogoEventListener;
import com.bcgogo.search.service.CurrentUsed.IProductCurrentUsedService;
import com.bcgogo.search.service.CurrentUsed.IVehicleCurrentUsedService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.service.*;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 12-4-10
 * Time: 下午4:57
 * To change this template use File | Settings | File Templates.
 */
public abstract class OrderSavedListener extends BcgogoEventListener {
  public static final Logger LOG = LoggerFactory.getLogger(OrderSavedListener.class);
  public static final String SURPAY="surPay";//付款方式

  public OrderSavedListener() {
  }

  public abstract void run();

  /**
   * 单据统计
   * @param bcgogoOrderDto
   * @param isRepeal 单据是否作废
   * @param isBusinessStat 单据是否统计营业  如果是当前时间 统计营业额   如果不是当前时间 不统计营业额
   */
  public void businessStatByOrder(BcgogoOrderDto bcgogoOrderDto, boolean isRepeal,boolean isBusinessStat,Long vestDate) {

    try {
      List<Object> objectList = getStatInfoByOrder(bcgogoOrderDto);
      if (CollectionUtils.isEmpty(objectList) || objectList.size() != 3) {
        LOG.error("/OrderSavedListener" + " method=businessStatByOrder" + "shopId:" + bcgogoOrderDto.getShopId());
        LOG.error("营业统计和流水统计出错,获得单据信息出错");
        return;
      }

        //营业统计
      if (!(bcgogoOrderDto instanceof PurchaseInventoryDTO) && isBusinessStat) {
        try {
          IBusinessStatService businessStatService = ServiceManager.getService(IBusinessStatService.class);
          BusinessStatDTO businessStatDTO = (BusinessStatDTO) objectList.get(0);
          businessStatService.businessStat(businessStatDTO, isRepeal,vestDate);
        } catch (Exception e) {
          LOG.error("营业统计出错 /OrderSavedListener method=businessStat shopId:" + bcgogoOrderDto.getShopId());
          LOG.error(e.getMessage(), e);
          LOG.error("单据内容:" + String.valueOf(objectList.get(2)));
        }
      }
      //流水统计
      try {
        RunningStatDTO runningStatDTO = (RunningStatDTO) objectList.get(1);
        if (NumberUtil.longValue(runningStatDTO.getAccountDate()) > 0 && (!DateUtil.isCurrentTime(runningStatDTO.getAccountDate())) && (!isRepeal)) {
          IRunningStatService runningStatService = ServiceManager.getService(IRunningStatService.class);

          Long accountDate = runningStatDTO.getAccountDate();
          runningStatDTO.setStatDate(accountDate);
          runningStatDTO.setStatYear(DateUtil.getYearByVestDate(accountDate));
          runningStatDTO.setStatMonth(DateUtil.getMonthByVestDate(accountDate));
          runningStatDTO.setStatDay(DateUtil.getDayByVestDate(accountDate));
          runningStatService.saveRunningStatChangeFromDTO(runningStatDTO);
        }else{
          this.runningStat(runningStatDTO, isRepeal);
        }

      } catch (Exception e) {
        LOG.error("/OrderSavedListener method=runningStat" + " shopId:" + bcgogoOrderDto.getShopId());
        LOG.error("流水统计出错");
        LOG.error(e.getMessage(), e);
        LOG.error("单据内容:" + String.valueOf(objectList.get(2)));
      }
    } catch (Exception e) {
      LOG.error("/OrderSavedListener method=businessStatByOrder " + "shopId:" + bcgogoOrderDto.getShopId());
      LOG.error("营业统计和流水统计出错");
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * 流水统计
   * @param statDTO
   * @param isRepeal
   */
  public void runningStat(RunningStatDTO statDTO,boolean isRepeal) {
    IRunningStatService runningStatService = ServiceManager.getService(IRunningStatService.class);
    runningStatService.runningStat(statDTO,isRepeal);
  }

  /**
   * 获得统计信息 包括流水信息和营业额信息
   * @param bcgogoOrderDto
   * @return
   * @throws Exception
   */
  public List getStatInfoByOrder(BcgogoOrderDto bcgogoOrderDto) throws Exception {
    RFITxnService rfiTxnService = ServiceManager.getService(RFITxnService.class);
    List<Object> objectList = new ArrayList<Object>();

    String orderString = "";//单据信息

    BusinessStatDTO businessStatDTO = new BusinessStatDTO();
    businessStatDTO.setShopId(bcgogoOrderDto.getShopId());
    businessStatDTO.setStatYear((long)DateUtil.getCurrentYear());
    businessStatDTO.setStatMonth((long)DateUtil.getCurrentMonth());
    businessStatDTO.setStatDay((long)DateUtil.getCurrentDay());

    RunningStatDTO runningStatDTO = new RunningStatDTO();
    runningStatDTO.setShopId(bcgogoOrderDto.getShopId());
    runningStatDTO.setStatYear((long)DateUtil.getCurrentYear());
    runningStatDTO.setStatMonth((long)DateUtil.getCurrentMonth());
    runningStatDTO.setStatDay((long)DateUtil.getCurrentDay());


    double washTotal = 0.0;  //洗车单金额
    double salesTotal = 0.0; //销售单营业额 实收 + 欠款
    double serviceTotal = 0.0; //施工单营业额 实收+欠款
    double productCostTotal = 0.0; //商品成本
    double memberIncome = 0.0;//会员营业统计收入

    double cash = 0; //流水统计现金支付
    double cheque = 0; //流水统计支票支付
    double unionPay = 0;  //流水统计银联支付
    double customerDepositExpenditure = 0; // 预收款统计总和
    double debt =0;//流水统计欠款
    double cashExpenditure = 0.0; //现金支出总和
    double chequeExpenditure = 0.0; //支票支出总和
    double unionPayExpenditure = 0.0; //银联支出总和
    double memberPayIncome = 0.0;//流水统计会员支付
    double debtNewExpenditure = 0.0; //供应商新增欠款
    double depositPayExpenditure = 0.0; //供应商订金总和
    double debtWithdrawalIncome = 0.0;//供应商欠款回笼

    double customerDebtDiscount = 0.0; //客户欠款结算时产生的折扣
    double strikeAmountIncome = 0.0;//销售退货单 冲账

    double customerReturnDebt = 0.0;
    double supplierReturnDebt = 0.0;

    double otherIncomeCostPrice = 0.0;//施工单或者销售单其他费用成本

    double coupon = 0.0;  //流水统计代金券支付
    double couponExpenditure = 0.0; //代金券支出总和

    if (bcgogoOrderDto instanceof WashOrderDTO) {
      WashOrderDTO washOrderDTO = (WashOrderDTO) bcgogoOrderDto;
      if (washOrderDTO != null) {
        washTotal = washOrderDTO.getCashNum();
        orderString = washOrderDTO.toString();
      }
    } else if (bcgogoOrderDto instanceof RepairOrderDTO) {
      RepairOrderDTO repairOrderDTO = (RepairOrderDTO) bcgogoOrderDto;
      //营业额 实收 + 欠款
      serviceTotal += repairOrderDTO.getDebt() + repairOrderDTO.getSettledAmount();
      //商品成本 直接从dto中拿 总成本减去其他费用成本
      productCostTotal += NumberUtil.toReserve((NumberUtil.doubleVal(repairOrderDTO.getTotalCostPrice()) - NumberUtil.doubleVal(repairOrderDTO.getOtherTotalCostPrice())),2);

      otherIncomeCostPrice += NumberUtil.doubleVal(repairOrderDTO.getOtherTotalCostPrice());
      cash = NumberUtil.doubleVal(repairOrderDTO.getCashAmount());
      cheque = NumberUtil.doubleVal(repairOrderDTO.getBankCheckAmount());
      unionPay = NumberUtil.doubleVal(repairOrderDTO.getBankAmount());
      debt = repairOrderDTO.getDebt();
      memberPayIncome =NumberUtil.doubleVal(repairOrderDTO.getMemberAmount());
      orderString = repairOrderDTO.toString();

      coupon = NumberUtil.doubleVal(repairOrderDTO.getCouponAmount());  //流水统计代金券支付

      if (StringUtil.isNotEmpty(repairOrderDTO.getAccountDateStr()) && DateUtil.convertDateStringToDateLong(DateUtil.STANDARD, repairOrderDTO.getAccountDateStr()) > 0) {
        runningStatDTO.setAccountDate(DateUtil.convertDateStringToDateLong(DateUtil.STANDARD,repairOrderDTO.getAccountDateStr()));
      }
      //如果是作废单，获取今日新增欠款和欠款回笼的钱
      if (OrderStatus.REPAIR_REPEAL == repairOrderDTO.getStatus()) {
        RunningStatDTO returnDTO = rfiTxnService.updateOrderRepealReception(bcgogoOrderDto, OrderTypes.REPAIR);
        debt = returnDTO.getDebtNewIncome();
        customerDebtDiscount = returnDTO.getCustomerDebtDiscount();
        debtWithdrawalIncome = returnDTO.getDebtWithdrawalIncome();
      }
    } else if (bcgogoOrderDto instanceof SalesOrderDTO) {
      SalesOrderDTO salesOrderDTO = (SalesOrderDTO) bcgogoOrderDto;
      //营业额 实收 + 欠款
      salesTotal += salesOrderDTO.getDebt() + salesOrderDTO.getSettledAmount();
      //商品成本 直接从dto中拿 总成本减去其他费用成本
      productCostTotal += NumberUtil.toReserve((NumberUtil.doubleVal(salesOrderDTO.getTotalCostPrice()) - NumberUtil.doubleVal(salesOrderDTO.getOtherTotalCostPrice())),2);

      debt = salesOrderDTO.getDebt();
      cash = NumberUtil.doubleVal(salesOrderDTO.getCashAmount());
      cheque = NumberUtil.doubleVal(salesOrderDTO.getBankCheckAmount());
      unionPay = NumberUtil.doubleVal(salesOrderDTO.getBankAmount());
      customerDepositExpenditure = NumberUtil.doubleVal(salesOrderDTO.getCustomerDeposit()); // add by zhuj
      debt = salesOrderDTO.getDebt();
      memberPayIncome = NumberUtil.doubleVal(salesOrderDTO.getMemberAmount());
      otherIncomeCostPrice += NumberUtil.doubleVal(salesOrderDTO.getOtherTotalCostPrice());

      if (StringUtil.isNotEmpty(salesOrderDTO.getAccountDateStr()) && DateUtil.convertDateStringToDateLong(DateUtil.STANDARD, salesOrderDTO.getAccountDateStr()) > 0) {
        runningStatDTO.setAccountDate(DateUtil.convertDateStringToDateLong(DateUtil.STANDARD,salesOrderDTO.getAccountDateStr()));
      }
      //如果是作废单，获取今日新增欠款和欠款回笼的钱
      if(salesOrderDTO.getStatus() == OrderStatus.SALE_REPEAL) {
        RunningStatDTO returnDTO = rfiTxnService.updateOrderRepealReception(bcgogoOrderDto, OrderTypes.SALE);
        debt = returnDTO.getDebtNewIncome();
        customerDebtDiscount = returnDTO.getCustomerDebtDiscount();
        debtWithdrawalIncome = returnDTO.getDebtWithdrawalIncome();
      }
      orderString = salesOrderDTO.toString();
    } else if (bcgogoOrderDto instanceof SalesReturnDTO) {
      SalesReturnDTO salesReturnDTO = (SalesReturnDTO) bcgogoOrderDto;
      salesTotal += -salesReturnDTO.getSettledAmount()-salesReturnDTO.getAccountDebtAmount();
      productCostTotal += -salesReturnDTO.getTotalCostPrice();
      cash = -NumberUtil.doubleVal(salesReturnDTO.getCashAmount());
      cheque = -NumberUtil.doubleVal(salesReturnDTO.getBankCheckAmount());
      unionPay = -NumberUtil.doubleVal(salesReturnDTO.getBankAmount());
      customerDepositExpenditure = -NumberUtil.doubleVal(salesReturnDTO.getCustomerDeposit());
      strikeAmountIncome = NumberUtil.doubleVal(salesReturnDTO.getStrikeAmount());
      customerReturnDebt = NumberUtil.doubleVal(salesReturnDTO.getAccountDebtAmount());
      //销售退货单冲账 计入到欠款回笼当中
      debtWithdrawalIncome = NumberUtil.doubleVal(salesReturnDTO.getStrikeAmount());
      if(salesReturnDTO.getStatus() == OrderStatus.REPEAL) {
        RunningStatDTO returnDTO = rfiTxnService.updateOrderRepealReception(bcgogoOrderDto, OrderTypes.SALE_RETURN);
      }
      orderString = salesReturnDTO.toString();
    } else if (bcgogoOrderDto instanceof MemberCardOrderDTO) {
      MemberCardOrderDTO memberCardOrderDTO = (MemberCardOrderDTO) bcgogoOrderDto;
      if (memberCardOrderDTO == null || memberCardOrderDTO.getReceivableDTO() == null) {
        LOG.error("/OrderSavedList");
        LOG.error("method=orderRunBusinessStat");
        LOG.error(" 营业统计多线程 thread run方法中会员购卡信息 或实收信息不正确 ");
        LOG.error("shopId:" + bcgogoOrderDto.getShopId());
        LOG.error("单据字符串:" + memberCardOrderDTO.toString());
      }
      ReceivableDTO receivableDTO = memberCardOrderDTO.getReceivableDTO();
      //会员卡购卡续卡不算入营业额 此处不计算memberIncome

      cash = NumberUtil.doubleVal(receivableDTO.getCash());
      unionPay = NumberUtil.doubleVal(receivableDTO.getBankCard());
      cheque = NumberUtil.doubleVal(receivableDTO.getCheque());
      debt = NumberUtil.doubleVal(receivableDTO.getDebt());
      orderString = memberCardOrderDTO.toString();
    } else if(bcgogoOrderDto instanceof MemberCardReturnDTO) {
      MemberCardReturnDTO memberCardReturnDTO = (MemberCardReturnDTO) bcgogoOrderDto;
      if(memberCardReturnDTO == null || memberCardReturnDTO.getReceptionRecordDTO() == null){
        LOG.error("OrderSavedListener.orderRunBusinessStat 统计会员退卡信息出错，无实收信息。");
        LOG.error("shopID:{} ; 单据: {}", bcgogoOrderDto.getShopId(), memberCardReturnDTO.toString());
      }
      ReceptionRecordDTO receptionRecordDTO = memberCardReturnDTO.getReceptionRecordDTO();
      //会员卡退卡不计算营业统计

      cash = NumberUtil.doubleVal(receptionRecordDTO.getCash());
      unionPay = NumberUtil.doubleVal(receptionRecordDTO.getBankCard());
      cheque = NumberUtil.doubleVal(receptionRecordDTO.getCheque());
      orderString = memberCardReturnDTO.toString();
    } else if (bcgogoOrderDto instanceof WashBeautyOrderDTO) {
      WashBeautyOrderDTO washBeautyOrderDTO = (WashBeautyOrderDTO) bcgogoOrderDto;
      if (washBeautyOrderDTO != null) {
        washTotal = washBeautyOrderDTO.getSettledAmount() + NumberUtil.doubleVal(washBeautyOrderDTO.getDebt());
      }

      cash = NumberUtil.doubleVal(washBeautyOrderDTO.getCashAmount());
      unionPay = NumberUtil.doubleVal(washBeautyOrderDTO.getBankAmount());
      cheque = NumberUtil.doubleVal(washBeautyOrderDTO.getBankCheckAmount());
      debt = NumberUtil.doubleVal(washBeautyOrderDTO.getDebt());
      memberPayIncome = NumberUtil.doubleVal(washBeautyOrderDTO.getMemberAmount());
      orderString = washBeautyOrderDTO.toString();

      coupon = NumberUtil.doubleVal(washBeautyOrderDTO.getCouponAmount());  //流水统计代金券支付

      //如果是作废单，获取今日新增欠款和欠款回笼的钱
      if (washBeautyOrderDTO.getStatus() == OrderStatus.WASH_REPEAL) {
        RunningStatDTO returnDTO = rfiTxnService.updateOrderRepealReception(bcgogoOrderDto, OrderTypes.WASH_BEAUTY);
        debt = returnDTO.getDebtNewIncome();
        customerDebtDiscount = returnDTO.getCustomerDebtDiscount();
        debtWithdrawalIncome = returnDTO.getDebtWithdrawalIncome();
      }

    }else if(bcgogoOrderDto instanceof PurchaseInventoryDTO){
      PurchaseInventoryDTO purchaseInventoryDTO = (PurchaseInventoryDTO)bcgogoOrderDto;

      //入库单 流水统计
      cashExpenditure = NumberUtil.doubleVal(purchaseInventoryDTO.getCash());
      unionPayExpenditure = NumberUtil.doubleVal(purchaseInventoryDTO.getBankCardAmount());
      chequeExpenditure = NumberUtil.doubleVal(purchaseInventoryDTO.getCheckAmount());
      debtNewExpenditure = NumberUtil.doubleVal(purchaseInventoryDTO.getCreditAmount());
      depositPayExpenditure = NumberUtil.doubleVal(purchaseInventoryDTO.getDepositAmount());

      if (StringUtil.isNotEmpty(purchaseInventoryDTO.getAccountDateStr()) && DateUtil.convertDateStringToDateLong(DateUtil.STANDARD, purchaseInventoryDTO.getAccountDateStr()) > 0) {
        runningStatDTO.setAccountDate(DateUtil.convertDateStringToDateLong(DateUtil.STANDARD,purchaseInventoryDTO.getAccountDateStr()));
      }

      //正常结算
      if(cashExpenditure == 0 && unionPayExpenditure == 0 && chequeExpenditure == 0 && depositPayExpenditure == 0 && debtNewExpenditure == 0){
        cashExpenditure = NumberUtil.doubleVal(purchaseInventoryDTO.getStroageActuallyPaid());
        debtNewExpenditure = NumberUtil.doubleVal(purchaseInventoryDTO.getStroageCreditAmount());
      }

      if (purchaseInventoryDTO.getStatus() == OrderStatus.PURCHASE_INVENTORY_REPEAL) {
        ISupplierPayableService supplierPayableService = ServiceManager.getService(ISupplierPayableService.class);
        RunningStatDTO returnRunningStatDTO = supplierPayableService.repealPayableHistoryRecordForInventory(purchaseInventoryDTO);
        debtNewExpenditure = returnRunningStatDTO.getDebtNewExpenditure();
        runningStatDTO.setDebtWithdrawalExpenditure(returnRunningStatDTO.getDebtWithdrawalExpenditure());
        runningStatDTO.setSupplierDebtDiscount(returnRunningStatDTO.getSupplierDebtDiscount());
      }

      orderString = purchaseInventoryDTO.toString();
    } else if(bcgogoOrderDto instanceof PurchaseReturnDTO){
      PurchaseReturnDTO purchaseReturnDTO = (PurchaseReturnDTO)bcgogoOrderDto;
      cashExpenditure = -NumberUtil.doubleVal(purchaseReturnDTO.getCash());
      chequeExpenditure = -NumberUtil.doubleVal(purchaseReturnDTO.getBankCheckAmount());
      unionPayExpenditure = -NumberUtil.doubleVal(purchaseReturnDTO.getBankAmount());
      depositPayExpenditure = -NumberUtil.doubleVal(purchaseReturnDTO.getDepositAmount());
      supplierReturnDebt = NumberUtil.doubleVal(purchaseReturnDTO.getAccountDebtAmount());
      orderString = purchaseReturnDTO.toString();

    }else {
      throw new Exception(" 营业统计多线程 thread run方法中单据的类型不正确.");
    }

    businessStatDTO.setSales(salesTotal);
    businessStatDTO.setWash(washTotal);
    businessStatDTO.setService(serviceTotal);
    businessStatDTO.setProductCost(productCostTotal);
    businessStatDTO.setOrderOtherIncomeCost(otherIncomeCostPrice);
    businessStatDTO.setMemberIncome(memberIncome);
    businessStatDTO.setStatTime(System.currentTimeMillis());
    businessStatDTO.setStatSum(salesTotal + washTotal + serviceTotal);

    runningStatDTO.setDebtNewIncome(debt);
    runningStatDTO.setCashIncome(cash);
    runningStatDTO.setChequeIncome(cheque);
    runningStatDTO.setUnionPayIncome(unionPay);
    runningStatDTO.setMemberPayIncome(memberPayIncome);
    runningStatDTO.setStatDate(System.currentTimeMillis());

    runningStatDTO.setCouponIncome(coupon);  //流水统计代金券支付
    runningStatDTO.setCouponExpenditure(couponExpenditure);  //代金券支出总和

    runningStatDTO.setCashExpenditure(cashExpenditure);
    runningStatDTO.setUnionPayExpenditure(unionPayExpenditure);
    runningStatDTO.setChequeExpenditure(chequeExpenditure);
    runningStatDTO.setDebtNewExpenditure(debtNewExpenditure);
    runningStatDTO.setCustomerDepositExpenditure(customerDepositExpenditure); // add by zhuj
    runningStatDTO.setDepositPayExpenditure(depositPayExpenditure);
    runningStatDTO.setExpenditureSum(cashExpenditure + unionPayExpenditure + chequeExpenditure);
    runningStatDTO.setDebtWithdrawalIncome(debtWithdrawalIncome);
    runningStatDTO.setCustomerDebtDiscount(customerDebtDiscount);
    runningStatDTO.setStrikeAmountIncome(strikeAmountIncome);
    runningStatDTO.setCustomerReturnDebt(customerReturnDebt);
    runningStatDTO.setSupplierReturnDebt(supplierReturnDebt);

    runningStatDTO.setIncomeSum(runningStatDTO.getCashIncome() + runningStatDTO.getChequeIncome() + runningStatDTO.getUnionPayIncome());
    runningStatDTO.setRunningSum(runningStatDTO.getIncomeSum() - runningStatDTO.getExpenditureSum());

    objectList.add(businessStatDTO);
    objectList.add(runningStatDTO);
    objectList.add(orderString);
    return objectList;
  }

  //针对销售单与施工单 非当天的单据
  protected void orderRunBusinessStatChange(BcgogoOrderDto bcgogoOrderDto) {
    IBusinessStatService businessStatService = ServiceManager.getService(IBusinessStatService.class);
    Long shopId = bcgogoOrderDto.getShopId();
    Long vestDate = null;
    BusinessStatDTO businessStatDTO = new BusinessStatDTO();
    businessStatDTO.setShopId(shopId);
    try {
      if (bcgogoOrderDto instanceof RepairOrderDTO) {
        RepairOrderDTO repairOrderDTO = (RepairOrderDTO) bcgogoOrderDto;
        businessStatDTO.setService(repairOrderDTO.getDebt() + repairOrderDTO.getSettledAmount());
        businessStatDTO.setProductCost(NumberUtil.toReserve((NumberUtil.doubleVal(repairOrderDTO.getTotalCostPrice()) - NumberUtil.doubleVal(repairOrderDTO.getOtherTotalCostPrice())),2));
        businessStatDTO.setOrderOtherIncomeCost(NumberUtil.doubleVal(repairOrderDTO.getOtherTotalCostPrice()));
        vestDate = repairOrderDTO.getVestDate();
      } else if (bcgogoOrderDto instanceof SalesOrderDTO) {
        SalesOrderDTO salesOrderDTO = (SalesOrderDTO) bcgogoOrderDto;
        businessStatDTO.setSales(NumberUtil.doubleVal(salesOrderDTO.getDebt()) + NumberUtil.doubleVal(salesOrderDTO.getSettledAmount()));
        businessStatDTO.setProductCost(NumberUtil.toReserve((NumberUtil.doubleVal(salesOrderDTO.getTotalCostPrice()) - NumberUtil.doubleVal(salesOrderDTO.getOtherTotalCostPrice())),2));
        businessStatDTO.setOrderOtherIncomeCost(NumberUtil.doubleVal(salesOrderDTO.getOtherTotalCostPrice()));
        vestDate = salesOrderDTO.getVestDate();
      }else if(bcgogoOrderDto instanceof SalesReturnDTO){
        SalesReturnDTO salesReturnDTO = (SalesReturnDTO) bcgogoOrderDto;
        businessStatDTO.setSales(-NumberUtil.doubleVal(salesReturnDTO.getSettledAmount()));
        businessStatDTO.setProductCost(-NumberUtil.doubleVal(salesReturnDTO.getTotalCostPrice()));
        vestDate = salesReturnDTO.getVestDate();
      }else if(bcgogoOrderDto instanceof WashBeautyOrderDTO) {
        WashBeautyOrderDTO washBeautyOrderDTO = (WashBeautyOrderDTO) bcgogoOrderDto;
        businessStatDTO.setWash(NumberUtil.doubleVal(washBeautyOrderDTO.getSettledAmount()) + NumberUtil.doubleVal(washBeautyOrderDTO.getDebt()));
        vestDate = washBeautyOrderDTO.getVestDate();
      }
      if (vestDate == null) {
        throw new Exception(" 营业统计多线程 thread run方法中归属时间为空.");
      }

      businessStatDTO.setStatTime(vestDate);
      businessStatDTO.setStatYear(DateUtil.getYearByVestDate(vestDate));
      businessStatDTO.setStatMonth(DateUtil.getMonthByVestDate(vestDate));
      businessStatDTO.setStatDay(DateUtil.getDayByVestDate(vestDate));
      businessStatService.saveBusinessStatChangeFromDTO(businessStatDTO);

    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * 品牌下拉框更新，对每个单据通过多线程处理
   *
   * @param bcgogoOrderDto 每个单据的dto
   */
  public void currentUsedSaved(BcgogoOrderDto bcgogoOrderDto) {
//    ConcurrentScene scene =  ConcurrentScene.CURRENT_USED;
//    try {
//      if (!BcgogoConcurrentController.lock(scene, key)) {
//        return;
//      }
      IProductCurrentUsedService productCurrentUsedService = ServiceManager.getService(IProductCurrentUsedService.class);
      IVehicleCurrentUsedService vehicleCurrentUsedService = ServiceManager.getService(IVehicleCurrentUsedService.class);
      //更新常用商品数据
      productCurrentUsedService.currentUsedProductSaved(bcgogoOrderDto);
      //更新常用车辆数据
      vehicleCurrentUsedService.currentUsedVehicleSaved(bcgogoOrderDto);
//    } finally {
//      BcgogoConcurrentController.release(scene, key);
//    }
  }

  protected void salesStat(BcgogoOrderDto bcgogoOrderDto,OrderStatus orderStatus) {
    try{
      IPurchaseCostStatService purchaseCostStatService = ServiceManager.getService(IPurchaseCostStatService.class);
      purchaseCostStatService.salesStat(bcgogoOrderDto,orderStatus);
    }catch(Exception e){
      LOG.error("销售/施工统计出错 /SalesOrderSavedListener method=salesStat. shopId:" + bcgogoOrderDto.getShopId());
      LOG.error(e.getMessage(), e);
      LOG.error("bcgogoOrderDTO: {}", bcgogoOrderDto);
    }
  }

  //solr 统一 reindex
  protected void reCreateSolrIndex(Long shopId, BcgogoOrderDto orderDto) {

  }

  protected void vehicleServeStat(BcgogoOrderDto bcgogoOrderDto, boolean isRepeal){
    try {
      ServiceManager.getService(IVehicleStatService.class).vehicleServeStat(bcgogoOrderDto, isRepeal);
    } catch (Exception e) {
      LOG.error("洗车/施工统计出错 /OrderSavedListener method=vehicleServeStat. shopId:" + bcgogoOrderDto.getShopId());
      LOG.error(e.getMessage(), e);
      LOG.error("bcgogoOrderDTO: {}", bcgogoOrderDto);
    }
  }

}
