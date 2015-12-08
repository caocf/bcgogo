package com.bcgogo.txn.service.importexcel.order;

import com.bcgogo.config.dto.ImportResult;
import com.bcgogo.config.service.IImportService;
import com.bcgogo.config.service.excelimport.BcgogoExcelDataImporter;
import com.bcgogo.config.service.excelimport.CheckResult;
import com.bcgogo.config.service.excelimport.ExcelImportConstants;
import com.bcgogo.config.service.excelimport.ImportContext;
import com.bcgogo.constant.ImportConstants;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.search.dto.OrderSearchConditionDTO;
import com.bcgogo.search.dto.OrderSearchResultDTO;
import com.bcgogo.search.dto.OrderSearchResultListDTO;
import com.bcgogo.search.service.order.ISearchOrderService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.model.ImportedOrderTemp;
import com.bcgogo.txn.model.TxnDaoManager;
import com.bcgogo.txn.model.TxnWriter;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.utils.ArrayUtil;
import com.bcgogo.utils.StringUtil;
import com.bcgogo.utils.TxnConstant;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 单据导入执行类
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 12-10-29
 * Time: 上午8:52
 * To change this template use File | Settings | File Templates.
 */
@Component
public class OrderImporter extends BcgogoExcelDataImporter {

  private static final Logger LOG = LoggerFactory.getLogger(OrderImporter.class);

  /**
   * 执行客户数据导入
   *
   * @param importContext
   * @return
   * @throws com.bcgogo.config.service.excelimport.ExcelImportException
   */
  @Override
  public ImportResult importData(ImportContext importContext) throws Exception {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    TxnDaoManager txnDaoManager = ServiceManager.getService(TxnDaoManager.class);
    ImportResult importResult = new ImportResult();
    List<Map<String, Object>> dataList = importContext.getDataList();
    importResult.setTotalCount(dataList.size());
    List<ImportedOrderTemp> orderTemps = new ArrayList<ImportedOrderTemp>();
    ImportedOrderTemp orderTemp = null;
    int successCount = 0;
    int failCount = 0;
    StringBuffer messageBuffer = new StringBuffer();
    for (int index = 0; index < dataList.size(); index++) {
      Map<String, Object> data = dataList.get(index);
      if (data == null || data.isEmpty()) {
        continue;
      }
      orderTemp = orderDTOGenerator.generate(data, importContext.getFieldMapping(), importContext.getShopId());
      if (orderTemp != null) {
        orderTemps.add(orderTemp);
      }
      TxnWriter writer=txnDaoManager.getWriter();
      if (orderTemps.size() >= BATCH_SAVE_SIZE || index >= dataList.size() - 1) {
         Object status = writer.begin();
        try {
          txnService.batchCreateImportedOrder(orderTemps,writer);
          successCount += orderTemps.size();
          writer.commit(status);
        } catch (BcgogoException e) {
          LOG.error("批量保存导入单据发生异常 : " + e.getMessage(), e);
          failCount += orderTemps.size();
          messageBuffer.append(e.getMessage());
          throw e;
        } finally {
          writer.rollback(status);
          orderTemps.clear();
        }
      }
    }
    //更新导入记录状态
    remarkImportRecordStatus(importContext.getImportRecordIdList(), importContext.getShopId(), successCount, ImportConstants.Type.TYPE_ORDER);
    importResult.setFailCount(failCount);
    importResult.setSuccessCount(successCount);
    importResult.setMessage(messageBuffer.toString());
    return importResult;
  }

  /**
   * 执行导入单据校验
   *
   * @param importContext
   * @return
   * @throws com.bcgogo.config.service.excelimport.ExcelImportException
   */
  @Override
  public CheckResult checkData(ImportContext importContext) throws BcgogoException {
    CheckResult checkResult = new CheckResult();
    ISearchOrderService searchOrderService = ServiceManager.getService(ISearchOrderService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    List<Map<String, Object>> dataList = importContext.getDataList();
    if (dataList == null || dataList.isEmpty()) {
      checkResult.setMessage(ExcelImportConstants.CheckResultMessage.EMPTY_DATA_CONTENT);
      return checkResult;
    }
    Map<String, String> fieldMapping = importContext.getFieldMapping();
    if (fieldMapping == null || fieldMapping.isEmpty()) {
      checkResult.setMessage(ExcelImportConstants.CheckResultMessage.EMPTY_FIELD_MAPPING);
      return checkResult;
    }
    List<Map<String, Object>> failDataList = new ArrayList<Map<String, Object>>();
    checkResult.setFailDataList(failDataList);
    String fieldCheckResult = null;
    Map<String,String> checkReceiptNoRepeat = new HashMap<String, String>();
    int headLineNum = 2;
    Set<String> existReceipts = new HashSet<String>();
    for(int index = 0;index < dataList.size();index++ ){
      Map<String,Object> data = dataList.get(index);
      if (data == null) {
        continue;
      }
      if (data.isEmpty()) {
        data.put("message", "第" + String.valueOf(index + headLineNum) +"行" + ExcelImportConstants.CheckResultMessage.EMPTY_DATA_ITEM_CONTENT);
        failDataList.add(data);
        if (!StringUtil.isEmpty(checkResult.getMessage())) {
          checkResult.setMessage(checkResult.getMessage() + "第" + String.valueOf(index + headLineNum) + "行" + ExcelImportConstants.CheckResultMessage.EMPTY_DATA_ITEM_CONTENT);
        }else{
          checkResult.setMessage("第" + String.valueOf(index + headLineNum) + "行" + ExcelImportConstants.CheckResultMessage.EMPTY_DATA_ITEM_CONTENT);
        }

        continue;
      }

      //校验库存信息名
      data.put("shopId",importContext.getShopId());
      fieldCheckResult = orderImportVerifier.verify(data, fieldMapping,null);
      if (!StringUtil.isEmpty(fieldCheckResult)) {
        data.put("message", "第" + String.valueOf(index + headLineNum) +"行"+fieldCheckResult);
        failDataList.add(data);
        if (!StringUtil.isEmpty(checkResult.getMessage())) {
          checkResult.setMessage( checkResult.getMessage() +"第" + String.valueOf(index + headLineNum) +"行" + fieldCheckResult );
        }else{
          checkResult.setMessage( "第" + String.valueOf(index + headLineNum) +"行" + fieldCheckResult );
        }
//        continue;
      }

      String receiptNo = "";
      if(null != data.get(fieldMapping.get(OrderImportConstants.FieldName.RECEIPT)))
      {
        receiptNo = String.valueOf(data.get(fieldMapping.get(OrderImportConstants.FieldName.RECEIPT)));
      }
      receiptNo = receiptNo.replace(" ","");

      if(StringUtils.isNotBlank(receiptNo)){
        OrderSearchConditionDTO condition = new OrderSearchConditionDTO();
        String orderType = orderDTOGenerator.getOrderType(data.get("fileName").toString());
        String receiptPrefix=orderDTOGenerator.getReceiptPrefix(orderType);
        condition.setShopId(importContext.getShopId());
        condition.setOrderType(new String[]{orderType});
        condition.setReceiptNo(receiptPrefix + receiptNo);
        OrderSearchResultListDTO resultDTO = null;
        try {
          //1. 找Solr中是否有重复单据号
//          resultDTO = searchOrderService.queryOrders(condition);
//          if(CollectionUtils.isNotEmpty(resultDTO.getOrders())){
//            existReceipts.add(receiptNo);
//          }else{
//            //2. 找imported_order中是否有重复单据号
//            resultDTO = txnService.getImportedOrderByConditions(condition);
//            if(CollectionUtils.isNotEmpty(resultDTO.getOrders())){
//              existReceipts.add(receiptNo);
//            }
//          }
        } catch (Exception e) {
          LOG.error(e.getMessage(), e);
        }
      }
      checkDataMapChange(checkReceiptNoRepeat, receiptNo, String.valueOf(index + headLineNum));
    }
    String str = "";
    if(CollectionUtils.isNotEmpty(existReceipts)){
      String[] array = new String[0];
      str = "单据号：" + ArrayUtils.toString(existReceipts.toArray(array)).replace("{", "[").replace("}", "]") + " 在系统中已存在！";
      str += "<br/>";
    }

    str += getCheckRepeatInfo(checkReceiptNoRepeat,"单据号");

    if(StringUtils.isNotBlank(str))
    {
      checkResult.setMessage(StringUtils.isBlank(checkResult.getMessage())?str:checkResult.getMessage()+str);
    }
    return checkResult;
  }

  public void checkDataMapChange(Map<String,String> map,String key,String line)
  {
    if(StringUtils.isBlank(key))
    {
      return;
    }
    String str = map.get(key);
    if(StringUtils.isBlank(str))
    {
      map.put(key,1+"_2");
    }
    else
    {
      String[] strs = str.split("_");
      strs[0] = String.valueOf(Integer.valueOf(strs[0])+1);
      map.put(key,StringUtils.join(strs,"_")+"_"+line);
    }
  }

  public String getCheckRepeatInfo(Map<String,String> map,String sceneInfo)
  {
    String str = "";
    if(map.size()!=0)
    {
      for(String key : map.keySet())
      {
        if(Integer.valueOf(map.get(key).split("_")[0])>1)
        {
          String[] strs = map.get(key).split("_");
          str += "第"+StringUtils.join(strs,",",1,strs.length)+"行的"+sceneInfo+key+"相同！";
        }
      }
    }

    return str;
  }
  @Autowired
  private OrderImportVerifier orderImportVerifier;

  @Autowired
  private OrderDTOGenerator orderDTOGenerator;
}
