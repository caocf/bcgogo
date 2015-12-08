package com.bcgogo.config.model;

import com.bcgogo.config.dto.juhe.JuheCityOilPriceDTO;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 14-2-7
 * Time: 下午4:53
 */
@Entity
@Table(name = "juhe_city_oil_price")
public class JuheCityOilPrice extends LongIdentifier {
  private String provinceName;
  private String provinceAreaNo;
  private Double e90;
  private Double e93;
  private Double e97;
  private Double e0;
  private String areaFirstCarNo;

  public JuheCityOilPriceDTO toDTO() {
    JuheCityOilPriceDTO dto = new JuheCityOilPriceDTO();
    dto.setProvinceName(getProvinceName());
    dto.setProvinceAreaNo(getProvinceAreaNo());
    dto.setAreaFirstCarNo(getAreaFirstCarNo());
    dto.setE0(getE0());
    dto.setE90(getE90());
    dto.setE93(getE93());
    dto.setE97(getE97());
    return dto;
  }

  @Column(name = "province_name")
  public String getProvinceName() {
    return provinceName;
  }

  public void setProvinceName(String provinceName) {
    this.provinceName = provinceName;
  }

  @Column(name = "province_area_no")
  public String getProvinceAreaNo() {
    return provinceAreaNo;
  }

  public void setProvinceAreaNo(String provinceAreaNo) {
    this.provinceAreaNo = provinceAreaNo;
  }

  @Column(name = "e90")
  public Double getE90() {
    return e90;
  }

  public void setE90(Double e90) {
    this.e90 = e90;
  }

  @Column(name = "e93")
  public Double getE93() {
    return e93;
  }

  public void setE93(Double e93) {
    this.e93 = e93;
  }

  @Column(name = "e97")
  public Double getE97() {
    return e97;
  }

  public void setE97(Double e97) {
    this.e97 = e97;
  }

  @Column(name = "e0")
  public Double getE0() {
    return e0;
  }

  public void setE0(Double e0) {
    this.e0 = e0;
  }

  @Column(name = "area_first_car_no")
  public String getAreaFirstCarNo() {
    return areaFirstCarNo;
  }

  public void setAreaFirstCarNo(String areaFirstCarNo) {
    this.areaFirstCarNo = areaFirstCarNo;
  }


}
