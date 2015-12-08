package com.bcgogo.user.service.excelimport.customer;

import com.bcgogo.config.cache.AreaCacheManager;
import com.bcgogo.config.dto.AreaDTO;
import com.bcgogo.config.dto.ImportResult;
import com.bcgogo.config.service.excelimport.*;
import com.bcgogo.constant.ImportConstants;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.*;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.user.service.IUserService;
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
 * 客户数据导入执行类
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-4-6
 * Time: 上午8:52
 * To change this template use File | Settings | File Templates.
 */
@Component
public class CustomerImporter extends BcgogoExcelDataImporter {

  private static final Logger LOG = LoggerFactory.getLogger(CustomerImporter.class);

  /**
   * 执行客户数据导入
   *
   * @param importContext
   * @return
   * @throws ExcelImportException
   */
  @Override
  public ImportResult importData(ImportContext importContext) throws Exception {
    IUserService userService = ServiceManager.getService(IUserService.class);
    ImportResult importResult = new ImportResult();
    List<Map<String, Object>> dataList = importContext.getDataList();
    importResult.setTotalCount(dataList.size());
    List<CustomerWithVehicleDTO> customerWithVehicleDTOList = new ArrayList<CustomerWithVehicleDTO>();
    CustomerWithVehicleDTO customerWithVehicleDTO = null;
    int successCount = 0;
    int failCount = 0;
    StringBuffer messageBuffer = new StringBuffer();
    for (int index = 0; index < dataList.size(); index++) {
      Map<String, Object> data = dataList.get(index);
      if (data == null || data.isEmpty()) {
        continue;
      }
      customerWithVehicleDTO = customerWithVehicleDTOGenerator.generate(data, importContext.getFieldMapping(), importContext.getShopId());
      if (customerWithVehicleDTO != null) {
        customerWithVehicleDTOList.add(customerWithVehicleDTO);
      }
      if (customerWithVehicleDTOList.size() >= BATCH_SAVE_SIZE || index >= dataList.size() - 1) {
        try {
          userService.batchCreateCustomerAndVehicle(customerWithVehicleDTOList);
          successCount += customerWithVehicleDTOList.size();
        } catch (BcgogoException e) {
          LOG.error("批量保存客户数据时发生异常 : " + e.getMessage(), e);
          failCount += customerWithVehicleDTOList.size();
          messageBuffer.append(e.getMessage());
        } finally {
          customerWithVehicleDTOList.clear();
        }
      }
    }
    //更新导入记录状态
    remarkImportRecordStatus(importContext.getImportRecordIdList(), importContext.getShopId(), successCount, ImportConstants.Type.TYPE_CUSTOMER);

    importResult.setFailCount(failCount);
    importResult.setSuccessCount(successCount);
    importResult.setMessage(messageBuffer.toString());
    return importResult;
  }

  /**
   * 执行客户数据校验
   *
   * @param importContext
   * @return
   * @throws ExcelImportException
   */
  @Override
  public CheckResult checkData(ImportContext importContext) throws BcgogoException {
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

    Map<String,String> checkChassisNumberRepeat = new HashMap<String,String>();
    Map<String,String> checkVehicleNoRepeat = new HashMap<String, String>();
    Map<String,String> checkMemberNoRepeat = new HashMap<String, String>();
    Map<String,String> checkMobileRepeat = new HashMap<String, String>();
    int headLineNum = 2;

    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    Map<String,CustomerDTO> mobileCustomerMap = customerService.getMobileCustomerMapOnlyForMobileCheck(importContext.getShopId());
    Map<String,CustomerDTO> landLineCustomerMap = customerService.getLandLineCustomerMap(importContext.getShopId());
    Map<String,VehicleDTO> vehicleDTOMap = userService.getVehicleDTOMap(importContext.getShopId());
    Map<String,MemberDTO> memberDTOMap = userService.getMemberMap(importContext.getShopId());
    Map<String,AreaDTO> areaDTOMap = AreaCacheManager.getAreaDTOMap();
    ValidateImportDataDTO validateImportDataDTO = new ValidateImportDataDTO();
    validateImportDataDTO.setMobileCustomerMap(mobileCustomerMap);
    validateImportDataDTO.setVehicleDTOMap(vehicleDTOMap);
    validateImportDataDTO.setMemberDTOMap(memberDTOMap);
    validateImportDataDTO.setLandLineCustomerMap(landLineCustomerMap);
    validateImportDataDTO.setAreaDTOMap(areaDTOMap);

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
      fieldCheckResult = customerImportVerifier.verify(data, fieldMapping,validateImportDataDTO);
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

      String chassisNumber = "";
      if(null != data.get(fieldMapping.get(CustomerImportConstants.FieldName.VEHICLE_CHASSIS_NUMBER)))
      {
        chassisNumber = String.valueOf(data.get(fieldMapping.get(CustomerImportConstants.FieldName.VEHICLE_CHASSIS_NUMBER)));
      }

      chassisNumber = chassisNumber.replace(" ","");
      checkDataMapChange(checkChassisNumberRepeat,chassisNumber,String.valueOf(index + headLineNum));

      String vehicleNos = "";
      if(null !=data.get(fieldMapping.get(CustomerImportConstants.FieldName.VEHICLE_LICENCE_NO)))
      {
        vehicleNos = String.valueOf(data.get(fieldMapping.get(CustomerImportConstants.FieldName.VEHICLE_LICENCE_NO)));
      }
      vehicleNos = vehicleNos.replace(" ","");
      if(StringUtils.isNotBlank(vehicleNos))
      {
        String[] vehicleArray = vehicleNos.split(",");

        for(String vehicle : vehicleArray)
        {
          checkDataMapChange(checkVehicleNoRepeat,vehicle,String.valueOf(index + headLineNum));
        }
      }

      String mobile = "";
      if(null != data.get(fieldMapping.get(CustomerImportConstants.FieldName.MOBILE)))
      {
        mobile = String.valueOf(data.get(fieldMapping.get(CustomerImportConstants.FieldName.MOBILE)));
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

      String memberNo = "";
      if(null != data.get(fieldMapping.get(CustomerImportConstants.FieldName.MEMBER_NO)))
      {
        memberNo = String.valueOf(data.get(fieldMapping.get(CustomerImportConstants.FieldName.MEMBER_NO)));
      }
      memberNo = memberNo.replace(" ","");
      checkDataMapChange(checkMemberNoRepeat,memberNo,String.valueOf(index + headLineNum));
    }

    String str = getCheckRepeatInfo(checkChassisNumberRepeat,"车架号");

    if(StringUtils.isNotBlank(str))
    {
      checkResult.setMessage(StringUtils.isBlank(checkResult.getMessage())?str:checkResult.getMessage()+str);
    }

    String str2 = getCheckRepeatInfo(checkVehicleNoRepeat,"车牌号");

    if(StringUtils.isNotBlank(str2))
    {
      checkResult.setMessage(StringUtils.isBlank(checkResult.getMessage())?str2:checkResult.getMessage()+str2);
    }

    String str3 = getCheckRepeatInfo(checkMobileRepeat,"手机号");

    if(StringUtils.isNotBlank(str3))
    {
      checkResult.setMessage(StringUtils.isBlank(checkResult.getMessage())?str3:checkResult.getMessage()+str3);
    }

    String str4 = getCheckRepeatInfo(checkMemberNoRepeat,"会员号");

    if(StringUtils.isNotBlank(str4))
    {
      checkResult.setMessage(StringUtils.isBlank(checkResult.getMessage())?str4:checkResult.getMessage()+str4);
    }

    checkChassisNumberRepeat.clear();
    checkVehicleNoRepeat.clear();
    checkMemberNoRepeat.clear();
    checkMobileRepeat.clear();
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
      map.put(key,"1_"+line);
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
  private CustomerImportVerifier customerImportVerifier;

  @Autowired
  private CustomerWithVehicleDTOGenerator customerWithVehicleDTOGenerator;
}
