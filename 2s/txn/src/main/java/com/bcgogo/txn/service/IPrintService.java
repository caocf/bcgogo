package com.bcgogo.txn.service;

import com.bcgogo.common.Result;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.txn.dto.PrintTemplateDTO;
import com.bcgogo.txn.dto.ShopPrintTemplateDTO;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 13-5-6
 * Time: 下午1:49
 */
public interface IPrintService {
  public void createPrintTemplate(Long shopId,PrintTemplateDTO printTemplateDTO) throws Exception;

  public void createOrUpdateDefaultPrintTemplate(PrintTemplateDTO printTemplateDTO) throws Exception;

  public void createOrUpdateShopPrintTemplate(ShopPrintTemplateDTO shopPrintTemplateDTO) throws Exception;

  public void createOrUpdateShopPrintTemplateByEmptyShopName(ShopPrintTemplateDTO shopPrintTemplateDTO) throws Exception;

  public PrintTemplateDTO getSinglePrintTemplateDTOByShopIdAndType(Long shopId, OrderTypes type) throws Exception;

  public ShopPrintTemplateDTO getSingleShopPrintTemplateDTOByShopIdAndType(Long shopId, OrderTypes type) throws Exception;

  public int countPrintTemplateDTOByName(Long shopId, String name) throws Exception;

  public List<PrintTemplateDTO> getPrintTemplateDTOByType(OrderTypes type) throws Exception;

  List<PrintTemplateDTO> getAllPrintTemplateDTOByShopIdAndType(Long shopId, OrderTypes orderType) throws Exception ;

  void deleteTemplateByShopPrintTemplateId(Long id);

  PrintTemplateDTO getPrintTemplateDTOFullById(Long templateId);

  void updateDisplayNameByShopPrintTemplateId(Long id, String displayName) throws Exception ;

  Result sendPrintCommand(Long shopId,Long orderId,String cameraSerialNo) throws Exception;
}
