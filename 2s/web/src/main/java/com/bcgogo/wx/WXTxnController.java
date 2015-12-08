package com.bcgogo.wx;

import com.bcgogo.common.Result;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.ConsumeType;
import com.bcgogo.enums.MemberStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.notification.OperatorType;
import com.bcgogo.enums.supplierComment.CommentRecordType;
import com.bcgogo.enums.supplierComment.CommentStatus;
import com.bcgogo.search.dto.OrderSearchConditionDTO;
import com.bcgogo.search.dto.OrderSearchResultDTO;
import com.bcgogo.search.dto.OrderSearchResultListDTO;
import com.bcgogo.search.service.order.ISearchOrderService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.dto.supplierComment.AppUserCommentRecordDTO;
import com.bcgogo.txn.service.IProductHistoryService;
import com.bcgogo.txn.service.IServiceHistoryService;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.txn.service.supplierComment.AppUserCommentService;
import com.bcgogo.txn.service.supplierComment.IAppUserCommentService;
import com.bcgogo.user.dto.*;
import com.bcgogo.user.service.IMembersService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.wx.IWXUserService;
import com.bcgogo.user.service.wx.WXHelper;
import com.bcgogo.utils.ArrayUtil;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;
import com.bcgogo.wx.user.WXUserVehicleDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigInteger;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 14-9-19
 * Time: 下午4:29
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/wxTxn.do")
public class WXTxnController {
  private static final Logger LOG = LoggerFactory.getLogger(WXTxnController.class);

  @Autowired
  private IWXUserService wxUserService;
  @Autowired
  private ITxnService txnService;
  @Autowired
  private IConfigService configService;
  @Autowired
  private IProductHistoryService productHistoryService;
  @Autowired
  private ISearchOrderService searchOrderService;

  private static final String PAGE_ORDER_DETAIL="/wx/o_detail";

  private static final String PAGE_ORDER_List="/wx/o_list";
  //会员卡详细
  private static final String PAGE_MEMBER_CARD="/wx/m_card";


  @RequestMapping(params="method=saveCommentRecord")
  @ResponseBody
  public Object saveOrUpdateWXUserCommentRecord(AppUserCommentRecordDTO commentRecordDTO){
    try{
      Result result=new Result();
      IAppUserCommentService commentService=ServiceManager.getService(IAppUserCommentService.class);
      commentRecordDTO.setCommentTime(System.currentTimeMillis());
      commentRecordDTO.setCommentatorType(OperatorType.WX_USER);
      commentRecordDTO.setCommentRecordType(CommentRecordType.WX_TO_SHOP);
//      commentRecordDTO.setCommentStatus(CommentStatus.UN_STAT);
      commentService.saveOrUpdateWXUserCommentRecord(commentRecordDTO);

      return result;
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
      return null;
    }
  }


  /**
   *
   * @param modelMap
   * @param o    openId
   * @param m    memberId
   * @return
   */
  @RequestMapping(params = "method=mCard")
  public Object toMemberCardDetail(ModelMap modelMap,String o,String m){
    try{
      if(StringUtil.isEmpty(m)) return null;
      Long memberId= NumberUtil.longValue(new BigInteger(m, 36).toString(10));
      MemberDTO memberDTO=txnService.getMemberInfo(memberId);
      ShopDTO shopDTO=ServiceManager.getService(IConfigService.class).getShopById(memberDTO.getShopId());
      if(shopDTO!=null) {
        memberDTO.setShopName(shopDTO.getName());
        modelMap.put("address",shopDTO.getAddress());
      }
      modelMap.put("memberDTO",memberDTO);
      OrderSearchConditionDTO conditionDTO=new OrderSearchConditionDTO();
      conditionDTO.setAccountMemberNo(memberDTO.getMemberNo());
      List<String> vehicleNos=new ArrayList<String>();
      List<WXUserVehicleDTO> userVehicleDTOs=wxUserService.getWXUserVehicle(o, null);
      if(CollectionUtil.isNotEmpty(userVehicleDTOs)){
        for(WXUserVehicleDTO userVehicleDTO:userVehicleDTOs){
          if(userVehicleDTO==null||StringUtil.isEmpty(userVehicleDTO.getVehicleNo())){
            continue;
          }
          vehicleNos.add(userVehicleDTO.getVehicleNo());
        }
      }
      conditionDTO.setVehicleList(vehicleNos.toArray(new String[vehicleNos.size()]));
      List<OrderSearchResultDTO> orderSearchResultDTOs=wxUserService.getWOrdersByConditionDTO(conditionDTO);
      if(CollectionUtil.isNotEmpty(orderSearchResultDTOs)){
        List<WXOrderDTO> orderDTOs=new ArrayList<WXOrderDTO>();
        for(OrderSearchResultDTO resultDTO:orderSearchResultDTOs){
          WXOrderDTO orderDTO=new WXOrderDTO();
          orderDTO.setVestDateStr(resultDTO.getVestDateStr());
          orderDTO.setVehicle(resultDTO.getVehicle());
          orderDTO.setTotal(resultDTO.getAmount()>0?StringUtil.valueOf(resultDTO.getAmount()):"计次划卡");
          orderDTO.setOrderDetailUrl(WXHelper.orderDetailUrl(resultDTO.getOrderId(),resultDTO.getOrderType(),resultDTO.getVehicle()));
          orderDTOs.add(orderDTO);
        }
        modelMap.put("orders",orderDTOs);
      }
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
      return null;
    }
    return PAGE_MEMBER_CARD;
  }


  /**
   * 跳转到单据列举
   * @param modelMap
   * @param _i
   * @return
   */
  @RequestMapping(params = "method=oList")
  public Object toWOrderList(ModelMap modelMap,String _i){
    if(StringUtil.isEmpty(_i))  return PAGE_ORDER_List;
    try{
      List<WXUserVehicleDTO> userVehicleDTOs=wxUserService.getWXUserVehicle(_i, null);
      List<String> vehicleNos=new ArrayList<String>();
      for(WXUserVehicleDTO userVehicleDTO:userVehicleDTOs){
        vehicleNos.add(userVehicleDTO.getVehicleNo());
      }
      modelMap.put("vehicleNos",vehicleNos);
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
      return null;
    }
    return PAGE_ORDER_List;
  }


  /**
   * 查询单据信息
   * @param conditionDTO
   * @return
   */
  @RequestMapping(params = "method=gList")
  @ResponseBody
  public Object getWOrderList(OrderSearchConditionDTO conditionDTO){
    try{
      conditionDTO.setSort("created_time desc");
      String[] orderTypes={OrderTypes.SALE.toString(), OrderTypes.REPAIR.toString(), OrderTypes.WASH_BEAUTY.toString()};
      conditionDTO.setOrderType(orderTypes);
      String[] searchStrategy={OrderSearchConditionDTO.SEARCHSTRATEGY_NO_SHOP_RESTRICT};
      conditionDTO.setSearchStrategy(searchStrategy);
      conditionDTO.setRowStart((conditionDTO.getStartPageNo() - 1) *conditionDTO.getPageRows());
      OrderSearchResultListDTO resultListDTO = searchOrderService.queryOrders(conditionDTO);
      if(CollectionUtil.isEmpty(resultListDTO.getOrders())){
        return null;
      }
      List<WXOrderDTO> orderDTOs=new ArrayList<WXOrderDTO>();
      for(OrderSearchResultDTO resultDTO:resultListDTO.getOrders()){
        WXOrderDTO orderDTO=new WXOrderDTO();
        orderDTO.setOrderId(resultDTO.getOrderId());
        orderDTO.setShopName(resultDTO.getShopName());
        orderDTO.setOrderType(resultDTO.getOrderTypeValue());
        orderDTO.setVestDateStr(resultDTO.getVestDateStr());
        orderDTO.setTotal(StringUtil.valueOf(resultDTO.getAmount()));
        orderDTO.setOrderDetailUrl(WXHelper.orderDetailUrl(resultDTO.getOrderId(),resultDTO.getOrderType(),resultDTO.getVehicle()));
        if(ArrayUtil.contains(resultDTO.getPayMethod(),"MEMBER_BALANCE_PAY")){
          orderDTO.setPayMethod("计次卡消费");
        }
        orderDTOs.add(orderDTO);
      }
      return orderDTOs;
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
    }
    return PAGE_ORDER_List;

  }

  @RequestMapping(params = "method=2w")
  public Object toWOrderDetail(ModelMap modelMap,String v,OrderTypes o,String _i){
    if(StringUtil.isEmpty(v)||StringUtil.isEmpty(_i)) return null;
    Long orderId= NumberUtil.longValue(new BigInteger(_i, 36).toString(10));
    Long shopId=null;
    Long customerId=null;
    OrderTypes orderTypes=o;
    try {
      switch (orderTypes){
        case SALE:
          SalesOrderDTO salesOrderDTO=getSalesOrderDTO(modelMap, orderId);
          shopId=salesOrderDTO.getShopId();
          customerId=salesOrderDTO.getCustomerId();
          if(salesOrderDTO==null){
            return PAGE_ORDER_DETAIL;
          }
          break;
        case REPAIR:
          RepairOrderDTO repairOrderDTO=getRepairOrderDTO(modelMap, orderId);
          if(repairOrderDTO==null){
            return PAGE_ORDER_DETAIL;
          }
          shopId=repairOrderDTO.getShopId();
          customerId=repairOrderDTO.getCustomerId();
          break;
        case WASH_BEAUTY:
          WashBeautyOrderDTO washBeautyOrderDTO=getWashBeautyOrderDTO(modelMap,orderId);
          if(washBeautyOrderDTO==null){
            return PAGE_ORDER_DETAIL;
          }
          shopId=washBeautyOrderDTO.getShopId();
          customerId=washBeautyOrderDTO.getCustomerId();
          break;
      }

      modelMap.put("orderTypes",orderTypes);
      //店铺信息
      modelMap.put("shopDTO",configService.getShopById(shopId));
      //评分信息
      modelMap.put("commentRecordDTO",ServiceManager.getService(AppUserCommentService.class).getAppUserCommentRecordByOrderId(null,orderId));

      //会员信息
      if(OrderTypes.REPAIR.equals(orderTypes)||OrderTypes.WASH_BEAUTY.equals(orderTypes)){
        modelMap.put("memberDTO",txnService.getMemberInfo(shopId,customerId));
      }

    }catch (Exception e){
      LOG.error(e.getMessage(),e);
    }
    return PAGE_ORDER_DETAIL;
  }

  private SalesOrderDTO getSalesOrderDTO(ModelMap modelMap,Long orderId) throws Exception {
    SalesOrderDTO salesOrderDTO=txnService.getSalesOrder(orderId);
    if(salesOrderDTO==null){
      return null;
    }
    Long shopId=salesOrderDTO.getShopId();
    ReceivableDTO receivableDTO = txnService.getReceivableByShopIdAndOrderTypeAndOrderId(shopId,null,salesOrderDTO.getId());
    salesOrderDTO.setReceivableDTO(receivableDTO);
    SalesOrderItemDTO[] itemDTOs=salesOrderDTO.getItemDTOs();
    if (ArrayUtil.isNotEmpty(salesOrderDTO.getItemDTOs())){
      for(int i=0;i<itemDTOs.length;i++){
        SalesOrderItemDTO itemDTO=itemDTOs[i];
        itemDTO.setProductHistoryDTO(productHistoryService.getProductHistoryById(itemDTO.getProductHistoryId(),shopId));
        itemDTO.setProductInfo(itemDTO.generateProductInfo());
      }
    }
    List<SalesOrderOtherIncomeItemDTO> salesOrderOtherIncomeItemDTOs=txnService.getSalesOrderOtherIncomeItemDTOs(salesOrderDTO.getId());
    salesOrderDTO.setOtherIncomeItemDTOList(salesOrderOtherIncomeItemDTOs);
    salesOrderDTO.calculateTotal();
    modelMap.put("order",salesOrderDTO);
    modelMap.put("vehicleNo",salesOrderDTO.getLicenceNo());
    modelMap.put("settleDate",salesOrderDTO.getVestDateStr());
    modelMap.put("oItemDTOs",salesOrderOtherIncomeItemDTOs);
    return salesOrderDTO;
  }

  private RepairOrderDTO getRepairOrderDTO(ModelMap modelMap,Long orderId) throws Exception {
    RepairOrderDTO repairOrderDTO=txnService.getRepairOrder(orderId);
    if(repairOrderDTO==null){
      return null;
    }
    Long shopId=repairOrderDTO.getShopId();
    RepairOrderItemDTO [] rItemDTOs=repairOrderDTO.getItemDTOs();
    if (ArrayUtil.isNotEmpty(repairOrderDTO.getItemDTOs())){
      for(int i=0;i<rItemDTOs.length;i++){
        RepairOrderItemDTO itemDTO=rItemDTOs[i];
        itemDTO.setProductHistoryDTO(productHistoryService.getProductHistoryById(itemDTO.getProductHistoryId(),shopId));
        itemDTO.setProductInfo(itemDTO.generateProductInfo());
      }
    }
    if(ArrayUtil.isNotEmpty(repairOrderDTO.getServiceDTOs())){
      ServiceManager.getService(IServiceHistoryService.class).setServiceHistory(repairOrderDTO);
    }
    List<RepairOrderOtherIncomeItemDTO> repairOrderOtherIncomeItemDTOs=txnService.getRepairOtherIncomeItemByOrderId(shopId,repairOrderDTO.getId());
    repairOrderDTO.setOtherIncomeItemDTOList(repairOrderOtherIncomeItemDTOs);
    repairOrderDTO.calculateTotal();
    modelMap.put("order",repairOrderDTO);
    modelMap.put("vehicleNo",repairOrderDTO.getVechicle());
    modelMap.put("settleDate",repairOrderDTO.getSettleDateStr());
    modelMap.put("oItemDTOs",repairOrderOtherIncomeItemDTOs);
    return repairOrderDTO;
  }

  private WashBeautyOrderDTO getWashBeautyOrderDTO(ModelMap modelMap,Long orderId) throws Exception {
    WashBeautyOrderDTO washBeautyOrderDTO=txnService.getWashBeautyOrderDTOById(null,orderId);
    if(washBeautyOrderDTO==null){
      return null;
    }
    Long shopId=washBeautyOrderDTO.getShopId();
    ReceivableDTO receivableDTO = txnService.getReceivableByShopIdAndOrderTypeAndOrderId(shopId,null,washBeautyOrderDTO.getId());
    washBeautyOrderDTO.setReceivableDTO(receivableDTO);
    if(ArrayUtil.isNotEmpty(washBeautyOrderDTO.getWashBeautyOrderItemDTOs())){
      double serviceTotal=0;
      ServiceManager.getService(IServiceHistoryService.class).setServiceHistory(washBeautyOrderDTO);
      for (WashBeautyOrderItemDTO itemDTO : washBeautyOrderDTO.getWashBeautyOrderItemDTOs()) {
        if(ConsumeType.TIMES.equals(itemDTO.getPayType())){
          itemDTO.setPriceStr(itemDTO.getConsumeTypeName());
        } else if(ConsumeType.COUPON.equals(itemDTO.getPayType())){
          itemDTO.setPriceStr(ConsumeType.COUPON.getType());
        }else if(ConsumeType.MONEY.equals(itemDTO.getPayType())){
          itemDTO.setPriceStr(String.valueOf(itemDTO.getPrice()));
          serviceTotal += itemDTO.getPrice();
        }
      }
      washBeautyOrderDTO.setServiceTotal(NumberUtil.doubleVal(serviceTotal));
    }
    modelMap.put("order",washBeautyOrderDTO);
    modelMap.put("vehicleNo",washBeautyOrderDTO.getVechicle());
    modelMap.put("settleDate",washBeautyOrderDTO.getVestDateStr());
    return washBeautyOrderDTO;
  }
}