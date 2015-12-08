package com.bcgogo.search.service.user;

import com.bcgogo.search.dto.CustomerSupplierSearchConditionDTO;
import com.bcgogo.search.dto.CustomerSupplierSearchResultListDTO;
import com.bcgogo.search.dto.CustomerSupplierSolrIndexDTO;
import com.bcgogo.search.dto.SearchSuggestionDTO;
import org.apache.solr.common.SolrInputDocument;

import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-8-28
 * Time: 下午11:37
 * supplier 与 customer solr搜索service
 */
public interface ISearchCustomerSupplierService {
  /**
   * customer and supplier 查询（不知道Field的情况下三字段全部查询）
   *
   * @param searchConditionDTO CustomerSupplierSearchConditionDTO
   * @return CustomerSupplierSearchResultListDTO
   * @throws Exception 异常向外抛
   */
  public CustomerSupplierSearchResultListDTO queryCustomerSupplierWithUnknownField(CustomerSupplierSearchConditionDTO searchConditionDTO) throws Exception;

  /**
   * 通过条件获得 满足条件的mobiles
   *
   * @param searchConditionDTO CustomerSupplierSearchConditionDTO
   * @return CustomerSupplierSearchResultListDTO
   * @throws Exception 异常向外抛
   */
  CustomerSupplierSearchResultListDTO queryCustomerMobiles(CustomerSupplierSearchConditionDTO searchConditionDTO) throws Exception;

  /**
   * customer 查询（不知道Field的情况下三字段全部查询）
   *
   * @param searchConditionDTO CustomerSupplierSearchConditionDTO
   * @return CustomerSupplierSearchResultListDTO
   * @throws Exception 异常向外抛
   */
  CustomerSupplierSearchResultListDTO queryCustomerWithUnknownField(CustomerSupplierSearchConditionDTO searchConditionDTO) throws Exception;

  /**
   * supplier 查询（不知道Field的情况下三字段全部查询）
   *
   * @param searchConditionDTO CustomerSupplierSearchConditionDTO
   * @return CustomerSupplierSearchResultListDTO
   * @throws Exception 异常向外抛
   */
  CustomerSupplierSearchResultListDTO querySupplierWithUnknownField(CustomerSupplierSearchConditionDTO searchConditionDTO) throws Exception;

  /**
   * customer and supplier 查询 下拉建议
   *
   * @param searchConditionDTO CustomerSupplierSearchConditionDTO
   * @return List<SearchSuggestionDTO>
   * @throws Exception 异常向外抛
   */
  public List<SearchSuggestionDTO> queryCustomerSupplierSuggestion(CustomerSupplierSearchConditionDTO searchConditionDTO) throws Exception;


  /**
   * 联系人（包括车辆和客户和供应商） 查询 下拉建议
   *
   * @param searchConditionDTO CustomerSupplierSearchConditionDTO
   * @return List<SearchSuggestionDTO>
   * @throws Exception 异常向外抛
   */
  public List<SearchSuggestionDTO> queryContactSuggestion(CustomerSupplierSearchConditionDTO searchConditionDTO) throws Exception;

  /**
   * 通过客户名找到对应的客户id集
   * @param customerName
   * @return
   * @throws Exception
   */
  public List<Long> queryCustomerInfo(String customerName) throws Exception;

  CustomerSupplierSearchResultListDTO queryContact(CustomerSupplierSearchConditionDTO searchConditionDTO) throws Exception;
}
