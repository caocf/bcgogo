package com.bcgogo.txn.dto;

import com.bcgogo.enums.RepairOrderTemplateStatus;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-10-11
 * Time: 下午2:46
 * To change this template use File | Settings | File Templates.
 */
public class RepairOrderTemplateDTO {

  private Long id;

  private String idStr;

    //店面ID
  private Long shopId;

  //模板名称
  private String templateName;

  //模板使用计数
  private Integer usageCounter;

  //模板状态
  private RepairOrderTemplateStatus status;

  private List<RepairOrderTemplateServiceDTO>   repairOrderTemplateServiceDTOs;

  private List<RepairOrderTemplateItemDTO> repairOrderTemplateItemDTOs;

  private RepairOrderDTO repairOrderDTO;

  private List<RepairOrderTemplateOtherIncomeItemDTO> repairOrderTemplateOtherIncomeItemDTOList;

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getTemplateName() {
    return templateName;
  }

  public void setTemplateName(String templateName) {
    this.templateName = templateName;
  }

  public Integer getUsageCounter() {
    return usageCounter;
  }

  public void setUsageCounter(Integer usageCounter) {
    this.usageCounter = usageCounter;
  }

  public RepairOrderTemplateStatus getStatus() {
    return status;
  }

  public void setStatus(RepairOrderTemplateStatus status) {
    this.status = status;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
    if(id != null)
    {
       this.idStr = id.toString();
    }
  }

  public List<RepairOrderTemplateItemDTO> getRepairOrderTemplateItemDTOs() {
    return repairOrderTemplateItemDTOs;
  }

  public void setRepairOrderTemplateItemDTOs(List<RepairOrderTemplateItemDTO> repairOrderTemplateItemDTOs) {
    this.repairOrderTemplateItemDTOs = repairOrderTemplateItemDTOs;
  }

  public List<RepairOrderTemplateServiceDTO> getRepairOrderTemplateServiceDTOs() {
    return repairOrderTemplateServiceDTOs;
  }

  public void setRepairOrderTemplateServiceDTOs(List<RepairOrderTemplateServiceDTO> repairOrderTemplateServiceDTOs) {
    this.repairOrderTemplateServiceDTOs = repairOrderTemplateServiceDTOs;
  }

  public RepairOrderDTO getRepairOrderDTO() {
    return repairOrderDTO;
  }

  public void setRepairOrderDTO(RepairOrderDTO repairOrderDTO) {
    this.repairOrderDTO = repairOrderDTO;
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public List<RepairOrderTemplateOtherIncomeItemDTO> getRepairOrderTemplateOtherIncomeItemDTOList() {
    return repairOrderTemplateOtherIncomeItemDTOList;
  }

  public void setRepairOrderTemplateOtherIncomeItemDTOList(List<RepairOrderTemplateOtherIncomeItemDTO> repairOrderTemplateOtherIncomeItemDTOList) {
    this.repairOrderTemplateOtherIncomeItemDTOList = repairOrderTemplateOtherIncomeItemDTOList;
  }
}
