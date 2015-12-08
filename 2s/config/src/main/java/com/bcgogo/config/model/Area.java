package com.bcgogo.config.model;

import com.bcgogo.config.dto.AreaDTO;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: MZDong
 * Date: 11-11-18
 * Time: 下午5:41
 */
@Entity
@Table(name = "area")
public class Area extends LongIdentifier {
  private String name;
  private Long no;
  private Long parentNo;
  //百度cityCode
  private Integer cityCode;
  private String juheCityCode;

  public Area() {
  }

  public AreaDTO toDTO() {
    AreaDTO areaDTO = new AreaDTO();
    areaDTO.setName(this.getName());
    areaDTO.setNo(this.getNo());
    areaDTO.setParentNo(this.getParentNo());
    areaDTO.setCityCode(getCityCode());
    areaDTO.setJuheCityCode(getJuheCityCode());
    return areaDTO;
  }

  public void fromDTO(AreaDTO areaDTO) {
    this.setName(areaDTO.getName());
    this.setNo(areaDTO.getNo());
    this.setParentNo(areaDTO.getParentNo());
    this.setCityCode(areaDTO.getCityCode());
    this.setJuheCityCode(areaDTO.getJuheCityCode());
  }


  @Column(name = "name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "no")
  public Long getNo() {
    return no;
  }

  public void setNo(Long no) {
    this.no = no;
  }

  @Column(name = "parent_no")
  public Long getParentNo() {
    return parentNo;
  }

  public void setParentNo(Long parentNo) {
    this.parentNo = parentNo;
  }

  @Column(name = "city_code")
  public Integer getCityCode() {
    return cityCode;
  }

  public void setCityCode(Integer cityCode) {
    this.cityCode = cityCode;
  }

  @Column(name = "juhe_city_code")
  public String getJuheCityCode() {
    return juheCityCode;
  }

  public void setJuheCityCode(String juheCityCode) {
    this.juheCityCode = juheCityCode;
  }

}
