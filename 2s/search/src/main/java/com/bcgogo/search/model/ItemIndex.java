package com.bcgogo.search.model;

import com.bcgogo.enums.ItemTypes;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.ServiceLimitTypes;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.txn.dto.RepairOrderDTO;
import com.bcgogo.txn.dto.RepairOrderItemDTO;
import com.bcgogo.txn.dto.RepairOrderOtherIncomeItemDTO;
import com.bcgogo.txn.dto.RepairOrderServiceDTO;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: caiweili
 * Date: 12/31/11
 * Time: 12:07 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "item_index")
public class ItemIndex extends LongIdentifier {
  Logger logger = LoggerFactory.getLogger(ItemIndex.class);

  public ItemIndex() {
  }

  public void setRepairOrderService(RepairOrderDTO repairOrderDTO, RepairOrderServiceDTO repairOrderServiceDTO) {
    this.setOrderTypeEnum(OrderTypes.REPAIR);
    this.setItemTypeEnum(ItemTypes.SERVICE);
    if (repairOrderDTO != null) {
      this.setCustomerId(repairOrderDTO.getCustomerId());
      this.setShopId(repairOrderDTO.getShopId());
      this.setVehicle(repairOrderDTO.getLicenceNo());
      this.setOrderId(repairOrderDTO.getId());
      this.setOrderTimeCreated(repairOrderDTO.getStartDate());
      this.setCustomerOrSupplierName(repairOrderDTO.getCustomerName());
      this.setOrderStatusEnum(repairOrderDTO.getStatus());
      this.setArrears(repairOrderDTO.getDebt());
      this.setVehicleBrand(repairOrderDTO.getBrand());
      this.setVehicleModel(repairOrderDTO.getModel());
      this.setVehicleYear(repairOrderDTO.getYear());
      this.setVehicleEngine(repairOrderDTO.getEngine());
      this.setOrderTotalAmount(repairOrderDTO.getTotal());
      Long payTime = null;
      try {
        payTime = DateUtil.convertDateStringToDateLong("yyyy-MM-dd", repairOrderDTO.getHuankuanTime());
      } catch (Exception e) {
      }
      this.setPaymentTime(payTime);
    }
    if (repairOrderServiceDTO != null) {
      this.setServices(repairOrderServiceDTO.getService());
      this.setServiceId(repairOrderServiceDTO.getServiceId());
      this.setItemName(repairOrderServiceDTO.getService() != null ? repairOrderServiceDTO.getService() : "");
      this.setItemMemo(repairOrderServiceDTO.getMemo());
      this.setItemId(repairOrderServiceDTO.getId());
      this.setItemPrice(repairOrderServiceDTO.getTotal());
      this.setBusinessCategoryId(repairOrderServiceDTO.getBusinessCategoryId());
      this.setBusinessCategoryName(repairOrderServiceDTO.getBusinessCategoryName());
    }
  }

  public void setRepairOrderItem(RepairOrderDTO repairOrderDTO,RepairOrderItemDTO repairOrderItemDTO){
    this.setOrderTypeEnum(OrderTypes.REPAIR);
    this.setItemTypeEnum(ItemTypes.MATERIAL);
    if (repairOrderDTO != null) {
      this.setCustomerId(repairOrderDTO.getCustomerId());
      this.setShopId(repairOrderDTO.getShopId());
      this.setVehicle(repairOrderDTO.getLicenceNo());
      this.setOrderId(repairOrderDTO.getId());
      Long startDateLong = null;
      try {
        startDateLong = DateUtil.convertDateStringToDateLong("yyyy-MM-dd", repairOrderDTO.getStartDateStr());
      } catch (Exception e) {
        startDateLong = System.currentTimeMillis();
      }
      this.setOrderTimeCreated(startDateLong);

      this.setCustomerOrSupplierName(repairOrderDTO.getCustomerName());
      this.setOrderStatusEnum(repairOrderDTO.getStatus());
      Long payTime = null;
      try {
        payTime = DateUtil.convertDateStringToDateLong("yyyy-MM-dd", repairOrderDTO.getHuankuanTime());
      } catch (Exception e) {
      }
      this.setPaymentTime(payTime);
    }

    if (repairOrderItemDTO != null) {
      this.setCommodityCode(repairOrderItemDTO.getCommodityCode());
      this.setItemName(repairOrderItemDTO.getProductName());
      this.setItemMemo(repairOrderItemDTO.getMemo());
      this.setItemBrand(repairOrderItemDTO.getBrand());
      this.setItemModel(repairOrderItemDTO.getModel());
      this.setItemSpec(repairOrderItemDTO.getSpec());
      this.setItemId(repairOrderItemDTO.getId());
      this.setItemPrice(repairOrderItemDTO.getPrice());
      this.setItemCostPrice(repairOrderItemDTO.getCostPrice());
      this.setTotalCostPrice(repairOrderItemDTO.getTotalCostPrice());
      this.setItemCount(repairOrderItemDTO.getAmount());
      this.setUnit(repairOrderItemDTO.getUnit());
      this.setProductId(repairOrderItemDTO.getProductId());
      this.setOrderTotalAmount(repairOrderDTO.getTotal());
      this.setArrears(repairOrderDTO.getDebt());
      this.setVehicleBrand(repairOrderItemDTO.getVehicleBrand());
      this.setVehicleModel(repairOrderItemDTO.getVehicleModel());
      this.setVehicleYear(repairOrderItemDTO.getVehicleYear());
      this.setVehicleEngine(repairOrderItemDTO.getVehicleEngine());
      this.setBusinessCategoryId(repairOrderItemDTO.getBusinessCategoryId());
      this.setBusinessCategoryName(repairOrderItemDTO.getBusinessCategoryName());
    }
  }

  public void setRepairOrderOtherIncome(RepairOrderDTO repairOrderDTO, RepairOrderOtherIncomeItemDTO itemDTO) throws Exception {
    this.setOrderTypeEnum(OrderTypes.REPAIR);
    this.setItemTypeEnum(ItemTypes.OTHER_INCOME);
    if (repairOrderDTO != null) {
      this.setCustomerId(repairOrderDTO.getCustomerId());
      this.setShopId(repairOrderDTO.getShopId());
      this.setVehicle(repairOrderDTO.getLicenceNo());
      this.setOrderId(repairOrderDTO.getId());
      this.setOrderTimeCreated(repairOrderDTO.getStartDate());

      this.setCustomerOrSupplierName(repairOrderDTO.getCustomerName());
      this.setOrderStatusEnum(repairOrderDTO.getStatus());
      this.setOrderTotalAmount(repairOrderDTO.getTotal());
      this.setArrears(repairOrderDTO.getDebt());
      this.setVehicleBrand(repairOrderDTO.getBrand());
      this.setVehicleModel(repairOrderDTO.getModel());
      this.setVehicleYear(repairOrderDTO.getYear());
      this.setVehicleEngine(repairOrderDTO.getEngine());
      String paymentTimeStr = repairOrderDTO.getHuankuanTime();
      Long paymentTime = StringUtils.isNotBlank(paymentTimeStr) ?
          new Long(DateUtil.convertDateStringToDateLong("yyyy-MM-dd", paymentTimeStr)) : null;
      this.setPaymentTime(paymentTime);
    }
    if (itemDTO != null) {
      this.setItemName(itemDTO.getName());
      this.setItemMemo(itemDTO.getMemo());
      this.setItemId(itemDTO.getId());
      this.setItemPrice(itemDTO.getPrice());
      this.setItemCostPrice(0D);
      this.setItemCount(1d);
    }
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "customer_id")
  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  @Column(name = "vehicle", length = 20)
  public String getVehicle() {
    return vehicle;
  }

  public void setVehicle(String vehicle) {
    this.vehicle = vehicle;
  }

  @Column(name = "order_id")
  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  @Column(name = "order_type", length = 20)
  public String getOrderType() {
    return orderType;
  }

  public void setOrderType(String orderType) {
    this.orderType = orderType;
  }

  @Column(name = "order_type_enum")
  @Enumerated(EnumType.STRING)
  public OrderTypes getOrderTypeEnum() {
    return orderTypeEnum;
  }

  public void setOrderTypeEnum(OrderTypes orderTypeEnum) {
    this.orderTypeEnum = orderTypeEnum;
  }

  @Column(name = "item_id")
  public Long getItemId() {
    return itemId;
  }

  public void setItemId(Long itemId) {
    this.itemId = itemId;
  }

  @Column(name = "item_type", length = 20)
  public String getItemType() {
    return itemType;
  }

  public void setItemType(String itemType) {
    this.itemType = itemType;
  }

  @Column(name="item_type_enum")
  @Enumerated(EnumType.STRING)
  public ItemTypes getItemTypeEnum() {
    return itemTypeEnum;
  }

  public void setItemTypeEnum(ItemTypes itemTypeEnum) {
    this.itemTypeEnum = itemTypeEnum;
  }

  @Column(name = "item_name", length = 100)
  public String getItemName() {
    return itemName;
  }

  public void setItemName(String itemName) {
    this.itemName = itemName;
  }

  @Column(name = "order_status", length = 20)
  public String getOrderStatus() {
    return orderStatus;
  }

  public void setOrderStatus(String orderStatus) {
    this.orderStatus = orderStatus;
  }

  @Column(name = "order_status_enum")
  @Enumerated(EnumType.STRING)
  public OrderStatus getOrderStatusEnum() {
    return orderStatusEnum;
  }

  public void setOrderStatusEnum(OrderStatus orderStatusEnum) {
    this.orderStatusEnum = orderStatusEnum;
  }

  @Column(name = "order_time_created")
  public Long getOrderTimeCreated() {
    return orderTimeCreated;
  }

  public void setOrderTimeCreated(Long orderTimeCreated) {
    this.orderTimeCreated = orderTimeCreated;
  }

  //zhouxiaochen 2012-1-6
  @Column(name = "customer_or_supplier_name", length = 100)
  public String getCustomerOrSupplierName() {
    return customerOrSupplierName;
  }

  public void setCustomerOrSupplierName(String customerOrSupplierName) {
    this.customerOrSupplierName = customerOrSupplierName;
  }

  @Column(name = "item_brand", length = 100)
  public String getItemBrand() {
    return itemBrand;
  }

  public void setItemBrand(String itemBrand) {
    this.itemBrand = itemBrand;
  }

  @Column(name = "item_spec", length = 100)
  public String getItemSpec() {
    return itemSpec;
  }

  public void setItemSpec(String itemSpec) {
    this.itemSpec = itemSpec;
  }

  @Column(name = "item_model", length = 100)
  public String getItemModel() {
    return itemModel;
  }

  public void setItemModel(String itemModel) {
    this.itemModel = itemModel;
  }

  @Column(name = "item_count")
  public Double getItemCount() {
    return itemCount;
  }

  public void setItemCount(Double itemCount) {
    this.itemCount = itemCount;
  }
  //end

  @Column(name = "item_price")
  public Double getItemPrice() {
    return itemPrice;
  }

  public void setItemPrice(Double itemPrice) {
    this.itemPrice = itemPrice;
  }

  @Column(name = "services", length = 50)
  public String getServices() {
    return services;
  }

  public void setServices(String services) {
    this.services = services;
  }

  @Column(name = "order_total_amount")
  public Double getOrderTotalAmount() {
    return orderTotalAmount;
  }

  public void setOrderTotalAmount(Double orderTotalAmount) {
    this.orderTotalAmount = orderTotalAmount;
  }

  @Column(name = "arrears")
  public Double getArrears() {
    return arrears;
  }

  public void setArrears(Double arrears) {
    this.arrears = arrears;
  }

  @Column(name = "payment_time")
  public Long getPaymentTime() {
    return paymentTime;
  }

  public void setPaymentTime(Long paymentTime) {
    this.paymentTime = paymentTime;
  }

  @Column(name = "vehicle_brand", length = 20)
  public String getVehicleBrand() {
    return vehicleBrand;
  }

  public void setVehicleBrand(String vehicleBrand) {
    this.vehicleBrand = vehicleBrand;
  }

  @Column(name = "vehicle_model", length = 20)
  public String getVehicleModel() {
    return vehicleModel;
  }

  public void setVehicleModel(String vehicleModel) {
    this.vehicleModel = vehicleModel;
  }

  @Column(name = "vehicle_year", length = 20)
  public String getVehicleYear() {
    return vehicleYear;
  }

  public void setVehicleYear(String vehicleYear) {
    this.vehicleYear = vehicleYear;
  }

  @Column(name = "vehicle_engine", length = 20)
  public String getVehicleEngine() {
    return vehicleEngine;
  }

  public void setVehicleEngine(String vehicleEngine) {
    this.vehicleEngine = vehicleEngine;
  }

  @Column(name = "item_cost_price")
  public Double getItemCostPrice() {
    return itemCostPrice;
  }

  @Column(name = "item_memo", length = 200)
  public String getItemMemo() {
    return itemMemo;
  }
  public void setItemCostPrice(Double itemCostPrice) {
    this.itemCostPrice = itemCostPrice;
  }

  public void setItemMemo(String itemMemo) {
    this.itemMemo = itemMemo;
  }

  @Column(name = "unit", length = 20)
  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  @Column(name = "customer_card_id")
  public Long getCustomerCardId() {
    return customerCardId;
  }

  public void setCustomerCardId(Long customerCardId) {
    this.customerCardId = customerCardId;
  }

  private Long shopId;         //店面ID
  private Long customerId;    // 客户或供应商ID
  private String vehicle;      // 不需要了
  private Long orderId;        //单子ID
  private String orderType;    // 单子类型
  private OrderTypes orderTypeEnum;
  private Long itemId;          //Item ID
  private String itemType;     //Item 类型，页面暂未用到 ：10为购会员卡
  private ItemTypes itemTypeEnum;
  private String itemName;      //品名
  private String orderStatus;     //  状态
  private OrderStatus orderStatusEnum;
  private Long orderTimeCreated; // 时间
  private String customerOrSupplierName;   // 客户或供应商名字
  private String itemBrand;        //item    品牌
  private String itemSpec;         //item    规格
  private String itemModel;        //item    型号‘
  //itemCount 改为Double类型
  private Double itemCount;        //item    数量
  private String itemMemo;        //item    备注
  //end
  private Double itemPrice;        //item    价格
  private String services;          //施工内容
  private Double orderTotalAmount; //单据金额总计
  private Double arrears; //欠款；
  private Long paymentTime;//还款时间

  private String vehicleBrand;
  private String vehicleModel;
  private String vehicleYear;
  private String vehicleEngine;
  private String unit;
  private Double itemCostPrice;
  private Long customerCardId;

  private Double totalCostPrice;
  private Long memberCardId;
  private Long serviceId;
  private Integer increasedTimes;
  private String vehicles;
  private Long deadline;
  private Integer cardTimes;
  private Integer balanceTimes;
  private Integer oldTimes;
  private Long productId;
  private ServiceLimitTypes increasedTimesLimitType;
  private ServiceLimitTypes cardTimesLimitType;
  private ServiceLimitTypes oldTimesLimitType;
  private ServiceLimitTypes balanceTimesLimitType;

		private String commodityCode;//商品编码

  private Long businessCategoryId;
  private String businessCategoryName;
  private Double afterMemberDiscountOrderTotal;

  private String productKind;
  @Column(name = "product_id")
  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }



  @Column(name = "total_cost_price")
  public Double getTotalCostPrice() {
    return totalCostPrice;
  }

  public void setTotalCostPrice(Double totalCostPrice) {
    this.totalCostPrice = totalCostPrice;
  }

  @Column(name="member_card_id")
  public Long getMemberCardId() {
    return memberCardId;
  }

  public void setMemberCardId(Long memberCardId) {
    this.memberCardId = memberCardId;
  }

  @Column(name="service_id")
  public Long getServiceId() {
    return serviceId;
  }

  @Column(name="increased_times")
  public Integer getIncreasedTimes() {
    return increasedTimes;
  }

  @Column(name="vehicles")
  public String getVehicles() {
    return vehicles;
  }
  @Column(name="deadline")
  public Long getDeadline() {
    return deadline;
  }
  @Column(name="card_times")
  public Integer getCardTimes() {
    return cardTimes;
  }
  @Column(name="balance_times")
  public Integer getBalanceTimes() {
    return balanceTimes;
  }
  @Column(name="old_times")
  public Integer getOldTimes() {
    return oldTimes;
  }

  @Enumerated(EnumType.STRING)
  @Column(name="increased_times_limit_type")
  public ServiceLimitTypes getIncreasedTimesLimitType() {
    return increasedTimesLimitType;
  }

  @Enumerated(EnumType.STRING)
  @Column(name="card_times_limit_type")
  public ServiceLimitTypes getCardTimesLimitType() {
    return cardTimesLimitType;
  }

  @Enumerated(EnumType.STRING)
  @Column(name="old_times_limit_type")
  public ServiceLimitTypes getOldTimesLimitType() {
    return oldTimesLimitType;
  }

  @Enumerated(EnumType.STRING)
  @Column(name="balance_times_limit_type")
  public ServiceLimitTypes getBalanceTimesLimitType() {
    return balanceTimesLimitType;
  }

	@Column(name = "commodity_code", length = 50)
	public String getCommodityCode() {
		return commodityCode;
	}

	public void setCommodityCode(String commodityCode) {
		if(StringUtils.isNotBlank(commodityCode)){
			this.commodityCode = commodityCode.trim().toUpperCase();
		}else {
			this.commodityCode = null;
		}
	}

  public void setServiceId(Long serviceId) {
    this.serviceId = serviceId;
  }

  public void setIncreasedTimes(Integer increasedTimes) {
    this.increasedTimes = increasedTimes;
  }

  public void setVehicles(String vehicles) {
    this.vehicles = vehicles;
  }

  public void setDeadline(Long deadline) {
    this.deadline = deadline;
  }

  public void setBalanceTimes(Integer balanceTimes) {
    this.balanceTimes = balanceTimes;
  }

  public void setCardTimes(Integer cardTimes) {
    this.cardTimes = cardTimes;
  }

  public void setOldTimes(Integer oldTimes) {
    this.oldTimes = oldTimes;
  }

  public void setIncreasedTimesLimitType(ServiceLimitTypes increasedTimesLimitType) {
    this.increasedTimesLimitType = increasedTimesLimitType;
  }

  public void setCardTimesLimitType(ServiceLimitTypes cardTimesLimitType) {
    this.cardTimesLimitType = cardTimesLimitType;
  }

  public void setOldTimesLimitType(ServiceLimitTypes oldTimesLimitType) {
    this.oldTimesLimitType = oldTimesLimitType;
  }

  public void setBalanceTimesLimitType(ServiceLimitTypes balanceTimesLimitType) {
    this.balanceTimesLimitType = balanceTimesLimitType;
  }

  @Column(name="business_category_id")
  public Long getBusinessCategoryId() {
    return businessCategoryId;
  }

  public void setBusinessCategoryId(Long businessCategoryId) {
    this.businessCategoryId = businessCategoryId;
  }

  @Column(name="business_category_name")
  public String getBusinessCategoryName() {
    return businessCategoryName;
  }

  public void setBusinessCategoryName(String businessCategoryName) {
    this.businessCategoryName = businessCategoryName;
  }

  @Column(name="product_kind")
  public String getProductKind() {
    return productKind;
  }

  public void setProductKind(String productKind) {
    this.productKind = productKind;
  }

  @Column(name="after_member_discount_order_total")
  public Double getAfterMemberDiscountOrderTotal() {
    return afterMemberDiscountOrderTotal;
  }

  public void setAfterMemberDiscountOrderTotal(Double afterMemberDiscountOrderTotal) {
    this.afterMemberDiscountOrderTotal = afterMemberDiscountOrderTotal;
  }

  public ItemIndexDTO toDTO() {
    ItemIndexDTO itemIndexDTO = new ItemIndexDTO();
//          this.getLastModified()
    itemIndexDTO.setId(this.getId());
    //增加单位转化 add by liuWei
    itemIndexDTO.setUnit(this.getUnit());
    itemIndexDTO.setShopId(this.getShopId());
    itemIndexDTO.setCustomerId(this.getCustomerId());
    itemIndexDTO.setVehicle(this.getVehicle());
    itemIndexDTO.setOrderId(this.getOrderId());
    itemIndexDTO.setOrderType(getOrderTypeEnum());
    itemIndexDTO.setOrderTypeStr(getOrderTypeEnum()==null?"":getOrderTypeEnum().getName());

    itemIndexDTO.setItemId(this.getItemId());
    itemIndexDTO.setItemType(getItemTypeEnum());
    itemIndexDTO.setItemName(this.getItemName());
    itemIndexDTO.setOrderStatusStr(getOrderStatusEnum()==null?"":getOrderStatusEnum().getName());
    itemIndexDTO.setOrderStatus(getOrderStatusEnum());
    itemIndexDTO.setOrderTimeCreated(this.getOrderTimeCreated());
    itemIndexDTO.setItemCostPrice(NumberUtil.doubleVal(this.itemCostPrice));
    itemIndexDTO.setTotalCostPrice(NumberUtil.doubleVal(this.totalCostPrice));
    itemIndexDTO.setOrderTimeCreatedStr(DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY, getOrderTimeCreated()));

    itemIndexDTO.setCustomerOrSupplierName(this.getCustomerOrSupplierName());
    itemIndexDTO.setProductId(this.getProductId());
    itemIndexDTO.setItemBrand(this.getItemBrand());
    itemIndexDTO.setItemSpec(this.getItemSpec());
    itemIndexDTO.setItemModel(this.getItemModel());
    itemIndexDTO.setItemCount(this.getItemCount());
    itemIndexDTO.setItemPrice(this.getItemPrice());
    itemIndexDTO.setServices(this.getServices());
    itemIndexDTO.setOrderTotalAmount(this.getOrderTotalAmount());
    itemIndexDTO.setArrears(this.getArrears());
    itemIndexDTO.setPaymentTime(this.getPaymentTime());
    itemIndexDTO.setVehicleBrand(this.getVehicleBrand());
    itemIndexDTO.setVehicleModel(this.getVehicleModel());
    itemIndexDTO.setVehicleYear(this.getVehicleYear());
    itemIndexDTO.setVehicleEngine(this.getVehicleEngine());
    itemIndexDTO.setItemMemo(this.getItemMemo());
    itemIndexDTO.setCustomerCardId(this.getCustomerCardId());
    itemIndexDTO.setMemberCardId(this.getMemberCardId());
    itemIndexDTO.setServiceId(this.getServiceId());
    itemIndexDTO.setIncreasedTimes(this.getIncreasedTimes());
    itemIndexDTO.setVehicles(this.getVehicles());
    itemIndexDTO.setDeadline(this.getDeadline());
    itemIndexDTO.setCardTimes(this.getCardTimes());
    itemIndexDTO.setBalanceTimes(this.getBalanceTimes());
    itemIndexDTO.setOldTimes(this.getOldTimes());
    itemIndexDTO.setIncreasedTimesLimitType(this.getIncreasedTimesLimitType());
    itemIndexDTO.setCardTimesLimitType(this.getCardTimesLimitType());
    itemIndexDTO.setOldTimesLimitType(this.getOldTimesLimitType());
    itemIndexDTO.setBalanceTimesLimitType(this.getBalanceTimesLimitType());
	  itemIndexDTO.setCommodityCode(this.getCommodityCode());
    itemIndexDTO.setBusinessCategoryId(this.getBusinessCategoryId());
    itemIndexDTO.setBusinessCategoryName(this.getBusinessCategoryName());
    itemIndexDTO.setProductKind(this.getProductKind());
    if(null != this.getAfterMemberDiscountOrderTotal())
    {
      itemIndexDTO.setAfterMemberDiscountOrderTotal(this.getAfterMemberDiscountOrderTotal());
    }
    else
    {
      itemIndexDTO.setAfterMemberDiscountOrderTotal(this.getOrderTotalAmount());
    }
    return itemIndexDTO;
  }

  public ItemIndex fromDTO(ItemIndexDTO itemIndexDTO,boolean isSetId) {
    if(isSetId){
      this.setId(itemIndexDTO.getId());
    }

    this.setUnit(itemIndexDTO.getUnit());
    this.setShopId(itemIndexDTO.getShopId());
    this.setCustomerId(itemIndexDTO.getCustomerId());
    this.setVehicle(itemIndexDTO.getVehicle());
    this.setOrderId(itemIndexDTO.getOrderId());
    this.setOrderTypeEnum(itemIndexDTO.getOrderType());
    this.setItemId(itemIndexDTO.getItemId());
    this.setItemTypeEnum(itemIndexDTO.getItemType());
    this.setItemName(itemIndexDTO.getItemName());
    this.setOrderStatusEnum(itemIndexDTO.getOrderStatus());
    this.setOrderTimeCreated(itemIndexDTO.getOrderTimeCreated());
    this.setItemCostPrice(itemIndexDTO.getItemCostPrice());
    this.setTotalCostPrice(itemIndexDTO.getTotalCostPrice());
    this.setCustomerCardId(itemIndexDTO.getCustomerCardId());
    this.setItemMemo(itemIndexDTO.getItemMemo());

    this.setCustomerOrSupplierName(itemIndexDTO.getCustomerOrSupplierName());
    this.setProductId(itemIndexDTO.getProductId());
    this.setItemBrand(itemIndexDTO.getItemBrand());
    this.setItemSpec(itemIndexDTO.getItemSpec());
    this.setItemModel(itemIndexDTO.getItemModel());
    this.setItemCount(itemIndexDTO.getItemCount());
    this.setItemPrice(itemIndexDTO.getItemPrice());
    this.setServices(itemIndexDTO.getServices());
    this.setOrderTotalAmount(itemIndexDTO.getOrderTotalAmount());
    this.setArrears(itemIndexDTO.getArrears());
    this.setPaymentTime(itemIndexDTO.getPaymentTime());
    this.setVehicleBrand(itemIndexDTO.getVehicleBrand());
    this.setVehicleModel(itemIndexDTO.getVehicleModel());
    this.setVehicleYear(itemIndexDTO.getVehicleYear());
    this.setVehicleEngine(itemIndexDTO.getVehicleEngine());
    this.setMemberCardId(itemIndexDTO.getMemberCardId());
    this.setServiceId(itemIndexDTO.getServiceId());
    this.setIncreasedTimes(itemIndexDTO.getIncreasedTimes());
    this.setVehicles(itemIndexDTO.getVehicles());
    this.setDeadline(itemIndexDTO.getDeadline());
    this.setCardTimes(itemIndexDTO.getCardTimes());
    this.setBalanceTimes(itemIndexDTO.getBalanceTimes());
    this.setOldTimes(itemIndexDTO.getOldTimes());
    this.setIncreasedTimesLimitType(itemIndexDTO.getIncreasedTimesLimitType());
    this.setCardTimesLimitType(itemIndexDTO.getCardTimesLimitType());
    this.setOldTimesLimitType(itemIndexDTO.getOldTimesLimitType());
    this.setBalanceTimesLimitType(itemIndexDTO.getBalanceTimesLimitType());
    this.setBusinessCategoryId(itemIndexDTO.getBusinessCategoryId());
    this.setBusinessCategoryName(itemIndexDTO.getBusinessCategoryName());
    this.setProductKind(itemIndexDTO.getProductKind());
    if(null != itemIndexDTO.getAfterMemberDiscountOrderTotal())
    {
      this.setAfterMemberDiscountOrderTotal(itemIndexDTO.getAfterMemberDiscountOrderTotal());
    }
    else
    {
      this.setAfterMemberDiscountOrderTotal(itemIndexDTO.getOrderTotalAmount());
    }
    this.setCommodityCode(itemIndexDTO.getCommodityCode());
    return this;
  }
}
