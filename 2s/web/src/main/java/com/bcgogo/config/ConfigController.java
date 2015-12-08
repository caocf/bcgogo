package com.bcgogo.config;

import com.bcgogo.common.CommonUtil;
import com.bcgogo.common.Result;
import com.bcgogo.common.UploadImg;
import com.bcgogo.common.WebUtil;
import com.bcgogo.config.cache.ServiceCategoryCache;
import com.bcgogo.config.dto.AreaDTO;
import com.bcgogo.config.dto.ShopBusinessDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.dto.ShopUnitDTO;
import com.bcgogo.config.model.Business;
import com.bcgogo.config.model.Shop;
import com.bcgogo.config.service.IAgentProductService;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IShopService;
import com.bcgogo.config.upyun.UpYunManager;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.constant.LogicResource;
import com.bcgogo.enums.shop.*;
import com.bcgogo.enums.user.ResourceType;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.ContactDTO;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.UserDTO;
import com.bcgogo.user.dto.permission.ShopVersionDTO;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.permission.IPrivilegeService;
import com.bcgogo.user.service.permission.IShopVersionService;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.*;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: MZDong
 * Date: 11-10-11
 * Time: 下午2:57
 */

// ToDo: handle exception.
@Controller
@RequestMapping("/shop.do")
public class ConfigController{
  private static final Logger LOG = LoggerFactory.getLogger(ConfigController.class);
  protected static final String registerSuccess = "success";
  protected static final String registerFailure = "error";
  protected static final String result = "resu";
  private static final String REGISTER_UPDATE= "regist/registerUpdate";
  private static final String REGISTER_SALES = "regist/salesRegister";
  private static final String REGISTER= "regist/register";

  @RequestMapping(params = "method=shoplist")
  public String shopList(ModelMap model) {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    List<Shop> shopList = configService.getShop();
    model.addAttribute("shopList", shopList);
    return "/config/shop";
  }

  @RequestMapping(params = "method=modifyshop")
  public String getShopById(ModelMap model, @RequestParam("shopId") String shopId) {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    ShopDTO shopDTO = configService.getShopById(Long.parseLong(shopId));
    model.addAttribute("shopDTO", shopDTO);
    return "/config/saveshop";
  }

  //deactivateshop
  @RequestMapping(params = "method=deactivateshop")
  public String deactivateShop(ModelMap model, @RequestParam("shopId") String shopId) {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    try {
      configService.deactivateShop(Long.parseLong(shopId));
    } catch (BcgogoException e) {
      LOG.debug("method=deactivateshop");
      LOG.debug("shopId:" + shopId);
    }
    return shopList(model);
  }

   //获取用户单位
  @RequestMapping(params = "method=validateShopRegBasicInfo")
  @ResponseBody
  public Object validateShopRegBasicInfo(HttpServletRequest request, HttpServletResponse response, Long customerId) {
    Result result = new Result();
    Long shopId = null;
    IConfigService configService = ServiceManager.getService(IConfigService.class);
	  try {
	     shopId = WebUtil.getShopId(request);
      result = configService.validateShopRegBasicInfo(shopId,customerId);
		  return result;
	  } catch (Exception e) {
      LOG.error("method=validateShopRegBasicInfo:shopId:{},customerId:" + customerId + e.getMessage(), shopId, e);
      return new Result("网络异常",false);
	  }
  }

  //bcgogo 业务员注册目前还走该逻辑，批发商店铺已经不走这个逻辑了
  @RequestMapping(params = "method=shopregbasicinfo")
  public String ShopRegBasicInfo(ModelMap model, HttpServletRequest request) {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    IShopVersionService shopVersionService = ServiceManager.getService(IShopVersionService.class);
    try {
      Long userId =  WebUtil.getUserId(request);
      Long shopId = WebUtil.getShopId(request);
      ShopVersionDTO shopVersionDTO = WebUtil.getShopVersion(request);
      Long toRegisterShopVersionId = NumberUtil.longValue(request.getParameter("shopVersionId"));
      model.addAttribute("toRegisterShopVersionId",toRegisterShopVersionId);
      String commonShopVersionsToRegister = configService.getConfig("CommonShopVersionsToRegister", ShopConstant.BC_SHOP_ID);
      String wholesalerShopVersionsToRegister = configService.getConfig("WholesalerShopVersionsToRegister", ShopConstant.BC_SHOP_ID);
      //属于普通版的分类
      String commonShopVersionIds = configService.getConfig("CommonShopVersions", ShopConstant.BC_SHOP_ID);
      //属于批发商版的分类

      String wholesalerShopVersionIds = configService.getConfig("WholesalerShopVersions", ShopConstant.BC_SHOP_ID);
      List<ShopVersionDTO> shopVersionDTOList = new ArrayList<ShopVersionDTO>();
      List<ShopVersionDTO> allShopVersions = shopVersionService.getAllShopVersion();
      Long[] supplierShopVersionToRegister = NumberUtil.getSameIds(NumberUtil.parseLongValuesToArray(wholesalerShopVersionsToRegister, ",")
             ,NumberUtil.parseLongValuesToArray(wholesalerShopVersionIds, ","));
      shopVersionDTOList.addAll(shopVersionService.getShopVersionByIds(allShopVersions,supplierShopVersionToRegister));

      Long[] customerShopVersionToRegister = NumberUtil.getSameIds(NumberUtil.parseLongValuesToArray(commonShopVersionsToRegister, ",")
      ,NumberUtil.parseLongValuesToArray(commonShopVersionIds, ","));
      shopVersionDTOList.addAll(shopVersionService.getShopVersionByIds(allShopVersions,customerShopVersionToRegister));

      String fourSShopVersionIds = configService.getConfig("fourSShopVersions", ShopConstant.BC_SHOP_ID);
      Long[] fourSShopVersionToRegister = NumberUtil.getSameIds(NumberUtil.parseLongValuesToArray(fourSShopVersionIds, ",")
          , NumberUtil.parseLongValuesToArray(fourSShopVersionIds, ","));
      List<ShopVersionDTO> fourSShopVersions = shopVersionService.getShopVersionByIds(allShopVersions, fourSShopVersionToRegister);
      if (CollectionUtils.isNotEmpty(fourSShopVersions)) {
        shopVersionDTOList.addAll(fourSShopVersions);
      }
      model.addAttribute("shopVersionDTOList", shopVersionDTOList);

      List<ShopVersionDTO> toRegisterShops = shopVersionService.getShopVersionByIds(allShopVersions,toRegisterShopVersionId);
      ShopVersionDTO toRegisterShopVersionDTO = CollectionUtil.getFirst(toRegisterShops);
      if(toRegisterShopVersionDTO != null){
        model.addAttribute("toRegisterPrice", toRegisterShopVersionDTO.getSoftPrice());
      }

      //要注册的shop种类， 目前分成 汽修版CUSTOMER，和汽配版SUPPLIER两种
      if (toRegisterShopVersionId != null) {
        model.addAttribute("toRegisterShopVersionId", toRegisterShopVersionId);
        if(wholesalerShopVersionIds.contains(toRegisterShopVersionId.toString())){
          model.addAttribute("shopType", ShopType.SHOP_AUTO_PARTS);
        }else  {
          model.addAttribute("shopType",ShopType.SHOP_AUTO_REPAIR);
        }
      }else{
        model.addAttribute("shopType",ShopType.SHOP_AUTO_REPAIR);
      }
      model.put("registerType",request.getParameter("paramRegisterType"));
      IUserService userService = ServiceManager.getService(IUserService.class);
      UserDTO userDTO = userService.getUserByUserId(userId);

      //根据URL传递的customerId获取部分客户信息，带入到注册页面
      Long customerId = NumberUtil.longValue(request.getParameter("customerId"));
      if (customerId != null) {
        CustomerDTO customerDTO = userService.getCustomerById(customerId);
        List<ContactDTO> contactDTOs = new ArrayList<ContactDTO>();
        if(customerDTO!=null&&customerDTO.hasValidContact()){
          boolean hasMainContact = false;
          for(ContactDTO contactDTO: customerDTO.getContacts()){
            if(contactDTO!=null && contactDTO.getIsMainContact()==1){
              hasMainContact = true;
              break;
            }
          }
          for (int i = 0; i < customerDTO.getContacts().length; i++) {
            ContactDTO contactDTO = customerDTO.getContacts()[i];
            if (contactDTO == null) {
              continue;
            }
            if (!hasMainContact ) {
              hasMainContact = true;
              contactDTO.setIsMainContact(1);
            }
            contactDTO.setShopId(null);
            contactDTO.setId(null);
            contactDTO.setCustomerId(null);
            contactDTO.setSupplierId(null);
            if(contactDTO.getIsMainContact() == 1){
              contactDTOs.add(contactDTO);
            }
          }
          for (int i = 0; i < customerDTO.getContacts().length; i++) {
            ContactDTO contactDTO = customerDTO.getContacts()[i];
            if (contactDTO == null) {
              continue;
            }
            if (contactDTO.getIsMainContact() != 1) {
              contactDTOs.add(contactDTO);
            }
          }
          customerDTO.setContacts(contactDTOs.toArray(new ContactDTO[contactDTOs.size()]));
        }else{
          ContactDTO contactDTO = new ContactDTO();
          contactDTO.setIsMainContact(1);
          ContactDTO[] contacts = new ContactDTO[]{contactDTO};
          customerDTO.setContacts(contacts);
        }
        model.addAttribute("customerDTO", customerDTO);
        model.addAttribute("contacts", customerDTO.getContacts());

      }else{
        ContactDTO contactDTO = new ContactDTO();
        contactDTO.setIsMainContact(1);
        ContactDTO[] contacts = new ContactDTO[]{contactDTO};
        model.addAttribute("contacts", contacts);
      }
      model.addAttribute("userDTO", userDTO);

      //ToDo: need to use cache. No need to be accurate.
      model.addAttribute("shopCount", (ServiceManager.getService(IConfigService.class).countShop() + 80L));
      model.addAttribute("upYunFileDTO", UpYunManager.getInstance().generateDefaultUpYunFileDTO(ShopConstant.BC_SHOP_ID));
      model.put("agentProducts",ServiceManager.getService(IAgentProductService.class).getAgentProductDTO(ShopConstant.BC_SHOP_ID));
      model.put("isWholesalerVersion",ConfigUtils.isWholesalerVersion(toRegisterShopVersionId));
      model.put("fourSShopVersions",ConfigUtils.getFourSShopVersions());
      return REGISTER;

//      if (shopVersionDTO.getName().contains("BCGOGO_SHOP")) {
//        return REGISTER_SALES;
//      } else {
//        return REGISTER_UPDATE;
//      }
    } catch (Exception e) {
      LOG.debug("method=shopregbasicinfo");
      LOG.error(e.getMessage(), e);
      return "/web";
    }
  }

  @RequestMapping(params = "method=shopbusinessinfo")
  public String ShopBusinessInfo(ModelMap model, HttpServletRequest request) {
    ShopDTO shopDTO = new ShopDTO();

    shopDTO.setName(request.getParameter("name"));
    String shopVersionId = request.getParameter("shopVersionId");
    if (StringUtils.isBlank(shopVersionId)) {
      LOG.error(request.getSession().getAttribute("shopId") + " register error,shop type is empty. ");
    }
     shopDTO.setShopVersionId(Long.valueOf(shopVersionId));

    //
    if (!StringUtil.isEmpty(request.getParameter("shortname"))) {
      shopDTO.setShortname(request.getParameter("shortname"));
    } else {
      shopDTO.setShortname(StringUtil.subString(shopDTO.getName(), 0, 50));
    }

    if (request.getParameter("licencePlate") != null) {
      shopDTO.setLicencePlate(request.getParameter("licencePlate"));
    }
    //
    if (request.getParameter("areaId") != null) {
      shopDTO.setAreaId(Long.parseLong(request.getParameter("areaId")));
    } else {
      shopDTO.setAreaId(0L);
    }

    //
    if (request.getParameter("address") != null && !"".equals(request.getParameter("address").trim())) {
      shopDTO.setAddress(request.getParameter("address"));
    }

    if(StringUtils.isNotBlank(request.getParameter("province"))){
      shopDTO.setProvince(NumberUtil.longValue(request.getParameter("province")));
    }
    if(StringUtils.isNotBlank(request.getParameter("city"))){
      shopDTO.setCity(NumberUtil.longValue(request.getParameter("city")));
    }
    if(StringUtils.isNotBlank(request.getParameter("region"))){
      shopDTO.setRegion(NumberUtil.longValue(request.getParameter("region")));
    }
    shopDTO.setLegalRep(request.getParameter("owner"));
    shopDTO.setMobile(request.getParameter("mobile"));

    if (request.getParameter("landLine") != null && !"".equals(request.getParameter("landLine").trim())) {
      shopDTO.setLandline(request.getParameter("landLine"));
    }

    if (request.getParameter("storeManager") != null && !"".equals(request.getParameter("storeManager").trim())) {
      shopDTO.setStoreManager(request.getParameter("storeManager"));
    }

    if (request.getParameter("storeManagerMobile") != null && !"".equals(request.getParameter("storeManagerMobile").trim())) {
      shopDTO.setStoreManagerMobile(request.getParameter("storeManagerMobile"));
    }

    if (request.getParameter("qq") != null && !"".equals(request.getParameter("qq").trim())) {
      shopDTO.setQq(request.getParameter("qq"));
    }

    if (request.getParameter("email") != null && !"".equals(request.getParameter("email").trim())) {
      shopDTO.setEmail(request.getParameter("email"));
    }
    shopDTO.setAgent(request.getParameter("agent"));
    shopDTO.setAgentId(request.getParameter("agentId"));
    shopDTO.setAgentMobile(request.getParameter("agentMobile"));
    shopDTO.setSoftPrice(Double.parseDouble(request.getParameter("softPrice")));

    request.getSession().setAttribute("regShop", shopDTO);

    String customerIdStr = request.getParameter("customerId");
    request.setAttribute("customerId",customerIdStr);

    model.addAttribute("shopCount", (ServiceManager.getService(IConfigService.class).countShop() + 80L));

    return "/config/shopBusinessInfo";
  }

  //业务员注册，老页面
  @RequestMapping(params = "method=saveshop")
  public void SaveShop(ModelMap model, HttpServletRequest request, HttpServletResponse response) throws IOException {
    try {
    LOG.debug("method=saveshop");
    LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    String jsonStr = "";
    StringBuffer sb = new StringBuffer("");
    ShopDTO shopDTO = (ShopDTO) request.getSession().getAttribute("regShop");
      if(shopDTO == null){
        throw new BcgogoException("session中找不到注册店面数据，店面注册失败！");
      }

      if (request instanceof MultipartHttpServletRequest) {
        try {
      //文件上传到数据库里
      MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;

      MultipartFile  multipartFile = multipartRequest.getFile("input_fileLoad");
          if (multipartFile != null && !multipartFile.isEmpty()) {
      InputStream is = multipartFile.getInputStream();
            BufferedImage tag = UploadImg.getResizeImg(is, 100, 100, true);

              byte[] studentPhotoData = UploadImg.getBytesOfIma(tag);
              if (studentPhotoData.length > 2000000) {
          sb.append("maxOver");
      }
      String multipartFileToString = multipartFile.getOriginalFilename();
      shopDTO.setPhoto(multipartFileToString);
      shopDTO.setAttachment(studentPhotoData);
    }
        } catch (Exception e) {
          LOG.error("ConfigController.java");
          LOG.error("method=saveshop");
          LOG.error("注册时保存图片失败");
          LOG.error(e.getMessage(),e);
        }
      }
    //修改到此处
    if (request.getParameter("operationMode") != null && !"".equals(request.getParameter("operationMode").trim())) {
      shopDTO.setOperationMode(request.getParameter("operationMode"));
    }

    if (request.getParameter("businessHours") != null && !"".equals(request.getParameter("businessHours").trim())) {
      shopDTO.setBusinessHours(request.getParameter("businessHours"));
    }

    if (request.getParameter("established") != null && !"".equals(request.getParameter("established").trim())) {
      //把时间转换成长整型数字
      shopDTO.setEstablished(Timestamp.valueOf(request.getParameter("established") + " 00:00:00").getTime());
    }

    if (request.getParameter("qualification") != null && !"".equals(request.getParameter("qualification").trim())) {
      shopDTO.setQualification(request.getParameter("qualification"));
    }

    if (request.getParameter("personnel") != null && !"".equals(request.getParameter("personnel").trim())) {
      shopDTO.setPersonnel(request.getParameter("personnel"));
    }

    if (request.getParameter("area") != null && !"".equals(request.getParameter("area").trim())) {
      shopDTO.setArea(request.getParameter("area"));
    }

    if (request.getParameter("businessScope") != null && !"".equals(request.getParameter("businessScope").trim())) {
      shopDTO.setBusinessScope(request.getParameter("businessScope"));
    }

    if (request.getParameter("relatedBusiness") != null && !"".equals(request.getParameter("relatedBusiness").trim())) {
      shopDTO.setRelatedBusiness(request.getParameter("relatedBusiness"));
    }

    if (request.getParameter("feature") != null && !"".equals(request.getParameter("feature").trim())) {
      shopDTO.setFeature(request.getParameter("feature"));
    }

    if (request.getParameter("memo") != null && !"".equals(request.getParameter("memo").trim())) {
      shopDTO.setMemo(request.getParameter("memo"));
    }
    // 如果不是由bcgogo业务员注册，则记下批发商的shopId
    if (!ServiceManager.getService(IPrivilegeService.class).verifierUserGroupResource(WebUtil.getShopId(request),
        WebUtil.getUserGroupId(request), ResourceType.logic, LogicResource.SALE_LOGIN)) {
      shopDTO.setWholesalerShopId(WebUtil.getShopId(request));
    }
    try {
      shopDTO.setShopStatus(ShopStatus.CHECK_PENDING);
      shopDTO.setShopState(ShopState.ACTIVE);
      shopDTO.setRegisterType(RegisterType.SALESMAN_REGISTER);
      shopDTO.setSubmitApplicationDate(System.currentTimeMillis());
      // 关联客户上限
      if (ConfigUtils.isWholesalerVersion(WebUtil.getShopVersionId(request))) {
        shopDTO.setShopLevel(ShopLevel.PRIMARY_WHOLESALER);
      }
      if (shopDTO.getShopKind() == null) {
        shopDTO.setShopKind(ShopKind.OFFICIAL);
      }
      shopDTO.setAgentDBId(WebUtil.getUserId(request));
      shopDTO = ServiceManager.getService(IShopService.class).createShop(shopDTO);

      if (shopDTO.getId() != null) {

        String customerIdStr = request.getParameter("customerId");
        Long customerId = null;
        if(StringUtil.isNotEmpty(customerIdStr)){
          //保存店铺与客户的临时关联关系
          customerId = Long.parseLong(customerIdStr);
          configService.saveShopCustomerRelation(shopDTO.getId(),customerId);
        }

        List<ShopBusinessDTO> shopBusinessDTOList = new ArrayList<ShopBusinessDTO>();

        //shopBusinessDTO.setBusinessId(shopDTO.getId());
        if (request.getParameter("busScope") != null && !"".equals(request.getParameter("busScope").trim())) {
          String busScope = request.getParameter("busScope");
          String[] strings = busScope.split(",");
          for (String str : strings) {
            ShopBusinessDTO shopBusinessDTO = new ShopBusinessDTO();
            shopBusinessDTO.setShopId(shopDTO.getId());
            if (str.length() > 2) {
              shopBusinessDTO.setBusinessId(Long.parseLong(str.substring(0, 2)));
              shopBusinessDTO.setMemo(str.substring(2));
            } else {
              shopBusinessDTO.setBusinessId(Long.parseLong(str));
            }
            shopBusinessDTOList.add(shopBusinessDTO);
          }
        }

        for (ShopBusinessDTO shopBusinessDTO : shopBusinessDTOList) {
          configService.createShopBusiness(shopBusinessDTO);
        }
      }
    } catch (Exception e) {
      LOG.debug("method=saveshop");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }

      if(StringUtil.isEmpty(sb.toString())){
        sb.append(registerSuccess);
              }

      Map<String,String> jsonMap = new HashMap();
      jsonMap.put(result,sb.toString());
      jsonStr = JsonUtil.mapToJson(jsonMap);
      model.addAttribute("jsonStr", jsonStr);
      PrintWriter printWriter = response.getWriter();
      response.setCharacterEncoding("UTF-8");
      printWriter.write(jsonStr);
      printWriter.close();
    } catch (Exception e) {
      LOG.error("ConfigController.java, method=saveshop");
      LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error("店面注册失败");
      LOG.error(e.getMessage(),e);
      PrintWriter printWriter = response.getWriter();
      response.setCharacterEncoding("UTF-8");
      Map<String,String> jsonMap = new HashMap();
      jsonMap.put(result,registerFailure);
      printWriter.write(JsonUtil.mapToJson(jsonMap));
      model.addAttribute("jsonStr", JsonUtil.mapToJson(jsonMap));
      printWriter.close();
  }
  }


  //shopBusContent --经营内容
  @RequestMapping(params = "method=shopbuscontent")
  public String ShopBusContent
  (ModelMap
       model) {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    List<Business> businessDTOList = configService.getBusinessList("2");
    model.addAttribute("businessDTOList2", businessDTOList);
    model.addAttribute("businessDTOList10", configService.getBusinessList("10"));
    model.addAttribute("businessDTOList34", configService.getBusinessList("34"));
    return "/config/shopBusContent";
  }

  //shopBusTime --营业时间
  @RequestMapping(params = "method=shopbustime")
  public String ShopBusTime
  (ModelMap
       model) {
    return "/config/shopBusTime";
  }

  //shopOperateMode --经营方式
  @RequestMapping(params = "method=shopoperatemode")
  public String ShopOperateMode
  (ModelMap
       model) {
    return "/config/shopOperateMode";
  }

  //shopRelatedBus --相关业务
  @RequestMapping(params = "method=shoprelatedbus")
  public String ShopRelatedBus
  (ModelMap
       model) {
    return "/config/shopRelatedBus";
  }

  //shopStoreCharacter --店面特色
  @RequestMapping(params = "method=shopstorecharacter")
  public String ShopStoreCharacter
  (ModelMap
       model) {
    return "/config/shopStoreCharacter";
  }

  @RequestMapping(params = "method=shopagreement")
  public String ShopAgreement(ModelMap model) {
    return "/config/shopAgreement";
  }

  //读取地址
  @RequestMapping(params = "method=selectarea")
  @ResponseBody
  public List SelectArea(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
    try {
      IConfigService configService = ServiceManager.getService(IConfigService.class);
      String parentNo = request.getParameter("parentNo");
      List<AreaDTO> areaList = configService.getChildAreaDTOList(NumberUtil.longValue(parentNo));
      return areaList;
    } catch (Exception e) {
      LOG.debug("method=selectarea");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      return null;
    }

  }

  //一次性读取地区信息，到二级市
  @RequestMapping(params = "method=getAllAreaToCity")
  @ResponseBody
  public List getAllAreaToCity(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
    try {
      IConfigService configService = ServiceManager.getService(IConfigService.class);
      List<AreaDTO> provinceList = configService.getChildAreaDTOList(1l);
      if(CollectionUtil.isNotEmpty(provinceList)){
        for(AreaDTO areaDTO:provinceList){
          areaDTO.setChildAreaDTOList(configService.getChildAreaDTOList(areaDTO.getNo()));
        }
      }
      return provinceList;
    } catch (Exception e) {
      LOG.error(e.getMessage(),e);
      return null;
    }
  }

 
//  @RequestMapping(params = "method=getAreaBySort")
//  @ResponseBody
//  public Object getAreaBySort(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
//    try {
//      IConfigService configService = ServiceManager.getService(IConfigService.class);
//      List<AreaDTO> provinceList = configService.getChildAreaDTOList(1l);
//      Map<String,List<AreaDTO>> areaDTOMap=new HashMap<String, List<AreaDTO>>();
//      if(CollectionUtil.isNotEmpty(provinceList)){
//        //将省areaDTO按拼音分类
//        for(AreaDTO areaDTO:provinceList){
//          String fl= PinyinUtil.getFirstLetter(areaDTO.getName()).toUpperCase();
//          List<AreaDTO> areaDTOList=areaDTOMap.get(fl);
//          if(CollectionUtil.isEmpty(areaDTOList)){
//            areaDTOList=new ArrayList<AreaDTO>();
//            areaDTOMap.put(fl,areaDTOList);
//          }
//          areaDTOList.add(areaDTO);
//        }
//        //组装市areaDTO
//        for(List<AreaDTO> areaDTOs:areaDTOMap.values()){
//          for(AreaDTO areaDTO:areaDTOs){
//            areaDTO.setChildAreaDTOList(configService.getChildAreaDTOList(areaDTO.getNo()));
//          }
//        }
//      }
//      List arrayList = new ArrayList(areaDTOMap.entrySet());
//      Collections.sort(arrayList, new Comparator() {
//        public int compare(Object o1, Object o2) {
//          Map.Entry obj1 = (Map.Entry) o1;
//          Map.Entry obj2 = (Map.Entry) o2;
//          return (obj1.getKey()).toString().compareTo(obj2.getKey().toString());
//        }
//      });
//      return arrayList;
//    } catch (Exception e) {
//      LOG.error(e.getMessage(),e);
//      return null;
//    }
//  }

  //检测店面名称是否重复
  @RequestMapping(params = "method=checkshopname")
  public void checkShopName(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
    IConfigService configService = ServiceManager.getService(IConfigService.class);

    if (request.getParameter("name") == null || request.getParameter("name").trim().isEmpty())
      return;

    ShopDTO shopDTO = configService.getShopByName(request.getParameter("name"));

    try {
      PrintWriter out = response.getWriter();
      if (shopDTO != null) {
        out.write("\"false\"");
      } else {
        out.write("\"true\"");
      }

      out.close();
    } catch (Exception e) {
      LOG.debug("method=checkshopname");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
    }

  }

  //检查店面管理员手机号码 重复
  @RequestMapping(params = "method=checkStoreManagerMobile")
  public void checkStoreManagerMobile(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
    IUserService userService = ServiceManager.getService(IUserService.class);
    if (request.getParameter("mobile") == null || request.getParameter("mobile").trim().isEmpty())
      return;
    List<UserDTO> userDTOs = userService.getUserByMobile(request.getParameter("mobile"));
    String jsonStr = "";
    try {
      PrintWriter out = response.getWriter();
      if (userDTOs.size() > 0) {
        jsonStr = "\"true\"";
      } else {
        jsonStr = "\"false\"";
      }
      model.addAttribute("jsonStr", jsonStr);
      out.write(jsonStr);
      out.close();
    } catch (Exception e) {
    }

  }


  //检测店面名称是否重复
  @RequestMapping(params = "method=getname")
  public void getName(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
    //IUserService userService = ServiceManager.getService(IUserService.class);

    /*request.getSession(true).setAttribute("shopId", user.getShopId());
    request.getSession(true).setAttribute("shopName", shopDTO.getName());
    request.getSession(true).setAttribute("userId", user.getId());
    request.getSession(true).setAttribute("userName", user.getName());*/
    String shopName="";
    String userName ="";
    if(request.getSession().getAttribute("shopId")!=null)
    {
      ShopDTO shopDTO=ServiceManager.getService(IConfigService.class).getShopById(Long.parseLong(request.getSession().getAttribute("shopId").toString()));
      if(shopDTO!=null)
      {
           shopName = shopDTO.getName();
      }

    }
    if(request.getSession().getAttribute("userId")!=null)
    {
       UserDTO userDTO=ServiceManager.getService(IUserService.class).getUserByUserId(Long.parseLong(request.getSession().getAttribute("userId").toString()));
       if(userDTO!=null)
       {
              userName = userDTO.getName();
       }
    }
    try {
      PrintWriter out = response.getWriter();
      out.write("new Object({shopName:\"" + shopName + "\",userName:\"" + userName + "\"})");
      out.close();
    } catch (Exception e) {
      LOG.debug("method=getname");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
    }
  }

  private static String revision = null;

  public static String getBuildVersion() {
    if (CommonUtil.isDevMode()) {
      return "";
    }
    ResourceLoader resourceLoader = new DefaultResourceLoader();
    Resource url = resourceLoader.getResource("version.properties");
    if (revision == null) {
      synchronized (result){
        revision = "";
        try {
          String versionNo = StringUtil.convertinputStreamToString(url.getInputStream()).trim();
          if ("${buildNumber}".equals(versionNo)) {
            revision = "";
          } else {
            revision = "-min-" + versionNo;
          }

        } catch (Exception e) {
          LOG.error(e.getMessage(), e);
        }
      }
    }

    return revision;
  }

  //获取用户单位
  @RequestMapping(params = "method=getShopUnit")
  @ResponseBody
  public Object getShopUnit(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
	  Map<String, Object> returnMap = new HashMap<String, Object>();
	  try {
		  IConfigService configService = ServiceManager.getService(IConfigService.class);
		  Long shopId = WebUtil.getShopId(request);
		  List<ShopUnitDTO> shopUnitDTOs = configService.getShopUnit(shopId);
		  if (CollectionUtils.isEmpty(shopUnitDTOs)) {
			  shopUnitDTOs = configService.initShopUnit(shopId);
		  }
		  String jsonStr = "";

		  if (shopUnitDTOs != null && shopUnitDTOs.size() > 0) {
			  returnMap.put("shopUnitDTOs", shopUnitDTOs);
			  returnMap.put("shopUnitStatus", "true");
		  } else {
			  returnMap.put("shopUnitStatus", "false");
		  }
		  return returnMap;
	  } catch (Exception e) {
		  LOG.debug("method=getname" + "shopId:" + request.getSession().getAttribute("shopId") + ",userId:"
				            + request.getSession().getAttribute("userId") + e.getMessage());
		  returnMap.put("shopUnitStatus", "false");
		  return returnMap;
	  }
  }

  @RequestMapping(params = "method=getCurrentSysTime")
  @ResponseBody
  public Long getCurrentSysTime(HttpServletRequest request){
    return System.currentTimeMillis();
  }

  @RequestMapping(params = "method=companyProfile")
  public String companyProfile(ModelMap model) {
    return "/common/companyProfile";
  }

  @RequestMapping(params = "method=getAllServiceCategoryLeaf")
  @ResponseBody
  public Object getAllServiceCategoryLeaf(HttpServletRequest request,ModelMap model) {
    return  ServiceCategoryCache.getAllTreeLeafNode();
  }



}