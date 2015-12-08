package com.bcgogo.autoaccessoryonline;

import com.bcgogo.common.Result;
import com.bcgogo.common.WebUtil;
import com.bcgogo.config.cache.ServiceCategoryCache;
import com.bcgogo.config.dto.AreaDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.dto.image.DataImageRelationDTO;
import com.bcgogo.config.dto.image.ImageInfoDTO;
import com.bcgogo.config.model.Shop;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IServiceCategoryService;
import com.bcgogo.config.service.IShopService;
import com.bcgogo.config.service.image.IImageService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.enums.DeletedType;
import com.bcgogo.enums.config.*;
import com.bcgogo.config.service.image.IImageService;
import com.bcgogo.config.upyun.UpYunManager;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.enums.config.ImageScene;
import com.bcgogo.notification.service.ISmsService;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.dto.ShopRegisterProductDTO;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.product.standardVehicleBrandModel.ShopVehicleBrandModelDTO;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.supplierComment.CommentStatDTO;
import com.bcgogo.txn.service.recommend.IPreciseRecommendService;
import com.bcgogo.txn.service.solr.IShopSolrWriterService;
import com.bcgogo.txn.service.supplierComment.ISupplierCommentService;
import com.bcgogo.user.dto.AccidentSpecialistDTO;
import com.bcgogo.user.service.IRelatedShopUpdateService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.wx.IWXUserService;
import com.bcgogo.user.service.wx.WXHelper;
import com.bcgogo.utils.*;
import com.bcgogo.wx.WXConstant;
import com.bcgogo.wx.qr.QRScene;
import com.bcgogo.wx.qr.WXQRCodeDTO;
import com.bcgogo.wx.user.WXUserDTO;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-8-7
 * Time: 下午1:33
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/shopData.do")
public class ShopDataController {
  private static final Logger LOG = LoggerFactory.getLogger(ShopDataController.class);
  private static final String MANAGE_SHOP_DATA = "redirect:shopData.do?method=toManageShopData";

  @RequestMapping(params = "method=toManageShopData")
  public String toManageShopData(ModelMap modelMap, HttpServletRequest request) {
    Long shopId = WebUtil.getShopId(request);
    try {
      if (shopId == null) throw new Exception("shop id is null!");
      modelMap.addAttribute("provinces", ServiceManager.getService(IConfigService.class).getChildAreaDTOList(1l));
      modelMap.addAttribute("upYunFileDTO", UpYunManager.getInstance().generateDefaultUpYunFileDTO(shopId));
      modelMap.put("isWholesalerVersion", ConfigUtils.isWholesalerVersion(WebUtil.getShopVersionId(request)));
      IWXUserService userService = ServiceManager.getService(IWXUserService.class);
      WXQRCodeDTO qrCodeDTO = userService.getUnExpireWXQRCodeDTOByShopId(shopId, QRScene.ACCIDENT);
      if (qrCodeDTO == null) {
        String publicNo = ServiceManager.getService(IConfigService.class).getConfig("MIRROR_PUBLIC_NO", ShopConstant.BC_SHOP_ID);
        qrCodeDTO = userService.createTempQRCode(publicNo, shopId, QRScene.ACCIDENT);
      }
      modelMap.put("qr_code_show_url", WXConstant.URL_SHOW_QR_CODE.replace("{TICKET}", qrCodeDTO.getTicket()));
    } catch (Exception e) {
      LOG.debug("/shopData.do");
      LOG.debug("method=toManageShopData");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }
    return "/autoaccessoryonline/shopData/manageShopData";
  }

  @RequestMapping(params = "method=sendInActiveLocateStatus")
  @ResponseBody
  public Object sendInActiveLocateStatus(HttpServletRequest request) {
    Long shopId = WebUtil.getShopId(request);
    try {
      ServiceManager.getService(IShopService.class).updateShopGeocode(shopId);
      ServiceManager.getService(IShopSolrWriterService.class).reCreateShopIdSolrIndex(shopId);
      return new Result("发送成功", true);
    } catch (Exception e) {
      LOG.debug("/shopData.do");
      LOG.debug("method=sendInActiveLocateStatus");
      LOG.error(e.getMessage(), e);
      return new Result("发送失败", false);
    }
  }

  @RequestMapping(params = "method=getShopData")
  @ResponseBody
  public Object getShopData(HttpServletRequest request) {
    Long shopId = WebUtil.getShopId(request);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    IPreciseRecommendService preciseRecommendService = ServiceManager.getService(IPreciseRecommendService.class);
    IProductService productService = ServiceManager.getService(IProductService.class);
    Map<Long, ShopDTO> shopDTOs = configService.getShopByShopId(shopId);
    ShopDTO shopDTO = shopDTOs.get(shopId);
    shopDTO.setBusinessScope(shopDTO.fromBusinessScopes());
    shopDTO.resetBusinessScope();
    shopDTO.setOperationMode(shopDTO.fromOperationModes());
    shopDTO.resetOperationMode();

    if (StringUtils.isEmpty(shopDTO.getRegistrationDateStr())) {
      shopDTO.setRegistrationDateStr(shopDTO.getCreationDateStr());
    }
    //事故专员
    IUserService userService = ServiceManager.getService(IUserService.class);
    List<AccidentSpecialistDTO> specialistDTOs = userService.getAccidentSpecialistByOpenId(shopId, null);
    if (CollectionUtil.isNotEmpty(specialistDTOs)) {
      IWXUserService wxUserService = ServiceManager.getService(IWXUserService.class);
      for (AccidentSpecialistDTO specialistDTO : specialistDTOs) {
        WXUserDTO wxUserDTO = wxUserService.getWXUserDTOByOpenId(specialistDTO.getOpenId());
        if (wxUserDTO != null) {
          specialistDTO.setWxName(StringUtil.isEmpty(wxUserDTO.getName()) ? wxUserDTO.getNickname() : wxUserDTO.getName());
          specialistDTO.setWxNickName(wxUserDTO.getNickname());
        }
      }
      shopDTO.setSpecialistDTOs(specialistDTOs.toArray(new AccidentSpecialistDTO[specialistDTOs.size()]));
    }
    //主营车型
    List<ShopVehicleBrandModelDTO> bmDTOs = productService.getShopVehicleBrandModelByShopId(shopId);
    shopDTO.generateShopVehicleBrandModelStr(bmDTOs);
    //营业范围
    Set<Long> shopIdSet = new HashSet<Long>();
    shopIdSet.add(shopDTO.getId());
    Map<Long, String> businessScopeMap = preciseRecommendService.getSecondCategoryByShopId(shopIdSet);
    if (MapUtils.isNotEmpty(businessScopeMap) && StringUtils.isNotEmpty(businessScopeMap.get(shopDTO.getId()))) {
      shopDTO.setBusinessScopeStr(businessScopeMap.get(shopDTO.getId()));
    }
    //主营产品
    List<ProductDTO> productDTOs = productService.getShopRegisterProductList(shopId);
    if (CollectionUtil.isNotEmpty(productDTOs)) {
      shopDTO.setProductDTOs(productDTOs.toArray(new ProductDTO[productDTOs.size()]));
    }

    if (!ConfigUtils.isWholesalerVersion(WebUtil.getShopVersionId(request))) {
      //所有的预约服务二级分类
      shopDTO.setServiceCategory(ServiceCategoryCache.getAllTreeLeafNode());
      //本店面的服务
      String serviceCategoryIdStr = "";
      Map<Long, String> shopServiceCategoryIdNameMap = ServiceManager.getService(IServiceCategoryService.class).getShopServiceCategoryIdNameMap(shopId);
      if (shopServiceCategoryIdNameMap != null && shopServiceCategoryIdNameMap.size() > 0) {
        for (Long serviceCategoryId : shopServiceCategoryIdNameMap.keySet()) {
          serviceCategoryIdStr += serviceCategoryId.toString() + ",";
        }
        shopDTO.setServiceCategoryIdStr(serviceCategoryIdStr.substring(0, serviceCategoryIdStr.length() - 1));
      }

    }

    IImageService imageService = ServiceManager.getService(IImageService.class);
    List<ImageScene> imageSceneList = new ArrayList<ImageScene>();
    imageSceneList.add(ImageScene.SHOP_MANAGE_UPLOAD_IMAGE);
    imageSceneList.add(ImageScene.SHOP_BUSINESS_LICENSE_IMAGE);
    //二维码图片
    imageSceneList.add(ImageScene.SHOP_RQ_IMAGE);
    imageService.addImageToShopDTO(imageSceneList, false, shopDTO);

    return shopDTO;
  }

  @RequestMapping(params = "method=saveShopInfo")
  @ResponseBody
  public Object saveShopInfo(HttpServletRequest request, HttpServletResponse response, ShopDTO dto) {
    StopWatchUtil sw = new StopWatchUtil("saveShopInfo", "start");
    IShopService shopService = ServiceManager.getService(IShopService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    IRelatedShopUpdateService relatedShopUpdateService = ServiceManager.getService(IRelatedShopUpdateService.class);
    Result result = new Result();
    dto.setId(WebUtil.getShopId(request));
    Long shopId = WebUtil.getShopId(request);
    try {
      ShopDTO dbShopDTO = configService.getShopById(WebUtil.getShopId(request));
      sw.stopAndStart("check create task");
      boolean isNeedToCreateTask = relatedShopUpdateService.isNeedToCreateTask(dbShopDTO, dto);
      sw.stopAndStart("save info");
      result = shopService.saveShopInfo(result, WebUtil.getShopId(request), dto);
      sw.stopAndStart("create task");
      if (isNeedToCreateTask) {
        relatedShopUpdateService.createRelatedShopUpdateTask(shopId);
      }
      sw.stopAndStart("solr");
      ServiceManager.getService(IShopSolrWriterService.class).reCreateShopIdSolrIndex(shopId);
      sw.stopAndPrintLog();
      return result;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  @RequestMapping(params = "method=saveShopContacts")
  @ResponseBody
  public Object saveShopContacts(HttpServletRequest request, HttpServletResponse response, ShopDTO dto) {
    IShopService shopService = ServiceManager.getService(IShopService.class);
    Result result = new Result();
    try {
      return shopService.saveShopContacts(result, WebUtil.getShopId(request), dto);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  @RequestMapping(params = "method=saveAccidentSpecialist")
  @ResponseBody
  public Object saveAccidentSpecialist(HttpServletRequest request, HttpServletResponse response, ShopDTO dto) {
    try {
      Long shopId = WebUtil.getShopId(request);
      AccidentSpecialistDTO[] specialistDTOs = dto.getSpecialistDTOs();
      if (ArrayUtil.isEmpty(specialistDTOs)) return new Result(false, "事故专员信息不能为空");
      for (AccidentSpecialistDTO specialistDTO : specialistDTOs) {
        specialistDTO.setShopId(shopId);
        specialistDTO.setDeleted(DeletedType.FALSE);
      }
      IUserService userService = ServiceManager.getService(IUserService.class);
      userService.saveOrUpdateAccidentSpecialist(specialistDTOs);
      return new Result();
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  @RequestMapping(params = "method=unBindAccidentSpecial")
  @ResponseBody
  public Object unBindAccidentSpecial(HttpServletRequest request, HttpServletResponse response, String openId) {
    try {
      Long shopId = WebUtil.getShopId(request);
      IUserService userService = ServiceManager.getService(IUserService.class);
      AccidentSpecialistDTO specialistDTO = CollectionUtil.getFirst(userService.getAccidentSpecialistByOpenId(shopId, openId));
      if (specialistDTO == null) return new Result(false, "事故专员不存在");
      specialistDTO.setDeleted(DeletedType.TRUE);
      userService.saveOrUpdateAccidentSpecialist(specialistDTO);
      return new Result();
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return null;
    }
  }


  @RequestMapping(params = "method=toShopComment")
  public String toShopComment(ModelMap modelMap, HttpServletRequest request) {
    ISupplierCommentService supplierCommentService = ServiceManager.getService(ISupplierCommentService.class);
    try {
      modelMap.put("paramShopId", WebUtil.getShopId(request));
      if (ConfigUtils.isWholesalerVersion(WebUtil.getShopVersionId(request))) {
        return "/autoaccessoryonline/shopData/shopComment";
      } else {
        return "/autoaccessoryonline/shopData/appUserShopComment";
      }

    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return MANAGE_SHOP_DATA;
    }
  }

  @RequestMapping(params = "method=getShopComment")
  @ResponseBody
  public Object getShopComment(HttpServletRequest request, Boolean selShopFlag, Long shopId) {
    try {
      if (!Boolean.FALSE.equals(selShopFlag)) {
        shopId = WebUtil.getShopId(request);
      }
      ShopDTO shopDTO = ServiceManager.getService(IShopService.class).getShopDTOById(shopId);
      if (shopDTO == null) {
        return null;
      }
      shopDTO.setCommentStatDTO(ServiceManager.getService(ISupplierCommentService.class).getShopCommentStat(shopId));
      IImageService imageService = ServiceManager.getService(IImageService.class);
      shopDTO.setLicensed(imageService.isExistDataImageRelation(shopId, ImageType.SHOP_BUSINESS_LICENSE_IMAGE, DataType.SHOP, shopId, 1));
      return shopDTO;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  @RequestMapping(params = "method=saveOwnShopImageRelation")
  @ResponseBody
  public Object saveOwnShopImageRelation(HttpServletRequest request, String[] imagePaths) {
    IImageService imageService = ServiceManager.getService(IImageService.class);
    Long shopId = WebUtil.getShopId(request);
    Result result = new Result();
    try {
      if (shopId == null) throw new Exception("shopId is null!");
      List<DataImageRelationDTO> dataImageRelationDTOList = new ArrayList<DataImageRelationDTO>();
      if (!ArrayUtils.isEmpty(imagePaths)) {
        int i = 0;
        for (String imagePath : imagePaths) {
          if (StringUtils.isNotBlank(imagePath)) {
            DataImageRelationDTO dataImageRelationDTO = new DataImageRelationDTO(shopId, shopId, DataType.SHOP, i == 0 ? ImageType.SHOP_MAIN_IMAGE : ImageType.SHOP_AUXILIARY_IMAGE, i);
            dataImageRelationDTO.setImageInfoDTO(new ImageInfoDTO(shopId, imagePath));
            dataImageRelationDTOList.add(dataImageRelationDTO);
            i++;
          }
        }
      }

      Set<ImageType> imageTypeSet = new HashSet<ImageType>();
      imageTypeSet.add(ImageType.SHOP_AUXILIARY_IMAGE);
      imageTypeSet.add(ImageType.SHOP_MAIN_IMAGE);
      imageService.saveOrUpdateDataImageDTOs(shopId, imageTypeSet, DataType.SHOP, shopId, dataImageRelationDTOList.toArray(new DataImageRelationDTO[dataImageRelationDTOList.size()]));
      return result;
    } catch (Exception e) {
      LOG.error("/shopData.do");
      LOG.error("method=saveOwnShopImageRelation");
      LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
      result.setSuccess(false);
      return result;
    }
  }


  @RequestMapping(params = "method=saveOwnShopBusinessLicenseImageRelation")
  @ResponseBody
  public Object saveOwnShopBusinessLicenseImageRelation(HttpServletRequest request, String shopBusinessLicenseImagePath) {
    IImageService imageService = ServiceManager.getService(IImageService.class);
    Long shopId = WebUtil.getShopId(request);
    Result result = new Result();
    try {
      if (shopId == null) throw new Exception("shopId is null!");
      if (StringUtils.isBlank(shopBusinessLicenseImagePath))
        throw new Exception("shopBusinessLicenseImagePath is null!");
      DataImageRelationDTO dataImageRelationDTO = new DataImageRelationDTO(shopId, shopId, DataType.SHOP, ImageType.SHOP_BUSINESS_LICENSE_IMAGE, 1);
      dataImageRelationDTO.setImageInfoDTO(new ImageInfoDTO(shopId, shopBusinessLicenseImagePath));
      Set<ImageType> imageTypeSet = new HashSet<ImageType>();
      imageTypeSet.add(ImageType.SHOP_BUSINESS_LICENSE_IMAGE);
      imageService.saveOrUpdateDataImageDTOs(shopId, imageTypeSet, DataType.SHOP, shopId, dataImageRelationDTO);
      result.setData(dataImageRelationDTO);
      return result;
    } catch (Exception e) {
      LOG.error("/shopData.do");
      LOG.error("method=saveOwnShopBusinessLicenseImageRelation");
      LOG.error("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
      LOG.error(e.getMessage(), e);
      result.setSuccess(false);
      return result;
    }
  }

  @RequestMapping(params = "method=printShopRQ")
  public String printShopRQ(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
    return "/autoaccessoryonline/shopData/printShopRQ";
  }

  /**
   * 即时生成二维码图片
   *
   * @param model
   * @param request
   * @param response
   * @param size     图片尺寸
   */
  @RequestMapping(params = "method=getShopRqImage")
  public void getShopRqImage(ModelMap model, HttpServletRequest request, HttpServletResponse response, Integer size) {
    Long shopId = WebUtil.getShopId(request);
    if (shopId == null) {
      return;
    }
    if (size == null) {
      size = 300;
    }
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    ShopDTO shopDTO = configService.getShopById(shopId);
    if (shopDTO == null) {
      return;
    }
    BufferedImage image = RQUtil.getRQ(shopDTO.getId().toString() + "," + shopDTO.getName(), size);
    try {
      response.setContentType("image/png");
      response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
      response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
      response.setDateHeader("Expires", 0); // Proxies.
      ServletOutputStream outputStream = response.getOutputStream();
      ImageIO.write(image, "png", outputStream);
      outputStream.flush();
      outputStream.close();
    } catch (IOException e) {
      LOG.error("shopData.do?method=getShopRqImage", e);
    }

  }


}