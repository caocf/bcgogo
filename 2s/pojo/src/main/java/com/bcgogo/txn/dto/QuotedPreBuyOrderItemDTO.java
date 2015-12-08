package com.bcgogo.txn.dto;

import com.bcgogo.enums.ItemTypes;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.txn.preBuyOrder.QuotedResult;
import com.bcgogo.enums.txn.preBuyOrder.ShippingMethod;
import com.bcgogo.product.ProductCategory.ProductCategoryDTO;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.lang.StringUtils;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 12-12-14
 * Time: 上午11:42
 * To change this template use File | Settings | File Templates.
 */
public class QuotedPreBuyOrderItemDTO extends BcgogoOrderItemDto {
  private String idStr;
  private Long quotedPreBuyOrderId;
  private Long preBuyOrderItemId;
  private Long preBuyOrderId;
  private String preBuyOrderIdStr;
  private PreBuyOrderItemDTO preBuyOrderItemDTO;
  private String includingTax;
  private ShippingMethod shippingMethod;
  private String shippingMethodStr;
  private Integer arrivalTime;
  private String memo;
  private QuotedResult quotedResult = QuotedResult.NotOrders;

  private Long shopId;
  private String shopIdStr;
  private Long quotedDate;
  private String quotedDateStr;
  private String shopName;
  private Double price;
  private Double inSalesAmount;
  private ProductDTO productDTO;

  private String productCategoryName;
  private Long productCategoryId;
  private int countSupplierOtherQuoted;
  private String qqArray;

  public String getQqArray() {
    return qqArray;
  }

  public void setQqArray(String qqArray) {
    this.qqArray = qqArray;
  }

  public QuotedPreBuyOrderItemDTO() {
    this.amount = 0d;

  }

  public String getShopIdStr() {
    return shopIdStr;
  }

  public void setShopIdStr(String shopIdStr) {
    this.shopIdStr = shopIdStr;
  }

  public Long getPreBuyOrderId() {
    return preBuyOrderId;
  }

  public void setPreBuyOrderId(Long preBuyOrderId) {
    this.preBuyOrderId = preBuyOrderId;
    this.preBuyOrderIdStr= StringUtil.valueOf(preBuyOrderId);
  }

  public String getPreBuyOrderIdStr() {
    return preBuyOrderIdStr;
  }

  public void setPreBuyOrderIdStr(String preBuyOrderIdStr) {
    this.preBuyOrderIdStr = preBuyOrderIdStr;
  }

  public PreBuyOrderItemDTO getPreBuyOrderItemDTO() {
    return preBuyOrderItemDTO;
  }

  public void setPreBuyOrderItemDTO(PreBuyOrderItemDTO preBuyOrderItemDTO) {
    this.preBuyOrderItemDTO = preBuyOrderItemDTO;
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
    if(shopId != null) {
      setShopIdStr(shopId.toString());
    }
  }

  public Long getQuotedDate() {
    return quotedDate;
  }

  public void setQuotedDate(Long quotedDate) {
    if(quotedDate!=null){
      this.quotedDateStr = DateUtil.convertDateLongToDateString(DateUtil.DATE_STRING_FORMAT_DAY,quotedDate);
    }
    this.quotedDate = quotedDate;
  }

  public String getQuotedDateStr() {
    return quotedDateStr;
  }

  public void setQuotedDateStr(String quotedDateStr) {
    this.quotedDateStr = quotedDateStr;
  }

  public Long getQuotedPreBuyOrderId() {
    return quotedPreBuyOrderId;
  }

  public void setQuotedPreBuyOrderId(Long quotedPreBuyOrderId) {
    this.quotedPreBuyOrderId = quotedPreBuyOrderId;
  }

  public Long getPreBuyOrderItemId() {
    return preBuyOrderItemId;
  }

  public void setPreBuyOrderItemId(Long preBuyOrderItemId) {
    this.preBuyOrderItemId = preBuyOrderItemId;
  }

  public String getIncludingTax() {
    return includingTax;
  }

  public void setIncludingTax(String includingTax) {
    this.includingTax = includingTax;
  }

  public ShippingMethod getShippingMethod() {
    return shippingMethod;
  }

  public void setShippingMethod(ShippingMethod shippingMethod) {
    if(shippingMethod==null) return;
    this.shippingMethod = shippingMethod;
    this.shippingMethodStr=shippingMethod.getName();
  }

  public String getShippingMethodStr() {
    return shippingMethodStr;
  }

  public void setShippingMethodStr(String shippingMethodStr) {
    this.shippingMethodStr = shippingMethodStr;
  }

  public Integer getArrivalTime() {
    return arrivalTime;
  }

  public void setArrivalTime(Integer arrivalTime) {
    this.arrivalTime = arrivalTime;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    if(id!=null){
      this.idStr = id.toString();
    }
    this.id = id;
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public String getProductCategoryName() {
    return productCategoryName;
  }

  public void setProductCategoryName(String productCategoryName) {
    this.productCategoryName = productCategoryName;
  }

  public Double getInSalesAmount() {
    return inSalesAmount;
  }

  public void setInSalesAmount(Double inSalesAmount) {
    this.inSalesAmount = inSalesAmount;
  }

  public Long getProductCategoryId() {
    return productCategoryId;
  }

  public void setProductCategoryId(Long productCategoryId) {
    this.productCategoryId = productCategoryId;
  }

  public QuotedResult getQuotedResult() {
    return quotedResult;
  }

  public void setQuotedResult(QuotedResult quotedResult) {
    this.quotedResult = quotedResult;
  }

  public String getShopName() {
    return shopName;
  }

  public void setShopName(String shopName) {
    this.shopName = shopName;
  }

  public ProductDTO getProductDTO() {
    return productDTO;
  }

  public void setProductDTO(ProductDTO productDTO) {
    this.productDTO = productDTO;
  }

  public ItemIndexDTO toItemIndexDTO(QuotedPreBuyOrderDTO quotedPreBuyOrderDTO) {
    ItemIndexDTO itemIndexDTO = super.toBcgogoItemIndexDTO();
    itemIndexDTO.setEditor(quotedPreBuyOrderDTO.getEditor());
    itemIndexDTO.setShopId(quotedPreBuyOrderDTO.getShopId());
    itemIndexDTO.setOrderId(quotedPreBuyOrderDTO.getId());
    itemIndexDTO.setOrderTimeCreated(quotedPreBuyOrderDTO.getVestDate() == null ? quotedPreBuyOrderDTO.getCreationDate() : quotedPreBuyOrderDTO.getVestDate());
    itemIndexDTO.setOrderType(OrderTypes.QUOTED_PRE_BUY_ORDER);
    itemIndexDTO.setItemType(ItemTypes.MATERIAL);
    itemIndexDTO.setCustomerOrSupplierName(quotedPreBuyOrderDTO.getCustomerShopName());
    itemIndexDTO.setQuotedResult(this.getQuotedResult());
    itemIndexDTO.setItemPrice(this.getPrice());
    return itemIndexDTO;
  }

  public void setQuotedPreBuyOrderDTO(QuotedPreBuyOrderDTO orderDTO){
    if(orderDTO==null) return;
    this.setQuotedDateStr(DateUtil.convertDateLongToDateString(DateUtil.STANDARD,orderDTO.getVestDate()));
  }

  public String getProductInfo(){
    StringBuffer sb = new StringBuffer();
    if(StringUtils.isNotBlank(this.getCommodityCode())){
      sb.append(this.getCommodityCode()).append(" ");
    }
    if(StringUtils.isNotBlank(this.getProductName())){
      sb.append(this.getProductName()).append(" ");
    }
    if(StringUtils.isNotBlank(this.getBrand())){
      sb.append(this.getBrand()).append(" ");
    }
    if(StringUtils.isNotBlank(this.getSpec())){
      sb.append(this.getSpec()).append(" ");
    }
    if(StringUtils.isNotBlank(this.getModel())){
      sb.append(this.getModel()).append(" ");
    }
    if(StringUtils.isNotBlank(this.getVehicleBrand())){
      sb.append(this.getVehicleBrand()).append(" ");
    }
    if(StringUtils.isNotBlank(this.getVehicleModel())){
      sb.append(this.getVehicleModel()).append(" ");
    }
    return sb.toString().trim();
  }

  public void setQuotedPreBuyOrder(QuotedPreBuyOrderDTO order){
    this.setQuotedDate(order.getVestDate());
  }

  public int getCountSupplierOtherQuoted() {
    return countSupplierOtherQuoted;
  }

  public void setCountSupplierOtherQuoted(int countSupplierOtherQuoted) {
    this.countSupplierOtherQuoted = countSupplierOtherQuoted;
  }
}
