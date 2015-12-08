package com.bcgogo.wx.user;

import com.bcgogo.user.dto.InsuranceCompanyDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Author: zj
 * Date: 2015-5-14
 * Time: 17:45
 */
public class OneKeyRescueDTO {
  private String appUserNo;
  private String vehicleNo;//车牌号
  private List<InsuranceCompanyDTO> insuranceCompanyDTOs = new ArrayList<InsuranceCompanyDTO>(); //所有保险公司信息
  private InsuranceCompanyDTO insuranceCompanyDTO = new InsuranceCompanyDTO();  //排在第一位的保险公司
  private List<String> accident_mobile;    //事故专员电话
  private String mirror_mobile;            //后视镜问题电话

  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  public String getVehicleNo() {
    return vehicleNo;
  }

  public void setVehicleNo(String vehicleNo) {
    this.vehicleNo = vehicleNo;
  }

  public List<InsuranceCompanyDTO> getInsuranceCompanyDTOs() {
    return insuranceCompanyDTOs;
  }

  public void setInsuranceCompanyDTOs(List<InsuranceCompanyDTO> insuranceCompanyDTOs) {
    this.insuranceCompanyDTOs = insuranceCompanyDTOs;
  }

  public InsuranceCompanyDTO getInsuranceCompanyDTO() {
    return insuranceCompanyDTO;
  }

  public void setInsuranceCompanyDTO(InsuranceCompanyDTO insuranceCompanyDTO) {
    this.insuranceCompanyDTO = insuranceCompanyDTO;
  }

  public List<String> getAccident_mobile() {
    return accident_mobile;
  }

  public void setAccident_mobile(List<String> accident_mobile) {
    this.accident_mobile = accident_mobile;
  }

  public String getMirror_mobile() {
    return mirror_mobile;
  }

  public void setMirror_mobile(String mirror_mobile) {
    this.mirror_mobile = mirror_mobile;
  }
}
