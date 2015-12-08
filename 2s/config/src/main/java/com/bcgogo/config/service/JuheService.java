package com.bcgogo.config.service;

import com.bcgogo.api.ApiArea;
import com.bcgogo.api.response.ApiVehicleViolateRegulationResponse;
import com.bcgogo.common.Result;
import com.bcgogo.config.cache.AreaCacheManager;
import com.bcgogo.config.dto.AreaDTO;
import com.bcgogo.config.dto.juhe.*;
import com.bcgogo.config.model.*;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.enums.app.MessageCode;
import com.bcgogo.enums.app.VRegulationRecordQueryType;
import com.bcgogo.enums.app.ValidateConstant;
import com.bcgogo.enums.app.ValidateMsg;
import com.bcgogo.enums.config.JuheStatus;
import com.bcgogo.http.BcgogoHttpRequest;
import com.bcgogo.http.BcgogoHttpResponse;
import com.bcgogo.utils.*;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: ZhangJuntao
 * Date: 13-10-24
 * Time: 下午3:08
 */
@Component
public class JuheService implements IJuheService {
  private static final Logger LOG = LoggerFactory.getLogger(JuheService.class);

  private static final String JUHE_RESULT_REASON = "查询成功";
  private static final String JUHE_RESULT_CODE = "200";

  @Autowired
  private ConfigDaoManager configDaoManager;

  @Override
  public void initJuheViolateRegulationCitySearchCondition(Map<String, ViolateRegulationCitySearchConditionProvince> map) {
    if (MapUtils.isEmpty(map)) return;
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try {
      writer.deleteJuheViolateRegulationCitySearchCondition();
      JuheViolateRegulationCitySearchCondition searchCondition;
      for (Map.Entry<String, ViolateRegulationCitySearchConditionProvince> entry : map.entrySet()) {
        String key = entry.getKey();
        ViolateRegulationCitySearchConditionProvince province = entry.getValue();
        if (CollectionUtil.isEmpty(province.getCitys())) {
          continue;
        }
        for (ViolateRegulationCitySearchConditionCity city : province.getCitys()) {
          searchCondition = new JuheViolateRegulationCitySearchCondition(city, key, province.getProvince());
          writer.save(searchCondition);

          List<Area> areaList = writer.getAreaLikeCityName(city.getCity_name());

          if (CollectionUtil.isNotEmpty(areaList) && areaList.size() == 1) {
            Area area = CollectionUtil.getFirst(areaList);
            if (StringUtil.isEmpty(area.getJuheCityCode()) && area.getName().indexOf("省") == -1) {
              area.setJuheCityCode(city.getCity_code());
              writer.update(area);
            }
          } else if (CollectionUtil.isEmpty(areaList)) {
          } else if (CollectionUtil.isNotEmpty(areaList) && areaList.size() > 1) {
            for (Area area : areaList) {
              if (StringUtil.isEmpty(area.getJuheCityCode())) {

                if ((area.getName().length() - city.getCity_name().length() == 1)
                  && (area.getName().equals(city.getCity_name() + "县") || area.getName().equals(city.getCity_name() + "市")
                  || area.getName().equals(city.getCity_name() + "州") || area.getName().equals(city.getCity_name() + "盟") || area.getName().equals(city.getCity_name() + "区"))) {
                  area.setJuheCityCode(city.getCity_code());
                  writer.update(area);
                }
              }
            }
          }
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }


  @Override
  public List<ApiArea> obtainJuheSupportAreaAndViolateRegulations() {
    ConfigReader reader = configDaoManager.getReader();
    List<JuheViolateRegulationCitySearchCondition> conditionList = reader.getActiveJuheViolateRegulationCitySearchCondition();
    Map<String, JuheViolateRegulationCitySearchConditionDTO> map = new HashMap<String, JuheViolateRegulationCitySearchConditionDTO>();
    for (JuheViolateRegulationCitySearchCondition condition : conditionList) {
      map.put(condition.getCityCode(), condition.toDTO());
    }
    AreaDTO areaDTO = AreaCacheManager.getAreaDTOByNo(1l);
    List<ApiArea> apiAreaList = areaDTO.getChildJuheList(map);
    for (ApiArea apiArea : apiAreaList) {
      areaDTO = AreaCacheManager.getAreaDTOByNo(apiArea.getId());
      apiArea.setChildren(areaDTO.getChildrenJuheList(map));
    }
    return apiAreaList;
  }

  /**
   * 查询未处理，未交罚款的记录
   *
   * @param city
   * @param hphm
   * @param hpzl
   * @param engineNo
   * @param classno
   * @param registno
   * @return
   */
  public Result queryUnHandledVehicleViolateRegulation(String city, String hphm, String hpzl, String engineNo, String classno, String registno) {
    Result result = new Result();

    ApiVehicleViolateRegulationResponse response = queryVehicleViolateRegulation(city, hphm, hpzl, engineNo, classno, registno);
    if ("SUCCESS".equals(response.getStatus())) {
      List<VehicleViolateRegulationRecordDTO> qRecordDTOs = response.getQueryResponse().getResult().getLists();
      if (CollectionUtil.isEmpty(qRecordDTOs)) {
        return result;
      }
      List<VehicleViolateRegulationRecordDTO> recordDTOs = new ArrayList<VehicleViolateRegulationRecordDTO>();
      for (VehicleViolateRegulationRecordDTO recordDTO : qRecordDTOs) {
        if ("1".equals(recordDTO.getHandled())) {
          continue;
        }
        recordDTOs.add(recordDTO);
      }
      result.setData(recordDTOs);
      return result;
    } else {
      return result.LogErrorMsg(response.getMessage());
    }
  }

  /**
   * @param city     城市代码 *
   * @param hphm     号牌号码 完整7位 *
   * @param hpzl     号牌种类编号 (参考号牌种类接口)*
   * @param engineNo 发动机号 (根据城市接口中的参数填写)
   * @param classno  车架号 (根据城市接口中的参数填写)
   * @param registno 车辆登记证书号 (根据城市接口中的参数填写)
   * @return
   */
  public ApiVehicleViolateRegulationResponse queryVehicleViolateRegulation(String city, String hphm, String hpzl, String engineNo, String classno, String registno) {
        StopWatchUtil sw = new StopWatchUtil("queryVehicleViolateRegulation", "start");
    Result result = this.validateQueryVRegulationParam(city, hphm, hpzl, engineNo, classno, registno);
    ApiVehicleViolateRegulationResponse apiVehicleViolateRegulationResponse = null;
    if (!result.isSuccess()) {
      apiVehicleViolateRegulationResponse = new ApiVehicleViolateRegulationResponse(MessageCode.toApiResponse(MessageCode.VEHICLE_VIOLATE_REGULATION_QUERY_FAIL, result.getMsg()));
      return apiVehicleViolateRegulationResponse;
    }
    ConfigReader configReader = configDaoManager.getReader();
    //城市是否支持查询
    sw.stopAndStart("getJuheViolateRegulationCitySearchCondition");
    JuheViolateRegulationCitySearchCondition condition = CollectionUtil.getFirst(configReader.getJuheViolateRegulationCitySearchCondition(city, JuheStatus.ACTIVE));
    if (condition == null) {
      apiVehicleViolateRegulationResponse = new ApiVehicleViolateRegulationResponse(MessageCode.toApiResponse(MessageCode.VEHICLE_VIOLATE_REGULATION_QUERY_FAIL, ValidateMsg.DO_NOT_SUPPORT_THE_CITY));
      return apiVehicleViolateRegulationResponse;
    }
    String validateResult = this.validateQueryVRegulationCondition(condition, engineNo, classno, registno);
    if (StringUtil.isNotEmpty(validateResult)) {
      apiVehicleViolateRegulationResponse = new ApiVehicleViolateRegulationResponse(MessageCode.toApiResponse(MessageCode.VEHICLE_VIOLATE_REGULATION_QUERY_FAIL, validateResult));
      return apiVehicleViolateRegulationResponse;
    }
    sw.stopAndStart("handler-param");
    //处理查询参数
    if (condition.getEngine() != 0 && condition.getEngineNo() != 0 && engineNo != null && condition.getEngineNo() < engineNo.length()) {
      engineNo = engineNo.substring(engineNo.length() - condition.getEngineNo(), engineNo.length());
    }
    if (condition.getClassa() != 0 && condition.getClassNo() != 0 && classno != null && condition.getClassNo() < classno.length()) {
      classno = classno.substring(classno.length() - condition.getClassNo(), classno.length());
    }
    if (condition.getRegist() != 0 && condition.getRegistNo() != 0 && registno != null && condition.getRegistNo() < registno.length()) {
      registno = registno.substring(registno.length() - condition.getRegistNo(), registno.length());
    }


    apiVehicleViolateRegulationResponse = new ApiVehicleViolateRegulationResponse(MessageCode.toApiResponse(MessageCode.VEHICLE_VIOLATE_REGULATION_QUERY_SUCCESS));

    VehicleViolateRegulationCityQueryResponse queryResponse = new VehicleViolateRegulationCityQueryResponse(JUHE_RESULT_CODE, JUHE_RESULT_REASON, null);
    VehicleViolateRegulationCityQueryResult qResult = new VehicleViolateRegulationCityQueryResult(null, city, hphm, hpzl, null);

    try {
      //先查数据库
      sw.stopAndStart("queryDb");
      Long queryDate = DateUtil.getLastWeekStartTime();
      List<VehicleViolateRegulationQueryRecord> queryRecordList = configReader.getVehicleViolateRegulationQueryRecord(city, hphm, queryDate, JUHE_RESULT_CODE);
      //组装违章详细信息
      if (CollectionUtil.isNotEmpty(queryRecordList)) {
        LOG.info("get VehicleViolateRegulationQueryRecord from db");
        List<VehicleViolateRegulationRecordDTO> recordDTOList = this.getVehicleViolateRegulationRecord(city, hphm, null);
        qResult.setLists(recordDTOList);
        queryResponse.setResult(qResult);
        apiVehicleViolateRegulationResponse.setQueryResponse(queryResponse);
      }
      if (CollectionUtil.isNotEmpty(queryRecordList) && CollectionUtil.isNotEmpty(getVehicleViolateRegulationRecord(city, hphm, null))) {
         sw.stopAndStart("get-from-db");
        return apiVehicleViolateRegulationResponse;
      } else {
        LOG.info("http post,query VRegulation From Juhe");
        List list = this.queryVRegulationFromJuhe(city, hphm, hpzl, engineNo, classno, registno, ConfigUtils.getJuheViolateRegulationKey(), VRegulationRecordQueryType.APP_USER);
        return (ApiVehicleViolateRegulationResponse) CollectionUtil.getFirst(list);
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      LOG.error("查询违章记录异常！");
      apiVehicleViolateRegulationResponse =
        new ApiVehicleViolateRegulationResponse(MessageCode.toApiResponse(MessageCode.VEHICLE_VIOLATE_REGULATION_QUERY_EXCEPTION));
    }
    sw.stopAndPrintLog();
    return apiVehicleViolateRegulationResponse;
  }


  public void saveVehicleViolateRegulationQueryRecord(VehicleViolateRegulationQueryRecordDTO dto) {
    ConfigWriter configWriter = configDaoManager.getWriter();
    Object status = configWriter.begin();
    try {
      VehicleViolateRegulationQueryRecord record = new VehicleViolateRegulationQueryRecord(dto);
      configWriter.save(record);
      configWriter.commit(status);
    } finally {
      configWriter.rollback(status);
    }
  }

  public boolean saveVehicleViolateRegulationRecord(List<VehicleViolateRegulationRecordDTO> vehicleViolateRegulationRecordDTOs, String city, String vehicleNo, Long recordDate, VRegulationRecordQueryType queryType) {
    boolean hasNewVRegulationNRecord = false;
    if (CollectionUtil.isEmpty(vehicleViolateRegulationRecordDTOs)) {
      return hasNewVRegulationNRecord;
    }
    List<VehicleViolateRegulationRecordDTO> dbRecordDTOList = this.getVehicleViolateRegulationRecord(city, vehicleNo, null);
    Map<String, VehicleViolateRegulationRecordDTO> recordDTOMap = new HashMap<String, VehicleViolateRegulationRecordDTO>();
    if (CollectionUtil.isNotEmpty(dbRecordDTOList)) {
      for (VehicleViolateRegulationRecordDTO recordDTO : dbRecordDTOList) {
        recordDTOMap.put(recordDTO.getCity() + recordDTO.getDate().substring(0,10) + vehicleNo, recordDTO);
      }
    }
    List<VehicleViolateRegulationRecordDTO> recordDTOs = new ArrayList<VehicleViolateRegulationRecordDTO>();
    for (VehicleViolateRegulationRecordDTO regulationRecordDTO : vehicleViolateRegulationRecordDTOs) {
      regulationRecordDTO.formatDate();
      VehicleViolateRegulationRecordDTO dbRecordDTO = recordDTOMap.get(city + regulationRecordDTO.getDate() + vehicleNo);
      if (dbRecordDTO != null) {
        regulationRecordDTO.setId(dbRecordDTO.getId());
      }else {
        hasNewVRegulationNRecord = true;
      }
      regulationRecordDTO.setVehicleNo(vehicleNo);
      regulationRecordDTO.setCity(city);
      regulationRecordDTO.setRecordDate(recordDate);
      regulationRecordDTO.setQueryType(queryType);
      recordDTOs.add(regulationRecordDTO);
    }
    saveOrUpdateVehicleViolateRegulationRecord(recordDTOs.toArray(new VehicleViolateRegulationRecordDTO[recordDTOs.size()]));
    return hasNewVRegulationNRecord;
  }

  private Result validateQueryVRegulationParam(String city, String vehicleNo, String hpzl, String engineno, String classno, String registno) {
    if (StringUtil.isEmpty(city)) {
      return new Result(false, "未输入城市");
    }
    if (StringUtil.isEmpty(vehicleNo)) {
      return new Result(false, "请输入车牌号");
    }
    return new Result();
  }

  private String validateQueryVRegulationCondition(JuheViolateRegulationCitySearchCondition condition, String engineNo, String classNo, String registNo) {
    if (condition.getEngine() == ValidateConstant.PARAM_VIOLATE_REGULATION_REQUIRED && StringUtil.isEmpty(engineNo)) {
      return "请填写发动机号";
    }
    if (condition.getClassa() == ValidateConstant.PARAM_VIOLATE_REGULATION_REQUIRED && StringUtil.isEmpty(classNo)) {
      return "请填写车架号";
    }
    if (condition.getRegist() == ValidateConstant.PARAM_VIOLATE_REGULATION_REQUIRED && StringUtil.isEmpty(registNo)) {
      return "请填写登记证书号";
    }
    return null;

  }

  @Override
  public void saveOrUpdateVehicleViolateRegulationRecord(VehicleViolateRegulationRecordDTO... recordDTOs) {
    if (ArrayUtil.isEmpty(recordDTOs)) return;
    ConfigWriter writer = configDaoManager.getWriter();
    VehicleViolateRegulationRecord record = null;
    Object status = writer.begin();
    try {
      for (VehicleViolateRegulationRecordDTO recordDTO : recordDTOs) {
        if (recordDTO.getId() != null) {
          record = writer.getById(VehicleViolateRegulationRecord.class, recordDTO.getId());
          if (record == null) {
            LOG.error("VehicleViolateRegulationRecord isn't existed,id is {}", recordDTO.getId());
            continue;
          }
        } else {
          record = new VehicleViolateRegulationRecord();
        }
        record.fromDTO(recordDTO);
        writer.saveOrUpdate(record);
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public List<VehicleViolateRegulationRecordDTO> getVehicleViolateRegulationRecord(String city, String vehicleNo, Long recordDate) {
    List<VehicleViolateRegulationRecordDTO> recordDTOs = new ArrayList<VehicleViolateRegulationRecordDTO>();
    ConfigReader configReader = configDaoManager.getReader();
    List<VehicleViolateRegulationRecord> recordList = configReader.getVehicleViolateRegulationRecord(city, vehicleNo, recordDate);
    if (CollectionUtil.isEmpty(recordList)) {
      return recordDTOs;
    }
    for (VehicleViolateRegulationRecord record : recordList) {
      recordDTOs.add(record.toDTO());
    }
    return recordDTOs;
  }

  public List<JuheViolateRegulationCitySearchConditionDTO> getJuheViolateRegulationCitySearchCondition(String juheCityCode, JuheStatus status) {
    ConfigReader configReader = configDaoManager.getReader();
    List<JuheViolateRegulationCitySearchCondition> conditions = configReader.getJuheViolateRegulationCitySearchCondition(juheCityCode, status);
    List<JuheViolateRegulationCitySearchConditionDTO> conditionDTOs = new ArrayList<JuheViolateRegulationCitySearchConditionDTO>();
    if (CollectionUtil.isEmpty(conditions)) {
      return conditionDTOs;
    }
    for (JuheViolateRegulationCitySearchCondition condition : conditions) {
      conditionDTOs.add(condition.toDTO());
    }
    return conditionDTOs;
  }

  public Map<String, JuheViolateRegulationCitySearchConditionDTO> getJuheSearchCondition() {
    ConfigReader configReader = configDaoManager.getReader();

    List<JuheViolateRegulationCitySearchCondition> searchConditions = configReader.getActiveJuheViolateRegulationCitySearchCondition();

    Map<String, JuheViolateRegulationCitySearchConditionDTO> map = new HashMap<String, JuheViolateRegulationCitySearchConditionDTO>();
    if (CollectionUtil.isNotEmpty(searchConditions)) {
      for (JuheViolateRegulationCitySearchCondition condition : searchConditions) {
        map.put(condition.getCityCode(), condition.toDTO());
      }
    }
    return map;

  }

  public List queryVRegulationFromJuhe(String city, String hphm, String hpzl, String engineno, String classno, String registno, String key, VRegulationRecordQueryType queryType) {
    List result = new ArrayList();
    ApiVehicleViolateRegulationResponse apiVehicleViolateRegulationResponse = null;
    Boolean hasNewVRegulationNRecord = false;
    try {
      BcgogoHttpRequest bcgogoHttpRequest = new BcgogoHttpRequest();

      StringBuffer stringBuffer = new StringBuffer();
      stringBuffer.append("http://v.juhe.cn/wz/query?city=" + city + "&hphm=" + URLEncoder.encode(hphm, "UTF-8") + "&hpzl=" + hpzl + "&key=" + key);

      if (StringUtil.isNotEmptyAppGetParameter(engineno)) {
        stringBuffer.append("&engineno=" + URLEncoder.encode(engineno.toUpperCase(), "UTF-8"));
      }
      if (StringUtil.isNotEmptyAppGetParameter(classno)) {
        stringBuffer.append("&classno=" + URLEncoder.encode(classno.toUpperCase(), "UTF-8"));
      }
      if (StringUtil.isNotEmptyAppGetParameter(registno)) {
        stringBuffer.append("&registno=" + URLEncoder.encode(registno.toUpperCase(), "UTF-8"));
      }
      BcgogoHttpResponse bcgogoHttpResponse = bcgogoHttpRequest.sendPost(stringBuffer.toString());

      LOG.info(bcgogoHttpResponse.getContent());

      boolean hasViolateRegulationRecord = true;
      String content = bcgogoHttpResponse.getContent();
      if (StringUtil.isNotEmpty(content) && content.indexOf("\"lists\":[]") != -1) {
        content = content.replace("\"lists\":[]", "\"lists\":[{}]");
        hasViolateRegulationRecord = false;
      }

      VehicleViolateRegulationCityQueryResponse response = null;
      try {
        response = JsonUtil.fromJson(content, VehicleViolateRegulationCityQueryResponse.class);
      } catch (Exception e) {
        LOG.error(e.getMessage(), e);
      }

      boolean isSuccess = false;
      String message = "";

      if (response != null) {
        VehicleViolateRegulationQueryRecordDTO queryRecordDTO = new VehicleViolateRegulationQueryRecordDTO(response, city, hphm, System.currentTimeMillis(), queryType);
        this.saveVehicleViolateRegulationQueryRecord(queryRecordDTO);
        isSuccess = response.isSuccess();
        message = response.getReason();
        VehicleViolateRegulationCityQueryResult queryResult = response.getResult();
        if (queryResult != null && CollectionUtil.isNotEmpty(queryResult.getLists()) && hasViolateRegulationRecord) {
          hasNewVRegulationNRecord = this.saveVehicleViolateRegulationRecord(queryResult.getLists(), city, hphm, System.currentTimeMillis(), queryType);
        } else {
          if (queryResult == null) {
            queryResult = new VehicleViolateRegulationCityQueryResult();
          }
          message = response.getReason();
          queryResult.setLists(null);
        }
      }

      if (isSuccess) {
        apiVehicleViolateRegulationResponse = new ApiVehicleViolateRegulationResponse(MessageCode.toApiResponse(MessageCode.VEHICLE_VIOLATE_REGULATION_QUERY_SUCCESS));
      } else {
        apiVehicleViolateRegulationResponse = new ApiVehicleViolateRegulationResponse(MessageCode.toApiResponse(MessageCode.VEHICLE_VIOLATE_REGULATION_QUERY_FAIL));
        apiVehicleViolateRegulationResponse.setMessage("违章查询失败");
      }
      if (StringUtil.isNotEmpty(message)) {
        apiVehicleViolateRegulationResponse.setMessage(message);
      }
      apiVehicleViolateRegulationResponse.setQueryResponse(response);
    } catch (IOException e) {
      apiVehicleViolateRegulationResponse =
        new ApiVehicleViolateRegulationResponse(MessageCode.toApiResponse(MessageCode.VEHICLE_VIOLATE_REGULATION_QUERY_EXCEPTION));
    }
    result.add(apiVehicleViolateRegulationResponse);
    result.add(hasNewVRegulationNRecord);
    return result;
  }

  @Override
  public JuheViolateRegulationCitySearchCondition getJuheViolateRegulationCitySearchConditionByCityName(String cityName) {
    JuheViolateRegulationCitySearchCondition juheViolateRegulationCitySearchCondition
        = null;
    ConfigReader configReader = configDaoManager.getReader();
    juheViolateRegulationCitySearchCondition = configReader.getJuheViolateRegulationCitySearchConditionByCityName(cityName);
    return juheViolateRegulationCitySearchCondition;
  }
}
