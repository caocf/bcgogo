package com.bcgogo.web;

import com.bcgogo.common.WebUtil;
import com.bcgogo.enums.TodoOrderType;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.verifier.PrivilegeRequestProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-12-15
 * Time: 下午9:32
 * 系统导航controller
 */
@Controller
@RequestMapping("/navigator.do")
public class NavigateController {
  private static final Logger LOG = LoggerFactory.getLogger(NavigateController.class);

  //=====================================系统设置=====================================
  @RequestMapping(params = "method=systemSetting")
  public void systemSetting(HttpServletRequest request, HttpServletResponse response) throws IOException {
    if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/shopData.do?method=toManageShopData")) {
      response.sendRedirect(request.getContextPath() + "/shopData.do?method=toManageShopData");
    } else if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/shopBasic.do?method=getShopBasicInfo")) {
      response.sendRedirect(request.getContextPath() + "/shopBasic.do?method=getShopBasicInfo");
    } else if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/staffManage.do?method=showStaffManagePage")) {
      response.sendRedirect(request.getContextPath() + "/staffManage.do?method=showStaffManagePage");
    } else if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/navigator.do?method=permissionManager")) {
      response.sendRedirect(request.getContextPath() + "/userGroupsManage.do?method=showUserGroupsManage");
    } else if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/admin.do?method=messageSwitch")) {
      response.sendRedirect(request.getContextPath() + "/admin.do?method=messageSwitch");
    } else if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/storehouse.do?method=storehouseManager")) {
      response.sendRedirect(request.getContextPath() + "/storehouse.do?method=storehouseManager");
    } else if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/import.do?method=openImportPage")) {
      response.sendRedirect(request.getContextPath() + "/import.do?method=openImportPage");
    } else if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/loanTransfers.do?method=showPage")) {
      response.sendRedirect(request.getContextPath() + "/loanTransfers.do?method=showPage");
    } else {
      LOG.warn("method=systemSetting,shopId:{},userId:{},userGroupId:{},shopVersion:{}",
          new Object[]{WebUtil.getShopId(request), WebUtil.getUserId(request), WebUtil.getUserGroupId(request), WebUtil.getShopVersion(request)});
      LOG.error("系统设置:权限配置错误！");
    }
  }

  //权限设置
  @RequestMapping(params = "method=permissionManager")
  public void permissionManager(HttpServletRequest request, HttpServletResponse response) throws IOException {

    if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/userGroupsManage.do?method=showUserGroupsManage")) {
      response.sendRedirect(request.getContextPath() + "/userGroupsManage.do?method=showUserGroupsManage");
    } else if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/permissionManager.do?method=showPermissionConfig")) {
      response.sendRedirect(request.getContextPath() + "/permissionManager.do?method=showPermissionConfig");
    } else {
      LOG.warn("method=permissionManager,shopId:{},userId:{},userGroupId:{},shopVersion:{}",
          new Object[]{WebUtil.getShopId(request), WebUtil.getUserId(request), WebUtil.getUserGroupId(request), WebUtil.getShopVersion(request)});
      LOG.error("系统设置:权限配置错误！");
    }
  }


  //=====================================财务统计=====================================
  @RequestMapping(params = "method=stat")
  public void stat(HttpServletRequest request, HttpServletResponse response) throws IOException {

    if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/navigator.do?method=businessStat")) {
      response.sendRedirect(request.getContextPath() + "/navigator.do?method=businessStat");
    } else if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/businessAccount.do?method=initBusinessAccountSearch")) {
      response.sendRedirect(request.getContextPath() + "/businessAccount.do?method=initBusinessAccountSearch");
    } else if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/navigator.do?method=arrearsStat")) {
      response.sendRedirect(request.getContextPath() + "/navigator.do?method=arrearsStat");
    } else if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/navigator.do?method=costStat")) {
      response.sendRedirect(request.getContextPath() + "/navigator.do?method=costStat");
    } else if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/bizstat.do?method=agentAchievements&month=thisMonth")) {
      response.sendRedirect(request.getContextPath() + "/bizstat.do?method=agentAchievements&month=thisMonth");
    } else if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/navigator.do?method=salesStat")) {
      response.sendRedirect(request.getContextPath() + "navigator.do?method=salesStat");
    } else {
      LOG.warn("method=stat,shopId:{},userId:{},userGroupId:{},shopVersion:{}",
          new Object[]{WebUtil.getShopId(request), WebUtil.getUserId(request), WebUtil.getUserGroupId(request), WebUtil.getShopVersion(request)});
      LOG.error("系统设置:权限配置错误！");
    }
  }

  //营业统计
  @RequestMapping(params = "method=businessStat")
  public void businessStat(HttpServletRequest request, HttpServletResponse response) throws IOException {

    if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/businessStat.do?method=getBusinessStat")) {
      response.sendRedirect(request.getContextPath() + "/businessStat.do?method=getBusinessStat");
    } else if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/itemStat.do?method=getItemStat")) {
      response.sendRedirect(request.getContextPath() + "/itemStat.do?method=getItemStat");
    } else if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/runningStat.do?method=getRunningStat")) {
      response.sendRedirect(request.getContextPath() + "/runningStat.do?method=getRunningStat");
    } else if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/member.do?method=memberStat")) {
      response.sendRedirect(request.getContextPath() + "/member.do?method=memberStat");
    } else {
      LOG.warn("method=businessStat,shopId:{},userId:{},userGroupId:{},shopVersion:{}",
          new Object[]{WebUtil.getShopId(request), WebUtil.getUserId(request), WebUtil.getUserGroupId(request), WebUtil.getShopVersion(request)});
      LOG.error("系统设置:权限配置错误！");
    }
  }

  //采购分析
  @RequestMapping(params = "method=costStat")
  public void costStat(HttpServletRequest request, HttpServletResponse response) throws IOException {

    if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/costStat.do?method=getCostStat")) {
      response.sendRedirect(request.getContextPath() + "/costStat.do?method=getCostStat");
    } else if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/costStat.do?method=getPriceStat")) {
      response.sendRedirect(request.getContextPath() + "/itemStat.do?method=getItemStat");
    } else if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/costStat.do?method=getSupplierStat")) {
      response.sendRedirect(request.getContextPath() + "/costStat.do?method=getSupplierStat");
    } else if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/costStat.do?method=getReturnStat")) {
      response.sendRedirect(request.getContextPath() + "/costStat.do?method=getReturnStat");
    } else {
      LOG.warn("method=costStat,shopId:{},userId:{},userGroupId:{},shopVersion:{}",
          new Object[]{WebUtil.getShopId(request), WebUtil.getUserId(request), WebUtil.getUserGroupId(request), WebUtil.getShopVersion(request)});
      LOG.error("系统设置:权限配置错误！");
    }
  }

  //畅销滞销
  @RequestMapping(params = "method=salesStat")
  public void salesStat(HttpServletRequest request, HttpServletResponse response) throws IOException {

    if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/salesStat.do?method=getGoodSaleCost")) {
      response.sendRedirect(request.getContextPath() + "/salesStat.do?method=getGoodSaleCost");
    } else if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/costStat.do?method=getPriceStat")) {
      response.sendRedirect(request.getContextPath() + "/salesStat.do?method=getBadSaleCost");
    } else {
      LOG.warn("method=salesStat,shopId:{},userId:{},userGroupId:{},shopVersion:{}",
          new Object[]{WebUtil.getShopId(request), WebUtil.getUserId(request), WebUtil.getUserGroupId(request), WebUtil.getShopVersion(request)});
      LOG.error("系统设置:权限配置错误！");
    }
  }

  //应收应付统计
  @RequestMapping(params = "method=arrearsStat")
  public void arrearsStat(HttpServletRequest request, HttpServletResponse response) throws IOException {

    if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/arrears.do?method=toReceivableStat")) {
      response.sendRedirect(request.getContextPath() + "/arrears.do?method=toReceivableStat");
    } else if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/arrears.do?method=toPayableStat")) {
      response.sendRedirect(request.getContextPath() + "/arrears.do?method=toPayableStat");
    } else {
      LOG.warn("method=arrearsStat,shopId:{},userId:{},userGroupId:{},shopVersion:{}",
          new Object[]{WebUtil.getShopId(request), WebUtil.getUserId(request), WebUtil.getUserGroupId(request), WebUtil.getShopVersion(request)});
      LOG.error("系统设置:权限配置错误！");
    }
  }

  // add by zhuj
  // 预收预付统计
  @RequestMapping(params = "method=depositStat")
  public void depositStat(HttpServletRequest request, HttpServletResponse response) {
    try {

      if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/depositOrdersStat.do?method=renderCustomerDepositOrderQueryPage")) {
        response.sendRedirect(request.getContextPath() + "/depositOrdersStat.do?method=renderCustomerDepositOrderQueryPage");
      } else if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/depositOrdersStat.do?method=renderSupplierDepositOrderQueryPage")) {
        response.sendRedirect(request.getContextPath() + "/depositOrdersStat.do?method=renderSupplierDepositOrderQueryPage");
      } else {
        LOG.warn("method=arrearsStat,shopId:{},userId:{},userGroupId:{},shopVersion:{}",
            new Object[]{WebUtil.getShopId(request), WebUtil.getUserId(request), WebUtil.getUserGroupId(request), WebUtil.getShopVersion(request)});
        LOG.error("系统设置:权限配置错误！");
      }
    } catch (IOException e) {
      LOG.warn("method=depositStat,shopId:{},userId:{},userGroupId:{},shopVersion:{}",
          new Object[]{WebUtil.getShopId(request), WebUtil.getUserId(request), WebUtil.getUserGroupId(request), WebUtil.getShopVersion(request)});
      LOG.error("系统设置:权限配置错误！");
    }
  }

  //=====================================供应商=====================================
  @RequestMapping(params = "method=supplierManager")
  public void supplierManager(HttpServletRequest request, HttpServletResponse response) throws IOException {

    if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/customer.do?method=searchSuppiler")) {
      response.sendRedirect(request.getContextPath() + "/customer.do?method=searchSuppiler");
    } else {
      LOG.warn("method=supplierManager,shopId:{},userId:{},userGroupId:{},shopVersion:{}",
          new Object[]{WebUtil.getShopId(request), WebUtil.getUserId(request), WebUtil.getUserGroupId(request), WebUtil.getShopVersion(request)});
      LOG.error("系统设置:权限配置错误！");
    }
  }

  //=====================================客户=====================================
  @RequestMapping(params = "method=customerManager")
  public void customerManager(HttpServletRequest request, HttpServletResponse response) throws IOException {

    if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/customer.do?method=customerdata")) {
      response.sendRedirect(request.getContextPath() + "/customer.do?method=customerdata");
    } else if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/sms.do?method=smswrite")) {
      response.sendRedirect(request.getContextPath() + "/sms.do?method=smswrite");
    } else if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/smsrecharge.do?method=smsrecharge")) {
      response.sendRedirect(request.getContextPath() + "/smsrecharge.do?method=smsrecharge");
    } else if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/member.do?method=selectFirstPage")) {
      response.sendRedirect(request.getContextPath() + "/member.do?method=selectFirstPage");
    } else if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/vehicleStat.do?method=carStatistics")) {
      response.sendRedirect(request.getContextPath() + "/vehicleStat.do?method=carStatistics");
    }  else if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/customer.do?method=vehicleManageList")) {
      response.sendRedirect(request.getContextPath() + "/customer.do?method=vehicleManageList");
    } else {
      LOG.warn("method=customerManager,shopId:{},userId:{},userGroupId:{},shopVersion:{}",
          new Object[]{WebUtil.getShopId(request), WebUtil.getUserId(request), WebUtil.getUserGroupId(request), WebUtil.getShopVersion(request)});
      LOG.error("系统设置:权限配置错误！");
    }
  }

  //=====================================txn=====================================
  @RequestMapping(params = "method=txnNavigator")
  public void txnNavigator(HttpServletRequest request, HttpServletResponse response) throws IOException {

    if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/navigator.do?method=stockSearch")) {
      response.sendRedirect(request.getContextPath() + "/navigator.do?method=stockSearch");
    } else {
      txnOtherNavigator(request, response);
    }
  }

  //库存管理
  @RequestMapping(params = "method=stockSearch")
  public void stockSearch(HttpServletRequest request, HttpServletResponse response) throws IOException {

    IUserService userService = ServiceManager.getService(IUserService.class);
    if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/stockSearch.do?method=getStockSearch")) {
      response.sendRedirect(request.getContextPath() + "/stockSearch.do?method=getStockSearch&type=txn");
    } else if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/inventoryCheck.do?method=createInventoryCheckByProductIds")) {
      response.sendRedirect(request.getContextPath() + "/inventoryCheck.do?method=createInventoryCheckByProductIds");
    } else if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/pick.do?method=showInnerPickingListPage")) {
      response.sendRedirect(request.getContextPath() + "/pick.do?method=showInnerPickingListPage");
    } else if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/pick.do?method=showInnerReturnListPage")) {
      response.sendRedirect(request.getContextPath() + "/pick.do?method=showInnerReturnListPage");
    } else if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/allocateRecord.do?method=allocateRecordList")) {
      response.sendRedirect(request.getContextPath() + "/allocateRecord.do?method=allocateRecordList");
    } else if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/pick.do?method=showRepairPickingListPage")
        && userService.isRepairPickingSwitchOn(WebUtil.getShopId(request))) {
      response.sendRedirect(request.getContextPath() + "/pick.do?method=showRepairPickingListPage");
    } else {
      txnOtherNavigator(request, response);
    }
  }

  private void txnOtherNavigator(HttpServletRequest request, HttpServletResponse response) throws IOException {
    if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/navigator.do?method=storage")) {
      response.sendRedirect(request.getContextPath() + "/navigator.do?method=storage");
    } else if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/navigator.do?method=sale")) {
      response.sendRedirect(request.getContextPath() + "/navigator.do?method=sale");
    } else if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/navigator.do?method=vehicleConstruction")) {
      response.sendRedirect(request.getContextPath() + "/navigator.do?method=vehicleConstruction");
    } else {
      LOG.warn("method=txnOtherNavigator,shopId:{},userId:{},userGroupId:{},shopVersion:{}",
          new Object[]{WebUtil.getShopId(request), WebUtil.getUserId(request), WebUtil.getUserGroupId(request), WebUtil.getShopVersion(request)});
      LOG.error("系统设置:权限配置错误！");
    }
  }

  //入库管理
  @RequestMapping(params = "method=storage")
  public void storage(HttpServletRequest request, HttpServletResponse response) throws IOException {

    if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/storage.do?method=getProducts")) {
      response.sendRedirect(request.getContextPath() + "/storage.do?method=getProducts&type=txn");
    } else if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/RFbuy.do?method=create")) {
      response.sendRedirect(request.getContextPath() + "/RFbuy.do?method=create");
    } else if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/goodsReturn.do?method=createReturnStorage")) {
      response.sendRedirect(request.getContextPath() + "/goodsReturn.do?method=createReturnStorage");
    } else {
      LOG.warn("method=storage,shopId:{},userId:{},userGroupId:{},shopVersion:{}",
          new Object[]{WebUtil.getShopId(request), WebUtil.getUserId(request), WebUtil.getUserGroupId(request), WebUtil.getShopVersion(request)});
      LOG.error("系统设置:权限配置错误！");
    }
  }

  //销售管理
  @RequestMapping(params = "method=sale")
  public void sale(HttpServletRequest request, HttpServletResponse response) throws IOException {

    if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/sale.do?method=getProducts")) {
      response.sendRedirect(request.getContextPath() + "/sale.do?method=getProducts&type=txn");
    } else if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/salesReturn.do?method=createSalesReturn")) {
      response.sendRedirect(request.getContextPath() + "/salesReturn.do?method=createSalesReturn");
    } else {
      LOG.warn("method=sale,shopId:{},userId:{},userGroupId:{},shopVersion:{}",
          new Object[]{WebUtil.getShopId(request), WebUtil.getUserId(request), WebUtil.getUserGroupId(request), WebUtil.getShopVersion(request)});
      LOG.error("系统设置:权限配置错误！");
    }
  }

  //=====================================车辆施工=====================================
  @RequestMapping(params = "method=vehicleConstruction")
  public void vehicleConstruction(HttpServletRequest request, HttpServletResponse response) throws IOException {

    if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/customer.do?method=carindex")) {
      response.sendRedirect(request.getContextPath() + "/customer.do?method=carindex");
    } else if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/txn.do?method=getRepairOrderByVehicleNumber")) {
      response.sendRedirect(request.getContextPath() + "/txn.do?method=getRepairOrderByVehicleNumber&task=maintain");
    } else if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/washBeauty.do?method=createWashBeautyOrder")) {
      response.sendRedirect(request.getContextPath() + "/washBeauty.do?method=createWashBeautyOrder");
    } else if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/category.do?method=getCategoryItemSearch")) {
      response.sendRedirect(request.getContextPath() + "/category.do?method=getCategoryItemSearch");
    } else if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/insurance.do?method=showInsuranceOrderList")) {
      response.sendRedirect(request.getContextPath() + "/insurance.do?method=showInsuranceOrderList");
    } else {
      LOG.warn("method=vehicleConstruction,shopId:{},userId:{},userGroupId:{},shopVersion:{}",
          new Object[]{WebUtil.getShopId(request), WebUtil.getUserId(request), WebUtil.getUserGroupId(request), WebUtil.getShopVersion(request)});
      LOG.error("系统设置:权限配置错误！");
    }
  }
  //=====================================单据查询=====================================
  @RequestMapping(params = "method=inquiryCenter")
  public void inquiryCenter(HttpServletRequest request, HttpServletResponse response) throws IOException {

    if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/inquiryCenter.do?method=inquiryCenterIndex")) {
      response.sendRedirect(request.getContextPath() + "/inquiryCenter.do?method=inquiryCenterIndex&pageType=all");
    } else if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/productThrough.do?method=redirectProductThroughDetail")) {
      response.sendRedirect(request.getContextPath() + "/productThrough.do?method=redirectProductThroughDetail");
    } else {
      LOG.warn("method=vehicleConstruction,shopId:{},userId:{},userGroupId:{},shopVersion:{}",
          new Object[]{WebUtil.getShopId(request), WebUtil.getUserId(request), WebUtil.getUserGroupId(request), WebUtil.getShopVersion(request)});
      LOG.error("系统设置:权限配置错误！");
    }
  }
  //=====================================订单中心=====================================
  @RequestMapping(params = "method=orderCenter")
  public void orderCenter(HttpServletRequest request, HttpServletResponse response) throws IOException {

    if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/orderCenter.do?method=showOrderCenter")) {
      response.sendRedirect(request.getContextPath() + "/orderCenter.do?method=showOrderCenter");
    } else if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/orderCenter.do?method=getTodoOrders")) {
      response.sendRedirect(request.getContextPath() + "/orderCenter.do?method=getTodoOrders&type=" + TodoOrderType.TODO_SALE_ORDERS.toString());
    } else if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/orderCenter.do?method=getTodoOrders")) {
      response.sendRedirect(request.getContextPath() + "/orderCenter.do?method=getTodoOrders&type=" + TodoOrderType.TODO_SALE_RETURN_ORDERS.toString());
    } else if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/orderCenter.do?method=getTodoOrders")) {
      response.sendRedirect(request.getContextPath() + "/orderCenter.do?method=getTodoOrders&type=" + TodoOrderType.TODO_PURCHASE_ORDERS.toString());
    } else if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/orderCenter.do?method=getTodoOrders")) {
      response.sendRedirect(request.getContextPath() + "/orderCenter.do?method=getTodoOrders&type=" + TodoOrderType.TODO_PURCHASE_RETURN_ORDERS.toString());
    } else {
      LOG.warn("method=orderCenter,shopId:{},userId:{},userGroupId:{},shopVersion:{}",
          new Object[]{WebUtil.getShopId(request), WebUtil.getUserId(request), WebUtil.getUserGroupId(request), WebUtil.getShopVersion(request)});
      LOG.error("系统设置:权限配置错误！");
    }
  }

  //=====================================代办事项=====================================
  @RequestMapping(params = "method=schedule")
  public String schedule(HttpServletRequest request, HttpServletResponse response) throws IOException {

    if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/remind.do?method=newtodo")) {
      return "redirect:remind.do?method=newtodo";
    } else if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/remind.do?method=toPlansRemind")) {
      return "redirect:remind.do?method=toPlansRemind";
    } else if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/draft.do?method=toDraftOrderBox")) {
      return "redirect:draft.do?method=toDraftOrderBox";
    } else if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/navigator.do?method=messageCenter")) {
      return "redirect:navigator.do?method=messageCenter";
    } else if(PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/appoint.do?method=showAppointOrderList")){
      return "redirect:appoint.do?method=showAppointOrderList";
    }else if(PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/supplier.do?method=showAppShopCommentList")){
      return "redirect:supplier.do?method=showAppShopCommentList";
    }
    else {
      LOG.warn("method=schedule,shopId:{},userId:{},userGroupId:{},shopVersion:{}",
          new Object[]{WebUtil.getShopId(request), WebUtil.getUserId(request), WebUtil.getUserGroupId(request), WebUtil.getShopVersion(request)});
      LOG.error("系统设置:权限配置错误！");
    }
    return "redirect:user.do?method=createmain";
  }

  //消息中心
  @RequestMapping(params = "method=messageCenter")
  public String messageCenter(HttpServletRequest request, HttpServletResponse response) throws IOException {

    if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/pushMessage.do?method=receiverPushMessageList")) {
      return "redirect:pushMessage.do?method=receiverPushMessageList";
    }else if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/stationMessage.do?method=showSendStationMessageList")) {
      return "redirect:stationMessage.do?method=showSendStationMessageList";
    } else if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/stationMessage.do?method=createStationMessage")) {
      return "redirect:stationMessage.do?method=createStationMessage";
    } else {
      LOG.warn("method=messageCenter,shopId:{},userId:{},userGroupId:{},shopVersion:{}",
          new Object[]{WebUtil.getShopId(request), WebUtil.getUserId(request), WebUtil.getUserGroupId(request), WebUtil.getShopVersion(request)});
      LOG.error("系统设置:权限配置错误！");
    }
    return "redirect:user.do?method=createmain";
  }

  //=====================================供求中心=====================================
  @RequestMapping(params = "method=autoAccessoryOnline")
  public void autoAccessoryOnline(HttpServletRequest request, HttpServletResponse response) throws IOException {

    if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/supplyDemand.do?method=toSupplyDemand")) {
      response.sendRedirect(request.getContextPath() + "/supplyDemand.do?method=toSupplyDemand");
    }else if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/navigator.do?method=orderCenter")) {
      response.sendRedirect(request.getContextPath() + "/navigator.do?method=orderCenter");
    } else if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/navigator.do?method=salesAccessory")) {
      response.sendRedirect(request.getContextPath() + "/navigator.do?method=salesAccessory");
    } else if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/navigator.do?method=buyAccessory")) {
      response.sendRedirect(request.getContextPath() + "/navigator.do?method=buyAccessory");
    } else if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/preBuyOrder.do?method=preBuyInformation")) {
      response.sendRedirect(request.getContextPath() + "/preBuyOrder.do?method=preBuyInformation");
    } else {
      LOG.warn("method=autoAccessoryOnline,shopId:{},userId:{},userGroupId:{},shopVersion:{}",
          new Object[]{WebUtil.getShopId(request), WebUtil.getUserId(request), WebUtil.getUserGroupId(request), WebUtil.getShopVersion(request)});
      LOG.error("系统设置:权限配置错误！");
    }

  }

  //=====================================我要卖配件=====================================
  @RequestMapping(params = "method=salesAccessory")
  public void salesAccessory(HttpServletRequest request, HttpServletResponse response) throws IOException {
    //商品上架、客户库存、我的报价
    if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/goodsInOffSales.do?method=goodsInOffSalesManage")) {
      response.sendRedirect(request.getContextPath() + "/goodsInOffSales.do?method=goodsInOffSalesManage");
    }else if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/autoAccessoryOnline.do?method=toRelatedCustomerStock")) {
      response.sendRedirect(request.getContextPath() + "/autoAccessoryOnline.do?method=toRelatedCustomerStock");
    }else if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/preBuyOrder.do?method=quotedPreBuyOrderManage")) {
      response.sendRedirect(request.getContextPath() + "preBuyOrder.do?method=quotedPreBuyOrderManage");
    }else {
      LOG.warn("method=salesAccessory,shopId:{},userId:{},userGroupId:{},shopVersion:{}",
          new Object[]{WebUtil.getShopId(request), WebUtil.getUserId(request), WebUtil.getUserGroupId(request), WebUtil.getShopVersion(request)});
      LOG.error("系统设置:权限配置错误！");
    }
  }

  //=====================================我要买配件=====================================
  @RequestMapping(params = "method=buyAccessory")
  public void buyAccessory(HttpServletRequest request, HttpServletResponse response) throws IOException {
    //发布求购、我的求购、购物车、在线退货
    if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/preBuyOrder.do?method=createPreBuyOrder")) {
      response.sendRedirect(request.getContextPath() + "/preBuyOrder.do?method=createPreBuyOrder");
    } else if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/preBuyOrder.do?method=preBuyOrderManage")) {
      response.sendRedirect(request.getContextPath() + "/preBuyOrder.do?method=preBuyOrderManage");
    } else if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/shoppingCart.do?method=shoppingCartManage")) {
      response.sendRedirect(request.getContextPath() + "/shoppingCart.do?method=shoppingCartManage");
    } else if (PrivilegeRequestProxy.verifierUserGroupResourceProxy(request, "/web/onlineReturn.do?method=toOnlinePurchaseReturnSelect")) {
      response.sendRedirect(request.getContextPath() + "/onlineReturn.do?method=toOnlinePurchaseReturnSelect");
    } else {
      LOG.warn("method=buyAccessory,shopId:{},userId:{},userGroupId:{},shopVersion:{}",
          new Object[]{WebUtil.getShopId(request), WebUtil.getUserId(request), WebUtil.getUserGroupId(request), WebUtil.getShopVersion(request)});
      LOG.error("系统设置:权限配置错误！");
    }
  }
}
