package com.bcgogo.autoaccessoryonline;

import com.bcgogo.common.Pager;
import com.bcgogo.common.WebUtil;
import com.bcgogo.config.dto.image.DataImageDetailDTO;
import com.bcgogo.config.service.image.IImageService;
import com.bcgogo.config.util.ImageUtils;
import com.bcgogo.enums.ProductStatus;
import com.bcgogo.enums.config.DataType;
import com.bcgogo.enums.config.ImageScene;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.model.ProductQueryCondition;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.product.service.IPromotionsService;
import com.bcgogo.search.dto.ProductSearchResultListDTO;
import com.bcgogo.search.dto.SearchConditionDTO;
import com.bcgogo.search.service.product.ISearchProductService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.PreBuyOrderDTO;
import com.bcgogo.txn.dto.PromotionsDTO;
import com.bcgogo.txn.dto.QuotedPreBuyOrderDTO;
import com.bcgogo.txn.dto.QuotedPreBuyOrderItemDTO;
import com.bcgogo.txn.dto.remind.OrderCenterDTO;
import com.bcgogo.txn.service.IPreBuyOrderService;
import com.bcgogo.txn.service.remind.IOrderCenterService;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.PromotionsUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * User: terry
 * Date: 13-8-19
 * Time: 上午9:36
 * 我要卖配件
 */
@Controller
@RequestMapping("/preSellMain.do")
public class PreSellMainController {

  private static final Logger LOG = LoggerFactory.getLogger(PreSellMainController.class);

  private static final int START = 0;
  private static final int DEFAULT_LIMIT = 2;
  private static final int CUSTOMER_LIMIT = 6;

  @RequestMapping(params = "method=toPreSellMain")
  public String toPreBuyMain(HttpServletRequest request, ModelMap modelMap) {

    Long shopId = WebUtil.getShopId(request);

    try {
      //    在线销售订单
      //    在线销售退货单
      //    查询订单中心相关
      IOrderCenterService orderCenterService = ServiceManager.getService(IOrderCenterService.class);
      OrderCenterDTO orderCenterDTO = orderCenterService.getOrderCenterStatistics(shopId);
      modelMap.addAttribute("orderCenterDTO", orderCenterDTO);

      //    我的上架商品
      ISearchProductService searchProductService = ServiceManager.getService(ISearchProductService.class);
      IPromotionsService promotionsService = ServiceManager.getService(IPromotionsService.class);
      SearchConditionDTO searchConditionDTO = new SearchConditionDTO();
      searchConditionDTO.setShopId(shopId);
      searchConditionDTO.setSalesStatus(ProductStatus.InSales);
      searchConditionDTO.setSort("last_in_sales_time desc");
      searchConditionDTO.setStartPageNo(1);
      searchConditionDTO.setRows(2);
      searchConditionDTO.setMaxRows(2);
      ProductSearchResultListDTO productSearchResultListDTO = searchProductService.queryProductWithStdQuery(searchConditionDTO);
      List<ProductDTO> productDTOs = productSearchResultListDTO.getProducts();
      if (CollectionUtil.isNotEmpty(productDTOs)){
        for (ProductDTO productDTO:productDTOs){
          List<PromotionsDTO> promotionsDTOs =null;
          Map<Long,ProductDTO> pMap=promotionsService.getProductPromotionDetail(productDTO.getShopId(), productDTO.getProductLocalInfoId());
          if(pMap!=null){
            ProductDTO productDTOTemp=pMap.get(productDTO.getProductLocalInfoId());
            if(productDTOTemp!=null){
              promotionsDTOs=productDTOTemp.getPromotionsDTOs();
            }
          }
          productDTO.setPromotionsDTOs(promotionsDTOs);
          productDTO.setInSalesPriceAfterCal(PromotionsUtils.calculateBargainPrice(promotionsDTOs, productDTO.getInSalesPrice()));
          String[] titles = PromotionsUtils.genPromotionTypesStr(promotionsDTOs);
          productDTO.setPromotionTypesShortStr(titles[0]);
          productDTO.setPromotionTypesStr(titles[1]);
        }
      }
      List<ImageScene> imageSceneList = new ArrayList<ImageScene>();
      imageSceneList.add(ImageScene.PRODUCT_LIST_IMAGE_SMALL);
      ServiceManager.getService(IImageService.class).addImageInfoToProductDTO(imageSceneList,true,productDTOs.toArray(new ProductDTO[productDTOs.size()]));
      modelMap.addAttribute("products", productDTOs);
      modelMap.addAttribute("productInSalesCount", productSearchResultListDTO.getNumFound());

      //    我的报价
      IPreBuyOrderService iPreBuyOrderService = ServiceManager.getService(IPreBuyOrderService.class);
//      Long preBuyOrdersCount = iPreBuyOrderService.countPreBuyOrders(shopId);

      modelMap.addAttribute("ordersFromQuotedPreBuyOrderCount", iPreBuyOrderService.countOrdersFromQuotedPreBuyOrder(shopId));
      Long allPreBuyOrdersCount = iPreBuyOrderService.countQuotedPreBuyOrders(shopId);
      modelMap.addAttribute("allPreBuyOrdersCount",allPreBuyOrdersCount.longValue());
      List<QuotedPreBuyOrderDTO> quotedPreBuyOrderDTOs = iPreBuyOrderService.getQuotedPreBuyOrderDtoList(shopId, 0, 2);
      for (QuotedPreBuyOrderDTO quotedPreBuyOrderDTO : quotedPreBuyOrderDTOs) {
        quotedPreBuyOrderDTO.genIsPurchase();  //　设置是否有报价
      }
      addImageToQuotedPreBuyOrderDTO(quotedPreBuyOrderDTOs);
      modelMap.addAttribute("quotedPreBuyOrders",quotedPreBuyOrderDTOs);
      Map<Long, QuotedPreBuyOrderItemDTO> quotedPrebuyItems = new HashMap<Long, QuotedPreBuyOrderItemDTO>();
      if(CollectionUtils.isNotEmpty(quotedPreBuyOrderDTOs)){
        for(QuotedPreBuyOrderDTO order : quotedPreBuyOrderDTOs){
          if(ArrayUtils.isEmpty(order.getItemDTOs())){
            continue;
          }
          quotedPrebuyItems.put(order.getId(), order.getItemDTOs()[0]);
        }
      }
      modelMap.addAttribute("quotedPreBuyItems", quotedPrebuyItems);

      //    我的客户
      ICustomerService iCustomerService = ServiceManager.getService(ICustomerService.class);
      List<CustomerDTO> customerDTOs = iCustomerService.getRelatedCustomersByShopId(shopId);
      modelMap.addAttribute("customerCount",customerDTOs.size());
      modelMap.addAttribute("customerDTOs", subListForSize2(customerDTOs, CUSTOMER_LIMIT));

      OnlinePurchaseReturnOrderController.setDateToModel(modelMap);
    } catch (Exception e) {
      LOG.error("/preSellMain.do?method=toPreSellMain", e);
    }
    return "autoaccessoryonline/preSellMain";
  }

  /**
   * 需求比较特殊 随便找个有图片的显示在 单据上 而不是 item上
   * @param quotedPreBuyOrderDTOs
   */
  private void addImageToQuotedPreBuyOrderDTO(List<QuotedPreBuyOrderDTO> quotedPreBuyOrderDTOs){
    if(CollectionUtils.isNotEmpty(quotedPreBuyOrderDTOs)){
      IImageService imageService = ServiceManager.getService(IImageService.class);
      List<ImageScene> imageSceneList = new ArrayList<ImageScene>();
      imageSceneList.add(ImageScene.PRODUCT_LIST_IMAGE_SMALL);
      Set<Long> shopIdSet = new HashSet<Long>();
      List<Long> productIdList = new ArrayList<Long>();
      for(QuotedPreBuyOrderDTO quotedPreBuyOrderDTO:quotedPreBuyOrderDTOs){
        if(CollectionUtils.isNotEmpty(quotedPreBuyOrderDTO.getProductIdList())){
          shopIdSet.add(quotedPreBuyOrderDTO.getShopId());
          productIdList.addAll(quotedPreBuyOrderDTO.getProductIdList());
          Map<Long,Map<ImageScene,List<DataImageDetailDTO>>> imageMap = imageService.getDataImageDetailDTO(shopIdSet, imageSceneList, DataType.PRODUCT, productIdList.toArray(new Long[productIdList.size()]));
          if(MapUtils.isNotEmpty(imageMap)){
            quotedPreBuyOrderDTO.setItemImageCenterDTO(ImageUtils.generateCommonImageDetail(imageSceneList, imageMap.get(CollectionUtils.get(imageMap.keySet(),0)), true));
          }else {
            quotedPreBuyOrderDTO.setItemImageCenterDTO(ImageUtils.generateCommonImageDetail(imageSceneList,null, true));
          }
        }
      }
    }
  }

  private <T> List<T> subListForSize2(List<T> list, int limit) {
    int size = list.size();
    if (size > limit) {
      return list.subList(START, limit);
    }
    return list;
  }


}
