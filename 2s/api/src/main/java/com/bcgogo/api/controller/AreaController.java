package com.bcgogo.api.controller;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.config.service.IAreaService;
import com.bcgogo.enums.app.AreaType;
import com.bcgogo.enums.app.MessageCode;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * User: ZhangJuntao
 * Date: 13-8-31
 * Time: 下午2:06
 */
@Controller
@RequestMapping("/area/*")
public class AreaController {
  private static final Logger LOG = LoggerFactory.getLogger(AreaController.class);

  /**
   * 获取地区列表
   * type：      AreaType
   * provinceId：省份ID
   * comment zjt: 省范围(没有注册过的省市不显示) 顺序 (维持原来)
   */
  @ResponseBody
  @RequestMapping(value = "/list/{type}/{provinceId}",
      method = RequestMethod.GET)
  public ApiResponse obtainShopArea(@PathVariable("provinceId") String provinceId,
                                    @PathVariable("type") AreaType type) throws Exception {
    try {
      ApiResponse apiResponse = ServiceManager.getService(IAreaService.class)
          .obtainAppShopArea(StringUtil.isEmptyAppGetParameter(provinceId) ? null : Long.valueOf(provinceId), type);
      apiResponse.setDebug("provinceId:" + provinceId);
      return apiResponse;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.OBTAIN_AREA_EXCEPTION);
    }
  }

  /**
   * id can be null means null
   * @throws Exception
   */
  @ResponseBody
  @RequestMapping(value = "/juhe/list", method = RequestMethod.GET)
  public ApiResponse obtainJuheArea() throws Exception {
    try {
      return ServiceManager.getService(IAreaService.class)
          .obtainJuheSupportArea();
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.OBTAIN_AREA_EXCEPTION);
    }
  }


}
