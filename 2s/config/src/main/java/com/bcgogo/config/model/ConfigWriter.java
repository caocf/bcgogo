package com.bcgogo.config.model;

import com.bcgogo.CameraStatus;
import com.bcgogo.api.ShopNameSuggestion;
import com.bcgogo.camera.CameraConfigDTO;
import com.bcgogo.camera.CameraDTO;
import com.bcgogo.camera.CameraRecordDTO;
import com.bcgogo.camera.CameraSearchCondition;
import com.bcgogo.common.Pager;
import com.bcgogo.config.CRMOperationLogCondition;
import com.bcgogo.config.ShopSearchCondition;
import com.bcgogo.config.dto.*;
import com.bcgogo.enums.DeletedType;
import com.bcgogo.enums.ObjectTypes;
import com.bcgogo.enums.RelationTypes;
import com.bcgogo.enums.ShopConfigScene;
import com.bcgogo.enums.app.AppPlatform;
import com.bcgogo.enums.app.AppUserType;
import com.bcgogo.enums.app.ServiceScope;
import com.bcgogo.enums.config.AttachmentType;
import com.bcgogo.enums.config.DataType;
import com.bcgogo.enums.config.ImageType;
import com.bcgogo.enums.config.RecentlyUsedDataType;
import com.bcgogo.enums.shop.*;
import com.bcgogo.service.GenericWriterDao;
import com.bcgogo.user.merge.MergeRecordDTO;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.transaction.support.ResourceTransactionManager;

import java.text.SimpleDateFormat;
import java.util.*;


public class ConfigWriter extends GenericWriterDao {

  public ConfigWriter(ResourceTransactionManager transactionManager) {
    super(transactionManager);
  }

  public Config getConfig(String name, Long shopId) {
    Session session = getSession();
    try {
      Query q = SQL.getConfig(session, name, shopId);
      Config config = (Config) q.uniqueResult();
      if (LOG.isDebugEnabled()) {
        if (config == null)
          LOG.debug("DB config is null.");
        else
          LOG.debug("DB config " + config.getName() + " value is " + config.getValue() + ".");
      }
      return config;
    } finally {
      release(session);
    }
  }

  public ImageVersionConfig getImageVersionConfig(Long shopId, String name) {
    Session session = getSession();
    try {
      Query query = SQL.getImageVersionConfig(session, shopId, name);
      ImageVersionConfig imageVersionConfig = (ImageVersionConfig) query.uniqueResult();
      if (LOG.isDebugEnabled()) {
        if (imageVersionConfig == null)
          LOG.debug("DB imageVersionConfig is null.");
        else
          LOG.debug("DB imageVersionConfig " + imageVersionConfig.getName() + ".");
      }
      return imageVersionConfig;
    } finally {
      release(session);
    }
  }

  public Object[] getDataImageRelation(Long shopId,ImageType imageType,DataType dataType, Long dataId,int imageSequence) {
    Session session = getSession();
    try {
      Query q = SQL.getDataImageRelation(session,shopId,imageType,dataType,dataId,imageSequence);
      return (Object[])q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<Object[]> getDataImageRelation(Set<Long> shopIdSet, Set<String> appUserNoSet, Set<ImageType> imageTypeSet, DataType dataType, Long... dataId) {
    Session session = getSession();
    try {
      Query q = SQL.getDataImageRelation(session,shopIdSet, appUserNoSet, imageTypeSet,dataType,dataId);
      return (List<Object[]>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Object[]> getAppUserDataImageRelation(Set<String> appUseNos,Set<ImageType> imageTypeSet,DataType dataType, Long... dataId) {
    Session session = getSession();
    try {
      Query q = SQL.getAppUserDataImageRelation(session,appUseNos,imageTypeSet,dataType,dataId);
      return (List<Object[]>) q.list();
    } finally {
      release(session);
    }
  }


  public List<ImageInfo> getDataImageInfoList(Long shopId,Set<ImageType> imageTypeSet,DataType dataType, Long dataId) {
    Session session = getSession();
    try {
      Query q = SQL.getDataImageInfoList(session,shopId,imageTypeSet,dataType,dataId);
      return (List<ImageInfo>) q.list();
    } finally {
      release(session);
    }
  }

  @Deprecated
  public List<ImageInfo> getAllDataImageInfoList(Set<ImageType> imageTypeSet) {
    Session session = getSession();
    try {
      Query q = SQL.getAllDataImageInfoList(session,imageTypeSet);
      return (List<ImageInfo>) q.list();
    } finally {
      release(session);
    }
  }

  public List<ImageVersionConfig> getAllImageVersionConfig() {
    Session session = getSession();
    try {
      Query q = SQL.getAllImageVersionConfig(session);
      return (List<ImageVersionConfig>) q.list();
    } finally {
      release(session);
    }
  }
  public List<Config> getConfig(String name,String value, Long shopId,Pager pager) {

    Session session = getSession();
    try {
      Query q = SQL.getConfig(session, name, value,shopId,pager);
      return (List<Config>) q.list();
    } finally {
      release(session);
    }

  }

  public List<Shop> getShop() {
    Session session = getSession();
    try {
      Query q = SQL.getShop(session);
      return (List<Shop>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Long> getShopId() {
    Session session = getSession();
    try {
      Query q = SQL.getShopId(session);
      return (List<Long>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Shop> getSendInvitationCodeActiveShop() {
    Session session = getSession();
    try {
      Query q = SQL.getSendInvitationCodeActiveShop(session);
      return (List<Shop>) q.list();
    } finally {
      release(session);
    }
  }

  public List<String> getSendInvitationCodeActiveShopMobile() {
    Session session = getSession();
    try {
      Query q = SQL.getSendInvitationCodeActiveShopMobile(session);
      return (List<String>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Shop> getShop(ShopStatus shopStatus,Long trialEndTime) {
    Session session = getSession();
    try {
      Query q = SQL.getShop(session,shopStatus,trialEndTime);
      return (List<Shop>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Shop> getShopExcludeTest() {
    Session session = getSession();
    try {
      Query q = SQL.getShopExcludeTest(session);
      return (List<Shop>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Shop> getActiveShop() {
    Session session = getSession();
    try {
      Query q = SQL.getActiveShop(session);
      return (List<Shop>) q.list();
    } finally {
      release(session);
    }
  }

   public List<Shop> getShopSuggestion(String name,ShopKind shopKind,int maxRows) {
    Session session = getSession();
    try {
      Query q = SQL.getShopSuggestion(session,name,shopKind,maxRows);
      return  q.list();
    } finally {
      release(session);
    }
  }

  public List<Shop> getAdShops() {
    Session session = getSession();
    try {
      Query q = SQL.getAdShops(session);
      return (List<Shop>) q.list();
    } finally {
      release(session);
    }
  }

  public List<ShopDTO> searchShopByCondition(ShopSearchCondition condition) {
    Session session = getSession();
    List<ShopDTO> dtoList = new ArrayList<ShopDTO>();
    ShopDTO dto;
    try {
      Query q = SQL.searchShopByCondition(session, condition);
      List<Object[]> objects = q.list();
      for (Object[] o : objects) {
        dto = ((Shop) o[0]).toDTO();
        dto.setShopVersionName((String) o[1]);
        dto.setManagerUserNo((String) o[2]);
        dto.setManagerId((Long) o[3]);
        dtoList.add(dto);
      }
      return dtoList;
    } finally {
      release(session);
    }
  }

  public Long countShopByCondition(ShopSearchCondition condition) {
    Session session = getSession();
    try {
      Query q = SQL.countShopByCondition(session, condition);
      return Long.valueOf(q.uniqueResult().toString());
    } finally {
      release(session);
    }
  }

  //已注册
  public List<Shop> getShopByState(int pageNo, int pageSize) {
    Session session = this.getSession();
    try {
      Query q = SQL.getShopByState(session, pageNo, pageSize);
      return (List<Shop>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Config> getAllConfig(int limit) {
    Session session = getSession();
    try {
      Query q = SQL.getAllConfig(session, limit);
      return (List<Config>) q.list();
    } finally {
      release(session);
    }
  }

  public int countConfigs(String name,String value,Long shopId){
    Session session = getSession();
    try {
      Query q = SQL.countConfigs(session,name,value,shopId);
      return Integer.parseInt(q.uniqueResult().toString());
    } finally {
      release(session);
    }
  }
  //shao
  public int countShopByState() {
    Session session = this.getSession();
    try {
      SQLQuery q = SQL.countShopByState(session);
      q.addScalar("count", StandardBasicTypes.STRING);
      String s = q.uniqueResult().toString();
      int i = Integer.parseInt(s);
      return i;
    } finally {
      release(session);
    }
  }

  //待注册
  public List<Shop> getShopByState1(int pageNo, int pageSize) {
    Session session = this.getSession();
    try {
      Query q = SQL.getShopByState1(session, pageNo, pageSize);
      return (List<Shop>) q.list();
    } finally {
      release(session);
    }
  }

  //shao
  public int countShopByState1() {
    Session session = this.getSession();
    try {
      SQLQuery q = SQL.countShopByState1(session);
      q.addScalar("count", StandardBasicTypes.STRING);
      String s = q.uniqueResult().toString();
      int i = Integer.parseInt(s);
      return i;
    } finally {
      release(session);
    }
  }

  public List<Shop> getShops(int pageNo, int pageSize) {
    if (pageSize <= 0) return new ArrayList<Shop>();
    Session session = getSession();
    try {
      Query q = SQL.getShops(session, pageNo, pageSize);
      return (List<Shop>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Shop> getShopByName(String shopName) {
    Session session = getSession();
    try {
      Query q = SQL.getShopByName(session, shopName);
      return (List<Shop>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Shop> getActiveUsingShopByName(String shopName) {
    Session session = getSession();
    try {
      Query q = SQL.getActiveUsingShopByName(session, shopName);
      return (List<Shop>) q.list();
    } finally {
      release(session);
    }
  }

  public List<ShopDTO> getShopByObscureName(String shopName) {
    Session session = getSession();
    List<ShopDTO> shopDTOList = new ArrayList<ShopDTO>();
    try {
      Query q = SQL.getShopByObscureName(session, shopName);
      List<Shop> shopList = q.list();
      for (Shop shop : shopList) {
        shopDTOList.add(shop.toDTO());
      }
      return shopDTOList;
    } finally {
      release(session);
    }
  }

  public List<Long> getShopIdByShopCondition(ShopSearchCondition shopSearchCondition) {
    Session session = getSession();
    try {
      return SQL.getShopIdByShopCondition(session, shopSearchCondition).list();
    } finally {
      release(session);
    }
  }

  public List<ShopDTO> getShopByObscureName(String shopName, ShopStatus... shopStatuses) {
    Session session = getSession();
    List<ShopDTO> shopDTOList = new ArrayList<ShopDTO>();
    try {
      Query q = SQL.getShopByObscureName(session, shopName,shopStatuses);
      List<Shop> shopList = q.list();
      for (Shop shop : shopList) {
        shopDTOList.add(shop.toDTO());
      }
      return shopDTOList;
    } finally {
      release(session);
    }
  }

  public List<Area> getAllAreaList() {
    Session session = getSession();
    try {
      Query q = SQL.getAllAreaList(session);
      return (List<Area>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Area> getAreaList(String parentNo) {
    Session session = getSession();
    try {
      Query q = SQL.getAreaList(session, parentNo);
      return (List<Area>) q.list();
    } finally {
      release(session);
    }
  }

  public Area getArea(long no) {
    Session session = getSession();
    try {
      Query q = SQL.getArea(session, no);
      List<Area> areaList= q.list();
      if(CollectionUtils.isNotEmpty(areaList)){
        return areaList.get(0);
      }
      return null;
    } finally {
      release(session);
    }
  }

  public List<Area> getAreaListByParentNos(List<Long> parentNoList) {
    Session session = getSession();
    try {
      Query q = SQL.getAreaListByParentNos(session, parentNoList);
      return (List<Area>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Area> getAreaListBySelfNos(Set<Long> selfNoList) {
    Session session = getSession();
    try {
      Query q = SQL.getAreaListByNos(session, selfNoList);
      return (List<Area>) q.list();
    } finally {
      release(session);
    }
  }


  public List<Business> getBusinessList(String parentNo) {
    Session session = getSession();
    try {
      Query q = SQL.getBusinessList(session, parentNo);
      return (List<Business>) q.list();
    } finally {
      release(session);
    }
  }

  public List<ShopBusiness> getShopBusinessList(Long shopId) {
    Session session = getSession();
    try {
      Query q = SQL.getShopBusinessList(session, shopId);
      return (List<ShopBusiness>) q.list();
    } finally {
      release(session);
    }
  }

  public Long countShop() {
    Session session = getSession();
    try {
      Query q = SQL.countShop(session);
      return Long.parseLong(q.iterate().next().toString());
    } finally {
      release(session);
    }
  }

  public List<Shop> getShopByStoreManagerMobile(String mobile) {
    Session session = getSession();
    try {
      Query q = SQL.getShopByStoreManagerMobile(session, mobile);
      return (List<Shop>) q.list();
    } finally {
      release(session);
    }
  }

  public int countShopByAgentIdAndTime(Long agentId, Long startTime, Long endTime) {
    Session session = this.getSession();
    try {
      Query q = SQL.countShopByAgentIdAndTime(session, agentId, startTime, endTime);
      return Integer.parseInt(q.uniqueResult().toString());
    } finally {
      release(session);
    }
  }

  public List<ImportRecordDTO> getImportRecordList(List<Long> importRecordIdList, Long shopId, String status, String type){
    Session session=this.getSession();
    List<ImportRecordDTO> result = new ArrayList<ImportRecordDTO>();
    List<ImportRecord> importRecordList = null;
    try{
      Query q = SQL.getImportRecordList(session,importRecordIdList, shopId, status, type);
      importRecordList = q.list();
    }finally {
      release(session);
    }
    if(importRecordList == null || importRecordList.isEmpty()){
      return result;
    }
    for(ImportRecord importRecord : importRecordList){
      if(importRecord == null){
        continue;
      }
      result.add(importRecord.toDTO());
    }
    return result;
  }

  public List<Shop> getStoreManager(String name) {
    Session session = getSession();
    try {
      Query q = SQL.getStoreManager(session, name);
      return (List<Shop>) q.list();
    } finally {
      release(session);
    }
  }

  //根据店面ID获取短信余额
  public List<ShopBalance> getSmsBalanceByShopId(long shopId) {
    Session session = getSession();
    try {
      Query q = SQL.getSmsBalanceByShopId(session, shopId);

      return (List<ShopBalance>) q.list();
    } finally {
      release(session);
    }
  }
  public List<ShopUnit> getShopUnit(Long shopId) {
    Session session = getSession();
    try {
      Query q = SQL.getShopUnit(session, shopId);
      List<ShopUnit> shopUnitList = (List<ShopUnit>) q.list();
      return shopUnitList;
    } finally {
      release(session);
    }
  }

  public ShopUnit getShopUnitByUnitName(Long shopId, String unitName) {
    Session session = getSession();
    try {
      Query q = SQL.getShopUnitByUnitName(session, shopId, unitName);
      List<ShopUnit> shopUnits = (List<ShopUnit>) q.list();
      if (!shopUnits.isEmpty()) {
        return shopUnits.get(0);
      } else {
        return null;
      }
    } finally {
      release(session);
    }
  }

  public ShopConfig getShopConfig(ShopConfigScene scene,Long shopId)
  {
    Session session = getSession();
    try{
      Query q = SQL.getShopConfig(session,scene,shopId);

      List<ShopConfig> shopConfigs = (List<ShopConfig>)q.list();

      if(CollectionUtils.isNotEmpty(shopConfigs))
      {
        return shopConfigs.get(0);
      }

      return null;
    }finally {
      release(session);
    }
  }


  public List<ShopConfig> searchShopConfigDTOByShopAndScene(Long shopId,ShopConfigScene scene,Integer startPageNo, Integer maxRows)
  {
    Session session = getSession();

    try{
      Query q = SQL.searchShopConfigDTOByShopAndScene(session,shopId,scene,startPageNo,maxRows);

      return (List<ShopConfig>)q.list();
    }finally {
      release(session);
    }
  }

  public void deleteAllShopConfig() {
    Session session = getSession();
    try {
      Query q = SQL.deleteAllShopConfig(session);
      q.executeUpdate();
    } finally {
      release(session);
    }
  }

  public int countShopConfigByScene(Long shopId,ShopConfigScene scene)
  {
    Session session = getSession();

    try{
      Query q = SQL.countShopConfigByScene(session,shopId,scene);

      return Integer.parseInt(q.uniqueResult().toString());
    }
    finally {
      release(session);
    }
  }

  public ShopCustomerRelation getShopCustomerRelationByShopId(Long shopId){
    Session session = getSession();
    try{
      Query q = SQL.getShopCustomerRelationByShopId(session,shopId);
      List<ShopCustomerRelation> resultList = (List<ShopCustomerRelation>)q.list();
      if(CollectionUtils.isNotEmpty(resultList)){
        return resultList.get(0);
      }else{
        return null;
      }
    }finally {
      release(session);
    }
  }

  public ShopCustomerRelation getShopCustomerRelationByCustomerId(Long customerId){
    Session session = getSession();
    try{
      Query q = SQL.getShopCustomerRelationByCustomerId(session,customerId);
      List<ShopCustomerRelation> resultList = (List<ShopCustomerRelation>)q.list();
      if(CollectionUtils.isNotEmpty(resultList)){
        return resultList.get(0);
      }else{
        return null;
      }
    }finally {
      release(session);
    }
  }

  public String getCustomerShopStatus(Long customerId){
    Session session = getSession();
    try{
      Query q = SQL.getCustomerShopStatus(session,customerId);
      if(q.uniqueResult()!=null){
        return q.uniqueResult().toString();
      }else{
        return null;
      }
    }finally {
      release(session);
    }
  }

  public List<WholesalerShopRelation> getWholesalerShopRelationByWholesalerShopId(Long wholesalerShopId ,
                                                                                  List<RelationTypes> relationTypeList){
    Session session = getSession();
    try{
      Query q = SQL.getWholesalerShopRelationByWholesalerShopId(session,wholesalerShopId,relationTypeList);
      List<WholesalerShopRelation> resultList = q.list();
      if(CollectionUtils.isNotEmpty(resultList)){
        return resultList;
      }else{
        return null;
      }
    }finally {
      release(session);
    }
  }

  public List<WholesalerShopRelation> getWholesalerShopRelationByShopId(Long shopId,List<RelationTypes> relationTypeList){
    Session session = getSession();
    try{
      Query q = SQL.getWholesalerShopRelationByShopId(session,shopId,relationTypeList);
      List<WholesalerShopRelation> resultList = q.list();
      if(CollectionUtils.isNotEmpty(resultList)){
        return resultList;
      }else{
        return null;
      }
    }finally {
      release(session);
    }
  }

  public List<WholesalerShopRelation> getWholesalerShopRelationByWholesalerShopIds(Long shopId,
                                                     List<RelationTypes> relationTypeList, Long... wholesalerShopIds) {
    Session session = getSession();
    try {
      Query q = SQL.getWholesalerShopRelationByWholesalerShopIds(session, shopId,relationTypeList, wholesalerShopIds);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<WholesalerShopRelation> getWholesalerShopRelationByCustomerShopIds(Long shopId, List<RelationTypes> relationTypeList, Long... customerShopIds) {
    Session session = getSession();
    try {
      Query q = SQL.getWholesalerShopRelationByCustomerShopIds(session, shopId, relationTypeList, customerShopIds);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<Shop> getShopByShopId(Long... shopId) {
    Session session = getSession();
    try {
      Query q = SQL.getShopByShopId(session, shopId);
      return (List<Shop>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Shop> getShopByShopId(List<Long> shopId) {
    Session session = getSession();
    try {
      Query q = SQL.getShopByShopId(session, shopId);
      return (List<Shop>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Shop> getShopWithoutShopIdByShopVersionId(Long shopVersionId,Long... withoutShopIds) {
    Session session = getSession();
    try {
      Query q = SQL.getShopWithoutShopIdByShopVersionId(session, shopVersionId,withoutShopIds);
      return (List<Shop>) q.list();
    } finally {
      release(session);
    }
  }

  public Long countCRMOperationLogsByCondition(CRMOperationLogCondition condition) {
    Session session = getSession();
    try {
      Query q = SQL.countCRMOperationLogsByCondition(session, condition);
      Object o = q.uniqueResult();
      return o == null ? 0l : Long.valueOf(o.toString());
    } finally {
      release(session);
    }
  }

  public List<CRMOperationLogDTO> getCRMOperationLogsByCondition(CRMOperationLogCondition condition) {
    Session session = getSession();
    try {
      Query q = SQL.getCRMOperationLogsByCondition(session, condition);
      return  q.list();
    } finally {
      release(session);
    }
  }

  public int getMergeRecordCount(MergeRecordDTO mergeRecordIndex){
    Session session = getSession();
    try {
      Query query = SQL.getMergeRecordCount(session,mergeRecordIndex);
      return  Integer.parseInt(query.uniqueResult().toString());
    } finally {
      release(session);
    }
  }

  public List<MergeRecord> getMergeRecords(MergeRecordDTO mergeRecordIndex){
    Session session = getSession();
    try {
      SQLQuery query = SQL.getMergeRecords(session, mergeRecordIndex);
      query.addScalar("childId", StandardBasicTypes.LONG)
          .addScalar("parentId", StandardBasicTypes.LONG)
          .addScalar("child", StandardBasicTypes.STRING)
          .addScalar("parent", StandardBasicTypes.STRING)
          .addScalar("mergeTime", StandardBasicTypes.LONG)
          .addScalar("operator", StandardBasicTypes.STRING);
      query.setResultTransformer(Transformers.aliasToBean(MergeRecord.class));
      return query.list();
    } finally {
      release(session);
    }
  }

  public MergeRecord getMergeRecordDetail(Long shopId,Long parentId,Long childId){
    Session session = getSession();
    try {
      Query q = SQL.getMergeRecordDetail(session, shopId, parentId, childId);
      return  (MergeRecord)q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<Shop> searchApplyCustomerShops(ApplyShopSearchCondition searchCondition,String shopVersionIdStr, boolean isTesShop, Pager pager) {
    Session session = getSession();
    try {
      Query q = SQL.searchApplyCustomerShop(session, searchCondition,shopVersionIdStr, isTesShop, pager);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<Shop> getSupplierOrCustomerShopSuggestion(Long shopId,String searchWord,String shopVersionIdStr, boolean isTestShop,String customerOrSupplier,String shopRange) {
    Session session = getSession();
    try {
      Query q = SQL.getSupplierOrCustomerShopSuggestion(session, shopId,searchWord,shopVersionIdStr, isTestShop,customerOrSupplier,shopRange);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<Shop> searchApplySupplierShops(ApplyShopSearchCondition searchCondition,String shopVersionIdStr, boolean isTesShop, Pager pager) {
    Session session = getSession();
    try {
      Query q = SQL.searchApplySupplierShop(session, searchCondition,shopVersionIdStr, isTesShop, pager);
      return q.list();
    } finally {
      release(session);
    }
  }

  public Integer countApplyCustomerShop(ApplyShopSearchCondition searchCondition,String shopVersionIdStr, boolean isTesShop) {
    Session session = getSession();
    try {
      Query q = SQL.countApplyCustomerShop(session, searchCondition,shopVersionIdStr, isTesShop);
      return (Integer) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public Integer countApplySupplierShop(ApplyShopSearchCondition searchCondition,String shopVersionIdStr, boolean isTestShop) {
    Session session = getSession();
    try {
      Query q = SQL.countApplySupplierShop(session, searchCondition,shopVersionIdStr, isTestShop);
      return (Integer) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<ShopRelationInvite> getShopRelationInvitesByInvitedShopIds(
      InviteType inviteType, InviteStatus status, Long shopId,Long expiredTime, Long... invitedShopIds) {
    Session session = getSession();
    try {
      Query q = SQL.getShopRelationInvitesByInvitedShopIds(session, inviteType, status, shopId,expiredTime, invitedShopIds);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<ShopRelationInvite> getShopRelationInvitesByOriginShopIds(
      InviteType inviteType, InviteStatus status, Long invitedShopId,Long expiredTime, Long... originShopIds) {
    Session session = getSession();
    try {
      Query q = SQL.getShopRelationInvitesByOriginShopIds(session, inviteType, status, invitedShopId,expiredTime, originShopIds);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<Long> getShopRelationInviteInvitedShopIdsByOriginShopObscureName(String originShopName) {
    Session session = getSession();
    try {
      Query q = SQL.getShopRelationInviteInvitedShopIdsByOriginShopObscureName(session, originShopName);
      return q.list();
    } finally {
      release(session);
    }
  }

  public Map<Long, Shop> getShopRelationInviteOriginShopByInvitedShopIds(Long... shopIds) {
    Map<Long, Shop> shopMap = new HashMap<Long, Shop>();
    Session session = getSession();
    try {
      Query q = SQL.getShopRelationInviteOriginShopByInvitedShopIds(session, shopIds);
      List<Object[]> objects = q.list();
      for (Object[] o : objects) {
        shopMap.put((Long)o[0], (Shop)o[1]);
      }
      return shopMap;
    } finally {
      release(session);
    }
  }

  public ShopRelationInvite getShopRelationInviteByInvitedShopIdAndId(Long invitedShopId, Long inviteId) {
    Session session = getSession();
    try {
      Query q = SQL.getShopRelationInviteDTOByInvitedShopIdAndId(session, invitedShopId, inviteId);
      List<ShopRelationInvite> shopRelationInvites   = q.list();
      return CollectionUtil.uniqueResult(shopRelationInvites);
    } finally {
      release(session);
    }
  }

  public List<OperationLog> getOprationLogByObjectId(ObjectTypes type,Long objectId){
    Session session = this.getSession();
    try{
      Query q = SQL.getOprationLogByObjectId(session, type, objectId);
      return (List<OperationLog>)q.list();
    } finally {
      release(session);
    }
  }

  public List<ShopRelationInvite> getShopRelationInvitesByInvitedShopIdAndIds(Long invitedShopId, Long... inviteId) {
    Session session = getSession();
    try {
      Query q = SQL.getShopRelationInviteDTOByInvitedShopIdAndId(session, invitedShopId, inviteId);
      return q.list();
    } finally {
      release(session);
    }
  }
  public List<ShopRelationInvite> getPendingShopRelationInvites(Long shopId,InviteType inviteType) {
    Session session = getSession();
    try {
      Query q = SQL.getPendingShopRelationInvites(session, shopId, inviteType);
      return q.list();
    } finally {
      release(session);
    }
  }


  public Map<InviteCountStatus, Integer> countShopRelationInvites(Long shopId) {
    Map<InviteCountStatus, Integer> inviteTypeMap = new HashMap<InviteCountStatus, Integer>();
    Session session = getSession();
    try {
      Query q = SQL.countPendingShopRelationInvites(session, shopId, InviteType.SUPPLIER_INVITE);
      Long count = (Long) q.uniqueResult();
      inviteTypeMap.put(InviteCountStatus.SUPPLIER_INVITE_PENDING, NumberUtil.intValue(count.toString(),0));
      q = SQL.countPendingShopRelationInvites(session, shopId, InviteType.CUSTOMER_INVITE);
      count = (Long) q.uniqueResult();
      inviteTypeMap.put(InviteCountStatus.CUSTOMER_INVITE_PENDING, NumberUtil.intValue(count.toString(),0));
      List<InviteStatus> notDelete = new ArrayList<InviteStatus>();
      notDelete.add(InviteStatus.ACCEPTED);
      notDelete.add(InviteStatus.PENDING);
      notDelete.add(InviteStatus.REFUSED);
      q = SQL.countSearchShopRelationInvites(session, shopId, InviteType.CUSTOMER_INVITE, null, notDelete);
      count = (Long) q.uniqueResult();
      inviteTypeMap.put(InviteCountStatus.CUSTOMER_INVITE_ALL, NumberUtil.intValue(count.toString(), 0));
      q = SQL.countSearchShopRelationInvites(session, shopId, InviteType.SUPPLIER_INVITE, null, notDelete);
      count = (Long) q.uniqueResult();
      inviteTypeMap.put(InviteCountStatus.SUPPLIER_INVITE_ALL, NumberUtil.intValue(count.toString(), 0));
      return inviteTypeMap;
    } finally {
      release(session);
    }
  }

  public int countSearchShopRelationInvites(Long shopId, InviteType inviteType, Long originShopId, List<InviteStatus> statuses) {
    Session session = getSession();
    try {
      Query q = SQL.countSearchShopRelationInvites(session, shopId, inviteType, originShopId, statuses);
      Long count = (Long) q.uniqueResult();
      return NumberUtil.intValue(count.toString(),0);
    } finally {
      release(session);
    }
  }

  public List<ShopRelationInvite> searchShopRelationInvites(Long shopId, InviteType inviteType, Long originShopId, List<InviteStatus> statuses, Pager pager) {
    Session session = getSession();
    try {
      Query q = SQL.searchShopRelationInvites(session, shopId, inviteType, statuses, originShopId, pager);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<ShopOperateHistory> getLatestShopOperateHistory(Long operateShopId, ShopOperateType type) {
    Session session = getSession();
    try {
      Query q = SQL.getLatestShopOperateHistory(session, operateShopId, type);
      return q.list();
    } finally {
      release(session);
    }
  }

  public RegisterInfo getRegisterInfoByRegisterShopId(Long registerShopId) {
    Session session = getSession();
    try {
      Query q = SQL.getRegisterInfoByRegisterShopId(session, registerShopId);
      return (RegisterInfo) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public ShopOperationTask getFirstReadyShopOperationTask() {
    Session session = getSession();
    try {
      Query q = SQL.getFirstReadyShopOperationTask(session);
      List<ShopOperationTask> shopOperationTasks = q.list();
      return CollectionUtil.getFirst(shopOperationTasks);
    } finally {
      release(session);
    }
  }

  public Set<Long> getRelationWholesalerShopIds(Long shopId,List<RelationTypes> relationTypeList) {
    Session session = getSession();
    try {
      Set<Long> supplierShopIdSet = new HashSet<Long>();
      Query q = SQL.getRelationWholesalerShopIds(session,shopId ,relationTypeList);
      List<Long> supplierShopIds = q.list();
      if(CollectionUtil.isNotEmpty(supplierShopIds)){
        for(Long supplierShopId : supplierShopIds){
          supplierShopIdSet.add(supplierShopId);
        }
      }
      return supplierShopIdSet;
    } finally {
      release(session);
    }
  }

  public Set<Long> getRelatedCustomerShopIds(Long wholesalerShopId,List<RelationTypes> relationTypeList) {
    Session session = getSession();
    try {
      Set<Long> customerShopIdSet = new HashSet<Long>();
      Query q = SQL.getRelationCustomerShopIds(session,wholesalerShopId ,relationTypeList);
      List<Long> customerShopIds = q.list();
      if(CollectionUtil.isNotEmpty(customerShopIds)){
        for(Long customerShopId : customerShopIds){
          customerShopIdSet.add(customerShopId);
        }
      }
      return customerShopIdSet;
    } finally {
      release(session);
    }
  }

  public List<Long> getBcgogoRecommendSupplierShopIds(Long shopId, List<Long> shopVersionIds) {
    Session session = getSession();
    try {
      Query q = SQL.getBcgogoRecommendSupplierShopIds(session, shopId, shopVersionIds);
      return (List<Long>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Attachment> getAttachmentByShopId(Long shopId,AttachmentType attachmentType) {
    Session session = getSession();
    try {
      Query q = SQL.getAttachmentByShopId(session, shopId, attachmentType);
      return (List<Attachment>) q.list();
    } finally {
      release(session);
    }
  }

  @Deprecated
  public SaleManShopMap getSaleManShopMapByShopId(Long shopId) {
    Session session = getSession();
    try {
      Query q = SQL.getSaleManShopMapByShopId(session, shopId);
      List<SaleManShopMap> list = (List<SaleManShopMap>) q.list();
      return list.size() > 0 ? list.get(0) : null;
    } finally {
      release(session);
    }
  }

  public List<SmsDonationLog> getSmsDonationLogByShopId(Long shopId) {
    Session session = getSession();
    try {
      Query q = SQL.getSmsDonationLogByShopId(session, shopId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<ShopBargainRecord> getShopBargainRecordsByShopId(long shopId) {
    Session session = getSession();
    try {
      Query q = SQL.getShopBargainRecordsByShopId(session, shopId);
      return q.list();
    } finally {
      release(session);
    }
  }
  public List<ShopBargainRecord> getShopAuditPassBargainRecordByShopId(Long... shopId) {
    Session session = getSession();
    try {
      Query q = SQL.getShopAuditPassBargainRecordByShopId(session, shopId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public ShopBargainRecord getShopBargainRecordByShopId(long shopId, BargainStatus status) {
    Session session = getSession();
    try {
      Query q = SQL.getShopBargainRecordByShopId(session, shopId, status);
      return CollectionUtil.uniqueResult((List<ShopBargainRecord>) q.list());
    } finally {
      release(session);
    }
  }


  public List<ShopExtensionLog> getShopExtensionLogs(long shopId) {
    Session session = getSession();
    try {
      Query q = SQL.getShopExtensionLogs(session, shopId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public int countShopExtensionLogs(long shopId) {
    Session session = getSession();
    try {
      Query q = SQL.countShopExtensionLogs(session, shopId);
      Long count = (Long) q.uniqueResult();
      return NumberUtil.intValue(count.toString(),0);
    } finally {
      release(session);
    }
  }

  public Long countShopByShopStatusAndShopStatues(ShopState[] shopStates, ShopStatus shopStatus) {
    Session session = getSession();
    try {
      Query q = SQL.countShopByShopStatusAndShopStatues(session, shopStates, shopStatus);
      return (Long) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<Long> getAllShopIds() {
    Session session = this.getSession();
    try {
      Query q = SQL.getAllShopIds(session);
      return (List<Long>) q.list();

    } finally {
      release(session);
    }
  }
  public List<Long> getActiveShopIds() {
    Session session = this.getSession();
    try {
      Query q = SQL.getActiveShopIds(session);
      return (List<Long>) q.list();

    } finally {
      release(session);
    }
  }

  public List<PageCustomizerConfig> getPageCustomizerConfigByShopId(Long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getCustomizerConfigByShopId(session, shopId);
      return  (List<PageCustomizerConfig>) q.list();
    } finally {
      release(session);
    }
  }


  public List<CustomerRelatedShopDTO> getRelatedShopByCustomerMobile(Long shopId, Integer cancelRecommendAssociatedCountLimit, ShopKind shopKind) {
    Session session = this.getSession();
    try {
      List<CustomerRelatedShopDTO> customerRelatedShopDTOList  = new ArrayList<CustomerRelatedShopDTO>();
      CustomerRelatedShopDTO customerRelatedShopDTO ;
      Query q = SQL.getRelatedShopByCustomerMobile(session, shopId, cancelRecommendAssociatedCountLimit, shopKind);
      List<Object[]> objectsList =  q.list();
      for(Object[] os: objectsList){
        customerRelatedShopDTO = new CustomerRelatedShopDTO();
        customerRelatedShopDTO.setCustomerId((Long) os[0]);
        customerRelatedShopDTO.setCustomerMobile((String) os[1]);
        customerRelatedShopDTO.setRelatedShopId((Long) os[2]);
        customerRelatedShopDTO.setRelatedShopName((String) os[3]);
        customerRelatedShopDTO.setCustomerName((String) os[4]);
        customerRelatedShopDTO.setShopId(shopId);
        customerRelatedShopDTOList.add(customerRelatedShopDTO);
      }
      return customerRelatedShopDTOList;
    } finally {
      release(session);
    }
  }

  public List<SupplierRelatedShopDTO> getRelatedShopBySupplierMobile(Long shopId, Integer cancelRecommendAssociatedCountLimit, ShopKind shopKind) {
    Session session = this.getSession();
    try {
      List<SupplierRelatedShopDTO> list  = new ArrayList<SupplierRelatedShopDTO>();
      SupplierRelatedShopDTO dto ;
      Query q = SQL.getRelatedShopBySupplierMobile(session, shopId, cancelRecommendAssociatedCountLimit, shopKind);
      List<Object[]> objectsList =  q.list();
      for(Object[] os: objectsList){
        dto = new SupplierRelatedShopDTO();
        dto.setSupplierId((Long) os[0]);
        dto.setSupplierMobile((String) os[1]);
        dto.setRelatedShopId((Long) os[2]);
        dto.setRelatedShopName((String) os[3]);
        dto.setSupplierName((String) os[4]);
        dto.setShopId(shopId);
        list.add(dto);
      }
      return list;
    } finally {
      release(session);
    }
  }

  public List<Shop> getRecommendedShop() {
    Session session = this.getSession();
    try {
      Query q = SQL.getRecommendedShop(session);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<ShopRelationInvite> getShopRelationInviteInShopIds(Set<Long> shopIdSet,Long shopId,Set<InviteStatus> inviteStatusSet) {
    Session session = this.getSession();
    try {
      Query q = SQL.getShopRelationInviteInShopIds(session,shopIdSet,shopId,inviteStatusSet);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<ShopContact> getShopContactsByShopId(Long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getShopContactsByShopId(session, shopId);
      return (List<ShopContact>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Long> getShopIdByContactMobile(Long customerShopId, Long supplierShopId, List<String> mobiles) {
    Session session = this.getSession();
    try {
      Query q = SQL.getShopIdByContactMobile(session, customerShopId, supplierShopId, mobiles);
      return (List<Long>) q.list();
    } finally {
      release(session);
    }
  }

  public List<ShopBusinessScope> getShopBusinessScopeByShopId(Set<Long> shopIdSet) {
    Session session = getSession();
    try {
      Query q = SQL.getShopBusinessScopeByShopId(session, shopIdSet);
      return (List<ShopBusinessScope>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Long> getBusinessScopeIdsByShopId(Long shopId) {
    Session session = getSession();
    try {
      Query q = SQL.getBusinessScopeIdsByShopId(session, shopId);
      return (List<Long>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Long> getShopBusinessScopeProductCategoryIdListByShopId(Long shopId) {
    Session session = getSession();
    try {
      Query q = SQL.getShopBusinessScopeProductCategoryIdListByShopId(session, shopId);
      return (List<Long>) q.list();
    } finally {
      release(session);
    }
  }

  public List<ShopDTO> getBcgogoRecommendSupplierShop(Long shopId, List<Long> shopVersionIds) {
    Session session = getSession();
    try {
      Query q = SQL.getBcgogoRecommendSupplierShop(session, shopId, shopVersionIds);
      List<ShopDTO> shopDTOList = new ArrayList<ShopDTO>();
      List<Object[]> objectsList = q.list();
      if (CollectionUtils.isNotEmpty(objectsList)) {
        ShopDTO shopDTO = null;
        for (Object[] os : objectsList) {
          shopDTO = new ShopDTO();
          shopDTO.setId((Long) os[0]);
          shopDTO.setIdStr(shopDTO.getId() == null?null:shopDTO.getId().toString());
          shopDTO.setName((String) os[1]);
          shopDTO.setProvince((Long) os[2]);
          shopDTO.setCity((Long) os[3]);
          shopDTO.setRegion((Long) os[4]);
          shopDTOList.add(shopDTO);
        }
      }
      return shopDTOList;
    } finally {
      release(session);
    }
  }

  public List<MaintainShopLog> getMaintainShopLog(Long shopId) {
    Session session = getSession();
    try {
      Query q = SQL.getMaintainShopLog(session, shopId);
      return (List<MaintainShopLog>) q.list();
    } finally {
      release(session);
    }
  }

  public List<ShopBusinessScope> getShopBusinessScopeById(Set<Long> ids) {
    Session session = getSession();
    try {
      Query q = SQL.getShopBusinessScopeById(session, ids);
      return (List<ShopBusinessScope>) q.list();
    } finally {
      release(session);
    }
  }

  public SolrReindexJob getSolrReindexJobByBatchIdShopId(Long batchId, Long shopId) {
    Session session = getSession();
    try{
      Query q = SQL.getSolrReindexJobByBatchIdShopId(session, batchId, shopId);
      return (SolrReindexJob) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<SolrReindexJob> getFailedSolrReindexJob(Long batchId, String reindexType) {
    Session session = getSession();
    try{
      Query q = SQL.getFailedSolrReindexJob(session, batchId, reindexType);
      return q.list();
    } finally{
      release(session);
    }
  }

  public SolrReindexJob getTodoJobByBatchId(Long batchId) {
    Session session = getSession();
    try{
      Query q = SQL.getTodoJobByBatchId(session, batchId);
      return (SolrReindexJob)q.uniqueResult();
    } finally{
      release(session);
    }
  }

  public int countBeFavoured(Long paramShopId) {
    Session session = getSession();
    try{
      Query q = SQL.countBeFavoured(session, paramShopId);
      return NumberUtil.intValue(q.uniqueResult().toString(),0);
    } finally{
      release(session);
    }
  }
  public List<RecentlyUsedData> getRecentlyUsedDataList(Long shopId,Long userId, RecentlyUsedDataType type,Integer maxSize) {
    Session session = getSession();
    try {
      Query q = SQL.getRecentlyUsedDataList(session, shopId,userId, type,maxSize);
      return (List<RecentlyUsedData>) q.list();
    } finally {
      release(session);
    }
  }
  public List<Object[]> statRecentlyUsedDataCountByDataId(Long shopId,RecentlyUsedDataType type,Long... dataIds) {
    Session session = getSession();
    try {
      Query q = SQL.statRecentlyUsedDataCountByDataId(session, shopId,type,dataIds);
      return (List<Object[]>) q.list();
    } finally {
      release(session);
    }
  }

  public int statShopRecentlyUsedDataCount(Long shopId,RecentlyUsedDataType type) {
    Session session = getSession();
    try {
      Query q = SQL.statShopRecentlyUsedDataCount(session, shopId,type);
      return NumberUtil.roundInt(q.uniqueResult());
    } finally {
      release(session);
    }
  }

  public RecentlyUsedData getRecentlyUsedDataByDataId(Long shopId,Long userId, RecentlyUsedDataType type,Long dataId) {
    Session session = getSession();
    try {
      Query q = SQL.getRecentlyUsedDataByDataId(session, shopId,userId, type,dataId);
      return (RecentlyUsedData)q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<RecentlyUsedData> getRecentlyUsedDataListByDataId(Long shopId,Long userId, RecentlyUsedDataType type,Long... dataId) {
    Session session = getSession();
    try {
      Query q = SQL.getRecentlyUsedDataListByDataId(session, shopId,userId, type,dataId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public void deleteAllRecentlyUsedDataByType(Long shopId,Long userId, RecentlyUsedDataType type) {
    Session session = getSession();
    try {
      Query query = SQL.deleteAllRecentlyUsedDataByType(session,shopId,userId,type);
      query.executeUpdate();
    } finally {
      release(session);
    }
  }

  public RecentlyUsedData getViewedBusinessChance(Long shopId,Long userId,Long preBuyOrderItemId) {
    Session session = getSession();
    try {
      Query q = SQL.getViewedBusinessChance(session, shopId, userId, preBuyOrderItemId);
      return (RecentlyUsedData) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<RecentlyUsedData> getViewedBusinessChance(Long shopId,Long userId,Integer limit) {
    Session session = getSession();
    try {
      Query q = SQL.getViewedBusinessChance(session, shopId, userId,limit);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<RecentlyUsedData> getViewedBusinessChances(Long... preBuyOrderItemIds) {
    Session session = getSession();
    try {
      return SQL.getViewedBusinessChances(session,preBuyOrderItemIds).list();
    } finally {
      release(session);
    }
  }
    //只用于初始化
  public List<Long> getAllRelatedShopIds() {
    Session session = getSession();
    try{
      Query q = SQL.getAllRelatedShopIds(session);
      return q.list();
    } finally {
      release(session);
    }
  }
  public List<Shop> getTodoBugfixShops() {
    Session session = getSession();
    try{
      Query q = SQL.getTodoBugfixShops(session);
      return q.list();
    } finally{
      release(session);
    }
  }

  public Area getAreaByCityCode(Integer cityCode) {
    Session session = getSession();
    try{
      Query q = SQL.getAreaByCityCode(session, cityCode);
      return (Area)q.uniqueResult();
    } finally{
      release(session);
    }
  }

  public List<ServiceCategory> getServiceCategory(Long shopId) {
    Session session = getSession();
    try {
      Query query = SQL.getServiceCategory(session, shopId);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<AgentProduct> getAgentProductByShopId(Long shopId) {
    Session session = getSession();
    try {
      Query query = SQL.getAgentProductByShopId(session, shopId);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<ShopAgentProduct> getShopAgentProductByShopId(Long shopId) {
    Session session = getSession();
    try {
      Query query = SQL.getShopAgentProductByShopId(session, shopId);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<ShopServiceCategory> getShopServiceCategoriesById(Long shopId) {
    Session session = getSession();
    try {
      Query query = SQL.getShopServiceCategoriesById(session, shopId);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<ShopServiceCategory> getShopServiceCategoryByShopIds(Long[] shopIds) {
    Session session = getSession();
    try {
      Query query = SQL.getShopServiceCategoryByShopIds(session, shopIds);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<Long> getServiceCategoryChildren(Long parentId) {
    Session session = getSession();
    try {
      Query query = SQL.getServiceCategoryChildren(session, parentId);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<Long> getServiceCategoryChildrenIds(Set<Long> parentId) {
    Session session = getSession();
    try {
      Query query = SQL.getServiceCategoryChildrenIds(session, parentId);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<ShopAuditLog> getShopAuditLogDTOListByShopIdAndStatus(Long shopId, AuditStatus auditStatus) {
    Session session = getSession();
    try {
      Query query = SQL.getShopAuditLogDTOListByShopIdAndStatus(session, shopId, auditStatus);
      return query.list();
    } finally {
      release(session);
    }
  }

  public void deleteJuheViolateRegulationCitySearchCondition() {
    Session session = getSession();
    try {
      Query query = SQL.deleteJuheViolateRegulationCitySearchCondition(session);
      query.executeUpdate();
    } finally {
      release(session);
    }
  }

  public void updateShopAgentProductStatus(Long shopId, DeletedType deletedType) {
    Session session = getSession();
    try {
      Query query = SQL.updateShopAgentProductStatus(session, shopId, deletedType);
      query.executeUpdate();
    } finally {
      release(session);
    }
  }
  public List<Long> getServiceCategoryChildrenIdsByParentServiceScope(ServiceScope scope) {
    Session session = getSession();
    try {
      Query query = SQL.getServiceCategoryChildrenIdsByParentServiceScope(session, scope);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<Shop> getInitShopServiceScope() {
    Session session = getSession();
    try {
      Query query = SQL.getInitShopServiceScope(session);
      return query.list();
    } finally {
      release(session);
    }
  }

  public JuheCityOilPrice getJuheCityOilPriceByFirstCarNo(String firstCarNo) {
    Session session = getSession();
    try {
      Query query = SQL.getJuheCityOilPriceByFirstCarNo(session, firstCarNo);
      List<JuheCityOilPrice> juheCityOilPrices = query.list();
      return CollectionUtil.getFirst(juheCityOilPrices);
    } finally {
      release(session);
    }
  }
  public List<OperationLog> getOperationLogByPager(int startPageNo, int pageSize) {
    Session session = getSession();
    try {
      Query query = SQL.getOperationLogByPager(session,startPageNo,pageSize);
      return query.list();
    } finally {
      release(session);
    }
  }

  public void updateSalesOrderOperationLogType(List<String> objectTypeList) {
    Session session = getSession();
    try {
      Query query = SQL.updateSalesOrderOperationLogType(session,objectTypeList);
      query.executeUpdate();
    } finally {
      release(session);
    }
  }

  public void updateRepairOrderOperationLogType() {
    Session session = getSession();
    try {
      Query query = SQL.updateRepairOrderOperationLogType(session);
      query.executeUpdate();
    } finally {
      release(session);
    }
  }

  public AppUpdateAnnounce getAppUpdateAnnounce(AppPlatform platform, String appVersion,AppUserType appUserType) {
    Session session = getSession();
    try {
      Query query = SQL.getAppUpdateAnnounce(session, platform,appVersion,appUserType);
      List<AppUpdateAnnounce> appUpdateAnnounces = query.list();
      return CollectionUtil.getFirst(appUpdateAnnounces);
    } finally {
      release(session);
    }
  }
  public List<Long> getShopByShopVersionAndArea(Long[] shopVersionId, Long province, Long city, Long region) {
    Session session = getSession();
    try {
      Query q = SQL.getShopByShopVersionAndArea(session, shopVersionId, province, city, region);
      return (List<Long>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Area> getAreaLikeCityName(String name) {
    Session session = getSession();
    try {
      Query q = SQL.getAreaLikeCityName(session, name);
      return (List<Area>) q.list();
    } finally {
      release(session);
    }
  }


  public int countObdSimMobileSuggestion(ShopNameSuggestion suggestion) {
    Session session = getSession();
    try {
      Query q = SQL.countObdSimMobileSuggestion(session, suggestion);
      return NumberUtil.intValue( q.uniqueResult());
    } finally {
      release(session);
    }
  }

  public List<Shop> getObdOutStorageShopNameSuggestion(ShopNameSuggestion suggestion) {
    Session session = getSession();
    try {
      Query q = SQL.getObdOutStorageShopNameSuggestion(session, suggestion);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<ShopAdArea> getShopAdAreaByShopId(Long shopId) {
    if(shopId == null){
      return new ArrayList<ShopAdArea>();
    }
    Session session = getSession();
    try {
      Query q = SQL.getShopAdAreaByShopId(session, shopId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<Long> getAdShopIds(Integer size) {
    Session session = getSession();
    try {
      Query q = SQL.getAdShopIds(session,size);
      return q.list();
    } finally {
      release(session);
    }
  }


  public List<RecommendTree> getRecommendTree() {
    Session session = getSession();
    try {
      Query q = SQL.getRecommendTree(session);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<RecommendShop> getRecommendShopByShopId(Long shopId) {
    if(shopId == null){
      return new ArrayList<RecommendShop>();
    }
    Session session = getSession();
    try {
      Query q = SQL.getRecommendShopByShopId(session,shopId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<Shop> getRecommendShopByShopArea(Long parentId,Long province,Long city,Long region) {
    Session session = getSession();
    try {
      Query q = SQL.getRecommendShopByShopArea(session,parentId,province,city,region);
     return q.list();
    } finally {
      release(session);
    }
  }

 public List<Object[]> countRecommendShopByShopArea(Long province,Long city,Long region,Long... parentId) {
    Session session = getSession();
    try {
      Query q = SQL.countRecommendShopByShopArea(session,province,city,region,parentId);
     return q.list();
    } finally {
      release(session);
    }
  }

  public List<RecommendShop> getRecommendShopByRecommendId(Long recommendId) {
    if(recommendId == null){
      return new ArrayList<RecommendShop>();
    }

    Session session = getSession();
    try {
      Query q = SQL.getRecommendShopByRecommendId(session, recommendId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<RecommendTree> getRecommendTreeByRecommendIds(Set<Long> recommendIds) {
    if(CollectionUtils.isEmpty(recommendIds)){
      return new ArrayList<RecommendTree>();
    }

    Session session = getSession();
    try {
      Query q = SQL.getRecommendTreeByRecommendIds(session, recommendIds);
      return q.list();
    } finally {
      release(session);
    }
  }

 public WXImageLib getWXImageLib(String name){
    Session session = this.getSession();
    try {
      Query q = SQL.getWXImageLib(session, name);
      Object object=CollectionUtil.getFirst(q.list()) ;
      return (WXImageLib)object;
    } finally {
      release(session);
    }
  }

  public int countCameraDTOList(String shopId) {
    Session session = this.getSession();
    try {
      Query query = SQL.countCameraDTO(session,shopId);
      return NumberUtil.intValue(query.uniqueResult());
    } finally {
      release(session);
    }
  }

  public List<CameraDTO> getCameraDTOList(Pager pager,String shopId) {
    List<CameraDTO> cameraDTOs = new ArrayList<CameraDTO>();
    Session session = this.getSession();
    try {
      Query query = SQL.getCameraDTOList(session,pager,shopId);
      List<Object[]> result = query.list() ;
      if(CollectionUtils.isNotEmpty(result)){
        for(Object[] objects : result){
          CameraDTO cameraDTO = new CameraDTO();
          cameraDTO.setId(StringUtil.valueOf(objects[0]));
          cameraDTO.setSerial_no(StringUtil.valueOf(objects[1]));
          if(NumberUtil.longValue(objects[2])!=null&&NumberUtil.longValue(objects[2])!=0L){
            cameraDTO.setLast_heart_date(formatter.format(NumberUtil.longValue(objects[2])));
          }else{
            cameraDTO.setLast_heart_date("");
          }
          cameraDTO.setLan_ip(StringUtil.valueOf(objects[3]));
          cameraDTO.setLan_port(StringUtil.valueOf(objects[4]));
          cameraDTO.setUsername(StringUtil.valueOf(objects[5]));
          cameraDTO.setPassword(StringUtil.valueOf(objects[6]));
          cameraDTO.setDomain_name(StringUtil.valueOf(objects[7]));
          cameraDTO.setDomain_username(StringUtil.valueOf(objects[8]));
          cameraDTO.setDomain_password(StringUtil.valueOf(objects[9]));
          if("binding".equals(StringUtil.valueOf(objects[10]))){
            cameraDTO.setStatus(CameraStatus.ENABLED.getStatus().toString());
          } else{
            cameraDTO.setStatus(CameraStatus.DISABLED.getStatus().toString());
          }
          cameraDTO.setRemark(StringUtil.valueOf(objects[11]));
          cameraDTO.setExternal_address(StringUtil.valueOf(objects[12]));
          cameraDTO.setName(StringUtil.valueOf(objects[13]));
//          cameraDTO.setWhite_vehicle_nos(StringUtil.valueOf(objects[14]));
          if(NumberUtil.longValue(objects[14])!=null){
            cameraDTO.setInstall_date(formatter.format(NumberUtil.longValue(objects[14])));
          }
          cameraDTO.setCamera_shop_id(StringUtil.valueOf(objects[15]));
          cameraDTOs.add(cameraDTO);
        }
      }
      return cameraDTOs;
    }
      finally {
      release(session);
    }
  }

  public CameraShop getCameraShop(CameraDTO cameraDTO){
    Session session = this.getSession();
    try {
      Query q = SQL.getCameraShop(session, cameraDTO);
      Object object=CollectionUtil.getFirst(q.list()) ;
      return (CameraShop)object;
    } finally {
      release(session);
    }
  }

  public Camera getCamera(Camera camera){
    Session session = this.getSession();
    try {
      Query q = SQL.getCamera(session, camera);
      Object object=CollectionUtil.getFirst(q.list()) ;
      if(object != null){
        return (Camera)object;
      } else{
        return null;
      }

    } finally {
      release(session);
    }
  }

 public Camera getCameraBySerialNo(String serialNo){
    Session session = this.getSession();
    try {
      Query q = SQL.getCameraBySerialNo(session, serialNo);
      Object object=CollectionUtil.getFirst(q.list()) ;
      if(object != null){
        return (Camera)object;
      } else{
        return null;
      }

    } finally {
      release(session);
    }
  }

  public int countCameraRecordDTOList(String id) {
    Session session = this.getSession();
    try {
      Query query = SQL.countCameraRecordDTO(session, id);
      return NumberUtil.intValue(query.uniqueResult());
    } finally {
      release(session);
    }
  }

  public List<CameraRecordDTO> getCameraRecordDTOList(Pager pager,String id ) {
    List<CameraRecordDTO> cameraRecordDTOs = new ArrayList<CameraRecordDTO>();
    Session session = this.getSession();
    try {
      Query query = SQL.getCameraRecordDTOList(session,pager,id);
      List<Object[]> result = query.list() ;
      if(CollectionUtils.isNotEmpty(result)){
        for(Object[] objects : result){
          CameraRecordDTO cameraRecordDTO = new CameraRecordDTO();
          cameraRecordDTO.setId(StringUtil.valueOf(objects[0]));
          cameraRecordDTO.setCamera_id(StringUtil.valueOf(objects[1]));
          cameraRecordDTO.setVehicle_no(StringUtil.valueOf(objects[2]));
          cameraRecordDTO.setArrive_date(formatter.format(NumberUtil.longValue(objects[3])));
          if("NONE".equals(StringUtil.valueOf(objects[4]))){
            cameraRecordDTO.setRef_order_type("无");
          }else if("WASH".equals(StringUtil.valueOf(objects[4]))){
            cameraRecordDTO.setRef_order_type("洗车");
          }
          cameraRecordDTO.setOrder_id(NumberUtil.longValue(objects[5]));
          cameraRecordDTO.setName(StringUtil.valueOf(objects[6]));
          cameraRecordDTOs.add(cameraRecordDTO);
        }
      }
      return cameraRecordDTOs;
    }
    finally {
      release(session);
    }
  }

  public String getShopNameByCameraId(Long id) {
    Session session = this.getSession();
    try {
      Query query = SQL.getShopNameByCameraId(session, id);
      Object object = CollectionUtil.getFirst(query.list()) ;
      if(object!=null){
        String shopName = StringUtil.valueOf(object);
        return shopName;
      }else{
        return "";
      }
    }
    finally {
      release(session);
    }
  }


  public CameraRecord getCameraRecordByVehicle_no(String vehicle_no){
    Session session = this.getSession();
    try {
      Query q = SQL.getCameraRecordByVehicle_no(session,vehicle_no);
      Object object=CollectionUtil.getFirst(q.list()) ;
      if(object != null){
        return (CameraRecord)object;
      } else{
        return null;
      }
    } finally {
      release(session);
    }
  }

  public CameraConfig getCameraConfigByCameraId(String CameraId){
    Session session = this.getSession();
    try {
      Query q = SQL.getCameraConfigByCameraId(session, CameraId);
      Object object=CollectionUtil.getFirst(q.list()) ;
      if(object != null){
        return (CameraConfig)object;
      } else{
        return null;
      }
    } finally {
      release(session);
    }
  }


  public List<CameraRecordDTO> getCameraRecordListByShopId(Pager pager,CameraSearchCondition condition){
    Session session = this.getSession();
    if(StringUtil.isEmpty(condition.getShopId())){
      return new ArrayList<CameraRecordDTO>();
    }
    List<CameraRecordDTO> cameraRecordDTOList = new ArrayList<CameraRecordDTO>();
    List<CameraRecord> cameraRecordList = new ArrayList<CameraRecord>();
    try{
      Query q = SQL.getCameraRecordByShopId(pager,session,condition) ;
      cameraRecordList =(List<CameraRecord>)q.list();
      for(CameraRecord cameraRecord:cameraRecordList){
        CameraRecordDTO cameraRecordDTO = cameraRecord.toCameraRecordDTO();
        cameraRecordDTOList.add(cameraRecordDTO);
      }
    }finally {
      release(session);
    }
    return cameraRecordDTOList;
  }

  public int countCameraRecordDTOListByShopId(CameraSearchCondition condition) {
    Session session = this.getSession();
    try {
      Query query = SQL.countCameraRecordDTOByShopId(session,condition);
      return NumberUtil.intValue(query.uniqueResult());
    } finally {
      release(session);
    }
  }

  public int getCountCameraConfigByShopId(String  shopId) {
    Session session = this.getSession();
    try {
      Query query = SQL.getCountCameraConfigByShopId(session,shopId);
      return NumberUtil.intValue(query.uniqueResult());
    } finally {
      release(session);
    }
  }



  public List<CameraConfigDTO> getCameraConfigByShopId(String shopId){
    Session session = this.getSession();
    List<CameraConfigDTO> cameraConfigDTOList = new ArrayList<CameraConfigDTO>();
    try {
      Query q = SQL.getCameraConfigByShopId(session, shopId);
      List<Object[]> result = q.list() ;
      if(CollectionUtils.isNotEmpty(result)){
        for(Object[] objects : result){
          CameraConfigDTO cameraConfigDTO = new CameraConfigDTO();
          cameraConfigDTO.setId(StringUtil.valueOf(objects[0]));
          cameraConfigDTO.setCamera_id(StringUtil.valueOf(objects[1]));
          cameraConfigDTO.setInterval_time_warn(StringUtil.valueOf(objects[2]));
          cameraConfigDTO.setMember_card(StringUtil.valueOf(objects[3]));
          cameraConfigDTO.setOrder_type(StringUtil.valueOf(objects[4]));
          cameraConfigDTO.setWhite_vehicle_nos(StringUtil.valueOf(objects[5]));
          cameraConfigDTO.setSerial_no(StringUtil.valueOf(objects[6]));
          cameraConfigDTO.setConstruction_project_text(StringUtil.valueOf(objects[7]));
          cameraConfigDTO.setConstruction_project_value(StringUtil.valueOf(objects[8]));
          cameraConfigDTOList.add(cameraConfigDTO);
        }
      }
      } finally {
      release(session);
    }
    return cameraConfigDTOList;
  }

  public List<CameraSearchCondition> getVehicle_nos(String vehicle_nos){
    Session session = this.getSession();
    List<CameraSearchCondition> cameraSearchConditions = new ArrayList<CameraSearchCondition>();
    try {
      Query q = SQL.getVehicle_nos(session, vehicle_nos);
      List<String> result = q.list() ;
      if(CollectionUtils.isNotEmpty(result)){
        for(String objects : result){
          CameraSearchCondition cameraSearchCondition = new CameraSearchCondition();
          cameraSearchCondition.setVehicle_nos(objects);
          cameraSearchConditions.add(cameraSearchCondition);
        }
      }
    } finally {
      release(session);
    }
    return cameraSearchConditions;
  }

  public List<ShopsDTO> getShopsByGPS (double minlat , double maxlat ,double minlon , double maxlon){
    Session session = this.getSession();
    List<ShopsDTO> shopsDTOs = new ArrayList<ShopsDTO>();
    try {
      Query q = SQL.getShopsByGPS(session ,minlat , maxlat ,minlon , maxlon);
      List<Object []> list = q.list();
      if (CollectionUtils.isNotEmpty(list)) {
        for ( Object[] objects : list) {
          ShopsDTO shopsDTO = new ShopsDTO();
          shopsDTO.setShopId((Long) objects[0]);
          shopsDTO.setShopName((String) objects[1]);
          shopsDTOs.add(shopsDTO);
        }
      }
    }finally {
      release(session);
    }
    return shopsDTOs;
  }

  SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


}
