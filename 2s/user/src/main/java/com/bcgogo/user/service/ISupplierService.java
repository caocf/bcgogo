package com.bcgogo.user.service;

import com.bcgogo.common.Result;
import com.bcgogo.config.dto.ImportResult;
import com.bcgogo.config.service.excelimport.ImportContext;
import com.bcgogo.enums.user.RelationChangeEnum;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.product.standardVehicleBrandModel.ShopVehicleBrandModelDTO;
import com.bcgogo.user.dto.CustomerOrSupplierDTO;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.user.merge.MergeResult;
import com.bcgogo.user.merge.MergeSupplierSnap;
import com.bcgogo.user.merge.SearchMergeResult;
import com.bcgogo.user.model.Supplier;
import com.bcgogo.user.model.UserWriter;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-3-11
 * Time: 下午3:22
 * To change this template use File | Settings | File Templates.
 */
public interface ISupplierService {

  public List<SupplierDTO> getSupplierInfoList(Long shopId, String searchKey, int currentPage, int pageSize) throws BcgogoException;

  public ImportResult importSupplierFromExcel(Map map,ImportContext importContext) throws BcgogoException;

  public boolean batchCreateSupplier(Map map,List<SupplierDTO> supplierDTOList) throws BcgogoException;

  public void batchUpdateSupplier(List<SupplierDTO> supplierDTOList);

  public SupplierDTO getSupplierById(long supplierId, Long shopId);

  public  SupplierDTO getSupplierDTONoContact(Long supplyId ,Long shopId);

  Map<Long,SupplierDTO> getSupplierByIdSet(Long shopId, Set<Long> supplierIds);

  /**
   * key 为 supplierShopId
   * @param shopId
   * @param supplierShopId
   * @return
   */
  Map<Long,SupplierDTO> getSupplierBySupplierShopId(Long shopId, Long... supplierShopId);

  /**
   * key 为 本店shopId
   * @param supplierShopId
   * @param nativeShopIds
   * @return
   */
  Map<Long,SupplierDTO> getSupplierByNativeShopIds(Long supplierShopId, Long... nativeShopIds);

  List<SupplierDTO> getShopSuppliers(Long id);

  /**
   * 精确匹配供应商，如果有同名供应商存在，优先取出批发商，其次按照最后使用时间取第一个
   * @param shopId
   * @param supplierName
   * @return
   */
  SupplierDTO getSupplierDTOByPreciseName(Long shopId,String supplierName);

  List<SupplierDTO> getWholesalerByFuzzyName(Long shopId, String wholesalername);

  Result deleteSupplier(Result result,SupplierDTO supplierDTO) throws BcgogoException;

  boolean compareSupplierSameWithHistory(SupplierDTO historySupplierDTO, Long shopId);

  RelationChangeEnum compareSupplierRelationChange(SupplierDTO historySupplierDTO, Long shopId);

  MergeResult mergeSupplierInfo(MergeResult<SupplierDTO,MergeSupplierSnap> result,Long parentId, Long[] childIds) throws Exception;

  SearchMergeResult getMergedSuppliers(SearchMergeResult result,List<Long> supplierIds) throws Exception;

  List<SupplierDTO> getSuppliersByShopIdSendInvitationCode(Long shopId, long startId, int pageSize, Long createTime);

  //供应商名，或者手机，或者座机，或者地址有一个相同的  (不包括自己）
  List<SupplierDTO> getSimilarSupplier(SupplierDTO supplierDTO);
  //更新供应商关联关系，不包括做索引
  Set<Long> cancelSupplierRelation(Long supplierShopId, Long customerShopId) throws Exception;

  ImportResult simpleImportSupplierFromExcel(Map map,ImportContext importContext) throws Exception;

  Map<String,SupplierDTO> getMobileSupplierMapOnlyForMobileCheck(Long shopId);

  Map<String,SupplierDTO> getLandLineSupplierMap(Long shopId);

  Supplier saveOrUpdateSupplierByCsDTO(CustomerOrSupplierDTO csDTO);

  public List<SupplierDTO> getSuppliersByPageAndStart(int pageSize,int start);

  void addCancelRecommendAssociatedCount(Set<Long> customerOrSupplierIds);

  void cancelApplyRecommendAssociated(Set<Long> customerOrSupplierIds);

  //只用于特殊情况，没有shopId的时候，只取出supplier entity to dto 然后，
  // 再根据supplier entity上的shopId 再取SupplierDto  一般情况不建议使用
  SupplierDTO getSupplierNoContactByIdNoShopId(Long supplierId);

  Long[] validateApplySupplierContactMobile(Long shopId, Long... supplerShopId);

  List<Long> deleteSupplierUpdateCustomerRelationStatus(Long customerShopId, Long supplierShopId)throws Exception;

  Result validateSupplierMobiles(Long shopId, Long supplierId, String[] mobiles);

  void addAreaInfoToSupplierDTO(SupplierDTO supplierDTO);

  void saveSupplierVehicleBrandModelRelation(Long shopId, SupplierDTO supplierDTO, Map<Long, ShopVehicleBrandModelDTO> shopVehicleBrandModelDTOMap);

  void saveSupplierVehicleBrandModelRelation(UserWriter writer, Long shopId, SupplierDTO supplierDTO, Map<Long, ShopVehicleBrandModelDTO> shopVehicleBrandModelDTOMap);
}
