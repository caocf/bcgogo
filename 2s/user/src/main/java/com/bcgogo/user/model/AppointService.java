package com.bcgogo.user.model;

import com.bcgogo.enums.OperateType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.user.dto.AppointServiceDTO;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;

import javax.persistence.*;
import java.text.ParseException;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 12-12-10
 * Time: 上午1:50
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "appoint_service")
public class AppointService extends LongIdentifier {
  private Long shopId;
  private Long customerId;
  private Long vehicleId;
  private String appointName;
  private Long appointDate;
  private AppointServiceStatus status;

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "customer_id")
  public Long getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Long customerId) {
    this.customerId = customerId;
  }

  @Column(name = "vehicle_id")
  public Long getVehicleId() {
    return vehicleId;
  }

  public void setVehicleId(Long vehicleId) {
    this.vehicleId = vehicleId;
  }

  @Column(name = "appoint_name")
  public String getAppointName() {
    return appointName;
  }

  public void setAppointName(String appointName) {
    this.appointName = appointName;
  }

  @Column(name = "appoint_date")
  public Long getAppointDate() {
    return appointDate;
  }

  public void setAppointDate(Long appointDate) {
    this.appointDate = appointDate;
  }

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  public AppointServiceStatus getStatus() {
    return status;
  }

  public void setStatus(AppointServiceStatus status) {
    this.status = status;
  }




  public static AppointService fromDTO(AppointServiceDTO appointServiceDTO) throws ParseException {
    AppointService appointService=new AppointService();
    if(StringUtil.isNotEmpty(appointServiceDTO.getIdStr()))
      appointService.setId(NumberUtil.longValue(appointServiceDTO.getIdStr()));
    appointService.setShopId(appointServiceDTO.getShopId());
    appointService.setCustomerId(NumberUtil.longValue(appointServiceDTO.getCustomerId()));
    appointService.setVehicleId(NumberUtil.longValue(appointServiceDTO.getVehicleId()));
    appointService.setAppointName(appointServiceDTO.getAppointName());
    appointService.setAppointDate(DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE,appointServiceDTO.getAppointDate()));
    if(StringUtil.isNotEmpty(appointServiceDTO.getOperateType())&& OperateType.LOGIC_DELETE.toString().equals(appointServiceDTO.getOperateType())){
      appointService.setStatus(AppointServiceStatus.DISABLED);
    }else{
      appointService.setStatus(AppointServiceStatus.ENABLED);
    }
    return appointService;
  }

  public AppointServiceDTO toDTO(){
    AppointServiceDTO appointServiceDTO=new AppointServiceDTO();
    appointServiceDTO.setId(this.getId());
    appointServiceDTO.setVehicleId(String.valueOf(this.getVehicleId()));
    appointServiceDTO.setCustomerId(String.valueOf(this.getCustomerId()));
    appointServiceDTO.setAppointDate(DateUtil.convertDateLongToString(this.getAppointDate(),DateUtil.YEAR_MONTH_DATE));
    appointServiceDTO.setAppointName(this.getAppointName());
    return appointServiceDTO;
  }

  public enum AppointServiceStatus {
    ENABLED("有效"),
    DISABLED("失效");

    private final String name;
    private AppointServiceStatus(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }
  }
}
