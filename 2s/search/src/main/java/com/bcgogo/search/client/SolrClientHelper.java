package com.bcgogo.search.client;

import com.bcgogo.config.service.IConfigService;
import com.bcgogo.service.ServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xzhu
 */
public class SolrClientHelper {
  private static Logger logger = LoggerFactory.getLogger(SolrClientHelper.class);

	private static SolrClient getClient(String core){
    SolrClient client = null;
    try {
      IConfigService configService = ServiceManager.getService(IConfigService.class);
      String solrConfig = configService.getConfig("USE_REMMOTE_SOLR", -1L);
      if (solrConfig != null && "T".equals(solrConfig)) {
        client = BcgogoHttpSolrClientCreator.getBcgogoSolrClient(core);
      } else {
        client = BcgogoEmbeddedSolrClientCreator.getBcgogoSolrClient(core);
      }
    } catch (Exception e) {
      logger.error("core:" + core);
      logger.error("初始化Solr Server 出错");
      logger.error(e.getMessage(), e);
    }
    return client;
  }

  public static SolrClient getProductSolrClient(){
    return getClient(BcgogoSolrCore.PRODUCT_CORE.getValue());
  }
  public static SolrClient getOrderSolrClient(){
    return getClient(BcgogoSolrCore.ORDER_CORE.getValue());
  }
  public static SolrClient getCustomerSupplierSolrClient(){
    return getClient(BcgogoSolrCore.CUSTOMER_SUPPLIER_CORE.getValue());
  }
  public static SolrClient getVehicleSolrClient(){
    return getClient(BcgogoSolrCore.VEHICLE_CORE.getValue());
  }
   public static SolrClient getSuggestionClient(){
    return getClient(BcgogoSolrCore.SUGGESTION.getValue());
  }
  public static SolrClient getOrderItemSolrClient(){
    return getClient(BcgogoSolrCore.ORDER_ITEM_CORE.getValue());
  }
  public static SolrClient geShopSolrClient(){
    return getClient(BcgogoSolrCore.SHOP.getValue());
  }

  public enum BcgogoSolrCore {
    PRODUCT_CORE("product"), ORDER_CORE("order"),ORDER_ITEM_CORE("order_item"),
    VEHICLE_CORE("vehicle"), CUSTOMER_SUPPLIER_CORE("customer_supplier"),
    SUGGESTION("suggestion"),SHOP("shop");

    String value;

    public String getValue() {
      return value;
    }

    private BcgogoSolrCore(String value) {
      this.value = value;
    }
  }

  public enum BcgogoSolrDocumentType {
    SERVICE_DOC_TYPE("service"),PRODUCT_CATEGORY_DOC_TYPE("product_category"),VEHICLE_DOC_TYPE("vehicle"), VEHICLE_PROPERTY_SUGGESTION_DOC_TYPE("vehicle_suggestion"),
    ORDER_ITEM_DOC_TYPE("order_item"),INOUT_RECORD_DOC_TYPE("inout_record"),CUSTOMER_SUPPLIER("customer_supplier"),CONTACT("contact");

    String value;

    public String getValue() {
      return value;
    }

    private BcgogoSolrDocumentType(String value) {
      this.value = value;
    }
  }


  /**
   *set - to set a field.
   * @return
   */
  public static Map<String,Object> getSetOperation(Object val){
    Map<String, Object> oper = new HashMap<String, Object>();
    oper.put("set",val);
    return oper;
  }

  /**
   * add - to add to a multi-valued field.
   * @param val
   * @return
   */
  public static Map<String,Object> getAddOperation(Object val){
    Map<String, Object> oper = new HashMap<String, Object>();
    oper.put("add",val);
    return oper;
  }

  /**
   * inc - to increment a field.
   * @param val
   * @return
   */
  public static Map<String,Object> getIncOperation(Object val){
    Map<String, Object> oper = new HashMap<String, Object>();
    oper.put("inc",val);
    return oper;
  }

  //统一在单元测试的时候清理 索引数据  防止老脏数据影响单元测试   导致单元测试过不了
  public static void unitTestClear() throws Exception {
    getProductSolrClient().deleteByQuery("*:*");
    getVehicleSolrClient().deleteByQuery("*:*");
    getOrderSolrClient().deleteByQuery("*:*");
    getOrderItemSolrClient().deleteByQuery("*:*");
    getSuggestionClient().deleteByQuery("*:*");
    getCustomerSupplierSolrClient().deleteByQuery("*:*");
    geShopSolrClient().deleteByQuery("*:*");
  }
}

