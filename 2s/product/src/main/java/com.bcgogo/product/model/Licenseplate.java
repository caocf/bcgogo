package com.bcgogo.product.model;

import com.bcgogo.model.LongIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: Rex
 * Date: 12-1-9
 * Time: 下午5:03
 * todo need migrate to config zjt
 */
@Entity
@Table(name = "licenseplate")
public class Licenseplate extends LongIdentifier {
  private String carno;
  private String areaName;
  private Long areaNo;
  private String areaFirstname;
  private String areaFirstcarno;

  @Column(name = "carno", length = 2)
  public String getCarno() {
    return carno;
  }

  public void setCarno(String carno) {
    this.carno = carno;
  }

  @Column(name = "area_name", length = 20)
  public String getAreaName() {
    return areaName;
  }

  public void setAreaName(String areaName) {
    this.areaName = areaName;
  }

  @Column(name = "area_firstname", length = 1)
  public String getAreaFirstname() {
    return areaFirstname;
  }

  public void setAreaFirstname(String areaFirstname) {
    this.areaFirstname = areaFirstname;
  }

  @Column(name = "area_firstcarno", length = 1)
  public String getAreaFirstcarno() {
    return areaFirstcarno;
  }

  public void setAreaFirstcarno(String areaFirstcarno) {
    this.areaFirstcarno = areaFirstcarno;
  }

  @Column(name = "area_no")
  public Long getAreaNo() {
    return areaNo;
  }

  public void setAreaNo(Long areaNo) {
    this.areaNo = areaNo;
  }
}
