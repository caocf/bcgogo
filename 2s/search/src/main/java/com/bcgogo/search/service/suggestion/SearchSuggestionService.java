package com.bcgogo.search.service.suggestion;

import com.bcgogo.enums.Product.ProductCategoryStatus;
import com.bcgogo.enums.Product.ProductCategoryType;
import com.bcgogo.enums.ServiceStatus;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.product.ProductCategory.ProductCategoryDTO;
import com.bcgogo.search.client.SolrClientHelper;
import com.bcgogo.search.dto.SearchSuggestionDTO;
import com.bcgogo.search.util.SolrQueryUtils;
import com.bcgogo.txn.dto.CarDTO;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.ShopConstant;
import com.bcgogo.utils.SolrConstant;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.HighlightParams;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: xzhu
 * Date: 12-10-19
 * Time: 上午11:51
 * To change this template use File | Settings | File Templates.
 */
@Component
public class SearchSuggestionService implements ISearchSuggestionService{
  @Override
  public List<SearchSuggestionDTO> getRepairServiceSuggestion(Long shopId, String name) throws Exception {
    if (shopId == null) throw new BcgogoException("shopId nullPointException!");
    List<SearchSuggestionDTO> searchSuggestionDTOList = new ArrayList<SearchSuggestionDTO>();
    SolrQuery query = new SolrQuery();
    StringBuffer qString = new StringBuffer();

    if (StringUtils.isBlank(name)) {
      qString.append("name:*");
    } else {
      name = SolrQueryUtils.escape(name);
      qString.append("(");
      qString.append("name").append(":").append("(").append(name).append("*").append(")^10");
      qString.append(" OR ").append("name").append("_ngram_continuous:").append("(").append(name).append(")^5");
      qString.append(" OR ").append("name").append("(\"").append(name).append("\")^100");
      qString.append(" OR ").append("name").append("_fl:").append("(").append(name + "*").append(")");
      qString.append(" OR ").append("name").append("_py:").append("(").append(name + "*").append(")");
      qString.append(")");
    }
    query.setQuery(qString.toString());

    StringBuffer fQueryString = new StringBuffer();
    fQueryString.append("(shop_id:").append(shopId.toString()).append(" AND doc_type:").append(SolrClientHelper.BcgogoSolrDocumentType.SERVICE_DOC_TYPE.getValue()).append(" AND status:"+ ServiceStatus.ENABLED+")");
    query.setFilterQueries(fQueryString.toString());
    query.setParam("sort", "use_times desc");


    //设置高亮，以下两种方式都行（相当于开启高亮功能）

//    query.setHighlight(true);
    //设置高亮显示的请求，高亮显示的内容由该参数决定，但是返回结果还是由SolrQuery决定
//    query.setParam("hl.q", "name_ngram_continuous:"+name);
//    query.setParam(HighlightParams.MERGE_CONTIGUOUS_FRAGMENTS, false);
    /*
    * 那些字段高亮显示，可以用空格或者逗号分隔（有一个域的时候正常，两个及以上没测试通过）
    * 老版本使用query.addHighlightField("name");query.addHighlightField("description");给多个字段开启高亮功能
    */
//    query.setHighlightSnippets(3);
//    query.setHighlightRequireFieldMatch(true);
//    query.addHighlightField("name_ngram_continuous");
    //高亮显示字段前后添加html代码
//    query.setHighlightSimplePre("<font color=\"red\">");
//    query.setHighlightSimplePost("</font>");


    QueryResponse rsp = SolrClientHelper.getSuggestionClient().query(query);
    //第一个Map的键是文档的ID，第二个Map的键是高亮显示的字段名
//    Map<String, Map<String, List<String>>> highlightMap = rsp.getHighlighting();

    SolrDocumentList docs = rsp.getResults();
    SearchSuggestionDTO resultDTO = null;
    for (SolrDocument doc : docs) {
//      String docId = String.valueOf(doc.getFirstValue("id"));
      resultDTO = new SearchSuggestionDTO();
      resultDTO.addEntry("price",String.valueOf(doc.getFirstValue("price")==null?0d:doc.getFirstValue("price")));
      resultDTO.addEntry("business_category_id", StringUtil.valueOf(doc.getFirstValue("business_category_id")));
//      if(highlightMap.get(docId)!=null && highlightMap.get(docId).get("name_ngram_continuous")!=null){
//        resultDTO.addEntry("name",highlightMap.get(docId).get("name_ngram_continuous").get(0));
//      }else{
//      }
      resultDTO.addEntry("name", (String) doc.getFirstValue("name"));
      resultDTO.addEntry("id", String.valueOf(doc.getFirstValue("id")));
      searchSuggestionDTOList.add(resultDTO);
    }
    return searchSuggestionDTOList;
  }

  @Override
  public List<SearchSuggestionDTO> getProductCategorySuggestion(Long shopId,String searchWord,ProductCategoryType productCategoryType,Long parentId) throws Exception {
    List<SearchSuggestionDTO> searchSuggestionDTOList = new ArrayList<SearchSuggestionDTO>();
    SolrQuery query = new SolrQuery();
    StringBuffer qString = new StringBuffer();

    if (StringUtils.isBlank(searchWord)) {
      qString.append("name:*");
    } else {
      searchWord = SolrQueryUtils.escape(searchWord);
      qString.append("(");
      qString.append("name").append(":").append("(").append(searchWord).append("*").append(")^10");
      qString.append(" OR ").append("name").append("_ngram_continuous:").append("(").append(searchWord).append(")^5");
      qString.append(" OR ").append("name").append("(\"").append(searchWord).append("\")^100");
      qString.append(" OR ").append("name").append("_fl:").append("(").append(searchWord + "*").append(")");
      qString.append(" OR ").append("name").append("_py:").append("(").append(searchWord + "*").append(")");
      qString.append(")");
    }
    query.setQuery(qString.toString());

    StringBuffer fQueryString = new StringBuffer();
    if(shopId!=null){
      fQueryString.append("(shop_id:").append(ShopConstant.BC_ADMIN_SHOP_ID).append(" OR ").append("shop_id:").append(shopId).append(")");
    }else{
      fQueryString.append("shop_id:").append(ShopConstant.BC_ADMIN_SHOP_ID);
    }
    fQueryString.append(" AND doc_type:").append(SolrClientHelper.BcgogoSolrDocumentType.PRODUCT_CATEGORY_DOC_TYPE.getValue()).append(" AND status:" + ProductCategoryStatus.ENABLED);

    if(productCategoryType!=null){
      fQueryString.append(" AND (product_category_type:").append(productCategoryType).append(")");
    }
    if(parentId!=null){
      fQueryString.append(" AND (parent_id:").append(SolrQueryUtils.escape(parentId.toString())).append(")");
    }
    query.setFilterQueries(fQueryString.toString());

    QueryResponse rsp = SolrClientHelper.getSuggestionClient().query(query);
    SolrDocumentList docs = rsp.getResults();
    SearchSuggestionDTO resultDTO = null;
    for (SolrDocument doc : docs) {
      resultDTO = new SearchSuggestionDTO();
      resultDTO.addEntry("parent_id", StringUtil.valueOf(doc.getFirstValue("parent_id")));
      resultDTO.addEntry("name", (String) doc.getFirstValue("name"));
      resultDTO.addEntry("id", String.valueOf(doc.getFirstValue("id")));
      resultDTO.addEntry("product_category_type", String.valueOf(doc.getFirstValue("product_category_type")));
      searchSuggestionDTOList.add(resultDTO);
    }
    return searchSuggestionDTOList;
  }

  @Override
  public List<ProductCategoryDTO> getProductCategoryDetailList(Long shopId,String searchWord,ProductCategoryType... productCategoryTypes) throws Exception {
    List<ProductCategoryDTO> productCategoryDTOList = new ArrayList<ProductCategoryDTO>();
    SolrQuery query = new SolrQuery();
    StringBuffer qString = new StringBuffer();

    if (StringUtils.isBlank(searchWord)) {
      qString.append("name:*");
    } else {
      searchWord = SolrQueryUtils.escape(searchWord);
      qString.append("(");
      qString.append("name").append(":").append("(").append(searchWord).append("*").append(")^10");
      qString.append(" OR ").append("name").append("_ngram_continuous:").append("(").append(searchWord).append(")^5");
      qString.append(" OR ").append("name").append("(\"").append(searchWord).append("\")^100");
      qString.append(")");
    }
    query.setQuery(qString.toString());

    StringBuffer fQueryString = new StringBuffer();
    if(shopId!=null){
      fQueryString.append("(shop_id:").append(ShopConstant.BC_ADMIN_SHOP_ID).append(" OR ").append("shop_id:").append(shopId).append(")");
    }else{
      fQueryString.append("shop_id:").append(ShopConstant.BC_ADMIN_SHOP_ID);
    }
    fQueryString.append(" AND doc_type:").append(SolrClientHelper.BcgogoSolrDocumentType.PRODUCT_CATEGORY_DOC_TYPE.getValue()).append(" AND status:" + ProductCategoryStatus.ENABLED);
    if(!ArrayUtils.isEmpty(productCategoryTypes)){
      fQueryString.append(" AND product_category_type:(");
      for (int i = 0, max = productCategoryTypes.length; i < max; i++) {
        fQueryString.append(productCategoryTypes[i]);
        if (i < (max - 1)) fQueryString.append(" OR ");
      }
      fQueryString.append(")");
    }
    query.setFilterQueries(fQueryString.toString());

    QueryResponse rsp = SolrClientHelper.getSuggestionClient().query(query);
    SolrDocumentList docs = rsp.getResults();
    ProductCategoryDTO productCategoryDTO = null;
    for (SolrDocument doc : docs) {
      productCategoryDTO = new ProductCategoryDTO();
      productCategoryDTO.setParentId(NumberUtil.longValue(doc.getFirstValue("parent_id")));
      productCategoryDTO.setShopId(NumberUtil.longValue(doc.getFirstValue("shop_id")));
      productCategoryDTO.setName((String) doc.getFirstValue("name"));
      productCategoryDTO.setId(NumberUtil.longValue(doc.getFirstValue("id")));
      productCategoryDTO.setCategoryType(ProductCategoryType.valueOf(String.valueOf(doc.getFirstValue("product_category_type"))));
      productCategoryDTOList.add(productCategoryDTO);
    }
    return productCategoryDTOList;
  }
}
