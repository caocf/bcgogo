package com.bcgogo.user.service;

import com.bcgogo.common.Result;
import com.bcgogo.config.dto.ImportResult;
import com.bcgogo.config.service.excelimport.ImportContext;
import com.bcgogo.enums.StatProcessStatus;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.txn.dto.*;
import com.bcgogo.user.CustomerResponse;
import com.bcgogo.user.dto.*;
import com.bcgogo.user.merge.MergeCustomerSnap;
import com.bcgogo.user.merge.MergeResult;
import com.bcgogo.user.merge.SearchMergeResult;
import com.bcgogo.user.model.*;
import com.bcgogo.user.model.task.MergeTask;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: monrove
 * Date: 11-12-7
 * Time: 上午11:42
 * To change this template use File | Settings | File Templates.
 */
public interface ICustomerService {

  /**
   * 销售单更新客户联系人信息
   * @param salesOrderDTO
   * @param shopId
   * @param contact
   * @param payTime
   * @throws Exception
   */
  public void updateVehicleAndCustomer(SalesOrderDTO salesOrderDTO, Long shopId, String contact, Long payTime) throws Exception;

  //新增或者更新客户或者车辆信息
  public void handleCustomerForRepairOrder(RepairOrderDTO repairOrderDTO, Long shopId, Long userId);

  //预约单新增或者更新客户信息，车辆信息
  public void handleCustomerForAppointOrder(AppointOrderDTO appointOrderDTO) throws Exception;

  public void batchCreateVehicleModifyLog(List<VehicleModifyLogDTO> vehicleModifyLogDTOs);

  public CustomerResponse findCustomerById(Long customerId,Long shopId) throws Exception;

  public List<CustomerRecordDTO> getCustomerRecordByKey(Long shopId, String key, int rowStart, int pageSize) throws BcgogoException;

  public List<CustomerRecordDTO> getCustomerRecordInfoByKey(Long shopId, String key, int rowStart, int pageSize) throws BcgogoException;

  public List<CustomerDTO> getCustomerByIds(List<Long> customerIds) throws BcgogoException;

  public CustomerDTO getCustomerById(Long CustomerId);

  public CustomerDTO getCustomerById(Long CustomerId, Long shopId);

//  public List<String> getCustomersPhonesByShopPlanDTO(ShopPlanDTO shopPlanDTO);

  public List<String> getCustomersPhonesByShopId(Long shopId);

  List<CustomerDTO> getCustomersByShopIdSendInvitationCode(Long shopId, long startId, int pageSize, Long createTime);

//  @Deprecated
//  public void saveSmsJob(ShopPlanDTO shopPlanDTO);

  /**
   * 保存车辆预约服务信息
   * 客户ID与车辆ID是必须的，否则抛出异常
   *
   * @param customerId   //客户ID 必需
   * @param vehicleId    //车辆ID 必需
   * @param maintainTime //预约保养时间
   * @param insureTime   //预约保险时间
   * @param examineTime  //预约验车时间
   * @author wjl
   */
  public void saveVehicleAppointment(
      Long customerId, Long vehicleId, Long maintainTime, Long insureTime, Long examineTime) throws BcgogoException;

  /**
   * 查询车辆预约服务信息
   * 客户ID与车辆ID是必须的，否则抛出异常
   *
   * @param customerId //客户ID 必需
   * @param vehicleId  //车辆ID 必需
   * @return
   * @throws BcgogoException
   * @author wjl
   */
  public CustomerVehicle getVehicleAppointment(Long customerId, Long vehicleId) throws BcgogoException;

  public ImportResult importCustomerFromExcel(ImportContext importContext) throws Exception;

  public void updateCustomerAndCustomerRecord(UserWriter writer, MemberCardOrderDTO memberCardOrderDTO) throws Exception;

  /**
   * 当月新增用户数
   *
   * @param shopId
   * @return
   * @author zhangchuanlong
   */
  public Long getCountOfCustomerByMonth(Long shopId) throws BcgogoException, ParseException;

  /**
   * 当天新增用户数
   *
   * @param shopId
   * @return
   * @author zhang chuanlong
   */
  public Long  getCountOfCustomerByToay(Long shopId) throws ParseException;

  /**
   * 昨日新增用户数
   *
   * @param shopId
   * @return
   * @auther zhangchuanlong
   */
  public Long getCountOfCustomerByYesterDay(Long shopId) throws ParseException;

  /**
   * key 为 customerShopId
   * @param shopId
   * @param customerShopId
   * @return
   */
  Map<Long,CustomerDTO> getCustomerBySupplierShopId(Long shopId, Long... customerShopId);

  /**
   * 客户 or supplier 下拉建议
   * 在搭建solr之前临时解决
   */
  @Deprecated
  public List<CustomerOrSupplierDTO> getCustomerOrSupplierSuggestion(Long shopId, String keyWord);

  Map<Long, CustomerDTO> getCustomerByIdSet(Long shopId, Set<Long> customerIds);

  Map<Long, UserDTO> getExecutorByIdSet(Long shopId, Set<Long> executorIds);

  Map<Long, MemberCardDTO> getMemberCardByIds(Long shopId, Set<Long> carIds);


  /**
   * count 拥有的车辆数
   *
   * @param customerId
   * @return
   */
  int countVehicleByCustomerId(Long customerId);

  public void deleteCustomer(Long shopId,Long customerId);

  //作废洗车单后 更新客户相关信息
  void updateCustomerAfterRepealWashOrder(WashBeautyOrderDTO washBeautyOrderDTO, ReceivableDTO receivableDTO) throws Exception;

  public SearchMergeResult getMergedCustomers(SearchMergeResult result, List<Long> customerIdList) throws Exception;

  public CustomerRecord getUniqueCustomerRecordByCustomerId(Long shopId,Long customerId);

  public MergeResult mergeCustomerInfo(MergeResult<CustomerDTO,MergeCustomerSnap> mergeResult, Long parentId,Long[] childIds) throws Exception;

  public MergeTask getMergeTaskOneByOne();

  public void deleteMergeTask(Long taskId) throws BcgogoException;

  List<VehicleModifyLog> getVehicleModifyLogByStatus(StatProcessStatus[] statProcessStatuses);

  void batchUpdateVehicleModifyLogStatus(List<VehicleModifyLog> toProcessLogs, StatProcessStatus done);

  public CustomerRecordDTO getCustomerRecordDTOByCustomerId(Long shopId,Long customerId);

  public void saveExceptionMergeTask(Long taskId) throws BcgogoException;

  public List<CustomerDTO> getRelatedCustomerByMatchedName(Long shopId,String customerName);

  public List<CustomerDTO> getRelatedCustomersByShopId(Long wholeSalerShopId);

  List<CustomerDTO> getShopRelatedCustomer(Long wholeSalerShopId,Long... customerIds);

  int countRelatedCustomersByShopId(Long wholeSalerShopId);

  public List<Long> getRelatedCustomerIdListByShopId(Long wholeSalerShopId);

  public List<SupplierDTO> getWholeSalersByCustomerShopId(Long shopId);

  public Result saveOrUpdateCustomerInfo(SalesReturnDTO salesReturnDTO) throws Exception;

  public List<Customer> getCustomerByNameAndMobile(long shopId, String name, String mobile);

  void updateCustomerRecordForRepairOrder(RepairOrderDTO repairOrderDTO);

  boolean compareCustomerSameWithHistory(CustomerDTO customerDTO, Long shopId);

  void saveOrUpdateCustomerVehicle(InsuranceOrderDTO insuranceOrderDTO)throws Exception;

  List<CustomerDTO> getCustomers(CustomerDTO customerIndex);

  public List<Customer> getAllCustomerByNameAndMobile(long shopId, String name, String mobile);

  public List<Supplier> getAllSupplierByNameAndMobile(long shopId, String name, String mobile);


  void batchUpdateCustomer(List<CustomerDTO> customerDTOList);

  List<CustomerDTO> getCustomerByShopId(Long shopId,int start,int pageSize);

   //客户名，或者手机，或者座机，或者地址有一个相同的  (不包括自己）
  List<CustomerDTO> getSimilarCustomer(CustomerDTO customerDTO);

  List<Long> cancelCustomerRelationAndReindex(Long customerShopId, Long supplierShopId);

  public ImportResult simpleImportCustomerFromExcel(ImportContext importContext) throws Exception;

  public Map<String,CustomerDTO> getMobileCustomerMapOnlyForMobileCheck(Long shopId);

  public Map<String,CustomerDTO> getLandLineCustomerMap(Long shopId);

  Result saveOrUpdateCustomerByCsDTO(Result result,CustomerOrSupplierDTO csDTO) throws BcgogoException;

  public Map<Long,CustomerRecordDTO> getCustomerRecordMap(Long shopId,Long... customerId) throws BcgogoException;

  Map<Long, MemberDTO> getCustomerMemberMap(Long shopId, Long... customerId) throws BcgogoException;


  List<Long> getCustomerIdList(Long shopId, int start, int pageSize);

  Map<Long, List<VehicleDTO>> getCustomerLicenseNosForReindex(Long shopId, List<Long> ids);

  public List<CustomerDTO> getCustomerByPageSizeAndStart(int pageSize,int start);

  /**
   * 判断这个客户是否重复 如果存在返回第一个符合的客户DTO
   *
   * @param shopId
   * @param name   客户名
   * @param mobile 客户手机
   * @param phone  客户座机
   * @return
   */
  public CustomerDTO isCustomerExist(Long shopId, String name, String mobile, String phone) throws BcgogoException;

  void addCancelRecommendAssociatedCount(Set<Long> customerOrSupplierIds);

  void cancelApplyRecommendAssociated(Set<Long> customerOrSupplierIds);

  Long[] validateApplyCustomerContactMobile(Long shopId, Long... customerShopId);

  void validateAddCustomer(CustomerRecordDTO customerRecordDTO, Result result) throws BcgogoException;

  int countCustomerByName(String name, Long customerId, Long shopId);

  int countCustomerByNameAndMobile(String name, String mobile, Long customerId, Long shopId);

  //删除客户的时候，如果对方供应商还是关联关系，更新为收藏
  List<Long> deleteCustomerUpdateSupplierRelationStatus(Long shopId, Long supplierShopId) throws Exception;

  Set<Long> getCustomerShopIds(Long shopId);

  public Map<String,List<CustomerDTO>> getCustomerByMobiles(Long shopId, Set<String> mobiles, Set<Long> excludeCustomerIds);

  Result validateCustomerMobiles(Long shopId, Long customerId, String... mobiles);

  void addAreaInfoToCustomerDTO(CustomerDTO customerDTO);

  void saveCustomAppointServiceDTO(Long shopId, Long customerId, Long vehicleId, AppointServiceDTO[] appointServiceDTOs);

  //create and relation matching
  Long createOrMatchingCustomerByAppUserNo(String appUserNo, Long shopId) throws BcgogoException;

  Long createOrMatchingCustomerByAppUserNo(String appUserNo, Long shopId, UserWriter writer) throws BcgogoException;

  public int countCustomerRecordByKey(Long shopId, String key);

  public String[] getCustomerOrSupplierId(String[] customerOrSupplierIds);

  List<String> getAppUserMobileByContactMobile(Long shopId, String mobile);
}
