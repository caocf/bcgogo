package com.bcgogo.search.dto;

import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: caiweili
 * Date: 8/2/12
 * Time: 10:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class SearchSuggestionDTO {
  public SearchSuggestionDTO() {
    suggestionEntry = new ArrayList<String[]>();
  }

  public SearchSuggestionDTO(String uuid) {
    this.uuid = uuid;
    suggestionEntry = new ArrayList<String[]>();
  }

  public void addEntry(String fieldName, String value) {
    String[] pair = new String[2];
    pair[0] = fieldName;
    pair[1] = value;
    suggestionEntry.add(pair);
  }


  public List<String[]> suggestionEntry;
  private String uuid;

  /*
   * 为了转成前台js 约定好的json格式
   */
  public Map toStandardDropDownItemMap() {
    Map<String, String> propertyMap = new HashMap<String, String>();
    StringBuilder joinedText = new StringBuilder();
    for (String[] se : suggestionEntry) {
      joinedText.append(se[1]).append(" ");
      propertyMap.put(se[0], se[1]);
    }
    Map<String, Object> dropDownItem = new HashMap<String, Object>();
    dropDownItem.put("label", joinedText.toString().trim());
    dropDownItem.put("details", propertyMap);
    dropDownItem.put("type", "option");  //目前只使用   option  （category）暂时不用
    return dropDownItem;
  }


  /*
   * 为了转成前台js 约定好的json格式
   */
  public Map toSupplierCustomerDropDownItemMap(List<String> titleList,boolean isPrefix) {
    Map<String, String> propertyMap = new HashMap<String, String>();
    if(CollectionUtils.isEmpty(titleList)) return propertyMap;

    StringBuilder joinedText = new StringBuilder();
    for (String[] se : suggestionEntry) {
      propertyMap.put(se[0], se[1]==null?"":se[1]);
    }
    if(isPrefix){
      if("customer".equals(propertyMap.get("customerOrSupplier"))){
        joinedText.append("客户：");
      }else if("supplier".equals(propertyMap.get("customerOrSupplier"))){
        joinedText.append("供应商：");
      }
    }
    for(String title : titleList){
       joinedText.append(propertyMap.get(title)==null?"":propertyMap.get(title)).append(" ");
    }
    Map<String, Object> dropDownItem = new HashMap<String, Object>();
    dropDownItem.put("label", joinedText.toString().trim());
    dropDownItem.put("details", propertyMap);
    dropDownItem.put("type", "option");  //目前只使用   option  （category）暂时不用
    return dropDownItem;
  }

  /*
   * 为了转成前台js 约定好的json格式
   */
  @Deprecated
  public Map toRepairServiceDropDownItemMap() {
    Map<String, String> dropDownItem = new HashMap<String, String>();
    for (String[] se : suggestionEntry) {
      dropDownItem.put(se[0], se[1]);
    }
    return dropDownItem;
  }


  public List<String[]> getSuggestionEntry() {
    return suggestionEntry;
  }

  public void setSuggestionEntry(List<String[]> suggestionEntry) {
    this.suggestionEntry = suggestionEntry;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }
}
