package com.bcgogo.admin.config.shop;

import com.bcgogo.common.Result;
import com.bcgogo.util.WebUtil;
import com.bcgogo.config.dto.ShopBargainRecordDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.model.Shop;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IShopBargainService;
import com.bcgogo.config.service.IShopService;
import com.bcgogo.enums.shop.BargainStatus;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.finance.IBcgogoReceivableService;
import com.bcgogo.user.dto.UserDTO;
import com.bcgogo.user.service.permission.IUserCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: ZhangJuntao
 * Date: 13-3-31
 * Time: 下午3:33
 */
@Controller
@RequestMapping("/shopBargain.do")
public class ShopBargainController {
  private static final Logger LOG = LoggerFactory.getLogger(ShopBargainController.class);

  @RequestMapping(params = "method=getShopBargainRecords")
  @ResponseBody
  public Object getShopBargainRecords(HttpServletRequest request, HttpServletResponse response, long shopId) {
    IShopBargainService bargainService = ServiceManager.getService(IShopBargainService.class);
    Result result = new Result(true);
    try {
      List<ShopBargainRecordDTO> dtoList = bargainService.getShopBargainRecordsByShopId(shopId);
      Set<Long> userIds = new HashSet<Long>();
      for (ShopBargainRecordDTO dto : dtoList) {
        userIds.add(dto.getAuditorId());
        userIds.add(dto.getApplicantId());
      }
      UserDTO userDTO;
      Map<Long, UserDTO> userDTOMap = ServiceManager.getService(IUserCacheService.class).getUserMap(userIds);
      for (ShopBargainRecordDTO dto : dtoList) {
        userDTO = userDTOMap.get(dto.getAuditorId());
        if (userDTO != null) dto.setAuditorName(userDTO.getName());
        userDTO = userDTOMap.get(dto.getApplicantId());
        if (userDTO != null) dto.setApplicantName(userDTO.getName());
      }
      result.setData(dtoList);
      result.setTotal(dtoList.size());
    } catch (Exception e) {
      result.setSuccess(false);
      LOG.debug("/shopBargain.do");
      LOG.debug("method=getShopBargainRecords");
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  @RequestMapping(params = "method=createShopBargainRecord")
  @ResponseBody
  public Object createShopBargainRecord(HttpServletRequest request, HttpServletResponse response, ShopBargainRecordDTO dto) {
    Result result = new Result(true);
    try {
      IShopBargainService bargainService = ServiceManager.getService(IShopBargainService.class);
      if (dto.getShopId() == null) throw new Exception("shopId is null.");
      result = ServiceManager.getService(IBcgogoReceivableService.class).validateBargainContext(dto.getShopId());
      if (!result.isSuccess()) return result;
      dto.setApplicantId(WebUtil.getUserId(request));
      dto.setApplicationTime(System.currentTimeMillis());
      dto.setBargainStatus(BargainStatus.PENDING_REVIEW);
      bargainService.createShopBargainRecord(dto);
      result.setMsg("议价提交成功！");
    } catch (Exception e) {
      LOG.debug("/shopBargain.do");
      LOG.debug("method=createShopBargainRecord");
      LOG.error(e.getMessage(), e);
      result.setSuccess(false);
      result.setMsg("操作失败!");
    }
    return result;
  }

  @RequestMapping(params = "method=auditShopBargainRecord")
  @ResponseBody
  public Object auditShopBargainRecord(HttpServletRequest request, HttpServletResponse response, long shopId, BargainStatus status, String reason) {
    Result result = new Result(true);
    try {
      IShopBargainService bargainService = ServiceManager.getService(IShopBargainService.class);
      if (BargainStatus.AUDIT_PASS == status) {
        ShopDTO shopDTO = ServiceManager.getService(IConfigService.class).getShopById(shopId);
        //更新待支付记录
        result = ServiceManager.getService(IBcgogoReceivableService.class).updateSoftwareReceivable(shopId, shopDTO.getBargainPrice());
        if (!result.isSuccess())
          return result;
      }
      bargainService.auditShopBargainRecord(shopId, WebUtil.getUserId(request), status, reason);
      result.setMsg("议价审核成功！");
    } catch (Exception e) {
      LOG.debug("/shopBargain.do");
      LOG.debug("method=auditShopBargainRecord");
      LOG.error(e.getMessage(), e);
      result.setSuccess(false);
      result.setMsg("操作失败!");
    }
    return result;
  }

}
