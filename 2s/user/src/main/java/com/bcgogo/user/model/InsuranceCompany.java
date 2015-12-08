package com.bcgogo.user.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.user.dto.InsuranceCompanyDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-1-7
 * Time: 下午8:23
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "insurance_company")
public class InsuranceCompany extends LongIdentifier {
  private String name;
  private Long sort;
  private String mobile;

  @Column(name = "mobile")
  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public InsuranceCompanyDTO toDTO() {
    InsuranceCompanyDTO insuranceCompanyDTO = new InsuranceCompanyDTO();
    insuranceCompanyDTO.setId(getId());
    insuranceCompanyDTO.setName(getName());
    insuranceCompanyDTO.setSort(getSort());
    insuranceCompanyDTO.setMobile(getMobile());
    return insuranceCompanyDTO;
  }

  @Column(name = "name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "sort")
  public Long getSort() {
    return sort;
  }

  public void setSort(Long sort) {
    this.sort = sort;
  }
}
