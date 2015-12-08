package com.bcgogo.user.service.excelimport.obd;

import com.bcgogo.api.ObdDTO;
import com.bcgogo.api.ObdSimBindDTO;
import com.bcgogo.api.ObdSimDTO;
import com.bcgogo.config.dto.ImportResult;
import com.bcgogo.config.service.excelimport.BcgogoExcelDataImporter;
import com.bcgogo.config.service.excelimport.CheckResult;
import com.bcgogo.config.service.excelimport.ExcelImportConstants;
import com.bcgogo.config.service.excelimport.ImportContext;
import com.bcgogo.constant.ImportConstants;
import com.bcgogo.enums.app.ObdType;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.CustomerWithVehicleDTO;
import com.bcgogo.user.dto.ValidateImportDataDTO;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.excelimport.customer.CustomerImportConstants;
import com.bcgogo.user.service.obd.IObdManagerService;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by XinyuQiu on 14-6-30.
 */
@Component
public class ObdImporter extends BcgogoExcelDataImporter {

  private static final Logger LOG = LoggerFactory.getLogger(ObdImporter.class);

  @Autowired
  private ObdImportVerifier obdImportVerifier;

  @Autowired
  private ObdImportDTOGenerator obdImportDTOGenerator;

  @Override
  public ImportResult importData(ImportContext importContext) throws Exception {

    IObdManagerService obdManagerService = ServiceManager.getService(IObdManagerService.class);
    ImportResult importResult = new ImportResult();
    List<Map<String, Object>> dataList = importContext.getDataList();
    importResult.setTotalCount(dataList.size());
    List<ObdSimBindDTO> obdSimBindDTOs = new ArrayList<ObdSimBindDTO>();
    int successCount = 0;
    int failCount = 0;
    StringBuilder messageBuffer = new StringBuilder();
    for (int index = 0; index < dataList.size(); index++) {
      Map<String, Object> data = dataList.get(index);
      if (data == null || data.isEmpty()) {
        continue;
      }
      ObdSimBindDTO obdSimBindDTO = obdImportDTOGenerator.generate(data, importContext.getFieldMapping(),
          importContext.getUserId(), importContext.getUserName());
      if (obdSimBindDTO != null) {
        obdSimBindDTO.setOperateUserName(importContext.getUserName());
        obdSimBindDTO.setOperateUserId(importContext.getUserId());
        obdSimBindDTO.setOperateShopId(importContext.getShopId());
        obdSimBindDTOs.add(obdSimBindDTO);
      }
      if (obdSimBindDTOs.size() >= BATCH_SAVE_SIZE || index >= dataList.size() - 1) {
        try {
          obdManagerService.batchCreateObdAndSim(obdSimBindDTOs);
          successCount += obdSimBindDTOs.size();
        } catch (Exception e) {
          LOG.error("批量保存OBD数据时发生异常 : " + e.getMessage(), e);
          failCount += obdSimBindDTOs.size();
          messageBuffer.append("批量保存OBD数据时发生异常");
        } finally {
          obdSimBindDTOs.clear();
        }
      }
    }
    importResult.setFailCount(failCount);
    importResult.setSuccessCount(successCount);
    importResult.setMessage(messageBuffer.toString());
    return importResult;
  }

  public ImportResult initImportData(ImportContext importContext) throws Exception {

    IObdManagerService obdManagerService = ServiceManager.getService(IObdManagerService.class);
    ImportResult importResult = new ImportResult();
    List<Map<String, Object>> dataList = importContext.getDataList();
    importResult.setTotalCount(dataList.size());
    List<ObdSimBindDTO> obdSimBindDTOs = new ArrayList<ObdSimBindDTO>();
    int successCount = 0;
    int failCount = 0;
    StringBuilder messageBuffer = new StringBuilder();
    for (int index = 0; index < dataList.size(); index++) {
      Map<String, Object> data = dataList.get(index);
      if (data == null || data.isEmpty()) {
        continue;
      }
      ObdSimBindDTO obdSimBindDTO = obdImportDTOGenerator.generate(data, importContext.getFieldMapping(),
          importContext.getUserId(), importContext.getUserName());
      if (obdSimBindDTO != null) {
        obdSimBindDTO.setOperateUserName(importContext.getUserName());
        obdSimBindDTO.setOperateUserId(importContext.getUserId());
        obdSimBindDTO.setOperateShopId(importContext.getShopId());
        obdSimBindDTOs.add(obdSimBindDTO);
      }
      if (obdSimBindDTOs.size() >= BATCH_SAVE_SIZE || index >= dataList.size() - 1) {
        try {
          obdManagerService.initCreateObdAndSim(obdSimBindDTOs);
          successCount += obdSimBindDTOs.size();
        } catch (Exception e) {
          LOG.error("批量保存OBD数据时发生异常 : " + e.getMessage(), e);
          failCount += obdSimBindDTOs.size();
          messageBuffer.append("批量保存OBD数据时发生异常");
        } finally {
          obdSimBindDTOs.clear();
        }
      }
    }
    importResult.setFailCount(failCount);
    importResult.setSuccessCount(successCount);
    importResult.setMessage(messageBuffer.toString());
    return importResult;
  }



  @Override
  public CheckResult checkData(ImportContext importContext) throws BcgogoException {
    IObdManagerService obdManagerService = ServiceManager.getService(IObdManagerService.class);
    CheckResult checkResult = new CheckResult();
    checkResult.setPass(false);
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
    Set<String> imeiSet = new HashSet<String>();
    Set<String> mobileSet = new HashSet<String>();
    Set<String> mobileNoSet = new HashSet<String>();
    for(int index = 0;index < dataList.size();index++ ){
      Map<String,Object> data = dataList.get(index);
      if (data == null||data.isEmpty()) {
        continue;
      }
      Object imei = data.get(ObdImportConstants.FieldName.IMEI_DESC);
      if(imei!=null && StringUtils.isNotBlank(imei.toString().trim())){
        imeiSet.add(imei.toString().trim());
      }

      Object mobile = data.get(ObdImportConstants.FieldName.MOBILE_DESC);
      if(mobile!=null && StringUtils.isNotBlank(mobile.toString().trim())){
        mobileSet.add(mobile.toString().trim());
      }

      Object mobileNo = data.get(ObdImportConstants.FieldName.SIM_NO_DESC);
      if(mobileNo!=null && StringUtils.isNotBlank(mobileNo.toString().trim())){
        mobileNoSet.add(mobileNo.toString().trim());
      }
    }
    List<Map<String, Object>> failDataList = new ArrayList<Map<String, Object>>();
    checkResult.setFailDataList(failDataList);
    String fieldCheckResult = null;
    Map<String,ObdDTO> obdDTOMap = obdManagerService.getImeiObdDTOMap(imeiSet, ObdType.GSM);
    Map<String,ObdSimDTO> mobileObdSimDTOMap = obdManagerService.getMobileObdSimDTOMap(mobileSet);
    Map<String,ObdSimDTO> mobileNoObdSimDTOMap = obdManagerService.getSimNoObdSimDTOMap(mobileNoSet);

    ValidateImportDataDTO validateImportDataDTO = new ValidateImportDataDTO();
    validateImportDataDTO.setImeiObdDTOMap(obdDTOMap);
    validateImportDataDTO.setMobileObdSimDTOMap(mobileObdSimDTOMap);
    validateImportDataDTO.setMobileNoObdSimDTOMap(mobileNoObdSimDTOMap);

    Map<String,String> checkMobileRepeat = new HashMap<String, String>();
    Map<String,String> checkIMeiRepeat = new HashMap<String, String>();
    Map<String,String> checkMobileNoRepeat = new HashMap<String, String>();
    int headLineNum = 2;


    for(int index = 0;index < dataList.size();index++ ){
      Map<String,Object> data = dataList.get(index);
      if (data == null) {
        continue;
      }
      if (data.isEmpty()) {
        data.put("message", "第" + String.valueOf(index + headLineNum) +"行"
            + ExcelImportConstants.CheckResultMessage.EMPTY_DATA_ITEM_CONTENT);
        failDataList.add(data);
        if (!StringUtil.isEmpty(checkResult.getMessage())) {
          checkResult.setMessage(checkResult.getMessage() + "第" + (index + headLineNum) + "行"
              + ExcelImportConstants.CheckResultMessage.EMPTY_DATA_ITEM_CONTENT);
        }else{
          checkResult.setMessage("第" + String.valueOf(index + headLineNum) + "行"
              + ExcelImportConstants.CheckResultMessage.EMPTY_DATA_ITEM_CONTENT);
        }
        continue;
      }
      //校验imei号，SIM卡号，mobile
      fieldCheckResult = obdImportVerifier.verify(data, fieldMapping,validateImportDataDTO);
      if (!StringUtil.isEmpty(fieldCheckResult)) {
        data.put("message", "第" + String.valueOf(index + headLineNum) +"行"+fieldCheckResult);
        failDataList.add(data);
        if (!StringUtil.isEmpty(checkResult.getMessage())) {
          checkResult.setMessage( checkResult.getMessage() +"第" + String.valueOf(index + headLineNum) +"行" + fieldCheckResult );
        }else{
          checkResult.setMessage( "第" + String.valueOf(index + headLineNum) +"行" + fieldCheckResult );
        }
      }
      //校验excel 内部是否重复

      Object imei = data.get(fieldMapping.get(ObdImportConstants.FieldName.IMEI));
      Object simNo = data.get(fieldMapping.get(ObdImportConstants.FieldName.SIM_NO));
      Object mobile = data.get(fieldMapping.get(ObdImportConstants.FieldName.MOBILE));
      if(imei != null && StringUtils.isNotBlank(imei.toString().trim())){
        checkDataMapChange(checkIMeiRepeat,imei.toString().trim(),String.valueOf(index + headLineNum));
      }
      if(simNo != null && StringUtils.isNotBlank(simNo.toString().trim())){
        checkDataMapChange(checkMobileNoRepeat,simNo.toString().trim(),String.valueOf(index + headLineNum));
      }
      if(mobile != null && StringUtils.isNotBlank(mobile.toString().trim())){
        checkDataMapChange(checkMobileRepeat,mobile.toString().trim(),String.valueOf(index + headLineNum));
      }
    }

    String str = getCheckRepeatInfo(checkIMeiRepeat,"IMEI号");
    if (StringUtils.isNotBlank(str)) {
      checkResult.setMessage(StringUtils.isBlank(checkResult.getMessage()) ? str : checkResult.getMessage() + str);
    }

    String str2 = getCheckRepeatInfo(checkMobileNoRepeat, "SIM卡编号");
    if (StringUtils.isNotBlank(str2)) {
      checkResult.setMessage(StringUtils.isBlank(checkResult.getMessage()) ? str2 : checkResult.getMessage() + str2);
    }

    String str3 = getCheckRepeatInfo(checkMobileRepeat, "手机号");
    if (StringUtils.isNotBlank(str3)) {
      checkResult.setMessage(StringUtils.isBlank(checkResult.getMessage()) ? str3 : checkResult.getMessage() + str3);
    }
    if(StringUtils.isEmpty(checkResult.getMessage())){
      checkResult.setPass(true);
    }
    return checkResult;
  }


  public boolean validateExcelHeadContext(List<String> headList) {
    List<String> standHeadFieldList = ObdImportConstants.headFieldList;
    if (CollectionUtils.isEmpty(headList) || standHeadFieldList.size() != headList.size()) {
      return false;
    }
    Set<String> validateHeadList = new HashSet<String>();
    for (String str : headList) {
      if(StringUtils.isNotBlank(str)){
        validateHeadList.add(str);
      }
      if (!standHeadFieldList.contains(str) ) {
        return false;
      }
    }
    if(validateHeadList.size() != standHeadFieldList.size()){
      return false;
    }
    return true;
  }

  //检查导入数据文件中数据是否重复
  public void checkDataMapChange(Map<String, String> map, String key, String line) {
    if (StringUtils.isBlank(key)) {
      return;
    }
    String str = map.get(key);
    if (StringUtils.isBlank(str)) {
      map.put(key, 1 + "_2");
    } else {
      String[] strs = str.split("_");
      strs[0] = String.valueOf(Integer.valueOf(strs[0]) + 1);
      map.put(key, StringUtils.join(strs, "_") + "_" + line);
    }
  }

  public String getCheckRepeatInfo(Map<String, String> map, String sceneInfo) {
    String str = "";
    if (map.size() != 0) {
      for (String key : map.keySet()) {
        if (Integer.valueOf(map.get(key).split("_")[0]) > 1) {
          String[] strs = map.get(key).split("_");
          str += "第" + StringUtils.join(strs, ",", 1, strs.length) + "行的" + sceneInfo + key + "相同！";
        }
      }
    }

    return str;
  }
}
