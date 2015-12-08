package com.bcgogo.txn.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.stat.dto.PurchaseInventoryStatDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 入库月统计表（采购成本统计）
 * User: Jimuchen
 * Date: 12-10-30
 * Time: 下午4:18
 */
@Entity
@Table(name="purchase_inventory_month_stat")
public class PurchaseInventoryMonthStat extends LongIdentifier {
  private Long shopId;
  private Integer statYear;
  private Integer statMonth;
  private String productName;
  private String productBrand;
  private String vehicleModel;
  private String vehicleBrand;
  private double amount;    //数量
  private int times;        //次数
  private double total;     //总额

  @Column(name="shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name="stat_year")
  public Integer getStatYear() {
    return statYear;
  }

  public void setStatYear(Integer statYear) {
    this.statYear = statYear;
  }

  @Column(name="stat_month")
  public Integer getStatMonth() {
    return statMonth;
  }

  public void setStatMonth(Integer statMonth) {
    this.statMonth = statMonth;
  }

  @Column(name="product_name")
  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  @Column(name="product_brand")
  public String getProductBrand() {
    return productBrand;
  }

  public void setProductBrand(String productBrand) {
    this.productBrand = productBrand;
  }

  @Column(name="vehicle_model")
  public String getVehicleModel() {
    return vehicleModel;
  }

  public void setVehicleModel(String vehicleModel) {
    this.vehicleModel = vehicleModel;
  }

  @Column(name="vehicle_brand")
  public String getVehicleBrand() {
    return vehicleBrand;
  }

  public void setVehicleBrand(String vehicleBrand) {
    this.vehicleBrand = vehicleBrand;
  }

  @Column(name="amount")
  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }

  @Column(name="times")
  public int getTimes() {
    return times;
  }

  public void setTimes(int times) {
    this.times = times;
  }

  @Column(name="total")
  public double getTotal() {
    return total;
  }

  public void setTotal(double total) {
    this.total = total;
  }

  public PurchaseInventoryStatDTO toDTO(){
    PurchaseInventoryStatDTO statDTO = new PurchaseInventoryStatDTO();
    statDTO.setProductBrand(getProductBrand());
    statDTO.setProductName(getProductName());
    statDTO.setVehicleBrand(getVehicleBrand());
    statDTO.setVehicleModel(getVehicleModel());
    statDTO.setAmount(getAmount());
    statDTO.setTotal(getTotal());
    return statDTO;
  }
}
