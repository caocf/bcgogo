package com.bcgogo.search.dto;

import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.enums.*;
import com.bcgogo.enums.txn.preBuyOrder.BusinessChanceType;
import com.bcgogo.enums.txn.preBuyOrder.QuotedResult;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.txn.dto.PurchaseReturnItemDTO;
import com.bcgogo.user.dto.MemberCardDTO;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.JsonUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: wjl
 * Date: 12-1-6
 * Time: 下午9:05
 * To change this template use File | Settings | File Templates.
 */
public class ItemIndexDTO implements Serializable {
  private static final Logger LOG = LoggerFactory.getLogger(ItemIndexDTO.class);
  private Long id;
  private Long lastUpdate;
  private String editor;

  private Long shopId;         //店面ID
  private String shopKind;
  private String shopName;
  private List<Long> shopAreaIdList;
  private String shopAreaInfo;

  private Long customerId;    // 客户或供应商ID
  private String vehicle;      // 不需要了
  private Long orderId;        //单子ID
  private String orderIdStr;        //单子ID
  private String orderReceiptNo;//单据号
  private OrderTypes orderType;    // 单子类型
  private Long itemId;          //Item ID
  private ItemTypes itemType;     //Item 类型，页面暂未用到
  private OrderStatus orderStatus;     //  状态
  private Long orderTimeCreated; // 时间
  private Long vestDate;
  private Long endTime; // 预购 截止时间时间

  private String customerOrSupplierName;   // 客户或供应商名字
  private String customerOrSupplierStatus;
  private String itemName;      //品名
  private String itemBrand;        //item    品牌
  private String itemSpec;         //item    规格
  private String itemModel;        //item    型号‘
  private String vehicleBrand;
  private String vehicleModel;
  //itemCount改为Double类型
  private Double itemCount;        //item    数量
  //end
  private String orderTimeCreatedStr;  //时间的字符串形式
  private String orderTypeStr;  //单据状态文字说明
  private Boolean goodsBuyOrderType;
  private Boolean goodsSaleOrderType;
  private Boolean goodsStorageOrderType;
  private Boolean repairOrderType;
  private Boolean returnOrderType;
  private List<OrderTypes> selectedOrderTypes;

  private String serviceWorker;
  private String services;          //施工内容


  private String startDateStr;
  private String endDateStr;
  private String pageNo;
  private Double itemPrice, itemPriceApprox;       //单价 单价近似值
  private Double itemTotalAmount, itemTotalAmountApprox; //总金额  总金额近似值
  private String itemMemo; //备注

  private Double orderTotalAmount; //单据金额总计
  private Double arrears; //欠款；
  private Long paymentTime;//还款时间
  private String paymentTimeStr;//还款时间
  private String orderStatusStr;
  private Integer indexNo;

  private ProductDTO productDTO;//商品
  private MemberCardDTO memberCardDTO; //会员卡


  private String vehicleYear;
  private String vehicleEngine;
  private Double returnAbleCount;
  //  private List<ItemIndexDTO> itemIndexDTOs;
  private Long productId;
  private String productIdStr;
  private Long productKindId;
  private String productKind;
  private Double itemCostPrice;
  private String unit;
  private Double totalCostPrice;

  private Double inoutRecordTotalCostPrice;
  private Double inoutRecordItemTotalAmount;


  private Long customerCardId;
  private Long memberCardId;
  private PurchaseReturnItemDTO[] itemDTOs;     //生成退货单之前的查询结果
  private PurchaseReturnItemDTO[] selectItemDTOs;//生成退货单之前的选中结果

  private Long serviceId;
  private ConsumeType consumeType;
  private Integer increasedTimes;   //新增次数 会员
  private String vehicles;    //限制车辆
  private Long deadline;       //截止日期
  private String deadlineStr;       //截止日期
  private Integer cardTimes;    //原来 卡的 新增次数
  private Integer balanceTimes;  //剩余次数
  private Integer oldTimes;       //原有次数
  private ServiceLimitTypes increasedTimesLimitType;
  private ServiceLimitTypes cardTimesLimitType;
  private ServiceLimitTypes oldTimesLimitType;
  private ServiceLimitTypes balanceTimesLimitType;

  private String memberCardName;

	private String commodityCode;//商品编码

  private Long businessCategoryId;
  private String businessCategoryName;
	private Long supplierProductId;//批发商产品Id

  private Double afterMemberDiscountOrderTotal;
  private String storehouseName;
  private Long storehouseId;

  private Long relatedSupplierId;
  private String relatedSupplierName;
  private Long relatedCustomerId;
  private String relatedCustomerName;
  private Long inOutRecordId;

  private QuotedResult quotedResult;

  private String couponType;

  private String customMatchPContent;
  private String customMatchPVContent;
  private BusinessChanceType businessChanceType;

  //单据的实收
  private Double settledAmount;
  private Double discount;//单据的折扣
  public ItemIndexDTO() {

  }

  //order 中的 item 使用
  public ItemIndexDTO(String itemDetailJson) {
    Map<String, String> map = JsonUtil.jsonToStringMap(itemDetailJson);
    if (StringUtils.isNotBlank(map.get("consume_type"))) this.consumeType = ConsumeType.valueOf(map.get("consume_type"));
    if (StringUtils.isNotBlank(map.get("item_id"))) this.itemId = Long.valueOf(map.get("item_id"));
    if (StringUtils.isNotBlank(map.get("item_type"))) this.itemType = ItemTypes.valueOf(map.get("item_type"));
    if (StringUtils.isNotBlank(map.get("service_id"))) this.serviceId =Long.valueOf(map.get("service_id"));
    if (StringUtils.isNotBlank(map.get("product_id"))) this.setProductId(Long.valueOf(map.get("product_id")));
	  if(StringUtils.isNotBlank(map.get("supplier_product_id"))) this.supplierProductId = Long.valueOf(map.get("supplier_product_id"));
    this.commodityCode = map.get("commodity_code");
    if (ItemTypes.MATERIAL.equals(itemType)) {
      this.itemName = map.get("product_name");
      this.itemBrand = map.get("product_brand");
      this.itemSpec = map.get("product_spec");
      this.itemModel = map.get("product_model");
      this.vehicleBrand = map.get("product_vehicle_brand");
      this.vehicleModel = map.get("product_vehicle_model");
    } else if(ItemTypes.SERVICE.equals(itemType)){
      this.services = map.get("services");
      this.serviceWorker = map.get("service_worker");
    } else if (ItemTypes.SALE_MEMBER_CARD_SERVICE.equals(this.itemType)) {
      this.setItemName(map.get("service_name"));
      this.setOldTimes(NumberUtil.toInteger(map.get("old_times")));
      this.setBalanceTimes(NumberUtil.toInteger(map.get("balance_times")));
      this.setIncreasedTimes(NumberUtil.toInteger(map.get("increased_times")));
      this.setVehicles(map.get("limit_vehicles"));
      this.setDeadline(NumberUtil.toLong(map.get("dead_line")));
      String increasedTimesLimitTypeStr=map.get("increased_times_limit_type");
      if (StringUtils.isNotBlank(increasedTimesLimitTypeStr))
        this.setIncreasedTimesLimitType(ServiceLimitTypes.valueOf(increasedTimesLimitTypeStr));
    }else if(ItemTypes.OTHER_INCOME.equals(this.itemType)){
      this.setItemName(map.get("other_income_name"));
    }
    this.unit = map.get("unit");
    this.itemCount = map.get("item_count")==null?0d:Double.valueOf(map.get("item_count"));
    this.itemPrice = map.get("item_price")==null?0d:Double.valueOf(map.get("item_price"));
    if (StringUtils.isNotBlank(map.get("item_memo"))) this.itemMemo = map.get("item_memo");
	  if(StringUtils.isNotBlank(map.get("commodity_code"))){
		  this.commodityCode = map.get("commodity_code");
	  }
  }

  public String generateItemDetail() {
    Map<String, String> map = new HashMap<String, String>();
    map.put("item_id", this.itemId == null ? "" : this.itemId.toString());
    if (ItemTypes.MATERIAL.equals(this.itemType)) {
      map.put("product_name", this.itemName);
      map.put("product_id", this.productId == null ? "" : this.productId.toString());
	    map.put("supplier_product_id",this.supplierProductId == null? "" :this.supplierProductId.toString());
      map.put("item_memo", this.getItemMemo() == null?"":this.getItemMemo());
    } else if (ItemTypes.SERVICE.equals(this.itemType)) {
      map.put("services", this.itemName);
      map.put("service_id", this.serviceId == null ? "" : this.serviceId.toString());
      map.put("item_memo", this.getItemMemo() == null ? "" : this.getItemMemo());
    } else if (ItemTypes.SALE_MEMBER_CARD_SERVICE.equals(this.itemType)) {
      map.put("service_name", this.itemName);
      map.put("old_times", StringUtil.StringValueOf(this.getOldTimes()));
      map.put("balance_times", StringUtil.StringValueOf(this.getBalanceTimes()));
      map.put("increased_times", StringUtil.StringValueOf(this.getIncreasedTimes()));
      map.put("limit_vehicles", StringUtil.StringValueOf(this.getVehicles()));
      map.put("dead_line", StringUtil.StringValueOf(this.getDeadline()));
      if(getGoodsBuyOrderType()!=null) map.put("increased_times_limit_type", getIncreasedTimesLimitType().name());
    }else if(ItemTypes.OTHER_INCOME.equals(this.itemType)){
      map.put("other_income_name",this.itemName);
      map.put("item_memo", this.getItemMemo() == null ? "" : this.getItemMemo());
    }
    map.put("product_brand", this.itemBrand);
    map.put("product_spec", this.itemSpec);
    map.put("product_model", this.itemModel);
    map.put("product_vehicle_brand", this.vehicleBrand);
    map.put("product_vehicle_model", this.vehicleModel);
    map.put("item_count", this.itemCount == null ? "0" : this.itemCount.toString());
    map.put("item_price", this.itemPrice == null ? "0" : this.itemPrice.toString());
    map.put("item_type", this.itemType == null ? "" : this.itemType.toString());
    map.put("unit", this.unit);
    map.put("consume_type", this.consumeType == null ? "" : this.consumeType.toString());
	  map.put("commodity_code",this.commodityCode);
	  map.put("service_worker",this.serviceWorker);
    return JsonUtil.mapToJson(map);
  }

  public BusinessChanceType getBusinessChanceType() {
    return businessChanceType;
  }

  public void setBusinessChanceType(BusinessChanceType businessChanceType) {
    this.businessChanceType = businessChanceType;
  }

  public Double getSettledAmount() {
    return settledAmount;
  }

  public void setSettledAmount(Double settledAmount) {
    this.settledAmount = settledAmount;
  }

  public Double getDiscount() {
    return discount;
  }

  public void setDiscount(Double discount) {
    this.discount = discount;
  }

  public String getCustomMatchPVContent() {
    return customMatchPVContent;
  }

  public void setCustomMatchPVContent(String customMatchPVContent) {
    this.customMatchPVContent = customMatchPVContent;
  }

  public String getCustomMatchPContent() {
    return customMatchPContent;
  }

  public void setCustomMatchPContent(String customMatchPContent) {
    this.customMatchPContent = customMatchPContent;
  }

  public String getShopName() {
    return shopName;
  }

  public void setShopName(String shopName) {
    this.shopName = shopName;
  }

  public List<Long> getShopAreaIdList() {
    return shopAreaIdList;
  }

  public void setShopAreaIdList(List<Long> shopAreaIdList) {
    this.shopAreaIdList = shopAreaIdList;
  }

  public String getShopAreaInfo() {
    return shopAreaInfo;
  }

  public void setShopAreaInfo(String shopAreaInfo) {
    this.shopAreaInfo = shopAreaInfo;
  }

  public String getEditor() {
    return editor;
  }

  public void setEditor(String editor) {
    this.editor = editor;
  }

  public String getShopKind() {
    return shopKind;
  }

  public void setShopKind(String shopKind) {
    this.shopKind = shopKind;
  }

  public Long getVestDate() {
    return vestDate;
  }

  public void setVestDate(Long vestDate) {
    this.vestDate = vestDate;
  }

  public Long getEndTime() {
    return endTime;
  }

  public void setEndTime(Long endTime) {
    this.endTime = endTime;
  }

  public String getServiceWorker() {
    return serviceWorker;
  }

  public void setServiceWorker(String serviceWorker) {
    this.serviceWorker = serviceWorker;
  }

  public MemberCardDTO getMemberCardDTO() {
    return memberCardDTO;
  }

  public void setMemberCardDTO(MemberCardDTO memberCardDTO) {
    this.memberCardDTO = memberCardDTO;
  }

  public PurchaseReturnItemDTO[] getSelectItemDTOs() {
    return selectItemDTOs;
  }

  public void setSelectItemDTOs(PurchaseReturnItemDTO[] selectItemDTOs) {
    this.selectItemDTOs = selectItemDTOs;
  }

  public PurchaseReturnItemDTO[] getItemDTOs() {
    return itemDTOs;
  }

  public void setItemDTOs(PurchaseReturnItemDTO[] itemDTOs) {
    this.itemDTOs = itemDTOs;
  }

  public Long getCustomerCardId() {
    return customerCardId;
  }

  public void setCustomerCardId(Long customerCardId) {
    this.customerCardId = customerCardId;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Double getTotalCostPrice() {
      return totalCostPrice;
  }

  public void setTotalCostPrice(Double totalCostPrice) {
      this.totalCostPrice = totalCostPrice;
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    if (productId != null) {
      this.productIdStr = String.valueOf(productId);
    }
    this.productId = productId;
  }

  public String getProductIdStr() {
    return productIdStr;
  }

  public void setProductIdStr(String productIdStr) {
    this.productIdStr = productIdStr;
  }

  public String getItemMemo() {
    return itemMemo;
  }

  public void setItemMemo(String itemMemo) {
    this.itemMemo = itemMemo;
  }

  public Integer getIndexNo() {
    return indexNo;
  }

  public void setIndexNo(Integer indexNo) {
    this.indexNo = indexNo;
  }

//  public List<ItemIndexDTO> getItemIndexDTOs() {
//    return itemIndexDTOs;
//  }
//
//  public void setItemIndexDTOs(List<ItemIndexDTO> itemIndexDTOs) {
//    this.itemIndexDTOs = itemIndexDTOs;
//  }

  public Double getReturnAbleCount() {
    return returnAbleCount;
  }

  public void setReturnAbleCount(Double returnAbleCount) {
    this.returnAbleCount = returnAbleCount;
  }

  public Double getItemCostPrice() {
    return itemCostPrice;
  }

  public void setItemCostPrice(Double itemCostPrice) {
    this.itemCostPrice = itemCostPrice;
  }


  public String getVehicleBrand() {
    return vehicleBrand;
  }

  public void setVehicleBrand(String vehicleBrand) {
    this.vehicleBrand = vehicleBrand;
  }

  public String getVehicleModel() {
    return vehicleModel;
  }

  public void setVehicleModel(String vehicleModel) {
    this.vehicleModel = vehicleModel;
  }

  public void setLastUpdate(Long lastUpdate) {
    this.lastUpdate = lastUpdate;
  }

  public String getVehicleYear() {
    return vehicleYear;
  }

  public void setVehicleYear(String vehicleYear) {
    this.vehicleYear = vehicleYear;
  }

  public String getVehicleEngine() {
    return vehicleEngine;
  }

  public Long getLastUpdate() {
    return lastUpdate;
  }

  public void setVehicleEngine(String vehicleEngine) {
    this.vehicleEngine = vehicleEngine;
  }

  public String getOrderStatusStr() {
    return orderStatusStr;
  }

  public void setOrderStatusStr(String orderStatusStr) {
    this.orderStatusStr = orderStatusStr;
  }

  public String getPaymentTimeStr() {
    return paymentTimeStr;
  }

  public void setPaymentTimeStr(String paymentTimeStr) {
    this.paymentTimeStr = paymentTimeStr;
  }

  public String getServices() {
    return services;
  }

  public void setServices(String services) {
    this.services = services;
  }

  public Double getOrderTotalAmount() {
    return orderTotalAmount;
  }

  public void setOrderTotalAmount(Double orderTotalAmount) {
    this.orderTotalAmount = orderTotalAmount;
  }

  public Double getArrears() {
    return arrears;
  }

  public void setArrears(Double arrears) {
    this.arrears = arrears;
  }

  public Long getPaymentTime() {
    return paymentTime;
  }

  public void setPaymentTime(Long paymentTime) {
    this.paymentTime = paymentTime;
    if (paymentTime != null && !paymentTime.equals(0L)) {
      this.paymentTimeStr = DateUtil.convertDateLongToString(paymentTime);
    } else {
      this.paymentTimeStr = "";
    }
  }

  public Double getItemTotalAmount() {
    return itemTotalAmount;
  }

  public void setItemTotalAmount(Double itemTotalAmount) {
    this.itemTotalAmount = itemTotalAmount;
  }

  public Double getItemPrice() {
    return itemPrice;
  }

  public void setItemPrice(Double itemPrice) {
    this.itemPrice = itemPrice;
  }

  public Double getItemPriceApprox() {
    itemPriceApprox = NumberUtil.round(itemPrice, NumberUtil.MONEY_PRECISION);
    return itemPriceApprox;
  }

  public void setItemPriceApprox(Double itemPriceApprox) {
    this.itemPriceApprox = itemPriceApprox;
  }

  public Double getItemTotalAmountApprox() {
    itemTotalAmountApprox = NumberUtil.round(itemTotalAmount, NumberUtil.MONEY_PRECISION);
    return itemTotalAmountApprox;
  }

  public void setItemTotalAmountApprox(Double itemTotalAmountApprox) {
    this.itemTotalAmountApprox = itemTotalAmountApprox;
  }

  public String getPageNo() {
    return pageNo;
  }

  public void setPageNo(String pageNo) {
    this.pageNo = pageNo;
  }

  public String getStartDateStr() {
    return startDateStr;
  }

  public void setStartDateStr(String startDateStr) {
    this.startDateStr = startDateStr;
  }

  public String getEndDateStr() {
    return endDateStr;
  }

  public void setEndDateStr(String endDateStr) {
    this.endDateStr = endDateStr;
  }

  public Boolean getGoodsBuyOrderType() {
    return goodsBuyOrderType;
  }

  public void setGoodsBuyOrderType(Boolean goodsBuyOrderType) {
    this.goodsBuyOrderType = goodsBuyOrderType;
  }

  public Boolean getGoodsSaleOrderType() {
    return goodsSaleOrderType;
  }

  public void setGoodsSaleOrderType(Boolean goodsSaleOrderType) {
    this.goodsSaleOrderType = goodsSaleOrderType;
  }

  public Boolean getGoodsStorageOrderType() {
    return goodsStorageOrderType;
  }

  public void setGoodsStorageOrderType(Boolean goodsStorageOrderType) {
    this.goodsStorageOrderType = goodsStorageOrderType;
  }

  public Boolean getRepairOrderType() {
    return repairOrderType;
  }

  public void setRepairOrderType(Boolean repairOrderType) {
    this.repairOrderType = repairOrderType;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  public String getVehicle() {
    return vehicle;
  }

  public void setVehicle(String vehicle) {
    this.vehicle = vehicle;
  }

  public Long getOrderId() {
    return orderId;
  }

  public void setOrderId(Long orderId) {
    if(orderId!=null) this.orderIdStr=orderId.toString();
    this.orderId = orderId;
  }

  public String getOrderIdStr() {
    return orderIdStr;
  }

  public void setOrderIdStr(String orderIdStr) {
    this.orderIdStr = orderIdStr;
  }

  public OrderTypes getOrderType() {
    return orderType;
  }

  public void setOrderType(OrderTypes orderType) {
    this.orderType = orderType;
  }

  public Long getItemId() {
    return itemId;
  }

  public void setItemId(Long itemId) {
    this.itemId = itemId;
  }

  public ItemTypes getItemType() {
    return itemType;
  }

  public void setItemType(ItemTypes itemType) {
    this.itemType = itemType;
  }

  public String getItemName() {
    return itemName;
  }

  public void setItemName(String itemName) {
    this.itemName = itemName;
  }

  public OrderStatus getOrderStatus() {
    return orderStatus;
  }

  public void setOrderStatus(OrderStatus orderStatus) {
    this.orderStatus = orderStatus;
  }

  public Long getOrderTimeCreated() {
    return orderTimeCreated;
  }

  public void setOrderTimeCreated(Long orderTimeCreated) {
    this.orderTimeCreated = orderTimeCreated;
    if (orderTimeCreated != null) {
      this.orderTimeCreatedStr = DateUtil.convertDateLongToString(orderTimeCreated);
    } else {
      this.orderTimeCreatedStr = "";
    }
  }

  public String getCustomerOrSupplierName() {
    return customerOrSupplierName;
  }

  public void setCustomerOrSupplierName(String customerOrSupplierName) {
    this.customerOrSupplierName = customerOrSupplierName;
  }

  public String getItemBrand() {
    return itemBrand;
  }

  public void setItemBrand(String itemBrand) {
    this.itemBrand = itemBrand;
  }

  public String getItemSpec() {
    return itemSpec;
  }

  public void setItemSpec(String itemSpec) {
    this.itemSpec = itemSpec;
  }

  public String getItemModel() {
    return itemModel;
  }

  public void setItemModel(String itemModel) {
    this.itemModel = itemModel;
  }

  public Double getItemCount() {
    return itemCount;
  }

  public void setItemCount(Double itemCount) {
    this.itemCount = itemCount;
  }

  public String getOrderTimeCreatedStr() {
    return orderTimeCreatedStr;
  }

  public void setOrderTimeCreatedStr(String orderTimeCreatedStr) {
    this.orderTimeCreatedStr = orderTimeCreatedStr;
  }

  public String getOrderTypeStr() {
    return orderTypeStr;
  }

  public void setOrderTypeStr(String orderTypeStr) {
    this.orderTypeStr = orderTypeStr;
  }


  public Boolean getReturnOrderType() {
    return returnOrderType;
  }

  public void setReturnOrderType(Boolean returnOrderType) {
    this.returnOrderType = returnOrderType;
  }

  public ProductDTO getProductDTO() {
    return productDTO;
  }

  public void setProductDTO(ProductDTO productDTO) {
    this.productDTO = productDTO;
  }

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public Long getMemberCardId() {
    return memberCardId;
  }

  public void setMemberCardId(Long memberCardId) {
    this.memberCardId = memberCardId;
  }

  public Long getServiceId() {
    return serviceId;
  }

  public Integer getIncreasedTimes() {
    return increasedTimes;
  }

  public String getVehicles() {
    return vehicles;
  }

  public Long getDeadline() {
    return deadline;
  }

  public Integer getCardTimes() {
    return cardTimes;
  }

  public Integer getBalanceTimes() {
    return balanceTimes;
  }

  public Integer getOldTimes() {
    return oldTimes;
  }

  public ServiceLimitTypes getIncreasedTimesLimitType() {
    return increasedTimesLimitType;
  }

  public ServiceLimitTypes getCardTimesLimitType() {
    return cardTimesLimitType;
  }

  public ServiceLimitTypes getOldTimesLimitType() {
    return oldTimesLimitType;
  }

  public ServiceLimitTypes getBalanceTimesLimitType() {
    return balanceTimesLimitType;
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

    public Double getInoutRecordTotalCostPrice() {
        return inoutRecordTotalCostPrice;
    }

    public void setInoutRecordTotalCostPrice(Double inoutRecordTotalCostPrice) {
        this.inoutRecordTotalCostPrice = inoutRecordTotalCostPrice;
    }

    public Double getInoutRecordItemTotalAmount() {
        return inoutRecordItemTotalAmount;
    }

    public void setInoutRecordItemTotalAmount(Double inoutRecordItemTotalAmount) {
        this.inoutRecordItemTotalAmount = inoutRecordItemTotalAmount;
    }

    public void setDeadline(Long deadline) {
    if (deadline != null) {
      if (deadline != -1l) {
        this.deadlineStr = DateUtil.convertDateLongToString(deadline);
      } else {
        this.deadlineStr = "无限期";
      }
    }
    this.deadline = deadline;
  }

  public String getDeadlineStr() {
    return deadlineStr;
  }

  public void setDeadlineStr(String deadlineStr) {
    this.deadlineStr = deadlineStr;
  }

  public void setCardTimes(Integer cardTimes) {
    this.cardTimes = cardTimes;
  }

  public void setBalanceTimes(Integer balanceTimes) {
    this.balanceTimes = balanceTimes;
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

  public String getMemberCardName() {
    return memberCardName;
  }

  public void setMemberCardName(String memberCardName) {
    this.memberCardName = memberCardName;
  }

  public List<OrderTypes> getSelectedOrderTypes() {
    return selectedOrderTypes;
  }

  public void setSelectedOrderTypes(List<OrderTypes> selectedOrderTypes) {
    this.selectedOrderTypes = selectedOrderTypes;
  }

  public ConsumeType getConsumeType() {
    return consumeType;
  }

  public void setConsumeType(ConsumeType consumeType) {
    this.consumeType = consumeType;
  }

	public String getCommodityCode() {
		return commodityCode;
	}

	public void setCommodityCode(String commodityCode) {
		this.commodityCode = commodityCode;
	}

  public Long getBusinessCategoryId() {
    return businessCategoryId;
  }

  public void setBusinessCategoryId(Long businessCategoryId) {
    this.businessCategoryId = businessCategoryId;
  }

  public String getBusinessCategoryName() {
    return businessCategoryName;
  }

  public void setBusinessCategoryName(String businessCategoryName) {
    this.businessCategoryName = businessCategoryName;
  }

  public String getOrderReceiptNo() {
    return orderReceiptNo;
  }

  public void setOrderReceiptNo(String orderReceiptNo) {
    this.orderReceiptNo = orderReceiptNo;
  }

  public String getCustomerOrSupplierStatus() {
    return customerOrSupplierStatus;
  }

  public void setCustomerOrSupplierStatus(String customerOrSupplierStatus) {
    this.customerOrSupplierStatus = customerOrSupplierStatus;
  }

  public Long getProductKindId() {
    return productKindId;
  }

  public void setProductKindId(Long productKindId) {
    this.productKindId = productKindId;
  }

  public String getProductKind() {
    return productKind;
  }

  public void setProductKind(String productKind) {
    this.productKind = productKind;
  }

  public Double getAfterMemberDiscountOrderTotal() {
    return afterMemberDiscountOrderTotal;
  }

  public void setAfterMemberDiscountOrderTotal(Double afterMemberDiscountOrderTotal) {
    this.afterMemberDiscountOrderTotal = afterMemberDiscountOrderTotal;
  }
	public Long getSupplierProductId() {
		return supplierProductId;
	}

	public void setSupplierProductId(Long supplierProductId) {
		this.supplierProductId = supplierProductId;
	}

  public String getStorehouseName() {
    return storehouseName;
  }

  public void setStorehouseName(String storehouseName) {
    this.storehouseName = storehouseName;
  }

  public Long getStorehouseId() {
    return storehouseId;
  }

  public void setStorehouseId(Long storehouseId) {
    this.storehouseId = storehouseId;
  }

  public Long getRelatedSupplierId() {
    return relatedSupplierId;
  }

  public void setRelatedSupplierId(Long relatedSupplierId) {
    this.relatedSupplierId = relatedSupplierId;
  }

  public String getRelatedSupplierName() {
    return relatedSupplierName;
  }

  public void setRelatedSupplierName(String relatedSupplierName) {
    this.relatedSupplierName = relatedSupplierName;
  }

  public Long getRelatedCustomerId() {
    return relatedCustomerId;
  }

  public void setRelatedCustomerId(Long relatedCustomerId) {
    this.relatedCustomerId = relatedCustomerId;
  }

  public String getRelatedCustomerName() {
    return relatedCustomerName;
  }

  public void setRelatedCustomerName(String relatedCustomerName) {
    this.relatedCustomerName = relatedCustomerName;
  }

  public Long getInOutRecordId() {
    return inOutRecordId;
  }

  public void setInOutRecordId(Long inOutRecordId) {
    this.inOutRecordId = inOutRecordId;
  }

  public QuotedResult getQuotedResult() {
    return quotedResult;
  }

  public void setQuotedResult(QuotedResult quotedResult) {
    this.quotedResult = quotedResult;
  }

  public String getCouponType() {
    return couponType;
  }

  public void setCouponType(String couponType) {
    this.couponType = couponType;
  }

  public void setShopInfo(ShopDTO shopDTO){
    if(shopDTO.getId().equals(this.getShopId())){
      this.setShopName(shopDTO.getName());
      this.setShopKind(shopDTO.getShopKind()==null?"":shopDTO.getShopKind().toString());
      this.setShopAreaInfo(shopDTO.getAreaName());
      List<Long> areaIdList =new ArrayList<Long>();
      if(shopDTO.getProvince()!=null){
        areaIdList.add(shopDTO.getProvince());
      }
      if(shopDTO.getCity()!=null){
        areaIdList.add(shopDTO.getCity());
      }
      if(shopDTO.getRegion()!=null){
        areaIdList.add(shopDTO.getRegion());
      }
      this.setShopAreaIdList(areaIdList);
    }else{
      LOG.error("shopId no equals,order id:"+this.getOrderId()+",shopDTO id:"+shopDTO.getId()+",order shopId:"+shopId);
    }
  }
}
