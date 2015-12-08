package com.bcgogo.supplier;

import com.bcgogo.common.WebUtil;
import com.bcgogo.config.dto.*;
import com.bcgogo.config.service.ApplyService;
import com.bcgogo.config.service.IApplyService;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.image.IImageService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.enums.RelationMidStatus;
import com.bcgogo.enums.RelationTypes;
import com.bcgogo.enums.config.AttachmentType;
import com.bcgogo.enums.config.DataType;
import com.bcgogo.enums.config.ImageScene;
import com.bcgogo.enums.config.ImageType;
import com.bcgogo.enums.shop.InviteStatus;
import com.bcgogo.enums.shop.InviteType;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.recommend.IPreciseRecommendService;
import com.bcgogo.user.dto.ContactDTO;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.utils.ArrayUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 页面需要显示的店铺信息获取帮助类
 * <p/>
 * User: terry
 * Date: 13-8-29
 * Time: 下午1:41
 */
@Service("shopMsgHelper")
public class ShopMsgHelper {

  public static final Logger LOG = LoggerFactory.getLogger(ShopMsgHelper.class);

  public ShopDTO getShopMsgBasic(Long shopId, Long paramShopId) {
    IImageService imageService = ServiceManager.getService(IImageService.class);
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    IPreciseRecommendService preciseRecommendService = ServiceManager.getService(IPreciseRecommendService.class);
    Map<Long, ShopDTO> shopDTOs = configService.getShopByShopId(shopId, paramShopId);
    ShopDTO shopDTO = shopDTOs.get(paramShopId);
    shopDTO.setBusinessScope(shopDTO.fromBusinessScopes());

    shopDTO.resetBusinessScope();
    shopDTO.setOperationMode(shopDTO.fromOperationModes());
    Set<Long> areaNos = new HashSet<Long>();
    areaNos.add(shopDTO.getProvince());
    areaNos.add(shopDTO.getCity());
    areaNos.add(shopDTO.getRegion());
    Map<Long, AreaDTO> areaMap = configService.getAreaByAreaNo(areaNos);
    shopDTO.setAreaNameByAreaNo(areaMap);

    Set<Long> shopIdSet = new HashSet<Long>();
    shopIdSet.add(shopDTO.getId());
    Map<Long, String> businessScopeMap = preciseRecommendService.getSecondCategoryByShopId(shopIdSet);
    if (MapUtils.isNotEmpty(businessScopeMap) && StringUtils.isNotEmpty(businessScopeMap.get(shopDTO.getId()))) {
      shopDTO.setBusinessScopeStr(businessScopeMap.get(shopDTO.getId()));
    }

    if (StringUtils.isEmpty(shopDTO.getRegistrationDateStr())) {
      shopDTO.setRegistrationDateStr(shopDTO.getCreationDateStr());
    }

    // 收藏该店铺的supplier数量
    int beFavouredCnt = configService.countBeFavoured(paramShopId);
    shopDTO.setBeStored(beFavouredCnt);

    // 认证
//    AttachmentDTO attachmentDTO = configService.getAttachmentByShopId(paramShopId, AttachmentType.SHOP_BUSINESS_LICENSE_PHOTO);
    shopDTO.setLicensed(imageService.isExistDataImageRelation(shopDTO.getId(), ImageType.SHOP_BUSINESS_LICENSE_IMAGE, DataType.SHOP, shopDTO.getId(),1));
//    if (attachmentDTO!=null) {
//      shopDTO.setBusinessLicenseId(attachmentDTO.getId());
//      shopDTO.setBusinessLicenseName(attachmentDTO.getName());
//    }

    //商品数量
    int totalProductCount = ServiceManager.getService(IProductService.class).countProductInSales(paramShopId);
    shopDTO.setTotalProductCount(totalProductCount);

    //图片

    List<ImageScene> imageSceneList = new ArrayList<ImageScene>();
//    imageSceneList.add(ImageScene.SHOP_IMAGE_BIG);
//    imageSceneList.add(ImageScene.SHOP_IMAGE_SMALL);
    imageSceneList.add(ImageScene.SHOP_IMAGE);
    imageService.addImageToShopDTO(imageSceneList, false, shopDTO);

    return shopDTO;
  }

  /**
   * 关联关系相关上下文设置
   *
   * @param request
   * @param model
   * @param paramShopId
   * @param shopId
   */
  public void relateContextSet(HttpServletRequest request, ModelMap model, Long paramShopId, Long shopId) {

    IConfigService configService = ServiceManager.getService(IConfigService.class);

    Object[] relationResult = getShopRelationStatus(shopId, paramShopId);
    RelationMidStatus relationStatus = (RelationMidStatus)relationResult[0];
    if(relationStatus == RelationMidStatus.BE_APPLY_RELATED){
      ShopRelationInviteDTO inviteDTO = (ShopRelationInviteDTO) relationResult[1];
      model.addAttribute("inviteDTO", inviteDTO);
    }
    model.addAttribute("relationStatus", relationStatus);

    String relateFlag;
    ShopDTO paramShopDTO = configService.getShopById(paramShopId);
    if(ConfigUtils.isWholesalerVersion(paramShopDTO.getShopVersionId())){
      relateFlag = "relatedAsCustomer";
    } else {
      relateFlag = "relatedAsSupplier";
    }
    model.addAttribute("relateFlag", relateFlag);

    // 是否显示产品相关的一些信息
    boolean isShowProductRelatedMsg = true;
    // 本店是否为汽配店
    if (ConfigUtils.isWholesalerVersion(WebUtil.getShopVersionId(request))) {
      // 本店如果是这个店铺的客户或者无关联关系不给查看
      if (MapUtils.isEmpty(configService.getWholesalerShopRelationMapByWholesalerShopId(paramShopId, RelationTypes.WHOLESALER_RELATE_TO_CUSTOMER_LIST, shopId))) {
        isShowProductRelatedMsg = false;
      }
    }
    model.addAttribute("isShowProductRelateMsg", isShowProductRelatedMsg);

  }

  public Object[] getShopRelationStatus(Long myShopId, Long oppositeShopId){
    Object[] result = new Object[2];
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    IApplyService applyService = ServiceManager.getService(IApplyService.class);

    List<RelationTypes> related = new ArrayList<RelationTypes>();
    related.add(RelationTypes.RELATED);
    //我关联了对方(汽配)
    Map<Long, WholesalerShopRelationDTO> relateWholesaler = configService.getWholesalerShopRelationMapByWholesalerShopId(myShopId, related, oppositeShopId);
    //我关联了对方(汽修)
    Map<Long, WholesalerShopRelationDTO> relateRepair = configService.getWholesalerShopRelationMapByWholesalerShopId(oppositeShopId, related, myShopId);
    if(!MapUtils.isEmpty(relateWholesaler) || !MapUtils.isEmpty(relateRepair)){
      result[0] = RelationMidStatus.RELATED;
      return result;
    }

    List<RelationTypes> customerCollection = new ArrayList<RelationTypes>();
    customerCollection.add(RelationTypes.CUSTOMER_COLLECTION);
    List<RelationTypes> supplierCollection = new ArrayList<RelationTypes>();
    supplierCollection.add(RelationTypes.SUPPLIER_COLLECTION);
    //我(汽修)收藏对方(汽配)
    Map<Long, WholesalerShopRelationDTO> collectWholesaler = configService.getWholesalerShopRelationMapByWholesalerShopId(myShopId, customerCollection, oppositeShopId);
    //我（汽配）收藏对方（汽修）
    Map<Long, WholesalerShopRelationDTO> collectRepair = configService.getWholesalerShopRelationMapByWholesalerShopId(oppositeShopId, supplierCollection, myShopId);
    //我收藏了对方
    boolean collectedOther = !MapUtils.isEmpty(collectWholesaler) || !MapUtils.isEmpty(collectRepair);

    //我向对方发起了关联请求
    Map<Long, ShopRelationInviteDTO> hasInvite = applyService.getShopRelationInviteDTOMapByInvitedShopIds(null, InviteStatus.PENDING, myShopId, ApplyService.EXPIRED_TIME, oppositeShopId);
    //对方向我发起了关联请求
    Map<Long, ShopRelationInviteDTO> hasBeenInvited = applyService.getShopRelationInviteDTOMapByOriginShopId(null, InviteStatus.PENDING, myShopId, ApplyService.EXPIRED_TIME, oppositeShopId);
    if (collectedOther) {
      result[0] = RelationMidStatus.RELATED;
      return result;
    }
    if (!MapUtils.isEmpty(hasBeenInvited)) {
      ShopRelationInviteDTO shopRelationInviteDTO = hasBeenInvited.get(oppositeShopId);
      result[0] = RelationMidStatus.BE_APPLY_RELATED;
      result[1] = shopRelationInviteDTO;
      return result;
    }
    if (!MapUtils.isEmpty(hasInvite)) {
      result[0] = RelationMidStatus.APPLY_RELATED;
      return result;
    }
    result[0] = RelationMidStatus.UN_APPLY_RELATED;
    return result;
  }


}
