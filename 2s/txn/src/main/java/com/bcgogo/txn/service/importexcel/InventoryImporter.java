package com.bcgogo.txn.service.importexcel;

import com.bcgogo.config.dto.ImportResult;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.excelimport.*;
import com.bcgogo.constant.ImportConstants;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.InventoryInfoDTO;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.txn.service.solr.IProductSolrWriterService;
import com.bcgogo.user.service.utils.BcgogoShopLogicResourceUtils;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 库存导入执行类
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-4-19
 * Time: 下午1:31
 * To change this template use File | Settings | File Templates.
 */
@Component
public class InventoryImporter extends BcgogoExcelDataImporter{

  private static final Logger LOG = LoggerFactory.getLogger(InventoryImporter.class);

  /**
   * 执行库存信息数据导入
   *
   * @param importContext
   * @return
   * @throws com.bcgogo.config.service.excelimport.ExcelImportException
   */
  @Override
  public ImportResult importData(ImportContext importContext) throws ExcelImportException {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    IProductSolrWriterService productSolrWriterService = ServiceManager.getService(IProductSolrWriterService.class);
    ImportResult importResult = new ImportResult();
    List<Map<String, Object>> dataList = importContext.getDataList();
    importResult.setTotalCount(dataList.size());
    List<InventoryInfoDTO> inventoryInfoDTOList = new ArrayList<InventoryInfoDTO>();
    InventoryInfoDTO inventoryInfoDTO = null;
    int successCount = 0;
    int failCount = 0;
    StringBuffer messageBuffer = new StringBuffer();
    for (int index = 0; index < dataList.size(); index++) {
      Map<String, Object> data = dataList.get(index);
      if (data == null || data.isEmpty()) {
        continue;
      }
      inventoryInfoDTO = inventoryInfoDTOGenerator.generate(data, importContext.getFieldMapping(), importContext.getShopId());
      if (inventoryInfoDTO != null) {
        inventoryInfoDTOList.add(inventoryInfoDTO);
      }
      if (inventoryInfoDTOList.size() >= BATCH_SAVE_SIZE || index >= dataList.size() - 1) {
        try {
          txnService.batchCreateInventory(inventoryInfoDTOList, importContext.getShopId());
          successCount += inventoryInfoDTOList.size();
        } catch (BcgogoException e) {
          LOG.error("批量保存库存信息数据时发生异常 : " + e.getMessage(),e);
          failCount += inventoryInfoDTOList.size();
          messageBuffer.append(e.getMessage());
        } finally {
          inventoryInfoDTOList.clear();
        }
      }
    }

     //数据导入后重建solr索引
    try {
      productSolrWriterService.reCreateProductSolrIndex(importContext.getShopId(), 2000);
      LOG.info("solr重建product索引，店面shopId:" + importContext.getShopId());
    } catch (Exception e) {
      LOG.error("solr重建product索引失败！");
      LOG.error(e.getMessage(), e);
    }

    //更新导入记录状态
    remarkImportRecordStatus(importContext.getImportRecordIdList(), importContext.getShopId(), successCount, ImportConstants.Type.TYPE_INVENTORY);

    importResult.setFailCount(failCount);
    importResult.setSuccessCount(successCount);
    importResult.setMessage(messageBuffer.toString());
    return importResult;
  }

  /**
   * 执行库存信息数据校验
   *
   * @param importContext
   * @return
   * @throws ExcelImportException
   */
  @Override
  public CheckResult checkData(ImportContext importContext) throws ExcelImportException {
    //校验返回结果
    CheckResult checkResult = new CheckResult();
    //导入数据的集合，Map的key是导入的列，value是导入的值
    List<Map<String, Object>> dataList = importContext.getDataList();
    if (dataList == null || dataList.isEmpty()) {
      checkResult.setMessage(ExcelImportConstants.CheckResultMessage.EMPTY_DATA_CONTENT);
      return checkResult;
    }
     //导入的excel的列和我们实体属性的映射
    Map<String, String> fieldMapping = importContext.getFieldMapping();
    if (fieldMapping == null || fieldMapping.isEmpty()) {
      checkResult.setMessage(ExcelImportConstants.CheckResultMessage.EMPTY_FIELD_MAPPING);
      return checkResult;
    }
    ShopDTO shopDTO = ServiceManager.getService(IConfigService.class).getShopById(importContext.getShopId());
    //判断导入的仓库是不是为空，为空，则提示用户
    if(BcgogoShopLogicResourceUtils.isHaveStoreHouseResource(shopDTO.getShopVersionId()) && !importContext.isImportToDefault()) {
      for(int index = 0;index < dataList.size();index++ ){
        Map<String,Object> data = dataList.get(index);
        if (data == null) {
          continue;
        }
        Object storeHouse = data.get(fieldMapping.get(InventoryImportConstants.FieldName.STORE_HOUSE));
        if(storeHouse == null || StringUtils.isEmpty(String.valueOf(storeHouse))) {
          checkResult.setMessage(ExcelImportConstants.CheckResultMessage.EMPTY_STORE_HOUSE);
          return checkResult;
        }
      }
    }


    List<Map<String, Object>> failDataList = new ArrayList<Map<String, Object>>();
    checkResult.setFailDataList(failDataList);
    String fieldCheckResult = null;
    int headLineNum = 2;

    for(int index = 0;index < dataList.size();index++ ){
      Map<String,Object> data = dataList.get(index);
      if (data == null) {
        continue;
      }
      if (data.isEmpty()) {
        data.put("message", "第" + String.valueOf(index + headLineNum) +"行" + ExcelImportConstants.CheckResultMessage.EMPTY_DATA_ITEM_CONTENT + "<br>");
        failDataList.add(data);
        if (!StringUtil.isEmpty(checkResult.getMessage())) {
          checkResult.setMessage(checkResult.getMessage() + "第" + String.valueOf(index + headLineNum) + "行" + ExcelImportConstants.CheckResultMessage.EMPTY_DATA_ITEM_CONTENT + "<br>");
        }else{
          checkResult.setMessage("第" + String.valueOf(index + headLineNum) + "行" + ExcelImportConstants.CheckResultMessage.EMPTY_DATA_ITEM_CONTENT + "<br>");
        }
      }
      //校验库存信息名
      fieldCheckResult = inventoryImportVerifier.verify(data, fieldMapping,null);
      if (!StringUtil.isEmpty(fieldCheckResult)) {
        data.put("message", "第" + String.valueOf(index + headLineNum) +"行"+fieldCheckResult);
        failDataList.add(data);
        if (!StringUtil.isEmpty(checkResult.getMessage())) {
          checkResult.setMessage( checkResult.getMessage() +"第" + String.valueOf(index + headLineNum) +"行" + fieldCheckResult );
        }else{
          checkResult.setMessage( "第" + String.valueOf(index + headLineNum) +"行" + fieldCheckResult );
        }
      }
    }
    validateContextCommodityCode(shopDTO,importContext,fieldMapping,checkResult,headLineNum);
    validateContextSameProduct7P(shopDTO,importContext,fieldMapping,checkResult,headLineNum);
    return checkResult;
  }


  //1，校验上传的文档里有没有重复的商品编码
  //第x、x、x行商品编码xx重复无法导入,
  //第x、x、x行商品编码xx已经存在无法导入,
  private void validateContextCommodityCode(ShopDTO shopDTO, ImportContext importContext, Map<String, String> fieldMapping,
                                            CheckResult checkResult, int headLineNum) {
    IProductService productService = ServiceManager.getService(IProductService.class);
    List<Map<String, Object>> dataList = importContext.getDataList();
    Map<String, List<Integer>> commodityCodeMap = new LinkedHashMap <String, List<Integer>>(dataList.size());
    //将导入的数据构造成  commodityCodeMap key 是商品编码，value 是商品编码行的序列（包括headLineNum）
    for (int i = 0, len = dataList.size(); i < len; i++) {
      String commodityCode = StringUtil.valueOf(dataList.get(i).get(fieldMapping.get(InventoryImportConstants.FieldName.COMMODITY_CODE)));
      commodityCode = commodityCode.trim().toUpperCase();
      if (StringUtils.isNotEmpty(commodityCode)) {
        List<Integer> commodityCodeIndexList = commodityCodeMap.get(commodityCode);
        if (commodityCodeIndexList == null) {
          commodityCodeIndexList = new ArrayList<Integer>();
          commodityCodeMap.put(commodityCode, commodityCodeIndexList);
        }
        commodityCodeIndexList.add(i + headLineNum);
      }
    }
    Iterator iterator = commodityCodeMap.entrySet().iterator();
    StringBuffer msg = new StringBuffer();
    while (iterator.hasNext()) {
      Map.Entry entry = (Map.Entry) iterator.next();
      List<Integer> commodityCodeIndexList = (ArrayList) entry.getValue();
      if (CollectionUtils.isNotEmpty(commodityCodeIndexList) && commodityCodeIndexList.size() > 1) {
        msg.append("第");
        String commodityCode = (String) entry.getKey();
        boolean isFirstCommodityCode = true;
        for (Integer index : commodityCodeIndexList) {
          if (isFirstCommodityCode) {
            isFirstCommodityCode = false;
            msg.append(index);
          } else {
            msg.append("," + index);
          }
        }
        msg.append("行商品编码：").append(commodityCode).append(" 重复无法导入,<br>");
      }
    }
    if (msg.length() > 0) {
      if (checkResult.getMessage() == null) {
        checkResult.setMessage(msg.toString());
      } else {
        checkResult.setMessage(checkResult.getMessage() + msg.toString());
      }
    }

    //校验数据库是否存在商品编码
    List<String> dbCommodityCode = productService.getAllCommodityCode(shopDTO.getId());
    if(CollectionUtils.isNotEmpty(dbCommodityCode)){
      iterator = commodityCodeMap.entrySet().iterator();
      msg = new StringBuffer();
      while (iterator.hasNext()) {
        Map.Entry entry = (Map.Entry) iterator.next();
        String commodityCode = (String) entry.getKey();
        List<Integer> commodityCodeIndexList = (ArrayList) entry.getValue();
        if (dbCommodityCode.contains(commodityCode) && CollectionUtils.isNotEmpty(commodityCodeIndexList)) {
          msg.append("第");
          boolean isFirstCommodityCode = true;
          for (Integer index : commodityCodeIndexList) {
            if (isFirstCommodityCode) {
              isFirstCommodityCode = false;
              msg.append(index);
            } else {
              msg.append("," + index);
            }
          }
          msg.append("行商品编码：").append(commodityCode).append("已经存在无法导入,<br>");
        }
      }
    }
    if (msg.length() > 0) {
      if (checkResult.getMessage() == null) {
        checkResult.setMessage(msg.toString());
      } else {
        checkResult.setMessage(checkResult.getMessage() + msg.toString());
      }
    }
  }
  //2,校验上传文档里有没有重复的商品7属性
  private void validateContextSameProduct7P(ShopDTO shopDTO,ImportContext importContext,Map<String, String> fieldMapping,
                                            CheckResult checkResult, int headLineNum) {
    List<Map<String, Object>> dataList = importContext.getDataList();
       Map<String, List<Integer>> productInfoMap = new LinkedHashMap<String, List<Integer>>(dataList.size());
       //将导入的数据构造成  productInfoMap key 是商品7属性信息，value 是商品信息行的序列（包括headLineNum）
       for (int i = 0, len = dataList.size(); i < len; i++) {
         String productInfo =  getProductInfoKey(dataList.get(i),fieldMapping);
         if (StringUtils.isNotEmpty(productInfo)) {
           List<Integer> productInfoIndexList = productInfoMap.get(productInfo);
           if (productInfoIndexList == null) {
             productInfoIndexList = new ArrayList<Integer>();
             productInfoMap.put(productInfo, productInfoIndexList);
           }
           productInfoIndexList.add(i + headLineNum);
         }
       }
       Iterator iterator = productInfoMap.entrySet().iterator();
       StringBuffer msg = new StringBuffer();
       while (iterator.hasNext()) {
         Map.Entry entry = (Map.Entry) iterator.next();
         List<Integer> productInfoIndexList = (ArrayList) entry.getValue();
         if (CollectionUtils.isNotEmpty(productInfoIndexList) && productInfoIndexList.size() > 1) {
           msg.append("第");
           String productInfo = (String) entry.getKey();
           boolean isFirstProductInfo = true;
           for (Integer index : productInfoIndexList) {
             if (isFirstProductInfo) {
               isFirstProductInfo = false;
               msg.append(index);
             } else {
               msg.append("," + index);
             }
           }
           msg.append("行商品：").append(productInfo.split(",")[1]).append(" 重复无法导入,<br>");
         }
       }
       if (msg.length() > 0) {
         if (checkResult.getMessage() == null) {
           checkResult.setMessage(msg.toString());
         } else {
           checkResult.setMessage(checkResult.getMessage() + msg.toString());
         }
       }
  }

  private String getProductInfoKey(Map<String, Object> data,Map<String, String> fieldMapping){
    StringBuffer sb = new StringBuffer();
    sb.append(StringUtil.valueOf(data.get(fieldMapping.get(InventoryImportConstants.FieldName.COMMODITY_CODE))).trim().toUpperCase());
    sb.append(",");
    sb.append(StringUtil.valueOf(data.get(fieldMapping.get(InventoryImportConstants.FieldName.PRODUCT_NAME))).trim());
    sb.append(",");
    sb.append(StringUtil.valueOf(data.get(fieldMapping.get(InventoryImportConstants.FieldName.PRODUCT_BRAND))).trim());
    sb.append(",");
    sb.append(StringUtil.valueOf(data.get(fieldMapping.get(InventoryImportConstants.FieldName.PRODUCT_SPEC))).trim());
    sb.append(",");
    sb.append(StringUtil.valueOf(data.get(fieldMapping.get(InventoryImportConstants.FieldName.PRODUCT_MODEL))).trim());
    sb.append(",");
    sb.append(StringUtil.valueOf(data.get(fieldMapping.get(InventoryImportConstants.FieldName.VEHICLE_BRAND))).trim());
    sb.append(",");
    sb.append(StringUtil.valueOf(data.get(fieldMapping.get(InventoryImportConstants.FieldName.VEHICLE_MODEL))).trim());
    return  sb.toString();
  }





  @Autowired
  private InventoryImportVerifier inventoryImportVerifier;

  @Autowired
  private InventoryInfoDTOGenerator inventoryInfoDTOGenerator;

}
