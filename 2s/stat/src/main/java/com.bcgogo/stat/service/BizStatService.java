package com.bcgogo.stat.service;

import com.bcgogo.enums.ConsumeType;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.search.model.InventorySearchIndex;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.stat.dto.AssistantStatDTO;
import com.bcgogo.stat.dto.BizStatDTO;
import com.bcgogo.stat.model.AssistantStat;
import com.bcgogo.stat.model.BizStat;
import com.bcgogo.stat.model.StatDaoManager;
import com.bcgogo.stat.model.StatWriter;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.model.RepairOrder;
import com.bcgogo.txn.model.SalesOrder;
import com.bcgogo.txn.model.WashOrder;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.user.dto.SalesManDTO;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.RfTxnConstant;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Xiao Jian
 * Date: 12-1-6
 */

@Component
public class BizStatService implements IBizStatService {
  private static final Logger LOG = LoggerFactory.getLogger(BizStatService.class);

  @Autowired
  private StatDaoManager statDaoManager;

  @Override
  public BizStatDTO saveBizStat(BizStatDTO bizStatDTO) {
    if (bizStatDTO == null) return null;

    StatWriter writer = statDaoManager.getWriter();
    Object status = writer.begin();

    try {
      BizStat bizStat = new BizStat(bizStatDTO);

      writer.deleteBizStatByShopAndTypeAndYearAndMonthAndDayAndWeek(
          bizStat.getShopId(),
          bizStat.getStatType(),
          bizStat.getStatYear(),
          bizStat.getStatMonth(),
          bizStat.getStatDay(),
          bizStat.getStatWeek()
      );

      writer.save(bizStat);

      writer.commit(status);

      bizStatDTO.setId(bizStat.getId());

      return bizStatDTO;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public List<AssistantStatDTO> saveAssistantStat(List<AssistantStatDTO> assistantStatDTOlist) {
    if (CollectionUtils.isEmpty(assistantStatDTOlist)) {
      return null;
    }
    List<AssistantStatDTO> dtoList = new ArrayList<AssistantStatDTO>();
    AssistantStatDTO dto = new AssistantStatDTO();

    StatWriter writer = statDaoManager.getWriter();
    Object status = writer.begin();
    try {
      for (AssistantStatDTO assistantStatDTO : assistantStatDTOlist) {
        AssistantStat assistantStat = new AssistantStat(assistantStatDTO);
        writer.deleteAssistantStat(assistantStat.getShopId(), assistantStat.getAssistant(), assistantStat.getStatYear(),
            assistantStat.getStatMonth(), assistantStat.getStatDay(), assistantStat.getStatWeek());
        writer.save(assistantStat);
        dto = assistantStat.toDTO();
        dtoList.add(dto);
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }

    return dtoList;
  }


  @Override
  public BizStatDTO getBizStatById(long bizStatId) {
    StatWriter writer = statDaoManager.getWriter();

    BizStat bizStat = writer.getById(BizStat.class, bizStatId);

    if (bizStat == null) return null;
    return bizStat.toDTO();
  }

  @Override
  public List<BizStatDTO> getShopEachYearStatByType(long shopId, String statType) {
    StatWriter writer = statDaoManager.getWriter();

    List<BizStatDTO> bizStatDTOList = new ArrayList<BizStatDTO>();
    for (BizStat bizStat : writer.getShopEachYearStatByType(shopId, statType)) {
      bizStatDTOList.add(bizStat.toDTO());
    }

    return bizStatDTOList;
  }

  @Override
  public double getShopOneYearStatByTypeAndYear(long shopId, String statType, long statYear) {
    StatWriter writer = statDaoManager.getWriter();

    return writer.getShopOneYearStatByTypeAndYear(shopId, statType, statYear);
  }

  @Override
  public List<BizStatDTO> getShopOneYearStatByYear(long shopId, long statYear) {
    StatWriter writer = statDaoManager.getWriter();

    List<BizStatDTO> bizStatDTOList = new ArrayList<BizStatDTO>();
    for (BizStat bizStat : writer.getShopOneYearStatByYear(shopId, statYear)) {
      bizStatDTOList.add(bizStat.toDTO());
    }

    return bizStatDTOList;
  }

  @Override
  public List<BizStatDTO> getShopEachMonthInAYearStatByTypeAndYear(long shopId, String statType, long statYear) {
    StatWriter writer = statDaoManager.getWriter();

    List<BizStatDTO> bizStatDTOList = new ArrayList<BizStatDTO>();
    for (BizStat bizStat : writer.getShopEachMonthInAYearStatByTypeAndYear(shopId, statType, statYear)) {
      bizStatDTOList.add(bizStat.toDTO());
    }

    return bizStatDTOList;
  }

  @Override
  public double getShopOneMonthStatByTypeAndYearAndMonth(long shopId, String statType, long statYear, long statMonth) {
    StatWriter writer = statDaoManager.getWriter();

    return writer.getShopOneMonthStatByTypeAndYearAndMonth(shopId, statType, statYear, statMonth);
  }

  @Override
  public List<BizStatDTO> getShopOneMonthStatByYearAndMonth(long shopId, long statYear, long statMonth) {
    StatWriter writer = statDaoManager.getWriter();

    List<BizStatDTO> bizStatDTOList = new ArrayList<BizStatDTO>();
    for (BizStat bizStat : writer.getShopOneMonthStatByYearAndMonth(shopId, statYear, statMonth)) {
      bizStatDTOList.add(bizStat.toDTO());
    }

    return bizStatDTOList;
  }

  @Override
  public List<BizStatDTO> getShopEachDayInAMonthStatByTypeAndYearAndMonth(long shopId, String statType, long statYear, long statMonth) {
    StatWriter writer = statDaoManager.getWriter();

    List<BizStatDTO> bizStatDTOList = new ArrayList<BizStatDTO>();
    for (BizStat bizStat : writer.getShopEachDayInAMonthStatByTypeAndYearAndMonth(shopId, statType, statYear, statMonth)) {
      bizStatDTOList.add(bizStat.toDTO());
    }

    return bizStatDTOList;
  }

  @Override
  public double getShopOneDayStatByTypeAndYearAndMonthAndDay(long shopId, String statType, long statYear, long statMonth, long statDay) {
    StatWriter writer = statDaoManager.getWriter();

    return writer.getShopOneDayStatByTypeAndYearAndMonthAndDay(shopId, statType, statYear, statMonth, statDay);
  }

  @Override
  public List<BizStatDTO> getShopOneDayStatByYearAndMonthAndDay(long shopId, long statYear, long statMonth, long statDay) {
    StatWriter writer = statDaoManager.getWriter();

    List<BizStatDTO> bizStatDTOList = new ArrayList<BizStatDTO>();
    for (BizStat bizStat : writer.getShopOneDayStatByYearAndMonthAndDay(shopId, statYear, statMonth, statDay)) {
      bizStatDTOList.add(bizStat.toDTO());
    }

    return bizStatDTOList;
  }

  @Override
  public List<BizStatDTO> getShopEachWeekInAYearStatByTypeAndYear(long shopId, String statType, long statYear) {
    StatWriter writer = statDaoManager.getWriter();

    List<BizStatDTO> bizStatDTOList = new ArrayList<BizStatDTO>();
    for (BizStat bizStat : writer.getShopEachWeekInAYearStatByTypeAndYear(shopId, statType, statYear)) {
      bizStatDTOList.add(bizStat.toDTO());
    }

    return bizStatDTOList;
  }

  @Override
  public double getShopOneWeekStatByTypeAndYearAndWeek(long shopId, String statType, long statYear, long statWeek) {
    StatWriter writer = statDaoManager.getWriter();

    return writer.getShopOneWeekStatByTypeAndYearAndWeek(shopId, statType, statYear, statWeek);
  }

  @Override
  public List<BizStatDTO> getShopOneWeekStatByYearAndWeek(long shopId, long statYear, long statWeek) {
    StatWriter writer = statDaoManager.getWriter();

    List<BizStatDTO> bizStatDTOList = new ArrayList<BizStatDTO>();
    for (BizStat bizStat : writer.getShopOneWeekStatByYearAndWeek(shopId, statYear, statWeek)) {
      bizStatDTOList.add(bizStat.toDTO());
    }

    return bizStatDTOList;
  }

  @Override
  public double getInventoryTotalAmountByShopId(long shopId) throws Exception {
    double totalAmount = 0;
    List<InventorySearchIndex> invList = ServiceManager.getService(ISearchService.class)
        .searchInventorySearchIndexByProductIds(shopId, null);
    if (invList != null && invList.size() > 0) {
      for (InventorySearchIndex inv : invList) {
        Double amount = inv.getAmount();
        Double purchasePrice = inv.getPurchasePrice();
        double itemTotalAmount = amount == null || purchasePrice == null ? 0 : amount * purchasePrice;
        totalAmount += itemTotalAmount;
      }
    }
    return totalAmount;
  }

  public List<AssistantStatDTO> getAssistantMonth(long shopId, long statYear, long startMonth, long endMonth, int pageNo, int pageSize) {
    StatWriter writer = statDaoManager.getWriter();

    List<AssistantStatDTO> assistantStatDTOList = new ArrayList<AssistantStatDTO>();
    for (AssistantStat assistantStat : writer.getAssistantMonth(shopId, statYear, startMonth, endMonth, pageNo, pageSize)) {
      assistantStatDTOList.add(assistantStat.toDTO());
    }
    return assistantStatDTOList;
  }

  public long countAssistantByMonth(long shopId, int statYear, int statMonth) {
    StatWriter writer = statDaoManager.getWriter();
    return writer.countAssistantByMonth(shopId, statYear, statMonth);
  }

  /**
   * 根据shop_id ,统计 年 月 日 周 开始时间和结束时间统计店员业绩
   *
   * @param shopId,int statYear,Integer statMonth,Integer statDay,Integer week,long startTime,long endTime
   * @throws Exception
   * @author liuWei
   */
  public void countAssistant(long shopId, int statYear, Integer statMonth, Integer statDay, Integer week, long startTime, long endTime) {

    IBizStatService bizStatService = ServiceManager.getService(IBizStatService.class);

    //由于涉及作废单 当统计该日期下的数据，自动删除该日期下已有的员工业绩统计数据
    bizStatService.deleteAllAssistantStat(shopId, statYear, statMonth, statDay, week);

    List<String> assistantList = new ArrayList<String>(); //店员list
    Map<String, Double> serviceAchieveOfAgent = new HashMap<String, Double>();   //每个店员的服务金额 维修美容单中的销售金额算作销售业绩
    Map<String, Double> salesAchieveOfAgent = new HashMap<String, Double>();   //每个店员的销售金额
    Map<String, Double> washAchieveOfAgent = new HashMap<String, Double>();   //每个店员的洗车费用
    Map<String, Double> memberAchieveOfAgent = new HashMap<String, Double>();//每个店员的会员相关业绩

    //计算施工单中每个员工的业绩
    this.calculateRepairAssistant(shopId, startTime, endTime, salesAchieveOfAgent, serviceAchieveOfAgent, assistantList);

    //计算洗车单中每个员工的业绩
    this.calculateWashAssistant(shopId, startTime, endTime, washAchieveOfAgent, assistantList);

    //计算销售单中每个员工的业绩
    this.calculateSalesAssistant(shopId, startTime, endTime, salesAchieveOfAgent, assistantList);

    //计算购卡续卡单中每个员工的业绩
    this.calculateMemberAssistant(shopId, startTime, endTime, memberAchieveOfAgent, assistantList);

     //计算会员卡退卡每个员工的业绩
    this.calculateMemberReturnAssistant(shopId,startTime,endTime,memberAchieveOfAgent,assistantList);

    //计算洗车美容单每个员工的业绩
    this.calculateWashBeautyAssistant(shopId, startTime, endTime, washAchieveOfAgent, assistantList);

    //获得员工业绩的一个实例
    AssistantStatDTO assistantStatDTO = this.getAssistantDTO(shopId, statYear, statMonth, statDay, week);

    //获得每个员工的员工业绩
    List<AssistantStatDTO> assistantStatList = this.getAssistantStatDTOList(assistantStatDTO, assistantList, serviceAchieveOfAgent, salesAchieveOfAgent, washAchieveOfAgent, memberAchieveOfAgent);

    //保存员工业绩
    bizStatService.saveAssistantStat(assistantStatList);
  }

  /**
   * 根据开始时间、结束时间、shop_id统计员工的洗车美容业绩 并放入map和asssistantList中
   *
   * @param shopId
   * @param startTime
   * @param endTime
   * @param washAchieveOfAgent
   * @param assistantList
   */
  public void calculateWashBeautyAssistant(Long shopId, long startTime, long endTime, Map<String, Double> washAchieveOfAgent, List<String> assistantList) {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);

    List<WashBeautyOrderDTO> washBeautyOrderDTOList = txnService.countWashBeautyAgentAchievements(shopId, startTime, endTime);//维修美容单记录
    if (CollectionUtils.isEmpty(washBeautyOrderDTOList)) {
      return;
    }
    double serviceTotal = 0; //每个洗车美容服务项目的统计金额
    for (WashBeautyOrderDTO washBeautyOrderDTO : washBeautyOrderDTOList) {
      if(washBeautyOrderDTO.getStatus() == OrderStatus.WASH_REPEAL){
        continue;
      }

      List<WashBeautyOrderItemDTO> washBeautyOrderItemDTOList = txnService.getWashBeautyOrderItemDTOByOrderId(washBeautyOrderDTO.getShopId(), washBeautyOrderDTO.getId());
      if (CollectionUtils.isEmpty(washBeautyOrderItemDTOList)) {
        continue;
      }
      for (WashBeautyOrderItemDTO washBeautyOrderItemDTO : washBeautyOrderItemDTOList) {
        if (washBeautyOrderItemDTO == null) {
          continue;
        }

        if (ConsumeType.TIMES == washBeautyOrderItemDTO.getConsumeTypeStr()) {
          serviceTotal = 0;
        } else {
          serviceTotal = NumberUtil.doubleVal(washBeautyOrderItemDTO.getPrice());
        }
        calculateAssistantByTotalAndWorkers(serviceTotal, washBeautyOrderItemDTO.getSalesMan(), washAchieveOfAgent, assistantList);
      }
    }
  }

  /**
   * 根据统计数据生成一个员工业绩数据对象
   *
   * @param shopId
   * @param statYear
   * @param statMonth
   * @param statDay
   * @param week
   * @return
   */
  public AssistantStatDTO getAssistantDTO(Long shopId, int statYear, Integer statMonth, Integer statDay, Integer week) {

    AssistantStatDTO assistantStatDTO = new AssistantStatDTO();
    assistantStatDTO.setShopId(shopId);
    assistantStatDTO.setStatYear(Integer.valueOf(statYear).longValue());
    if (statMonth != null) {
      assistantStatDTO.setStatMonth(Integer.valueOf(statMonth).longValue());
    }
    if (week != null) {
      assistantStatDTO.setStatWeek(Integer.valueOf(week).longValue());
    }
    if (statDay != null) {
      assistantStatDTO.setStatDay(Integer.valueOf(statDay).longValue());
    }
    return assistantStatDTO;
  }

  /**
   * 根据获得的统计数据map 和员工list 取得每个员工的业绩
   *
   * @param assistantStatDTO
   * @param assistantList
   * @param serviceAchieveOfAgent
   * @param salesAchieveOfAgent
   * @param washAchieveOfAgent
   * @param memberAchieveOfAgent
   * @return
   */
  public List<AssistantStatDTO> getAssistantStatDTOList(AssistantStatDTO assistantStatDTO, List<String> assistantList, Map<String, Double> serviceAchieveOfAgent, Map<String, Double> salesAchieveOfAgent,
                                                        Map<String, Double> washAchieveOfAgent, Map<String, Double> memberAchieveOfAgent) {
    List<AssistantStatDTO> assistantStatList = new ArrayList<AssistantStatDTO>();
    if (CollectionUtils.isEmpty(assistantList)) {
      return assistantStatList;
    }

    for (String assistant : assistantList) {

      AssistantStatDTO newAssistantStatDTO = new AssistantStatDTO();
      newAssistantStatDTO.setShopId(assistantStatDTO.getShopId());
      newAssistantStatDTO.setStatYear(assistantStatDTO.getStatYear());
      newAssistantStatDTO.setStatMonth(assistantStatDTO.getStatMonth());
      newAssistantStatDTO.setStatDay(assistantStatDTO.getStatDay());
      newAssistantStatDTO.setStatWeek(assistantStatDTO.getStatWeek());
      newAssistantStatDTO.setAssistant(assistant);
      //服务金额
      if (serviceAchieveOfAgent.containsKey(assistant)) {
        newAssistantStatDTO.setService(NumberUtil.doubleVal(serviceAchieveOfAgent.get(assistant)));
      }
      //销售金额
      if (salesAchieveOfAgent.containsKey(assistant)) {
        newAssistantStatDTO.setSales(NumberUtil.doubleVal(salesAchieveOfAgent.get(assistant)));
      }
      //洗车金额
      if (washAchieveOfAgent.containsKey(assistant)) {
        newAssistantStatDTO.setWash(NumberUtil.doubleVal(washAchieveOfAgent.get(assistant)));
      }
      //会员购卡续卡
      if (memberAchieveOfAgent.containsKey(assistant)) {
        newAssistantStatDTO.setMemberIncome(NumberUtil.doubleVal(memberAchieveOfAgent.get(assistant)));
      }
      newAssistantStatDTO.setStatSum(newAssistantStatDTO.getService() + newAssistantStatDTO.getSales() + newAssistantStatDTO.getWash() + newAssistantStatDTO.getMemberIncome());
      assistantStatList.add(newAssistantStatDTO);
    }
    return assistantStatList;
  }

  /**
   * 根据开始时间 结束时间 shop_id 统计施工单下 每个员工的服务金额 和销售金额
   *
   * @param shopId
   * @param startTime
   * @param endTime
   * @param assistantSalesAchieve
   * @param assistantServiceAchieve
   * @param assistantList
   */
  public void calculateRepairAssistant(Long shopId, long startTime, long endTime, Map<String, Double> assistantSalesAchieve, Map<String, Double> assistantServiceAchieve, List<String> assistantList) {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    List<RepairOrder> repairOrderList = txnService.countAgentAchievements(shopId, startTime, endTime);//维修美容单记录
    if (CollectionUtils.isEmpty(repairOrderList)) {
      return;
    }
    for (RepairOrder repairOrder : repairOrderList) {
      if (repairOrder == null) {
        continue;
      }
      calculateAssistantOfRepairOrder(repairOrder, assistantSalesAchieve, assistantServiceAchieve, assistantList);
    }
  }

  /**
   * 根据开始时间 结束时间 shop_id 统计每个员工的销售金额
   *
   * @param shopId
   * @param startTime
   * @param endTime
   * @param salesAchieveOfAgent
   * @param assistantList
   */
  public void calculateSalesAssistant(Long shopId, long startTime, long endTime, Map<String, Double> salesAchieveOfAgent, List<String> assistantList) {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    List<SalesOrder> salesOrderList = txnService.countSalesAgentAchievements(shopId, startTime, endTime);//销售记录
    if (CollectionUtils.isEmpty(salesOrderList)) {
      return;
    }
    for (SalesOrder salesOrder : salesOrderList) {
      if (salesOrder == null || salesOrder.getStatusEnum() == OrderStatus.SALE_REPEAL) {
        continue;
      }
      calculateAssistantByTotalAndWorkers(salesOrder.getTotal(), salesOrder.getGoodsSaler(), salesAchieveOfAgent, assistantList);
    }
  }

  /**
   * 根据开始时间 结束时间 shop_id统计每个员工的洗车金额
   *
   * @param shopId
   * @param startTime
   * @param endTime
   * @param washAchieveOfAgent
   * @param assistantList
   */
  public void calculateWashAssistant(Long shopId, long startTime, long endTime, Map<String, Double> washAchieveOfAgent, List<String> assistantList) {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    List<WashOrder> washOrderList = txnService.countWashAgentAchievements(shopId, startTime, endTime); //洗车记录
    if (CollectionUtils.isEmpty(washOrderList)) {
      return;
    }
    for (WashOrder washOrder : washOrderList) {
      if (washOrder == null) {
        continue;
      }
      calculateAssistantOfWashOrder(washOrder, washAchieveOfAgent, assistantList);
    }
  }

  /**
   * 根据shop_id 统计时间 删除对应时间下的员工业绩
   *
   * @param shopId
   * @param statYear
   * @param statMonth
   * @param statDay
   * @param week
   */
  public void deleteAllAssistantStat(long shopId, int statYear, Integer statMonth, Integer statDay, Integer week) {
    StatWriter writer = statDaoManager.getWriter();
    Object status = writer.begin();
    try {
      writer.deleteAllAssistantStat(shopId, statYear, statMonth, statDay, week);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  /**
   * 根据washOrder统计员工业绩
   *
   * @param washOrder
   * @param washAchieveOfAgent
   * @param assistantList
   */
  public void calculateAssistantOfWashOrder(WashOrder washOrder, Map<String, Double> washAchieveOfAgent, List<String> assistantList) {
    if (washOrder == null) {
      return;
    }
    String washWorkerStr = "";
    int assistantNum = 0;
    if (!StringUtil.isEmpty(washOrder.getWashWorker())) {
      washWorkerStr = washOrder.getWashWorker();
    } else {
      washWorkerStr = RfTxnConstant.ASSISTANT_NAME;
    }

    if (washWorkerStr.length() <= 1) {
      putDataToMapAndAssistantList(washAchieveOfAgent, washWorkerStr, washOrder.getCashNum(), assistantList);
      return;
    }

    washWorkerStr = removeFirstChar(washWorkerStr);
    washWorkerStr = washWorkerStr.replace("，", ",");//页面输入：若存在多个店员，则以逗号分开，把中文逗号转换为英文逗号
    assistantNum = (washWorkerStr.split(",").length == 0 ? 1 : washWorkerStr.split(",").length);//判断施工人数量

    //根据输入的店员数量，每个店员平分业绩
    for (String worker : washWorkerStr.split(",")) {
      putDataToMapAndAssistantList(washAchieveOfAgent, worker, NumberUtil.toReserve(washOrder.getCashNum() / assistantNum, NumberUtil.MONEY_PRECISION), assistantList);
    }
  }

  /**
   * 根据施工单 获得每个施工内容 和材料内容 统计员工业绩
   *
   * @param repairOrder
   * @param assistantSalesAchieve
   * @param assistantServiceAchieve
   * @param assistantList
   */
  public void calculateAssistantOfRepairOrder(RepairOrder repairOrder, Map<String, Double> assistantSalesAchieve, Map<String, Double> assistantServiceAchieve, List<String> assistantList) {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    if (repairOrder == null) {
      return;
    }
    RepairOrderDTO repairOrderDTO = null;
    try {
      repairOrderDTO = txnService.getRepairOrder(repairOrder.getId());
    } catch (Exception e) {
      LOG.error("BizStatService.java");
      LOG.error("施工单id:" + repairOrder.getId());
      LOG.error("根据施工单id获取repairOrderDTO失败");
      LOG.error(e.getMessage(), e);
      return;
    }
    RepairOrderServiceDTO[] repairOrderServiceDTOs = repairOrderDTO.getServiceDTOs();
    if (!ArrayUtils.isEmpty(repairOrderServiceDTOs)) {
      for (RepairOrderServiceDTO repairOrderServiceDTO : repairOrderServiceDTOs) {
        //if (repairOrderServiceDTO == null || NumberUtil.doubleVal(repairOrderServiceDTO.getTotal()) == 0) {
        if (repairOrderServiceDTO == null) {
          continue;
        }

        calculateAssistantByTotalAndWorkers(repairOrderServiceDTO.getTotal(), repairOrderServiceDTO.getWorkers(), assistantServiceAchieve, assistantList);
      }
    }

    RepairOrderItemDTO[] repairOrderItemDTOs = repairOrderDTO.getItemDTOs();
    if (ArrayUtils.isEmpty(repairOrderItemDTOs)) {
      return;
    }
    double itemTotalAmount = 0;
    for (RepairOrderItemDTO repairOrderItemDTO : repairOrderItemDTOs) {
      if (repairOrderItemDTO == null || NumberUtil.doubleVal(repairOrderItemDTO.getTotal()) == 0) {
        continue;
      }
      itemTotalAmount += NumberUtil.doubleVal(repairOrderItemDTO.getTotal());
    }
    itemTotalAmount = NumberUtil.toReserve(itemTotalAmount, NumberUtil.MONEY_PRECISION);
    calculateAssistantByTotalAndWorkers(itemTotalAmount, repairOrderDTO.getProductSaler(), assistantSalesAchieve, assistantList);
    return;

  }

  /**
   * 根据总金额 员工列表 统计每个员工的业绩 并放入到map和  员工assistantList中
   *
   * @param itemTotal
   * @param serviceWorker
   * @param assistantAchieve
   * @param assistantList
   */
  public void calculateAssistantByTotalAndWorkers(double itemTotal, String serviceWorker, Map<String, Double> assistantAchieve, List<String> assistantList) {
    if (StringUtil.isEmpty(serviceWorker) || StringUtil.isEmpty(serviceWorker.trim())) {
      serviceWorker = RfTxnConstant.ASSISTANT_NAME;
    }
    int assistantNum = 0;

    if (serviceWorker.length() <= 1) {
      putDataToMapAndAssistantList(assistantAchieve, serviceWorker, itemTotal, assistantList);
      return;
    }

    serviceWorker = removeFirstChar(serviceWorker);
    serviceWorker = serviceWorker.replace("，", ",");//页面输入：若存在多个店员，则以逗号分开，把中文逗号转换为英文逗号
    assistantNum = (serviceWorker.split(",").length == 0 ? 1 : serviceWorker.split(",").length);//判断施工人数量

    //根据输入的店员数量，每个店员平分业绩
    for (String worker : serviceWorker.split(",")) {
      putDataToMapAndAssistantList(assistantAchieve, worker, NumberUtil.toReserve(itemTotal / assistantNum, NumberUtil.MONEY_PRECISION), assistantList);
    }
  }

  /**
   * 根据开始时间 结束时间 统计每个员工的购卡续卡业绩
   *
   * @param shopId
   * @param startTime
   * @param endTime
   * @param agentMemberAchieve
   * @param assistantList
   */
  public void calculateMemberAssistant(Long shopId, long startTime, long endTime, Map<String, Double> agentMemberAchieve, List<String> assistantList) {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);

    List<MemberCardOrderDTO> memberCardOrderDTOList = txnService.countMemberAgentAchievements(shopId, startTime, endTime);
    if (CollectionUtils.isEmpty(memberCardOrderDTOList)) {
      return;
    }
    String serviceWorker = "";
    SalesManDTO salesManDTO = null;
    for (MemberCardOrderDTO memberCardOrderDTO : memberCardOrderDTOList) {
      if (memberCardOrderDTO == null) {
        continue;
      }
      List<MemberCardOrderItemDTO> memberCardOrderItemDTOList = txnService.getMemberCardOrderItemDTOByOrderId(memberCardOrderDTO.getShopId(), memberCardOrderDTO.getId());
      if (CollectionUtils.isEmpty(memberCardOrderItemDTOList)) {
        return;
      }
      MemberCardOrderItemDTO memberCardOrderItemDTO = memberCardOrderItemDTOList.get(0);
      if (memberCardOrderItemDTO.getSalesId() == null) {
        serviceWorker = RfTxnConstant.ASSISTANT_NAME;
      } else {
        salesManDTO = userService.getSalesManDTOById(memberCardOrderItemDTO.getSalesId());
        serviceWorker = (salesManDTO == null ? RfTxnConstant.ASSISTANT_NAME : salesManDTO.getName());
      }

      putDataToMapAndAssistantList(agentMemberAchieve, serviceWorker, memberCardOrderDTO.getTotal(), assistantList);

    }
  }

  /**
   * 根据开始时间 结束时间 统计每个员工的退卡业绩
   *
   * @param shopId
   * @param startTime
   * @param endTime
   * @param agentMemberAchieve
   * @param assistantList
   */
  public void calculateMemberReturnAssistant(Long shopId, long startTime, long endTime, Map<String, Double> agentMemberAchieve, List<String> assistantList) {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);

    List<MemberCardReturnDTO> memberCardReturnDTOList = txnService.getMemberReturnListByReturnDate(shopId, startTime, endTime);
    if (CollectionUtils.isEmpty(memberCardReturnDTOList)) {
      return;
    }
    String serviceWorker = "";
    SalesManDTO salesManDTO = null;
    for (MemberCardReturnDTO memberCardReturnDTO : memberCardReturnDTOList) {
      if (memberCardReturnDTO == null) {
        continue;
      }
      List<MemberCardReturnItemDTO> memberCardReturnItemDTOList = txnService.getMemberCardReturnItemDTOByOrderId(memberCardReturnDTO.getShopId(), memberCardReturnDTO.getId());
      if (CollectionUtils.isEmpty(memberCardReturnItemDTOList)) {
        continue;
      }
      MemberCardReturnItemDTO memberCardReturnItemDTO = memberCardReturnItemDTOList.get(0);
      if (memberCardReturnItemDTO.getSalesId() == null) {
        serviceWorker = RfTxnConstant.ASSISTANT_NAME;
      } else {
        salesManDTO = userService.getSalesManDTOById(memberCardReturnItemDTO.getSalesId());
        serviceWorker = (salesManDTO == null ? RfTxnConstant.ASSISTANT_NAME : salesManDTO.getName());
      }

      putDataToMapAndAssistantList(agentMemberAchieve, serviceWorker,0 - NumberUtil.doubleVal(memberCardReturnDTO.getTotal()), assistantList);
    }
  }


  /**
   * 把业绩放入对应的map中 把员工放入到员工列表中
   *
   * @param map
   * @param serviceWorker
   * @param assistantStatValue
   * @param assistantList
   */
  public void putDataToMapAndAssistantList(Map map, String serviceWorker, Double assistantStatValue, List<String> assistantList) {
    double originalAssistantStatValue = 0; //原来的业绩
    if (map.containsKey(serviceWorker)) {
      originalAssistantStatValue = NumberUtil.doubleVal((Double) map.get(serviceWorker));
      originalAssistantStatValue += NumberUtil.doubleVal(assistantStatValue);
      map.put(serviceWorker, originalAssistantStatValue);
    } else {
      originalAssistantStatValue = NumberUtil.doubleVal(assistantStatValue);
      map.put(serviceWorker, originalAssistantStatValue);
    }
    if (!assistantList.contains(serviceWorker)) {
      assistantList.add(serviceWorker);
    }
  }

  /**
   * 如果第一个字母为 ,，去除第一个字母
   *
   * @param workers
   * @return
   */
  public String removeFirstChar(String workers) {
    if (StringUtil.isEmpty(workers) || workers.length() <= 1) {
      return workers;
    }
    char firstCharOfServiceWorker = workers.charAt(0);
    if (firstCharOfServiceWorker == ' ' || firstCharOfServiceWorker == ',' || firstCharOfServiceWorker == '，') {
      workers = workers.substring(1, workers.length());
    }
    return workers;
  }
}
