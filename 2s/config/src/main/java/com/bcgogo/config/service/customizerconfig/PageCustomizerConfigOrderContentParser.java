package com.bcgogo.config.service.customizerconfig;

import com.bcgogo.config.CustomizerConfigResult;
import com.bcgogo.utils.JsonUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 13-5-26
 * Time: 下午1:16
 */
@Component
public class PageCustomizerConfigOrderContentParser implements IPageCustomizerConfigContentParser<List<CustomizerConfigResult>> {

  @Override
  public List<CustomizerConfigResult> parseJsonToDto(String json) {
    List<CustomizerConfigResult> results = new ArrayList<CustomizerConfigResult>();
    results = JsonUtil.jsonArrayToList(json, CustomizerConfigResult.class, results);
    for (CustomizerConfigResult result : results) {
      result.sortConfigInfoList();
    }
    sort(results);
    return results;
  }

  @Override
  public String parseDtoToJson(List<CustomizerConfigResult> dtoList) {
    for (CustomizerConfigResult result : dtoList) {
      result.sortConfigInfoList();
      result.isAllConfigInfoListUnchecked();
    }
    return JsonUtil.listToJson(dtoList);
  }

  public void sort(List<CustomizerConfigResult> results) {
    if (CollectionUtils.isNotEmpty(results)) {
      Collections.sort(results, new Comparator<CustomizerConfigResult>() {
        @Override
        public int compare(CustomizerConfigResult o1, CustomizerConfigResult o2) {
          if (o1.getSort() == null) {
            return -1;
          }
          if (o2.getSort() == null) {
            return 1;
          }
          try {
            return o1.getSort().compareTo(o2.getSort());
          } catch (Exception e) {
            return -1;
          }
        }
      });
    }
  }
}
