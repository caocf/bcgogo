package com.bcgogo.search;

import com.bcgogo.product.dto.PingyinInfo;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.search.client.SolrClientHelper;
import com.bcgogo.search.dto.ProductSearchResultGroupListDTO;
import com.bcgogo.search.dto.ProductSearchResultListDTO;
import com.bcgogo.search.dto.SearchConditionDTO;
import com.bcgogo.search.dto.SearchSuggestionDTO;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.search.service.product.ISearchProductService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.utils.PinyinUtil;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: caiweili
 * Date: 1/30/12
 * Time: 12:46 AM
 * To change this template use File | Settings | File Templates.
 */
public class ProductSearchTest extends AbstractTest {
  @Before
  public void setUpTest() throws Exception {
    SolrClientHelper.getProductSolrClient().deleteAll();
  }


  @Test
  public void productNameWithSingleChineseChar2() throws Exception {

    ISearchService searchService = ServiceManager.getService(ISearchService.class);

    SolrClientHelper.getProductSolrClient().deleteAll();

    Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();

    SolrInputDocument doc = new SolrInputDocument();
    doc.addField("id", "1", 1);
    doc.addField("product_name", "机油滤清器");
    doc.addField("shop_id", 0);
    doc.addField("product_status", "ENABLED");
    docs.add(doc);

    doc = new SolrInputDocument();
    doc.addField("id", "2", 1);
    doc.addField("product_name", "机油泵");
    doc.addField("shop_id", 0);
    doc.addField("product_status", "ENABLED");

    docs.add(doc);

    doc = new SolrInputDocument();
    doc.addField("id", "3", 1);
    doc.addField("product_name", "昆仑机滤");
    doc.addField("shop_id", 0);
    doc.addField("product_status", "ENABLED");

    docs.add(doc);

    doc = new SolrInputDocument();
    doc.addField("id", "4", 1);
    doc.addField("product_name", "壳牌机油滤");
    doc.addField("shop_id", 0);
    doc.addField("product_status", "ENABLED");

    docs.add(doc);

    doc = new SolrInputDocument();
    doc.addField("id", "5", 1);
    doc.addField("product_name", "长城机滤");
    doc.addField("shop_id", 0);
    doc.addField("product_status", "ENABLED");

    docs.add(doc);

    doc = new SolrInputDocument();
    doc.addField("id", "6", 1);
    doc.addField("product_name", "长城机油滤清器");
    doc.addField("shop_id", 0);
    doc.addField("product_status", "ENABLED");

    docs.add(doc);

    doc = new SolrInputDocument();
    doc.addField("id", "7", 1);
    doc.addField("product_name", "长城机油");
    doc.addField("shop_id", 0);
    doc.addField("product_status", "ENABLED");

    docs.add(doc);

    SolrClientHelper.getProductSolrClient().addDocs(docs);
    List<ProductDTO> res = searchService.queryProduct("机油", 0l, 0, 10);
    res = searchService.queryProduct("滤清器", 0l, 0, 10);
  }


  @Test
  public void productNameWithSingleChineseChar3() throws Exception {
    ISearchProductService searchProductService = ServiceManager.getService(ISearchProductService.class);
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    SolrClientHelper.getProductSolrClient().deleteByQuery("*:*");

    Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();

    SolrInputDocument doc = new SolrInputDocument();
    doc.addField("id", "1", 1);
    doc.addField("product_name", "轮胎");
    PingyinInfo pingyinInfo = PinyinUtil.getPingyinInfo("轮胎");
    doc.addField("product_name_fl", pingyinInfo.firstLetters);
    doc.addField("product_name_py", pingyinInfo.pingyin);
    doc.addField("product_brand", "正新");
    doc.addField("shop_id", 0);
    doc.addField("product_status", "ENABLED");
    docs.add(doc);

    doc = new SolrInputDocument();
    doc.addField("id", "3", 1);
    doc.addField("product_name", "轮胎");
    pingyinInfo = PinyinUtil.getPingyinInfo("轮胎");
    doc.addField("product_name_fl", pingyinInfo.firstLetters);
    doc.addField("product_name_py", pingyinInfo.pingyin);
    doc.addField("product_brand", "米其林");
    doc.addField("shop_id", 0);
    doc.addField("purchase_price", 2000);
    doc.addField("product_vehicle_brand", "宝马");
    doc.addField("product_status", "ENABLED");
    docs.add(doc);

    doc = new SolrInputDocument();
    doc.addField("id", "4", 1);
    doc.addField("product_name", "轮胎");
    pingyinInfo = PinyinUtil.getPingyinInfo("轮胎");
    doc.addField("product_name_fl", pingyinInfo.firstLetters);
    doc.addField("product_name_py", pingyinInfo.pingyin);
    doc.addField("product_brand", "米其林");
    doc.addField("shop_id", 0);
    doc.addField("product_vehicle_brand", "别克");
    doc.addField("purchase_price", 1000);
    doc.addField("product_status", "ENABLED");
    docs.add(doc);

    doc = new SolrInputDocument();
    doc.addField("id", "5", 1);
    doc.addField("product_name", "轮毂");
    pingyinInfo = PinyinUtil.getPingyinInfo("轮毂");
    doc.addField("product_name_fl", pingyinInfo.firstLetters);
    doc.addField("product_name_py", pingyinInfo.pingyin);
    doc.addField("product_brand", "米其林");
    doc.addField("shop_id", 0);
    doc.addField("purchase_price", 2500);
    doc.addField("product_vehicle_brand", "宝马");
    doc.addField("product_status", "ENABLED");
    docs.add(doc);

    doc = new SolrInputDocument();
    doc.addField("id", "2", 1);
    doc.addField("product_name", "元宝梁");
    pingyinInfo = PinyinUtil.getPingyinInfo("元宝梁");
    doc.addField("product_name_fl", pingyinInfo.firstLetters);
    doc.addField("product_name_py", pingyinInfo.pingyin);
    doc.addField("product_brand", "正");
    doc.addField("shop_id", 0);
    doc.addField("product_status", "ENABLED");

    docs.add(doc);

    SolrClientHelper.getProductSolrClient().addDocs(docs);
    SearchConditionDTO searchConditionDTO = new SearchConditionDTO();
    searchConditionDTO.setSearchWord("轮");
    searchConditionDTO.setShopId(0l);

    searchConditionDTO.setStart(0);
    searchConditionDTO.setRows(10);
    List<ProductDTO> res = searchProductService.queryProductWithUnknownField(searchConditionDTO).getProducts();  //todo solr zjt
    searchConditionDTO = new SearchConditionDTO();
    searchConditionDTO.setSearchWord("lun");
    searchConditionDTO.setShopId(0l);
    res = searchService.queryProduct("滤清器", 0l, 0, 10);
  }

  @Test
  public void productNameSuggestion() throws Exception {
    ISearchProductService searchProductService = ServiceManager.getService(ISearchProductService.class);
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    SolrClientHelper.getProductSolrClient().deleteAll();

    Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();

    SolrInputDocument doc = new SolrInputDocument();
    doc.addField("id", "1", 1);
    doc.addField("product_name", "轮胎");
    doc.addField("product_brand", "正新");
    doc.addField("product_spec", "1234");
    doc.addField("product_status", "ENABLED");
    doc.addField("shop_id", 0);
    docs.add(doc);

    doc = new SolrInputDocument();
    doc.addField("id", "2", 1);
    doc.addField("product_name", "米其林轮胎");
    doc.addField("product_brand", "米其林");
    doc.addField("product_spec", "1234");
    doc.addField("product_status", "ENABLED");
    doc.addField("shop_id", 0);
    docs.add(doc);

    doc = new SolrInputDocument();
    doc.addField("id", "3", 1);
    doc.addField("product_name", "米其林轮胎");
    doc.addField("product_brand", "米其林");
    doc.addField("product_spec", "1236");
    doc.addField("product_status", "ENABLED");
    doc.addField("shop_id", 0);
    docs.add(doc);

    doc = new SolrInputDocument();
    doc.addField("id", "4", 1);
    doc.addField("product_name", "元宝梁");
    doc.addField("product_brand", "正");
    doc.addField("product_spec", "1236");
    doc.addField("shop_id", 0);
    doc.addField("product_status", "ENABLED");
    docs.add(doc);
    SolrClientHelper.getProductSolrClient().addDocs(docs);

    SearchConditionDTO searchConditionDTO = new SearchConditionDTO();
    searchConditionDTO.setSearchWord("轮胎正");
    searchConditionDTO.setRows(10);
    searchConditionDTO.setShopId(0l);
    searchConditionDTO.setIncludeBasic(false);
    List<String> res = searchProductService.queryProductSuggestionWithSimpleList(searchConditionDTO);
    assertEquals(3, res.size());
    assertEquals("轮胎 正新", res.get(0));
  }

  @Test
  public void firstLetterSplitTest() throws Exception {
    ISearchProductService searchProductService = ServiceManager.getService(ISearchProductService.class);

    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    SolrClientHelper.getProductSolrClient().deleteAll();

    Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
    SolrInputDocument doc = new SolrInputDocument();
    doc.addField("id", "1", 1);
    doc.addField("product_name_fl", "hphhs hh");
    doc.addField("product_brand", "正新");
    doc.addField("product_spec", "1234");
    doc.addField("shop_id", 0);
    doc.addField("product_status", "ENABLED");
    docs.add(doc);


    SolrClientHelper.getProductSolrClient().addDocs(docs);

    StringBuilder qString = new StringBuilder();
    String q = "hh";
    qString.append("(");
    qString.append("product_name_fl").append(":").append("(").append(q + "*").append(")^4");
    qString.append(")");
    SolrQuery query = new SolrQuery();
    query.setQuery(qString.toString());
    query.setParam("debugQuery", "true");
    query.setParam("fl", "*,score");

    QueryResponse rsp = SolrClientHelper.getProductSolrClient().query(query);
    assertEquals(1, rsp.getResults().size());

  }


  @Test
  public void queryCustomerShopInventoryTest() throws Exception {
    ISearchProductService searchProductService = ServiceManager.getService(ISearchProductService.class);
    createProducts();
    SearchConditionDTO searchConditionDTO = new SearchConditionDTO();
    searchConditionDTO.setWholesalerShopId(0l);
    searchConditionDTO.setProductName("轮");
    ProductSearchResultGroupListDTO productSearchResultListDTOList = searchProductService.queryCustomerShopInventory(searchConditionDTO);
    assertNumber(7, productSearchResultListDTOList.getProductSearchResultList());

    searchConditionDTO = new SearchConditionDTO();
    searchConditionDTO.setWholesalerShopId(0l);
    searchConditionDTO.setRelatedCustomerShopIds(new String[]{"1", "3"});
    searchProductService = ServiceManager.getService(ISearchProductService.class);
    productSearchResultListDTOList = searchProductService.queryCustomerShopInventory(searchConditionDTO);
    assertNumber(5, productSearchResultListDTOList.getProductSearchResultList());
  }

  @Test
  public void queryCustomerInventoryByCustomerShopIdTest() throws Exception {
    ISearchProductService searchProductService = ServiceManager.getService(ISearchProductService.class);
    createProducts();
    //查找批发商下面某个客户库存 分页
    SearchConditionDTO searchConditionDTO = new SearchConditionDTO();
    searchConditionDTO.setWholesalerShopId(0l);
    searchConditionDTO.setRelatedCustomerShopIds(new String[]{"3"});
    searchConditionDTO.setRows(2);
    searchConditionDTO.setStart(0);
    ProductSearchResultListDTO productSearchResultListDTO = searchProductService.queryCustomerInventoryByCustomerShopId(searchConditionDTO);
    assertEquals(2, productSearchResultListDTO.getProducts().size());
    assertEquals(3, productSearchResultListDTO.getNumFound());

    //测试 库存作为条件
    searchConditionDTO = new SearchConditionDTO();
    searchConditionDTO.setWholesalerShopId(0l);
    searchConditionDTO.setRelatedCustomerShopIds(new String[]{"3"});
    searchConditionDTO.setInventoryAmountDown(11);
    searchConditionDTO.setInventoryAmountUp(30);
    searchProductService = ServiceManager.getService(ISearchProductService.class);
    productSearchResultListDTO = searchProductService.queryCustomerInventoryByCustomerShopId(searchConditionDTO);
    assertEquals(2, productSearchResultListDTO.getProducts().size());

    searchConditionDTO = new SearchConditionDTO();
    searchConditionDTO.setWholesalerShopId(0l);
    searchConditionDTO.setRelatedCustomerShopIds(new String[]{"3"});
    searchConditionDTO.setInventoryAmountUp(11);
    searchProductService = ServiceManager.getService(ISearchProductService.class);
    productSearchResultListDTO = searchProductService.queryCustomerInventoryByCustomerShopId(searchConditionDTO);
    assertEquals(1, productSearchResultListDTO.getProducts().size());


  }

  private void assertNumber(int number, List<ProductSearchResultListDTO> productSearchResultListDTOList) {
    int i = 0;
    for (ProductSearchResultListDTO dto : productSearchResultListDTOList) {
      i += dto.getProducts().size();
    }
    assertEquals(number, i);
  }

  private void createProducts() throws Exception {
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
    SolrInputDocument doc = new SolrInputDocument();
    //============shop_id = 1===========
    doc.addField("id", "1");
    doc.addField("product_name", "轮胎");
    doc.addField("shop_id", "1");
    doc.addField("inventory_amount", 2);
    doc.addField("wholesaler_shop_id", "0");
    doc.addField("product_status", "ENABLED");
    docs.add(doc);

    doc = new SolrInputDocument();
    doc.addField("id", "11");
    doc.addField("product_name", "轮毂");
    doc.addField("inventory_amount", 10);
    doc.addField("shop_id", "1");
    doc.addField("wholesaler_shop_id", "0");
    doc.addField("product_status", "ENABLED");
    docs.add(doc);

    //============shop_id = 2===========
    doc = new SolrInputDocument();
    doc.addField("id", "2");
    doc.addField("product_name", "轮胎");
    doc.addField("shop_id", "2");
    doc.addField("inventory_amount", 1);
    doc.addField("wholesaler_shop_id", "0");
    doc.addField("product_status", "ENABLED");
    docs.add(doc);

    doc = new SolrInputDocument();
    doc.addField("id", "22");
    doc.addField("product_name", "轮毂");
    doc.addField("shop_id", "2");
    doc.addField("inventory_amount", 12);
    doc.addField("wholesaler_shop_id", "0");
    doc.addField("product_status", "ENABLED");
    docs.add(doc);

    //============shop_id =3===========
    doc = new SolrInputDocument();
    doc.addField("id", "3");
    doc.addField("product_name", "轮胎");
    doc.addField("shop_id", "3");
    doc.addField("inventory_amount", 23);
    doc.addField("wholesaler_shop_id", "0");
    doc.addField("product_status", "ENABLED");
    docs.add(doc);

    doc = new SolrInputDocument();
    doc.addField("id", "33");
    doc.addField("product_name", "轮毂");
    doc.addField("shop_id", "3");
    doc.addField("inventory_amount", 4);
    doc.addField("wholesaler_shop_id", "0");
    doc.addField("product_status", "ENABLED");
    docs.add(doc);

    doc = new SolrInputDocument();
    doc.addField("id", "333");
    doc.addField("product_name", "轮胎架");
    doc.addField("shop_id", "3");
    doc.addField("inventory_amount", 11);
    doc.addField("wholesaler_shop_id", "0");
    doc.addField("product_status", "ENABLED");
    docs.add(doc);
    SolrClientHelper.getProductSolrClient().addDocs(docs);
  }

}
