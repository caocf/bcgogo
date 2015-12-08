package com.bcgogo.autoaccessoryonline;

import com.bcgogo.common.Pager;
import com.bcgogo.common.Result;
import com.bcgogo.common.StringUtil;
import com.bcgogo.common.WebUtil;
import com.bcgogo.config.cache.BcgogoConcurrentController;
import com.bcgogo.config.dto.OperationLogDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IOperationLogService;
import com.bcgogo.config.service.image.IImageService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.config.util.ImageUtils;
import com.bcgogo.constant.ChinaPayConstants;
import com.bcgogo.enums.ConcurrentScene;
import com.bcgogo.enums.ObjectTypes;
import com.bcgogo.enums.OperationTypes;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.config.ImageScene;
import com.bcgogo.enums.payment.ChinaPayParamStatus;
import com.bcgogo.enums.payment.ChinaPayScene;
import com.bcgogo.enums.txn.finance.*;
import com.bcgogo.notification.service.ISmsService;
import com.bcgogo.payment.chinapay.ChinaPay;
import com.bcgogo.payment.dto.ChinapayDTO;
import com.bcgogo.payment.model.ChinaPayParamLog;
import com.bcgogo.payment.service.IChinapayService;
import com.bcgogo.payment.service.IPaymentService;
import com.bcgogo.product.BcgogoProductDTO;
import com.bcgogo.product.BcgogoProductPropertyDTO;
import com.bcgogo.product.service.IBcgogoProductService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.PrintTemplateDTO;
import com.bcgogo.txn.dto.PurchaseOrderDTO;
import com.bcgogo.txn.dto.finance.*;
import com.bcgogo.txn.service.IPrintService;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.txn.service.finance.IBcgogoReceivableService;
import com.bcgogo.user.dto.UserDTO;
import com.bcgogo.user.model.permission.ShopVersion;
import com.bcgogo.user.service.permission.IUserCacheService;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.NumberUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

/**
 * 在线支付页面 web controller
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-3-26
 * Time: 下午5:58
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/bcgogoReceivable.do")
public class BcgogoReceivableController {

  private static final Logger LOG = LoggerFactory.getLogger(BcgogoReceivableController.class);

  /**
   * @param request
   * @return
   */
  @RequestMapping(params = "method=confirmBcgogoReceivableOrder")
  public String confirmBcgogoReceivableOrder(ModelMap modelMap, HttpServletRequest request,BcgogoReceivableOrderDTO bcgogoReceivableOrderDTO) {
    Long shopId = null;
    try {
      shopId = WebUtil.getShopId(request);
      modelMap.addAttribute("bcgogoProductDTOList", getSalesBcgogoProductDTOs(WebUtil.getShopVersionId(request)));

      IConfigService configService = ServiceManager.getService(IConfigService.class);
      IBcgogoProductService bcgogoProductService = ServiceManager.getService(IBcgogoProductService.class);

      ShopDTO shopDTO = configService.getShopById(shopId);
      bcgogoReceivableOrderDTO.setProvince(shopDTO.getProvince());
      bcgogoReceivableOrderDTO.setCity(shopDTO.getCity());
      bcgogoReceivableOrderDTO.setRegion(shopDTO.getRegion());
      bcgogoReceivableOrderDTO.setAddress(shopDTO.getAddress());
      bcgogoReceivableOrderDTO.setContact(shopDTO.getOwner());
      bcgogoReceivableOrderDTO.setMobile(shopDTO.getMobile());
      bcgogoReceivableOrderDTO.setAddressDetail(shopDTO.getAreaName()+shopDTO.getAddress());
      if(CollectionUtils.isNotEmpty(bcgogoReceivableOrderDTO.getBcgogoReceivableOrderItemDTOList())){
        Iterator<BcgogoReceivableOrderItemDTO> iterator = bcgogoReceivableOrderDTO.getBcgogoReceivableOrderItemDTOList().iterator();
        BcgogoReceivableOrderItemDTO itemDTO = null;
        Double orderTotalAmount = 0d;
        while (iterator.hasNext()){
          itemDTO = iterator.next();
          if(itemDTO.getProductId()==null || itemDTO.getProductPropertyId() ==null || itemDTO.getAmount()<=0){
            iterator.remove();
          }else{
            BcgogoProductDTO bcgogoProductDTO = null;
            BcgogoProductPropertyDTO bcgogoProductPropertyDTO = bcgogoProductService.getBcgogoProductPropertyDTOById(itemDTO.getProductPropertyId());
            if(!bcgogoProductPropertyDTO.getProductId().equals(itemDTO.getProductId())){
              bcgogoProductDTO = bcgogoProductService.getBcgogoProductDTOById(bcgogoProductPropertyDTO.getProductId());
            }else{
              bcgogoProductDTO = bcgogoProductService.getBcgogoProductDTOById(itemDTO.getProductId());
            }
            itemDTO.setBcgogoProductDTO(bcgogoProductDTO);
            itemDTO.setBcgogoProductPropertyDTO(bcgogoProductPropertyDTO);
            if(StringUtils.isNotBlank(itemDTO.getImagePath())){
              itemDTO.setImageUrl(ImageUtils.generateUpYunImagePath(itemDTO.getImagePath(),ImageScene.PRODUCT_LIST_IMAGE_SMALL.getImageVersion()));
            }else{
              itemDTO.setImageUrl(ImageUtils.generateNotFindImageUrl(ImageScene.PRODUCT_LIST_IMAGE_SMALL.getImageVersion()));
            }
            itemDTO.setAmount(NumberUtil.doubleVal(itemDTO.getAmount()));
            itemDTO.setTotal(NumberUtil.round(itemDTO.getAmount()*itemDTO.getPrice(),NumberUtil.MONEY_PRECISION));
            orderTotalAmount+=itemDTO.getTotal();
          }
        }
        bcgogoReceivableOrderDTO.setTotalAmount(orderTotalAmount);
      }
      modelMap.addAttribute("bcgogoReceivableOrderDTO", bcgogoReceivableOrderDTO);
      modelMap.addAttribute("bcgogoPhone", ConfigUtils.getBcgogoPhone());
      modelMap.addAttribute("bcgogoQQ", ConfigUtils.getBcgogoQQ());

    } catch (Exception e) {
      LOG.error("bcgogoReceivable.do?method=confirmBcgogoReceivableOrder,shopId:" + shopId);
      LOG.error(e.getMessage(), e);
    }
    return "/autoaccessoryonline/payOnline/bcgogoConfirmHardwareReceivableOrder";
  }

  /**
   * @param request
   * @return
   */
  @RequestMapping(params = "method=saveBcgogoReceivableOrder")
  public String saveBcgogoReceivableOrder(ModelMap modelMap, HttpServletRequest request,BcgogoReceivableOrderDTO bcgogoReceivableOrderDTO) {
    IBcgogoReceivableService receivableService = ServiceManager.getService(IBcgogoReceivableService.class);
    Long shopId = null;
    Long userId = null;
    try {
      shopId = WebUtil.getShopId(request);
      userId = WebUtil.getUserId(request);
      bcgogoReceivableOrderDTO.setShopId(shopId);
      bcgogoReceivableOrderDTO.setUserId(userId);
      bcgogoReceivableOrderDTO.setBuyChannels(BuyChannels.ONLINE_ORDERS);
      receivableService.saveBcgogoReceivableOrderDTO(null,bcgogoReceivableOrderDTO);
      //发短信通知客服
      if(PaymentType.HARDWARE.equals(bcgogoReceivableOrderDTO.getPaymentType())){
        ShopDTO shopDTO = ServiceManager.getService(IConfigService.class).getShopById(bcgogoReceivableOrderDTO.getShopId());
        ServiceManager.getService(ISmsService.class).sendBcgogoOrderSms(shopDTO,bcgogoReceivableOrderDTO);
      }

      IOperationLogService operationLogService = ServiceManager.getService(IOperationLogService.class);
      OperationLogDTO operationLogDTO = new OperationLogDTO(shopId, userId,bcgogoReceivableOrderDTO.getId(),ObjectTypes.BCGOGO_HARDWARE_RECEIVABLE_ORDER, OperationTypes.CREATE);
      operationLogDTO.setContent(WebUtil.getUserName(request)+"创建订单");
      operationLogService.saveOperationLog(operationLogDTO);
      modelMap.addAttribute("bcgogoReceivableOrderId",bcgogoReceivableOrderDTO.getId());
    } catch (Exception e) {
      LOG.error("bcgogoReceivable.do?method=saveBcgogoReceivableOrder,shopId:" + shopId);
      LOG.error(e.getMessage(), e);
    }
    return "redirect:bcgogoReceivable.do?method=bcgogoReceivableOrderDetail";
  }


  /**
   * @param request
   * @param bcgogoReceivableOrderId
   * @return
   */
  @RequestMapping(params = "method=bcgogoReceivableOrderDetail")
  public String bcgogoReceivableOrderDetail(ModelMap modelMap, HttpServletRequest request,Long bcgogoReceivableOrderId) {
    Long shopId = null;
    IBcgogoReceivableService receivableService = ServiceManager.getService(IBcgogoReceivableService.class);
    try {
      shopId = WebUtil.getShopId(request);
      BcgogoReceivableOrderDTO bcgogoReceivableOrderDTO = receivableService.getBcgogoReceivableOrderDetail(shopId,bcgogoReceivableOrderId);
      if(PaymentStatus.CANCELED.equals(bcgogoReceivableOrderDTO.getStatus())){
        if(bcgogoReceivableOrderDTO.getCancelUserId()!=null){
          UserDTO userDTO = ServiceManager.getService(IUserCacheService.class).getUserById(bcgogoReceivableOrderDTO.getCancelUserId());
          if(userDTO.getShopId().equals(bcgogoReceivableOrderDTO.getShopId())){
            bcgogoReceivableOrderDTO.setCancelOptInfo("买家取消订单");
          }else{
            bcgogoReceivableOrderDTO.setCancelOptInfo("卖家取消订单");
          }
        }
      }
      modelMap.addAttribute("bcgogoReceivableOrderDTO", bcgogoReceivableOrderDTO);
      if(PaymentType.HARDWARE.equals(bcgogoReceivableOrderDTO.getPaymentType())){
        //
        modelMap.addAttribute("bcgogoPhone", ConfigUtils.getBcgogoPhone());
        modelMap.addAttribute("bcgogoQQ", ConfigUtils.getBcgogoQQ());

        modelMap.addAttribute("bcgogoProductDTOList", getSalesBcgogoProductDTOs(WebUtil.getShopVersionId(request)));
        return "/autoaccessoryonline/payOnline/bcgogoHardwareReceivableOrderDetail";
      }else{
        modelMap.addAttribute("algorithmList",receivableService.getInstalmentPlanAlgorithms().getData());
        return "/autoaccessoryonline/payOnline/bcgogoSoftwareReceivableOrderDetail";
      }

    } catch (Exception e) {
      LOG.error("bcgogoReceivable.do?method=bcgogoReceivableOrderDetail,shopId:" + shopId);
      LOG.error(e.getMessage(), e);
    }
    return "/";
  }

  private List<BcgogoProductDTO> getSalesBcgogoProductDTOs(Long shopVersionId) {
    IBcgogoProductService bcgogoProductService = ServiceManager.getService(IBcgogoProductService.class);
    IImageService imageService = ServiceManager.getService(IImageService.class);
    List<BcgogoProductDTO> bcgogoProductDTOList = bcgogoProductService.getBcgogoProductDTOByPaymentType(PaymentType.HARDWARE,false);
    Iterator<BcgogoProductDTO> iterator = bcgogoProductDTOList.iterator();
    while (iterator.hasNext()){
      if(iterator.next().getShowToShopVersions().indexOf(shopVersionId.toString())<=-1){
        iterator.remove();
      }
    }
    List<ImageScene> imageSceneList = new ArrayList<ImageScene>();
    imageSceneList.add(ImageScene.BCGOGO_PRODUCT_LIST_IMAGE);
    imageService.addImageToBcgogoProductDTO(imageSceneList, bcgogoProductDTOList.toArray(new BcgogoProductDTO[bcgogoProductDTOList.size()]));
    return bcgogoProductDTOList;
  }


  @RequestMapping(params = "method=cancelBcgogoReceivableOrder")
  @ResponseBody
  public Object cancelBcgogoReceivableOrder(HttpServletRequest request,Long bcgogoReceivableOrderId,String cancelReason) {
    IBcgogoReceivableService receivableService = ServiceManager.getService(IBcgogoReceivableService.class);
    Result result = new Result();
    try {
      receivableService.cancelBcgogoReceivableOrder(WebUtil.getShopId(request),bcgogoReceivableOrderId,WebUtil.getUserId(request),WebUtil.getUserName(request),cancelReason);
    } catch (Exception e) {
      LOG.debug("/bcgogoReceivable.do");
      LOG.debug("method=cancelBcgogoReceivableOrder");
      LOG.error(e.getMessage(), e);
      result.setSuccess(false);
      result.setMsg("操作失败!");
    }
    return result;
  }
  /**
   * 跳转到在线支付页面
   *
   * @param request
   * @return
   */
  @RequestMapping(params = "method=bcgogoReceivableOrderList")
  public String bcgogoReceivableOrderList(ModelMap modelMap, HttpServletRequest request) {
    Long shopId = null;
    IBcgogoReceivableService receivableService = ServiceManager.getService(IBcgogoReceivableService.class);
    try {
      shopId = WebUtil.getShopId(request);
      modelMap.addAttribute("bcgogoProductDTOList", getSalesBcgogoProductDTOs(WebUtil.getShopVersionId(request)));

      BcgogoReceivableSearchCondition condition = new BcgogoReceivableSearchCondition();
      condition.setShopIds(Arrays.asList(new Long[]{WebUtil.getShopId(request)}));
      Map<String,Integer> statMap = receivableService.statBcgogoReceivableOrderByStatus(condition);
      modelMap.addAttribute("statMap",statMap);

      modelMap.addAttribute("algorithmList",receivableService.getInstalmentPlanAlgorithms().getData());

    } catch (Exception e) {
      LOG.error("bcgogoReceivable.do?method=bcgogoReceivableOrderList,shopId:" + shopId);
      LOG.error(e.getMessage(), e);
    }
    return "/autoaccessoryonline/payOnline/bcgogoReceivableOrderList";
  }
  /**
   * @param modelMap
   * @param request
   * @param condition
   * @return
   */
  @RequestMapping(params = "method=searchBcgogoReceivableOrderList")
  @ResponseBody
  public Object searchBcgogoReceivableOrderList(ModelMap modelMap, HttpServletRequest request,BcgogoReceivableSearchCondition condition) {
    IBcgogoReceivableService receivableService = ServiceManager.getService(IBcgogoReceivableService.class);
    try {
      condition.setShopIds(Arrays.asList(new Long[]{WebUtil.getShopId(request)}));
      List<BcgogoReceivableOrderDTO> bcgogoReceivableOrderDTOList =  receivableService.searchBcgogoReceivableOrderDTO(WebUtil.getShopVersionId(request),condition);

      List<Object> result = new ArrayList<Object>();
      Pager pager = new Pager(receivableService.countBcgogoReceivableOrderDTO(condition), condition.getStartPageNo(), condition.getMaxRows());
      result.add(bcgogoReceivableOrderDTOList);
      result.add(pager);
      return result;
    } catch (Exception e) {
      LOG.debug("/bcgogoReceivable.do");
      LOG.debug("method=searchBcgogoReceivableOrderList");
      LOG.error(e.getMessage(), e);
    }
    return null;
  }

  /**
   * 获取支付详情
   *
   * @param request
   * @return
   */
  @RequestMapping(params = "method=getInstalmentPlanAlgorithms")
  @ResponseBody
  public Object getInstalmentPlanAlgorithms(HttpServletRequest request) {
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

  /**
   * 根据分期id获得已支付 和未支付分期
   *
   * @param request
   * @param instalmentPlanId
   * @return
   */
  @RequestMapping(params = "method=getInstalmentPlanDetails")
  @ResponseBody
  public Object getInstalmentPlanDetails(HttpServletRequest request, Long instalmentPlanId) {
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
   * 充值第二步：充值
   * 硬件付款（目前只支持全额付款）
   *
   * @param request  HttpServletRequest
   * @param dto      BcgogoReceivableDTO
   */
  @RequestMapping(params = "method=hardwareOnlineReceivable")
  public String hardwareOnlineReceivable(ModelMap modelMap, HttpServletRequest request, BcgogoReceivableDTO dto) {
    Boolean payCheckResult = true;
    try {
      Long shopId = WebUtil.getShopId(request);
      IChinapayService chinapayService = ServiceManager.getService(IChinapayService.class);
      IPaymentService paymentService = ServiceManager.getService(IPaymentService.class);
      IBcgogoReceivableService receivableService = ServiceManager.getService(IBcgogoReceivableService.class);
      if (dto == null || dto.getBcgogoReceivableOrderRecordRelationId() == null|| dto.getBcgogoReceivableOrderId() == null  || NumberUtil.doubleVal(dto.getPaidAmount()) <= 0) {
        payCheckResult = false;
      }
      BcgogoReceivableOrderDTO bcgogoReceivableOrderDTO = receivableService.getSimpleBcgogoReceivableOrderDTO(dto.getBcgogoReceivableOrderId());
      payCheckResult = receivableService.verifyAndGetBcgogoReceivableOrderDTO(dto, shopId, bcgogoReceivableOrderDTO);

      dto.setPaymentMethod(PaymentMethod.ONLINE_PAYMENT);
      dto.setReceivableMethod(ReceivableMethod.FULL);

      dto.setSubmitterId(WebUtil.getUserId(request));
      dto.setShopId(WebUtil.getShopId(request));
      dto.setPaymentTime(System.currentTimeMillis());
      dto.setPaymentTypeStr("硬件费用");
      dto.setPaymentTimeStr(DateUtil.dateLongToStr(System.currentTimeMillis(), DateUtil.DATE_STRING_FORMAT_DEFAULT));
      dto.setTotalAmount(bcgogoReceivableOrderDTO.getTotalAmount());

      modelMap.addAttribute("bcgogoReceivableDTO", dto);
      ChinaPayParamLog log = new ChinaPayParamLog();
      log.fromHardwareBcgogoReceivableDTO(dto);
      log.setChinaPayScene(ChinaPayScene.HARDWARE_PAY);
      log.setChinaPayParamStatus(ChinaPayParamStatus.EFFECTIVE);
      Long referenceId = paymentService.createChinaPayParamLog(log);
      //保存银联付款提交记录，付款单
      ChinapayDTO chinapayDTO = chinapayService.pay(referenceId, NumberUtil.yuanToFen(dto.getPaidAmount()), shopId, "硬件费用", ChinaPayConstants.HARDWARE_BG_RET_URL, ChinaPayConstants.HARDWARE_PAGE_RET_URL);
      if (chinapayDTO == null) {
        payCheckResult = false;
      }
      paymentService.updateChinaPayParamLog(referenceId, Long.valueOf(chinapayDTO.getOrdId()));
      request.setAttribute("chinapayForm", ChinaPay.commitFormOfDefray(chinapayDTO));
    } catch (Exception e) {
      LOG.debug("/bcgogoReceivable.do");
      LOG.debug("method=hardwareOnlineReceivable");
      LOG.error("硬件付款:{}", e.getMessage(), e);
      payCheckResult = false;
    }
    if(!payCheckResult){
      modelMap.addAttribute("receivableErrorInfo", "支付失败，数据错误!");
      modelMap.remove("chinapayForm");
    }
    return "/autoaccessoryonline/payOnline/bcgogoreceiving";
  }

  /**
   * 充值第二步：充值
   * 软件付款（全额/分期）
   *
   * @param request  HttpServletRequest
   * @param dto      BcgogoReceivableDTO
   */
  @RequestMapping(params = "method=softwareOnlineReceivable")
  public String softwareOnlineReceivable(ModelMap modelMap, HttpServletRequest request, BcgogoReceivableDTO dto) {
    Long shopId = WebUtil.getShopId(request);
    Boolean payCheckResult = true;
    try {
      IChinapayService chinapayService = ServiceManager.getService(IChinapayService.class);
      IPaymentService paymentService = ServiceManager.getService(IPaymentService.class);
      IBcgogoReceivableService receivableService = ServiceManager.getService(IBcgogoReceivableService.class);
      if (dto == null || dto.getBcgogoReceivableOrderRecordRelationId() == null || dto.getBcgogoReceivableOrderId() == null ||dto.getReceivableMethod() == null || NumberUtil.doubleVal(dto.getPaidAmount()) <= 0) {
        payCheckResult = false;
      }
      BcgogoReceivableOrderDTO bcgogoReceivableOrderDTO = receivableService.getSimpleBcgogoReceivableOrderDTO(dto.getBcgogoReceivableOrderId());
      payCheckResult = receivableService.verifyAndGetBcgogoReceivableOrderDTO(dto, shopId, bcgogoReceivableOrderDTO);

      if (dto.getReceivableMethod() == ReceivableMethod.FULL) {
        dto.setPaymentTypeStr("软件首次付款 - 全额支付");
      } else if (dto.getReceivableMethod() == ReceivableMethod.INSTALLMENT) {
        dto.setPaymentTypeStr("软件首次 - 分期支付");
      } else if (dto.getReceivableMethod() == ReceivableMethod.UNCONSTRAINED) {
        dto.setPaymentTypeStr("软件付款 - 其他付款");
      } else {
        payCheckResult = false;
      }

      dto.setSubmitterId(WebUtil.getUserId(request));
      dto.setShopId(WebUtil.getShopId(request));
      dto.setPaymentTime(System.currentTimeMillis());
      dto.setPaymentTimeStr(DateUtil.dateLongToStr(System.currentTimeMillis(), DateUtil.DATE_STRING_FORMAT_DEFAULT));
      dto.setPaymentMethod(PaymentMethod.ONLINE_PAYMENT);
      dto.setTotalAmount(bcgogoReceivableOrderDTO.getTotalAmount());

      modelMap.addAttribute("bcgogoReceivableDTO", dto);
      ChinaPayParamLog log = new ChinaPayParamLog();
      log.fromSoftwareBcgogoReceivableDTO(dto);
      log.setChinaPayScene(ChinaPayScene.SOFTWARE_PAY);
      log.setChinaPayParamStatus(ChinaPayParamStatus.EFFECTIVE);
      Long referenceId = paymentService.createChinaPayParamLog(log);
      //保存银联付款提交记录，付款单
      ChinapayDTO chinapayDTO = chinapayService.pay(referenceId, NumberUtil.yuanToFen(dto.getPaidAmount()), shopId,dto.getPaymentTypeStr(), ChinaPayConstants.SOFTWARE_BG_RET_URL, ChinaPayConstants.SOFTWARE_PAGE_RET_URL);
      if (chinapayDTO == null) {
        payCheckResult = false;
      }
      paymentService.updateChinaPayParamLog(referenceId, Long.valueOf(chinapayDTO.getOrdId()));
      modelMap.addAttribute("chinapayForm", ChinaPay.commitFormOfDefray(chinapayDTO));
    } catch (Exception e) {
      LOG.debug("/bcgogoReceivable.do");
      LOG.debug("method=softwareOnlineReceivable");
      LOG.error("软件付款:{}", e.getMessage(), e);
      payCheckResult = false;
    }
    if(!payCheckResult){
      modelMap.addAttribute("receivableErrorInfo", "支付失败，数据错误!");
      modelMap.remove("chinapayForm");
    }
    return "/autoaccessoryonline/payOnline/bcgogoreceiving";
  }

  /**
   * 充值第二步：充值 继续分期
   * 继续分期付款
   *
   * @param request  HttpServletRequest
   * @param dto      BcgogoReceivableDTO
   */
  @RequestMapping(params = "method=instalmentOnLineReceivable")
  public String instalmentOnLineReceivable(ModelMap modelMap, HttpServletRequest request, BcgogoReceivableDTO dto) {
    Boolean payCheckResult = true;
    try {
      Long shopId = WebUtil.getShopId(request);
      IPaymentService paymentService = ServiceManager.getService(IPaymentService.class);
      IChinapayService chinapayService = ServiceManager.getService(IChinapayService.class);
      IBcgogoReceivableService receivableService = ServiceManager.getService(IBcgogoReceivableService.class);
      if (dto == null || dto.getBcgogoReceivableOrderRecordRelationId() == null || dto.getBcgogoReceivableOrderId() == null || NumberUtil.doubleVal(dto.getPaidAmount()) <= 0) {
        payCheckResult = false;
      }

      BcgogoReceivableOrderDTO bcgogoReceivableOrderDTO = receivableService.getSimpleBcgogoReceivableOrderDTO(dto.getBcgogoReceivableOrderId());
      payCheckResult = receivableService.verifyAndGetBcgogoReceivableOrderDTO(dto, shopId, bcgogoReceivableOrderDTO);

      dto.setPaymentMethod(PaymentMethod.ONLINE_PAYMENT);
      dto.setReceivableMethod(ReceivableMethod.INSTALLMENT);
      dto.setPaymentTypeStr("软件分期付款");
      dto.setSubmitterId(WebUtil.getUserId(request));
      dto.setShopId(WebUtil.getShopId(request));
      dto.setPaymentTime(System.currentTimeMillis());
      dto.setPaymentTimeStr(DateUtil.dateLongToStr(System.currentTimeMillis(), DateUtil.DATE_STRING_FORMAT_DEFAULT));
      dto.setTotalAmount(bcgogoReceivableOrderDTO.getTotalAmount());

      modelMap.addAttribute("bcgogoReceivableDTO", dto);
      ChinaPayParamLog log = new ChinaPayParamLog();
      log.fromSoftwareBcgogoReceivableDTO(dto);
      log.setChinaPayScene(ChinaPayScene.SOFTWARE_PAY);
      log.setChinaPayParamStatus(ChinaPayParamStatus.EFFECTIVE);
      Long referenceId = paymentService.createChinaPayParamLog(log);
      //保存银联付款提交记录，付款单
      ChinapayDTO chinapayDTO = chinapayService.pay(referenceId, NumberUtil.yuanToFen(dto.getPaidAmount()),
          shopId, dto.getPaymentTypeStr(), ChinaPayConstants.SOFTWARE_INSTALMENT_BG_RET_URL, ChinaPayConstants.SOFTWARE_INSTALMENT_PAGE_RET_URL);
      if (chinapayDTO == null) {
        payCheckResult = false;
      }
      paymentService.updateChinaPayParamLog(referenceId, Long.valueOf(chinapayDTO.getOrdId()));
      modelMap.addAttribute("chinapayForm", ChinaPay.commitFormOfDefray(chinapayDTO));
    } catch (Exception e) {
      LOG.debug("/bcgogoReceivable.do");
      LOG.debug("method=instalmentOnLineReceivable");
      LOG.error("软件付款:{}", e.getMessage(), e);
      payCheckResult = false;
    }
    if(!payCheckResult){
      modelMap.addAttribute("receivableErrorInfo", "支付失败，数据错误!");
      modelMap.remove("chinapayForm");
    }
    return "/autoaccessoryonline/payOnline/bcgogoreceiving";
  }

  /**
   * 充值第二步：充值
   * 合并付款(只支持  硬件付款+软件非首次付款)
   *
   * @param request  HttpServletRequest
   * @param bcgogoReceivableOrderRecordRelationIds      String[]
   * @param paidAmounts      Double[]
   */
  @RequestMapping(params = "method=combinedPaymentsOnlineReceivable")
  public String combinedPaymentsOnlineReceivable(ModelMap modelMap, HttpServletRequest request,Long[] bcgogoReceivableOrderRecordRelationIds,Double[] paidAmounts) {
    Boolean payCheckResult = true;
    try {
      Long shopId = WebUtil.getShopId(request);
      IChinapayService chinapayService = ServiceManager.getService(IChinapayService.class);
      IPaymentService paymentService = ServiceManager.getService(IPaymentService.class);
      IBcgogoReceivableService receivableService = ServiceManager.getService(IBcgogoReceivableService.class);
      if (ArrayUtils.isEmpty(bcgogoReceivableOrderRecordRelationIds) || ArrayUtils.isEmpty(paidAmounts)) {
        payCheckResult = false;
      }
      if (bcgogoReceivableOrderRecordRelationIds.length != paidAmounts.length) {
        payCheckResult = false;
      }
      Double totalPaidAmount = 0d;
      for(Double paidAmount:paidAmounts){
        totalPaidAmount+=paidAmount;
      }

      Double totalReceivableAmount = 0d,totalOrderAmount=0d;
      List<BcgogoReceivableDTO> bcgogoReceivableDTOList = receivableService.getBcgogoReceivableDTOByRelationId(bcgogoReceivableOrderRecordRelationIds);
      if(CollectionUtils.isEmpty(bcgogoReceivableDTOList)){
        payCheckResult = false;
      }else{
        for(BcgogoReceivableDTO bcgogoReceivableDTO:bcgogoReceivableDTOList){
          if(!bcgogoReceivableDTO.getShopId().equals(shopId)){
            payCheckResult = false;
          }else{
            totalReceivableAmount+=bcgogoReceivableDTO.getOrderReceivableAmount();
            totalOrderAmount+=bcgogoReceivableDTO.getOrderTotalAmount();
          }
        }
      }

      if(!NumberUtil.compareDouble(totalPaidAmount,totalReceivableAmount)){
        payCheckResult = false;
      }

      BcgogoReceivableDTO dto = new BcgogoReceivableDTO();
      dto.setPaymentMethod(PaymentMethod.ONLINE_PAYMENT);
      dto.setSubmitterId(WebUtil.getUserId(request));
      dto.setShopId(WebUtil.getShopId(request));
      Long currentTime = System.currentTimeMillis();
      dto.setPaymentTime(currentTime);
      dto.setPaymentTimeStr(DateUtil.dateLongToStr(currentTime, DateUtil.DATE_STRING_FORMAT_DEFAULT));
      dto.setPaymentTypeStr("合并支付费用");
      dto.setTotalAmount(totalOrderAmount);
      dto.setPaidAmount(totalPaidAmount);

      modelMap.addAttribute("bcgogoReceivableDTO", dto);
      ChinaPayParamLog log = new ChinaPayParamLog();
      log.setCombinedPaymentsParam(dto, StringUtils.join(bcgogoReceivableOrderRecordRelationIds, ","), StringUtils.join(paidAmounts, ","));

      log.setChinaPayScene(ChinaPayScene.COMBINED_PAY);
      log.setChinaPayParamStatus(ChinaPayParamStatus.EFFECTIVE);
      Long referenceId = paymentService.createChinaPayParamLog(log);
      //保存银联付款提交记录，付款单
      ChinapayDTO chinapayDTO = chinapayService.pay(referenceId, NumberUtil.yuanToFen(dto.getPaidAmount()), shopId, "合并支付费用", ChinaPayConstants.COMBINED_BG_RET_URL, ChinaPayConstants.COMBINED_PAGE_RET_URL);
      if (chinapayDTO == null) {
        payCheckResult = false;
      }
      paymentService.updateChinaPayParamLog(referenceId, Long.valueOf(chinapayDTO.getOrdId()));
      request.setAttribute("chinapayForm", ChinaPay.commitFormOfDefray(chinapayDTO));
    } catch (Exception e) {
      LOG.debug("/bcgogoReceivable.do");
      LOG.debug("method=combinedPaymentsOnlineReceivable");
      LOG.error("合并支付费用:{}", e.getMessage(), e);
      payCheckResult = false;
    }
    if(!payCheckResult){
      modelMap.addAttribute("receivableErrorInfo", "支付失败，数据错误!");
      modelMap.remove("chinapayForm");
    }
    return "/autoaccessoryonline/payOnline/bcgogoreceiving";
  }
  /**
   * 充值第三步：充值完成
   * 合并付款(只支持  硬件付款+软件非首次付款)
   *
   * @param request     HttpServletRequest
   * @param chinapayDTO ChinapayDTO
   */
  @RequestMapping(params = "method=combinedPayOnlineComplete")
  public String combinedPayOnlineComplete(ModelMap modelMap, HttpServletRequest request, ChinapayDTO chinapayDTO) {
    IBcgogoReceivableService receivableService = ServiceManager.getService(IBcgogoReceivableService.class);
    IChinapayService chinapayService = ServiceManager.getService(IChinapayService.class);
    IPaymentService paymentService = ServiceManager.getService(IPaymentService.class);
    Long ordId = null;
    BcgogoReceivableOrderChinaPayResultDTO chinaPayResultDTO = new BcgogoReceivableOrderChinaPayResultDTO();
    chinaPayResultDTO.setPaymentTypeStr("合并付款");
    chinaPayResultDTO.setPaymentTimeStr(DateUtil.dateLongToStr(System.currentTimeMillis(), DateUtil.DATE_STRING_FORMAT_DEFAULT));
    try {
      chinapayDTO.setMessage("ChinaPay call front page for combined.");
      chinapayDTO = chinapayService.pgReceive(chinapayDTO);
      if (chinapayDTO == null) {
        LOG.warn("ChinaPay post us chinapayDTO is null");
        chinaPayResultDTO.setErrorInfo("支付失败,充值序号无效！");
      } else if (ChinaPay.isPaySuccess(chinapayDTO.getPayStat())) {
        if (StringUtils.isEmpty(chinapayDTO.getOrdId())) {
          chinaPayResultDTO.setErrorInfo("支付失败,数据错误！");
          throw new Exception("ordId is null");
        }
        ordId = Long.valueOf(chinapayDTO.getOrdId());
        if (BcgogoConcurrentController.lock(ConcurrentScene.CHINA_PAY,ordId )) {
          Double totalPaidAmount = 0d;
          ChinaPayParamLog log = paymentService.getChinaPayParamLog(Long.valueOf(chinapayDTO.getOrdId()));

          List<BcgogoReceivableDTO> dtoList = log.toCombinedBcgogoReceivableDTOList();
          if (ChinaPayParamStatus.EFFECTIVE.equals(log.getChinaPayParamStatus())) {
            if(CollectionUtils.isNotEmpty(dtoList)){
              List<Long> bcgogoReceivableOrderRecordRelationIds = new ArrayList<Long>();
              for(BcgogoReceivableDTO dto : dtoList) {
                bcgogoReceivableOrderRecordRelationIds.add(dto.getBcgogoReceivableOrderRecordRelationId());
              }
              List<BcgogoReceivableDTO> bcgogoReceivableDTOList = receivableService.getBcgogoReceivableDTOByRelationId(bcgogoReceivableOrderRecordRelationIds.toArray(new Long[bcgogoReceivableOrderRecordRelationIds.size()]));
              Map<Long,BcgogoReceivableDTO> bcgogoReceivableDTOMap = new HashMap<Long, BcgogoReceivableDTO>();
              for(BcgogoReceivableDTO bcgogoReceivableDTO :bcgogoReceivableDTOList){
                bcgogoReceivableDTOMap.put(bcgogoReceivableDTO.getBcgogoReceivableOrderRecordRelationId(),bcgogoReceivableDTO);
              }
              for(BcgogoReceivableDTO dto : dtoList) {
                totalPaidAmount+=dto.getPaidAmount();
                dto.setPaymentMethod(PaymentMethod.ONLINE_PAYMENT);
                BcgogoReceivableDTO dbBcgogoReceivableDTO = bcgogoReceivableDTOMap.get(dto.getBcgogoReceivableOrderRecordRelationId());
                if(dbBcgogoReceivableDTO!=null){
                  dto.setPaymentType(PaymentType.valueOf(dbBcgogoReceivableDTO.getOrderPaymentType()));
                  dto.setReceivableMethod(ReceivableMethod.valueOf(dbBcgogoReceivableDTO.getRelationReceivableMethod()));
                  if(PaymentType.HARDWARE.equals(dto.getPaymentType())){
                    receivableService.hardwareReceivable(dto);
                  }else if(PaymentType.SOFTWARE.equals(dto.getPaymentType()) && ReceivableMethod.INSTALLMENT.equals(dto.getReceivableMethod())){//不会是首次
                    receivableService.instalmentReceivable(dto);
                  }else if(PaymentType.SOFTWARE.equals(dto.getPaymentType()) && ReceivableMethod.UNCONSTRAINED.equals(dto.getReceivableMethod())){//
                    receivableService.softwareReceivable(dto);
                  }
                }
              }
            }else{
              chinaPayResultDTO.setErrorInfo("支付失败,数据错误！");
            }
            paymentService.updateChinaPayParamLogChinaPayStatus(log.getOrdId(), ChinaPayParamStatus.BE_USED);
          }else{
            if(CollectionUtils.isNotEmpty(dtoList)){
              for(BcgogoReceivableDTO dto : dtoList) {
                totalPaidAmount+=dto.getPaidAmount();
              }
            }
          }
          chinaPayResultDTO.setCurrentPaidAmount(totalPaidAmount);
        }

        LOG.info("ChinaPay call front page for combined.");
      } else {
        chinaPayResultDTO.setErrorInfo("支付失败,数据错误！");
        LOG.info("支付失败：{}", chinapayDTO.toString());
      }
    } catch (Exception e) {
      LOG.debug("/bcgogoReceivable.do");
      LOG.debug("method=combinedPayOnlineComplete");
      LOG.error(e.getMessage(), e);
      chinaPayResultDTO.setErrorInfo("支付失败,数据错误！");
    }finally {
      if(ordId!=null)
        BcgogoConcurrentController.release(ConcurrentScene.CHINA_PAY, ordId);
    }
    modelMap.addAttribute("chinaPayResultDTO",chinaPayResultDTO);
    return "/autoaccessoryonline/payOnline/bcgogoReceivableOrderChinaPayResult";
  }

  /**
   * 充值第三步：充值完成
   * 硬件付款（目前只支持全额付款）
   *
   * @param request     HttpServletRequest
   * @param chinapayDTO ChinapayDTO
   */
  @RequestMapping(params = "method=hardwareOnlineComplete")
  public String hardwareOnlineComplete(ModelMap modelMap, HttpServletRequest request, ChinapayDTO chinapayDTO) {
    IBcgogoReceivableService receivableService = ServiceManager.getService(IBcgogoReceivableService.class);
    IChinapayService chinapayService = ServiceManager.getService(IChinapayService.class);
    IPaymentService paymentService = ServiceManager.getService(IPaymentService.class);
    Long ordId = null;
    BcgogoReceivableOrderChinaPayResultDTO chinaPayResultDTO = new BcgogoReceivableOrderChinaPayResultDTO();
    chinaPayResultDTO.setPaymentTypeStr("软件费用 - 分期支付");
    chinaPayResultDTO.setPaymentTimeStr(DateUtil.dateLongToStr(System.currentTimeMillis(), DateUtil.DATE_STRING_FORMAT_DEFAULT));
    try {
      chinapayDTO.setMessage("ChinaPay call front page for hardware.");
      chinapayDTO = chinapayService.pgReceive(chinapayDTO);
      if (chinapayDTO == null) {
        LOG.warn("ChinaPay post us chinapayDTO is null");
        chinaPayResultDTO.setErrorInfo("支付失败,充值序号无效！");
      } else if (ChinaPay.isPaySuccess(chinapayDTO.getPayStat())) {
        if (StringUtils.isEmpty(chinapayDTO.getOrdId())) {
          chinaPayResultDTO.setErrorInfo("支付失败,数据错误！");
          throw new Exception("ordId is null");
        }
        ordId = Long.valueOf(chinapayDTO.getOrdId());
        if (BcgogoConcurrentController.lock(ConcurrentScene.CHINA_PAY,ordId )) {
          ChinaPayParamLog log = paymentService.getChinaPayParamLog(Long.valueOf(chinapayDTO.getOrdId()));
          BcgogoReceivableDTO dto = log.toHardwareBcgogoReceivableDTO();
          if (ChinaPayParamStatus.EFFECTIVE.equals(log.getChinaPayParamStatus())) {
            dto.setPaymentMethod(PaymentMethod.ONLINE_PAYMENT);
            receivableService.hardwareReceivable(dto);
            paymentService.updateChinaPayParamLogChinaPayStatus(log.getOrdId(), ChinaPayParamStatus.BE_USED);
          }else{
            BcgogoReceivableOrderRecordRelationDTO bcgogoReceivableOrderRecordRelationDTO = receivableService.getBcgogoReceivableOrderRecordRelationDTOById(WebUtil.getShopId(request), dto.getBcgogoReceivableOrderRecordRelationId());
            dto.setBcgogoReceivableOrderId(bcgogoReceivableOrderRecordRelationDTO.getBcgogoReceivableOrderId());
          }
          chinaPayResultDTO.setCurrentPaidAmount(dto.getPaidAmount());
          chinaPayResultDTO.setBcgogoReceivableOrderId(dto.getBcgogoReceivableOrderId());
        }

        LOG.info("ChinaPay call front page for hardware.");
      } else {
        chinaPayResultDTO.setErrorInfo("支付失败,数据错误！");
        LOG.info("支付失败：{}", chinapayDTO.toString());
      }
    } catch (Exception e) {
      LOG.debug("/bcgogoReceivable.do");
      LOG.debug("method=hardwareOnlineComplete");
      LOG.error(e.getMessage(), e);
      chinaPayResultDTO.setErrorInfo("支付失败,数据错误！");
    }finally {
      if(ordId!=null)
        BcgogoConcurrentController.release(ConcurrentScene.CHINA_PAY, ordId);
    }
    modelMap.addAttribute("chinaPayResultDTO",chinaPayResultDTO);
    return "/autoaccessoryonline/payOnline/bcgogoReceivableOrderChinaPayResult";
  }

  /**
   * 充值第三步：充值完成
   * 软件付款 首期/全额
   *
   * @param request     HttpServletRequest
   * @param chinapayDTO ChinapayDTO
   */
  @RequestMapping(params = "method=softwareOnlineComplete")
  public String softwareOnlineComplete(ModelMap modelMap, HttpServletRequest request,ChinapayDTO chinapayDTO) {
    IBcgogoReceivableService receivableService = ServiceManager.getService(IBcgogoReceivableService.class);
    IChinapayService chinapayService = ServiceManager.getService(IChinapayService.class);
    IPaymentService paymentService = ServiceManager.getService(IPaymentService.class);
    Long ordId = null;
    BcgogoReceivableOrderChinaPayResultDTO chinaPayResultDTO = new BcgogoReceivableOrderChinaPayResultDTO();
    chinaPayResultDTO.setPaymentTimeStr(DateUtil.dateLongToStr(System.currentTimeMillis(), DateUtil.DATE_STRING_FORMAT_DEFAULT));
    try {
      chinapayDTO.setMessage("ChinaPay call front page for software.");
      chinapayDTO = chinapayService.pgReceive(chinapayDTO);
      if (chinapayDTO == null) {
        LOG.warn("ChinaPay post us chinapayDTO is null");
        chinaPayResultDTO.setErrorInfo("支付失败,充值序号无效！");
      } else if (ChinaPay.isPaySuccess(chinapayDTO.getPayStat())) {
        if (StringUtils.isEmpty(chinapayDTO.getOrdId())) {
          chinaPayResultDTO.setErrorInfo("支付失败,数据错误！");
          throw new Exception("ordId is null");
        }
        ordId = Long.valueOf(chinapayDTO.getOrdId());
        if (BcgogoConcurrentController.lock(ConcurrentScene.CHINA_PAY,ordId )) {
          ChinaPayParamLog log = paymentService.getChinaPayParamLog(Long.valueOf(chinapayDTO.getOrdId()));
          BcgogoReceivableDTO dto = log.toSoftwareBcgogoReceivableDTO();
          if (ChinaPayParamStatus.EFFECTIVE.equals(log.getChinaPayParamStatus())) {
            dto.setPaymentMethod(PaymentMethod.ONLINE_PAYMENT);
            receivableService.softwareReceivable(dto);
            paymentService.updateChinaPayParamLogChinaPayStatus(log.getOrdId(), ChinaPayParamStatus.BE_USED);
          }else{
            BcgogoReceivableOrderRecordRelationDTO bcgogoReceivableOrderRecordRelationDTO = receivableService.getBcgogoReceivableOrderRecordRelationDTOById(WebUtil.getShopId(request), dto.getBcgogoReceivableOrderRecordRelationId());
            dto.setBcgogoReceivableOrderId(bcgogoReceivableOrderRecordRelationDTO.getBcgogoReceivableOrderId());
          }
          if (dto.getReceivableMethod() == ReceivableMethod.FULL) {
            chinaPayResultDTO.setPaymentTypeStr("软件费用 - 全额支付");
          } else if (dto.getReceivableMethod() == ReceivableMethod.INSTALLMENT) {
            chinaPayResultDTO.setPaymentTypeStr("软件费用 - 分期支付");
          } else if (dto.getReceivableMethod() == ReceivableMethod.UNCONSTRAINED) {
            chinaPayResultDTO.setPaymentTypeStr("软件费用 - 其他付款");
          }
          chinaPayResultDTO.setCurrentPaidAmount(dto.getPaidAmount());
          chinaPayResultDTO.setBcgogoReceivableOrderId(dto.getBcgogoReceivableOrderId());
        }

        LOG.info("ChinaPay call front page for software.");
      } else {
        chinaPayResultDTO.setErrorInfo("支付失败,数据错误！");
        LOG.info("支付失败：{}", chinapayDTO.toString());
      }
    } catch (Exception e) {
      LOG.debug("/bcgogoReceivable.do");
      LOG.debug("method=softwareOnlineComplete");
      LOG.error(e.getMessage(), e);
      chinaPayResultDTO.setErrorInfo("支付失败,数据错误！");
    }finally {
      if(ordId!=null)
        BcgogoConcurrentController.release(ConcurrentScene.CHINA_PAY, ordId);
    }
    modelMap.addAttribute("chinaPayResultDTO",chinaPayResultDTO);
    return "/autoaccessoryonline/payOnline/bcgogoReceivableOrderChinaPayResult";
  }

  /**
   * 充值第三步：充值完成
   * 软件 分期
   *
   * @param request     HttpServletRequest
   * @param chinapayDTO ChinapayDTO
   */
  @RequestMapping(params = "method=instalmentOnLineComplete")
  public String instalmentOnLineComplete(ModelMap modelMap, HttpServletRequest request, ChinapayDTO chinapayDTO) {
    IBcgogoReceivableService receivableService = ServiceManager.getService(IBcgogoReceivableService.class);
    IChinapayService chinapayService = ServiceManager.getService(IChinapayService.class);
    IPaymentService paymentService = ServiceManager.getService(IPaymentService.class);
    Long ordId = null;
    BcgogoReceivableOrderChinaPayResultDTO chinaPayResultDTO = new BcgogoReceivableOrderChinaPayResultDTO();
    chinaPayResultDTO.setPaymentTypeStr("软件费用 - 分期支付");
    chinaPayResultDTO.setPaymentTimeStr(DateUtil.dateLongToStr(System.currentTimeMillis(), DateUtil.DATE_STRING_FORMAT_DEFAULT));
    try {
      chinapayDTO.setMessage("ChinaPay call front page for instalment software.");
      chinapayDTO = chinapayService.pgReceive(chinapayDTO);
      if (chinapayDTO == null) {
        LOG.warn("ChinaPay post us chinapayDTO is null");
        chinaPayResultDTO.setErrorInfo("支付失败,充值序号无效！");
      } else if (ChinaPay.isPaySuccess(chinapayDTO.getPayStat())) {
        if (StringUtils.isEmpty(chinapayDTO.getOrdId())) {
          chinaPayResultDTO.setErrorInfo("支付失败,数据错误！");
          throw new Exception("ordId is null");
        }
        ordId = Long.valueOf(chinapayDTO.getOrdId());
        if (BcgogoConcurrentController.lock(ConcurrentScene.CHINA_PAY,ordId )) {
          ChinaPayParamLog log = paymentService.getChinaPayParamLog(Long.valueOf(chinapayDTO.getOrdId()));
          BcgogoReceivableDTO dto = log.toSoftwareBcgogoReceivableDTO();
          if (ChinaPayParamStatus.EFFECTIVE.equals(log.getChinaPayParamStatus())) {
            dto.setPaymentMethod(PaymentMethod.ONLINE_PAYMENT);
            receivableService.instalmentReceivable(dto);
            paymentService.updateChinaPayParamLogChinaPayStatus(log.getOrdId(), ChinaPayParamStatus.BE_USED);
          }else{
            BcgogoReceivableOrderRecordRelationDTO bcgogoReceivableOrderRecordRelationDTO = receivableService.getBcgogoReceivableOrderRecordRelationDTOById(WebUtil.getShopId(request), dto.getBcgogoReceivableOrderRecordRelationId());
            dto.setBcgogoReceivableOrderId(bcgogoReceivableOrderRecordRelationDTO.getBcgogoReceivableOrderId());
          }
          chinaPayResultDTO.setCurrentPaidAmount(dto.getPaidAmount());
          chinaPayResultDTO.setBcgogoReceivableOrderId(dto.getBcgogoReceivableOrderId());
        }
        LOG.info("ChinaPay call front page for software.");
      } else {
        chinaPayResultDTO.setErrorInfo("支付失败,数据错误！");
        LOG.info("支付失败：{}", chinapayDTO.toString());
      }
    } catch (Exception e) {
      LOG.debug("/bcgogoReceivable.do");
      LOG.debug("method=instalmentOnLineComplete");
      LOG.error(e.getMessage(), e);
      chinaPayResultDTO.setErrorInfo("支付失败,数据错误！");
    }finally {
      if(ordId!=null)
        BcgogoConcurrentController.release(ConcurrentScene.CHINA_PAY, ordId);
    }
    modelMap.addAttribute("chinaPayResultDTO",chinaPayResultDTO);
    return "/autoaccessoryonline/payOnline/bcgogoReceivableOrderChinaPayResult";
  }

  /**
   * 根据分期id获得分期详情
   *
   * @param modelMap
   * @param request
   * @return
   */
  @RequestMapping(params = "method=getInstalmentPlanAlgorithmsById")
  @ResponseBody
  public Object getInstalmentPlanAlgorithmsById(ModelMap modelMap, HttpServletRequest request,String instalmentPlanAlgorithmIdStr) {
    if (StringUtil.isEmpty(instalmentPlanAlgorithmIdStr) || !NumberUtil.isNumber(instalmentPlanAlgorithmIdStr)) {
      return null;
    }
    IBcgogoReceivableService receivableService = ServiceManager.getService(IBcgogoReceivableService.class);
    return receivableService.getInstalmentPlanAlgorithmsById(Long.valueOf(instalmentPlanAlgorithmIdStr));
  }


  @RequestMapping(params = "method=print")
  public void print(Long orderId,HttpServletRequest request,HttpServletResponse response) {
    IBcgogoReceivableService receivableService = ServiceManager.getService(IBcgogoReceivableService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    IPrintService printService = ServiceManager.getService(IPrintService.class);
    Long shopId = null;
    try {
      shopId = WebUtil.getShopId(request);
      BcgogoReceivableOrderDTO bcgogoReceivableOrderDTO = receivableService.getBcgogoReceivableOrderDetail(shopId,orderId);

      PrintTemplateDTO printTemplateDTO = printService.getSinglePrintTemplateDTOByShopIdAndType(shopId, OrderTypes.BCGOGO_HARDWARE_RECEIVABLE_ORDER);

      PrintWriter out = response.getWriter();
      response.setContentType("text/html");
      response.setCharacterEncoding("UTF-8");
      if (printTemplateDTO!=null) {
        byte bytes[] = printTemplateDTO.getTemplateHtml();
        String myTemplate = new String(bytes, "UTF-8");

        //初始化并取得Velocity引擎
        VelocityEngine ve = new VelocityEngine();
        ve.setProperty(VelocityEngine.RESOURCE_LOADER, "string");
        ve.setProperty("string.resource.loader.class", "org.apache.velocity.runtime.resource.loader.StringResourceLoader");
        ve.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.SimpleLog4JLogSystem");
        ve.setProperty("runtime.log.logsystem.log4j.category", "velocity");
        ve.setProperty("runtime.log.logsystem.log4j.logger", "velocity");
        ve.init();
        //创建资源库

        StringResourceRepository repo = StringResourceLoader.getRepository();
        String myTemplateName = "bcgogoReceivableOrderPrint" + String.valueOf(WebUtil.getShopId(request));
        //模板资源存放 资源库 中
        repo.putStringResource(myTemplateName, myTemplate);
        //从资源库中加载模板
        Template template = ve.getTemplate(myTemplateName);
        //取得velocity的模版
        Template t = ve.getTemplate(myTemplateName, "UTF-8");
        //取得velocity的上下文context
        VelocityContext context = new VelocityContext();
        //把数据填入上下文
        context.put("bcgogoReceivableOrderDTO", bcgogoReceivableOrderDTO);
        //输出流
        StringWriter writer = new StringWriter();
        //转换输出
        t.merge(context, writer);
        out.print(writer);
        writer.close();
      } else {
        out.print("<html><head><title></title></head><body>没有可用的模板</body><html>");
      }

      out.close();

    } catch (Exception e) {
      LOG.debug("/bcgogoReceivable.do");
      LOG.debug("method=print");
      LOG.error(e.getMessage(), e);
    }

  }
}
