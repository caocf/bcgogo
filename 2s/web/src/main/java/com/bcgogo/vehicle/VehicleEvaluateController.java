package com.bcgogo.vehicle;

import com.bcgogo.common.Result;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.service.vEvaluate.Car360EvaluateService;
import com.bcgogo.user.service.vEvaluate.IVehicleEvaluateService;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;
import com.bcgogo.utils.UserAgentUtil;
import com.bcgogo.vehicle.evalute.EvaluateDataSource;
import com.bcgogo.vehicle.evalute.EvaluateRecordDTO;
import com.bcgogo.vehicle.evalute.EvaluateResult;
import com.bcgogo.vehicle.evalute.car360.Car360EvaluateCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 14-11-6
 * Time: 下午1:24
 */
@Controller
@RequestMapping("/evaluate")
public class VehicleEvaluateController {
  private static final Logger LOG = LoggerFactory.getLogger(VehicleEvaluateController.class);

  @Autowired
  private Car360EvaluateService evaluateService;

  @RequestMapping(value = "/getVehicleModelDTOs")
  @ResponseBody
  public Object getVehicleModelDTOs(String seriesId){
    try{
      return evaluateService.getVehicleModelDTOs(seriesId);
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
      return null;
    }
  }

  @RequestMapping(value = "/getVehicleSeriesDTOs")
  @ResponseBody
  public Object getVehicleSeriesDTOs(String brandId){
    try{
      return evaluateService.getVehicleSeriesDTOs(brandId);
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
      return null;
    }
  }

  @RequestMapping(value = "/getAreaDTOByNo")
  @ResponseBody
  public Object getAreaDTOByNo(Long parentNo){
    try{
      return evaluateService.getAreaDTOByNo(parentNo);
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
      return null;
    }
  }

  @RequestMapping(value = "/getLastEvaluateRecord")
  @ResponseBody
  public Object getLastEvaluateRecord(String vehicleNo){
    try{
      if(StringUtil.isEmpty(vehicleNo)) return null;
      return evaluateService.getLastEvaluateRecordDTOByVehicleNo(vehicleNo);
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
      return null;
    }
  }

  /**
   * 精准评估
   * @param openId
   * @param condition
   * @return
   */
  @RequestMapping(value = "/evaluate")
  @ResponseBody
  public Object vehicleEvaluate(String openId,Car360EvaluateCondition condition){

    try{
      EvaluateResult result=evaluateService.validateEvaluateCondition(condition);
      if(!result.isSuccess()) return result;
      //转化成接口标准 万公里
      Double mile=NumberUtil.doubleVal(condition.getMile())/10000;
      condition.setMile(StringUtil.valueOf(mile));
      //转化成接口标准 时间格式
      condition.setRegDate(condition.getRegDate().replace("月","")); //todo delete
      condition.setRegDate(condition.getRegDate().replace("年","")); //todo delete
      result= evaluateService.evaluate(condition);
      EvaluateRecordDTO recordDTO=new EvaluateRecordDTO();
      recordDTO.setOpenId(openId);
      setCar360EvaluateCondition(recordDTO,condition);
      setCar360EvaluateResult(recordDTO, result);
      evaluateService.saveOrUpdateEvaluateRecord(recordDTO);
      return result;
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
      return new Result(false,"网络异常");
    }
  }

  private void setCar360EvaluateResult(EvaluateRecordDTO recordDTO,EvaluateResult result){
    if(recordDTO==null||result==null) return;
    recordDTO.setEvalPrice(NumberUtil.doubleVal(result.getEvalPrice()));
    recordDTO.setEvalDate(System.currentTimeMillis());
    recordDTO.setLowPrice(NumberUtil.doubleVal(result.getLowPrice()));
    recordDTO.setGoodPrice(NumberUtil.doubleVal(result.getGoodPrice()));
    recordDTO.setHighPrice(NumberUtil.doubleVal(result.getHighPrice()));
    recordDTO.setSource(EvaluateDataSource.CAR300);
  }

  private void setCar360EvaluateCondition(EvaluateRecordDTO recordDTO,Car360EvaluateCondition condition){
    if(recordDTO==null||condition==null) return;
    recordDTO.setModelId(condition.getModelId());
    recordDTO.setMile(condition.getMile());
    recordDTO.setVehicleNo(condition.getVehicleNo());
    try {
      recordDTO.setAreaId(condition.getZone());
      recordDTO.setRegDate(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_YEAR_MON, condition.getRegDate()));
    } catch (ParseException e) {
      LOG.error(e.getMessage(),e);
    }
  }


}
