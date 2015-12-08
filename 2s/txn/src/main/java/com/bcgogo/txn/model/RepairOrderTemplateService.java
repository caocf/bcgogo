package com.bcgogo.txn.model;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.RepairOrderTemplateServiceDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: MZDong
 * Date: 11-9-14
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Table(name = "repair_order_template_service")
public class RepairOrderTemplateService extends LongIdentifier {
  public RepairOrderTemplateService() {
  }

  // Repair_Order_Template 表ID
  private Long repairOrderTemplateId;

  //Service 表ID
  private Long serviceId;

  //施工内容名称
  private String serviceName;

  //施工单工时费
  private Double price;

  private String businessCategoryName;

  private Long businessCategoryId;


  @Column(name = "repair_order_template_id")
  public Long getRepairOrderTemplateId() {
    return repairOrderTemplateId;
  }

  public void setRepairOrderTemplateId(Long repairOrderTemplateId) {
    this.repairOrderTemplateId = repairOrderTemplateId;
  }

  @Column(name = "service_id")
  public Long getServiceId() {
    return serviceId;
  }

  public void setServiceId(Long serviceId) {
    this.serviceId = serviceId;
  }

  @Column(name = "service_name")
  public String getServiceName() {
    return serviceName;
  }

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  @Column(name = "price")
  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public RepairOrderTemplateService fromDTO(RepairOrderTemplateServiceDTO templateServiceDTO) {
    if (templateServiceDTO == null) {
      return this;
    }
    this.setPrice(templateServiceDTO.getPrice());
    this.setRepairOrderTemplateId(templateServiceDTO.getRepairOrderTemplateId());
    this.setServiceId(templateServiceDTO.getServiceId());
    this.setServiceName(templateServiceDTO.getServiceName());
    this.setBusinessCategoryId(templateServiceDTO.getBusinessCategoryId());
    this.setBusinessCategoryName(templateServiceDTO.getBusinessCategoryName());
    return this;
  }

  public RepairOrderTemplateServiceDTO toDTO() {
    RepairOrderTemplateServiceDTO repairOrderTemplateServiceDTO = new RepairOrderTemplateServiceDTO();
    repairOrderTemplateServiceDTO.setRepairOrderTemplateId(this.getRepairOrderTemplateId());
    repairOrderTemplateServiceDTO.setServiceId(this.getServiceId());
    repairOrderTemplateServiceDTO.setServiceName(this.getServiceName());
    repairOrderTemplateServiceDTO.setPrice(this.getPrice());
    repairOrderTemplateServiceDTO.setId(this.getId());
    repairOrderTemplateServiceDTO.setIdStr(this.getId().toString());
    repairOrderTemplateServiceDTO.setBusinessCategoryId(this.getBusinessCategoryId());
    repairOrderTemplateServiceDTO.setBusinessCategoryName(this.getBusinessCategoryName());
    return repairOrderTemplateServiceDTO;
  }

  @Column(name="business_category_name")
  public String getBusinessCategoryName() {
    return businessCategoryName;
  }

  public void setBusinessCategoryName(String businessCategoryName) {
    this.businessCategoryName = businessCategoryName;
  }

  @Column(name="business_category_id")
  public Long getBusinessCategoryId() {
    return businessCategoryId;
  }

  public void setBusinessCategoryId(Long businessCategoryId) {
    this.businessCategoryId = businessCategoryId;
  }
}