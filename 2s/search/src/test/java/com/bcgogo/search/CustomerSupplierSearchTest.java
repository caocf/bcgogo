package com.bcgogo.search;

import com.bcgogo.search.client.SolrClientHelper;
import com.bcgogo.search.dto.CustomerSupplierSearchConditionDTO;
import com.bcgogo.search.dto.CustomerSupplierSearchResultListDTO;
import com.bcgogo.search.dto.CustomerSupplierSolrIndexDTO;
import com.bcgogo.search.dto.SearchSuggestionDTO;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.search.service.user.ISearchCustomerSupplierService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.ContactDTO;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.MemberDTO;
import com.bcgogo.user.dto.VehicleDTO;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-8-29
 * Time: 下午5:26
 * To change this template use File | Settings | File Templates.
 */
public class CustomerSupplierSearchTest extends AbstractTest {

  @Before
  public void setUpTest() throws Exception {
    SolrClientHelper.getCustomerSupplierSolrClient().deleteAll();
    SolrClientHelper.getProductSolrClient().deleteAll();
    create();
  }

  private void create() throws Exception {
    CustomerDTO customerDTO = new CustomerDTO();
    customerDTO.setId(2l);
    customerDTO.setName("威尼尤至");

    List<ContactDTO> contactDTOs = new ArrayList<ContactDTO>();
    ContactDTO contactDTO = new ContactDTO();
    contactDTO.setId(21l);
    contactDTO.setName("李蔡伟");
    contactDTO.setMobile("18926125378");
    contactDTOs.add(contactDTO);
    customerDTO.setContactDTOList(contactDTOs);

    MemberDTO memberDTO = new MemberDTO();
    memberDTO.setMemberNo("189261");
    customerDTO.setMemberDTO(memberDTO);
    List<VehicleDTO> vehicleDTOList = new ArrayList<VehicleDTO>();
    VehicleDTO vehicleDTO = new VehicleDTO();
    vehicleDTO.setLicenceNo("美B25378");
    vehicleDTOList.add(vehicleDTO);
    customerDTO.setVehicleDTOList(vehicleDTOList);
    customerDTO.setCustomerShopId(1000l);
    customerDTO.setShopId(0l);
    CustomerSupplierSolrIndexDTO customerSupplierSolrIndexDTO = new CustomerSupplierSolrIndexDTO(customerDTO,SolrClientHelper.BcgogoSolrDocumentType.CUSTOMER_SUPPLIER.getValue());
    Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
    docs.add(customerSupplierSolrIndexDTO.toSolrDocument());


    customerDTO = new CustomerDTO();
    customerDTO.setId(3l);
    customerDTO.setName("统购");

    contactDTOs = new ArrayList<ContactDTO>();
    contactDTO = new ContactDTO();
    contactDTO.setId(31l);
    contactDTO.setName("陈子豪");
    contactDTO.setMobile("15862936076");
    contactDTOs.add(contactDTO);
    customerDTO.setContactDTOList(contactDTOs);

    memberDTO = new MemberDTO();
    memberDTO.setMemberNo("158629");
    customerDTO.setMemberDTO(memberDTO);
    vehicleDTOList = new ArrayList<VehicleDTO>();
    vehicleDTO = new VehicleDTO();
    vehicleDTO.setLicenceNo("苏A36E76");
    vehicleDTOList.add(vehicleDTO);
    customerDTO.setVehicleDTOList(vehicleDTOList);
    customerDTO.setCustomerShopId(1001l);
    customerDTO.setShopId(0l);
    customerSupplierSolrIndexDTO = new CustomerSupplierSolrIndexDTO(customerDTO,SolrClientHelper.BcgogoSolrDocumentType.CUSTOMER_SUPPLIER.getValue());
    docs.add(customerSupplierSolrIndexDTO.toSolrDocument());

    customerDTO = new CustomerDTO();
    customerDTO.setId(4l);
    customerDTO.setName("威尼尤至");

    contactDTOs = new ArrayList<ContactDTO>();
    contactDTO = new ContactDTO();
    contactDTO.setId(41l);
    contactDTO.setName("张峻滔");
    contactDTO.setMobile("15851654173");
    contactDTOs.add(contactDTO);
    customerDTO.setContactDTOList(contactDTOs);


    memberDTO = new MemberDTO();
    memberDTO.setMemberNo("158516");
    customerDTO.setMemberDTO(memberDTO);
    vehicleDTOList = new ArrayList<VehicleDTO>();
    vehicleDTO = new VehicleDTO();
    vehicleDTO.setLicenceNo("苏A54173");
    vehicleDTOList.add(vehicleDTO);
    customerDTO.setVehicleDTOList(vehicleDTOList);
    customerDTO.setCustomerShopId(1002l);
    customerDTO.setShopId(0l);
    customerSupplierSolrIndexDTO = new CustomerSupplierSolrIndexDTO(customerDTO,SolrClientHelper.BcgogoSolrDocumentType.CUSTOMER_SUPPLIER.getValue());
    docs.add(customerSupplierSolrIndexDTO.toSolrDocument());

    SolrClientHelper.getCustomerSupplierSolrClient().addDocs(docs);
  }

  //客户供应商 solr 搜索
  @Test
  public void customerSupplierSolrSuggestion() throws Exception {
    ISearchCustomerSupplierService service = ServiceManager.getService(ISearchCustomerSupplierService.class);
    SolrQuery query = new SolrQuery();
    query.setQuery("*:*");
    query.setFilterQueries("shop_id:0");
    QueryResponse rsp = SolrClientHelper.getCustomerSupplierSolrClient().query(query);
    assertEquals(3, rsp.getResults().size());

    CustomerSupplierSearchConditionDTO searchConditionDTO = new CustomerSupplierSearchConditionDTO();
    searchConditionDTO.setSearchWord("威尼尤至");
    searchConditionDTO.setRows(10);
    searchConditionDTO.setShopId(0l);
    //不包含 会员号
    List<SearchSuggestionDTO> res = service.queryCustomerSupplierSuggestion(searchConditionDTO);
    assertEquals(2, res.size());

    searchConditionDTO.setSearchWord("zjt");
    searchConditionDTO.setRows(10);
    searchConditionDTO.setShopId(0l);
    res = service.queryCustomerSupplierSuggestion(searchConditionDTO);
    assertEquals(1, res.size());
    assertEquals("4", res.get(0).suggestionEntry.get(1)[1]);
    assertEquals("威尼尤至", res.get(0).suggestionEntry.get(2)[1]);
    assertEquals("张峻滔", res.get(0).suggestionEntry.get(3)[1]);
    assertEquals("15851654173", res.get(0).suggestionEntry.get(4)[1]);

    searchConditionDTO.setSearchWord("zhangjuntao");
    searchConditionDTO.setRows(10);
    searchConditionDTO.setShopId(0l);
    res = service.queryCustomerSupplierSuggestion(searchConditionDTO);
    assertEquals(1, res.size());
    assertEquals("4", res.get(0).suggestionEntry.get(1)[1]);
    assertEquals("威尼尤至", res.get(0).suggestionEntry.get(2)[1]);
    assertEquals("张峻滔", res.get(0).suggestionEntry.get(3)[1]);
    assertEquals("15851654173", res.get(0).suggestionEntry.get(4)[1]);


    searchConditionDTO.setSearchWord("威尼");
    searchConditionDTO.setRows(10);
    searchConditionDTO.setShopId(0l);
    res = service.queryCustomerSupplierSuggestion(searchConditionDTO);
    assertEquals(2, res.size());
    assertEquals("4", res.get(1).suggestionEntry.get(1)[1]);
    assertEquals("威尼尤至", res.get(1).suggestionEntry.get(2)[1]);
    assertEquals("张峻滔", res.get(1).suggestionEntry.get(3)[1]);
    assertEquals("15851654173", res.get(1).suggestionEntry.get(4)[1]);

    //包括会员信息
    searchConditionDTO.setSearchWord("158516");//完整 信息
    searchConditionDTO.setRows(10);
    searchConditionDTO.setSearchFieldStrategies(new CustomerSupplierSearchConditionDTO.SearchFieldStrategy[]{CustomerSupplierSearchConditionDTO.SearchFieldStrategy.searchIncludeLicenseNo,CustomerSupplierSearchConditionDTO.SearchFieldStrategy.searchIncludeMemberNo});
    searchConditionDTO.setShopId(0l);
    res = service.queryCustomerSupplierSuggestion(searchConditionDTO);
    assertEquals(1, res.size());
    assertEquals("4", res.get(0).suggestionEntry.get(1)[1]);
    assertEquals("威尼尤至", res.get(0).suggestionEntry.get(2)[1]);
    assertEquals("张峻滔", res.get(0).suggestionEntry.get(3)[1]);
    assertEquals("15851654173", res.get(0).suggestionEntry.get(4)[1]);

    //车牌
    searchConditionDTO.setSearchWord("a54173");//完整 信息
    searchConditionDTO.setRows(10);
    searchConditionDTO.setSearchFieldStrategies(new CustomerSupplierSearchConditionDTO.SearchFieldStrategy[]{CustomerSupplierSearchConditionDTO.SearchFieldStrategy.searchIncludeLicenseNo, CustomerSupplierSearchConditionDTO.SearchFieldStrategy.searchIncludeMemberNo});
    searchConditionDTO.setShopId(0l);
    res = service.queryCustomerSupplierSuggestion(searchConditionDTO);
    assertEquals(1, res.size());
    assertEquals("4", res.get(0).suggestionEntry.get(1)[1]);
    assertEquals("威尼尤至", res.get(0).suggestionEntry.get(2)[1]);
    assertEquals("张峻滔", res.get(0).suggestionEntry.get(3)[1]);
    assertEquals("15851654173", res.get(0).suggestionEntry.get(4)[1]);

    //车牌
    searchConditionDTO.setSearchWord("苏A54173");
    searchConditionDTO.setRows(10);
    searchConditionDTO.setSearchFieldStrategies(new CustomerSupplierSearchConditionDTO.SearchFieldStrategy[]{CustomerSupplierSearchConditionDTO.SearchFieldStrategy.searchIncludeLicenseNo, CustomerSupplierSearchConditionDTO.SearchFieldStrategy.searchIncludeMemberNo});
    searchConditionDTO.setShopId(0l);
    res = service.queryCustomerSupplierSuggestion(searchConditionDTO);
    assertEquals(1, res.size());  // 苏  A
    assertEquals("4", res.get(0).suggestionEntry.get(1)[1]);
    assertEquals("威尼尤至", res.get(0).suggestionEntry.get(2)[1]);
    assertEquals("张峻滔", res.get(0).suggestionEntry.get(3)[1]);
    assertEquals("15851654173", res.get(0).suggestionEntry.get(4)[1]);

    //车牌
    searchConditionDTO.setSearchWord("SA541");
    searchConditionDTO.setRows(10);
    searchConditionDTO.setSearchFieldStrategies(new CustomerSupplierSearchConditionDTO.SearchFieldStrategy[]{CustomerSupplierSearchConditionDTO.SearchFieldStrategy.searchIncludeLicenseNo, CustomerSupplierSearchConditionDTO.SearchFieldStrategy.searchIncludeMemberNo});
    searchConditionDTO.setShopId(0l);
    res = service.queryCustomerSupplierSuggestion(searchConditionDTO);
    assertEquals(1, res.size());
    assertEquals("4", res.get(0).suggestionEntry.get(1)[1]);
    assertEquals("威尼尤至", res.get(0).suggestionEntry.get(2)[1]);
    assertEquals("张峻滔", res.get(0).suggestionEntry.get(3)[1]);
    assertEquals("15851654173", res.get(0).suggestionEntry.get(4)[1]);

    searchConditionDTO.setSearchWord("A541");
    searchConditionDTO.setRows(10);
    searchConditionDTO.setSearchFieldStrategies(new CustomerSupplierSearchConditionDTO.SearchFieldStrategy[]{CustomerSupplierSearchConditionDTO.SearchFieldStrategy.searchIncludeLicenseNo, CustomerSupplierSearchConditionDTO.SearchFieldStrategy.searchIncludeMemberNo});
    searchConditionDTO.setShopId(0l);
    res = service.queryCustomerSupplierSuggestion(searchConditionDTO);
    assertEquals(1, res.size());
    assertEquals("4", res.get(0).suggestionEntry.get(1)[1]);
    assertEquals("威尼尤至", res.get(0).suggestionEntry.get(2)[1]);
    assertEquals("张峻滔", res.get(0).suggestionEntry.get(3)[1]);
    assertEquals("15851654173", res.get(0).suggestionEntry.get(4)[1]);

  }


  //通过商品查供应商
  @Test
  public void querySupplierByProductInfoTest() throws Exception {
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    ISearchCustomerSupplierService service = ServiceManager.getService(ISearchCustomerSupplierService.class);
    Collection<SolrInputDocument> docs;
    docs = new ArrayList<SolrInputDocument>();
    SolrInputDocument doc = new SolrInputDocument();
    doc.addField("id", "1", 1);
    doc.addField("product_name", "轮胎");
    doc.addField("product_brand", "正新");
    doc.addField("product_spec", "1234");
    doc.addField("supplier_id", 2);
    doc.addField("supplier_id", 3);
    doc.addField("shop_id", 0);
    docs.add(doc);
    doc = new SolrInputDocument();
    doc.addField("id", "2", 1);
    doc.addField("product_name", "米其林轮胎");
    doc.addField("product_brand", "米其林");
    doc.addField("product_spec", "1234");
    doc.addField("supplier_id", 3);
    doc.addField("supplier_id", 4);
    doc.addField("shop_id", 0);
    docs.add(doc);
    SolrClientHelper.getProductSolrClient().addDocs(docs);

    SolrQuery query = new SolrQuery();
    query.setQuery("*:*");
    query.setFilterQueries("shop_id:0");
    QueryResponse rsp = SolrClientHelper.getCustomerSupplierSolrClient().query(query);
    assertEquals(3, rsp.getResults().size());
  }

  //批发商 customer_shop_id 查找客户
  @Test
  public void wholeSalerQueryCustomerTest() throws Exception {
    ISearchCustomerSupplierService service = ServiceManager.getService(ISearchCustomerSupplierService.class);
    Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
    SolrInputDocument doc = new SolrInputDocument();
    doc.addField("id", 12);
    doc.addField("shop_id", 0);
    docs.add(doc);
    SolrClientHelper.getCustomerSupplierSolrClient().addDocs(docs);
    CustomerSupplierSearchConditionDTO searchConditionDTO = new CustomerSupplierSearchConditionDTO();
    searchConditionDTO.getRelatedCustomerOrSupplierShopIds().add(1000l);
    searchConditionDTO.getRelatedCustomerOrSupplierShopIds().add(1001l);
    searchConditionDTO.setRows(10);
    searchConditionDTO.setShopId(0l);
    CustomerSupplierSearchResultListDTO resultListDTO = service.queryCustomerWithUnknownField(searchConditionDTO);
    assertEquals(2, resultListDTO.getCustomerSuppliers().size());

    searchConditionDTO = new CustomerSupplierSearchConditionDTO();
    searchConditionDTO.setSearchStrategies(new CustomerSupplierSearchConditionDTO.SearchStrategy[]{CustomerSupplierSearchConditionDTO.SearchStrategy.customerOrSupplierShopIdNotEmpty});

    searchConditionDTO.setRows(10);
    searchConditionDTO.setShopId(0l);
    resultListDTO = service.queryCustomerWithUnknownField(searchConditionDTO);
    assertEquals(3, resultListDTO.getCustomerSuppliers().size());
  }


}
