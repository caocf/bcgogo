package com.bcgogo.user.service.vEvaluate;

import com.bcgogo.api.response.HttpResponse;
import com.bcgogo.config.dto.AreaDTO;
import com.bcgogo.product.standardVehicleBrandModel.StandardVehicleBrandDTO;
import com.bcgogo.product.standardVehicleBrandModel.StandardVehicleModelDTO;
import com.bcgogo.product.standardVehicleBrandModel.StandardVehicleSeriesDTO;
import com.bcgogo.utils.*;
import com.bcgogo.vehicle.evalute.EvaluateResult;
import com.bcgogo.vehicle.evalute.VEvaluteConstant;
import com.bcgogo.vehicle.evalute.car360.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

/**
 * Car360车辆价格评估
 * Author: ndong
 * Date: 14-11-5
 * Time: 上午10:42
 */
@Component
public class Car360EvaluateService extends AbstractVehicleEvaluateService{
  public static final Logger LOG = LoggerFactory.getLogger(Car360EvaluateService.class);

  private static Map<Long,AreaDTO> cityAreaMap =new HashMap<Long,AreaDTO>();

  private static Map<Long,AreaDTO> provAreaMap =new HashMap<Long,AreaDTO>();

  private static List<StandardVehicleBrandDTO> brandDTOs=new ArrayList<StandardVehicleBrandDTO>();

  private static Map<String,List<StandardVehicleSeriesDTO>> seriesMap=new HashMap<String, List<StandardVehicleSeriesDTO>>();

  private static Map<String,List<StandardVehicleModelDTO>> modelMap=new HashMap<String, List<StandardVehicleModelDTO>>();

  public static Map<String, List<StandardVehicleSeriesDTO>> getSeriesMap() {
    return seriesMap;
  }

  public static void setSeriesMap(Map<String, List<StandardVehicleSeriesDTO>> seriesMap) {
    Car360EvaluateService.seriesMap = seriesMap;
  }

  public static List<StandardVehicleBrandDTO> getBrandDTOs() {
    return brandDTOs;
  }

  public static void setBrandDTOs(List<StandardVehicleBrandDTO> brandDTOs) {
    Car360EvaluateService.brandDTOs = brandDTOs;
  }



  /**
   * 返回所估车型在指定地区的精确估值信息。
   * @param condition
   * @return
   */
  @Override
  public EvaluateResult evaluate(Car360EvaluateCondition condition) {
    try {
      String url= VEvaluteConstant.URL_GET_USED_CAR_PRICE.replace("{TOKEN}", VEvaluteConstant.TOKEN_CAR360)+condition.genQueryParam();
      HttpResponse response = HttpUtils.sendGet(url);
      GetCar360UsedCarPriceResult qResult= JsonUtil.jsonToObj(response.getContent(), GetCar360UsedCarPriceResult.class);
      EvaluateResult result=qResult.toEvaluateResult();
      return result;
    } catch (Exception e) {
      LOG.error(e.getMessage(),e);
      return null;
    }
  }



//  private synchronized String getCar360ModelName(String modelId){
//    if(StringUtil.isEmpty(modelId)){
//      return null;
//    }
//    return vModelMap.get(modelId);
//  }

  @Override
  public EvaluateResult validateEvaluateCondition(Car360EvaluateCondition condition){
    EvaluateResult result=new EvaluateResult();
    if(StringUtil.isNotEmpty(condition.getVehicleNo())&&!RegexUtils.isVehicleNo(condition.getVehicleNo())){
      return new EvaluateResult("您输入的车牌号“"+condition.getVehicleNo()+"”不符合车牌号格式，请检查后重新输入。",false);
    }
    if(StringUtil.isEmpty(condition.getModelId())){
      return new EvaluateResult("请选择估价车辆的车型。",false);
    }
    if(StringUtil.isEmpty(condition.getRegDate())){
      return new EvaluateResult("请选择估价车辆的上牌时间。",false);
    }
    if(StringUtil.isEmpty(condition.getZone())){
      return new EvaluateResult("请选择估价车辆的所在地区。",false);
    }
    if(!NumberUtil.isNumber(condition.getMile())||StringUtil.isEmpty(condition.getMile())){
      return new EvaluateResult("您填写的车辆里程不正确。",false);
    }
    return result;
  }

  /**
   * 查询车辆品牌
   * @return
   */
  @Override
  public List<StandardVehicleBrandDTO> getVehicleBrandDTOs() {
    try {
      return getCachedVehicleBrandDTOs();
    } catch (Exception e) {
      LOG.error(e.getMessage(),e);
      return null;
    }
  }

  @Override
  public List<StandardVehicleSeriesDTO> getVehicleSeriesDTOs(String brandId) {
    try {
      if(StringUtil.isEmpty(brandId)) return null;
      return getCachedVehicleSeriesDTOs(brandId);
    } catch (Exception e) {
      LOG.error(e.getMessage(),e);
      return null;
    }
  }

  @Override
  public List<StandardVehicleModelDTO> getVehicleModelDTOs(String seriesId) {
    try {
      if(StringUtil.isEmpty(seriesId)) return null;
      return getCachedVehicleModelDTOs(seriesId);
    } catch (Exception e) {
      LOG.error(e.getMessage(),e);
      return null;
    }
  }


  /**
   * 查询估值城市
   * @param parentNo  0获取省，否则获取市
   * @return
   */
  @Override
  public synchronized List<AreaDTO> getAreaDTOByNo(Long parentNo){
    return getCachedAreaDTOByNo(parentNo);
  }

  /**
   * 返回所有的品牌列表。
   * @return
   * @throws IOException
   */
  private static GetCar360BrandResult getCar360VehicleBrandList() throws IOException {
    String url= VEvaluteConstant.URL_GET_CAR_BRAND_LIST.replace("{TOKEN}", VEvaluteConstant.TOKEN_CAR360);
    HttpResponse response = HttpUtils.sendGet(url);
    return JsonUtil.jsonToObj(response.getContent(), GetCar360BrandResult.class);
  }

  /**
   * 返回指定品牌下面的所有车系列表
   * @param brandId
   * @return
   * @throws IOException
   */
  private static GetCar360SeriesResult getCar360VehicleSeriesDTOs(String brandId) throws IOException {
    if(StringUtil.isEmpty(brandId)) return null;
    String url= VEvaluteConstant.URL_GET_CAR_SERIES_LIST.replace("{TOKEN}", VEvaluteConstant.TOKEN_CAR360);
    url+="&brandId="+brandId;
    HttpResponse response = HttpUtils.sendGet(url);
    return JsonUtil.jsonToObj(response.getContent(), GetCar360SeriesResult.class);
  }

  /**
   * 返回指定车系下面的所有车型
   * @param seriesId
   * @return
   * @throws IOException
   */
  private static GetCar360ModelResult getCar360VehicleModelDTOs(String seriesId) throws IOException {
    if(StringUtil.isEmpty(seriesId)) return null;
    String url= VEvaluteConstant.URL_GET_CAR_MODEL_LIST.replace("{TOKEN}", VEvaluteConstant.TOKEN_CAR360);
    url+="&seriesId="+seriesId;
    HttpResponse response = HttpUtils.sendGet(url);
    return JsonUtil.jsonToObj(response.getContent(), GetCar360ModelResult.class);
  }

  /**
   * 返回所有的城市列表
   * @return
   * @throws IOException
   */
  private static GetCar360CityResult getCar360AllCity() throws IOException {
    String url= VEvaluteConstant.URL_GET_ALL_CITY.replace("{TOKEN}", VEvaluteConstant.TOKEN_CAR360);
    HttpResponse response= HttpUtils.sendGet(url);
    GetCar360CityResult result= JsonUtil.jsonToObj(response.getContent(), GetCar360CityResult.class);
    return result;
  }

  @Override
  public String getCarList() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public String getCarDetail() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }
//
//  @Override
//   public boolean initAreaDTO(){
//    //init province
//    getAreaDTOByNo(null);
//    if(MapUtils.isEmpty(provAreaMap)) return false;
//    return true;
//  }

  /**
   *   parentNo 0获取省，否则获取市
   * @param parentNo
   * @return
   */
  public synchronized static List<AreaDTO> getCachedAreaDTOByNo(Long parentNo){
    if(parentNo==null||parentNo==0l){
      Collection<AreaDTO> areaDTOs= provAreaMap.values();
      if(CollectionUtil.isEmpty(areaDTOs)){
        synAreaDTO();
        areaDTOs=provAreaMap.values();
      }
      List<AreaDTO> areaDTOsTemp=new ArrayList<AreaDTO>();
      for(AreaDTO areaDTO:areaDTOs){
        areaDTOsTemp.add(areaDTO);
      }
      return areaDTOsTemp;
    }else {
      AreaDTO areaDTO=provAreaMap.get(parentNo);
      if(areaDTO==null){
        synAreaDTO();
        areaDTO=provAreaMap.get(parentNo);
      }
      return areaDTO.getChildAreaDTOList();
    }
  }

  /**
   *  缓存car300 车型列表
   * @return
   */
  private synchronized static List<StandardVehicleModelDTO> getCachedVehicleModelDTOs(String seriesId) throws IOException {
    if(StringUtil.isEmpty(seriesId)){
      return null;
    }
    List<StandardVehicleModelDTO> modelDTOs=modelMap.get(seriesId);
    if(CollectionUtil.isEmpty(modelDTOs)){
      GetCar360ModelResult result=getCar360VehicleModelDTOs(seriesId);
      List<Car360Model> car360Models=result.getModel_list();
      if(CollectionUtil.isEmpty(car360Models)) return null;
      modelDTOs=new ArrayList<StandardVehicleModelDTO>();
      for(Car360Model model:car360Models){
        modelDTOs.add(model.toStandardVehicleModelDTO());
      }
      modelMap.put(seriesId,modelDTOs);
    }
    return modelDTOs;
  }

  /**
   *  缓存car300 品牌列表
   * @return
   */
  private synchronized static List<StandardVehicleSeriesDTO> getCachedVehicleSeriesDTOs(String brandId) throws IOException {
    if(StringUtil.isEmpty(brandId)){
      return null;
    }
    List<StandardVehicleSeriesDTO> seriesDTOs=seriesMap.get(brandId);
    if(CollectionUtil.isEmpty(seriesDTOs)){
      GetCar360SeriesResult result=getCar360VehicleSeriesDTOs(brandId);
      List<Car360Series> car360SeriesList=result.getSeries_list();
      if(CollectionUtil.isEmpty(car360SeriesList)) return null;
      seriesDTOs=new ArrayList<StandardVehicleSeriesDTO>();
      for(Car360Series series:car360SeriesList){
        seriesDTOs.add(series.toStandardVehicleSeriesDTO());
      }
      seriesMap.put(brandId,seriesDTOs);
    }
    return seriesDTOs;
  }

  /**
   *  缓存car300 品牌列表
   * @return
   */
  private synchronized static List<StandardVehicleBrandDTO> getCachedVehicleBrandDTOs() throws IOException {
    if(CollectionUtil.isNotEmpty(brandDTOs)){
      return brandDTOs;
    }
    GetCar360BrandResult result=getCar360VehicleBrandList();
    List<Car360Brand> car360Brands=result.getBrand_list();
    if(CollectionUtil.isEmpty(car360Brands)) return null;
    for(Car360Brand brand:car360Brands){
      brandDTOs.add(brand.toStandardVehicleBrandDTO());
    }
    return brandDTOs;
  }

  private static boolean synAreaDTO(){
    //get city info from car360
    GetCar360CityResult result= null;
    try {
      result =getCar360AllCity();
    } catch (IOException e) {
      LOG.error(e.getMessage(),e);
      return false;
    }
    List<Car360City> city_list=result.getCity_list();
    if(CollectionUtil.isEmpty(city_list)) return false;
    //处理成标准area结构
    AreaDTO root=new AreaDTO();
    root.setNo(0l);
    cityAreaMap.put(root.getNo(),root);
    for(Car360City city:city_list){
      AreaDTO areaDTO=city.toAreaDTO();
      cityAreaMap.put(areaDTO.getNo(),areaDTO);
      //create province area
      if(provAreaMap.get(areaDTO.getParentNo())==null){
        AreaDTO provAreaDTO=new AreaDTO();
        provAreaDTO.setNo(NumberUtil.longValue(city.getProv_id()));
        provAreaDTO.setName(city.getProv_name());
        provAreaMap.put(provAreaDTO.getNo(),provAreaDTO);
      }
    }
    //build tree
    for(Long no:cityAreaMap.keySet()){
      AreaDTO areaDTO=cityAreaMap.get(no);
      AreaDTO pAreaDTO=provAreaMap.get(areaDTO.getParentNo());
      if(pAreaDTO==null){
        continue;
      }
      List<AreaDTO> childAreaDTOs=pAreaDTO.getChildAreaDTOList();
      if(CollectionUtil.isEmpty(childAreaDTOs)){
        childAreaDTOs=new ArrayList<AreaDTO>();
        pAreaDTO.setChildAreaDTOList(childAreaDTOs);
      }
      childAreaDTOs.add(areaDTO);
    }
    return true;
  }



}
