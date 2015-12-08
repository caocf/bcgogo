package com.bcgogo.user.dto;

import com.bcgogo.api.ObdDTO;
import com.bcgogo.api.ObdSimBindDTO;
import com.bcgogo.api.ObdSimDTO;
import com.bcgogo.config.dto.AreaDTO;
import com.bcgogo.product.dto.ProductDTO;
import org.omg.CORBA.PRIVATE_MEMBER;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 13-2-19
 * Time: 上午11:32
 */
public class ValidateImportDataDTO {
  private Map<String,CustomerDTO> mobileCustomerMap;

  private Map<String,CustomerDTO> landLineCustomerMap;

  private Map<String,VehicleDTO> vehicleDTOMap;

  private Map<String,MemberDTO> memberDTOMap;

  private Map<String,SupplierDTO> mobileSupplierMap;

  private Map<String,SupplierDTO> landLineSupplierMap;

  private Map<String,ProductDTO> comProductMap;

  private Map<String,AreaDTO> areaDTOMap;

  private Map<String,ObdDTO> imeiObdDTOMap;

  private Map<String,ObdSimDTO> mobileObdSimDTOMap;

  private Map<String,ObdSimDTO> mobileNoObdSimDTOMap;

  public Map<String, AreaDTO> getAreaDTOMap() {
    return areaDTOMap;
  }

  public void setAreaDTOMap(Map<String, AreaDTO> areaDTOMap) {
    this.areaDTOMap = areaDTOMap;
  }

  public Map<String, CustomerDTO> getMobileCustomerMap() {
    return mobileCustomerMap;
  }

  public void setMobileCustomerMap(Map<String, CustomerDTO> mobileCustomerMap) {
    this.mobileCustomerMap = mobileCustomerMap;
  }

  public Map<String, VehicleDTO> getVehicleDTOMap() {
    return vehicleDTOMap;
  }

  public void setVehicleDTOMap(Map<String, VehicleDTO> vehicleDTOMap) {
    this.vehicleDTOMap = vehicleDTOMap;
  }

  public Map<String, MemberDTO> getMemberDTOMap() {
    return memberDTOMap;
  }

  public void setMemberDTOMap(Map<String, MemberDTO> memberDTOMap) {
    this.memberDTOMap = memberDTOMap;
  }

  public Map<String, SupplierDTO> getMobileSupplierMap() {
    return mobileSupplierMap;
  }

  public void setMobileSupplierMap(Map<String, SupplierDTO> mobileSupplierMap) {
    this.mobileSupplierMap = mobileSupplierMap;
  }

  public Map<String, ProductDTO> getComProductMap() {
    return comProductMap;
  }

  public void setComProductMap(Map<String, ProductDTO> comProductMap) {
    this.comProductMap = comProductMap;
  }

  public Map<String, CustomerDTO> getLandLineCustomerMap() {
    return landLineCustomerMap;
  }

  public void setLandLineCustomerMap(Map<String, CustomerDTO> landLineCustomerMap) {
    this.landLineCustomerMap = landLineCustomerMap;
  }

  public Map<String, SupplierDTO> getLandLineSupplierMap() {
    return landLineSupplierMap;
  }

  public void setLandLineSupplierMap(Map<String, SupplierDTO> landLineSupplierMap) {
    this.landLineSupplierMap = landLineSupplierMap;
  }

  public Map<String, ObdDTO> getImeiObdDTOMap() {
    return imeiObdDTOMap;
  }

  public void setImeiObdDTOMap(Map<String, ObdDTO> imeiObdDTOMap) {
    this.imeiObdDTOMap = imeiObdDTOMap;
  }

  public Map<String, ObdSimDTO> getMobileObdSimDTOMap() {
    return mobileObdSimDTOMap;
  }

  public void setMobileObdSimDTOMap(Map<String, ObdSimDTO> mobileObdSimDTOMap) {
    this.mobileObdSimDTOMap = mobileObdSimDTOMap;
  }

  public Map<String, ObdSimDTO> getMobileNoObdSimDTOMap() {
    return mobileNoObdSimDTOMap;
  }

  public void setMobileNoObdSimDTOMap(Map<String, ObdSimDTO> mobileNoObdSimDTOMap) {
    this.mobileNoObdSimDTOMap = mobileNoObdSimDTOMap;
  }
}
