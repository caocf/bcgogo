package com.bcgogo.config;

import com.bcgogo.common.Result;
import com.bcgogo.common.WebUtil;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.dto.image.DataImageRelationDTO;
import com.bcgogo.config.dto.image.ImageInfoDTO;
import com.bcgogo.config.service.IAgentProductService;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IServiceCategoryService;
import com.bcgogo.config.service.IShopService;
import com.bcgogo.config.service.image.IImageService;
import com.bcgogo.config.upyun.UpYunManager;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.constant.LogicResource;
import com.bcgogo.enums.config.DataType;
import com.bcgogo.enums.config.ImageType;
import com.bcgogo.enums.shop.*;
import com.bcgogo.enums.txn.finance.ChargeType;
import com.bcgogo.enums.user.ResourceType;
import com.bcgogo.notification.dto.InvitationCodeDTO;
import com.bcgogo.notification.invitationCode.InvitationCodeGeneratorClient;
import com.bcgogo.notification.service.ISmsService;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.user.dto.ContactDTO;
import com.bcgogo.user.dto.permission.ShopVersionDTO;
import com.bcgogo.user.service.permission.IPrivilegeService;
import com.bcgogo.user.service.permission.IShopVersionService;
import com.bcgogo.user.service.wx.impl.WXAccountService;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-1-22
 * Time: 上午9:48
 */
@Controller
@RequestMapping("/shopRegister.do")
public class ShopRegisterController {
  private static final Logger LOG = LoggerFactory.getLogger(ShopRegisterController.class);
  private static final String REGISTER_MAIN = "regist/registerMain";
  private static final String REGISTER_CUSTOMER = "regist/registerCustomer";
  private static final String REGISTER_SUPPLIER = "regist/registerSupplier";
  private static final String REGISTER_UPDATE= "regist/registerUpdate";
  private static final String REGISTER= "regist/register";
  private IConfigService configService = null;
  private IShopService shopService = null;
  private IShopVersionService shopVersionService;
  private InvitationCodeGeneratorClient invitationCodeGeneratorClient;
  private ITxnService  txnService;
  private IProductService productService;

  public IProductService getProductService() {
    return productService== null ? ServiceManager.getService(IProductService.class) : productService;
  }

  public ITxnService getTxnService() {
    return txnService == null ? ServiceManager.getService(ITxnService.class) : txnService;
  }

  public IConfigService getConfigService() {
    if (configService == null) {
      configService = ServiceManager.getService(IConfigService.class);
    }
    return configService;
  }

  public IShopService getShopService() {
    if (shopService == null) {
      shopService = ServiceManager.getService(IShopService.class);
    }
    return shopService;
  }

  public IShopVersionService getShopVersionService() {
    return shopVersionService == null ? ServiceManager.getService(IShopVersionService.class) : shopVersionService;
  }

  public InvitationCodeGeneratorClient getInvitationCodeGeneratorClient() {
    return invitationCodeGeneratorClient == null ? ServiceManager.getService(InvitationCodeGeneratorClient.class) : invitationCodeGeneratorClient;
  }

  @RequestMapping(params = "method=registerShopInfo")
  public String registerShopInfo(HttpServletRequest request) {
    return "/config/registerShopInfo";
  }

  //  @ResponseBody 此处不能用ie9下有问题BCSHOP-6738
  @RequestMapping(params = "method=saveShopInfo")
  public void saveShopInfo(HttpServletRequest request,HttpServletResponse response, ShopDTO shopDTO) throws IOException {
    response.setContentType("text/html;charset=utf-8");
    PrintWriter out = response.getWriter();
    try {
      Result result = shopDataHandler(shopDTO,request);
      //数据处理过程
      if (result != null && !result.isSuccess()) {
        out.write(JsonUtil.objectToJson(result));
        return;
      }
      shopDTO.setLocateStatus(LocateStatus.ACTIVE);
      shopDTO = ServiceManager.getService(IShopService.class).registerShop(shopDTO);
      if (shopDTO.getId() == null) {
        result.LogErrorMsg("店铺信息异常");
        out.write(JsonUtil.objectToJson(result));
        return;
      }
      shopDTO.prepareForSaveProduct();
      getTxnService().batchSaveProductWithReindex(shopDTO.getId(),null,shopDTO.getProductDTOs());
      getProductService().saveShopRegisterProduct(shopDTO.getProductDTOs());
      getConfigService().saveOrUpdateUnitSort(shopDTO.getId(),shopDTO.getProductDTOs());
      getConfigService().saveShopBusinessScopeFromShopDTO(shopDTO);
      getProductService().saveShopVehicleBrandModel(shopDTO);
      //服务范围
      ServiceManager.getService(IServiceCategoryService.class).saveShopServiceCategory(shopDTO.getId(),ArrayUtil.toLongArr(shopDTO.getServiceCategoryIds()));
      //代理产品
      ServiceManager.getService(IAgentProductService.class).saveShopAgentProduct(shopDTO.getId(),ArrayUtil.toLongArr(shopDTO.getAgentProductIds()));
      //保存店铺与客户的临时关联关系
      String customerIdStr = request.getParameter("customerId");
      Long customerId = null;
      if (StringUtil.isNotEmpty(customerIdStr)) {

        customerId = Long.parseLong(customerIdStr);
        getConfigService().saveShopCustomerRelation(shopDTO.getId(), customerId);
      }
      //保存成功后处理图片
      if(shopDTO.getImageCenterDTO()!=null){
        List<DataImageRelationDTO> dataImageRelationDTOList = new ArrayList<DataImageRelationDTO>();
        IImageService imageService = ServiceManager.getService(IImageService.class);
        if(CollectionUtils.isNotEmpty(shopDTO.getImageCenterDTO().getShopImagePaths())){
          int i=0;
          for(String imagePath:shopDTO.getImageCenterDTO().getShopImagePaths()){
            if(StringUtils.isNotBlank(imagePath)){
              DataImageRelationDTO dataImageRelationDTO = new DataImageRelationDTO(shopDTO.getId(), shopDTO.getId(), DataType.SHOP, i==0?ImageType.SHOP_MAIN_IMAGE:ImageType.SHOP_AUXILIARY_IMAGE, i);
              dataImageRelationDTO.setImageInfoDTO(new ImageInfoDTO(shopDTO.getId(), imagePath));
              dataImageRelationDTOList.add(dataImageRelationDTO);
              i++;
            }
          }
        }
        if(StringUtils.isNotBlank(shopDTO.getImageCenterDTO().getShopBusinessLicenseImagePath())){
          DataImageRelationDTO dataImageRelationDTO = new DataImageRelationDTO(shopDTO.getId(), shopDTO.getId(), DataType.SHOP, ImageType.SHOP_BUSINESS_LICENSE_IMAGE, 1);
          dataImageRelationDTO.setImageInfoDTO(new ImageInfoDTO(shopDTO.getId(),shopDTO.getImageCenterDTO().getShopBusinessLicenseImagePath()));
          dataImageRelationDTOList.add(dataImageRelationDTO);
        }
        Set<ImageType> imageTypeSet = new HashSet<ImageType>();
        imageTypeSet.add(ImageType.SHOP_MAIN_IMAGE);
        imageTypeSet.add(ImageType.SHOP_AUXILIARY_IMAGE);
        imageTypeSet.add(ImageType.SHOP_BUSINESS_LICENSE_IMAGE);
        imageService.saveOrUpdateDataImageDTOs(shopDTO.getId(), imageTypeSet, DataType.SHOP, shopDTO.getId(), dataImageRelationDTOList.toArray(new DataImageRelationDTO[dataImageRelationDTOList.size()]));
      }
      getInvitationCodeGeneratorClient().updateInvitationCodeToUsed(shopDTO.getInvitationCode());
      ServiceManager.getService(ISmsService.class).sendRegistrationReminderForBackgroundAuditStaff(shopDTO.getName());
      result = new Result(ValidatorConstant.REGISTER_SUCCESS, true, Result.Operation.ALERT.name(), null);
      //保存店铺微信信息
      ServiceManager.getService(WXAccountService.class).createDefaultWXShopAccount(shopDTO.getId());
      out.write(JsonUtil.objectToJson(result));
    } catch (Exception e) {
      LOG.error("method=saveShopInfo" + e.getMessage(), e);
      out.write(JsonUtil.objectToJson(new Result("网络异常",false,Result.Operation.ALERT.name(),null)));
    }finally {
      out.flush();
      out.close();
    }
  }

  //处理shop数据
  private Result shopDataHandler(ShopDTO shopDTO, HttpServletRequest request) throws Exception {
    Result result = new Result();
    Map<String, Object> resultData = new HashMap<String, Object>();
    shopDTO.setShopStatus(ShopStatus.CHECK_PENDING);
    shopDTO.setShopState(ShopState.ACTIVE);
    shopDTO.setSubmitApplicationDate(System.currentTimeMillis());
    if(ArrayUtils.isEmpty(shopDTO.getContacts())){
      result.setSuccess(false);
    }
    ContactDTO[] contactDTOs = new ContactDTO[0];
    int contactNum = 0;
    for(ContactDTO contactDTO : shopDTO.getContacts()){
      if(StringUtils.isBlank(contactDTO.getName()) && StringUtils.isBlank(contactDTO.getMobile())
        && StringUtils.isBlank(contactDTO.getEmail()) && StringUtils.isBlank(contactDTO.getQq())){
        continue;
      }
      if(contactNum == 0){
        shopDTO.setOwner(contactDTO.getName());
        shopDTO.setLegalRep(contactDTO.getName());
        shopDTO.setMobile(contactDTO.getMobile());
        if(StringUtils.isBlank(shopDTO.getStoreManager())){
          shopDTO.setStoreManager(contactDTO.getName());
        }
        if(StringUtils.isBlank(shopDTO.getStoreManagerMobile())){
          shopDTO.setStoreManagerMobile(contactDTO.getMobile());
        }
        contactDTO.setIsShopOwner(1);
      }else{
        contactDTO.setIsShopOwner(0);
      }
      contactDTO.setIsMainContact(1);
      contactDTO.setLevel(contactNum);
      contactDTOs = (ContactDTO[])ArrayUtils.add(contactDTOs, contactDTO);
      contactNum++;
    }
    shopDTO.setContacts(contactDTOs);
    if (StringUtils.isBlank(shopDTO.getName())) {
      resultData.put("name", "empty");
      result.setSuccess(false);
    } else if (getShopService().checkShopName(shopDTO.getName(), shopDTO.getId())) {
      resultData.put("name", "duplicate");
      result.setSuccess(false);
    }
    //负责人/店主   管理员就是店主
    if (StringUtils.isBlank(shopDTO.getOwner())) {
      resultData.put("owner", "empty");
      result.setSuccess(false);
    }
    if (StringUtils.isBlank(shopDTO.getMobile())) {
      resultData.put("mobile", "empty");
      result.setSuccess(false);
    }
    //地址
    if (StringUtils.isBlank(shopDTO.getAddress())) {
      resultData.put("address", "empty");
      result.setSuccess(false);
    }

    if (("专卖店").equals(shopDTO.getOperationMode())) {
      if (StringUtil.isNotEmpty(shopDTO.getOperationModeBrand())
        && ("请输入品牌").equals(shopDTO.getOperationModeBrand().trim())) {
        shopDTO.setOperationMode(shopDTO.getOperationMode() + ":" + shopDTO.getOperationModeBrand());
      }
    }

    //店铺升级客户
    if ("UPDATE".equals(request.getParameter("registerShopType"))) {
      // 如果不是由bcgogo业务员注册，则记下批发商的shopId
      if (!ServiceManager.getService(IPrivilegeService.class).verifierUserGroupResource(WebUtil.getShopId(request),
        WebUtil.getUserGroupId(request), ResourceType.logic, LogicResource.SALE_LOGIN)) {
        shopDTO.setWholesalerShopId(WebUtil.getShopId(request));
      }
      ShopDTO inviter = getConfigService().getShopById(WebUtil.getShopId(request));
      shopDTO.setAgentId(String.valueOf(inviter.getId()));
      if (shopDTO != null) {
        shopDTO.setAgent(inviter.getName());
        shopDTO.setAgentMobile(inviter.getMobile());
      }
      shopDTO.setRegisterType(RegisterType.SUPPLIER_REGISTER);
      shopDTO.setChargeType(ChargeType.ONE_TIME);
    }
    //业务员注册
    else if("SALES_REGISTER".equals(request.getParameter("registerShopType"))){
      shopDTO.setRegisterType(RegisterType.SALESMAN_REGISTER);
      if(shopDTO.getAgentId() == null){
        resultData.put("agentId", "empty");
        result.setSuccess(false);
      }
      shopDTO.setFollowId(WebUtil.getUserId(request));
      shopDTO.setFollowName(WebUtil.getUserName(request));
    }
    //自己注册/邀请码注册
    else {
      if (StringUtils.isBlank(shopDTO.getInvitationCode())) {
        shopDTO.setRegisterType(RegisterType.SELF_REGISTER);
//        UserDTO userDTO = ServiceManager.getService(IUserService.class).getUserByUserId(ShopConstant.JACK_CHEN_USER_ID);
//        shopDTO.setAgent(userDTO.getName());
//        shopDTO.setAgentMobile(userDTO.getMobile());
//        shopDTO.setAgentId(userDTO.getUserNo());
      } else {
        //邀请码    invitationCode
        InvitationCodeDTO invitationCodeDTO = getInvitationCodeGeneratorClient().validateInvitationCode(
          shopDTO.getInvitationCode(), result, resultData);
        shopDTO.setInvitationCodeDTO(invitationCodeDTO);
        if (invitationCodeDTO != null) {
          ShopDTO inviter = getConfigService().getShopById(invitationCodeDTO.getInviterId());
          shopDTO.setAgentId(String.valueOf(invitationCodeDTO.getInviterId()));
          if (shopDTO != null) {
            shopDTO.setAgent(inviter.getName());
            shopDTO.setAgentMobile(inviter.getMobile());
          }
          shopDTO.setRegisterType(RegisterType.getRegisterTypeByInviteType(invitationCodeDTO.getType(),
            invitationCodeDTO.getInviteeType()));
        }

      }
//      shopDTO.setChargeType(ChargeType.ONE_TIME);
    }
    //软件售价
    ShopVersionDTO shopVersionDTO = getShopVersionService().getShopVersionById(shopDTO.getShopVersionId());
    if(shopVersionDTO!=null && shopVersionDTO.getSoftPrice()!=null && shopDTO.getSoftPrice() == null){
      shopDTO.setSoftPrice(shopVersionDTO.getSoftPrice().doubleValue());
    }
    // 关联客户上限
    if(ConfigUtils.isWholesalerVersion(WebUtil.getShopVersionId(request))){
      shopDTO.setShopLevel(ShopLevel.PRIMARY_WHOLESALER);
    }
    if (shopDTO.getShopKind() == null) {
      shopDTO.setShopKind(ShopKind.OFFICIAL);
    }
    result.setData(resultData);
    return result;
  }


  @RequestMapping(params = "method=searchLicenseNo")
  @ResponseBody
  public Object searchLicenseNo(HttpServletRequest request, String localArea) {
    IProductService productService = ServiceManager.getService(IProductService.class);
    Map<String, Object> result = new HashMap<String, Object>();
    try {
      //根据地区找到车牌
      String localCarNo = productService.getCarNoByAreaNo(Long.valueOf(localArea));
      if (StringUtils.isNotBlank(localCarNo)) {
        result.put("plateCarNo", localCarNo);
        result.put("success", true);
      }
    } catch (Exception e) {
      result.put("success", false);
      LOG.debug("/shopManager.do");
      LOG.debug("method=searchLicenseNo");
      LOG.debug("shopId:" + WebUtil.getShopId(request) + ",userId:" + WebUtil.getUserId(request));
      LOG.debug("localArea:" + localArea);
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  @RequestMapping(params = "method=registerMain")
  public String  registerMain(HttpServletRequest request, ModelMap modelMap){
    try{
      String registerType = request.getParameter("registerType");
      //系统配置推荐给客户注册的店铺
      String commonShopVersionsToRegister =  getConfigService().getConfig("CommonShopVersionsToRegister", ShopConstant.BC_SHOP_ID);
      //系统配置推荐给供应商注册的店铺
      String wholesalerShopVersionsToRegister =  getConfigService().getConfig("WholesalerShopVersionsToRegister", ShopConstant.BC_SHOP_ID);
      //属于普通版的分类
      String commonShopVersionIds = getConfigService().getConfig("CommonShopVersions", ShopConstant.BC_SHOP_ID);
      //属于批发商版的分类
      String wholesalerShopVersionIds = getConfigService().getConfig("WholesalerShopVersions", ShopConstant.BC_SHOP_ID);
      //批发商可以注册的店铺版本
      String supplierRegisterShopVersionIds =  getConfigService().getConfig("WHOLESALER_SHOP_REGISTER_SHOP_VERSIONS", ShopConstant.BC_SHOP_ID);

      modelMap.addAttribute("SHOP_PROBATIONARY_PERIOD",getConfigService().getConfig("SHOP_PROBATIONARY_PERIOD",ShopConstant.BC_SHOP_ID));

      List<ShopVersionDTO> shopVersionDTOs = getShopVersionService().getAllShopVersion();
      List<ShopVersionDTO> wholesalerShopVersionDTOs = null;
      List<ShopVersionDTO> commonShopVersionDTOs = null;
      List<ShopVersionDTO> fourSShopVersions = null;

      //拿到客户邀请供应商的邀请码
      if (("SUPPLIER").equals(registerType)){
        Long[] supplierShopVersionToRegister = NumberUtil.getSameIds(NumberUtil.parseLongValuesToArray(wholesalerShopVersionsToRegister, ",")
          ,NumberUtil.parseLongValuesToArray(wholesalerShopVersionIds, ","));
        wholesalerShopVersionDTOs = getShopVersionService().getShopVersionByIds(shopVersionDTOs,supplierShopVersionToRegister);

        Long[] customerShopVersionToRegister = NumberUtil.getSameIds(NumberUtil.parseLongValuesToArray(wholesalerShopVersionsToRegister, ",")
          ,NumberUtil.parseLongValuesToArray(commonShopVersionIds, ","));
        commonShopVersionDTOs = getShopVersionService().getShopVersionByIds(shopVersionDTOs,customerShopVersionToRegister);
        //拿到供应商邀请客户的邀请码 或者 批发商注册店铺 或者升级客户
      } else if (("CUSTOMER").equals(registerType)) {
        Long[] supplierShopVersionToRegister = NumberUtil.getSameIds(NumberUtil.parseLongValuesToArray(commonShopVersionsToRegister, ",")
          ,NumberUtil.parseLongValuesToArray(wholesalerShopVersionIds, ","));
        wholesalerShopVersionDTOs = getShopVersionService().getShopVersionByIds(shopVersionDTOs,supplierShopVersionToRegister);

        Long[] customerShopVersionToRegister = NumberUtil.getSameIds(NumberUtil.parseLongValuesToArray(commonShopVersionsToRegister, ",")
          ,NumberUtil.parseLongValuesToArray(commonShopVersionIds, ","));
        commonShopVersionDTOs = getShopVersionService().getShopVersionByIds(shopVersionDTOs,customerShopVersionToRegister);
      } else if (("SUPPLIER_REGISTER").equals(registerType) || ("SUPPLIER_UPDATE").equals(registerType)) {
        Long[] supplierShopVersionToRegister = NumberUtil.getSameIds(NumberUtil.parseLongValuesToArray(supplierRegisterShopVersionIds, ",")
          , NumberUtil.parseLongValuesToArray(wholesalerShopVersionIds, ","));
        wholesalerShopVersionDTOs = getShopVersionService().getShopVersionByIds(shopVersionDTOs, supplierShopVersionToRegister);

        Long[] customerShopVersionToRegister = NumberUtil.getSameIds(NumberUtil.parseLongValuesToArray(supplierRegisterShopVersionIds, ",")
          , NumberUtil.parseLongValuesToArray(commonShopVersionIds, ","));
        commonShopVersionDTOs = getShopVersionService().getShopVersionByIds(shopVersionDTOs, customerShopVersionToRegister);
//        modelMap.put("from","update");
      } else if(("SALES_REGISTER").equals(registerType)){
        Long[] supplierShopVersionToRegister = NumberUtil.getSameIds(NumberUtil.parseLongValuesToArray(wholesalerShopVersionsToRegister, ",")
          ,NumberUtil.parseLongValuesToArray(wholesalerShopVersionIds, ","));
        wholesalerShopVersionDTOs = getShopVersionService().getShopVersionByIds(shopVersionDTOs,supplierShopVersionToRegister);

        Long[] customerShopVersionToRegister = NumberUtil.getSameIds(NumberUtil.parseLongValuesToArray(commonShopVersionsToRegister, ",")
          ,NumberUtil.parseLongValuesToArray(commonShopVersionIds, ","));
        commonShopVersionDTOs = getShopVersionService().getShopVersionByIds(shopVersionDTOs,customerShopVersionToRegister);

        String fourSShopVersionIds = getConfigService().getConfig("fourSShopVersions", ShopConstant.BC_SHOP_ID);
        Long[] fourSShopVersionToRegister = NumberUtil.getSameIds(NumberUtil.parseLongValuesToArray(fourSShopVersionIds, ",")
          , NumberUtil.parseLongValuesToArray(fourSShopVersionIds, ","));
        fourSShopVersions = getShopVersionService().getShopVersionByIds(shopVersionDTOs, fourSShopVersionToRegister);
      } else {
        registerType = "";
        Long[] supplierShopVersionToRegister = NumberUtil.getSameIds(NumberUtil.parseLongValuesToArray(wholesalerShopVersionsToRegister, ",")
          ,NumberUtil.parseLongValuesToArray(wholesalerShopVersionIds, ","));
        wholesalerShopVersionDTOs = getShopVersionService().getShopVersionByIds(shopVersionDTOs,supplierShopVersionToRegister);

        Long[] customerShopVersionToRegister = NumberUtil.getSameIds(NumberUtil.parseLongValuesToArray(commonShopVersionsToRegister, ",")
          ,NumberUtil.parseLongValuesToArray(commonShopVersionIds, ","));
        commonShopVersionDTOs = getShopVersionService().getShopVersionByIds(shopVersionDTOs,customerShopVersionToRegister);
      }

      modelMap.addAttribute("fourSShopVersions", fourSShopVersions);
      modelMap.addAttribute("wholesalerShopVersion", wholesalerShopVersionDTOs);
      modelMap.addAttribute("commonShopVersions", commonShopVersionDTOs);
      modelMap.addAttribute("registerType", registerType);
      Map<String, Integer> softPrice = new HashMap<String, Integer>();
      for (ShopVersionDTO shopVersionDTO : shopVersionDTOs) {
        softPrice.put(shopVersionDTO.getName(), shopVersionDTO.getSoftPrice());
      }
      modelMap.addAttribute("softPrice", softPrice);

      return REGISTER_MAIN;
    } catch (Exception e){
      LOG.error("method=registerMain"+e.getMessage(),e);
      return "/web";
    }
  }

  @RequestMapping(params = "method=registerDetail")
  public String  registerDetail(HttpServletRequest request, ModelMap modelMap){
    try{
      String registerType = request.getParameter("registerShopType");
      modelMap.addAttribute("upYunFileDTO", UpYunManager.getInstance().generateDefaultUpYunFileDTO(ShopConstant.BC_SHOP_ID));
      modelMap.put("agentProducts",ServiceManager.getService(IAgentProductService.class).getAgentProductDTO(ShopConstant.BC_SHOP_ID));
      modelMap.addAttribute("registerType",registerType);
      ContactDTO contactDTO = new ContactDTO();
      contactDTO.setIsMainContact(1);
      ContactDTO[] contacts = new ContactDTO[]{contactDTO};
      modelMap.addAttribute("contacts", contacts);
      String shopVersionIdStr = request.getParameter("shopVersionId");
      if(StringUtils.isNotBlank(shopVersionIdStr) && NumberUtil.isNumber(shopVersionIdStr)){
        modelMap.addAttribute("toRegisterShopVersionId",shopVersionIdStr);
        modelMap.put("isWholesalerVersion",ConfigUtils.isWholesalerVersion(NumberUtil.longValue(shopVersionIdStr)));
      }
      if("CUSTOMER".equals(registerType)){
        modelMap.put("from","user");
        modelMap.put("registerShopType","CUSTOMER");
        return REGISTER;
//        return REGISTER_CUSTOMER;
      }else if("SUPPLIER".equals(registerType)){
        modelMap.put("from","user");
        modelMap.put("registerShopType","SUPPLIER");
        return REGISTER;
//        return REGISTER_SUPPLIER;
      }else if("UPDATE".equals(registerType)){
        return REGISTER;
//        return REGISTER_UPDATE;
      }else {
        modelMap.put("registerShopType","SALES");
        return REGISTER;
//        return REGISTER_CUSTOMER;
      }
    } catch (Exception e){
      LOG.error("method=registerDetail"+e.getMessage(),e);
      return "/web";
    }
  }

  @RequestMapping(params = "method=validateInvitationCode")
  @ResponseBody
  public Object validateInvitationCode(HttpServletRequest request, ModelMap modelMap, String invitationCode) {
    Result result = new Result();
    Map<String, Object> resultData = new HashMap<String, Object>();
    try {
//      String mobile = request.getParameter("mobile");
      getInvitationCodeGeneratorClient().validateInvitationCode(invitationCode, result, resultData);
      result.setData(resultData);
      return result;
    } catch (Exception e) {
      LOG.error("method=validateInvitationCode:invitationCode:{}" + e.getMessage(), e, invitationCode);
      result.setSuccess(false);
      resultData.put("invitationCode", "error");
      result.setData(resultData);
      return result;
    }
  }
}
