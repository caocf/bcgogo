package com.bcgogo.stat;

import com.bcgogo.common.Pager;
import com.bcgogo.common.StringUtil;
import com.bcgogo.enums.*;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.search.dto.OrderIndexDTO;
import com.bcgogo.search.model.ItemIndex;
import com.bcgogo.search.service.IItemIndexService;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.stat.dto.BusinessAccountDTO;
import com.bcgogo.stat.service.IBusinessAccountService;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.dto.StatementAccount.StatementAccountConstant;
import com.bcgogo.txn.service.ICustomerDepositService;
import com.bcgogo.txn.service.IDepositOrderStatService;
import com.bcgogo.txn.service.IServiceHistoryService;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.txn.service.statementAccount.IStatementAccountService;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.user.service.CustomerService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.utils.BusinessAccountConstant;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.JsonUtil;
import com.bcgogo.utils.NumberUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 12-9-6
 * Time: 上午10:38
 * To change this template use File | Settings | File Templates.
 */
@Component
public class StatUtil {
  private static final Log LOG = LogFactory.getLog(StatUtil.class);

  public static final int PAGE_SIZE = 25;       //页面显示条数
  public static final int DEFAULT_PAGE_NO = 1;  //默认查询页码 1
  public static final int STRING_SIZE = 2;//默认查询到的list 大小为2
  public static final int STRING_SIZE2 = 3;//默认查询到的list 大小为3
  public static final int DEFAULT_SIZE = 0;//默认大小为0
  public static final String QUERY_TYPE_DAY = "day"; //营业统计按天查询
  public static final String QUERY_TYPE_MONTH = "month";//营业统计按月查询
  public static final String QUERY_TYPE_YEAR = "year"; //营业统计按年查询
  public static final String INCOME = "income"; //流水统计：收入
  public static final String EXPENDITURE = "expenditure"; //流水统计：支出
  public static final int QUERY_SIZE = 4;//默认查询到的list 大小为2


  /**
   * 根据排序类型获得对item_index表的排序sql语句
   *
   * @param arrayType
   * @return
   */
  public String getItemArrayType(String arrayType) {
    arrayType = arrayType.replaceAll("total", "order_total_amount");
    return arrayType;
  }

  /**
   * 根据查询时间 和查询类型 day month year获得开始时间或者结束时间
   *
   * @param queryDate
   * @param type
   * @param getType
   * @return
   */
  public Long getTimeByQueryDateAndType(Long queryDate, String type, String getType) {
    Calendar calendar = Calendar.getInstance();
    calendar.clear();
    calendar.setTimeInMillis(queryDate);
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH) + 1;
    int day = calendar.get(Calendar.DAY_OF_MONTH);

    calendar.set(year, month - 1, day, 0, 0, 0);
    long startTime = calendar.getTimeInMillis();
    long endTime = calendar.getTimeInMillis();

    if (type.equals(QUERY_TYPE_DAY)) {
      calendar.add(Calendar.DATE, 1);
      endTime = calendar.getTimeInMillis();
    } else if (type.equals(QUERY_TYPE_MONTH)) {
      calendar.set(year, month - 1, 1, 0, 0, 0);
      startTime = calendar.getTimeInMillis();
      calendar.add(Calendar.MONTH, 1);
      endTime = calendar.getTimeInMillis();
    } else if (type.equals(QUERY_TYPE_YEAR)) {
      calendar.set(year, 0, 1, 0, 0, 0);
      startTime = calendar.getTimeInMillis();
      calendar.add(Calendar.YEAR, 1);
      endTime = calendar.getTimeInMillis();
    }

    if (getType.equals("startTime")) {
      return startTime;
    }
    return endTime;
  }

  /**
   * 获取分页组件
   *
   * @param size
   * @param startPageNo
   * @param maxRows
   * @return
   * @throws Exception
   */
  public Pager getPager(int size, Integer startPageNo, Integer maxRows) throws Exception {
    //分页相关
    startPageNo = (startPageNo == null ? DEFAULT_PAGE_NO : startPageNo); //页数
    maxRows = (maxRows == null ? PAGE_SIZE : maxRows);//每页显示的条数
    if (size < 0) {
      size = 0;
    }
    if (startPageNo <= 0) {
      startPageNo = DEFAULT_PAGE_NO;
    }
    if (maxRows <= 0) {
      maxRows = PAGE_SIZE;
    }

    Pager pager = new Pager(size, startPageNo, maxRows);
    return pager;
  }


  /**
   * 根据单据列表 总成本 总实收 总欠款 总折扣 总利润 单据总和 本页单据总和 获得json String
   *
   * @param resultList
   * @param stringList
   * @return
   */
  public String getJsonStr(List resultList, List<String> stringList) {

    String jsonStr = JsonUtil.listToJsonNoQuote(resultList);
    jsonStr = jsonStr.substring(0, jsonStr.length() - 1);

    String json = JsonUtil.listToJson(stringList);

    if (jsonStr.length() == 1) {
      return jsonStr + json.substring(1, json.length() - 1);
    } else {
      return jsonStr + "," + json.substring(1, json.length() - 1);
    }
  }

  /**
   * 根据前台传递过来的排序类型 转换成需要的sql排序语句
   *
   * @param arrayType
   * @return
   */
  public String getArrayTypeByType(String arrayType) {

    if (StringUtil.isEmpty(arrayType)) {
      arrayType = "timeDesc";
    }
    if (arrayType.equals("timeDesc")) {
      arrayType = " order by created desc";
    } else if (arrayType.equals("timeAsc")) {
      arrayType = " order by created asc";
    } else if (arrayType.equals("moneyDesc")) {
      arrayType = " order by total desc";
    } else if (arrayType.equals("moneyAsc")) {
      arrayType = " order by total asc";
    } else {
      arrayType = " order by created desc";
    }
    return arrayType;
  }

  /**
   * 拿到查询结果的单据条数
   *
   * @param stringList
   * @param index
   * @return
   */
  public int getIntValueByIndex(List<String> stringList, int index) {
    if (stringList == null || stringList.size() != STRING_SIZE) {
      return DEFAULT_SIZE;
    }
    return Integer.valueOf(stringList.get(index)).intValue();
  }

  /**
   * 拿到查询结果的单据条数
   *
   * @param stringList
   * @param index
   * @return
   */
  public int getIntValueByIndex2(List<String> stringList, int index) {
    if (stringList == null || stringList.size() != STRING_SIZE2) {
      return DEFAULT_SIZE;
    }
    return Integer.valueOf(stringList.get(index)).intValue();
  }

  /**
   * 拿到查询结果的单据总和  （用于体现会员折扣统计的地方）
   *
   * @param stringList
   * @param index
   * @return
   */
  public double getDoubleValueByIndex2(List<String> stringList, int index) {
   if (stringList == null || stringList.size() != STRING_SIZE2) {
      return DEFAULT_SIZE;
    }
    return Double.valueOf(stringList.get(index)).doubleValue();
  }

  /**
   * 拿到查询结果的单据总和
   *
   * @param stringList
   * @param index
   * @return
   */
  public double getDoubleValueByIndex(List<String> stringList, int index) {
    if (stringList == null || stringList.size() != STRING_SIZE) {
      return DEFAULT_SIZE;
    }
    return Double.valueOf(stringList.get(index)).doubleValue();
  }

  /**
   * 根据前台传递的查询时间得到Long型时间
   *
   * @param dateStr
   * @param request
   * @return
   */
  public Long getTimeLongValue(String dateStr, HttpServletRequest request) {
    Long queryDate = null;
    try {
      queryDate = DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DAY, dateStr);
    } catch (ParseException e) {
      LOG.error("/statUtil.do");
      LOG.error("营业统计:查询日期出错");
      LOG.error(e.getMessage(), e);
      LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      queryDate = System.currentTimeMillis();
    }
    if(queryDate == null) {
      LOG.warn("/statUtil.do");
      LOG.warn("营业统计:查询日期解析错误,系统默认为当前系统时间");
      LOG.warn("日期:" + dateStr);
      LOG.warn("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      queryDate = System.currentTimeMillis();
    }

    return queryDate;
  }


  /**
   * 根据shop_id、开始时间 结束时间 排序类型 分页 查询收入历史记录列表
   *
   * @param shopId
   * @param startTime
   * @param endTime
   * @param arrayType
   * @param pager
   * @return
   * @throws Exception
   */
  public List<ReceptionRecordDTO> getReceptionRecordDTOList(long shopId, long startTime, long endTime, String arrayType, Pager pager,String type) {

    List<ReceptionRecordDTO> receptionRecordDTOList = null;
    try {
      ITxnService txnService = ServiceManager.getService(ITxnService.class);
      Long orderId = null;

      try {
        receptionRecordDTOList = txnService.getReceptionRecordByReceptionDate(shopId, startTime, endTime, pager);
      } catch (Exception ex) {
        LOG.error("/StatUtil.java");
        LOG.error("method=getReceptionRecordDTOList");
        LOG.error("流水统计:获取每天收入列表出错");
        LOG.error("shopId:" + shopId);
        LOG.error(ex.getMessage(), ex);
        return null;
      }

      if (CollectionUtils.isEmpty(receptionRecordDTOList)) {
        return null;
      }

      for (ReceptionRecordDTO receptionRecordDTO : receptionRecordDTOList) {
        orderId = receptionRecordDTO.getOrderId();
        getOrderInfoByOrderId(shopId, orderId, receptionRecordDTO);
      }
    } catch (Exception e) {
      LOG.error("/runningStat.do");
      LOG.error("method=getReceptionRecordDTOList");
      LOG.error("流水统计:获得收入详细出错");
      LOG.error("shopId:" + shopId);
      LOG.error(e.getMessage(), e);
    }
    return receptionRecordDTOList;
  }

  private void getOrderInfoByOrderId(long shopId, Long orderId , ReceptionRecordDTO receptionRecordDTO) {
    if (orderId == null) {
      return;
    }

    //营业外记账
    if(receptionRecordDTO.getOrderTypeEnum() == OrderTypes.BUSINESS_ACCOUNT) {
      IBusinessAccountService businessAccountService = ServiceManager.getService(IBusinessAccountService.class);
      receptionRecordDTO.setReceptionDateStr(DateUtil.dateLongToStr(receptionRecordDTO.getReceptionDate()));
      BusinessAccountDTO businessAccountDTO = businessAccountService.getBusinessAccountById(orderId);
      receptionRecordDTO.setOrderContent(businessAccountDTO.getContent());
      receptionRecordDTO.setOrderType(receptionRecordDTO.getOrderTypeEnum().getName());
      if(StringUtils.isNotEmpty(receptionRecordDTO.getMemo())){
        receptionRecordDTO.setOrderContent(receptionRecordDTO.getMemo());
      }else{
        receptionRecordDTO.setOrderContent(BusinessAccountConstant.OTHER_INCOME);
      }

      return;
    }else if(receptionRecordDTO.getOrderTypeEnum() == OrderTypes.CUSTOMER_STATEMENT_ACCOUNT) {
      receptionRecordDTO.setReceptionDateStr(DateUtil.dateLongToStr(receptionRecordDTO.getReceptionDate()));
      IStatementAccountService statementAccountService = ServiceManager.getService(IStatementAccountService.class);
      StatementAccountOrderDTO statementAccountOrderDTO = statementAccountService.getStatementAccountOrderById(receptionRecordDTO.getOrderId());
      if (statementAccountOrderDTO == null) {
        return;
      }
      if (statementAccountOrderDTO.getTotalReceivable() >= statementAccountOrderDTO.getTotalPayable()) {
        receptionRecordDTO.setOrderContent(OrderTypes.CUSTOMER_STATEMENT_ACCOUNT.getName() + StatementAccountConstant.RECEIVABLE);
      } else {
        receptionRecordDTO.setOrderContent(OrderTypes.CUSTOMER_STATEMENT_ACCOUNT.getName() + StatementAccountConstant.PAY);
      }

      receptionRecordDTO.setOrderType(OrderTypes.CUSTOMER_STATEMENT_ACCOUNT.getName());
      receptionRecordDTO.setCustomerName(statementAccountOrderDTO.getCustomerOrSupplier());
      return;
      // add by zhuj
    }else if(receptionRecordDTO.getOrderTypeEnum() == OrderTypes.DEPOSIT ){
      receptionRecordDTO.setOrderContent("客户付预收款");
      receptionRecordDTO.setOrderType("客户付预收款"); //TODO zhuj 需要改掉
      ICustomerDepositService customerDepositService = ServiceManager.getService(ICustomerDepositService.class);
      DepositOrderDTO depositOrderDTO = customerDepositService.getById(orderId);
      if (depositOrderDTO != null) {
        receptionRecordDTO.setReceptionDateStr(depositOrderDTO.getCreatedTime());
        CustomerService customerService = ServiceManager.getService(CustomerService.class);
        CustomerDTO customerDTO = customerService.getCustomerById(depositOrderDTO.getCustomerId());
        if (customerDTO != null) {
          receptionRecordDTO.setCustomerName(customerDTO.getName());
        }
      }
    }

    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    IItemIndexService itemIndexService = ServiceManager.getService(IItemIndexService.class);

    OrderIndexDTO orderIndexDTO = new OrderIndexDTO();

    List<OrderIndexDTO> orderIndexDTOList = searchService.getOrderIndexDTOByOrderId(shopId, orderId);
    List<ItemIndex> itemIndexList = itemIndexService.getItemIndexDTOByOrderId(shopId, orderId);

    StringBuffer productNames = new StringBuffer();
    if (CollectionUtils.isNotEmpty(itemIndexList))
    {
      for(ItemIndex item : itemIndexList)
      {
        if(StringUtils.isNotBlank(item.getItemName()) && (ItemTypes.MATERIAL.equals(item.getItemTypeEnum()) || ItemTypes.SERVICE.equals(item.getItemTypeEnum())))
        {
          productNames.append(item.getItemName() + (StringUtils.isNotBlank(item.getItemName())?",":""));
        }
      }
    }

    if (CollectionUtils.isNotEmpty(orderIndexDTOList)) {
      orderIndexDTO = orderIndexDTOList.get(0);
    } else {

      if (CollectionUtils.isEmpty(itemIndexList)) {
        return;
      }

      StringBuffer orderContent = new StringBuffer();

      for (int index = 0; index < itemIndexList.size(); index++) {
        ItemIndex itemIndex = itemIndexList.get(0);

        if (index == 0) {
          orderIndexDTO.setCustomerOrSupplierName(itemIndex.getCustomerOrSupplierName());
          orderIndexDTO.setVehicle(itemIndex.getVehicle());
          orderIndexDTO.setCreationDate(itemIndex.getOrderTimeCreated());
          orderIndexDTO.setOrderTotalAmount(itemIndex.getOrderTotalAmount());
          orderIndexDTO.setOrderType(itemIndex.getOrderTypeEnum());
        }
        if (index == itemIndexList.size() - 1) {
          orderContent.append(itemIndex.getItemName());
        } else {
          orderContent.append(itemIndex.getItemName()).append(",");
        }
      }
      orderIndexDTO.setOrderContent(orderContent.toString());
    }
//    orderIndexDTO.setProductNames(productNames.toString());
    receptionRecordDTO.setReceptionDateStr(DateUtil.dateLongToStr(receptionRecordDTO.getReceptionDate()));
    receptionRecordDTO.setCustomerName(orderIndexDTO.getCustomerOrSupplierName());
    receptionRecordDTO.setVehicle(orderIndexDTO.getVehicle());
    //receptionRecordDTO.setDeposit(0.0);  //comment by zhuj 默认为0?
    receptionRecordDTO.setOrderType(receptionRecordDTO.getOrderTypeEnum().getName());
    receptionRecordDTO.setProductNames(productNames.toString());

    if (OrderStatus.REPAIR_REPEAL == receptionRecordDTO.getOrderStatusEnum() || OrderStatus.SALE_REPEAL == receptionRecordDTO.getOrderStatusEnum()) {
      receptionRecordDTO.setOrderContent("单据作废");
      receptionRecordDTO.setProductNames("单据作废");
    } else {
      receptionRecordDTO.setOrderContent(orderIndexDTO.getOrderContent());
    }



  }
   /**
   * 根据memberCardOrderDTOList拿到 本页单据总和、总成本、总利润、总实收、总欠款、总折扣
   * @param memberCardOrderDTOList
   * @return
   * @throws Exception
   */
  public List<String> getStringList(List<MemberCardOrderDTO> memberCardOrderDTOList,List<ItemIndexDTO> itemIndexDTOList) throws Exception {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    List<String> stringList = new ArrayList<String>();

    Double pageTotal = 0.0; //本页小计单据总额
    Double costTotal = 0.0;//单据成本总和
    Double settleTotal = 0.0; //单据实收总和
    Double debtTotal = 0.0;   //单据欠款总和
    Double discountTotal = 0.0; //单据折扣总和
    Double profitTotal = 0.0;  //单据毛利总和

    if(CollectionUtils.isNotEmpty(itemIndexDTOList)) {
      for (ItemIndexDTO itemIndexDTO : itemIndexDTOList) {
        pageTotal += itemIndexDTO.getOrderTotalAmount();
        if (itemIndexDTO.getOrderType() == OrderTypes.WASH_BEAUTY) {
          itemIndexDTO.setVehicleYear(itemIndexDTO.getOrderId().toString());
          List<OrderIndexDTO> orderIndexDTOList = searchService.getOrderIndexDTOByOrderId(itemIndexDTO.getShopId(), itemIndexDTO.getOrderId());
          if (CollectionUtils.isEmpty(orderIndexDTOList)) {
            LOG.error("/businessStat.do");
            LOG.error("method=getStringList");
            LOG.error("order_id:" + itemIndexDTO.getOrderId() + "orderIndex记录为空");
            LOG.error("shopId:" + itemIndexDTO.getShopId());
          } else {
            itemIndexDTO.setItemName(orderIndexDTOList.get(0).getOrderContent());
          }

          ReceivableDTO receivableDTO = txnService.getReceivableByShopIdOrderId(itemIndexDTO.getShopId(),itemIndexDTO.getOrderId());
          if (receivableDTO == null) {
            LOG.error("/businessStat.do");
            LOG.error("method=getStringList");
            LOG.error("营业统计:获取洗车单实收记录出错");
            LOG.error("itemIndexDTO:" + itemIndexDTO.toString());
            LOG.error("shopId:" + itemIndexDTO.getShopId());
            continue;
          }
          debtTotal += receivableDTO.getDebt();
          settleTotal += receivableDTO.getSettledAmount();
          discountTotal += (receivableDTO.getTotal() - receivableDTO.getDebt() - receivableDTO.getSettledAmount());
          profitTotal += receivableDTO.getDebt() + receivableDTO.getSettledAmount();
        }else{
          profitTotal += itemIndexDTO.getOrderTotalAmount();
          settleTotal += NumberUtil.doubleVal(itemIndexDTO.getOrderTotalAmount());
        }
      }
    }


    if(CollectionUtils.isNotEmpty(memberCardOrderDTOList)) {
      for (MemberCardOrderDTO memberCardOrderDTO : memberCardOrderDTOList) {
        pageTotal += memberCardOrderDTO.getTotal();

        ReceivableDTO receivableDTO = txnService.getReceivableByShopIdOrderId(memberCardOrderDTO.getShopId(), memberCardOrderDTO.getId());
        if (receivableDTO == null) {
          LOG.error("/businessStat.do");
          LOG.error("method=getMemberOrder");
          LOG.error("营业统计:获得会员详细列表，获取实收记录出错");
          LOG.error("member_card_order:" + memberCardOrderDTO.toString());
          LOG.error("shopId:" + memberCardOrderDTO.getShopId());
          memberCardOrderDTO.setSettledAmount(0d);
        } else {
          memberCardOrderDTO.setSettledAmount(receivableDTO.getSettledAmount());
          debtTotal += receivableDTO.getDebt();
          settleTotal += receivableDTO.getSettledAmount();
          discountTotal += (receivableDTO.getTotal() - receivableDTO.getDebt() - receivableDTO.getSettledAmount());
          profitTotal += receivableDTO.getDebt() + receivableDTO.getSettledAmount();
        }
      }
    }

    stringList.add(String.valueOf(NumberUtil.toReserve(costTotal, NumberUtil.MONEY_PRECISION)));
    stringList.add(String.valueOf(NumberUtil.toReserve(settleTotal, NumberUtil.MONEY_PRECISION)));
    stringList.add(String.valueOf(NumberUtil.toReserve(debtTotal, NumberUtil.MONEY_PRECISION)));
    stringList.add(String.valueOf(NumberUtil.toReserve(discountTotal, NumberUtil.MONEY_PRECISION)));
    stringList.add(String.valueOf(NumberUtil.toReserve(profitTotal, NumberUtil.MONEY_PRECISION)));
    stringList.add(String.valueOf(NumberUtil.toReserve(pageTotal, NumberUtil.MONEY_PRECISION)));

    return stringList;
  }


  /**
   * 根据itemindex拿到 本页单据总和、总成本、总利润、总实收、总欠款、总折扣
   * @param
   * @return
   * @throws Exception
   */
  public List<String> getWashBeautyOrderStringList(List<ItemIndexDTO> itemIndexDTOList) throws Exception {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    List<String> stringList = new ArrayList<String>();

    Double pageTotal = 0.0; //本页小计单据总额
    Double costTotal = 0.0;//单据成本总和
    Double settleTotal = 0.0; //单据实收总和
    Double debtTotal = 0.0;   //单据欠款总和
    Double discountTotal = 0.0; //单据折扣总和
    Double profitTotal = 0.0;  //单据毛利总和

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
        List<WashBeautyOrderDTO> washBeautyOrderDTOList = txnService.getWashBeautyOrdersDetailByShopIdAndOrderIds(shopId, orderIdSet.toArray(new Long[orderIdSet.size()]));
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
        receivableDTOMap = txnService.getReceivableDTOByShopIdAndArrayOrderId(shopId, orderIdSet.toArray(new Long[orderIdSet.size()]));
      }


      for (ItemIndexDTO itemIndexDTO : itemIndexDTOList) {

        if (itemIndexDTO.getOrderType() == OrderTypes.WASH_BEAUTY) {
          itemIndexDTO.setVehicleYear(itemIndexDTO.getOrderId().toString());
          ReceivableDTO receivableDTO = receivableDTOMap.get(itemIndexDTO.getOrderId());
          WashBeautyOrderDTO washBeautyOrderDTO = washBeautyOrderDTOMap.get(itemIndexDTO.getOrderId());
          if (receivableDTO == null || washBeautyOrderDTO == null) {
            continue;
          }
          itemIndexDTO.setOrderTotalAmount(NumberUtil.doubleVal(receivableDTO.getTotal()));
          itemIndexDTO.setItemName(washBeautyOrderDTO.getOrderContent());
          itemIndexDTO.setOrderReceiptNo(StringUtils.isEmpty(washBeautyOrderDTO.getReceiptNo()) ? "--" : washBeautyOrderDTO.getReceiptNo());
          itemIndexDTO.setSettledAmount(NumberUtil.doubleVal(receivableDTO.getSettledAmount()));
          itemIndexDTO.setDiscount(NumberUtil.toReserve(receivableDTO.getTotal() - receivableDTO.getDebt() - receivableDTO.getSettledAmount(),NumberUtil.MONEY_PRECISION));
          itemIndexDTO.setArrears(NumberUtil.doubleVal(receivableDTO.getDebt()));
          itemIndexDTO.setMemberCardName(receivableDTO.getMemberNo());
          debtTotal += receivableDTO.getDebt();
          settleTotal += receivableDTO.getSettledAmount();
          discountTotal += (receivableDTO.getTotal() - receivableDTO.getDebt() - receivableDTO.getSettledAmount());
          profitTotal += receivableDTO.getDebt() + receivableDTO.getSettledAmount();
        } else {
          profitTotal += itemIndexDTO.getOrderTotalAmount();
          settleTotal += NumberUtil.doubleVal(itemIndexDTO.getOrderTotalAmount());

          itemIndexDTO.setSettledAmount(NumberUtil.doubleVal(itemIndexDTO.getOrderTotalAmount()));
          itemIndexDTO.setDiscount(0D);
          itemIndexDTO.setArrears(0D);
          itemIndexDTO.setMemberCardName("--");
          itemIndexDTO.setOrderReceiptNo("--");
        }

        pageTotal += itemIndexDTO.getOrderTotalAmount();

      }

    }

    stringList.add(String.valueOf(NumberUtil.toReserve(costTotal, NumberUtil.MONEY_PRECISION)));
    stringList.add(String.valueOf(NumberUtil.toReserve(settleTotal, NumberUtil.MONEY_PRECISION)));
    stringList.add(String.valueOf(NumberUtil.toReserve(debtTotal, NumberUtil.MONEY_PRECISION)));
    stringList.add(String.valueOf(NumberUtil.toReserve(discountTotal, NumberUtil.MONEY_PRECISION)));
    stringList.add(String.valueOf(NumberUtil.toReserve(profitTotal, NumberUtil.MONEY_PRECISION)));
    stringList.add(String.valueOf(NumberUtil.toReserve(pageTotal, NumberUtil.MONEY_PRECISION)));

    return stringList;
  }


  /**
   * 根据返回结果集合，总计本页所需数据
   * @param receptionRecordDTOList
   * @param runningStatDTOList
   * @param payableHistoryRecordDTOList
   * @return
   * @throws Exception
   */
  public List<String> getRunningStringList(List<ReceptionRecordDTO> receptionRecordDTOList,List<RunningStatDTO> runningStatDTOList,
                                           List<PayableHistoryRecordDTO> payableHistoryRecordDTOList,String statType) throws Exception {

    IUserService userService = ServiceManager.getService(IUserService.class);

    List<String> stringList = new ArrayList<String>();

    double pageTotal = 0.0; //本页单据总额
    double pagePayTotal = 0.0;//本页实收或者实付总额
    double pageCashTotal = 0.0; //本页现金总额
    double pageChequeTotal = 0.0; //本页支票总额
    double pageUnionPayTotal = 0.0;//本页银联总额
    double pageMemberPayTotal = 0.0; //本页会员总额
    double pageDepositPayTotal = 0.0; //本页定金总和
    double pageDebtTotal = 0.0; //本页欠款总额
    double pageDiscountTotal = 0.0; //本页折扣总和
    double pageStrikeAmountTotal = 0.0;//本页冲账总和
    double pageCouponTotal = 0.0;//本页代金券总和
    if(CollectionUtils.isNotEmpty(receptionRecordDTOList)) {
      for (ReceptionRecordDTO receptionRecordDTO : receptionRecordDTOList) {
        pageTotal += NumberUtil.doubleVal(receptionRecordDTO.getOrderTotal());
        pageCashTotal += NumberUtil.doubleVal(receptionRecordDTO.getCash());
        pageChequeTotal += NumberUtil.doubleVal(receptionRecordDTO.getCheque());
        pageUnionPayTotal += NumberUtil.doubleVal(receptionRecordDTO.getBankCard());
        pageMemberPayTotal += NumberUtil.doubleVal(receptionRecordDTO.getMemberBalancePay());
        pageDebtTotal += NumberUtil.doubleVal(receptionRecordDTO.getRemainDebt());
        pagePayTotal += NumberUtil.doubleVal(receptionRecordDTO.getAmount());
        pageDiscountTotal += NumberUtil.doubleVal(receptionRecordDTO.getDiscount());
        pageStrikeAmountTotal += NumberUtil.doubleVal(receptionRecordDTO.getStrike());
        pageDepositPayTotal += NumberUtil.doubleVal(receptionRecordDTO.getDeposit());
        pageCouponTotal += NumberUtil.doubleVal(receptionRecordDTO.getCoupon());  //add by litao
      }
    }else if(CollectionUtils.isNotEmpty(runningStatDTOList)) {
      for (RunningStatDTO runningStatDTO : runningStatDTOList) {
        if (INCOME.equals(statType)) {
          pageTotal += runningStatDTO.getIncomeSum();
          pageCashTotal += NumberUtil.doubleVal(runningStatDTO.getCashIncome());
          pageChequeTotal += NumberUtil.doubleVal(runningStatDTO.getChequeIncome());
          pageUnionPayTotal += NumberUtil.doubleVal(runningStatDTO.getUnionPayIncome());
          pageDepositPayTotal += NumberUtil.doubleVal(runningStatDTO.getCustomerDepositExpenditure());
          pageMemberPayTotal += NumberUtil.doubleVal(runningStatDTO.getMemberPayIncome());
          pageDebtTotal += NumberUtil.doubleVal(runningStatDTO.getDebtNewIncome());
          pageDiscountTotal += NumberUtil.doubleVal(runningStatDTO.getDebtWithdrawalIncome());
          pageStrikeAmountTotal += NumberUtil.doubleVal(runningStatDTO.getStrikeAmountIncome());
          pageCouponTotal += NumberUtil.doubleVal(runningStatDTO.getCouponIncome());  //add by litao
        } else {
          pageTotal += runningStatDTO.getExpenditureSum();
          pageCashTotal += NumberUtil.doubleVal(runningStatDTO.getCashExpenditure());
          pageChequeTotal += NumberUtil.doubleVal(runningStatDTO.getChequeExpenditure());
          pageUnionPayTotal += NumberUtil.doubleVal(runningStatDTO.getUnionPayExpenditure());
          pageDepositPayTotal += NumberUtil.doubleVal(runningStatDTO.getDepositPayExpenditure());
          pageDebtTotal += NumberUtil.doubleVal(runningStatDTO.getDebtNewExpenditure());
          pageDiscountTotal += NumberUtil.doubleVal(runningStatDTO.getDebtWithdrawalExpenditure());
          pageStrikeAmountTotal += NumberUtil.doubleVal(runningStatDTO.getStrikeAmountExpenditure());
          pageCouponTotal += NumberUtil.doubleVal(runningStatDTO.getCouponExpenditure());  //add by litao
        }
      }
    }else if(CollectionUtils.isNotEmpty(payableHistoryRecordDTOList)) {
      for (PayableHistoryRecordDTO payableHistoryRecordDTO : payableHistoryRecordDTOList) {
        List<OrderIndexDTO> orderIndexDTOs = null;
        if(payableHistoryRecordDTO.getPurchaseInventoryId() != null) {
          orderIndexDTOs = ServiceManager.getService(ISearchService.class).getOrderIndexDTOByOrderId(payableHistoryRecordDTO.getShopId(), payableHistoryRecordDTO.getPurchaseInventoryId());
        }
        if(CollectionUtils.isNotEmpty(orderIndexDTOs)){
          payableHistoryRecordDTO.setCustomerName(CollectionUtil.getFirst(orderIndexDTOs).getCustomerOrSupplierName());
        }else if (payableHistoryRecordDTO.getSupplierId() != null) {
          SupplierDTO supplierDTO = userService.getSupplierById(payableHistoryRecordDTO.getSupplierId());
          payableHistoryRecordDTO.setCustomerName(supplierDTO.getName());
        }
        if (payableHistoryRecordDTO.getPaymentType() != null) {
          payableHistoryRecordDTO.setOrderType(payableHistoryRecordDTO.getPaymentType().getName());
        }

        pageTotal += NumberUtil.doubleVal(payableHistoryRecordDTO.getAmount());
        pageCashTotal += NumberUtil.doubleVal(payableHistoryRecordDTO.getCash());
        pageChequeTotal += NumberUtil.doubleVal(payableHistoryRecordDTO.getCheckAmount());
        pageUnionPayTotal += NumberUtil.doubleVal(payableHistoryRecordDTO.getBankCardAmount());
        pageDepositPayTotal += NumberUtil.doubleVal(payableHistoryRecordDTO.getDepositAmount());
        pageDiscountTotal += NumberUtil.doubleVal(payableHistoryRecordDTO.getDeduction());
        pageDebtTotal += NumberUtil.toReserve(NumberUtil.doubleVal(payableHistoryRecordDTO.getCreditAmount()),NumberUtil.MONEY_PRECISION);
        pagePayTotal += NumberUtil.doubleVal(payableHistoryRecordDTO.getActuallyPaid());
        if(PaymentTypes.INVENTORY_RETURN == payableHistoryRecordDTO.getPaymentType())
        {
          Double addPageStrikeAmountTotal= payableHistoryRecordDTO.getStrikeAmount();

          if(null == addPageStrikeAmountTotal)
          {
            addPageStrikeAmountTotal = 0D;
          }
          if(addPageStrikeAmountTotal<0)
          {
            addPageStrikeAmountTotal = -addPageStrikeAmountTotal;
          }

          pageStrikeAmountTotal +=  addPageStrikeAmountTotal;
        }

      }
    }

    stringList.add(String.valueOf(NumberUtil.toReserve(pageCouponTotal, NumberUtil.MONEY_PRECISION)));  //add by litao
    stringList.add(String.valueOf(NumberUtil.toReserve(pageTotal, NumberUtil.MONEY_PRECISION)));
    stringList.add(String.valueOf(NumberUtil.toReserve(pagePayTotal, NumberUtil.MONEY_PRECISION)));
    stringList.add(String.valueOf(NumberUtil.toReserve(pageCashTotal, NumberUtil.MONEY_PRECISION)));
    stringList.add(String.valueOf(NumberUtil.toReserve(pageUnionPayTotal, NumberUtil.MONEY_PRECISION)));
    stringList.add(String.valueOf(NumberUtil.toReserve(pageChequeTotal, NumberUtil.MONEY_PRECISION)));
    stringList.add(String.valueOf(NumberUtil.toReserve(pageMemberPayTotal, NumberUtil.MONEY_PRECISION)));
    stringList.add(String.valueOf(NumberUtil.toReserve(pageDepositPayTotal, NumberUtil.MONEY_PRECISION)));
    stringList.add(String.valueOf(NumberUtil.toReserve(pageDiscountTotal, NumberUtil.MONEY_PRECISION)));
    stringList.add(String.valueOf(NumberUtil.toReserve(pageDebtTotal, NumberUtil.MONEY_PRECISION)));
    stringList.add(String.valueOf(NumberUtil.toReserve(pageStrikeAmountTotal,NumberUtil.MONEY_PRECISION)));
    return stringList;
  }


   /**
   * 根据返回结果集合，总计本页所需数据
   * @param receptionRecordDTOList
   * @param
   * @param
   * @return
   * @throws Exception
   */
  public List<String> getRunningIncomeStringList(List<ReceptionRecordDTO> receptionRecordDTOList) throws Exception {

    IUserService userService = ServiceManager.getService(IUserService.class);

    List<String> stringList = new ArrayList<String>();

    double pageTotal = 0.0; //本页单据总额
    double pagePayTotal = 0.0;//本页实收或者实付总额
    double pageCashTotal = 0.0; //本页现金总额
    double pageChequeTotal = 0.0; //本页支票总额
    double pageUnionPayTotal = 0.0;//本页银联总额
    double pageMemberPayTotal = 0.0; //本页会员总额
    double pageDepositPayTotal = 0.0; //本页定金总和
    double pageDebtTotal = 0.0; //本页欠款总额
    double pageDiscountTotal = 0.0; //本页折扣总和
    double pageStrikeAmountTotal = 0.0;//本页冲账总和
    double pageCouponTotal = 0.0;//本页代金券总和
    if(CollectionUtils.isNotEmpty(receptionRecordDTOList)) {
      for (ReceptionRecordDTO receptionRecordDTO : receptionRecordDTOList) {

        if (receptionRecordDTO.getRecordNum() == 0) {
          receptionRecordDTO.setDiscount(NumberUtil.doubleVal(receptionRecordDTO.getOrderTotal()) - NumberUtil.doubleVal(receptionRecordDTO.getAmount()) - NumberUtil.doubleVal(receptionRecordDTO.getRemainDebt()));
          receptionRecordDTO.setDiscount(NumberUtil.toReserve(receptionRecordDTO.getDiscount(), NumberUtil.MONEY_PRECISION));
        }
        pageTotal += NumberUtil.doubleVal(receptionRecordDTO.getOrderTotal());
        pageCashTotal += NumberUtil.doubleVal(receptionRecordDTO.getCash());
        pageChequeTotal += NumberUtil.doubleVal(receptionRecordDTO.getCheque());
        pageUnionPayTotal += NumberUtil.doubleVal(receptionRecordDTO.getBankCard());
        pageMemberPayTotal += NumberUtil.doubleVal(receptionRecordDTO.getMemberBalancePay());
        pageDepositPayTotal += NumberUtil.doubleVal(receptionRecordDTO.getDeposit()); // add by zhuj 
        pageDebtTotal += NumberUtil.doubleVal(receptionRecordDTO.getRemainDebt());
        pagePayTotal += NumberUtil.doubleVal(receptionRecordDTO.getAmount());
        pageDiscountTotal += NumberUtil.doubleVal(receptionRecordDTO.getDiscount());
        pageStrikeAmountTotal += NumberUtil.doubleVal(receptionRecordDTO.getStrike());
        pageCouponTotal += NumberUtil.doubleVal(receptionRecordDTO.getCoupon());  //add by litao
      }
    }

    stringList.add(String.valueOf(NumberUtil.toReserve(pageCouponTotal, NumberUtil.MONEY_PRECISION)));  //add by litao
    stringList.add(String.valueOf(NumberUtil.toReserve(pageTotal, NumberUtil.MONEY_PRECISION)));
    stringList.add(String.valueOf(NumberUtil.toReserve(pagePayTotal, NumberUtil.MONEY_PRECISION)));
    stringList.add(String.valueOf(NumberUtil.toReserve(pageCashTotal, NumberUtil.MONEY_PRECISION)));
    stringList.add(String.valueOf(NumberUtil.toReserve(pageUnionPayTotal, NumberUtil.MONEY_PRECISION)));
    stringList.add(String.valueOf(NumberUtil.toReserve(pageChequeTotal, NumberUtil.MONEY_PRECISION)));
    stringList.add(String.valueOf(NumberUtil.toReserve(pageMemberPayTotal, NumberUtil.MONEY_PRECISION)));
    stringList.add(String.valueOf(NumberUtil.toReserve(pageDepositPayTotal, NumberUtil.MONEY_PRECISION)));
    stringList.add(String.valueOf(NumberUtil.toReserve(pageDiscountTotal, NumberUtil.MONEY_PRECISION)));
    stringList.add(String.valueOf(NumberUtil.toReserve(pageDebtTotal, NumberUtil.MONEY_PRECISION)));
    stringList.add(String.valueOf(NumberUtil.toReserve(pageStrikeAmountTotal,NumberUtil.MONEY_PRECISION)));
    return stringList;
  }

   /**
   * 拿到查询结果的单据条数
   *
   * @param stringList
   * @param index
   * @return
   */
  public int getIntValueForSale(List<String> stringList, int index) {
    if (stringList == null || stringList.size() != QUERY_SIZE) {
      return DEFAULT_SIZE;
    }
    return Integer.valueOf(stringList.get(index)).intValue();
  }


}
