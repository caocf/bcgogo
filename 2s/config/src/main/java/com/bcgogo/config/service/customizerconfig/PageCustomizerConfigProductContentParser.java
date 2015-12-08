package com.bcgogo.config.service.customizerconfig;

import com.bcgogo.config.CustomizerConfigInfo;
import com.bcgogo.config.CustomizerConfigResult;
import com.bcgogo.utils.JsonUtil;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 13-5-26
 * Time: 下午1:16
 */
@Component
public class PageCustomizerConfigProductContentParser implements IPageCustomizerConfigContentParser<CustomizerConfigResult> {
  @Override
  public CustomizerConfigResult parseJsonToDto(String json) {
    CustomizerConfigResult result = (CustomizerConfigResult) JsonUtil.jsonToObject(json, CustomizerConfigResult.class);
    result.sortConfigInfoList();
    return result;
  }

  @Override
  public String parseDtoToJson(CustomizerConfigResult dto) {
    dto.sortConfigInfoList();
    //必须选择一个库存查询的属性
    return JsonUtil.objectToJson(dto);
  }

}
