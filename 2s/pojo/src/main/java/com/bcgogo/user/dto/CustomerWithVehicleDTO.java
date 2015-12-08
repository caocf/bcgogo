package com.bcgogo.user.dto;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-4-6
 * Time: 上午8:15
 * To change this template use File | Settings | File Templates.
 */
public class CustomerWithVehicleDTO {

  private CustomerDTO customerDTO;
  private VehicleDTO vehicleDTO;
  private CustomerRecordDTO customerRecordDTO;
  private MemberDTO memberDTO;
  private List<VehicleDTO> vehicleDTOList;
  public void setCustomerRecordDTO(CustomerRecordDTO customerRecordDTO) {
    this.customerRecordDTO = customerRecordDTO;
  }

  public CustomerRecordDTO getCustomerRecordDTO() {
    return customerRecordDTO;
  }

  public CustomerDTO getCustomerDTO() {
    return customerDTO;
  }

  public VehicleDTO getVehicleDTO() {
    return vehicleDTO;
  }

  public void setCustomerDTO(CustomerDTO customerDTO) {
    this.customerDTO = customerDTO;
  }

  public void setVehicleDTO(VehicleDTO vehicleDTO) {
    this.vehicleDTO = vehicleDTO;
  }

  public MemberDTO getMemberDTO() {
    return memberDTO;
  }

  public void setMemberDTO(MemberDTO memberDTO) {
    this.memberDTO = memberDTO;
  }

  public List<VehicleDTO> getVehicleDTOList() {
    return vehicleDTOList;
  }

  public void setVehicleDTOList(List<VehicleDTO> vehicleDTOList) {
    this.vehicleDTOList = vehicleDTOList;
  }
}
