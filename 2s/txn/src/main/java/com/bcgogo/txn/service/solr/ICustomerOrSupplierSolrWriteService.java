package com.bcgogo.txn.service.solr;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 12-9-12
 * Time: 下午2:26
 */
public interface ICustomerOrSupplierSolrWriteService {
  /**
   * 供应商 reindex
   *
   * @param shopId
   * @param pageSize
   */
  void reindexSupplierIndexList(Long shopId, int pageSize) throws Exception;

  /**
   * 客户 reindex
   *
   * @param shopId
   * @param pageSize
   */
  void reindexCustomerIndexList(Long shopId, int pageSize) throws Exception;

  /**
   *  supplier update in solr
   * @param supplierId
   */
  public void reindexSupplierBySupplierId(Long supplierId) ;

  /**
   * customer update solr
   *
   * @param customerId
   */
  public void reindexCustomerByCustomerId(Long customerId);

  //根据appUserNo reindex
  void reindexCustomersByAppUserNos(String... appUserNos);

  /**
   * 客户与供应商 reindex
   *
   * @param shopId
   * @param pageSize
   */
  void reindexCustomerSupplierIndexList(Long shopId, int pageSize) throws Exception;

  void optimizeSolrCustomerSupplierCore()throws Exception;

  void deleteCustomerOrSupplierSolrIndexById(Long id)throws Exception;

  void deleteContactSolrIndexById(String... specialIds)throws Exception;

  void reindexOtherContactSolrIndex(Long shopId, Long... contactId) throws Exception;

  void reindexOtherContactIndexList(Long shopId, int pageSize) throws Exception;
}
