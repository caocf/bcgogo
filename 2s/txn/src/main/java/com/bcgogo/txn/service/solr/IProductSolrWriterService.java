package com.bcgogo.txn.service.solr;

import java.util.Map;

/**
 * User: xzhu
 * Date: 12-8-25
 * Time: 下午6:30
 * 该service 负责查找 txn 库中 各种单据  ,reindex 一些数据
 */
public interface IProductSolrWriterService {
  /**
   * 重做索引根据数据库 在用户入口操作使用
   *
   * @param shopId
   * @param productLocalInfoId
   */
  public void createProductSolrIndex(Long shopId,Long... productLocalInfoId) throws Exception;

  /**
   * 重做索引根据数据库   在admin  重做索引使用
   *
   *
   * @param shopId
   * @param pageSize
   * @throws Exception
   */
  public void reCreateProductSolrIndex(Long shopId, int pageSize) throws Exception;


  void reCreateProductCategorySolrIndex(Long shopId, int pageSize) throws Exception;
  void createProductCategorySolrIndex(Long shopId,Long... productCategoryIds) throws Exception;


  void optimizeSolrProductCore() throws Exception;

  /**
   * propertyMap key  solr index id
   * propertyMap value  fieldName-newValue
   * @param propertyMap
   * @throws Exception
   */
  void productSolrAtomUpdate(Map<Long, Map<String, Object>> propertyMap) throws Exception;
}
