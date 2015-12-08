package com.bcgogo.admin;

import com.bcgogo.common.WebUtil;
import com.bcgogo.config.dto.AttachmentDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.dto.image.DataImageDetailDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.image.IImageService;
import com.bcgogo.config.util.ImageUtils;
import com.bcgogo.enums.config.AttachmentType;
import com.bcgogo.enums.config.DataType;
import com.bcgogo.enums.config.ImageScene;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.recommend.IPreciseRecommendService;
import com.bcgogo.utils.ArrayUtil;
import com.bcgogo.utils.CollectionUtil;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.*;

/**
 * 本店基本资料专用controller
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-3-19
 * Time: 下午1:50
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/shopBasic.do")
public class ShopBasicController {
  private static final Logger LOG = LoggerFactory.getLogger(ShopBasicController.class);

  /**
   * 获取本店基本资料
   * @param modelMap
   * @param request
   * @param response
   * @return
   */
  @RequestMapping(params = "method=getShopBasicInfo")
  public String getShopBasicInfo(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) {
    Long shopId = WebUtil.getShopId(request);
    if (shopId == null) {
      return "/";
    }
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    IPreciseRecommendService preciseRecommendService = ServiceManager.getService(IPreciseRecommendService.class);
    IProductService productService=ServiceManager.getService(IProductService.class);
    Map<Long, ShopDTO> shopDTOs = configService.getShopByShopId(shopId);
    ShopDTO shopDTO = shopDTOs.get(shopId);
    shopDTO.setBusinessScope(shopDTO.fromBusinessScopes());
    shopDTO.resetBusinessScope();
    shopDTO.setOperationMode(shopDTO.fromOperationModes());
    shopDTO.resetOperationMode();

    if(StringUtils.isEmpty(shopDTO.getRegistrationDateStr())){
      shopDTO.setRegistrationDateStr(shopDTO.getCreationDateStr());
    }
    //营业范围
    Set<Long> shopIdSet = new HashSet<Long>();
    shopIdSet.add(shopDTO.getId());
    Map<Long, String> businessScopeMap = preciseRecommendService.getSecondCategoryByShopId(shopIdSet);
    if (MapUtils.isNotEmpty(businessScopeMap) && StringUtils.isNotEmpty(businessScopeMap.get(shopDTO.getId()))) {
      shopDTO.setBusinessScopeStr(businessScopeMap.get(shopDTO.getId()));
    }
    //主营产品
    List<ProductDTO> productDTOs=productService.getShopRegisterProductList(shopId);
    if(CollectionUtil.isNotEmpty(productDTOs)){
      shopDTO.setProductDTOs(productDTOs.toArray(new ProductDTO[productDTOs.size()]));
    }
    IImageService imageService = ServiceManager.getService(IImageService.class);
    List<ImageScene> imageSceneList = new ArrayList<ImageScene>();
    imageSceneList.add(ImageScene.SHOP_IMAGE_SMALL);
    imageSceneList.add(ImageScene.SHOP_IMAGE_BIG);
    imageSceneList.add(ImageScene.SHOP_IMAGE);
    imageSceneList.add(ImageScene.SHOP_BUSINESS_LICENSE_IMAGE);
    imageService.addImageToShopDTO(imageSceneList,true,shopDTO);
    modelMap.addAttribute("shopDTO", shopDTO);
    return "admin/shopBasic/shopBasicInfo";
  }

  /**
   * 获取店面照片
   * @param shopId
   * @param response
   * @param request
   * @throws IOException
   * @throws SQLException
   */
  @RequestMapping(params = "method=getShopPhotoByShopId")
  public void getShopPhotoByShopId(String shopId, final HttpServletResponse response, HttpServletRequest request) throws IOException, SQLException {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    if ("".equals(shopId) || shopId == null) {
      shopId = String.valueOf(WebUtil.getShopId(request));
      if (shopId == null || "".equals(shopId)) {
      }
    }

    AttachmentDTO attachmentDTO = configService.getAttachmentByShopId(Long.valueOf(shopId), AttachmentType.SHOP_APPEARANCE_PHOTO);
    if (attachmentDTO == null) {
      return;
    }
    byte[] data = attachmentDTO.getContent();


    if (data != null) {
      response.setContentType("image/jpg");
      response.setCharacterEncoding("UTF-8");
      OutputStream outputSream = response.getOutputStream();
      InputStream in = new ByteArrayInputStream(data);
      int len = 0;
      byte[] buf = new byte[1024];
      while ((len = in.read(buf, 0, 1024)) != -1) {
        outputSream.write(buf, 0, len);
      }
      outputSream.close();
    }
  }


  /**
   * 获取营业执照
   * @param shopId
   * @param response
   * @param request
   * @throws IOException
   * @throws SQLException
   */
  @RequestMapping(params = "method=getShopBusinessLicense")
  public void getShopBusinessLicense(String shopId, final HttpServletResponse response, HttpServletRequest request) throws IOException, SQLException {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    if ("".equals(shopId) || shopId == null) {
      shopId = String.valueOf(WebUtil.getShopId(request));
      if (shopId == null || "".equals(shopId)) {
        return;
      }
    }

    AttachmentDTO attachmentDTO = configService.getAttachmentByShopId(Long.valueOf(shopId), AttachmentType.SHOP_BUSINESS_LICENSE_PHOTO);
    if (attachmentDTO == null) {
      return;
    }
    byte[] data = attachmentDTO.getContent();
    if (data != null) {
      response.setContentType("image/jpg");
      response.setCharacterEncoding("UTF-8");
      OutputStream outputSream = response.getOutputStream();
      InputStream in = new ByteArrayInputStream(data);
      int len = 0;
      byte[] buf = new byte[1024];
      while ((len = in.read(buf, 0, 1024)) != -1) {
        outputSream.write(buf, 0, len);
      }
      outputSream.close();
    }
  }

}
