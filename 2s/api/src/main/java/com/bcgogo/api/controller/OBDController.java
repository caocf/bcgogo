package com.bcgogo.api.controller;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.api.OBDBindingDTO;
import com.bcgogo.user.service.utils.SessionUtil;
import com.bcgogo.api.response.ApiOBDMessageResponse;
import com.bcgogo.common.Pair;
import com.bcgogo.enums.app.MessageCode;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.bcgogoListener.threadPool.OrderThreadPool;
import com.bcgogo.txn.service.app.IHandleAppUserShopCustomerMatchService;
import com.bcgogo.txn.service.solr.ICustomerOrSupplierSolrWriteService;
import com.bcgogo.user.service.app.IAppUserVehicleObdService;
import com.bcgogo.user.service.solr.IVehicleSolrWriterService;
import com.bcgogo.utils.JsonUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Set;

/**
 * User: ZhangJuntao
 * Date: 13-8-8
 * Time: 下午4:17
 */
@Controller
public class OBDController {
  private static final Logger LOG = LoggerFactory.getLogger(OBDController.class);



  @ResponseBody
  @RequestMapping(value = "/obd/binding", method = RequestMethod.POST)
  public ApiResponse binding(final HttpServletRequest request, final HttpServletResponse response, OBDBindingDTO obdBindingDTO) throws Exception {
    try {
      obdBindingDTO.setAppUserId(SessionUtil.getAppUserId(request, response));
      obdBindingDTO.setUserNo(SessionUtil.getAppUserNo(request, response));
      obdBindingDTO.filter();
      Pair<ApiResponse, Boolean> responsePair = ServiceManager.getService(IAppUserVehicleObdService.class).bindingObd(obdBindingDTO);
      ApiResponse apiResponse = responsePair.getKey();
      if (apiResponse.getMsgCode() > 0 && responsePair.getValue()) {
        Long appUserId = SessionUtil.getAppUserId(request, response);
        IHandleAppUserShopCustomerMatchService handleAppUserShopCustomerMatchService = ServiceManager.getService(IHandleAppUserShopCustomerMatchService.class);
        handleAppUserShopCustomerMatchService.handleAppUserCustomerMatch(appUserId);
      }
      final String appUserNos = obdBindingDTO.getUserNo();
      final Set<String> updatedVehicleNoSet = obdBindingDTO.getUpdatedVehicleNoSet();
      OrderThreadPool.getInstance().execute(new Runnable() {
        @Override
        public void run() {
          try {
            ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomersByAppUserNos(appUserNos);
            if(CollectionUtils.isNotEmpty(updatedVehicleNoSet)){
              ServiceManager.getService(IVehicleSolrWriterService.class).createVehicleSolrIndexByLicenceNo(updatedVehicleNoSet.toArray(new String[updatedVehicleNoSet.size()]));
            }
          } catch (Exception e) {
            LOG.error(e.getMessage(), e);
          }
        }
      });
      return apiResponse;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MessageCode.toApiResponse(MessageCode.OBD_BINDING_EXCEPTION);
    }
  }

  @ResponseBody
  @RequestMapping(value = "/obd/OBDMessage", method = RequestMethod.POST)
  public ApiResponse sendOBDMessage(HttpServletRequest request, HttpServletResponse response) {
    try {
      Map<String, String[]> resultParameterMap = request.getParameterMap();
      LOG.warn("/obd/OBDMessage/INFO:{}", JsonUtil.objectToJson(resultParameterMap));
      ApiOBDMessageResponse apiOBDMessageResponse = new ApiOBDMessageResponse(MessageCode.toApiResponse(MessageCode.OBD_MESSAGE_SUCCESS));
      if (MapUtils.isNotEmpty(resultParameterMap) && resultParameterMap.get("debug") != null
          && "true".equals(resultParameterMap.get("debug")[0])) {
        apiOBDMessageResponse.setMessageInfo(JsonUtil.objectToJson(resultParameterMap));
      }
      return apiOBDMessageResponse;
    } catch (Exception e) {
      LOG.error("/obd/OBDMessage", e);
      return MessageCode.toApiResponse(MessageCode.OBD_MESSAGE_FAIL);
    }
  }


}
