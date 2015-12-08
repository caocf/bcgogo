package com.bcgogo.api.controller;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.api.DictionaryFaultInfoDTO;
import com.bcgogo.api.response.FaultCodeResponse;
import com.bcgogo.enums.app.MessageCode;
import com.bcgogo.product.service.app.IAppDictionaryService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 故障码
 * <p/>
 * Author: zj
 * Date: 2015-4-23
 * Time: 16:57
 */
@Controller
public class FaultCodeController {
  private static final Logger LOG = LoggerFactory.getLogger(FaultCodeController.class);


  /**
   * 根据故障码编号获取故障码信息
   * @param request
   * @param response
   * @param faultCode
   * @return
   */
  @ResponseBody
  @RequestMapping(value = "/vehicle/findFaultCode/{faultCode}", method = RequestMethod.GET)
  public ApiResponse getFaultCode(HttpServletRequest request, HttpServletResponse response,@PathVariable("faultCode") String faultCode) {
    LOG.info("故障码编号,faultCode={}", faultCode);
    if (StringUtil.isEmpty(faultCode)) {
      return MessageCode.toApiResponse(MessageCode.FAULT_CODE_FALL);
    }
    IAppDictionaryService iAppDictionaryService = ServiceManager.getService(IAppDictionaryService.class);
    FaultCodeResponse faultCodeResponse = new FaultCodeResponse();
    DictionaryFaultInfoDTO dictionaryFaultInfoDTO = iAppDictionaryService.getDictionaryFaultInfoDTOByFaultCode(faultCode);
     if(dictionaryFaultInfoDTO!=null){
       faultCodeResponse.setMessage("根据故障码编号获取故障码详细信息成功！");
       faultCodeResponse.setDictionaryFaultInfoDTO(dictionaryFaultInfoDTO);
     }else{
       faultCodeResponse.setMessage("未查询到该故障码相关信息！");
       faultCodeResponse.setDictionaryFaultInfoDTO(dictionaryFaultInfoDTO);
     }

    return faultCodeResponse;
  }

}
