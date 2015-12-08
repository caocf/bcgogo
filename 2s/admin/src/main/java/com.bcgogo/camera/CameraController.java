package com.bcgogo.camera;

import com.bcgogo.common.Pager;
import com.bcgogo.common.Result;
import com.bcgogo.config.model.Camera;
import com.bcgogo.config.service.camera.ICameraService;
import com.bcgogo.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangjie on 14-12-22.
 */
@Controller
@RequestMapping("/camera.do")
public class CameraController {
  private static final Logger LOG = LoggerFactory.getLogger(CameraController.class);

  @Autowired
  private ICameraService cameraService;


  //-------------cameraList界面的内容  开始---------------------------------------------------------------------
  @RequestMapping(params = "method=initCameraList")
  public String initCameraList() {
    return "/camera/cameraList";
  }

  @ResponseBody
  @RequestMapping(params = "method=cameraList")
  public Object cameraList(HttpServletRequest request, Integer page, Integer rows, String editCamera_shop_id) {
    Map<String, Object> result = new HashMap<String, Object>();
    try {
      int total = cameraService.getCameraDTOListAccount(editCamera_shop_id);
      Pager pager = new Pager(total, page, rows);
      result.put("total", total);
      List<CameraDTO> rowsList = new ArrayList<CameraDTO>();
      if (total > 0) {
        rowsList = cameraService.getCameraDTOList(pager, editCamera_shop_id);
      }
      result.put("rows", rowsList);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  @ResponseBody
  @RequestMapping(params = "method=saveOrUpdateCameraRef")
  public Object saveOrUpdateCameraRef(HttpServletRequest request, CameraDTO cameraDTO) {
    try {
      if ("".equals(cameraDTO.getId())) {
        Camera camera = new Camera();
        camera.setSerial_no(cameraDTO.getSerial_no());
        Camera ca = cameraService.getCamera(camera);
        if (ca != null && cameraDTO.getSerial_no().equals(ca.getSerial_no())) {
          return new Result("摄像头序列号重复！", false);
        }
      }
      cameraService.saveOrUpdateCameraRef(cameraDTO);
      return new Result("保存成功", true);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return new Result("网络异常", false);
    }
  }

  @ResponseBody
  @RequestMapping(params = "method=unBandShop")
  public Object unBandShop(HttpServletRequest request, CameraDTO cameraDTO) {
    try {
      cameraService.unBandShop(cameraDTO);
      return new Result("解绑成功", true);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return new Result("网络异常", false);
    }
  }

  //-------------cameraList界面的内容  结束--------------------------------------------------------------------

  // -------------cameraRecordList界面  开始--------------------------------------------------------------------


  @ResponseBody
  @RequestMapping(params = "method=cameraRecordList")
  public Object cameraRecordList(HttpServletRequest request, Integer page, Integer rows, String id) {
    Map<String, Object> result = new HashMap<String, Object>();
    try {
      int total = cameraService.getCameraRecordDTOListAccount(id);
      Pager pager = new Pager(total, page, rows);
      result.put("total", total);
      List<CameraRecordDTO> rowsList = new ArrayList<CameraRecordDTO>();
      if (total > 0) {
        rowsList = cameraService.getCameraRecordDTOList(pager, id);
      }
      result.put("rows", rowsList);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  // -------------cameraRecordList界面  结束--------------------------------------------------------------------
  @RequestMapping(params = "method=toCameraConfig")
  public String toCameraConfig(HttpServletRequest request, String id, ModelMap model) {
    Map<String, Object> result = new HashMap<String, Object>();
    CameraConfigDTO cameraConfigDTO = cameraService.getCameraConfigByCameraId(id);
    if (cameraConfigDTO == null) {
      cameraConfigDTO = new CameraConfigDTO();
      cameraConfigDTO.setCamera_id(id);
    }
    model.addAttribute("cameraConfigDTO", cameraConfigDTO);
    return "/camera/cameraConfig";
  }

  @ResponseBody
  @RequestMapping(params = "method=updateCameraConfig")
  public Object updateCameraConfig(HttpServletRequest request, CameraConfigDTO cameraConfigDTO) {
    cameraService.updateCameraConfig_admin(cameraConfigDTO);
    return new Result("保存成功", true);
  }

  @RequestMapping(params = "method=toCameraRecordList")
  public String toCameraRecordList(HttpServletRequest request, ModelMap modelMap, String id) {
    modelMap.addAttribute("id", id);
    return "/camera/cameraRecordList_admin";
  }


}
