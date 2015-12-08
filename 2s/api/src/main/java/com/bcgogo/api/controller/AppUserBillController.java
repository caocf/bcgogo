package com.bcgogo.api.controller;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.api.AppUserBillDTO;
import com.bcgogo.api.EnquiryDTO;
import com.bcgogo.user.service.utils.SessionUtil;
import com.bcgogo.api.request.AppUserBillRequest;
import com.bcgogo.api.response.ApiResultResponse;
import com.bcgogo.common.AllListResult;
import com.bcgogo.enums.app.MessageCode;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.app.IAppUserBillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * User: ZhangJuntao
 * Date: 13-10-17
 * Time: 上午11:08
 */
@Controller
@RequestMapping("/appUserBill/*")
public class AppUserBillController {
  private static final Logger LOG = LoggerFactory.getLogger(AppUserBillController.class);

  @ResponseBody
  @RequestMapping(value = "/list/{pageNo}/{pageSize}", method = RequestMethod.GET)
  public ApiResponse list(HttpServletRequest request, HttpServletResponse response,
                          @PathVariable("pageNo") int pageNo,
                          @PathVariable("pageSize") int pageSize) throws Exception {
    try {
      String appUserNo = SessionUtil.getAppUserNo(request, response);
      ApiResponse apiResponse = MessageCode.toApiResponse(MessageCode.ACCOUNT_LIST_SUCCESS);
      IAppUserBillService appUserBillService = ServiceManager.getService(IAppUserBillService.class);
      AllListResult<AppUserBillDTO> appUserBillDTOList = appUserBillService
          .getAppUserBillListByUserNo(appUserNo, SessionUtil.getAppUserBillImageScenes(request, response), pageNo, pageSize);
      ApiResultResponse<AllListResult<AppUserBillDTO>> result = new ApiResultResponse<AllListResult<AppUserBillDTO>>(apiResponse);
      result.setResult(appUserBillDTOList);
      result.setDebug("pageNo:" + pageNo + ",pageSize:" + pageSize);
      return result;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.ACCOUNT_LIST_EXCEPTION);
    }
  }

  @ResponseBody
  @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
  public ApiResponse detail(HttpServletRequest request, HttpServletResponse response,
                            @PathVariable("id") Long id) throws Exception {
    try {
      ApiResponse apiResponse = MessageCode.toApiResponse(MessageCode.ACCOUNT_DETAIL_SUCCESS);
      AppUserBillDTO billDTO = ServiceManager.getService(IAppUserBillService.class)
          .getAppUserBillById(id, SessionUtil.getAppUserBillImageScenes(request, response));
      ApiResultResponse<AppUserBillDTO> accountResponse = new ApiResultResponse<AppUserBillDTO>(apiResponse);
      accountResponse.setResult(billDTO);
      accountResponse.setDebug(id);
      return accountResponse;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.ACCOUNT_LIST_EXCEPTION);
    }
  }

  @ResponseBody
  @RequestMapping(value = "/{id}/inquiry", method = RequestMethod.GET)
  public ApiResponse toInquiry(HttpServletRequest request, HttpServletResponse response,
                               @PathVariable("id") Long id) throws Exception {
    try {
      ApiResponse apiResponse = MessageCode.toApiResponse(MessageCode.ACCOUNT_TRANSFORM_INQUIRY_SUCCESS);
      AppUserBillDTO billDTO = ServiceManager.getService(IAppUserBillService.class)
          .getAppUserBillById(id, SessionUtil.getAppUserBillImageScenes(request, response));
      ApiResultResponse<EnquiryDTO> accountResponse = new ApiResultResponse<EnquiryDTO>(apiResponse);
      if (billDTO == null) return MessageCode.toApiResponse(MessageCode.ACCOUNT_TRANSFORM_INQUIRY_FAIL);
      accountResponse.setResult(billDTO.toEnquiryDTO());
      accountResponse.setDebug(id);
      return accountResponse;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.ACCOUNT_TRANSFORM_INQUIRY_EXCEPTION);
    }
  }

  @ResponseBody
  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  public ApiResponse delete(HttpServletRequest request, HttpServletResponse response,
                            @PathVariable("id") Long id) throws Exception {
    try {
      String appUserNo = SessionUtil.getAppUserNo(request, response);
      String failMsg = "";
      if (ServiceManager.getService(IAppUserBillService.class).deleteAppUserBillService(id, appUserNo, failMsg)) {
        ApiResponse apiResponse = MessageCode.toApiResponse(MessageCode.ACCOUNT_DELETE_SUCCESS);
        apiResponse.setDebug(id);
        return apiResponse;
      } else {
        return MessageCode.toApiResponse(MessageCode.ACCOUNT_DELETE_FAIL, failMsg);
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.ACCOUNT_DELETE_EXCEPTION);
    }
  }

  @ResponseBody
  @RequestMapping(value = "/save", method = RequestMethod.PUT)
  public ApiResponse save(HttpServletRequest request, HttpServletResponse response,
                          @RequestBody AppUserBillRequest billRequest) throws Exception {
    try {
      String appUserNo = SessionUtil.getAppUserNo(request, response);
      ApiResponse apiResponse = MessageCode.toApiResponse(MessageCode.ACCOUNT_SAVE_SUCCESS);
      ServiceManager.getService(IAppUserBillService.class)
          .saveAppUserBill(billRequest.toAppUserBillDTO(appUserNo));
      apiResponse.setDebug(billRequest.toString());
      return apiResponse;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.ACCOUNT_SAVE_EXCEPTION);
    }
  }

}
