package com.bcgogo.txn.dto;

import com.bcgogo.common.Pair;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.search.dto.CurrentUsedProductDTO;
import com.bcgogo.search.dto.CurrentUsedVehicleDTO;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-4-10
 * Time: 下午6:04
 * To change this template use File | Settings | File Templates.
 */
public abstract class BcgogoOrderDto implements Serializable {
  private Long id;
  private String idStr;
  private Long shopId;
  private String shopIdStr;
  private Long userId;
  private String userName;
  private Long shopVersionId;  //店铺类型

  private Long storehouseId;
  private String storehouseIdStr;
  private String storehouseName; //仓库名称
  private String storageBin;

  //商品出入库打通相关
  private boolean selectSupplier;//商品库存打通 是否选择供应商
  private Map<OrderTypes,Set<Long>> itemIdMap = new HashMap<OrderTypes,Set<Long>>();
  private List<InStorageRecordDTO> inStorageRecordDTOList = new ArrayList<InStorageRecordDTO>();
  private OrderTypes orderTypes;

  //对账单相关
  private Long statementAccountOrderId;   //对账单id
  private Double statementAmount;//对账金额


  private Boolean isMergeInOutRecordFlag = false;

  private List<CurrentUsedProductDTO> currentUsedProductDTOList;
  private List<CurrentUsedVehicleDTO> currentUsedVehicleDTOList;

  private OrderStatus ptOrderStatus; //由于orderStatus，status已经被子类起了名字，为了兼容之前的这里加了pt productThroug

  public BcgogoOrderDto(List<CurrentUsedProductDTO> currentUsedProductDTOList, Long shopId) {
    this.currentUsedProductDTOList = currentUsedProductDTOList;
    this.shopId = shopId;
  }

  public BcgogoOrderDto() {
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
    if (id != null)
      this.idStr = id.toString();
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
    this.shopIdStr= StringUtil.valueOf(shopId);
  }

  public String getShopIdStr() {
    return shopIdStr;
  }

  public void setShopIdStr(String shopIdStr) {
    this.shopIdStr = shopIdStr;
  }

  public List<CurrentUsedProductDTO> getCurrentUsedProductDTOList() {
    return currentUsedProductDTOList;
  }

  public void setCurrentUsedProductDTOList(List<CurrentUsedProductDTO> currentUsedProductDTOList) {
    this.currentUsedProductDTOList = currentUsedProductDTOList;
  }

  public void setCurrentUsedProductDTOList() {
    if (ArrayUtils.isEmpty(getItemDTOs())) {
      return;
    }
    List<CurrentUsedProductDTO> currentUsedProductDTOList = new ArrayList<CurrentUsedProductDTO>();
    for (BcgogoOrderItemDto bcgogoOrderItemDto : getItemDTOs()) {
      if (StringUtils.isBlank(bcgogoOrderItemDto.getProductName())) continue;
      CurrentUsedProductDTO cupDTO = new CurrentUsedProductDTO();
      cupDTO.setShopId(getShopId());
      cupDTO.setTimeOrder(System.currentTimeMillis());
      cupDTO.setProductName(bcgogoOrderItemDto.getProductName());
      cupDTO.setBrand(bcgogoOrderItemDto.getBrand());
      currentUsedProductDTOList.add(cupDTO);
    }
    setCurrentUsedProductDTOList(currentUsedProductDTOList);
  }

  public List<CurrentUsedVehicleDTO> getCurrentUsedVehicleDTOList() {
    return currentUsedVehicleDTOList;
  }

  public void setCurrentUsedVehicleDTOList(List<CurrentUsedVehicleDTO> currentUsedVehicleDTOList) {
    this.currentUsedVehicleDTOList = currentUsedVehicleDTOList;
  }

  public OrderStatus getPtOrderStatus() {
    return ptOrderStatus;
  }

  public void setPtOrderStatus(OrderStatus ptOrderStatus) {
    this.ptOrderStatus = ptOrderStatus;
  }

  public void setCurrentUsedVehicleDTOList() {
    if (ArrayUtils.isEmpty(getItemDTOs())) {
      return;
    }
    List<CurrentUsedVehicleDTO> currentUsedVehicleDTOList = new ArrayList<CurrentUsedVehicleDTO>();
    for (BcgogoOrderItemDto bcgogoOrderItemDto : getItemDTOs()) {
      if (StringUtils.isBlank(bcgogoOrderItemDto.getVehicleBrand())) continue;
      CurrentUsedVehicleDTO cuvDTO = new CurrentUsedVehicleDTO();
      cuvDTO.setShopId(getShopId());
      cuvDTO.setTimeOrder(System.currentTimeMillis());
      cuvDTO.setBrand(bcgogoOrderItemDto.getVehicleBrand());
      currentUsedVehicleDTOList.add(cuvDTO);
    }
    setCurrentUsedVehicleDTOList(currentUsedVehicleDTOList);
  }

  public Map<Long, Pair<Long, Boolean>> generateRecentChangedProductDTOList() {
    if (ArrayUtils.isEmpty(getItemDTOs())) {
      return null;
    }
    Map<Long, Pair<Long, Boolean>> recentChangedProductMap = new HashMap<Long, Pair<Long, Boolean>>();
    for (BcgogoOrderItemDto bcgogoOrderItemDto : getItemDTOs()) {
      if (StringUtils.isBlank(bcgogoOrderItemDto.getProductName())) continue;
      recentChangedProductMap.put(bcgogoOrderItemDto.getProductId(), new Pair<Long, Boolean>());
    }
    return recentChangedProductMap;
  }

  public abstract BcgogoOrderItemDto[] getItemDTOs();

  public abstract Long getVestDate();

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public Long getShopVersionId() {
    return shopVersionId;
  }

  public void setShopVersionId(Long shopVersionId) {
    this.shopVersionId = shopVersionId;
  }

  public Long getStorehouseId() {
    return storehouseId;
  }

  public void setStorehouseId(Long storehouseId) {
    if (storehouseId != null) {
      this.storehouseIdStr = storehouseId.toString();
    }
    this.storehouseId = storehouseId;
  }

  public String getStorehouseIdStr() {
    return storehouseIdStr;
  }

  public void setStorehouseIdStr(String storehouseIdStr) {
    this.storehouseIdStr = storehouseIdStr;
  }

  public String getStorehouseName() {
    return storehouseName;
  }

  public void setStorehouseName(String storehouseName) {
    this.storehouseName = storehouseName;
  }

  public String getStorageBin() {
    return storageBin;
  }

  public void setStorageBin(String storageBin) {
    this.storageBin = storageBin;
  }

  public Long[] getProductIds() {
    List<Long> productIdList = getProductIdList();
    if (CollectionUtils.isNotEmpty(productIdList)) {
      return productIdList.toArray(new Long[productIdList.size()]);
    }
    return null;
  }
  public List<Long> getProductIdList() {
    List<Long> productIdList = new ArrayList<Long>();
    if (!ArrayUtils.isEmpty(this.getItemDTOs())) {
      for (BcgogoOrderItemDto bcgogoOrderItemDto : this.getItemDTOs()) {
        productIdList.add(bcgogoOrderItemDto.getProductId());
      }
    }
    return productIdList;
  }

  public Set<Long> getProductIdSet() {
    Set<Long> productIds = new HashSet<Long>();
    if (!ArrayUtils.isEmpty(this.getItemDTOs())) {
      for (BcgogoOrderItemDto bcgogoOrderItemDto : this.getItemDTOs()) {
        if(bcgogoOrderItemDto.getProductId() != null) {
          productIds.add(bcgogoOrderItemDto.getProductId());
        }
      }
    }
    return productIds;
  }

  public Set<Long> getProductHistoryIds(){
    Set<Long> productHistoryIds = new HashSet<Long>();
    if (!ArrayUtils.isEmpty(this.getItemDTOs())) {
      for (BcgogoOrderItemDto bcgogoOrderItemDto : this.getItemDTOs()) {
        if(bcgogoOrderItemDto.getProductHistoryId() != null) {
          productHistoryIds.add(bcgogoOrderItemDto.getProductHistoryId());
        }
      }
    }
    return productHistoryIds;
  }





  public Long getStatementAccountOrderId() {
    return statementAccountOrderId;
  }

  public void setStatementAccountOrderId(Long statementAccountOrderId) {
    this.statementAccountOrderId = statementAccountOrderId;
  }

  public Double getStatementAmount() {
    return statementAmount;
  }

  public void setStatementAmount(Double statementAmount) {
    this.statementAmount = statementAmount;
  }

  public boolean isSelectSupplier() {
    return selectSupplier;
  }

  public void setSelectSupplier(boolean selectSupplier) {
    this.selectSupplier = selectSupplier;
  }

  public Map<OrderTypes, Set<Long>> getItemIdMap() {
    return itemIdMap;
  }

  public void setItemIdMap(Map<OrderTypes, Set<Long>> itemIdMap) {
    this.itemIdMap = itemIdMap;
  }

  public void addItemId(OrderTypes orderType,Long itemId) {
    if (orderType == null || itemId == null) {
      return;
    }

    Set<Long> itemIds = null;
    if (itemIdMap.containsKey(orderType)) {
      itemIds = itemIdMap.get(orderType);
    } else {
      itemIds = new HashSet<Long>();
    }
    itemIds.add(itemId);

    itemIdMap.put(orderType,itemIds);
  }

  public Boolean getMergeInOutRecordFlag() {
    return isMergeInOutRecordFlag;
  }

  public void setMergeInOutRecordFlag(Boolean mergeInOutRecordFlag) {
    isMergeInOutRecordFlag = mergeInOutRecordFlag;
  }

  public List<InStorageRecordDTO> getInStorageRecordDTOList() {
    return inStorageRecordDTOList;
  }

  public void setInStorageRecordDTOList(List<InStorageRecordDTO> inStorageRecordDTOList) {
    this.inStorageRecordDTOList = inStorageRecordDTOList;
  }

  public OrderTypes getOrderTypes() {
    return orderTypes;
  }

  public void setOrderTypes(OrderTypes orderTypes) {
    this.orderTypes = orderTypes;
  }

  public String getAllProductNames(String joiner){
    if(ArrayUtils.isEmpty(getItemDTOs())){
      return "";
    }
    List<String> productNames = new ArrayList<String>();
    for(BcgogoOrderItemDto itemDTO : getItemDTOs()){
      productNames.add(itemDTO.getProductName());
    }
    return StringUtils.join(productNames, joiner);
  }

  public void initDefaultItemUnit(Map<String,String> productUnitMap){
    if (ArrayUtils.isEmpty(getItemDTOs())) {
      return;
    }
    for (BcgogoOrderItemDto bcgogoOrderItemDto : getItemDTOs()) {
      if (StringUtils.isBlank(bcgogoOrderItemDto.getProductName())) continue;
      if (StringUtils.isNotBlank(bcgogoOrderItemDto.getUnit())) continue;
      bcgogoOrderItemDto.setUnit(productUnitMap.get(bcgogoOrderItemDto.getProductName().trim()));
    }
  }
}
