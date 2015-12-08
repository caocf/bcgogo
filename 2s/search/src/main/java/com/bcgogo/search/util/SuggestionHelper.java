package com.bcgogo.search.util;

import com.bcgogo.search.dto.ProductSearchSuggestionListDTO;
import com.bcgogo.search.dto.SearchConditionDTO;
import com.bcgogo.search.dto.SearchSuggestionDTO;
import com.bcgogo.utils.ArrayUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-11-28
 * Time: 上午10:14
 * To change this template use File | Settings | File Templates.
 */
public class SuggestionHelper {
  private static String [] vehicleFields=new String[]{"product_vehicle_brand","product_vehicle_model"};

  public static Map<String, Object> generateProductDropDownMap(List<SearchSuggestionDTO> pssDTOList,String uuid){
    List<Map> dropDownList = new ArrayList<Map>();
    if (CollectionUtils.isNotEmpty(pssDTOList)) {
      for (SearchSuggestionDTO pssDTO : pssDTOList){
        dropDownList.add(pssDTO.toStandardDropDownItemMap());
      }
    }
    Map<String, Object> dropDownMap = new HashMap<String, Object>();
    dropDownMap.put("uuid", uuid);
    dropDownMap.put("data", dropDownList);
    return dropDownMap;
  }

  public static Map<String, Object> generateVehicleDropDownMap(List<String> vehicleList,String searchField,String uuid){
    List<Map> dropDownList = new ArrayList<Map>();
    if (CollectionUtils.isNotEmpty(vehicleList)) {
      for (String vehicleInfo : vehicleList){
          if(StringUtil.isEmpty(vehicleInfo)||"null".equals(vehicleInfo)){
              continue;
          }
        Map<String, String> propertyMap = new HashMap<String, String>();
        StringBuilder joinedText = new StringBuilder();
        joinedText.append(vehicleInfo).append(" ");
        propertyMap.put(searchField, vehicleInfo);
        Map<String, Object> dropDownItem = new HashMap<String, Object>();
        dropDownItem.put("label", joinedText.toString().trim());
        dropDownItem.put("details", propertyMap);
        dropDownItem.put("type", "option");  //目前只使用   option  （category）暂时不用
        dropDownList.add(dropDownItem);
      }
    }
    Map<String, Object> dropDownMap = new HashMap<String, Object>();
    dropDownMap.put("uuid", uuid);
    dropDownMap.put("data", dropDownList);
    return dropDownMap;
  }
  public static Map<String, Object> generateVehicleSuggestionWithCategoryDropDownMap(List<String> vehicleList,String uuid) {
    List<Map> dropDownList = new ArrayList<Map>();
    if(CollectionUtils.isNotEmpty(vehicleList)){
      for(String s : vehicleList){
          if(StringUtil.isEmpty(s)||"null".equals(s)){
              continue;
          }
        String tmp = s.trim().replace("<","").replace(">", "");
        if(s.startsWith("<") && s.endsWith(">") && tmp.length()==1 && Character.isLetter(tmp.charAt(0))){
          Map<String, Object> dropDownItem = new HashMap<String, Object>();
          dropDownItem.put("label", tmp);
          dropDownItem.put("type", "category");
          dropDownList.add(dropDownItem);
        }else{
          Map<String, Object> dropDownItem = new HashMap<String, Object>();
          dropDownItem.put("label", s.trim());
          dropDownItem.put("type", "option");
          dropDownList.add(dropDownItem);
        }
      }
    }
    Map<String, Object> result = new HashMap<String, Object>();
    result.put("uuid", uuid);
    result.put("data", dropDownList);
    return result;
  }
  public static void adaptSearchConditionFromProduct(SearchConditionDTO searchConditionDTO){
    if(searchConditionDTO==null) return;
    if("product_vehicle_model".equals(searchConditionDTO.getSearchField())){
      searchConditionDTO.setSearchField("model");
      searchConditionDTO.setVehicleModel(null);
    }
    if("product_vehicle_brand".equals(searchConditionDTO.getSearchField())){
      searchConditionDTO.setSearchField("brand");
      searchConditionDTO.setVehicleBrand(null);
    }
  }

  public static void adaptSearchConditionFromVehicle(SearchConditionDTO searchConditionDTO){
    if(searchConditionDTO==null) return;
    if("model".equals(searchConditionDTO.getSearchField())){
      searchConditionDTO.setSearchField("product_vehicle_model");
    }
    if("brand".equals(searchConditionDTO.getSearchField())){
      searchConditionDTO.setSearchField("product_vehicle_brand");
    }
  }

  public static boolean  isVehicleSuggestion(String searchField){
    return ArrayUtil.contains(vehicleFields,searchField);
  }


}
