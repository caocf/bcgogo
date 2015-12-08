package com.bcgogo.admin.finance;

import com.bcgogo.common.Result;
import com.bcgogo.config.dto.OperationLogDTO;
import com.bcgogo.config.dto.ShopBargainRecordDTO;
import com.bcgogo.config.service.IOperationLogService;
import com.bcgogo.config.service.IShopBargainService;
import com.bcgogo.enums.ObjectTypes;
import com.bcgogo.enums.OperationTypes;
import com.bcgogo.enums.shop.BargainStatus;
import com.bcgogo.enums.txn.finance.BuyChannels;
import com.bcgogo.enums.txn.finance.PaymentMethod;
import com.bcgogo.enums.txn.finance.PaymentStatus;
import com.bcgogo.enums.txn.finance.PaymentType;
import com.bcgogo.product.service.IBcgogoProductService;
import com.bcgogo.txn.dto.finance.*;
import com.bcgogo.user.dto.UserDTO;
import com.bcgogo.user.dto.permission.ShopVersionDTO;
import com.bcgogo.user.service.permission.IShopVersionService;
import com.bcgogo.user.service.permission.IUserCacheService;
import com.bcgogo.util.WebUtil;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.finance.IBcgogoReceivableService;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.ShopConstant;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;


/**
 * User: zhangjuntao
 * Date: 13-3-19
 * Time: 10:34 PM
 */
@Controller
@RequestMapping("/bcgogoReceivable.do")
public class BcgogoReceivableController {
  private static final Logger LOG = LoggerFactory.getLogger(BcgogoReceivableController.class);


  @RequestMapping(params = "method=bcgogoReceivableOrderItemChangePrice")
  @ResponseBody
  public Object bcgogoReceivableOrderItemChangePrice(HttpServletRequest request, HttpServletResponse response,Long id,Double price,Double total) {
    IBcgogoReceivableService receivableService = ServiceManager.getService(IBcgogoReceivableService.class);
    IOperationLogService operationLogService = ServiceManager.getService(IOperationLogService.class);

    Result result = new Result();
    try {
      BcgogoReceivableOrderItemDTO bcgogoReceivableOrderItemDTO = receivableService.getBcgogoReceivableOrderItemDTO(id);
      Double oldPrice = bcgogoReceivableOrderItemDTO.getPrice();
      Double oldTotal = bcgogoReceivableOrderItemDTO.getTotal();
      if(!NumberUtil.compareDouble(oldPrice,price) || !NumberUtil.compareDouble(oldTotal,total)){
        bcgogoReceivableOrderItemDTO.setPrice(price);
        bcgogoReceivableOrderItemDTO.setTotal(total);
        receivableService.updateBcgogoReceivableOrderItemDTO(bcgogoReceivableOrderItemDTO);

        StringBuilder log = new StringBuilder(WebUtil.getUserName(request)+"修改价格;");
        if(!NumberUtil.compareDouble(oldPrice,price)){
          log.append("单价由￥"+oldPrice+"变为￥"+price+"；");
        }
        if(!NumberUtil.compareDouble(oldTotal,total)){
          log.append("总价由￥"+oldTotal+"变为￥"+total+"；");
        }
        BcgogoReceivableOrderDTO bcgogoReceivableOrderDTO = receivableService.getSimpleBcgogoReceivableOrderDTO(bcgogoReceivableOrderItemDTO.getOrderId());
        ObjectTypes objectTypes = null;
        if(PaymentType.HARDWARE.equals(bcgogoReceivableOrderDTO.getPaymentType())){
          objectTypes = ObjectTypes.BCGOGO_HARDWARE_RECEIVABLE_ORDER;
        }else if(PaymentType.SOFTWARE.equals(bcgogoReceivableOrderDTO.getPaymentType())){
          objectTypes = ObjectTypes.BCGOGO_SOFTWARE_RECEIVABLE_ORDER;
        }else if(PaymentType.SMS_RECHARGE.equals(bcgogoReceivableOrderDTO.getPaymentType())){
          objectTypes = ObjectTypes.BCGOGO_SMS_RECHARGE_RECEIVABLE_ORDER;
        }
        if(objectTypes!=null){
          OperationLogDTO operationLogDTO = new OperationLogDTO(ShopConstant.BC_ADMIN_SHOP_ID, WebUtil.getUserId(request),bcgogoReceivableOrderDTO.getId(),objectTypes, OperationTypes.CHANGE_PRICE);
          operationLogDTO.setContent(log.toString());
          operationLogService.saveOperationLog(operationLogDTO);
        }
      }

    } catch (Exception e) {
      result = new Result(false);
      LOG.debug("/bcgogoReceivable.do");
      LOG.debug("method=bcgogoReceivableOrderItemChangePrice");
      LOG.error(e.getMessage(), e);
      result.setSuccess(false);
      result.setMsg("操作失败!");
    }
    return result;
  }

  @RequestMapping(params = "method=getBcgogoReceivableOrderItemDetail")
  @ResponseBody
  public Object getBcgogoReceivableOrderItemDetail(HttpServletRequest request, HttpServletResponse response,Long orderItemId) {
    IBcgogoReceivableService receivableService = ServiceManager.getService(IBcgogoReceivableService.class);
    Result result = new Result();
    try {
      BcgogoReceivableOrderItemDTO bcgogoReceivableOrderItemDTO =  receivableService.getBcgogoReceivableOrderItemDTO(orderItemId);
      result.setData(bcgogoReceivableOrderItemDTO);
    } catch (Exception e) {
      result = new Result(false);
      LOG.debug("/bcgogoReceivable.do");
      LOG.debug("method=getBcgogoReceivableOrderItemDetail");
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  @RequestMapping(params = "method=getBcgogoReceivableOrderDetail")
  @ResponseBody
  public Object getBcgogoReceivableOrderDetail(HttpServletRequest request, HttpServletResponse response,Long orderId) {
    IBcgogoReceivableService receivableService = ServiceManager.getService(IBcgogoReceivableService.class);
    IOperationLogService operationLogService = ServiceManager.getService(IOperationLogService.class);
    IUserCacheService userCacheService = ServiceManager.getService(IUserCacheService.class);
    Result result = new Result();
    try {
      BcgogoReceivableOrderDTO bcgogoReceivableOrderDTO =  receivableService.getBcgogoReceivableOrderDetail(null,orderId);
      ObjectTypes objectTypes = null;
      if(PaymentType.HARDWARE.equals(bcgogoReceivableOrderDTO.getPaymentType())){
        objectTypes = ObjectTypes.BCGOGO_HARDWARE_RECEIVABLE_ORDER;
      }else if(PaymentType.SOFTWARE.equals(bcgogoReceivableOrderDTO.getPaymentType())){
        objectTypes = ObjectTypes.BCGOGO_SOFTWARE_RECEIVABLE_ORDER;
      }else if(PaymentType.SMS_RECHARGE.equals(bcgogoReceivableOrderDTO.getPaymentType())){
        objectTypes = ObjectTypes.BCGOGO_SMS_RECHARGE_RECEIVABLE_ORDER;
      }
      if(objectTypes!=null){
        List<OperationLogDTO> operationLogDTOList = operationLogService.getOprationLogByObjectId(objectTypes, orderId);
        if(PaymentStatus.CANCELED.equals(bcgogoReceivableOrderDTO.getStatus())){
          if(bcgogoReceivableOrderDTO.getCancelUserId()!=null){
            UserDTO userDTO = ServiceManager.getService(IUserCacheService.class).getUserById(bcgogoReceivableOrderDTO.getCancelUserId());
            if(userDTO!=null){
              String userName = userDTO.getName();
              if(userDTO.getShopId().equals(bcgogoReceivableOrderDTO.getShopId())){
                userName = "买家";
              }
              OperationLogDTO operationLogDTO = new OperationLogDTO();
              operationLogDTO.setContent(userName+"取消交易,取消理由："+ StringUtils.defaultIfEmpty(bcgogoReceivableOrderDTO.getCancelReason(),"无"));
              operationLogDTO.setCreationDate(bcgogoReceivableOrderDTO.getCancelTime());
              operationLogDTOList.add(operationLogDTO);
            }
          }
        }
        if(PaymentType.SOFTWARE.equals(bcgogoReceivableOrderDTO.getPaymentType())){//取出议价记录
          List<ShopBargainRecordDTO> shopBargainRecordDTOList = ServiceManager.getService(IShopBargainService.class).getShopBargainRecordsByShopId(bcgogoReceivableOrderDTO.getShopId());
          if(CollectionUtils.isNotEmpty(shopBargainRecordDTOList)){
            Set<Long> userIds = new HashSet<Long>();
            for (ShopBargainRecordDTO shopBargainRecordDTO : shopBargainRecordDTOList) {
              userIds.add(shopBargainRecordDTO.getAuditorId());
              userIds.add(shopBargainRecordDTO.getApplicantId());
            }
            UserDTO userDTO = null;
            Map<Long, UserDTO> userDTOMap = userCacheService.getUserMap(userIds);
            for(ShopBargainRecordDTO shopBargainRecordDTO : shopBargainRecordDTOList){
              if(shopBargainRecordDTO.getApplicantId()!=null){
                OperationLogDTO operationLogDTO = new OperationLogDTO();
                userDTO = userDTOMap.get(shopBargainRecordDTO.getApplicantId());
                operationLogDTO.setContent("申请议价，申请人："+(userDTO==null?"":userDTO.getName())+";申请价格："+shopBargainRecordDTO.getApplicationPrice()+"；申请理由："+shopBargainRecordDTO.getApplicationReason());
                operationLogDTO.setCreationDate(shopBargainRecordDTO.getApplicationTime());
                operationLogDTOList.add(operationLogDTO);
              }
              if(shopBargainRecordDTO.getAuditorId()!=null){
                OperationLogDTO operationLogDTO = new OperationLogDTO();
                userDTO = userDTOMap.get(shopBargainRecordDTO.getAuditorId());
                operationLogDTO.setContent("审核议价，审核人："+(userDTO==null?"":userDTO.getName())+";审核结果："+shopBargainRecordDTO.getBargainStatus().getValue()+"；理由："+shopBargainRecordDTO.getAuditReason());
                operationLogDTO.setCreationDate(shopBargainRecordDTO.getAuditTime());
                operationLogDTOList.add(operationLogDTO);
              }
            }
          }
        }
        Collections.sort(operationLogDTOList, new Comparator<OperationLogDTO>() {
          public int compare(OperationLogDTO arg0, OperationLogDTO arg1) {
            return arg0.getCreationDate().compareTo(arg1.getCreationDate());
          }
        });
        bcgogoReceivableOrderDTO.setOperationLogDTOList(operationLogDTOList);
      }
      result.setData(bcgogoReceivableOrderDTO);
    } catch (Exception e) {
      result = new Result(false);
      LOG.debug("/bcgogoReceivable.do");
      LOG.debug("method=getBcgogoReceivableOrderDetail");
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  @RequestMapping(params = "method=getAllBcgogoProduct")
  @ResponseBody
  public Object getAllBcgogoProduct(HttpServletRequest request, HttpServletResponse response,Boolean isSimple) {
    IBcgogoProductService bcgogoProductService = ServiceManager.getService(IBcgogoProductService.class);
    Result result = new Result();
    try {
      result.setData(bcgogoProductService.getBcgogoProductDTOByPaymentType(PaymentType.HARDWARE,isSimple));
    } catch (Exception e) {
      result = new Result(false);
      LOG.debug("/bcgogoReceivable.do");
      LOG.debug("method=getAllBcgogoProduct");
      LOG.error(e.getMessage(), e);
    }
    return result;
  }
  @RequestMapping(params = "method=cancelBcgogoReceivableOrder")
  @ResponseBody
  public Object cancelBcgogoReceivableOrder(HttpServletRequest request, HttpServletResponse response,Long bcgogoReceivableOrderId,String cancelReason) {
    IBcgogoReceivableService receivableService = ServiceManager.getService(IBcgogoReceivableService.class);
    Result result = new Result();
    try {
      receivableService.cancelBcgogoReceivableOrder(ShopConstant.BC_ADMIN_SHOP_ID,bcgogoReceivableOrderId,WebUtil.getUserId(request),WebUtil.getUserName(request),cancelReason);
    } catch (Exception e) {
      LOG.debug("/bcgogoReceivable.do");
      LOG.debug("method=cancelBcgogoReceivableOrder");
      LOG.error(e.getMessage(), e);
      result.setSuccess(false);
      result.setMsg("操作失败!");
    }
    return result;
  }
  @RequestMapping(params = "method=shipBcgogoReceivableOrder")
  @ResponseBody
  public Object shipBcgogoReceivableOrder(HttpServletRequest request,Long bcgogoReceivableOrderId) {
    IBcgogoReceivableService receivableService = ServiceManager.getService(IBcgogoReceivableService.class);
    Result result = new Result();
    try {
      receivableService.shipBcgogoReceivableOrder(WebUtil.getUserName(request),WebUtil.getUserId(request),bcgogoReceivableOrderId);
    } catch (Exception e) {
      LOG.debug("/bcgogoReceivable.do");
      LOG.debug("method=shipBcgogoReceivableOrder");
      LOG.error(e.getMessage(), e);
      result.setSuccess(false);
      result.setMsg("操作失败!");
    }
    return result;
  }
  @RequestMapping(params = "method=searchBcgogoReceivableResult")
   @ResponseBody
   public Object searchBcgogoPaymentResult(HttpServletRequest request, HttpServletResponse response, BcgogoReceivableSearchCondition condition) {
    IBcgogoReceivableService receivableService = ServiceManager.getService(IBcgogoReceivableService.class);
    Result result = null;
    try {
      condition.setSearchByPendingReviewAndPaid();
      result = receivableService.searchBcgogoReceivableResult(condition, true);
    } catch (Exception e) {
      result = new Result(false);
      LOG.debug("/bcgogoReceivable.do");
      LOG.debug("method=searchBcgogoReceivableResult");
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  @RequestMapping(params = "method=statBcgogoReceivableOrderByStatusResult")
  @ResponseBody
  public Object statBcgogoReceivableOrderByStatusResult(HttpServletRequest request, HttpServletResponse response, BcgogoReceivableSearchCondition condition) {
    IBcgogoReceivableService receivableService = ServiceManager.getService(IBcgogoReceivableService.class);
    Result result = null;
    try {
      if(ArrayUtils.isEmpty(condition.getPaymentTypes())) throw  new Exception("PaymentTypes is null!");
      result = receivableService.statBcgogoReceivableOrderByStatusResult(condition);
    } catch (Exception e) {
      result = new Result(false);
      LOG.debug("/bcgogoReceivable.do");
      LOG.debug("method=statBcgogoReceivableOrderByStatusResult");
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  @RequestMapping(params = "method=statBcgogoReceivableOrderRecordByStatusResult")
  @ResponseBody
  public Object statBcgogoReceivableOrderRecordByStatusResult(HttpServletRequest request, HttpServletResponse response, BcgogoReceivableSearchCondition condition) {
    IBcgogoReceivableService receivableService = ServiceManager.getService(IBcgogoReceivableService.class);
    Result result = null;
    try {
      condition.setSearchByPendingReviewAndPaid();
      result = receivableService.statBcgogoReceivableOrderRecordByStatusResult(condition);
    } catch (Exception e) {
      result = new Result(false);
      LOG.debug("/bcgogoReceivable.do");
      LOG.debug("method=statBcgogoReceivableOrderByStatusResult");
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  @RequestMapping(params = "method=searchBcgogoReceivableOrderResult")
  @ResponseBody
  public Object searchBcgogoReceivableOrderResult(HttpServletRequest request, HttpServletResponse response, BcgogoReceivableSearchCondition condition) {
    IBcgogoReceivableService receivableService = ServiceManager.getService(IBcgogoReceivableService.class);
    Result result = null;
    try {
      if(ArrayUtils.isEmpty(condition.getPaymentTypes())) throw  new Exception("PaymentTypes is null!");
      result = receivableService.searchBcgogoReceivableOrderResult(condition);
    } catch (Exception e) {
      result = new Result(false);
      LOG.debug("/bcgogoReceivable.do");
      LOG.debug("method=searchBcgogoReceivableOrderResult");
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  @RequestMapping(params = "method=getInstalmentPlanAlgorithms")
  @ResponseBody
  public Object getInstalmentPlanAlgorithms(HttpServletRequest request, HttpServletResponse response) {
    IBcgogoReceivableService receivableService = ServiceManager.getService(IBcgogoReceivableService.class);
    Result result = null;
    try {
      result = receivableService.getInstalmentPlanAlgorithms();
    } catch (Exception e) {
      result = new Result(false);
      LOG.debug("/bcgogoReceivable.do");
      LOG.debug("method=getInstalmentPlanAlgorithms");
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  @RequestMapping(params = "method=getInstalmentPlanDetails")
  @ResponseBody
  public Object getInstalmentPlanDetails(HttpServletRequest request, HttpServletResponse response, Long instalmentPlanId) {
    IBcgogoReceivableService receivableService = ServiceManager.getService(IBcgogoReceivableService.class);
    Result result = null;
    try {
      result = receivableService.getInstalmentPlanDetails(instalmentPlanId);
    } catch (Exception e) {
      result = new Result(false);
      LOG.debug("/bcgogoReceivable.do");
      LOG.debug("method=getInstalmentPlanDetails");
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  /**
   * 创建硬件应付款
   *
   * @param request  HttpServletRequest
   * @param response HttpServletResponse
   * @param dto      BcgogoHardwareReceivableDetailDTO
   */
  @RequestMapping(params = "method=createBcgogoHardwareReceivableDetail")
  @ResponseBody
  public Object createBcgogoHardwareReceivableDetail(HttpServletRequest request, HttpServletResponse response, BcgogoReceivableOrderDTO dto) {
    IBcgogoReceivableService receivableService = ServiceManager.getService(IBcgogoReceivableService.class);
    Result result = new Result(true);
    try {
      Long bcgogoUserId = WebUtil.getUserId(request);
      dto.setBuyChannels(BuyChannels.BACKGROUND_ENTRY);
      receivableService.saveBcgogoReceivableOrderDTO(bcgogoUserId,dto);

      IOperationLogService operationLogService = ServiceManager.getService(IOperationLogService.class);
      OperationLogDTO operationLogDTO=new OperationLogDTO(ShopConstant.BC_ADMIN_SHOP_ID, bcgogoUserId, dto.getId(), ObjectTypes.BCGOGO_HARDWARE_RECEIVABLE_ORDER, OperationTypes.CREATE);
      operationLogDTO.setContent(WebUtil.getUserName(request)+"录入客户订单");
      operationLogService.saveOperationLog(operationLogDTO);

    } catch (Exception e) {
      LOG.debug("/bcgogoReceivable.do");
      LOG.debug("method=createBcgogoHardwareReceivableDetail");
      LOG.error(e.getMessage(), e);
      result.setSuccess(false);
      result.setMsg("操作失败!");
    }
    return result;
  }

  /**
   * 硬件付款（目前只支持全额付款）
   *
   * @param request  HttpServletRequest
   * @param response HttpServletResponse
   * @param dto      BcgogoReceivableDTO
   */
  @RequestMapping(params = "method=hardwareOfflineReceivable")
  @ResponseBody
  public Object hardwareOfflinePay(HttpServletRequest request, HttpServletResponse response, BcgogoReceivableDTO dto) {
    IBcgogoReceivableService receivableService = ServiceManager.getService(IBcgogoReceivableService.class);
    Result result = new Result(true);
    try {
      dto.setSubmitterId(WebUtil.getUserId(request));
      dto.setShopId(WebUtil.getShopId(request));
      receivableService.hardwareReceivable(dto);
//      IOperationLogService operationLogService = ServiceManager.getService(IOperationLogService.class);
//      OperationLogDTO operationLogDTO = new OperationLogDTO(ShopConstant.BC_ADMIN_SHOP_ID, WebUtil.getUserId(request), dto.getBcgogoReceivableOrderId(), ObjectTypes.BCGOGO_HARDWARE_RECEIVABLE_ORDER, OperationTypes.OFFLINE_PAY);
//      operationLogDTO.setContent(WebUtil.getUserName(request)+"线下支付￥"+dto.getPaidAmount());
//      operationLogService.saveOperationLog(operationLogDTO);
    } catch (Exception e) {
      LOG.debug("/bcgogoReceivable.do");
      LOG.debug("method=hardwareOfflineReceivable");
      LOG.error(e.getMessage(), e);
      result.setSuccess(false);
      result.setMsg("操作失败!");
    }
    return result;
  }

  /**
   * 软件付款（全额/分期）
   *
   * @param request  HttpServletRequest
   * @param response HttpServletResponse
   * @param dto      BcgogoReceivableDTO
   */
  @RequestMapping(params = "method=softwareOfflineReceivable")
  @ResponseBody
  public Object softwareOfflinePay(HttpServletRequest request, HttpServletResponse response, BcgogoReceivableDTO dto) {
    IBcgogoReceivableService receivableService = ServiceManager.getService(IBcgogoReceivableService.class);
    Result result = new Result(true);
    try {
      dto.setSubmitterId(WebUtil.getUserId(request));
      dto.setPaymentMethod(PaymentMethod.DOOR_CHARGE);
      receivableService.softwareReceivable(dto);
    } catch (Exception e) {
      LOG.debug("/payment.do");
      LOG.debug("method=softwareOfflineReceivable");
      LOG.error(e.getMessage(), e);
      result.setSuccess(false);
      result.setMsg("操作失败!");
    }
    return result;
  }

  /**
   * 继续分期付款
   *
   * @param request  HttpServletRequest
   * @param response HttpServletResponse
   * @param dto      BcgogoReceivableDTO
   */
  @RequestMapping(params = "method=instalmentReceivable")
  @ResponseBody
  public Object instalmentReceivable(HttpServletRequest request, HttpServletResponse response, BcgogoReceivableDTO dto) {
    IBcgogoReceivableService receivableService = ServiceManager.getService(IBcgogoReceivableService.class);
    Result result = new Result(true);
    try {
      dto.setSubmitterId(WebUtil.getUserId(request));
      dto.setShopId(WebUtil.getShopId(request));
      receivableService.instalmentReceivable(dto);
    } catch (Exception e) {
      LOG.debug("/bcgogoReceivable.do");
      LOG.debug("method=instalmentReceivable");
      LOG.error(e.getMessage(), e);
      result.setSuccess(false);
      result.setMsg("操作失败!");
    }
    return result;
  }

  //审核
  @RequestMapping(params = "method=auditReceivable")
  @ResponseBody
  public Object auditReceivable(HttpServletRequest request, HttpServletResponse response, Long bcgogoReceivableOrderRecordRelationId) {
    IBcgogoReceivableService receivableService = ServiceManager.getService(IBcgogoReceivableService.class);
    Result result = new Result(true);
    try {
      receivableService.auditReceivable(WebUtil.getUserId(request), WebUtil.getUserName(request),bcgogoReceivableOrderRecordRelationId);
    } catch (Exception e) {
      LOG.debug("/bcgogoReceivable.do");
      LOG.debug("method=auditReceivable");
      LOG.error(e.getMessage(), e);
      result.setSuccess(false);
      result.setMsg("操作失败!");
    }
    return result;
  }

  //增加待支付
  @RequestMapping(params = "method=addSoftwareReceivable")
  @ResponseBody
  public Object addSoftwareReceivable(HttpServletRequest request, HttpServletResponse response, UnconstrainedSoftwareReceivableDTO receivableDTO) {
    IBcgogoReceivableService receivableService = ServiceManager.getService(IBcgogoReceivableService.class);
    Result result = new Result(true);
    try {
      if (receivableDTO.getReceivingAmount() == null) throw new BcgogoException("receiving amount is null.");
      if (receivableDTO.getShopId() == null) throw new BcgogoException("shopId is null.");
      if (receivableDTO.getPayeeId() == null) throw new BcgogoException("payee is null.");
      receivableDTO.setOperateTime(System.currentTimeMillis());
      receivableDTO.setOperatorId(WebUtil.getUserId(request));
      result = receivableService.createSoftwareReceivable(receivableDTO);
    } catch (Exception e) {
      LOG.debug("/bcgogoReceivable.do");
      LOG.debug("method=addSoftwareReceivable");
      LOG.error(e.getMessage(), e);
      result.setSuccess(false);
      result.setMsg("操作失败!");
    }
    return result;
  }

  //增加已支付
  @RequestMapping(params = "method=addSoftwareReceived")
  @ResponseBody
  public Object addSoftwareReceived(HttpServletRequest request, HttpServletResponse response, UnconstrainedSoftwareReceivableDTO receivableDTO) {
    IBcgogoReceivableService receivableService = ServiceManager.getService(IBcgogoReceivableService.class);
    Result result = new Result(true);
    try {
      if (receivableDTO.getReceivedAmount() == null) throw new BcgogoException("received amount is null.");
      if (receivableDTO.getReceivedTime() == null) throw new BcgogoException("received time is null.");
      if (receivableDTO.getShopId() == null) throw new BcgogoException("shopId is null.");
      if (receivableDTO.getPayeeId() == null) throw new BcgogoException("payee is null.");
      receivableDTO.setSubmitTime(System.currentTimeMillis());
      receivableDTO.setSubmitterId(WebUtil.getUserId(request));
      result = receivableService.addSoftwareReceived(receivableDTO);
    } catch (Exception e) {
      LOG.debug("/bcgogoReceivable.do");
      LOG.debug("method=addSoftwareReceived");
      LOG.error(e.getMessage(), e);
      result.setSuccess(false);
      result.setMsg("操作失败!");
    }
    return result;
  }
}
