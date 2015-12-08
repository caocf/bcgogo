package com.bcgogo.admin;

import com.bcgogo.common.Result;
import com.bcgogo.common.WebUtil;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.constant.LogicResource;
import com.bcgogo.enums.MessageScene;
import com.bcgogo.enums.MessageSwitchStatus;
import com.bcgogo.enums.user.UserSwitchType;
import com.bcgogo.notification.dto.MessageSwitchDTO;
import com.bcgogo.notification.dto.MessageTemplateDTO;
import com.bcgogo.notification.service.INotificationService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.IPickingService;
import com.bcgogo.user.dto.UserLimitDTO;
import com.bcgogo.user.dto.UserSwitchDTO;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.utils.BcgogoShopLogicResourceUtils;
import com.bcgogo.user.verifier.PrivilegeRequestProxy;
import com.bcgogo.utils.JsonUtil;
import com.bcgogo.utils.ShopConstant;
import com.bcgogo.utils.ValidatorConstant;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.*;

/**
 * User: Xiao Jian
 * Date: 12-1-18
 */

@Controller
@RequestMapping("/admin.do")
public class AdminController {
  private static final Logger LOG = LoggerFactory.getLogger(AdminController.class);


  /**
   * 获取短信开关列表
   * @param request
   * @param response
   * @return
   */
  @RequestMapping(params = "method=messageSwitch")
  public String messageSwitch(HttpServletRequest request,HttpServletResponse response) {
    INotificationService notificationService = ServiceManager.getService(INotificationService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    Long shopId = (Long) request.getSession().getAttribute("shopId");

    List<MessageTemplateDTO> messageTemplateDTOs = notificationService.getDistinctSceneMessageTemplateDTOAndSwitchStatus(shopId);
    if (CollectionUtils.isNotEmpty(messageTemplateDTOs)) {
      //批发商不显示完工短信
      if (!PrivilegeRequestProxy.verifierShopVersionResourceProxy(request, LogicResource.WEB_VERSION_VEHICLE_CONSTRUCTION)) {
        Iterator<MessageTemplateDTO> iterator = messageTemplateDTOs.iterator();
        while (iterator.hasNext()) {
          MessageTemplateDTO messageTemplateDTO = iterator.next();
          if (messageTemplateDTO.getScene() != null && (MessageScene.FINISH_MSG.equals(messageTemplateDTO.getScene()) || MessageScene.MEMBER_CONSUME_SMS_SWITCH.equals(messageTemplateDTO.getScene())||MessageScene.MOBILE_SMS.equals(messageTemplateDTO.getScene())||MessageScene.MOBILE_APP.equals(messageTemplateDTO.getScene()))) {
            iterator.remove();
          }

        }
      }
      //非供应商客户不显示，采购短信
      IConfigService configService = ServiceManager.getService(IConfigService.class);
      String customerShopVersionIds = configService.getConfig("CommonShopVersions", ShopConstant.BC_SHOP_ID);
      String fourSShopVersionIds = configService.getConfig("fourSShopVersions", ShopConstant.BC_SHOP_ID);
      if (StringUtils.isNotEmpty(customerShopVersionIds) && StringUtils.isNotEmpty(fourSShopVersionIds)) {
        customerShopVersionIds = customerShopVersionIds + "," + fourSShopVersionIds;
      }
      if (StringUtils.isNotBlank(customerShopVersionIds) && customerShopVersionIds.contains(String.valueOf(WebUtil.getShopVersionId(request)))) {
        String smsTypeFilter = "SALES_ACCEPTED,SALES_REFUSE,STOCKING_CANCEL,SHIPPED_CANCEL,RETURNS_ACCEPTED,RETURNS_REFUSE";
        Iterator<MessageTemplateDTO> iterator = messageTemplateDTOs.iterator();
        while (iterator.hasNext()) {
          MessageTemplateDTO messageTemplateDTO = iterator.next();
          if (messageTemplateDTO.getScene() != null && smsTypeFilter.contains(messageTemplateDTO.getScene().toString())) {
            iterator.remove();
          }
        }
      }
    }

    //获取全部必要的配置权限
    List<UserLimitDTO> userNecessaryLimitDTOList = userService.getUserNecessaryLimitTags(shopId);
    //第一次访问该页面时，初始化每个店铺的开关状态 - 默认关闭
    List<UserSwitchDTO> userSwitchDTOList = new ArrayList<UserSwitchDTO>();
    for (UserLimitDTO limitDTO : userNecessaryLimitDTOList) {
      //在UserSwitch表，每个权限开关，如果在该店铺已经使用过，则直接取来，如果是首次应用，则新增一条
      UserSwitchDTO userSwitchDTO = userService.getUserSwitchByShopIdAndScene(shopId, limitDTO.getScene());
      if (userSwitchDTO == null) {

        if(ConfigUtils.isFourSShopVersion(WebUtil.getShopVersionId(request))) {
          if (UserSwitchType.SCANNING_BARCODE.toString().equals(limitDTO.getScene()) || UserSwitchType.SCANNING_CARD.toString().equals(limitDTO.getScene())) {
            continue;
          }
        }

        if (!PrivilegeRequestProxy.verifierShopVersionResourceProxy(request, LogicResource.WEB_VERSION_VEHICLE_CONSTRUCTION)) {
          if(UserSwitchType.SETTLED_REMINDER.toString().equals(limitDTO.getScene())){
            continue;
          }
        }

        if(ConfigUtils.isWholesalerVersion(WebUtil.getShopVersionId(request))) {
             if(!UserSwitchType.SCANNING_CARD.toString().equals(limitDTO.getScene())) {
                 userService.addUserSwitch(shopId, limitDTO,true);
             }
        } else {
            userService.addUserSwitch(shopId, limitDTO,false);
        }

      }
    }
    userSwitchDTOList = userService.getUserSwitchListByShopId(shopId);


    request.setAttribute("messageTemplateDTOs", messageTemplateDTOs);
    request.setAttribute("userSwitchDTOList", userSwitchDTOList);

    return "/admin/customConfig/messageSwitch";
  }

  /**
   * 前台改变按钮状态，ajax请求到后台，相应改变数据库和MemCache中的值
   * @param request
   * @param response
   * @param scene
   * @param status
   * @throws Exception
   */
  @RequestMapping(params = "method=changeMessageSwitch")
  public void changeMessageSwitch(HttpServletRequest request,HttpServletResponse response,MessageScene scene,MessageSwitchStatus status) throws Exception
  {
    INotificationService notificationService = ServiceManager.getService(INotificationService.class);
    Long shopId = (Long)request.getSession().getAttribute("shopId");
    PrintWriter out = response.getWriter();
    String jsonStr = "";
    try{
      MessageSwitchDTO messageSwitchDTO = notificationService.SaveOrUpdateMessageSwitch(shopId,scene,status);

      if(null != messageSwitchDTO && status == messageSwitchDTO.getStatus())
      {
        jsonStr = "success";
      }
      else
      {
        jsonStr = "error";
      }
      Map<String,String> map = new HashMap<String, String>();
      map.put("resu",jsonStr);

      out.print(JsonUtil.mapToJson(map));
    }catch (Exception e){
      LOG.error("method=changeMessageSwitch");
      LOG.error("jsonStr",jsonStr);
      LOG.error("scene",scene.getScene());
      LOG.error("status",status.getStatus());
      LOG.error(e.getMessage(),e);
    }finally {
      out.flush();
      out.close();
    }
  }

  private String toNull(String s) {
    if (s != null && s.trim() == "") return null;
    return s;
  }

  /**
   * 前台改变按钮状态，ajax请求到后台，相应改变数据库和MemCache中的值
   * @param request
   * @param response
   * @param scene
   * @param status
   * @throws Exception
   */
  @RequestMapping(params = "method=changeUserSwitch")
  @ResponseBody
  public Object changeUserSwitch(HttpServletRequest request, HttpServletResponse response, String scene, String status) throws Exception {
    IUserService userService = ServiceManager.getService(IUserService.class);
    IPickingService pickingService = ServiceManager.getService(IPickingService.class);
    Long shopId = (Long) request.getSession().getAttribute("shopId");
    Result result = new Result();
    String jsonStr = "";
    try {
      if (StringUtils.isBlank(scene) || StringUtils.isBlank(status)) {
        result.setSuccess(false);
        result.setMsg(ValidatorConstant.REQUEST_ERROR_MSG);
        return result;
      }

      if (scene.trim().toUpperCase().equals(UserSwitchType.REPAIR_PICKING.toString())
          && status.trim().toUpperCase().equals("OFF")) {              //维修领料单关闭校验
        if (pickingService.checkRepairPickingUsedInProcessingOrder(shopId)) {
          return new Result(ValidatorConstant.REPAIR_PICKING_USED_PROCESSING_ORDER_MSG, false);
        }
      } else if (scene.trim().toUpperCase().equals(UserSwitchType.REPAIR_PICKING.toString())
          && status.trim().toUpperCase().equals("ON")) {         //维修领料单打开
        if (pickingService.checkProcessingRepairOrderUseMaterialByShopId(shopId)) {
          return new Result(ValidatorConstant.REPAIR_USED_PROCESSING_ORDER_MSG, false);
        }
      }
       //维修领料单打开校验
      UserSwitchDTO userSwitchDTO = userService.saveOrUpdateUserSwitch(shopId, scene, status);

      if (null != userSwitchDTO && status == userSwitchDTO.getStatus()) {
        return result;
      } else {
        result.setSuccess(false);
        result.setMsg(ValidatorConstant.REQUEST_ERROR_MSG);
        return result;
      }
    } catch (Exception e) {
      LOG.error("method=changeUserSwitch");
      LOG.error("jsonStr", jsonStr);
      LOG.error("scene", scene);
      LOG.error("status", status);
      LOG.error(e.getMessage(), e);
      result.setSuccess(false);
      result.setMsg(ValidatorConstant.REQUEST_ERROR_MSG);
      return result;
    }
  }

  @RequestMapping(params = "method=getScanningGroupStatus")
  @ResponseBody
  public Object getScanningGroupStatus(HttpServletRequest request, HttpServletResponse response) {
     Map<String,String> scanningGroupStatus = new HashMap<String,String>();
      IUserService userService = ServiceManager.getService(IUserService.class);
      Long shopId = (Long) request.getSession().getAttribute("shopId");
      UserSwitchDTO userSwitchDTO = userService.getUserSwitchByShopIdAndScene(shopId, UserSwitchType.SCANNING_CARD.toString());
      if(userSwitchDTO != null) {
          scanningGroupStatus.put(UserSwitchType.SCANNING_CARD.toString().toLowerCase(),userSwitchDTO.getStatus().toLowerCase());
      }
      userSwitchDTO = userService.getUserSwitchByShopIdAndScene(shopId, UserSwitchType.SCANNING_BARCODE.toString());
      if(userSwitchDTO != null) {
          scanningGroupStatus.put(UserSwitchType.SCANNING_BARCODE.toString().toLowerCase(),userSwitchDTO.getStatus().toLowerCase());
      }
      return scanningGroupStatus;
  }
}
