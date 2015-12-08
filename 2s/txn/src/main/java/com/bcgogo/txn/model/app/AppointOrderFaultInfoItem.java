package com.bcgogo.txn.model.app;

import com.bcgogo.api.AppointOrderFaultInfoItemDTO;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 14-1-20
 * Time: 下午1:40
 */
@Entity
@Table(name = "appoint_order_fault_code_item")
public class AppointOrderFaultInfoItem extends LongIdentifier {
  private Long appointOrderId;
  private String faultCode;
  private Long appVehicleId;
  private String description;

  public void fromDTO(AppointOrderFaultInfoItemDTO appointOrderFaultInfoItemDTO) {
    if(appointOrderFaultInfoItemDTO != null){
      this.setAppointOrderId(appointOrderFaultInfoItemDTO.getAppointOrderId());
      this.setFaultCode(appointOrderFaultInfoItemDTO.getFaultCode());
      this.setAppVehicleId(appointOrderFaultInfoItemDTO.getAppVehicleId());
      this.setDescription(appointOrderFaultInfoItemDTO.getDescription());
    }
   }

  @Column(name = "appoint_order_id")
  public Long getAppointOrderId() {
    return appointOrderId;
  }

  public void setAppointOrderId(Long appointOrderId) {
    this.appointOrderId = appointOrderId;
  }

  @Column(name = "fault_code")
  public String getFaultCode() {
    return faultCode;
  }

  public void setFaultCode(String faultCode) {
    this.faultCode = faultCode;
  }

  @Column(name = "app_vehicle_id")
  public Long getAppVehicleId() {
    return appVehicleId;
  }

  public void setAppVehicleId(Long appVehicleId) {
    this.appVehicleId = appVehicleId;
  }

  @Column(name = "description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }


}
