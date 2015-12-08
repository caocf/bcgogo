package com.bcgogo.txn.service.exportExcel;

import com.bcgogo.common.Pager;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.excelexport.BcgogoExportDataIterator;
import com.bcgogo.enums.ConsumeType;
import com.bcgogo.enums.ItemTypes;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.exception.PageException;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.search.dto.OrderIndexDTO;
import com.bcgogo.search.dto.OrderSearchResultDTO;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.service.IServiceHistoryService;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: jinyuan
 * Date: 13-8-8
 * Time: 下午10:28
 * To change this template use File | Settings | File Templates.
 */
public class BusinessStatExportDataIterator extends BcgogoExportDataIterator {
  private static final Logger LOG = LoggerFactory.getLogger(InventoryExportDataIterator.class);
  private static final int PAGE_SIZE = 1000;
  public static final String QUERY_TYPE_DAY = "day"; //营业统计按天查询
  private String arrayType;    //排序类型   金额 或者  时间  排序
  private String type;   //年   月   日
  private String pageType;   //页面类型，用于判断是导出哪个营业额统计
  private String dateStr;   //2013-12-4格式的
  private Long shopId;
  private int totalNum;
  public BusinessStatExportDataIterator(String arrayType, String type, String pageType, String dateStr, Long shopId, int totalNum) throws PageException {
    super(totalNum, PAGE_SIZE, Integer.valueOf(ServiceManager.getService(IConfigService.class).getConfig("TotalNumPerExcel", ShopConstant.BC_SHOP_ID)));
    this.arrayType = arrayType;
    this.type = type;
    this.pageType = pageType;
    this.dateStr = dateStr;
    this.shopId = shopId;
    this.totalNum = totalNum;
  }

  @Override
  protected int getTotalRows() {
   return totalNum;
  }

  @Override
  protected List<String> getHead() {
    List<String> head = null;
    if("repair".equals(pageType)) {
      head = Arrays.asList(BusinessStatConstant.repairFields);
    } else if("sale".equals(pageType)) {
      head = Arrays.asList(BusinessStatConstant.salesFields);
    } else if("wash".equals(pageType)) {
      head = Arrays.asList(BusinessStatConstant.washFields);
    }
    return head;
  }

  @Override
  public Object next() {
    //取下一页数据
    getPage().gotoNextPage();
    //生成要导出的数据
    List<List<String>> rows = assembleBusinessStatInfo();
    return rows;
  }

  @Override
  protected List<String> getHeadShowInfo() {
    return null;
  }

  @Override
  protected List<String> getTailShowInfo() {
    return null;
  }

  private List<List<String>> assembleBusinessStatInfo() {
    List<List<String>> rows = new ArrayList<List<String>>();
    if("repair".equals(pageType)) {
      List<RepairOrderDTO> repairOrderDTOs = getRepairOrderDTOs();
      if(CollectionUtils.isNotEmpty(repairOrderDTOs)) {
        for(RepairOrderDTO repairOrderDTO : repairOrderDTOs) {
          List<String> row = new ArrayList<String>();
          row.add(repairOrderDTO.getVestDateStr() == null ? "" : repairOrderDTO.getVestDateStr());
          row.add(repairOrderDTO.getReceiptNo() == null ? "" : repairOrderDTO.getReceiptNo());
          row.add(repairOrderDTO.getVechicle() == null ? "" : repairOrderDTO.getVechicle());
          row.add(repairOrderDTO.getServiceTotal() + "");
          row.add(repairOrderDTO.getServiceTotalCost() + "");
          row.add(repairOrderDTO.getSalesTotal() + "");
          row.add(repairOrderDTO.getSalesTotalCost() + "");
          row.add(repairOrderDTO.getOtherIncomeTotal() + "");
          row.add(repairOrderDTO.getOtherTotalCostPrice() + "");
          row.add(repairOrderDTO.getAfterMemberDiscountTotal() == null ? "" : repairOrderDTO.getAfterMemberDiscountTotal().toString());
          row.add(repairOrderDTO.getOrderTotalCost() + "");
          row.add(repairOrderDTO.getSettledAmount() + "");
          row.add(repairOrderDTO.getDebt() + "");
          row.add(repairOrderDTO.getOrderProfit() + "");
          row.add(repairOrderDTO.getOrderProfitPercent());
          row.add(repairOrderDTO.getOrderDiscount() + "");
          rows.add(row);
        }
      }
    } else if("sale".equals(pageType)) {
      List<OrderSearchResultDTO> orderSearchResultDTOs = getOrderSearchResultDTOs();
      if(CollectionUtils.isNotEmpty(orderSearchResultDTOs)) {
        for(OrderSearchResultDTO orderSearchResultDTO : orderSearchResultDTOs) {
          List<String> row = new ArrayList<String>();
          row.add(orderSearchResultDTO.getVestDateStr() == null ? "" : orderSearchResultDTO.getVestDateStr());
          row.add(orderSearchResultDTO.getReceiptNo() == null ? "" : orderSearchResultDTO.getReceiptNo());
          row.add(orderSearchResultDTO.getOrderTypeValue() == null ? "" : orderSearchResultDTO.getOrderTypeValue());
          row.add(orderSearchResultDTO.getCustomerOrSupplierName() == null ? "" : orderSearchResultDTO.getCustomerOrSupplierName());
          row.add(orderSearchResultDTO.getOrderContent() == null ? "" : orderSearchResultDTO.getOrderContent());
          row.add(orderSearchResultDTO.getProductTotal() == null ? "0.0" : orderSearchResultDTO.getProductTotal().toString());
          row.add(orderSearchResultDTO.getProductTotalCostPrice() == null ? "0.0" : orderSearchResultDTO.getProductTotalCostPrice().toString());
          row.add(orderSearchResultDTO.getOtherIncomeTotal() == null ? "0.0" : orderSearchResultDTO.getOtherIncomeTotal().toString());
          row.add(orderSearchResultDTO.getOtherTotalCostPrice() == null ? "0.0" : orderSearchResultDTO.getOtherTotalCostPrice().toString());
          row.add(orderSearchResultDTO.getAmount() == null ? "0.0" : orderSearchResultDTO.getAmount().toString());
          row.add(orderSearchResultDTO.getSettled() == null ? "0.0" : orderSearchResultDTO.getSettled().toString());
          row.add(orderSearchResultDTO.getDebt() == null ? "0.0" : orderSearchResultDTO.getDebt().toString());
          row.add(orderSearchResultDTO.getTotalCostPrice() == null ? "0.0" : orderSearchResultDTO.getTotalCostPrice().toString());
          row.add(orderSearchResultDTO.getGrossProfit() == null ? "0.0" : orderSearchResultDTO.getGrossProfit().toString());
          row.add(orderSearchResultDTO.getGrossProfitRate() == null ? "0%" : orderSearchResultDTO.getGrossProfitRate().toString() + "%");
          row.add(orderSearchResultDTO.getDiscount() == null ? "0.0" : orderSearchResultDTO.getDiscount().toString());
          rows.add(row);
        }
      }
    } else if("wash".equals(pageType)) {
      List<ItemIndexDTO> itemIndexDTOs = getItemIndexDTOs();
      if(CollectionUtils.isNotEmpty(itemIndexDTOs)) {
        for(ItemIndexDTO itemIndexDTO : itemIndexDTOs) {
          List<String> row = new ArrayList<String>();
          row.add(itemIndexDTO.getOrderTimeCreatedStr() == null ? "" : itemIndexDTO.getOrderTimeCreatedStr());
          row.add(itemIndexDTO.getOrderReceiptNo() == null ? "" : itemIndexDTO.getOrderReceiptNo());
          row.add(itemIndexDTO.getVehicle() == null ? "" : itemIndexDTO.getVehicle());
          row.add(itemIndexDTO.getItemName() == null ? "" : itemIndexDTO.getItemName());
          row.add(itemIndexDTO.getMemberCardName() == null ? "" : itemIndexDTO.getMemberCardName());
          row.add(itemIndexDTO.getAfterMemberDiscountOrderTotal() == null ? "0.0" : itemIndexDTO.getAfterMemberDiscountOrderTotal().toString());
          row.add(itemIndexDTO.getSettledAmount() == null ? "0.0" : itemIndexDTO.getSettledAmount().toString());
          row.add(itemIndexDTO.getArrears() == null ? "0.0" : itemIndexDTO.getArrears().toString());
          row.add(itemIndexDTO.getDiscount() == null ? "0.0" : itemIndexDTO.getDiscount().toString());
          rows.add(row);
        }
      }
    }
    return  rows;
  }

  //车辆施工
  private List<RepairOrderDTO> getRepairOrderDTOs() {
    List<RepairOrderDTO> resultList = null;
    try {
      ExportUtil statUtil = new ExportUtil();
      ITxnService txnService = ServiceManager.getService(ITxnService.class);
      ISearchService searchService = ServiceManager.getService(ISearchService.class);
      Long queryDate = DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY, dateStr); //查询开始日期
      type = (StringUtil.isEmpty(type) == true ? QUERY_TYPE_DAY : type);
      arrayType = statUtil.getArrayTypeByType(arrayType); //排序类型:按照金额 或者时间排序
      Long startTime = statUtil.getTimeByQueryDateAndType(queryDate, type, "startTime");  //查询的开始时间
      Long endTime = statUtil.getTimeByQueryDateAndType(queryDate, type, "endTime");   //查询的结束时间
      String startTimeStr = DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE,startTime);
      String endTimeStr = DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE,endTime-1L);
      int size = 0;
      double total = 0.0;
      double pageTotal = 0.0;
      double repairCostTotal = 0.0;
      double repairSettleTotal = 0.0;
      double repairDebtTotal = 0.0;
      double repairDiscountTotal = 0.0;
      double repairProfitTotal = 0.0;

      List<String>  stringList = new ArrayList<String>();

      resultList = new ArrayList<RepairOrderDTO>();

      try {
        stringList = txnService.getRepairOrderDTOListByVestDate(shopId, startTime, endTime, OrderStatus.REPAIR_SETTLED);
      } catch (Exception e) {
        LOG.error(e.getMessage(),e);
      }
      size = statUtil.getIntValueByIndex2(stringList, 0); //获得当前查询日期内的单据条数
      total = statUtil.getDoubleValueByIndex2(stringList, 2); //获得当前查询日期内的单据总和

      if(size > 0) {
        //查询时间使用单据时间vest_date
        List<RepairOrderDTO> repairOrderDTOList = txnService.getRepairOrderDTOList(shopId, startTime, endTime, getPage().getCurrentPage(), getPage().getPageSize(),arrayType,OrderStatus.REPAIR_SETTLED);

        StringBuilder idString = new StringBuilder();
        String strTmp = "";
        idString.append(" ( ");

        if (CollectionUtils.isNotEmpty(repairOrderDTOList)){

          Map<Long, RepairOrderDTO> repairOrderDTOMap = new HashMap<Long, RepairOrderDTO>();

          for (RepairOrderDTO roDto : repairOrderDTOList) {
            if (roDto == null && roDto.getId() == null) {
              LOG.error("营业统计:获得施工单详细列表,单据为空" + roDto.toString());
              continue;
            }
            idString.append(roDto.getId().toString()).append(" ,");
            repairOrderDTOMap.put(roDto.getId(), roDto);
          }
          strTmp = idString.substring(0, idString.length() - 1);
          strTmp = strTmp + " ) ";

          arrayType = statUtil.getItemArrayType(arrayType);//获得对item_index表的排序sql语句

          List<ItemIndexDTO> itemIndexDTOList = searchService.getRepairOrderItemIndexList(shopId,strTmp, arrayType);

          Map<Long, List<ItemIndexDTO>> itemIndexMap = new HashMap<Long, List<ItemIndexDTO>>();
          for (ItemIndexDTO itemIndexDTO : itemIndexDTOList) {
            if (itemIndexDTO == null) {
              continue;
            }
            if (repairOrderDTOMap.containsKey(itemIndexDTO.getOrderId())) {
              if (itemIndexMap.containsKey(itemIndexDTO.getOrderId())) {
                List<ItemIndexDTO> itemIndexDTOs = itemIndexMap.get(itemIndexDTO.getOrderId());
                itemIndexDTOs.add(itemIndexDTO);
                itemIndexMap.remove(itemIndexDTO.getOrderId());
                itemIndexMap.put(itemIndexDTO.getOrderId(), itemIndexDTOs);
              } else {
                List<ItemIndexDTO> itemIndexDTOs = new ArrayList<ItemIndexDTO>();
                itemIndexDTOs.add(itemIndexDTO);
                itemIndexMap.put(itemIndexDTO.getOrderId(), itemIndexDTOs);
              }
            }
          }

          for (RepairOrderDTO repairOrderDTO : repairOrderDTOList) {
            Long orderId = repairOrderDTO.getId();
            List<ItemIndexDTO> itemIndexDTOs = itemIndexMap.get(orderId);
            double serviceTotal = 0.0; //施工单施工总费用
            double salesTotal = 0.0;  //施工单销售总费用
            double serviceTotalCost = 0.0; //施工单中的施工 总工时成本
            double salesTotalCost = 0.0;//施工单中的销售总成本
            double orderProfit = 0.0; //施工单毛利
            double repairAfterMemberDiscountTotal = 0.0; //施工单打折后总额
//            StringBuilder serviceContent = new StringBuilder();
//            StringBuilder salesContent = new StringBuilder();
            StringBuffer productNames = new StringBuffer();
            repairOrderDTO.setBrand(repairOrderDTO.getId().toString());

            if (CollectionUtils.isNotEmpty(itemIndexDTOs)) {
              for (int i = 0; i < itemIndexDTOs.size(); i++) {
                ItemIndexDTO itemIndexDTO = itemIndexDTOs.get(i);

                //施工item
                if (itemIndexDTO.getItemType() == ItemTypes.SERVICE) {
//                  serviceContent.append(itemIndexDTO.getItemName()).append("(").append(itemIndexDTO.getItemPrice() == null ? 0 : itemIndexDTO.getItemPrice()).append("元),");
                  serviceTotal += (itemIndexDTO.getItemPrice() == null ? 0 : itemIndexDTO.getItemPrice());
                  if (itemIndexDTO.getTotalCostPrice() != null) {
                    serviceTotalCost += itemIndexDTO.getTotalCostPrice();
                  }
                } else if (itemIndexDTO.getItemType() == ItemTypes.MATERIAL) {

                  productNames.append(itemIndexDTO.getItemName() + (StringUtils.isNotBlank(itemIndexDTO.getItemName())?",":""));
                  //销售item
                  if (itemIndexDTO.getItemCount() != null && itemIndexDTO.getItemPrice() != null) {
//                    salesContent.append(itemIndexDTO.getItemName()).append("(").
//                        append(itemIndexDTO.getItemPrice()).append("*").append(itemIndexDTO.getItemCount());
//                    if (!StringUtil.isEmpty(itemIndexDTO.getUnit())) {
//                      salesContent.append(itemIndexDTO.getUnit());
//                    }
//                    salesContent.append("),");
                    salesTotal += (itemIndexDTO.getItemPrice() * itemIndexDTO.getItemCount());
                  } else {
                    LOG.error("itemIndexDTO toString:" + itemIndexDTO.toString());
                  }

                  if (itemIndexDTO.getTotalCostPrice() != null) {
                    salesTotalCost += itemIndexDTO.getTotalCostPrice();
                  }
                }
              }
            } else {
              LOG.error("itemIndexDTO toString:" + repairOrderDTO.toString());
            }

//            if (serviceContent.length() <= 1) {
//              repairOrderDTO.setServiceContent(serviceContent.toString());
//              repairOrderDTO.setServiceContentStr(serviceContent.toString());
//            } else if (serviceContent.length() < 5) {
//              repairOrderDTO.setServiceContent(serviceContent.toString().substring(0, serviceContent.length() - 1));
//              repairOrderDTO.setServiceContentStr(serviceContent.toString().substring(0, serviceContent.length() - 1));
//            } else {
//              repairOrderDTO.setServiceContentStr(serviceContent.toString().substring(0, serviceContent.length() - 1));
//              repairOrderDTO.setServiceContent(serviceContent.toString().substring(0, 5) + "...");
//            }
//
//            if (salesContent.length() <= 1) {
//              repairOrderDTO.setSalesContent(salesContent.toString());
//              repairOrderDTO.setSalesContentStr(salesContent.toString());
//            } else if (salesContent.length() < 5) {
//              repairOrderDTO.setSalesContent(salesContent.toString().substring(0, salesContent.length() - 1));
//              repairOrderDTO.setSalesContentStr(salesContent.toString().substring(0, salesContent.length() - 1));
//            } else {
//              repairOrderDTO.setSalesContentStr(salesContent.toString().substring(0, salesContent.length() - 1));
//              repairOrderDTO.setSalesContent(salesContent.toString().substring(0, 5) + "...");
//            }

            repairOrderDTO.setProductNames(productNames.toString());
            repairOrderDTO.setServiceTotal(serviceTotal);
            repairOrderDTO.setServiceTotalCost(serviceTotalCost);
            repairOrderDTO.setSalesTotal(salesTotal);
            repairOrderDTO.setSalesTotalCost(salesTotalCost);
            repairOrderDTO.setOrderTotalCost(repairOrderDTO.getTotalCostPrice());

            //实收和欠款从receivable表拿
            ReceivableDTO receivableDTO = txnService.getReceivableByShopIdAndOrderTypeAndOrderId(repairOrderDTO.getShopId(), OrderTypes.REPAIR, repairOrderDTO.getId());
            if (receivableDTO != null) {
              repairOrderDTO.setDebt(receivableDTO.getDebt());
              repairOrderDTO.setSettledAmount(receivableDTO.getSettledAmount());
              repairOrderDTO.setAfterMemberDiscountTotal(receivableDTO.getAfterMemberDiscountTotal());
              if(null != receivableDTO.getAfterMemberDiscountTotal())
              {
                receivableDTO.setAfterMemberDiscountTotal(receivableDTO.getAfterMemberDiscountTotal());
              }
              else
              {
                receivableDTO.setAfterMemberDiscountTotal(receivableDTO.getTotal());
              }
            } else {
              repairOrderDTO.setDebt(0.0);
              repairOrderDTO.setSettledAmount(repairOrderDTO.getTotal());
              repairOrderDTO.setAfterMemberDiscountTotal(repairOrderDTO.getTotal());
            }

            orderProfit = repairOrderDTO.getDebt() + repairOrderDTO.getSettledAmount() - repairOrderDTO.getOrderTotalCost();
            repairOrderDTO.setOrderProfit(orderProfit);
            repairOrderDTO.setOrderDiscount(repairOrderDTO.getAfterMemberDiscountTotal() - repairOrderDTO.getDebt() - repairOrderDTO.getSettledAmount());
            double orderProfitTmp = (orderProfit * 100) / (repairOrderDTO.getAfterMemberDiscountTotal());
            BigDecimal bigDecimal = null;
            try {
              bigDecimal = new BigDecimal(orderProfitTmp);
            } catch (Exception e) {
              bigDecimal = new BigDecimal(0.0);
            }

            pageTotal += repairOrderDTO.getAfterMemberDiscountTotal();
            repairCostTotal += repairOrderDTO.getOrderTotalCost();
            repairSettleTotal += repairOrderDTO.getSettledAmount();
            repairDebtTotal += repairOrderDTO.getDebt();
            repairDiscountTotal += repairOrderDTO.getOrderDiscount();
            repairProfitTotal += orderProfit;

            orderProfitTmp = bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
            repairOrderDTO.setOrderProfitPercent(String.valueOf(orderProfitTmp) + "%");
            resultList.add(repairOrderDTO);
          }
        }
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(),e);
    }
   return resultList;
  }

  //商品销售
  private  List<OrderSearchResultDTO> getOrderSearchResultDTOs() {
    List<OrderSearchResultDTO> orderSearchResultDTOList = null;
    try {
      ExportUtil statUtil = new ExportUtil();
      ITxnService txnService = ServiceManager.getService(ITxnService.class);
      ISearchService searchService = ServiceManager.getService(ISearchService.class);

      Long queryDate = DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY, dateStr); //查询开始日期
      type = (StringUtil.isEmpty(type) == true ? QUERY_TYPE_DAY : type);
      arrayType = statUtil.getArrayTypeByType(arrayType); //排序类型:按照金额 或者时间排序
      Long startTime = statUtil.getTimeByQueryDateAndType(queryDate, type, "startTime");  //查询的开始时间
      Long endTime = statUtil.getTimeByQueryDateAndType(queryDate, type, "endTime");   //查询的结束时间
      String startTimeStr = DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE,startTime);
      String endTimeStr = DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE,endTime-1L);
      double total = 0.0; //单据总计
      double pageTotal = 0.0; //本页小计单据总额
      double memberDiscountTotal = 0.0; //折后总额
      //总条数和总金额 使用 count 和 sum进行查询

      int size = 0;

      List<String>  stringList = new ArrayList<String>();
      try {
        stringList = txnService.getSalesOrderCountAndSum(shopId, startTime, endTime);
      } catch (Exception e) {
        LOG.error(e.getMessage(),e);
      }
      size = CollectionUtils.isEmpty(stringList)? 0 :Integer.valueOf(stringList.get(0)); //获得当前查询日期内的单据条数
      total = (Double.valueOf(CollectionUtils.isEmpty(stringList)? 0D :Double.valueOf(stringList.get(2)))); //获得当前查询日期内的单据总和
      size += CollectionUtils.isEmpty(stringList)? 0 :Integer.valueOf(stringList.get(3));
      total -=  CollectionUtils.isEmpty(stringList)? 0D :Double.valueOf(stringList.get(4));
      String str = "";
      double saleCostTotal = 0.0;//销售单成本
      double saleSettleTotal = 0.0; //销售单实收
      double saleDebtTotal = 0.0;   //销售单欠款
      double saleDiscountTotal = 0.0; //销售单优惠
      double saleProfitTotal = 0.0;  //销售单毛利
      double saleAfterMemberDiscountTotal = 0.0;//销售折后总额
      if(size > 0) {
        try {
          //使用归属时间进行查询vest_date
          orderSearchResultDTOList = txnService.getSalesOrderDTOList(shopId, startTime, endTime, getPage().getCurrentPage(), getPage().getPageSize(), arrayType);
        } catch (Exception ex) {
          LOG.error(ex.getMessage(),ex);
        }

        arrayType = statUtil.getItemArrayType(arrayType);

        if(CollectionUtils.isNotEmpty(orderSearchResultDTOList)){
          for(OrderSearchResultDTO orderSearchResultDTO : orderSearchResultDTOList) {

            List<OrderIndexDTO> orderIndexDTOList = searchService.getOrderIndexDTOByOrderId(shopId, orderSearchResultDTO.getOrderId());
            List<ItemIndexDTO> itemIndexDTOList = searchService.getSalesOrderItemIndexList(shopId, " ( " + orderSearchResultDTO.getOrderIdStr() + " ) ", arrayType);

            if (CollectionUtils.isNotEmpty(itemIndexDTOList)) {
              StringBuffer productNames = new StringBuffer();

              for (ItemIndexDTO itemIndexDTO : itemIndexDTOList) {

                if(StringUtils.isNotBlank(itemIndexDTO.getItemName()) && itemIndexDTO.getItemType().equals(ItemTypes.MATERIAL))
                {
                  productNames.append(itemIndexDTO.getItemName()+(StringUtils.isNotBlank(itemIndexDTO.getItemName())?",":""));
                }
              }
              orderSearchResultDTO.setProductNames(productNames.toString());
            }

            if (CollectionUtils.isNotEmpty(orderIndexDTOList)) {
              OrderIndexDTO orderIndexDTO = orderIndexDTOList.get(0);
              orderSearchResultDTO.setOrderContent(orderIndexDTO.getOrderContent());

            } else {
              if (CollectionUtils.isNotEmpty(itemIndexDTOList)) {
                StringBuffer orderContent = new StringBuffer();

                for (ItemIndexDTO itemIndexDTO : itemIndexDTOList) {

                  if (itemIndexDTO.getItemCount() != null && itemIndexDTO.getItemPrice() != null) {
                    orderContent.append(itemIndexDTO.getItemName()).append("(").
                        append(itemIndexDTO.getItemPrice()).append("*").append(itemIndexDTO.getItemCount());
                    if (!StringUtil.isEmpty(itemIndexDTO.getUnit())) {
                      orderContent.append(itemIndexDTO.getUnit());
                    }
                    orderContent.append("),");
                  }
                }
                orderSearchResultDTO.setOrderContent(orderContent.toString());
              }
            }

            ReceivableDTO receivableDTO = txnService.getReceivableDTOByShopIdAndOrderId(shopId, orderSearchResultDTO.getOrderId());
            if (OrderTypes.SALE.getName().equals(orderSearchResultDTO.getOrderTypeValue())) {

              if (receivableDTO != null) {
                orderSearchResultDTO.setSettled(NumberUtil.toReserve(receivableDTO.getSettledAmount(), NumberUtil.MONEY_PRECISION));
                orderSearchResultDTO.setDebt(NumberUtil.toReserve(receivableDTO.getDebt(),NumberUtil.MONEY_PRECISION));
                orderSearchResultDTO.setDiscount(NumberUtil.toReserve(receivableDTO.getDiscount(),NumberUtil.MONEY_PRECISION));
                orderSearchResultDTO.setGrossProfit(orderSearchResultDTO.getSettled() + orderSearchResultDTO.getDebt() -NumberUtil.doubleVal(orderSearchResultDTO.getTotalCostPrice()));
                orderSearchResultDTO.setGrossProfit(NumberUtil.toReserve(orderSearchResultDTO.getGrossProfit(),NumberUtil.MONEY_PRECISION));

                if (NumberUtil.toReserve(orderSearchResultDTO.getSettled() + orderSearchResultDTO.getDebt(),NumberUtil.MONEY_PRECISION) == 0.0) {
                  orderSearchResultDTO.setGrossProfitRate(0D);
                } else {
                  orderSearchResultDTO.setGrossProfitRate(NumberUtil.toReserve(orderSearchResultDTO.getGrossProfit() * 100 / (orderSearchResultDTO.getSettled() + orderSearchResultDTO.getDebt()), 1));
                }
                orderSearchResultDTO.setAfterMemberDiscountTotal(null!=receivableDTO.getAfterMemberDiscountTotal()?receivableDTO.getAfterMemberDiscountTotal():receivableDTO.getTotal());
              }
              else
              {
                orderSearchResultDTO.setAfterMemberDiscountTotal(orderSearchResultDTO.getAmount());
              }

              orderSearchResultDTO.setProductTotal(NumberUtil.toReserve(orderSearchResultDTO.getAfterMemberDiscountTotal() - orderSearchResultDTO.getOtherIncomeTotal(),NumberUtil.MONEY_PRECISION));
              orderSearchResultDTO.setProductTotalCostPrice(NumberUtil.toReserve(orderSearchResultDTO.getTotalCostPrice() - orderSearchResultDTO.getOtherTotalCostPrice(),NumberUtil.MONEY_PRECISION));

              saleCostTotal += orderSearchResultDTO.getTotalCostPrice();
              saleSettleTotal += orderSearchResultDTO.getSettled();
              saleDebtTotal += orderSearchResultDTO.getDebt();
              saleDiscountTotal += orderSearchResultDTO.getDiscount();
              saleProfitTotal += orderSearchResultDTO.getGrossProfit();
              pageTotal += orderSearchResultDTO.getAmount();
              saleAfterMemberDiscountTotal += orderSearchResultDTO.getAfterMemberDiscountTotal();
            } else {
              orderSearchResultDTO.setTotalCostPrice(0- orderSearchResultDTO.getTotalCostPrice());
              orderSearchResultDTO.setAmount(0- orderSearchResultDTO.getAmount());
              orderSearchResultDTO.setProductTotal(orderSearchResultDTO.getAmount());
              orderSearchResultDTO.setProductTotalCostPrice(orderSearchResultDTO.getTotalCostPrice());

              if (receivableDTO != null) {
                orderSearchResultDTO.setSettled(receivableDTO.getSettledAmount());
                orderSearchResultDTO.setDebt(receivableDTO.getDebt());
                orderSearchResultDTO.setDiscount(receivableDTO.getDiscount());
                orderSearchResultDTO.setGrossProfit(orderSearchResultDTO.getSettled() + orderSearchResultDTO.getDebt() - orderSearchResultDTO.getTotalCostPrice());

                if (NumberUtil.toReserve(orderSearchResultDTO.getSettled() + orderSearchResultDTO.getDebt(),NumberUtil.MONEY_PRECISION) == 0.0) {
                  orderSearchResultDTO.setGrossProfitRate(0D);
                } else {
                  orderSearchResultDTO.setGrossProfitRate(NumberUtil.toReserve(orderSearchResultDTO.getGrossProfit() * 100 / (0 - orderSearchResultDTO.getSettled() - orderSearchResultDTO.getDebt()), 1));
                }
              }
              saleCostTotal += orderSearchResultDTO.getTotalCostPrice();
              saleSettleTotal += orderSearchResultDTO.getSettled();
              saleDebtTotal += orderSearchResultDTO.getDebt();
              saleDiscountTotal += orderSearchResultDTO.getDiscount();
              saleProfitTotal += orderSearchResultDTO.getGrossProfit();
              pageTotal += orderSearchResultDTO.getAmount();
              saleAfterMemberDiscountTotal += orderSearchResultDTO.getAmount();

            }
          }
        }
      }

    } catch (Exception e) {
      LOG.error(e.getMessage(),e);
    }
    return  orderSearchResultDTOList;
  }

  //洗车
  private  List<ItemIndexDTO> getItemIndexDTOs() {
    List<ItemIndexDTO> itemIndexDTOList = null;
    try {
      ExportUtil statUtil = new ExportUtil();
      ISearchService searchService = ServiceManager.getService(ISearchService.class);
      Long queryDate = DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY, dateStr); //查询开始日期
      type = (StringUtil.isEmpty(type) == true ? QUERY_TYPE_DAY : type);
      arrayType = statUtil.getArrayTypeByType(arrayType); //排序类型:按照金额 或者时间排序
      Long startTime = statUtil.getTimeByQueryDateAndType(queryDate, type, "startTime");  //查询的开始时间
      Long endTime = statUtil.getTimeByQueryDateAndType(queryDate, type, "endTime");   //查询的结束时间
      arrayType = statUtil.getItemArrayType(arrayType); //获得对item_type表的排序sql语句
      String startTimeStr = DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE,startTime);
      String endTimeStr = DateUtil.convertDateLongToDateString(DateUtil.YEAR_MONTH_DATE,endTime-1L);
      //总条数和总金额 使用 count 和 sum进行查询
      List<String> washStringList = searchService.getWashItemTotal(shopId, startTime, endTime);
      int size = statUtil.getIntValueByIndex2(washStringList, 0); //获得当前查询日期内的单据条数
      Double total = statUtil.getDoubleValueByIndex2(washStringList, 2); //获得当前查询日期内的单据总和

      if (size > 0) {
        itemIndexDTOList = searchService.getWashOrderItemIndexList(shopId, startTime, endTime, getPage().getCurrentPage(), getPage().getPageSize(), arrayType);
      }
    } catch (Exception ex) {
      LOG.error(ex.getMessage(), ex);
    }
      Set<Long> orderIdSet  = new HashSet();
      Set<Long> serviceHistoryIds = new HashSet<Long>();

      Long shopId = null;
      Map<Long, ReceivableDTO> receivableDTOMap = null;
      Map<Long, WashBeautyOrderDTO> washBeautyOrderDTOMap = new HashMap<Long, WashBeautyOrderDTO>();

      if(CollectionUtils.isNotEmpty(itemIndexDTOList)) {
          for(ItemIndexDTO itemIndexDTO :itemIndexDTOList){
              shopId = itemIndexDTO.getShopId();
              if (itemIndexDTO.getOrderType() == OrderTypes.WASH_BEAUTY) {
                  orderIdSet.add(itemIndexDTO.getOrderId());
              }
          }

          if(CollectionUtils.isNotEmpty(orderIdSet)) {
              List<WashBeautyOrderDTO> washBeautyOrderDTOList = ServiceManager.getService(ITxnService.class).getWashBeautyOrdersDetailByShopIdAndOrderIds(shopId, orderIdSet.toArray(new Long[orderIdSet.size()]));
              for (WashBeautyOrderDTO beautyOrderDTO : washBeautyOrderDTOList) {
                  //serviceIds
                  String orderContent = "";
                  if (CollectionUtils.isNotEmpty(beautyOrderDTO.getWashBeautyOrderItemDTOList())) {
                      for (WashBeautyOrderItemDTO itemDTO : beautyOrderDTO.getWashBeautyOrderItemDTOList()) {
                          serviceHistoryIds.add(itemDTO.getServiceHistoryId());
                      }
                      Map<Long, ServiceHistoryDTO> serviceHistoryDTOMap = ServiceManager.getService(IServiceHistoryService.class).getServiceHistoryByServiceHistoryIdSet(shopId, serviceHistoryIds);
                      ServiceHistoryDTO serviceHistoryDTO = null;
                      for (WashBeautyOrderItemDTO itemDTO : beautyOrderDTO.getWashBeautyOrderItemDTOList()) {
                          serviceHistoryDTO = serviceHistoryDTOMap.get(itemDTO.getServiceHistoryId());
                          if (serviceHistoryDTO != null) {
                              itemDTO.setServiceName(serviceHistoryDTO.getName());
                              orderContent += (serviceHistoryDTO.getName() + "(" + (itemDTO.getConsumeTypeStr() == ConsumeType.TIMES?"计次划卡":(NumberUtil.doubleVal(itemDTO.getPrice()) +"元")) + ");");
                          }
                      }
                      beautyOrderDTO.setOrderContent(orderContent);
                  }
                  washBeautyOrderDTOMap.put(beautyOrderDTO.getId(),beautyOrderDTO);
              }
              receivableDTOMap = ServiceManager.getService(ITxnService.class).getReceivableDTOByShopIdAndArrayOrderId(shopId, orderIdSet.toArray(new Long[orderIdSet.size()]));
          }


          for (ItemIndexDTO itemIndexDTO : itemIndexDTOList) {

              if (itemIndexDTO.getOrderType() == OrderTypes.WASH_BEAUTY) {
                  itemIndexDTO.setVehicleYear(itemIndexDTO.getOrderId().toString());
                  ReceivableDTO receivableDTO = receivableDTOMap.get(itemIndexDTO.getOrderId());
                  WashBeautyOrderDTO washBeautyOrderDTO = washBeautyOrderDTOMap.get(itemIndexDTO.getOrderId());
                  if (receivableDTO == null || washBeautyOrderDTO == null) {
                      continue;
                  }
                  itemIndexDTO.setItemName(washBeautyOrderDTO.getOrderContent());
                  if (null == receivableDTO.getAfterMemberDiscountTotal()) {
                      receivableDTO.setAfterMemberDiscountTotal(receivableDTO.getTotal());
                  }
                  itemIndexDTO.setOrderReceiptNo(StringUtils.isEmpty(washBeautyOrderDTO.getReceiptNo()) ? "" : washBeautyOrderDTO.getReceiptNo());
                  itemIndexDTO.setSettledAmount(NumberUtil.doubleVal(receivableDTO.getSettledAmount()));
                  itemIndexDTO.setDiscount(NumberUtil.doubleVal(receivableDTO.getDiscount()));
                  itemIndexDTO.setArrears(NumberUtil.doubleVal(receivableDTO.getDebt()));
                  itemIndexDTO.setMemberCardName(receivableDTO.getMemberNo());

              } else {
                  itemIndexDTO.setAfterMemberDiscountOrderTotal(NumberUtil.doubleVal(itemIndexDTO.getAfterMemberDiscountOrderTotal()));
                  itemIndexDTO.setSettledAmount(NumberUtil.doubleVal(itemIndexDTO.getOrderTotalAmount()));
                  itemIndexDTO.setDiscount(0D);
                  itemIndexDTO.setArrears(0D);
              }
          }

      }
    return itemIndexDTOList;
  }

}
