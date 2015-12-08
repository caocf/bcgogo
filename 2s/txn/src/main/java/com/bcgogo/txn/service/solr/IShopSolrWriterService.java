package com.bcgogo.txn.service.solr;

/**
 * User: ZhangJuntao
 * Date: 13-9-10
 * Time: 下午1:01
 */
public interface IShopSolrWriterService {
  void reCreateShopSolrIndexAll() throws Exception;

  void reCreateShopIdSolrIndex(Long...  shopId) throws Exception;

}
