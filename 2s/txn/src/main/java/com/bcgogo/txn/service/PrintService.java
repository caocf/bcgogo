package com.bcgogo.txn.service;

import com.bcgogo.camera.CameraConfigDTO;
import com.bcgogo.common.Result;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.camera.ICameraService;
import com.bcgogo.constant.BMSConstant;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.notification.jms.MQProductHelper;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.PrintTemplateDTO;
import com.bcgogo.txn.dto.ShopPrintTemplateDTO;
import com.bcgogo.txn.model.PrintTemplate;
import com.bcgogo.txn.model.ShopPrintTemplate;
import com.bcgogo.txn.model.TxnDaoManager;
import com.bcgogo.txn.model.TxnWriter;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.ShopConstant;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 13-5-6
 * Time: 下午1:50
 */
@Component
public class PrintService implements IPrintService {
  private static final Logger LOG = LoggerFactory.getLogger(PrintService.class);

  public static final Long defaultTemplateShopId = -1L;
  public static final OrderTypes[] multiTemplateSupported = {OrderTypes.SALE, OrderTypes.REPAIR};

  @Autowired
  private TxnDaoManager txnDaoManager;

  @Override
  public PrintTemplateDTO getSinglePrintTemplateDTOByShopIdAndType(Long shopId, OrderTypes type) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    List<ShopPrintTemplate> shopPrintTemplates = writer.getShopPrintTemplateDTOByShopIdAndType(shopId, type);
    if (CollectionUtils.isNotEmpty(shopPrintTemplates)) {
      ShopPrintTemplate shopPrintTemplate = CollectionUtil.getFirst(shopPrintTemplates);
      PrintTemplate printTemplate = writer.getById(PrintTemplate.class, shopPrintTemplate.getTemplateId());
      if (null != printTemplate) {
        PrintTemplateDTO printTemplateDTO = printTemplate.toDTO();
        printTemplateDTO.setShopId(shopPrintTemplate.getShopId());
        printTemplateDTO.setShopPrintTemplateId(shopPrintTemplate.getId());
        return printTemplateDTO;
      }
    }
    return null;
  }

  @Override
  public ShopPrintTemplateDTO getSingleShopPrintTemplateDTOByShopIdAndType(Long shopId, OrderTypes type) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    List<ShopPrintTemplate> shopPrintTemplates = writer.getShopPrintTemplateDTOByShopIdAndType(shopId, type);
    if (CollectionUtils.isNotEmpty(shopPrintTemplates)) {
      ShopPrintTemplate shopPrintTemplate = CollectionUtil.getFirst(shopPrintTemplates);
      ShopPrintTemplateDTO shopPrintTemplateDTO = shopPrintTemplate.toDTO();
      return shopPrintTemplateDTO;
    }
    return null;
  }

  @Override
  public int countPrintTemplateDTOByName(Long shopId, String name) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.countPrintTemplateDTOByName(shopId, name);
  }

  @Override
  public List<PrintTemplateDTO> getPrintTemplateDTOByType(OrderTypes type) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();

    List<PrintTemplate> printTemplateList = writer.getPrintTemplateDTOByType(type);

    List<PrintTemplateDTO> printTemplateDTOList = null;
    if (null != printTemplateList && 0 != printTemplateList.size()) {
      printTemplateDTOList = new ArrayList<PrintTemplateDTO>();
      for (PrintTemplate printTemplate : printTemplateList) {
        PrintTemplateDTO printTemplateDTO = printTemplate.toDTO();
        printTemplateDTOList.add(printTemplateDTO);
      }
      return printTemplateDTOList;
    }

    return null;
  }

  @Override
  public List<PrintTemplateDTO> getAllPrintTemplateDTOByShopIdAndType(Long shopId, OrderTypes orderType) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    List<ShopPrintTemplate> shopPrintTemplates = writer.getShopPrintTemplateDTOByShopIdAndType(shopId, orderType);
    List<PrintTemplateDTO> results = new ArrayList<PrintTemplateDTO>();
    if (CollectionUtils.isNotEmpty(shopPrintTemplates)) {
      for (ShopPrintTemplate shopPrintTemplate : shopPrintTemplates) {
        if (shopPrintTemplates.size() > 1 && shopPrintTemplate.getShopId().equals(defaultTemplateShopId)) {
          continue;
        }
        PrintTemplateDTO printTemplateDTO = writer.getPrintTemplateInfoById(shopPrintTemplate.getTemplateId());
        if (printTemplateDTO != null) {
          printTemplateDTO.setShopId(shopPrintTemplate.getShopId());
          printTemplateDTO.setShopPrintTemplateId(shopPrintTemplate.getId());
          printTemplateDTO.setDisplayName(shopPrintTemplate.getDisplayName());
          results.add(printTemplateDTO);
        }
      }
    }
    return results;
  }

  @Override
  public void deleteTemplateByShopPrintTemplateId(Long id) {
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      ShopPrintTemplate shopPrintTemplate = writer.getById(ShopPrintTemplate.class, id);
      if (shopPrintTemplate != null) {
        writer.delete(ShopPrintTemplate.class, shopPrintTemplate.getId());
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public PrintTemplateDTO getPrintTemplateDTOFullById(Long templateId) {
    TxnWriter writer = txnDaoManager.getWriter();
    PrintTemplate printTemplate = writer.getPrintTemplateFullById(templateId);
    return printTemplate == null ? null : printTemplate.toDTO();
  }

  @Override
  public void createPrintTemplate(Long shopId, PrintTemplateDTO printTemplateDTO) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    if (printTemplateDTO == null) {
      return;
    }
    OrderTypes type = printTemplateDTO.getOrderType();

    ShopPrintTemplate shopPrintTemplate = null;
    PrintTemplate printTemplate = null;
    PrintTemplateDTO currentSinglePrintTemplate = this.getSinglePrintTemplateDTOByShopIdAndType(shopId, type);
    Object status = writer.begin();
    try {
      printTemplate = new PrintTemplate();
      printTemplate.setName(printTemplateDTO.getName());
      printTemplate.setOrderTypeEnum(printTemplateDTO.getOrderType());
      printTemplate.setTemplateHtml(printTemplateDTO.getTemplateHtml());
      writer.save(printTemplate);

      //支持多模板的打印单逻辑
      if (ArrayUtils.contains(multiTemplateSupported, type)) {
        shopPrintTemplate = new ShopPrintTemplate();
        shopPrintTemplate.setTemplateId(printTemplate.getId());
        shopPrintTemplate.setShopId(shopId);
        shopPrintTemplate.setOrderTypeEnum(type);
        shopPrintTemplate.setDisplayName(printTemplateDTO.getDisplayName());
        writer.save(shopPrintTemplate);
      } else {  //单模板打印单
        //如果已存在此店铺的个性模板
        if (null != currentSinglePrintTemplate && !Long.valueOf("-1").equals(currentSinglePrintTemplate.getShopId())) {
          shopPrintTemplate = writer.getById(ShopPrintTemplate.class, currentSinglePrintTemplate.getShopPrintTemplateId());
          shopPrintTemplate.setTemplateId(printTemplate.getId());
          shopPrintTemplate.setDisplayName(printTemplateDTO.getDisplayName());
          writer.delete(PrintTemplate.class, currentSinglePrintTemplate.getId());
          writer.update(shopPrintTemplate);
        } else {
          //当前为默认模板
          ShopPrintTemplateDTO shopPrintTemplateDTO = this.getSingleShopPrintTemplateDTOByShopIdAndType(shopId, type);
          if (null == shopPrintTemplateDTO || Long.valueOf("-1").equals(shopPrintTemplateDTO.getShopId())) {
            shopPrintTemplate = new ShopPrintTemplate();
            shopPrintTemplate.setShopId(shopId);
            shopPrintTemplate.setTemplateId(printTemplate.getId());
            shopPrintTemplate.setOrderTypeEnum(printTemplate.getOrderTypeEnum());
            shopPrintTemplate.setOrderType(printTemplate.getOrderType());
            shopPrintTemplate.setDisplayName(printTemplateDTO.getDisplayName());
            writer.save(shopPrintTemplate);
          } else {
            shopPrintTemplate = writer.getById(ShopPrintTemplate.class, shopPrintTemplateDTO.getId());
            shopPrintTemplate.setTemplateId(printTemplate.getId());
            shopPrintTemplate.setDisplayName(printTemplateDTO.getDisplayName());
            writer.update(shopPrintTemplate);
          }
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }


  @Override
  public void createOrUpdateDefaultPrintTemplate(PrintTemplateDTO printTemplateDTO) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    OrderTypes type = null;
    if (null != printTemplateDTO) {
      type = printTemplateDTO.getOrderType();
    }
    ShopPrintTemplate shopPrintTemplate = null;
    PrintTemplate printTemplate = null;
    PrintTemplateDTO defaultTemplate = this.getSinglePrintTemplateDTOByShopIdAndType(defaultTemplateShopId, type);
    Object status = writer.begin();
    try {
      if (null != printTemplateDTO) {
        printTemplate = new PrintTemplate();
        printTemplate.setName(printTemplateDTO.getName());
        printTemplate.setOrderTypeEnum(printTemplateDTO.getOrderType());
        printTemplate.setTemplateHtml(printTemplateDTO.getTemplateHtml());
        writer.save(printTemplate);
        if (null != defaultTemplate) {
          shopPrintTemplate = writer.getById(ShopPrintTemplate.class, defaultTemplate.getShopPrintTemplateId());
          shopPrintTemplate.setTemplateId(printTemplate.getId());
          shopPrintTemplate.setDisplayName(printTemplateDTO.getDisplayName());
          writer.update(shopPrintTemplate);
        } else {
          ShopPrintTemplateDTO shopPrintTemplateDTO = this.getSingleShopPrintTemplateDTOByShopIdAndType(defaultTemplateShopId, type);
          if (null == shopPrintTemplateDTO) {
            shopPrintTemplate = new ShopPrintTemplate();
            shopPrintTemplate.setShopId(defaultTemplateShopId);
            shopPrintTemplate.setTemplateId(printTemplate.getId());
            shopPrintTemplate.setOrderTypeEnum(printTemplate.getOrderTypeEnum());
            shopPrintTemplate.setOrderType(printTemplate.getOrderType());
            shopPrintTemplate.setDisplayName(printTemplateDTO.getDisplayName());
            writer.save(shopPrintTemplate);
          } else {
            shopPrintTemplate = writer.getById(ShopPrintTemplate.class, shopPrintTemplateDTO.getId());
            shopPrintTemplate.setTemplateId(printTemplate.getId());
            shopPrintTemplate.setDisplayName(printTemplateDTO.getDisplayName());
            writer.update(shopPrintTemplate);
          }
        }

      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void createOrUpdateShopPrintTemplate(ShopPrintTemplateDTO shopPrintTemplateDTO) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    ShopPrintTemplateDTO shopPrintTemplateDTO2 = getSingleShopPrintTemplateDTOByShopIdAndType(shopPrintTemplateDTO.getShopId(), shopPrintTemplateDTO.getOrderType());
    ShopPrintTemplate shopPrintTemplate = null;
    Object status = writer.begin();
    try {
      if (null == shopPrintTemplateDTO2 || Long.valueOf("-1").equals(shopPrintTemplateDTO2.getShopId())
        || ArrayUtils.contains(multiTemplateSupported, shopPrintTemplateDTO.getOrderType())) {
        shopPrintTemplate = new ShopPrintTemplate();
        shopPrintTemplate.setTemplateId(shopPrintTemplateDTO.getTemplateId());
        shopPrintTemplate.setOrderTypeEnum(shopPrintTemplateDTO.getOrderType());
        shopPrintTemplate.setShopId(shopPrintTemplateDTO.getShopId());
        shopPrintTemplate.setDisplayName(shopPrintTemplateDTO.getDisplayName());
        writer.save(shopPrintTemplate);
      } else {
        shopPrintTemplate = writer.getById(ShopPrintTemplate.class, shopPrintTemplateDTO2.getId());
        shopPrintTemplate.setTemplateId(shopPrintTemplateDTO.getTemplateId());
        shopPrintTemplate.setDisplayName(shopPrintTemplateDTO.getDisplayName());
        writer.update(shopPrintTemplate);
      }

      writer.commit(status);

    } finally {
      writer.rollback(status);
    }

  }

  @Override
  public void createOrUpdateShopPrintTemplateByEmptyShopName(ShopPrintTemplateDTO shopPrintTemplateDTO) throws Exception {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    TxnWriter writer = txnDaoManager.getWriter();
    ShopPrintTemplateDTO shopPrintTemplateDTO2 = getSingleShopPrintTemplateDTOByShopIdAndType(shopPrintTemplateDTO.getShopId(), shopPrintTemplateDTO.getOrderType());
    ShopPrintTemplate shopPrintTemplate = null;
    Object status = writer.begin();
    try {
      if (null == shopPrintTemplateDTO2) {
        shopPrintTemplate = new ShopPrintTemplate();
        shopPrintTemplate.setTemplateId(shopPrintTemplateDTO.getTemplateId());
        shopPrintTemplate.setOrderTypeEnum(shopPrintTemplateDTO.getOrderType());
        shopPrintTemplate.setShopId(shopPrintTemplateDTO.getShopId());
        shopPrintTemplate.setDisplayName(shopPrintTemplateDTO.getDisplayName());
        writer.save(shopPrintTemplate);
      } else {
        shopPrintTemplate = writer.getById(ShopPrintTemplate.class, shopPrintTemplateDTO2.getId());
        shopPrintTemplate.setTemplateId(shopPrintTemplateDTO.getTemplateId());
        shopPrintTemplate.setDisplayName(shopPrintTemplateDTO.getDisplayName());
        writer.update(shopPrintTemplate);
      }

      writer.commit(status);

    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void updateDisplayNameByShopPrintTemplateId(Long id, String displayName) throws Exception {
    TxnWriter writer = txnDaoManager.getWriter();
    ShopPrintTemplate shopPrintTemplate = null;
    Object status = writer.begin();
    try {
      shopPrintTemplate = writer.getById(ShopPrintTemplate.class, id);
      if (shopPrintTemplate == null) {
        throw new BcgogoException("ShopPrintTemplate不存在!");
      }
      shopPrintTemplate.setDisplayName(displayName);
      writer.update(shopPrintTemplate);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  private static final String URL_PRINT_WASHBEAUTY_ORDER = "/web/print.do?method=printWashBeautyOrder&shopId={SHOP_ID}&orderId={ORDER_ID}";

  @Override
  public Result sendPrintCommand(Long shopId, Long orderId, String cameraSerialNo) throws Exception {
    LOG.info("sendPrintCommand,cameraSerialNo:{},orderId:{}",cameraSerialNo,orderId);
    if (StringUtil.isEmpty(cameraSerialNo)) return new Result(false, "摄像头序列号不应为空");
    ICameraService cameraService = ServiceManager.getService(ICameraService.class);
    CameraConfigDTO cameraConfigDTO = cameraService.getCameraConfigBySerialNo(cameraSerialNo);
    if (cameraConfigDTO == null || StringUtil.isEmpty(cameraConfigDTO.getPrinter_serial_no())) {
      LOG.error("未配置摄像头客户端,cameraSerialNo is {}", cameraSerialNo);
      return new Result(false, "未配置摄像头客户端");
    }
    String url = URL_PRINT_WASHBEAUTY_ORDER.replace("{SHOP_ID}", StringUtil.valueOf(shopId)).replace("{ORDER_ID}", StringUtil.valueOf(orderId));
    String msg = BMSConstant.COMMAND_PRINT + "#" + url;
    String subject = BMSConstant.PREFIX_SUBJECT_PRINT + cameraConfigDTO.getPrinter_serial_no();
    MQProductHelper.produce(subject, msg);
    return new Result();
  }


}
