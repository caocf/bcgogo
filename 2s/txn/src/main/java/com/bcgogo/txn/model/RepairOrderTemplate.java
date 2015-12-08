package com.bcgogo.txn.model;

import com.bcgogo.enums.RepairOrderTemplateStatus;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.RepairOrderTemplateDTO;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: MZDong
 * Date: 11-9-14
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Table(name = "repair_order_template")
public class RepairOrderTemplate extends LongIdentifier {

  //店面ID
  private Long shopId;

  //模板名称
  private String templateName;

  //模板使用计数
  private Integer usageCounter;

  //模板状态
  private RepairOrderTemplateStatus status;

  public RepairOrderTemplate() {
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "template_name")
  public String getTemplateName() {
    return templateName;
  }

  public void setTemplateName(String templateName) {
    this.templateName = templateName;
  }

  @Column(name = "usage_counter")
  public Integer getUsageCounter() {
    return usageCounter;
  }

  public void setUsageCounter(Integer usageCounter) {
    this.usageCounter = usageCounter;
  }

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  public RepairOrderTemplateStatus getStatus() {
    return status;
  }

  public void setStatus(RepairOrderTemplateStatus status) {
    this.status = status;
  }

  public RepairOrderTemplateDTO toDTO() {
    RepairOrderTemplateDTO repairOrderTemplateDTO = new RepairOrderTemplateDTO();
    repairOrderTemplateDTO.setShopId(this.getShopId());
    repairOrderTemplateDTO.setStatus(this.getStatus());
    repairOrderTemplateDTO.setTemplateName(this.getTemplateName());
    repairOrderTemplateDTO.setUsageCounter(this.getUsageCounter());
    repairOrderTemplateDTO.setId(this.getId());
    repairOrderTemplateDTO.setIdStr(this.getId().toString());
    return repairOrderTemplateDTO;
  }

  public RepairOrderTemplate fromDTO(RepairOrderTemplateDTO repairOrderTemplateDTO) {
    if (repairOrderTemplateDTO == null) {
      return this;
    }

    this.setShopId(repairOrderTemplateDTO.getShopId());
    this.setStatus(repairOrderTemplateDTO.getStatus());
    this.setTemplateName(repairOrderTemplateDTO.getTemplateName());
    this.setUsageCounter(repairOrderTemplateDTO.getUsageCounter());
    return this;
  }

}
