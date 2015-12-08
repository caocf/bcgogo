package com.bcgogo.user.service.excelimport.supplier;

import com.bcgogo.config.cache.AreaCacheManager;
import com.bcgogo.config.dto.AreaDTO;
import com.bcgogo.config.dto.ImportResult;
import com.bcgogo.config.service.excelimport.*;
import com.bcgogo.constant.ImportConstants;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.user.dto.ValidateImportDataDTO;
import com.bcgogo.user.service.ISupplierService;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 供应商数据导入执行类
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-4-17
 * Time: 下午3:20
 * To change this template use File | Settings | File Templates.
 */
@Component
public class SupplierImporter extends BcgogoExcelDataImporter {

  private static final Logger LOG = LoggerFactory.getLogger(SupplierImporter.class);

  @Override
  public ImportResult importData(ImportContext importContext) throws ExcelImportException {
//    ISupplierService supplierService = ServiceManager.getService(ISupplierService.class);
//    ImportResult importResult = new ImportResult();
//    List<Map<String, Object>> dataList = importContext.getDataList();
//    importResult.setTotalCount(dataList.size());
//    List<SupplierDTO> supplierDTOList = new ArrayList<SupplierDTO>();
//    SupplierDTO supplierDTO = null;
//    int successCount = 0;
//    int failCount = 0;
//    StringBuffer messageBuffer = new StringBuffer();
//    for (int index = 0; index < dataList.size(); index ++) {
//      Map<String, Object> data = dataList.get(index);
//      if (data == null || data.isEmpty()) {
//        continue;
//      }
//      supplierDTO = supplierImportDTOGenerator.generate(data, importContext.getFieldMapping(), importContext.getShopId());
//      if (supplierDTO != null) {
//        supplierDTOList.add(supplierDTO);
//      }
//      if (supplierDTOList.size() >= BATCH_SAVE_SIZE || index >= dataList.size() - 1) {
//        try {
//          supplierService.batchCreateSupplier(map,supplierDTOList);
//          successCount += supplierDTOList.size();
//        } catch (BcgogoException e) {
//          LOG.error("批量保存供应商数据时发生异常 : " + e.getMessage(), e);
//          failCount += supplierDTOList.size();
//          messageBuffer.append(";").append(e.getMessage());
//        } finally {
//          supplierDTOList.clear();
//        }
//      }
//    }
//    //更新导入记录状态
//    remarkImportRecordStatus(importContext.getImportRecordIdList(), importContext.getShopId(), successCount, ImportConstants.Type.TYPE_SUPPLIER);
//
//    importResult.setFailCount(failCount);
//    importResult.setSuccessCount(successCount);
//    importResult.setMessage(messageBuffer.toString());
//    return importResult;
    return null;
  }

  public ImportResult importData(Map map,ImportContext importContext) throws ExcelImportException {
    ISupplierService supplierService = ServiceManager.getService(ISupplierService.class);
    ImportResult importResult = new ImportResult();
    List<Map<String, Object>> dataList = importContext.getDataList();
    importResult.setTotalCount(dataList.size());
    List<SupplierDTO> supplierDTOList = new ArrayList<SupplierDTO>();
    SupplierDTO supplierDTO = null;
    int successCount = 0;
    int failCount = 0;
    StringBuffer messageBuffer = new StringBuffer();
    for (int index = 0; index < dataList.size(); index ++) {
      Map<String, Object> data = dataList.get(index);
      if (data == null || data.isEmpty()) {
        continue;
      }
      supplierDTO = supplierImportDTOGenerator.generate(data, importContext.getFieldMapping(), importContext.getShopId());
      if (supplierDTO != null) {
        supplierDTOList.add(supplierDTO);
      }
      if (supplierDTOList.size() >= BATCH_SAVE_SIZE || index >= dataList.size() - 1) {
        try {
          supplierService.batchCreateSupplier(map,supplierDTOList);
          successCount += supplierDTOList.size();
        } catch (BcgogoException e) {
          LOG.error("批量保存供应商数据时发生异常 : " + e.getMessage(), e);
          failCount += supplierDTOList.size();
          messageBuffer.append(";").append(e.getMessage());
        } finally {
          supplierDTOList.clear();
        }
      }
    }
    //更新导入记录状态
    remarkImportRecordStatus(importContext.getImportRecordIdList(), importContext.getShopId(), successCount, ImportConstants.Type.TYPE_SUPPLIER);

    importResult.setFailCount(failCount);
    importResult.setSuccessCount(successCount);
    importResult.setMessage(messageBuffer.toString());
    return importResult;
  }

  @Override
  public CheckResult checkData(ImportContext importContext) throws ExcelImportException {
    CheckResult checkResult = new CheckResult();
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
    Map<String,String> checkMobileRepeat = new HashMap<String, String>();
    Map<String,SupplierDTO> mobileSupplierMap = ServiceManager.getService(ISupplierService.class).getMobileSupplierMapOnlyForMobileCheck(importContext.getShopId());
    Map<String,SupplierDTO> landLineSupplierMap= ServiceManager.getService(ISupplierService.class).getLandLineSupplierMap(importContext.getShopId());
    Map<String,AreaDTO> areaDTOMap = AreaCacheManager.getAreaDTOMap();
    ValidateImportDataDTO validateImportDataDTO = new ValidateImportDataDTO();
    validateImportDataDTO.setMobileSupplierMap(mobileSupplierMap);
    validateImportDataDTO.setLandLineSupplierMap(landLineSupplierMap);
    validateImportDataDTO.setAreaDTOMap(areaDTOMap);
    int headLineNum = 2;

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
      fieldCheckResult = supplierImportVerifier.verify(data, fieldMapping,validateImportDataDTO);
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

      String mobile = "";
      if(null != data.get(fieldMapping.get(SupplierImportConstants.FieldName.MOBILE)))
      {
        mobile = String.valueOf(data.get(fieldMapping.get(SupplierImportConstants.FieldName.MOBILE)));
      }
      else
      {
        continue;
      }
      mobile = mobile.replace(" ","");
      if(StringUtils.isNotBlank(mobile))
      {
        String[] mobileArray = mobile.split("/");
        for(String s : mobileArray)
        {
          checkDataMapChange(checkMobileRepeat,s,String.valueOf(index + headLineNum));
        }
      }
    }

    String str = getCheckRepeatInfo(checkMobileRepeat,"手机号");

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
  private SupplierImportDTOGenerator supplierImportDTOGenerator;

  @Autowired
  private SupplierImportVerifier supplierImportVerifier;
}
