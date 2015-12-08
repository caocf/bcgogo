package com.bcgogo.supplier;

import com.bcgogo.common.Result;
import com.bcgogo.common.WebUtil;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.dto.ShopServiceCategoryDTO;
import com.bcgogo.config.service.IConfigService;

import com.bcgogo.config.service.IServiceCategoryService;
import com.bcgogo.config.service.image.IImageService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.enums.ProductStatus;
import com.bcgogo.search.dto.ProductSearchResultListDTO;
import com.bcgogo.search.dto.SearchConditionDTO;
import com.bcgogo.search.service.product.ISearchProductService;
import com.bcgogo.search.service.product.SearchProductService;
import com.bcgogo.enums.config.ImageScene;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.product.standardVehicleBrandModel.ShopVehicleBrandModelDTO;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.supplierComment.CommentConstant;
import com.bcgogo.txn.dto.supplierComment.CommentStatDTO;
import com.bcgogo.txn.dto.supplierComment.CommentStatDTO;
import com.bcgogo.txn.service.supplierComment.IAppUserCommentService;
import com.bcgogo.txn.service.ShopRelation.IShopRelationService;
import com.bcgogo.txn.service.supplierComment.ISupplierCommentService;

import com.bcgogo.user.dto.ContactDTO;
import com.bcgogo.utils.ArrayUtil;
import com.bcgogo.utils.NumberUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 店铺资料页面
 * User: terry
 * Date: 13-7-30
 * Time: 下午5:56
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/shopMsgDetail.do")
public class ShopMsgDetailController {

  public static final Logger LOG = LoggerFactory.getLogger(ShopMsgDetailController.class);
  private static final String SHOP_MSG_TAB_FLAG = "shopMsgTabFlag";
  private static final List<String> shopMsgDetails = new ArrayList<String>();

  private static final String DEFAULT = "default";
  public static final String COMMENT = "comment";
  public static final String PRODUCT_LIST = "productList";

  static {
    shopMsgDetails.add(DEFAULT);
    shopMsgDetails.add(COMMENT);
    shopMsgDetails.add(PRODUCT_LIST);
  }

  public static final String PARAM_SHOP_ID = "paramShopId";
  public static final String SEARCH_WORD = "searchWord";

  @Autowired
  private ShopProductCategoryHelper shopProductCategoryHelper;
  @Autowired
  private ShopMsgHelper shopMsgHelper;

  @RequestMapping(params = "method=renderShopMsgDetail")
  public String renderShopMsgDetail(HttpServletRequest request, ModelMap model, Boolean fromCustomerPage) {
    ISupplierCommentService supplierCommentService = ServiceManager.getService(ISupplierCommentService.class);
    String paramShopIdStr = request.getParameter(PARAM_SHOP_ID);
    try {
      if (StringUtils.isBlank(paramShopIdStr)) {
        throw new Exception("method=renderShopMsgDetail paramShopId is null");
      }
      Long paramShopId = NumberUtil.longValue(paramShopIdStr);
      Long shopId = WebUtil.getShopId(request);

      String shopMsgDetailFlag = request.getParameter(SHOP_MSG_TAB_FLAG); //页面显示哪个tab的Flag
      if (StringUtils.isBlank(shopMsgDetailFlag)) {
        shopMsgDetailFlag = DEFAULT;
      }
      if (!shopMsgDetails.contains(shopMsgDetailFlag)) {
        shopMsgDetailFlag = DEFAULT;
      }
      model.addAttribute("shopMsgDetailFlag", shopMsgDetailFlag);

      ShopDTO shopDTO = shopMsgHelper.getShopMsgBasic(shopId, paramShopId);
      boolean isQQExist = false;
      if (ArrayUtil.isNotEmpty(shopDTO.getContacts())) {
        for (ContactDTO contactDTO : shopDTO.getContacts()) {
          if (contactDTO != null && StringUtils.isNotBlank(contactDTO.getQq())) {
            isQQExist = true;
          }
        }
      }
      model.addAttribute("isQQExist", isQQExist);
      shopMsgHelper.relateContextSet(request, model, paramShopId, shopId);
      //主营车型
      List<ShopVehicleBrandModelDTO> bmDTOs= ServiceManager.getService(IProductService.class).getShopVehicleBrandModelByShopId(paramShopId);
      shopDTO.generateShopVehicleBrandModelStr(bmDTOs);

      if(!ConfigUtils.isWholesalerVersion(shopDTO.getShopVersionId())) {
        //服务范围
        String serviceCategoryStr = "";
        List<ShopServiceCategoryDTO> shopServiceCategoryDTOs = ServiceManager.getService(IServiceCategoryService.class).getShopServiceCategoryDTOByShopId(paramShopId);
        if(CollectionUtils.isNotEmpty(shopServiceCategoryDTOs)) {
          for(ShopServiceCategoryDTO shopServiceCategoryDTO : shopServiceCategoryDTOs) {
            serviceCategoryStr += shopServiceCategoryDTO.getServiceCategoryName() + ",";
          }
        }
        if(StringUtils.isNotEmpty(serviceCategoryStr)) {
          shopDTO.setServiceCategoryStr(serviceCategoryStr.substring(0,serviceCategoryStr.length() - 1));
        }
      }

      //店铺二维码图片
      IImageService imageService = ServiceManager.getService(IImageService.class);
      List<ImageScene> imageSceneList = new ArrayList<ImageScene>();
      imageSceneList.add(ImageScene.SHOP_RQ_IMAGE);
      imageService.addImageToShopDTO(imageSceneList, false, shopDTO);
      CommentStatDTO supplierCommentStatDTO = supplierCommentService.getCommentStatByShopId(paramShopId);
      if (supplierCommentStatDTO == null) {
        supplierCommentStatDTO = new CommentStatDTO();
      }
      if(!ConfigUtils.isWholesalerVersion(shopDTO.getShopVersionId())) {
        //汽修版本统计综合评分
        if(supplierCommentStatDTO.getTotalScore() != 0 && supplierCommentStatDTO.getRecordAmount() != 0) {
          supplierCommentStatDTO.setAverageScore(NumberUtil.round(supplierCommentStatDTO.getTotalScore() / supplierCommentStatDTO.getRecordAmount(), 2));
        }
        if (supplierCommentStatDTO.getAverageScore() - supplierCommentStatDTO.getAverageScore().intValue() > 0) {
          supplierCommentStatDTO.setTotalScoreSpan(((5 - supplierCommentStatDTO.getAverageScore().intValue() - CommentConstant.SCORE_UNIT) / CommentConstant.SCORE_UNIT) * CommentConstant.SPAN_SIZE);
        } else {
          supplierCommentStatDTO.setTotalScoreSpan(((5 - supplierCommentStatDTO.getAverageScore().intValue()) / CommentConstant.SCORE_UNIT) * CommentConstant.SPAN_SIZE);
        }

      } else {
        supplierCommentStatDTO.calculate();
      }

      model.addAttribute("productIds", request.getParameter("productIds"));
      model.addAttribute("fromSource", request.getParameter("fromSource"));
      model.addAttribute("supplierCommentStatDTO", supplierCommentStatDTO);
      model.addAttribute("shopDTO", shopDTO);
      model.addAttribute("paramShopId", paramShopId);
      model.addAttribute("isWholesalerVersion",ConfigUtils.isWholesalerVersion(shopDTO.getShopVersionId()));
      if(ConfigUtils.isWholesalerVersion(shopDTO.getShopVersionId()) && (fromCustomerPage==null || !fromCustomerPage)){
        return "customer/shopMsgDetailWholesaler";
      }else{
        return "customer/shopMsgDetail";
      }
    } catch (Exception e) {
      LOG.error("supplier.redirectSupplierComment,paramShopId:" + paramShopIdStr);
      LOG.error(e.getMessage(), e);
    }
    return "customer/shopMsgDetailWholesaler";
  }



  @RequestMapping(params = "method=getProductCategoryListSimpleJsonFormat")
  @ResponseBody
  public Object getProductCategoryListSimpleJsonFormat(HttpServletRequest request, Long shopId) {
    Result result = new Result();
    if (shopId == null || shopId == 0L) {
      result.setSuccess(false);
      result.setMsg("paramShopId should not be null or 0L.");
      return result;
    }
    result.setSuccess(true);
    try {
      List<Node> productCategoryList = shopProductCategoryHelper.getSimpleJsonShop2nd3rdProductCategorys(shopId);
      result.setData(productCategoryList);
    } catch (Exception e) {
      LOG.error("getProductCategoryListSimpleJsonFormat", e);
    }
    // for test
//    buildTestJsonDatas(result);
    return result;
  }

  private void buildTestJsonDatas(Result result) {
    List<Node> testDatas = new ArrayList<Node>();
    testDatas.add(new Node(1L, 0L, "父节点1 - 展开", true, true, "1111111111111"));
    testDatas.add(new Node(11L, 1L, "父节点11 - 折叠", true, false, "1111111111112"));
    testDatas.add(new Node(12L, 1L, "父节点12 - 折叠", true, false, "1111111111113"));
    testDatas.add(new Node(13L, 1L, "父节点13 - 折叠", true, true, "1111111111114"));
    testDatas.add(new Node(2L, 0L, "父节点2 - 折叠", true, false, "1111111111115"));
    testDatas.add(new Node(21L, 2L, "父节点21 - 展开", true, true, "1111111111116"));
    testDatas.add(new Node(22L, 2L, "父节点22 - 展开", true, false, "1111111111117"));
    testDatas.add(new Node(23L, 2L, "父节点23 - 展开", true, false, "1111111111118"));
    testDatas.add(new Node(3L, 0L, "父节点3 - 没有子节点", true, false, "1111111111119"));
    result.setData(testDatas);
  }


  @RequestMapping(params = "method=simpleSearch")
  public String productSimpleSearch(HttpServletRequest request, ModelMap modelMap) {
    // 获取查询字段
    String paramShopId = request.getParameter(PARAM_SHOP_ID);
    String searchWorld = request.getParameter(SEARCH_WORD);
    String tradePriceStart = request.getParameter("tradePriceStart");
    String tradePriceEnd = request.getParameter("tradePriceEnd");
    modelMap.addAttribute(SEARCH_WORD, searchWorld);
    modelMap.addAttribute("tradePriceStart", tradePriceStart);
    modelMap.addAttribute("tradePriceEnd", tradePriceEnd);
    modelMap.addAttribute("simpleSearchFlag", "simpleSearch");
    modelMap.addAttribute(PARAM_SHOP_ID, paramShopId);
    return renderShopMsgDetail(request, modelMap, false);
  }


}