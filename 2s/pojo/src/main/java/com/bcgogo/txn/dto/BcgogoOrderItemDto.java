package com.bcgogo.txn.dto;

import com.bcgogo.config.dto.image.ImageCenterDTO;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.search.dto.ItemIndexDTO;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-5-26
 * Time: 下午1:04
 * To change this template use File | Settings | File Templates.
 */
public class BcgogoOrderItemDto implements Serializable {
  protected Long id;//每个item的id
  protected String idStr;
  protected Long productId; //对应 product_local_info 的id
  protected Long productOriginId;//对应product表的id
  protected String productIdStr;
  protected Long productHistoryId;
  protected String productHistoryIdStr;
  protected String brand;
  protected String productName;
  protected Long productKindId;
  protected String productKind;

  protected String vehicleBrand;
  protected String vehicleModel;
  protected String vehicleYear;
  protected String vehicleEngine;

  protected String productInfo;

  protected OutStorageRelationDTO[] outStorageRelationDTOs;
  protected InStorageRecordDTO[] inStorageRecordDTOs;


  protected String unit;
  protected String storageUnit;
  protected String sellUnit;
  protected Long rate;

	protected String  commodityCode;    //商品编码
	private boolean commodityCodeModifyFlag = false;//商品编码修改Flag true 表示用草稿箱的编码，false表示用自己原先的编码

  protected String model;
  protected String spec;

  protected Double inventoryAveragePrice;//均价
  protected Double tradePrice;   //批发价
	protected String storageBin;// 仓位

  protected Double amount;
  protected Double amountHid;
  protected String amountStr;
  protected Double reserved;//采购单   入库退货单   施工单 有预留
  protected Double reservedApprox;//近似值

  protected Double inventoryAmount;
  protected Double inventoryAmountApprox;
  protected Double remainAmount; //库存增加类型的单据剩余数量

  private Double itemCostPrice; //item的成本
  private Double itemTotalCostPrice;//item的总成本
  private boolean isAddVehicleInfoToSolr = false; //是否添加车辆品牌信息到solr  只有vehicle的基本信息

  private ImageCenterDTO imageCenterDTO;

  public void setProductDTOWithOutUnit(ProductDTO productDTO) {
    if (productDTO == null) {
      return;
    }
    this.setProductId(productDTO.getProductLocalInfoId());
    this.productName = productDTO.getName();
    this.brand = productDTO.getBrand();
    this.model = productDTO.getModel();
    this.spec = productDTO.getSpec();
    this.vehicleBrand = productDTO.getProductVehicleBrand();
    this.vehicleModel = productDTO.getProductVehicleModel();
    this.vehicleYear = productDTO.getProductVehicleYear();
	  this.vehicleEngine = productDTO.getVehicleEngine();
    this.sellUnit = productDTO.getSellUnit();
    this.storageUnit = productDTO.getStorageUnit();
    this.rate = productDTO.getRate();
    this.tradePrice = productDTO.getTradePrice();
    this.inventoryAveragePrice = productDTO.getInventoryAveragePrice();
    this.storageBin = productDTO.getStorageBin();
    this.commodityCode = productDTO.getCommodityCode();
    this.setProductKind(productDTO.getKindName());
    this.setProductKindId(productDTO.getKindId());
  }

  public void setProductHistoryDTO(ProductHistoryDTO productHistoryDTO){
    if (productHistoryDTO == null) {
      return;
    }
//    this.setProductId(productHistoryDTO.getProductLocalInfoId());
    this.productName = productHistoryDTO.getName();
    this.brand = productHistoryDTO.getBrand();
    this.model = productHistoryDTO.getModel();
    this.spec = productHistoryDTO.getSpec();
    this.vehicleBrand = productHistoryDTO.getProductVehicleBrand();
    this.vehicleModel = productHistoryDTO.getProductVehicleModel();
    this.vehicleYear = productHistoryDTO.getProductVehicleYear();
    this.vehicleEngine = productHistoryDTO.getProductVehicleEngine();
    this.sellUnit = productHistoryDTO.getSellUnit();
    this.storageUnit = productHistoryDTO.getStorageUnit();
    this.rate = productHistoryDTO.getRate();
    this.tradePrice = productHistoryDTO.getTradePrice();
    this.inventoryAveragePrice = productHistoryDTO.getInventoryAveragePrice();
    this.storageBin = productHistoryDTO.getStorageBin();
    this.commodityCode = productHistoryDTO.getCommodityCode();
    this.productKind = productHistoryDTO.getKindName();
    this.productKindId = productHistoryDTO.getKindId();
    this.setProductHistoryId(productHistoryDTO.getId());
  }

  public ImageCenterDTO getImageCenterDTO() {
    return imageCenterDTO;
  }

  public void setImageCenterDTO(ImageCenterDTO imageCenterDTO) {
    this.imageCenterDTO = imageCenterDTO;
  }

  public InStorageRecordDTO[] getInStorageRecordDTOs() {
    return inStorageRecordDTOs;
  }

  public void setInStorageRecordDTOs(InStorageRecordDTO[] inStorageRecordDTOs) {
    this.inStorageRecordDTOs = inStorageRecordDTOs;
  }

  /**
   * 设置productDTO中的sellUnit, storageUnit, rate信息
   * @param productDTO
   */
  public void setProductUnitRateInfo(ProductDTO productDTO){
    if(productDTO==null) return;
    this.setStorageUnit(productDTO.getStorageUnit());
    this.setSellUnit(productDTO.getSellUnit());
    this.setRate(productDTO.getRate());
    if(StringUtils.isBlank(getUnit()) && StringUtils.isNotBlank(productDTO.getSellUnit())){
      setUnit(productDTO.getSellUnit());
    }
  }

  public String getProductIdStr() {
    return productIdStr;
  }

  public void setProductIdStr(String productIdStr) {
    this.productIdStr = productIdStr;
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
    if (productId != null) {
      productIdStr = productId.toString();
    }else {
      productIdStr = null;
    }
  }

  public Long getProductHistoryId() {
    return productHistoryId;
  }

  public void setProductHistoryId(Long productHistoryId) {
    this.productHistoryId = productHistoryId;
    if (productHistoryId != null) {
      productHistoryIdStr = productHistoryId.toString();
    }else {
      productHistoryIdStr = null;
    }
  }

  public String getProductHistoryIdStr() {
    return productHistoryIdStr;
  }

  public void setProductHistoryIdStr(String productHistoryIdStr) {
    this.productHistoryIdStr = productHistoryIdStr;
  }

  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
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
    this.productInfo = sb.toString().trim();
    return this.productInfo;
  }

  public void setProductInfo(String productInfo) {
    this.productInfo = productInfo;
  }

  public String generateProductInfo() {
   return StringUtil.truncValue(this.getProductName())+ " " + StringUtil.truncValue(this.getBrand())+ " " + StringUtil.truncValue(this.getSpec())+ " " +
        StringUtil.truncValue(this.getModel())+ " " + StringUtil.truncValue(this.getVehicleModel())+ " " + StringUtil.truncValue(this.getVehicleBrand());
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

  public String getVehicleYear() {
    return vehicleYear;
  }

  public void setVehicleYear(String vehicleYear) {
    this.vehicleYear = vehicleYear;
  }

  public String getVehicleEngine() {
    return vehicleEngine;
  }

  public void setVehicleEngine(String vehicleEngine) {
    this.vehicleEngine = vehicleEngine;
  }

  public String getUnit() {
    return unit;
  }

  public Double getInventoryAveragePrice() {
    return inventoryAveragePrice;
  }

  public void setInventoryAveragePrice(Double inventoryAveragePrice) {
    this.inventoryAveragePrice = inventoryAveragePrice;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public String getStorageUnit() {
    return storageUnit;
  }

  public void setStorageUnit(String storageUnit) {
    this.storageUnit = storageUnit;
  }

  public String getSellUnit() {
    return sellUnit;
  }

  public void setSellUnit(String sellUnit) {
    this.sellUnit = sellUnit;
  }

  public Long getRate() {
    return rate;
  }

  public void setRate(Long rate) {
    this.rate = rate;
  }

	public String getCommodityCode() {
		return commodityCode;
	}

	public void setCommodityCode(String commodityCode) {
		if(StringUtils.isNotBlank(commodityCode)){
			this.commodityCode = commodityCode.trim().toUpperCase();
		}else {
			this.commodityCode = commodityCode;
		}
	}

	public boolean getCommodityCodeModifyFlag() {
		return commodityCodeModifyFlag;
	}

	public void setCommodityCodeModifyFlag(boolean commodityCodeModifyFlag) {
		this.commodityCodeModifyFlag = commodityCodeModifyFlag;
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

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public String getSpec() {
    return spec;
  }

  public void setSpec(String spec) {
    this.spec = spec;
  }
  public Double getTradePrice() {
		return tradePrice;
	}

	public void setTradePrice(Double tradePrice) {
		this.tradePrice = tradePrice;
	}

	public String getStorageBin() {
		return storageBin;
	}

	public void setStorageBin(String storageBin) {
		this.storageBin = storageBin;
	}

  public Double getAmountHid() {
    if(amountHid==null) return 0d;
    return amountHid;
  }

  public void setAmountHid(Double amountHid) {
    this.amountHid = amountHid;
  }

  public Double getAmount() {
    if(amount==null) return 0d;
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = NumberUtil.round(amount, 2);
    if (amount == null) {
      this.amountStr = "0";
      return;
    }
    this.amountStr = String.valueOf(this.amount.doubleValue()).split("\\.")[0];
  }

  public String getAmountStr() {
    return amountStr;
  }

  public void setAmountStr(String amountStr) {
    this.amountStr = amountStr;
  }

  public Double getReserved() {
    return reserved;
  }

  public void setReserved(Double reserved) {
    this.reserved = reserved;
  }

  public Double getReservedApprox() {
    if (reserved == null) return 0d;
    reservedApprox = NumberUtil.round(reserved, 2);
    return reservedApprox;
  }

  public void setReservedApprox(Double reservedApprox) {
    this.reservedApprox = reservedApprox;
  }


  public Double getInventoryAmount() {
    if(inventoryAmount==null){
      return 0D;
    }
    return inventoryAmount;
  }

  public void setInventoryAmount(Double inventoryAmount) {
    this.inventoryAmount = inventoryAmount;
  }

  public double getInventoryAmountApprox() {
    inventoryAmountApprox = NumberUtil.round(inventoryAmount, 2);
    return inventoryAmountApprox;
  }

  public void setInventoryAmountApprox(double inventoryAmountApprox) {
    this.inventoryAmountApprox = inventoryAmountApprox;
  }

  public OutStorageRelationDTO[] getOutStorageRelationDTOs() {
    return outStorageRelationDTOs;
  }

  public void setOutStorageRelationDTOs(OutStorageRelationDTO[] outStorageRelationDTOs) {
    this.outStorageRelationDTOs = outStorageRelationDTOs;
  }

  public Double getRemainAmount() {
    return remainAmount;
  }

  public void setRemainAmount(Double remainAmount) {
    this.remainAmount = remainAmount;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
    this.setIdStr(StringUtil.valueOf(id));
  }

  public Double getItemCostPrice() {
    return itemCostPrice;
  }

  public void setItemCostPrice(Double itemCostPrice) {
    this.itemCostPrice = itemCostPrice;
  }

  public Double getItemTotalCostPrice() {
    return itemTotalCostPrice;
  }

  public void setItemTotalCostPrice(Double itemTotalCostPrice) {
    this.itemTotalCostPrice = itemTotalCostPrice;
  }

  public boolean isAddVehicleInfoToSolr() {
    return isAddVehicleInfoToSolr;
  }

  public void setAddVehicleInfoToSolr(boolean addVehicleInfoToSolr) {
    isAddVehicleInfoToSolr = addVehicleInfoToSolr;
  }

  protected ItemIndexDTO toBcgogoItemIndexDTO() {
    ItemIndexDTO itemIndexDTO = new ItemIndexDTO();
    itemIndexDTO.setItemId(getId());
    itemIndexDTO.setItemName(getProductName());
    itemIndexDTO.setItemBrand(getBrand());
    itemIndexDTO.setItemSpec(getSpec());
    itemIndexDTO.setItemModel(getModel());
    itemIndexDTO.setVehicleBrand(getVehicleBrand());
    itemIndexDTO.setVehicleModel(getVehicleModel());

    itemIndexDTO.setCommodityCode(getCommodityCode());
    itemIndexDTO.setProductId(getProductId());
    itemIndexDTO.setItemCount(getAmount());
    itemIndexDTO.setProductKind(getProductKind());
    itemIndexDTO.setProductKindId(getProductKindId());
    itemIndexDTO.setUnit(getUnit());
    return itemIndexDTO;
  }

  protected void fromBcgogoItemIndexDTO(ItemIndexDTO itemIndexDTO){
    this.setProductId(itemIndexDTO.getProductId());
    this.setProductName(itemIndexDTO.getItemName());
    this.setBrand(itemIndexDTO.getItemBrand());
    this.setSpec(itemIndexDTO.getItemSpec());
    this.setModel(itemIndexDTO.getItemModel());
    this.setVehicleBrand(itemIndexDTO.getVehicleBrand());
    this.setVehicleModel(itemIndexDTO.getVehicleModel());

    this.setCommodityCode(itemIndexDTO.getCommodityCode());
    this.setProductId(itemIndexDTO.getProductId());
    this.setAmount(itemIndexDTO.getItemCount());
    this.setProductKind(itemIndexDTO.getProductKind());
    this.setProductKindId(itemIndexDTO.getProductKindId());
    this.setUnit(itemIndexDTO.getUnit());
    this.setId(itemIndexDTO.getItemId());
    if (itemIndexDTO.getItemId() != null) {
      this.setIdStr(String.valueOf(itemIndexDTO.getItemId()));
    }
  }


  public InStorageRecordDTO toInStorageRecordDTO(){
    InStorageRecordDTO inStorageRecordDTO = new InStorageRecordDTO();
    inStorageRecordDTO.setInStorageItemId(getId());
    inStorageRecordDTO.setProductId(getProductId());
    inStorageRecordDTO.setInStorageItemAmount(NumberUtil.doubleVal(getAmount()));
    inStorageRecordDTO.setInStorageUnit(getUnit());
    inStorageRecordDTO.setRemainAmount(getRemainAmount());
    return inStorageRecordDTO;
  }

  public Long getProductOriginId() {
    return productOriginId;
  }

  public void setProductOriginId(Long productOriginId) {
    this.productOriginId = productOriginId;
  }
  public String generateCustomMatchPContent(){
    StringBuffer sb = new StringBuffer();
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
    return sb.toString().trim();
  }

  public String generateCustomMatchPVContent(){
    StringBuffer sb = new StringBuffer();
    if(StringUtils.isNotBlank(this.getVehicleBrand())){
      sb.append(this.getVehicleBrand()).append(" ");
    }
    if(StringUtils.isNotBlank(this.getVehicleModel())){
      sb.append(this.getVehicleModel()).append(" ");
    }

    return sb.toString().trim();
  }
  @Override
  public String toString() {
    return "BcgogoOrderItemDto{" +
        "id=" + id +
        ", productId=" + productId +
        ", productIdStr='" + productIdStr + '\'' +
        ", productHistoryId=" + productHistoryId +
        ", productHistoryIdStr='" + productHistoryIdStr + '\'' +
        ", brand='" + brand + '\'' +
        ", productName='" + productName + '\'' +
        ", productKindId=" + productKindId +
        ", productKind='" + productKind + '\'' +
        ", vehicleBrand='" + vehicleBrand + '\'' +
        ", vehicleModel='" + vehicleModel + '\'' +
        ", vehicleYear='" + vehicleYear + '\'' +
        ", vehicleEngine='" + vehicleEngine + '\'' +
        ", outStorageRelationDTOs=" + (outStorageRelationDTOs == null ? null : Arrays.asList(outStorageRelationDTOs)) +
        ", inStorageRecordDTOs=" + (inStorageRecordDTOs == null ? null : Arrays.asList(inStorageRecordDTOs)) +
        ", unit='" + unit + '\'' +
        ", storageUnit='" + storageUnit + '\'' +
        ", sellUnit='" + sellUnit + '\'' +
        ", rate=" + rate +
        ", commodityCode='" + commodityCode + '\'' +
        ", commodityCodeModifyFlag=" + commodityCodeModifyFlag +
        ", model='" + model + '\'' +
        ", spec='" + spec + '\'' +
        ", inventoryAveragePrice=" + inventoryAveragePrice +
        ", tradePrice=" + tradePrice +
        ", storageBin='" + storageBin + '\'' +
        ", amount=" + amount +
        ", amountHid=" + amountHid +
        ", amountStr='" + amountStr + '\'' +
        ", reserved=" + reserved +
        ", reservedApprox=" + reservedApprox +
        ", inventoryAmount=" + inventoryAmount +
        ", inventoryAmountApprox=" + inventoryAmountApprox +
        ", remainAmount=" + remainAmount +
        ", itemCostPrice=" + itemCostPrice +
        ", itemTotalCostPrice=" + itemTotalCostPrice +
        '}';
  }
}
