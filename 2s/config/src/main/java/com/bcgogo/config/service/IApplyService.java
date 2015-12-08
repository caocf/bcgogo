package com.bcgogo.config.service;

import com.bcgogo.common.Pager;
import com.bcgogo.common.PagingListResult;
import com.bcgogo.common.Result;
import com.bcgogo.config.dto.*;
import com.bcgogo.enums.shop.InviteCountStatus;
import com.bcgogo.enums.shop.InviteStatus;
import com.bcgogo.enums.shop.InviteType;
import com.bcgogo.enums.shop.ShopKind;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.SupplierDTO;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-1-22
 * Time: 下午1:30
 * To change this template use File | Settings | File Templates.
 */
public interface IApplyService {
  Result validateApplySupplierRelation(Long shopId, Long... supplerShopIds);

  Long[] initApplySupplierRelationShopIds(Long shopId, Long... supplerShopIds);

  Result validateApplyCustomerRelation(Long shopId, Long... customerShopId);

  Long[] initApplyCustomerRelationShopIds(Long shopId, Long... customerShopId);


  List<ApplyShopSearchCondition> searchApplyCustomerShop(ApplyShopSearchCondition searchCondition,String shopVersionIdStr, Pager pager
      , boolean isIncludeTestShop);

  List<ApplyShopSearchCondition> searchApplySupplierShop(ApplyShopSearchCondition searchCondition,String shopVersionIdStr, Pager pager
      , boolean isIncludeTestShop);

  Integer countApplyCustomerShop(ApplyShopSearchCondition searchCondition,String shopVersionIdStr, boolean isIncludeTestShop);

  Integer countApplySupplierShop(ApplyShopSearchCondition searchCondition,String shopVersionIdStr, boolean isTestShop);

  boolean isTestShop(ShopDTO shopDTO);

  /**
   * 查询发送过的关联请求
   * @param inviteType 可为空
   * @param status
   * @param originShopId 请求发起方
   * @param expiredTime
   * @param invitedShopIds 请求接收方(可多个）
   * @return
   */
  Map<Long, ShopRelationInviteDTO> getShopRelationInviteDTOMapByInvitedShopIds(
      InviteType inviteType, InviteStatus status, Long originShopId,Long expiredTime, Long... invitedShopIds);

  /**
   * 查询发送过的关联请求
   * @param inviteType
   * @param status
   * @param invitedShopId 请求接收方
   * @param expiredTime
   * @param originShopIds 请求发起方(可多个）
   * @return
   */
  Map<Long, ShopRelationInviteDTO> getShopRelationInviteDTOMapByOriginShopId(
      InviteType inviteType, InviteStatus status, Long invitedShopId,Long expiredTime, Long... originShopIds);



  List<ShopRelationInviteDTO> batchSaveApplySupplierRelation(Long shopId, Long userId, Long... supplierShopIds) throws Exception;

  ShopRelationInviteDTO saveApplySupplierRelation(Long shopId, Long userId, Long supplierShopId) throws Exception;

  ShopRelationInviteDTO saveApplyCustomerRelation(Long shopId, Long userId, Long customerShopId) throws Exception;

  List<ShopRelationInviteDTO> batchSaveApplyCustomerRelation(Long shopId, Long userId, Long... customerShopIds) throws Exception;

  ShopRelationInviteDTO getShopRelationInviteDTOByInvitedShopIdAndId(Long invitedShopId, Long inviteId);

  Result validateRefuseApply(ShopDTO shopDTO, ShopRelationInviteDTO shopRelationInviteDTO);

  void refuseApply(ShopRelationInviteDTO shopRelationInviteDTO) throws Exception;

  Result validateAcceptCustomerApply(ShopDTO shopDTO, ShopRelationInviteDTO shopRelationInviteDTO);

  boolean acceptApply(ShopRelationInviteDTO shopRelationInviteDTO)throws Exception;

  Result validateAcceptSupplierApply(ShopDTO shopDTO, ShopRelationInviteDTO shopRelationInviteDTO);

  void deleteShopRelationInvites(Long shopId, Long userId,String userName, Long... shopRelationInviteId) throws Exception;


  PagingListResult<ShopRelationInviteDTO> getShopRelationInvites(Long shopId, InviteType inviteType
      , List<InviteStatus> statuses, Long originShopId, Pager pager) throws Exception;

  //取消店铺关联，保存操作日志
  boolean cancelShopRelation(Long customerShopId, Long supplierShopId, Long operateShopId,
                             Long userId, String cancelMsg)throws Exception;

  /**
   *  删除供应商的时候，如果该供应商是在线店铺，同时取消在线关联关系。
   * 客户店铺删除供应商时 取消关联逻辑
   * @param supplierDTO
   */
  void customerShopDeleteSupplierUpdateRelation(SupplierDTO supplierDTO,Long userId) throws Exception;

  /**
    *  删除供客户的时候，如果该客户是在线店铺，同时取消在线关联关系。
    * 供应商店铺删除客户时 取消关联逻辑
    * @param customerDTO
    */
  void wholesalerShopDeleteCustomerUpdateRelation(CustomerDTO customerDTO,Long userId)throws Exception;

  ShopDTO getShopAreaDTOInfo(Long id);

  void updateShopRelationInvite(ShopRelationInviteDTO shopRelationInviteDTO) throws Exception;

  List<ShopDTO> getSupplierOrCustomerShopSuggestion(Long shopId,String searchWord, boolean isTesShop,String customerOrSupplier,String shopRange);

  //自动匹配
  List<CustomerRelatedShopDTO> getRelatedShopByCustomerMobile(Long shopId,ShopKind shopKind);

  List<SupplierRelatedShopDTO> getRelatedShopBySupplierMobile(Long shopId, ShopKind shopKind);

  /**
   * @return ShopRelationInvite id
   */
  List<Long> deleteOtherSupplierShopRelationInviteByInvitedShopContactMobile(Long customerShopId, Long supplierShopId, List<String> supplierMobiles) throws Exception;

  /**
   * @return ShopRelationInvite id
   */
  List<Long> deleteOtherCustomerShopRelationInviteByInvitedShopContactMobile(Long customerShopId, Long supplierShopId, List<String> customerMobiles) throws Exception;

  //创建或者更新关联关系 客户收藏供应商关联关系
  void collectSupplierShopUpdateRelation(Long customerShopId, Long wholesalerShopId) throws Exception;

    //创建或者更新关联关系 供应商收藏客户关联关系
  void collectCustomerShopUpdateRelation(Long customerShopId, Long wholesalerShopId) throws Exception;

  List<ShopRelationInviteDTO> getPendingShopRelationInviteDTOs(Long shopId, InviteType inviteType) throws Exception;
}
