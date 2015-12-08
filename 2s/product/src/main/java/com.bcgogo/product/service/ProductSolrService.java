package com.bcgogo.product.service;

import com.bcgogo.enums.ProductStatus;
import com.bcgogo.product.dto.PingyinInfo;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.enums.Product.ProductRelevanceStatus;
import com.bcgogo.product.model.Product;
import com.bcgogo.product.model.ProductDaoManager;
import com.bcgogo.product.model.ProductWriter;
import com.bcgogo.search.client.SolrClientHelper;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.VehicleDTO;
import com.bcgogo.utils.PinyinUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 处理产品模块SOLR相关逻辑
 * Created by IntelliJ IDEA.
 * User: wjl
 * Date: 12-3-8
 * Time: 下午5:26
 * To change this template use File | Settings | File Templates.
 */
@Component
public class ProductSolrService implements IProductSolrService {
  private static final Logger LOG = LoggerFactory.getLogger(ProductSolrService.class);

  @Autowired
  private ProductDaoManager productDaoManager;

  /**
   * 将产品加入SOLR
   *
   * @param productDTOList
   * @param isBasicData 是否为标准库产品
   * @throws Exception
   * @author wjl
   */
  @Override
  public void addProductForSolr(List<ProductDTO> productDTOList, boolean isBasicData) throws Exception {
    long begin = System.currentTimeMillis();
    LOG.debug("AOP_SOLR start:ProductSolrService:addProductForSolr");
    if (CollectionUtils.isEmpty(productDTOList)) return;
    Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
    PingyinInfo pingyinInfo = null;
    for (ProductDTO productDTO : productDTOList) {
      SolrInputDocument doc = new SolrInputDocument();
      doc.addField("id", productDTO.getId());
      doc.addField("product_status", productDTO.getStatus()==null? ProductStatus.ENABLED.toString():productDTO.getStatus().toString());
      doc.addField("product_id", productDTO.getProductLocalInfoId());  //to add productLocalInfoId
      doc.addField("isBasicData", isBasicData);
      doc.addField("first_letter", productDTO.getFirstLetter());
      doc.addField("first_letter_combination", productDTO.getFirstLetterCombination());
      doc.addField("inventory_amount", productDTO.getInventoryNum());
      doc.addField("storage_time", productDTO.getEditDate());
      doc.addField("purchase_price", productDTO.getPurchasePrice());
      doc.addField("shop_id", productDTO.getShopId() == null ? 1L : productDTO.getShopId());
      if (!StringUtils.isBlank(productDTO.getSpec())) {
        doc.addField("product_spec", productDTO.getSpec());
        pingyinInfo = PinyinUtil.getPingyinInfo(productDTO.getSpec());
        doc.addField("product_spec_fl", pingyinInfo.firstLetters);
        doc.addField("product_spec_py", pingyinInfo.pingyin);
      }
      if (!StringUtils.isBlank(productDTO.getName())) {
        doc.addField("product_name", productDTO.getName());
        pingyinInfo = PinyinUtil.getPingyinInfo(productDTO.getName());
        doc.addField("product_name_fl", pingyinInfo.firstLetters);
        doc.addField("product_name_py", pingyinInfo.pingyin);
      }
      if (!StringUtils.isBlank(productDTO.getModel())) {
        doc.addField("product_model", productDTO.getModel());
        pingyinInfo = PinyinUtil.getPingyinInfo(productDTO.getModel());
        doc.addField("product_model_fl", pingyinInfo.firstLetters);
        doc.addField("product_model_py", pingyinInfo.pingyin);
      }
      if (!StringUtils.isBlank(productDTO.getBrand())) {
        doc.addField("product_brand", productDTO.getBrand());
        pingyinInfo = PinyinUtil.getPingyinInfo(productDTO.getBrand());
        doc.addField("product_brand_fl", pingyinInfo.firstLetters);
        doc.addField("product_brand_py", pingyinInfo.pingyin);
      }
      if (!StringUtils.isBlank(productDTO.getProductVehicleBrand())) {
        doc.addField("product_vehicle_brand", productDTO.getProductVehicleBrand());
        pingyinInfo = PinyinUtil.getPingyinInfo(productDTO.getProductVehicleBrand());
        doc.addField("product_vehicle_brand_fl", pingyinInfo.firstLetters);
        doc.addField("product_vehicle_brand_py", pingyinInfo.pingyin);
      }
      if (!StringUtils.isBlank(productDTO.getProductVehicleModel())) {
        doc.addField("product_vehicle_model", productDTO.getProductVehicleModel());
        pingyinInfo = PinyinUtil.getPingyinInfo(productDTO.getProductVehicleModel());
        doc.addField("product_vehicle_model_fl", pingyinInfo.firstLetters);
        doc.addField("product_vehicle_model_py", pingyinInfo.pingyin);
      }

      doc.addField("product_vehicle_status", productDTO.getProductVehicleStatus());


      if (StringUtils.isNotBlank(productDTO.getStorageUnit())) {
        doc.addField("product_storage_unit", productDTO.getStorageUnit());
      }
      if (StringUtils.isNotBlank(productDTO.getSellUnit())) {
        doc.addField("product_sell_unit", productDTO.getSellUnit());
      }
      if (productDTO.getRate() != null && productDTO.getRate() != 0L) {
        doc.addField("product_rate", productDTO.getRate());
      }
	    if(productDTO.getTradePrice()!=null){
		    doc.addField("trade_price",productDTO.getTradePrice());
	    }
	    if(StringUtils.isNotBlank(productDTO.getStorageBin())){
		    doc.addField("storage_bin",productDTO.getStorageBin());
	    }
      if(null != productDTO.getNormalProductId())
      {
        doc.addField("normal_product_id",productDTO.getStorageBin());
      }
      doc.addField("relevance_status",productDTO.getRelevanceStatus()==null?ProductRelevanceStatus.NO.toString():productDTO.getRelevanceStatus().toString());
      docs.add(doc);
    }
    SolrClientHelper.getProductSolrClient().addDocs(docs);
    LOG.debug("AOP_SOLR end:ProductSolrService:addProductForSolr 用时：{}ms",System.currentTimeMillis() - begin);
  }

  /**
   * 将车型加入SOLR
   *
   * @param vehicleDTOList 需要加入SOLR的车型集合
   * @throws Exception
   * @author wjl
   */
  @Override
  public void addVehicleForSearch(List<VehicleDTO> vehicleDTOList) throws Exception {
    long begin = System.currentTimeMillis();
    LOG.debug("AOP_SOLR start:ProductSolrService:addVehicleForSearch");
    Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
    if (CollectionUtils.isNotEmpty(vehicleDTOList)) {
      for (VehicleDTO vehicleDTO : vehicleDTOList) {
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("doc_type", SolrClientHelper.BcgogoSolrDocumentType.VEHICLE_PROPERTY_SUGGESTION_DOC_TYPE.getValue());
        doc.addField("id", "s_"+vehicleDTO.getId());
        doc.addField("brand", "".equals(vehicleDTO.getBrand()) ? null : vehicleDTO.getBrand());
        doc.addField("model", "".equals(vehicleDTO.getModel()) ? null : vehicleDTO.getModel());
        doc.addField("year", "".equals(vehicleDTO.getYear()) ? null : vehicleDTO.getYear());
        doc.addField("engine", "".equals(vehicleDTO.getEngine()) ? null : vehicleDTO.getEngine());

        doc.addField("pv_brand_id", vehicleDTO.getVirtualBrandId());
        doc.addField("pv_model_id", vehicleDTO.getVirtualModelId());
        doc.addField("pv_year_id", vehicleDTO.getVirtualYearId());
        doc.addField("pv_engine_id", vehicleDTO.getVirtualEngineId());

        doc.addField("brand_first_letter", vehicleDTO.getBrandPinYin());
        doc.addField("model_first_letter", vehicleDTO.getModelPinYin());

        doc.addField("contact", vehicleDTO.getContact());
        doc.addField("mobile", vehicleDTO.getMobile());
        docs.add(doc);
      }
      SolrClientHelper.getVehicleSolrClient().addDocs(docs);
    }
    LOG.debug("AOP_SOLR end:ProductSolrService:addVehicleForSearch 用时：{}ms",System.currentTimeMillis() - begin);
  }

  /**
   * 将数据重新reindex
   *
   * @param shopId
   * @throws Exception
   */
  @Override
  public void reindexProductForSolr(Long shopId) throws Exception {
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    StringBuffer query = new StringBuffer("product_name:*");
    if (shopId != null)
      query.append(" AND shop_id:").append(shopId);
    else
      query.append(" AND (shop_id:{1 TO *} OR shop_id:0)");
    //删除solr中shop信息
    searchService.deleteByQuery(query.toString(), "product");
    List<ProductDTO> productDTOList = new ArrayList<ProductDTO>();
    Long start = 0l;
    int num = 2000;
    ProductWriter writer = productDaoManager.getWriter();
    while (true) {
      List<Product> products = writer.getProductsByShopId(shopId, start, num);
      int size = products.size();
      if (size == 0) break;
      for (int i = 0; i < size; i++) {
        ProductDTO productDTO = products.get(i).toDTO();
        start = productDTO.getId();
        productDTOList.add(productDTO);
      }
      boolean isBasic = (shopId != null && shopId == 1L);
      ServiceManager.getService(IProductSolrService.class).addProductForSolr(productDTOList, isBasic);
      productDTOList.clear();
    }
  }

  @Override
  public void reindexVehicleLetter() throws Exception {
    SolrClientHelper.getVehicleSolrClient().deleteByQuery("doc_type:" + SolrClientHelper.BcgogoSolrDocumentType.VEHICLE_PROPERTY_SUGGESTION_DOC_TYPE.getValue());

    IBaseProductService baseProductService = ServiceManager.getService(IBaseProductService.class);
    List<VehicleDTO> vehicleDTOList = baseProductService.getVehicleDTOListByBrand();
    this.addVehicleForSearch(vehicleDTOList);

    vehicleDTOList = baseProductService.getVehicleDTOListByModel();
    this.addVehicleForSearch(vehicleDTOList);

    vehicleDTOList = baseProductService.getVehicleDTOListByYear();
    this.addVehicleForSearch(vehicleDTOList);

    vehicleDTOList = baseProductService.getVehicleDTOListByEngine();
    this.addVehicleForSearch(vehicleDTOList);

  }

}
