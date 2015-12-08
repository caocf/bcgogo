package com.bcgogo.search.service.vehicle;

import com.bcgogo.search.dto.SearchConditionDTO;
import com.bcgogo.search.dto.VehicleSearchConditionDTO;
import com.bcgogo.search.dto.VehicleSearchResultDTO;
import com.bcgogo.txn.dto.CarDTO;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: xzhu
 * Date: 8/2/12
 * Time: 2:49 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ISearchVehicleService {
  /**
   * 没有 字母分类 A  B  C
   * @param searchConditionDTO
   * @return
   * @throws Exception
   */
  public List<String> getVehicleSuggestionListByKeywords(SearchConditionDTO searchConditionDTO) throws Exception;
  /**
   * 有 字母分类 A  B  C
   * @param searchConditionDTO
   * @return
   * @throws Exception
   */
  public List<String> getVehicleSuggestionList(SearchConditionDTO searchConditionDTO) throws Exception;


  /**
   * 车牌号下拉建议
   * @param shopId
   * @param searchWord
   * @return
   * @throws Exception
   */
  List<CarDTO> queryVehicleLicenseNoSuggestion(Long shopId,String searchWord) throws Exception;

  VehicleSearchResultDTO queryVehicle(VehicleSearchConditionDTO vehicleSearchConditionDTO) throws Exception;


  /**
   * 发动机号 车架号建议下拉
   * @param shopId
   * @param searchWord
   * @return
   * @throws Exception
   */
  List<Map> getVehicleEngineNoClassNoSuggestion(Long shopId,String searchWord,String searchField) throws Exception;

  public Map<String,String> queryVehicleNoForVehiclePosition(VehicleSearchConditionDTO vehicleSearchConditionDTO) throws Exception;
  }
