package com.bcgogo.api.controller;

import com.bcgogo.api.*;
import com.bcgogo.api.response.ApiResultResponse;
import com.bcgogo.api.response.EnquiryListResponse;
import com.bcgogo.user.service.utils.SessionUtil;
import com.bcgogo.common.Pager;
import com.bcgogo.enums.app.EnquiryStatus;
import com.bcgogo.enums.app.MessageCode;
import com.bcgogo.enums.app.ValidateMsg;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.app.IEnquiryService;
import com.bcgogo.utils.JsonUtil;
import com.bcgogo.utils.NumberUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-10-21
 * Time: 下午4:16
 */
@Controller
@RequestMapping("/enquiry/*")
public class EnquiryController {
  private static final Logger LOG = LoggerFactory.getLogger(EnquiryController.class);

  /**
   * 新增询价单
   */
  @ResponseBody
  @RequestMapping(value = "/newEnquiry", method = RequestMethod.POST)
  public ApiResponse saveEnquiry(HttpServletRequest request, HttpServletResponse response,EnquiryDTO enquiry) {
    IEnquiryService enquiryService = ServiceManager.getService(IEnquiryService.class);
    try {
      String appUserNo = SessionUtil.getAppUserNo(request, response);
      ApiResponse apiResponse ;
     String validateMsg = enquiry.validateSave();
      if(StringUtils.isEmpty(validateMsg)){
        enquiry.setAppUserNo(appUserNo);
        enquiryService.handleSaveEnquiry(enquiry,SessionUtil.getCommonAppUserImageScenes(request, response));
        apiResponse = MessageCode.toApiResponse(MessageCode.ENQUIRY_SAVE_SUCCESS);
        apiResponse.setDebug(JsonUtil.objectToJson(enquiry));
        return  new ApiResultResponse<EnquiryDTO>(apiResponse,enquiry);
      }else {
        apiResponse = MessageCode.toApiResponse(MessageCode.ENQUIRY_SAVE_FAIL,validateMsg);
        apiResponse.setDebug("enquiry" + JsonUtil.objectToJson(enquiry));
      }
      return apiResponse;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.ENQUIRY_SAVE_EXCEPTION);
    }
  }

  /**
   * 更新询价单
   */
  @ResponseBody
  @RequestMapping(value = "/existingEnquiry", method = RequestMethod.PUT)
  public ApiResponse updateEnquiry(HttpServletRequest request, HttpServletResponse response, @RequestBody EnquiryDTO enquiry) {
    IEnquiryService enquiryService = ServiceManager.getService(IEnquiryService.class);
    try {
      String appUserNo = SessionUtil.getAppUserNo(request, response);
      ApiResponse apiResponse;
      String validateMsg = enquiry.validateUpdateFromPage();
      if (StringUtils.isEmpty(validateMsg)) {
        EnquiryDTO enquiryDTO = enquiryService.getSimpleEnquiryDTO(enquiry.getId(), appUserNo);
        if (enquiryDTO == null) {
          return MessageCode.toApiResponse(MessageCode.ENQUIRY_UPDATE_FAIL, ValidateMsg.APP_ENQUIRY_UPDATE_NOT_EXIST);
        }
        validateMsg = enquiryDTO.validateUpdateFromDB();
        if (StringUtils.isNotEmpty(validateMsg)) {
          apiResponse = MessageCode.toApiResponse(MessageCode.ENQUIRY_UPDATE_FAIL, validateMsg);
          apiResponse.setDebug("enquiry" + JsonUtil.objectToJson(enquiry));
        } else {
          enquiry.setAppUserNo(appUserNo);
          enquiryService.handleUpdateEnquiry(enquiry, SessionUtil.getCommonAppUserImageScenes(request, response));
          apiResponse = MessageCode.toApiResponse(MessageCode.ENQUIRY_UPDATE_SUCCESS);
          return new ApiResultResponse<EnquiryDTO>(apiResponse, enquiry);
        }
      } else {
        apiResponse = MessageCode.toApiResponse(MessageCode.ENQUIRY_UPDATE_FAIL, validateMsg);
        apiResponse.setDebug("enquiry" + JsonUtil.objectToJson(enquiry));
      }
      return apiResponse;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.ENQUIRY_UPDATE_EXCEPTION);
    }
  }


  /**
   * 询价单发送
   */
  @ResponseBody
  @RequestMapping(value = "/toSendEnquiry", method = RequestMethod.POST)
  public ApiResponse sendEnquiry(HttpServletRequest request, HttpServletResponse response, EnquiryDTO enquiry) {
    IEnquiryService enquiryService = ServiceManager.getService(IEnquiryService.class);
    try {
      String appUserNo = SessionUtil.getAppUserNo(request, response);
      ApiResponse apiResponse ;
      String validateMsg = enquiry.validateSend();
      if (StringUtils.isEmpty(validateMsg) && enquiry.getId() != null) {
        EnquiryDTO enquiryDTO = enquiryService.getSimpleEnquiryDTO(enquiry.getId(), appUserNo);
        if (enquiryDTO == null) {
          validateMsg = ValidateMsg.APP_ENQUIRY_SEND_NOT_EXIST.getValue();
        } else {
          validateMsg = enquiryDTO.validateSendFromDB();
        }
      }
      if (StringUtils.isNotEmpty(validateMsg)) {
        apiResponse = MessageCode.toApiResponse(MessageCode.ENQUIRY_SEND_FAIL, validateMsg);
        apiResponse.setDebug("enquiry" + JsonUtil.objectToJson(enquiry));
        return apiResponse;
      } else {
        enquiry.setAppUserNo(appUserNo);
        enquiryService.handleSendEnquiry(enquiry, SessionUtil.getCommonAppUserImageScenes(request, response));
        apiResponse = MessageCode.toApiResponse(MessageCode.ENQUIRY_SEND_SUCCESS);
        return new ApiResultResponse<EnquiryDTO>(apiResponse, enquiry);
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.ENQUIRY_SEND_EXCEPTION);
    }
  }

   /*
    *询价单列表
    */
  @ResponseBody
  @RequestMapping(value = "/list/{status}/{pageNo}/{pageSize}", method = RequestMethod.GET)
  public ApiResponse getEnquiryList(HttpServletRequest request, HttpServletResponse response,
                                    @PathVariable("status") String status,
                                    @PathVariable("pageNo") int pageNo,
                                    @PathVariable("pageSize") int pageSize) {
    try {
      IEnquiryService enquiryService = ServiceManager.getService(IEnquiryService.class);
      String appUserNo = SessionUtil.getAppUserNo(request, response);
      ApiResponse apiResponse = MessageCode.toApiResponse(MessageCode.ENQUIRY_LIST_SUCCESS);
      EnquiryListResponse apiResultResponse = new EnquiryListResponse(apiResponse);

      Set<EnquiryStatus> enquiryStatuses = EnquiryStatus.generateStatus(status);
      int count = enquiryService.countEnquiryListByUserNoAndStatus(appUserNo, enquiryStatuses);
      Pager pager = new Pager(count, pageNo, pageSize);
      List<EnquiryDTO> enquiryDTOs = enquiryService.getEnquiryListByUserNoAndStatus(appUserNo, enquiryStatuses,
          SessionUtil.getCommonAppUserImageScenes(request, response), pager);
      apiResultResponse.setResult(enquiryDTOs);
      apiResultResponse.setPager(pager);
      return apiResultResponse;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.ENQUIRY_SEND_EXCEPTION);
    }
  }

  /*
 *询价单转发
 */
  @ResponseBody
  @RequestMapping(value = "/toForwardEnquiry/{enquiryId}", method = RequestMethod.GET)
  public ApiResponse getForwardEnquiry(HttpServletRequest request, HttpServletResponse response,
                                       @PathVariable("enquiryId") String enquiryId) {
    try {
      IEnquiryService enquiryService = ServiceManager.getService(IEnquiryService.class);
      String appUserNo = SessionUtil.getAppUserNo(request, response);
      EnquiryDTO enquiryDTO = null;
      if (StringUtils.isNotEmpty(enquiryId) && StringUtils.isNumeric(enquiryId)) {
        Long longEnquiryId = NumberUtil.longValue(enquiryId);
        enquiryDTO = enquiryService.getEnquiryDTODetail(longEnquiryId, appUserNo, SessionUtil.getCommonAppUserImageScenes(request, response));
      }
      if (enquiryDTO != null) {
        enquiryDTO.generateForwardInfo();
        ApiResponse apiResponse = MessageCode.toApiResponse(MessageCode.ENQUIRY_FORWARD_SUCCESS);
        return new ApiResultResponse<EnquiryDTO>(apiResponse, enquiryDTO);
      } else {
        ApiResponse apiResponse = MessageCode.toApiResponse(MessageCode.ENQUIRY_FORWARD_SUCCESS, ValidateMsg.APP_ENQUIRY_DETAIL_NOT_EXIST);
        apiResponse.setDebug("enquiryId=" + enquiryId);
        return apiResponse;
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.ENQUIRY_FORWARD_EXCEPTION);
    }
  }

  /*
 *询价单删除
 */
  @ResponseBody
  @RequestMapping(value = "/singleEnquiry/{enquiryId}", method = RequestMethod.DELETE)
  public ApiResponse deleteSingleEnquiry(HttpServletRequest request, HttpServletResponse response,
                                         @PathVariable("enquiryId") String enquiryId) {
    try {
      IEnquiryService enquiryService = ServiceManager.getService(IEnquiryService.class);
      String appUserNo = SessionUtil.getAppUserNo(request, response);
      ApiResponse apiResponse;
      EnquiryDTO enquiryDTO = null;
      Long longEnquiryId = null;
      if (StringUtils.isNotBlank(enquiryId) && StringUtils.isNumeric(enquiryId) && StringUtils.isNotBlank(appUserNo)) {
        longEnquiryId = NumberUtil.longValue(enquiryId);
        enquiryDTO = enquiryService.getSimpleEnquiryDTO(longEnquiryId, appUserNo);
      }
      if (enquiryDTO != null) {
        enquiryService.handleDeleteEnquiry(appUserNo, longEnquiryId);
        apiResponse = MessageCode.toApiResponse(MessageCode.ENQUIRY_DELETE_SUCCESS);
      } else {
        apiResponse = MessageCode.toApiResponse(MessageCode.ENQUIRY_DELETE_FAIL, ValidateMsg.APP_ENQUIRY_DETAIL_NOT_EXIST);
        apiResponse.setDebug("enquiryId=" + enquiryId);
      }
      return apiResponse;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.ENQUIRY_DELETE_EXCEPTION);
    }
  }

  /*
 *询价单详情
 */
  @ResponseBody
  @RequestMapping(value = "/detail/{enquiryId}", method = RequestMethod.GET)
  public ApiResponse getEnquiryDetail(HttpServletRequest request, HttpServletResponse response,
                                      @PathVariable("enquiryId") String enquiryId) {
    try {
      IEnquiryService enquiryService = ServiceManager.getService(IEnquiryService.class);
      String appUserNo = SessionUtil.getAppUserNo(request, response);
      EnquiryDTO enquiryDTO = null;
      if (StringUtils.isNotEmpty(enquiryId) && StringUtils.isNumeric(enquiryId)) {
        Long longEnquiryId = NumberUtil.longValue(enquiryId);
        enquiryDTO = enquiryService.getEnquiryDTODetail(longEnquiryId, appUserNo, SessionUtil.getCommonAppUserImageScenes(request, response));
      }
      if (enquiryDTO != null) {
        ApiResponse apiResponse = MessageCode.toApiResponse(MessageCode.ENQUIRY_DETAIL_SUCCESS);
        return new ApiResultResponse<EnquiryDTO>(apiResponse, enquiryDTO);
      } else {
        ApiResponse apiResponse = MessageCode.toApiResponse(MessageCode.ENQUIRY_DETAIL_FAIL, ValidateMsg.APP_ENQUIRY_DETAIL_NOT_EXIST);
        apiResponse.setDebug("enquiryId=" + enquiryId);
        return apiResponse;
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.ENQUIRY_DETAIL_EXCEPTION);
    }
  }


}
