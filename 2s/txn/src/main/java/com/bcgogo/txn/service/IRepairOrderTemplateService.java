package com.bcgogo.txn.service;

import com.bcgogo.txn.dto.RepairOrderTemplateDTO;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-10-12
 * Time: 下午5:24
 * To change this template use File | Settings | File Templates.
 */
public interface IRepairOrderTemplateService {

  public RepairOrderTemplateDTO saveOrUpdateRepairOrderTemplate(RepairOrderTemplateDTO repairOrderTemplateDTO) throws Exception ;

  public RepairOrderTemplateDTO deleteRepairOrderTemplateById(Long repairOrderTemplateId);

  public RepairOrderTemplateDTO renameRepairOrderTemplateById(Long shopId ,Long repairOrderTemplateId, String newRepairOrderTemplateName);

  public List<RepairOrderTemplateDTO> getAllRepairOrderTemplate(Long shopId);

  public RepairOrderTemplateDTO getRepairOrderTemplateByTemplateName(Long shopId,Long shopVersionId,Long storehouseId, String repairOrderTemplateName) throws Exception ;
  public RepairOrderTemplateDTO getSimpleRepairOrderTemplateByTemplateName(Long shopId,Long shopVersionId,Long storehouseId, String repairOrderTemplateName) throws Exception ;

  public List<RepairOrderTemplateDTO> getTop5RepairOrderTemplateOrderByUsageCounter(Long shopId);

  public  RepairOrderTemplateDTO updateRepairOrderTemplateUsageCounter(Long repairOrderTemplateId);
}
