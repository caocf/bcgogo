package com.bcgogo.enums;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-4-12
 * Time: 上午10:38
 * To change this template use File | Settings | File Templates.
 */
public enum OutStorageSupplierType {
  IMPORT_PRODUCT_SUPPLIER("导入商品供应商"),
  NATIVE_SUPPLIER("本店供应商"),
  UNDEFINED_SUPPLIER("系统默认供应商");
  private String name;

  private OutStorageSupplierType(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }
}
