package com.bcgogo.txn.service;


import com.bcgogo.common.Pager;
import com.bcgogo.common.Result;
import com.bcgogo.common.Sort;
import com.bcgogo.config.dto.ImportResult;
import com.bcgogo.config.dto.OperationLogDTO;
import com.bcgogo.config.service.excelimport.ImportContext;
import com.bcgogo.enums.*;
import com.bcgogo.enums.Product.NormalProductStatType;
import com.bcgogo.enums.Product.ProductRelevanceStatus;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.exception.PageException;
import com.bcgogo.payment.dto.RechargeSearchDTO;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.remind.dto.RemindEventDTO;
import com.bcgogo.search.dto.*;
import com.bcgogo.search.model.InventorySearchIndex;
import com.bcgogo.stat.dto.BizStatPrintDTO;
import com.bcgogo.stat.dto.SupplierRecordDTO;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.dto.StatementAccount.OrderDebtType;
import com.bcgogo.txn.model.*;
import com.bcgogo.user.dto.*;
import com.bcgogo.user.model.CustomerServiceJob;
import com.bcgogo.user.model.MemberService;
import org.apache.velocity.VelocityContext;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;


public interface ITxnService {
  public  String getStatementAccountOrderNo(Long shopId, Long statementAccountOrderId);

  PurchaseOrderDTO getPurchaseOrder(Long purchaseOrderId, Long shopId) throws Exception;

  PurchaseOrderDTO getSimplePurchaseOrder(Long purchaseOrderId, Long shopId) throws Exception;

  void savePurchasePrice(TxnWriter txnWriter, PurchasePriceDTO purchasePriceDTO);

  public SalesOrderDTO getSalesOrder(Long salesOrderId,Long shopId) throws Exception;

  public SalesOrderDTO getSalesOrderByPurchaseOrderId(Long purchaseOrderId,Long supplierShopId) throws Exception;

  public InventoryDTO getInventoryByShopIdAndProductId(Long shopId, Long productId) throws Exception;

  public List<InventoryDTO> getInventoryByShopIdAndProductIds(Long shopId, Long... productId) throws Exception;

  public InventoryDTO createOrUpdateInventory(InventoryDTO inventoryDTO) throws BcgogoException;

  public SalesOrderDTO createOrUpdateSalesOrder(SalesOrderDTO salesOrderDTO, String huankuanTime) throws Exception;

  public List<SalesOrderItem> getSaleOrderItemListByOrderId(Long saleOrderId) throws Exception;

  public RepairOrderDTO getRepairOrder(Long repairOrderId) throws Exception;

  public List<RepairOrderItem> getRepairOrderItemByRepairOrderId(Long repairOrderId) throws Exception;

  InventorySearchIndex createInventorySearchIndex(Inventory inventory, Long parentProductId) throws Exception;

  InventorySearchIndex createInventorySearchIndex(Inventory inventory, ProductDTO productDTO) throws Exception;

  public void updateRepairOrderAndItem(List<RepairOrderDTO> repairOrderDTOList) throws Exception;

  public RepairOrderDTO getUnbalancedAccountRepairOrderByVehicleNumber(Long shopId, String vehicleNumber,Long orderId) throws Exception;

  ReceivableDTO createOrUpdateReceivable(TxnWriter writer, ReceivableDTO receivableDTO) throws Exception;

   List<RepairOrderOtherIncomeItemDTO>  getRepairOtherIncomeItemByOrderId(Long shopId,Long orderId);

  public ReceivableDTO getReceivableByShopIdAndOrderTypeAndOrderId(Long shopId, OrderTypes orderType, Long orderId) throws Exception;


  public InventoryDTO getInventoryAmount(Long shopId, Long productId) throws Exception;

  public boolean updateRepairRemindEventByShopIdAndTypeAndProductId(Long shopId, RepairRemindEventTypes eventType, Long productId, RepairRemindEventTypes targetEventType, Long repairOrderId) throws BcgogoException;

  public List<RepairRemindEventDTO> getRepairRemindEventByShopIdAndOrderIdAndType(Long shopId, Long repairOrderId, RepairRemindEventTypes eventType) throws Exception;

  public List<RepairRemindEventDTO> getRepairRemindEvents(Long shopId, RepairRemindEventTypes eventType, Long pagNo, Long pageSize) throws Exception;

  public int countRepairRemindEvents(Long shopId, RepairRemindEventTypes eventType);

  public boolean deleteInventoryRemindEventByShopIdAndPurchaseOrderId(Long shopId, Long purchaseOrderId) throws BcgogoException;

  public List<InventoryRemindEventDTO> getInventoryRemindEventDTOByPurchaseOrderId(Long shopId, Long purchaseOrderId) throws BcgogoException;

  public List<ScheduleServiceEventDTO> getScheduleServiceEventByShopIdAndCustomerIdAndVehicleId(Long shopId, Long customerId, Long vehicleId) throws BcgogoException;

  public ScheduleServiceEventDTO updateScheduleServiceEvent(ScheduleServiceEventDTO scheduleServiceEventDTO) throws BcgogoException;

  public ScheduleServiceEventDTO createScheduleServiceEvent(ScheduleServiceEventDTO scheduleServiceEventDTO) throws BcgogoException;

  public ProductRemindEventDTO createOrUpdateProductRemindEvent(ProductRemindEventDTO productRemindEventDTO) throws BcgogoException;


  public List<DebtDTO> getDebtsByShopIdAndCustomerId(Long shopId, Long customerId) throws BcgogoException;

  public DebtDTO getDebtByShopIdAndCustomerIdAndOrderId(Long shopId, Long customerId,Long orderId) throws BcgogoException;

  public List<RepairRemindEvent> getRepairRemindEventByShopId(Long shopId, RepairRemindEventTypes eventType, Long[] productId, int pageNo, int pageSize);

  public int countRepairRemindEventByShopId(Long shopId, RepairRemindEventTypes eventType, Long[] productId);


  public List<LackMaterialDTO> getLackMaterialByProductId(Long shopId, RepairRemindEventTypes eventType, Long productId) throws Exception;
  public List<LackMaterialDTO> getLackMaterialByProductIdAndStorehouse(Long shopId, RepairRemindEventTypes eventType, Long productId,Long storehouseId) throws Exception;

  public Map<Long,List<LackMaterialDTO>> getLackMaterialMapByProductIds(Long shopId,RepairRemindEventTypes eventType,Set<Long> productIds)throws Exception;

  public Map<Long,List<LackMaterialDTO>> getLackMaterialMapByProductIdsAndStorehouse(Long shopId,RepairRemindEventTypes eventType,Set<Long> productIds,Long storehouseId)throws Exception;




  /*
  创建洗车单或者充值单
   */
  public WashOrderDTO createWashOrder(WashOrderDTO washOrderDTO) throws Exception;

  /*
  查询客户洗车单列表
   */
  public List<WashOrderDTO> getCustomerWashOrders(long customerId) throws Exception;

  /*
  查询客户当天洗车次数
   */
  public int getTodayWashTimes(long customerId) throws BcgogoException;

  /*
  欠款提醒
  */
  public List<DebtDTO> getDebtByShopIdAndOrderId(Long shopId, Long orderId) throws Exception;


  /*
  * 收款单
  */


  public int countNoSettlementRepairOrder(Long shopId);

  public int countInventoryRemindEventNumber(Long shopId) throws Exception;

  public int countInventoryNumber(Long shopId) throws Exception;

  public List<InventoryRemindEventDTO> getInventoryRemindEventByShopIdAndPageNoAndPageSize(Long shopId, Integer pageNo, Integer pageSize) throws Exception;

  //根据shopid和服务名查询服务项目表
  public List<ServiceDTO> getServiceByShopIdAndSearchKey(Long shopId,String searchKey) throws Exception;

  public ServiceDTO getServiceById(Long id) throws  Exception;

  List<ServiceDTO> getServiceDTOById(Long shopId,Long... serviceIds);

  Map<Long,ServiceDTO> getServiceDTOMapById(Long shopId,Long... serviceIds);

  public List<MemberCardServiceDTO> saveServicesByMemberCardDTO(MemberCardDTO memberCardDTO);

  public int countInventoryRemindEventByShopIdAndPageNoAndPageSize(Long shopId) throws Exception;

  public Boolean payDebt(double totalAmount,
                         double payedAmount,
                         double owedAmount,
                         String receivableOrderIdsString,
                         String orderTotalsString,
                         String orderOwedsString,
                         String orderPayedsString,
                         String debtIdsString,
                         String huankuanTime, HttpServletRequest request, Long shopId) throws Exception;
  public List<RepairRemindEvent>  getLackProductIdsByRepairOderId(Long repairOrderId,Long shopId,RepairRemindEventTypes eventType);

  public BusinessStatDTO saveBusinessStat(BusinessStatDTO businessStatDTO);

  public void  deleteExpendDetailByYearMonth(long shopId,long year,long month);

  public List<BusinessStatDTO> getBusinessStatByYearMonthDay(long shopId,long year,long month,long day);

  public BusinessStatDTO updateBusinessStat(BusinessStatDTO businessStatDTO);

  public List<ExpendDetailDTO> getExpendDetailByYearMonthDay(long shopId,long year,long month,long day);

  public List<ExpendDetailDTO> getExpendDetailByYearMonth(long shopId,long year,long month,long day);

  public List<ExpendDetailDTO> getExpendDetailByYearFromStartMonthToEndMonth(long shopId,long year,long startMonth,long endMonth);

  public List<BusinessStatDTO> getLatestBusinessStat(long shopId,Long year,int size);

  public List<BusinessStatDTO> getEarliestBusinessStat(long shopId,long year,int size);

  public List<BusinessStatDTO> getLatestBusinessStatMonth(long shopId,long year,long month ,int size);

  public ExpendDetailDTO saveExpendDetail(ExpendDetailDTO expendDetailDTO);

  public List<BusinessStatDTO> getBusinessStatMonth(long shopId,long year,String queryString);

  public List<BusinessStatDTO> getBusinessStatMonthEveryDay(long shopId,long year,long month,long day);


  public List<WashOrder> countWashAgentAchievements(long shopId, long startTime, long endTime);

  public List<RepairOrderDTO> getRepairOrderDTOList(long shopId,long startTime,long endTime,int pageNo,int PageSize,String arrayType,OrderStatus orderStatus) throws Exception;

  public List<String> getRepairOrderDTOListByVestDate(long shopId,long startTime,long endTime,OrderStatus orderStatus) throws Exception;

  public List<RepairOrderDTO> getHundredCostPriceNUllRepairOrderDTOList() throws Exception;

  public List<RepairOrderItemDTO> countShopRepairOrderSalesIncome(long shopId, long startTime, long endTime);

  public List<RepairOrderServiceDTO> countShopRepairOrderServiceIncome(long shopId, long startTime, long endTime);

  public List<SalesOrderItemDTO> countShopSalesIncome(long shopId, long startTime, long endTime );

  public List<SalesOrderItemDTO> countShopSalesIncomeByShopId(long shopId, long startTime, long endTime );

  public List<WashOrderDTO> countWashOrderList(long shopId,long startTime,long endTime);

  public int countSalesOrder(long shopId,long startTime,long endTime);

  public int countSalesOrder(Long shopId);

  public int countRepairOrder(Long shopId);

  public List<OrderSearchResultDTO> getSalesOrderDTOList(long shopId,long startTime,long endTime,int pageNo,int PageSize,String arrayType) throws Exception;

  void updateProductUnit(BcgogoOrderDto orderDTO) throws Exception;

  public List<SalesOrderDTO> getHundredCostPriceNUllSalesOrderDTOList() throws Exception;


  public void updateSaleOrderAndItem(List<SalesOrderDTO> salesOrderDTOList) throws Exception;

  public SalesOrderDTO getSalesOrder(Long salesOrderId) throws Exception;

  public List<SalesOrderDTO> getSalesOrderDTOListByVestDate(long shopId,long startTime,long endTime) throws Exception;


  public List<SalesOrder> countSalesAgentAchievements(long shopId, long startTime, long endTime);

  public List<WashOrder> getWashOrderListByAssistantName(String assistantName,long startTime,long endTime);

  public List<RepairOrder> getRepairOrderListByAssistantName(String assistantName,long startTime,long endTime);

  public List<SalesOrder> getSalesOrderListByAssistantName(String assistantName,long startTime,long endTime);

  public List<RepairOrder> countAgentAchievements(long shopId, long startTime, long endTime);

  public double countServiceAgentAchievements(long shopId, long startTime, long endTime);

  public double countItemAgentAchievements(long shopId, long startTime, long endTime);

  public boolean batchCreateInventory(List<InventoryInfoDTO> inventoryInfoDTOList, Long shopId) throws BcgogoException;

  public ImportResult importInventoryFromExcel(ImportContext importContext) throws BcgogoException;

  public void updatePurchaseInventoryStatus(PurchaseInventoryDTO purchaseInventoryDTO);

  public void updatePurchaseInventorySupplier(SupplierDTO supplierDTO);

  public List<PurchaseInventory> getPurchaseInventoryByShopIdAndSupplierId(Long shopId,Long supplierId);

  public void updatePurchaseOrderSupplier(SupplierDTO supplierDTO);

  public List<PurchaseOrder> getPurchaseOrderByShopIdAndSupplierId(Long shopId,Long supplierId);

  public void updatePurchaseOrderStatus(Long shopId, Long purchaseOrderId, OrderStatus purchaseOrderTypeStatus, Long userId, Long vestDate) throws BcgogoException;

  public void initPurchaseInventoryStatus(Long shopId,PurchaseInventoryDTO purchaseInventoryDTO) throws BcgogoException;

  public String checkInventoryAmount(ModelMap model,Long shopId,PurchaseInventoryDTO purchaseInventoryDTO,
                                     List<InventorySearchIndex>inventorySearchIndexDTOs) throws Exception;

  public String checkInventoryAmountByStoreHouse(ModelMap model,Long shopId,PurchaseInventoryDTO purchaseInventoryDTO,
                                                 List<InventorySearchIndex>inventorySearchIndexDTOs,List<StoreHouseInventoryDTO> storeHouseInventoryDTOList) throws Exception;
  List<SmsRechargeDTO> getSmsRechargesByConditions(RechargeSearchDTO rechargeSearchDTO);

  List<Long> getSmsRechargesByStatus(Long shopId, int start, int pageSize, Long loanTransferTime);

  List<SmsRechargeDTO> getSmsRechargesByIds(Long... ids);

  int countSmsRechargesByConditions(RechargeSearchDTO rechargeSearchDTO);

  public List<String> getSalesOrderCountAndSum(long shopId, long startTime, long endTime);

  public List<SmsBalance> smsBalanceMigrate(Pager pager);

  Integer countSmsBalance();

  public WashOrderDTO getWashOrder(Long washOrderId) throws Exception;

  public List<SalesOrderDTO> getSalesOrderDTOListByCustomerId(long shopId,long customerId) throws Exception;

  public String updateReceivable(ReceivableDTO receivableDTO) throws Exception;

  public List<RepairOrderDTO> getRepairOrderDTOListByCustomerId(long shopId,long customerId) throws Exception;

  public List<ReceivableDTO> getReceivableDTOList(Long shopId,int pageNo,int pageSize) throws Exception;

  public ReceivableDTO getReceivableDTOByShopIdAndOrderId(long shopId, long orderId);

  public long countReceivableDTOByShopId(Long shopId);

  public List<PurchaseOrderDTO> getPurchaseOrderDTOListByShopId(long shopId,long startTime,long endTime) throws Exception;

  public List<PurchaseInventoryDTO> getPurchaseInventoryDTOByShopId(long shopId) throws Exception;

  public List<RepairOrderDTO> getRepairOrderDTOListByCreated(long shopId,long startTime,long endTime) throws Exception;

  public long countPurchaseInventoryByShopId(long shopId,long startTime,long endTime);

  public List<PurchaseInventoryDTO> getPurchaseInventoryDTOList(long shopId,int pageNo,int pageSize,long startTime,long endTime) throws Exception;

  public List<RepairOrderItem> getRepairOrderItemByProductId(long shopId, long productId, OrderStatus status) throws Exception;

  public List<SalesOrderItem> getSalesOrderItemByProductId(long shopId,long productId, OrderStatus status) throws Exception;

  public List<PurchaseInventoryItem> getPurchaseInventoryItemByProductId(long shopId,long productId , OrderStatus status) throws Exception;

  public List<PurchaseOrderItem> getPurchaseOrderItemByProductId(long shopId,long productId ,Long status) throws Exception;

  public List<PurchaseReturnItem> getPurchaseReturnItemByProdctId(long shopId,long productId ,Long status) throws Exception;

  public List<RepairRemindEvent> getRepairRemindEventByProductId(long shopId,long productId)throws Exception;

  public List<InventoryRemindEvent> getInventoryRemindEventByProductId(Long shopId, Long productLocalInfoId);

  public int countSalesOrderByVestDate(long shopId,long startTime,long endTime) throws Exception;

  public List<SalesOrderDTO> getSalesOrderListByPager(long shopId,long startTime,long endTime,
                                                      Pager pager) throws Exception;

  public int countRepairOrderByVestDate(long shopId,long startTime,long endTime) throws Exception;

  public List<RepairOrderDTO> getRepairOrderListByPager(long shopId,long startTime,long endTime,
                                                        Pager pager) throws Exception;

  public List<RepealOrderDTO> getRepealOrderByShopIdAndOrderId(long shopId,long orderId);

  /**
   * 获取该客户上次(本次除外)的洗车记录（充值不算）
   */
  public WashOrderDTO getLastWashOrderDTO(Long shopId,Long customerId);

  /**
   * 清空 repairOrderDTO 数组中的空数据
   * @param repairOrderDTO
   * @return
   * @throws Exception
   */
  public RepairOrderDTO removeBlankArrayOfRepairOrder(RepairOrderDTO repairOrderDTO) throws Exception;
  public int countServices(Long shopId);

  public List<ServiceDTO> getServiceDTOByShopId(Long shopId,int ageNo,int maxPageSize) throws Exception;

  public List<Long> getAllServiceIdsByShopId(Long shopId,int start,int rows) throws Exception;

  public MemberCardOrderDTO saveMemberCardOrder(MemberCardOrderDTO memberCardOrderDTO) throws Exception;

  /**
   * 根据shop_id和服务名称查找服务
   * @param shopId
   * @param service
   * @return
   */
  public List<ServiceDTO> getServiceByServiceNameAndShopId(long shopId,String service);

  public MemberCardOrderDTO getMemberCardOrderDTOById(Long shopId,Long orderId);

  public List<MemberCardOrderItemDTO> getMemberCardOrderItemDTOByOrderId(Long shopId,Long orderId);

  public List<MemberCardOrderServiceDTO> getMemberCardOrderServiceDTOByOrderId(Long shopId,Long orderId);

  public List<ServiceDTO> getAllServiceDTOOfTimesByShopId(Long shopId);

  /**
   * 施工单结算完成后 更改会员相关信息
   * 在提交时已做校验 这里只做部分校验
   * @param repairOrderDTO
   */
  public VelocityContext updateMemberInfo(RepairOrderDTO repairOrderDTO);

  public WashBeautyOrderDTO getWashBeautyOrderDTOById(Long shopId,Long orderId);

  public List<WashBeautyOrderItemDTO> getWashBeautyOrderItemDTOByOrderId(Long shopId,Long orderId);

  public List<ServiceDTO> getAllServiceDTOByShopId(Long shopId);

  public List<ServiceDTO> getObscureServiceByName(Long shopId,String serviceName);

  public List<Category> getObscureCategoryByName(Long shopId,String categoryName);

  public List<ServiceDTO> searchSuggestionForServices(Long shopId,String searchKey);
  /**
   * 根据shop_id获取一段时间内购卡续卡单据的条数 和总金额
   * @param shopId
   * @param startTime
   * @param endTime
   * @return
   */
  public List<String> getMemberOrderCountAndSum(long shopId, long startTime, long endTime,OrderSearchConditionDTO orderSearchConditionDTO);

  /**
   * 根据开始时间、结束时间、shop_id、分页组件、排序类型获取购卡续卡单据列表
   * @param shopId
   * @param startTime
   * @param endTime
   * @param pager
   * @param arrayType
   * @return
   * @throws Exception
   */
  public List<MemberCardOrderDTO> getMemberOrderListByPagerTimeArrayType(long shopId,long startTime,long endTime,Pager pager,String arrayType,OrderSearchConditionDTO orderSearchConditionDTO) throws Exception;


  /**
   * 根据汽修车饰单ID查询汽修车饰单服务项目表
   *
   * @param repairOrderId
   * @return
   */
  public List<RepairOrderService> getRepairOrderServicesByRepairOrderId(Long repairOrderId);


  /**
   * 根据shop_id 服务名称查找服务
   * @param shopId
   * @param serviceName
   * @return
   */
  public List<Service> getServiceByShopIdAndName(Long shopId,String serviceName);

  /**
   * 根据开始时间 和结束时间 统计某一段时间内购卡续卡的单据列表
   * @param shopId
   * @param startTime
   * @param endTime
   * @return
   */
  public List<MemberCardOrderDTO> countMemberAgentAchievements(long shopId, long startTime, long endTime);

  /**
   * 根据开始时间 和结束时间 统计某一段时间内洗车美容单的单据列表
   * @param shopId
   * @param startTime
   * @param endTime
   * @return
   */
  public List<WashBeautyOrderDTO> countWashBeautyAgentAchievements(long shopId, long startTime, long endTime);

  public List<Service> getServiceByShopIdAndNameRemovalTrimAndUpper(Long shopId,String serviceName,Long serviceId);

  /**
   * 根据订单类型和订单ID查询该单据历史结算记录
   * @param shopId
   * @param orderTypeEnum
   * @param orderId
   * @return
   * @throws Exception
   */
  public List<ReceptionRecordDTO> getSettledRecord(Long shopId, OrderTypes orderTypeEnum, Long orderId);
  /**
   * 根据shopId分页获得reception记录
   * @param shopId
   * @param pager
   * @return
   * @throws Exception
   */
  public List<ReceptionRecordDTO> getReceptionByShopIdAndPager(long shopId,Pager pager) throws Exception;

  /**
   * 保存收款记录
   * @param receptionRecordDTO
   */
  public void saveOrUpdateReceptionRecord(ReceptionRecordDTO receptionRecordDTO);
  WashBeautyOrderItemDTO getWashBeautyOrderItemByItemId(Long shopId, Long itemId);

  PurchaseInventoryDTO getPurchaseInventoryById(long orderId, Long shopId);


  public Service deleteService(Long shopId,Long serviceId) throws Exception;

  public Service getServiceById(Long shopId,Long serviceId);

  public ServiceDTO saveOrUpdateService(Long shopId,String serviceName) throws Exception;

  Map<Long,ReceivableDTO> getReceivableDTOByShopIdAndArrayOrderId(Long shopId, Long... orderId);

  List<Long> getRepairOrderIds(Long shopId, int start, int pageSize);
  List<Long> getInventoryCheckOrderIds(Long shopId, int start, int pageSize);
  List<Long> getAllocateRecordOrderIds(Long shopId, int start, int pageSize);
  List<Long> getInnerPickingOrderIds(Long shopId, int start, int pageSize);
  List<Long> getInnerReturnOrderIds(Long shopId, int start, int pageSize);
  List<Long> getBorrowOrderIds(Long shopId, int start, int pageSize);
  List<Long> getReturnBorrowOrderIds(Long shopId, int start, int pageSize);

  List<RepairOrderItemDTO> getRepairOrderItemDTOsByShopIdAndArrayOrderId(Long shopId,Long... orderId);

  Map<Long, List<RepairOrderServiceDTO>> getRepairOrderServiceDTOByShopIdAndArrayOrderId(Long shopId, Long... orderId);

  Map<Long,PurchaseReturnDTO> getMapOfPurchaseReturnByShopIdAndOrderIds(Long shopId, Long... orderIds) ;

  Map<Long,MemberCardOrderDTO> getMapOfMemberCardOrdersByShopIdAndOrderIds(Long shopId, Long... orderIds) ;

  Map<Long,SalesOrderDTO> getMapOfSalesOrdersByShopIdAndOrderIds(Long shopId, Long... orderIds) ;

  Map<Long,WashBeautyOrderDTO> getMapOfWashBeautyOrdersByShopIdAndOrderIds(Long shopId, Long... orderIds);

  Map<Long,Map<Long, WashBeautyOrderItemDTO>> getWashBeautyOrderItemByShopIdAndArrayOrderId(Long shopId, Long... orderIds);

  List<PurchaseInventoryDTO> getPurchaseInventoryByShopIdAndOrderIds(Long shopId, Long... orderIds);

  List<PurchaseReturnDTO> getPurchaseReturnByShopIdAndOrderIds(Long shopId, Long... orderIds);

  List<SalesReturnDTO> getSalesReturnByShopIdAndOrderIds(Long shopId, Long... orderIds);

  List<SalesOrderDTO> getSalesOrdersByShopIdAndOrderIds(Long shopId, Long... orderIds);

  List<PreBuyOrderDTO> getPreBuyOrdersByShopIdAndOrderIds(Long shopId, Long... orderIds);

  List<QuotedPreBuyOrderDTO> getQuotedPreBuyOrdersByShopIdAndOrderIds(Long shopId, Long... orderIds);

  List<InventoryCheckDTO> getInventoryCheckDTOsByShopIdAndOrderIds(Long shopId, Long... orderIds);

  List<AllocateRecordDTO> getAllocateRecordDTOsByShopIdAndOrderIds(Long shopId, Long... orderIds);

  List<InnerPickingDTO> getInnerPickingDTOsByShopIdAndOrderIds(Long shopId, Long... orderIds);

  List<InnerReturnDTO> getInnerReturnDTOsByShopIdAndOrderIds(Long shopId, Long... orderIds);

  List<BorrowOrderDTO> getBorrowOrderDTOsByShopIdAndOrderIds(Long shopId, Long... orderIds);

  List<ReturnOrderDTO> getReturnBorrowOrderDTOsByShopIdAndOrderIds(Long shopId, Long... orderIds);

  List<WashBeautyOrderDTO> getWashBeautyOrdersDetailByShopIdAndOrderIds(Long shopId, Long... orderIds);

  List<MemberCardOrderDTO> getMemberCardOrdersDetailByShopIdAndOrderIds(Long shopId, Long... orderIds);

  List<MemberCardReturnDTO> getMemberReturnCardOrdersDetailByShopIdAndOrderIds(Long shopId, Long... orderIds);

  MemberDTO getMemberInfo(Long shopId,Long customerId) throws Exception;

  MemberDTO getMemberInfo(Long memberId) throws Exception;

  List<Long> getPurchaseInventoryIds(Long shopId, int start, int size)throws Exception;

  List<PurchaseInventoryItemDTO> getPurchaseInventoryItemByOrderIds(Long...orderIds)throws Exception;

  Map<Long,ServiceDTO> getServiceByServiceIdSet(Long shopId, Set<Long> vehicleIds);

  List<Long> getPurchaseReturnIds(Long shopId, int start, int size)throws Exception;

  List<Long> getSalesReturnIds(Long shopId, int start, int size)throws Exception;

  List<PurchaseReturnItemDTO> getPurchaseReturnItemDTOs (Long...orderIds)throws Exception;

  List<SalesReturnItemDTO> getSalesReturnItemDTOs (Long shopId,Long...orderIds)throws Exception;

  List<Long> getPurchaseOrderIds(Long shopId, int start, int size) throws Exception;

  List<PurchaseOrderItemDTO> getPurchaseOrderItemDTOs(Long... orderIds) throws Exception;

  List<Long> getSalesOrderDTOs(Long shopId, int start, int size) throws Exception;

  List<Long> getPreBuyOrderIds(Long shopId, int start, int size) throws Exception;

  List<Long> getQuotedPreBuyOrderIds(Long shopId, int start, int size) throws Exception;


  List<SalesOrderItemDTO> getSalesOrderItemDTOs(Long shopId,Long... orderIds) throws Exception;

  List<PreBuyOrderItemDTO> getPreBuyOrderItemDTOs(Long shopId,Long... orderIds) throws Exception;

  List<QuotedPreBuyOrderItemDTO> getQuotedPreBuyOrderItemDTOs(Long shopId,Long... orderIds) throws Exception;

  public Map<Long,List<QuotedPreBuyOrderItemDTO>> getQuotedPreBuyOrderItemDTOMap(Long shopId,Long...preBuyOrderIds);

  List<InventoryCheckItemDTO> getInventoryCheckItemDTOs(Long... orderIds) throws Exception;

  List<AllocateRecordItemDTO> getAllocateRecordItemDTOs(Long... orderIds) throws Exception;

  //获取所有入库单   ，用于数据初始化
  public List<PurchaseInventoryDTO> getAllPurchaseInventory() throws Exception;

  void saveInventory(InventoryDTO inventoryDTO)throws Exception;

  void checkAndInsertInventorySearchIndex(Long productLocalInfoId, Long shopId) throws Exception;

  void checkAndInsertInventorySearchIndexes(Long shopId,Long ...productLocalInfoId) throws Exception;

  //根据shopid查询服务项目表
  public List<ServiceDTO> getServiceByShopId(Long shopId) throws Exception;

  void saveBusinessStatChange(BusinessStatDTO businessStatDTO);

  BusinessStatDTO getBusinessStatChangeOfDay(Long shopId, long year, long month, long day);

  Map<Long, BusinessStatDTO> getDayBusinessStatChangeMap(Long shopId, long year, long month);

  BusinessStatDTO sumBusinessStatChangeForMonth(Long shopId, long year, long month);

  BusinessStatDTO sumBusinessStatChangeForYear(Long shopId, long year);

  Map<Long,BusinessStatDTO> getMonthBusinessStatChangeMap(Long shopId, long year);

  Map<String,BusinessStatDTO> getBusinessStatMapByYearMonth(Long shopId,String... yearMonth);
  Map<String,BusinessStatDTO> getBusinessStatMapByYearMonthDay(Long shopId,String yearMonthDayStart,String yearMonthDayEnd);
  Map<String,BusinessStatDTO> getBusinessStatChangeMapByYearMonth(Long shopId,Long[] year, Long[] month);
  Map<String,BusinessStatDTO> getBusinessStatChangeMapByYearMonthDay(Long shopId,Long[] year, Long[] month, Long[] day);
  /**
   * 根据开始时间 结束时间 获取施工单作废列表
   * @param shopId
   * @param startTime
   * @param endTime
   * @return
   * @throws Exception
   */
  public List<RepealOrder> getRepealOrderListByRepealDate(long shopId,long startTime,long endTime,Pager pager) throws Exception;


  /**
   * 根据开始时间 结束时间 获取施工单作废列表
   * @param shopId
   * @param startTime
   * @param endTime
   * @return
   * @throws Exception
   */
  public int countRepealOrderByRepealDate(long shopId,long startTime,long endTime) throws Exception;


  public RunningStatDTO saveRunningStat(RunningStatDTO runningStatDTO);

  public int countPurchaseInventoryOrderByCreated(long shopId,long startTime,long endTime) throws Exception;

  public List<PurchaseInventoryDTO> getInventoryOrderListByPager(long shopId,long startTime,long endTime,
                                                                 Pager pager) throws Exception;

  public int countPurchaseReturnOrderByCreated(long shopId,long startTime,long endTime) throws Exception;

  public List<PurchaseReturnDTO> getPurchaseReturnOrderListByPager(long shopId,long startTime,long endTime,
                                                                   Pager pager) throws Exception;

  public List<RunningStatDTO> getRunningStatByYearMonthDay(long shopId,Integer year,Integer month,Integer day,Integer resultSize,Sort sort);

  public List<RunningStatDTO> getRunningStatMonth(long shopId, long year, String queryString);

  public List<String> countReceptionRecordByReceptionDate(long shopId,long startTime,long endTime) throws Exception;

  public List<ReceptionRecordDTO> getReceptionRecordByReceptionDate(long shopId,long startTime,long endTime,Pager pager);

  public List<ReceptionRecordDTO> getReceptionRecordByOrderId(long shopId,long orderId,OrderTypes orderTypes);

  public int countPayHistoryRecordByPayTime(long shopId,long startTime,long endTime) throws Exception;

  public List<PayableHistoryRecordDTO> getPayHistoryRecordByPayTime(long shopId,long startTime,long endTime,Pager pager);

  /**
   * 根据收款单ID查询收款单记录
   *
   * @param receivableId
   * @return
   */
  public List<ReceptionRecord> getReceptionRecordsByReceivalbeId(Long receivableId);


  public String getReceiptNo(Long shopId,OrderTypes types,Long time);
  public String getBcgogoOrderReceiptNo(Long shopId,OrderTypes types,Long time);

  public String getLastOrderReceiptNo(Long shopId,OrderTypes types,String receiptNoNotNo);

  public List getOrderDTONoReceiptNo(OrderTypes types,int num,int pageNo);

  public void updateOrderListReceiptNo(List OrderList,OrderTypes types);

  public int countOrderNoReceiptNo(Long shopId,OrderTypes types);

  public ReceiptNo getReceiptNOByShopIdAndType(Long shopId,OrderTypes types);

  public ReceiptNo saveOrUpdateReceiptNo(ReceiptNo receiptNo);
  /**
   * 批量保存流水数据
   * @param businessStatDTOList
   */
  public void saveBusinessStat(Long shopId,Long year,List<BusinessStatDTO> businessStatDTOList);

  /**
   *保存营业分类
   */
  public void saveCategoryFromDTO(CategoryDTO categoryDTO);

  List<PurchaseOrderDTO> getPurchaseOrdersByShopIdAndOrderIds(Long shopId, Long... ids);

  List<PurchaseOrderDTO> getPurchaseOrdersWithItemAndProductByOrderIds(Long shopId, Long... ids) throws Exception;


  public BizStatPrintDTO getBusinessChangeInfoToPrint(Long shopId,Long startTime,Long endTime);


  /**
   * 施工单结算后更新进厂里程数
   * @param repairOrderId
   * @param startMileage
   */
  public boolean updateStartMileage(Long repairOrderId,double startMileage);

  public int countRepairOrderOfNotSettled(Long shopId,Long customerId);

  public List<RepairOrderDTO> getRepairOrderReceiptNoOfNotSettled(Long shopId,Long customerId);

  public ImportResult importMemberServiceFromExcel(ImportContext importContext) throws Exception;

  public boolean batchCreateMemberServiceAndService(List<MemberServiceDTO> memberServiceDTOList)  throws BcgogoException;

  public int countUndoneRepairOrderByVehicleId(Long shopId, Long vehicleId) throws Exception;

  List<RepairOrderDTO> getRepairOrdersByShopIdAndOrderIds(Long shopId, Long... orderIds);

  Map<Long,RepairOrderDTO> getRepairOrderMapByShopIdAndOrderIds(Long shopId, Long... orderIds);

  MemberCardOrderDTO getLatestMemberCardOrder(Long shopId, Long customerId);

  MemberCardReturnDTO saveMemberCardReturn(MemberCardReturnDTO memberCardReturnDTO) throws Exception;

  MemberCardReturnDTO getMemberCardReturnDTOById(Long shopId, Long orderId);

  List<MemberCardReturnItemDTO> getMemberCardReturnItemDTOByOrderId(Long shopId, Long orderId);

  List<MemberCardReturnServiceDTO> getMemberCardReturnServiceDTOByOrderId(Long shopId, Long orderId);

  public  String getOrderReceiptNO(ReceiptNoDTO receptNoDTO);

  public Double getTotalPayable(RecOrPayIndexDTO recOrPayIndexDTO);


  public ServiceDTO saveOrUpdateServiceForWashBeauty(Long shopId,String serviceName) throws Exception;

  public ServiceDTO changeServiceTimeType(Long shopId,Long serviceId,ServiceTimeType timeType);

  public void initServiceTimeType(List<Long> idList);

  public List<Service> getServiceByIds(List<Long> idList);

  public ImportResult importOrderFromExcel(ImportContext importContext) throws Exception ;

  public boolean batchCreateImportedOrder(List<ImportedOrderTemp> orderTemps, TxnWriter txnWriter ) throws BcgogoException ;

  Double getMemberCardConsumeTotal(Long shopId, Long id);

  /**
   * 根据开始时间 和结束时间 统计某一段时间内退卡的单据列表
   * @param shopId
   * @param startTime
   * @param endTime
   * @return
   */
  public List<MemberCardReturnDTO> getMemberReturnListByReturnDate(long shopId, long startTime, long endTime);

  public void importSupplierRecord(Long shopId,List<SupplierRecordDTO> supplierRecordDTOList);

  public ReceivableDTO saveReceivableDTO(ReceivableDTO receivableDTO);
  /**
   * 获取单据Item
   * @param shopId
   * @param start
   * @param pageSize
   * @return
   */
  List<Long> getRepairOrderItemIds(Long shopId, int start, int pageSize);

  public List<RepairOrderItemDTO> getRepairOrderItemDTOById(Long shopId, Long... repairOrderItemId) throws Exception;


  /**
   * 获取单据施工项目Item
   * @param shopId
   * @param start
   * @param pageSize
   * @return
   */
  List<Long> getRepairOrderServiceItemIds(Long shopId, int start, int pageSize);

  public List<RepairOrderServiceDTO> getRepairOrderServiceDTOById(Long shopId, Long... repairOrderServiceItemId) throws Exception;

  List<Long> getPurchaseInventoryOrderItemIds(Long shopId, int start, int pageSize)throws Exception;

  List<PurchaseInventoryItemDTO> getPurchaseInventoryOrderItemDTOById(Long shopId, Long... inventoryItemId)throws Exception;

  /**
   * 获取单据Item
   * @param shopId
   * @param start
   * @param pageSize
   * @return
   */
  List<Long> getSaleOrderItemIds(Long shopId, int start, int pageSize);

  List<SalesOrderItemDTO> getSaleOrderItemDTOById(Long shopId, Long... saleOrderItemId) throws Exception;

  List<Long> getWashBeautyOrderItemIds(Long shopId, int start, int pageSize);

  List<WashBeautyOrderItemDTO> getWashBeautyOrderItemDTOById(Long shopId, Long... washItemId) throws Exception;

  Map<Long, CategoryDTO> getServiceCategoryMapByServiceId(Long shopId, Long... serviceId);

  public List<RepairOrderService> getRepairOrderService();

  public List<RepairOrderService> initRepairOrderServiceCategory(List<RepairOrderService> repairOrderServiceList,Map<Long,Category> map);
  PurchaseInventory getFirstPurchaseInventoryByVestDate(Long shopId);

  List<PurchaseInventoryDTO> getPurchaseInventoryDTOByVestDate(Long shopId, long startTime, long endTime);

  void batchCreateProductModifyLog(List<ProductModifyLogDTO> logResults);

  List<ProductModifyLogDTO> getProductModifyLogByStatus(ProductModifyOperations productModifyOperation, StatProcessStatus[] status);

  List<ProductModifyLogDTO> getProductModifyLogDTOByRelevanceStatus(Long productId,ProductRelevanceStatus relevanceStatus);

  Map<Long,List<ProductModifyFields>> getRelevanceStatusUnCheckedProductModifiedFieldsMap(Long... productLocalInfoId);

  void batchUpdateProductModifyLogStatus(List<ProductModifyLogDTO> toProcessLogs, StatProcessStatus done);

  PurchaseReturn getFirstPurchaseReturnByVestDate(Long shopId);

  List<PurchaseReturnDTO> getPurchaseReturnDTOByVestDate(Long shopId, long startTime, long endTime);

  public SalesOrder getFirstSalesOrderByVestDate(Long shopId);

  public RepairOrder getFirstRepairOrderByVestDate(Long shopId);

  public List<RepairOrderDTO> getRepairOrderListByVestDate(Long shopId,long startTime,long endTime);

  Long getFirstPurchaseInventoryCreationDateByProductIdShopId(Long shopId, Long productId);

  List<PurchaseInventoryDTO> getPurchaseInventoryDTOByProductIdCreationDate(Long shopId, Long productId,  Long startTime, Long endTime);

  Long getFirstPurchaseReturnCreationDateByProductIdShopId(Long shopId, Long productId);

  List<PurchaseReturnDTO> getPurchaseReturnDTOByProductIdCreationDate(Long shopId, Long productId, Long startTime, Long endTime);

  public OrderSearchResultListDTO getImportedOrderByConditions(OrderSearchConditionDTO searchConditionDTO) throws PageException;

  public List<Long> getPurchaseInventoryIdFromPayableHistory(Long shopId, Long purchaseReturnId, PaymentTypes paymentType);
  WashBeautyOrder getFirstWashBeautyOrderByVestDate(Long shopId);

  List<WashBeautyOrderDTO> getWashBeautyOrderDTOByVestDate(Long id, long startTime, long endTime);

  Long getFirstRepairOrderCreationTimeByVehicleId(Long shopId, Long vehicleId);

  Long getFirstWashBeautyOrderCreationTimeByVehicleId(Long shopId, Long vehicleId);

  List<PurchaseInventoryDTO> getPurchaseInventoryDTOByCreationDate(Long shopId, long begin, long end);




  /**
   * 根据shop_id获取一段时间内退卡单据的条数 和总金额
   * @param shopId
   * @param startTime
   * @param endTime
   * @return
   */
  public List<String> getMemberReturnOrderCountAndSum(long shopId, long startTime, long endTime,OrderSearchConditionDTO orderSearchConditionDTO);

  /**
   * 根据开始时间、结束时间、shop_id、分页组件、排序类型获取购卡续卡单据列表
   * @param shopId
   * @param startTime
   * @param endTime
   * @param pager
   * @param arrayType
   * @return
   * @throws Exception
   */
  public List<MemberCardReturnDTO> getMemberReturnListByPagerTimeArrayType(long shopId, long startTime, long endTime, Pager pager, String arrayType,OrderSearchConditionDTO orderSearchConditionDTO) throws Exception;


  public Long getTodoSalesOrderCount(Long shopId, Long startTime, Long endTime, List<Long> customerIdList, String receiptNo, String orderStatus);

  public List<SalesOrderDTO> getTodoSalesOrderDTOListByCondition(Long shopId, Long startTime, Long endTime, List<Long> customerIdList, String receiptNo, String orderStatus, Pager pager);

  public List<SalesOrderDTO> getAllTodoSalesOrderDTOList(Long shopId, List<Long> customerIdList);

  public Long getTodoSalesReturnOrderCount(Long shopId, Long startTime, Long endTime, List<Long> customerIdList, String receiptNo, String orderStatus);

  public List<SalesReturnDTO> getTodoSalesReturnOrderDTOListByCondition(Long shopId, Long startTime, Long endTime, List<Long> customerIdList, String receiptNo, String orderStatus, Pager pager);

  public Long getTodoPurchaseOrderCount(Long shopId, Long startTime, Long endTime, List<Long> supplierIdList, String receiptNo, String orderStatus, String timeField);

  public List<PurchaseOrderDTO> getTodoPurchaseOrderDTOListByCondition(Long shopId, Long startTime, Long endTime, List<Long> supplierIdList, String receiptNo, String orderStatus, Pager pager, String timeField)throws Exception;

  public Long getTodoPurchaseReturnOrderCount(Long shopId, Long startTime, Long endTime, List<Long> supplierIdList, String receiptNo, String orderStatus);

  public List<PurchaseReturnDTO> getTodoPurchaseReturnOrderDTOListByCondition(Long shopId, Long startTime, Long endTime, List<Long> supplierIdList, String receiptNo, String orderStatus, Pager pager);

  void saveOrUpdateOrderIndexByOrderIdAndOrderType(Long shopId, OrderTypes orderType, Long... orderId) throws Exception;

  List<String> getDebtFromReceivableByCustomerId(Long shopId, Long customerId, OrderDebtType orderDebtType, ReceivableStatus receivableStatus);

  public PurchaseInventoryDTO getPurchaseInventoryIdByPurchaseOrderId(Long shopId, Long purchaseOrderId);

  public void updateSaleOrderItems(List<SalesOrderItem> salesOrderItemList);

  public void updateDebtRemindStatus(Long debtId,Long shopId);

  public PayableDTO getPayableDTOByOrderId(Long shopId,Long orderId);

  public DebtDTO getDebtByShopIdOrderId(Long shopId,Long orderId);

  public List<OtherIncomeKindDTO> vagueGetOtherIncomeKind(Long shopId,String keyWord);

  public OtherIncomeKind changeOtherIncomeKindStatus(Long shopId,Long id,KindStatus status);

  public OtherIncomeKindDTO getOtherIncomeKindById(Long shopId,Long id);

  public List<OtherIncomeKind> getOtherIncomeKindByName(Long shopId,String name);

  public OtherIncomeKindDTO updateOtherIncomeKind(Long shopId,Long id,String name);

  public OtherIncomeKindDTO saveOrUpdateOtherIncomeKind(Long shopId,String name);

  public Map<String,OtherIncomeKindDTO>  batchSaveOrUpdateOtherIncomeKind(Long shopId,Set<String> kindNames);

  public List<SalesOrderOtherIncomeItemDTO> getSalesOrderOtherIncomeItemDTOs(Long... orderIds) throws Exception;

  public Map<Long, List<RepairOrderOtherIncomeItemDTO>> getRepairOrderOtherIncomeItemDTOByShopIdAndArrayOrderId(Long shopId, Long... orderId);

  List<PurchaseOrder> getPurchaseOrderByShopIdAndSupplierId(SupplierDTO supplierDTO);

  public List<Object[]> getSupplierHistoryOrderList(Long supplierId, Long shopId, Long startTime, Long endTime, List<OrderTypes> orderTypes, Pager pager);

  public Double getSupplierTotalMoneyByTimeRangeAndOrderType(Long shopId, Long supplierId, Long startTime, Long endTime, String orderType);

  Inventory saveInventoryAfterSaveNewProduct(Long shopId, Long productLocalInfoId, double amount, TxnWriter writer, String sellUnit);

  public List<ReceivableDTO> getMemberCardConsumeByMemberId(Long memberId);

  List<PurchaseInventoryItemDTO> getPurchaseInventoryItemByProductIdVestDate(Long shopId,Long productId,Long vestDate);

  public void saveNormalProductStatList(Collection<NormalProductInventoryStatDTO> normalProductInventoryStatDTOList);

  public List<Long> countStatDateByNormalProductIds(Long[] shopIds,Long[] normalProductIds,NormalProductStatType normalProductStatType);

  public List<NormalProductInventoryStat> getStatDateByNormalProductIds(Long[] shopIds,Long[] normalProductIds,NormalProductStatType normalProductStatType);

  public void deleteAllNormalProductStat();

  public void moveSupplierReturnPayableToPayable(List<Long> ids,Long... orderIds);

  public QualifiedCredentialsDTO getQualifiedCredentialsDTO(Long shopId,Long orderId);

  public RepairOrderDTO getRepairOrder(Long shopId,Long id);

  public void saveOrUpdateQualifiedCredentials(QualifiedCredentialsDTO qualifiedCredentialsDTO);

  public Map<Long,PurchaseReturnDTO> getPurchaseReturnByPurchaseReturnId(Long... id);

  int getInventoryCheckCount(InventoryCheckDTO inventoryCheckIndex);

  List<InventoryCheck> getInventoryChecks(InventoryCheckDTO inventoryCheckIndex);

  List<InventoryCheck> getInventoryCheckByIds(Long shopId,Set<Long> orderIds,Pager pager);

  Map<Long,List<InventoryCheckItemDTO>> getInventoryCheckItemByProductIds(Long shopId,Pager pager,Long... productIds) throws Exception;

  int getInventoryCheckItemCountByProductIds(Long shopId,Long... productIds);

  Double getStockAdjustPriceTotal(Long shopId);


  List<StatementAccountOrderDTO> getStatementAccountOrderByShopIdAndOrderIds(Long shopId, Long... orderIds);


  //******************** begin  待办事项重构  *********************

  //用于初始化提醒数据，将施工单的提醒事件导出
  public List<RepairRemindEvent> getAllRepairRemindEvent();

  //用于初始化提醒数据，将欠款提醒事件导出
  public List<Debt> getAllDebt();

  //用于初始化提醒数据，将采购待入库的提醒事件导出
  public List<InventoryRemindEvent> getAllInventoryRemindEvent();

  List<RemindEventDTO> getWXRemindEvent(Long startTime,Long endTime);

  //获取提醒
  public RemindEventDTO getRemindEventById(Long id);


  //根据提醒类型和单据ID获取提醒
  public List<RemindEventDTO> getRemindEventByOrderId(RemindEventType type,Long shopId, Long orderId);

  //根据提醒类型和old_remind_event_id获取提醒
  public RemindEventDTO getRemindEventByOldRemindEventId(RemindEventType type,Long shopId, Long oldRemindEventId);

  //根据customerId和提醒类型获取提醒
  public List<RemindEventDTO> getRemindEventListByCustomerIdAndType(RemindEventType type, Long customerId);

    //根据supplierId和提醒类型获取提醒
    public List<RemindEventDTO> getRemindEventListBySupplierIdAndType(RemindEventType type, Long supplierId);

  //根据orderId和事件状态获取提醒
  public List<RemindEventDTO> getRemindEventListByOrderIdAndObjectIdAndEventStatus(RemindEventType type, Long orderId, Long objectId, String eventStatus);

  //更新提醒
  public void updateRemindEvent(RemindEventDTO remindEventDTO);

  //删除提醒
  public void cancelCustomerRemindEventById(Long remindEventId);

  //根据单据ID逻辑删除提醒
  public boolean cancelRemindEventByOrderId(RemindEventType type, Long orderId);

  //根据单据状态逻辑删除提醒
  public boolean cancelRemindEventByOrderIdAndStatus(RemindEventType type, Long orderId, RepairRemindEventTypes status);

  //根据单据类型和ID逻辑删除提醒
  public boolean cancelRemindEventByOrderTypeAndOrderId(TxnWriter writer, RemindEventType type, OrderTypes orderType, Long orderId);

  //根据单据ID和objectId逻辑删除提醒
  public boolean cancelRemindEventByOrderIdAndObjectId(RemindEventType remindType, String eventStatus, Long orderId, Long objectId);

  //根据old_remind_event_id逻辑删除提醒
  public boolean cancelRemindEventByOldRemindEventId(RemindEventType type, Long oldRemindEventId, TxnWriter writer);

  //新增维修美容类提醒
  public void saveRemindEvent(RepairRemindEvent repairRemindEvent,RepairOrderDTO repairOrderDTO, TxnWriter writer);

  //新增欠款提醒
  public void saveRemindEvent(TxnWriter writer, Debt debt, String customerName, String mobile);

  //新增对账单欠款提醒
  public void saveRemindEvent(TxnWriter writer, StatementAccountOrderDTO statementAccountOrderDTO);

  //新增进销存提醒
  public void saveRemindEvent(InventoryRemindEvent inventoryRemindEvent);

  //新增客户预约提醒
  public void saveRemindEvent(CustomerServiceJob customerServiceJob, String customerName, String mobile, String licenceNo);

  //新增客户，车辆预约提醒


  //新增会员服务到期提醒
  public void saveRemindEvent(MemberService memberService, Long shopId, Long customerId, String customerName, String mobile);

  void updateRemindEventWXRemindStatus(Long remindEventId, String wxRemindStatus);

  //新增供应商欠款提醒,在做入库退货单时用到
  public void saveRemindEvent(SupplierDTO supplierDTO,PurchaseReturnDTO purchaseReturnDTO,Long payableId,TxnWriter writer);

  //新增既是客户又是供应商欠款提醒,在建立关联的时候会用到
  public void saveRemindEvent(SupplierDTO supplierDTO,List<PayableDTO> payableDTOs);

  public void saveRemindEvent(List<Payable> payables);  //新增供应商的欠费提醒时做初始化时用到
  //更新客户生日提醒
  public boolean updateCustomerBirthdayRemindEvent(Long customerId, Long newBirthday);

  //根据事项类型，从Memcache中获取记录条数
  public int getRemindEventAmountByType(Long shopId, RemindEventType type, List<Long> customerOrSupplierIdList, Long flashTime);

  //更新缓存中待办事项的数量
  public void updateRemindCountInMemcacheByTypeAndShopId(RemindEventType type, Long shopId);

  //更新缓存中的待办单据数量
  public void updateTodoOrderCountInMemcacheByTypeAndShopId(RemindEventType type, Long shopId, List<Long> customerOrSupplierIdList);

  //查询维修美容提醒中“缺料待修”状态的提醒
  public List<RemindEventDTO> getLackStorageRemind(Long shopId, Integer pageNo, Integer pageSize);

  //查询维修美容提醒中“缺料待修”状态的提醒
  public int countLackStorageRemind(Long shopId);

  //******************** end  待办事项重构  *********************

  List<Debt> getAllDebtsByCustomerIds(Long shopId, Long[] customerIds);

  /**
   * 为客户列表服务,返回客户的退货总金额和退货次数
   * @param shopId
   * @param customerIds
   * @return Map, key为customerId, value: Object[], object[0] 为退货总金额，object[1] 为退货次数
   */
  Map<Long, Object[]> getTotalReturnAmountByCustomerIds(Long shopId, Long... customerIds);

  //保存客户服务
  void saveRepairOrderRemindEvent(RepairOrderDTO repairOrderDTO);

  public ImportResult simpleImportInventoryFromExcel(ImportContext importContext) throws BcgogoException;

  public ImportResult simpleImportMemberServiceFromExcel(ImportContext importContext) throws Exception;

  public ImportResult simpleImportOrderFromExcel(ImportContext importContext) throws Exception ;


  public ReceivableDTO getReceivableByShopIdOrderId(Long shopId,Long orderId) throws Exception;


  PurchaseOrderDTO getPurchaseOrderById(Long purchaseOrderId) throws Exception;

  public SalesOrderDTO getSalesOrderByPurchaseOrderIdShopId(Long purchaseOrderId,Long supplierShopId) throws Exception;

  public List<PurchaseOrder> getPurchaseOrderBySupplierShopId(Long supplierShopId);

  public int countRemindEvent(Long shopId,RepairRemindEventTypes repairRemindEventTypes);//计算来料待修 缺料待修 待交付的数量

  public  List<RemindEventDTO> queryRepairRemindEvent(Long shopId, Long flashTime, RepairRemindEventTypes repairRemindEventTypes, Integer pageNo, Integer pageSize);//根据提醒类型查找来料待修 缺料待修 待交付

  public int getTotalRemindAmount(Long shopId); //得到所有的待办事项数量

  public void initCustomerStock()  throws Exception;

  //productLocalInfo 存在一个product 但是没有销售单位和库存单位 ，添加一个单位，更新productLocalInfo，inventory，inventorySearchIndex，solr
  void updateProductUnit(Long shopId, Long productId, String storageUnit, String sellUnit, Long rate) throws Exception;

  public Double[] getPayableAndReceivable(Long shopId, Long customerId);
  public void getPayableAndReceivableToModel(ModelMap model, Long shopId, Long customerId);  //客户的应收应付款

  void updateProductUnit(Long shopId, BcgogoOrderItemDto[] itemDTOs) throws Exception;

  public Boolean validateCouponNoUsed(Long shopId,String couponType,String couponNo);

  public void updateRemindEvent(Long shopId, Long customerId, Long supplierId);//既是客户又是供应商，当断开关系时，更新remind_event表

  public void updateRemindEvent2(Long shopId, Long customerId, Long supplierId);//当建立既是客户又是供应商关系时，更新remind_event表

  public List<PayableDTO> getPayableDTOBySupplierIdAndOrderType(Long shopId, Long supplierId, OrderTypes orderType);


  /**
   *  这个接口只负责保存product，不负责校验。productName 为空的将不保存  ,storeHouseId 不为空则保存商品库存到某仓库
   * @param shopId
   * @param storeHouseId
   * @param productDTOs
   * @throws Exception
   */
  List<Long> batchSaveProduct(Long shopId,Long storeHouseId, ProductDTO[] productDTOs)throws Exception;

  List<Long> batchSaveProductWithReindex(Long shopId,Long storeHouseId, ProductDTO[] productDTOs)throws Exception;

  Result saveOrUpdateProductInSales(Result result,ProductDTO productDTO) throws Exception;

  Result batchSaveGoodsInSales(Result result,Long shopId,Long userId,ProductDTO... fromProductDTOs) throws Exception;

  Map<String,ProductDTO> getOrderPromotionsDetail(Long shopId,SearchConditionDTO searchConditionDTO) throws Exception;

  Map<String,ProductDTO> getOrderPromotionsHistoryDetail(Long shopId,SearchConditionDTO searchConditionDTO) throws Exception;


  //校验商品保存
  Result validateSaveNewProduct(ProductDTO productDTO) throws Exception;

  public List<Payable> getSupplierPayable();//得到有欠款的供应商的Payable,该方法只用于初始化（欠费提醒中加入供应商的应收）

  public void updateRemindEventStatus(Long shopId, Long customerOrSupplierId, String identity);

  public List<OrderItemPromotionDTO> getOrderItemPromotionsByOrderItemId(Long orderItemId);

  public Long getSalesNewOrderCountBySupplierShopId(Long supplierShopId,Long startTime, Long endTime,String orderStatus, String timeField);  //通过供应商的shopId，得到新订单数量

  void saveProductCategoryAndRelation(Long shopId,Long userId,ProductDTO... fromProductDTOs) throws Exception;

  public List<ServiceDTO> getUseTimesMostService(Long shopId);

  List<RepairAndDraftSearchResultDTO> getRepairAndDraftOrders(DraftOrderSearchDTO draftOrderSearchDTO) throws Exception;

  int countRepairOrders(Long shopId, Long vechicleId);

  void deleteTxnRemind(Long shopId, Long purchaseOrderId);

  void updateDebtRemindDeletedType(Long shopId, Long customerOrSupplierId, String identity, DeletedType deletedType);
  //保存用户的单据操作日志
  public void saveOperationLogTxnService(OperationLogDTO... operationLogDTO) throws Exception;


  /**
   * 记录提醒框是否发送的状态
   * @param shopId
   * @param isSend
   */
  void updateRepairOrderMessageFlag(Long shopId,String isSend,Long repairId);

  void updateProductModifyLogDTORelevanceStatus(Long productLocalInfoId, ProductRelevanceStatus relevanceStatus);

  public int countStatDetailByNormalProductIds(Long[] shopIds, Long normalProductId, NormalProductStatType normalProductStatType);

  public List<NormalProductInventoryStat> getStatDetailByNormalProductIds(Long[] shopIds,Long normalProductId,NormalProductStatType normalProductStatType,Pager pager);

  /**
   * 更新receivable的couponConsume字段
   * @param shopId
   * @param receivableId
   * @param couponConsume
   */
  public void updateReceivableCouponConsume(Long shopId, Long receivableId, Double couponConsume);

}