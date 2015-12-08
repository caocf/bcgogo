package com.bcgogo.enums;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 12-11-1
 * Time: 上午11:30
 */
public enum ProductModifyFields {
  commodityCode(ProductModifyTables.PRODUCT),//商品编码
  name(ProductModifyTables.PRODUCT),
  brand(ProductModifyTables.PRODUCT),
  spec(ProductModifyTables.PRODUCT),  //规格
  model(ProductModifyTables.PRODUCT),   //型号
  storageBin(ProductModifyTables.PRODUCT_LOCAL_INFO),// 仓位
  storageUnit(ProductModifyTables.PRODUCT_LOCAL_INFO),
  sellUnit(ProductModifyTables.PRODUCT_LOCAL_INFO),
  rate(ProductModifyTables.PRODUCT_LOCAL_INFO),
  productVehicleBrand(ProductModifyTables.PRODUCT),
  productVehicleModel(ProductModifyTables.PRODUCT),
  lowerLimit(ProductModifyTables.INVENTORY),
  upperLimit(ProductModifyTables.INVENTORY),
  salesPrice(ProductModifyTables.INVENTORY),    //销售价
  tradePrice(ProductModifyTables.PRODUCT_LOCAL_INFO),   //批发价
  kindId(ProductModifyTables.PRODUCT),  //商品分类
  amount(ProductModifyTables.INVENTORY),
  inventoryAveragePrice(ProductModifyTables.INVENTORY); //库存平均价

  private ProductModifyTables productModifyTables;
  private ProductModifyFields(ProductModifyTables productModifyTables){
    this.productModifyTables = productModifyTables;
  }

  public ProductModifyTables getTable(){
    return productModifyTables;
  }


  
}
