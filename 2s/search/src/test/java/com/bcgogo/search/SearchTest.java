package com.bcgogo.search;

import com.bcgogo.config.service.IConfigService;
import com.bcgogo.search.client.SolrClientHelper;
import com.bcgogo.search.dto.SearchConditionDTO;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.search.service.vehicle.ISearchVehicleService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.utils.ShopConstant;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: Caiwei
 * Date: 10/4/11
 * Time: 10:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class SearchTest extends AbstractTest {
  @Before
  public void setUpTest() throws Exception {
  }

 /* @Test
public void testSearchWithVehicle() throws Exception {
SolrInputDocument doc1 = new SolrInputDocument();
doc1.addField("id", "id1", 1);
doc1.addField("sku", "doc1", 1);
//    doc1.addField("price", 100);
doc1.addField("shop_id", 1);
doc1.addField("kind", "轮胎");
doc1.addField("product_name", "WOJL");
doc1.addField("product_model", "MODEL001");
doc1.addField("product_spec", "KIQLI");
doc1.addField("pv_model_id", 98999);
doc1.addField("pv_model_id", 98998);
doc1.addField("pv_brand_id", 98997);

SolrInputDocument doc2 = new SolrInputDocument();
doc2.addField("id", "id2", 2);
doc2.addField("sku", "doc2", 2);
//    doc2.addField("price", 200);
doc2.addField("shop_id", 1);
doc2.addField("kind", "轮胎");
doc2.addField("product_name", "KSJL");
doc2.addField("product_model", "MODEL002");
doc2.addField("product_spec", "KPC-004");
doc2.addField("pv_model_id", 98999);
doc2.addField("pv_model_id", 98996);
doc2.addField("pv_brand_id", 98995);

SolrInputDocument doc3 = new SolrInputDocument();
doc3.addField("id", "id3", 3);
doc3.addField("sku", "doc3", 3);
//    doc3.addField("price", 250);
doc3.addField("shop_id", 1);
doc3.addField("kind", "轮胎");
doc3.addField("product_name", null);
doc3.addField("product_model", "MODEL003");
doc3.addField("product_spec", "205/45R16");
doc3.addField("pv_model_id", 98994);
doc3.addField("pv_model_id", 98996);
doc3.addField("pv_brand_id", 98993);

Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
docs.add(doc1);
docs.add(doc2);
docs.add(doc3);

ISearchService searchService = ServiceManager.getService(ISearchService.class);
searchService.deleteByQuery("*:*", "product");
searchService.updateProduct(docs);

SolrQuery query = new SolrQuery();
//    query.setQuery("-(product_name:[* TO *])");
//    query.setQuery("(-product_name:[* TO *]) OR product_spec:KPC-004");
query.setQuery("*:*");
//    query.setFilterQueries("(id:id2 OR id:id3) AND (id:*)");
//    query.setFilterQueries("-(id:id1) OR (id:id1)");
query.setFilterQueries("-(-product_name:KSJL AND product_name:[* TO *]) AND (product_name:WOJL)");
//    query.setFilterQueries("(id:id1) OR !(id:id2) OR (id:id3)");
//    query.setFilterQueries("(id:id1 OR id:id3) OR !(product_spec:KPC-004)");
//    query.setFilterQueries("(!(product_spec:KPC-004) AND !(product_spec:KIQLI))");
//    query.setFilterQueries("(!(product_model:MODEL003) AND !(product_spec:205/45R16))");
//    query.setFilterQueries("(-(product_name:[* TO *])) OR product_spec:KPC-004");
//    query.setFilterQueries("-(product_name:[* TO *])");

QueryResponse rsp = searchService.query(query, "product");
SolrDocumentList documents = rsp.getResults();
int size = documents.size();
SolrDocument doc = documents.get(0);
ArrayList<String> values = (ArrayList<String>) doc.getFieldValue("product_spec");

assertEquals("韩泰185R14 6PR （RA08）轮胎", values.get(0));

TrimDTO trimDTO = new TrimDTO();
trimDTO.setId(111l);
trimDTO.setBrandId(98995l);
trimDTO.setMfrId(8984l);
trimDTO.setYearId(1999L);
trimDTO.setModelId(98789l);
rsp = searchService.queryProductWithFq("轮胎", trimDTO, null);
documents = rsp.getResults();
doc = documents.get(0);
values = (ArrayList<String>) doc.getFieldValue("product_name");
assertEquals("普利司通185/60R14（B250）轮胎", values.get(0));

trimDTO = new TrimDTO();
trimDTO.setId(111l);
trimDTO.setBrandId(98995l);
trimDTO.setMfrId(8984l);
trimDTO.setYearId(1999L);
trimDTO.setModelId(98999l);
rsp = searchService.queryProductWithFq("轮胎 sku:doc1", trimDTO, null);
documents = rsp.getResults();
doc = documents.get(0);
values = (ArrayList<String>) doc.getFieldValue("product_name");
assertEquals("佳通165/70R13轮胎", values.get(0));

List<String> terms = searchService.queryTermsForProduct("轮");
assertEquals("轮胎", terms.get(0));

List<String> productname = searchService.getProductByProductName("product_name:*", 1L);
assertEquals(3, productname.size());

//    trimDTO = new TrimDTO();
//    trimDTO.setId(111l);
//    trimDTO.setBrandId(98995l);
//    trimDTO.setMfrId(8984l);
//    trimDTO.setYearId(1999L);
//    trimDTO.setModelId(98996l);
//    List<String> products = searchService.getProductSuggestionListByKeywords("product_model:MODEL003 price:250", trimDTO, null);
//    assertEquals(1, products.size());
}


@Test
public void testSearchWithVehicleAndShop() throws Exception {
ISearchService searchService = ServiceManager.getService(ISearchService.class);
searchService.deleteByQuery("*:*", "product");

SolrInputDocument doc1 = new SolrInputDocument();
doc1.addField("id", "id1", 1);
doc1.addField("sku", "doc1", 1);
doc1.addField("price", 100);
doc1.addField("kind", "轮胎");
doc1.addField("product_name", "佳通165/70R13轮胎");
doc1.addField("pv_model_id", 98999);
doc1.addField("pv_model_id", 98998);
doc1.addField("pv_brand_id", 98997);
doc1.addField("shop_id", 1000);

SolrInputDocument doc2 = new SolrInputDocument();
doc2.addField("id", "id2", 2);
doc2.addField("sku", "doc2", 2);
doc2.addField("price", 200);
doc2.addField("kind", "轮胎");
doc2.addField("product_name", "普利司通185/60R14（B250）轮胎");
doc2.addField("pv_model_id", 98999);
doc2.addField("pv_model_id", 98996);
doc2.addField("pv_brand_id", 98995);
doc2.addField("shop_id", 1001);

SolrInputDocument doc3 = new SolrInputDocument();
doc3.addField("id", "id3", 3);
doc3.addField("sku", "doc3", 3);
doc3.addField("price", 250);
doc3.addField("kind", "轮胎");
doc3.addField("product_name", "韩泰185R14 6PR （RA08）轮胎");
doc3.addField("pv_model_id", 98994);
doc3.addField("pv_model_id", 98996);
doc3.addField("pv_brand_id", 98993);
doc3.addField("shop_id", 1000);

SolrInputDocument doc4 = new SolrInputDocument();
doc4.addField("id", "id4", 3);
doc4.addField("sku", "doc4", 3);
doc4.addField("price", 350);
doc4.addField("kind", "轮胎");
doc4.addField("product_name", "韩泰185R14 6PR （RA08）轮胎");
doc4.addField("pv_model_id", 98994);
doc4.addField("pv_model_id", 98996);
doc4.addField("pv_brand_id", 98993);
doc4.addField("shop_id", 1001);


Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
docs.add(doc1);
docs.add(doc2);
docs.add(doc3);
docs.add(doc4);


searchService.updateProduct(docs);

SolrQuery query = new SolrQuery();
query.setQuery("product_name:韩泰轮胎");

QueryResponse rsp = searchService.query(query, "product");
SolrDocumentList documents = rsp.getResults();
SolrDocument doc = documents.get(0);
ArrayList<String> values = (ArrayList<String>) doc.getFieldValue("product_name");

assertEquals("韩泰185R14 6PR （RA08）轮胎", values.get(0));

TrimDTO trimDTO = new TrimDTO();
trimDTO.setId(111l);
trimDTO.setBrandId(98995l);
trimDTO.setMfrId(8984l);
trimDTO.setYearId(1999L);
trimDTO.setModelId(98789l);
rsp = searchService.queryProductWithFq("轮胎", trimDTO, 1001l);
documents = rsp.getResults();
doc = documents.get(0);
values = (ArrayList<String>) doc.getFieldValue("product_name");
assertEquals("普利司通185/60R14（B250）轮胎", values.get(0));

trimDTO = new TrimDTO();
trimDTO.setId(111l);
trimDTO.setBrandId(98995l);
trimDTO.setMfrId(8984l);
trimDTO.setYearId(1999L);
trimDTO.setModelId(98999l);
rsp = searchService.queryProductWithFq("轮胎 sku:doc1", trimDTO, 1001l);
documents = rsp.getResults();
int size = documents.size();
assertEquals(0, size);

}
        */

  @Test
  public void testVehicleSuggestionListByKeywords() throws Exception {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
   configService.setConfig("SelectOptionNumber","15", ShopConstant.BC_SHOP_ID) ;
    SolrInputDocument doc1 = new SolrInputDocument();
    doc1.addField("id", "id1");
    doc1.addField("doc_type", SolrClientHelper.BcgogoSolrDocumentType.VEHICLE_PROPERTY_SUGGESTION_DOC_TYPE.getValue());
    doc1.addField("brand", "丰田");
    doc1.addField("model", "佳美E级");
    doc1.addField("year", 1989);
    doc1.addField("mfr", "广州丰田");
    doc1.addField("engine", "2.6L");

    SolrInputDocument doc2 = new SolrInputDocument();
    doc2.addField("doc_type", SolrClientHelper.BcgogoSolrDocumentType.VEHICLE_PROPERTY_SUGGESTION_DOC_TYPE.getValue());
    doc2.addField("id", "id2");
    doc2.addField("brand", "丰田");
    doc2.addField("model", "佳美D级");
    doc2.addField("year", 1989);
    doc2.addField("mfr", "广州丰田");
    doc2.addField("engine", "3.0L");

    Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
    docs.add(doc1);
    docs.add(doc2);

    ISearchVehicleService searchVehicleService = ServiceManager.getService(ISearchVehicleService.class);
    SolrClientHelper.getVehicleSolrClient().addDocs(docs);
    SearchConditionDTO searchConditionDTO = new SearchConditionDTO();
    searchConditionDTO.setSearchWord("");
    searchConditionDTO.setSearchField("model");
    searchConditionDTO.setVehicleBrand("丰田");
    searchConditionDTO.setVehicleModel("");
    searchConditionDTO.setVehicleEngine("");
    searchConditionDTO.setVehicleYear("");
    List<String> terms = searchVehicleService.getVehicleSuggestionListByKeywords(searchConditionDTO);
    //为什么terms有2条？
    assertEquals(2, terms.size());
    searchConditionDTO.setSearchWord("丰");
    searchConditionDTO.setSearchField("brand");
    searchConditionDTO.setVehicleBrand("");
    searchConditionDTO.setVehicleModel("");
    searchConditionDTO.setVehicleEngine("");
    searchConditionDTO.setVehicleYear("");
    terms = searchVehicleService.getVehicleSuggestionListByKeywords(searchConditionDTO);
    assertEquals(1, terms.size());
    searchConditionDTO.setSearchWord("D级");
    searchConditionDTO.setSearchField("model");
    searchConditionDTO.setVehicleBrand("");
    searchConditionDTO.setVehicleModel("");
    searchConditionDTO.setVehicleYear("2.6L");
    terms = searchVehicleService.getVehicleSuggestionListByKeywords(searchConditionDTO);
    assertEquals(0, terms.size());
    searchConditionDTO.setSearchWord("D级");
    searchConditionDTO.setSearchField("model");
    searchConditionDTO.setVehicleBrand("");
    searchConditionDTO.setVehicleModel("");
    searchConditionDTO.setVehicleYear("");
    terms = searchVehicleService.getVehicleSuggestionListByKeywords(searchConditionDTO);
    assertEquals(1, terms.size());
    searchConditionDTO.setSearchWord("3.0L");
    searchConditionDTO.setSearchField("engine");
    searchConditionDTO.setVehicleBrand("本田");
    searchConditionDTO.setVehicleEngine("");
    searchConditionDTO.setVehicleYear("");
    terms = searchVehicleService.getVehicleSuggestionListByKeywords(searchConditionDTO);
    assertEquals(0, terms.size());
    searchConditionDTO.setSearchWord("雅阁L");
    searchConditionDTO.setSearchField("model");
    searchConditionDTO.setVehicleBrand("本田");
    searchConditionDTO.setVehicleModel("雅阁");
    searchConditionDTO.setVehicleEngine("");
    searchConditionDTO.setVehicleYear("");

    terms = searchVehicleService.getVehicleSuggestionListByKeywords(searchConditionDTO);
    assertEquals(0, terms.size());
    searchConditionDTO.setSearchWord("3.0L");
    searchConditionDTO.setSearchField("engine");
    searchConditionDTO.setVehicleBrand("");
    searchConditionDTO.setVehicleModel("");
    searchConditionDTO.setVehicleEngine("");
    searchConditionDTO.setVehicleYear("1989");
    terms = searchVehicleService.getVehicleSuggestionListByKeywords(searchConditionDTO);
    assertEquals(1, terms.size());
    searchConditionDTO.setSearchWord("3.0L");
    searchConditionDTO.setSearchField("engine");
    searchConditionDTO.setVehicleBrand("");
    searchConditionDTO.setVehicleModel("");
    searchConditionDTO.setVehicleEngine("3.0L");
    searchConditionDTO.setVehicleYear("");
    terms = searchVehicleService.getVehicleSuggestionListByKeywords(searchConditionDTO);
    assertEquals(1, terms.size());

  }

  @Test
  public void testVehicleIdsByKeywords() throws Exception {
    SolrInputDocument doc1 = new SolrInputDocument();
    doc1.addField("id", "id1");
    doc1.addField("doc_type", SolrClientHelper.BcgogoSolrDocumentType.VEHICLE_PROPERTY_SUGGESTION_DOC_TYPE.getValue());
    doc1.addField("brand", "丰田");
    doc1.addField("model", "卡罗拉");
    doc1.addField("year", "2011");
    doc1.addField("engine", "1.6L");
    doc1.addField("pv_brand_id", 10001L);
    doc1.addField("pv_model_id", 20001L);
    doc1.addField("pv_year_id", 30001L);
    doc1.addField("pv_engine_id", 40001L);

    SolrInputDocument doc2 = new SolrInputDocument();
    doc2.addField("id", "id2");
    doc2.addField("doc_type", SolrClientHelper.BcgogoSolrDocumentType.VEHICLE_PROPERTY_SUGGESTION_DOC_TYPE.getValue());
    doc2.addField("brand", "丰田");
    doc2.addField("model", "卡罗拉");
    doc2.addField("year", "2011");
    doc2.addField("engine", "2.0L");
    doc2.addField("pv_brand_id", 10001L);
    doc2.addField("pv_model_id", 20001L);
    doc2.addField("pv_year_id", null);
    doc2.addField("pv_engine_id", 40001L);

    Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
    docs.add(doc1);
    docs.add(doc2);

    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    SolrClientHelper.getVehicleSolrClient().addDocs(docs);

    List<Long> terms = (List<Long>) searchService.getVehicleIdsByKeywords("", "卡罗拉", "", "2.0L", true).get(false);
    assertEquals(4, terms.size());
  }


}
