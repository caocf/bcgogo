package com.bcgogo.txn.dto;

import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.search.dto.OrderIndexDTO;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 12-12-10
 * Time: 下午2:57
 * To change this template use File | Settings | File Templates.
 */
public class RepairPickingDTO extends BcgogoOrderDto {
  private static final int DEFAULT_PAGE_SIZE = 5;
  private static final int DEFAULT_PAGE_INDEX = 1;
  private static final String  DEFAULT_SEARCH_STATUS = OrderStatus.PENDING.toString();
  //db字段
  private Long repairOrderId;
  private String repairOrderReceiptNo;
  private String receiptNo;
  private Long vestDate;  //下单时间，对应采购单的settleDate
  private String productSeller;
  private String pickingMan;//维修领料人



    private OrderStatus status;  //领料单状态，是否作废(待处理，结算，作废)


  //业务字段
  private String repairOrderIdStr;
  private String vestDateStr;
  private String vehicle;
  private String customer;
  private Long customerId;
  private String customerIdStr;
  private RepairPickingItemDTO[] itemDTOs;
  private List<RepairPickingItemDTO> totalItemDTOs;//未合并的所有记录
  private List<RepairPickingItemDTO> pendingItemDTOs;//未合并的待处理
  private List<RepairPickingItemDTO> handledItemDTOs;//合并过后的已处理记录
//  private Map<OperationTypes, Object> operations;//可以进行的操作；
  private Map<Long,List<RepairPickingItemDTO>> handledItemDTOMap;//查看领/退料流水记录
  private OrderStatus repairOrderStatus;
  private boolean isCanOutStorage = false;
  private boolean isCanReturnStorage = false;
  private Long toStorehouseId;      //退料仓库被删除是退料仓库

  //分页条件
  private Integer pageSize = DEFAULT_PAGE_SIZE;
  private Integer pageNo = DEFAULT_PAGE_INDEX;

  //查询条件
  private String startTimeStr;
  private String endTimeStr;
  private Long startTime;
  private Long endTime;
  private String selectStatus ;  //查找单据的状态
  private List<OrderStatus> searchStatus ;

  public void fromRepairDTO(RepairOrderDTO repairOrderDTO) {
    this.setShopId(repairOrderDTO.getShopId());
    this.setRepairOrderId(repairOrderDTO.getId());
    this.setRepairOrderReceiptNo(repairOrderDTO.getReceiptNo());
    this.setVestDate(repairOrderDTO.getSettleDate());
    this.setProductSeller(repairOrderDTO.getProductSaler());
   /* this.setPickingMan(rep);*/
    this.setVehicle(repairOrderDTO.getVechicle());
    this.setCustomer(repairOrderDTO.getCustomerName());
    this.setCustomerId(repairOrderDTO.getCustomerId());
    this.setRepairOrderStatus(repairOrderDTO.getStatus());
    this.setStorehouseId(repairOrderDTO.getStorehouseId());
    this.setStorehouseName(repairOrderDTO.getStorehouseName());
  }

  public RepairPickingDTO() {
    setSelectStatus(DEFAULT_SEARCH_STATUS);
  }

  public RepairPickingItemDTO[] getItemDTOs() {
    return itemDTOs;
  }

  public void setItemDTOs(RepairPickingItemDTO[] itemDTOs) {
    this.itemDTOs = itemDTOs;
  }

  //初始化查询时间
  public void initSearchTime() {
    Long startTime, endTime;
    try {
      startTime = DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, this.getStartTimeStr());
    } catch (Exception e) {
      startTime = null;
      this.setStartTimeStr(null);
    }
    try {
      endTime = DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, this.getEndTimeStr());
    } catch (Exception e) {
      endTime = null;
      this.setEndTimeStr(null);
    }
    if (startTime != null && endTime != null) {
      if (startTime > endTime) {
        Long temp = endTime;
        endTime = startTime;
        startTime = temp;
      }
    }
    if (endTime != null) {
      endTime = DateUtil.getInnerDayTime(endTime, 1);
    }
    this.setStartTime(startTime);
    this.setEndTime(endTime);
  }

  public void initRepairPickingDTO() {
    //组装item
    if (CollectionUtils.isNotEmpty(this.getTotalItemDTOs())) {
      Map<Long, RepairPickingItemDTO> handledItemMap = new HashMap<Long, RepairPickingItemDTO>();
      List<RepairPickingItemDTO> handledItemList = new ArrayList<RepairPickingItemDTO>();
      for (RepairPickingItemDTO repairPickingItemDTO : this.getTotalItemDTOs()) {
        if(repairPickingItemDTO.getStatus() == null){
          continue;
        }
        switch (repairPickingItemDTO.getStatus()) {
          case WAIT_OUT_STORAGE:
            if(NumberUtil.doubleVal(repairPickingItemDTO.getAmount())>NumberUtil.doubleVal(repairPickingItemDTO.getInventoryAmount())){
              repairPickingItemDTO.setIsLack(true);
            }else {
              repairPickingItemDTO.setIsLack(false);
            }
          case WAIT_RETURN_STORAGE:
            if (this.getPendingItemDTOs() == null) {
              this.setPendingItemDTOs(new ArrayList<RepairPickingItemDTO>());
            }
            //设定默认销售人
            if (StringUtils.isNotBlank(this.getProductSeller())) {
              String[] productSellers = this.getProductSeller().split(",");
              for (String str : productSellers) {
                if (StringUtils.isNotBlank(str) && !str.equals("未填写")) {
                  repairPickingItemDTO.setDefaultPickingMan(str);
                  break;
                }
              }
            }
            this.getPendingItemDTOs().add(repairPickingItemDTO);
            break;
          case OUT_STORAGE:
            handledItemList.add(repairPickingItemDTO);
            RepairPickingItemDTO itemDTO = handledItemMap.get(repairPickingItemDTO.getProductId());
            if (itemDTO == null) {
              itemDTO = repairPickingItemDTO.clone();
              itemDTO.setOutStorageAmount(itemDTO.getAmount());
            } else {
              double outAmount = NumberUtil.doubleVal(itemDTO.getOutStorageAmount());
              double itemAmount = NumberUtil.doubleVal(repairPickingItemDTO.getAmount());
              double lastItemAmount = NumberUtil.doubleVal(itemDTO.getAmount());
              itemDTO.setOutStorageAmount(outAmount + itemAmount);
              itemDTO.setPickingMan(StringUtil.joinStrings(itemDTO.getPickingMan(), repairPickingItemDTO.getPickingMan(), ","));
              if (OrderStatus.OUT_STORAGE.equals(itemDTO.getStatus())) {
                itemDTO.setAmount(NumberUtil.doubleVal(itemDTO.getAmount()) + itemAmount);
              } else if (OrderStatus.RETURN_STORAGE.equals(itemDTO.getStatus())) {
                if (lastItemAmount < itemAmount) {
                  itemDTO.setAmount(itemAmount - lastItemAmount);
                  itemDTO.setStatus(OrderStatus.OUT_STORAGE);
                } else {
                  itemDTO.setAmount(lastItemAmount - itemAmount);
                }
              }
            }
            handledItemMap.put(repairPickingItemDTO.getProductId(), itemDTO);
            break;
          case RETURN_STORAGE:
            handledItemList.add(repairPickingItemDTO);
            RepairPickingItemDTO returnItemDTO = handledItemMap.get(repairPickingItemDTO.getProductId());
            if (returnItemDTO == null) {
              returnItemDTO = repairPickingItemDTO.clone();
              returnItemDTO.setOutStorageAmount(returnItemDTO.getAmount());
            } else {
              double returnAmount = NumberUtil.doubleVal(returnItemDTO.getReturnStorageAmount());
              double itemAmount = NumberUtil.doubleVal(repairPickingItemDTO.getAmount());
              double lastItemAmount = NumberUtil.doubleVal(returnItemDTO.getAmount());
              returnItemDTO.setReturnStorageAmount(returnAmount + itemAmount);
              returnItemDTO.setPickingMan(StringUtil.joinStrings(returnItemDTO.getPickingMan(), repairPickingItemDTO.getPickingMan(), ","));
              if (OrderStatus.RETURN_STORAGE.equals(returnItemDTO.getStatus())) {
                returnItemDTO.setAmount(NumberUtil.doubleVal(returnItemDTO.getAmount()) + itemAmount);
              } else if (OrderStatus.OUT_STORAGE.equals(returnItemDTO.getStatus())) {
                if (lastItemAmount < itemAmount) {
                  returnItemDTO.setAmount(itemAmount - lastItemAmount);
                  returnItemDTO.setStatus(OrderStatus.RETURN_STORAGE);
                } else {
                  returnItemDTO.setAmount(lastItemAmount - itemAmount);
                }
              }
            }
            handledItemMap.put(repairPickingItemDTO.getProductId(), returnItemDTO);
            break;
        }
      }
      if(handledItemMap!=null && !handledItemMap.isEmpty()){
        this.setHandledItemDTOs(new ArrayList<RepairPickingItemDTO>(handledItemMap.values()));
      }
      //组装领退料流水
      if (CollectionUtils.isNotEmpty(handledItemList)) {
        Long defaultTime = System.currentTimeMillis();
        for(RepairPickingItemDTO repairPickingItemDTO :handledItemList){
          String handledTimeStr = DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DEFAULT,repairPickingItemDTO.getOperationDate()) ;
          Long handledTime = null;
          try{
            handledTime = DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DEFAULT,handledTimeStr);
          }catch (Exception e ){
            handledTime = null;
          }
          if(handledTime == null){
            handledTime = defaultTime;
          }
          if(this.getHandledItemDTOMap()==null){
            this.setHandledItemDTOMap(new TreeMap<Long, List<RepairPickingItemDTO>>());
          }
          List<RepairPickingItemDTO> repairPickingItemDTOs = this.getHandledItemDTOMap().get(handledTime);
          if(repairPickingItemDTOs == null){
            repairPickingItemDTOs = new ArrayList<RepairPickingItemDTO>();
          }
          repairPickingItemDTOs.add(repairPickingItemDTO);
          this.getHandledItemDTOMap().put(handledTime, repairPickingItemDTOs);
        }
      }
    }
     //设定默认领料人 第一个之前领料人>销售人
     if(CollectionUtils.isNotEmpty(this.getPendingItemDTOs())){
        String defaultPickingMan = "";
        if(CollectionUtils.isNotEmpty(this.getHandledItemDTOs())){
           for(RepairPickingItemDTO repairPickingItemDTO : this.getHandledItemDTOs()){
             if(StringUtil.isNotEmpty(repairPickingItemDTO.getPickingMan())){
               String[] defaultPickingMans = repairPickingItemDTO.getPickingMan().split(",");
               for (String str : defaultPickingMans) {
                 if (StringUtils.isNotBlank(str) && !str.equals("未填写")) {
                   defaultPickingMan = str;
                   break;
                 }
               }
               if(StringUtils.isNotBlank(defaultPickingMan)){
                 break;
               }
             }
           }
        }
       if (StringUtils.isBlank(defaultPickingMan) && StringUtils.isNotBlank(this.getProductSeller())) {
         String[] productSellers = this.getProductSeller().split(",");
         for (String str : productSellers) {
           if (StringUtils.isNotBlank(str) && !str.equals("未填写")) {
             defaultPickingMan = str;
             break;
           }
         }
       }
       for (RepairPickingItemDTO repairPickingItemDTO : this.getPendingItemDTOs()) {
         repairPickingItemDTO.setDefaultPickingMan(defaultPickingMan);
       }
     }
    //组装按钮显示状态
    if (CollectionUtils.isNotEmpty(this.getPendingItemDTOs())) {
      for (RepairPickingItemDTO repairPickingItemDTO : this.getPendingItemDTOs()) {
        if (OrderStatus.WAIT_OUT_STORAGE.equals(repairPickingItemDTO.getStatus())) {
          this.setIsCanOutStorage(true);
          break;
        }
      }
      for (RepairPickingItemDTO repairPickingItemDTO : this.getPendingItemDTOs()) {
        if (OrderStatus.WAIT_RETURN_STORAGE.equals(repairPickingItemDTO.getStatus())) {
          this.setIsCanReturnStorage(true);
          break;
        }
      }
    }

  }

  public void setOperationMan(String userName,Long userId) {
    if(CollectionUtils.isNotEmpty(this.getPendingItemDTOs())){
      for(RepairPickingItemDTO repairPickingItemDTO : this.getPendingItemDTOs()){
        if(repairPickingItemDTO.getId() == null){
          continue;
        }
          repairPickingItemDTO.setOperationMan(userName);
          repairPickingItemDTO.setOperationManId(userId);
      }
    }
  }

  public Long getRepairOrderId() {
    return repairOrderId;
  }

  public void setRepairOrderId(Long repairOrderId) {
    this.repairOrderId = repairOrderId;
  }

  public String getRepairOrderIdStr() {
    return repairOrderIdStr;
  }

  public void setRepairOrderIdStr(String repairOrderIdStr) {
    this.repairOrderIdStr = repairOrderIdStr;
  }

  public String getRepairOrderReceiptNo() {
    return repairOrderReceiptNo;
  }

  public void setRepairOrderReceiptNo(String repairOrderReceiptNo) {
    this.repairOrderReceiptNo = repairOrderReceiptNo;
  }

  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  public Long getVestDate() {
    return vestDate;
  }

  public void setVestDate(Long vestDate) {
    this.vestDate = vestDate;
    this.setVestDateStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DEFAULT, vestDate));
  }

  public String getVestDateStr() {
    return vestDateStr;
  }

  public void setVestDateStr(String vestDateStr) {
    this.vestDateStr = vestDateStr;
  }

  public String getProductSeller() {
    return productSeller;
  }

  public void setProductSeller(String productSeller) {
    this.productSeller = productSeller;
  }
  public String getPickingMan() {
    return pickingMan;
  }

  public void setPickingMan(String pickingMan) {
    this.pickingMan = pickingMan;
  }
  public String getVehicle() {
    return vehicle;
  }

  public void setVehicle(String vehicle) {
    this.vehicle = vehicle;
  }

  public String getCustomer() {
    return customer;
  }

  public void setCustomer(String customer) {
    this.customer = customer;
  }

//  public RepairPickingItemDTO[] getItemDTOs() {
//    return itemDTOs;
//  }
//
//  public void setItemDTOs(RepairPickingItemDTO[] itemDTOs) {
//    this.itemDTOs = itemDTOs;
//  }
//
//  public Map<OperationTypes, Object> getOperations() {
//    return operations;
//  }
//
//  public void setOperations(Map<OperationTypes, Object> operations) {
//    this.operations = operations;
//  }

  public OrderStatus getRepairOrderStatus() {
    return repairOrderStatus;
  }

  public void setRepairOrderStatus(OrderStatus repairOrderStatus) {
    this.repairOrderStatus = repairOrderStatus;
  }

  public OrderStatus getStatus() {
    return status;
  }

  public void setStatus(OrderStatus status) {
    this.status = status;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  public String getCustomerIdStr() {
    return customerIdStr;
  }

  public void setCustomerIdStr(String customerIdStr) {
    this.customerIdStr = customerIdStr;
  }

  public Integer getPageSize() {
    return pageSize;
  }

  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }

  public Integer getPageNo() {
    return pageNo;
  }

  public void setPageNo(Integer pageNo) {
    this.pageNo = pageNo;
  }

  public String getStartTimeStr() {
    return startTimeStr;
  }

  public void setStartTimeStr(String startTimeStr) {
    this.startTimeStr = startTimeStr;
  }

  public String getEndTimeStr() {
    return endTimeStr;
  }

  public void setEndTimeStr(String endTimeStr) {
    this.endTimeStr = endTimeStr;
  }

  public Long getStartTime() {
    return startTime;
  }

  public void setStartTime(Long startTime) {
    this.startTime = startTime;
  }

  public Long getEndTime() {
    return endTime;
  }

  public void setEndTime(Long endTime) {
    this.endTime = endTime;
  }

  public String getSelectStatus() {
    return selectStatus;
  }

  public void setSelectStatus(String selectStatus) {
    this.selectStatus = selectStatus;
    List<OrderStatus> statusList = new ArrayList<OrderStatus>();
    if (StringUtils.isNotBlank(selectStatus)) {
      if ("PENDING".equals(selectStatus)) {
        statusList.add(OrderStatus.WAIT_OUT_STORAGE);
        statusList.add(OrderStatus.WAIT_RETURN_STORAGE);
      } else if ("ALL".equals(selectStatus)) {
        statusList.add(OrderStatus.WAIT_OUT_STORAGE);
        statusList.add(OrderStatus.WAIT_RETURN_STORAGE);
        statusList.add(OrderStatus.OUT_STORAGE);
        statusList.add(OrderStatus.RETURN_STORAGE);
      }
    }
    this.setSearchStatus(statusList);
  }

  public List<OrderStatus> getSearchStatus() {
    return searchStatus;
  }

  public void setSearchStatus(List<OrderStatus> searchStatus) {
    this.searchStatus = searchStatus;
  }

  public List<RepairPickingItemDTO> getTotalItemDTOs() {
    return totalItemDTOs;
  }

  public void setTotalItemDTOs(List<RepairPickingItemDTO> totalItemDTOs) {
    this.totalItemDTOs = totalItemDTOs;
  }

  public List<RepairPickingItemDTO> getPendingItemDTOs() {
    return pendingItemDTOs;
  }

  public void setPendingItemDTOs(List<RepairPickingItemDTO> pendingItemDTOs) {
    this.pendingItemDTOs = pendingItemDTOs;
  }

  public List<RepairPickingItemDTO> getHandledItemDTOs() {
    return handledItemDTOs;
  }

  public void setHandledItemDTOs(List<RepairPickingItemDTO> handledItemDTOs) {
    this.handledItemDTOs = handledItemDTOs;
  }

  public boolean getIsCanOutStorage() {
    return isCanOutStorage;
  }

  public void setIsCanOutStorage(boolean canOutStorage) {
    isCanOutStorage = canOutStorage;
  }

  public boolean getIsCanReturnStorage() {
    return isCanReturnStorage;
  }

  public void setIsCanReturnStorage(boolean canReturnStorage) {
    isCanReturnStorage = canReturnStorage;
  }

  public Map<Long, List<RepairPickingItemDTO>> getHandledItemDTOMap() {
    return handledItemDTOMap;
  }

  public void setHandledItemDTOMap(Map<Long, List<RepairPickingItemDTO>> handledItemDTOMap) {
    this.handledItemDTOMap = handledItemDTOMap;
  }

  public Long getToStorehouseId() {
    return toStorehouseId;
  }

  public void setToStorehouseId(Long toStorehouseId) {
    this.toStorehouseId = toStorehouseId;
  }

  public OrderIndexDTO toOrderIndexDTO() {
    OrderIndexDTO orderIndexDTO = new OrderIndexDTO();
    orderIndexDTO.setShopId(this.getShopId());
    orderIndexDTO.setOrderId(this.getId());
    orderIndexDTO.setOrderType(OrderTypes.REPAIR_PICKING);
    orderIndexDTO.setCreationDate(this.getVestDate());
    orderIndexDTO.setOrderStatus(OrderStatus.SETTLED);
    orderIndexDTO.setVestDate(this.getVestDate());
    orderIndexDTO.setReceiptNo(this.getReceiptNo());
    orderIndexDTO.setStorehouseId(this.getStorehouseId());
    orderIndexDTO.setStorehouseName(this.getStorehouseName());

    List<ItemIndexDTO> inOutRecordDTOList = new ArrayList<ItemIndexDTO>();
    if (CollectionUtils.isNotEmpty(this.getHandledItemDTOs())) {
      for (RepairPickingItemDTO repairPickingItemDTO : this.getHandledItemDTOs()) {
        if (repairPickingItemDTO == null || NumberUtil.compareDouble(0d,repairPickingItemDTO.getAmount())) continue;
        //添加每个单据的产品信息
        inOutRecordDTOList.add(repairPickingItemDTO.toInOutRecordDTO(this));
      }
      orderIndexDTO.setInOutRecordDTOList(inOutRecordDTOList);
    }

    return orderIndexDTO;
  }

  @Override
  public RepairPickingDTO clone() throws CloneNotSupportedException {
    RepairPickingDTO repairPickingDTO = new RepairPickingDTO();
    repairPickingDTO.setId(this.getId());
    repairPickingDTO.setShopId(this.getShopId());
    repairPickingDTO.setRepairOrderId(this.getRepairOrderId());
    repairPickingDTO.setRepairOrderReceiptNo(this.getRepairOrderReceiptNo());
    repairPickingDTO.setReceiptNo(this.getReceiptNo());
    repairPickingDTO.setVestDate(this.getVestDate());
    repairPickingDTO.setProductSeller(this.getProductSeller());
    repairPickingDTO.setStatus(this.getStatus());
    repairPickingDTO.setStorehouseId(this.getStorehouseId());
    repairPickingDTO.setStorehouseName(this.getStorehouseName());
    return repairPickingDTO;
  }
}
