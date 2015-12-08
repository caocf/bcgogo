package com.bcgogo.txn.service;

import com.bcgogo.common.AllListResult;
import com.bcgogo.common.Pager;
import com.bcgogo.config.model.MergeChangeLog;
import com.bcgogo.config.model.MergeRecord;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.*;
import com.bcgogo.enums.common.ObjectStatus;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.exception.PageException;
import com.bcgogo.product.dto.ProductLocalInfoDTO;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.SupplierInventoryDTO;
import com.bcgogo.txn.model.*;
import com.bcgogo.txn.model.app.AppointOrder;
import com.bcgogo.txn.service.productThrough.IProductThroughService;
import com.bcgogo.user.MergeType;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.MemberDTO;
import com.bcgogo.user.dto.MemberServiceDTO;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.user.merge.*;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.user.service.ISupplierService;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-1-11
 * Time: 上午4:52
 * To change this template use File | Settings | File Templates.
 */
@Component
public class MergeService implements IMergeService{

  private static final Logger LOG = LoggerFactory.getLogger(MergeService.class);

  @Autowired
  private TxnDaoManager txnDaoManager;

  public MergeResult validateMergeSupplier(MergeResult<SupplierDTO,MergeSupplierSnap> mergeResult,String parentIdStr, String[] chilIdStrs) throws BcgogoException {
    if(!NumberUtil.isNumber(parentIdStr)||StringUtil.hasEmptyVal(chilIdStrs)){
      mergeResult.setCustomerOrSupplierIdStr(parentIdStr);
      mergeResult.setSuccess(false);
      mergeResult.setMsg("供应商id异常，合并终止！");
      return mergeResult;
    }
    List<Long> childIds=new ArrayList<Long>();
    for(String childIdStr:chilIdStrs){
      if(!NumberUtil.isNumber(childIdStr)){
        mergeResult.setCustomerOrSupplierIdStr(parentIdStr);
        mergeResult.setSuccess(false);
        mergeResult.setMsg("被合并供应商id异常，合并终止！");
      }
      if(childIds.contains(NumberUtil.longValue(childIdStr))){
        LOG.warn("被合并供应商id重复异常！");
        continue;
      }
      childIds.add(NumberUtil.longValue(childIdStr));
    }
    mergeResult.setChildIds(childIds);
    IUserService userService=ServiceManager.getService(IUserService.class);
    ISearchService searchService=ServiceManager.getService(ISearchService.class);
    SupplierDTO parent= CollectionUtil.uniqueResult(userService.getSupplierById(mergeResult.getShopId(),NumberUtil.longValue(parentIdStr)));
    if(parent==null|| CustomerStatus.DISABLED.equals(parent.getStatus())){
      mergeResult.setCustomerOrSupplierIdStr(parentIdStr);
      mergeResult.setSuccess(false);
      mergeResult.setMsg("供应商不存在，已经被删除，或被合并！");
      return mergeResult;
    }
    SupplierDTO child=null;
    for(Long childId:childIds){
      child=CollectionUtil.uniqueResult(userService.getSupplierById(mergeResult.getShopId(),childId));
      if(child==null|| CustomerStatus.DISABLED.equals(child.getStatus())||child.getParentId()!=null){
        mergeResult.setSuccess(false);
        mergeResult.setMsg("被合并供应商不存在，已经被删除，或被合并！");
        return mergeResult;
      }
      if(parent.getSupplierShopId()==null&&child.getSupplierShopId()!=null){  //当前不支持批发商合并到普通供应商
        mergeResult.setSuccess(false);
        mergeResult.setMsg("批发商不可以合并到普通供应商！");
        return mergeResult;
      }
      if(parent.getSupplierShopId()!=null&&child.getSupplierShopId()!=null){
        mergeResult.setSuccess(false);
        mergeResult.setMsg("两个批发商无法合并！");
        return mergeResult;
      }
    }

    TxnWriter txnWriter = txnDaoManager.getWriter();
    Deposit parentDeposit = txnWriter.getDepositBySupplierId(mergeResult.getShopId(), NumberUtil.longValue(parentIdStr));
    if (parentDeposit != null) {
      parent.setDeposit(parentDeposit.getActuallyPaid());
    }

    Map settlementTypeMap = TxnConstant.getSettlementTypeMap(mergeResult.getLocale());
    Map inoiceCatagoryMap= TxnConstant.getInvoiceCatagoryMap(mergeResult.getLocale());
    Map<String, String> areaMap = TxnConstant.getAreaMap(mergeResult.getLocale());
    Map<String, String> customerTypeMap = TxnConstant.getCustomerTypeMap(mergeResult.getLocale());
    parent.setSettlementType(String.valueOf(settlementTypeMap.get(String.valueOf(parent.getSettlementTypeId()))));
    parent.setInvoiceCategory(String.valueOf(inoiceCatagoryMap.get(String.valueOf(parent.getInvoiceCategoryId()))));
    parent.setAreaStr(String.valueOf(areaMap.get(String.valueOf(parent.getAreaId()))));
    parent.setCategoryStr(String.valueOf(customerTypeMap.get(String.valueOf(parent.getCategory()))));
    parent.setCountSupplierReturn(searchService.countReturn(parent.getShopId(),parent.getId(), OrderTypes.RETURN, OrderStatus.SETTLED));
    mergeResult.setCustomerOrSupplierDTO(parent);
    return mergeResult;
  }

  public MergeResult validateMergeCustomer(MergeResult<CustomerDTO,MergeCustomerSnap> mergeResult,String parentIdStr, String[] chilIdStrs){
    ICustomerService customerService=ServiceManager.getService(ICustomerService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    if(!NumberUtil.isNumber(parentIdStr)|| ArrayUtil.isEmpty(chilIdStrs)||StringUtil.hasEmptyVal(chilIdStrs)){
      mergeResult.setCustomerOrSupplierIdStr(parentIdStr);
      mergeResult.setSuccess(false);
      mergeResult.setMsg("客户id异常，合并终止！！！");
      return mergeResult;
    }
    List<Long> childIds=new ArrayList<Long>();
    for(String childIdStr:chilIdStrs){
      if(!NumberUtil.isNumber(childIdStr)){
        mergeResult.setCustomerOrSupplierIdStr(parentIdStr);
        mergeResult.setSuccess(false);
        mergeResult.setMsg("被合并客户id异常，合并终止！！！");
        return mergeResult;
      }
      if(childIds.contains(NumberUtil.longValue(childIdStr))){
        mergeResult.setSuccess(false);
        mergeResult.setMsg("合并客户id重复异常！！！");
        return mergeResult;
      }
      childIds.add(NumberUtil.longValue(childIdStr));
    }
    mergeResult.setChildIds(childIds);
    CustomerDTO customerIndex=new CustomerDTO();
    customerIndex.setShopId(mergeResult.getShopId());
    customerIndex.setId(NumberUtil.longValue(parentIdStr));
    CustomerDTO parent= CollectionUtil.uniqueResult(customerService.getCustomers(customerIndex));
    TxnWriter txnWriter = txnDaoManager.getWriter();
    CustomerDeposit parentDeposit = txnWriter.queryCustomerDepositByShopIdAndCustomerId(mergeResult.getShopId(), NumberUtil.longValue(parentIdStr));
    if (parentDeposit != null) {
      parent.setDeposit(parentDeposit.getActuallyPaid());
    }
    if(parent==null|| CustomerStatus.DISABLED.equals(parent.getStatus())){
      mergeResult.setCustomerOrSupplierIdStr(parentIdStr);
      mergeResult.setSuccess(false);
      mergeResult.setMsg("客户不存在，已经被删除，或被合并！");
      return mergeResult;
    }
    parent.setIsObd(userService.isOBDCustomer(NumberUtil.longValue(parentIdStr)));
    CustomerDTO child=null;
    for(Long childId:childIds){
      customerIndex.setId(childId);
      child= CollectionUtil.uniqueResult(customerService.getCustomers(customerIndex));
      CustomerDeposit childDeposit = txnWriter.queryCustomerDepositByShopIdAndCustomerId(mergeResult.getShopId(), NumberUtil.longValue(parentIdStr));
      if (childDeposit != null) {
        child.setDeposit(childDeposit.getActuallyPaid());
      }

      if(child==null|| CustomerStatus.DISABLED.equals(child.getStatus())||child.getParentId()!=null){
        mergeResult.setSuccess(false);
        mergeResult.setMsg("被合并客户不存在，已经被删除，或被合并！");
        return mergeResult;
      }
      child.setIsObd(userService.isOBDCustomer(child.getId()));
      if(parent.getCustomerShopId()==null&&child.getCustomerShopId()!=null){  //当前不支持关联客户合并到非关联客户
        mergeResult.setSuccess(false);
        mergeResult.setMsg("关联客户不可以合并到非关联客户！");
        return mergeResult;
      }
      if(parent.getCustomerShopId()!=null&&child.getCustomerShopId()!=null){
        mergeResult.setSuccess(false);
        mergeResult.setMsg("两个关联客户无法合并！");
        return mergeResult;
      }
      if(parent.getIsObd() && child.getIsObd()) {
        mergeResult.setSuccess(false);
        mergeResult.setMsg("两个OBD客户无法合并！");
        return mergeResult;
      }
    }
    Map settlementTypeMap = TxnConstant.getSettlementTypeMap(mergeResult.getLocale());
    Map inoiceCatagoryMap= TxnConstant.getInvoiceCatagoryMap(mergeResult.getLocale());
    Map<String, String> areaMap = TxnConstant.getAreaMap(mergeResult.getLocale());
    Map<String, String> customerTypeMap = TxnConstant.getCustomerTypeMap(mergeResult.getLocale());
    CustomerDTO parentDTO = parent;
    parentDTO.setSettlementTypeStr(String.valueOf(settlementTypeMap.get(String.valueOf(parentDTO.getSettlementType()))));
    parentDTO.setInvoiceCategoryStr(String.valueOf(inoiceCatagoryMap.get(String.valueOf(parentDTO.getInvoiceCategory()))));
    parentDTO.setAreaStr(String.valueOf(areaMap.get(parentDTO.getArea())));
    parentDTO.setCustomerKindStr(String.valueOf(customerTypeMap.get(parentDTO.getCustomerKind())));
    mergeResult.setCustomerOrSupplierDTO(parentDTO);
    return mergeResult;
  }

  public MergeResult mergeCustomerHandler(MergeResult<CustomerDTO,MergeCustomerSnap> mergeResult,Long parentId, Long[] childIds) throws Exception {
    ICustomerService customerService=ServiceManager.getService(ICustomerService.class);
    IConfigService configService=ServiceManager.getService(IConfigService.class);
    ISearchService searchService=ServiceManager.getService(ISearchService.class);
    //合并user库
    customerService.mergeCustomerInfo(mergeResult,parentId,childIds);
    if(!mergeResult.isSuccess()){
      return mergeResult;
    }
    //合并txn库信息
    mergeCustomerTxnInfo(mergeResult,parentId,childIds);
    if(!mergeResult.isSuccess()){
      return mergeResult;
    }
    //合并search库orderIndex
    searchService.mergeCustomerOrderIndex(mergeResult,childIds);
    //config库保存保存合并过程的log记录
    configService.saveMergeRecord(mergeResult);
    return mergeResult;
  }


  private MergeResult mergeCustomerTxnInfo(MergeResult<CustomerDTO,MergeCustomerSnap> mergeResult,Long parentId, Long[] chilIds) throws Exception {
    TxnWriter txnWriter=txnDaoManager.getWriter();
    Object txnTransaction=txnWriter.begin();
    ITxnService txnService=ServiceManager.getService(ITxnService.class);
    try{
      LOG.info("begin merge customer txn info,time={}",new Date());
      mergeCustomerOrder(mergeResult, chilIds);
      mergeDebt(mergeResult,chilIds,parentId);
      mergeCustomerStatementAccountOrder(mergeResult,chilIds);
      mergeCustomerDraftOrder(mergeResult,chilIds);
      mergeMemberConsume(mergeResult);
      mergeRemindEvent(mergeResult,chilIds);
      for(Long childId:chilIds){
        List<MemberServiceDTO> memberServiceDTOs=null;
        Service service=null;
        MemberDTO parentMember=mergeResult.getMergeSnapMap().get(childId).getParentMember();
        MemberDTO childMember=mergeResult.getMergeSnapMap().get(childId).getChildMember();
        if(parentMember!=null&&CollectionUtil.isNotEmpty(parentMember.getMemberServiceDTOs())){
          parentMember.setMemberConsumeTotal(txnService.getMemberCardConsumeTotal(mergeResult.getShopId(), parentMember.getId()));
          for(MemberServiceDTO memberServiceDTO:parentMember.getMemberServiceDTOs()){
            service=txnWriter.getServiceById(mergeResult.getShopId(),memberServiceDTO.getServiceId());
            if(service!=null) memberServiceDTO.setServiceName(service.getName());
          }
        }
        if(childMember!=null&&CollectionUtil.isNotEmpty(childMember.getMemberServiceDTOs())){
          childMember.setMemberConsumeTotal(txnService.getMemberCardConsumeTotal(mergeResult.getShopId(), childMember.getId()));
          for(MemberServiceDTO memberServiceDTO:childMember.getMemberServiceDTOs()){
            service=txnWriter.getServiceById(mergeResult.getShopId(),memberServiceDTO.getServiceId());
            if(service!=null) memberServiceDTO.setServiceName(service.getName());
          }
        }

      }
      txnWriter.commit(txnTransaction);
      return mergeResult;
    }catch (Exception e){
      LOG.error("合并客户单据出现异常！");
      throw new BcgogoException(e.getMessage(), e);
    }finally {
      txnWriter.rollback(txnTransaction);
    }
  }

  public MergeResult mergeSupplierHandler(MergeResult<SupplierDTO,MergeSupplierSnap> mergeResult,Long parentId, Long[] chilIds) throws Exception{
    ISupplierService supplierService=ServiceManager.getService(ISupplierService.class);
    ISearchService searchService=ServiceManager.getService(ISearchService.class);
    IConfigService configService=ServiceManager.getService(IConfigService.class);
    //合并user库
    supplierService.mergeSupplierInfo(mergeResult, parentId, chilIds);
    if(!mergeResult.isSuccess()){
      return mergeResult;
    }
    //合并txn库信息
    mergeSupplierTxnInfo(mergeResult,parentId,chilIds);
    if(!mergeResult.isSuccess()){
      return mergeResult;
    }
    //合并search库orderIndex
    searchService.mergeSupplierOrderIndex(mergeResult,chilIds);
    //config库保存保存合并过程的log记录
    List<MergeChangeLog> mergeChangeLogs=new ArrayList<MergeChangeLog>();
    if(CollectionUtils.isNotEmpty(mergeResult.getMergeChangeLogs())){
      MergeChangeLog mergeChangeLog=null;
      for(MergeChangeLogDTO logDTO:mergeResult.getMergeChangeLogs()){
        mergeChangeLog=new MergeChangeLog();
        mergeChangeLogs.add(mergeChangeLog.fromDTO(logDTO));
      }
    }
    configService.saveMergeRecord(mergeResult);
    return mergeResult;
  }

  private MergeResult mergeSupplierTxnInfo(MergeResult<SupplierDTO,MergeSupplierSnap> mergeResult,Long parentId, Long[] chilIds) throws Exception {
    ITxnService txnService=ServiceManager.getService(ITxnService.class);
    ISupplierPayableService supplierPayableService=ServiceManager.getService(ISupplierPayableService.class);
    TxnWriter txnWriter=txnDaoManager.getWriter();
    Object txnTransaction=txnWriter.begin();
    try{
      Long shopId=mergeResult.getShopId();
      SupplierDTO  parent=mergeResult.getCustomerOrSupplierDTO();
      supplierPayableService.fillSupplierTradeInfo(parent);
      SupplierDTO child=null;
      for(Long childId:chilIds){
        child=mergeResult.getMergeSnapMap().get(childId).getChild();
        supplierPayableService.fillSupplierTradeInfo(child);
      }
      mergeSupplierOrder(mergeResult,chilIds);
      mergeSupplierTradeRecords(mergeResult,chilIds, parentId);
      mergeSupplierRecord(mergeResult,chilIds);
      mergeSupplierStatementAccountOrder(mergeResult,chilIds);
      mergeSupplierDraftOrder(mergeResult,chilIds);
      mergeSupplierStatData(mergeResult,chilIds);
      mergeProductThrough(txnWriter,mergeResult,chilIds);
      txnWriter.commit(txnTransaction);
      return mergeResult;
    }catch (Exception e){
      LOG.error("合并供应商单据出现异常！");
      throw new BcgogoException(e.getMessage(), e);
    }finally {
      txnWriter.rollback(txnTransaction);
    }
  }

  private MergeResult mergeSupplierStatData(MergeResult<SupplierDTO,MergeSupplierSnap> mergeResult,Long[] childIds){
    TxnWriter txnWriter=txnDaoManager.getWriter();
    SupplierDTO parentDTO=mergeResult.getCustomerOrSupplierDTO();
    List<PurchaseReturnMonthStat> parentReturnStats=txnWriter.getAllPurchaseReturnMonthStatBySupplierIds(parentDTO.getShopId(),parentDTO.getId());
    Map<String,PurchaseReturnMonthStat> returnStatMap=new HashMap<String,PurchaseReturnMonthStat>();
    String key=null;
    for(PurchaseReturnMonthStat returnMonthStat:parentReturnStats){
      key=String.valueOf(returnMonthStat.getStatYear())+String.valueOf(returnMonthStat.getStatMonth());
      returnStatMap.put(key,returnMonthStat);
    }
    List<SupplierTranMonthStat> tranMonthStats=txnWriter.getAllSupplierTranMonthStatBySupplierIds(parentDTO.getShopId(),parentDTO.getId());
    Map<String,SupplierTranMonthStat> tranMonthStatMap=new HashMap<String,SupplierTranMonthStat>();
    for(SupplierTranMonthStat tranMonthStat:tranMonthStats){
      key=String.valueOf(tranMonthStat.getStatYear())+String.valueOf(tranMonthStat.getStatMonth());
      tranMonthStatMap.put(key,tranMonthStat);
    }
    PurchaseReturnMonthStat parentReturnMonthStat=null;
    SupplierTranMonthStat parentTranMonthStat=null;
    for(Long childId:childIds){
      //merge purchaseReturnMonthStat
      for(PurchaseReturnMonthStat childReturnMonthStat :txnWriter.getAllPurchaseReturnMonthStatBySupplierIds(parentDTO.getShopId(), childIds)){
        key=String.valueOf(childReturnMonthStat.getStatYear())+String.valueOf(childReturnMonthStat.getStatMonth());
        if(!returnStatMap.containsKey(key)){
          childReturnMonthStat.setSupplierId(parentDTO.getId());
          txnWriter.update(childReturnMonthStat);
          continue;
        }
        parentReturnMonthStat=returnStatMap.get(key);
        mergeResult.getMergeChangeLogs().add(MergeLogUtil.log_purchaseReturnMonthStat_supplierId(mergeResult.getShopId(), mergeResult.getUserId(),
            childReturnMonthStat.getId(), childReturnMonthStat.getSupplierId(), parentDTO.getId()));
        parentReturnMonthStat.setTimes(NumberUtil.intValue(parentReturnMonthStat.getTimes())+NumberUtil.intValue(childReturnMonthStat.getTimes()));
        parentReturnMonthStat.setAmount(NumberUtil.doubleVal(parentReturnMonthStat.getAmount())+NumberUtil.doubleVal(childReturnMonthStat.getAmount()));
        parentReturnMonthStat.setTotal(NumberUtil.doubleVal(parentReturnMonthStat.getTotal())+NumberUtil.doubleVal(childReturnMonthStat.getTotal()));
        childReturnMonthStat.setTimes(0);
        childReturnMonthStat.setAmount(0d);
        childReturnMonthStat.setTotal(0d);
        txnWriter.update(childReturnMonthStat);
        txnWriter.update(parentReturnMonthStat);
      }
      //merge supplierTranMonthStat
      for(SupplierTranMonthStat childTranMonthStat :txnWriter.getAllSupplierTranMonthStatBySupplierIds(parentDTO.getShopId(), childIds)){
        key=String.valueOf(childTranMonthStat.getStatYear())+String.valueOf(childTranMonthStat.getStatMonth());
        if(!tranMonthStatMap.containsKey(key)){
          childTranMonthStat.setSupplierId(parentDTO.getId());
          txnWriter.update(childTranMonthStat);
          continue;
        }
        parentTranMonthStat=tranMonthStatMap.get(key);
        mergeResult.getMergeChangeLogs().add(MergeLogUtil.log_supplierTranMonthStat_supplierId(mergeResult.getShopId(), mergeResult.getUserId(),
            childTranMonthStat.getId(), childTranMonthStat.getSupplierId(), parentDTO.getId()));
        parentTranMonthStat.setTimes(NumberUtil.intValue(parentTranMonthStat.getTimes())+NumberUtil.intValue(childTranMonthStat.getTimes()));
        parentTranMonthStat.setTotal(NumberUtil.doubleVal(parentTranMonthStat.getTotal())+NumberUtil.doubleVal(childTranMonthStat.getTotal()));
        childTranMonthStat.setTimes(0);
        childTranMonthStat.setTotal(0d);
        txnWriter.update(childTranMonthStat);
        txnWriter.update(parentTranMonthStat);

      }
    }
    txnWriter.flush();
    return mergeResult;
  }

  private MergeResult mergeSupplierRecord(MergeResult<SupplierDTO,MergeSupplierSnap> mergeResult,Long[] childIds){
    TxnWriter txnWriter=txnDaoManager.getWriter();
    SupplierDTO parentDTO=mergeResult.getCustomerOrSupplierDTO();
    ISupplierRecordService supplierRecordService= ServiceManager.getService(ISupplierRecordService.class);
    SupplierRecord parentRecord =supplierRecordService.getSupplierRecordBySupplierId(mergeResult.getShopId(), parentDTO.getId());
    if(parentRecord==null|| ObjectStatus.DISABLED.equals(parentRecord.getStatus())){
      mergeResult.setCustomerOrSupplierIdStr(String.valueOf(parentDTO.getId()));
//      mergeResult.setMessage("供应商记录不存在，已经被删除，或被合并！");
      LOG.error("parentRecord is null and exception!parentId={}",parentDTO.getId());
      return mergeResult;
    }
    SupplierRecord childRecord=null;
    for(Long childId:childIds){
      childRecord=supplierRecordService.getSupplierRecordBySupplierId(mergeResult.getShopId(), childId);
      if(childRecord==null|| ObjectStatus.DISABLED.equals(childRecord.getStatus())){
        mergeResult.setCustomerOrSupplierIdStr(String.valueOf(parentDTO.getId()));
        LOG.error("childRecord is null and exception!childId={}",childId);
        return mergeResult;
      }
      parentRecord.setCreditAmount(NumberUtil.doubleVal(parentRecord.getCreditAmount())+NumberUtil.doubleVal(childRecord.getCreditAmount()));
      childRecord.setStatus(ObjectStatus.DISABLED);
      txnWriter.update(childRecord);
    }
    return mergeResult;
  }

  private MergeResult mergeSupplierTradeRecords(MergeResult<SupplierDTO, MergeSupplierSnap> mergeResult, Long[] childIds, Long parentId){
    TxnWriter txnWriter=txnDaoManager.getWriter();
    SupplierDTO parentDTO=mergeResult.getCustomerOrSupplierDTO();
    //merge payable
    for(Payable payable:txnWriter.getAllPayablesBySupplierIds(mergeResult.getShopId(), childIds)){
      if(payable==null){
        LOG.warn("payable is null!");
        continue;
      }
      mergeResult.getMergeChangeLogs().add(MergeLogUtil.log_payable_supplierId(mergeResult.getShopId(), mergeResult.getUserId(),
          payable.getId(), payable.getSupplierId(), parentDTO.getId()));
      payable.setSupplierId(parentDTO.getId());
      txnWriter.update(payable);
    }
    //merge deposit
    List<Deposit> childDeposits = txnWriter.getDepositsBySupplierIds(mergeResult.getShopId(), childIds);
    if(CollectionUtils.isNotEmpty(childDeposits)){
      Deposit parentDeposit = txnWriter.getDepositBySupplierId(mergeResult.getShopId(), parentId);
      if(parentDeposit == null){
        parentDeposit = new Deposit();
        parentDeposit.setShopId(mergeResult.getShopId());
        parentDeposit.setSupplierId(parentId);
      }
      double allCash = 0, allBankAmount = 0, allCheckAmount = 0, allPaid = 0;

      for(Deposit deposit: childDeposits){
        if(deposit==null){
          LOG.warn("deposit is null!");
          continue;
        }
        allCash += NumberUtil.doubleVal(deposit.getCash());
        allBankAmount += NumberUtil.doubleVal(deposit.getBankCardAmount());
        allCheckAmount += NumberUtil.doubleVal(deposit.getCheckAmount());
        allPaid += NumberUtil.doubleVal(deposit.getActuallyPaid());
      }
      mergeResult.getMergeChangeLogs().add(MergeLogUtil.log_deposit_cash(mergeResult.getShopId(), mergeResult.getUserId(),
          parentDeposit.getId(), parentDeposit.getCash(), NumberUtil.round(NumberUtil.doubleVal(parentDeposit.getCash()) + allCash, 2)));
      mergeResult.getMergeChangeLogs().add(MergeLogUtil.log_deposit_bankCard(mergeResult.getShopId(), mergeResult.getUserId(),
          parentDeposit.getId(), parentDeposit.getBankCardAmount(), NumberUtil.round(NumberUtil.doubleVal(parentDeposit.getBankCardAmount()) + allBankAmount, 2)));
      mergeResult.getMergeChangeLogs().add(MergeLogUtil.log_deposit_checkAmount(mergeResult.getShopId(), mergeResult.getUserId(),
          parentDeposit.getId(), parentDeposit.getCheckAmount(), NumberUtil.round(NumberUtil.doubleVal(parentDeposit.getCheckAmount()) + allCheckAmount, 2)));
      mergeResult.getMergeChangeLogs().add(MergeLogUtil.log_deposit_actuallyPaid(mergeResult.getShopId(), mergeResult.getUserId(),
          parentDeposit.getId(), parentDeposit.getActuallyPaid(), NumberUtil.round(NumberUtil.doubleVal(parentDeposit.getActuallyPaid()) + allPaid, 2)));
      parentDeposit.setCash(NumberUtil.round(NumberUtil.doubleVal(parentDeposit.getCash()) + allCash, 2));
      parentDeposit.setBankCardAmount(NumberUtil.round(NumberUtil.doubleVal(parentDeposit.getBankCardAmount()) + allBankAmount, 2));
      parentDeposit.setCheckAmount(NumberUtil.round(NumberUtil.doubleVal(parentDeposit.getCheckAmount()) + allCheckAmount, 2));
      parentDeposit.setActuallyPaid(NumberUtil.round(NumberUtil.doubleVal(parentDeposit.getActuallyPaid()) + allPaid, 2));
      txnWriter.saveOrUpdate(parentDeposit);
    }

    if (CollectionUtils.isNotEmpty(childDeposits)) {
      mergeResult.getMergeSnapMap().get(childDeposits.get(0).getSupplierId()).getChild().setDeposit(childDeposits.get(0).getActuallyPaid());
    }

    // array to list
    List<Long> childIdList = new ArrayList<Long>();
    if (!ArrayUtils.isEmpty(childIds)) {
      for (Long id : childIds) {
        if (id != null) {
          childIdList.add(id);
        }
      }
    }
    // merge deposit orders
    List<DepositOrder> depositOrders = txnWriter.queryDepositOrderByShopIdAndIdsAndType(mergeResult.getShopId(), childIdList, "supplier");
    if (!CollectionUtils.isEmpty(depositOrders)) {
      for (DepositOrder depositOrder : depositOrders) {
        mergeResult.getMergeChangeLogs().add(MergeLogUtil.log_deposit_order_supplierId(mergeResult.getShopId(), mergeResult.getUserId(),
            depositOrder.getId(), depositOrder.getSupplierId(), parentDTO.getId()));
        depositOrder.setSupplierId(parentId);
        txnWriter.saveOrUpdate(depositOrder);
      }
    }

    //merge payableHistoryRecord
    for(PayableHistoryRecord record:txnWriter.getAllPayableHistoryRecordBySupplierIds(mergeResult.getShopId(), childIds)){
      if(record==null){
        LOG.warn("payableHistoryRecord is null!");
        continue;
      }
      mergeResult.getMergeChangeLogs().add(MergeLogUtil.log_payableHistoryRecord_supplierId(mergeResult.getShopId(),mergeResult.getUserId(),
          record.getId(),record.getSupplierId(),parentDTO.getId()));
      record.setSupplierId(parentDTO.getId());
      txnWriter.update(record);
    }
    //merge payableHistory
    for(PayableHistory record:txnWriter.getAllPayableHistoryBySupplierIds(mergeResult.getShopId(), childIds)){
      if(record==null){
        LOG.warn("payableHistoryRecord is null!");
        continue;
      }
      mergeResult.getMergeChangeLogs().add(MergeLogUtil.log_payableHistory_supplierId(mergeResult.getShopId(),mergeResult.getUserId(),
          record.getId(),record.getSupplierId(),parentDTO.getId()));
      record.setSupplierId(parentDTO.getId());
      txnWriter.update(record);
    }
    //merge supplierReturnPayable
    for(SupplierReturnPayable returnPayable:txnWriter.getAllSupplierReturnPayableBySupplierIds(mergeResult.getShopId(), childIds)){
      if(returnPayable==null){
        LOG.warn("supplierReturnPayable is null!");
        continue;
      }
      mergeResult.getMergeChangeLogs().add(MergeLogUtil.log_supplierReturnPayable_supplierId(mergeResult.getShopId(), mergeResult.getUserId(),
          returnPayable.getId(), returnPayable.getSupplierId(), parentDTO.getId()));
      returnPayable.setSupplierId(parentDTO.getId());
      txnWriter.update(returnPayable);
    }
    return mergeResult;
  }

  //出入库打通明细
  private void mergeProductThrough(TxnWriter writer, MergeResult<SupplierDTO, MergeSupplierSnap> mergeResult, Long[] childIds)throws Exception{
    IProductService productService = ServiceManager.getService(IProductService.class);
    IInventoryService inventoryService = ServiceManager.getService(IInventoryService.class);
    IProductThroughService productThroughService = ServiceManager.getService(IProductThroughService.class);
    SupplierDTO parentDTO = mergeResult.getCustomerOrSupplierDTO();
    Set<Long> supplierIds = new HashSet<Long>(Arrays.asList(childIds)); //childIds+ parentDTOId
    supplierIds.add(parentDTO.getId());
    //更新入库记录
    List<InStorageRecord> inStorageRecords = writer.getInStorageRecordBySupplierIds(mergeResult.getShopId(), new HashSet<Long>(Arrays.asList(childIds)));
    for (InStorageRecord inStorageRecord : inStorageRecords) {
      if (inStorageRecord == null) continue;
      mergeResult.getMergeChangeLogs().add(MergeLogUtil.log_inStorageRecord_supplierId(mergeResult.getShopId(), mergeResult.getUserId(),
          inStorageRecord.getId(), inStorageRecord.getSupplierId(), parentDTO.getId()));
      inStorageRecord.setSupplierId(parentDTO.getId());
      writer.update(inStorageRecord);
    }
    //更新出库记录   出库记录上的supplierRelatedAmount 需要合并relatedItemId—supplierId相同的值
    List<OutStorageRelation> outStorageRelations = writer.getOutStorageRelationBySupplierIds(mergeResult.getShopId(), supplierIds.toArray(new Long[supplierIds.size()]));
    Map<String,Double> fromSupplierRelatedAmountMap = new HashMap<String, Double>();
    Map<String,Double> toSupplierRelatedAmountMap = new HashMap<String, Double>();
    for (OutStorageRelation outStorageRelation : outStorageRelations) {
      //被合并的供应商的supplierRelatedAmount 多条相同itemId，supplierId的只取一条
     if(outStorageRelation.getRelatedSupplierId()!= null && ArrayUtil.contains(childIds,outStorageRelation.getRelatedSupplierId())){
       StringBuffer fromSupplierRelatedAmountKey = new StringBuffer();
       fromSupplierRelatedAmountKey.append(String.valueOf(outStorageRelation.getRelatedItemId())).append("_").append(String.valueOf(outStorageRelation.getRelatedSupplierId()));
       Double fromSupplierRelatedAmount =fromSupplierRelatedAmountMap.get(fromSupplierRelatedAmountKey.toString());
       if(fromSupplierRelatedAmount == null){
         fromSupplierRelatedAmountMap.put(fromSupplierRelatedAmountKey.toString(),outStorageRelation.getSupplierRelatedAmount());
       }
     }
      //合并到的供应商的supplierRelatedAmount 多条相同itemId，supplierId的只取一条
      if (outStorageRelation.getRelatedSupplierId() != null && outStorageRelation.getRelatedSupplierId().equals(parentDTO.getId())) {
        StringBuffer toSupplierRelatedAmountKey = new StringBuffer();
        toSupplierRelatedAmountKey.append(String.valueOf(outStorageRelation.getRelatedItemId())).append("_").append(String.valueOf(outStorageRelation.getRelatedSupplierId()));
        Double toSupplierRelatedAmount = toSupplierRelatedAmountMap.get(toSupplierRelatedAmountKey.toString());
        if (toSupplierRelatedAmount == null) {
          toSupplierRelatedAmountMap.put(toSupplierRelatedAmountKey.toString(), outStorageRelation.getSupplierRelatedAmount());
        }
      }
    }


    for (OutStorageRelation outStorageRelation : outStorageRelations) {
      if (outStorageRelation == null) continue;
      double newSupplierRelatedAmount = 0;
      for (Long supplierId : supplierIds) {
        StringBuffer supplierRelatedAmountKey = new StringBuffer();
        supplierRelatedAmountKey.append(String.valueOf(outStorageRelation.getRelatedItemId())).append("_").append(String.valueOf(supplierId));
        double fromRelatedAmount = NumberUtil.doubleVal(fromSupplierRelatedAmountMap.get(supplierRelatedAmountKey.toString()));
        newSupplierRelatedAmount+= fromRelatedAmount;
        double toRelatedAmount = NumberUtil.doubleVal(toSupplierRelatedAmountMap.get(supplierRelatedAmountKey.toString()));
        newSupplierRelatedAmount+= toRelatedAmount;
      }
      if (outStorageRelation.getRelatedSupplierId() != null && !outStorageRelation.getRelatedSupplierId().equals(parentDTO.getId())) {
        mergeResult.getMergeChangeLogs().add(MergeLogUtil.log_outStorageRelation_supplierId(mergeResult.getShopId(), mergeResult.getUserId(),
            outStorageRelation.getId(), outStorageRelation.getRelatedSupplierId(), parentDTO.getId()));
      }
      outStorageRelation.setRelatedSupplierId(parentDTO.getId());
      outStorageRelation.setSupplierRelatedAmount(newSupplierRelatedAmount);
      writer.update(outStorageRelation);
    }
    //合并供应商库存
    //1.取出需要合并有关的两个供应商的所有商品
    List<SupplierInventoryDTO> supplierInventoryDTOs = writer.getSupplierInventoryDTOBySupplierIds(mergeResult.getShopId(),supplierIds);
    //分成两组
    List<SupplierInventoryDTO> mergeFromList = new ArrayList<SupplierInventoryDTO>();
    Map<String,SupplierInventoryDTO> mergeToMap = new HashMap<String, SupplierInventoryDTO>(); //key是supplierId_productId_storehouseId

    if(CollectionUtils.isNotEmpty(supplierInventoryDTOs)){
      Set<Long> productIds = new HashSet<Long>();
      for(SupplierInventoryDTO supplierInventoryDTO : supplierInventoryDTOs){
        if(supplierInventoryDTO.getProductId() != null){
          productIds.add(supplierInventoryDTO.getProductId());
        }
        if(supplierInventoryDTO.getSupplierId() != null
            && ArrayUtil.contains(childIds,supplierInventoryDTO.getSupplierId())
            && !supplierInventoryDTO.getSupplierId().equals(parentDTO.getId())){
          mergeFromList.add(supplierInventoryDTO);
        }else if(supplierInventoryDTO.getSupplierId() != null && supplierInventoryDTO.getSupplierId().equals(parentDTO.getId())){
          String key = supplierInventoryDTO.toMapKey(supplierInventoryDTO.getSupplierId(), supplierInventoryDTO.getProductId(), supplierInventoryDTO.getStorehouseId());
          mergeToMap.put(key, supplierInventoryDTO);
        }
      }


      if(CollectionUtils.isNotEmpty(mergeFromList)){
        Map<Long,ProductLocalInfoDTO> productLocalInfoDTOMap = productService.getProductLocalInfoMap(mergeResult.getShopId(),productIds.toArray(new Long[productIds.size()]));
        for(SupplierInventoryDTO mergeFromDTO : mergeFromList){
          String mergeToDTOKey = mergeFromDTO.toMapKey(parentDTO.getId(),mergeFromDTO.getProductId(),mergeFromDTO.getStorehouseId());
          SupplierInventoryDTO mergeToDTO =  mergeToMap.get(mergeToDTOKey);
          mergeFromDTO.setDisabled(YesNo.YES);
          if(mergeToDTO == null){
            mergeToDTO = new SupplierInventoryDTO(mergeFromDTO);
            mergeToDTO.setSupplierId(parentDTO.getId());
            mergeToDTO.setId(null);
            mergeToDTO.setDisabled(YesNo.NO);
            mergeToMap.put(mergeToDTOKey,mergeToDTO);
          }else{
            double preMaxStoragePrice = NumberUtil.doubleVal(mergeFromDTO.getMaxStoragePrice());
            double preMinStoragePrice = NumberUtil.doubleVal(mergeFromDTO.getMinStoragePrice());
            double preAverageStoragePrice = NumberUtil.doubleVal(mergeFromDTO.getAverageStoragePrice());
            double preTotalInStorageAmount = NumberUtil.doubleVal(mergeFromDTO.getTotalInStorageAmount());
            double preRemainStorageAmount = NumberUtil.doubleVal(mergeFromDTO.getRemainAmount());
            double preLastStorageAmount = NumberUtil.doubleVal(mergeFromDTO.getLastStorageAmount());
            double preLastPurchasePrice = NumberUtil.doubleVal(mergeFromDTO.getLastStoragePrice());

            double newMaxStoragePrice = NumberUtil.doubleVal(mergeToDTO.getMaxStoragePrice());
            double newMinStoragePrice = NumberUtil.doubleVal(mergeToDTO.getMinStoragePrice());
            double newAverageStoragePrice = NumberUtil.doubleVal(mergeToDTO.getAverageStoragePrice());
            double newTotalInStorageAmount = NumberUtil.doubleVal(mergeToDTO.getTotalInStorageAmount());
            double newRemainStorageAmount = NumberUtil.doubleVal(mergeToDTO.getRemainAmount());
            double newLastStorageAmount = NumberUtil.doubleVal(mergeToDTO.getLastStorageAmount());
            double newLastPurchasePrice = NumberUtil.doubleVal(mergeToDTO.getLastStoragePrice());

            String mergeFromDTOUnit = mergeFromDTO.getUnit();
            String mergeToDTOUnit = mergeToDTO.getUnit();
            ProductLocalInfoDTO productLocalInfoDTO = productLocalInfoDTOMap.get(mergeFromDTO.getProductId());
            if(productLocalInfoDTO != null){
              mergeToDTOUnit = productLocalInfoDTO.getSellUnit();
            }
            if (UnitUtil.isStorageUnit(mergeFromDTOUnit, productLocalInfoDTO)) {
              preMaxStoragePrice = preMaxStoragePrice / productLocalInfoDTO.getRate();
              preAverageStoragePrice = preAverageStoragePrice / productLocalInfoDTO.getRate();
              preMinStoragePrice = preMinStoragePrice / productLocalInfoDTO.getRate();
              preTotalInStorageAmount = preTotalInStorageAmount * productLocalInfoDTO.getRate();
              preRemainStorageAmount = preRemainStorageAmount * productLocalInfoDTO.getRate();
              preLastStorageAmount = preLastStorageAmount * productLocalInfoDTO.getRate();
              preLastPurchasePrice = preLastPurchasePrice / productLocalInfoDTO.getRate();
            }
            if (UnitUtil.isStorageUnit(mergeToDTOUnit, productLocalInfoDTO)) {
              newMaxStoragePrice = newMaxStoragePrice / productLocalInfoDTO.getRate();
              newAverageStoragePrice = newAverageStoragePrice / productLocalInfoDTO.getRate();
              newMinStoragePrice = newMinStoragePrice / productLocalInfoDTO.getRate();
              newTotalInStorageAmount = newTotalInStorageAmount * productLocalInfoDTO.getRate();
              newRemainStorageAmount = newRemainStorageAmount * productLocalInfoDTO.getRate();
              newLastStorageAmount = newLastStorageAmount * productLocalInfoDTO.getRate();
              newLastPurchasePrice = newLastPurchasePrice / productLocalInfoDTO.getRate();
            }

            mergeToDTO.setUnit(mergeFromDTOUnit);
            mergeToDTO.setTotalInStorageAmount(NumberUtil.round(preTotalInStorageAmount+newTotalInStorageAmount,2));
            mergeToDTO.setRemainAmount(NumberUtil.round(preRemainStorageAmount+newRemainStorageAmount,2));
            newAverageStoragePrice =  inventoryService.calculateInventoryAveragePrice(preAverageStoragePrice, preRemainStorageAmount, newAverageStoragePrice, newRemainStorageAmount);
            mergeToDTO.setAverageStoragePrice(NumberUtil.round(newAverageStoragePrice,2));
            if(preMaxStoragePrice>newMaxStoragePrice){
              mergeToDTO.setMaxStoragePrice(preMaxStoragePrice);
            }
            if(preMinStoragePrice >0 && preMinStoragePrice < newMinStoragePrice){
              mergeToDTO.setMinStoragePrice(preMinStoragePrice);
            }

            if(mergeFromDTO.getLastStorageTime() != null && mergeFromDTO.getLastPurchaseInventoryOrderId() != null
                &&(mergeToDTO.getLastStorageTime() == null || mergeFromDTO.getLastStorageTime()>mergeToDTO.getLastStorageTime())){
              mergeToDTO.setLastStorageAmount(preLastStorageAmount);
              mergeToDTO.setLastStoragePrice(preLastPurchasePrice);
              mergeToDTO.setLastPurchaseInventoryOrderId(mergeFromDTO.getLastPurchaseInventoryOrderId());
            }
          }
        }
        mergeFromList.addAll(mergeToMap.values());
        productThroughService.saveOrUpdateSupplierInventoryByModify(writer,mergeFromList);
      }
    }
  }


  private MergeResult mergeCustomerDraftOrder(MergeResult<CustomerDTO,MergeCustomerSnap> mergeResult,Long[] childIds){
    IDraftOrderService draftOrderService= ServiceManager.getService(IDraftOrderService.class);
    CustomerDTO parentDTO=mergeResult.getCustomerOrSupplierDTO();
    List<DraftOrder> draftOrders=null;
    for(Long childId:childIds){
      draftOrders= draftOrderService.getDraftOrdersByCustomerOrSupplierId(mergeResult.getShopId(),childId);
      if(CollectionUtils.isEmpty(draftOrders)){
        LOG.info("被合并的客户customerId={},没有草稿信息",childId);
        continue;
      }
      for(DraftOrder draftOrder:draftOrders){
        if(draftOrder==null){
          continue;
        }
        draftOrder.setCustomerOrSupplierId(parentDTO.getId());    //todo 客户信息变更？
        draftOrder.setCustomerOrSupplierName(parentDTO.getName());
        draftOrder.setAbbr(parentDTO.getAddress());
        draftOrder.setContact(parentDTO.getContact());
        draftOrder.setFax(parentDTO.getFax());
        draftOrder.setLandLine(parentDTO.getLandLine());
        draftOrder.setEmail(parentDTO.getEmail());
        draftOrder.setMobile(parentDTO.getMobile());
      }
    }
    return mergeResult;
  }

  private MergeResult mergeSupplierInventory(MergeResult<SupplierDTO,MergeSupplierSnap> mergeResult,Long[] childIds){
    TxnWriter txnWriter=txnDaoManager.getWriter();
    SupplierDTO parentDTO=mergeResult.getCustomerOrSupplierDTO();
    IProductThroughService productThroughService=ServiceManager.getService(IProductThroughService.class);
    for(Long childId:childIds){
      List<SupplierInventory> supplierInventoryList=productThroughService.getSupplierAllInventory(mergeResult.getShopId(),childId);
      if(CollectionUtil.isNotEmpty(supplierInventoryList)){
        for(SupplierInventory supplierInventory:supplierInventoryList){
          if(supplierInventory==null) continue;
          mergeResult.getMergeChangeLogs().add(MergeLogUtil.log_supplierInventory_customerId(mergeResult.getShopId(), mergeResult.getUserId(),
              supplierInventory.getId(), supplierInventory.getSupplierId(),parentDTO.getId()));
          supplierInventory.setSupplierId(parentDTO.getId());
          supplierInventory.setSupplierId(parentDTO.getId());
          txnWriter.update(supplierInventory);
        }
      }
    }
    return mergeResult;
  }

  private MergeResult mergeSupplierDraftOrder(MergeResult<SupplierDTO,MergeSupplierSnap> mergeResult,Long[] childIds){
    IDraftOrderService draftOrderService=ServiceManager.getService(IDraftOrderService.class);
    SupplierDTO parentDTO=mergeResult.getCustomerOrSupplierDTO();
    List<DraftOrder> draftOrders=null;
    for(Long childId:childIds){
      draftOrders= draftOrderService.getDraftOrdersByCustomerOrSupplierId(mergeResult.getShopId(),childId);
      if(CollectionUtils.isEmpty(draftOrders)){
        LOG.info("被合并的供应商supplierId={},没有草稿信息",childId);
        continue;
      }
      for(DraftOrder draftOrder:draftOrders){
        if(draftOrder==null){
          continue;
        }
        draftOrder.setCustomerOrSupplierId(parentDTO.getId());
        draftOrder.setCustomerOrSupplierName(parentDTO.getName());
        draftOrder.setAbbr(parentDTO.getAddress());
        draftOrder.setContact(parentDTO.getContact());
        draftOrder.setFax(parentDTO.getFax());
        draftOrder.setLandLine(parentDTO.getLandLine());
        draftOrder.setEmail(parentDTO.getEmail());
        draftOrder.setMobile(parentDTO.getMobile());
      }
    }
    return mergeResult;
  }

  private MergeResult mergeCustomerStatementAccountOrder(MergeResult<CustomerDTO,MergeCustomerSnap> mergeResult,Long[] childIds){
    TxnWriter txnWriter=txnDaoManager.getWriter();
    CustomerDTO parentDTO=mergeResult.getCustomerOrSupplierDTO();
    List<StatementAccountOrder> statementAccountOrders=getCustomerOrSupplierOrders(mergeResult.getShopId(),OrderTypes.CUSTOMER_STATEMENT_ACCOUNT,childIds);
    for(StatementAccountOrder order:statementAccountOrders){
      if(order==null) continue;
      mergeResult.getMergeChangeLogs().add(MergeLogUtil.log_statementAccountOrder(mergeResult.getShopId(), mergeResult.getUserId(),
          order.getId(), order.getCustomerOrSupplierId(), parentDTO.getId(), UserConstant.CSType.CUSTOMER));
      order.setCustomerOrSupplierId(parentDTO.getId());
      txnWriter.update(order);
    }
    return mergeResult;
  }

  private MergeResult mergeSupplierStatementAccountOrder(MergeResult<SupplierDTO,MergeSupplierSnap> mergeResult,Long[] childIds){
    TxnWriter txnWriter=txnDaoManager.getWriter();
    SupplierDTO parentDTO=mergeResult.getCustomerOrSupplierDTO();
    List<StatementAccountOrder> statementAccountOrders=getCustomerOrSupplierOrders(mergeResult.getShopId(),OrderTypes.SUPPLIER_STATEMENT_ACCOUNT,childIds);
    for(StatementAccountOrder order:statementAccountOrders){
      if(order==null) continue;
      mergeResult.getMergeChangeLogs().add(MergeLogUtil.log_statementAccountOrder(mergeResult.getShopId(), mergeResult.getUserId(),
          order.getId(), order.getCustomerOrSupplierId(), parentDTO.getId(), UserConstant.CSType.SUPPLIER));
      order.setCustomerOrSupplierId(parentDTO.getId());
      txnWriter.update(order);
    }
    return mergeResult;
  }


  private MergeResult mergeDebt(MergeResult<CustomerDTO,MergeCustomerSnap> mergeResult,Long[] childIds,Long parentId){
    TxnWriter txnWriter=txnDaoManager.getWriter();
    CustomerDTO parentDTO=mergeResult.getCustomerOrSupplierDTO();
    List<Debt> debts=txnWriter.getAllDebtsByCustomerIds(mergeResult.getShopId(), childIds);
    for(Debt debt:debts){
      if(debt==null){
        continue;
      }
      mergeResult.getMergeChangeLogs().add(MergeLogUtil.log_debt_customerId(mergeResult.getShopId(),mergeResult.getUserId(),
          debt.getId(),debt.getCustomerId(),parentDTO.getId()));
      mergeResult.getMergeSnapMap().get(debt.getCustomerId()).setChildDebt(debt.toDTO());
      debt.setCustomerId(parentDTO.getId());
      txnWriter.update(debt);
    }

    // array to list
    List<Long> childIdList = new ArrayList<Long>();
    if (!ArrayUtils.isEmpty(childIds)) {
      for (Long id : childIds) {
        if (id != null) {
          childIdList.add(id);
        }
      }
    }

    // add by zhuj 合并customerDeposit
    List<CustomerDeposit> childDeposits = txnWriter.queryCustomerDepositsByShopIdAndCustomerIds(mergeResult.getShopId(), childIdList);
    CustomerDeposit parentDeposit = txnWriter.queryCustomerDepositByShopIdAndCustomerId(mergeResult.getShopId(), parentId);
    if (parentDeposit != null || !CollectionUtils.isEmpty(childDeposits)) {
      if (parentDeposit == null) {
        parentDeposit = new CustomerDeposit();
        parentDeposit.setShopId(mergeResult.getShopId());
        parentDeposit.setActuallyPaid(0d);
        parentDeposit.setBankCardAmount(0d);
        parentDeposit.setCash(0d);
        parentDeposit.setCheckAmount(0d);
        parentDeposit.setCustomerId(parentId);
        txnWriter.save(parentDeposit);
      }
      double oldCash = parentDeposit.getCash();
      double oldBankAmount = parentDeposit.getBankCardAmount();
      double oldCheckAmount = parentDeposit.getCheckAmount();
      double oldAllPaid = parentDeposit.getActuallyPaid();
      double allCash = 0, allBankAmount = 0, allCheckAmount = 0, allPaid = 0;
      for (CustomerDeposit deposit : childDeposits) {
        if (deposit == null) {
          LOG.warn("deposit is null!");
          continue;
        }
        allCash += NumberUtil.doubleVal(deposit.getCash());
        allBankAmount += NumberUtil.doubleVal(deposit.getBankCardAmount());
        allCheckAmount += NumberUtil.doubleVal(deposit.getCheckAmount());
        allPaid += NumberUtil.doubleVal(deposit.getActuallyPaid());
      }
      parentDeposit.setCash(NumberUtil.round(NumberUtil.doubleVal(oldCash) + allCash, 2));
      parentDeposit.setBankCardAmount(NumberUtil.round(NumberUtil.doubleVal(oldBankAmount) + allBankAmount, 2));
      parentDeposit.setCheckAmount(NumberUtil.round(NumberUtil.doubleVal(oldCheckAmount) + allCheckAmount, 2));
      parentDeposit.setActuallyPaid(NumberUtil.round(NumberUtil.doubleVal(oldAllPaid) + allPaid, 2));

      //mergeResult.getMergeSnapMap().get(parentDTO.getId()).getParent().setDeposit(oldAllPaid);
      if (CollectionUtils.isNotEmpty(childDeposits)) {
        mergeResult.getMergeSnapMap().get(childDeposits.get(0).getCustomerId()).getChild().setDeposit(childDeposits.get(0).getActuallyPaid());
      }

      mergeResult.getMergeChangeLogs().add(MergeLogUtil.log_customer_deposit_cash(mergeResult.getShopId(), mergeResult.getUserId(),
          parentDeposit.getId(), oldCash, NumberUtil.round(NumberUtil.doubleVal(parentDeposit.getCash()), 2)));
      mergeResult.getMergeChangeLogs().add(MergeLogUtil.log_customer_deposit_bankCard(mergeResult.getShopId(), mergeResult.getUserId(),
          parentDeposit.getId(), oldBankAmount, NumberUtil.round(NumberUtil.doubleVal(parentDeposit.getBankCardAmount()), 2)));
      mergeResult.getMergeChangeLogs().add(MergeLogUtil.log_customer_deposit_checkAmount(mergeResult.getShopId(), mergeResult.getUserId(),
          parentDeposit.getId(), oldCheckAmount, NumberUtil.round(NumberUtil.doubleVal(parentDeposit.getCheckAmount()), 2)));
      mergeResult.getMergeChangeLogs().add(MergeLogUtil.log_customer_deposit_actuallyPaid(mergeResult.getShopId(), mergeResult.getUserId(),
          parentDeposit.getId(), oldAllPaid, NumberUtil.round(NumberUtil.doubleVal(parentDeposit.getActuallyPaid()), 2)));

      txnWriter.saveOrUpdate(parentDeposit);

      // merge deposit orders
      List<DepositOrder> depositOrders = txnWriter.queryDepositOrderByShopIdAndIdsAndType(mergeResult.getShopId(), childIdList, "customer");
      if (!CollectionUtils.isEmpty(depositOrders)) {
        for (DepositOrder depositOrder : depositOrders) {
          mergeResult.getMergeChangeLogs().add(MergeLogUtil.log_deposit_order_customerId(mergeResult.getShopId(), mergeResult.getUserId(),
              depositOrder.getId(), depositOrder.getCustomerId(), parentDTO.getId()));
          depositOrder.setCustomerId(parentId);
          txnWriter.saveOrUpdate(depositOrder);
        }
      }
    }


    List<Receivable> receivables=txnWriter.getAllReceivablesByCustomerIds(mergeResult.getShopId(), childIds);
    for(Receivable receivable:receivables){
      if(receivable==null){
        continue;
      }
      mergeResult.getMergeChangeLogs().add(MergeLogUtil.log_receivable_customerId(mergeResult.getShopId(), mergeResult.getUserId(),
          receivable.getId(), receivable.getCustomerId(), parentDTO.getId()));
        mergeResult.getMergeSnapMap().get(receivable.getCustomerId()).setChildReceivable(receivable.toDTO());
      receivable.setCustomerId(parentDTO.getId());
      txnWriter.update(receivable);
    }

    return mergeResult;
  }

  private MergeResult mergeRemindEvent(MergeResult<CustomerDTO,MergeCustomerSnap> mergeResult,Long[] childIds){
    TxnWriter txnWriter=txnDaoManager.getWriter();
    CustomerDTO parentDTO=mergeResult.getCustomerOrSupplierDTO();
    List<RemindEvent> cRemindEvents=txnWriter.getRemindEventByCustomerId(mergeResult.getShopId(), childIds);
    List<RemindEvent> pRemindEvents=txnWriter.getRemindEventByCustomerId(mergeResult.getShopId(),new Long[]{parentDTO.getId()});
    if(CollectionUtil.isEmpty(pRemindEvents)&&CollectionUtil.isNotEmpty(cRemindEvents)){
      for(RemindEvent remindEvent:cRemindEvents){
        if(remindEvent==null){
          continue;
        }
        mergeResult.getMergeChangeLogs().add(MergeLogUtil.log_remind_event_customerId(mergeResult.getShopId(),mergeResult.getUserId(),
            remindEvent.getId(),remindEvent.getCustomerId(),parentDTO.getId()));
        remindEvent.setCustomerId(parentDTO.getId());
        txnWriter.update(remindEvent);
      }
    }
    return mergeResult;
  }

  private MergeResult mergeMemberConsume(MergeResult mergeResult){
    TxnWriter txnWriter=txnDaoManager.getWriter();
    if(mergeResult.getChildMemberId()==null){
      return mergeResult;
    }
    for(Receivable receivable:txnWriter.getMemberConsumeReceivable(mergeResult.getShopId(),mergeResult.getChildMemberId())){
      if(receivable!=null&&receivable.getMemberId()!=null){
        receivable.setMemberId(mergeResult.getMergedMemberId());
        txnWriter.update(receivable);
      }
      if(receivable!=null){
        for(ReceptionRecord record:txnWriter.getReceptionRecordsByReceivalbeId(receivable.getId())){
          if(record!=null&&record.getMemberId()!=null){
            record.setMemberId(mergeResult.getMergedMemberId());
            txnWriter.update(record);
          }
        }
      }
    }
    return mergeResult;
  }

  private MergeResult mergeCustomerOrder(MergeResult<CustomerDTO,MergeCustomerSnap> mergeResult,Long[] childIds){
    TxnWriter txnWriter=txnDaoManager.getWriter();
    CustomerDTO parentDTO=mergeResult.getCustomerOrSupplierDTO();
    //合并销售单
    List<SalesOrder> salesOrders=getCustomerOrSupplierOrders(mergeResult.getShopId(), OrderTypes.SALE, childIds);
    for(SalesOrder salesOrder:salesOrders){
      if(salesOrder==null) continue;
      mergeResult.getMergeChangeLogs().add(MergeLogUtil.log_salesOrder_customerId(mergeResult.getShopId(), mergeResult.getUserId(),
          salesOrder.getId(), salesOrder.getCustomerId(), parentDTO.getId()));
      salesOrder.setCustomerId(parentDTO.getId());
      txnWriter.save(salesOrder);
    }
    //合并施工单
    List<RepairOrder> repairOrders=getCustomerOrSupplierOrders(mergeResult.getShopId(), OrderTypes.REPAIR, childIds);
    for(RepairOrder repairOrder:repairOrders){
      if(repairOrder==null) continue;
      mergeResult.getMergeChangeLogs().add(MergeLogUtil.log_repairOrder_customerId(mergeResult.getShopId(), mergeResult.getUserId(),
          repairOrder.getId(), repairOrder.getCustomerId(), parentDTO.getId()));
      repairOrder.setCustomerId(parentDTO.getId());
      if(OrderStatus.REPAIR_DISPATCH.equals(repairOrder.getStatusEnum()) || OrderStatus.REPAIR_DONE.equals(repairOrder.getStatusEnum())) {
        CustomerDTO customerDTO = ServiceManager.getService(IUserService.class).getCustomerById(parentDTO.getId());
        if(customerDTO != null) {
          repairOrder.setCustomer(customerDTO.getName());
        }
      }
      txnWriter.save(repairOrder);
      for(RepairRemindEvent remindEvent:txnWriter.getRepairRemindEventByRepairOrderId(mergeResult.getShopId(),repairOrder.getId())){
        remindEvent.setCustomer(parentDTO.getName());
        remindEvent.setMobile(parentDTO.getMobile());
        txnWriter.update(remindEvent);
        List<RemindEvent> events = txnWriter.getRemindEventByOldRemindEventId(RemindEventType.REPAIR,mergeResult.getShopId(), remindEvent.getId());
        if(CollectionUtil.isNotEmpty(events)){
          events.get(0).setCustomerName(parentDTO.getName());
          events.get(0).setMobile(parentDTO.getMobile());
          txnWriter.update(events.get(0));
        }
      }
    }
    //合并洗车美容
    List<WashBeautyOrder> washBeautyOrders=getCustomerOrSupplierOrders(mergeResult.getShopId(), OrderTypes.WASH_BEAUTY, childIds);
    for(WashBeautyOrder washBeautyOrder:washBeautyOrders){
      if(washBeautyOrder==null) continue;
      mergeResult.getMergeChangeLogs().add(MergeLogUtil.log_washBeautyOrder_customerId(mergeResult.getShopId(), mergeResult.getUserId(),
          washBeautyOrder.getId(), washBeautyOrder.getCustomerId(), parentDTO.getId()));
      washBeautyOrder.setCustomerId(parentDTO.getId());
      txnWriter.save(washBeautyOrder);
    }
    //合并销售退货单
    List<SalesReturn> salesReturns=getCustomerOrSupplierOrders(mergeResult.getShopId(), OrderTypes.SALE_RETURN, childIds);
    for(SalesReturn salesReturn:salesReturns){
      if(salesReturn==null) continue;
      mergeResult.getMergeChangeLogs().add(MergeLogUtil.log_washBeautyOrder_customerId(mergeResult.getShopId(), mergeResult.getUserId(),
          salesReturn.getId(), salesReturn.getCustomerId(), parentDTO.getId()));
      salesReturn.setCustomerId(parentDTO.getId());
      txnWriter.save(salesReturn);
    }
    //会员卡购卡
    List<MemberCardOrder> memberCardOrders=getCustomerOrSupplierOrders(mergeResult.getShopId(), OrderTypes.MEMBER_BUY_CARD, childIds);
    for(MemberCardOrder memberCardOrder:memberCardOrders){
      if(memberCardOrder==null) continue;
      mergeResult.getMergeChangeLogs().add(MergeLogUtil.log_washBeautyOrder_customerId(mergeResult.getShopId(), mergeResult.getUserId(),
          memberCardOrder.getId(), memberCardOrder.getCustomerId(), parentDTO.getId()));
      memberCardOrder.setCustomerId(parentDTO.getId());
      txnWriter.save(memberCardOrder);
    }
    //合并会员卡退卡
    List<MemberCardReturn> memberCardReturns=getCustomerOrSupplierOrders(mergeResult.getShopId(), OrderTypes.WASH_BEAUTY, childIds);
    for(MemberCardReturn memberCardReturn:memberCardReturns){
      if(memberCardReturn==null) continue;
      mergeResult.getMergeChangeLogs().add(MergeLogUtil.log_washBeautyOrder_customerId(mergeResult.getShopId(), mergeResult.getUserId(),
          memberCardReturn.getId(), memberCardReturn.getCustomerId(), parentDTO.getId()));
      memberCardReturn.setCustomerId(parentDTO.getId());
      txnWriter.save(memberCardReturn);
    }
    //合并借调单
    List<BorrowOrder> borrowOrders=getCustomerOrSupplierOrders(mergeResult.getShopId(), OrderTypes.BORROW_ORDER, childIds);
    for(BorrowOrder borrowOrder:borrowOrders){
      if(borrowOrder==null) continue;
      mergeResult.getMergeChangeLogs().add(MergeLogUtil.log_borrowOrder(mergeResult.getShopId(), mergeResult.getUserId(),
          borrowOrder.getId(), borrowOrder.getBorrowerId(), parentDTO.getId(), UserConstant.CSType.CUSTOMER));
      borrowOrder.setBorrowerId(parentDTO.getId());
      txnWriter.save(borrowOrder);
    }
    //合并预约单
    List<AppointOrder> appointOrders = getCustomerOrSupplierOrders(mergeResult.getShopId(), OrderTypes.APPOINT_ORDER, childIds);
    for(AppointOrder appointOrder : appointOrders) {
      if(appointOrder == null) continue;
      mergeResult.getMergeChangeLogs().add(MergeLogUtil.log_appointOrder_customerId(mergeResult.getShopId(), mergeResult.getUserId(),
          appointOrder.getId(), appointOrder.getCustomerId(), parentDTO.getId()));
      appointOrder.setCustomerId(parentDTO.getId());
      txnWriter.save(appointOrder);
    }
    txnWriter.flush();
    return mergeResult;
  }

  private MergeResult mergeSupplierOrder(MergeResult<SupplierDTO,MergeSupplierSnap> mergeResult,Long[] childIds){
    TxnWriter txnWriter=txnDaoManager.getWriter();
    SupplierDTO parentDTO=mergeResult.getCustomerOrSupplierDTO();
    //合并采购单
    List<PurchaseOrder> purchaseOrders=getCustomerOrSupplierOrders(mergeResult.getShopId(), OrderTypes.PURCHASE, childIds);
    for(PurchaseOrder purchaseOrder:purchaseOrders){
      if(purchaseOrder==null) continue;
      mergeResult.getMergeChangeLogs().add(MergeLogUtil.log_purchaseOrder_supplierId(mergeResult.getShopId(), mergeResult.getUserId(),
          purchaseOrder.getId(), purchaseOrder.getSupplierId(), parentDTO.getId()));
      purchaseOrder.setSupplierId(parentDTO.getId());
      txnWriter.save(purchaseOrder);
    }
    //合并入库单
    List<PurchaseInventory> purchaseInventories=getCustomerOrSupplierOrders(mergeResult.getShopId(), OrderTypes.INVENTORY, childIds);
    for(PurchaseInventory purchaseInventory:purchaseInventories){
      if(purchaseInventory==null) continue;
      mergeResult.getMergeChangeLogs().add(MergeLogUtil.log_purchaseInventory_supplierId(mergeResult.getShopId(), mergeResult.getUserId(),
          purchaseInventory.getId(), purchaseInventory.getSupplierId(), parentDTO.getId()));
      purchaseInventory.setSupplierId(parentDTO.getId());
      txnWriter.save(purchaseInventory);
    }
    //合并采购退货单
    List<PurchaseReturn> purchaseReturns=getCustomerOrSupplierOrders(mergeResult.getShopId(), OrderTypes.RETURN, childIds);
    for(PurchaseReturn purchaseReturn:purchaseReturns){
      if(purchaseReturn==null) continue;
      mergeResult.getMergeChangeLogs().add(MergeLogUtil.log_purchaseReturn_supplierId(mergeResult.getShopId(), mergeResult.getUserId(),
          purchaseReturn.getId(), purchaseReturn.getSupplierId(), parentDTO.getId()));
      purchaseReturn.setSupplierId(parentDTO.getId());
      txnWriter.save(purchaseReturn);
    }
    //合并借调单
    List<BorrowOrder> borrowOrders=getCustomerOrSupplierOrders(mergeResult.getShopId(), OrderTypes.BORROW_ORDER, childIds);
    for(BorrowOrder borrowOrder:borrowOrders){
      if(borrowOrder==null) continue;
      mergeResult.getMergeChangeLogs().add(MergeLogUtil.log_borrowOrder(mergeResult.getShopId(), mergeResult.getUserId(),
          borrowOrder.getId(), borrowOrder.getBorrowerId(), parentDTO.getId(), UserConstant.CSType.SUPPLIER));
      borrowOrder.setBorrowerId(parentDTO.getId());
      txnWriter.save(borrowOrder);
    }
    txnWriter.flush();
    return mergeResult;
  }

  public  List getCustomerOrSupplierOrders(Long shopId,OrderTypes orderType,Long[] customerOrSupplierIds){
    TxnWriter txnWriter=txnDaoManager.getWriter();
    return txnWriter.getCustomerOrSupplierOrders(shopId, orderType, customerOrSupplierIds);
  }

  public  List<Object> getMergeRecords(MergeRecordDTO mergeRecordIndex) throws PageException {
    IConfigService configService=ServiceManager.getService(IConfigService.class);
    IUserService userService=ServiceManager.getService(IUserService.class);
    List<MergeRecordDTO> mergeRecordDTOs=new ArrayList<MergeRecordDTO>();
    Pager pager = new Pager(configService.getMergeRecordCount(mergeRecordIndex), NumberUtil.intValue(String.valueOf(mergeRecordIndex.getStartPageNo()), 1));
    mergeRecordIndex.setPager(pager);
    List<MergeRecord> mergeRecords=configService.getMergeRecords(mergeRecordIndex);
    CustomerDTO parentCustomer=null;
    SupplierDTO parentSupplier=null;
    MergeRecordDTO mergeRecordDTO=null;
    if(CollectionUtil.isNotEmpty(mergeRecords)){
      for(MergeRecord mergeRecord:mergeRecords){
        mergeRecordDTO=mergeRecord.toDTO();
        if(MergeType.MERGE_CUSTOMER.equals(mergeRecordIndex.getMergeType())){
          parentCustomer=userService.getCustomerById(mergeRecord.getParentId());
          if(parentCustomer==null||(parentCustomer!=null&& CustomerStatus.DISABLED.equals(parentCustomer.getStatus()))){
            mergeRecordDTO.setParentStatus(ObjectStatus.DISABLED);
          }
        }else{
          parentSupplier=userService.getSupplierById(mergeRecord.getParentId());
          if(parentSupplier==null||(parentSupplier!=null&& CustomerStatus.DISABLED.equals(parentSupplier.getStatus()))){
            mergeRecordDTO.setParentStatus(ObjectStatus.DISABLED);
          }
        }
        mergeRecordDTOs.add(mergeRecordDTO);
      }
    }
    List<Object> result=new ArrayList<Object>();
    AllListResult data=new AllListResult();
    data.setResults(mergeRecordDTOs);
    data.setTotalRows(pager.getTotalRows());
    result.add(data);
    result.add(pager);
    return result;
  }

  public SearchMergeResult<MergeRecord> getMergeSnap(SearchMergeResult<MergeRecord> result,Long parentId,Long childId){
    IConfigService configService=ServiceManager.getService(IConfigService.class);
    IUserService userService=ServiceManager.getService(IUserService.class);
    try {
      List<MergeRecord> mergeRecords=new ArrayList<MergeRecord>();
      MergeRecord mergeRecord=configService.getMergeRecordDetail(result.getShopId(),parentId,childId);
      if(mergeRecord==null){
        return result;
      }
      boolean mergeRelatedFlag=false;
      if(MergeType.MERGE_CUSTOMER.equals(mergeRecord.getMergeType())){
        CustomerDTO parent=userService.getCustomerById(mergeRecord.getParentId());
        CustomerDTO child=userService.getCustomerById(mergeRecord.getParentId());
        result.setParent(parent);
        result.setChild(child);
        if(parent.getCustomerShopId()!=null||child.getCustomerShopId()!=null){
          mergeRelatedFlag=true;
        }
      }else{
        SupplierDTO parent=userService.getSupplierById(mergeRecord.getParentId());
        SupplierDTO child=userService.getSupplierById(mergeRecord.getParentId());
        result.setParent(parent);
        result.setChild(child);
        if(parent.getSupplierShopId()!=null||child.getSupplierShopId()!=null){
          mergeRelatedFlag=true;
        }
      }
      result.setMergeRelatedFlag(mergeRelatedFlag);
      mergeRecords.add(mergeRecord);
      result.setResults(mergeRecords);
      return result;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

}
