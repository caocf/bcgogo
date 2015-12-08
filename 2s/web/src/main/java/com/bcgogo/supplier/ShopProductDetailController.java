package com.bcgogo.supplier;

import com.bcgogo.common.WebUtil;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.dto.image.DataImageDetailDTO;
import com.bcgogo.config.service.image.IImageService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.config.util.ImageUtils;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.ProductStatus;
import com.bcgogo.enums.PromotionsEnum;
import com.bcgogo.enums.config.ImageScene;
import com.bcgogo.product.ProductCategory.ProductCategoryDTO;
import com.bcgogo.product.dto.ProductCategoryRelationDTO;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.dto.ProductMappingDTO;
import com.bcgogo.product.service.IProductCategoryService;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.product.service.IPromotionsService;
import com.bcgogo.product.service.PromotionsService;
import com.bcgogo.search.dto.ProductSearchResultListDTO;
import com.bcgogo.search.dto.SearchConditionDTO;
import com.bcgogo.search.service.product.ISearchProductService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.*;
import com.bcgogo.txn.dto.supplierComment.CommentStatDTO;
import com.bcgogo.txn.model.TxnDaoManager;
import com.bcgogo.txn.service.*;
import com.bcgogo.txn.service.supplierComment.ISupplierCommentService;
import com.bcgogo.user.dto.ContactDTO;
import com.bcgogo.utils.ArrayUtil;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.PromotionsUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * <p/>
 * 商品详情controller
 * <p/>
 * User: terry
 * Date: 13-8-12
 * Time: 下午2:12
 */
@Controller
@RequestMapping("/shopProductDetail.do")
public class ShopProductDetailController {

  private static final Logger LOG = LoggerFactory.getLogger(ShopProductDetailController.class);

  private static final String FROM_FLAG = "order";

  @Autowired
  private ShopMsgHelper shopMsgHelper;

  /**
   * 跳转到产品详情页面
   *
   * @param request
   * @param modelMap
   * @return
   */
  @RequestMapping(params = "method=toShopProductDetail")
  public String toShopProductDetail(HttpServletRequest request, ModelMap modelMap) {
    IProductService productService = ServiceManager.getService(IProductService.class);
    IProductHistoryService iProductHistoryService = ServiceManager.getService(IProductHistoryService.class);
    Long paramShopId = NumberUtils.toLong(request.getParameter("paramShopId"));
    if (paramShopId == null) {
      throw new RuntimeException("/shopProductDetail.do?method=toShopProductDetail,paramShopId is null or 0L");
    }
    modelMap.addAttribute("paramShopId", paramShopId);
    Long shopId = WebUtil.getShopId(request);
    Long productLocalId = NumberUtils.toLong(request.getParameter("productLocalId"));
    String strFrom = request.getParameter("itemFrom");

    Long orderItemId = NumberUtils.toLong(request.getParameter("orderItemId"));
    String orderType = request.getParameter("orderType"); //订单类型

    String quotedPreBuyOrderItemIdStr = request.getParameter("quotedPreBuyOrderItemId");
    if(StringUtils.isNotEmpty(quotedPreBuyOrderItemIdStr)) {
        Long quotedPreBuyOrderItemId = NumberUtils.toLong(quotedPreBuyOrderItemIdStr);
        try {
          QuotedPreBuyOrderItemDTO quotedPreBuyOrderItemDTO = ServiceManager.getService(IPreBuyOrderService.class).getQuotedPreBuyOrderItemDTOById(quotedPreBuyOrderItemId);
          modelMap.addAttribute("quotedPreBuyOrderItemDTO",quotedPreBuyOrderItemDTO);
        } catch (Exception e) {
          LOG.error(e.getMessage(),e);
        }
    }
    // FLAG isFromOrder
    boolean isFromOrder = StringUtils.equals(strFrom, FROM_FLAG) ? true : false;  // 表明是否从订单查询商品详情

    // 显示最新产品或者快照
    boolean showSnapShot = false;
    // 该商品是否上架
    boolean isInSales = true;
    try {

      OrderTypes orderTypes = null;
      if (StringUtils.isNotBlank(orderType)) {
        orderTypes = OrderTypes.valueOf(orderType);
      }

      if (isFromOrder) {
        if (orderItemId == null || orderItemId == 0L) {
          throw new RuntimeException("/shopProductDetail.do?method=toShopProductDetail,orderItemId is null or 0L");
        }
        if (orderTypes == null) {
          throw new RuntimeException("/shopProductDetail.do?method=toShopProductDetail,orderTypes is null,orderType from request is " + orderType);
        }
      } else {
        if (productLocalId == null || productLocalId == 0L) {
          throw new RuntimeException("/shopProductDetail.do?method=toShopProductDetail,productLocalId is null or 0L");
        }
      }

      ShopDTO shopDTO = shopMsgHelper.getShopMsgBasic(shopId, paramShopId);
      modelMap.addAttribute("shopDTO", shopDTO);
      shopMsgHelper.relateContextSet(request, modelMap, paramShopId, shopId);
      boolean isQQExist = false;
      if (ArrayUtil.isNotEmpty(shopDTO.getContacts())) {
        for (ContactDTO contactDTO : shopDTO.getContacts()) {
          if (contactDTO != null && StringUtils.isNotBlank(contactDTO.getQq())) {
            isQQExist = true;
          }
        }
      }
      modelMap.addAttribute("isQQExist", isQQExist);
      CommentStatDTO supplierCommentStatDTO = ServiceManager.getService(ISupplierCommentService.class).getCommentStatByShopId(paramShopId);
      if (supplierCommentStatDTO == null) {
        supplierCommentStatDTO = new CommentStatDTO();
      }
      supplierCommentStatDTO.calculate();
      modelMap.addAttribute("supplierCommentStatDTO", supplierCommentStatDTO);

      ProductDTO productDTO = null;
      ProductHistoryDTO productHistoryDTO = null;
      if (isFromOrder) {
        Long supplierProductId = productLocalId;
        Long supplierShopId = paramShopId;
        // 查订单item 获取productHistoryId productLocalInfoId
        Long productHistoryId = 0L;
        Long[] results = getProductHistoryByOrderTypeAndItemId(orderTypes, orderItemId, paramShopId);
        if (results != null && results[0] != null && results[1] != null && results[2] != null) {
          productLocalId = results[0];
          productHistoryId = results[1];
        }
        supplierProductId = productLocalId;

        Long productShopId = shopId;
        if(orderType.equals(OrderTypes.PURCHASE.toString())){
          productShopId = paramShopId;
        }

        if(orderType.equals(OrderTypes.RETURN.toString())){
          //退货单, 使用相应的采购单中的商品历史。
          Map<Long, ProductMappingDTO> mappingDTOMap = productService.getCustomerProductMappings(shopId, new Long[]{productLocalId});
          ProductMappingDTO mappingDTO = mappingDTOMap.get(productLocalId);
          if(mappingDTO!=null){
            supplierProductId = mappingDTO.getSupplierProductId();
            supplierShopId = mappingDTO.getSupplierShopId();

            PurchaseOrderItemDTO purchaseOrderItemDTO = ServiceManager.getService(IPurchaseReturnService.class).getPurchaseOrderItemDTOByPurchaseReturnItemId(orderItemId);
            productHistoryId = purchaseOrderItemDTO.getProductHistoryId();
            productShopId = paramShopId;
          }
        }

        // 查询productHistory 和 productDTO
        productHistoryDTO = iProductHistoryService.getProductHistoryById(productHistoryId, productShopId);

        productHistoryDTO.generateProductInfo();
//        productHistoryDTO.setPromotionOrderRecordDTOs(ServiceManager.getService(IPromotionsService.class).getPromotionOrderRecordByShopIdProductIdOrderId(orderId, productLocalId, paramShopId, orderTypes));
        // 查询促销历史
        List<Long> promotionRecordIds = new ArrayList<Long>();
        List<OrderItemPromotionDTO> orderItemPromotionDTOs = ServiceManager.getService(TxnService.class).getOrderItemPromotionsByOrderItemId(orderItemId);
        if (CollectionUtils.isNotEmpty(orderItemPromotionDTOs)) {
          for (OrderItemPromotionDTO promotionDTO : orderItemPromotionDTOs) {
            promotionRecordIds.add(promotionDTO.getPromotionOrderRecordId());
          }
        }
        List<PromotionOrderRecordDTO> promotionOrderRecordDTOs = ServiceManager.getService(IPromotionsService.class).getPromotionOrderRecordsById(promotionRecordIds.toArray(new Long[promotionRecordIds.size()]));
        productHistoryDTO.setPromotionOrderRecordDTOs(promotionOrderRecordDTOs);

        setProductHistoryPromotionStr(productHistoryDTO);

        productDTO = productService.getProductByProductLocalInfoId(supplierProductId, supplierShopId);

        productHistoryDTO.setLastUpdateTime(DateUtil.convertDateLong2SimpleCNFormat(productDTO.getLastModified()));
        productHistoryDTO.setInSalesAmount(productDTO.getInSalesAmount());
        setProductPromotionsStr(paramShopId, supplierProductId, productDTO);

        if (productHistoryDTO != null && productDTO != null) {

          // 根据productLocalInfo状态选择相应的显示
          if (productDTO.getSalesStatus() == ProductStatus.NotInSales) {
            showSnapShot = true;
            isInSales = false;
          } else {
            // 比较productHistory 和 productDTO
            if (!iProductHistoryService.isProductSnapShotBeLastVersionProductByKeyFields(productHistoryDTO, productDTO)) {
              showSnapShot = true;
            }
          }
        }
      } else {
        // 查产品最新状态
        productDTO = productService.getProductByProductLocalInfoId(productLocalId, paramShopId);
        if (productDTO == null) {
          LOG.warn("/shopProductDetail.do?method=toShopProductDetail,paramShopId is {0},productLocalId is {1}", new Object[]{paramShopId, productLocalId});
        }
        if (productDTO.getSalesStatus() == ProductStatus.NotInSales) {
          isInSales = false;
        }
        setProductPromotionsStr(paramShopId, productLocalId, productDTO);
      }
      //图片相关处理  和 商品分类
      IProductCategoryService productCategoryService = ServiceManager.getService(IProductCategoryService.class);
      IPromotionsService promotionsService = ServiceManager.getService(IPromotionsService.class);
      IImageService imageService = ServiceManager.getService(IImageService.class);
      List<ImageScene> imageSceneList = new ArrayList<ImageScene>();
      imageSceneList.add(ImageScene.PRODUCT_INFO_IMAGE_SMALL);
      imageSceneList.add(ImageScene.PRODUCT_INFO_IMAGE_BIG);
      imageSceneList.add(ImageScene.PRODUCT_INFO_DESCRIPTION_IMAGE);
      if (showSnapShot) {
        imageService.addImageInfoToProductHistoryDTO(imageSceneList, false, productHistoryDTO);
        List<DataImageDetailDTO> productInfoDescriptionImageDetailDTOs = productHistoryDTO.getImageCenterDTO().getProductInfoDescriptionImageDetailDTOs();
        String description = productHistoryDTO.getDescription();
        if (CollectionUtils.isNotEmpty(productInfoDescriptionImageDetailDTOs) && StringUtils.isNotBlank(description)) {
          for (int i = 0; i < productInfoDescriptionImageDetailDTOs.size(); i++) {
            description = description.replaceAll(ImageUtils.ImageSrcPlaceHolder + i, productInfoDescriptionImageDetailDTOs.get(i).getImageURL());
          }
          productHistoryDTO.setDescription(description);
        }
        if(productHistoryDTO.getProductCategoryId()!=null){
          ProductCategoryDTO productCategoryDTO = CollectionUtil.getFirst(productCategoryService.getProductCategoryDTOByIds(new HashSet<Long>(Arrays.asList(new Long[]{productHistoryDTO.getProductCategoryId()}))));
          productCategoryService.fillProductCategoryDTOInfo(productCategoryDTO);
          productHistoryDTO.setProductCategoryDTO(productCategoryDTO);
        }

        modelMap.addAttribute("product", productHistoryDTO);
      } else {
        ProductCategoryRelationDTO productCategoryRelationDTO = CollectionUtil.getFirst(productCategoryService.productCategoryRelationDTOQuery(paramShopId, productDTO.getProductLocalInfoId()));
        if(productCategoryRelationDTO!=null){
          ProductCategoryDTO productCategoryDTO = CollectionUtil.getFirst(productCategoryService.getProductCategoryDTOByIds(new HashSet<Long>(Arrays.asList(new Long[]{productCategoryRelationDTO.getProductCategoryId()}))));
          productCategoryService.fillProductCategoryDTOInfo(productCategoryDTO);
          productDTO.setProductCategoryDTO(productCategoryDTO);
        }
        imageService.addImageInfoToProductDTO(imageSceneList, false, productDTO);
        List<DataImageDetailDTO> productInfoDescriptionImageDetailDTOs = productDTO.getImageCenterDTO().getProductInfoDescriptionImageDetailDTOs();
        String description = productDTO.getDescription();
        if (CollectionUtils.isNotEmpty(productInfoDescriptionImageDetailDTOs) && StringUtils.isNotBlank(description)) {
          for (int i = 0; i < productInfoDescriptionImageDetailDTOs.size(); i++) {
            description = description.replaceAll(ImageUtils.ImageSrcPlaceHolder + i, productInfoDescriptionImageDetailDTOs.get(i).getImageURL());
          }
          productDTO.setDescription(description);
        }
//        promotionsService.addPromotionInfoToProductDTO(productDTO);
        modelMap.addAttribute("product", productDTO);
      }
      modelMap.addAttribute("fromSource", request.getParameter("fromSource"));
      modelMap.addAttribute("isSnapShot", showSnapShot);
      modelMap.addAttribute("isInSales", isInSales);
      modelMap.addAttribute("notFindImageURL_200X200", ImageUtils.generateNotFindImageUrl() + ConfigUtils.getUpYunSeparator() + ImageScene.PRODUCT_INFO_IMAGE_BIG.getImageVersion());


      ISearchProductService searchProductService = ServiceManager.getService(ISearchProductService.class);
      SearchConditionDTO searchConditionDTO = new SearchConditionDTO();
      searchConditionDTO.setShopId(paramShopId);
      searchConditionDTO.setSalesStatus(ProductStatus.InSales);
      searchConditionDTO.setIncludeBasic(false);
      searchConditionDTO.setSort("last_in_sales_time desc");
      searchConditionDTO.setRows(6);
      ProductSearchResultListDTO listDTO = searchProductService.queryProductWithUnknownField(searchConditionDTO);

      List<ProductDTO> productDTOs = listDTO.getProducts();
      if(CollectionUtils.isNotEmpty(productDTOs)){
        Iterator<ProductDTO> iter = productDTOs.iterator();
        while(iter.hasNext()){
          ProductDTO product = iter.next();
          if(product.getProductLocalInfoId().equals(productLocalId)){
            iter.remove();
          }
        }
        if(productDTOs.size()>5){
          productDTOs = productDTOs.subList(0, 5);
        }
        List<ImageScene> imageScenes = new ArrayList<ImageScene>();
        imageScenes.add(ImageScene.OTHER_PRODUCT_IMAGE);
        imageService.addImageInfoToProductDTO(imageScenes, true, productDTOs.toArray(new ProductDTO[productDTOs.size()]));
      }
      modelMap.addAttribute("otherProducts", productDTOs);
    } catch (Exception e) {
      LOG.error("/shopProductDetail.do?method=toShopProductDetail", e);
    }
    return "customer/ShopProductDetail";
  }

  private void setProductHistoryPromotionStr(ProductHistoryDTO productHistoryDTO) throws Exception {
    List<PromotionsDTO> promotions = PromotionsUtils.generatePromotionsFromRecord(productHistoryDTO.getPromotionOrderRecordDTOs());
    String[] titles = PromotionsUtils.genPromotionTypesStr(promotions);
    productHistoryDTO.setPromotionTypesShortStr(titles[0]);
    productHistoryDTO.setPromotionTypesStr(titles[1]);
    productHistoryDTO.setPromotionContent(PromotionsUtils.generatePromotionsContent(productHistoryDTO));
    if(PromotionsUtils.hasBargain(promotions)){
      productHistoryDTO.setHasBargain(true);
      productHistoryDTO.setInSalesPriceAfterCal(PromotionsUtils.calculateBargainPrice(promotions, productHistoryDTO.getInSalesPrice()) == null ? 0.00d : PromotionsUtils.calculateBargainPrice(promotions, productHistoryDTO.getInSalesPrice()));
    }
  }

  private void setProductPromotionsStr(Long paramShopId, Long productLocalId, ProductDTO productDTO) throws Exception {
    if(productDTO == null){
      return;
    }
    IPromotionsService iPromotionsService = ServiceManager.getService(IPromotionsService.class);
    Map<Long, List<PromotionsDTO>> allPromotionsDTOMap = iPromotionsService.getPromotionsDTOMapByProductLocalInfoId(paramShopId, true, productLocalId);
    List<PromotionsDTO> promotions = allPromotionsDTOMap.get(productDTO.getProductLocalInfoId());
    List<PromotionsDTO> usingPromotion=new ArrayList<PromotionsDTO>();
    if(CollectionUtil.isNotEmpty(promotions)){
      for(PromotionsDTO promotionsDTO:promotions){
        if(promotionsDTO!=null&& PromotionsEnum.PromotionStatus.USING.equals(promotionsDTO.getStatus())){
          usingPromotion.add(promotionsDTO);
        }
      }
    }
    productDTO.setPromotionsDTOs(usingPromotion);
    if(PromotionsUtils.hasBargain(usingPromotion)){
      productDTO.setHasBargain(true);
      productDTO.setInSalesPriceAfterCal(PromotionsUtils.calculateBargainPrice(usingPromotion, productDTO.getInSalesPrice()) == null ? 0.00d : PromotionsUtils.calculateBargainPrice(usingPromotion, productDTO.getInSalesPrice()));
    }
    String[] titles = PromotionsUtils.genPromotionTypesStr(usingPromotion);
    productDTO.setPromotionTypesShortStr(titles[0]);
    productDTO.setPromotionTypesStr(titles[1]);
    productDTO.setPromotionContent(PromotionsUtils.generatePromotionsContent(productDTO));
  }

  /**
   * 通过订单类型和itemId获取productLocalId 和productHistoryId
   *
   * @param orderTypes
   * @param itemId
   * @return
   */
  private Long[] getProductHistoryByOrderTypeAndItemId(OrderTypes orderTypes, Long itemId, Long shopId) {
    Long[] ids = new Long[3];
    switch (orderTypes) {
      case SALE:
        SalesOrderItemDTO salesOrderItemDTO = ServiceManager.getService(IGoodSaleService.class).getSalesOrderItemById(itemId);
        if (salesOrderItemDTO == null) {
          return ids;
        }
        ids[0] = salesOrderItemDTO.getProductId();
        ids[1] = salesOrderItemDTO.getProductHistoryId();
        ids[2] = salesOrderItemDTO.getSalesOrderId();
        break;
      case PURCHASE:
        PurchaseOrderItemDTO purchaseOrderItemDTO = ServiceManager.getService(IGoodBuyService.class).getPurchaseOrderItemById(itemId);
        if (purchaseOrderItemDTO == null) {
          return ids;
        }
        ids[0] = purchaseOrderItemDTO.getSupplierProductId();
        ids[1] = purchaseOrderItemDTO.getProductHistoryId();
        ids[2] = purchaseOrderItemDTO.getPurchaseOrderId();
        break;
      case SALE_RETURN:
        SalesReturnItemDTO salesReturnItemDTO = ServiceManager.getService(ISaleReturnOrderService.class).getSalesReturnOrderItemDTOById(itemId);
        if (salesReturnItemDTO == null) {
          return ids;
        }
        ids[0] = salesReturnItemDTO.getProductId();
        ids[1] = salesReturnItemDTO.getProductHistoryId();
        ids[2] = salesReturnItemDTO.getSalesReturnId();
        break;
      case RETURN:
        PurchaseReturnItemDTO purchaseReturnItemDTO = ServiceManager.getService(IPurchaseReturnService.class).getPurchaseReturnItemDTOById(itemId);
        if (purchaseReturnItemDTO == null) {
          return ids;
        }
        ids[0] = purchaseReturnItemDTO.getProductId();
        ids[1] = purchaseReturnItemDTO.getProductHistoryId();
        ids[2] = purchaseReturnItemDTO.getPurchaseReturnId();
        break;
      default:
        return ids;
    }
    return ids;
  }


}
