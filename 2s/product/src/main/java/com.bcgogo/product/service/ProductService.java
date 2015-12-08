package com.bcgogo.product.service;

import com.bcgogo.common.Pager;
import com.bcgogo.common.Result;
import com.bcgogo.common.Sort;
import com.bcgogo.common.TwoTuple;
import com.bcgogo.config.dto.AreaDTO;
import com.bcgogo.config.dto.RecentlyUsedDataDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.ConfigService;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IRecentlyUsedDataService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.constant.NormalProductConstants;
import com.bcgogo.constant.autoaccessoryonline.SupplyDemandConstant;
import com.bcgogo.enums.DeletedType;
import com.bcgogo.enums.KindStatus;
import com.bcgogo.enums.*;
import com.bcgogo.enums.Product.ProductRelevanceStatus;
import com.bcgogo.enums.config.RecentlyUsedDataType;
import com.bcgogo.enums.config.VehicleSelectBrandModel;
import com.bcgogo.enums.shop.ShopKind;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.product.NormalProductModifyRecordDTO;
import com.bcgogo.product.NormalProductModifyScene;
import com.bcgogo.product.ProductCategory.ProductCategoryDTO;
import com.bcgogo.product.StandardBrandModelCache.StandardBrandModelCache;
import com.bcgogo.product.cache.ProductUnitCache;
import com.bcgogo.product.dto.*;
import com.bcgogo.product.model.*;
import com.bcgogo.product.productManage.ProductSearchCondition;
import com.bcgogo.product.standardVehicleBrandModel.NormalProductVehicleBrandModelDTO;
import com.bcgogo.product.standardVehicleBrandModel.ShopVehicleBrandModelDTO;
import com.bcgogo.product.standardVehicleBrandModel.StandardVehicleBrandDTO;
import com.bcgogo.product.standardVehicleBrandModel.StandardVehicleModelDTO;
import com.bcgogo.search.dto.InventorySearchIndexDTO;
import com.bcgogo.search.dto.SearchConditionDTO;
import com.bcgogo.search.model.InventorySearchIndex;
import com.bcgogo.search.model.SearchDaoManager;
import com.bcgogo.search.model.SearchWriter;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.*;
import com.bcgogo.user.dto.*;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: wjl
 * Date: 11-9-23
 * Time: 下午3:28
 */
@Component
public class ProductService implements IProductService {


  private static final Logger LOG = LoggerFactory.getLogger(ProductService.class);
  @Autowired
  private ProductDaoManager productDaoManager;

  private IBaseProductService baseProductService;

  public IBaseProductService getBaseProductService() {
    if (baseProductService == null) {
      baseProductService = ServiceManager.getService(IBaseProductService.class);
    }
    return baseProductService;
  }

  @Override
  public KindDTO createKind(KindDTO kindDTO) {
    ProductWriter writer = productDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Kind kind = new Kind();
      kind.setCategoryId(kindDTO.getCategoryId());
      kind.setMemo(kindDTO.getMemo());
      kind.setName(kindDTO.getName());
      kind.setNameEn(kindDTO.getNameEn());
      kind.setState(kindDTO.getState());
      kind.setShopId(kindDTO.getShopId());
      kind.setStatus(KindStatus.ENABLE.toString());

      writer.save(kind);
      writer.commit(status);

      kindDTO.setId(kind.getId());
      return kindDTO;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public BrandDTO getBrand(Long brandId) {
    ProductWriter writer = productDaoManager.getWriter();
    Brand brand = writer.getBrand(brandId);
    if (brand != null) {
      BrandDTO brandDTO = new BrandDTO();
      brandDTO.setFirstLetter(brand.getFirstLetter());
      brandDTO.setMemo(brand.getMemo());
      brandDTO.setName(brand.getName());
      brandDTO.setNameEn(brand.getNameEn());
      brandDTO.setShopId(brand.getShopId());
      brandDTO.setState(brand.getState());
      return brandDTO;
    }
    return null;
  }

  @Override
  public BrandDTO createBrand(BrandDTO brandDTO) {
    ProductWriter writer = productDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Brand brand = new Brand();
      brand.setFirstLetter(brandDTO.getFirstLetter());
      brand.setMemo(brandDTO.getMemo());
      brand.setName(brandDTO.getName());
      brand.setNameEn(brandDTO.getNameEn());
      brand.setState(brandDTO.getState());
      brand.setShopId(brandDTO.getShopId());

      writer.save(brand);
      writer.commit(status);

      brandDTO.setId(brand.getId());
      return brandDTO;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public ModelDTO createModel(ModelDTO modelDTO) {
    ProductWriter writer = productDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Model model = new Model();
      model.setBrandId(modelDTO.getBrandId());
      model.setFirstLetter(modelDTO.getFirstLetter());
      model.setMemo(modelDTO.getMemo());
      model.setNameEn(modelDTO.getNameEn());
      model.setMfrId(modelDTO.getMfrId());
      model.setName(modelDTO.getName());
      model.setShopId(modelDTO.getShopId());
      model.setState(modelDTO.getState());

      writer.save(model);
      writer.commit(status);

      modelDTO.setId(model.getId());
      return modelDTO;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public ModelDTO getModel(Long modelId) {
    ProductWriter writer = productDaoManager.getWriter();
    Model model = writer.getModel(modelId);
    if (model != null) {
      ModelDTO modelDTO = new ModelDTO();
      modelDTO.setBrandId(model.getBrandId());
      modelDTO.setFirstLetter(model.getFirstLetter());
      modelDTO.setMemo(model.getMemo());
      modelDTO.setMfrId(model.getMfrId());
      modelDTO.setName(model.getName());
      modelDTO.setNameEn(model.getNameEn());
      modelDTO.setShopId(model.getShopId());
      modelDTO.setState(model.getState());
      return modelDTO;
    }
    return null;
  }


  @Override
  public ProductDTO createProduct(ProductDTO productDTO) {
    ProductWriter writer = productDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Product product = new Product();
      product.setBrand(productDTO.getBrand());
      product.setKindId(productDTO.getKindId());
      product.setMemo(productDTO.getMemo());
      product.setMfr(productDTO.getMfr());
      product.setMfrEn(productDTO.getMfrEn());
      product.setModel(productDTO.getModel());
      product.setName(productDTO.getName());
      product.setNameEn(productDTO.getNameEn());
      product.setProductVehicleStatus(productDTO.getProductVehicleStatus());
      product.setProductVehicleBrand(productDTO.getProductVehicleBrand());
      product.setOrigin(productDTO.getOrigin());
      product.setOriginNo(productDTO.getOriginNo());
      product.setShopId(productDTO.getShopId());
      product.setSpec(productDTO.getSpec());
      product.setCheckStatus(productDTO.getCheckStatus());
      product.setParentId(productDTO.getParentId());
      product.setState(productDTO.getState());
      product.setUnit(productDTO.getUnit());
      product.setFirstLetter(productDTO.getFirstLetter());
      product.setFirstLetterCombination(productDTO.getFirstLetterCombination());
      product.setBarcode(productDTO.getBarcode());
      writer.save(product);

      writer.commit(status);
      productDTO.setId(product.getId());

      return productDTO;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public Product getProductById(Long productId) {
    return productDaoManager.getWriter().getById(Product.class, productId);
  }

  @Override
  public List<ProductLocalInfoDTO> getShopAdProductLocalInfoDTO(Long... shopIds){
    if(ArrayUtil.isEmpty(shopIds)){
      return null;
    }
    ProductWriter writer = productDaoManager.getWriter();
    List<ProductLocalInfo> localInfos= writer.getShopAdProductLocalInfo(shopIds);
    List<ProductLocalInfoDTO> localInfoDTOs=new ArrayList<ProductLocalInfoDTO>();
    if(CollectionUtil.isNotEmpty(localInfos)){
      for(ProductLocalInfo localInfo:localInfos){
        localInfoDTOs.add(localInfo.toDTO());
      }
    }
    return localInfoDTOs;
  }

  @Override
  public List<ProductLocalInfoDTO> getLastInSalesProductLocalInfo(Long shopId,int maxRows){
    if(shopId==null){
      return null;
    }
    ProductWriter writer = productDaoManager.getWriter();
    List<ProductLocalInfo> localInfos= writer.getLastInSalesProductLocalInfo(shopId,maxRows);
    List<ProductLocalInfoDTO> localInfoDTOs=new ArrayList<ProductLocalInfoDTO>();
    if(CollectionUtil.isNotEmpty(localInfos)){
      for(ProductLocalInfo localInfo:localInfos){
        localInfoDTOs.add(localInfo.toDTO());
      }
    }
    return localInfoDTOs;
  }

  @Override
  public List<ProductDTO> getSimpleProductDTOListById(Long... id) {
    if(ArrayUtils.isEmpty(id)) return null;
    ProductWriter writer = productDaoManager.getWriter();
    List<Product> productList = writer.getSimpleProductListById(id);
    if(CollectionUtils.isNotEmpty(productList)){
      List<ProductDTO> productDTOList = new ArrayList<ProductDTO>();
      for(Product product:productList){
        productDTOList.add(product.toDTO());
      }
      return productDTOList;
    }
    return null;
  }

  @Override
  public ProductDTO getProductByProductLocalInfoId(Long productLocalInfoId, Long shopId) throws Exception {
    ProductWriter writer = productDaoManager.getWriter();
    if (productLocalInfoId == null || shopId == null) {
      return null;
    }
    Object[] objects = writer.getProductByProductLocalInfoId(productLocalInfoId,shopId);
    if (objects != null && objects[0] != null && objects[1] != null) {
      Product product = (Product) objects[0];
      ProductLocalInfo productLocalInfo = (ProductLocalInfo) objects[1];
      ProductDTO productDTO = product.toDTO();
      productDTO.setProductLocalInfoDTO(productLocalInfo.toDTO());
      productDTO.setLastModified(product.getLastModified()>productLocalInfo.getLastModified()?product.getLastModified():productLocalInfo.getLastModified());
      if (product.getKindId() != null) {
        Kind kind = writer.getById(Kind.class, product.getKindId());
        productDTO.setKindName(kind != null ?kind.getName() : null);
      }
      return productDTO;
    }
    return null;
  }

   @Override
   public Map<Long,ProductDTO> getProductDTOMapByProductLocalInfoIds(Long shopId,Long... productLocalInfoId) throws Exception {
     Map<Long,ProductDTO> productDTOMap=new HashMap<Long, ProductDTO>();
     if(ArrayUtil.isEmpty(productLocalInfoId)) return productDTOMap;
     List<ProductDTO> productDTOs= getProductDTOByProductLocalInfoIds(shopId,productLocalInfoId);
     if(CollectionUtil.isNotEmpty(productDTOs)){
       for (ProductDTO productDTO:productDTOs){
         productDTOMap.put(productDTO.getProductLocalInfoId(),productDTO);
       }
     }
     return productDTOMap;
   }

  @Override
  public List<ProductDTO> getProductDTOByProductLocalInfoIds(Long shopId,Long... productLocalInfoId) throws Exception {
    if (ArrayUtil.isEmpty(productLocalInfoId)|| shopId == null) {
      return null;
    }
    ProductWriter writer = productDaoManager.getWriter();
    List<ProductDTO> productDTOs=new ArrayList<ProductDTO>();
    List<Object[]> objectsList = writer.getProductByProductLocalInfoId(shopId, productLocalInfoId);
    if(CollectionUtil.isNotEmpty(objectsList)){
      for(Object[] objects:objectsList){
        if (objects != null && objects[0] != null && objects[1] != null){
          Product product = (Product) objects[0];
          ProductLocalInfo productLocalInfo = (ProductLocalInfo) objects[1];
          ProductDTO productDTO = product.toDTO();
          productDTO.setProductLocalInfoDTO(productLocalInfo.toDTO());
          if (product.getKindId() != null) {
            Kind kind = writer.getById(Kind.class, product.getKindId());
            productDTO.setKindName(kind != null ?kind.getName() : null);
          }
          productDTOs.add(productDTO);
        }
      }
    }
    return productDTOs;
  }

  @Override
  public List<ProductDTO> getProductDTOByIds(Long... productLocalInfoIds) {
    if (ArrayUtil.isEmpty(productLocalInfoIds)) {
      return null;
    }
    ProductWriter writer = productDaoManager.getWriter();
    List<ProductDTO> productDTOs=new ArrayList<ProductDTO>();
    List<Object[]> objectsList = writer.getProductByProductLocalInfoId(null,productLocalInfoIds);
    if(CollectionUtil.isNotEmpty(objectsList)){
      for(Object[] objects:objectsList){
        if (objects != null && objects[0] != null && objects[1] != null){
          Product product = (Product) objects[0];
          ProductLocalInfo productLocalInfo = (ProductLocalInfo) objects[1];
          ProductDTO productDTO = product.toDTO();
          productDTO.setProductLocalInfoDTO(productLocalInfo.toDTO());
          productDTOs.add(productDTO);
        }
      }
    }
    return productDTOs;
  }

  @Override
  public ProductDTO getProductById(Long productId, Long shopId) throws Exception {
    ProductWriter writer = productDaoManager.getWriter();
    Product product = writer.getProductById(productId, shopId);
    if (product != null) {
      ProductDTO productDTO = null;
      List<ProductLocalInfo> productLocalInfos = writer.getProductLocalInfoByProductId(product.getId(), shopId);
      productDTO = product.toDTO();
      if (product.getKindId() != null) {
        Kind kind = writer.getById(Kind.class, product.getKindId());
        productDTO.setKindName(kind == null ? "" : kind.getName());
      }
      if (productLocalInfos != null && !productLocalInfos.isEmpty()) {
        productDTO.setStorageUnit(productLocalInfos.get(0).getStorageUnit());
        productDTO.setSellUnit(productLocalInfos.get(0).getSellUnit());
        productDTO.setRate(productLocalInfos.get(0).getRate());
        productDTO.setPurchasePrice(productLocalInfos.get(0).getPurchasePrice());
        productDTO.setProductLocalInfoId(productLocalInfos.get(0).getId());
        productDTO.setTradePrice(productLocalInfos.get(0).getTradePrice());
        productDTO.setStorageBin(productLocalInfos.get(0).getStorageBin());
        productDTO.setBusinessCategoryId(productLocalInfos.get(0).getBusinessCategoryId());
      }

      return productDTO;
    }
    return null;
  }

  @Override
  public ProductVehicleDTO createProductVehicle(ProductVehicleDTO productVehicleDTO) {
    ProductWriter writer = productDaoManager.getWriter();
    Object status = writer.begin();
    ProductVehicle productVehicle = null;
    try {
      List<ProductVehicle> list = writer.getProductVehicleByVehicleIds(productVehicleDTO.getProductId(),
          productVehicleDTO.getBrandId(), productVehicleDTO.getModelId(), productVehicleDTO.getYearId(),
          productVehicleDTO.getEngineId());
      if (list != null && list.size() > 0) {
        productVehicle = list.get(0);
      } else {
        productVehicle = new ProductVehicle();
        productVehicle.setBrandId(productVehicleDTO.getBrandId());
        productVehicle.setMfrId(productVehicleDTO.getMfrId());
        productVehicle.setModelId(productVehicleDTO.getModelId());
        productVehicle.setTrimId(productVehicleDTO.getTrimId());
        productVehicle.setProductId(productVehicleDTO.getProductId());
        productVehicle.setShopId(productVehicleDTO.getShopId());
        productVehicle.setYearId(productVehicleDTO.getYearId());
        productVehicle.setEngineId(productVehicleDTO.getEngineId());

        writer.save(productVehicle);
        writer.commit(status);
      }
      productVehicleDTO.setId(productVehicle.getId());
      return productVehicleDTO;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public ProductVehicleDTO getProductVehicle(Long productVehicleId) {
    ProductWriter writer = productDaoManager.getWriter();
    ProductVehicle productVehicle = writer.getById(ProductVehicle.class, productVehicleId);
    if (productVehicle != null) {
      ProductVehicleDTO productVehicleDTO = new ProductVehicleDTO();
      productVehicleDTO.setBrandId(productVehicle.getBrandId());
      productVehicleDTO.setMfrId(productVehicle.getMfrId());
      productVehicleDTO.setModelId(productVehicle.getModelId());
      productVehicleDTO.setProductId(productVehicle.getProductId());
      productVehicleDTO.setShopId(productVehicle.getShopId());
      productVehicleDTO.setTrimId(productVehicle.getTrimId());
      productVehicleDTO.setYearId(productVehicle.getYearId());

      return productVehicleDTO;
    }
    return null;
  }

  @Override
  public TemplateDTO createTemplate(TemplateDTO templateDTO) {
    ProductWriter writer = productDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Template template = new Template();
      template.setMemo(templateDTO.getMemo());
      template.setKindId(templateDTO.getKindId());
      template.setName(templateDTO.getName());
      template.setShopId(templateDTO.getShopId());
      template.setState(templateDTO.getState());
      template.setTemplate(templateDTO.getTemplate());
      template.setType(templateDTO.getType());
      template.setVer(templateDTO.getVer());

      writer.save(template);
      writer.commit(status);

      templateDTO.setId(template.getId());
      return templateDTO;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public TemplateDTO getTemplate(Long templateId) {
    ProductWriter writer = productDaoManager.getWriter();
    Template template = writer.getTemplate(templateId);
    if (template != null) {
      TemplateDTO templateDTO = new TemplateDTO();
      templateDTO.setMemo(template.getMemo());
      templateDTO.setKindId(template.getKindId());
      templateDTO.setName(template.getName());
      templateDTO.setTemplate(template.getTemplate());
      templateDTO.setShopId(template.getShopId());
      templateDTO.setState(template.getState());
      templateDTO.setType(template.getType());
      templateDTO.setVer(template.getVer());

      return templateDTO;
    }
    return null;
  }

  @Override
  public EngineDTO getEngine(Long engineId) {
    ProductWriter writer = productDaoManager.getWriter();
    if (engineId == null) {
      return null;
    }
    Engine engine = writer.getById(Engine.class, engineId);
    if (engine != null) {
      EngineDTO engineDTO = new EngineDTO();
      engineDTO.setBrandId(engine.getBrandId());
      engineDTO.setMemo(engine.getMemo());
      engineDTO.setMfrId(engine.getMfrId());
      engineDTO.setModelId(engine.getModelId());
      engineDTO.setEngine(engine.getEngine());
      engineDTO.setShopId(engine.getShopId());
      engineDTO.setState(engine.getState());
      engineDTO.setYearId(engine.getYearId());

      return engineDTO;
    }
    return null;
  }

  @Override
  public YearDTO createYear(YearDTO yearDTO) {
    ProductWriter writer = productDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Year year = new Year();
      year.setBrandId(yearDTO.getBrandId());
      year.setMemo(yearDTO.getMemo());
      year.setMfrId(yearDTO.getMfrId());
      year.setModelId(yearDTO.getModelId());
      year.setShopId(yearDTO.getShopId());
      year.setState(yearDTO.getState());
      year.setYear(yearDTO.getYear());

      writer.save(year);
      writer.commit(status);

      yearDTO.setId(year.getId());
      return yearDTO;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public YearDTO getYear(Long yearId) {
    ProductWriter writer = productDaoManager.getWriter();
    Year year = writer.getYear(yearId);
    if (year != null) {
      YearDTO yearDTO = new YearDTO();
      yearDTO.setBrandId(year.getBrandId());
      yearDTO.setMemo(year.getMemo());
      yearDTO.setMfrId(year.getMfrId());
      yearDTO.setModelId(year.getModelId());
      yearDTO.setShopId(year.getShopId());
      yearDTO.setState(year.getState());
      yearDTO.setYear(year.getYear());

      return yearDTO;
    }
    return null;
  }


  @Override
  public ProductLocalInfoDTO getProductLocalInfoByProductId(Long productId, Long shopId) throws Exception {
    if (productId == null) {
      return null;
    }
    ProductWriter writer = productDaoManager.getWriter();
    try {
      List<ProductLocalInfo> productLocalInfoList = writer.getProductLocalInfoByProductId(productId, shopId);
      if (productLocalInfoList != null && productLocalInfoList.size() > 0) {
        ProductLocalInfo productLocalInfo = productLocalInfoList.get(0);
        if (productLocalInfo != null) {
          return productLocalInfo.toDTO();
        }
      }
    } catch (Exception e) {
      LOG.debug(e.getMessage(), e);
    }
    return null;
  }

  @Override
  public <T> String getJsonWithList(List<T> list) {
    if (list.size() == 0 || list == null) {
      return "[]";
    }
    if (list.get(0) == null) {
      return "[]";
    }
    Field[] fields = list.get(0).getClass().getDeclaredFields();
    String[] fieldName = new String[fields.length];
    String[][] fieldValue = new String[list.size()][fields.length];
    for (int t = 0; t < fields.length; t++) {
      fieldName[t] = fields[t].getName();
    }
    for (int z = 0; z < list.size(); z++) {
      for (int w = 0; w < fields.length; w++) {
        if (null != list.get(z)) {
          fieldValue[z][w] = getReadMethodValue(fieldName[w], list.get(z));
        }
      }
    }
    StringBuffer sb = new StringBuffer();
    sb.append("[");
    for (int j = 0; j < list.size(); j++) {
      sb.append("{");
      for (int h = 0; h < fieldName.length; h++) {
        sb.append("\'" + fieldName[h] + "\':\'" + fieldValue[j][h] + "\',");
        if (h == fieldName.length - 1) {
          sb.delete(sb.length() - 1, sb.length());
        }
      }
      sb.append("},");
      if (j == list.size() - 1) {
        sb.delete(sb.length() - 1, sb.length());
      }
    }
    sb.append("]");
    return sb.toString();
  }

  public <T> String getReadMethodValue(String fieldName, T x) {
    try {
      PropertyDescriptor pd = new PropertyDescriptor(fieldName, x.getClass());
      Method method = pd.getReadMethod();
      if (method.invoke(x) == null) return "";
      return method.invoke(x).toString();
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return "";
    }
  }

  @Override
  public List getModelWithFirstLetter(String firstLetter, Long brandId) {
    ProductWriter writer = productDaoManager.getWriter();
    List modelList = writer.getModelWithFirstLetter(firstLetter, brandId);
    if (modelList != null) {
      Iterator iterator = modelList.iterator();
      List modelDTOList = new ArrayList();
      while (iterator.hasNext()) {
        ModelDTO modelDTO = new ModelDTO();
        Model model = (Model) iterator.next();
        modelDTO.setBrandId(model.getBrandId());
        modelDTO.setFirstLetter(model.getFirstLetter());
        modelDTO.setName(model.getName());
        modelDTO.setId(model.getId());
        modelDTOList.add(modelDTO);
      }
      return modelDTOList;
    }
    return null;
  }

  /**
   * 根据汉字字符串查询首字母组合
   *
   * @param productName
   * @return
   */
  public String getFirstLetterAndCombination(String productName) {
    try {
      int strLength = productName.length();
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < strLength; i++) {
        char c = productName.charAt(i);
        if (c >= 0x0391 && c <= 0xFFE5) {
          sb.append(getProductFirstLetterByWord(c + ""));
        } else if (c >= 0x0000 && c <= 0x00FF) {
          sb.append((c + "").toLowerCase());
        }
      }
      return sb.toString();
    } catch (Exception e) {
      return "";
    }

  }

  @Override
  public List<BrandDTO> getBrandByKeyword(String searchWord) {
    ProductWriter writer = productDaoManager.getWriter();
    List brandList = writer.getBrandByKeyword(searchWord);
    if (brandList != null) {
      Iterator iterator = brandList.iterator();
      List<BrandDTO> brandDTOList = new ArrayList();
      while (iterator.hasNext()) {
        BrandDTO brandDTO = new BrandDTO();
        Brand brand = (Brand) iterator.next();
        brandDTO.setId(brand.getId());
        brandDTO.setFirstLetter(brand.getFirstLetter());
        brandDTO.setMemo(brand.getMemo());
        brandDTO.setName(brand.getName());
        brandDTO.setNameEn(brand.getNameEn());
        brandDTO.setShopId(brand.getShopId());
        brandDTO.setState(brand.getState());

        brandDTOList.add(brandDTO);
      }
      return brandDTOList;
    }
    return null;
  }

  @Override
  public List getBrandWithFirstLetter(String firstLetter) {
    ProductWriter writer = productDaoManager.getWriter();
    List brandList = writer.getBrandWithFirstLetter(firstLetter);
    if (brandList != null) {
      Iterator iterator = brandList.iterator();
      List brandDTOList = new ArrayList();
      while (iterator.hasNext()) {
        BrandDTO brandDTO = new BrandDTO();
        Brand brand = (Brand) iterator.next();
        brandDTO.setFirstLetter(brand.getFirstLetter().toUpperCase());
        brandDTO.setMemo(brand.getMemo());
        brandDTO.setName(brand.getName());
        brandDTO.setNameEn(brand.getNameEn());
        brandDTO.setShopId(brand.getShopId());
        brandDTO.setState(brand.getState());
        brandDTO.setId(brand.getId());
        brandDTOList.add(brandDTO);
      }
      return brandDTOList;
    }
    return null;
  }

  @Override
  public String getProductFirstLetterByWord(String chnChar) {
    ProductWriter writer = productDaoManager.getWriter();
    List<ProductDTO> productDTOList = writer.getProductFirstLetterByWord(chnChar);
    String firstLetter = productDTOList != null && productDTOList.size() > 0 ? productDTOList.get(0).getFirstLetter() : "";
    return firstLetter;
  }

  @Override
  public List<ProductVehicleDTO> getProductVehicleByProductIdReturnList(Long productId) {
    ProductWriter writer = productDaoManager.getWriter();
    List<ProductVehicle> pvList = writer.getProductVehicleByProductId(productId);
    if (pvList != null && pvList.size() > 0) {
      List<ProductVehicleDTO> pvDTOList = new ArrayList<ProductVehicleDTO>();
      for (ProductVehicle pv : pvList) {
        ProductVehicleDTO pvd = new ProductVehicleDTO();
        pvd.setId(pv.getId());
        pvd.setProductId(pv.getProductId());
        pvd.setShopId(pv.getShopId());
        pvd.setBrandId(pv.getBrandId());
        pvd.setModelId(pv.getModelId());
        pvd.setYearId(pv.getYearId());
        pvd.setEngineId(pv.getEngineId());
        pvDTOList.add(pvd);
      }
      return pvDTOList;
    }
    return new ArrayList<ProductVehicleDTO>();
  }

  @Override
  public ProductLocalInfoDTO getProductLocalInfoById(Long productLocalInfoId, Long shopId) {
    ProductWriter writer = productDaoManager.getWriter();
    if (productLocalInfoId == null) return null;
    if (shopId == null) {
      LOG.error("shop Id null");
      return null;
    }
    ProductLocalInfo productLocalInfo = writer.getById(ProductLocalInfo.class, productLocalInfoId);
    if (productLocalInfo != null) {
      return productLocalInfo.toDTO();
    }
    return null;
  }

  @Override
  public BrandDTO getBrandByName(String name) {
    ProductWriter writer = productDaoManager.getWriter();
    Brand brand = writer.getBrandByName(name);
    if (brand != null) {
      BrandDTO brandDTO = new BrandDTO();
      brandDTO.setFirstLetter(brand.getFirstLetter());
      brandDTO.setMemo(brand.getMemo());
      brandDTO.setName(brand.getName());
      brandDTO.setNameEn(brand.getNameEn());
      brandDTO.setShopId(brand.getShopId());
      brandDTO.setState(brand.getState());
      brandDTO.setId(brand.getId());
      return brandDTO;
    }
    return null;
  }

  @Override
  public ModelDTO getModelByName(Long brandId, String name) {
    ProductWriter writer = productDaoManager.getWriter();
    Model model = writer.getModelByName(brandId, name);
    if (model != null) {
      ModelDTO modelDTO = new ModelDTO();
      modelDTO.setBrandId(model.getBrandId());
      modelDTO.setFirstLetter(model.getFirstLetter());
      modelDTO.setMemo(model.getMemo());
      modelDTO.setMfrId(model.getMfrId());
      modelDTO.setName(model.getName());
      modelDTO.setNameEn(model.getNameEn());
      modelDTO.setShopId(model.getShopId());
      modelDTO.setState(model.getState());
      modelDTO.setId(model.getId());
      return modelDTO;
    }
    return null;
  }

  @Override
  public YearDTO getYearByNameAndOtherId(Integer year, Long modelId, Long brandId) {
    ProductWriter writer = productDaoManager.getWriter();
    Year y = writer.getYearByName(year, modelId, brandId);
    if (y != null) {
      YearDTO yearDTO = new YearDTO();
      yearDTO.setBrandId(y.getBrandId());
      yearDTO.setModelId(y.getModelId());
      yearDTO.setYear(y.getYear());
      yearDTO.setMemo(y.getMemo());
      yearDTO.setShopId(y.getShopId());
      yearDTO.setId(y.getId());
      return yearDTO;
    }
    return null;
  }

  @Override
  public EngineDTO getEngineByName(String engine, Long yearId, Long modelId, Long brandId) {
    ProductWriter writer = productDaoManager.getWriter();
    Engine e = writer.getEngineByName(engine, yearId, modelId, brandId);
    if (e != null) {
      EngineDTO engineDTO = new EngineDTO();
      engineDTO.setBrandId(e.getBrandId());
      engineDTO.setModelId(e.getModelId());
      engineDTO.setYearId(e.getYearId());
      engineDTO.setEngine(e.getEngine());
      engineDTO.setState(e.getState());
      engineDTO.setMemo(e.getMemo());
      engineDTO.setId(e.getId());
      return engineDTO;
    }
    return null;
  }


  /**
   * @param shopId
   * @param name
   * @param readType readType:"0"--导入车型数据，“1”--导入产品数据，"2"--导入车型与产品规格对应表数据
   */

  public void readFormFile(Long shopId, String name, String readType) {
    IConfigService configService = ServiceManager.getService(ConfigService.class);
    String FileUrl = configService.getConfig(name, shopId);
    try {
      BufferedReader in = new BufferedReader(
          new InputStreamReader(new FileInputStream(FileUrl), "UTF8"));
      String strLine;
      Set<String[]> dataSet = new HashSet<String[]>();
      while ((strLine = in.readLine()) != null) {
        dataSet.add(strLine.split(",", -1));
      }
      if ("0".equals(readType)) {
        insertVehicleData(dataSet);
      } else if ("1".equals(readType)) {
        insertProductData(dataSet);
      } else if ("2".equals(readType)) {
        insertProductVehicleData(dataSet);
      } else if ("3".equals(readType)) {  //插入车牌
        insertLicenseplateData(dataSet);
      } else if ("4".equals(readType)) {
        insertArea(dataSet);
      } else {
        LOG.error("readType参数值不正确:readType:\"0\"--导入车型数据，\"1\"--导入产品数据，" +
            "\"2\"--导入车型与产品规格对应表数据,\"3\"--车牌前缀表数据,\"4\"--地区表数据");
      }
      in.close();

    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }

  @Override
  public void readFormFile(InputStream fis, String readType) {
    try {
      InputStreamReader isr = new InputStreamReader(fis, "GBK");
      BufferedReader br = new BufferedReader(isr);

      String strLine;
      Set<String[]> dataSet = new HashSet<String[]>();
      while ((strLine = br.readLine()) != null) {
        dataSet.add(strLine.split(",", -1));
      }
      br.close();
      isr.close();
      fis.close();

      switch (Integer.parseInt(readType)) {
        case SolrConstant.UPLOAD_VEHICLE:
          insertVehicleData(dataSet);
          break;
        case SolrConstant.UPLOAD_PRODUCT:
          insertProductData(dataSet);
          break;
        case SolrConstant.UPLOAD_PRODUCT_VEHICLE:
          insertProductVehicleData(dataSet);
          break;
        case SolrConstant.UPLOAD_LICENSE_PREFIX:
          insertLicenseplateData(dataSet);
          break;
        case SolrConstant.UPLOAD_REGION:
          insertArea(dataSet);
          break;
        default:
          LOG.error("readType参数值不正确:readType:\"0\"--导入车型数据，\"1\"--导入产品数据，\"2\"--" +
              "导入车型与产品规格对应表数据,\"3\"--车牌前缀表数据,\"4\"--地区表数据");
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }

  private void insertVehicleData(Set dataSet) throws Exception {
    StringBuffer sb = null;
    List<String[]> dataList = new ArrayList<String[]>(dataSet);
    int
        listSize = dataList.size(),
        maxCommit = 100,              //每次提交数
        commitNum = (listSize % maxCommit == 0 ? listSize / maxCommit : listSize / maxCommit + 1);

    for (int i = 0; i < commitNum; i++) {
      List<VehicleDTO> vdList = new ArrayList();
      int x = (i + 1 == commitNum ? listSize : (i + 1) * maxCommit);
      for (int j = i * maxCommit; j < x; j++) {
        String[] strs = dataList.get(j);
        sb = checkDataFormat(strs, 4, sb);
        if (null != sb) continue;

        VehicleDTO vehicleDTO = ServiceManager.getService(IBaseProductService.class).
            addVehicleToDB(strs[0], strs[1], strs[2], strs[3]);
        vdList.add(vehicleDTO);
      }
      ServiceManager.getService(IProductSolrService.class).addVehicleForSearch(vdList);
    }

    if (sb != null) LOG.error(sb.toString());
  }

  //车牌前缀
  private void insertLicenseplateData(Set dataSet) {
    Iterator iterator = dataSet.iterator();
    ProductWriter writer = productDaoManager.getWriter();
    StringBuffer sb = null;
    Object status = writer.begin();

    try {
      while (iterator.hasNext()) {
        String[] strs = (String[]) iterator.next();
        sb = checkDataFormat(strs, 4, sb);
        if (null != sb) continue;

        Licenseplate licenseplate = new Licenseplate();
        licenseplate.setCarno(strs[0]);
        licenseplate.setAreaName(strs[1]);
        licenseplate.setAreaFirstname(strs[2]);
        licenseplate.setAreaFirstcarno(strs[3]);
        writer.save(licenseplate);

      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
      if (sb != null) LOG.error(sb.toString());
    }
  }

  //地区表上传
  public void insertArea(Set dateSet) throws Exception {
    ServiceManager.getService(ConfigService.class).insertArea(dateSet);
  }

  private void insertProductData(Set dataSet) throws Exception {
    ProductWriter writer = productDaoManager.getWriter();
    StringBuffer sb = null;
    List<String[]> dataList = new ArrayList<String[]>(dataSet);
    int
        listSize = dataList.size(),
        maxCommit = 100,
        commitNum = (listSize % maxCommit == 0 ? listSize / maxCommit : listSize / maxCommit + 1);

    for (int i = 0; i < commitNum; i++) {
      List<ProductDTO> productDTOList = new ArrayList<ProductDTO>();
      int x = (i + 1 == commitNum ? listSize : (i + 1) * maxCommit);
      for (int j = i * maxCommit; j < x; j++) {
        String[] strs = dataList.get(j);
        sb = checkDataFormat(strs, 4, sb);
        if (null != sb) continue;

        List<Product> productList = writer.getProductByProductInfo(strs[0], strs[1], strs[2], strs[3]);
        if (productList == null || productList.size() <= 0) {
          ProductDTO pDTO = ServiceManager.getService(IBaseProductService.class).addProductToDB(1L, strs[0], strs[1], strs[2], strs[3]);
          productDTOList.add(pDTO);
        }
      }
      ServiceManager.getService(IProductSolrService.class).addProductForSolr(productDTOList, true);
    }
    if (sb != null) LOG.error(sb.toString());
  }

  private void insertProductVehicleData(Set dataSet) throws Exception {
    ProductWriter writer = productDaoManager.getWriter();
    Object status = null;
    Map<String, HashSet> vehicleSpecMap = new HashMap<String, HashSet>();
    StringBuffer sb = null;

    try {
      Iterator iterator = dataSet.iterator();
      while (iterator.hasNext()) {
        String[] strs = (String[]) iterator.next();
        sb = checkDataFormat(strs, 5, sb);
        if (null != sb || "".equals(strs[4])) continue;

        HashSet<String[]> valSet = vehicleSpecMap.get(strs[4]);
        if (valSet == null) {
          HashSet<String[]> valueSet = new HashSet();
          valueSet.add(strs);
          vehicleSpecMap.put(strs[4], valueSet);
        } else {
          valSet.add(strs);
          vehicleSpecMap.put(strs[4], valSet);
        }
      }
      if (sb != null) LOG.error(sb.toString());

      int pCount = writer.getProductCount().intValue();
      int pRows = 50;
      int pSize = pCount / pRows;
      if (pCount % pRows > 0) {
        pSize++;
      }
      for (int io = 1; io <= pSize; io++) {
        status = writer.begin();
        List<Product> productList = writer.getAllProductByRows(io, pRows);
        List<ProductDTO> productDTOList = new ArrayList<ProductDTO>();
        for (Product product : productList) {
          if (product.getSpec() == null || "".equals(product.getSpec())) {
            continue;
          }
          ProductDTO productDTO = new ProductDTO();
          productDTO.setId(product.getId());
          productDTO.setFirstLetter(product.getFirstLetter());
          productDTO.setFirstLetterCombination(product.getFirstLetterCombination());
          productDTO.setShopId(product.getShopId());
          productDTO.setBrand(product.getBrand());
          productDTO.setModel(product.getModel());
          productDTO.setSpec(product.getSpec());
          productDTO.setName(product.getName());

          HashSet<String[]> pvSet = vehicleSpecMap.get(product.getSpec());
          if (pvSet == null || pvSet.size() <= 0) {
            continue;
          }
          List<ProductVehicleDTO> pvdList = new ArrayList();
          for (String[] strs : pvSet) {
            ProductVehicleDTO productVehicleDTO = new ProductVehicleDTO();
            BrandDTO brandDTO = getBrandByName(strs[0]);
            Long brandid = brandDTO != null ? brandDTO.getId() : null;
            ModelDTO modelDTO = getModelByName(brandid, strs[1]);
            Long modelid = modelDTO != null ? modelDTO.getId() : null;
            Integer year = "".equals(strs[2]) ? null : Integer.parseInt(strs[2]);
            YearDTO yearDTO = getYearByNameAndOtherId(year, modelid, brandid);
            Long yearid = yearDTO != null ? yearDTO.getId() : null;
            EngineDTO engineDTO = getEngineByName(strs[3], yearid, modelid, brandid);
            Long engineid = engineDTO != null ? engineDTO.getId() : null;
            if (brandid == null && modelid == null && yearid == null && engineid == null) continue;
            List pvlist = writer.getProductVehicleByVehicleIds(product.getId(), brandid, modelid, yearid, engineid);
            if (pvlist != null && pvlist.size() > 0) {
              continue;
            }
            productVehicleDTO.setProductId(product.getId());
            productVehicleDTO.setBrandId(brandid);
            productVehicleDTO.setModelId(modelid);
            productVehicleDTO.setYearId(yearid);
            productVehicleDTO.setEngineId(engineid);
            productVehicleDTO.setShopId(1L);
            productVehicleDTO.setPvBrand(strs[0]);
            productVehicleDTO.setPvModel(strs[1]);
            productVehicleDTO.setPvYear(strs[2]);
            productVehicleDTO.setPvEngine(strs[3]);
            createProductVehicle(productVehicleDTO);
            pvdList.add(productVehicleDTO);
          }
          if (pvdList != null && pvdList.size() == 1) {

            productDTO.setProductVehicleStatus(SearchConstant.PRODUCT_PRODUCTSTATUS_SPECIAL);
            productDTO.setProductVehicleBrand(pvdList.get(0).getPvBrand());
            productDTO.setProductVehicleModel(pvdList.get(0).getPvModel());
            productDTO.setProductVehicleYear(pvdList.get(0).getPvYear());
            productDTO.setProductVehicleEngine(pvdList.get(0).getPvEngine());

            product.setProductVehicleStatus(SearchConstant.PRODUCT_PRODUCTSTATUS_SPECIAL);
            product.setProductVehicleBrand(pvdList.get(0).getPvBrand());
            product.setProductVehicleModel(pvdList.get(0).getPvModel());
            product.setProductVehicleYear(pvdList.get(0).getPvYear());
            product.setProductVehicleEngine(pvdList.get(0).getPvEngine());
          } else if (pvdList != null && pvdList.size() > 1) {
            productDTO.setProductVehicleStatus(SearchConstant.PRODUCT_PRODUCTSTATUS_MULTIPLE);
            productDTO.setProductVehicleBrand("多款");

            product.setProductVehicleStatus(SearchConstant.PRODUCT_PRODUCTSTATUS_MULTIPLE);
            product.setProductVehicleBrand("多款");

          }
          writer.update(product);
          productDTOList.add(productDTO);
        }
        writer.commit(status);
        ServiceManager.getService(IProductSolrService.class).addProductForSolr(productDTOList, true);
      }
    } finally {
      writer.rollback(status);
    }
  }

  public void updateAllVehicleFirstLetter() {
    ProductWriter writer = productDaoManager.getWriter();
    Object status = writer.begin();
    try {
      List<Brand> brandList = writer.getAllBrand();
      for (Brand b : brandList) {
        String fl = b.getFirstLetter();
        b.setFirstLetter(fl.toLowerCase());
        writer.update(b);
      }
      List<Model> modelList = writer.getAllModel();
      for (Model m : modelList) {
        String fl = m.getFirstLetter();
        m.setFirstLetter(fl.toLowerCase());
        writer.update(m);
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }


  //根据shopid查找area
  public String getAreaByShopId(Long shopId) {
    ProductWriter writer = productDaoManager.getWriter();
    List<AreaDTO> areaDTOList = writer.getAreaByShopId(shopId);
    String areaName = areaDTOList != null && areaDTOList.size() > 0 ? areaDTOList.get(0).getName() : "";
    return areaName;
  }

  // 根据地区ID 找到地区名称
  public String getCarNoByareaById(String areaid) {
    ProductWriter writer = productDaoManager.getWriter();
    List<AreaDTO> AreaDTOList = writer.getCarNoByareaById(areaid);
    String name = AreaDTOList != null && AreaDTOList.size() > 0 ? AreaDTOList.get(0).getName() : "";
    return name;
  }

  //根据地区名称找到车牌
  public String getCarNoByAreaName(String area) {
    ProductWriter writer = productDaoManager.getWriter();
    List<LicenseplateDTO> carNoDTOList = writer.getCarNoByAreaName(area);
    String carNo = carNoDTOList != null && carNoDTOList.size() > 0 ? carNoDTOList.get(0).getCarno() : "";
    return carNo;
  }

  public String getCarNoByAreaNo(Long areaNo) {
    ProductWriter writer = productDaoManager.getWriter();
    Licenseplate licenseplate = writer.getCarNoByAreaNo(areaNo);
    return licenseplate != null ? licenseplate.getCarno() : "";
  }

  //根据车牌首汉字查询车牌列表
  public List getCarNosByFirstLetters(String carnoFirstLetter) {
    ProductWriter writer = productDaoManager.getWriter();
    List<LicenseplateDTO> carNoDTOList = writer.getCarNosByFirstLetters(carnoFirstLetter);
    if (carNoDTOList == null) {
      return null;
    } else {
      List<String> areaFirstcarnos = new ArrayList<String>();
      for (int i = 0; i < carNoDTOList.size(); i++) {
        String areaFirstcarno = carNoDTOList.get(i).getCarno();
        areaFirstcarnos.add(areaFirstcarno);
      }
      return areaFirstcarnos;
    }
  }

  //根据车牌的首英文字母查询

  public List getCarNosByAreaFirstLetters(String areaFirstLetter) {
    ProductWriter writer = productDaoManager.getWriter();
    List<LicenseplateDTO> carNoDTOList = writer.getCarNosByAreaFirstLetters(areaFirstLetter);
    if (carNoDTOList == null) {
      return null;
    } else {
      List<String> carNos = new ArrayList<String>();
      for (int i = 0; i < carNoDTOList.size(); i++) {
        String carNo = carNoDTOList.get(i).getAreaFirstcarno();
        carNos.add(carNo);
      }
      return carNos;
    }
  }

  //根据车牌查找车牌号
  public List getCarsByCarNos(String carNo, Long shopId) {
    ProductWriter writer = productDaoManager.getWriter();
    List<VehicleDTO> carsList = writer.getCarsByCarNos(carNo, shopId);
    if (carsList.size() == 0) {
      return null;
    } else {
      List<String> cars = new ArrayList<String>();
      for (int i = 0; i < carsList.size(); i++) {
        String car = carsList.get(i).getLicenceNo();
        cars.add(car);
      }
      return cars;
    }
  }

  //反向本地模糊查询
  public List getCarsByCarNosReverse(String carNoValue, String area, Long shopId) {
    ProductWriter writer = productDaoManager.getWriter();
    List<VehicleDTO> carsList = writer.getCarsByCarNosReverse(carNoValue, area, shopId);
    if (carsList.size() == 0) {
      return null;
    } else {
      List<String> cars = new ArrayList<String>();
      for (int i = 0; i < carsList.size(); i++) {
        String car = carsList.get(i).getLicenceNoRevert();
        cars.add(car);
      }
      return cars;
    }
  }

  /**
   * 在数据库中添加一个新商品。
   * 根据saveNewProduct修改来  去掉产品建索引操作 和 ProductVehicle
   *
   * @param productDTO
   * @return
   */
  @Override
  public boolean addProduct(ProductDTO productDTO) throws Exception {
    Long shopId = productDTO.getShopId();
    if (productDTO.getProductVehicleBrandId() == null) {
      VehicleDTO vehicleDTO = getBaseProductService().addVehicleToDB(productDTO.getProductVehicleBrand(), productDTO.getProductVehicleModel(),
          productDTO.getProductVehicleYear(), productDTO.getProductVehicleEngine());
      productDTO.setProductVehicleBrandId(vehicleDTO.getVirtualBrandId());
      productDTO.setProductVehicleModelId(vehicleDTO.getVirtualModelId());
      productDTO.setProductVehicleYearId(vehicleDTO.getVirtualYearId());
      productDTO.setProductVehicleEngineId(vehicleDTO.getVirtualEngineId());
    }

    Integer defPvStatus = productDTO.getProductVehicleStatus();
    if (defPvStatus == null) {
      productDTO.setProductVehicleStatus(SearchConstant.PRODUCT_PRODUCTSTATUS_ALL);
    }

    ProductWriter writer = productDaoManager.getWriter();
    Object productStatus = writer.begin();
    try {
      List<Product> basicProductList = writer.getBasicProductByProductInfo(1L, productDTO);
      Long basicProductId = basicProductList != null && basicProductList.size() > 0 ? basicProductList.get(0).getId() : null;
      productDTO.setParentId(basicProductId);

      Product product = null;
      List<Product> localProductList = writer.getBasicProductByProductInfo(shopId, productDTO);
      if (CollectionUtils.isNotEmpty(localProductList)) {
        product = localProductList.get(0);
        List<ProductLocalInfo> productLocalInfo = writer.getProductLocalInfoByProductId(product.getId(), productDTO.getShopId());
        productDTO.setId(product.getId());
        productDTO.setProductLocalInfoId(productLocalInfo.get(0).getId());
        productDTO.setFirstLetter(product.getFirstLetter());
        productDTO.setFirstLetterCombination(product.getFirstLetterCombination());
        return false;
      } else {
        if (!(productDTO.getFirstLetter() != null && productDTO.getFirstLetterCombination() != null)) {
          String productFirstLetter = PinyinUtil.converterToFirstSpell(productDTO.getName());
          if (StringUtils.isNotEmpty(productFirstLetter)) {
            productDTO.setFirstLetter(productFirstLetter.toLowerCase().charAt(0) + "");
            productDTO.setFirstLetterCombination(productFirstLetter.toLowerCase());
          }
        }
        product = new Product();
        productDTO.setCheckStatus(0);//不知道干嘛用的
        product.fromDTO(productDTO);
        writer.save(product);
        productDTO.setId(product.getId());
        ProductLocalInfo productLocalInfo = saveLocalProductInfoWithUnit(productDTO, writer);
        productDTO.setProductLocalInfoId(productLocalInfo.getId());
      }
      HashMap<ProductDTO, List<ProductVehicleDTO>> pvMap = addProductVehicleForDB(shopId, productDTO, true, writer);
      writer.commit(productStatus);
      return true;
    } finally {
      writer.rollback(productStatus);
    }
  }

  /**
   * 该方法用于保存数据库中没有的新产品。
   * modify by  xzhu    整理部分代码  以后重构的时候 或许还要进行修改
   * 考虑点： 1.product  FirstLetter字段是否有用 2. 产品建索引 是否需要在这里做
   *
   * @param productDTO
   * @return
   */
  @Override
  public boolean saveNewProduct(ProductDTO productDTO) throws Exception {
    Long shopId = productDTO.getShopId();
    if (productDTO.getProductVehicleBrandId() == null) {
      Long[] ids = getVehicleIds(productDTO.getProductVehicleBrand(), productDTO.getProductVehicleModel(),
          productDTO.getProductVehicleYear(), productDTO.getProductVehicleEngine());
      productDTO.setProductVehicleBrandId(ids[0]);
      productDTO.setProductVehicleModelId(ids[1]);
      productDTO.setProductVehicleYearId(ids[2]);
      productDTO.setProductVehicleEngineId(ids[3]);
    }

    Integer defPvStatus = productDTO.getProductVehicleStatus();
    if (defPvStatus == null) {
      productDTO.setProductVehicleStatus(SearchConstant.PRODUCT_PRODUCTSTATUS_ALL);
    }

    ProductWriter writer = productDaoManager.getWriter();
    Object productStatus = writer.begin();
    try {
      List<Product> basicProductList = writer.getBasicProductByProductInfo(1L, productDTO);
      Long basicProductId = basicProductList != null && basicProductList.size() > 0 ? basicProductList.get(0).getId() : null;
      productDTO.setParentId(basicProductId);

      Product product = null;
      List<Product> localProductList = writer.getBasicProductByProductInfo(shopId, productDTO);
      if ((new Integer(1)).equals(localProductList.size())) {
        product = localProductList.get(0);
        product.setKindId(productDTO.getKindId());
        writer.update(product);
//        updateProductBarCode(product.getId(), productDTO.getBarcode()); //库存存在，但库存中此商品无barcode信息并且新入库商品有barcode 更新此商品barcode
        List<ProductLocalInfo> productLocalInfo = writer.getProductLocalInfoByProductId(product.getId(), productDTO.getShopId());
        productDTO.setId(product.getId());
        productDTO.setProductLocalInfoId(productLocalInfo.get(0).getId());
        productDTO.setFirstLetter(product.getFirstLetter());
        productDTO.setFirstLetterCombination(product.getFirstLetterCombination());

        return false;
      } else {
        if (!(productDTO.getFirstLetter() != null && productDTO.getFirstLetterCombination() != null)) {
          String productFirstLetter = PinyinUtil.converterToFirstSpell(productDTO.getName());
          if (StringUtils.isNotEmpty(productFirstLetter)) {
            productDTO.setFirstLetter(productFirstLetter.toLowerCase().charAt(0) + "");
            productDTO.setFirstLetterCombination(productFirstLetter.toLowerCase());
          }
        }
        product = new Product();
        productDTO.setCheckStatus(0);//不知道干嘛用的
        product.fromDTO(productDTO);
        writer.save(product);
        productDTO.setId(product.getId());
        ProductLocalInfo productLocalInfo = saveLocalProductInfoWithUnit(productDTO, writer);
        productDTO.setProductLocalInfoId(productLocalInfo.getId());
      }
      HashMap<ProductDTO, List<ProductVehicleDTO>> pvMap = addProductVehicleForDB(shopId, productDTO, true, writer);
      writer.commit(productStatus);
//      ServiceManager.getService(IProductSolrService.class).addProductForSolr(pvMap, false);
      return true;
    } finally {
      writer.rollback(productStatus);
    }
  }

  @Override
  public void addVehicleToProduct(Long shopId, ProductDTO productDTO) throws Exception {
    ProductWriter writer = productDaoManager.getWriter();
    Object productStatus = writer.begin();
    try {
      HashMap<ProductDTO, List<ProductVehicleDTO>> pvMap = addProductVehicleForDB(shopId, productDTO, false, writer);
      writer.commit(productStatus);
      ServiceManager.getService(IProductSolrService.class).addProductForSolr(new ArrayList<ProductDTO>(pvMap.keySet()), false);
      return;
    } finally {
      writer.rollback(productStatus);
    }

  }

  private HashMap<ProductDTO, List<ProductVehicleDTO>> addProductVehicleForDB(Long shopId, ProductDTO productDTO,
                                                                              boolean isNew,
                                                                              ProductWriter writer) {
    ProductVehicleDTO productVehicleDTO = null;
    List<ProductVehicleDTO> productVehicleDTOList = new ArrayList();
    HashMap<ProductDTO, List<ProductVehicleDTO>> pvMap = new HashMap<ProductDTO, List<ProductVehicleDTO>>();
    Integer productVehicleStatus = productDTO.getProductVehicleStatus();
    if (productVehicleStatus != null &&
        (productVehicleStatus == SearchConstant.PRODUCT_PRODUCTSTATUS_MULTIPLE ||
            productVehicleStatus == SearchConstant.PRODUCT_PRODUCTSTATUS_SPECIAL)) {
      List<ProductVehicleDTO> pvDTOList = null;
      if (!isNew) {
        pvDTOList = getProductVehicleByProductIdReturnList(productDTO.getId());
      } else {
        pvDTOList = new ArrayList<ProductVehicleDTO>();
      }
      boolean included = false;
      if (pvDTOList.size() > 0) {
        for (ProductVehicleDTO pvd : pvDTOList) {
          if (productDTO.getVehicleEngineId() != null && pvd.getEngineId() != null
              && pvd.getEngineId().longValue() == productDTO.getVehicleEngineId().longValue()) {
            included = true;
            break;
          }
          if (productDTO.getVehicleYearId() != null && pvd.getYearId() != null
              && pvd.getYearId().longValue() == productDTO.getVehicleYearId().longValue()
              && pvd.getEngineId() == null
              ) {
            included = true;
            break;
          }
          if (productDTO.getVehicleModelId() != null && pvd.getModelId() != null
              && pvd.getModelId().longValue() == productDTO.getVehicleModelId().longValue()
              && pvd.getEngineId() == null && pvd.getYearId() == null
              ) {
            included = true;
            break;
          }
          if (productDTO.getVehicleBrandId() != null && pvd.getBrandId() != null
              && pvd.getBrandId().longValue() == productDTO.getVehicleBrandId().longValue()
              && pvd.getEngineId() == null && pvd.getYearId() == null && pvd.getModelId() == null) {
            included = true;
            break;
          }
        }
      }
      if (!included) {
        if (productDTO.getVehicleBrandId() != null || productDTO.getVehicleModelId() != null ||
            productDTO.getVehicleYearId() != null || productDTO.getVehicleEngineId() != null) {
          ProductVehicle productVehicle = new ProductVehicle();
          productVehicle.setModelId(productDTO.getVehicleModelId());
          productVehicle.setBrandId(productDTO.getVehicleBrandId());
          productVehicle.setYearId(productDTO.getVehicleYearId());
          productVehicle.setEngineId(productDTO.getVehicleEngineId());
          productVehicle.setProductId(productDTO.getId());
          productVehicle.setShopId(shopId);
          writer.save(productVehicle);
        }

        productVehicleDTO = new ProductVehicleDTO();
        productVehicleDTO.setModelId(productDTO.getVehicleModelId());
        productVehicleDTO.setBrandId(productDTO.getVehicleBrandId());
        productVehicleDTO.setYearId(productDTO.getVehicleYearId());
        productVehicleDTO.setEngineId(productDTO.getVehicleEngineId());
        productVehicleDTO.setProductId(productDTO.getId());
        productVehicleDTO.setShopId(shopId);
        pvDTOList.add(productVehicleDTO);

        pvMap.put(productDTO, pvDTOList);
      }
    } else {
      if (isNew) {
        productVehicleDTO = new ProductVehicleDTO();
        productVehicleDTO.setModelId(1L);
        productVehicleDTO.setBrandId(1L);
        productVehicleDTO.setYearId(1L);
        productVehicleDTO.setEngineId(1L);
        productVehicleDTOList.add(productVehicleDTO);
        pvMap.put(productDTO, productVehicleDTOList);
      }
    }
    return pvMap;
  }

  @Override
  public void updateProductLocalInfo(Long id, Double price, Double purchasePrice, String storageBin, Double tradePrice,
                                     String storageUnit, String sellUnit) {
    ProductWriter writer = productDaoManager.getWriter();
    Object status = writer.begin();
    try {
      ProductLocalInfo productLocalInfo = writer.getById(ProductLocalInfo.class, id);
      if (price != null) {
        productLocalInfo.setPrice(price);
      }
      if (purchasePrice != null) {
        productLocalInfo.setPurchasePrice(purchasePrice);
      }
      if (StringUtils.isBlank(storageBin)) {
        storageBin = null;
      }
      productLocalInfo.setStorageBin(storageBin);
      productLocalInfo.setTradePrice(tradePrice);

      if ((StringUtils.isNotBlank(sellUnit) && !sellUnit.equals(productLocalInfo.getSellUnit())) ||
          (StringUtils.isNotBlank(storageUnit) && !storageUnit.equals(productLocalInfo.getStorageUnit()))) {
        OldProductModifyLog oldProductModifyLog = new OldProductModifyLog();
        oldProductModifyLog.setProductId(productLocalInfo.getId());
        oldProductModifyLog.setShopId(productLocalInfo.getShopId());
        oldProductModifyLog.setPreviousSellUnit(productLocalInfo.getSellUnit());
        oldProductModifyLog.setPreviousStorageUnit(productLocalInfo.getStorageUnit());
        oldProductModifyLog.setPreviousRate(productLocalInfo.getRate());
        if (productLocalInfo.getRate() != null) {
          oldProductModifyLog.setFollowingStorageUnit(storageUnit);
          oldProductModifyLog.setFollowingSellUnit(sellUnit);
        } else {
          oldProductModifyLog.setFollowingStorageUnit(sellUnit);
          oldProductModifyLog.setFollowingSellUnit(sellUnit);
        }
        oldProductModifyLog.setModificationTime(System.currentTimeMillis());
        oldProductModifyLog.setOperation("UPDATE_UNIT");
        writer.save(oldProductModifyLog);
      }

      if (productLocalInfo.getRate() != null) {
        if (StringUtils.isNotBlank(storageUnit)) {
          productLocalInfo.setStorageUnit(storageUnit);
        }
        if (StringUtils.isNotBlank(sellUnit)) {
          productLocalInfo.setSellUnit(sellUnit);
        }
      } else {
        if (StringUtils.isNotBlank(sellUnit)) {
          productLocalInfo.setStorageUnit(sellUnit);
          productLocalInfo.setSellUnit(sellUnit);
        }
      }
      writer.update(productLocalInfo);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void updateProductLocalInfo(ProductLocalInfoDTO productLocalInfoDTO) {
    ProductWriter writer = productDaoManager.getWriter();
    Object status = writer.begin();
    try {
      ProductLocalInfo productLocalInfo = writer.getById(ProductLocalInfo.class, productLocalInfoDTO.getId());
      productLocalInfo.setStorageUnit(productLocalInfoDTO.getStorageUnit());
      productLocalInfo.setSellUnit(productLocalInfoDTO.getSellUnit());
      productLocalInfo.setPrice(productLocalInfoDTO.getPrice());
      productLocalInfo.setRate(productLocalInfoDTO.getRate());
      productLocalInfo.setPurchasePrice(productLocalInfoDTO.getPurchasePrice());
      productLocalInfo.setInSalesAmount(productLocalInfoDTO.getInSalesAmount());
      productLocalInfo.setInSalesPrice(productLocalInfoDTO.getInSalesPrice());
      productLocalInfo.setInSalesUnit(productLocalInfoDTO.getInSalesUnit());
      productLocalInfo.setGuaranteePeriod(productLocalInfoDTO.getGuaranteePeriod());
      if (StringUtils.isBlank(productLocalInfoDTO.getStorageBin())) {
        productLocalInfo.setStorageBin(null);
      } else {
        productLocalInfo.setStorageBin(productLocalInfoDTO.getStorageBin());
      }
      productLocalInfo.setTradePrice(productLocalInfoDTO.getTradePrice());
      productLocalInfo.setBusinessCategoryId(productLocalInfoDTO.getBusinessCategoryId());
      writer.update(productLocalInfo);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void updateProductLocalInfo(ProductLocalInfoDTO... localInfoDTOs) {
    if(ArrayUtil.isEmpty(localInfoDTOs)){
      return;
    }
    ProductWriter writer = productDaoManager.getWriter();
    Object status = writer.begin();
    try {
      for(ProductLocalInfoDTO localInfoDTO:localInfoDTOs){
        ProductLocalInfo productLocalInfo = writer.getById(ProductLocalInfo.class, localInfoDTO.getId());
        if(productLocalInfo==null){
          LOG.warn("productLocalInfo does't exsit,id={}",localInfoDTO.getId());
          continue;
        }
        productLocalInfo.fromDTO(localInfoDTO);
        writer.update(productLocalInfo);
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public Result updateProductForInSales(Result result,ProductDTO fromProductDTO) throws Exception {
    Long shopId=fromProductDTO.getShopId();
    if(shopId==null){
      return result.LogErrorMsg("参数异常。");
    }
    ProductWriter writer = productDaoManager.getWriter();
    Object status = writer.begin();
    try {
      ProductModifyLogDTO newLogDTO = new ProductModifyLogDTO();
      ProductModifyLogDTO oldLogDTO = new ProductModifyLogDTO();
      Product oldProduct = writer.getProductById(fromProductDTO.getId(), shopId);
      if(oldProduct==null||ProductStatus.DISABLED.equals(oldProduct.getStatus())){
        return result.LogErrorMsg("商品不存在。");
      }
      ProductDTO productDTO=oldProduct.toDTO();
      if(fromProductDTO.getName()!=null)
        productDTO.setName(fromProductDTO.getName());
      if(fromProductDTO.getBrand()!=null)
        productDTO.setBrand(fromProductDTO.getBrand());
      if(fromProductDTO.getSpec()!=null)
        productDTO.setSpec(fromProductDTO.getSpec());
      if(fromProductDTO.getModel()!=null)
        productDTO.setModel(fromProductDTO.getModel());
      if(fromProductDTO.getProductVehicleBrand()!=null)
        productDTO.setProductVehicleBrand(fromProductDTO.getProductVehicleBrand());
      if(fromProductDTO.getProductVehicleModel()!=null)
        productDTO.setProductVehicleModel(fromProductDTO.getProductVehicleModel());
      if(fromProductDTO.getCommodityCode()!=null)
        productDTO.setCommodityCode(fromProductDTO.getCommodityCode());
      if(fromProductDTO.getKindId()!=null)
        productDTO.setKindId(fromProductDTO.getKindId());
      if (!oldProduct.toDTO().checkSameBasicProperties(productDTO)&&!checkSameProduct(shopId,productDTO)) {
        return result.LogErrorMsg("商品已经存在。");
      }
      oldProduct.fromDTO(productDTO);
      writer.update(productDTO);
      newLogDTO.setProduct(productDTO);

      if(fromProductDTO.getProductLocalInfoId()!=null){
        ProductLocalInfoDTO localInfoDTO=new ProductLocalInfoDTO();
        localInfoDTO.setId(fromProductDTO.getProductLocalInfoId());
        localInfoDTO.setInSalesAmount(fromProductDTO.getInSalesAmount());
        localInfoDTO.setInSalesPrice(fromProductDTO.getInSalesPrice());
        ProductLocalInfo productLocalInfo = writer.getById(ProductLocalInfo.class,fromProductDTO.getId());
        if(productLocalInfo==null){
          return result.LogErrorMsg("商品不存在。");
        }
        if(fromProductDTO.getStorageUnit()!=null)
          productLocalInfo.setStorageUnit(fromProductDTO.getStorageUnit());
        if(fromProductDTO.getSellUnit()!=null)
          productLocalInfo.setSellUnit(fromProductDTO.getSellUnit());
        if(fromProductDTO.getPrice()!=null)
          productLocalInfo.setPrice(fromProductDTO.getPrice());
        if(fromProductDTO.getRate()!=null)
          productLocalInfo.setRate(fromProductDTO.getRate());

        if(fromProductDTO.getTradePrice()!=null)
          productLocalInfo.setTradePrice(fromProductDTO.getTradePrice());
        if(fromProductDTO.getBusinessCategoryId()!=null)
          productLocalInfo.setBusinessCategoryId(fromProductDTO.getBusinessCategoryId());
        writer.update(productLocalInfo);
        newLogDTO.setProductLocalInfo(productLocalInfo.toDTO());
      }
      writer.commit(status);
      return result;
    } finally {
      writer.rollback(status);
    }
  }

  public ProductLocalInfo getProductLocalInfoById(Long productLocalInfoId){
    if(productLocalInfoId==null) return null;
    ProductWriter writer = productDaoManager.getWriter();
    Object status=writer.begin();
    try{
      return writer.getById(ProductLocalInfo.class,productLocalInfoId);
    }finally {
      writer.rollback(status);
    }
  }

  @Override
  public List<InventorySearchIndexDTO> initInventorySearchIndexDTOWithUnit(List<InventorySearchIndexDTO> inventorySearchIndexDTOs) {
    ProductWriter writer = productDaoManager.getWriter();
    List<InventorySearchIndexDTO> inventorySearchIndexDTOList = new ArrayList<InventorySearchIndexDTO>();
    if (inventorySearchIndexDTOs != null && inventorySearchIndexDTOs.size() > 0) {
      for (InventorySearchIndexDTO inventorySearchIndexDTO : inventorySearchIndexDTOs) {
        if (inventorySearchIndexDTO.getProductId() == null) {
          continue;
        }
        ProductLocalInfo productLocalInfo = writer.getById(ProductLocalInfo.class, inventorySearchIndexDTO.getProductId());
        if (productLocalInfo != null) {
          inventorySearchIndexDTO.setStorageUnit(productLocalInfo.getStorageUnit());
          inventorySearchIndexDTO.setSellUnit(productLocalInfo.getSellUnit());
          inventorySearchIndexDTO.setRate(productLocalInfo.getRate());
        }
        inventorySearchIndexDTOList.add(inventorySearchIndexDTO);
      }
    }
    return inventorySearchIndexDTOList;
  }

  @Override
  public void updateProductLocalInfoUnit(Long shopId, Long productId, String storageUnit, String sellUnit, Long rate) {
    ProductWriter writer = productDaoManager.getWriter();
    Object status = writer.begin();
    try {
      List<ProductLocalInfo> productLocalInfos = writer.getProductLocalInfoById(productId, shopId);
      if (productLocalInfos != null && productLocalInfos.size() > 0) {
        ProductLocalInfo productLocalInfo = productLocalInfos.get(0);
        productLocalInfo.setSellUnit(sellUnit);
        productLocalInfo.setStorageUnit(storageUnit);
        productLocalInfo.setRate(rate);
        writer.saveOrUpdate(productLocalInfo);
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  private ProductLocalInfo saveOrUpdateLocalProductInfo(Long shopId, Double price, Double purchasePrice,
                                                        Long localProductId, List<ProductLocalInfo> productLocalInfoList,
                                                        ProductWriter writer) {
    ProductLocalInfo productLocalInfo = productLocalInfoList.get(0);
    productLocalInfo.setPrice(price);
    productLocalInfo.setPurchasePrice(purchasePrice);
    productLocalInfo.setProductId(localProductId);
    productLocalInfo.setShopId(shopId);
    writer.saveOrUpdate(productLocalInfo);
    return productLocalInfo;
  }

  private ProductLocalInfo saveLocalProductInfo(Long shopId, Double price, Double purchasePrice, Product product,
                                                ProductWriter writer) {
    ProductLocalInfo productLocalInfo = new ProductLocalInfo();
    productLocalInfo.setPrice(price);
    productLocalInfo.setPurchasePrice(purchasePrice);
    productLocalInfo.setProductId(product.getId());
    productLocalInfo.setShopId(shopId);
    writer.save(productLocalInfo);
    return productLocalInfo;
  }
  //end

  private ProductLocalInfo saveLocalProductInfoWithUnit(ProductDTO productDTO, ProductWriter writer) {
    ProductLocalInfo productLocalInfo = new ProductLocalInfo();
    productLocalInfo.setPrice(productDTO.getPrice());
    productLocalInfo.setPurchasePrice(productDTO.getPurchasePrice());
    productLocalInfo.setProductId(productDTO.getId());
    productLocalInfo.setShopId(productDTO.getShopId());
    productLocalInfo.setStorageUnit(productDTO.getStorageUnit());
    productLocalInfo.setSellUnit(productDTO.getSellUnit());
    productLocalInfo.setRate(productDTO.getRate());
    productLocalInfo.setStorageBin(productDTO.getStorageBin());
    productLocalInfo.setTradePrice(productDTO.getTradePrice());
    productLocalInfo.setInSalesAmount(productDTO.getInSalesAmount());
    productLocalInfo.setInSalesPrice(productDTO.getInSalesPrice());
    productLocalInfo.setGuaranteePeriod(productDTO.getGuaranteePeriod());
    productLocalInfo.setBusinessCategoryId(productDTO.getBusinessCategoryId());
    writer.save(productLocalInfo);
    return productLocalInfo;
  }

  //车牌前缀的测试
  public LicenseplateDTO caeateLicenseplateDTO(LicenseplateDTO licenseplateDTO) {
    ProductWriter writer = productDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Licenseplate mfr = new Licenseplate();
      // mfr.setId(licenseplateDTO.getId());
      mfr.setCarno(licenseplateDTO.getCarno());
      mfr.setAreaFirstname(licenseplateDTO.getAreaFirstname());
      mfr.setAreaFirstcarno(licenseplateDTO.getAreaFirstcarno());
      mfr.setAreaName(licenseplateDTO.getAreaName());


      writer.save(mfr);
      writer.commit(status);

      licenseplateDTO.setId(mfr.getId());
      return licenseplateDTO;
    } finally {
      writer.rollback(status);
    }
  }


  //车牌前缀
  @Override
  public LicenseplateDTO getLicenseplate(Long lplateId) {
    ProductWriter writer = productDaoManager.getWriter();
    Licenseplate licenseplate = writer.getLicenseplate(lplateId);
    if (licenseplate != null) {
      LicenseplateDTO lpateDTO = new LicenseplateDTO();
      lpateDTO.setCarno(licenseplate.getCarno());
      lpateDTO.setAreaName(licenseplate.getAreaName());
      lpateDTO.setAreaFirstcarno(licenseplate.getAreaFirstcarno());
      lpateDTO.setAreaFirstname(licenseplate.getAreaFirstname());

      return lpateDTO;
    }
    return null;
  }

  //ProductAdmin -----
  public List<ProductAdminDTO> getProductAdminList(int pageNo, int pageSize) {
    ProductWriter writer = productDaoManager.getWriter();

    List<ProductAdminDTO> productAdminDTOList = new ArrayList<ProductAdminDTO>();
    for (ProductAdmin productAdmin : writer.getProductAdminList(pageNo, pageSize)) {
      productAdminDTOList.add(productAdmin.toDTO());
    }

    return productAdminDTOList;
  }

  @Override
  public Integer getInventoryIndexPageNo(HttpServletRequest request, String pageStatus, String pageName) {
    HttpSession session = request.getSession(true);
    Integer page = (Integer) session.getAttribute(pageName);
    Integer pageNo = page == null || page <= 0 ? 1 : page;
    if ("Home".equals(pageStatus)) {
      session.setAttribute(pageName, 1);
      pageNo = 1;
    } else if ("PageDown".equals(pageStatus)) {
      session.setAttribute(pageName, ++pageNo);
    } else if ("PageUp".equals(pageStatus)) {
      if (pageNo > 1) {
        session.setAttribute(pageName, --pageNo);
      }
    } else if ("currentPage".equals(pageStatus)) {
      session.setAttribute(pageName, pageNo);
    } else {
      session.setAttribute(pageName, 1);
      pageNo = 1;
    }
    return pageNo;
  }


  //根据首汉字找到对应的字母
  public List<ChnfirstletterDTO> getFirstLetterFromChnFirstLetter(String hanzi) {
    ProductWriter writer = productDaoManager.getWriter();
    List<Chnfirstletter> letters = writer.getFirstLetterFromChnFirstLetter(hanzi);
    List<ChnfirstletterDTO> chnDTOs = new ArrayList<ChnfirstletterDTO>();
    if (CollectionUtils.isNotEmpty(letters)) {
      for (Chnfirstletter c : letters) {
        chnDTOs.add(c.toDTO());
      }
    }
    return chnDTOs;
  }

  @Override
  public Long[] getVehicleIds(String brand, String model, String year, String engine) throws Exception {
    IBaseProductService baseProductService = ServiceManager.getService(IBaseProductService.class);
    ProductWriter writer = productDaoManager.getWriter();
    Brand b = writer.getBrandByName(brand);
    if (b == null) {
      return baseProductService.saveVehicle(null, null, null, null, brand, model, year, engine);
    }
    if (StringUtils.isBlank(model)) {
      return new Long[]{b.getId(), null, null, null};
    }
    Model m = writer.getModelByName(b.getId(), model);
    if (m == null) {
      return baseProductService.saveVehicle(null, null, null, null, brand, model, year, engine);
    }
    if (StringUtils.isBlank(year)) {
      return new Long[]{b.getId(), m.getId(), null, null};
    }
    Year y = writer.getYearByName(("".equals(year) || year == null ? null : Integer.valueOf(year)), b.getId(), m.getId());
    if (y == null) {
      return baseProductService.saveVehicle(null, null, null, null, brand, model, year, engine);
    }
    if (StringUtils.isBlank(engine)) {
      return new Long[]{b.getId(), m.getId(), y.getId(), null};
    }
    Engine e = writer.getEngineByName(engine, y.getId(), b.getId(), m.getId());
    if (e == null) {
      return baseProductService.saveVehicle(null, null, null, null, brand, model, year, engine);
    }
    return new Long[]{b.getId(), m.getId(), y.getId(), e.getId()};
  }

  /**
   * 导入数据前检测数据格式是否正确，不正确就组装错误信息
   *
   * @param strs      被检测的数据
   * @param maxLength 正确的字符数组长度
   * @param errorStr  需要组装的错误信息
   * @return
   * @author wjl
   */
  private StringBuffer checkDataFormat(String[] strs, int maxLength, StringBuffer errorStr) {
    if (strs.length != maxLength) {
      if (errorStr == null) errorStr = new StringBuffer("如下数据格式不正确(数组长度不为" + maxLength + ")\n");
      for (String errorLineStr : strs) {
        errorStr.append(errorLineStr + "  ");
      }
      errorStr.append("该行数组长度为:" + strs.length);
      errorStr.append("\n");
    }
    return errorStr;
  }

  //tag qxy to delete
  @Override
  public ProductBarcode searchBarcode(Long shopId, String barcode) {
    ProductWriter productWriter = productDaoManager.getWriter();
    ProductBarcode productBarcode = productWriter.getProductByShopIdAndBarcode(shopId, barcode);
    if (productBarcode != null) {
      return productBarcode;
    }
    productBarcode = productWriter.getProductBarcodeByBarcode(barcode);
    if (productBarcode != null) {
      return productBarcode;
    }
    return null;
  }

  @Override
  public Product getProductByBarcode(String barcode) throws BcgogoException {
    ProductWriter productWriter = productDaoManager.getWriter();
    Product product = productWriter.getProductByBarcode(barcode);
    return product;
  }

  @Override
  public void updateProductBarCode(Long productId, String barcode) throws BcgogoException {
    ProductWriter writer = productDaoManager.getWriter();
    Product product = writer.getById(Product.class, productId);
    Object pStatus = writer.begin();
    try {
      if (product != null) {
        if (StringUtils.isNotBlank(product.getBarcode())) {
          product.setBarcode(barcode);
          writer.saveOrUpdate(product);
          writer.commit(pStatus);
        }
      }
    } finally {
      writer.rollback(pStatus);
    }
  }

  /**
   * 获取店铺一定数量的产品信息
   *
   * @param shopId
   * @param start
   * @param num
   * @return
   */
  public List<ProductDTO> getProducts(Long shopId, Long start, int num) throws BcgogoException {
    ProductWriter writer = productDaoManager.getWriter();
    return writer.getProducts(shopId, start, num);
  }

  @Override
  public List<ProductDTO> getProductsByCondition(ProductQueryCondition productQueryCondition) throws Exception {
    List<ProductDTO> productDTOs = new ArrayList<ProductDTO>();
    if (productQueryCondition == null) {
      return productDTOs;
    }
    ProductWriter productWriter = productDaoManager.getWriter();
    Map<Long, ProductLocalInfoDTO> productLocalInfoMap = new HashMap<Long, ProductLocalInfoDTO>();
    List<Long> productIds = new ArrayList<Long>();
    List<ProductLocalInfo> productLocalInfos = productWriter.getProductLocalInfosByCondition(productQueryCondition);
    if (CollectionUtils.isNotEmpty(productLocalInfos)) {
      for (ProductLocalInfo productLocalInfo : productLocalInfos) {
        long productId = productLocalInfo.getProductId();
        productLocalInfoMap.put(productId, productLocalInfo.toDTO());
        productIds.add(productId);
      }
    }
    if (CollectionUtils.isNotEmpty(productIds)) {
      List<Product> products = productWriter.getProductByIds(productIds);
      if (CollectionUtils.isNotEmpty(products)) {
        for (Product product : products) {
          ProductDTO productDTO = product.toDTO();
          if (product.getKindId() != null) {
            Kind kind = productWriter.getById(Kind.class, product.getKindId());
            productDTO.setKindName(kind == null ? "" : kind.getName());
          }
          productDTOs.add(productDTO);
        }
      }
    }

    IPromotionsService iPromotionsService = ServiceManager.getService(IPromotionsService.class);
    Map<Long, List<PromotionsDTO>> allPromotionsDTOMap = iPromotionsService.getPromotionsDTOMapByProductLocalInfoId(productQueryCondition.getShopId(), true, productIds.toArray(new Long[productIds.size()]));
    if (CollectionUtils.isNotEmpty(productDTOs)) {
      for (ProductDTO productDTO : productDTOs) {
        productDTO.setProductLocalInfoDTO(productLocalInfoMap.get(productDTO.getId()));
        List<PromotionsDTO> promotions = allPromotionsDTOMap.get(productDTO.getProductLocalInfoId());
        productDTO.setPromotionsDTOs(promotions);
        productDTO.setInSalesPriceAfterCal(PromotionsUtils.calculateBargainPrice(promotions, productDTO.getInSalesPrice()) == null ? 0.00d : PromotionsUtils.calculateBargainPrice(promotions, productDTO.getInSalesPrice()));
        String[] titles = PromotionsUtils.genPromotionTypesStr(promotions);
        productDTO.setPromotionTypesShortStr(titles[0]);
        productDTO.setPromotionTypesStr(titles[1]);
      }
    }
    return productDTOs;
  }

  @Override
  public void updateProduct(Long shopId, ProductDTO productDTO) throws Exception {
    ProductWriter writer = productDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Product product = writer.getProductById(productDTO.getId(), shopId);
      product.setName(productDTO.getName());
      product.setBrand(productDTO.getBrand());
      product.setSpec(productDTO.getSpec());
      product.setModel(productDTO.getModel());
      product.setProductVehicleBrand(productDTO.getProductVehicleBrand());
      product.setProductVehicleModel(productDTO.getProductVehicleModel());
      product.setCommodityCode(productDTO.getCommodityCode());
      product.setDescription(productDTO.getDescription());
      if(productDTO.getKindId()!=null){//无奈
        if(-1l==productDTO.getKindId().longValue()){
          product.setKindId(null);
        }else{
          product.setKindId(productDTO.getKindId());
        }
      }

      writer.update(product);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public List<VehicleDTO> saveOrUpdateVehicleInfo(Long shopId, Long userId, Long customerId, CarDTO[] carDTOs) throws Exception {
    IBaseProductService baseProductService = ServiceManager.getService(IBaseProductService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    IProductSolrService productSolrService = ServiceManager.getService(IProductSolrService.class);
    List<VehicleDTO> vehicleDTOs = new ArrayList<VehicleDTO>();
    List<VehicleDTO> vehicleDTOListForSolr = new ArrayList<VehicleDTO>();
    List<VehicleDTO> vehicleDTOsReturn = null;
    if (carDTOs != null && carDTOs.length > 0) {
      for (CarDTO carDTO : carDTOs) {
        if (!StringUtil.strArrayIsBlank(carDTO.getBrand(), carDTO.getModel(), carDTO.getYear(), carDTO.getEngine())) {
          VehicleDTO vehicleDTOForSolr = baseProductService.addVehicleToDB(carDTO.getBrand(), carDTO.getModel(), carDTO.getYear(), carDTO.getEngine());
          vehicleDTOForSolr.setContact(carDTO.getContact());
          vehicleDTOForSolr.setMobile(carDTO.getMobile());
          vehicleDTOListForSolr.add(vehicleDTOForSolr);
          VehicleDTO vehicleDTOForUser = vehicleDTOForSolr.clone();
          vehicleDTOForUser.setId(null);
          vehicleDTOForUser.setCarDTO(carDTO);
          if (carDTO.getDateString() != null && !"".equals(carDTO.getDateString())) {
            vehicleDTOForUser.setCarDate(DateUtil.convertDateStringToDateLong("yyyy-MM-dd", carDTO.getDateString()));
          }
          if (carDTO.getCarDate() != 0) {
            vehicleDTOForUser.setCarDate(carDTO.getCarDate());
          }
          vehicleDTOForUser.setColor(carDTO.getColor());
          vehicleDTOForUser.setEngineNo(carDTO.getEngineNo());
          vehicleDTOForUser.setChassisNumber(carDTO.getChassisNumber());
          vehicleDTOs.add(vehicleDTOForUser);
        } else {
          VehicleDTO vehicleDTOForUser = new VehicleDTO();
          vehicleDTOForUser.setCarDTO(carDTO);
          if (carDTO.getDateString() != null && !"".equals(carDTO.getDateString())) {
            vehicleDTOForUser.setCarDate(DateUtil.convertDateStringToDateLong("yyyy-MM-dd", carDTO.getDateString()));
          } else {
            vehicleDTOForUser.setCarDate(0L);
          }
          if (carDTO.getCarDate() != 0) {
            vehicleDTOForUser.setCarDate(carDTO.getCarDate());
          }
          vehicleDTOForUser.setColor(carDTO.getColor());
          vehicleDTOForUser.setEngineNo(carDTO.getEngineNo());
          vehicleDTOForUser.setChassisNumber(carDTO.getChassisNumber());
          vehicleDTOs.add(vehicleDTOForUser);
        }
      }
      vehicleDTOsReturn = userService.saveOrUpdateCustomerVehicles(customerId, shopId, userId, vehicleDTOs);
      productSolrService.addVehicleForSearch(vehicleDTOListForSolr);
    }
    return vehicleDTOsReturn;
  }

  @Override
  public List<Object[]> getProductDataByProductLocalInfoId(Long shopId, Long... productLocalInfoId) throws Exception {
    ProductWriter writer = productDaoManager.getWriter();
    return writer.getProductDataByProductLocalInfoId(shopId, productLocalInfoId);
  }

  @Override
  public List<Long> getProductLocalInfoIdList(Long shopId, int start, int num) throws Exception {
    ProductWriter writer = productDaoManager.getWriter();
    return writer.getProductLocalInfoIdList(shopId, start, num);
  }

  @Override
  public void saveProductSupplier(List<ProductSupplierDTO> productSupplierDTOs) {
    if (CollectionUtils.isEmpty(productSupplierDTOs)) {
      return;
    }
    ProductWriter writer = productDaoManager.getWriter();
    Object status = writer.begin();
    try {
      for (ProductSupplierDTO productSupplierDTO : productSupplierDTOs) {
        if (productSupplierDTO == null) {
          continue;
        }
        ProductSupplier productSupplier = new ProductSupplier(productSupplierDTO);
        writer.save(productSupplier);
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void saveOrUpdateProductSupplier(List<ProductSupplierDTO> productSupplierDTOs) {
    if (CollectionUtils.isEmpty(productSupplierDTOs)) {
      return;
    }
    ProductWriter writer = productDaoManager.getWriter();
    Object object = writer.begin();
    try {
      for (ProductSupplierDTO productSupplierDTO : productSupplierDTOs) {
        if (productSupplierDTO == null || productSupplierDTO.getProductId() == null
            || productSupplierDTO.getSupplierId() == null) {
          continue;
        }
        if (productSupplierDTO.getLastUsedTime() == null) {
          productSupplierDTO.setLastUsedTime(System.currentTimeMillis());
        }
        List<ProductSupplier> list = writer.getProductSuppliers(productSupplierDTO.getProductId(), productSupplierDTO.getShopId());
        if (CollectionUtils.isEmpty(list)) {
          writer.save(new ProductSupplier(productSupplierDTO));
        } else {
          int len = list.size();
          Long minLastUsedTime = list.get(0).getLastUsedTime();
          ProductSupplier minLastUsedEntity = list.get(0);
          boolean saveFlag = true;
          for (ProductSupplier productSupplier : list) {
            if (productSupplierDTO.getSupplierId().equals(productSupplier.getSupplierId())) {
              if (productSupplier.getLastUsedTime() == null
                  || productSupplierDTO.getLastUsedTime() > productSupplier.getLastUsedTime()) {
                productSupplier.setLastUsedTime(productSupplierDTO.getLastUsedTime());
                productSupplier.setMobile(productSupplierDTO.getMobile());
                productSupplier.setName(productSupplierDTO.getName());
                productSupplier.setContact(productSupplierDTO.getContact());
                writer.update(productSupplier);
              }
              saveFlag = false;
              break;
            }
            if (minLastUsedTime > productSupplier.getLastUsedTime()) {
              minLastUsedTime = productSupplier.getLastUsedTime();
              minLastUsedEntity = productSupplier;
            }
          }
          if (saveFlag) {
            if (len > 2) {
              writer.delete(ProductSupplier.class, minLastUsedEntity.getId());
            }
            writer.save(new ProductSupplier(productSupplierDTO));
          }
        }
      }
      writer.commit(object);
    } finally {
      writer.rollback(object);
    }
  }

  @Override
  public List<ProductSupplierDTO> getProductSupplierDTOs(Long productId, Long shopId) {
    ProductWriter writer = productDaoManager.getWriter();
    return writer.getProductSupplierDTO(productId, shopId);
  }

  @Override
  public int countProductSupplierDTOsByShopId(Long shopId) {
    if (shopId == null) {
      return 0;
    }
    Long result = productDaoManager.getWriter().countProductSupplierByShopId(shopId);
    if (result == null) {
      return 0;
    } else {
      return result.intValue();
    }
  }

  @Override
  public List<ProductSupplierDTO> getProductSupplierDTOsByShopId(Long shopId, Pager pager) {
    List<ProductSupplierDTO> productSupplierDTOs = new ArrayList<ProductSupplierDTO>();
    if (shopId == null || pager == null) {
      return productSupplierDTOs;
    }
    List<ProductSupplier> productSuppliers = productDaoManager.getWriter().getProductSupplierByShopId(shopId, pager);
    if (CollectionUtil.isNotEmpty(productSuppliers)) {
      for (ProductSupplier productSupplier : productSuppliers) {
        productSupplierDTOs.add(productSupplier.toDTO());
      }
    }
    return productSupplierDTOs;
  }

  /**
   * @param productLocalInfoIds
   * @param shopId
   * @param limit               表示需要重做索引的数量 （以时间排序）    如limit=3 表示只要前三个，  limit=null 表示不限制
   * @return
   */
  @Override
  public void getProductSupplierMap(Map<Long, List<ProductSupplierDTO>> productSupplierMap,
                                    Map<Long, List<ProductSupplierDTO>> productSupplierIdsMap, Long[] productLocalInfoIds, Long shopId, Integer limit) {
    ProductWriter writer = productDaoManager.getWriter();
    if (productSupplierMap == null) {
      productSupplierMap = new HashMap<Long, List<ProductSupplierDTO>>((productLocalInfoIds == null ? 16 : (int) (productLocalInfoIds.length / 0.75f + 1)));
    }
    if (productSupplierIdsMap == null) {
      productSupplierIdsMap = new HashMap<Long, List<ProductSupplierDTO>>((productLocalInfoIds == null ? 16 : (int) (productLocalInfoIds.length / 0.75f + 1)));
    }
    List<ProductSupplierDTO> productSupplierDTOs = writer.getProductSupplierDTO(productLocalInfoIds, shopId);
    if (CollectionUtils.isEmpty(productSupplierDTOs)) {
      return;
    }
    //组装productsupplierInfoMap
    List<ProductSupplierDTO> temp = new ArrayList<ProductSupplierDTO>();
    for (ProductSupplierDTO productSupplierDTO : productSupplierDTOs) {
      if (productSupplierDTO == null || productSupplierDTO.getProductId() == null || productSupplierDTO.getSupplierId() == null) {
        continue;
      }
      if (CollectionUtils.isEmpty(temp)) {
        temp.add(productSupplierDTO);
      } else {
        if (productSupplierDTO.getProductId().equals(temp.get(0).getProductId())) {
          boolean supplierSameFlag = true;
          for (ProductSupplierDTO temProductSupplierDTO : temp) {
            if (productSupplierDTO.getSupplierId().equals(temProductSupplierDTO.getSupplierId())) {
              supplierSameFlag = false;
              break;
            }
          }
          if (supplierSameFlag) {
            temp.add(productSupplierDTO);
          }
        } else {
          productSupplierMap.put(temp.get(0).getProductId(), ServiceUtil.getTopNProductSupplierDTO(temp, limit));
          temp = new ArrayList<ProductSupplierDTO>();
          temp.add(productSupplierDTO);
        }
      }
    }
    if (CollectionUtils.isNotEmpty(temp)) {
      productSupplierMap.put(temp.get(0).getProductId(), ServiceUtil.getTopNProductSupplierDTO(temp, limit));
    }

    //组装productsupplierIdMap
    List<ProductSupplierDTO> supplierIds = new ArrayList<ProductSupplierDTO>();
    for (ProductSupplierDTO productSupplierDTO : productSupplierDTOs) {
      if (productSupplierDTO == null || productSupplierDTO.getProductId() == null || productSupplierDTO.getSupplierId() == null) {
        continue;
      }
      if (CollectionUtils.isEmpty(supplierIds)) {
        supplierIds.add(productSupplierDTO);
      } else {
        if (productSupplierDTO.getProductId().equals(supplierIds.get(0).getProductId())) {
          boolean supplierSameFlag = true;
          for (ProductSupplierDTO temProductSupplierDTO : supplierIds) {
            if (productSupplierDTO.getSupplierId().equals(temProductSupplierDTO.getSupplierId())) {
              supplierSameFlag = false;
              break;
            }
          }
          if (supplierSameFlag) {
            supplierIds.add(productSupplierDTO);
          }
        } else {
          productSupplierIdsMap.put(supplierIds.get(0).getProductId(), supplierIds);
          supplierIds = new ArrayList<ProductSupplierDTO>();
          supplierIds.add(productSupplierDTO);
        }
      }
    }
    if (CollectionUtils.isNotEmpty(supplierIds)) {
      productSupplierIdsMap.put(supplierIds.get(0).getProductId(), supplierIds);
    }
  }

  @Override
  public Map<Long, ProductDTO> getProductDTOMapByProductLocalInfoIds(Set<Long> productLocalInfoIds) {
    if (CollectionUtils.isEmpty(productLocalInfoIds)) {
      return new HashMap<Long, ProductDTO>();
    }
    ProductWriter writer = productDaoManager.getWriter();
    List<Object[]> list = writer.getProductDTOMapByProductLocalInfoIds(productLocalInfoIds);

    if (CollectionUtils.isNotEmpty(list)) {
      Map<Long, ProductDTO> productDTOMap = new HashMap<Long, ProductDTO>((int) (list.size() / 0.75f) + 1, 0.75f);
      for (Object[] objects : list) {
        if (objects != null && objects.length == 2) {
          Product product = (Product) objects[0];
          ProductLocalInfo productLocalInfo = (ProductLocalInfo) objects[1];
          ProductDTO productDTO = product.toDTO();
          productDTO.setProductLocalInfoDTO(productLocalInfo.toDTO());
          productDTOMap.put(productLocalInfo.getId(), productDTO);
        }
      }
      List<Long> kindIdList = new ArrayList<Long>();
      for (ProductDTO productDTO : productDTOMap.values()) {
        if (productDTO.getKindId() != null) {
          kindIdList.add(productDTO.getKindId());
        }
      }
      if (CollectionUtils.isNotEmpty(kindIdList)) {
        Map<Long, KindDTO> kindDTOMap = getProductKindById(kindIdList.toArray(new Long[kindIdList.size()]));
        KindDTO kindDTO = null;
        for (ProductDTO productDTO : productDTOMap.values()) {
          kindDTO = kindDTOMap.get(productDTO.getKindId());
          productDTO.setKindName(kindDTO == null ? null : kindDTO.getName());
        }
      }
      return productDTOMap;
    }
    return new HashMap<Long, ProductDTO>();
  }

  @Override
  public Map<Long, ProductDTO> getProductDTOMapByProductLocalInfoIds(Long shopId, Set<Long> productLocalInfoIds) {
    if (shopId == null || productLocalInfoIds == null || productLocalInfoIds.isEmpty()) {
      return new HashMap<Long, ProductDTO>();
    }
    ProductWriter writer = productDaoManager.getWriter();
    List<Object[]> list = writer.getProductDTOMapByProductLocalInfoIds(shopId, productLocalInfoIds);  //productList

    if (CollectionUtils.isNotEmpty(list)) {
      Map<Long, ProductDTO> productDTOMap = new HashMap<Long, ProductDTO>((int) (list.size() / 0.75f) + 1, 0.75f);
      for (Object[] objects : list) {
        if (objects != null && objects.length == 2) {
          Product product = (Product) objects[0];
          ProductLocalInfo productLocalInfo = (ProductLocalInfo) objects[1];
          ProductDTO productDTO = product.toDTO();
          productDTO.setProductLocalInfoDTO(productLocalInfo.toDTO());
          productDTOMap.put(productLocalInfo.getId(), productDTO);
        }
      }
      List<Long> kindIdList = new ArrayList<Long>();
      for (ProductDTO productDTO : productDTOMap.values()) {
        if (productDTO.getKindId() != null) {
          kindIdList.add(productDTO.getKindId());
        }
      }
      if (CollectionUtils.isNotEmpty(kindIdList)) {
        Map<Long, KindDTO> kindDTOMap = getProductKindById(kindIdList.toArray(new Long[kindIdList.size()]));
        KindDTO kindDTO = null;
        for (ProductDTO productDTO : productDTOMap.values()) {
          kindDTO = kindDTOMap.get(productDTO.getKindId());
          productDTO.setKindName(kindDTO == null ? null : kindDTO.getName());
        }
      }
      return productDTOMap;
    }
    return new HashMap<Long, ProductDTO>();
  }

  @Override
  public Map<Long, Map<Long, ProductMappingDTO>> getCustomerProductMappingDTOMapInMap(Long shopId, Long... productLocalInfoIds) {
    if (ArrayUtils.isEmpty(productLocalInfoIds)) {
      return new HashMap<Long, Map<Long, ProductMappingDTO>>();
    }
    ProductWriter writer = productDaoManager.getWriter();
    List<ProductMapping> productMappings = writer.getCustomerTradedProductMappingByProductIds(shopId, productLocalInfoIds);
    Map<Long, Map<Long, ProductMappingDTO>> productMappingDTOMap = new HashMap<Long, Map<Long, ProductMappingDTO>>();
    if (CollectionUtils.isNotEmpty(productMappings)) {
      for (ProductMapping productMapping : productMappings) {
        if (productMappingDTOMap.get(productMapping.getCustomerProductId()) == null) {
          Map<Long, ProductMappingDTO> tempProductMappingDTOMap = new HashMap<Long, ProductMappingDTO>();
          tempProductMappingDTOMap.put(productMapping.getSupplierProductId(), productMapping.toDTO());
          productMappingDTOMap.put(productMapping.getCustomerProductId(), tempProductMappingDTOMap);
        } else {
          productMappingDTOMap.get(productMapping.getCustomerProductId()).put(productMapping.getSupplierProductId(), productMapping.toDTO());
        }
      }
    }
    return productMappingDTOMap;
  }

  @Override
  public Map<Long, ProductMappingDTO> getCustomerProductMappingDTODetailMap(Long customerShopId, Long supplierShopId, Long... productLocalInfoIds) {
    if (ArrayUtils.isEmpty(productLocalInfoIds)) {
      return new HashMap<Long, ProductMappingDTO>();
    }
    ProductWriter writer = productDaoManager.getWriter();
    List<ProductMapping> productMappings = writer.getCustomerProductMappingByCustomerProductLocalInfoIds(customerShopId, supplierShopId, productLocalInfoIds);
    Set<Long> totalProductIds = new HashSet<Long>();
    if (CollectionUtils.isNotEmpty(productMappings)) {
      for (ProductMapping productMapping : productMappings) {
        totalProductIds.add(productMapping.getCustomerProductId());
        totalProductIds.add(productMapping.getSupplierProductId());
      }
    }
    Map<Long, ProductDTO> productDTOMap = new HashMap<Long, ProductDTO>();
    if (!totalProductIds.isEmpty()) {
      productDTOMap = getProductDTOMapByProductLocalInfoIds(totalProductIds);
    }
    Map<Long, ProductMappingDTO> productMappingDTOMap = new HashMap<Long, ProductMappingDTO>();
    if (CollectionUtils.isNotEmpty(productMappings)) {
      for (ProductMapping productMapping : productMappings) {
        ProductMappingDTO productMappingDTO = productMapping.toDTO();
        if (!productDTOMap.isEmpty()) {
          productMappingDTO.setCustomerProductDTO(productDTOMap.get(productMapping.getCustomerProductId()));
          productMappingDTO.setSupplierProductDTO(productDTOMap.get(productMapping.getSupplierProductId()));
        }
        productMappingDTOMap.put(productMapping.getCustomerProductId(), productMappingDTO);
      }
    }
    return productMappingDTOMap;
  }


  @Override
  public Map<Long, ProductMappingDTO> getSupplierProductMappingDTODetailMap(Long supplierShopId, Long customerShopId, Set<Long> productLocalInfoIds) {
    if (productLocalInfoIds == null || productLocalInfoIds.isEmpty()) {
      return new HashMap<Long, ProductMappingDTO>();
    }
    ProductWriter writer = productDaoManager.getWriter();
    List<ProductMapping> productMappings = writer.getSupplierProductMappingByProductIds(supplierShopId, customerShopId, productLocalInfoIds);
    Set<Long> totalProductIds = new HashSet<Long>();
    if (CollectionUtils.isNotEmpty(productMappings)) {
      for (ProductMapping productMapping : productMappings) {
        totalProductIds.add(productMapping.getCustomerProductId());
        totalProductIds.add(productMapping.getSupplierProductId());
      }
    }
    Map<Long, ProductDTO> productDTOMap = new HashMap<Long, ProductDTO>();
    if (!totalProductIds.isEmpty()) {
      productDTOMap = getProductDTOMapByProductLocalInfoIds(totalProductIds);
    }
    Map<Long, ProductMappingDTO> productMappingDTOMap = new HashMap<Long, ProductMappingDTO>();
    if (CollectionUtils.isNotEmpty(productMappings)) {
      for (ProductMapping productMapping : productMappings) {
        ProductMappingDTO productMappingDTO = productMapping.toDTO();
        if (!productDTOMap.isEmpty()) {
          productMappingDTO.setCustomerProductDTO(productDTOMap.get(productMapping.getCustomerProductId()));
          productMappingDTO.setSupplierProductDTO(productDTOMap.get(productMapping.getSupplierProductId()));
        }
        productMappingDTOMap.put(productMapping.getSupplierProductId(), productMappingDTO);
      }
    }
    return productMappingDTOMap;
  }

  @Override
  public List<ProductDTO> getProductInfo(ProductSearchCondition conditionDTO){
    ProductWriter writer = productDaoManager.getWriter();
    List<Object[]> results=writer.getProductInfo(conditionDTO);
    List<ProductDTO> productDTOs=new ArrayList<ProductDTO>();
    if(CollectionUtil.isNotEmpty(results)){
      for(Object[] result:results){
        if(result==null){
          continue;
        }
        ProductLocalInfo localInfo= (ProductLocalInfo)result[0];
        Product product= (Product)result[1];
        ProductDTO productDTO=product.toDTO();
        productDTO.setProductLocalInfoDTO(localInfo.toDTO());
        productDTO.setProductInfo(productDTO.generateProductInfo());
        productDTOs.add(productDTO);
      }

    }
    return productDTOs;
  }

  @Override
  public Map<Long, ProductMappingDTO> getCustomerProductMappingDTOMap(Long customerShopId, Long supplierShopId, Long... customerProductLocalInfoIds) {
    if (ArrayUtils.isEmpty(customerProductLocalInfoIds)) {
      return new HashMap<Long, ProductMappingDTO>();
    }
    ProductWriter writer = productDaoManager.getWriter();
    List<ProductMapping> productMappingList = writer.getCustomerProductMappingByCustomerProductLocalInfoIds(customerShopId, supplierShopId, customerProductLocalInfoIds);
    Map<Long, ProductMappingDTO> productMappingDTOMap = new HashMap<Long, ProductMappingDTO>();
    if (CollectionUtils.isNotEmpty(productMappingList)) {
      for (ProductMapping productMapping : productMappingList) {
        ProductMappingDTO productMappingDTO = productMapping.toDTO();
        productMappingDTOMap.put(productMapping.getCustomerProductId(), productMappingDTO);
      }
    }
    return productMappingDTOMap;
  }

  public List<ProductMapping> getProductMappings(ProductMappingDTO productMappingIndex) {
    ProductWriter writer = productDaoManager.getWriter();
    return writer.getProductMappings(productMappingIndex);
  }

  public Map<Long,ProductMappingDTO> getCustomerProductMappings(Long customerShopId,Long[] productIds) {
    ProductMappingDTO productMappingIndex=new ProductMappingDTO();
    productMappingIndex.setCustomerShopId(customerShopId);
    productMappingIndex.setCustomerProductIds(productIds);
    List<ProductMapping> productMappings=getProductMappings(productMappingIndex);
    Map<Long,ProductMappingDTO> productMappingDTOMap=new HashMap<Long,ProductMappingDTO>();
    if(CollectionUtil.isNotEmpty(productMappings)){
      for(ProductMapping productMapping:productMappings){
        if(productMapping==null) continue;
        productMappingDTOMap.put(productMapping.getCustomerProductId(),productMapping.toDTO());
      }
    }
    return productMappingDTOMap;
  }

  public void updateProductMappings(List<ProductMappingDTO> productMappingDTOs) throws BcgogoException {
    if (CollectionUtil.isEmpty(productMappingDTOs)) {
      return;
    }
    ProductWriter writer = productDaoManager.getWriter();
    ProductMapping productMapping;
    Object status = writer.begin();
    try {
      for (ProductMappingDTO productMappingDTO : productMappingDTOs) {
        productMapping = writer.getById(ProductMapping.class, productMappingDTO.getId());
        if (null == productMapping || ProductStatus.DISABLED.equals(productMapping.getStatus())) {
          continue;
        }
        productMapping.setCustomerProductId(productMappingDTO.getCustomerProductId());
        productMapping.setSupplierProductId(productMappingDTO.getSupplierProductId());
        productMapping.setCustomerShopId(productMapping.getCustomerShopId());
        productMapping.setSupplierShopId(productMappingDTO.getSupplierShopId());
        productMapping.setSupplierId(productMappingDTO.getSupplierId());
        productMapping.setTradeStatus(productMappingDTO.getTradeStatus());
        productMapping.setCustomerLastPurchasePrice(productMappingDTO.getCustomerLastPurchasePrice());
        productMapping.setCustomerLastPurchaseDate(productMappingDTO.getCustomerLastPurchaseDate());
        productMapping.setCustomerLastPurchaseAmount(productMappingDTO.getCustomerLastPurchaseAmount());
        writer.update(productMapping);
      }
      writer.commit(status);
    } catch (Exception e) {
      writer.rollback(status);
      throw new BcgogoException(e.getMessage());
    }
  }

  @Override
  public void updateTradePrice(ProductDTO[] productDTOs, Long shopId) throws Exception {
    if (ArrayUtils.isEmpty(productDTOs)) {
      return;
    }
    Long[] productIds = new Long[productDTOs.length];
    Map<Long, ProductDTO> productDTOMap = new HashMap<Long, ProductDTO>(productDTOs.length * 2, 0.75f);
    for (int i = 0, len = productDTOs.length; i < len; i++) {
      productIds[i] = productDTOs[i].getProductLocalInfoId();
      productDTOMap.put(productDTOs[i].getProductLocalInfoId(), productDTOs[i]);
    }
    ProductWriter writer = productDaoManager.getWriter();
    Object status = writer.begin();
    try {
      List<ProductLocalInfo> productLocalInfoList = writer.getProductLocalInfoByIds(shopId, productIds);
      if (CollectionUtils.isNotEmpty(productLocalInfoList)) {
        for (ProductLocalInfo productLocalInfo : productLocalInfoList) {
          if (productDTOMap != null) {
            productLocalInfo.setTradePrice((productDTOMap.get(productLocalInfo.getId())).getTradePrice());
            writer.update(productLocalInfo);
          }
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void updateStorageBin(ProductDTO[] productDTOs, Long shopId) throws Exception {
    if (productDTOs == null && productDTOs.length == 0) {
      return;
    }
    Long[] productIds = new Long[productDTOs.length];
    Map<Long, ProductDTO> productDTOMap = new HashMap<Long, ProductDTO>(productDTOs.length * 2, 0.75f);
    for (int i = 0, len = productDTOs.length; i < len; i++) {
      productIds[i] = productDTOs[i].getProductLocalInfoId();
      productDTOMap.put(productDTOs[i].getProductLocalInfoId(), productDTOs[i]);
    }
    ProductWriter writer = productDaoManager.getWriter();
    Object status = writer.begin();
    try {
      List<ProductLocalInfo> productLocalInfoList = writer.getProductLocalInfoByIds(shopId, productIds);
      if (CollectionUtils.isNotEmpty(productLocalInfoList)) {
        for (ProductLocalInfo productLocalInfo : productLocalInfoList) {
          if (productDTOMap != null) {
            productLocalInfo.setStorageBin((productDTOMap.get(productLocalInfo.getId())).getStorageBin());
            writer.update(productLocalInfo);
          }
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void updateProductInSalesAmount(Long shopId, ProductDTO[] productDTOs) throws Exception {
    if (ArrayUtils.isEmpty(productDTOs)) {
      return;
    }
    ProductWriter writer = productDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Long[] productLocalInfoIds = new Long[productDTOs.length];
      Map<Long, ProductDTO> productDTOMap = new HashMap<Long, ProductDTO>(productDTOs.length * 2, 0.75f);
      for (int i = 0, len = productDTOs.length; i < len; i++) {
        productLocalInfoIds[i] = productDTOs[i].getProductLocalInfoId();
        productDTOMap.put(productDTOs[i].getProductLocalInfoId(), productDTOs[i]);
      }
      List<ProductLocalInfo> productLocalInfoList = writer.getProductLocalInfoByIds(shopId, productLocalInfoIds);
      if (CollectionUtils.isNotEmpty(productLocalInfoList)) {
        for (ProductLocalInfo productLocalInfo : productLocalInfoList) {
          productLocalInfo.setInSalesAmount((productDTOMap.get(productLocalInfo.getId())).getInSalesAmount());
          writer.update(productLocalInfo);
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

@Override
public Result updateProductGuaranteePeriod(Result result,Long shopId, ProductDTO[] productDTOs) throws Exception {
  if(ArrayUtil.isEmpty(productDTOs)){
    return result.LogErrorMsg("选择商品不能为空。");
  }
  ProductWriter writer = productDaoManager.getWriter();
  Object status = writer.begin();
  try {
    Long[] productLocalInfoIds = new Long[productDTOs.length];
    Map<Long, ProductDTO> productDTOMap = new HashMap<Long, ProductDTO>(productDTOs.length * 2, 0.75f);
    for (int i = 0, len = productDTOs.length; i < len; i++) {
      productLocalInfoIds[i] = productDTOs[i].getProductLocalInfoId();
      productDTOMap.put(productDTOs[i].getProductLocalInfoId(), productDTOs[i]);
    }
    List<ProductLocalInfo> productLocalInfoList = writer.getProductLocalInfoByIds(shopId, productLocalInfoIds);
    if (CollectionUtils.isNotEmpty(productLocalInfoList)) {
      for (ProductLocalInfo productLocalInfo : productLocalInfoList) {
        productLocalInfo.setGuaranteePeriod((productDTOMap.get(productLocalInfo.getId())).getGuaranteePeriod());
        writer.update(productLocalInfo);
      }
    }
    writer.commit(status);
    result.setDataList(productLocalInfoIds);
    return result;
  } finally {
    writer.rollback(status);
  }
}

  @Override
public Result updateProductInSalesPrice(Result result,Long shopId, ProductDTO[] productDTOs) throws Exception {
  if(ArrayUtil.isEmpty(productDTOs)){
    return result.LogErrorMsg("选择商品不能为空。");
  }
  ProductWriter writer = productDaoManager.getWriter();
  Object status = writer.begin();
  try {
    Long[] productLocalInfoIds = new Long[productDTOs.length];
    Map<Long, ProductDTO> productDTOMap = new HashMap<Long, ProductDTO>(productDTOs.length * 2, 0.75f);
    for (int i = 0, len = productDTOs.length; i < len; i++) {
      productLocalInfoIds[i] = productDTOs[i].getProductLocalInfoId();
      productDTOMap.put(productDTOs[i].getProductLocalInfoId(), productDTOs[i]);
    }
    List<ProductLocalInfo> productLocalInfoList = writer.getProductLocalInfoByIds(shopId, productLocalInfoIds);
    if (CollectionUtils.isNotEmpty(productLocalInfoList)) {
      for (ProductLocalInfo productLocalInfo : productLocalInfoList) {
        productLocalInfo.setInSalesPrice((productDTOMap.get(productLocalInfo.getId())).getInSalesPrice());
        writer.update(productLocalInfo);
      }
    }
    writer.commit(status);
    result.setDataList(productLocalInfoIds);
    return result;
  } finally {
    writer.rollback(status);
  }
}

  @Override
  public void mergeCacheProductDTO(Long shopId, List<Long> addedProductLocalInfoIdList, List<Long> updatedProductLocalInfoIdList,
                                   List<ProductDTO> productDTOs) {
    LOG.debug("addedProductLocalInfoIdList cache中新增的产品数量:" + addedProductLocalInfoIdList.size());
    LOG.debug("updatedProductLocalInfoIdList cache中更新的并且跟solr有交集的产品数量:" + updatedProductLocalInfoIdList.size());
    for (ProductDTO productDTO : productDTOs) {
      LOG.debug("productDTOs solr搜索出的产品:" + productDTO.getId());
    }
    ISearchService searchService = ServiceManager.getService(ISearchService.class);
    if (CollectionUtils.isNotEmpty(updatedProductLocalInfoIdList) && CollectionUtils.isNotEmpty(productDTOs)) {
      Set<Long> updatedProductLocalInfoIdSet = new HashSet<Long>(updatedProductLocalInfoIdList.size());
      for (Long temp : updatedProductLocalInfoIdList) {
        if (temp != null) {
          updatedProductLocalInfoIdSet.add(temp);
        }
      }
      Map<Long, ProductDTO> updatedProductMap = getProductDTOMapByProductLocalInfoIds(new HashSet<Long>(updatedProductLocalInfoIdList));
      List<InventorySearchIndex> inventorySearchIndexList = searchService.searchInventorySearchIndexByProductIds(shopId, updatedProductLocalInfoIdList.toArray(new Long[updatedProductLocalInfoIdList.size()]));
      Map<Long, Integer> productDTOIndexMap = new HashMap<Long, Integer>();
      for (ProductDTO productDTO : productDTOs) {
        productDTOIndexMap.put(productDTO.getProductLocalInfoId(), productDTOs.indexOf(productDTO));
      }
      Integer tempProductDTOIndex = null;
      for (InventorySearchIndex inventorySearchIndex : inventorySearchIndexList) {
        tempProductDTOIndex = productDTOIndexMap.get(inventorySearchIndex.getProductId());
        if (tempProductDTOIndex != null) {
          ProductDTO productDTOFromInventorySearchIndex = new ProductDTO(inventorySearchIndex.toDTO());
          if (updatedProductMap != null) {
            ProductDTO updatedProductDTO = updatedProductMap.get(inventorySearchIndex.getProductId());
            if (updatedProductDTO != null) {
              productDTOFromInventorySearchIndex.setTradePrice(updatedProductDTO.getTradePrice());
              productDTOFromInventorySearchIndex.setStorageBin(updatedProductDTO.getStorageBin());
              productDTOFromInventorySearchIndex.setSellUnit(updatedProductDTO.getSellUnit());
              productDTOFromInventorySearchIndex.setStorageUnit(updatedProductDTO.getStorageUnit());
            }
          }
          productDTOs.set(tempProductDTOIndex, productDTOFromInventorySearchIndex);
        }
      }
    }

    if (CollectionUtils.isNotEmpty(addedProductLocalInfoIdList)) {
      List<InventorySearchIndex> inventorySearchIndexList = searchService.searchInventorySearchIndexByProductIds(shopId, addedProductLocalInfoIdList.toArray(new Long[addedProductLocalInfoIdList.size()]));
      Map<Long, ProductDTO> addedProductMap = getProductDTOMapByProductLocalInfoIds(new HashSet<Long>(addedProductLocalInfoIdList));
      for (InventorySearchIndex inventorySearchIndex : inventorySearchIndexList) {
        ProductDTO productDTOFromInventorySearchIndex = new ProductDTO(inventorySearchIndex.toDTO());
        if (addedProductMap != null) {
          ProductDTO addedProductDTO = addedProductMap.get(inventorySearchIndex.getProductId());
          if (addedProductDTO != null) {
            productDTOFromInventorySearchIndex.setTradePrice(addedProductDTO.getTradePrice());
            productDTOFromInventorySearchIndex.setStorageBin(addedProductDTO.getStorageBin());
            productDTOFromInventorySearchIndex.setSellUnit(addedProductDTO.getSellUnit());
            productDTOFromInventorySearchIndex.setStorageUnit(addedProductDTO.getStorageUnit());
          }
        }
        productDTOs.add(productDTOFromInventorySearchIndex);
      }
    }
  }


  @Override
  public ProductDTO updateCommodityCodeByProductLocalInfoId(Long shopId, Long productLocalInfoId, String commodityCode) {
    ProductWriter writer = productDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Object[] objects = writer.getProductByProductLocalInfoId(productLocalInfoId,shopId);
      if (objects == null || objects[0] == null || objects[1] == null) {
        LOG.error("updateCommodityCodeByProductLocalInfoId can't find product，shopId：{},productLocalInfoId:{},commodityCode:{}",
            new Object[]{shopId, productLocalInfoId, commodityCode});
        return null;
      }
      Product product = (Product) objects[0];
      if (StringUtils.isNotBlank(commodityCode)) {
        product.setCommodityCode(commodityCode);
      } else {
        product.setCommodityCode(null);
      }
      writer.update(product);
      writer.commit(status);
      return product.toDTO();
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public ProductDTO getProductDTOByCommodityCode(long shopId, String commodityCode) throws Exception {
    if (StringUtils.isBlank(commodityCode)) {
      return null;
    }
    ProductWriter writer = productDaoManager.getWriter();
    return writer.getProductInfoByCommodityCode(shopId, commodityCode);
  }

  @Override
  public List<ProductDTO> getProductDTOsByCommodityCodes(Long shopId, String... commodityCodes) throws Exception {
    return productDaoManager.getWriter().getProductDTOsByCommodityCodes(shopId, commodityCodes);
  }

  @Override
  public Map<String, ProductDTO> getProductDTOMapsByCommodityCodes(Long shopId, String... commodityCodes) throws Exception {
    List<ProductDTO> productDTOs = productDaoManager.getWriter().getProductDTOsByCommodityCodes(shopId, commodityCodes);
    Map<String, ProductDTO> productDTOMap = new HashMap<String, ProductDTO>();
    if (CollectionUtils.isNotEmpty(productDTOs)) {
      for (ProductDTO productDTO : productDTOs) {
        productDTOMap.put(productDTO.getCommodityCode(), productDTO);
      }
    }
    return productDTOMap;
  }

  /**
   * 包括更改product.product表和search.inventory_search_index表
   *
   * @param shopId
   * @param productStatus
   * @param userDTO
   * @param productId
   * @throws Exception
   */
  @Override
  public void setProductStatus(Long shopId, ProductStatus productStatus, UserDTO userDTO, Long... productId) throws Exception {
    ProductWriter writer = productDaoManager.getWriter();
    Object status = writer.begin();
    try {
      List<Product> products = writer.getProductByProductLocalInfoIds(shopId, productId);
      if (CollectionUtils.isNotEmpty(products)) {
        for (Product product : products) {
          product.setStatus(productStatus);
          writer.update(product);
          if (ProductStatus.DISABLED.equals(productStatus)) {
            OldProductModifyLog oldProductModifyLog = new OldProductModifyLog();
            oldProductModifyLog.setProductId(product.getId());
            oldProductModifyLog.setModificationTime(System.currentTimeMillis());
            oldProductModifyLog.setOperation(productStatus.name());
            if (userDTO != null) {
              oldProductModifyLog.setShopId(userDTO.getShopId());
              oldProductModifyLog.setUserId(userDTO.getId());
            }
            writer.save(oldProductModifyLog);
          }
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }

    SearchDaoManager searchDaoManager = ServiceManager.getService(SearchDaoManager.class);
    SearchWriter searchWriter = searchDaoManager.getWriter();
    Object searchStatus = searchWriter.begin();
    try {
      List<InventorySearchIndex> inventorySearchIndexes = searchWriter.getInventorySearchIndexByProductLocalInfoIds(shopId, productId);
      if (CollectionUtils.isNotEmpty(inventorySearchIndexes)) {
        for (InventorySearchIndex inventorySearchIndex : inventorySearchIndexes) {
          if (inventorySearchIndex != null) {
            inventorySearchIndex.setStatus(productStatus);
            searchWriter.update(inventorySearchIndex);
          }
        }
      }
      searchWriter.commit(searchStatus);
    } finally {
      searchWriter.rollback(searchStatus);
    }
  }

  @Override
  public Long getProductKindId(Long shopId, String productKind) {
    Long kindId = null;
    if (StringUtils.isBlank(productKind)) {
      return null;
    }
    ProductWriter writer = productDaoManager.getWriter();
    //先根据shopId和分类名称判断该分类是否可用
    kindId = writer.getKindIdByName(shopId, productKind);
    if (kindId == null) {
      //再根据shopId和分类名称查找是否有被逻辑删除过的分类
      kindId = writer.getKindIdByDisabledName(shopId, productKind);
      //如果该分类也未被逻辑删除过，则新增一条
      if (kindId == null) {
        Object status = writer.begin();
        try {
          Kind kind = new Kind();
          kind.setName(productKind);
          kind.setNameEn(PinyinUtil.converterToFirstSpell(productKind).toUpperCase());
          kind.setShopId(shopId);
          kind.setStatus(KindStatus.ENABLE.toString());
          writer.save(kind);
          writer.commit(status);
          kindId = kind.getId();
        } finally {
          writer.rollback(status);
        }
      } else {
        //如果该分类名已被逻辑删除，则恢复其可用状态
        Kind kind = writer.getById(Kind.class, kindId);
        kind.setStatus(KindStatus.ENABLE.toString());
        Object status = writer.begin();
        try {
          writer.update(kind);
          writer.commit(status);
        } finally {
          writer.rollback(status);
        }
      }
    }
    return kindId;
  }

  @Override
  public Map<String, KindDTO> batchSaveAndGetProductKind(Long shopId, Set<String> kindNames) throws Exception {
    Map<String, KindDTO> kindDTOMap = new HashMap<String, KindDTO>();
    if(shopId == null || CollectionUtils.isEmpty(kindNames)){
      return kindDTOMap;
    }
    ProductWriter writer = productDaoManager.getWriter();
    Object status = writer.begin();
    try{
      Map<String,Kind> kindMap = new HashMap<String, Kind>();
      List<Kind> kindList = writer.getKindByNames(shopId,kindNames);
      if(CollectionUtil.isNotEmpty(kindList)){
        for(Kind kind : kindList){
          kindMap.put(kind.getName(), kind);
        }
      }
      for(String kineName : kindNames){
         Kind kind = kindMap.get(kineName);
        if (kind == null) {
          kind = new Kind();
          kind.setName(kineName);
          kind.setNameEn(PinyinUtil.converterToFirstSpell(kineName).toUpperCase());
          kind.setShopId(shopId);
          kind.setStatus(KindStatus.ENABLE.toString());
          writer.save(kind);
          kindMap.put(kineName,kind);
          kindDTOMap.put(kineName,kind.toKindDTO());
        }else{
          if(KindStatus.DISABLED.name().equals(kind.getStatus())){
            kind.setStatus(KindStatus.ENABLE.name());
            writer.update(kind);
          }
          kindDTOMap.put(kineName,kind.toKindDTO());
        }
      }

      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    return kindDTOMap;
  }

  @Override
  public Long getKindIdByName(Long shopId, String kindName) {
    Long kindId = null;
    if (StringUtils.isBlank(kindName)) {
      return null;
    }
    ProductWriter writer = productDaoManager.getWriter();
    kindId = writer.getKindIdByName(shopId, kindName);
    return kindId;
  }

  @Override
  public String getKindNameById(Long kindId) {
    ProductWriter writer = productDaoManager.getWriter();
    Kind kind = writer.getById(Kind.class, kindId);
    return kind == null ? null : kind.getName();
  }

  @Override
  public List<String> getProductKindsRecentlyUsed(Long shopId) {
    List<String> kindNameList = new ArrayList<String>();
    ProductWriter writer = productDaoManager.getWriter();
    kindNameList = writer.getProductKindsRecentlyUsed(shopId);
    return kindNameList;
  }

  @Override
  public void updateKind(Long kindId, String newKindName) {
    ProductWriter writer = productDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Kind kind = writer.getById(Kind.class, kindId);
      kind.setName(newKindName);
      kind.setNameEn(PinyinUtil.converterToFirstSpell(newKindName).toUpperCase());
      writer.update(kind);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public List<String> getProductKindsWithFuzzyQuery(Long shopId, String keyword) {
    List<String> kindNameList = new ArrayList<String>();
    ProductWriter writer = productDaoManager.getWriter();
    kindNameList = writer.getProductKindsWithFuzzyQuery(shopId, keyword);
    return kindNameList;
  }

  @Override
  public void updateMultipleProductKind(Long shopId, Long[] idList, Long newKindId) {
    ProductWriter writer = productDaoManager.getWriter();
    Object status = writer.begin();
    try {
      if (idList != null && idList.length > 0) {
        for (int i = 0; i < idList.length; i++) {
          Product p = writer.getById(Product.class, idList[i]);
          p.setKindId(newKindId);
          writer.update(p);
        }
        writer.commit(status);
      }
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void deleteProductKind(Long shopId, String kindName) {
    ProductWriter writer = productDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Long kindId = writer.getKindIdByName(shopId, kindName);
      Kind kind = writer.getById(Kind.class, kindId);
      kind.setStatus(KindStatus.DISABLED.toString());
      writer.update(kind);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  public Map<Long, KindDTO> getProductKindById(Long... kindId) {
    if (ArrayUtils.isEmpty(kindId)) return new HashMap<Long, KindDTO>();
    ProductWriter writer = productDaoManager.getWriter();
    Map<Long, KindDTO> kindDTOMap = new HashMap<Long, KindDTO>();
    List<Kind> kindList = writer.getProductKindById(kindId);
    if (CollectionUtils.isNotEmpty(kindList)) {
      for (Kind kind : kindList) {
        kindDTOMap.put(kind.getId(), kind.toKindDTO());
      }
    }
    return kindDTOMap;
  }

  @Override
  public Map<Long, KindDTO> getKindDTOMap(Long shopId, Set<Long> ids) {
    if(shopId == null || CollectionUtils.isEmpty(ids)) {
      return new HashMap<Long, KindDTO>();
    }
    ProductWriter writer = productDaoManager.getWriter();
    Map<Long, KindDTO> kindDTOMap = new HashMap<Long, KindDTO>();
    List<Kind> kindList = writer.getProductKindByIdAndShopId(shopId, ids);
    if (CollectionUtils.isNotEmpty(kindList)) {
      for (Kind kind : kindList) {
        kindDTOMap.put(kind.getId(), kind.toKindDTO());
      }
    }
    return kindDTOMap;
  }

  public Map<Long, KindDTO> getAllEnabledProductKindByShop(Long shopId) {
    ProductWriter writer = productDaoManager.getWriter();
    Map<Long, KindDTO> kindDTOMap = new HashMap<Long, KindDTO>();
    List<Kind> kindList = writer.getAllEnabledProductKindByShop(shopId);
    if (CollectionUtils.isNotEmpty(kindList)) {
      for (Kind kind : kindList) {
        kindDTOMap.put(kind.getId(), kind.toKindDTO());
      }
    }
    return kindDTOMap;
  }

  @Override
  public List<ProductDTO> getProductDTOsByProductKindId(Long shopId, Long... productKindId) throws Exception {
    if (ArrayUtils.isEmpty(productKindId)) {
      return null;
    }
    ProductWriter writer = productDaoManager.getWriter();
    return writer.getProductDTOsByProductKindId(shopId, productKindId);
  }

  @Override
  public ProductLocalInfoDTO updateProductLocalInfoCategory(Long shopId, Long productLocalInfoId, Long categoryId) {

    if (null == shopId || null == productLocalInfoId) {
      return null;
    }

    ProductWriter writer = productDaoManager.getWriter();

    Object status = writer.begin();
    try {
      List<ProductLocalInfo> productLocalInfoList = writer.getProductLocalInfoById(productLocalInfoId, shopId);

      if (CollectionUtils.isEmpty(productLocalInfoList)) {
        return null;
      }

      ProductLocalInfo productLocalInfo = productLocalInfoList.get(0);

      productLocalInfo.setBusinessCategoryId(categoryId);

      writer.update(productLocalInfo);

      writer.commit(status);

      return productLocalInfo.toDTO();
    } finally {
      writer.rollback(status);
    }

  }

  @Override
  public boolean checkSameProduct(Long shopId, ProductDTO productDTO) {
    ProductWriter writer = productDaoManager.getWriter();
    return writer.checkSameProduct(shopId, productDTO);
  }

  @Override
  public KindDTO getEnabledKindDTOById(Long shopId, Long id) {
    ProductWriter writer = productDaoManager.getWriter();

    Kind kind = writer.getEnabledKindDTOById(shopId, id);

    if (null == kind) {
      return null;
    }
    return kind.toKindDTO();
  }

  @Override
  public void deleteProductLocalInfoCategoryId(Long shopId, Long categoryId) {
    if (null == categoryId) {
      return;
    }

    ProductWriter writer = productDaoManager.getWriter();

    Object status = writer.begin();

    try {

      List<ProductLocalInfo> productLocalInfoList = writer.getProductLocalInfoByCategoryId(shopId, categoryId);

      if (CollectionUtils.isNotEmpty(productLocalInfoList)) {
        for (ProductLocalInfo productLocalInfo : productLocalInfoList) {
          productLocalInfo.setBusinessCategoryId(null);
          writer.update(productLocalInfo);
        }
      }

      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void saveOrUpdateProductForPurchaseOrder(PurchaseOrderDTO purchaseOrderDTO) throws Exception {
    IProductService productService = ServiceManager.getService(IProductService.class);
    //新增或者更新产品信息
    List<ProductDTO> toUpdateProductDTOs = new ArrayList<ProductDTO>();

    for (PurchaseOrderItemDTO purchaseOrderItemDTO : purchaseOrderDTO.getItemDTOs()) {
      if (StringUtils.isBlank(purchaseOrderItemDTO.getProductName())) {
        continue;
      }

      if (StringUtils.isNotBlank(purchaseOrderItemDTO.getProductKind())) {
        purchaseOrderItemDTO.setProductKindId(productService.getProductKindId(purchaseOrderDTO.getShopId(), purchaseOrderItemDTO.getProductKind()));
      }

      if (purchaseOrderDTO.getSupplierShopId() != null && purchaseOrderItemDTO.getSupplierProductId() == null) {
        ProductDTO searchSupplierProductCondition = new ProductDTO(purchaseOrderDTO.getShopId(), purchaseOrderItemDTO);
        List<ProductDTO> supplierProductDTO = getProductDTOsBy7P(purchaseOrderDTO.getSupplierShopId(), searchSupplierProductCondition);
        if (CollectionUtils.isNotEmpty(supplierProductDTO)) {
          ProductLocalInfoDTO productLocalInfoDTO = getProductLocalInfoByProductId(supplierProductDTO.get(0).getId(),
              supplierProductDTO.get(0).getShopId());
          if (productLocalInfoDTO != null) {
            purchaseOrderItemDTO.setSupplierProductId(productLocalInfoDTO.getId());
          }
        }
      }

      boolean isNewProductFlag = false;
      ProductDTO productDTO = new ProductDTO(purchaseOrderDTO.getShopId(), purchaseOrderItemDTO);
      productDTO.setProductLocalInfoId(purchaseOrderItemDTO.getProductId());
      productDTO.setKindId(purchaseOrderItemDTO.getProductKindId());
      if (purchaseOrderItemDTO.getProductId() == null) {
        isNewProductFlag = saveNewProduct(productDTO);
        purchaseOrderItemDTO.setProductId(productDTO.getProductLocalInfoId());
      }
      if (!isNewProductFlag) {
        toUpdateProductDTOs.add(productDTO);
      }
    }

    if (CollectionUtils.isNotEmpty(toUpdateProductDTOs)) {
      updateProductWithPurchaseOrder(purchaseOrderDTO.getShopId(), toUpdateProductDTOs.toArray(new ProductDTO[toUpdateProductDTOs.size()]));
    }
  }

  @Override
  public List<ProductDTO> getProductDTOsBy7P(Long shopId, ProductDTO searchCondition) {
    if (shopId == null || searchCondition == null) {
      return new ArrayList<ProductDTO>();
    }
    ProductWriter writer = productDaoManager.getWriter();
    List<Product> products = writer.getProductDTOsBy7P(shopId, searchCondition);
    List<ProductDTO> productDTOs = new ArrayList<ProductDTO>();
    if (CollectionUtils.isNotEmpty(products)) {
      for (Product product : products) {
        productDTOs.add(product.toDTO());
      }
    }
    return productDTOs;
  }

  @Override
  public void updateProductWithPurchaseOrder(Long shopId, ProductDTO... productDTOs) throws Exception {
    if (shopId == null || productDTOs == null || ArrayUtils.isEmpty(productDTOs)) {
      return;
    }
    Set<Long> productLocalInfoIds = new HashSet<Long>();
    Map<Long, ProductDTO> toUpdateProductMap = new HashMap<Long, ProductDTO>();
    for (ProductDTO productDTO : productDTOs) {
      if (productDTO.getProductLocalInfoId() != null) {
        toUpdateProductMap.put(productDTO.getProductLocalInfoId(), productDTO);
      }
    }
    productLocalInfoIds = toUpdateProductMap.keySet();
    if (CollectionUtils.isEmpty(productLocalInfoIds)) {
      return;
    }
    ProductWriter writer = productDaoManager.getWriter();
    Object status = writer.begin();
    try {
      List<Object[]> productDateList = writer.getProductDataByProductLocalInfoId(shopId, productLocalInfoIds.toArray(new Long[productLocalInfoIds.size()]));
      for (Object[] productDates : productDateList) {
        if (productDates != null && productDates.length == 2) {
          Product product = (Product) productDates[0];
          ProductLocalInfo productLocalInfo = (ProductLocalInfo) productDates[1];
          ProductDTO productDTO = toUpdateProductMap.get(productLocalInfo.getId());
          if (productDTO != null) {
            product.setCommodityCode(productDTO.getCommodityCode());
            //todo 下面的代码暂时注释掉，等在线采购出来之后需要重构
//            if (NumberUtil.doubleVal(productDTO.getTradePrice()) > 0.0001) {
//              if (UnitUtil.isStorageUnit(productDTO.getStorageUnit(), productLocalInfo.toDTO())) {
//                productLocalInfo.setTradePrice(NumberUtil.doubleVal(productDTO.getTradePrice()) * productLocalInfo.getRate());
//              } else {
//                productLocalInfo.setTradePrice(productDTO.getTradePrice());
//              }
//            }

            if (StringUtils.isBlank(productLocalInfo.getSellUnit()) && StringUtils.isNotBlank(productDTO.getSellUnit())) {
              productLocalInfo.setSellUnit(productDTO.getSellUnit());
              productLocalInfo.setStorageUnit(productDTO.getSellUnit());
            }
             //todo 下面的代码暂时处理，等在线采购出来之后需要重构
            if(productDTO.getKindId() != null){
              product.setKindId(productDTO.getKindId());
            }
            writer.update(product);
            writer.update(productLocalInfo);
          }
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void updateProductWithRepairOrder(Long shopId,Set<Long> deleteProductIds, ProductDTO... productDTOs) throws Exception {
    if (shopId == null || productDTOs == null || ArrayUtils.isEmpty(productDTOs)) {
      return;
    }
    Set<Long> productLocalInfoIds = new HashSet<Long>();
    Map<Long, ProductDTO> toUpdateProductMap = new HashMap<Long, ProductDTO>();
    for (ProductDTO productDTO : productDTOs) {
      if (productDTO.getProductLocalInfoId() != null) {
        toUpdateProductMap.put(productDTO.getProductLocalInfoId(), productDTO);
      }
    }
    productLocalInfoIds = toUpdateProductMap.keySet();
    if (CollectionUtils.isEmpty(productLocalInfoIds)) {
      return;
    }
    ProductWriter writer = productDaoManager.getWriter();
    Object status = writer.begin();
    try {
      List<Object[]> productDateList = writer.getProductDataByProductLocalInfoId(shopId, productLocalInfoIds.toArray(new Long[productLocalInfoIds.size()]));
      for (Object[] productDates : productDateList) {
        if (productDates != null && productDates.length == 2) {
          Product product = (Product) productDates[0];
          ProductLocalInfo productLocalInfo = (ProductLocalInfo) productDates[1];
          ProductDTO productDTO = toUpdateProductMap.get(productLocalInfo.getId());
          if (productDTO != null) {
            product.setCommodityCode(productDTO.getCommodityCode());
            if(ProductStatus.DISABLED.equals(product.getStatus())){
              if(deleteProductIds == null){
                deleteProductIds = new HashSet<Long>();
              }
              if(productLocalInfo != null) {
                deleteProductIds.add(productLocalInfo.getId());
              }
              product.setStatus(null);
            }
            if (StringUtils.isBlank(productLocalInfo.getSellUnit()) && StringUtils.isNotBlank(productDTO.getSellUnit())) {
              productLocalInfo.setSellUnit(productDTO.getSellUnit());
              productLocalInfo.setStorageUnit(productDTO.getSellUnit());
            }
            productLocalInfo.setBusinessCategoryId(productDTO.getBusinessCategoryId());
            writer.update(product);
            writer.update(productLocalInfo);
          }
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public Map<Long, ProductLocalInfoDTO> getProductLocalInfoMap(Long shopId, Long... productLocalInfoIds) {
    if (shopId == null || ArrayUtils.isEmpty(productLocalInfoIds)) {
      return new HashMap<Long, ProductLocalInfoDTO>();
    }
    ProductWriter writer = productDaoManager.getWriter();
    List<ProductLocalInfo> list = writer.getProductLocalInfoByIds(shopId, productLocalInfoIds);
    Map<Long, ProductLocalInfoDTO> productLocalInfoDTOMap = new HashMap<Long, ProductLocalInfoDTO>((int) (list.size() / 0.75f) + 1, 0.75f);
    if (CollectionUtils.isNotEmpty(list)) {
      for (ProductLocalInfo productLocalInfo : list) {
        productLocalInfoDTOMap.put(productLocalInfo.getId(), productLocalInfo.toDTO());
      }
    }
    return productLocalInfoDTOMap;
  }

  @Override
  public Map<Long, ProductLocalInfoDTO> getProductLocalInfoMapByProductIds(Long shopId, Set<Long> productIds) {
    if (shopId == null || CollectionUtils.isEmpty(productIds)) {
      return new HashMap<Long, ProductLocalInfoDTO>();
    }
    ProductWriter writer = productDaoManager.getWriter();
    List<ProductLocalInfo> list = writer.getProductLocalInfoByProductIds(shopId, productIds);
    Map<Long, ProductLocalInfoDTO> productLocalInfoDTOMap = new HashMap<Long, ProductLocalInfoDTO>((int) (list.size() / 0.75f) + 1, 0.75f);
    if (CollectionUtils.isNotEmpty(list)) {
      for (ProductLocalInfo productLocalInfo : list) {
        if(productLocalInfo.getProductId() != null){
          productLocalInfoDTOMap.put(productLocalInfo.getProductId(), productLocalInfo.toDTO());
        }
      }
    }
    return productLocalInfoDTOMap;
  }

  @Override
  public Map<Long, ProductLocalInfoDTO> getProductLocalInfoMap(Long... productLocalInfoIds) {
    if (ArrayUtils.isEmpty(productLocalInfoIds)) {
      return new HashMap<Long, ProductLocalInfoDTO>();
    }
    ProductWriter writer = productDaoManager.getWriter();
    List<ProductLocalInfo> list = writer.getProductLocalInfoByIds(productLocalInfoIds);
    Map<Long, ProductLocalInfoDTO> productLocalInfoDTOMap = new HashMap<Long, ProductLocalInfoDTO>((int) (list.size() / 0.75f) + 1, 0.75f);
    if (CollectionUtils.isNotEmpty(list)) {
      for (ProductLocalInfo productLocalInfo : list) {
        productLocalInfoDTOMap.put(productLocalInfo.getId(), productLocalInfo.toDTO());
      }
    }
    return productLocalInfoDTOMap;
  }

  @Override
  public void handleInSalesAmountByOrder(BcgogoOrderDto orderDto, double symbolNumber) throws Exception {
    if (Math.abs(symbolNumber) != 1) return;

    ProductWriter writer = productDaoManager.getWriter();
    Object status = writer.begin();
    try {
      List<Long> productIdList = orderDto.getProductIdList();
      List<ProductLocalInfo> productLocalInfoList = writer.getProductLocalInfoByIds(orderDto.getShopId(), productIdList.toArray(new Long[productIdList.size()]));
      Map<Long, ProductLocalInfo> productLocalInfoMap = ProductLocalInfo.listToProductLocalInfoIdKeyMap(productLocalInfoList);
      ProductLocalInfo productLocalInfo = null;
      for (BcgogoOrderItemDto itemDto : orderDto.getItemDTOs()) {
        productLocalInfo = productLocalInfoMap.get(itemDto.getProductId());
        if (productLocalInfo != null) {
          double itemAmount = itemDto.getAmount() * symbolNumber;
          if (UnitUtil.isStorageUnit(itemDto.getUnit(), productLocalInfo.toDTO())) {
            itemAmount = itemAmount * productLocalInfo.getRate();
          }
          double inSalesAmount = NumberUtil.doubleVal(productLocalInfo.getInSalesAmount()) + itemAmount;
          productLocalInfo.setInSalesAmount(inSalesAmount > 0 ? inSalesAmount : 0d);
          writer.update(productLocalInfo);
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void saveOrUpdateProductMapping(PurchaseInventoryDTO purchaseInventoryDTO) throws Exception {
    if (!(purchaseInventoryDTO != null && !ArrayUtils.isEmpty(purchaseInventoryDTO.getItemDTOs())
        && purchaseInventoryDTO.getShopId() != null && purchaseInventoryDTO.getSupplierShopId() != null)) {
      LOG.error("开始更新产品关系出错，purchaseOrderDTO ，itemDTOs，shopId，supplierShopId，存在空值");
      return;
    }
    ProductWriter writer = productDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Set<Long> customerProductIds = new HashSet<Long>();
      Set<Long> supplierProductIds = new HashSet<Long>();
      Set<Long> allProductIds = new HashSet<Long>();
      for (PurchaseInventoryItemDTO purchaseInventoryItemDTO : purchaseInventoryDTO.getItemDTOs()) {
        if (purchaseInventoryItemDTO.getProductId() != null && purchaseInventoryItemDTO.getSupplierProductId() != null) {
          customerProductIds.add(purchaseInventoryItemDTO.getProductId());
          supplierProductIds.add(purchaseInventoryItemDTO.getSupplierProductId());
          allProductIds.add(purchaseInventoryItemDTO.getProductId());
          allProductIds.add(purchaseInventoryItemDTO.getSupplierProductId());
        }
      }
      List<ProductMapping> productMappings = getProductMappings(purchaseInventoryDTO.getShopId(), purchaseInventoryDTO.getSupplierShopId(),
          allProductIds.toArray(new Long[allProductIds.size()]));
      Map<Long, ProductMappingDTO> productMappingDTOMap =
          getCustomerProductMappingDTODetailMap(purchaseInventoryDTO.getShopId(), purchaseInventoryDTO.getSupplierShopId(),
              customerProductIds.toArray(new Long[customerProductIds.size()]));
      Map<Long, ProductDTO> productDTOMap = getProductDTOMapByProductLocalInfoIds(purchaseInventoryDTO.getShopId(), customerProductIds);
      Map<Long, ProductDTO> allProductDTOMap = new HashMap<Long, ProductDTO>();
      allProductDTOMap.putAll(productDTOMap);
      productDTOMap = getProductDTOMapByProductLocalInfoIds(purchaseInventoryDTO.getSupplierShopId(), supplierProductIds);
      allProductDTOMap.putAll(productDTOMap);

      for (PurchaseInventoryItemDTO purchaseInventoryItemDTO : purchaseInventoryDTO.getItemDTOs()) {
        if(purchaseInventoryItemDTO.getSupplierProductId() == null || purchaseInventoryItemDTO.getProductId() == null){
          continue;
        }
        ProductMappingDTO productMappingDTO = productMappingDTOMap.get(purchaseInventoryItemDTO.getProductId());

        //本地产品不存在关系
        if (productMappingDTO == null) {
          //disable supplierProductId mapping
          if (CollectionUtils.isNotEmpty(productMappings)) {
            for (ProductMapping productMapping : productMappings) {
              if (productMapping.getSupplierProductId() != null
                  && productMapping.getSupplierProductId().equals(purchaseInventoryItemDTO.getSupplierProductId())
                  && ProductStatus.ENABLED.equals(productMapping.getStatus())
                  && productMapping.getCustomerShopId() != null
                  && productMapping.getSupplierShopId() != null
                  && productMapping.getCustomerShopId().equals(purchaseInventoryDTO.getShopId())
                  && productMapping.getSupplierShopId().equals(purchaseInventoryDTO.getSupplierShopId())) {
                productMapping.setStatus(ProductStatus.DISABLED);
                writer.update(productMapping);
                if (productMapping.getCustomerProductId() != null) {
                  ProductMappingDTO tempProductMappingDTO = productMapping.toDTO();
                  tempProductMappingDTO.setCustomerProductDTO(allProductDTOMap.get(productMapping.getCustomerProductId()));
                  tempProductMappingDTO.setSupplierProductDTO(allProductDTOMap.get(productMapping.getSupplierProductId()));
                  productMappingDTOMap.put(productMapping.getCustomerProductId(), tempProductMappingDTO);
                }
              }
            }
          }
          ProductMapping productMapping = new ProductMapping(purchaseInventoryDTO, purchaseInventoryItemDTO);
          productMapping.setTradeStatus(ProductStatus.TradeStatus.PURCHASE_INVENTORY);
          writer.save(productMapping);
          productMappings.add(productMapping);
          ProductMappingDTO tempProductMappingDTO = productMapping.toDTO();
          tempProductMappingDTO.setCustomerProductDTO(allProductDTOMap.get(productMapping.getCustomerProductId()));
          tempProductMappingDTO.setSupplierProductDTO(allProductDTOMap.get(productMapping.getSupplierProductId()));
          productMappingDTOMap.put(purchaseInventoryItemDTO.getProductId(), tempProductMappingDTO);
        } else {
          //本地产品已经存在关系且关系不成立
          if (!productMappingDTO.getSupplierProductId().equals(purchaseInventoryItemDTO.getSupplierProductId())
              || !productMappingDTO.isProductMappingEnabled()) {
            for (ProductMapping productMapping : productMappings) {
              if (productMapping.getCustomerProductId() != null
                  && productMapping.getCustomerProductId().equals(purchaseInventoryItemDTO.getProductId())
                  && ProductStatus.ENABLED.equals(productMapping.getStatus())
                  && productMapping.getCustomerShopId() != null
                  && productMapping.getSupplierShopId() != null
                  && productMapping.getCustomerShopId().equals(purchaseInventoryDTO.getShopId())
                  && productMapping.getSupplierShopId().equals(purchaseInventoryDTO.getSupplierShopId())) {
                productMapping.setStatus(ProductStatus.DISABLED);
                writer.update(productMapping);
                ProductMappingDTO tempProductMappingDTO = productMapping.toDTO();
                tempProductMappingDTO.setCustomerProductDTO(allProductDTOMap.get(productMapping.getCustomerProductId()));
                tempProductMappingDTO.setSupplierProductDTO(allProductDTOMap.get(productMapping.getSupplierProductId()));
                productMappingDTOMap.put(productMapping.getCustomerProductId(), tempProductMappingDTO);
              }
            }
            ProductMapping productMapping = new ProductMapping(purchaseInventoryDTO, purchaseInventoryItemDTO);
            productMapping.setTradeStatus(ProductStatus.TradeStatus.PURCHASE_INVENTORY);
            writer.save(productMapping);
            productMappings.add(productMapping);
            ProductMappingDTO tempProductMappingDTO = productMapping.toDTO();
            tempProductMappingDTO.setCustomerProductDTO(allProductDTOMap.get(productMapping.getCustomerProductId()));
            tempProductMappingDTO.setSupplierProductDTO(allProductDTOMap.get(productMapping.getSupplierProductId()));
            productMappingDTOMap.put(purchaseInventoryItemDTO.getProductId(), productMapping.toDTO());
          }else{
            for (ProductMapping productMapping : productMappings) {
              if (productMapping.getCustomerProductId() != null
                  && productMapping.getCustomerProductId().equals(purchaseInventoryItemDTO.getProductId())
                  && ProductStatus.ENABLED.equals(productMapping.getStatus())
                  && productMapping.getCustomerShopId() != null
                  && productMapping.getSupplierShopId() != null
                  && productMapping.getCustomerShopId().equals(purchaseInventoryDTO.getShopId())
                  && productMapping.getSupplierShopId().equals(purchaseInventoryDTO.getSupplierShopId())) {
                if(UnitUtil.isStorageUnit(purchaseInventoryItemDTO.getUnit(),purchaseInventoryItemDTO)){
                  productMapping.setCustomerLastPurchaseAmount(NumberUtil.round(purchaseInventoryItemDTO.getAmount()*purchaseInventoryItemDTO.getRate(),1));
                  productMapping.setCustomerLastPurchasePrice(NumberUtil.round(NumberUtil.doubleVal(purchaseInventoryItemDTO.getPurchasePrice())/purchaseInventoryItemDTO.getRate(),NumberUtil.MONEY_PRECISION));
                }else{
                  productMapping.setCustomerLastPurchaseAmount(purchaseInventoryItemDTO.getAmount());
                  productMapping.setCustomerLastPurchasePrice(NumberUtil.doubleVal(purchaseInventoryItemDTO.getPurchasePrice()));
                }

                productMapping.setCustomerLastPurchaseDate(purchaseInventoryDTO.getVestDate());
                writer.update(productMapping);
                ProductMappingDTO tempProductMappingDTO = productMapping.toDTO();
                tempProductMappingDTO.setCustomerProductDTO(allProductDTOMap.get(productMapping.getCustomerProductId()));
                tempProductMappingDTO.setSupplierProductDTO(allProductDTOMap.get(productMapping.getSupplierProductId()));
                productMappingDTOMap.put(productMapping.getCustomerProductId(), tempProductMappingDTO);
              }
            }
          }
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }

  }

  public void updateProductMappingForSalesReturn(Long customerShopId, Long supplierShopId, Map<Long, Long> customerSupplierProductMapping) {
    if (customerSupplierProductMapping.isEmpty()) {
      return;
    }
    ProductWriter writer = productDaoManager.getWriter();

    Object status = writer.begin();
    try {
      Set<Long> customerProductIdSet = customerSupplierProductMapping.keySet();
      Long[] productIds = customerProductIdSet.toArray(new Long[customerProductIdSet.size()]);
      List<ProductMapping> productMappings = getProductMappings(customerShopId, supplierShopId, productIds);
      for (ProductMapping productMapping : productMappings) {
        Long supplierProductId = customerSupplierProductMapping.get(productMapping.getCustomerProductId());
        if (supplierProductId != null && productMapping.getCustomerShopId().equals(customerShopId) && productMapping.getSupplierShopId().equals(supplierShopId)) {
          productMapping.setSupplierProductId(supplierProductId);
          writer.update(productMapping);
        }
      }
      writer.commit(status);
    } catch (Exception e) {
      LOG.error("ProductService.updateProductMappingForSalesReturn 出错.");
      LOG.error(e.getMessage(), e);
      writer.rollback(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public List<ProductMapping> getProductMappings(Long shopId, Long supplierShopId, Long... productIds) {
    if (shopId == null || supplierShopId == null || ArrayUtils.isEmpty(productIds)) {
      return new ArrayList<ProductMapping>();
    }
    return productDaoManager.getWriter().getProductMapping(shopId, supplierShopId, productIds);
  }

  @Override
  public ProductMapping getProductMappingById(long id) {
    return productDaoManager.getWriter().getById(ProductMapping.class,id);
  }

  @Override
  public void updateProductForSalesReturn(SalesReturnDTO salesReturnDTO) throws Exception {
    if (salesReturnDTO == null || salesReturnDTO.getShopId() == null) {
      return;
    }
    if (ArrayUtils.isEmpty(salesReturnDTO.getItemDTOs())) {
      return;
    }
    for (SalesReturnItemDTO itemDTO : salesReturnDTO.getItemDTOs()) {
      if (itemDTO.getProductId() == null) {
        continue;
      }
      ProductDTO productDTO = getProductByProductLocalInfoId(itemDTO.getProductId(), salesReturnDTO.getShopId());
      if (productDTO != null && productDTO.getStatus() == ProductStatus.DISABLED) {
        ProductDTO commodityProductDTO = getProductDTOByCommodityCode(salesReturnDTO.getShopId(), productDTO.getCommodityCode());
        if (commodityProductDTO != null) {    //已删除的商品的商品编码已经被其他商品占用
          itemDTO.setCommodityCode(null);
        }
      }
      updateCommodityCodeByProductLocalInfoId(salesReturnDTO.getShopId(), itemDTO.getProductId(), itemDTO.getCommodityCode());
    }
  }

  private List<NormalProductVehicleBrandModelDTO> getNormalProductVehicleBrandModelDTOByNormalProductId(Long... normalProductId){
    if(ArrayUtils.isEmpty(normalProductId)) return null;
    ProductWriter writer = productDaoManager.getWriter();
    List<NormalProductVehicleBrandModel> bmList=writer.getNormalProductVehicleBrandModelByNormalProductId(normalProductId);
    List<NormalProductVehicleBrandModelDTO> bmDTOList= new ArrayList<NormalProductVehicleBrandModelDTO>();
    if(CollectionUtil.isNotEmpty(bmList)){
      for(NormalProductVehicleBrandModel bm:bmList){
        bmDTOList.add(bm.toDTO());
      }
    }
    return bmDTOList;
  }
  @Override
  public Map<Long,List<NormalProductVehicleBrandModelDTO>> getNormalProductVehicleBrandModelDTOMapByNormalProductId(Long... normalProductId){
    Map<Long,List<NormalProductVehicleBrandModelDTO>> map = new HashMap<Long, List<NormalProductVehicleBrandModelDTO>>();
    if(ArrayUtils.isEmpty(normalProductId)) return map;
    ProductWriter writer = productDaoManager.getWriter();
    List<NormalProductVehicleBrandModelDTO> normalProductVehicleBrandModelDTOList=getNormalProductVehicleBrandModelDTOByNormalProductId(normalProductId);
    List<NormalProductVehicleBrandModelDTO> bmDTOList= null;
    if(CollectionUtil.isNotEmpty(normalProductVehicleBrandModelDTOList)){
      for(NormalProductVehicleBrandModelDTO bmDTO:normalProductVehicleBrandModelDTOList){
        bmDTOList = map.get(bmDTO.getNormalProductId());
        if(bmDTOList==null)
          bmDTOList = new ArrayList<NormalProductVehicleBrandModelDTO>();
        bmDTOList.add(bmDTO);
        map.put(bmDTO.getNormalProductId(),bmDTOList);
      }
    }
    return map;
  }

  @Override
  public List<NormalProductDTO> getNormalProductDTO(ProductSearchCondition searchCondition) {
    ProductWriter writer = productDaoManager.getWriter();
    List<NormalProduct> normalProductList = writer.getNormalProduct(searchCondition);

    if (CollectionUtils.isEmpty(normalProductList)) {
      return null;
    }

    List<NormalProductDTO> normalProductDTOList = new ArrayList<NormalProductDTO>();

    List<Long> thirdIds = new ArrayList<Long>();
    List<Long> normalProductIdList = new ArrayList<Long>();

    for (NormalProduct normalProduct : normalProductList) {
      if (null != normalProduct.getProductCategoryId()) {
        thirdIds.add(normalProduct.getProductCategoryId());
      }
      normalProductIdList.add(normalProduct.getId());
    }

    List<Object[]> objectList = writer.countProductByNormalProductId(normalProductIdList.toArray(new Long[normalProductIdList.size()]));
    Map<Long,Integer> bindingShopProductCountMap = new HashMap<Long, Integer>();
    for(Object[] objects:objectList){
      bindingShopProductCountMap.put((Long)objects[0],NumberUtil.intValue(objects[1]));
    }

    Map<Long,List<NormalProductVehicleBrandModelDTO>> normalProductVehicleBrandModelDTOMap = this.getNormalProductVehicleBrandModelDTOMapByNormalProductId(normalProductIdList.toArray(new Long[normalProductIdList.size()]));
        //根据三级分类的id获取三级分类列表

    List<ProductCategory> thirdCategoryList = writer.getCategoryListByIds(thirdIds);
    Map<Long, ProductCategory> thirdCategoryMap = ProductCategory.listToMap(thirdCategoryList);

    List<Long> secondIds = new ArrayList<Long>();

    if (CollectionUtils.isNotEmpty(thirdCategoryList)) {
      for (ProductCategory productCategory : thirdCategoryList) {
        if (null != productCategory.getParentId()) {
          secondIds.add(productCategory.getParentId());
        }
      }
    }

    //根据二级分类id获取二级分类列表

    List<ProductCategory> secondCategoryList = writer.getCategoryListByIds(secondIds);
    Map<Long, ProductCategory> secondCategoryMap = ProductCategory.listToMap(secondCategoryList);

    List<Long> firstIds = new ArrayList<Long>();

    if (CollectionUtils.isNotEmpty(secondCategoryList)) {
      for (ProductCategory productCategory : secondCategoryList) {
        if (null != productCategory.getParentId()) {
          firstIds.add(productCategory.getParentId());
        }
      }
    }
    //根据一级分了id获取一级分类列表
    List<ProductCategory> firstCategoryList = writer.getCategoryListByIds(firstIds);
    Map<Long, ProductCategory> firstCategoryMap = ProductCategory.listToMap(firstCategoryList);

    Map<String,String> allBrandModelNameMap = new HashMap<String, String>();
    for(ShopVehicleBrandModelDTO shopVehicleBrandModelDTO:StandardBrandModelCache.getShopVehicleBrandModelDTOList()){
      allBrandModelNameMap.put(shopVehicleBrandModelDTO.getBrandName(),StringUtil.formateStr(allBrandModelNameMap.get(shopVehicleBrandModelDTO.getBrandName()))+shopVehicleBrandModelDTO.getModelName()+"/");
    }

    for (NormalProduct normalProduct : normalProductList) {
      NormalProductDTO normalProductDTO = normalProduct.toDTO();
      List<NormalProductVehicleBrandModelDTO> normalProductVehicleBrandModelDTOList = null;
      if (null != thirdCategoryMap.get(normalProduct.getProductCategoryId())) {
        ProductCategory thirdCategory = thirdCategoryMap.get(normalProduct.getProductCategoryId());
        ProductCategory secondCategory = secondCategoryMap.get(thirdCategory.getParentId());
        ProductCategory firstCategory = firstCategoryMap.get(secondCategory.getParentId());
        normalProductDTO.setProductCategoryName(firstCategory.getName() + "-" + secondCategory.getName());
        normalProductDTO.setProductFirstCategoryName(firstCategory.getName());
        normalProductDTO.setProductFirstCategoryId(firstCategory.getId());
        normalProductDTO.setProductSecondCategoryName(secondCategory.getName());
        normalProductDTO.setProductSecondCategoryId(secondCategory.getId());
        if(VehicleSelectBrandModel.PART_MODEL.equals(normalProductDTO.getSelectBrandModel())){
          normalProductVehicleBrandModelDTOList = normalProductVehicleBrandModelDTOMap.get(normalProductDTO.getId());
          if(CollectionUtils.isNotEmpty(normalProductVehicleBrandModelDTOList)){
            Map<String,String> map = new HashMap<String, String>();
            String vehicleModelIds = "";
            for(NormalProductVehicleBrandModelDTO normalProductVehicleBrandModelDTO:normalProductVehicleBrandModelDTOList){
              vehicleModelIds +=normalProductVehicleBrandModelDTO.getModelId()+",";
              map.put(normalProductVehicleBrandModelDTO.getBrandName(),StringUtil.formateStr(map.get(normalProductVehicleBrandModelDTO.getBrandName()))+normalProductVehicleBrandModelDTO.getModelName()+"/");
            }
            String vehicleBrandModelInfo = "";
            for(Map.Entry<String,String> entry:map.entrySet()){
              vehicleBrandModelInfo += entry.getKey();
              if(!entry.getValue().equals(allBrandModelNameMap.get(entry.getKey()))){
                vehicleBrandModelInfo += "("+entry.getValue().substring(0,entry.getValue().length()-1)+")";
              }
              vehicleBrandModelInfo += ",";
            }
            normalProductDTO.setVehicleModelIds(vehicleModelIds.substring(0,vehicleModelIds.length()-1));
            normalProductDTO.setVehicleBrandModelInfo(vehicleBrandModelInfo.substring(0,vehicleBrandModelInfo.length()-1));
          }
        }else{
          normalProductDTO.setVehicleBrandModelInfo("所有车型");
        }
      }
      normalProductDTO.setBindingShopProductCount(NumberUtil.intValue(bindingShopProductCountMap.get(normalProductDTO.getId())));
      normalProductDTOList.add(normalProductDTO);
    }

    return normalProductDTOList;
  }

  public int countNormalProductDTO(ProductSearchCondition searchCondition) {
    ProductWriter writer = productDaoManager.getWriter();
    return writer.countNormalProductDTO(searchCondition);
  }

  public List<Product> getProductByNormalProductId(Long id) {
    if (null == id) {
      return null;
    }

    return productDaoManager.getWriter().getProductByNormalProductId(id);
  }

  public String checkNormalProductCommodityCodeRepeat(Long id, String commodityCode) {
    ProductWriter writer = productDaoManager.getWriter();

    NormalProduct normalProduct = writer.getNormalProductByCommodityCode(commodityCode);

    if (null == normalProduct) {
      return null;
    }

    if (null != id && id.equals(normalProduct.getId())) {
      return null;
    }

    return NormalProductConstants.PRODUCT_COMMODITY_CODE_REPEAT;
  }

  public String checkNormalProductRepeat(NormalProductDTO normalProductDTO) {
    ProductWriter writer = productDaoManager.getWriter();

    NormalProduct normalProduct = writer.getNormalProductBySixProperty(normalProductDTO);

    if (null == normalProduct) {
      return null;
    }

    if (null != normalProductDTO.getId() && normalProductDTO.getId().equals(normalProduct.getId())) {
      return null;
    }

    return NormalProductConstants.PRODUCT_REPEAT;
  }

  public NormalProductDTO saveOrUpdateNormalProduct(NormalProductDTO normalProductDTO) {
    if (null == normalProductDTO) {
      return null;
    }
    ProductWriter writer = productDaoManager.getWriter();

    Object status = writer.begin();

    try {
      if(normalProductDTO.getSelectAllBrandModel()){
        normalProductDTO.setSelectBrandModel(VehicleSelectBrandModel.ALL_MODEL);
      }else{
        normalProductDTO.setSelectBrandModel(VehicleSelectBrandModel.PART_MODEL);
      }
      if (null == normalProductDTO.getId()) {
        //新增
        NormalProduct normalProduct = new NormalProduct(normalProductDTO);
        writer.save(normalProduct);
        normalProductDTO.setId(normalProduct.getId());
      } else {
        NormalProduct normalProduct = writer.getById(NormalProduct.class, normalProductDTO.getId());
        if (null == normalProduct) {
          //新增
          normalProductDTO.setId(null);
          normalProduct = new NormalProduct(normalProductDTO);
          writer.save(normalProduct);
          normalProductDTO.setId(normalProduct.getId());
        } else {
          //更新
          normalProduct.fromDTO(normalProductDTO);
          writer.update(normalProduct);
        }
      }
      List<NormalProductVehicleBrandModel> bmList=writer.getNormalProductVehicleBrandModelByNormalProductId(normalProductDTO.getId());
      if(CollectionUtils.isNotEmpty(bmList)){
        for(NormalProductVehicleBrandModel bm:bmList){
          writer.delete(bm);
        }
      }
      if(VehicleSelectBrandModel.PART_MODEL.equals(normalProductDTO.getSelectBrandModel()) && StringUtils.isNotBlank(normalProductDTO.getVehicleModelIds())){
        Long[] vehicleModelIds=NumberUtil.parseLongValuesToArray(normalProductDTO.getVehicleModelIds(),NumberUtil.SPLIT_REGEX);
        for(Long vehicleModelId:vehicleModelIds){
          ShopVehicleBrandModelDTO shopVehicleBrandModelDTO = StandardBrandModelCache.getShopVehicleBrandModelDTOByModelId(vehicleModelId);
          NormalProductVehicleBrandModelDTO normalProductVehicleBrandModelDTO = new NormalProductVehicleBrandModelDTO(shopVehicleBrandModelDTO);
          normalProductVehicleBrandModelDTO.setNormalProductId(normalProductDTO.getId());
          NormalProductVehicleBrandModel normalProductVehicleBrandModel = new NormalProductVehicleBrandModel();
          normalProductVehicleBrandModel.fromDTO(normalProductVehicleBrandModelDTO);
          writer.save(normalProductVehicleBrandModel);
        }
      }

      writer.commit(status);
      return normalProductDTO;
    } finally {
      writer.rollback(status);
    }
  }

  public void deleteNormalProduct(Long id,List shopProductIdList) {
    if (null == id) {
      return;
    }
    ProductWriter writer = productDaoManager.getWriter();
    List<Product> productList = writer.getProductByNormalProductId(id);
    Object status = writer.begin();
    try {
      NormalProduct normalProduct = writer.getById(NormalProduct.class, id);
      if (null != normalProduct) {
        writer.delete(normalProduct);
      }
      if(CollectionUtils.isNotEmpty(productList)){
        for(Product product:productList){
          shopProductIdList.add(product.getId());
          product.setNormalProductId(null);
          product.setRelevanceStatus(ProductRelevanceStatus.NO);
          writer.update(product);
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void updateProductRelevanceStatus(Long productId,ProductRelevanceStatus relevanceStatus){
    Product product=getProductById(productId);
    if(product==null||product.getNormalProductId()==null) return;
    ProductWriter writer = productDaoManager.getWriter();
    Object status = writer.begin();
    try {
      product.setRelevanceStatus(relevanceStatus);
      writer.update(product);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  public void saveNormalProductModifyRecord(Long userId,Long normalProductId,Long shopProductId,NormalProductModifyScene scene){
    NormalProductModifyRecordDTO recordDTO=new NormalProductModifyRecordDTO(userId,normalProductId,shopProductId,scene);
    saveNormalProductModifyRecord(recordDTO);
  }

  public void saveNormalProductModifyRecord(NormalProductModifyRecordDTO... recordDTOs){
    if(ArrayUtil.isEmpty(recordDTOs)) return;
    ProductWriter writer = productDaoManager.getWriter();
    Object status = writer.begin();
    try {
      for(NormalProductModifyRecordDTO recordDTO:recordDTOs){
        NormalProductModifyRecord record=new NormalProductModifyRecord();
        record.fromDTO(recordDTO);
        writer.save(record);
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  public List<ProductCategoryDTO> getFirstProductCategory() {
    ProductWriter writer = productDaoManager.getWriter();

    List<ProductCategory> productCategoryList = writer.getFirstProductCategory();

    if (CollectionUtils.isEmpty(productCategoryList)) {
      return null;
    }

    List<ProductCategoryDTO> productCategoryDTOList = new ArrayList<ProductCategoryDTO>();

    for (ProductCategory productCategory : productCategoryList) {
      productCategoryDTOList.add(productCategory.toDTO());
    }

    return productCategoryDTOList;
  }



  /**
   * 如果前面有一级分类，没有二级分类，则返回null
   * <p/>
   * 如果前面有一级分类有二级分类，根据2级分类和name去后太模糊匹配
   * <p/>
   * 如果没有一级分类，则根据name去后太模糊匹配
   *
   * @param searchCondition
   * @return
   */
  public List<ProductCategoryDTO> getThirdCategoryByCondition(ProductSearchCondition searchCondition) {
    ProductWriter writer = productDaoManager.getWriter();

    List<ProductCategory> productCategoryList = writer.getThirdCategoryByCondition(searchCondition);

    if (CollectionUtils.isEmpty(productCategoryList)) {
      return null;
    }

    List<ProductCategoryDTO> productCategoryDTOList = new ArrayList<ProductCategoryDTO>();

    for (ProductCategory productCategory : productCategoryList) {
      productCategoryDTOList.add(productCategory.toDTO());
    }

    return productCategoryDTOList;
  }

  public List<String> getNormalProductByCondition(ProductSearchCondition searchCondition) {
    ProductWriter writer = productDaoManager.getWriter();
    return writer.getNormalProductByCondition(searchCondition);
  }

  public int countShopProductsByNormalProductId(Long id) {
    ProductWriter writer = productDaoManager.getWriter();

    return writer.countShopProductsByNormalProductId(id);
  }

  public List<ProductDTO> getShopProductsByNormalProductId(Long id, int page, int limit) {
    ProductWriter writer = productDaoManager.getWriter();
    List<Product> productList = writer.getShopProductsByNormalProductId(id, page, limit);
    if (CollectionUtils.isEmpty(productList)) {
      return null;
    }
    List<Long> shopIds = new ArrayList<Long>();
    List<Long> productIds = new ArrayList<Long>();

    for (Product product : productList) {
      shopIds.add(product.getShopId());
      productIds.add(product.getId());
    }

    IConfigService configService = ServiceManager.getService(IConfigService.class);

    Map<Long, ShopDTO> shopMap = configService.getShopByShopId(shopIds.toArray(new Long[shopIds.size()]));
    List<ProductLocalInfo> productLocalInfoList = writer.getProductLocalInfoByProductIds(productIds);
    Map<Long, ProductLocalInfo> productLocalInfoMap = ProductLocalInfo.listToMap(productLocalInfoList);

    List<ProductDTO> productDTOList = new ArrayList<ProductDTO>();

    for (Product product : productList) {
      ProductDTO productDTO = product.toDTO();
      ShopDTO shopDTO = shopMap.get(product.getShopId());
      productDTO.setShopName(null != shopDTO ? shopDTO.getName() : null);
      ProductLocalInfo productLocalInfo = productLocalInfoMap.get(product.getId());
      if(productLocalInfo!=null){
        productDTO.setProductLocalInfoDTO(productLocalInfo.toDTO());
        productDTO.setUnit(productLocalInfo.getSellUnit());
      }
      productDTOList.add(productDTO);
    }
    return productDTOList;
  }

  @Override
  public void deleteRelevance(Long id) {
    if (null == id) {
      return;
    }
    ProductWriter writer = productDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Product product = writer.getById(Product.class, id);
      product.setNormalProductId(null);
      product.setRelevanceStatus(ProductRelevanceStatus.NO);
      writer.update(product);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }
  @Override
  public void checkRelevance(Long id) {
    if (null == id) {
      return;
    }
    ProductWriter writer = productDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Product product = writer.getById(Product.class, id);
      product.setRelevanceStatus(ProductRelevanceStatus.YES);
      writer.update(product);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  public int countShopProductsByCondition(ProductSearchCondition searchCondition) {
    return productDaoManager.getWriter().countShopProductsByCondition(searchCondition);
  }

  public List<ProductDTO> getShopProductsByCondition(ProductSearchCondition searchCondition) {
    ProductWriter writer = productDaoManager.getWriter();

    List<Product> productList = writer.getShopProductsByCondition(searchCondition);

    if (CollectionUtils.isEmpty(productList)) {
      return null;
    }

    List<Long> shopIds = new ArrayList<Long>();
    List<Long> productIds = new ArrayList<Long>();

    for (Product product : productList) {
      shopIds.add(product.getShopId());
      productIds.add(product.getId());
    }

    IConfigService configService = ServiceManager.getService(IConfigService.class);

    Map<Long, ShopDTO> shopMap = configService.getShopByShopId(shopIds.toArray(new Long[shopIds.size()]));

    List<ProductLocalInfo> productLocalInfoList = writer.getProductLocalInfoByProductIds(productIds);

    Map<Long, ProductLocalInfo> productLocalInfoMap = ProductLocalInfo.listToMap(productLocalInfoList);

    List<ProductDTO> productDTOList = new ArrayList<ProductDTO>();

    for (Product product : productList) {
      ProductDTO productDTO = product.toDTO();
      ShopDTO shopDTO = shopMap.get(product.getShopId());
      productDTO.setShopName(null != shopDTO ? shopDTO.getName() : null);
      ProductLocalInfo productLocalInfo = productLocalInfoMap.get(product.getId());
      productDTO.setUnit(null != productLocalInfo ? productLocalInfo.getSellUnit() : null);
      if (null == product.getNormalProductId()) {
        productDTO.setRelevanceStatus(ProductRelevanceStatus.NO);
      }
      productDTOList.add(productDTO);
    }

    return productDTOList;
  }

  public List<NormalBrandDTO> getNormalBrandByKeyWord(String keyWord) {
    ProductWriter writer = productDaoManager.getWriter();

    List<NormalBrand> normalBrandList = writer.getNormalBrandByKeyWord(keyWord);

    if (CollectionUtils.isEmpty(normalBrandList)) {
      return null;
    }

    List<NormalBrandDTO> normalBrandDTOList = new ArrayList<NormalBrandDTO>();

    for (NormalBrand normalBrand : normalBrandList) {
      normalBrandDTOList.add(normalBrand.toDTO());
    }

    return normalBrandDTOList;
  }

  public List<NormalModelDTO> getNormalModelByBrandId(Long brandId) {
    if (null == brandId) {
      return null;
    }

    List<NormalModel> normalModelList = productDaoManager.getWriter().getNormalModelByBrandId(brandId);

    if (CollectionUtils.isEmpty(normalModelList)) {
      return null;
    }

    List<NormalModelDTO> normalModelDTOList = new ArrayList<NormalModelDTO>();

    for (NormalModel normalModel : normalModelList) {
      normalModelDTOList.add(normalModel.toDTO());
    }

    return normalModelDTOList;
  }

  public void completeShopName(List<ProductDTO> productDTOList) {
    if (CollectionUtils.isEmpty(productDTOList)) {
      return;
    }

    List<Long> shopIds = new ArrayList<Long>();

    for (ProductDTO productDTO : productDTOList) {
      shopIds.add(productDTO.getShopId());
    }

    IConfigService configService = ServiceManager.getService(IConfigService.class);

    Map<Long, ShopDTO> shopMap = configService.getShopByShopId(shopIds.toArray(new Long[shopIds.size()]));


    for (ProductDTO productDTO : productDTOList) {
      ShopDTO shopDTO = shopMap.get(productDTO.getShopId());
      productDTO.setShopName(null != shopDTO ? shopDTO.getName() : null);
    }
  }

  public boolean isNormalProductIdRelevanceInShop(long normalProductId, long shopId, long shopProductId) {
    ProductWriter writer = productDaoManager.getWriter();
    return writer.isNormalProductIdRelevanceInShop(normalProductId, shopId, shopProductId);
  }

  @Override
  public ProductDTO getProductByNormalProductId(Long shopId, Long normalProductId) {
    ProductWriter writer = productDaoManager.getWriter();
    return writer.getProductByNormalProductId(shopId, normalProductId);
  }

  public void relevanceProduct(List<Long> shopProductIds, Long normalProductId) {
    if (CollectionUtils.isEmpty(shopProductIds) || null == normalProductId) {
      return;
    }
    ProductWriter writer = productDaoManager.getWriter();
    Object status = writer.begin();
    try {
      List<Product> productList = writer.getProductByIds(shopProductIds);
      if (CollectionUtils.isNotEmpty(productList)) {
        shopProductIds.clear();
        for (Product product : productList) {
          if(product.getNormalProductId()==null){
            product.setNormalProductId(normalProductId);
            product.setRelevanceStatus(ProductRelevanceStatus.YES);
            writer.update(product);
            shopProductIds.add(product.getId());
          }
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  public List<NormalProductDTO> getNormalProductDTOByIds(List<Long> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return null;
    }

    ProductWriter writer = productDaoManager.getWriter();

    List<NormalProduct> normalProductList = writer.getNormalProductDTOByIds(ids);

    if (CollectionUtils.isEmpty(normalProductList)) {
      return null;
    }
    Map<Long,List<NormalProductVehicleBrandModelDTO>> normalProductVehicleBrandModelDTOMap = this.getNormalProductVehicleBrandModelDTOMapByNormalProductId(ids.toArray(new Long[ids.size()]));
    Map<String,String> allBrandModelNameMap = new HashMap<String, String>();
    for(ShopVehicleBrandModelDTO shopVehicleBrandModelDTO:StandardBrandModelCache.getShopVehicleBrandModelDTOList()){
      allBrandModelNameMap.put(shopVehicleBrandModelDTO.getBrandName(),StringUtil.formateStr(allBrandModelNameMap.get(shopVehicleBrandModelDTO.getBrandName()))+shopVehicleBrandModelDTO.getModelName()+"/");
    }
    List<NormalProductDTO> normalProductDTOList = new ArrayList<NormalProductDTO>();
    List<NormalProductVehicleBrandModelDTO> normalProductVehicleBrandModelDTOList = null;
    for (NormalProduct normalProduct : normalProductList) {
      NormalProductDTO normalProductDTO = normalProduct.toDTO();
      if(VehicleSelectBrandModel.PART_MODEL.equals(normalProductDTO.getSelectBrandModel())){
        normalProductVehicleBrandModelDTOList = normalProductVehicleBrandModelDTOMap.get(normalProductDTO.getId());
        if(CollectionUtils.isNotEmpty(normalProductVehicleBrandModelDTOList)){
          Map<String,String> map = new HashMap<String, String>();
          String vehicleModelIds = "";
          for(NormalProductVehicleBrandModelDTO normalProductVehicleBrandModelDTO:normalProductVehicleBrandModelDTOList){
            vehicleModelIds +=normalProductVehicleBrandModelDTO.getModelId()+",";
            map.put(normalProductVehicleBrandModelDTO.getBrandName(),StringUtil.formateStr(map.get(normalProductVehicleBrandModelDTO.getBrandName()))+normalProductVehicleBrandModelDTO.getModelName()+"/");
          }
          String vehicleBrandModelInfo = "";
          for(Map.Entry<String,String> entry:map.entrySet()){
            vehicleBrandModelInfo += entry.getKey();
            if(!entry.getValue().equals(allBrandModelNameMap.get(entry.getKey()))){
              vehicleBrandModelInfo += "("+entry.getValue().substring(0,entry.getValue().length()-1)+")";
            }
            vehicleBrandModelInfo += ",";
          }
          normalProductDTO.setVehicleModelIds(vehicleModelIds.substring(0,vehicleModelIds.length()-1));
          normalProductDTO.setVehicleBrandModelInfo(vehicleBrandModelInfo.substring(0,vehicleBrandModelInfo.length()-1));
        }
      }else{
        normalProductDTO.setVehicleBrandModelInfo("所有车型");
      }
      normalProductDTOList.add(normalProductDTO);
    }

    return normalProductDTOList;
  }

  /**
   * 根据标准产品获得其关联产品
   *
   * @param normalProductId
   * @return
   */
  public List<ProductLocalInfo> getProductLocalInfoByNormalProductId(Long normalProductId) {
    if (normalProductId == null) {
      return null;
    }
    return productDaoManager.getWriter().getProductLocalInfoByNormalProductId(normalProductId);
  }

  /**
   * 获得所有标准产品 用于采购统计
   *
   * @return
   */
  public List<NormalProduct> getAllNormalProducts() {
    return productDaoManager.getWriter().getAllNormalProducts();
  }

  /**
   * 根据标准产品id获得标准产品
   *
   * @param normalProductId
   * @return
   */
  public NormalProductDTO getNormalProductById(Long normalProductId) {
    if (normalProductId == null) {
      return null;
    }
    ProductWriter productWriter = productDaoManager.getWriter();
    NormalProduct normalProduct = productWriter.getById(NormalProduct.class, normalProductId);
    return normalProduct == null ? null : normalProduct.toDTO();

  }

  public List<ModelDTO> getModelHasBrandId() {
    ProductWriter productWriter = productDaoManager.getWriter();

    List<Model> modelList = productWriter.getModelHasBrandId();

    if (CollectionUtils.isEmpty(modelList)) {
      return null;
    }

    List<ModelDTO> modelDTOList = new ArrayList<ModelDTO>();

    for (Model model : modelList) {
      modelDTOList.add(model.toDTO());
    }

    return modelDTOList;
  }

  public List<BrandDTO> getBrandList(Long... id) {
    ProductWriter productWriter = productDaoManager.getWriter();

    List<Brand> brandList = productWriter.getBrandList(id);

    if (CollectionUtils.isEmpty(brandList)) {
      return null;
    }

    List<BrandDTO> brandDTOList = new ArrayList<BrandDTO>();

    for (Brand brand : brandList) {
      brandDTOList.add(brand.toDTO());
    }

    return brandDTOList;
  }

  public void initNormalBrandAndModel(List<BrandDTO> brandDTOList) {

    if (CollectionUtils.isEmpty(brandDTOList)) {
      return;
    }

    ProductWriter writer = productDaoManager.getWriter();

    Object status = writer.begin();

    try {

      for (BrandDTO brandDTO : brandDTOList) {
        if (null == brandDTO) {
          continue;
        }

        NormalBrand normalBrand = new NormalBrand(brandDTO);

        writer.save(normalBrand);

        if (CollectionUtils.isEmpty(brandDTO.getModelDTOList())) {
          continue;
        }

        for (ModelDTO modelDTO : brandDTO.getModelDTOList()) {
          NormalModel normalModel = new NormalModel(modelDTO);
          normalModel.setNormalBrandId(normalBrand.getId());
          writer.save(normalModel);
        }
      }

      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  public List<NormalProductDTO> getNormalProductDTOByCategoryId(Long categoryId) {
    ProductWriter writer = productDaoManager.getWriter();

    List<NormalProduct> normalProductList = writer.getNormalProductDTOByCategoryId(categoryId);

    if (CollectionUtils.isEmpty(normalProductList)) {
      return null;
    }

    List<NormalProductDTO> normalProductDTOList = new ArrayList<NormalProductDTO>();

    for (NormalProduct normalProduct : normalProductList) {
      normalProductDTOList.add(normalProduct.toDTO());
    }

    return normalProductDTOList;
  }

  public List<ProductLocalInfo> getAllProductInSales(Long shopId){
    ProductWriter writer = productDaoManager.getWriter();
    return writer.getProductLocalInfoByIds(shopId,null);
  }

  public List<ProductLocalInfoDTO> getProductLocalInfoDTOById(Long shopId,Long... productLocalInfoIds){
    ProductWriter writer = productDaoManager.getWriter();
    List<ProductLocalInfo> productLocalInfoList=writer.getProductLocalInfoByIds(shopId,productLocalInfoIds);
    List<ProductLocalInfoDTO> productLocalInfoDTOs=new ArrayList<ProductLocalInfoDTO>();
    if(CollectionUtil.isNotEmpty(productLocalInfoList)){
      for(ProductLocalInfo pli:productLocalInfoList){
        if(pli==null) continue;
        productLocalInfoDTOs.add(pli.toDTO());
      }
    }
    return productLocalInfoDTOs;
  }



  @Override
  public List<Long> updateProductSalesStatus(Long shopId, ProductStatus productStatus, Long... productLocalInfoIds) throws Exception {
    List<Long> productLocalInfoIdList = new ArrayList<Long>();
    if (ArrayUtils.isEmpty(productLocalInfoIds)) {
      return productLocalInfoIdList;
    }
    ProductWriter writer = productDaoManager.getWriter();
    Object status = writer.begin();
    try {
      List<ProductLocalInfo> productLocalInfoList = writer.getProductLocalInfoByIds(shopId, productLocalInfoIds);
      if (CollectionUtils.isNotEmpty(productLocalInfoList)) {
        if (ProductStatus.InSales.equals(productStatus)) {
          for (ProductLocalInfo productLocalInfo : productLocalInfoList) {
            if(NumberUtil.doubleVal(productLocalInfo.getInSalesPrice())>0){
              productLocalInfo.setSalesStatus(productStatus);
              productLocalInfo.setLastInSalesTime(System.currentTimeMillis());
              productLocalInfoIdList.add(productLocalInfo.getId());
              writer.update(productLocalInfo);
            }
          }
        } else if (ProductStatus.NotInSales.equals(productStatus)) {
          for (ProductLocalInfo productLocalInfo : productLocalInfoList) {
            productLocalInfo.setSalesStatus(productStatus);
            productLocalInfo.setLastOffSalesTime(System.currentTimeMillis());
            productLocalInfoIdList.add(productLocalInfo.getId());
            writer.update(productLocalInfo);
          }
        }
      }
      writer.commit(status);
       //下架的商品要删除对应的促销
      if (ProductStatus.NotInSales.equals(productStatus)) {
        ServiceManager.getService(IPromotionsService.class).updatePromotionsForGoodsInOff(shopId,productLocalInfoIds);
      }
    } finally {
      writer.rollback(status);
    }
    return productLocalInfoIdList;
  }

  @Override
  public List<Long> startSellAllProductsWithTradePrice(Long shopId) throws Exception {
    ProductWriter writer = productDaoManager.getWriter();
    Object status = writer.begin();
    List<Long> productLocalInfoIdList = new ArrayList<Long>();
    try {
      List<ProductLocalInfo> productLocalInfoList = writer.getAllProductLocalInfoWithTradePriceAndNotInSales(shopId);
      if (CollectionUtils.isNotEmpty(productLocalInfoList)) {
        for (ProductLocalInfo productLocalInfo : productLocalInfoList) {
          productLocalInfo.setSalesStatus(ProductStatus.InSales);
          productLocalInfo.setLastInSalesTime(System.currentTimeMillis());
          writer.update(productLocalInfo);
          productLocalInfoIdList.add(productLocalInfo.getId());
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    return productLocalInfoIdList;
  }

  @Override
  public int countAllProductLocalInfoWithNotTradePriceAndNotInSales(Long shopId) throws Exception {
    ProductWriter writer = productDaoManager.getWriter();
    return writer.countAllProductLocalInfoWithNotTradePriceAndNotInSales(shopId);
  }

  @Override
  public List<Long> stopSellAllProducts(Long shopId) throws Exception {
    ProductWriter writer = productDaoManager.getWriter();
    Object status = writer.begin();
    List<Long> productLocalInfoIdList = new ArrayList<Long>();
    try {
      List<ProductLocalInfo> productLocalInfoList = writer.getAllProductLocalInfInSales(shopId);
      if (CollectionUtils.isNotEmpty(productLocalInfoList)) {
        for (ProductLocalInfo productLocalInfo : productLocalInfoList) {
          productLocalInfo.setSalesStatus(ProductStatus.NotInSales);
          productLocalInfo.setLastOffSalesTime(System.currentTimeMillis());
          writer.update(productLocalInfo);
          productLocalInfoIdList.add(productLocalInfo.getId());
        }
      }

      writer.commit(status);
      ServiceManager.getService(IPromotionsService.class).updatePromotionsForGoodsInOff(shopId,productLocalInfoIdList.toArray(new Long[productLocalInfoIdList.size()]));
    } finally {
      writer.rollback(status);
    }
    return productLocalInfoIdList;
  }

  @Override
  public boolean checkProductInSalesByProductLocalInfoId(Long shopId, Long... productLocalInfoIds) throws Exception {
    ProductWriter writer = productDaoManager.getWriter();
    int count = writer.countProductInSalesByProductLocalInfoId(shopId, productLocalInfoIds);
    if (count <= 0) {
      return false;
    } else {
      return true;
    }
  }

  public List<ProductLocalInfoDTO> getAllProductLocalInfoWithTradePriceAndNotInSales(Long shopId) throws Exception {
    ProductWriter writer = productDaoManager.getWriter();
    List<ProductLocalInfo> productLocalInfoList = writer.getAllProductLocalInfoWithTradePriceAndNotInSales(shopId);
    List<ProductLocalInfoDTO> productLocalInfoDTOList = new ArrayList<ProductLocalInfoDTO>();
    if (CollectionUtils.isNotEmpty(productLocalInfoList)) {
      for (ProductLocalInfo productLocalInfo : productLocalInfoList) {
        productLocalInfoDTOList.add(productLocalInfo.toDTO());
      }
    }
    return productLocalInfoDTOList;
  }

  @Override
  public boolean cancelProductRelation(Long customerShopId, Long supplierShopId) throws Exception {
    boolean isSuccess = false;
    if (customerShopId == null || supplierShopId == null) {
      return isSuccess;
    }
    ProductWriter writer = productDaoManager.getWriter();
    Object status = writer.begin();
    try {
      int currentPage = 1, pageSize = 100;

      List<ProductMapping> productMappings = new ArrayList<ProductMapping>();
      int totalRows = writer.countProductMappingByShopIds(customerShopId, supplierShopId, null, null);
      if (totalRows < 1) {
        return isSuccess;
      }

      Pager pager = new Pager(totalRows, currentPage, pageSize);
      for (currentPage = 1; currentPage <= pager.getTotalPage(); currentPage++) {
        pager = new Pager(totalRows, currentPage, pageSize);
        productMappings = writer.getProductMappingByShopId(customerShopId, supplierShopId, null, null, pager);
        if (CollectionUtil.isNotEmpty(productMappings)) {
          for (ProductMapping productMapping : productMappings) {
            if (!ProductStatus.DISABLED.equals(productMapping.getStatus())) {
              productMapping.setStatus(ProductStatus.DISABLED);
              writer.update(productMapping);
            }
          }
          writer.flush();
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    return isSuccess;
  }

  @Override
  public Map<Long, ProductDTO> getProductDTOMapByIds(Long shopId, Long[] productIds) {
    if (ArrayUtils.isEmpty(productIds)) {
      return new HashMap<Long, ProductDTO>();
    }
    ProductWriter writer = productDaoManager.getWriter();
    List<Product> list = writer.getProductByIds(shopId, productIds);
    Map<Long, ProductDTO> productDTOMap = new HashMap<Long, ProductDTO>();
    if (CollectionUtils.isNotEmpty(list)) {
      for (Product product : list) {
        productDTOMap.put(product.getId(), product.toDTO());
      }
    }
    return productDTOMap;
  }

  @Override
  public Map<String,ProductDTO> getProductDTOMaps(Long shopId)
  {
    Map<String,ProductDTO> map = new HashMap<String, ProductDTO>();

    if(null == shopId)
    {
      return map;
    }

    ProductWriter writer = productDaoManager.getWriter();

    List<Product> list =  writer.getProduct(shopId);

    if(CollectionUtils.isEmpty(list))
    {
      return map;
    }

    for(Product product : list)
    {
      if(StringUtils.isBlank(product.getCommodityCode()))
      {
        continue;
      }

      map.put(product.getCommodityCode(),product.toDTO());
    }

    return map;
  }

  @Override
  public List<String> getAllCommodityCode(Long shopId) {
    return  productDaoManager.getWriter().getAllCommodityCode(shopId);
  }

  @Override
  public int countProductNotInSales(Long shopId) throws Exception {
    return productDaoManager.getWriter().countProductNotInSales(shopId);
  }

  @Override
  public void updateOldProductMappingsData(PurchaseInventoryDTO purchaseInventoryDTO){
    ProductWriter writer = productDaoManager.getWriter();
    Object status = writer.begin();
    try {
      Set<Long> allProductIds = new HashSet<Long>();
      for (PurchaseInventoryItemDTO purchaseInventoryItemDTO : purchaseInventoryDTO.getItemDTOs()) {
        allProductIds.add(purchaseInventoryItemDTO.getProductId());
      }
      List<ProductMapping> productMappings = ServiceManager.getService(IProductService.class).getProductMappings(purchaseInventoryDTO.getShopId(), purchaseInventoryDTO.getSupplierShopId(),
          allProductIds.toArray(new Long[allProductIds.size()]));

      for (PurchaseInventoryItemDTO purchaseInventoryItemDTO : purchaseInventoryDTO.getItemDTOs()) {
        for(ProductMapping productMapping : productMappings){
          if(productMapping.getCustomerProductId().equals(purchaseInventoryItemDTO.getProductId())){
            if(UnitUtil.isStorageUnit(purchaseInventoryItemDTO.getUnit(),purchaseInventoryItemDTO)){
              productMapping.setCustomerLastPurchaseAmount(NumberUtil.round(purchaseInventoryItemDTO.getAmount()*purchaseInventoryItemDTO.getRate(),1));
              productMapping.setCustomerLastPurchasePrice(NumberUtil.round(NumberUtil.doubleVal(purchaseInventoryItemDTO.getPurchasePrice())/purchaseInventoryItemDTO.getRate(),NumberUtil.MONEY_PRECISION));
            }else{
              productMapping.setCustomerLastPurchaseAmount(purchaseInventoryItemDTO.getAmount());
              productMapping.setCustomerLastPurchasePrice(NumberUtil.doubleVal(purchaseInventoryItemDTO.getPurchasePrice()));
            }
            productMapping.setCustomerLastPurchaseDate(purchaseInventoryDTO.getVestDate());
            writer.update(productMapping);
          }
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public Long getBcgogoRecommendProductIds(Long normalProductId, Double comparePrice, Long... shopIds) {
    return productDaoManager.getWriter().getBcgogoRecommendProductIds(normalProductId, comparePrice,shopIds);
  }

   /**
   * 获取店铺注册的商品 商品信息拿的是最新的商品信息
   * @param shopId
   * @return
   */
  public List<ProductDTO> getShopRegisterProductList(Long shopId) {
    ProductWriter writer = productDaoManager.getWriter();
    List<ProductDTO> productDTOList = new ArrayList<ProductDTO>();

    List<ShopRegisterProduct> shopRegisterProductList = writer.getShopRegisterProductList(shopId);
    if (CollectionUtils.isEmpty(shopRegisterProductList)) {
      return productDTOList;
    }

    Set<Long> productIdSet = new HashSet<Long>();
    for (ShopRegisterProduct shopRegisterProduct : shopRegisterProductList) {
      productIdSet.add(shopRegisterProduct.getProductLocalInfoId());
    }

    IProductService productService = ServiceManager.getService(IProductService.class);
    Map<Long, ProductDTO> productDTOMap = productService.getProductDTOMapByProductLocalInfoIds(shopId, productIdSet);
    if (CollectionUtil.isEmpty(productDTOMap.values())) {
      return productDTOList;
    }

    for (ProductDTO productDTO : productDTOMap.values()) {
      productDTOList.add(productDTO);
    }

    return productDTOList;
  }

  /**
   * 根据id获取商品分类（经营范围）
   * @param productCategoryIds
   * @return
   */
  public List<ProductCategory> getCategoryListByIds(List<Long> productCategoryIds) {

    ProductWriter writer = productDaoManager.getWriter();
    List<ProductCategory> productCategories = writer.getCategoryListByIds(productCategoryIds);
    return productCategories;
  }

   /**
   * 根据营业分类的id查找父类id
   * @param ids
   * @return
   */
  public List<Long> getCategoryParentIds(List<Long> ids) {
    ProductWriter writer = productDaoManager.getWriter();
    List<Long> productCategories = writer.getCategoryParentIds(ids);
    return productCategories;
  }

    /**
   * 保存注册时填写的商品
   * @param productDTOs
   */
  public void saveShopRegisterProduct(ProductDTO[] productDTOs) {
    if (ArrayUtil.isEmpty(productDTOs)) {
      return;
    }

    ProductWriter writer = productDaoManager.getWriter();
    Object status = writer.begin();
    try {

      for (ProductDTO productDTO : productDTOs) {
        ShopRegisterProductDTO shopRegisterProductDTO = new ShopRegisterProductDTO();
        shopRegisterProductDTO.fromProductDTO(productDTO);
        ShopRegisterProduct shopRegisterProduct = new ShopRegisterProduct();
        shopRegisterProduct.fromDTO(shopRegisterProductDTO);
        writer.save(shopRegisterProduct);
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public List<ShopRegisterProductDTO> getShopRegisterProductListByShopId(Long shopId) {
    ProductWriter writer = productDaoManager.getWriter();
    List<ShopRegisterProductDTO> shopRegisterProductDTOList = new ArrayList<ShopRegisterProductDTO>();
    List<ShopRegisterProduct> shopRegisterProductList =   writer.getShopRegisterProductListByShopId(shopId);
    for(ShopRegisterProduct entity:  shopRegisterProductList){
      shopRegisterProductDTOList.add(entity.toDTO());
    }
    return shopRegisterProductDTOList;
  }

  @Override
  public void analyzeProductWord() throws Exception {
    ProductWriter writer = productDaoManager.getWriter();
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    List<ShopDTO> shopDTOList = configService.getActiveShop();
    Map<String,Integer> tempWordMap = new HashMap<String, Integer>();
    List<ProductDic> productDicList = new ArrayList<ProductDic>();
    for(ShopDTO shopDTO:shopDTOList){
      if(shopDTO.getId()!=10000010003050205l && shopDTO.getId()!=10000010015511458l && ShopKind.OFFICIAL.equals(shopDTO.getShopKind())){
        List<Product> productList = writer.getProduct(shopDTO.getId());
        for(Product product : productList){
          if(StringUtils.isNotBlank(product.getName())){
            List<String> p = MMSegUtil.getTocken(product.getName());
            if(p!=null && p.size()>1){
              for(String w:p){
                String b = w.replaceAll("[^\u4E00-\u9FA5]", "");
                if(StringUtils.isNotBlank(b) && b.length()==1){

                  String[] asp = product.getName().replaceAll("[^\u4E00-\u9FA5]", ",").split(",");
                  for(String s:asp){
                    if(StringUtils.isNotBlank(s) && s.length()>1){
                      Integer count = tempWordMap.get(s.trim());
                      if(count!=null){
                        tempWordMap.put(s.trim(),count+1);
                      }else{
                        tempWordMap.put(s.trim(),1);
                      }
                    }
                  }
                }
              }

            }
          }
          if(StringUtils.isNotBlank(product.getBrand())){
            List<String> p = MMSegUtil.getTocken(product.getBrand());
            if(p!=null && p.size()>1){
              for(String w:p){
                String b = w.replaceAll("[^\u4E00-\u9FA5]", "");
                if(StringUtils.isNotBlank(b) && b.length()==1){

                  String[] asp = product.getBrand().replaceAll("[^\u4E00-\u9FA5]", ",").split(",");
                  for(String s:asp){
                    if(StringUtils.isNotBlank(s) && s.length()>1){
                      Integer count = tempWordMap.get(s.trim());
                      if(count!=null){
                        tempWordMap.put(s.trim(),count+1);
                      }else{
                        tempWordMap.put(s.trim(),1);
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    LOG.error("sassssssss:"+tempWordMap.size());
    for(Map.Entry<String,Integer> entry : tempWordMap.entrySet()) {
      ProductDic productDic = new ProductDic();
      productDic.setWord(entry.getKey());
      productDic.setCount(entry.getValue());
      productDic.setPinyinSort(PinyinUtil.getFirstLetter(entry.getKey()));
      productDic.setType(entry.getKey().length()+"");
      productDicList.add(productDic);
    }
    Object status = writer.begin();
    try {
      for(ProductDic productDic : productDicList){
        writer.save(productDic);
      }

      writer.commit(status);
    } finally {
      writer.rollback(status);
    }

  }

  @Override
  public void collectDuplicateProductNameAndUnits() throws Exception {
    ProductWriter writer = productDaoManager.getWriter();
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    List<ShopDTO> shopDTOList = configService.getActiveShop();
    Map<String,Map<String,Integer>> tempProductNameSellUnitMap = new HashMap<String, Map<String,Integer>>();
//    Map<String,Integer> tempProductNameStorageUnitMap = new HashMap<String, Integer>();
    for(ShopDTO shopDTO:shopDTOList){
      if(ShopKind.OFFICIAL.equals(shopDTO.getShopKind())){
        List<Object[]> objectsList = writer.getProductByShopIdAndHaveUnit(shopDTO.getId());
        for(Object[] objects : objectsList){
          if (objects != null && objects[0] != null && objects[1] != null){
            Product product = (Product)objects[0];
            ProductLocalInfo productLocalInfo = (ProductLocalInfo)objects[1];
            if(StringUtils.isNotBlank(product.getName())){
              if(StringUtils.isNotBlank(product.getName().replaceAll("[^\u4E00-\u9FA5]", ""))){
                if((StringUtils.isNotBlank(productLocalInfo.getSellUnit().replaceAll("[^\u4E00-\u9FA5]", "")))){
                  Map<String,Integer> unitCountMap = tempProductNameSellUnitMap.get(product.getName().trim());
                  if(unitCountMap==null){
                    unitCountMap = new HashMap<String, Integer>();
                  }
                  Integer count = unitCountMap.get(productLocalInfo.getSellUnit().trim());
                  if(count!=null){
                    unitCountMap.put(productLocalInfo.getSellUnit().trim(),count+1);
                  }else{
                    unitCountMap.put(productLocalInfo.getSellUnit().trim(),1);
                  }
                  tempProductNameSellUnitMap.put(product.getName().trim(),unitCountMap);
                }


              }
            }
          }
        }
      }
    }

    List<ProductUnit> productUnitList = new ArrayList<ProductUnit>();
    Iterator<Map.Entry<String,Map<String,Integer>>> iterator = tempProductNameSellUnitMap.entrySet().iterator();
    while(iterator.hasNext()){
      Map.Entry<String,Map<String,Integer>> entry=iterator.next();
      Map<String,Integer> unitCountMap = entry.getValue();
      if(MapUtils.isNotEmpty(unitCountMap) && unitCountMap.keySet().size()==1){
        ProductUnit productUnit = new ProductUnit();
        productUnit.setProductName(entry.getKey());
        for(Map.Entry<String,Integer> e:unitCountMap.entrySet()){
          productUnit.setUnit(e.getKey());
          productUnit.setCount(e.getValue());
        }
        if(productUnit.getCount()>1){
          productUnitList.add(productUnit);
        }
      }else if(MapUtils.isNotEmpty(unitCountMap) && unitCountMap.keySet().size()>1) {
        ProductUnit productUnit = new ProductUnit();
        for(Map.Entry<String,Integer> e:unitCountMap.entrySet()){
          productUnit.setProductName(entry.getKey());
          if(productUnit.getCount()==null || productUnit.getCount()<e.getValue()){
            productUnit.setCount(e.getValue());
            productUnit.setUnit(e.getKey());
          }
        }
        if(productUnit.getCount()>1){
          productUnitList.add(productUnit);
        }
      }
    }

    Object status = writer.begin();
    try {
      for(ProductUnit productUnit : productUnitList){
        writer.save(productUnit);
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }

    ProductUnitCache.refreshProductUnitCache();
  }

  @Override
  public List<ProductUnitDTO> getAllProductUnitDTOList(){
    List<ProductUnitDTO> productUnitDTOList = new ArrayList<ProductUnitDTO>();
    ProductWriter writer = productDaoManager.getWriter();
    List<ProductUnit> productUnitList = writer.getAllProductUnit();
    if(CollectionUtils.isNotEmpty(productUnitList)){
      for(ProductUnit productUnit:productUnitList){
        productUnitDTOList.add(productUnit.toDTO());
      }
    }
    return productUnitDTOList;
  }
  @Override
  public void updateProductForPurchaseInventory(Long shopId, Set<Long> deleteProductIds, ProductDTO[] productDTOs) {
    if (shopId == null || productDTOs == null || ArrayUtils.isEmpty(productDTOs)) {
      return;
    }
    Set<Long> productLocalInfoIds = new HashSet<Long>();
    Map<Long, ProductDTO> toUpdateProductMap = new HashMap<Long, ProductDTO>();
    for (ProductDTO productDTO : productDTOs) {
      if (productDTO.getProductLocalInfoId() != null) {
        toUpdateProductMap.put(productDTO.getProductLocalInfoId(), productDTO);
      }
    }
    productLocalInfoIds = toUpdateProductMap.keySet();
    if (CollectionUtils.isEmpty(productLocalInfoIds)) {
      return;
    }
    ProductWriter writer = productDaoManager.getWriter();
    Object status = writer.begin();
    try {
      List<Object[]> productDateList = writer.getProductDataByProductLocalInfoId(shopId, productLocalInfoIds.toArray(new Long[productLocalInfoIds.size()]));
      for (Object[] productDates : productDateList) {
        if (productDates != null && productDates.length == 2) {
          Product product = (Product) productDates[0];
          ProductLocalInfo productLocalInfo = (ProductLocalInfo) productDates[1];
          ProductDTO productDTO = toUpdateProductMap.get(productLocalInfo.getId());
          if (productDTO != null) {
            double tradePrice = NumberUtil.doubleVal(productDTO.getTradePrice());
            double purchasePrice = NumberUtil.doubleVal(productDTO.getPurchasePrice());
            if(productLocalInfo !=null && UnitUtil.isStorageUnit(productDTO.getStorageUnit(),productLocalInfo.toDTO())){
              tradePrice = tradePrice /productLocalInfo.getRate();
              purchasePrice = purchasePrice / productLocalInfo.getRate();
            }
            product.setCommodityCode(productDTO.getCommodityCode());
            product.setKindId(productDTO.getKindId());
            if(ProductStatus.DISABLED.equals(product.getStatus())){
              if(deleteProductIds == null){
                deleteProductIds = new HashSet<Long>();
              }
              if(productLocalInfo != null) {
                deleteProductIds.add(productLocalInfo.getId());
              }
              product.setStatus(null);
            }

            if (StringUtils.isBlank(productLocalInfo.getSellUnit()) && StringUtils.isNotBlank(productDTO.getSellUnit())) {
              productLocalInfo.setSellUnit(productDTO.getSellUnit());
              productLocalInfo.setStorageUnit(productDTO.getSellUnit());
            }
            productLocalInfo.setPurchasePrice(purchasePrice);
            productLocalInfo.setTradePrice(tradePrice);
            productLocalInfo.setStorageBin(productDTO.getStorageBin());

            writer.update(product);
            writer.update(productLocalInfo);
          }
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

    /**
   * 注册时专用 保存注册时填写的车辆品牌、车型
   * @param shopDTO
   */
  public void saveShopVehicleBrandModel(ShopDTO shopDTO) {
    if (shopDTO == null || shopDTO.getId() == null || ArrayUtil.isEmpty(shopDTO.getShopVehicleBrandModelDTOs())) {
      return;
    }

    ProductWriter writer = productDaoManager.getWriter();
    Object status = writer.begin();
    try {
      for (ShopVehicleBrandModelDTO shopVehicleBrandModelDTO : shopDTO.getShopVehicleBrandModelDTOs()) {
        shopVehicleBrandModelDTO.setId(null);
        shopVehicleBrandModelDTO.setShopId(shopDTO.getId());
        ShopVehicleBrandModel shopVehicleBrandModel = new ShopVehicleBrandModel();
        shopVehicleBrandModel = shopVehicleBrandModel.fromDTO(shopVehicleBrandModelDTO);
        writer.save(shopVehicleBrandModel);
      }
      writer.commit(status);

    } finally {
      writer.rollback(status);
    }
    return;
  }

  @Override
  public List<ShopVehicleBrandModelDTO> getShopVehicleBrandModelByShopId(Long shopId){
    if(shopId == null)
      return null;
    ProductWriter writer = productDaoManager.getWriter();
    List<ShopVehicleBrandModel> bmList=writer.getShopVehicleBrandModelByShopId(shopId);
    List<ShopVehicleBrandModelDTO> bmDTOs=new ArrayList<ShopVehicleBrandModelDTO>();
    if(CollectionUtil.isNotEmpty(bmList)){
      for(ShopVehicleBrandModel bm:bmList){
        bmDTOs.add(bm.toDTO());
      }
    }
    return bmDTOs;
  }

  @Override
  public void saveOrUpdateRecentlyViewedProduct(Long shopId,Long userId,Long viewedProductLocalInfoId) {
    if(shopId==null || viewedProductLocalInfoId==null) return;
    IRecentlyUsedDataService recentlyUsedDataService = ServiceManager.getService(IRecentlyUsedDataService.class);
    recentlyUsedDataService.saveOrUpdateRecentlyUsedData(shopId,userId,RecentlyUsedDataType.VISITED_PRODUCT,viewedProductLocalInfoId);
  }
  @Override
  public List<ProductDTO> getRecentlyViewedProductDTOList(Long shopId,Long userId) throws Exception {
    List<ProductDTO> productDTOList = new ArrayList<ProductDTO>();
    if (shopId == null) return productDTOList;
    IRecentlyUsedDataService recentlyUsedDataService = ServiceManager.getService(IRecentlyUsedDataService.class);
    IPromotionsService promotionsService = ServiceManager.getService(IPromotionsService.class);
    int maxSize = ConfigUtils.getRecentlyViewedProductNum();
    List<RecentlyUsedDataDTO> recentlyUsedDataDTOList = recentlyUsedDataService.getRecentlyUsedDataDTOList(shopId,userId, RecentlyUsedDataType.VISITED_PRODUCT,maxSize);
    if (CollectionUtils.isNotEmpty(recentlyUsedDataDTOList)) {
      Set<Long> productLocalInfoIdSet = new HashSet<Long>();
      for (RecentlyUsedDataDTO recentlyUsedDataDTO : recentlyUsedDataDTOList) {
        productLocalInfoIdSet.add(recentlyUsedDataDTO.getDataId());
      }
      Map<Long, ProductDTO> productDTOMap = this.getProductDTOMapByProductLocalInfoIds(productLocalInfoIdSet);
      for (RecentlyUsedDataDTO recentlyUsedDataDTO : recentlyUsedDataDTOList) {
        ProductDTO productDTO = productDTOMap.get(recentlyUsedDataDTO.getDataId());
        List<PromotionsDTO> promotionsDTOs = null;
        Map<Long, ProductDTO> pMap = promotionsService.getProductPromotionDetail(productDTO.getShopId(), productDTO.getProductLocalInfoId());
        if (pMap != null) {
          ProductDTO productDTOTemp = pMap.get(productDTO.getProductLocalInfoId());
          if (productDTOTemp != null) {
            promotionsDTOs = productDTOTemp.getPromotionsDTOs();
          }
        }
        productDTO.setPromotionsDTOs(promotionsDTOs);
        productDTO.setInSalesPriceAfterCal(PromotionsUtils.calculateBargainPrice(promotionsDTOs, productDTO.getInSalesPrice()));
        String[] titles = PromotionsUtils.genPromotionTypesStr(promotionsDTOs);
        productDTO.setPromotionTypesShortStr(titles[0]);
        productDTO.setPromotionTypesStr(titles[1]);
        productDTOList.add(productDTO);
      }
    }
    return productDTOList;
  }

  public int countProductInSales(Long shopId){
    ProductWriter writer=productDaoManager.getWriter();
    return writer.countProductInSales(shopId);
  }

  public int countProductByPromotions(Long shopId,Long[] productIds){
    if(shopId==null||ArrayUtil.isEmpty(productIds)){
      return 0;
    }
    ProductWriter writer=productDaoManager.getWriter();
    return writer.countProductByPromotions(shopId,productIds);
  }

public int countAllStockProduct(Long shopId){
    ProductWriter writer=productDaoManager.getWriter();
    return writer.countAllStockProduct(shopId);
  }

  @Override
  public Node getVehicleBrandModelByShopId(Long shopId) {
    List<ShopVehicleBrandModelDTO> allVehicleBrandModelDTOList = StandardBrandModelCache.getShopVehicleBrandModelDTOList();
    List<ShopVehicleBrandModelDTO> shopVehicleBrandModelDTOList = getShopVehicleBrandModelByShopId(shopId);
    Map<Long, Object> modelMap = new HashMap<Long, Object>();
    Set<Long> brandIdSet = new HashSet<Long>();
    if(CollectionUtil.isNotEmpty(shopVehicleBrandModelDTOList)) {
      for(ShopVehicleBrandModelDTO shopVehicleBrandModelDTO : shopVehicleBrandModelDTOList) {
        modelMap.put(shopVehicleBrandModelDTO.getModelId(),shopVehicleBrandModelDTO);
        brandIdSet.add(shopVehicleBrandModelDTO.getBrandId());
      }
    }
    CheckNode root = buildCheckVehicleBrandModelTree(allVehicleBrandModelDTOList, modelMap,brandIdSet);
    root.reBuildTreeForChecked();
    return root;
  }

  @Override
  public Node getCheckedVehicleBrandModel(Set<Long> ids) {
    List<ShopVehicleBrandModelDTO> allVehicleBrandModelDTOList = StandardBrandModelCache.getShopVehicleBrandModelDTOList();
    List<ShopVehicleBrandModelDTO> shopVehicleBrandModelDTOList = this.generateVehicleBrandModelDTOByModelIds(ids);
    Map<Long, Object> modelMap = new HashMap<Long, Object>();
    Set<Long> brandIdSet = new HashSet<Long>();
    for (ShopVehicleBrandModelDTO dto : shopVehicleBrandModelDTOList) {
      modelMap.put(dto.getModelId(), dto);
      brandIdSet.add(dto.getBrandId());
    }
    CheckNode root = buildCheckVehicleBrandModelTree(allVehicleBrandModelDTOList, modelMap,brandIdSet);
    root.reBuildTreeForChecked();
    return root;
  }

  private CheckNode buildCheckVehicleBrandModelTree(List<ShopVehicleBrandModelDTO> allVehicleBrandModelDTOList,
                                             Map<Long, Object> shopVehicleBrandModelDTOMap,Set<Long> brandIdSet) {
    CheckNode root = new CheckNode();
    root.setId(-1L);     //根节点
    if (CollectionUtils.isEmpty(allVehicleBrandModelDTOList)) return root;
    CheckNode node;
    List<Node> nodeList = new ArrayList<Node>();
    Map<Long, Object> brandMap = new HashMap<Long, Object>();
    //model节点
    for (ShopVehicleBrandModelDTO dto : allVehicleBrandModelDTOList) {
      dto.setChecked(shopVehicleBrandModelDTOMap.get(dto.getModelId()) != null);
      node = dto.toModelCheckNode();
      nodeList.add(node);
      brandMap.put(dto.getBrandId(), dto);
    }
    //brand节点
    for(Long brandId : brandMap.keySet()) {
      ShopVehicleBrandModelDTO shopVehicleBrandModelDTO = (ShopVehicleBrandModelDTO)brandMap.get(brandId);
      node = shopVehicleBrandModelDTO.toBrandCheckNode();
      node.setExpanded(brandIdSet.contains(shopVehicleBrandModelDTO.getBrandId()));
      nodeList.add(node);
    }
    root.buildTree(root, nodeList);
    return root;
  }

  @Override
  public  List<ShopVehicleBrandModelDTO> generateVehicleBrandModelDTOByModelIds(Set<Long> ids) {
    IStandardBrandModelService standardBrandModelService = ServiceManager.getService(IStandardBrandModelService.class);
    List<ShopVehicleBrandModelDTO> shopVehicleBrandModelDTOList = new ArrayList<ShopVehicleBrandModelDTO>();
    Set<Long> brandIds = new HashSet<Long>();
    Map<Long, StandardVehicleBrandDTO> standardVehicleBrandDTOMap = new HashMap<Long, StandardVehicleBrandDTO>();
    if (CollectionUtils.isEmpty(ids)) return shopVehicleBrandModelDTOList;
    List<StandardVehicleModelDTO> standardVehicleModelDTOList = standardBrandModelService.getStandardVehicleModelListByIds(ids);
    if(CollectionUtil.isNotEmpty(standardVehicleModelDTOList)) {
      for(StandardVehicleModelDTO standardVehicleModelDTO : standardVehicleModelDTOList) {
        brandIds.add(standardVehicleModelDTO.getStandardVehicleBrandId());
      }
    }
    if(CollectionUtil.isNotEmpty(brandIds)) {
      standardVehicleBrandDTOMap = standardBrandModelService.getStandardVehicleBrandMapByIds(brandIds);
    }
    if(CollectionUtil.isNotEmpty(standardVehicleModelDTOList)) {
      for(StandardVehicleModelDTO standardVehicleModelDTO : standardVehicleModelDTOList) {
        StandardVehicleBrandDTO standardVehicleBrandDTO = (StandardVehicleBrandDTO)standardVehicleBrandDTOMap.get(standardVehicleModelDTO.getStandardVehicleBrandId());
        ShopVehicleBrandModelDTO vehicleBrandModelDTO = new ShopVehicleBrandModelDTO();

        vehicleBrandModelDTO.setBrandId(standardVehicleBrandDTO.getId());
        vehicleBrandModelDTO.setBrandName(standardVehicleBrandDTO.getName());
        vehicleBrandModelDTO.setFirstLetter(standardVehicleBrandDTO.getFirstLetter());

        vehicleBrandModelDTO.setModelId(standardVehicleModelDTO.getId());
        vehicleBrandModelDTO.setModelName(standardVehicleModelDTO.getName());

        shopVehicleBrandModelDTOList.add(vehicleBrandModelDTO);
      }
    }

    return shopVehicleBrandModelDTOList;
  }

  @Override
  public void updateShopVehicleBrandModel(Long shopId, Set<Long> vehicleModelIds) {
    ProductWriter writer=productDaoManager.getWriter();
    if(shopId == null) return;
    Set<Long> vehicleBrandModelSetDB = new HashSet<Long>();
    List<ShopVehicleBrandModelDTO> shopVehicleBrandModelDTOList = getShopVehicleBrandModelByShopId(shopId);
    Object status = writer.begin();
    try {
      //设置disable的
      if(CollectionUtil.isNotEmpty(shopVehicleBrandModelDTOList)) {
        for(ShopVehicleBrandModelDTO shopVehicleBrandModelDTO : shopVehicleBrandModelDTOList) {
          if(CollectionUtil.isEmpty(vehicleModelIds) || !vehicleModelIds.contains(shopVehicleBrandModelDTO.getModelId())) {
            shopVehicleBrandModelDTO.setDeleted(DeletedType.TRUE);
            saveOrUpdateShopVehicleBrandModel(shopVehicleBrandModelDTO);
          }
          vehicleBrandModelSetDB.add(shopVehicleBrandModelDTO.getModelId());
        }
      }
      //需要save的
      if(CollectionUtil.isNotEmpty(vehicleModelIds)) {
        Map<Long,ShopVehicleBrandModelDTO> shopVehicleBrandModelDTOMap = new HashMap<Long, ShopVehicleBrandModelDTO>();
        List<ShopVehicleBrandModelDTO> shopVehicleBrandModelDTOs = StandardBrandModelCache.getShopVehicleBrandModelDTOList();
        for(ShopVehicleBrandModelDTO shopVehicleBrandModelDTO : shopVehicleBrandModelDTOs) {
          shopVehicleBrandModelDTOMap.put(shopVehicleBrandModelDTO.getModelId(), shopVehicleBrandModelDTO);
        }
        for(Long id : vehicleModelIds) {
          if(id!= null && !vehicleBrandModelSetDB.contains(id)) {
            ShopVehicleBrandModelDTO shopVehicleBrandModelDTO = new ShopVehicleBrandModelDTO();
            shopVehicleBrandModelDTO.setShopId(shopId);
            shopVehicleBrandModelDTO.setDeleted(DeletedType.FALSE);
            shopVehicleBrandModelDTO.setModelId(id);
            shopVehicleBrandModelDTO.setModelName(shopVehicleBrandModelDTOMap.get(id).getModelName());
            shopVehicleBrandModelDTO.setBrandId(shopVehicleBrandModelDTOMap.get(id).getBrandId());
            shopVehicleBrandModelDTO.setBrandName(shopVehicleBrandModelDTOMap.get(id).getBrandName());
            shopVehicleBrandModelDTO.setFirstLetter(shopVehicleBrandModelDTOMap.get(id).getFirstLetter());
            saveOrUpdateShopVehicleBrandModel(shopVehicleBrandModelDTO);
          }
        }
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }


  }

  private void saveOrUpdateShopVehicleBrandModel(ShopVehicleBrandModelDTO shopVehicleBrandModelDTO) {
    if(shopVehicleBrandModelDTO == null) return;
    ProductWriter writer=productDaoManager.getWriter();
    if(shopVehicleBrandModelDTO.getId() != null) {
      ShopVehicleBrandModel shopVehicleBrandModel = writer.getById(ShopVehicleBrandModel.class, shopVehicleBrandModelDTO.getId());
      if(shopVehicleBrandModel != null) {
        shopVehicleBrandModel.setDeleted(shopVehicleBrandModelDTO.getDeleted());
      }
      writer.update(shopVehicleBrandModel);
    } else {
      ShopVehicleBrandModel shopVehicleBrandModel = new ShopVehicleBrandModel();
      shopVehicleBrandModel.fromDTO(shopVehicleBrandModelDTO);
      writer.save(shopVehicleBrandModel);
    }
  }
  @Override
  public List<ProductDTO> getInSalingProductForSupplyDemand(Long shopId,int maxSize){
    List<ProductDTO> productDTOs=new ArrayList<ProductDTO>();
    if(maxSize<=0){
      return productDTOs;
    }
    ProductWriter writer=productDaoManager.getWriter();
    List<Long> productIds=null;
    List<ProductDTO> tProductDTOs=null;
    ProductSearchCondition conditionDTO=new ProductSearchCondition();
    conditionDTO.setSortCondition(new Sort("lastInSalesTime","desc"));
    //1.取促销结束时间不为空的
    productIds=writer.getInSalingProductForSupplyDemand(shopId,0,maxSize,true);
    if(CollectionUtil.isNotEmpty(productIds)){
      conditionDTO.setProductIds(ArrayUtil.toLongArr(productIds));
      tProductDTOs=getProductInfo(conditionDTO);
      if(CollectionUtil.isNotEmpty(tProductDTOs)){
        productDTOs.addAll(tProductDTOs);
      }
    }
    //2.取促销结束时间为空的
    if(NumberUtil.subtraction(maxSize,productDTOs.size())>0){
      productIds=writer.getInSalingProductForSupplyDemand(shopId,0,(maxSize-productDTOs.size()),false);
      if(CollectionUtil.isNotEmpty(productIds)){
        conditionDTO.setProductIds(ArrayUtil.toLongArr(productIds));
        tProductDTOs=getProductInfo(conditionDTO);
        if(CollectionUtil.isNotEmpty(tProductDTOs)){
          productDTOs.addAll(tProductDTOs);
        }
      }
    }
    //3.取上架商品
    if(NumberUtil.subtraction(maxSize,productDTOs.size())>0){
      productIds=writer.getInSalingProductWithOutPromotion(shopId,0,(maxSize-productDTOs.size()));
      if(CollectionUtil.isNotEmpty(productIds)){
        conditionDTO.setProductIds(ArrayUtil.toLongArr(productIds));
        tProductDTOs=getProductInfo(conditionDTO);
        if(CollectionUtil.isNotEmpty(tProductDTOs)){
          productDTOs.addAll(tProductDTOs);
        }
      }
    }
    return productDTOs;
  }


  @Override
  public List<ProductDTO> getProductDTOByRelationSupplier(Long shopId) throws Exception {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    IPromotionsService promotionsService = ServiceManager.getService(IPromotionsService.class);
    List<RelationTypes> relationTypeList = new ArrayList<RelationTypes>();
    relationTypeList.add(RelationTypes.RELATED);
    relationTypeList.add(RelationTypes.CUSTOMER_COLLECTION);
    List<ProductDTO> productDTOs = new ArrayList<ProductDTO>();
    Set<Long> supplierShopIdSet = configService.getRelationWholesalerShopIds(shopId, relationTypeList);
    ProductWriter writer = productDaoManager.getWriter();
    List<Object[]> objectsList = null;
    if (supplierShopIdSet != null && CollectionUtils.isNotEmpty(supplierShopIdSet)) {
      objectsList = writer.getProductDTOByRelationSupplier(supplierShopIdSet);
    }
    if (CollectionUtil.isNotEmpty(objectsList)) {
      for (Object[] objects : objectsList) {
        if (objects != null && objects[0] != null && objects[1] != null) {
          Product product = (Product) objects[0];
          ProductLocalInfo productLocalInfo = (ProductLocalInfo) objects[1];
          ProductDTO productDTO = product.toDTO();
          productDTO.setProductLocalInfoDTO(productLocalInfo.toDTO());
          if (product.getKindId() != null) {
            Kind kind = writer.getById(Kind.class, product.getKindId());
            productDTO.setKindName(kind != null ? kind.getName() : null);
          }
          List<PromotionsDTO> promotionsDTOs = null;
          Map<Long, ProductDTO> pMap = promotionsService.getProductPromotionDetail(productDTO.getShopId(), productDTO.getProductLocalInfoId());
          if (pMap != null) {
            ProductDTO productDTOTemp = pMap.get(productDTO.getProductLocalInfoId());
            if (productDTOTemp != null) {
              promotionsDTOs = productDTOTemp.getPromotionsDTOs();
            }
          }
          productDTO.setPromotionsDTOs(promotionsDTOs);
          productDTO.setInSalesPriceAfterCal(PromotionsUtils.calculateBargainPrice(promotionsDTOs, productDTO.getInSalesPrice()));
          String[] titles = PromotionsUtils.genPromotionTypesStr(promotionsDTOs);
          productDTO.setPromotionTypesShortStr(titles[0]);
          productDTO.setPromotionTypesStr(titles[1]);
          productDTOs.add(productDTO);
        }
      }
    }
    return productDTOs;
  }

  public List<ProductDTO> filterProductByShopKind(Long shopId,ProductDTO... productDTOs){
    List<ProductDTO> filterProductDTOs=new ArrayList<ProductDTO>();
    if(ArrayUtil.isEmpty(productDTOs)||shopId==null){
      return filterProductDTOs;
    }
    IConfigService configService=ServiceManager.getService(IConfigService.class);
    ShopDTO shopDTO=configService.getShopById(shopId);
    if(shopDTO==null||shopDTO.getShopKind()==null){
      return filterProductDTOs;
    }
    for(ProductDTO productDTO:productDTOs){
      ShopDTO tShopDTO=configService.getShopById(productDTO.getShopId());
      if(tShopDTO!=null&&shopDTO.getShopKind().equals(tShopDTO.getShopKind())){
        filterProductDTOs.add(productDTO);
      }
    }
    return filterProductDTOs;
  }

  public void filterInvalidPromotions(List<ProductDTO> list) {
    if (list != null && CollectionUtils.isNotEmpty(list)) {
      for (ProductDTO productDTO : list) {
        List<PromotionsDTO> promotionsDTOList = productDTO.getPromotionsDTOs();
        List<PromotionsDTO> target = new ArrayList<PromotionsDTO>();
        if (promotionsDTOList != null && CollectionUtils.isNotEmpty(promotionsDTOList)) {
          for (PromotionsDTO pd : promotionsDTOList) {
            if (PromotionsEnum.PromotionStatus.USING.equals(pd.getStatus())) {
              target.add(pd);
            }
          }
        }
        productDTO.setPromotionsDTOs(target);
        productDTO.setInSalesPriceAfterCal(PromotionsUtils.calculateBargainPrice(target, productDTO.getInSalesPrice()));
        String[] titles = PromotionsUtils.genPromotionTypesStr(target);
        productDTO.setPromotionTypesShortStr(titles[0]);
        productDTO.setPromotionTypesStr(titles[1]);
        productDTO.setHasBargain(PromotionsUtils.hasBargain(target));
      }
    }
  }

  @Override
  public Node getNormalProductVehicleBrandModelByNormalProductId(Long normalProductId) {
    List<ShopVehicleBrandModelDTO> allVehicleBrandModelDTOList = StandardBrandModelCache.getShopVehicleBrandModelDTOList();
    List<NormalProductVehicleBrandModelDTO> normalProductVehicleBrandModelDTOList = getNormalProductVehicleBrandModelDTOByNormalProductId(normalProductId);
    Map<Long, Object> modelMap = new HashMap<Long, Object>();
    Set<Long> brandIdSet = new HashSet<Long>();
    if(CollectionUtil.isNotEmpty(normalProductVehicleBrandModelDTOList)) {
      for(NormalProductVehicleBrandModelDTO normalProductVehicleBrandModelDTO : normalProductVehicleBrandModelDTOList) {
        modelMap.put(normalProductVehicleBrandModelDTO.getModelId(),normalProductVehicleBrandModelDTO);
        brandIdSet.add(normalProductVehicleBrandModelDTO.getBrandId());
      }
    }
    CheckNode root = buildCheckVehicleBrandModelTree(allVehicleBrandModelDTOList, modelMap,brandIdSet);
    root.reBuildTreeForChecked();
    return root;
  }

  public Map<Long,NormalProductDTO> getNormalProductDTOMapByIds(List<Long> ids) {
    Map<Long, NormalProductDTO> normalProductDTOMap = new HashMap<Long, NormalProductDTO>();
    if (CollectionUtils.isEmpty(ids)) {
      return normalProductDTOMap;
    }

    ProductWriter writer = productDaoManager.getWriter();

    List<NormalProduct> normalProductList = writer.getNormalProductDTOByIds(ids);

    if (CollectionUtils.isEmpty(normalProductList)) {
      return normalProductDTOMap;
    }

    for (NormalProduct normalProduct : normalProductList) {
      normalProductDTOMap.put(normalProduct.getId(), normalProduct.toDTO());
    }
    return normalProductDTOMap;
  }

  @Override
  public Map<Long,NormalProductDTO> getSimpleNormalProductDTO(ProductSearchCondition searchCondition) {
    ProductWriter writer = productDaoManager.getWriter();
    List<NormalProduct> normalProductList = writer.getAllNormalProductByCondition(searchCondition);

    if (CollectionUtils.isEmpty(normalProductList)) {
      return null;
    }
    Map<Long,NormalProductDTO> normalProductDTOs = new HashMap<Long, NormalProductDTO>();
    for (NormalProduct normalProduct : normalProductList) {
      normalProductDTOs.put(normalProduct.getId(),normalProduct.toDTO());
    }
    return normalProductDTOs;
  }

  public List<Long> getCommodityAdProductIds(Pager pager,Long... adShopIds){
    if(ArrayUtil.isEmpty(adShopIds)){
      return null;
    }
    ProductWriter writer = productDaoManager.getWriter();
    return writer.getCommodityAdProductIds(pager,adShopIds);
  }

  public List<Long> getAdShopIdByShopArea(Long province,Long city,Long region){
    ProductWriter writer = productDaoManager.getWriter();
    return writer.getAdShopIdByShopArea(province,city,region);
  }

  public int countCommodityAdProduct(Long... adShopIds){
    if(ArrayUtil.isEmpty(adShopIds)){
      return 0;
    }
    ProductWriter writer = productDaoManager.getWriter();
    return writer.countCommodityAdProduct(adShopIds);
  }

  public List<Long> getLackAutoPreBuyProductId(Long shopId) throws ParseException {
    IConfigService configService=ServiceManager.getService(IConfigService.class);
    int auto_lack_pre_buy_num=NumberUtil.intValue(configService.getConfig("lack_auto_pre_buy_num", ShopConstant.BC_SHOP_ID));
    int auto_lack_pre_buy_day=NumberUtil.intValue(configService.getConfig("lack_auto_pre_buy_day",ShopConstant.BC_SHOP_ID));
    ProductWriter writer = productDaoManager.getWriter();
    Long startDate=DateUtil.getDateByDay(auto_lack_pre_buy_day*(-1));
    return writer.getLackAutoPreBuyProductId(shopId,startDate,auto_lack_pre_buy_num);
  }

}