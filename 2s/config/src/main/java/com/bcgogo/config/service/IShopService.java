package com.bcgogo.config.service;

import com.bcgogo.common.AllListResult;
import com.bcgogo.common.ListResult;
import com.bcgogo.common.Result;
import com.bcgogo.config.ShopSearchCondition;
import com.bcgogo.config.ShopSearchResult;
import com.bcgogo.config.dto.*;
import com.bcgogo.config.model.ConfigWriter;
import com.bcgogo.config.model.Shop;
import com.bcgogo.config.model.TrafficPackage;
import com.bcgogo.enums.shop.ShopOperateTaskScene;
import com.bcgogo.enums.shop.ShopOperateType;
import com.bcgogo.enums.shop.ShopState;
import com.bcgogo.enums.shop.ShopStatus;
import com.bcgogo.enums.txn.finance.ChargeType;
import com.bcgogo.enums.txn.finance.PaymentStatus;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.user.dto.ContactDTO;
import org.springframework.security.authentication.BadCredentialsException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-1-1
 * Time: 下午3:53
 */
public interface IShopService {
  Map<Long, ShopDTO> getShopByShopIds(Long... shopIds);

  //Shop
  public ShopDTO createShop(ShopDTO shopDTO) throws BcgogoException, IOException;

  public ShopDTO updateShop(ShopDTO shopDTO) throws BcgogoException, IOException;

  /**
   * 仅仅适用于新增
   *
   * @throws Exception
   */
  ShopDTO registerShop(ShopDTO shopDTO) throws Exception;

  ShopDTO activateShop(Long shopId,long shopVersionId, ChargeType chargeType) throws BcgogoException;

  //todo 明星店铺推荐
  List<ShopDTO> getRecommendedShop();

  ShopSearchResult getShopByShopCondition(ShopSearchCondition condition);

  //check shop name is duplicate
  boolean checkShopName(String shopName, Long id);

  ShopDTO getShopAreaInfo(Long shopId);

  void updateShopStatus(Long shopId, ShopStatus shopStatus);

  void updateShopState(Long shopId, ShopState shopState);

  void updateShopPaymentStatus(Long shopId, PaymentStatus paymentStatus, Long usingEndTime);

  void updateShopStatus(Long shopId, ShopStatus shopStatus, PaymentStatus paymentStatus);

  /**
   * 创建 禁用&启用历史记录
   *
   * @param dto ShopOperateHistoryDTO
   * @throws BcgogoException
   */
  void createShopOperateHistory(ShopOperateHistoryDTO dto) throws BcgogoException;

  /**
   * 查找试用过期的店铺 并 更新其状态
   */
  void checkTrialEndTimeShop();

  ShopDTO checkTrialEndTimeShop(long shopId);

  ShopOperateHistoryDTO getLatestShopOperateHistory(Long operateShopId, ShopOperateType type) throws BcgogoException;

  AllListResult<ShopOperateHistoryDTO> getLatestShopOperateHistoryList(Long operateShopId) throws BcgogoException;

  ShopOperationTaskDTO getFirstReadyShopOperationTaskDTO();

  void updateShopOperationTaskDTO(ShopOperationTaskDTO taskDTO);

  ListResult<ShopDTO> getShopSuggestionByName(String shopName,ShopStatus... shopStatuses) throws BcgogoException;

  ShopDTO getActiveUsingShopByName(String shopName);

  List<Long> getShopIdByShopCondition(ShopSearchCondition shopSearchCondition);


  /**
   * 跟进人变更 log
   *
   * @param shopId 跟进店铺
   * @param userId 跟进人
   */
  void maintainShopLog(long shopId, long userId, String userName);


  /**
   * 如果店铺使用截止时间小于当前系统时间 把店铺状态改为欠费状态
   */
  void checkTrialDebtShop();

  boolean initShopServiceScope();

  Result shopRegisteredStatistics(ShopStatus shopStatus) throws BcgogoException;

  List<Long> getAllShopIds();//获取user_switch表里的所有的shop_id,以便初始化所有店面的刷卡机开关状态

  List<Long> getActiveShopIds();//获取user_switch表里的所有的shop_id,以便初始化所有店面的刷卡机开关状态

  void createShopOperationTask(ConfigWriter writer, ShopOperateTaskScene scene, Long... shopIds);

  List<ContactDTO> getShopContactsByShopId(Long shopId);

  List<Long> getBusinessScopeIdsByShopId(Long shopId);

  Shop getShopById(Long shopId);

  ShopDTO getShopDTOById(Long shopId);

  /**
   * 保存在线本店信息
   * @param result
   * @param shopId
   * @param dto
   * @return
   */
  Result saveShopInfo(Result result,Long shopId,ShopDTO dto);

  Result saveShopContacts(Result result,Long shopId,ShopDTO dto);

  void saveOrUpdateShopRQImage(ShopDTO dto);  //生成或更新店面的二维码图片
  @Deprecated
  void updateShopCoordinate(Long shopId)throws IOException ;

  boolean updateShopGeocode(Long shopId);

  Result updateShopAddressCoordinate(Long id, String coordinateLon, String coordinateLat,
                                     Long province, Long city, Long region, String address);

  public void verifyShop(ShopDTO shopDTO) throws BadCredentialsException;

  List<ShopsDTO> getShopsByGPS ( double lat , double lon);

  List<TrafficPackageDTO> getTrafficPackage (File f);
}
