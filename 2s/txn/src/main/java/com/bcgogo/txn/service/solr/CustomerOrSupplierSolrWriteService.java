package com.bcgogo.txn.service.solr;

import com.bcgogo.config.cache.AreaCacheManager;
import com.bcgogo.config.dto.AreaDTO;
import com.bcgogo.enums.CustomerStatus;
import com.bcgogo.enums.RelationTypes;
import com.bcgogo.enums.SolrIdPrefix;
import com.bcgogo.search.client.SolrClientHelper;
import com.bcgogo.search.dto.CustomerSupplierSolrIndexDTO;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.CustomerDepositDTO;
import com.bcgogo.txn.dto.DepositDTO;
import com.bcgogo.txn.dto.StatementAccount.OrderDebtType;
import com.bcgogo.txn.model.TxnDaoManager;
import com.bcgogo.txn.model.TxnWriter;
import com.bcgogo.txn.service.ICustomerDepositService;
import com.bcgogo.txn.service.ISupplierPayableService;
import com.bcgogo.user.dto.*;
import com.bcgogo.user.model.UserDaoManager;
import com.bcgogo.user.model.UserWriter;
import com.bcgogo.user.service.*;
import com.bcgogo.user.service.app.IAppUserService;
import com.bcgogo.utils.ArrayUtil;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.NumberUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 12-9-12
 * Time: 下午2:32
 */
@Component
public class CustomerOrSupplierSolrWriteService implements ICustomerOrSupplierSolrWriteService {
  private static final Logger LOG = LoggerFactory.getLogger(ICustomerOrSupplierSolrWriteService.class);

  @Override
  public void reindexSupplierIndexList(Long shopId, int pageSize) throws Exception {
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    ISupplierPayableService supplierPayableService = ServiceManager.getService(ISupplierPayableService.class);
    IContactService contactService = ServiceManager.getService(IContactService.class);

    int start = 0;
    List<CustomerSupplierSolrIndexDTO> solrIndexDTOList = null;
    while (true) {
      List<SupplierDTO> supplierDTOList = this.getSupplierForReindex(shopId, start, pageSize);  //得到将要reindex 的supplierDTOList
      if (CollectionUtils.isEmpty(supplierDTOList)) break;
      List<Long> ids = new ArrayList<Long>();
      List<Long> customerIds = new ArrayList<Long>();
      for (SupplierDTO supplierDTO : supplierDTOList) {
        if (supplierDTO == null) {
          continue;
        }
        ids.add(supplierDTO.getId());
        if(supplierDTO.getCustomerId()!=null){
          customerIds.add(supplierDTO.getCustomerId());
        }
      }
      Map<Long,List<ContactDTO>> contactDTOMap = contactService.getContactsByCustomerOrSupplierIds(ids, "supplier");

      Map<Long,List<Double>> doubleListMap = supplierPayableService.getSumPayableMapBySupplierIdList(ids,shopId, OrderDebtType.SUPPLIER_DEBT_PAYABLE);
      Map<Long,List<Double>> returnListMap = supplierPayableService.getSumPayableMapBySupplierIdList(ids,shopId, OrderDebtType.SUPPLIER_DEBT_RECEIVABLE);

      Map<Long,CustomerRecordDTO> customerRecordDTOMap = customerService.getCustomerRecordMap(shopId,customerIds.toArray(new Long[customerIds.size()]));
      Map<Long, DepositDTO> depositDTOMap = getDepositForReindex(shopId, ids);
      solrIndexDTOList = new ArrayList<CustomerSupplierSolrIndexDTO>();

      List<Double> doubleList = null;
      List<Double> returnList = null;
      for (SupplierDTO supplierDTO : supplierDTOList) {
        if (supplierDTO == null) {
          continue;
        }
        doubleList = doubleListMap.get(supplierDTO.getId());
        if(doubleList!=null){
          supplierDTO.setTotalDebt(NumberUtil.doubleVal(doubleList.get(0)));//
          supplierDTO.setTotalTradeAmount(NumberUtil.doubleVal(doubleList.get(0))+ NumberUtil.doubleVal(doubleList.get(1)));   //累计消费金额为实付+欠款
        }else{
          supplierDTO.setTotalDebt(0d);//
          supplierDTO.setTotalTradeAmount(0d);   //累计消费金额为实付+欠款
        }
        returnList = returnListMap.get(supplierDTO.getId());
        if(returnList!=null){
          supplierDTO.setTotalReturnDebt(Math.abs(NumberUtil.doubleVal(returnList.get(0))));
          supplierDTO.setTotalReturnAmount(Math.abs(NumberUtil.doubleVal(returnList.get(0)) + NumberUtil.doubleVal(returnList.get(1))));
        }else{
          supplierDTO.setTotalReturnDebt(0d);
          supplierDTO.setTotalReturnAmount(0d);
        }

        //既是客户 又是 供应商
        if(supplierDTO.getCustomerId()!=null){
          CustomerRecordDTO customerRecordDTO =  customerRecordDTOMap.get(supplierDTO.getCustomerId());
          if (customerRecordDTO==null){
            LOG.error("customerId["+supplierDTO.getCustomerId()+"] customerRecordDTO is null");
          }else{
            supplierDTO.setTotalDebt(NumberUtil.doubleVal(supplierDTO.getTotalDebt())+NumberUtil.doubleVal(customerRecordDTO.getTotalPayable()));
            supplierDTO.setTotalReturnDebt(NumberUtil.doubleVal(supplierDTO.getTotalReturnDebt())+NumberUtil.doubleVal(customerRecordDTO.getTotalReceivable()));
          }
        }

        DepositDTO depositDTO = depositDTOMap.get(supplierDTO.getId());
        supplierDTO.setDeposit(depositDTO == null? 0d : NumberUtil.doubleVal(depositDTO.getActuallyPaid()));


        supplierDTO.setRelationType(supplierDTO.getRelationType() == null? RelationTypes.UNRELATED : supplierDTO.getRelationType());
        StringBuilder areaInfo = new StringBuilder();
        if(supplierDTO.getProvince()!=null){
          AreaDTO areaDTO = AreaCacheManager.getAreaDTOByNo(supplierDTO.getProvince());
          if(areaDTO!=null){
            areaInfo.append(areaDTO.getName());
          }
        }
        if(supplierDTO.getCity()!=null){
          AreaDTO areaDTO = AreaCacheManager.getAreaDTOByNo(supplierDTO.getCity());
          if(areaDTO!=null){
            areaInfo.append(areaDTO.getName());
          }
        }
        if(supplierDTO.getRegion()!=null){
          AreaDTO areaDTO = AreaCacheManager.getAreaDTOByNo(supplierDTO.getRegion());
          if(areaDTO!=null){
            areaInfo.append(areaDTO.getName());
          }
        }
        supplierDTO.setAreaInfo(areaInfo.toString());

        supplierDTO.setContactDTOList(contactDTOMap.get(supplierDTO.getId()));
        solrIndexDTOList.add(new CustomerSupplierSolrIndexDTO(supplierDTO,SolrClientHelper.BcgogoSolrDocumentType.CUSTOMER_SUPPLIER.getValue()));
      }
      this.reindexCustomerSupplierIndexList(solrIndexDTOList);
      start += pageSize;
    }
  }

  private List<SupplierDTO> getSupplierForReindex(Long shopId, int start, int pageSize) throws Exception {
    if (pageSize < 1) throw new Exception("pageSize should greater than 0");
    UserWriter writer = ServiceManager.getService(UserDaoManager.class).getWriter();
    return writer.getSupplierForReindex(shopId, start, pageSize);
  }

  private Map<Long, DepositDTO> getDepositForReindex(Long shopId, List<Long> ids) throws Exception{
    TxnWriter writer = txnDaoManager.getWriter();
    List<DepositDTO> depositDTOList = writer.getDepositForReindex(shopId, ids);
    Map<Long, DepositDTO> map = new HashMap<Long, DepositDTO>();
    if(CollectionUtils.isEmpty(depositDTOList)) return map;
    for(DepositDTO depositDTO : depositDTOList){
      map.put(depositDTO.getSupplierId(), depositDTO);
    }
    return map;
  }

  private Map<Long, CustomerDepositDTO> getCustomerDepositForReindex(Long shopId, List<Long> ids) throws Exception{
    TxnWriter writer = txnDaoManager.getWriter();
    List<CustomerDepositDTO> depositDTOList = writer.getCustomerDepositForReindex(shopId, ids);
    Map<Long, CustomerDepositDTO> map = new HashMap<Long, CustomerDepositDTO>();
    if (CollectionUtils.isEmpty(depositDTOList)) return map;
    for (CustomerDepositDTO customerDepositDTO : depositDTOList) {
      map.put(customerDepositDTO.getCustomerId(), customerDepositDTO);
    }
    return map;
  }

  
  public void reindexSupplierBySupplierId(Long supplierId) {
    try {
      if (supplierId == null) throw new Exception("supplierId is null");
      SupplierService supplierService = ServiceManager.getService(SupplierService.class);
      SupplierDTO supplierDTO = supplierService.getSupplierNoContactByIdNoShopId(supplierId);
      if (supplierDTO == null) {
        LOG.error("supplierDTO get by id[{supplierId:{}}] is null", supplierId);
        return;
      }else {
        if(CustomerStatus.DISABLED.equals(supplierDTO.getStatus())){
          deleteCustomerOrSupplierSolrIndexById(supplierDTO.getId());
          Set<Long> idSet = new HashSet<Long>();
          idSet.add(supplierDTO.getId());
          deleteContactByCustomerOrSupplierIds(idSet);
          return;
        }
      }
      supplierDTO.setContactDTOList(ServiceManager.getService(IContactService.class).getContactByCusOrSupOrShopIdOrName(null,supplierDTO.getId(),supplierDTO.getShopId(),null,null));
      reindexSupplierSelfBySupplierDTO(supplierDTO);
      //既是客户 又是 供应商
      if(supplierDTO.getCustomerId()!=null){
        ICustomerService customerService =ServiceManager.getService(ICustomerService.class);
        CustomerDTO customerDTO = customerService.getCustomerById(supplierDTO.getCustomerId());
        if (customerDTO == null){
          LOG.error("customerDTO get by id[{customerId:{}}] is null", supplierDTO.getCustomerId());
          return;
        }
        customerDTO.setContactListUsingArray();
        this.reindexCustomerSelfByCustomerDTO(customerDTO);
      }

    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      LOG.error("supplier reindex failed.");
    }
  }

  private void reindexSupplierSelfBySupplierDTO(SupplierDTO supplierDTO) {
    try {
      if (supplierDTO == null) throw new Exception("supplierDTO is null");

      ISupplierPayableService supplierPayableService = ServiceManager.getService(ISupplierPayableService.class);
      List<Double> doubleList = supplierPayableService.getSumPayableBySupplierId(supplierDTO.getId(),supplierDTO.getShopId(), OrderDebtType.SUPPLIER_DEBT_PAYABLE);
      supplierDTO.setTotalDebt(NumberUtil.doubleVal(doubleList.get(0)));//
      supplierDTO.setTotalTradeAmount(NumberUtil.doubleVal(doubleList.get(0))+ NumberUtil.doubleVal(doubleList.get(1)));   //累计消费金额为实付+欠款
      List<Double> returnList = supplierPayableService.getSumPayableBySupplierId(supplierDTO.getId(),supplierDTO.getShopId(), OrderDebtType.SUPPLIER_DEBT_RECEIVABLE);
      supplierDTO.setTotalReturnDebt(Math.abs(NumberUtil.doubleVal(returnList.get(0))));
      supplierDTO.setTotalReturnAmount(Math.abs(NumberUtil.doubleVal(returnList.get(0)) + NumberUtil.doubleVal(returnList.get(1))));

      //既是客户 又是 供应商
      if(supplierDTO.getCustomerId()!=null){
          //应付款总额
          Double payable = supplierPayableService.getSumReceivableByCustomerId(supplierDTO.getCustomerId(), supplierDTO.getShopId(), OrderDebtType.CUSTOMER_DEBT_PAYABLE);
          Double totalPayable = 0.0;
          if(doubleList != null) {
              totalPayable = doubleList.get(0);
          }
          supplierDTO.setTotalDebt(Math.abs(NumberUtil.doubleVal(payable)) + Math.abs(NumberUtil.doubleVal(totalPayable)));
          //应收款总额
          Double receivable = supplierPayableService.getSumReceivableByCustomerId(supplierDTO.getCustomerId(), supplierDTO.getShopId(), OrderDebtType.CUSTOMER_DEBT_RECEIVABLE);
          Double totalReceivable = 0.0;
          if(returnList != null) {
              totalReceivable = returnList.get(0);
          }
          supplierDTO.setTotalReturnDebt(Math.abs(NumberUtil.doubleVal(receivable)) + Math.abs(NumberUtil.doubleVal(totalReceivable)));
      }

      DepositDTO depositDTO = ServiceManager.getService(ISupplierPayableService.class).getDepositBySupplierId(supplierDTO.getShopId(), supplierDTO.getId());
      supplierDTO.setDeposit(depositDTO == null? 0d : NumberUtil.doubleVal(depositDTO.getActuallyPaid()));
      supplierDTO.setRelationType(supplierDTO.getRelationType() == null? RelationTypes.UNRELATED : supplierDTO.getRelationType());
      StringBuilder areaInfo = new StringBuilder();
      if(supplierDTO.getProvince()!=null){
        AreaDTO areaDTO = AreaCacheManager.getAreaDTOByNo(supplierDTO.getProvince());
        if(areaDTO!=null){
          areaInfo.append(areaDTO.getName());
        }
      }
      if(supplierDTO.getCity()!=null){
        AreaDTO areaDTO = AreaCacheManager.getAreaDTOByNo(supplierDTO.getCity());
        if(areaDTO!=null){
          areaInfo.append(areaDTO.getName());
        }
      }
      if(supplierDTO.getRegion()!=null){
        AreaDTO areaDTO = AreaCacheManager.getAreaDTOByNo(supplierDTO.getRegion());
        if(areaDTO!=null){
          areaInfo.append(areaDTO.getName());
        }
      }
      supplierDTO.setAreaInfo(areaInfo.toString());

      List<CustomerSupplierSolrIndexDTO> solrIndexDTOList = new ArrayList<CustomerSupplierSolrIndexDTO>();
      solrIndexDTOList.add(new CustomerSupplierSolrIndexDTO(supplierDTO,SolrClientHelper.BcgogoSolrDocumentType.CUSTOMER_SUPPLIER.getValue()));
      this.reindexCustomerSupplierIndexList(solrIndexDTOList);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      LOG.error("supplier reindex failed.");
    }
  }
  private void reindexCustomerSelfByCustomerDTO(CustomerDTO customerDTO) {
    try {
      ICustomerService customerService =ServiceManager.getService(ICustomerService.class);
      if (customerDTO == null) throw new Exception("customerDTO is null");
      if(null == customerDTO.getStatus()) {
        customerDTO.setStatus(CustomerStatus.ENABLED);
      }
      //solr reindex
      List<CustomerSupplierSolrIndexDTO> solrIndexDTOList = new ArrayList<CustomerSupplierSolrIndexDTO>();
      //会员
      MemberDTO memberDTO = ServiceManager.getService(IMembersService.class).getMemberByCustomerId(customerDTO.getShopId(), customerDTO.getId());
      customerDTO.setMemberDTO(memberDTO);
      //CustomerRecordDTO
      CustomerRecordDTO customerRecordDTO = customerService.getCustomerRecordDTOByCustomerId(customerDTO.getShopId(),customerDTO.getId());

      if (customerRecordDTO==null){
        LOG.error("customerId["+customerDTO.getId()+"] customerRecordDTO is null");
      }else{
        customerDTO.setCustomerRecordDTO(customerRecordDTO);
      }
      List<VehicleDTO> vehicleDTOList=ServiceManager.getService(IVehicleService.class).getVehicleListByCustomerId(customerDTO.getId());
      customerDTO.setVehicleDTOList(vehicleDTOList);
      customerDTO.setRelationType(customerDTO.getRelationType() == null?RelationTypes.UNRELATED:customerDTO.getRelationType());
      StringBuilder areaInfo = new StringBuilder();
      if(customerDTO.getProvince()!=null){
        AreaDTO areaDTO = AreaCacheManager.getAreaDTOByNo(customerDTO.getProvince());
        if(areaDTO!=null){
          areaInfo.append(areaDTO.getName());
        }
      }
      if(customerDTO.getCity()!=null){
        AreaDTO areaDTO = AreaCacheManager.getAreaDTOByNo(customerDTO.getCity());
        if(areaDTO!=null){
          areaInfo.append(areaDTO.getName());
        }
      }
      if(customerDTO.getRegion()!=null){
        AreaDTO areaDTO = AreaCacheManager.getAreaDTOByNo(customerDTO.getRegion());
        if(areaDTO!=null){
          areaInfo.append(areaDTO.getName());
        }
      }
      customerDTO.setAreaInfo(areaInfo.toString());
      CustomerDepositDTO customerDepositDTO = ServiceManager.getService(ICustomerDepositService.class).queryCustomerDepositByShopIdAndCustomerId(customerDTO.getShopId(), customerDTO.getId());
      customerDTO.setDeposit(customerDepositDTO==null?null:customerDepositDTO.getActuallyPaid());
      //既是客户又是供应商
      if(customerDTO.getSupplierId()!=null){
          Double supplierPayable = 0.0;
          Double supplierReceivable = 0.0;
        //应付款总额
        Double totalPayable = ServiceManager.getService(ISupplierPayableService.class).getSumReceivableByCustomerId(customerDTO.getId(), customerDTO.getShopId(), OrderDebtType.CUSTOMER_DEBT_PAYABLE);
        ISupplierPayableService supplierPayableService = ServiceManager.getService(ISupplierPayableService.class);
        List<Double> returnList = supplierPayableService.getSumPayableBySupplierId(customerDTO.getSupplierId(),customerDTO.getShopId(), OrderDebtType.SUPPLIER_DEBT_PAYABLE);
        if(returnList != null) {
           supplierPayable = returnList.get(0);
        }
        customerDTO.setTotalReturnDebt(Math.abs(NumberUtil.doubleVal(totalPayable)) + Math.abs(NumberUtil.doubleVal(supplierPayable)));

        //应收款总额
        Double receivable = ServiceManager.getService(ISupplierPayableService.class).getSumReceivableByCustomerId(customerDTO.getId(), customerDTO.getShopId(), OrderDebtType.CUSTOMER_DEBT_RECEIVABLE);
        List<Double> doubleList = supplierPayableService.getSumPayableBySupplierId(customerDTO.getSupplierId(),customerDTO.getShopId(), OrderDebtType.SUPPLIER_DEBT_RECEIVABLE);
          if(doubleList != null) {
              supplierReceivable = doubleList.get(0);
          }
        customerDTO.setTotalReceivable(Math.abs(NumberUtil.doubleVal(receivable)) + Math.abs(NumberUtil.doubleVal(supplierReceivable)));
      }

      CustomerSupplierSolrIndexDTO customerSupplierSolrIndexDTO = new CustomerSupplierSolrIndexDTO(customerDTO,SolrClientHelper.BcgogoSolrDocumentType.CUSTOMER_SUPPLIER.getValue());
      solrIndexDTOList.add(customerSupplierSolrIndexDTO);
      this.reindexCustomerSupplierIndexList(solrIndexDTOList);

    } catch (Exception e) {
      LOG.error(e.getMessage(),e);
      LOG.error("createCustomer reindex solr fail!");
    }
  }

  @Override
  public void reindexCustomerByCustomerId(Long customerId) {
    try {
      ICustomerService customerService =ServiceManager.getService(ICustomerService.class);
      IContactService contactService =ServiceManager.getService(IContactService.class);
      IUserService userService = ServiceManager.getService(IUserService.class);


      if (customerId == null) throw new Exception("customerId is null");
      CustomerDTO customerDTO = customerService.getCustomerById(customerId);
      if (customerDTO == null) {
        LOG.error("customerDTO get by id[{customerId:{}}] is null", customerId);
        return;
      }else {
        if(CustomerStatus.DISABLED.equals(customerDTO.getStatus())){
          deleteCustomerOrSupplierSolrIndexById(customerDTO.getId());
          Set<Long> idSet = new HashSet<Long>();
          idSet.add(customerDTO.getId());
          deleteContactByCustomerOrSupplierIds(idSet);
          return;
        }
      }
      boolean isObd = userService.isOBDCustomer(customerId);
      customerDTO.setIsObd(isObd);

      List<Long> customerIdList = new ArrayList<Long>();
      customerIdList.add(customerId);
      Map<Long, Boolean> result = userService.isAppUserByCustomerId(customerIdList);
      customerDTO.setAppUser(result.get(customerId) == null ? Boolean.FALSE : result.get(customerId));


      customerDTO.setContactDTOList(contactService.getContactByCusOrSupOrShopIdOrName(customerDTO.getId(),null,customerDTO.getShopId(),null,null));
      reindexCustomerSelfByCustomerDTO(customerDTO);
      //既是客户又是供应商
      if(customerDTO.getSupplierId()!=null){
        SupplierDTO supplierDTO = ServiceManager.getService(SupplierService.class).getSupplierById(customerDTO.getSupplierId(),customerDTO.getShopId());
        if (supplierDTO == null) {
          LOG.error("supplierDTO get by id[{supplierId:{}}] is null", customerDTO.getSupplierId());
          return;
        }
        supplierDTO.setContactListUsingArray();
        this.reindexSupplierSelfBySupplierDTO(supplierDTO);
      }

    } catch (Exception e) {
      LOG.error(e.getMessage(),e);
      LOG.error("createCustomer reindex solr fail!");
    }
  }

  @Override
  public void reindexCustomersByAppUserNos(String... appUserNos) {
    if (ArrayUtil.isEmpty(appUserNos)) return;
    List<Long> customerIds = ServiceManager.getService(IAppUserService.class).getCustomerIdInAppUserCustomer(appUserNos);
    for (Long id : customerIds) {
      reindexCustomerByCustomerId(id);
    }
  }

  /**
   * for customer reindex
   * @param shopId
   * @param pageSize
   * @throws Exception
   */
  @Override
  public void reindexCustomerIndexList(Long shopId, int pageSize) throws Exception {
    int start = 0;
    //获得 一批 customer customerRecord member
    ISupplierPayableService supplierPayableService = ServiceManager.getService(ISupplierPayableService.class);
    ICustomerService customerService =ServiceManager.getService(ICustomerService.class);
    IContactService contactService =ServiceManager.getService(IContactService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    Map<Long, List<VehicleDTO>> customerVehicleDTOMap = null;
    while (true) {
      List<CustomerSupplierSolrIndexDTO> customerSupplierSolrIndexDTOList = new ArrayList<CustomerSupplierSolrIndexDTO>();
      List<Long> ids = customerService.getCustomerIdList(shopId, start, pageSize);
      if (CollectionUtils.isEmpty(ids)) break;
      List<CustomerDTO> customerDTOList = customerService.getCustomerByIds(ids);
      Map<Long,Boolean> isObds = userService.isOBDCustomer(ids);
      Map<Long,Boolean> isApps = userService.isAppUserByCustomerId(ids);
     
      Map<Long, List<ContactDTO>> contactDTOMap= contactService.getContactsByCustomerOrSupplierIds(ids, "customer");

      Map<Long,CustomerRecordDTO> customerRecordDTOMap = customerService.getCustomerRecordMap(shopId, ids.toArray(new Long[ids.size()]));
      Map<Long,MemberDTO> memberDTOMap = customerService.getCustomerMemberMap(shopId, ids.toArray(new Long[ids.size()]));
      customerVehicleDTOMap = customerService.getCustomerLicenseNosForReindex(shopId, ids);
      Map<Long,List<Double>> doubleListMap = supplierPayableService.getSumPayableMapBySupplierIdList(ids,shopId, OrderDebtType.SUPPLIER_DEBT_PAYABLE);
      Map<Long,List<Double>> returnListMap = supplierPayableService.getSumPayableMapBySupplierIdList(ids,shopId, OrderDebtType.SUPPLIER_DEBT_RECEIVABLE);
      List<Double> doubleList = null;
      List<Double> returnList = null;
      Map<Long, CustomerDepositDTO> depositDTOMap = getCustomerDepositForReindex(shopId, ids);

      for(CustomerDTO customerDTO:customerDTOList){
        if(null == customerDTO.getStatus()) {
          customerDTO.setStatus(CustomerStatus.ENABLED);
        }
        Boolean isObd = isObds.get(customerDTO.getId());
        if(isObd == null){
          isObd = false;
        }
        customerDTO.setIsObd(isObd);
        customerDTO.setAppUser(isApps.get(customerDTO.getId()) == null ? Boolean.FALSE : isApps.get(customerDTO.getId()));
        //会员
        customerDTO.setMemberDTO(memberDTOMap.get(customerDTO.getId()));
        //CustomerRecordDTO
        CustomerRecordDTO customerRecordDTO = customerRecordDTOMap.get(customerDTO.getId());
        if (customerRecordDTO==null){
          LOG.error("customerId["+customerDTO.getId()+"] customerRecordDTO is null");
        }else{
          customerDTO.setCustomerRecordDTO(customerRecordDTO);
        }

        customerDTO.setRelationType(customerDTO.getRelationType() == null?RelationTypes.UNRELATED:customerDTO.getRelationType());
        StringBuilder areaInfo = new StringBuilder();
        if(customerDTO.getProvince()!=null){
          AreaDTO areaDTO = AreaCacheManager.getAreaDTOByNo(customerDTO.getProvince());
          if(areaDTO!=null){
            areaInfo.append(areaDTO.getName());
          }
        }
        if(customerDTO.getCity()!=null){
          AreaDTO areaDTO = AreaCacheManager.getAreaDTOByNo(customerDTO.getCity());
          if(areaDTO!=null){
            areaInfo.append(areaDTO.getName());
          }
        }
        if(customerDTO.getRegion()!=null){
          AreaDTO areaDTO = AreaCacheManager.getAreaDTOByNo(customerDTO.getRegion());
          if(areaDTO!=null){
            areaInfo.append(areaDTO.getName());
          }
        }
        customerDTO.setAreaInfo(areaInfo.toString());

        //既是客户又是供应商
        if(customerDTO.getSupplierId()!=null){
          doubleList = doubleListMap.get(customerDTO.getSupplierId());
          if(doubleList!=null){
            customerDTO.setTotalReceivable(customerDTO.getTotalReceivable()+NumberUtil.doubleVal(doubleList.get(0)));//
          }
          returnList = returnListMap.get(customerDTO.getSupplierId());
          if(returnList!=null){
            customerDTO.setTotalReturnDebt(customerDTO.getTotalReturnDebt()+Math.abs(NumberUtil.doubleVal(returnList.get(0))));
          }
        }

        if(!depositDTOMap.isEmpty() && depositDTOMap.get(customerDTO.getId()) != null){
          customerDTO.setDeposit(depositDTOMap.get(customerDTO.getId()).getActuallyPaid());
        }
        customerDTO.setContactDTOList(contactDTOMap.get(customerDTO.getId()));
        customerDTO.setVehicleDTOList(customerVehicleDTOMap.get(customerDTO.getId()));

        CustomerSupplierSolrIndexDTO customerSupplierSolrIndexDTO = new CustomerSupplierSolrIndexDTO(customerDTO,SolrClientHelper.BcgogoSolrDocumentType.CUSTOMER_SUPPLIER.getValue());

        customerSupplierSolrIndexDTOList.add(customerSupplierSolrIndexDTO);
      }

      start+=pageSize;
      //reindex into solr
      this.reindexCustomerSupplierIndexList(customerSupplierSolrIndexDTOList);
    }
  }


  @Override
  public void reindexCustomerSupplierIndexList(Long shopId, int pageSize) throws Exception {
    this.reindexCustomerIndexList(shopId, pageSize);
    reindexSupplierIndexList(shopId, pageSize);
  }

  @Override
  public void optimizeSolrCustomerSupplierCore()throws Exception{
    SolrClientHelper.getCustomerSupplierSolrClient().solrOptimize();
  }

  @Override
  public void deleteCustomerOrSupplierSolrIndexById(Long id)throws Exception{
    if(id != null){
      SolrClientHelper.getCustomerSupplierSolrClient().deleteById(id.toString());
    }
  }

   @Override
   public void deleteContactSolrIndexById(String... specialIds)throws Exception{
     if(ArrayUtil.isEmpty(specialIds)) return;
     SolrClientHelper.getCustomerSupplierSolrClient().deleteByIds(Arrays.asList(specialIds));
   }


  @Override
  public void reindexOtherContactSolrIndex(Long shopId, Long... contactId) throws Exception {
    IContactService contactService = ServiceManager.getService(IContactService.class);
    List<ContactDTO> contactDTOList = contactService.getContactsByIds(shopId,contactId);
    if(CollectionUtils.isNotEmpty(contactDTOList)){
      Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
      for(ContactDTO contactDTO:contactDTOList){
        docs.add(contactDTO.toOtherContactSolrDocument(SolrClientHelper.BcgogoSolrDocumentType.CONTACT.getValue(), SolrIdPrefix.OTHER));
      }
      SolrClientHelper.getCustomerSupplierSolrClient().addDocs(docs);
    }
  }

  @Override
  public void reindexOtherContactIndexList(Long shopId, int pageSize) throws Exception {
    int start = 0;
    IContactService contactService =ServiceManager.getService(IContactService.class);
    while (true) {
      List<Long> ids = contactService.getOtherContactsIds(shopId, start, pageSize);
      if (CollectionUtils.isEmpty(ids)) break;
      this.reindexOtherContactSolrIndex(shopId,ids.toArray(new Long[ids.size()]));
      start+=pageSize;
    }
  }

  private void reindexCustomerSupplierIndexList(List<CustomerSupplierSolrIndexDTO> solrIndexDTOList) throws Exception {
    Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
    Set<Long> customerOrSupplierIdSet = new HashSet<Long>();
    for (CustomerSupplierSolrIndexDTO solrIndexDTO : solrIndexDTOList) {
      docs.add(solrIndexDTO.toSolrDocument());
      SolrIdPrefix solrIdPrefix = null;
      if("customer".equals(solrIndexDTO.getCustomer_or_supplier())){
        solrIdPrefix = SolrIdPrefix.CUSTOMER;
      }else if("supplier".equals(solrIndexDTO.getCustomer_or_supplier())){
        solrIdPrefix = SolrIdPrefix.SUPPLIER;
      }
      if(solrIdPrefix!=null) {
        if (CollectionUtils.isNotEmpty(solrIndexDTO.getContactDTOList())) {
          for (ContactDTO contactDTO : solrIndexDTO.getContactDTOList()) {
            contactDTO.setApp(solrIndexDTO.isAppUser());
            docs.add(solrIndexDTO.toContactSolrDocument(contactDTO, SolrClientHelper.BcgogoSolrDocumentType.CONTACT.getValue(), solrIdPrefix));
          }
        }
      }
      if(CollectionUtils.isNotEmpty(solrIndexDTO.getVehicles())){
         List<Long> vehicleIds=new ArrayList<Long>();
        for(VehicleDTO vehicleDTO:solrIndexDTO.getVehicles()){
          vehicleIds.add(vehicleDTO.getId());
        }
        Map<Long,Boolean> isAppVehicleMap = ServiceManager.getService(IVehicleService.class).isAppVehicle(vehicleIds.toArray(new Long[vehicleIds.size()]));
        for(VehicleDTO vehicleDTO : solrIndexDTO.getVehicles()){
//          if(StringUtils.isBlank(vehicleDTO.getMobile())) continue;
          ContactDTO contactDTO = new ContactDTO();
          contactDTO.setId(vehicleDTO.getId());
          contactDTO.setName(vehicleDTO.getContact());
          contactDTO.setMobile(vehicleDTO.getMobile());
          contactDTO.setApp(isAppVehicleMap.get(vehicleDTO.getId()));
          docs.add(solrIndexDTO.toContactSolrDocument(contactDTO,SolrClientHelper.BcgogoSolrDocumentType.CONTACT.getValue(), SolrIdPrefix.VEHICLE));
        }
      }
      customerOrSupplierIdSet.add(solrIndexDTO.getId());
    }
    this.deleteContactByCustomerOrSupplierIds(customerOrSupplierIdSet);
    SolrClientHelper.getCustomerSupplierSolrClient().addDocs(docs);
  }

  private void deleteContactByCustomerOrSupplierIds(Set<Long> customerOrSupplierIdSet) throws Exception{
    if(CollectionUtils.isNotEmpty(customerOrSupplierIdSet)){
      List<Long> customerOrSupplierIdList = new ArrayList<Long>(customerOrSupplierIdSet);
      int pageSize = 1000;
      StringBuilder idStr = new StringBuilder();
      for (int i = 0, max = customerOrSupplierIdList.size(); i < max; i++) {
        idStr.append(customerOrSupplierIdList.get(i)).append(" OR ");
        if(i%pageSize==0 || i==max-1){
          SolrClientHelper.getCustomerSupplierSolrClient().deleteByQuery("doc_type:"+SolrClientHelper.BcgogoSolrDocumentType.CONTACT.getValue()+" AND customer_or_supplier_id:("+idStr.toString().trim().substring(0,idStr.toString().trim().length()-2)+")");
          idStr = new StringBuilder();
        }
      }
    }
  }

  @Autowired
  private TxnDaoManager txnDaoManager;
}
