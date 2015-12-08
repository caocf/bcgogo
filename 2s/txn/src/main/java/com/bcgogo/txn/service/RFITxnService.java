package com.bcgogo.txn.service;

import com.bcgogo.api.AppUserCustomerDTO;
import com.bcgogo.common.Pager;
import com.bcgogo.common.Pair;
import com.bcgogo.common.Result;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.enums.*;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.notification.dto.CustomerRemindSms;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.remind.dto.RemindEventDTO;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.search.dto.OrderIndexDTO;
import com.bcgogo.search.model.InventorySearchIndex;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.model.*;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.CustomerRecordDTO;
import com.bcgogo.user.dto.MemberDTO;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.user.dto.VehicleDTO;
import com.bcgogo.user.model.app.AppUserCustomer;
import org.springframework.ui.ModelMap;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RFITxnService {
  public PurchaseOrderDTO createPurchaseOrder(Long shopId, Long userId, String userName, String supplierId, String productIds) throws Exception;

  /**
   * 创建在线采购单同时创建销售单
   * @param purchaseOrderDTO
   * @param salesOrderDTO
   * @throws Exception
   */
  @Deprecated
  public void saveOrUpdatePurchaseOrderOnline(PurchaseOrderDTO purchaseOrderDTO, SalesOrderDTO salesOrderDTO) throws Exception;

  /**
   * 创建在线采购单，不创建销售单
   * @param purchaseOrderDTO
   * @throws Exception
   */
  public void saveOrUpdatePurchaseOrderOnlineNotCreateSalesOrderDTO(PurchaseOrderDTO purchaseOrderDTO) throws Exception;

  public void saveOrUpdatePurchaseOrder(PurchaseOrderDTO purchaseOrderDTO) throws Exception;

  public PurchaseOrderDTO getPurchaseOrderDTOById(Long id,Long shopId) throws Exception;

  /**
   *
   * @param id
   * @param supplierShopId
   * @return
   * @throws Exception
   */
  public PurchaseOrderDTO getPurchaseOrderDTOByIdAndSupplierShopId(Long id,Long supplierShopId) throws Exception;

	public RepairOrderDTO getRepairOrderDTOById(Long id, Long shopId)throws Exception;

	public RepairOrderDTO getRepairOrderDTODetailById(Long id, Long shopId)throws Exception;

  public InventoryDTO getInventoryByShopIdAndProductId(Long shopId, Long productId) throws Exception;

  public PurchasePriceDTO getLatestPurchasePriceByShopIdAndProductId(Long shopId, Long productId) throws Exception;

  public void saveOrUpdatePurchaseInventory(PurchaseInventoryDTO purchaseInventoryDTO) throws Exception;

  public PurchaseInventoryDTO getPurchaseInventoryById(Long id) throws Exception;

  public List<VehicleDTO> populateRepairOrderDTO(RepairOrderDTO repairOrderDTO) throws Exception;

  public VehicleDTO populateVehicleAppointOrderDTO(AppointOrderDTO appointOrderDTO) throws Exception;

  public PurchaseInventoryDTO populatePurchaseInventoryDTO(PurchaseInventoryDTO purchaseInventoryDTO) throws Exception;

  public List<ItemIndexDTO> getItemIndexDTOs(ItemIndexDTO itemIndexDTO) throws Exception;

  public void populateVehicleAppointment(RepairOrderDTO repairOrderDTO) throws BcgogoException;

  public PurchaseReturnDTO createPurchaseReturnDTO(PurchaseReturnDTO purchaseReturnDTO,List<ItemIndexDTO> indexDTOs) throws Exception;

  //本地入库退货单
  public PurchaseReturnDTO savePurchaseReturn(Long shopId,Long shopVersionId,PurchaseReturnDTO purchaseReturnDTO) throws Exception;
  //在线入库退货单
  public PurchaseReturnDTO saveOnlinePurchaseReturn(Long shopId,Long shopVersionId,PurchaseReturnDTO purchaseReturnDTO) throws Exception;

  public PurchaseReturnDTO settlePurchaseReturn(Long shopId, Long userId, PurchaseReturnDTO purchaseReturnDTO) throws Exception;

  public Map doItemChecked(Map map,Integer[] indexNo,Double[] itemCount,ItemIndexDTO itemIndexDTO,String prePageNum) throws Exception;

  public PurchaseReturnDTO getPurchaseReturnDTOById(Long purchaseReturnId) throws Exception;

  public void saveInventoryRemindEvent(TxnWriter txnWriter, PurchaseOrderDTO purchaseOrderDTO, PurchaseOrderItemDTO purchaseOrderItemDTO);

  public void updateSaleOrderStatus(Long shopId,Long saleOrderId,OrderStatus saleOrderStatus) throws Exception;

  void updateSaleOrderStatus(Long shopId, Long saleOrderId, OrderStatus saleOrderStatus, TxnWriter writer) throws Exception;

  public CustomerRecordDTO updateCustomerRecordByShopIdAndOrderId(Long shopId, Long orderId, Long customerId, OrderTypes orderType) throws Exception;

  public ReceivableDTO updateReceivable(Long shopId, Long orderId, OrderTypes orderType, ReceivableStatus status) throws Exception;

  public ReceivableDTO updateReceivable(Long shopId, Long orderId, OrderTypes orderType, ReceivableStatus status, TxnWriter writer) throws Exception;

  public void updateDebtByRepealOrder(Long shopId,Long orderId,Long customerId,DebtStatus debtStatus)throws Exception;

  void updateDebtByRepealOrder(Long shopId, Long orderId, Long customerId, DebtStatus debtStatus, TxnWriter writer) throws Exception;

  public void saveRepealOrderByOrderIdAndOrderType(Long shopId, Long orderId, OrderTypes orderType) throws Exception;

  public PurchaseOrderDTO copyPurchaseOrder(Long shopId, PurchaseOrderDTO purchaseOrderDTO) throws Exception;

  public PurchaseInventoryDTO copyPurchaseInventory(Long shopId, PurchaseInventoryDTO purchaseInventoryDTO) throws Exception;

  public SalesOrderDTO copyGoodSale(Long shopId, SalesOrderDTO salesOrderDTO) throws Exception;

  public void initIntemIndexProductId(ModelMap model) throws Exception;

  public List<PurchaseReturnItemDTO> getPurchaseReturnItemDTOs(Long shopId,ItemIndexDTO itemIndexDTO,Pager pager) throws Exception;

  public MemberCardOrderItem getLastMemberCardOrderItemByCustomerId(Long shopId,Long customerId) throws Exception;

  public Service getServiceById(Long serviceId) throws Exception;

  public Map<Long,Service> getServiceMapByIds(Long shopId,Set<Long> serviceIds) throws Exception;

  public Map<Long,ServiceDTO> getServiceDTOMapByIds(Long shopId,Set<Long> serviceIds) throws Exception;

  public List<ServiceDTO> getAllServiceByShopId(Long shopId) throws Exception;

  public ServiceDTO[] getServicesByCategory(Long shopId,String serviceName,String categoryName,CategoryType categoryType,Long pageNo,Long pageSize) throws Exception;

  public CategoryDTO[] getCategoryByShopId(Long shopId) throws  Exception;

  public ServiceDTO[] getServiceNoCategory(Long shopId,String serviceName, Long pageNo,Long pageSize) throws Exception;

  public ServiceDTO[] getServiceNoPercentage(Long shopId,Long pageNo,Long pageSize) throws Exception;

  public void updateServiceSingle(Long shopId,ServiceDTO serviceDTO) throws Exception;

  public ServiceDTO[] updateServiceCategory(Long shopId,Long categoryId,String categoryName,ServiceDTO[] serviceDTOs) throws Exception;

  public int countServiceByCategory(Long shopId,String serviceName,String categoryName,CategoryType categoryType) throws Exception;

  public int countServiceNoCategory(Long shopId, String serviceName) throws Exception;

  public int countServiceNoPercentage(Long shopId) throws Exception;

  public ServiceDTO[] updateServicePercentage(ServiceDTO[] serviceDTOs,Double percentageAmount) throws Exception;

  public ServiceDTO[] getServiceByWashBeauty(Long shopId,MemberDTO memberDTO) throws Exception;

  public List<MemberDTO> doServiceAndCategoryInit(List<MemberDTO> memberDTOs) throws Exception;

  public Service getRFServiceByServiceNameAndShopId(long shopId, String serviceName);

  public Service changeServiceStatus(Long shopId,Long serviceId,ServiceStatus status) throws Exception;


	public PurchaseReturnDTO getPurchaseReturnDTOById(Long shopId,Long id) throws Exception;

  public CustomerDTO doCustomerAndVehicle(Long shopId, Long userId, Long customerId, WashBeautyOrderDTO washBeautyOrderDTO) throws Exception;

  public PurchaseReturnDTO getPurchaseReturnDTOByPurchaseReturnNo(Long shopId,String purchaseReturnNo) throws Exception;

  public WashBeautyOrderDTO populateWashBeautyOrderDTO(WashBeautyOrderDTO washBeautyOrderDTO) throws Exception;
  /**
   *
   * @param bcgogoOrderDto
   * @param orderTypes
   * @return
   * @throws Exception
   */
  public RunningStatDTO updateOrderRepealReception(BcgogoOrderDto bcgogoOrderDto,OrderTypes orderTypes) throws Exception;

  public CategoryDTO getCategoryDTOByName(Long shopId, String name, CategoryType type);

	public void saveCategoryDTO(CategoryDTO categoryDTO)throws Exception;

	public Map<String,CategoryDTO> getAndSaveCategoryDTOByNames(Long shopId,CategoryType type, String...categoryNames)throws Exception;

  /**
   * 获得已分类的项目
   * @param shopId
   * @param pageNo
   * @param pageSize
   * @return
   * @throws Exception
   */
  public ServiceDTO[] getServiceCategory(Long shopId,Long pageNo,Long pageSize) throws Exception;

  /**
   * 获得某个店铺下已分类项目的条数
   * @param shopId
   * @return
   * @throws Exception
   */
  public int countServiceCategory(Long shopId) throws Exception;

	/**
	 * 根据productId，查询入库单，和采购单，是否有未结算单据。
	 * @param shopId
	 * @param productId
	 * @return Map<String, String>  单据IdStr，单据编号，
	 */
	List<OrderIndexDTO> getUnsettledOrdersByProductId(Long shopId, Long productId);

  List<OrderIndexDTO> getUnsettledOrdersByServiceId(Long shopId, Long serviceId);

	//获得一张order上的productLocalInfo id
	Set getProductIdsFromOrder(BcgogoOrderDto bcgogoOrderDto);

  BcgogoOrderDto getOrderDTOByOrderIdAndType(Long orderId,Long shopId,String orderType)throws Exception;

	//获得一张order上的已经删除的商品
	List<ProductDTO> getDeletedProductsByOrderDTOs(BcgogoOrderDto bcgogoOrderDto);

	//获得删除商品的信息
	String getDeletedProductMsg(List<ProductDTO> productDTOs);

	//单据作废的时候商品状态置空，为可用状态，更新库存上下限信息
 void updateDeleteProductsByOrderDTO(BcgogoOrderDto bcgogoOrderDto)throws Exception;

	//单据提交时校验已经删除商品信息
	Result getDeletedProductValidatorResult(BcgogoOrderDto bcgogoOrderDto)throws Exception;

	//作废洗车单，更新洗车单状态，更新欠款，实收状态
	void repealWashOrderById(Long shopId, Long washBeautyOrderId) throws Exception;

	//remove已经删除的施工项目
	String removeDisabledAndChangedServiceInfo(WashBeautyOrderDTO washBeautyOrderDTO)throws Exception;

	//生成新的单据
	void doCopyWashBeautyOrderDTO(WashBeautyOrderDTO washBeautyOrderDTO)throws Exception;

	//repairOrder 对应的debt 或者 receivable 数据不一致

	Long [] getDebtOrReceivableErrorRepairOrderIds()throws Exception;
	//用于错误数据纠正
	void saveReceivableAndUpdateDebt(RepairOrderDTO repairOrderDTO, DebtDTO debtDTO,List<ReceptionRecordDTO> receptionRecordDTOList)  throws Exception;

	//用于错误数据纠正
	void saveMissingDebt(RepairOrderDTO repairOrderDTO, ReceivableDTO receivableDTO,List<ReceptionRecordDTO> receptionRecordDTOList) throws Exception;

  /**
   *
   * 根据施工单进行数据纠正
   * @param repairOrderDTO
   */
  public void saveReceivableAndDebtFromRepairOrder(RepairOrderDTO repairOrderDTO,List<ReceptionRecordDTO> receptionRecordDTOList) throws Exception;

	public void updateMultipleInventoryRecommendedPrice(ProductDTO[] productDTOs, Long shopId) throws Exception;

  public CategoryDTO saveCategory(Long shopId,String categoryName);

  public Map<String,CategoryDTO> batchSaveAndGetCateGory(Long shopId,Set<String> categoryNames);

  public CategoryDTO updateCategory(Long shopId,Long category,String name);

  public Category getCategoryById(Long shopId,Long categoryId);

  public List<CategoryDTO> vagueGetCategoryByShopIdAndName(Long shopId,String keyWord);

  public List<CategoryDTO> getCategoryByShopIdAndName(Long shopId,String name);

  public CategoryDTO getCateGoryByServiceId(Long shopId,Long serviceId);

  public CategoryItemRelation saveOrUpdateCategoryItemRelation(Long shopId,Long categoryId,Long serviceId);

  public List<CategoryItemRelation> getCategoryItemRelation();

  public Category getCategory(Long categoryId);

  public List<WashBeautyOrderItem> getWashBeautyOrderItem();

  public List<WashBeautyOrderItem> initWashBeautyOrderCategory(List<WashBeautyOrderItem> washBeautyOrderItemList,Map<Long,Category> map);

  public List<Category> initCategoryList(List<Category> categoryList);

  public Category getCategoryByShopIdAndNameForInit(Long shopId,String name);

  public List<Category> getCategoryByNameNotDefault(String name);

  public void initCategoryRelationItem(Category category,List<Category> categoryList);

  public List<CategoryItemRelation> getCategoryItemRelationByCategoryId(Long categoryId);

  public void changeCategoryStatus(Long shopId,Long categoryId,CategoryStatus status);

  public void deleteCategoryRelationItemByCategoryId(Long categoryId);

  public Category getEnabledCategoryById(Long shopId,Long categoryId);

  /**
   * 施工单模板保存使用
   * @param txnWriter
   * @param shopId
   * @param categoryId
   * @param serviceId
   * @return
   */
  public CategoryItemRelation saveOrUpdateCategoryItemRelation(TxnWriter txnWriter,Long shopId,Long categoryId,Long serviceId);

  /**
   * 施工单模板保存使用
   * @param shopId
   * @param categoryName
   * @return
   */
  public CategoryDTO saveCategory(TxnWriter txnWriter,Long shopId,String categoryName);




  public void addInventorySearchIndex(List<InventorySearchIndex> inventorySearchIndexList, SalesOrderDTO salesOrderDTO,
                                       SalesOrderItemDTO salesOrderItemDTO) throws Exception;

  //在线退货改单
  PurchaseReturnDTO updatePurchaseReturn(Long shopId,Long shopVersionId, PurchaseReturnDTO purchaseReturnDTO) throws Exception;

	ExpressDTO getExpressDTOById(Long expressId);

  void updateService(Service service) throws Exception;

  Map<Long, List<CategoryItemRelation>> getCategoryItemRelationMapByServiceIds(Long... serviceIds);

  Result validateWashBeautyCopy(Long washBeautyOrderId, Long shopId);

  Result validatePurchaseReturnCopy(Long purchaseReturnId, Long shopId);

  void updateProductsForInventoryCheckDTO(InventoryCheckDTO inventoryCheckDTO) throws Exception ;

  //在customerShop中创建一个supplier
  SupplierDTO createRelationSupplier(ShopDTO customerShopDTO, ShopDTO supplierShopDTO, RelationTypes relationTypes) throws Exception;

  Result validateCustomerCancelSupplierShopRelation(Long shopId, SupplierDTO supplierDTO);

  Result validateSupplierCancelCustomerShopRelation(Long shopId, CustomerDTO customerDTO);

  void cancelSupplierRelationAndReindex(Long supplierShopId, Long customerShopId) throws Exception;

  PurchaseOrderDTO createPurchaseOrderOnline(Long shopId, Long userId, String userName, SupplierDTO supplierDTO,Map<Long,Pair<Double,Long>> productPairMap) throws Exception;

  Map<Long, CategoryDTO> getCategoryDTOMapById(Long shopId, Set<Long> categoryIds);

  //key 是serviceId,value 是CategoryDTO
  Map<Long,CategoryDTO> getCategoryDTOMapByServiceIds(Long shopId, Set<Long> serviceIds);

  ServiceDTO[] getServiceHasCategory(Long shopId, String serviceName, String categoryName, Long pageNo, Long pageSize) throws Exception;

  int countServiceHasCategory(Long shopId, String serviceName, String categoryName) throws Exception;

  //仅用于测试，用后即删
  String getRandomNProductIdStr(Long shopId,int n);

  Result validateDeleteOnlineSupplier(Long shopId, SupplierDTO supplierDTO);

  Result validateDeleteCustomerHasUnSettledOrder(Long shopId, CustomerDTO customerDTO);

  /**
   * 增加appUserNo到repair_order wash_beauty_order中
   * @param appUserCustomerList
   */
  public void addAppUserNoToRepairWashBeautyAppoint(List<AppUserCustomer> appUserCustomerList);


  public void calculateCustomerConsume(CustomerRecordDTO customerRecordDTO, BcgogoOrderDto bcgogoOrderDto, OrderTypes orderType, ReceivableDTO receivableDTO, boolean isRepeal);

  public Map<Long,List<NormalProductInventoryStatDTO>> getProductTopPriceByProductIdTime(Long[] productIds, Long startTime, Long endTime);

  public Result bcgogoAppSendMsg(Long shopId,Long userId,Long remindEventId,CustomerRemindSms customerRemindSms) throws Exception;

  public int countRemindMileageCustomerRemind(Long shopId);

  public List<RemindEventDTO> getRemindMileageCustomerRemind(Long shopId,Pager pager);

  }
