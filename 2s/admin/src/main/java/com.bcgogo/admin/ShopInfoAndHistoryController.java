package com.bcgogo.admin;

import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.SmsRechargeDTO;
import com.bcgogo.txn.service.ISmsRechargeService;
import com.bcgogo.util.PageException;
import com.bcgogo.util.Pager;
import com.bcgogo.utils.NumberUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zcl
 * Date: 12-2-17
 * Time: 下午4:49
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/shopInfoHistory.do")
public class ShopInfoAndHistoryController {
  private static final Log LOG=LogFactory.getLog(ShopInfoAndHistoryController.class);

  //显示店面消费记录
    @RequestMapping(params = "method=shopInfo")
    public String shopInfo(ModelMap model,HttpServletRequest request) throws PageException {
        IConfigService configService = ServiceManager.getService(IConfigService.class);
        ISmsRechargeService smsRechargeService = ServiceManager.getService(ISmsRechargeService.class);
        Long shopId = (Long) request.getSession().getAttribute("shopId");//10000010001100012
        String startTime=request.getParameter("startTime");
        String endTime=request.getParameter("endTime");
        String money=request.getParameter("money");
         String other=request.getParameter("other");
          ShopDTO shopDTO=configService.getShopById(shopId);
          model.addAttribute("shopDTO",shopDTO);
          int currentPage = NumberUtil.intValue(request.getParameter("pageNo"), 1);
          Pager pager = new Pager(currentPage);
         if((startTime==null||startTime=="")&&(endTime==null||endTime=="")&&(money==null||money=="")&&(other==null||other==""))
         {
          //记录数
          int totalCount = smsRechargeService.countShopSmsRecharge(shopId);
          model.addAttribute("recordCount", totalCount);
          //分页充值记录列表
          if(totalCount > 0){
              List<SmsRechargeDTO> smsRechargeDTOList = smsRechargeService.getShopSmsRechargeList(shopId, pager.getCurrentPage(), pager.getPageSize());
              model.addAttribute("smsRechargeDTOList", smsRechargeDTOList);
          }
          pager = new Pager(totalCount, pager.getCurrentPage());
            request.setAttribute("pager", pager);
         }
      else
         {
               int totalCount = smsRechargeService.countShopSmsRecharge(startTime,endTime,money,other,shopId);
             model.addAttribute("recordCount", totalCount);
          //分页充值记录列表
          if(totalCount > 0){
              List<SmsRechargeDTO> smsRechargeDTOList = smsRechargeService.getShopSmsRechargeList(startTime,endTime,money,other,shopId,pager.getCurrentPage(), pager.getPageSize());
              model.addAttribute("smsRechargeDTOList", smsRechargeDTOList);
          }
          pager = new Pager(totalCount, pager.getCurrentPage());
            request.setAttribute("pager", pager);

         }



          return "/shopInfo/storesManage";
  }


    }
