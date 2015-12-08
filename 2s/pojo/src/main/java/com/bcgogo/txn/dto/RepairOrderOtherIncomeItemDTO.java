package com.bcgogo.txn.dto;

import com.bcgogo.enums.CustomerStatus;
import com.bcgogo.enums.ItemTypes;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.OtherIncomeCalculateWay;
import com.bcgogo.enums.assistantStat.AchievementType;
import com.bcgogo.search.dto.ItemIndexDTO;
import org.apache.commons.collections.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-12-12
 * Time: 下午4:43
 * To change this template use File | Settings | File Templates.
 */
public class RepairOrderOtherIncomeItemDTO  extends OrderOtherIncomeItemDTO{

  private Long templateId;
  private String templateIdStr;



  public Long getTemplateId() {
    return templateId;
  }

  public void setTemplateId(Long templateId) {
    this.templateId = templateId;
    this.templateIdStr = (null == this.templateId)?null:this.templateId.toString();
  }

  public String getTemplateIdStr() {
    return templateIdStr;
  }

  public void setTemplateIdStr(String templateIdStr) {
    this.templateIdStr = templateIdStr;
  }

  public static Map<Long, RepairOrderOtherIncomeItemDTO> listToMap(List<RepairOrderOtherIncomeItemDTO> itemDTOList) {
    Map<Long, RepairOrderOtherIncomeItemDTO> otherIncomeItemDTOMap = new HashMap<Long, RepairOrderOtherIncomeItemDTO>();
    if (CollectionUtils.isNotEmpty(itemDTOList)) {
      for (RepairOrderOtherIncomeItemDTO itemDTO : itemDTOList) {
        if (null == itemDTO.getId()) {
          continue;
        }
        otherIncomeItemDTOMap.put(itemDTO.getId(), itemDTO);
      }
    }
    return otherIncomeItemDTOMap;
  }

  public ItemIndexDTO toItemIndexDTO(RepairOrderDTO repairOrderDTO) {
    ItemIndexDTO itemIndexDTO = new ItemIndexDTO();
    itemIndexDTO.setItemId(this.getId());
    itemIndexDTO.setItemPrice(this.getPrice());
    itemIndexDTO.setItemTotalAmount(this.getPrice());
    itemIndexDTO.setItemName(this.getName());
    itemIndexDTO.setItemType(ItemTypes.OTHER_INCOME);
    itemIndexDTO.setOrderType(OrderTypes.REPAIR);
    itemIndexDTO.setItemMemo(this.getMemo());

    itemIndexDTO.setOrderId(repairOrderDTO.getId());
    itemIndexDTO.setShopId(repairOrderDTO.getShopId());
    itemIndexDTO.setCustomerId(repairOrderDTO.getCustomerId());
    itemIndexDTO.setCustomerOrSupplierName(repairOrderDTO.getCustomerName());
    itemIndexDTO.setCustomerOrSupplierStatus(repairOrderDTO.getCustomerStatus() == null ? CustomerStatus.ENABLED.toString() : repairOrderDTO.getCustomerStatus().toString());
    itemIndexDTO.setOrderReceiptNo(repairOrderDTO.getReceiptNo());
    itemIndexDTO.setOrderStatus(repairOrderDTO.getStatus());
    itemIndexDTO.setOrderTimeCreated(repairOrderDTO.getVestDate() == null ? repairOrderDTO.getCreationDate() : repairOrderDTO.getVestDate());
    itemIndexDTO.setVehicle(repairOrderDTO.getVechicle());
    return itemIndexDTO;
  }
}
