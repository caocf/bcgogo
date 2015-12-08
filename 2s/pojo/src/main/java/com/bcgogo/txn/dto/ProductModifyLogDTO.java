package com.bcgogo.txn.dto;

import com.bcgogo.enums.Product.ProductRelevanceStatus;
import com.bcgogo.enums.ProductModifyFields;
import com.bcgogo.enums.ProductModifyOperations;
import com.bcgogo.enums.ProductModifyTables;
import com.bcgogo.enums.StatProcessStatus;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.dto.ProductLocalInfoDTO;
import com.bcgogo.utils.ArrayUtil;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 12-11-1
 * Time: 上午11:31
 */
public class ProductModifyLogDTO {
  private Long id;
  private Long productId;
	private Long shopId;
  private Long userId;
  private Long operationId;
	private ProductModifyOperations operationType;
  private ProductModifyFields fieldName;
  private ProductModifyTables tableName;
  private String oldValue;
  private String newValue;
  private StatProcessStatus statProcessStatus;
  private Long creationDate;

  private String commodityCode;//商品编码
  private String name;
  private String brand;
  private String spec;  //规格
  private String model;   //型号
  private String storageBin;// 仓位
  //修改单位逻辑：没单位时新増单位：同时保存到product_local_info里的两个单位，并保存到inventory的unit中
  //新増小单位时，保存到product_local_info的sell_unit中，并保存到inventory的unit中
  private String storageUnit;
  private String sellUnit;
  private Long rate;
  private String productVehicleBrand;
  private String productVehicleModel;
  private Double lowerLimit;
  private Double upperLimit;
  private Double salesPrice;    //销售价
  private Double tradePrice;   //批发价
  private Long kindId;  //商品分类
  private Double amount;
  private Double inventoryAveragePrice; //库存平均价

  //以下8属性专为成本统计变动监听使用
  private String oldName;
  private String newName;
  private String oldBrand;
  private String newBrand;
  private String oldProductVehicleBrand;
  private String newProductVehicleBrand;
  private String oldProductVehicleModel;
  private String newProductVehicleModel;

  private ProductRelevanceStatus relevanceStatus; //商品标准化那边专用



  public static List<ProductModifyLogDTO> compare(ProductModifyLogDTO oldLog, ProductModifyLogDTO newLog){
    List<ProductModifyLogDTO> list = new ArrayList<ProductModifyLogDTO>();
    if(!StringUtil.compareSame(oldLog.getName(), newLog.getName()) || !StringUtil.compareSame(oldLog.getBrand(), newLog.getBrand())
        || !StringUtil.compareSame(oldLog.getProductVehicleBrand(), newLog.getProductVehicleBrand())
        || !StringUtil.compareSame(oldLog.getProductVehicleModel(), newLog.getProductVehicleModel())){
      //成本监听用四属性必须全部Log
      ProductModifyLogDTO baseDto = new ProductModifyLogDTO();
      baseDto.setFieldName(ProductModifyFields.name);
      baseDto.setTableName(ProductModifyFields.name.getTable());
      baseDto.setOldValue(oldLog.getName());
      baseDto.setNewValue(newLog.getName());
      list.add(baseDto);
      baseDto = new ProductModifyLogDTO();
      baseDto.setFieldName(ProductModifyFields.brand);
      baseDto.setTableName(ProductModifyFields.brand.getTable());
      baseDto.setOldValue(oldLog.getBrand());
      baseDto.setNewValue(newLog.getBrand());
      list.add(baseDto);
      baseDto = new ProductModifyLogDTO();
      baseDto.setFieldName(ProductModifyFields.productVehicleBrand);
      baseDto.setTableName(ProductModifyFields.productVehicleBrand.getTable());
      baseDto.setOldValue(oldLog.getProductVehicleBrand());
      baseDto.setNewValue(newLog.getProductVehicleBrand());
      list.add(baseDto);
      baseDto = new ProductModifyLogDTO();
      baseDto.setFieldName(ProductModifyFields.productVehicleModel);
      baseDto.setTableName(ProductModifyFields.productVehicleModel.getTable());
      baseDto.setOldValue(oldLog.getProductVehicleModel());
      baseDto.setNewValue(newLog.getProductVehicleModel());
      list.add(baseDto);
    }
    if(!StringUtil.compareSame(oldLog.getCommodityCode(), newLog.getCommodityCode())){
      ProductModifyLogDTO dto = new ProductModifyLogDTO();
      dto.setFieldName(ProductModifyFields.commodityCode);
      dto.setTableName(ProductModifyFields.commodityCode.getTable());
      dto.setOldValue(oldLog.getCommodityCode());
      dto.setNewValue(newLog.getCommodityCode());
      list.add(dto);
    }
    if(!StringUtil.compareSame(oldLog.getSpec(), newLog.getSpec())){
      ProductModifyLogDTO dto = new ProductModifyLogDTO();
      dto.setFieldName(ProductModifyFields.spec);
      dto.setTableName(ProductModifyFields.spec.getTable());
      dto.setOldValue(oldLog.getSpec());
      dto.setNewValue(newLog.getSpec());
      list.add(dto);
    }
    if(!StringUtil.compareSame(oldLog.getModel(), newLog.getModel())){
      ProductModifyLogDTO dto = new ProductModifyLogDTO();
      dto.setFieldName(ProductModifyFields.model);
      dto.setTableName(ProductModifyFields.model.getTable());
      dto.setOldValue(oldLog.getModel());
      dto.setNewValue(newLog.getModel());
      list.add(dto);
    }
    if(!StringUtil.compareSame(oldLog.getStorageBin(), newLog.getStorageBin())){
      ProductModifyLogDTO dto = new ProductModifyLogDTO();
      dto.setFieldName(ProductModifyFields.storageBin);
      dto.setTableName(ProductModifyFields.storageBin.getTable());
      dto.setOldValue(oldLog.getStorageBin());
      dto.setNewValue(newLog.getStorageBin());
      list.add(dto);
    }
    if(!StringUtil.compareSame(oldLog.getStorageUnit(), newLog.getStorageUnit())){
      ProductModifyLogDTO dto = new ProductModifyLogDTO();
      dto.setFieldName(ProductModifyFields.storageUnit);
      dto.setTableName(ProductModifyFields.storageUnit.getTable());
      dto.setOldValue(oldLog.getStorageUnit());
      dto.setNewValue(newLog.getStorageUnit());
      list.add(dto);
    }
    if(!StringUtil.compareSame(oldLog.getSellUnit(), newLog.getSellUnit())){
      ProductModifyLogDTO dto = new ProductModifyLogDTO();
      dto.setFieldName(ProductModifyFields.sellUnit);
      dto.setTableName(ProductModifyFields.sellUnit.getTable());
      dto.setOldValue(oldLog.getSellUnit());
      dto.setNewValue(newLog.getSellUnit());
      list.add(dto);
    }
    if(!NumberUtil.compare(oldLog.getRate(), newLog.getRate())){
      ProductModifyLogDTO dto = new ProductModifyLogDTO();
      dto.setFieldName(ProductModifyFields.rate);
      dto.setTableName(ProductModifyFields.rate.getTable());
      dto.setOldValue(StringUtil.longToString(oldLog.getRate(), null));
      dto.setNewValue(StringUtil.longToString(newLog.getRate(), null));
      list.add(dto);
    }
    if(!NumberUtil.compareDouble(oldLog.getLowerLimit(), newLog.getLowerLimit())){
      ProductModifyLogDTO dto = new ProductModifyLogDTO();
      dto.setFieldName(ProductModifyFields.lowerLimit);
      dto.setTableName(ProductModifyFields.lowerLimit.getTable());
      dto.setOldValue(StringUtil.doubleToString(oldLog.getLowerLimit(), 2, null));
      dto.setNewValue(StringUtil.doubleToString(newLog.getLowerLimit(), 2, null));
      list.add(dto);
    }
    if(!NumberUtil.compareDouble(oldLog.getUpperLimit(), newLog.getUpperLimit())){
      ProductModifyLogDTO dto = new ProductModifyLogDTO();
      dto.setFieldName(ProductModifyFields.upperLimit);
      dto.setTableName(ProductModifyFields.upperLimit.getTable());
      dto.setOldValue(StringUtil.doubleToString(oldLog.getUpperLimit(), 2, null));
      dto.setNewValue(StringUtil.doubleToString(newLog.getUpperLimit(), 2, null));
      list.add(dto);
    }
    if(!NumberUtil.compareDouble(oldLog.getSalesPrice(), newLog.getSalesPrice())){
      ProductModifyLogDTO dto = new ProductModifyLogDTO();
      dto.setFieldName(ProductModifyFields.salesPrice);
      dto.setTableName(ProductModifyFields.salesPrice.getTable());
      dto.setOldValue(StringUtil.doubleToString(oldLog.getSalesPrice(), 2, null));
      dto.setNewValue(StringUtil.doubleToString(newLog.getSalesPrice(), 2, null));
      list.add(dto);
    }
    if(!NumberUtil.compareDouble(oldLog.getTradePrice(), newLog.getTradePrice())){
      ProductModifyLogDTO dto = new ProductModifyLogDTO();
      dto.setFieldName(ProductModifyFields.tradePrice);
      dto.setTableName(ProductModifyFields.tradePrice.getTable());
      dto.setOldValue(StringUtil.doubleToString(oldLog.getTradePrice(), 2, null));
      dto.setNewValue(StringUtil.doubleToString(newLog.getTradePrice(), 2, null));
      list.add(dto);
    }
    if(!NumberUtil.compare(oldLog.getKindId(), newLog.getKindId())){
      ProductModifyLogDTO dto = new ProductModifyLogDTO();
      dto.setFieldName(ProductModifyFields.kindId);
      dto.setTableName(ProductModifyFields.kindId.getTable());
      dto.setOldValue(StringUtil.longToString(oldLog.getKindId(), null));
      dto.setNewValue(StringUtil.longToString(newLog.getKindId(), null));
      list.add(dto);
    }
    if(!NumberUtil.compareDouble(oldLog.getAmount(), newLog.getAmount())){
      ProductModifyLogDTO dto = new ProductModifyLogDTO();
      dto.setFieldName(ProductModifyFields.amount);
      dto.setTableName(ProductModifyFields.amount.getTable());
      dto.setOldValue(StringUtil.doubleToString(oldLog.getAmount(), 2, null));
      dto.setNewValue(StringUtil.doubleToString(newLog.getAmount(), 2, null));
      list.add(dto);
    }
    if(!NumberUtil.compareDouble(oldLog.getInventoryAveragePrice(), newLog.getInventoryAveragePrice())){
      ProductModifyLogDTO dto = new ProductModifyLogDTO();
      dto.setFieldName(ProductModifyFields.inventoryAveragePrice);
      dto.setTableName(ProductModifyFields.inventoryAveragePrice.getTable());
      dto.setOldValue(StringUtil.doubleToString(oldLog.getInventoryAveragePrice(), 2, null));
      dto.setNewValue(StringUtil.doubleToString(newLog.getInventoryAveragePrice(), 2, null));
      list.add(dto);
    }
    return list;
  }

  public void setProduct(ProductDTO productDTO){
    setCommodityCode(productDTO.getCommodityCode());
    setName(productDTO.getName());
    setBrand(productDTO.getBrand());
    setSpec(productDTO.getSpec());
    setModel(productDTO.getModel());
    setProductVehicleBrand(productDTO.getProductVehicleBrand());
    setProductVehicleModel(productDTO.getProductVehicleModel());
    setKindId(productDTO.getKindId());
  }

  public void setProductLocalInfo(ProductLocalInfoDTO productLocalInfoDTO){
    setStorageBin(productLocalInfoDTO.getStorageBin());
    setStorageUnit(productLocalInfoDTO.getStorageUnit());
    setSellUnit(productLocalInfoDTO.getSellUnit());
    setRate(productLocalInfoDTO.getRate());
    setTradePrice(productLocalInfoDTO.getTradePrice());
  }

  public void setInventory(InventoryDTO inventoryDTO) {
    if (inventoryDTO != null) {
      setLowerLimit(inventoryDTO.getLowerLimit());
      setUpperLimit(inventoryDTO.getUpperLimit());
      setSalesPrice(inventoryDTO.getSalesPrice());
      setAmount(inventoryDTO.getAmount());
      setInventoryAveragePrice(inventoryDTO.getInventoryAveragePrice());
    }
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public Long getOperationId() {
    return operationId;
  }

  public void setOperationId(Long operationId) {
    this.operationId = operationId;
  }

  public ProductModifyOperations getOperationType() {
    return operationType;
  }

  public void setOperationType(ProductModifyOperations operationType) {
    this.operationType = operationType;
  }

  public ProductModifyFields getFieldName() {
    return fieldName;
  }

  public void setFieldName(ProductModifyFields fieldName) {
    this.fieldName = fieldName;
  }

  public ProductModifyTables getTableName() {
    return tableName;
  }

  public void setTableName(ProductModifyTables tableName) {
    this.tableName = tableName;
  }

  public String getOldValue() {
    return oldValue;
  }

  public void setOldValue(String oldValue) {
    this.oldValue = oldValue;
  }

  public String getNewValue() {
    return newValue;
  }

  public void setNewValue(String newValue) {
    this.newValue = newValue;
  }

  public StatProcessStatus getStatProcessStatus() {
    return statProcessStatus;
  }

  public void setStatProcessStatus(StatProcessStatus statProcessStatus) {
    this.statProcessStatus = statProcessStatus;
  }

  public Long getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Long creationDate) {
    this.creationDate = creationDate;
  }

  public String getCommodityCode() {
    return commodityCode;
  }

  public void setCommodityCode(String commodityCode) {
    this.commodityCode = commodityCode;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  public String getSpec() {
    return spec;
  }

  public void setSpec(String spec) {
    this.spec = spec;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public String getStorageBin() {
    return storageBin;
  }

  public void setStorageBin(String storageBin) {
    this.storageBin = storageBin;
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

  public String getProductVehicleBrand() {
    return productVehicleBrand;
  }

  public void setProductVehicleBrand(String productVehicleBrand) {
    this.productVehicleBrand = productVehicleBrand;
  }

  public String getProductVehicleModel() {
    return productVehicleModel;
  }

  public void setProductVehicleModel(String productVehicleModel) {
    this.productVehicleModel = productVehicleModel;
  }

  public Double getLowerLimit() {
    return lowerLimit;
  }

  public void setLowerLimit(Double lowerLimit) {
    this.lowerLimit = lowerLimit;
  }

  public Double getUpperLimit() {
    return upperLimit;
  }

  public void setUpperLimit(Double upperLimit) {
    this.upperLimit = upperLimit;
  }

  public Double getSalesPrice() {
    return salesPrice;
  }

  public void setSalesPrice(Double salesPrice) {
    this.salesPrice = salesPrice;
  }

  public Double getTradePrice() {
    return tradePrice;
  }

  public void setTradePrice(Double tradePrice) {
    this.tradePrice = tradePrice;
  }

  public Long getKindId() {
    return kindId;
  }

  public void setKindId(Long kindId) {
    this.kindId = kindId;
  }

  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  public Double getInventoryAveragePrice() {
    return inventoryAveragePrice;
  }

  public void setInventoryAveragePrice(Double inventoryAveragePrice) {
    this.inventoryAveragePrice = inventoryAveragePrice;
  }

  public String getOldName() {
    return oldName;
  }

  public void setOldName(String oldName) {
    this.oldName = oldName;
  }

  public String getNewName() {
    return newName;
  }

  public void setNewName(String newName) {
    this.newName = newName;
  }

  public String getOldBrand() {
    return oldBrand;
  }

  public void setOldBrand(String oldBrand) {
    this.oldBrand = oldBrand;
  }

  public String getNewBrand() {
    return newBrand;
  }

  public void setNewBrand(String newBrand) {
    this.newBrand = newBrand;
  }

  public String getOldProductVehicleBrand() {
    return oldProductVehicleBrand;
  }

  public void setOldProductVehicleBrand(String oldProductVehicleBrand) {
    this.oldProductVehicleBrand = oldProductVehicleBrand;
  }

  public String getNewProductVehicleBrand() {
    return newProductVehicleBrand;
  }

  public void setNewProductVehicleBrand(String newProductVehicleBrand) {
    this.newProductVehicleBrand = newProductVehicleBrand;
  }

  public String getOldProductVehicleModel() {
    return oldProductVehicleModel;
  }

  public void setOldProductVehicleModel(String oldProductVehicleModel) {
    this.oldProductVehicleModel = oldProductVehicleModel;
  }

  public String getNewProductVehicleModel() {
    return newProductVehicleModel;
  }

  public void setNewProductVehicleModel(String newProductVehicleModel) {
    this.newProductVehicleModel = newProductVehicleModel;
  }

  public ProductRelevanceStatus getRelevanceStatus() {
    return relevanceStatus;
  }

  public void setRelevanceStatus(ProductRelevanceStatus relevanceStatus) {
    this.relevanceStatus = relevanceStatus;
  }
}
