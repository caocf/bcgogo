package com.bcgogo.config.service;

import com.bcgogo.common.Pager;
import com.bcgogo.common.Result;
import com.bcgogo.config.dto.*;
import com.bcgogo.config.model.*;
import com.bcgogo.enums.RelationTypes;
import com.bcgogo.enums.config.AttachmentType;
import com.bcgogo.enums.shop.ShopKind;
import com.bcgogo.enums.shop.ShopStatus;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.txn.dto.*;
import com.bcgogo.user.dto.ContactDTO;
import com.bcgogo.user.dto.ValidateImportDataDTO;
import com.bcgogo.user.merge.MergeRecordDTO;
import com.bcgogo.user.merge.MergeResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IConfigService {

  /**
   * 获取Config表中的配置属性。如果内存中不存在则放入内存作为缓存
   * @param name
   * @param shopId
   * @return
   */
  public String getConfig(String name, Long shopId);

  /**
   * 获取Config表中的配置属性，不使用内存缓存。配置表中值更新后获取无延迟，不需重启Tomcat
   * @param name
   * @param shopId
   * @return
   */
  public String getConfigWithoutCache(String name, Long shopId);

  public void setConfig(String name, String value, Long shopId);

  public void saveOrUpdateConfig(ConfigDTO configDTO);

  public List<ConfigDTO> getConfig(String name, String value, Long shopId, Pager pager);

  void deleteConfig(String name, Long shopId);

  public int countConfigs(String name, String value, Long shopId);

  public ShopDTO getStoreManager(String name);

  @Deprecated //by zhangjuntao
  public void activateShop(long shopId, ShopStatus shopStatus) throws BcgogoException, ParseException;

  public void deactivateShop(long shopId) throws BcgogoException;

  public List<Shop> getShop();

  public List<Long> getShopId();

  List<ShopDTO> getActiveShop();

  List<ShopDTO> getShopSuggestion(String name,ShopKind shopKind,int maxRows);

  List<ShopDTO> getAdShops();

  void updateShopList(ShopDTO ... shopDTOs);

  public List<ShopDTO> getActiveShopFromCache();

  List<Shop> getSendInvitationCodeActiveShop();

  List<String> getSendInvitationCodeActiveShopMobile();

  // 已注册
  public List<Shop> getShopByState(int pageNo, int pageSize);

  public int countShopByState();

  //待注册
  public List<Shop> getShopByState1(int pageNo, int pageSize);

  public int countShopByState1();

  public ShopDTO getShopById(long shopId);

  //没有根据shopStatus排除
  public ShopDTO getShopByName(String shopName);

  //模糊查询
  public List<ShopDTO> getShopByObscureName(String name);

  //根据省以下地区获得所有自地区id
  List<Long> getAreaLeafsByParentId(Long parentNo);

  //获得完整的地区信息
  List<Area> getArea(long no);

  public List<AreaDTO> getChildAreaDTOList(Long parentNo);

  public List<Business> getBusinessList(String parentNo);

  public ShopBusinessDTO createShopBusiness(ShopBusinessDTO shopBusinessDTO) throws BcgogoException;

  public List<ShopBusiness> getShopBusinessList(Long shopId);

  public Long countShop();

  public List<ShopDTO> getShopByStoreManagerMobile(String mobile);

  ShopDTO getShopByIdWithoutContacts(Long shopId);

  public ShopDTO getShopById(Long shopId);

  //地区表上传
  public void insertArea(Set dateSet);

  public int countShopByAgentIdAndTime(Long agentId, String startTimeStr, String endTimeStr) throws Exception;

  public String getBuildVersion();

  public byte[] InputStreamToByte(InputStream is) throws IOException;

  public List<ShopUnitDTO> getShopUnit(Long shopId);

  public void updateUnitSort(Long shopId,List<ShopUnitDTO> shopUnitDTOList);

  void batchSave(List<?> objects) throws Exception;

  public void updateSolrThesaurus(MultipartFile srcDic, MultipartFile targetDic, String charset, OutputStream outputStream) throws Exception;

  public void updateOrderUnitSort(Long shopid, SalesOrderDTO orderDTO);

  public void updateOrderUnitSort(Long shopid, RepairOrderDTO orderDTO);

  public void updateOrderUnitSort(Long shopid, PurchaseInventoryDTO orderDTO);

  public void updateOrderUnitSort(Long shopid, PurchaseOrderDTO orderDTO);

  public void updateOrderUnitSort(Long shopid, PurchaseReturnDTO orderDTO);

  public List<ShopUnitDTO> initShopUnit(Long shopId)throws Exception;

  public void saveOrUpdateUnitSort(Long shopId,String... unit)throws Exception;

  public MergeResult saveMergeRecord(MergeResult<?,?> mergeResult) throws BcgogoException;

  public void saveShopCustomerRelation(Long shopId, Long customerId);

  public Long getTempShopIdByCustomerId(Long customerId);

  public Long getTempCustomerIdByShopId(Long shopId);

  public String getCustomerShopStatus(Long customerId);

  public void deleteShopCustomerRelation(Long customerId);

  public void createWholesalerShopRelation(WholesalerShopRelationDTO wholesalerShopRelationDTO);

  public boolean createWholesalerShopRelationByShopRelation(ConfigWriter writer,ShopRelationInviteDTO shopRelationInviteDTO)throws Exception;

  public List<WholesalerShopRelationDTO> getWholesalerShopRelationByWholesalerShopId(Long wholesalerShopId ,List<RelationTypes> relationTypeList);

  public List<WholesalerShopRelationDTO> getWholesalerShopRelationByShopId(Long shopId ,List<RelationTypes> relationTypeList);

  /**查找已经收藏供应商的关联关系
   * @param shopId
   * @param relationTypeList
   * @param wholesalerShopId
   * @return  key 是wholesalerShopId
   */
  public Map<Long, WholesalerShopRelationDTO> getWholesalerShopRelationMapByWholesalerShopId(Long shopId,List<RelationTypes> relationTypeList, Long... wholesalerShopId);

  /**  查找有效的关联关系
   * @param shopId
   * @param customerShopId
   * @return  key customerShopId
   */
  public Map<Long, WholesalerShopRelationDTO> getWholesalerShopRelationMapByCustomerShopId(Long shopId, List<RelationTypes> relationTypeList, Long... customerShopId);

  public WholesalerShopRelationDTO getWholesalerShopRelationDTOByShopId(Long customerShopId, Long wholesalerShopId,List<RelationTypes> relationTypeList);

  public Map<Long,ShopDTO> getShopByShopId(Long... shopId);

  int getMergeRecordCount(MergeRecordDTO mergeRecordIndex);

  List<MergeRecord> getMergeRecords(MergeRecordDTO mergeRecordIndex);

  MergeRecord getMergeRecordDetail(Long shopId,Long parentId,Long childId);

  void initAllShopArea();
  void initAllShopNamePy();
  List<Shop> getShopExcludeTest();

  /**
   * 校验店铺关联客户数量是否超出
   * @param shopId
   * @param toApplyCustomerAmount
   * @return  true 表示超过了
   */
  boolean checkLimitCustomerAmount(Long shopId,Integer toApplyCustomerAmount);

  boolean checkLimitCustomerAmount(ShopDTO shopDTO,Integer toApplyCustomerAmount);

  Map<Long,AreaDTO> getAreaByAreaNo(Set<Long> areaNos);

  String getRegisterInfoByRegisterShopId(Long registerShopId);

  RegisterInfoDTO getRegisterInfoDTOByRegisterShopId(Long registerShopId);

  Result validateShopRegBasicInfo(Long shopId, Long customerId);

  //找到关联供应商店铺的shopIds ，shopId 是customerShopId
  Set<Long> getRelationWholesalerShopIds(Long shopId, List<RelationTypes> relationTypeList);

  //找到关联客户店铺的shopIds ，shopId 是wholesalerShopId
  Set<Long> getRelatedCustomerShopIds(Long wholesalerShopId,List<RelationTypes> relationTypeList);

  List<Long> getBcgogoRecommendSupplierShopIds(Long shopId);

  AttachmentDTO getAttachmentByShopId(Long shopId,AttachmentType attachmentType);

  String getShopAreaInfoByShopDTO(ShopDTO shopDTO);

  String getProvinceAndCityNoByCityName(String cityName,ValidateImportDataDTO validateImportDataDTO);

  ContactDTO[] getShopContactsByShopId(Long shopId);

  /**
   * 获取店铺的经营范围
   * @param shopIdSet
   * @return
   */
  public List<ShopBusinessScopeDTO> getShopBusinessScopeByShopId(Set<Long> shopIdSet);

  List<Long> getShopBusinessScopeProductCategoryIdListByShopId(Long shopId);

  void saveShopBusinessScopeProductCategory(List<ShopBusinessScope> shopBusinessScopeList);

  /**
   * 批量保存店铺的单位 注册时填写商品保存单位 专用
   * @param shopId
   * @param productDTOs
   * @throws Exception
   */
   public void saveOrUpdateUnitSort(Long shopId, ProductDTO[] productDTOs) throws Exception;

   /**
    * 注册时填写经营范围 保存经营范围
    * @param shopDTO
    */
    public void saveShopBusinessScopeFromShopDTO(ShopDTO shopDTO);

  /**
   * 获取明星供应商  shopDTO中只有名字和id
   * @param shopId
   * @return
   */
    List<ShopDTO> getBcgogoRecommendSupplierShop(Long shopId);


    public List<ShopDTO> getShopByIds(Long... shopId);

  /**
   * 更改Config表中TomcatIP值后不需重启
   * @return
   */
  String[] getConfigTomcatIps();

  int countBeFavoured(Long paramShopId);

  ShopDTO getSimpleShopById(Long shopId);

  List<Shop> getTodoBugfixShops();

  String getConfigSocketReceiverIp();

  public List<Long> getShopByShopVersionAndArea(Long[] shopVersionId, Long province, Long city, Long region);

  String getConfigApiTomcatIps();

  List<Long> getAdShopIds(Integer size);

  List<ShopDTO> getRecommendShopByShopArea(Long parentId,Long province,Long city,Long region);

  Map<Long,Integer> countRecommendShopByShopArea(Long province,Long city,Long region,Long... parentId);


  void saveWXImageLib(WXImageLib ...imageLibs);

  WXImageLib getWXImageLib(String name);

}
