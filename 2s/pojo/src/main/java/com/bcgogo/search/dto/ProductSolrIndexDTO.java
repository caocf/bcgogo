package com.bcgogo.search.dto;

import com.bcgogo.config.dto.AreaDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.constant.ProductConstants;
import com.bcgogo.enums.Product.ProductRelevanceStatus;
import com.bcgogo.enums.ProductStatus;
import com.bcgogo.product.dto.PingyinInfo;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.txn.dto.PromotionsDTO;
import com.bcgogo.txn.dto.SupplierInventoryDTO;
import com.bcgogo.utils.JsonUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.PinyinUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: xzhu
 * Date: 12-8-17
 * Time: 下午2:25
 * 目前创建Product solr索引时只需要设置以下几个字段值（并不代表solr的索引字段就以下几个，有写字段是自动copy的）
 */
public class ProductSolrIndexDTO {
  private static final Logger LOG = LoggerFactory.getLogger(ProductSolrIndexDTO.class);
  private static final String dynamicStorehouseInventoryAmountFieldName = "_storehouse_inventory_amount";
  private static final String dynamicStorehouseInventoryPriceFieldName = "_storehouse_inventory_price";

  private Long id;
  private String product_brand;
  private String product_model;
  private String product_spec;
  private String product_name;
  private String product_vehicle_brand;
  private String product_vehicle_model;
  private String product_vehicle_year;
  private String product_vehicle_engine;
  private Integer product_vehicle_status;
  private String first_letter;
  private String first_letter_combination;
  private String product_name_fl;
  private String product_brand_fl;
  private String product_spec_fl;
  private String product_model_fl;
  private String product_vehicle_brand_fl;
  private String product_vehicle_model_fl;
  private String product_name_py;
  private String product_brand_py;
  private String product_spec_py;
  private String product_model_py;
  private String product_vehicle_brand_py;
  private String product_vehicle_model_py;
  private Double inventory_amount;
  private Long storage_time;
  private Double purchase_price;
  private Boolean isBasicData;
  private Long shop_id;
  private String shop_name;
  private String shop_kind;
  private List<Long> shop_area_ids;
  private String shop_area_info;
  private Long product_id;
  private Double recommendedprice;
  private Long lastmodified;
  private String product_storage_unit;
  private String product_sell_unit;
  private Long product_rate;
  private List<String> supplier_info;
  private List<Long> supplier_id;
  private List<String> supplier_detail;
  private Double inventory_price;//入库价×库存量
  private Double last_30_sales;//最近30天销量

  private Long product_category_id;

  private String product_name_fl_sort;
  private String product_brand_fl_sort;
  private String product_spec_fl_sort;
  private String product_model_fl_sort;
  private String product_vehicle_brand_fl_sort;
  private String product_vehicle_model_fl_sort;
  private String product_kind_fl_sort;
  private String storage_bin;
  private Double trade_price;
  private String commodity_code;//商品编码
  private String product_status;
  private String product_kind;
  private List<String> wholesaler_shop_id;
  private Map<String,Double> dynamicStorehouseInventoryPriceFieldData;//dynamicField
  private Map<String,Double> dynamicStorehouseInventoryAmountFieldData;//dynamicField

  private Double inventory_average_price;

  private Long normal_product_id;
  private String relevance_status;

  private String sales_status;
  private Long last_in_sales_time;
  private Double in_sales_amount;
  private Double in_sales_price;
  private String in_sales_unit;
  private String guarantee_period;
  private List<PromotionsDTO> promotionsDTOs;
  private List<String> promotionsTypeList;
  private List<String> promotionsIdList;
  private Double lower_limit;
  private Double upper_limit;
  private String custom_match_p_content;
  private String custom_match_pv_content;


  public ProductSolrIndexDTO() {
  }

  public ProductSolrIndexDTO(ProductDTO productDTO) {
    this.id = productDTO.getId();
    this.shop_id = productDTO.getShopId() == null ? 1L : productDTO.getShopId();
    this.isBasicData = (shop_id != null && shop_id.longValue() == 1L);

    this.product_category_id = productDTO.getProductCategoryId();
    this.product_id = productDTO.getProductLocalInfoId();  //to add productLocalInfoId
    this.first_letter = productDTO.getFirstLetter();
    this.first_letter_combination = productDTO.getFirstLetterCombination();
    this.inventory_amount = productDTO.getInventoryNum();
    this.storage_time = productDTO.getEditDate();
    this.purchase_price = productDTO.getPurchasePrice();
    this.product_spec = productDTO.getSpec();
    this.product_name = productDTO.getName();
    this.product_model = productDTO.getModel();
    this.product_brand = productDTO.getBrand();
    this.product_vehicle_brand = productDTO.getProductVehicleBrand();
    this.product_vehicle_model = productDTO.getProductVehicleModel();
    this.product_vehicle_engine = productDTO.getProductVehicleEngine();
    this.product_vehicle_status = productDTO.getProductVehicleStatus();
    this.product_storage_unit = productDTO.getStorageUnit();
    this.product_sell_unit = productDTO.getSellUnit();
    this.product_rate = productDTO.getRate();
    this.storage_bin = productDTO.getStorageBin();
    this.trade_price = productDTO.getTradePrice();
    this.commodity_code = productDTO.getCommodityCode();
    this.inventory_average_price= productDTO.getInventoryAveragePrice();
	  this.product_status = productDTO.getStatus() == null? ProductStatus.ENABLED.toString():productDTO.getStatus().toString() ;
    this.product_kind = productDTO.getKindName();
    this.normal_product_id = productDTO.getNormalProductId();
    this.relevance_status = productDTO.getRelevanceStatus()==null? ProductRelevanceStatus.NO.toString():productDTO.getRelevanceStatus().toString();
    this.sales_status = productDTO.getSalesStatus()==null?null:productDTO.getSalesStatus().toString();
    this.last_in_sales_time = productDTO.getLastInSalesTime();
    this.in_sales_amount =NumberUtil.round(productDTO.getInSalesAmount());
    this.in_sales_price = productDTO.getInSalesPrice();
    this.in_sales_unit  = productDTO.getInSalesUnit();
    this.guarantee_period=productDTO.getGuaranteePeriod();
    this.promotionsDTOs = productDTO.getPromotionsDTOs();
    this.promotionsTypeList=productDTO.getPromotionsTypeList();
    this.promotionsIdList=productDTO.getPromotionsIdList();
    this.custom_match_p_content =productDTO.generateCustomMatchPContent();
    this.custom_match_pv_content =productDTO.generateCustomMatchPVContent();
  }

  public SolrInputDocument toSolrInputDocument() {
    generatePingyin();
    SolrInputDocument doc = new SolrInputDocument();
    doc.addField("id", this.id);
    doc.addField("product_id", this.product_id);  //to add productLocalInfoId
    doc.addField("shop_id", shop_id == null ? 1L : shop_id);
    doc.addField("shop_name", shop_name);
    doc.addField("shop_kind", shop_kind);
    doc.addField("shop_area_info", shop_area_info);
    if(CollectionUtils.isNotEmpty(this.getShop_area_ids())){
      for(Long shop_area_id:shop_area_ids){
        if(shop_area_id!=null){
          doc.addField("shop_area_ids",shop_area_id);
        }
      }
    }

    doc.addField("isBasicData", (shop_id != null && shop_id.longValue() == 1L));
    doc.addField("first_letter", first_letter);
    doc.addField("first_letter_combination", first_letter_combination);
    doc.addField("inventory_amount", inventory_amount);
    doc.addField("storage_time", storage_time);
    doc.addField("purchase_price", purchase_price);
    doc.addField("lastmodified", lastmodified);
    doc.addField("recommendedprice", NumberUtil.round(recommendedprice));
    doc.addField("product_category_id", product_category_id);
    doc.addField("product_name", product_name);
    if (!StringUtils.isBlank(product_name)) {
      doc.addField("product_name_fl", product_name_fl);
      doc.addField("product_name_fl_sort", product_name_fl_sort);
      doc.addField("product_name_py", product_name_py);
    }
    doc.addField("product_brand", product_brand);
    if (!StringUtils.isBlank(product_brand)) {
      doc.addField("product_brand_fl", product_brand_fl);
      doc.addField("product_brand_fl_sort", product_brand_fl_sort);
      doc.addField("product_brand_py", product_brand_py);
    }
    doc.addField("product_model", product_model);
    if (!StringUtils.isBlank(product_model)) {
      doc.addField("product_model_fl", product_model_fl);
      doc.addField("product_model_fl_sort", product_model_fl_sort);
      doc.addField("product_model_py", product_model_py);
    }
    doc.addField("product_spec", product_spec);
    if (!StringUtils.isBlank(product_spec)) {
      doc.addField("product_spec_fl", product_spec_fl);
      doc.addField("product_spec_fl_sort", product_spec_fl_sort);
      doc.addField("product_spec_py", product_spec_py);
    }
    doc.addField("product_vehicle_brand", product_vehicle_brand);
    if (!StringUtils.isBlank(product_vehicle_brand)) {
      doc.addField("product_vehicle_brand_fl", product_vehicle_brand_fl);
      doc.addField("product_vehicle_brand_fl_sort", product_vehicle_brand_fl_sort);
      doc.addField("product_vehicle_brand_py", product_vehicle_brand_py);
    }
    doc.addField("product_vehicle_model", product_vehicle_model);
    if (!StringUtils.isBlank(product_vehicle_model)) {
      doc.addField("product_vehicle_model_fl", product_vehicle_model_fl);
      doc.addField("product_vehicle_model_fl_sort", product_vehicle_model_fl_sort);
      doc.addField("product_vehicle_model_py", product_vehicle_model_py);
    }

    doc.addField("product_name_simple", product_name);
    doc.addField("product_brand_simple", product_brand);
    doc.addField("product_model_simple", product_model);
    doc.addField("product_spec_simple", product_spec);
    doc.addField("product_vehicle_brand_simple", product_vehicle_brand);
    doc.addField("product_vehicle_model_simple", product_vehicle_model);

    if (!StringUtils.isBlank(product_kind)) {
      doc.addField("product_kind_fl_sort", product_kind_fl_sort);
    }
    doc.addField("product_vehicle_year", product_vehicle_year);
    doc.addField("product_vehicle_engine", product_vehicle_engine);
    doc.addField("product_storage_unit", product_storage_unit);
    doc.addField("product_sell_unit", product_sell_unit);
    if (product_rate != null && product_rate.longValue() != 0l) {
      doc.addField("product_rate", product_rate);
    }
    if(CollectionUtils.isNotEmpty(supplier_info)){
      for(String supplierInfo:supplier_info){
        if(StringUtils.isNotBlank(supplierInfo)){
          doc.addField("supplier_info",supplierInfo);
        }
      }
    }

    if (CollectionUtils.isNotEmpty(supplier_id)) {
      for (Long supplierId : supplier_id) {
        if (supplierId != null) {
          doc.addField("supplier_id", supplierId);
        }
      }
    }
    if (CollectionUtils.isNotEmpty(supplier_detail)) {
      for (String supplierDetail : supplier_detail) {
        if (StringUtils.isNotBlank(supplierDetail)) {
          doc.addField("supplier_detail", supplierDetail);
        }
      }
    }
    if(inventory_price != null){
      doc.addField("inventory_price",inventory_price);
    }
    if (StringUtils.isNotBlank(storage_bin)) {
      doc.addField("storage_bin", storage_bin);
    }
    doc.addField("trade_price",NumberUtil.round(trade_price));
    doc.addField("product_vehicle_status", product_vehicle_status);
    if(StringUtils.isNotBlank(commodity_code)){
      doc.addField("commodity_code",commodity_code);
    }
    doc.addField("product_status",product_status);
    if (inventory_average_price != null) {
      doc.addField("inventory_average_price", inventory_average_price);
    }
    doc.addField("product_kind",product_kind);
    if (CollectionUtils.isNotEmpty(wholesaler_shop_id)) {
      for (String wholesalerShopId : wholesaler_shop_id) {
        if (StringUtils.isNotBlank(wholesalerShopId)) {
          doc.addField("wholesaler_shop_id", wholesalerShopId);
        }
      }
    }

    if(MapUtils.isNotEmpty(dynamicStorehouseInventoryAmountFieldData)){
      for (Map.Entry<String, Double> entry : dynamicStorehouseInventoryAmountFieldData.entrySet()) {
        String key = entry.getKey();
        doc.addField("storehouse_id", key);
        doc.addField(key+dynamicStorehouseInventoryAmountFieldName, entry.getValue());
      }
    }
    if(MapUtils.isNotEmpty(dynamicStorehouseInventoryPriceFieldData)){
      for (Map.Entry<String, Double> entry : dynamicStorehouseInventoryPriceFieldData.entrySet()) {
        doc.addField(entry.getKey()+dynamicStorehouseInventoryPriceFieldName, entry.getValue());
      }
    }
    doc.addField("normal_product_id",normal_product_id);
    doc.addField("relevance_status",relevance_status);

    doc.addField("sales_status",sales_status);
    doc.addField("last_in_sales_time",last_in_sales_time);
    in_sales_amount=in_sales_amount==-1D? ProductConstants.IN_SALES_AMOUNT_AVAILABLE:in_sales_amount;
    doc.addField("in_sales_amount",NumberUtil.round(in_sales_amount));
    doc.addField("in_sales_price",NumberUtil.round(in_sales_price));
    doc.addField("in_sales_unit",in_sales_unit);
    doc.addField("guarantee_period",guarantee_period);


    doc.addField("last_30_sales",last_30_sales);

    if (CollectionUtils.isNotEmpty(promotionsIdList)) {
      for (String pId : promotionsIdList) {
        if (pId != null) {
          doc.addField("promotions_id", pId);
        }
      }
      for (String type : promotionsTypeList) {
        if (type != null) {
          doc.addField("promotions_type", type);
        }
      }
      for (PromotionsDTO promotionsDTO : promotionsDTOs) {
        if (promotionsDTO == null) {
          continue;
        }
        doc.addField("promotions_info", JsonUtil.objectToJson(promotionsDTO));
        doc.addField("promotions_name", promotionsDTO.getName());
        doc.addField("promotions_type_status", promotionsDTO.getType().toString()+"_"+promotionsDTO.getStatus().toString());
      }
    }else {
      doc.addField("promotions_id", "");
      doc.addField("promotions_type", "NONE");
      doc.addField("promotions_info","");
    }

    doc.addField("lower_limit", NumberUtil.doubleVal(lower_limit));
    doc.addField("upper_limit", NumberUtil.doubleVal(upper_limit));

    doc.addField("custom_match_p_content", custom_match_p_content);
    doc.addField("custom_match_pv_content", custom_match_pv_content);
    return doc;
  }

  private void generatePingyin() {
    PingyinInfo pingyinInfo = null;
    if (!StringUtils.isBlank(this.product_spec)) {
      pingyinInfo = PinyinUtil.getPingyinInfo(this.product_spec);
      this.product_spec_fl = pingyinInfo.firstLetters;
      this.product_spec_fl_sort = pingyinInfo.firstLetter;
      this.product_spec_py = pingyinInfo.pingyin;
    }
    if (!StringUtils.isBlank(this.product_name)) {
      pingyinInfo = PinyinUtil.getPingyinInfo(this.product_name);
      this.product_name_fl = pingyinInfo.firstLetters;
      this.product_name_fl_sort = pingyinInfo.firstLetter;
      this.product_name_py = pingyinInfo.pingyin;
    }
    if (!StringUtils.isBlank(this.product_model)) {
      pingyinInfo = PinyinUtil.getPingyinInfo(this.product_model);
      this.product_model_fl = pingyinInfo.firstLetters;
      this.product_model_fl_sort = pingyinInfo.firstLetter;
      this.product_model_py = pingyinInfo.pingyin;
    }
    if (!StringUtils.isBlank(this.product_brand)) {
      pingyinInfo = PinyinUtil.getPingyinInfo(this.product_brand);
      this.product_brand_fl = pingyinInfo.firstLetters;
      this.product_brand_fl_sort = pingyinInfo.firstLetter;
      this.product_brand_py = pingyinInfo.pingyin;
    }
    if (!StringUtils.isBlank(this.product_vehicle_brand)) {
      pingyinInfo = PinyinUtil.getPingyinInfo(this.product_vehicle_brand);
      this.product_vehicle_brand_fl = pingyinInfo.firstLetters;
      this.product_vehicle_brand_fl_sort = pingyinInfo.firstLetter;
      this.product_vehicle_brand_py = pingyinInfo.pingyin;
    }
    if (!StringUtils.isBlank(this.product_vehicle_model)) {
      pingyinInfo = PinyinUtil.getPingyinInfo(this.product_vehicle_model);
      this.product_vehicle_model_fl = pingyinInfo.firstLetters;
      this.product_vehicle_model_fl_sort = pingyinInfo.firstLetter;
      this.product_vehicle_model_py = pingyinInfo.pingyin;
    }
    if (!StringUtils.isBlank(this.product_kind)) {
      pingyinInfo = PinyinUtil.getPingyinInfo(this.product_kind);
      this.product_kind_fl_sort = pingyinInfo.firstLetter;
    }
  }

  public Double getLast_30_sales() {
    return last_30_sales;
  }

  public void setLast_30_sales(Double last_30_sales) {
    this.last_30_sales = last_30_sales;
  }

  public Long getProduct_category_id() {
    return product_category_id;
  }

  public void setProduct_category_id(Long product_category_id) {
    this.product_category_id = product_category_id;
  }

  public String getShop_area_info() {
    return shop_area_info;
  }

  public void setShop_area_info(String shop_area_info) {
    this.shop_area_info = shop_area_info;
  }

  public String getShop_name() {
    return shop_name;
  }

  public void setShop_name(String shop_name) {
    this.shop_name = shop_name;
  }

  public String getShop_kind() {
    return shop_kind;
  }

  public void setShop_kind(String shop_kind) {
    this.shop_kind = shop_kind;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getProduct_brand() {
    return product_brand;
  }

  public void setProduct_brand(String product_brand) {
    this.product_brand = product_brand;
  }

  public String getProduct_model() {
    return product_model;
  }
  public void setProduct_model(String product_model) {
    this.product_model = product_model;
  }

  public String getProduct_spec() {
    return product_spec;
  }

  public void setProduct_spec(String product_spec) {
    this.product_spec = product_spec;
  }

  public String getProduct_name() {
    return product_name;
  }

  public void setProduct_name(String product_name) {
    this.product_name = product_name;
  }

  public String getProduct_vehicle_brand() {
    return product_vehicle_brand;
  }

  public void setProduct_vehicle_brand(String product_vehicle_brand) {
    this.product_vehicle_brand = product_vehicle_brand;
  }

  public String getProduct_vehicle_model() {
    return product_vehicle_model;
  }

  public void setProduct_vehicle_model(String product_vehicle_model) {
    this.product_vehicle_model = product_vehicle_model;
  }

  public String getProduct_vehicle_year() {
    return product_vehicle_year;
  }

  public void setProduct_vehicle_year(String product_vehicle_year) {
    this.product_vehicle_year = product_vehicle_year;
  }

  public String getProduct_vehicle_engine() {
    return product_vehicle_engine;
  }

  public void setProduct_vehicle_engine(String product_vehicle_engine) {
    this.product_vehicle_engine = product_vehicle_engine;
  }

  public Integer getProduct_vehicle_status() {
    return product_vehicle_status;
  }

  public void setProduct_vehicle_status(Integer product_vehicle_status) {
    this.product_vehicle_status = product_vehicle_status;
  }

  public String getFirst_letter() {
    return first_letter;
  }

  public void setFirst_letter(String first_letter) {
    this.first_letter = first_letter;
  }

  public String getFirst_letter_combination() {
    return first_letter_combination;
  }

  public void setFirst_letter_combination(String first_letter_combination) {
    this.first_letter_combination = first_letter_combination;
  }

  public String getProduct_name_fl() {
    return product_name_fl;
  }

  public void setProduct_name_fl(String product_name_fl) {
    this.product_name_fl = product_name_fl;
  }

  public String getProduct_brand_fl() {
    return product_brand_fl;
  }

  public void setProduct_brand_fl(String product_brand_fl) {
    this.product_brand_fl = product_brand_fl;
  }

  public String getProduct_spec_fl() {
    return product_spec_fl;
  }

  public void setProduct_spec_fl(String product_spec_fl) {
    this.product_spec_fl = product_spec_fl;
  }

  public String getProduct_model_fl() {
    return product_model_fl;
  }

  public void setProduct_model_fl(String product_model_fl) {
    this.product_model_fl = product_model_fl;
  }

  public String getProduct_vehicle_brand_fl() {
    return product_vehicle_brand_fl;
  }

  public void setProduct_vehicle_brand_fl(String product_vehicle_brand_fl) {
    this.product_vehicle_brand_fl = product_vehicle_brand_fl;
  }

  public String getProduct_vehicle_model_fl() {
    return product_vehicle_model_fl;
  }

  public void setProduct_vehicle_model_fl(String product_vehicle_model_fl) {
    this.product_vehicle_model_fl = product_vehicle_model_fl;
  }

  public String getProduct_name_py() {
    return product_name_py;
  }

  public void setProduct_name_py(String product_name_py) {
    this.product_name_py = product_name_py;
  }

  public String getProduct_brand_py() {
    return product_brand_py;
  }

  public void setProduct_brand_py(String product_brand_py) {
    this.product_brand_py = product_brand_py;
  }

  public String getProduct_spec_py() {
    return product_spec_py;
  }

  public void setProduct_spec_py(String product_spec_py) {
    this.product_spec_py = product_spec_py;
  }

  public String getProduct_model_py() {
    return product_model_py;
  }

  public void setProduct_model_py(String product_model_py) {
    this.product_model_py = product_model_py;
  }

  public String getProduct_vehicle_brand_py() {
    return product_vehicle_brand_py;
  }

  public void setProduct_vehicle_brand_py(String product_vehicle_brand_py) {
    this.product_vehicle_brand_py = product_vehicle_brand_py;
  }

  public String getProduct_vehicle_model_py() {
    return product_vehicle_model_py;
  }

  public void setProduct_vehicle_model_py(String product_vehicle_model_py) {
    this.product_vehicle_model_py = product_vehicle_model_py;
  }

  public Double getInventory_amount() {
    return inventory_amount;
  }

  public void setInventory_amount(Double inventory_amount) {
    this.inventory_amount = inventory_amount;
  }

  public Long getStorage_time() {
    return storage_time;
  }

  public void setStorage_time(Long storage_time) {
    this.storage_time = storage_time;
  }

  public Double getPurchase_price() {
    return purchase_price;
  }

  public void setPurchase_price(Double purchase_price) {
    this.purchase_price = purchase_price;
  }

  public Boolean getBasicData() {
    return isBasicData;
  }

  public void setBasicData(Boolean basicData) {
    isBasicData = basicData;
  }

  public Long getShop_id() {
    return shop_id;
  }

  public void setShop_id(Long shop_id) {
    this.shop_id = shop_id;
  }

  public Long getProduct_id() {
    return product_id;
  }

  public void setProduct_id(Long product_id) {
    this.product_id = product_id;
  }

  public Double getRecommendedprice() {
    return recommendedprice;
  }

  public void setRecommendedprice(Double recommendedprice) {
    this.recommendedprice = recommendedprice;
  }

  public Long getLastmodified() {
    return lastmodified;
  }

  public void setLastmodified(Long lastmodified) {
    this.lastmodified = lastmodified;
  }

  public String getProduct_storage_unit() {
    return product_storage_unit;
  }

  public void setProduct_storage_unit(String product_storage_unit) {
    this.product_storage_unit = product_storage_unit;
  }

  public String getProduct_sell_unit() {
    return product_sell_unit;
  }

  public void setProduct_sell_unit(String product_sell_unit) {
    this.product_sell_unit = product_sell_unit;
  }

  public Long getProduct_rate() {
    return product_rate;
  }

  public void setProduct_rate(Long product_rate) {
    this.product_rate = product_rate;
  }

  public List<String> getSupplier_info() {
    return supplier_info;
  }

  public void setSupplier_info(List<String> supplier_info) {
    this.supplier_info = supplier_info;
  }

  public List<Long> getSupplier_id() {
    return supplier_id;
  }

  public void setSupplier_id(List<Long> supplier_id) {
    this.supplier_id = supplier_id;
  }

  public Double getInventory_price() {
    return inventory_price;
  }

  public void setInventory_price(Double inventory_price) {
    this.inventory_price = inventory_price;
  }

//	public void setProductSupplierInfo(List<ProductSupplierDTO> productSupplierDTOs) {
//		if(CollectionUtils.isEmpty(productSupplierDTOs)){
//			return;
//		}
//		if(this.getSupplier_info()==null){
//			this.supplier_info = new ArrayList<String>();
//		}
//		if(this.getSupplier_detail() == null){
//			this.supplier_detail = new ArrayList<String>();
//		}
//		for(ProductSupplierDTO productSupplierDTO:productSupplierDTOs){
//			if(productSupplierDTO == null){
//				continue;
//			}
//			this.supplier_info.add(productSupplierDTO.toSupplierInfoStr());
//			this.supplier_detail.add(productSupplierDTO.generateProductSupplierDetail());
//		}
//	}

  public void setProductSupplierInfo(List<SupplierInventoryDTO> supplierInventoryDTOs) {
    if(CollectionUtils.isEmpty(supplierInventoryDTOs)){
      return;
    }
    if(this.getSupplier_info()==null){
      this.supplier_info = new ArrayList<String>();
    }
    if(this.getSupplier_detail() == null){
      this.supplier_detail = new ArrayList<String>();
    }
    if (this.supplier_id == null) {
      this.supplier_id = new ArrayList<Long>();
    }
    for(SupplierInventoryDTO supplierInventoryDTO:supplierInventoryDTOs){
      if(supplierInventoryDTO == null){
        continue;
      }
      this.supplier_info.add(supplierInventoryDTO.toSupplierInfoStr());
      this.supplier_detail.add(supplierInventoryDTO.generateProductSupplierDetail());
      if(supplierInventoryDTO.getSupplierId() != null){
        this.supplier_id.add(supplierInventoryDTO.getSupplierId());
      }
    }
  }


//	public void setProductSupplierIds(List<ProductSupplierDTO> productSupplierDTOs) {
//		if (CollectionUtils.isEmpty(productSupplierDTOs)) {
//			return;
//		}
//		if (this.supplier_id == null) {
//			this.supplier_id = new ArrayList<Long>();
//		}
//		for (ProductSupplierDTO productSupplierDTO : productSupplierDTOs) {
//			if (productSupplierDTO == null||productSupplierDTO.getSupplierId()==null) {
//				continue;
//			}
//			this.supplier_id.add(productSupplierDTO.getSupplierId());
//		}
//	}

  public String getProduct_name_fl_sort() {
    return product_name_fl_sort;
  }

  public void setProduct_name_fl_sort(String product_name_fl_sort) {
    this.product_name_fl_sort = product_name_fl_sort;
  }

  public String getProduct_brand_fl_sort() {
    return product_brand_fl_sort;
  }

  public void setProduct_brand_fl_sort(String product_brand_fl_sort) {
    this.product_brand_fl_sort = product_brand_fl_sort;
  }

  public String getProduct_spec_fl_sort() {
    return product_spec_fl_sort;
  }

  public void setProduct_spec_fl_sort(String product_spec_fl_sort) {
    this.product_spec_fl_sort = product_spec_fl_sort;
  }

  public String getProduct_model_fl_sort() {
    return product_model_fl_sort;
  }

  public void setProduct_model_fl_sort(String product_model_fl_sort) {
    this.product_model_fl_sort = product_model_fl_sort;
  }

  public String getProduct_vehicle_brand_fl_sort() {
    return product_vehicle_brand_fl_sort;
  }

  public void setProduct_vehicle_brand_fl_sort(String product_vehicle_brand_fl_sort) {
    this.product_vehicle_brand_fl_sort = product_vehicle_brand_fl_sort;
  }

  public String getProduct_vehicle_model_fl_sort() {
    return product_vehicle_model_fl_sort;
  }

  public void setProduct_vehicle_model_fl_sort(String product_vehicle_model_fl_sort) {
    this.product_vehicle_model_fl_sort = product_vehicle_model_fl_sort;
  }

  public String getStorage_bin() {
    return storage_bin;
  }

  public void setStorage_bin(String storage_bin) {
    this.storage_bin = storage_bin;
  }

  public Double getTrade_price() {
    return trade_price;
  }

  public void setTrade_price(Double trade_price) {
    this.trade_price = trade_price;
  }

  public String getCommodity_code() {
    return commodity_code;
  }

  public void setCommodity_code(String commodity_code) {
    this.commodity_code = commodity_code;
  }

  public Double getInventory_average_price() {
    return inventory_average_price;
  }

  public void setInventory_average_price(Double inventory_average_price) {
    this.inventory_average_price = inventory_average_price;
  }

  public String getProduct_status() {
    return product_status;
  }

  public void setProduct_status(String product_status) {
    this.product_status = product_status;
  }

  public List<String> getSupplier_detail() {
    return supplier_detail;
  }

  public void setSupplier_detail(List<String> supplier_detail) {
    this.supplier_detail = supplier_detail;
  }

  public String getProduct_kind() {
    return product_kind;
  }

  public void setProduct_kind(String product_kind) {
    this.product_kind = product_kind;
  }

  public List<String> getWholesaler_shop_id() {
    return wholesaler_shop_id;
  }

  public void setWholesaler_shop_id(List<String> wholesaler_shop_id) {
    this.wholesaler_shop_id = wholesaler_shop_id;
  }


  public Map<String, Double> getDynamicStorehouseInventoryAmountFieldData() {
    return dynamicStorehouseInventoryAmountFieldData;
  }

  public void setDynamicStorehouseInventoryAmountFieldData(Map<String, Double> dynamicStorehouseInventoryAmountFieldData) {
    this.dynamicStorehouseInventoryAmountFieldData = dynamicStorehouseInventoryAmountFieldData;
  }

  public Map<String, Double> getDynamicStorehouseInventoryPriceFieldData() {
    return dynamicStorehouseInventoryPriceFieldData;
  }

  public void setDynamicStorehouseInventoryPriceFieldData(Map<String, Double> dynamicStorehouseInventoryPriceFieldData) {
    this.dynamicStorehouseInventoryPriceFieldData = dynamicStorehouseInventoryPriceFieldData;
  }

  public Long getNormal_product_id() {
    return normal_product_id;
  }

  public void setNormal_product_id(Long normal_product_id) {
    this.normal_product_id = normal_product_id;
  }

  public String getRelevance_status() {
    return relevance_status;
  }

  public void setRelevance_status(String relevance_status) {
    this.relevance_status = relevance_status;
  }

  public String getSales_status() {
    return sales_status;
  }

  public void setSales_status(String sales_status) {
    this.sales_status = sales_status;
  }

  public Long getLast_in_sales_time() {
    return last_in_sales_time;
  }

  public void setLast_in_sales_time(Long last_in_sales_time) {
    this.last_in_sales_time = last_in_sales_time;
  }

  public Double getIn_sales_amount() {
    return in_sales_amount;
  }

  public void setIn_sales_amount(Double in_sales_amount) {
    this.in_sales_amount = in_sales_amount;
  }

  public Double getIn_sales_price() {
    return in_sales_price;
  }

  public void setIn_sales_price(Double in_sales_price) {
    this.in_sales_price = in_sales_price;
  }

  public String getIn_sales_unit() {
    return in_sales_unit;
  }

  public void setIn_sales_unit(String in_sales_unit) {
    this.in_sales_unit = in_sales_unit;
  }

  public String getGuarantee_period() {
    return guarantee_period;
  }

  public void setGuarantee_period(String guarantee_period) {
    this.guarantee_period = guarantee_period;
  }

  public List<PromotionsDTO> getPromotionsDTOs() {
    return promotionsDTOs;
  }

  public void setPromotionsDTOs(List<PromotionsDTO> promotionsDTOs) {
    this.promotionsDTOs = promotionsDTOs;
  }

  public List<String> getPromotionsTypeList() {
    return promotionsTypeList;
  }

  public void setPromotionsTypeList(List<String> promotionsTypeList) {
    this.promotionsTypeList = promotionsTypeList;
  }

  public List<String> getPromotionsIdList() {
    return promotionsIdList;
  }

  public void setPromotionsIdList(List<String> promotionsIdList) {
    this.promotionsIdList = promotionsIdList;
  }

  public List<Long> getShop_area_ids() {
    return shop_area_ids;
  }

  public void setShop_area_ids(List<Long> shop_area_ids) {
    this.shop_area_ids = shop_area_ids;
  }

  public Double getLower_limit() {
    return lower_limit;
  }

  public void setLower_limit(Double lower_limit) {
    this.lower_limit = lower_limit;
  }

  public Double getUpper_limit() {
    return upper_limit;
  }

  public void setUpper_limit(Double upper_limit) {
    this.upper_limit = upper_limit;
  }

  public void setShopInfo(ShopDTO shopDTO,AreaDTO areaDTO){
    if(shopDTO!=null){
      this.setShop_name(shopDTO.getName());
      this.setShop_kind(shopDTO.getShopKind()==null?"":shopDTO.getShopKind().toString());

      List<Long> shopAreaIdList = new ArrayList<Long>();
      shopAreaIdList.add(shopDTO.getProvince());
      shopAreaIdList.add(shopDTO.getCity());
      shopAreaIdList.add(shopDTO.getRegion());
      this.setShop_area_ids(shopAreaIdList);
      this.setShop_area_info(areaDTO==null?"":areaDTO.getFullName());
    }

  }
}
