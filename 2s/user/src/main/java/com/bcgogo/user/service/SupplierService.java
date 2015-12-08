package com.bcgogo.user.service;

import com.bcgogo.common.Result;
import com.bcgogo.config.cache.AreaCacheManager;
import com.bcgogo.config.dto.AreaDTO;
import com.bcgogo.config.dto.ImportResult;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IApplyService;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IImportService;
import com.bcgogo.config.service.excelimport.CheckResult;
import com.bcgogo.config.service.excelimport.ImportContext;
import com.bcgogo.enums.CustomerStatus;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.RelationTypes;
import com.bcgogo.enums.common.ObjectStatus;
import com.bcgogo.enums.config.VehicleSelectBrandModel;
import com.bcgogo.enums.user.RelationChangeEnum;
import com.bcgogo.enums.user.VehicleBrandModelDataType;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.product.standardVehicleBrandModel.ShopVehicleBrandModelDTO;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.stat.dto.SupplierRecordDTO;
import com.bcgogo.user.MergeType;
import com.bcgogo.user.dto.*;
import com.bcgogo.user.merge.MergeResult;
import com.bcgogo.user.merge.MergeSupplierSnap;
import com.bcgogo.user.merge.SearchMergeResult;
import com.bcgogo.user.model.*;
import com.bcgogo.user.model.task.MergeTask;
import com.bcgogo.user.service.excelimport.supplier.SupplierImporter;
import com.bcgogo.utils.ArrayUtil;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.TxnConstant;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-3-11
 * Time: 下午3:23
 * To change this template use File | Settings | File Templates.
 */
@Component
public class SupplierService implements ISupplierService {

  @Autowired
  private UserDaoManager userDaoManager;
  @Autowired
  private SupplierImporter supplierImporter;
  public static final Logger LOG = LoggerFactory.getLogger(SupplierService.class);

  /**
   * 根据关键字获取最近交易过的供应商列表信息
   *
   * @param shopId
   * @param searchKey
   * @param currentPage
   * @param pageSize
   * @return
   */
  public List<SupplierDTO> getSupplierInfoList(Long shopId, String searchKey, int currentPage, int pageSize) throws BcgogoException {
    UserWriter writer = userDaoManager.getWriter();
    List<SupplierDTO> supplierDTOList = writer.getRecentlyTradeSupppliers(shopId, searchKey, currentPage, pageSize);
    return supplierDTOList;
  }

  /**
   * 从excel中导入客户
   *
   * @param importContext
   * @return
   * @throws BcgogoException
   */
  @Override
  public ImportResult importSupplierFromExcel(Map map,ImportContext importContext) throws BcgogoException {
    IImportService importService = ServiceManager.getService(IImportService.class);
    ImportResult importResult = null;

    //1.解析数据
    importService.parseData(importContext);

    //2.校验数据
    CheckResult checkResult = supplierImporter.checkData(importContext);
    if (!checkResult.isPass()) {
      importResult = new ImportResult();
      importResult.setSuccess(false);
      importResult.setMessage(checkResult.getMessage());
      return importResult;
    }

    //3.保存数据
    importResult = supplierImporter.importData(map,importContext);

    return importResult;
  }

  /**
   * 批量创建供应商
   *
   * @param supplierDTOList
   * @return
   * @throws BcgogoException
   */
  @Override
  public boolean batchCreateSupplier(Map map,List<SupplierDTO> supplierDTOList) throws BcgogoException {
    if (supplierDTOList == null || supplierDTOList.isEmpty()) {
      return false;
    }
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    Supplier supplier = null;
    try {
      for (SupplierDTO supplierDTO : supplierDTOList) {
        if (supplierDTO == null) {
          continue;
        }
        supplier = new Supplier();
        supplier.fromDTO(supplierDTO);
        writer.save(supplier);
        supplierDTO.setId(supplier.getId());

        ContactDTO contactDTO = new ContactDTO(null, supplierDTO.getContact(), supplierDTO.getMobile(), supplierDTO.getEmail(), supplierDTO.getQq(),
            null, supplier.getId(), supplier.getShopId(), 0, 1, 1, 0);
        Contact contact = new Contact().fromDTO(contactDTO);
        writer.save(contact);

        SupplierRecordDTO supplierRecordDTO = supplierDTO.getSupplierRecordDTO();
        supplierRecordDTO.setSupplierId(supplierDTO.getId());

        List<SupplierRecordDTO> supplierRecordDTOList = (List<SupplierRecordDTO>)map.get("supplierRecordDTOList");

        if(null == supplierRecordDTOList)
        {
          supplierRecordDTOList = new ArrayList<SupplierRecordDTO>();
        }

        supplierRecordDTOList.add(supplierRecordDTO);

        map.put("supplierRecordDTOList",supplierRecordDTOList);
      }
      writer.commit(status);
    } catch (Exception e) {
      throw new BcgogoException(e);
    } finally {
      writer.rollback(status);
    }
    return true;
  }

  @Override
  public void batchUpdateSupplier(List<SupplierDTO> supplierDTOList){
    if (CollectionUtil.isEmpty(supplierDTOList)) return;
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Supplier supplier;
      for (SupplierDTO dto : supplierDTOList) {
        supplier = writer.getById(Supplier.class, dto.getId());
        supplier.fromDTO(dto);
        writer.save(supplier);
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public SupplierDTO getSupplierById(long supplierId,Long shopId) {
    UserWriter writer = userDaoManager.getWriter();
    Supplier supplier = CollectionUtil.getFirst(writer.getSupplierById(shopId, supplierId));
    if (supplier != null) {
      SupplierDTO  supplierDTO = supplier.toDTO();
      // add by zhuj 新增联系人
      List<Contact> contacts = writer.getContactByCusOrSupOrNameOrMobile(null, supplierId, shopId, null, null);
      if (!CollectionUtils.isEmpty(contacts)) {
        ContactDTO[] contactDTOs = new ContactDTO[contacts.size()];
        for (int i = 0; i < contacts.size(); i++) {
          contactDTOs[i] = contacts.get(i).toDTO();
        }
        supplierDTO.setContacts(contactDTOs);
      }
      return supplierDTO;
    }
    return null;
  }

  @Override
  public SupplierDTO getSupplierDTONoContact(Long supplyId, Long shopId) {
    if(shopId == null || supplyId == null){
      return  null;
    }
    UserWriter writer = userDaoManager.getWriter();
    Supplier supplier = CollectionUtil.uniqueResult(writer.getSupplierById(shopId,supplyId));
    if (supplier != null) {
      return supplier.toDTO();
    }
    return null;
  }

  @Override
  public Result deleteSupplier(Result result,SupplierDTO supplierDTO) throws BcgogoException {
    if (supplierDTO == null || supplierDTO.getId() == null) {
      result.setSuccess(false);
      result.setMsg("供应商不存在或已被删除！");
      return result;
    }
    UserWriter userWriter = userDaoManager.getWriter();
    Supplier supplier = userWriter.getById(Supplier.class, supplierDTO.getId());
    if (supplier== null||CustomerStatus.DISABLED.equals(supplier.getStatus())) {
      result.setSuccess(false);
      result.setMsg("供应商不存在或已被删除！");
      return result;
    }
    Object status=userWriter.begin();
    try{
      Long relateCustomerId = supplier.getCustomerId();
      supplier.setStatus(CustomerStatus.DISABLED);
      supplier.setIdentity(null);
      supplier.setCustomerId(null);
      userWriter.update(supplier);
      if(relateCustomerId != null) {
        CustomerDTO customerDTO =  ServiceManager.getService(IUserService.class).getCustomerById(relateCustomerId);
        customerDTO.setSupplierId(null);
        customerDTO.setIdentity(null);
        ServiceManager.getService(IUserService.class).updateCustomer(customerDTO);
      }
      userWriter.commit(status);
      return result;
    }catch (Exception e){
      throw new BcgogoException(e.getMessage());
    }finally {
      userWriter.rollback(status);
    }
  }

  /**
   * 比较供应商信息与DTO中信息是否一致。比较的字段包括：
   * name, contact, mobile, landline, address
   * @param historySupplierDTO
   * @param shopId
   * @return
   */
  @Override
  public boolean compareSupplierSameWithHistory(SupplierDTO historySupplierDTO, Long shopId) {
    if(historySupplierDTO == null){
      return false;
    }
    IUserService userService = ServiceManager.getService(IUserService.class);
    List<SupplierDTO> supplierDTOs = userService.getSupplierById(shopId, historySupplierDTO.getId());
    if(CollectionUtils.isEmpty(supplierDTOs)){
      return false;
    }
    SupplierDTO supplierDTO = supplierDTOs.get(0);
    return supplierDTO.compareHistory(historySupplierDTO);
  }

  /**
   * @param historySupplierDTO
   * @param shopId
   * @return
   */
  @Override
  public RelationChangeEnum compareSupplierRelationChange(SupplierDTO historySupplierDTO, Long shopId) {
    if(historySupplierDTO == null){
      return RelationChangeEnum.UNCHANGED;
    }
    IUserService userService = ServiceManager.getService(IUserService.class);
    SupplierDTO supplierDTO = CollectionUtil.getFirst(userService.getSupplierById(shopId, historySupplierDTO.getId()));
    return supplierDTO.compareRelationHistory(historySupplierDTO);
  }

  public List<SupplierDTO> getWholesalerByFuzzyName(Long shopId, String wholesalername) {
    UserWriter writer = userDaoManager.getWriter();
    List<SupplierDTO> supplierDTOs=new ArrayList<SupplierDTO>();
    if(StringUtils.isBlank(wholesalername)) return supplierDTOs;
    for(Supplier supplier:writer.getWholesalerByFuzzyName(shopId, wholesalername)){
      if(supplier==null){
        continue;
      }
      supplierDTOs.add(supplier.toDTO());
    }
    return supplierDTOs;
  }

  @Override
  public Map<Long, SupplierDTO> getSupplierByIdSet(Long shopId, Set<Long> supplierIds) {
    UserWriter writer = userDaoManager.getWriter();
    return writer.getSupplierByIdSet(shopId, supplierIds);
  }

  @Override
  public Map<Long, SupplierDTO> getSupplierBySupplierShopId(Long shopId, Long... supplierShopId) {
    UserWriter writer = userDaoManager.getWriter();
    return writer.getSupplierBySupplierShopId(shopId, supplierShopId);
  }

  @Override
  public Map<Long, SupplierDTO> getSupplierByNativeShopIds(Long supplierShopId, Long... nativeShopIds) {
    Map<Long, SupplierDTO> supplierDTOMap = new HashMap<Long, SupplierDTO>();
    if(supplierShopId == null || ArrayUtils.isEmpty(nativeShopIds)){
      return supplierDTOMap;
    }
    UserWriter writer = userDaoManager.getWriter();
    List<Supplier> suppliers = writer.getSupplierByNativeShopIds(supplierShopId, nativeShopIds);
    if(CollectionUtils.isNotEmpty(suppliers)){
      for(Supplier supplier  : suppliers){
        supplierDTOMap.put(supplier.getShopId(),supplier.toDTO());
      }
    }
    return supplierDTOMap;
  }


  @Override
  public List<SupplierDTO> getShopSuppliers(Long shopId) {
    if(shopId == null) return null;
    UserWriter writer = userDaoManager.getWriter();
    List<SupplierDTO> list = writer.getShopSuppliers(shopId);
    return list;
  }

  @Override
  public SupplierDTO getSupplierDTOByPreciseName(Long shopId, String supplierName) {
    SupplierDTO supplierDTO = null;
    if (shopId == null || StringUtils.isEmpty(supplierName)) {
      return supplierDTO;
    }
    UserWriter writer = userDaoManager.getWriter();
    List<Supplier> suppliers = writer.getSupplierByNameAndShopId(shopId, supplierName);
    boolean isWholesalerSupplier = false;
    if (CollectionUtils.isNotEmpty(suppliers)) {
      for (Supplier supplier : suppliers) {
        if (supplier.getSupplierShopId() != null) {
          supplierDTO = supplier.toDTO();
          isWholesalerSupplier = true;
          break;
        }
      }
      if (!isWholesalerSupplier) {
        supplierDTO = suppliers.get(0).toDTO();
      }
    }
    return supplierDTO;
  }

  public MergeResult mergeSupplierInfo(MergeResult<SupplierDTO,MergeSupplierSnap> result,Long parentId, Long[] childIds) throws Exception {
    UserWriter userWriter=userDaoManager.getWriter();
    SupplierDTO parent=result.getCustomerOrSupplierDTO();
    MergeSupplierSnap mergeSnap=new MergeSupplierSnap();
    mergeSnap.setShopId(result.getShopId());
    mergeSnap.setParentId(parent.getId());
    mergeSnap.setParentName(parent.getName());
    mergeSnap.setParent(parent);
    mergeSnap.setOperatorId(result.getUserId());
    UserDTO userDTO=userWriter.getUserDTO(result.getShopId(), result.getUserId());
    if(userDTO!=null) {
      mergeSnap.setOperator(userDTO.getName());
    }
    Object status=userWriter.begin();
    try{
      MergeSupplierSnap mergeSnapClone=null;
      for(Long childId:childIds){
        mergeSnapClone =mergeSnap.clone();
        result.getMergeSnapMap().put(childId,mergeSnapClone);
        mergeSupplier(result, parent, childId);
        userWriter.save(MergeTask.createTask(result.getShopId(), parentId, childId, MergeType.MERGE_SUPPLIER));
      }
      userWriter.commit(status);
      result.setCustomerOrSupplierDTO(parent);
      return result;
    }catch (Exception e){
      LOG.error("合并供应商出现异常！");
      throw new BcgogoException(e.getMessage(), e);
    }finally {
      userWriter.rollback(status);
    }
  }

  private MergeResult mergeSupplier(MergeResult<SupplierDTO,MergeSupplierSnap> result,SupplierDTO parent, Long childId) throws BcgogoException {
    ISearchService searchService=ServiceManager.getService(ISearchService.class);
    UserWriter userWriter = userDaoManager.getWriter();
    Supplier child= CollectionUtil.uniqueResult(userWriter.getSupplierById(result.getShopId(),childId));
    if(child==null||CustomerStatus.DISABLED.equals(child.getStatus())){
      result.setCustomerOrSupplierIdStr((String.valueOf(childId)));
      result.setSuccess(false);
      result.setMsg("被合并的供应商不存在，或被删除，或被合并！");
      return result;
    }
    MergeSupplierSnap mergeSnap=result.getMergeSnapMap().get(childId);
    mergeSnap.setChildId(child.getId());
    mergeSnap.setChildName(child.getName());
    SupplierDTO childDTO=child.toDTO();
    Map settlementTypeMap = TxnConstant.getSettlementTypeMap(result.getLocale());
    Map inoiceCatagoryMap= TxnConstant.getInvoiceCatagoryMap(result.getLocale());
    Map<String, String> areaMap = TxnConstant.getAreaMap(result.getLocale());
    Map<String, String> customerTypeMap = TxnConstant.getCustomerTypeMap(result.getLocale());
    childDTO.setSettlementType(String.valueOf(settlementTypeMap.get(String.valueOf(childDTO.getSettlementTypeId()))));
    childDTO.setInvoiceCategory(String.valueOf(inoiceCatagoryMap.get(String.valueOf(childDTO.getInvoiceCategoryId()))));
    childDTO.setAreaStr(String.valueOf(areaMap.get(String.valueOf(childDTO.getAreaId()))));
    childDTO.setCategoryStr(String.valueOf(customerTypeMap.get(String.valueOf(childDTO.getCategory()))));
    childDTO.setCountSupplierReturn(searchService.countReturn(childDTO.getShopId(),childDTO.getId(), OrderTypes.RETURN, OrderStatus.SETTLED));
    setSupplierContacts(childDTO.getId(),userWriter,childDTO); // add by zhuj
    mergeSnap.setChild(childDTO);
    child.setParentId(parent.getId());
    child.setStatus(CustomerStatus.DISABLED);
    userWriter.update(child);
    return result;
  }

  private void setSupplierContacts(long supplierId, UserWriter writer, SupplierDTO supplierDTO) {
    // add by zhuj 联系人列表
    ContactDTO[] contactDTOs = new ContactDTO[3];
    List<Contact> contactList = writer.getContactByCusOrSupOrNameOrMobile(null, supplierId, supplierDTO.getShopId(), null, null);
    if (!CollectionUtils.isEmpty(contactList)) {
      int size = contactList.size();
      if (size > 3) {
        LOG.error("supplier's contactlist is over 3,supplierId is" + supplierId);
        size = 3;
      }
      for (int i = 0; i < size; i++) {
        ContactDTO contactDTO = contactList.get(i).toDTO();
        contactDTOs[i] = contactDTO;
        if (contactDTO.getIsMainContact()!=null && contactDTO.getIsMainContact()== 1) {
          supplierDTO.setContactId(contactDTO.getId());
          supplierDTO.setContactIdStr(String.valueOf(contactDTO.getId()));
          supplierDTO.setContact(contactDTO.getName());
          supplierDTO.setMobile(contactDTO.getMobile());
          supplierDTO.setEmail(contactDTO.getEmail());
          supplierDTO.setQq(contactDTO.getQq());
        }
      }

    }

    supplierDTO.setContacts(contactDTOs);
  }

  public SearchMergeResult getMergedSuppliers(SearchMergeResult result,List<Long> supplierIds) throws Exception {
    IUserService userService=ServiceManager.getService(IUserService.class);
    Map settlementTypeMap = TxnConstant.getSettlementTypeMap(result.getLocale());
    Map inoiceCatagoryMap= TxnConstant.getInvoiceCatagoryMap(result.getLocale());
    Map<String, String> customerTypeMap = TxnConstant.getCustomerTypeMap(result.getLocale());
    SupplierDTO supplierDTO=null;
    SupplierRecordDTO supplierRecordDTO=null;
    List<SupplierDTO> supplierDTOs=new ArrayList<SupplierDTO>();
    for(Long supplierId:supplierIds){
      supplierDTO = userService.getSupplierById(supplierId);
      if(supplierDTO==null){
        continue;
      }
      if(CustomerStatus.DISABLED.equals(supplierDTO.getStatus())){
        result.setMsg(false,"其中一个被删除或者合并，不能合并。");
        return result;
      }
      if("isCustomer".equals(supplierDTO.getIdentity()) &&supplierDTO.getCustomerId()!=null){
        result.setSuccess(false);
        result.setMsg("当前供应商同时是客户,不能进行合并！！！");
        return result;
      }
      supplierDTO.setSettlementType(String.valueOf(settlementTypeMap.get(String.valueOf(supplierDTO.getSettlementTypeId()))));
      supplierDTO.setInvoiceCategory(String.valueOf(inoiceCatagoryMap.get(String.valueOf(supplierDTO.getInvoiceCategoryId()))));
      supplierDTO.setCategoryStr(String.valueOf(customerTypeMap.get(String.valueOf(supplierDTO.getCategory()))));
      supplierDTOs.add(supplierDTO);
    }
    result.setResults(supplierDTOs);
    return result;
  }

  @Override
  public List<SupplierDTO> getSuppliersByShopIdSendInvitationCode(Long shopId, long startId, int pageSize, Long createTime) {
    UserWriter writer = userDaoManager.getWriter();
    List<SupplierDTO> supplierDTOList = new ArrayList<SupplierDTO>();
    List<Supplier> supplierList = writer.getSuppliersByShopIdSendInvitationCode(shopId, startId, pageSize, createTime);
    if(CollectionUtils.isEmpty(supplierList)){
      return new ArrayList<SupplierDTO>();
    }
    Set<Long> ids = new HashSet<Long>();
    for(Supplier supplier:supplierList) {
      ids.add(supplier.getId());
      supplierDTOList.add(supplier.toDTO());
    }
    List<Contact> contactList;
    List<ContactDTO> contactDTOList;
    Map<Long, List<Contact>> contactMap = writer.getContactsBySupIds(new ArrayList<Long>(ids));
    for (SupplierDTO dto : supplierDTOList) {
      contactList = contactMap.get(dto.getId());
      if (CollectionUtil.isNotEmpty(contactList)) {
        contactDTOList = new ArrayList<ContactDTO>();
        for (Contact contact : contactList) {
          contactDTOList.add(contact.toDTO());
        }
        dto.setContacts(contactDTOList.toArray(new ContactDTO[contactDTOList.size()]));
      }
    }
    return supplierDTOList;
  }

  @Override
  public List<SupplierDTO> getSimilarSupplier(SupplierDTO supplierDTO) {
    List<SupplierDTO> supplierDTOs = new ArrayList<SupplierDTO>();
    List<Supplier> suppliers = userDaoManager.getWriter().getSimilarSupplier(supplierDTO);
    if (CollectionUtil.isNotEmpty(suppliers)) {
      for (Supplier supplier : suppliers) {
        supplierDTOs.add(supplier.toDTO());
      }
    }
    return supplierDTOs;
  }

  @Override
  public Set<Long> cancelSupplierRelation(Long supplierShopId, Long customerShopId) throws Exception{
    Set<Long> updatedSupplierIds = new HashSet<Long>();
    if (customerShopId == null || supplierShopId == null) {
      return updatedSupplierIds;
    }
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      List<Supplier> suppliers = writer.getSupplierBySupplierShopIds(customerShopId, supplierShopId);
      if (CollectionUtil.isNotEmpty(suppliers)) {
        for (Supplier supplier : suppliers) {
          supplier.setRelationType(RelationTypes.UNRELATED);
          supplier.setSupplierShopId(null);
          writer.update(supplier);
          updatedSupplierIds.add(supplier.getId());
        }
        writer.commit(status);
      }
      return  updatedSupplierIds;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public ImportResult simpleImportSupplierFromExcel(Map map,ImportContext importContext) throws Exception
  {
    IImportService importService = ServiceManager.getService(IImportService.class);
    ImportResult importResult = null;

    //1.解析数据
    importService.directParseData(importContext);

    //2.校验数据
    CheckResult checkResult = supplierImporter.checkData(importContext);
    if (!checkResult.isPass()) {
      importResult = new ImportResult();
      importResult.setSuccess(false);
      importResult.setMessage(checkResult.getMessage());
      return importResult;
    }

    //3.保存数据
    importResult = supplierImporter.importData(map,importContext);

    return importResult;
  }

  @Override
  public Map<String,SupplierDTO> getMobileSupplierMapOnlyForMobileCheck(Long shopId){
    Map<String,SupplierDTO> map = new HashMap<String,SupplierDTO>();
    if(null == shopId){
      return map;
    }
    UserWriter writer = userDaoManager.getWriter();
    List<Supplier> supplierList = writer.getSuppliersByShopId(shopId);
    if(CollectionUtils.isEmpty(supplierList)){
      return map;
    }
    Set<Long> mobileSet = new HashSet<Long>();
    for(Supplier supplier : supplierList){
      mobileSet.add(supplier.getId());
      map.put(supplier.getMobile(),supplier.toDTO());
    }
    Map<Long, List<Contact>> contactMap = writer.getContactsBySupIds(new ArrayList<Long>(mobileSet));
    List<Contact> contactList = new ArrayList<Contact>();
    SupplierDTO supplierDTO;
    for (Supplier supplier : supplierList) {
      if(contactMap != null) {
        contactList = contactMap.get(supplier.getId());
      }
      supplierDTO = supplier.toDTO();
      if (CollectionUtil.isNotEmpty(contactList)) {
        for (Contact c : contactList) {
          if (StringUtils.isBlank(c.getMobile())) {
            continue;
          }
          if (map.get(c.getMobile()) != null) {
            continue;
          }
          map.put(c.getMobile(), supplierDTO);
        }
      }
    }
    return map;
  }

  @Override
  public Map<String, SupplierDTO> getLandLineSupplierMap(Long shopId) {
    Map<String, SupplierDTO> map = new HashMap<String, SupplierDTO>();
    if (null == shopId) {
      return map;
    }
    UserWriter writer = userDaoManager.getWriter();
    List<Supplier> supplierList = writer.getSuppliersByShopId(shopId);
    if (CollectionUtils.isEmpty(supplierList)) {
      return map;
    }
    for (Supplier supplier : supplierList) {
      if (org.apache.commons.lang.StringUtils.isBlank(supplier.getLandLine())) {
        continue;
      }
      if (map.get(supplier.getLandLine()) != null) {
        continue;
      }
      map.put(supplier.getLandLine(), supplier.toDTO());
    }
    return map;
  }

  public Supplier saveOrUpdateSupplierByCsDTO(CustomerOrSupplierDTO csDTO){
    UserWriter writer=userDaoManager.getWriter();
    Object status=writer.begin();
    Supplier supplier=null;
    if(csDTO.getCustomerOrSupplierId()!=null){
      supplier=CollectionUtil.uniqueResult(writer.getSupplierById(csDTO.getShopId(),csDTO.getCustomerOrSupplierId()));
    }
    if(supplier==null){
      supplier=new Supplier();
    }
    try{
      supplier.fromCustomerOrSupplierDTO(csDTO);
      writer.saveOrUpdate(supplier);
      writer.commit(status);
      return supplier;
    }finally {
      writer.rollback(status);
    }
  }

  @Override
  public List<SupplierDTO> getSuppliersByPageAndStart(int pageSize, int start) {
    List<SupplierDTO> supplierDTOs = new ArrayList<SupplierDTO>();
    UserWriter writer=userDaoManager.getWriter();
    List<Supplier> suppliers = writer.getSuppliersByPage(pageSize,start);
    if (!CollectionUtils.isEmpty(suppliers)){
      for (Supplier supplier:suppliers){
        supplierDTOs.add(supplier.toDTO());
      }
    }
    return supplierDTOs;
  }

  @Override
  public void addCancelRecommendAssociatedCount(Set<Long> supplierIds) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      List<Supplier> suppliers = writer.getSupplierByIds(supplierIds);
      if (CollectionUtil.isNotEmpty(suppliers)) {
        for (Supplier supplier : suppliers) {
          supplier.addCancelRecommendAssociatedCount();
          writer.update(supplier);
        }
        writer.commit(status);
      }
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void cancelApplyRecommendAssociated(Set<Long> supplierIds) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      List<Supplier> suppliers = writer.getSupplierByIds(supplierIds);
      if (CollectionUtil.isNotEmpty(suppliers)) {
        for (Supplier supplier : suppliers) {
          supplier.setCancelRecommendAssociatedCount(-1);
          writer.update(supplier);
        }
        writer.commit(status);
      }
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public SupplierDTO getSupplierNoContactByIdNoShopId(Long supplierId) {
    if(supplierId == null){
      return  null;
    }
    Supplier supplier = userDaoManager.getWriter().getById(Supplier.class,supplierId);
    if(supplier != null && supplier.getShopId() != null){
      return supplier.toDTO();
    }
    return null;
  }

  @Override
  public Long[] validateApplySupplierContactMobile(Long shopId, Long... supplerShopIds) {
    if (ArrayUtil.isEmpty(supplerShopIds)) return null;
    UserWriter writer = userDaoManager.getWriter();
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    List<Long> supplierShopIdList = new ArrayList<Long>(Arrays.asList(supplerShopIds));
    Iterator<Long> iterator = supplierShopIdList.iterator();
    Long supplierShopId;
    ShopDTO shopDTO;
    while (iterator.hasNext()) {
      supplierShopId = iterator.next();
      shopDTO = configService.getShopById(supplierShopId);
      if (shopDTO == null) {
        iterator.remove();
        continue;
      }
      if (CollectionUtil.isNotEmpty(shopDTO.getContactMobiles())) {
        if (writer.getRelatedSupplierByContactMobiles(new HashSet<String>(shopDTO.getContactMobiles()), shopId) != null) {
          iterator.remove();
        }
      }
    }
    return supplierShopIdList.toArray(new Long[supplierShopIdList.size()]);
  }


  //删除供应商的时候，如果对方客户还是关联关系，更新为收藏
@Override
  public List<Long> deleteSupplierUpdateCustomerRelationStatus(Long customerShopId, Long supplierShopId)throws Exception{
    List<Long> customerIds = new ArrayList<Long>();
    if (customerShopId == null || supplierShopId == null) {
      return customerIds;
    }
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      List<Customer> customers = writer.getCustomerByCustomerShopIdAndShopId(supplierShopId, customerShopId);

      if (CollectionUtil.isNotEmpty(customers)) {
        for (Customer customer : customers) {
          if(RelationTypes.RELATED.equals(customer.getRelationType())){
            customer.setRelationType(RelationTypes.SUPPLIER_COLLECTION);
            writer.update(customer);
            customerIds.add(customer.getId());
          }
        }
        writer.commit(status);
      }
      return customerIds;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public Result validateSupplierMobiles(Long shopId, Long supplierId, String[] mobiles) {
    IUserService userService = ServiceManager.getService(IUserService.class);
    if (shopId == null || ArrayUtils.isEmpty(mobiles)) {
      return new Result();
    }
    SupplierDTO supplierDTO = null;
    if(supplierId!=null){
      supplierDTO = CollectionUtil.getFirst(userService.getSupplierById(shopId, supplierId));
    }
    Set<String> toNeedCheckMobile = new HashSet<String>();
    if (supplierDTO != null && !ArrayUtils.isEmpty(supplierDTO.getContacts())) {
      for (String mobile : mobiles) {
        if (StringUtils.isEmpty(mobile)) {
          continue;
        }
        boolean isNewMobile = true;
        for (ContactDTO contactDTO : supplierDTO.getContacts()) {
          if (contactDTO != null) {
            if (mobile.equals(contactDTO.getMobile())) {
              isNewMobile = false;
              break;
            }
          }
        }
        if (isNewMobile) {
          toNeedCheckMobile.add(mobile);
        }
      }
    }else{
      for (String mobile : mobiles) {
        if (StringUtils.isEmpty(mobile)) {
          continue;
        }
        toNeedCheckMobile.add(mobile);
      }
    }
    Set<Long> excludeSupplierIds = new HashSet<Long>();
    if (supplierDTO != null) {
      excludeSupplierIds.add(supplierDTO.getId());
    }
    if (CollectionUtils.isNotEmpty(toNeedCheckMobile)) {
      Map<String, List<SupplierDTO>> supplierDTOMap = getSupplierDTOByMobiles(shopId, toNeedCheckMobile, excludeSupplierIds);
      StringBuffer msg = new StringBuffer();
      for (String mobile : supplierDTOMap.keySet()) {
        List<SupplierDTO> supplierDTOs = supplierDTOMap.get(mobile);
        for (SupplierDTO tempSupplierDTO : supplierDTOs) {
          msg.append("【").append(mobile).append("】与供应商【").append(tempSupplierDTO.getName()).append("】有相同的联系人手机号!<br>");
        }
      }
      if (msg.length() > 0) {
        msg.append("请重新输入!");
      }
      if (MapUtils.isNotEmpty(supplierDTOMap)) {
        return new Result(msg.toString(), false, supplierDTOMap);
      }
    }
    return new Result();
  }

  public Map<String, List<SupplierDTO>> getSupplierDTOByMobiles(Long shopId, Set<String> mobiles, Set<Long> excludeSupplierIds) {
    Map<String, List<SupplierDTO>> supplierDTOMap = new HashMap<String, List<SupplierDTO>>();
    if (shopId == null || CollectionUtils.isEmpty(mobiles)) {
      return supplierDTOMap;
    }
    UserWriter writer = userDaoManager.getWriter();
    List<Contact> contacts = writer.getContactsBySupplierMobiles(shopId, mobiles, excludeSupplierIds);
    Set<Long> allSupplierIds = new HashSet<Long>();
    Map<String, Set<Long>> mobileSupplierIdsMap = new HashMap<String, Set<Long>>();
    if (CollectionUtils.isNotEmpty(contacts)) {
      for (Contact contact : contacts) {
        if (contact != null && contact.getSupplierId() != null && StringUtils.isNotEmpty(contact.getMobile())) {
          String mobile = contact.getMobile();
          Set<Long> supplierIds = mobileSupplierIdsMap.get(mobile);
          if (supplierIds == null) {
            supplierIds = new HashSet<Long>();
          }
          supplierIds.add(contact.getSupplierId());
          mobileSupplierIdsMap.put(mobile, supplierIds);
          allSupplierIds.add(contact.getSupplierId());
        }
      }
    }
    Map<Long, SupplierDTO> idSupplierDTOMap = writer.getSupplierByIdSet(shopId, allSupplierIds);

    for (String mobile : mobileSupplierIdsMap.keySet()) {
      Set<Long> supplierIds = mobileSupplierIdsMap.get(mobile);
      List<SupplierDTO> supplierDTOs = new ArrayList<SupplierDTO>();
      for (Long customerId : supplierIds) {
        supplierDTOs.add(idSupplierDTOMap.get(customerId));
      }
      if (CollectionUtils.isNotEmpty(supplierDTOs)) {
        supplierDTOMap.put(mobile, supplierDTOs);
      }
    }
    return supplierDTOMap;
  }

  @Override
  public void addAreaInfoToSupplierDTO(SupplierDTO supplierDTO){
    StringBuilder areaInfo = new StringBuilder();
    if (supplierDTO.getProvince() != null) {
      AreaDTO areaDTO = AreaCacheManager.getAreaDTOByNo(supplierDTO.getProvince());
      if (areaDTO != null) {
        areaInfo.append(areaDTO.getName());
      }
    }
    if (supplierDTO.getCity() != null) {
      AreaDTO areaDTO = AreaCacheManager.getAreaDTOByNo(supplierDTO.getCity());
      if (areaDTO != null) {
        areaInfo.append(areaDTO.getName());
      }
    }
    if (supplierDTO.getRegion() != null) {
      AreaDTO areaDTO = AreaCacheManager.getAreaDTOByNo(supplierDTO.getRegion());
      if (areaDTO != null) {
        areaInfo.append(areaDTO.getName());
      }
    }
    supplierDTO.setAreaInfo(areaInfo.toString());
  }

  @Override
  public void saveSupplierVehicleBrandModelRelation(Long shopId, SupplierDTO supplierDTO,Map<Long, ShopVehicleBrandModelDTO> shopVehicleBrandModelDTOMap) {
    if(shopId==null || supplierDTO==null || supplierDTO.getId()==null || MapUtils.isEmpty(shopVehicleBrandModelDTOMap)) return;
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      saveSupplierVehicleBrandModelRelation(writer,shopId, supplierDTO, shopVehicleBrandModelDTOMap);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }

  }
  @Override
  public void saveSupplierVehicleBrandModelRelation(UserWriter writer,Long shopId, SupplierDTO supplierDTO, Map<Long, ShopVehicleBrandModelDTO> shopVehicleBrandModelDTOMap) {
    if(shopId==null || supplierDTO==null || supplierDTO.getId()==null || MapUtils.isEmpty(shopVehicleBrandModelDTOMap)) return;
    if(VehicleSelectBrandModel.ALL_MODEL.equals(supplierDTO.getSelectBrandModel())){
      supplierDTO.setVehicleModelContent("全部车型");
    }
    if (StringUtils.isNotBlank(supplierDTO.getVehicleModelIdStr())) {
      String[] vehicleModelIds = supplierDTO.getVehicleModelIdStr().split(",");
      if (!ArrayUtils.isEmpty(vehicleModelIds)) {
        StringBuilder sb = new StringBuilder();
        List<ShopVehicleBrandModelDTO> shopVehicleBrandModelDTOList = new ArrayList<ShopVehicleBrandModelDTO>();
        Supplier supplier = writer.getById(Supplier.class, supplierDTO.getId());
        supplier.setSelectBrandModel(supplierDTO.getSelectBrandModel());
        writer.update(supplier);
        //先删除
        writer.deleteVehicleBrandModelRelation(shopId, supplierDTO.getId());
        //后保存
        if (VehicleSelectBrandModel.PART_MODEL.equals(supplierDTO.getSelectBrandModel())) {
          for (String vehicleModelId : vehicleModelIds) {
            if (StringUtils.isNotBlank(vehicleModelId)) {
              ShopVehicleBrandModelDTO shopVehicleBrandModelDTO = shopVehicleBrandModelDTOMap.get(Long.valueOf(vehicleModelId));
              VehicleBrandModelRelation vehicleBrandModelRelation = new VehicleBrandModelRelation();
              vehicleBrandModelRelation.setDataType(VehicleBrandModelDataType.SUPPLIER);
              vehicleBrandModelRelation.setDataId(supplierDTO.getId());
              vehicleBrandModelRelation.setModelId(Long.valueOf(vehicleModelId));
              vehicleBrandModelRelation.setShopId(shopId);
              vehicleBrandModelRelation.setModelId(shopVehicleBrandModelDTO.getModelId());
              vehicleBrandModelRelation.setModelName(shopVehicleBrandModelDTO.getModelName());
              vehicleBrandModelRelation.setBrandId(shopVehicleBrandModelDTO.getBrandId());
              vehicleBrandModelRelation.setBrandName(shopVehicleBrandModelDTO.getBrandName());
              vehicleBrandModelRelation.setFirstLetter(shopVehicleBrandModelDTO.getFirstLetter());
              writer.save(vehicleBrandModelRelation);
              sb.append(shopVehicleBrandModelDTO.getModelName()).append(",");
              shopVehicleBrandModelDTOList.add(shopVehicleBrandModelDTO);
            }
          }
          supplierDTO.setVehicleModelContent(sb.length() > 1 ? sb.substring(0, sb.length() - 1) : null);
          supplierDTO.setShopVehicleBrandModelDTOList(shopVehicleBrandModelDTOList);
        }
      }
    }
  }
}
