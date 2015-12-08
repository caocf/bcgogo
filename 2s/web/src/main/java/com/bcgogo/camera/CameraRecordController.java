package com.bcgogo.camera;

import com.bcgogo.common.Pager;
import com.bcgogo.common.Result;
import com.bcgogo.common.WebUtil;
import com.bcgogo.config.model.Camera;
import com.bcgogo.config.service.camera.ICameraService;
import com.bcgogo.enums.CategoryType;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.CategoryServiceSearchDTO;
import com.bcgogo.txn.dto.ServiceDTO;
import com.bcgogo.txn.service.RFITxnService;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: zhangjie
 * Date: 15-1-15
 * Time: 下午5:59
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/cameraRecord.do")
public class CameraRecordController {
  private static final Logger LOG = LoggerFactory.getLogger(CameraRecordController.class);

  SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  @RequestMapping(params = "method=toCameraRecordPage")
  public String toCameraRecordPage() {
    return "/camera/cameraRecordList";
  }

  @RequestMapping(params = "method=toCameraRecordContent")
  public String toCameraRecordContent(HttpServletRequest request, ModelMap model) throws Exception {
    return "/camera/cameraRecordContent";
  }

  @RequestMapping(params = "method=setCategoryPage")
  public void setCategoryPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
    String jsonStr = "[]";
    StringBuffer sb = new StringBuffer("[");
    Long shopId = WebUtil.getShopId(request);
    Long startPageNo = 1l;
    RFITxnService txnService = ServiceManager.getService(RFITxnService.class);
    CategoryServiceSearchDTO categoryServiceSearchDTO = new CategoryServiceSearchDTO();
    categoryServiceSearchDTO.setServiceDTOs(txnService.getServicesByCategory(shopId, categoryServiceSearchDTO.getServiceName(),
      categoryServiceSearchDTO.getCategoryName(), CategoryType.BUSINESS_CLASSIFICATION, startPageNo, PAGE_SIZE));
    for (ServiceDTO service : categoryServiceSearchDTO.getServiceDTOs()) {
      String name = service.getName() == null ? "" : service.getName();
      String id = service.getId() == null ? "" : service.getId().toString();
      sb.append("{\"name\":\"" + name + "\",");
      sb.append("\"id\":\"" + id + "\"},");
    }
    sb.replace(sb.length() - 1, sb.length(), "]");
    jsonStr = sb.toString();
    PrintWriter writer = response.getWriter();
    writer.write(jsonStr);
    writer.close();
  }


  /*
     初始化到店车辆记录
   */
  @ResponseBody
  @RequestMapping(params = "method=initCameraRecordList")
  public Object initCameraRecordList(HttpServletRequest request, CameraSearchCondition condition) {
    Long shopId = null;
    shopId = WebUtil.getShopId(request);
    condition.setShopId(shopId.toString());
    ICameraService cameraService = ServiceManager.getService(ICameraService.class);
    Map<String, Object> result = new HashMap<String, Object>();
    try {
      int total = cameraService.getCameraRecordDTOListAccountByShopId(condition);
      Pager pager = new Pager(total, condition.getPage(), condition.getRows());
      result.put("total", total);
      List<CameraRecordDTO> rowsList = new ArrayList<CameraRecordDTO>();
      if (total > 0) {
        rowsList = cameraService.getCameraRecordListByShopId(pager, condition);
      }
      result.put("rows", rowsList);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  @ResponseBody
  @RequestMapping(params = "method=getQueryVehicleNo")
  public Object getQueryVehicleNo(HttpServletRequest request, CameraSearchCondition condition) {
    ICameraService cameraService = ServiceManager.getService(ICameraService.class);
    return cameraService.getVehicle_nos(condition);
  }

  /*
   初始化摄像头配置
 */
  @ResponseBody
  @RequestMapping(params = "method=initCameraConfigList")
  public Object initCameraConfigList(HttpServletRequest request, Integer page, Integer rows) {
    Long shopId = null;
    shopId = WebUtil.getShopId(request);
    ICameraService cameraService = ServiceManager.getService(ICameraService.class);
    Map<String, Object> result = new HashMap<String, Object>();
    try {
      int total = cameraService.getCountCameraConfigByShopId(StringUtil.valueOf(shopId));
      Pager pager = new Pager(total, page, rows);
      result.put("total", total);
      if (total > 0) {
        List<CameraConfigDTO> rowsList = new ArrayList<CameraConfigDTO>();
        rowsList = cameraService.getCameraConfigByShopId(shopId.toString());
        result.put("rows", rowsList);
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  @RequestMapping(params = "method=toCameraConfigDetail")
  public Object toCameraConfig(HttpServletRequest request, String id, ModelMap model) {
    Map<String, Object> result = new HashMap<String, Object>();
    ICameraService cameraService = ServiceManager.getService(ICameraService.class);
    CameraConfigDTO cameraConfigDTO = cameraService.getCameraConfigByCameraId(id);
    if (cameraConfigDTO == null) {
      cameraConfigDTO = new CameraConfigDTO();
      cameraConfigDTO.setCamera_id(id);
    }
    model.addAttribute("cameraConfigDTO", cameraConfigDTO);
    return "/camera/cameraRecordContent";
  }


  @ResponseBody
  @RequestMapping(params = "method=updateCameraConfigs")
  public String updateCameraConfig(HttpServletRequest request, CameraConfigDTO cameraConfigDTO) {
    ICameraService cameraService = ServiceManager.getService(ICameraService.class);
    cameraService.updateCameraConfig(cameraConfigDTO);
    return "/camera/cameraRecordList";
  }

  @ResponseBody
  @RequestMapping(params = "method=getCategoryItemSearch")
  public Object getCategoryItemSearch(ModelMap model, HttpServletRequest request, CategoryServiceSearchDTO categoryServiceSearchDTO) {
    RFITxnService txnService = ServiceManager.getService(RFITxnService.class);
    Long shopId = WebUtil.getShopId(request);
    Long startPageNo = 1l;
    if (StringUtils.isNotBlank(request.getParameter("startPageNo")) && NumberUtil.isNumber(request.getParameter("startPageNo"))) {
      startPageNo = NumberUtil.longValue(request.getParameter("startPageNo"));
    }
    Map<String, Object> returnMap = new HashMap<String, Object>();
    try {
      if (categoryServiceSearchDTO == null) {
        categoryServiceSearchDTO = new CategoryServiceSearchDTO();
      }
      Pager pager = null;
      //全部
      if (categoryServiceSearchDTO.getCategoryServiceType() == null) {
        categoryServiceSearchDTO.setServiceDTOs(txnService.getServicesByCategory(shopId, categoryServiceSearchDTO.getServiceName(),
          categoryServiceSearchDTO.getCategoryName(), CategoryType.BUSINESS_CLASSIFICATION, startPageNo, PAGE_SIZE));
        pager = new Pager(txnService.countServiceByCategory(shopId, categoryServiceSearchDTO.getServiceName(),
          categoryServiceSearchDTO.getCategoryName(), CategoryType.BUSINESS_CLASSIFICATION), startPageNo.intValue(), (int) PAGE_SIZE);
      }
      categoryServiceSearchDTO.setCategoryDTOs(txnService.getCategoryByShopId(shopId));
      returnMap.put("pager", pager);
      returnMap.put("categoryServiceSearchDTO", categoryServiceSearchDTO);
      returnMap.put("result", new Result(true));
      return returnMap;
    } catch (Exception e) {
      LOG.debug("/category.do");
      LOG.debug("method=getCategoryItemSearch");
      LOG.error(e.getMessage(), e);
      returnMap.put("result", new Result(false));
      return returnMap;
    }
  }

  @RequestMapping(params = "method=cameraConfigList")
  public String toCameraConfigList() {
    return "/camera/cameraConfigList";
  }

  @RequestMapping(params = "method=toCameraConfigPage")
  public String toCameraConfigPage() {
    return "/camera/cameraConfigContent";
  }

  @ResponseBody
  @RequestMapping(params = "method=getPrinterSerialNo")
  public String getPrinterSerialNo(String cameraSerialNo) {
    ICameraService cameraService = ServiceManager.getService(ICameraService.class);
    CameraConfigDTO cameraConfigDTO = cameraService.getCameraConfigBySerialNo(cameraSerialNo);
    return cameraConfigDTO != null ? cameraConfigDTO.getPrinter_serial_no() : null;
  }

  public static final long PAGE_SIZE = 20;//默认分页条数

}
