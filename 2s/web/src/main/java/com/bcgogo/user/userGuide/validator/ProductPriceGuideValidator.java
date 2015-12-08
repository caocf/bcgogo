package com.bcgogo.user.userGuide.validator;

import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.dto.ShopRelationInviteDTO;
import com.bcgogo.config.service.IApplyService;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.ProductStatus;
import com.bcgogo.enums.shop.InviteStatus;
import com.bcgogo.enums.shop.InviteType;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.search.dto.ProductSearchResultListDTO;
import com.bcgogo.search.dto.SearchConditionDTO;
import com.bcgogo.search.service.product.ISearchProductService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.cache.UserGuideCacheManager;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.user.dto.userGuide.UserGuideDTO;
import com.bcgogo.user.dto.userGuide.UserGuideStepDTO;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.user.service.IUserGuideService;
import com.bcgogo.user.service.IUserService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-3-7
 * Time: 下午3:37
 */
@Component
public class ProductPriceGuideValidator extends AbstractValidator {
  private static final Logger LOG = LoggerFactory.getLogger(ProductPriceGuideValidator.class);
  private static final long EXPIRED_TIME = 2 * 24 * 60 * 60 * 1000l;

  @Override
  protected UserGuideDTO validate(long shopVersionId, long shopId, long userId, UserGuideDTO userGuideDTO, boolean needNext, String... excludeFlowName) throws Exception {
    if ("PRODUCT_PRICE_GUIDE_BEGIN".equals(userGuideDTO.getCurrentStep().getName())) {
      if (!haveRelatedCommodityQuotations(shopId) && !haveMoreCommodityQuotations(shopId)) {
        userGuideDTO.setValidatorSuccess(false);
        return userGuideDTO;
      } else {
        return defaultStep(shopVersionId, userId, userGuideDTO, shopId, excludeFlowName);
      }
    }else if("PRODUCT_PRICE_GUIDE_PURCHASE_CENTER".equals(userGuideDTO.getCurrentStep().getName())){
      return this.validateProductPriceGuidePurchaseNext(shopVersionId, shopId, userId, userGuideDTO, excludeFlowName);
    }
    else if ("PRODUCT_PRICE_GUIDE_PURCHASE".contains(userGuideDTO.getCurrentStep().getName())) {
      return this.validateProductPriceGuidePurchaseNext(shopVersionId, shopId, userId, userGuideDTO, excludeFlowName);
    } else if ("PRODUCT_PRICE_GUIDE_MORE_PRODUCT".equals(userGuideDTO.getCurrentStep().getName())) {
      return this.validateMoreProduct(shopVersionId, shopId, userId, userGuideDTO, excludeFlowName);
    } else {
      return defaultStep(shopVersionId, userId, userGuideDTO, shopId, excludeFlowName);
    }
  }



    //无关联报价
  private UserGuideDTO validateProductPriceGuidePurchaseNext(long shopVersionId, long shopId, long userId, UserGuideDTO userGuideDTO, String... excludeFlowName) throws Exception {
    IUserGuideService userGuideService = ServiceManager.getService(IUserGuideService.class);
    if (this.haveRelatedCommodityQuotations(shopId)) {
      return defaultStep(shopVersionId, userId, userGuideDTO, shopId, excludeFlowName);
    } else if (this.haveMoreCommodityQuotations(shopId)) {
      userGuideService.updateCurrentUserGuideStepFinished(userId, "PRODUCT_PRICE_GUIDE", "PRODUCT_PRICE_GUIDE_PURCHASE");
      UserGuideStepDTO stepDTO = UserGuideCacheManager.getUserGuideStepByName("PRODUCT_PRICE_GUIDE_PURCHASE").getKey();
      userGuideDTO.setCurrentFlow(UserGuideCacheManager.getCurrentUserGuideFlowByName(shopVersionId, stepDTO.getFlowName()));
      userGuideDTO.setCurrentStep(stepDTO);
      userGuideDTO.setNextStep(UserGuideCacheManager.getUserGuideStepByName(stepDTO.getNextStep()).getKey());
      return userGuideDTO;
    } else {
      userGuideService.updateCurrentUserGuideFlowFinished(userId, "PRODUCT_PRICE_GUIDE");
      return null;
    }
  }


  //无关联报价有更多供应商报价
  private UserGuideDTO validateMoreProduct(long shopVersionId, long shopId, long userId, UserGuideDTO userGuideDTO, String... excludeFlowName) throws Exception {
    IUserGuideService userGuideService = ServiceManager.getService(IUserGuideService.class);
    if (haveMoreCommodityQuotations(shopId)) {
      userGuideDTO.setValidatorSuccess(true);
      return userGuideDTO;
    } else {
      userGuideDTO.setValidatorSuccess(false);
      return userGuideDTO;
    }
  }


  //有关联报价
  private boolean haveRelatedCommodityQuotations(long shopId) throws Exception {
    boolean haveRelatedCommodityQuotations = false;
    List<SupplierDTO> supplierDTOList = ServiceManager.getService(IUserService.class).getRelatedSuppliersByShopId(shopId);
    Set<Long> supplierShopIdSet = new HashSet<Long>();
    if (CollectionUtils.isNotEmpty(supplierDTOList)) {
      for (SupplierDTO supplierDTO : supplierDTOList) {
        supplierShopIdSet.add(supplierDTO.getSupplierShopId());
      }
    }
    if (CollectionUtils.isNotEmpty(supplierShopIdSet)) {
      SearchConditionDTO searchConditionDTO = new SearchConditionDTO();
      searchConditionDTO.setSalesStatus(ProductStatus.InSales);
      searchConditionDTO.setIncludeBasic(false);
      searchConditionDTO.setSort("last_in_sales_time desc");
      ShopDTO shopDTO =ServiceManager.getService(IConfigService.class).getShopById(shopId);
      searchConditionDTO.setShopKind(shopDTO.getShopKind());
      searchConditionDTO.setSearchStrategy(new String[]{SearchConditionDTO.SEARCHSTRATEGY_NO_SHOP_RESTRICT});
      searchConditionDTO.setShopIds(supplierShopIdSet.toArray(new Long[supplierShopIdSet.size()]));
      ProductSearchResultListDTO productSearchResultListDTO = ServiceManager.getService(ISearchProductService.class).queryProductWithUnknownField(searchConditionDTO);
      if (productSearchResultListDTO.getNumFound() > 0) {
        haveRelatedCommodityQuotations = true;
      }
    }
    return haveRelatedCommodityQuotations;
  }

  //有无非关联报价
  private boolean haveMoreCommodityQuotations(long shopId) throws Exception {
    ProductSearchResultListDTO searchResultListDTO = getMoreCommodityQuotations(shopId);
    return searchResultListDTO.getNumFound() > 0;
  }

  //有无 非关联报价 能不能申请
  private boolean haveMoreCommodityQuotationsCanApply(long shopId) throws Exception {
    boolean haveMoreCommodityQuotations = false;
    ProductSearchResultListDTO searchResultListDTO = getMoreCommodityQuotations(shopId);
    if (searchResultListDTO.getNumFound() <= 0) {
      haveMoreCommodityQuotations = false;
    } else {
      Set<Long> shopIds = new HashSet<Long>();
      for (ProductDTO dto : searchResultListDTO.getProducts()) {
        shopIds.add(dto.getShopId());
      }
      IApplyService applyService = ServiceManager.getService(IApplyService.class);
      //自己向对方申请过的
      Map<Long, ShopRelationInviteDTO> shopRelationInviteDTOMap = applyService.getShopRelationInviteDTOMapByInvitedShopIds(
          InviteType.CUSTOMER_INVITE, InviteStatus.PENDING, shopId, EXPIRED_TIME, shopIds.toArray(new Long[shopIds.size()]));
      //对方申请过自己的
      Map<Long, ShopRelationInviteDTO> oppositesShopRelationInviteDTOMap = applyService.getShopRelationInviteDTOMapByOriginShopId(
          InviteType.SUPPLIER_INVITE, InviteStatus.PENDING, shopId, EXPIRED_TIME, shopIds.toArray(new Long[shopIds.size()]));
      for (Long s : shopIds) {
        if (shopRelationInviteDTOMap.get(s) == null && oppositesShopRelationInviteDTOMap.get(s) == null) {
          return true;
        }
      }
    }
    return haveMoreCommodityQuotations;
  }

  private ProductSearchResultListDTO getMoreCommodityQuotations(long shopId) throws Exception {
    List<SupplierDTO> supplierDTOList = ServiceManager.getService(IUserService.class).getRelatedSuppliersByShopId(shopId);
    Set<Long> supplierShopIdSet = new HashSet<Long>();
    if (CollectionUtils.isNotEmpty(supplierDTOList)) {
      for (SupplierDTO supplierDTO : supplierDTOList) {
        supplierShopIdSet.add(supplierDTO.getSupplierShopId());
      }
    }
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    SearchConditionDTO searchConditionDTO = new SearchConditionDTO();
    searchConditionDTO.setSalesStatus(ProductStatus.InSales);
    searchConditionDTO.setIncludeBasic(false);
    searchConditionDTO.setSort("last_in_sales_time desc");
    ShopDTO shopDTO =ServiceManager.getService(IConfigService.class).getShopById(shopId);
    searchConditionDTO.setShopKind(shopDTO.getShopKind());
    searchConditionDTO.setSearchStrategy(new String[]{SearchConditionDTO.SEARCHSTRATEGY_NO_SHOP_RESTRICT});
    searchConditionDTO.setShopIds(supplierShopIdSet.toArray(new Long[supplierShopIdSet.size()]));
    Set<Long> excludeShopIds =  supplierShopIdSet;
    excludeShopIds.add(shopId);
    return new ProductSearchResultListDTO();
  }
}
