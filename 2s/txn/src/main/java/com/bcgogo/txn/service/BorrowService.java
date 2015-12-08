package com.bcgogo.txn.service;

import com.bcgogo.common.AllListResult;
import com.bcgogo.common.Result;
import com.bcgogo.config.dto.OperationLogDTO;
import com.bcgogo.config.service.IOperationLogService;
import com.bcgogo.enums.*;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.search.model.InventorySearchIndex;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.stat.dto.SupplierRecordDTO;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.model.*;
import com.bcgogo.txn.service.productThrough.IProductInStorageService;
import com.bcgogo.txn.service.productThrough.IProductOutStorageService;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.CustomerOrSupplierDTO;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.user.model.Supplier;
import com.bcgogo.user.service.ISupplierService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.utils.BcgogoShopLogicResourceUtils;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-3-5
 * Time: 下午12:45
 * To change this template use File | Settings | File Templates.
 */
@Component
public class BorrowService implements IBorrowService {
  private static final Logger LOG = LoggerFactory.getLogger(BorrowService.class);
  @Autowired
  private TxnDaoManager txnDaoManager;

  private IProductService productService;
  private IInventoryService inventoryService;
  private ITxnService txnService;
  private RFITxnService rfTxnService;
  private IStoreHouseService storeHouseService;
  private ISearchService searchService;
  private IProductHistoryService productHistoryService;
  private IOperationLogService operationLogService;
  private IRepairService repairService;

  public IProductService getProductService() {
    return productService == null ? ServiceManager.getService(IProductService.class) :productService;
  }

  public IInventoryService getInventoryService() {
    return inventoryService == null ?ServiceManager.getService(IInventoryService.class) :inventoryService;
  }

  public ITxnService getTxnService() {
    return txnService == null ?ServiceManager.getService(ITxnService.class) : txnService;
  }

  public RFITxnService getRfTxnService() {
    return rfTxnService == null ?ServiceManager.getService(RFITxnService.class) : rfTxnService;
  }

  public IStoreHouseService getStoreHouseService() {
    return storeHouseService == null ?ServiceManager.getService(IStoreHouseService.class) : storeHouseService;
  }

  public ISearchService getSearchService() {
    return searchService == null ?ServiceManager.getService(ISearchService.class) : searchService;
  }

  public IProductHistoryService getProductHistoryService() {
    return productHistoryService == null ? ServiceManager.getService(IProductHistoryService.class) : productHistoryService;
  }

  public IOperationLogService getOperationLogService() {
    return operationLogService == null ? ServiceManager.getService(IOperationLogService.class) : operationLogService;
  }

  public IRepairService getRepairService() {
    return repairService == null ? ServiceManager.getService(IRepairService.class) : repairService;
  }

  @Override
  public void saveBorrowOrder(BorrowOrderDTO borrowOrderDTO) throws Exception {
    if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(borrowOrderDTO.getShopVersionId())&& borrowOrderDTO.getStorehouseId()!=null) {
      StoreHouseDTO storeHouseDTO = getStoreHouseService().getStoreHouseDTOById(borrowOrderDTO.getShopId(), borrowOrderDTO.getStorehouseId());
      borrowOrderDTO.setStorehouseName(storeHouseDTO == null ? null : storeHouseDTO.getName());
    }
    Map<Long, InventorySearchIndex> inventorySearchIndexMap = new HashMap<Long, InventorySearchIndex>();
    InventoryLimitDTO inventoryLimitDTO = new InventoryLimitDTO();
    Set<Long> productIds = new HashSet<Long>();
    double total=0;
    for (BorrowOrderItemDTO itemDTO : borrowOrderDTO.getItemDTOs()) {
      if(itemDTO.getTotal()!=null){
        total+=itemDTO.getTotal();
      }
      if (itemDTO.getProductId() != null) {
        productIds.add(itemDTO.getProductId());
      }
    }
    if (!productIds.isEmpty()) {
      inventorySearchIndexMap = getSearchService().getInventorySearchIndexMapByProductIds(borrowOrderDTO.getShopId(), productIds.toArray(new Long[productIds.size()]));
    }
    getTxnService().updateProductUnit(borrowOrderDTO);
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Map<Long, ProductHistoryDTO> productHistoryDTOMap = getProductHistoryService().getOrSaveProductHistoryByLocalInfoId(borrowOrderDTO.getShopId(), productIds.toArray(new Long[productIds.size()]));
      //save order
      BorrowOrder borrowOrder = new BorrowOrder();
      borrowOrderDTO.setId(null);
      borrowOrderDTO.setTotal(total);
      borrowOrderDTO.setStatus(OrderStatus.SETTLED);
      borrowOrder.fromDTO(borrowOrderDTO);
      writer.save(borrowOrder);
      borrowOrderDTO.setId(borrowOrder.getId());
      //保存item
      ProductHistoryDTO productHistoryDTO = null;
      for (BorrowOrderItemDTO itemDTO : borrowOrderDTO.getItemDTOs()) {
        if(itemDTO.getProductId() == null || StringUtils.isBlank(itemDTO.getProductName())){
          continue;
        }
        itemDTO.setId(null);
        if (itemDTO.getProductHistoryId() == null) {
          productHistoryDTO = productHistoryDTOMap.get(itemDTO.getProductId());
          itemDTO.setProductHistoryId(productHistoryDTO == null ? null : productHistoryDTO.getId());
        }
        itemDTO.setOrderId(borrowOrderDTO.getId());
        BorrowOrderItem borrowOrderItem = new BorrowOrderItem();
        borrowOrderItem.fromDTO(itemDTO);
        borrowOrderItem.setOrderId(borrowOrder.getId());
        borrowOrderItem.setShopId(borrowOrder.getShopId());
        writer.save(borrowOrderItem);
        itemDTO.setId(borrowOrderItem.getId());

        String unit=itemDTO.getUnit();
        String sellUnit=itemDTO.getSellUnit();
        Long rate= itemDTO.getRate();
        String storageUnit=itemDTO.getStorageUnit();
        double sAmount= NumberUtil.doubleVal(itemDTO.getAmount());
        if(StringUtil.isNotEmpty(unit)&&StringUtil.isNotEmpty(sellUnit)&&rate!=null&&rate!=0
            &&StringUtil.isNotEmpty(storageUnit)&&!sellUnit.equals(storageUnit)){     //存在大小单位转换问题
          if(unit.equals(storageUnit)){  //借大单位
            sAmount=NumberUtil.doubleVal(itemDTO.getAmount())*rate;
          }
        }
        //处理库存信息
        if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(borrowOrderDTO.getShopVersionId())) {
          StoreHouseInventoryDTO storeHouseInventoryDTO = new StoreHouseInventoryDTO(borrowOrderDTO.getStorehouseId(), itemDTO.getProductId(), null);
          storeHouseInventoryDTO.setChangeAmount(sAmount * -1);
          getStoreHouseService().saveOrUpdateStoreHouseInventoryDTO(writer, storeHouseInventoryDTO);
        }
        Inventory inventory = writer.getInventoryByIdAndshopId(itemDTO.getProductId(),borrowOrderDTO.getShopId());
        if (inventory != null) {
          getInventoryService().caculateBeforeLimit(inventory.toDTO(), inventoryLimitDTO);
          inventory.setAmount(inventory.getAmount() - sAmount);
          writer.update(inventory);
          getInventoryService().caculateAfterLimit(inventory.toDTO(), inventoryLimitDTO);
          InventorySearchIndex inventorySearchIndex = inventorySearchIndexMap.get(itemDTO.getProductId());
          if (inventorySearchIndex != null) {
            inventorySearchIndex.setAmount(inventory.getAmount());
          }
        }

      }
      ServiceManager.getService(IProductOutStorageService.class).productThroughByOrder(borrowOrderDTO,OrderTypes.BORROW_ORDER,borrowOrderDTO.getStatus(),writer,null);
      writer.commit(status);
      //操作记录
      getOperationLogService().saveOperationLog(new OperationLogDTO(borrowOrderDTO.getShopId(), borrowOrderDTO.getUserId(),
          borrowOrderDTO.getId(), ObjectTypes.BORROW_ORDER, OperationTypes.UPDATE));
      //更新库存上下限
      getInventoryService().updateInventoryLimit(inventoryLimitDTO);
      //更新searchIndex
      if (inventorySearchIndexMap != null && !inventorySearchIndexMap.isEmpty()) {
        getInventoryService().addOrUpdateInventorySearchIndexWithList(borrowOrderDTO.getShopId(),
            new ArrayList<InventorySearchIndex>(inventorySearchIndexMap.values()));
      }


    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public Result saveReturnOrder(AllListResult result,ReturnOrderDTO returnOrderDTO) throws Exception {
    if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(returnOrderDTO.getShopVersionId())) {
      StoreHouseDTO storeHouseDTO = getStoreHouseService().getStoreHouseDTOById(returnOrderDTO.getShopId(), returnOrderDTO.getStorehouseId());
      returnOrderDTO.setStorehouseName(storeHouseDTO == null ? null : storeHouseDTO.getName());
    }
    Map<Long, InventorySearchIndex> inventorySearchIndexMap = new HashMap<Long, InventorySearchIndex>();
    InventoryLimitDTO inventoryLimitDTO = new InventoryLimitDTO();
    Set<Long> productIds = new HashSet<Long>();
    for (ReturnOrderItemDTO itemDTO : returnOrderDTO.getItemDTOs()) {
      if (itemDTO.getProductId() != null) {
        productIds.add(itemDTO.getProductId());
      }
    }
    if (!productIds.isEmpty()) {
      inventorySearchIndexMap = getSearchService().getInventorySearchIndexMapByProductIds(returnOrderDTO.getShopId(), productIds.toArray(new Long[productIds.size()]));
    }
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Map<Long, ProductHistoryDTO> productHistoryDTOMap = getProductHistoryService().getOrSaveProductHistoryByLocalInfoId(returnOrderDTO.getShopId(), productIds.toArray(new Long[productIds.size()]));
      ReturnOrder returnOrder = new ReturnOrder();
      returnOrderDTO.setId(null);
      returnOrder.fromDTO(returnOrderDTO);
      returnOrder.setVestDate(System.currentTimeMillis());
      writer.save(returnOrder);
      returnOrderDTO.setId(returnOrder.getId());
      //保存item
      Map<Long,BorrowOrderItem> itemMap=new HashMap<Long, BorrowOrderItem>();
      List<BorrowOrderItem> items=getBorrowOrderItemByOrderId(returnOrderDTO.getShopId(),NumberUtil.longValue(returnOrderDTO.getBorrowOrderId()));
      if(CollectionUtil.isEmpty(items)){
        return result.LogErrorMsg("借调单项目为空！");
      }
      for(BorrowOrderItem item:items){
        itemMap.put(item.getProductId(),item);
      }
      ProductHistoryDTO productHistoryDTO = null;
      BorrowOrderItem borrowOrderItem=null;
      for (ReturnOrderItemDTO itemDTO : returnOrderDTO.getItemDTOs()) {
        if(itemDTO.getProductId() == null || StringUtils.isBlank(itemDTO.getProductName())){
          LOG.error("归还项目信息异常！");
          continue;
        }
        borrowOrderItem=itemMap.get(itemDTO.getProductId());
        if(borrowOrderItem==null){
          LOG.error("归还项目信息异常！");
          continue;
        }
        String bUnit=borrowOrderItem.getUnit();
        String rUnit=itemDTO.getUnit();
        String sellUnit=itemDTO.getSellUnit();
        Long rate= itemDTO.getRate();
        String storageUnit=itemDTO.getStorageUnit();
        double returnAmount=NumberUtil.doubleVal(itemDTO.getReturnAmount());//同一成借时的单位
        double sAmount= NumberUtil.doubleVal(itemDTO.getReturnAmount()); //统一成小单位改库存
        if(StringUtil.isNotEmpty(bUnit)&&StringUtil.isNotEmpty(bUnit)&&StringUtil.isNotEmpty(sellUnit)&&rate!=null
            &&StringUtil.isNotEmpty(storageUnit)&&!sellUnit.equals(storageUnit)){  //存在大小单位转换问题
          if(bUnit.equals(sellUnit)&&rUnit.equals(storageUnit)){      //借小-->还大
            returnAmount=NumberUtil.doubleVal(itemDTO.getReturnAmount())*rate;
          }else if(bUnit.equals(storageUnit)&&rUnit.equals(sellUnit)){  //借大-->还小
            returnAmount=NumberUtil.doubleVal(itemDTO.getReturnAmount())/rate;
          }
          if(rUnit.equals(storageUnit)){   //还大单位，同一成小单位
            sAmount=NumberUtil.doubleVal(itemDTO.getReturnAmount())*rate;
          }
        }
        double returnTotal=NumberUtil.doubleVal(borrowOrderItem.getReturnAmount())+returnAmount;
        if(returnTotal>NumberUtil.doubleVal(borrowOrderItem.getAmount())){
          return result.LogErrorMsg("归还量超过借调总量！");
        }
        borrowOrderItem.setReturnAmount(returnTotal);
        writer.save(borrowOrderItem);
        itemDTO.setId(null);
//        itemDTO.setReturnAmount(returnAmount);
        if (itemDTO.getProductHistoryId() == null) {
          productHistoryDTO = productHistoryDTOMap.get(itemDTO.getProductId());
          itemDTO.setProductHistoryId(productHistoryDTO == null ? null : productHistoryDTO.getId());
        }
        itemDTO.setOrderId(returnOrderDTO.getId());
        ReturnOrderItem item = new ReturnOrderItem();
        item.fromDTO(itemDTO);
        writer.save(item);
        itemDTO.setId(item.getId());
        //处理库存信息
        if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(returnOrderDTO.getShopVersionId())) {
          StoreHouseInventoryDTO storeHouseInventoryDTO = new StoreHouseInventoryDTO(returnOrderDTO.getStorehouseId(), itemDTO.getProductId(), null);
          storeHouseInventoryDTO.setChangeAmount(NumberUtil.doubleVal(sAmount));
          getStoreHouseService().saveOrUpdateStoreHouseInventoryDTO(writer, storeHouseInventoryDTO);
        }

        Inventory inventory = writer.getInventoryByIdAndshopId(itemDTO.getProductId(),returnOrderDTO.getShopId());
        if (inventory != null) {
          getInventoryService().caculateBeforeLimit(inventory.toDTO(), inventoryLimitDTO);
          inventory.setAmount(inventory.getAmount() + NumberUtil.doubleVal(sAmount));
          writer.update(inventory);
          getInventoryService().caculateAfterLimit(inventory.toDTO(), inventoryLimitDTO);
          InventorySearchIndex inventorySearchIndex = inventorySearchIndexMap.get(itemDTO.getProductId());
          if (inventorySearchIndex != null) {
            inventorySearchIndex.setAmount(inventory.getAmount());
          }
        }
      }
      //更新借调单归还状态
      BorrowOrder borrowOrder=getBorrowOrderById(returnOrderDTO.getShopId(),NumberUtil.longValue(returnOrderDTO.getBorrowOrderId()));
      int unReturnSize=0;
      int returnAllSize=0;
      for(BorrowOrderItem item:items){
        if(NumberUtil.doubleVal(item.getReturnAmount())==0D){
          unReturnSize++;
        }else if(NumberUtil.doubleVal(item.getReturnAmount())==NumberUtil.doubleVal(item.getAmount())){
          returnAllSize++;
        }
      }
      if(unReturnSize==items.size()){
        borrowOrder.setReturnStatus(ReturnStatus.RETURN_NONE);
      }else if(returnAllSize==items.size()){
        borrowOrder.setReturnStatus(ReturnStatus.RETURN_ALL);
      }else{
        borrowOrder.setReturnStatus(ReturnStatus.RETURN_PARTLY);
      }
      writer.update(borrowOrder);

      ServiceManager.getService(IProductInStorageService.class).productThroughByOrder(returnOrderDTO,OrderTypes.RETURN_ORDER,returnOrderDTO.getStatus(),writer);

      writer.commit(status);
      //操作记录
      getOperationLogService().saveOperationLog(new OperationLogDTO(returnOrderDTO.getShopId(), returnOrderDTO.getUserId(),
          returnOrderDTO.getId(), ObjectTypes.INNER_RETURN, OperationTypes.RETURN_STORAGE));
      //更新库存上下限
      getInventoryService().updateInventoryLimit(inventoryLimitDTO);
      //更新searchIndex
      if (inventorySearchIndexMap != null && !inventorySearchIndexMap.isEmpty()) {
        getInventoryService().addOrUpdateInventorySearchIndexWithList(returnOrderDTO.getShopId(),
            new ArrayList<InventorySearchIndex>(inventorySearchIndexMap.values()));
      }
      return result;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public Result initAndVerifySaveBorrowOrder(AllListResult result, BorrowOrderDTO borrowOrderDTO) throws Exception {
    Long shopId = borrowOrderDTO.getShopId();
    //去掉空行
    List<Long> productIdList = removeNullProductRow(borrowOrderDTO);
    if (ArrayUtil.isEmpty(borrowOrderDTO.getItemDTOs())) {
      return  result.LogErrorMsg(ValidatorConstant.ORDER_NULL_MSG);
    }
    if(borrowOrderDTO.getItemDTOs().length > productIdList.size()) {
      return result.LogErrorMsg(ValidatorConstant.ORDER_NEW_PRODUCT_ERROR);
    }
    CustomerOrSupplierDTO csDTO=borrowOrderDTO.getCustomerOrSupplierDTO();
    if(csDTO==null||StringUtil.isEmpty(csDTO.getName())){
      return result.LogErrorMsg("借调者信息异常！");
    }
    //校验产品库存
    if (!BcgogoShopLogicResourceUtils.isIgnoreVerifierInventoryResource(borrowOrderDTO.getShopVersionId())) {
      Map<String, String> data = new HashMap<String, String>();
      if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(borrowOrderDTO.getShopVersionId())) {
        //通过仓库校验库存
        if (borrowOrderDTO.getStorehouseId() != null) {
          if (!getInventoryService().checkBatchProductInventoryByStoreHouse(shopId, borrowOrderDTO.getStorehouseId(), borrowOrderDTO.getItemDTOs(), data, productIdList)) {
            return result.LogErrorMsg("对不起,商品库存不足,请重新输入！");
          }
        } else {
          return result.LogErrorMsg(ValidatorConstant.STOREHOUSE_NULL_MSG);
        }
      } else {
        if (!getInventoryService().checkBatchProductInventory(shopId, borrowOrderDTO, data, productIdList)) {
          return result.LogErrorMsg("对不起,商品库存不足,请重新输入！");
        }
      }
    }
    //init
    borrowOrderDTO.getCustomerOrSupplierDTO().setCustomerOrSupplierId(NumberUtil.longValue(borrowOrderDTO.getCustomerOrSupplierDTO().getCustomerOrSupplierIdStr()));
    borrowOrderDTO.setVestDate(DateUtil.convertDateStringToDateLong(DateUtil.STANDARD,borrowOrderDTO.getVestDateStr()));
    return result;
  }

  @Override
  public Result verifyAndInitSaveReturnOrder(AllListResult result,ReturnOrderDTO returnOrderDTO) throws Exception {
    if(ArrayUtil.isEmpty(returnOrderDTO.getItemDTOs())){
      return result.LogErrorMsg("归还项目为空！");
    }
    if(StringUtil.isEmpty(returnOrderDTO.getBorrowOrderId())){
      return result.LogErrorMsg("归还单据信息异常！");
    }
    BorrowOrder borrowOrder=getBorrowOrderById(returnOrderDTO.getShopId(),NumberUtil.longValue(returnOrderDTO.getBorrowOrderId()));
    if(borrowOrder==null){
      return result.LogErrorMsg("借调单不存在,归还失败！");
    }
    if(ReturnStatus.RETURN_ALL.equals(borrowOrder.getReturnStatus())){
      return result.LogErrorMsg("借调单已经全部归还,本次归还失败！！");
    }

    //校验仓库是否存在
    if (BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(returnOrderDTO.getShopVersionId())){
      //通过仓库校验库存
      if (returnOrderDTO.getStorehouseId() != null) {
        StoreHouseDTO storeHouseDTO = getStoreHouseService().getStoreHouseDTOById(returnOrderDTO.getShopId(), returnOrderDTO.getStorehouseId());
        if (storeHouseDTO == null) {
          return result.LogErrorMsg(ValidatorConstant.STOREHOUSE_NULL_MSG);
        } else {
          if (DeletedType.TRUE.equals(storeHouseDTO.getDeleted())) {
            return result.LogErrorMsg(ValidatorConstant.STOREHOUSE_DELETED_MSG);
          }
        }
      } else {
        return result.LogErrorMsg(ValidatorConstant.STOREHOUSE_NULL_MSG);
      }
    }
    return result;
  }


  private List<Long> removeNullProductRow(BorrowOrderDTO borrowOrderDTO) {
    List<Long> productIdList = new ArrayList<Long>();
    List<BorrowOrderItemDTO> borrowOrderItemDTOList = new ArrayList<BorrowOrderItemDTO>();
    BorrowOrderItemDTO[] itemDTOs = borrowOrderDTO.getItemDTOs();
    if (itemDTOs != null && !ArrayUtil.isEmpty(itemDTOs)) {
      for (int i = 0; i < itemDTOs.length; i++) {
        if (itemDTOs[i].getProductId() != null && StringUtils.isNotBlank(itemDTOs[i].getProductName())) {
          borrowOrderItemDTOList.add(itemDTOs[i]);
          productIdList.add(itemDTOs[i].getProductId());
        }
      }
    }
    if (CollectionUtils.isNotEmpty(borrowOrderItemDTOList)) {
      borrowOrderDTO.setItemDTOs(borrowOrderItemDTOList.toArray(new BorrowOrderItemDTO[borrowOrderItemDTOList.size()]));
    } else {
      borrowOrderDTO.setItemDTOs(new BorrowOrderItemDTO[0]);
    }
    return productIdList;
  }

  @Override
  public List<BorrowOrder> getBorrowOrders(BorrowOrderDTO searchCondition) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getBorrowOrders(searchCondition);
  }

  @Override
  public List getBorrowOrderStat(BorrowOrderDTO searchCondition) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getBorrowOrderStat(searchCondition);
  }

  @Override
  public BorrowOrder getBorrowOrderById(Long shopId,Long borrowOrderId) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getBorrowOrderById(shopId,borrowOrderId);
  }

  @Override
  public List<BorrowOrderItem> getBorrowOrderItemByOrderId(Long shopId,Long borrowOrderId) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getBorrowOrderItemByOrderId(shopId,borrowOrderId);
  }

  @Override
  public List<BorrowOrderItemDTO> getBorrowOrderItemDTOByOrderId(Long shopId, Long... borrowOrderId) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<BorrowOrderItem> borrowOrderItemList = writer.getBorrowOrderItemByOrderId(shopId,borrowOrderId);
    List<BorrowOrderItemDTO> borrowOrderItemDTOList = new ArrayList<BorrowOrderItemDTO>();
    if(CollectionUtils.isNotEmpty(borrowOrderItemList)){
      for(BorrowOrderItem borrowOrderItem:borrowOrderItemList){
        borrowOrderItemDTOList.add(borrowOrderItem.toDTO());
      }
    }
    return borrowOrderItemDTOList;
  }

  @Override
  public List<BorrowOrderItem> getBorrowOrderItemByIds(Long shopId,List<Long> itemIdList) {
    if(CollectionUtil.isEmpty(itemIdList)) return null;
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getBorrowOrderItemByIds(shopId,itemIdList);
  }

  @Override
  public List<ReturnOrderDTO> getReturnRunningRecord(Long shopId,Long borrowOrderId) throws Exception {
    List<ReturnOrderDTO> returnOrderDTOs=new ArrayList<ReturnOrderDTO>();
    if(borrowOrderId==null) return returnOrderDTOs;
    TxnWriter writer = txnDaoManager.getWriter();
    List<ReturnOrder> returnOrders = writer.getReturnOrderByBorrowOrderId(shopId,borrowOrderId);
    if(CollectionUtil.isEmpty(returnOrders)){
      return returnOrderDTOs;
    }
    Set<Long> returnOrderIds = new HashSet<Long>();
    for (ReturnOrder order : returnOrders) {
      returnOrderIds.add(order.getId());
    }
    Map<Long, List<ReturnOrderItemDTO>> itemDTOMap = getReturnOrderItemsByOrderIds(shopId,new ArrayList(returnOrderIds));
    if(CollectionUtil.isEmpty(itemDTOMap.keySet())){
      return returnOrderDTOs;
    }
    IProductService productService=ServiceManager.getService(IProductService.class);
    ReturnOrderDTO  orderDTO=null;
    List<ReturnOrderItemDTO> itemDTOs=null;
    for (ReturnOrder returnOrder : returnOrders) {
      orderDTO = returnOrder.toDTO();
      itemDTOs = itemDTOMap.get(returnOrder.getId());
      if(CollectionUtil.isEmpty(itemDTOs)){
        LOG.warn("info is exception!");
        continue;
      }
      for(ReturnOrderItemDTO itemDTO:itemDTOs){
        itemDTO.setProductDTOWithOutUnit(productService.getProductByProductLocalInfoId(itemDTO.getProductId(),shopId));
      }
      orderDTO.setItemDTOs(itemDTOs.toArray(new ReturnOrderItemDTO[itemDTOs.size()]));
      returnOrderDTOs.add(orderDTO);
    }
    return returnOrderDTOs;
  }

  @Override
  public List<ReturnOrderDTO> getReturnOrderDTOsByBorrowOrderIds(Long shopId, Long... borrowOrderId) throws Exception {
    List<ReturnOrderDTO> returnOrderDTOs=new ArrayList<ReturnOrderDTO>();
    TxnWriter writer = txnDaoManager.getWriter();
    List<ReturnOrder> returnOrders = writer.getReturnOrderByBorrowOrderIds(shopId, borrowOrderId);
    if(CollectionUtil.isEmpty(returnOrders)){
      for(ReturnOrder returnOrder : returnOrders){
        returnOrderDTOs.add(returnOrder.toDTO());
      }
    }
    return returnOrderDTOs;
  }

  @Override
  public List<ReturnOrderItemDTO> getReturnOrderItemDTOsByBorrowOrderIds(Long shopId, Long... borrowOrderId) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    List<ReturnOrderItem> items = writer.getReturnOrderItemsByOrderIds(shopId,borrowOrderId);
    List<ReturnOrderItemDTO> returnOrderItemDTOList = new ArrayList<ReturnOrderItemDTO>();
    if(CollectionUtils.isNotEmpty(items)){
      for(ReturnOrderItem returnOrderItem:items){
        returnOrderItemDTOList.add(returnOrderItem.toDTO());
      }
    }
    return returnOrderItemDTOList;
  }

  @Override
  public int countBorrowOrders(BorrowOrderDTO searchCondition) {
    if (searchCondition == null || searchCondition.getShopId() == null) {
      return 0;
    }
    return txnDaoManager.getWriter().countBorrowOrders(searchCondition);
  }

  @Override
  public Map<Long,List<ReturnOrderItemDTO>> getReturnOrderItemsByOrderIds(Long shopId,List<Long> orderIds) {
    Map<Long, List<ReturnOrderItemDTO>> itemDTOMap = new HashMap<Long, List<ReturnOrderItemDTO>>();
    if (CollectionUtil.isEmpty(orderIds)) {
      return itemDTOMap;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    List<ReturnOrderItem> items = writer.getReturnOrderItemsByOrderIds(shopId,orderIds.toArray(new Long[orderIds.size()]));
    if(CollectionUtil.isEmpty(items)){
      return itemDTOMap;
    }
    List<ReturnOrderItemDTO> itemDTOs=null;
    for (ReturnOrderItem item : items) {
      if(item==null) continue;
      itemDTOs = itemDTOMap.get(item.getOrderId());
      if (CollectionUtil.isEmpty(itemDTOs)) {
        itemDTOs = new ArrayList<ReturnOrderItemDTO>();
        itemDTOMap.put(item.getOrderId(), itemDTOs);
      }
      itemDTOs.add(item.toDTO());
    }
    return itemDTOMap;
  }

  public SupplierDTO saveOrUpdateSupplierByCsDTO(Result result,CustomerOrSupplierDTO csDTO) throws Exception {
    ISupplierRecordService supplierRecordService= ServiceManager.getService(ISupplierRecordService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    SupplierDTO supplierDTO = null;
    if(csDTO.getCustomerOrSupplierId()==null){
      supplierDTO = new SupplierDTO();
    }else{
      supplierDTO = userService.getSupplierById(csDTO.getCustomerOrSupplierId());
      if(supplierDTO == null){
        supplierDTO = new SupplierDTO();
      }
    }
    supplierDTO.fromCustomerOrSupplierDTO(csDTO);
    if(supplierDTO.getId() != null){
      userService.updateSupplier(supplierDTO);
    }else{
      userService.createSupplier(supplierDTO);
    }

    if(csDTO.getCustomerOrSupplierId()==null){
      SupplierRecordDTO recordDTO=new SupplierRecordDTO();
      recordDTO.setShopId(supplierDTO.getShopId());
      recordDTO.setSupplierId(supplierDTO.getId());
      recordDTO.setCreditAmount(0d);
      supplierRecordService.saveSupplierRecord(recordDTO);
    }
    csDTO.setCustomerOrSupplierId(supplierDTO.getId());
    return supplierDTO;
  }

  public List<BorrowOrder> getBorrowOrderByBorrower(Long shopId,String borrower){
    return txnDaoManager.getWriter().getBorrowOrderByBorrower(shopId,borrower);
  }

}
