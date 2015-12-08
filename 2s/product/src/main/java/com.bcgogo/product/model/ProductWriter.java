package com.bcgogo.product.model;

import com.bcgogo.common.Pager;
import com.bcgogo.common.TwoTuple;
import com.bcgogo.enums.ProductStatus;
import com.bcgogo.enums.txn.finance.PaymentType;
import com.bcgogo.product.PromotionOrderRecordQuery;
import com.bcgogo.product.dto.*;
import com.bcgogo.enums.PromotionsEnum;
import com.bcgogo.product.model.app.VehicleDictionary;
import com.bcgogo.product.model.app.Dictionary;
import com.bcgogo.product.model.app.DictionaryFaultInfo;
import com.bcgogo.product.model.app.VehicleDictionary;
import com.bcgogo.product.productManage.ProductSearchCondition;
import com.bcgogo.product.productManage.PromotionSearchCondition;
import com.bcgogo.search.dto.SearchConditionDTO;
import com.bcgogo.service.GenericWriterDao;
import com.bcgogo.stat.dto.CostStatConditionDTO;
import com.bcgogo.stat.dto.PurchaseInventoryStatDTO;
import com.bcgogo.txn.dto.PromotionIndex;
import com.bcgogo.user.dto.VehicleDTO;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.NumberUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.transaction.support.ResourceTransactionManager;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: wjl
 * Date: 11-9-23
 * Time: 下午4:12
 * To change this template use File | Settings | File Templates.
 */
public class ProductWriter extends GenericWriterDao {
  public ProductWriter(ResourceTransactionManager transactionManager) {
    super(transactionManager);
  }

  public Brand getBrand(Long brandId) {
    Session session = getSession();
    try {
      if (brandId != null) {
        Brand brand = this.getById(Brand.class, brandId);
        return brand;
      }
      return null;
    } finally {
      release(session);
    }
  }

  public Model getModel(Long modelId) {
    Session session = getSession();
    try {
      if (modelId != null) {
        Model model = this.getById(Model.class, modelId);
        return model;
      }
      return null;
    } finally {
      release(session);
    }
  }

  public Object[] getProductByProductLocalInfoId(Long productLocalInfoId, Long shopId) {
    Session session = getSession();
    try {
      Query query = SQL.getProductByProductLocalInfoId(session, shopId, productLocalInfoId);
      return (Object[]) query.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List<Object[]> getProductByProductLocalInfoId(Long shopId, Long... productLocalInfoId) {
    Session session = getSession();
    try {
      Query query = SQL.getProductByProductLocalInfoId(session, shopId, productLocalInfoId);
      return query.list();
    } finally {
      release(session);
    }
  }
  public List<Object[]> getProductDTOByRelationSupplier(Set<Long> shopIdSet) {
    Session session = getSession();
    try {
      Query query = SQL.getProductDTOByRelationSupplier(session, shopIdSet);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<Object[]> getProductByShopIdAndHaveUnit(Long shopId) {
    Session session = getSession();
    try {
      Query query = SQL.getProductByShopIdAndHaveUnit(session, shopId);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<Product> getSimpleProductListById(Long... id) {
    Session session = getSession();
    try {
      Query query = SQL.getSimpleProductListById(session,id);
      return (List<Product>) query.list();
    } finally {
      release(session);
    }
  }

  public List<ProductLocalInfo> getShopAdProductLocalInfo(Long... shopIds) {
    Session session = getSession();
    try {
      Query query = SQL.getShopAdProductLocalInfo(session,shopIds);
      return query.list();
    } finally {
      release(session);
    }
  }

public List<ProductLocalInfo> getLastInSalesProductLocalInfo(Long shopId,int maxRows) {
    Session session = getSession();
    try {
      Query query = SQL.getLastInSalesProductLocalInfo(session,shopId,maxRows);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<Product> getProductByProductLocalInfoIds(Long shopId, Long... productLocalInfoIds) {
    Session session = getSession();
    try {
      Query query = SQL.getProductByProductLocalInfoIds(session, shopId, productLocalInfoIds);
      return (List<Product>) query.list();
    } finally {
      release(session);
    }
  }

  public List getProductVehicleByVehicleIds(Long productId, Long brandId, Long modelId, Long yearId, Long engineId) {
    Session session = getSession();
    try {
      Query query = SQL.getProductVehicleByVehicleIds(session, productId, brandId, modelId, yearId, engineId);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<ProductVehicle> getProductVehicleByProductId(Long productId) {
    if (productId == null) return null;
    Session session = getSession();
    try {
      Query query = SQL.getProductVehicleByProductId(session, productId);
      return query.list();
    } finally {
      release(session);
    }
  }

  public Template getTemplate(Long templateId) {
    Session session = getSession();
    try {
      Template template = this.getById(Template.class, templateId);
      return template;
    } finally {
      release(session);
    }
  }

  public Year getYear(Long yearId) {
    Session session = getSession();
    try {
      if (yearId != null) {
        Year year = this.getById(Year.class, yearId);
        return year;
      }
      return null;
    } finally {
      release(session);
    }
  }


  public List getAllModel() {
    Session session = getSession();
    try {
      Query query = SQL.getAllModel(session);
      List modelList = query.list();
      return modelList;
    } finally {
      release(session);
    }
  }

  public Long getProductCount() {
    Session session = getSession();
    try {
      Query query = SQL.getProductCount(session);
      return (Long) query.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List getAllProductByRows(int startNo, int rows) {
    Session session = getSession();
    try {
      Query query = SQL.getAllProduct(session);
      query.setFirstResult((startNo - 1) * rows).setMaxResults(rows);
      List productList = query.list();
      return productList;
    } finally {
      release(session);
    }
  }

  public List getProductsByShopId(Long shopId, Long start, int num) {
    Session session = getSession();
    try {
      Query query = SQL.getProducts(session, shopId, start, num);
      List productList = query.list();
      return productList;
    } finally {
      release(session);
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
  public List<ProductDTO> getProducts(Long shopId, Long start, int num) {
    List<ProductDTO> productDTOList = new ArrayList<ProductDTO>();
    Session session = getSession();
    try {
      Query query = SQL.getProducts(session, shopId, start, num);
      List productList = query.list();
      if (productList == null || productList.isEmpty()) {
        return productDTOList;
      }
      Product product = null;
      for (Object obj : productList) {
        product = (Product) obj;
        if (product == null) {
          continue;
        }
        productDTOList.add(product.toDTO());
      }
      return productDTOList;
    } finally {
      release(session);
    }
  }

  /**
   * 通过条件查询产品
   *
   * @param productQueryCondition
   * @return
   */
  public List<ProductLocalInfo> getProductLocalInfosByCondition(ProductQueryCondition productQueryCondition) {
    List<ProductLocalInfo> productLocalInfoDTOs = new ArrayList<ProductLocalInfo>();
    if (productQueryCondition.getShopId() == null || productQueryCondition.getShopId() == 0L) {
      LOG.warn("[getProductLocalInfosByCondition],shopId is null or 0L.");
      return productLocalInfoDTOs;
    }
    Session session = getSession();
    try {
      Query query = SQL.getProductLocalInfosByCondition(session, productQueryCondition);
      return query.list();
    } finally {
      release(session);
    }
  }


  public List getAllBrand() {
    Session session = getSession();
    try {
      Query query = SQL.getAllBrand(session);
      List brandList = query.list();
      return brandList;
    } finally {
      release(session);
    }
  }

  public List<ProductUnit> getAllProductUnit() {
    Session session = getSession();
    try {
      Query query = SQL.getAllProductUnit(session);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List getModelWithFirstLetter(String firstLetter, Long brandId) {
    Session session = getSession();
    try {
      Query query = SQL.getModelWithFirstLetter(session, firstLetter, brandId);
      List modelList = query.list();
      return modelList;
    } finally {
      release(session);
    }
  }

  public List getBrandWithFirstLetter(String firstLetter) {
    Session session = getSession();
    try {
      Query query = SQL.getBrandWithFirstLetter(session, firstLetter);
      List brandList = query.list();
      return brandList;
    } finally {
      release(session);
    }
  }

  public Brand getBrandByName(String name) {
    Session session = getSession();
    try {
      Query query = SQL.getBrandByName(session, name);
      List<Brand> list = query.list();
      return CollectionUtil.getFirst(list);
    } finally {
      release(session);
    }
  }


  public Model getModelByName(Long brandId, String name) {
    Session session = getSession();
    try {
      Query query = SQL.getModelByName(session, brandId, name);
      List<Model> list = query.list();
      return CollectionUtil.getFirst(list);
    } finally {
      release(session);
    }
  }

  public Year getYearByName(Integer year, Long modelId, Long brandId) {
    Session session = getSession();
    try {
      Query query = SQL.getYearByName(session, year, modelId, brandId);
      return (Year) query.uniqueResult();
    } finally {
      release(session);
    }
  }

  public Engine getEngineByName(String engine, Long yearId, Long modelId, Long brandId) {
    Session session = getSession();
    try {
      Query query = SQL.getEngineByName(session, engine, yearId, modelId, brandId);
      return (Engine) query.uniqueResult();
    } finally {
      release(session);
    }
  }

  public List getBrandByKeyword(String searchWord) {
    Session session = getSession();
    try {
      Query query = SQL.getBrandByKeyword(session, searchWord);
      List brandList = query.list();
      return brandList;
    } finally {
      release(session);
    }
  }

  public List getProductFirstLetterByWord(String chnChar) {
    Session session = getSession();
    try {
      SQLQuery query = SQL.getProductFirstLetterByWord(session, chnChar);
      query.addScalar("firstLetter", StandardBasicTypes.STRING);
      List firstLetterList = query.list();
      return firstLetterList;
    } finally {
      release(session);
    }
  }

  public List getProductByProductInfo(String productname, String productbrand, String productspec, String productmodel) {
    Session session = getSession();
    try {
      Query query = SQL.getProductByProductInfo(session, productname, productbrand, productspec, productmodel);
      List productList = query.list();
      return productList;
    } finally {
      release(session);
    }
  }

  public List getBasicProductByProductInfo(Long shopId, ProductDTO productDTO) {
    Session session = getSession();
    try {

//      Query query = SQL.getBasicProductByProductInfo(session, shopId, productDTO);
      //改为6属性唯一判断
      Query query = SQL.getProductsBy7P(session, shopId, productDTO);
      return query.list();
    } finally {
      release(session);
    }
  }


  public List<ProductLocalInfo> getProductLocalInfoByProductId(Long productId, Long shopId) {
    Session session = getSession();
    try {
      Query query = SQL.getProductLocalInfoByProductId(session, productId, shopId);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<ProductLocalInfo> getProductLocalInfoById(Long productId, Long shopId) {
    Session session = getSession();
    try {
      Query query = SQL.getProductLocalInfoById(session, productId, shopId);
      List productList = query.list();
      return productList;
    } finally {
      release(session);
    }
  }

  public List<ProductLocalInfo> getProductLocalInfoByIds(Long shopId, Long... productIds) {
    Session session = getSession();
    try {
      Query query = SQL.getProductLocalInfoByIds(session, shopId, productIds);
      List<ProductLocalInfo> productList = (List<ProductLocalInfo>) query.list();
      return productList;
    } finally {
      release(session);
    }
  }

  public List<ProductLocalInfo> getProductLocalInfoByIds(Long... productLocalInfoIds) {
    Session session = getSession();
    try {
      Query query = SQL.getProductLocalInfoByIds(session, productLocalInfoIds);
      List<ProductLocalInfo> productList = (List<ProductLocalInfo>) query.list();
      return productList;
    } finally {
      release(session);
    }
  }


  //获得地区名称
  public List getAreaByShopId(Long shopId) {
    Session session = getSession();
    try {
      SQLQuery query = SQL.getAreaByShopId(session, shopId);
      query.addScalar("name", StandardBasicTypes.STRING);
      List shops = query.list();
      return shops;

    } finally {
      release(session);
    }
  }

  // 根据地区ID 找到地区名称
  public List getCarNoByareaById(String areaid) {
    Session session = getSession();
    try {
      SQLQuery query = SQL.getCarNoByareaById(session, areaid);
      query.addScalar("name", StandardBasicTypes.STRING);
      List areaDTOs = query.list();
      return areaDTOs;
    } finally {
      release(session);
    }
  }

  //根据地区名称查找车牌
  public List getCarNoByAreaName(String area) {
    Session session = getSession();
    try {
      SQLQuery query = SQL.getCarNoByarea(session, area);
      query.addScalar("carno", StandardBasicTypes.STRING);
      List licenseplateDTOs = query.list();
      return licenseplateDTOs;

    } finally {
      release(session);
    }
  }

  public Licenseplate getCarNoByAreaNo(Long areaNo) {
    Session session = getSession();
    try {
      Query query = SQL.getCarNoByAreaNo(session, areaNo);
      return (Licenseplate) query.uniqueResult();
    } finally {
      release(session);
    }
  }

  //根据车牌首汉字查询
  public List getCarNosByFirstLetters(String carnoFirstLetter) {
    Session session = getSession();
    try {
      SQLQuery query = SQL.getCarNosByFirstLetters(session, carnoFirstLetter);
      query.addScalar("carno", StandardBasicTypes.STRING);
      List licenseplateDTOs = query.list();

      return licenseplateDTOs;


    } finally {
      release(session);
    }
  }

  //根据车牌的首英文字母查询
  public List getCarNosByAreaFirstLetters(String areaFirstLetter) {
    Session session = getSession();
    try {
      SQLQuery query = SQL.getCarNosByAreaFirstLetters(session, areaFirstLetter);
      query.addScalar("areaFirstcarno", StandardBasicTypes.STRING);
      List licenseplateDTOs = query.list();

      return licenseplateDTOs;


    } finally {
      release(session);
    }
  }

  //根据车牌查找车牌号

  public List getCarsByCarNos(String carNo, Long shopId) {
    Session session = getSession();
    try {
      SQLQuery query = SQL.getCarsByCarNos(session, carNo, shopId);
      query.addScalar("licenceNo", StandardBasicTypes.STRING);
      List vehicles = query.list();
      return vehicles;

    } finally {
      release(session);
    }
  }

  //反向本地模糊查询
  public List getCarsByCarNosReverse(String carNoValue, String area, Long shopId) {
    Session session = getSession();
    try {
      SQLQuery query = SQL.getCarsByCarNosReverse(session, carNoValue, area, shopId);
      query.addScalar("licenceNoRevert", StandardBasicTypes.STRING);
      List vehicles = query.list();
      return vehicles;

    } finally {
      release(session);
    }

  }

  //车牌前缀
  public Licenseplate getLicenseplate(Long licenseplateId) {
    Session session = getSession();
    try {
      Licenseplate lplate = this.getById(Licenseplate.class, licenseplateId);
      return lplate;
    } finally {
      release(session);
    }
  }

  //商品管理
  public List<ProductAdmin> getProductAdminList(int pageNo, int pageSize) {
    Session session = this.getSession();

    try {
      Query q = SQL.getProductAdminList(session, pageNo, pageSize);

      return (List<ProductAdmin>) q.list();
    } finally {
      release(session);
    }
  }

  //查找汉字首字母
  public List getFirstLetterFromChnFirstLetter(String hanzi) {
    Session session = this.getSession();
    try {
      Query q = SQL.getFirstLetterFromChnFirstLetter(session, hanzi);
      return q.list();
    } finally {
      release(session);
    }
  }

  public Product getProductById(Long productId, Long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getProductById(session, productId, shopId);
      if (q != null) {
        List result = q.list();
        if (CollectionUtils.isNotEmpty(result)) {
          return (Product) result.get(0);
        }
      }
      return null;

    } finally {
      release(session);
    }
  }

  public ProductBarcode getProductByShopIdAndBarcode(Long shopId, String barcode) {
    Session session = this.getSession();
    try {
      List list = SQL.getProductByShopIdAndBarcode(session, shopId, barcode).list();
      if (list != null && list.size() > 0) {
        return (ProductBarcode) list.get(0);
      } else {
        return null;
      }
    } finally {
      release(session);
    }
  }

  public ProductBarcode getProductBarcodeByBarcode(String barcode) {
    Session session = this.getSession();
    try {
      List list = SQL.getProductBarcodeByBarcode(session, barcode).list();
      if (list != null && list.size() > 0) {
        return (ProductBarcode) list.get(0);
      } else {
        return null;
      }
    } finally {
      release(session);
    }
  }

  public Product getProductByBarcode(String barcode) {
    Session session = this.getSession();
    try {
      List<Product> list = SQL.getStandardProductByBarcode(session, barcode).list();
      if (list != null && list.size() > 0) {
        return list.get(0);
      } else {
        return null;
      }
    } finally {
      release(session);
    }
  }


  public List<Object[]> getProductDataByProductLocalInfoId(Long shopId, Long... productLocalInfoId) {
    Session session = this.getSession();
    try {
      Query query = SQL.getProductDataByProductLocalInfoId(session, shopId, productLocalInfoId);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<Long> getProductLocalInfoIdList(Long shopId, int start, int num) {
    Session session = this.getSession();
    try {
      Query query = SQL.getProductLocalInfoIdList(session, shopId, start, num);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<VehicleDTO> getVehicleDTOListByBrand() {
    Session session = this.getSession();
    try {
      Query query = SQL.getVehicleDTOListByBrand(session);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<VehicleDTO> getVehicleDTOListByModel() {
    Session session = this.getSession();
    try {
      Query query = SQL.getVehicleDTOListByModel(session);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<VehicleDTO> getVehicleDTOListByYear() {
    Session session = this.getSession();
    try {
      Query query = SQL.getVehicleDTOListByYear(session);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<VehicleDTO> getVehicleDTOListByEngine() {
    Session session = this.getSession();
    try {
      Query query = SQL.getVehicleDTOListByEngine(session);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<ProductSupplierDTO> getProductSupplierDTO(Long productId, Long shopId) {
    Session session = this.getSession();
    try {
      Query query = SQL.getProductSupplier(session, productId, shopId);
      List<ProductSupplier> list = (List<ProductSupplier>) query.list();
      if (CollectionUtils.isEmpty(list)) {
        return null;
      }
      List<ProductSupplierDTO> productSupplierDTOs = new ArrayList<ProductSupplierDTO>();
      for (ProductSupplier productSupplier : list) {
        productSupplierDTOs.add(productSupplier.toDTO());
      }
      return productSupplierDTOs;
    } finally {
      release(session);
    }
  }

  public List<ProductSupplier> getProductSuppliers(Long productId, Long shopId) {
    Session session = this.getSession();
    try {
      Query query = SQL.getProductSupplier(session, productId, shopId);
      return (List<ProductSupplier>) query.list();
    } finally {
      release(session);
    }
  }

  public List<ProductSupplierDTO> getProductSupplierDTO(Long[] productLocalInfoIds, Long shopId) {
    Session session = this.getSession();
    try {
      Query query = SQL.getProductSupplierByProductIds(session, productLocalInfoIds, shopId);
      List<ProductSupplier> list = (List<ProductSupplier>) query.list();
      if (CollectionUtils.isEmpty(list)) {
        return null;
      }
      List<ProductSupplierDTO> productSupplierDTOs = new ArrayList<ProductSupplierDTO>();
      for (ProductSupplier productSupplier : list) {
        productSupplierDTOs.add(productSupplier.toDTO());
      }
      return productSupplierDTOs;
    } finally {
      release(session);
    }
  }


  public List<Object[]> getProductDTOMapByProductLocalInfoIds(Set<Long> productLocalInfoIds) {
    Session session = this.getSession();
    try {
      Query query = SQL.getProductDTOMapByProductLocalInfoIds(session, productLocalInfoIds);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<Object[]> getProductDTOMapByProductLocalInfoIds(Long shopId, Set<Long> productLocalInfoIds) {
    Session session = this.getSession();
    try {
      Query query = SQL.getProductDTOMapByProductLocalInfoIds(session, shopId, productLocalInfoIds);
      return query.list();
    } finally {
      release(session);
    }
  }


  public List<ProductSupplierDTO> getLastProductSupplierByProductIds(Long[] productIds) {
    Session session = this.getSession();
    try {
      Query query = SQL.getLastProductSupplierByProductIds(session, productIds);
      List<ProductSupplier> list = (List<ProductSupplier>) query.list();
      if (CollectionUtils.isEmpty(list)) {
        return null;
      }
      List<ProductSupplierDTO> productSupplierDTOs = new ArrayList<ProductSupplierDTO>();
      for (ProductSupplier productSupplier : list) {
        productSupplierDTOs.add(productSupplier.toDTO());
      }
      return productSupplierDTOs;
    } finally {
      release(session);
    }
  }

  public ProductDTO getProductInfoByCommodityCode(long shopId, String commodityCode) {
    if (StringUtils.isBlank(commodityCode)) {
      return null;
    }
    Session session = this.getSession();
    try {
      Query query = SQL.getProductByCommodityCode(session, shopId, commodityCode);
      List<Object[]> list = query.list();
      if (CollectionUtils.isNotEmpty(list)) {
        Product product = (Product) list.get(0)[0];
        ProductLocalInfo productLocalInfo = (ProductLocalInfo) list.get(0)[1];
        ProductDTO productDTO = null;
        if (product != null) {
          productDTO = product.toDTO();
        }
        if (productLocalInfo != null && productDTO != null) {
          productDTO.setProductLocalInfoDTO(productLocalInfo.toDTO());
        }
        return productDTO;
      } else {
        return null;
      }
    } finally {
      release(session);
    }
  }

  public List<ProductDTO> getProductDTOsByCommodityCodes(Long shopId, String... commodityCodes) {
    List<ProductDTO> productDTOs = new ArrayList<ProductDTO>();
    if (commodityCodes == null || commodityCodes.length == 0) {
      return productDTOs;
    }
    Set<String> commodityCodeSet = new HashSet<String>((int) (commodityCodes.length / 0.75f + 1));
    for (String commodityCode : commodityCodes) {
      if (StringUtils.isNotBlank(commodityCode)) {
        commodityCodeSet.add(commodityCode);
      }
    }
    Session session = this.getSession();
    try {
      Query query = SQL.getProductsByCommodityCodes(session, shopId, commodityCodeSet);
      List<Object[]> list = query.list();
      if (CollectionUtils.isNotEmpty(list)) {
        for (Object[] objects : list) {
          Product product = (Product) objects[0];
          ProductLocalInfo productLocalInfo = (ProductLocalInfo) objects[1];
          ProductDTO productDTO = null;
          if (product != null) {
            productDTO = product.toDTO();
          }
          if (productLocalInfo != null && productDTO != null) {
            productDTO.setProductLocalInfoDTO(productLocalInfo.toDTO());
          }
          if (productDTO != null) {
            productDTOs.add(productDTO);
          }
        }
      }
      return productDTOs;
    } finally {
      release(session);
    }
  }

  public Long getKindIdByName(Long shopId, String productKind) {
    Session session = this.getSession();
    try {
      Query q = SQL.getKindIdByName(session, shopId, productKind);
      List<Kind> idList = q.list();
      if (idList == null || idList.size() == 0) {
        return null;
      } else {
        return idList.get(0).getId();
      }
    } finally {
      release(session);
    }
  }

  public List<String> getProductKindsRecentlyUsed(Long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getProductKindsRecentlyUsed(session, shopId);
      List<String> kindNameList = q.list();
      return kindNameList;
    } finally {
      release(session);
    }
  }

  public List<String> getProductKindsWithFuzzyQuery(Long shopId, String keyword) {
    Session session = this.getSession();
    try {
      Query q = SQL.getProductKindsWithFuzzyQuery(session, shopId, keyword);
      List<String> kindNameList = q.list();
      return kindNameList;
    } finally {
      release(session);
    }
  }

  public Long getKindIdByDisabledName(Long shopId, String productKind) {
    Session session = this.getSession();
    try {
      Query q = SQL.getKindIdByDisabledName(session, shopId, productKind);
      List<Kind> idList = q.list();
      if (idList == null || idList.size() == 0) {
        return null;
      } else {
        return idList.get(0).getId();
      }
    } finally {
      release(session);
    }
  }

  public List<Kind> getProductKindById(Long... kindId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getProductKindById(session, kindId);
      return (List<Kind>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Kind> getProductKindByIdAndShopId(Long shopId, Set<Long> kindIds) {
    Session session = this.getSession();
    try {
      Query q = SQL.getProductKindByShopIdAndIds(session, shopId, kindIds);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<Kind> getAllEnabledProductKindByShop(Long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getAllEnabledProductKindByShop(session, shopId);
      return (List<Kind>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Kind> getKindByNames(Long shopId, Set<String> kindNames) {
    Session session = this.getSession();
    try {
      Query q = SQL.getKindByNames(session, shopId, kindNames);
      return (List<Kind>) q.list();
    } finally {
      release(session);
    }
  }

  public List<ProductDTO> getProductDTOsByProductKindId(long shopId, Long... productKindId) {
    Session session = this.getSession();
    try {
      List<ProductDTO> productDTOs = new ArrayList<ProductDTO>();
      Query query = SQL.getProductDTOsByProductKindId(session, shopId, productKindId);
      List<Object[]> list = query.list();
      if (CollectionUtils.isNotEmpty(list)) {
        for (Object[] objects : list) {
          Product product = (Product) objects[0];
          ProductLocalInfo productLocalInfo = (ProductLocalInfo) objects[1];
          ProductDTO productDTO = null;
          if (product != null) {
            productDTO = product.toDTO();
          }
          if (productLocalInfo != null && productDTO != null) {
            productDTO.setProductLocalInfoDTO(productLocalInfo.toDTO());
          }
          if (productDTO != null) {
            productDTOs.add(productDTO);
          }
        }
      }
      return productDTOs;

    } finally {
      release(session);
    }
  }


  public List<PurchaseInventoryStatDTO> getProductListForCostStat(Long shopId, String[] queryFields) {
    Session session = getSession();
    try {
      Query query = SQL.getProductListForCostStat(session, shopId, queryFields);
      List<Object[]> list = query.list();
      List<PurchaseInventoryStatDTO> result = new ArrayList<PurchaseInventoryStatDTO>();
      if (CollectionUtils.isNotEmpty(list)) {
        for (Object[] o : list) {
          PurchaseInventoryStatDTO purchaseInventoryStatDTO = new PurchaseInventoryStatDTO();
          purchaseInventoryStatDTO.setProductIdStr(o[0] == null ? "" : o[0].toString());
          purchaseInventoryStatDTO.setProductName(o[1] == null ? "" : o[1].toString());
          if (ArrayUtils.contains(queryFields, CostStatConditionDTO.FIELD_BRAND)) {
            purchaseInventoryStatDTO.setProductBrand(o[2] == null ? "" : o[2].toString());
          }
          if (ArrayUtils.contains(queryFields, CostStatConditionDTO.FIELD_VEHICLE_MODEL)) {
            purchaseInventoryStatDTO.setVehicleBrand(o[3] == null ? "" : o[3].toString());
            purchaseInventoryStatDTO.setVehicleModel(o[4] == null ? "" : o[4].toString());
          }
          String[] idStrs = purchaseInventoryStatDTO.getProductIdStr().split(",");
          List<Long> ids = new ArrayList<Long>();
          for (int i = 0; i < idStrs.length; i++) {
            ids.add(NumberUtil.longValue(idStrs[i]));
          }
          purchaseInventoryStatDTO.setProductIds(ids);
          result.add(purchaseInventoryStatDTO);
        }
      }
      return result;
    } finally {
      release(session);
    }
  }

  public List<Long> getProductLocalInfoIdListByProductIds(List<Long> ids) {
    Session session = getSession();
    try {
      Query query = SQL.getProductLocalInfoIdListByProductIds(session, ids);
      Object o = query.uniqueResult();
      if (o != null) {
        String[] originIds = o.toString().split(",");
        List<Long> result = new ArrayList<Long>();
        for (String id : originIds) {
          result.add(NumberUtil.longValue(id));
        }
        return result;
      }
      return new ArrayList<Long>();
    } finally {
      release(session);
    }
  }

  public List<ProductMapping> getProductMappings(ProductMappingDTO productMappingIndex) {
    Session session = getSession();
    try {
      Query query = SQL.getProductMappings(session, productMappingIndex);
      return (List<ProductMapping>) query.list();
    } finally {
      release(session);
    }

  }

  public List<ProductMapping> getCustomerTradedProductMappingByProductIds(Long customerShopId, Long... productLocalInfoIds) {
    Session session = getSession();
    try {
      Query query = SQL.getCustomerTradedProductMappingByProductIds(session, customerShopId, productLocalInfoIds);
      return (List<ProductMapping>) query.list();
    } finally {
      release(session);
    }
  }

  public List<ProductMapping> getSupplierProductMappingByProductIds(Long supplierShopId, Long customerShopId, Set<Long> productLocalInfoIds) {
    Session session = getSession();
    try {
      Query query = SQL.getSupplierProductMappingByProductIds(session, supplierShopId, customerShopId, productLocalInfoIds);
      return (List<ProductMapping>) query.list();
    } finally {
      release(session);
    }

  }


  public List<Product> getProductDTOsBy7P(Long shopId, ProductDTO searchCondition) {
    long begin = System.currentTimeMillis();
    LOG.debug("AOP_SQL start:ProductWriter:getProductDTOsBy7P");
    Session session = getSession();
    try {
      Query query = SQL.getProductsBy7P(session, shopId, searchCondition);
      return (List<Product>) query.list();
    } finally {
      LOG.debug("AOP_SQL end:ProductWriter:getProductDTOsBy7P 用时：{}ms", System.currentTimeMillis() - begin);
      release(session);
    }
  }

  public List<ProductMapping> getCustomerProductMappingByCustomerProductLocalInfoIds(Long customerShopId, Long supplierShopId, Long... customerProductLocalInfoIds) {
    Session session = getSession();
    try {
      Query query = SQL.getCustomerProductMappingByCustomerProductLocalInfoIds(session, customerShopId, supplierShopId, customerProductLocalInfoIds);
      return (List<ProductMapping>) query.list();
    } finally {
      release(session);
    }
  }

  public boolean checkSameProduct(Long shopId, ProductDTO productDTO) {
    Session session = getSession();

    try {
      Query q = SQL.getProductsBy7P(session, shopId, productDTO);

      List<Product> productList = (List<Product>) q.list();

      if (CollectionUtils.isEmpty(productList)) {
        return true;
      }
      return false;
    } finally {
      release(session);
    }
  }

  public Kind getEnabledKindDTOById(Long shopId, Long id) {
    Session session = getSession();

    try {
      Query q = SQL.getEnabledKindDTOById(session, shopId, id);

      List<Kind> kindList = (List<Kind>) q.list();

      if (CollectionUtils.isEmpty(kindList)) {
        return null;
      }

      return kindList.get(0);

    } finally {
      release(session);
    }
  }

  public List<ProductLocalInfo> getProductLocalInfoByCategoryId(Long shopId, Long categoryId) {
    Session session = getSession();

    try {
      Query q = SQL.getProductLocalInfoByCategoryId(session, shopId, categoryId);

      return (List<ProductLocalInfo>) q.list();
    } finally {
      release(session);
    }
  }

  public List<ProductMapping> getProductMapping(Long customerShopId, Long supplierShopId, Long... productIds) {
    Session session = getSession();
    try {
      Query query = SQL.getProductMapping(session, customerShopId, supplierShopId, productIds);
      return (List<ProductMapping>) query.list();
    } finally {
      release(session);
    }
  }

  public List<ProductCategory> getProductCategoryByParentId(Long shopId, Long parentId, Pager pager) {
    Session session = getSession();
    try {
      Query query = SQL.getProductCategoryByParentId(session, shopId, parentId, pager);
      return (List<ProductCategory>) query.list();
    } finally {
      release(session);
    }
  }

  public List<ProductCategory> getProductCategoryByNameParentId(Long shopId, String name, Long parentId) {
    Session session = getSession();
    try {
      Query query = SQL.getProductCategoryByNameParentId(session, shopId, name, parentId);
      return (List<ProductCategory>) query.list();
    } finally {
      release(session);
    }
  }

  public List<ProductCategory> getProductCategoryFuzzyName(Long shopId, String name, Pager pager) {
    Session session = getSession();
    try {
      Query query = SQL.getProductCategoryFuzzyName(session, shopId, name, pager);
      return (List<ProductCategory>) query.list();
    } finally {
      release(session);
    }
  }

  public List<ProductCategory> getSecondCategoryByParentId(Long shopId, Long parentId) {
    Session session = getSession();
    try {
      Query query = SQL.getSecondCategoryByParentId(session, shopId, parentId);
      return (List<ProductCategory>) query.list();
    } finally {
      release(session);
    }
  }

  public List<Object[]> countProductByNormalProductId(Long...normalProductId ) {
    Session session = getSession();

    try {
      Query q = SQL.countProductByNormalProductId(session, normalProductId);
      return (List<Object[]>) q.list();
    } finally {
      release(session);
    }
  }

  public List<NormalProduct> getNormalProduct(ProductSearchCondition searchCondition) {
    Session session = getSession();

    try {
      Query q = SQL.getNormalProduct(session, searchCondition);
      return (List<NormalProduct>) q.list();
    } finally {
      release(session);
    }
  }

  public int countNormalProductDTO(ProductSearchCondition searchCondition) {
    Session session = getSession();

    try {
      Query q = SQL.countNormalProductDTO(session, searchCondition);
      Object o = q.uniqueResult();
      return o == null ? 1 : Integer.valueOf(o.toString());
    } finally {
      release(session);
    }
  }

  public List<Product> getProductByNormalProductId(Long id) {
    Session session = getSession();

    try {
      Query q = SQL.getProductByNormalProductId(session, id);
      return (List<Product>) q.list();
    } finally {
      release(session);
    }
  }

  public NormalProduct getNormalProductByCommodityCode(String commodityCode) {
    Session session = getSession();

    try {
      Query q = SQL.getNormalProductByCommodityCode(session, commodityCode);

      List<NormalProduct> normalProductList = (List<NormalProduct>) q.list();

      if (CollectionUtils.isEmpty(normalProductList)) {
        return null;
      }

      return normalProductList.get(0);

    } finally {
      release(session);
    }
  }

  public NormalProduct getNormalProductBySixProperty(NormalProductDTO normalProductDTO) {
    Session session = getSession();

    try {
      Query q = SQL.getNormalProductBySixProperty(session, normalProductDTO);
      List<NormalProduct> normalProductList = (List<NormalProduct>) q.list();

      if (CollectionUtils.isEmpty(normalProductList)) {
        return null;
      }

      return normalProductList.get(0);
    } finally {
      release(session);
    }
  }

  public List<ProductCategory> getProductCategoryByShopId(Long shopId) {
    Session session = getSession();
    try {
      Query query = SQL.getProductCategoryByShopId(session, shopId);
      return (List<ProductCategory>) query.list();
    } finally {
      release(session);
    }
  }

  public List<Long> getProductCategoryIdsByShopId(Long shopId, int start, int pageSize) {
    Session session = getSession();
    try {
      Query query = SQL.getProductCategoryIdsByShopId(session, shopId, start, pageSize);
      return (List<Long>) query.list();
    } finally {
      release(session);
    }
  }

  public List<ProductCategory> getProductCategoryByName(Long shopId, String name) {
    Session session = getSession();
    try {
      Query query = SQL.getProductCategoryByName(session, shopId, name);
      return (List<ProductCategory>) query.list();
    } finally {
      release(session);
    }
  }

  public List<ProductCategory> getFirstProductCategory() {
    Session session = getSession();

    try {
      Query q = SQL.getFirstProductCategory(session);

      return (List<ProductCategory>) q.list();
    } finally {
      release(session);
    }
  }

  public List<ProductCategory> getAllProductCategory() {
    Session session = getSession();
    try {
      Query q = SQL.getAllProductCategory(session);
      return (List<ProductCategory>) q.list();
    } finally {
      release(session);
    }
  }

  public List<ProductCategory> getThirdCategoryByCondition(ProductSearchCondition searchCondition) {
    Session session = getSession();

    try {
      Query q = SQL.getThirdCategoryByCondition(session, searchCondition);
      if (null == q) {
        return null;
      }
      return (List<ProductCategory>) q.list();
    } finally {
      release(session);
    }
  }

  public List<String> getNormalProductByCondition(ProductSearchCondition searchCondition) {
    Session session = getSession();

    try {
      Query q = SQL.getNormalProductByCondition(session, searchCondition);

      return (List<String>) q.list();

    } finally {
      release(session);
    }
  }

  public List<ProductCategory> getCategoryListByIds(List<Long> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return null;
    }
    Session session = getSession();

    try {
      Query q = SQL.getCategoryListByIds(session, ids);

      return (List<ProductCategory>) q.list();
    } finally {
      release(session);
    }
  }

  public int countShopProductsByNormalProductId(Long id) {
    if (null == id) {
      return 0;
    }

    Session session = getSession();

    try {
      Query q = SQL.countShopProductsByNormalProductId(session, id);
      Object o = q.uniqueResult();
      return o == null ? 0 : Integer.valueOf(o.toString());
    } finally {
      release(session);
    }
  }

  public List<Product> getShopProductsByNormalProductId(Long id, int page, int limit) {
    if (null == id) {
      return null;
    }
    Session session = getSession();
    try {
      Query q = SQL.getShopProductsByNormalProductId(session, id, page, limit);
      return (List<Product>) q.list();
    } finally {
      release(session);
    }
  }

  public List<ProductLocalInfo> getProductLocalInfoByProductIds(List<Long> productIds) {
    if (CollectionUtils.isEmpty(productIds)) {
      return null;
    }

    Session session = getSession();

    try {
      Query q = SQL.getProductLocalInfoByProductIds(session, productIds);
      return (List<ProductLocalInfo>) q.list();
    } finally {
      release(session);
    }
  }

  public List<ProductLocalInfo> getProductLocalInfoByProductIds(Long shopId, Set<Long> productIds) {
    if (shopId == null || CollectionUtils.isEmpty(productIds)) {
      return new ArrayList<ProductLocalInfo>();
    }
    Session session = getSession();
    try {
      Query q = SQL.getProductLocalInfoByProductIds(session, shopId, productIds);
      return (List<ProductLocalInfo>) q.list();
    } finally {
      release(session);
    }
  }

  public List<ProductLocalInfo> getProductLocalInfoByLocalInfoIds(Long shopId, Set<Long> productIds) {
    long begin = System.currentTimeMillis();
    LOG.debug("AOP_SQL start:ProductWriter:getProductLocalInfoByProductIds");
    if (shopId == null || CollectionUtils.isEmpty(productIds)) {
      return new ArrayList<ProductLocalInfo>();
    }
    Session session = getSession();
    try {
      Query q = SQL.getProductLocalInfoByLocalInfoIds(session, shopId, productIds);
      return q.list();
    } finally {
      LOG.debug("AOP_SQL end:ProductWriter:getProductLocalInfoByProductIds 用时：{}ms", System.currentTimeMillis() - begin);
      release(session);
    }
  }

  public int countShopProductsByCondition(ProductSearchCondition searchCondition) {
    Session session = getSession();

    try {
      Query q = SQL.countShopProductsByCondition(session, searchCondition);
      Object o = q.uniqueResult();
      return o == null ? 0 : Integer.valueOf(o.toString());
    } finally {
      release(session);
    }
  }

  public List<Product> getShopProductsByCondition(ProductSearchCondition searchCondition) {
    Session session = getSession();

    try {
      Query q = SQL.getShopProductsByCondition(session, searchCondition);

      return (List<Product>) q.list();

    } finally {
      release(session);
    }
  }

  public List<NormalBrand> getNormalBrandByKeyWord(String keyWord) {
    Session session = getSession();

    try {
      Query q = SQL.getNormalBrandByKeyWord(session, keyWord);
      return (List<NormalBrand>) q.list();
    } finally {
      release(session);
    }
  }

  public List<NormalModel> getNormalModelByBrandId(Long brandId) {
    Session session = getSession();

    try {
      Query q = SQL.getNormalModelByBrandId(session, brandId);
      return (List<NormalModel>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Product> getProductByIds(List<Long> ids) {
    Session session = getSession();

    try {
      Query q = SQL.getProductByIds(session, ids);
      return (List<Product>) q.list();
    } finally {
      release(session);
    }
  }

  public List<NormalProduct> getNormalProductDTOByIds(List<Long> ids) {
    Session session = getSession();

    try {
      Query q = SQL.getNormalProductDTOByIds(session, ids);
      return (List<NormalProduct>) q.list();
    } finally {
      release(session);
    }
  }

  public List<ProductLocalInfo> getProductLocalInfoByNormalProductId(Long normalProductId) {
    Session session = getSession();
    try {
      Query query = SQL.getProductLocalInfoByNormalProductId(session, normalProductId);
      return (List<ProductLocalInfo>) query.list();
    } finally {
      release(session);
    }
  }

  public List<NormalProduct> getAllNormalProducts() {
    Session session = getSession();
    try {
      Query query = SQL.getAllNormalProducts(session);
      return (List<NormalProduct>) query.list();
    } finally {
      release(session);
    }
  }

  public List<Model> getModelHasBrandId() {
    Session session = getSession();

    try {
      Query query = SQL.getModelHasBrandId(session);
      return (List<Model>) query.list();
    } finally {
      release(session);
    }
  }

  public List<Brand> getBrandList(Long... id) {
    Session session = getSession();

    try {
      Query query = SQL.getBrandList(session, id);
      return (List<Brand>) query.list();
    } finally {
      release(session);
    }
  }

  public List<NormalProduct> getNormalProductDTOByCategoryId(Long categoryId) {
    Session session = getSession();

    try {
      Query q = SQL.getNormalProductDTOByCategoryId(session, categoryId);

      return (List<NormalProduct>) q.list();
    } finally {
      release(session);
    }
  }

  public List<ProductLocalInfo> getAllProductLocalInfoWithTradePriceAndNotInSales(Long shopId) {
    Session session = getSession();
    try {
      Query query = SQL.getAllProductLocalInfoWithTradePriceAndNotInSales(session, shopId);
      List<ProductLocalInfo> productList = (List<ProductLocalInfo>) query.list();
      return productList;
    } finally {
      release(session);
    }
  }

  public int countAllProductLocalInfoWithNotTradePriceAndNotInSales(Long shopId) {
    Session session = getSession();
    try {
      Query query = SQL.countAllProductLocalInfoWithNotTradePriceAndNotInSales(session, shopId);
      return Integer.valueOf(query.uniqueResult().toString());
    } finally {
      release(session);
    }
  }

  public List<ProductLocalInfo> getAllProductLocalInfInSales(Long shopId) {
    Session session = getSession();
    try {
      Query query = SQL.getAllProductLocalInfInSales(session, shopId);
      List<ProductLocalInfo> productList = (List<ProductLocalInfo>) query.list();
      return productList;
    } finally {
      release(session);
    }
  }

  public List<Object[]> getPromotionsByProductLocalInfoIds(Long shopId, Long... productLocalInfoId) {
    Session session = getSession();
    try {
      Query q = SQL.getPromotionsByProductLocalInfoIds(session, shopId, productLocalInfoId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<Object[]> getPromotionsByProductIds(Set<Long> shopIdSet,Long... productIds) {
    Session session = getSession();
    try {
      Query q = SQL.getPromotionsByProductIds(session,shopIdSet,productIds);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<Promotions> getPromotionsByPromotionsId(Long shopId, Long... promotionsId) {
    Session session = getSession();
    try {
      Query q = SQL.getPromotionsByPromotionsId(session, shopId, promotionsId);
      return (List<Promotions>) q.list();
    } finally {
      release(session);
    }
  }

  public List<PromotionsRule> getPromotionsRulesByProductLocalInfoIds(Long shopId, Long... productLocalInfoId) {
    Session session = getSession();
    try {
      Query q = SQL.getPromotionsRulesByProductLocalInfoIds(session, shopId, productLocalInfoId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<Promotions> getPromotionsByRange(Long shopId, PromotionsEnum.PromotionsRanges range) {
    Session session = getSession();
    try {
      Query q = SQL.getPromotionsByRange(session, shopId, range);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<PromotionsRule> getPromotionsRuleByPromotionsId(Long promotionsId) {
    Session session = getSession();
    try {
      Query q = SQL.getPromotionsRuleByPromotionsId(session, promotionsId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<Promotions> getPromotionsById(Long shopId, Long... ids) {
    Session session = getSession();
    try {
      Query q = SQL.getPromotionsById(session, shopId, ids);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<Promotions> getPromotionsByPromotionsType(Long shopId, PromotionsEnum.PromotionsTypes type) {
    Session session = getSession();
    try {
      Query q = SQL.getPromotionsByPromotionsType(session, shopId, type);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<Promotions> getCurrentPromotions(Long shopId) {
    Session session = getSession();
    try {
      Query q = SQL.getCurrentPromotions(session, shopId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public void cancelPromotionsByProductLocalInfoIds(Long shopId, Long... productLocalInfoId) {
    Session session = getSession();
    try {
      Query q = SQL.cancelPromotionsByProductLocalInfoIds(session, shopId, productLocalInfoId);
      q.executeUpdate();
    } finally {
      release(session);
    }
  }


  public void cancelPromotionsByPromotionsId(Long shopId, Long promotionsId) {
    Session session = getSession();
    try {
      Query q = SQL.cancelPromotionsByPromotionsId(session, shopId, promotionsId);
      q.executeUpdate();
    } finally {
      release(session);
    }
  }

  public List<PromotionsProduct> getPromotionsProductByPromotionsId(Long shopId, Long... promotionsIds) {
    Session session = getSession();
    try {
      Query q = SQL.getPromotionsProductByPromotionsId(session, shopId, promotionsIds);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<PromotionsProduct> getPromotionsProductDTOByProductIds(Long shopId, Long... productIds) {
    Session session = getSession();
    try {
      Query q = SQL.getPromotionsProductDTOByProductIds(session, shopId, productIds);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<PromotionsProduct> getPromotionsProductDTO(PromotionSearchCondition condition) {
    Session session = getSession();
    try {
      Query q = SQL.getPromotionsProductDTO(session, condition);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<PromotionsProduct> getPromotionsProductDTO(Long shopId, Long productId, Long startTime, Long endTime, Long... promotionsIdList) {
    Session session = getSession();
    try {
      Query q = SQL.getPromotionsProductDTO(session, shopId, productId, startTime, endTime, promotionsIdList);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<PromotionsArea> getPromotionsAreaByPromotionsIds(Long... promotionsIds) {
    Session session = getSession();
    try {
      Query q = SQL.getPromotionsAreaByPromotionsIds(session, promotionsIds);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<PromotionsProduct> getPromotionsProduct(Long shopId, Long promotionsId, Long... productIds) {
    Session session = getSession();
    try {
      Query q = SQL.getPromotionsProduct(session, shopId, promotionsId, productIds);
      return q.list();
    } finally {
      release(session);
    }
  }

  //获取上架的并且没有个性促销的商品
  public List<ProductLocalInfo> getProductInSalesAndNoPromotions(Long shopId) {
    Session session = getSession();
    try {
      Query q = SQL.getProductInSalesAndNoPromotions(session, shopId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public int countPromotions(PromotionIndex condition) {
    Session session = getSession();
    try {
      Query q = SQL.countPromotions(session, condition);
      return NumberUtil.intValue(q.uniqueResult().toString(), 0);
    } finally {
      release(session);
    }
  }

  public List<Promotions> getPromotions(PromotionIndex condition) {
    Session session = getSession();
    try {
      Query q = SQL.getPromotions(session, condition);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<PromotionsRule> getPromotionsRuleByPromotionsIds(Long shopId, Long[] promotionsIds) {
    Session session = getSession();
    try {
      Query q = SQL.getPromotionsRuleByPromotionsIds(session, shopId, promotionsIds);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<PromotionsRuleMJS> getPromotionsRuleMJSByRuleIds(Long shopId, Long[] ruleIds) {
    Session session = getSession();
    try {
      Query q = SQL.getPromotionsRuleMJSByRuleIds(session, shopId, ruleIds);
      return q.list();
    } finally {
      release(session);
    }
  }

  public int countProductInSalesByProductLocalInfoId(Long shopId, Long... productLocalInfoIds) {
    Session session = getSession();
    try {
      Query q = SQL.countProductInSalesByProductLocalInfoId(session, shopId, productLocalInfoIds);
      return Integer.valueOf(q.uniqueResult().toString());
    } finally {
      release(session);
    }
  }

  public PromotionsProduct getPromotionsProductByPromotionsIdAndProductLocalInfoId(Long shopId, Long promotionsId, Long productLocalInfoId) {
    Session session = getSession();
    try {
      Query q = SQL.getPromotionsProductByPromotionsIdAndProductLocalInfoId(session, shopId, promotionsId, productLocalInfoId);
      return (PromotionsProduct) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public int countProductMappingByShopIds(Long customerShopId, Long supplierShopId, List<ProductStatus> productStatus,
                                          List<ProductStatus.TradeStatus> tradeStatus) {
    Session session = getSession();
    try {
      Query q = SQL.countProductMappingByShopIds(session, customerShopId, supplierShopId, productStatus, tradeStatus);
      return Integer.valueOf(q.uniqueResult().toString());
    } finally {
      release(session);
    }
  }

  public List<ProductMapping> getProductMappingByShopId(Long customerShopId, Long supplierShopId
      , List<ProductStatus> productStatus, List<ProductStatus.TradeStatus> tradeStatus, Pager pager) {
    Session session = getSession();
    try {
      Query q = SQL.getProductMappingByShopIds(session, customerShopId, supplierShopId, productStatus, tradeStatus, pager);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<Product> getProductByIds(Long shopId, Long[] productIds) {
    Session session = getSession();
    try {
      Query q = SQL.getProductByIds(session, shopId, productIds);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<Product> getProduct(Long shopId) {
    Session session = getSession();
    try {
      Query q = SQL.getProduct(session, shopId);
      return (List<Product>) q.list();
    } finally {
      release(session);
    }
  }

 public List<Object[]> getProductInfo(ProductSearchCondition conditionDTO)
  {
    Session session = getSession();
    try{
      Query q = SQL.getProductInfo(session,conditionDTO);
      return q.list();
    } finally{
      release(session);
    }
  }

  public List<String> getAllCommodityCode(Long shopId){
    Session session = getSession();
    try {
      Query q = SQL.getAllCommodityCode(session, shopId);
      return (List<String>) q.list();
    } finally {
      release(session);
    }
  }

  public int countProductNotInSales(Long shopId) {
    Session session = getSession();
    try {
      Query q = SQL.countProductInOffSales(session, shopId, ProductStatus.NotInSales);
      return NumberUtil.intValue(q.uniqueResult().toString());
    } finally {
      release(session);
    }
  }

  public int countProductInSales(Long shopId) {
    Session session = getSession();
    try {
      Query q = SQL.countProductInOffSales(session, shopId, ProductStatus.InSales);
      return NumberUtil.intValue(q.uniqueResult().toString());
    } finally {
      release(session);
    }
  }

  public int countProductByPromotions(Long shopId, Long[] productIds) {
    Session session = getSession();
    try {
      Query q = SQL.countProductByPromotions(session, shopId, productIds);
      return NumberUtil.intValue(q.uniqueResult().toString());
    } finally {
      release(session);
    }
  }

  public int countAllStockProduct(Long shopId) {
    Session session = getSession();
    try {
      Query q = SQL.countAllStockProduct(session, shopId);
      return NumberUtil.intValue(q.uniqueResult().toString());
    } finally {
      release(session);
    }
  }

  public List<ProductSupplier> getProductSupplierByShopId(Long shopId, Pager pager) {
    Session session = getSession();
    try {
      Query q = SQL.getProductSupplierByShopId(session, shopId, pager);
      return q.list();
    } finally {
      release(session);
    }
  }

  public Long countProductSupplierByShopId(Long shopId) {
    Session session = getSession();
    try {
      Query q = SQL.countProductSupplierByShopId(session, shopId);
      return (Long) q.uniqueResult();
    } finally {
      release(session);
    }
  }

  public Long getBcgogoRecommendProductIds(Long normalProductId, Double comparePrice, Long... shopIds) {
    Session session = getSession();
    try {
      Query q = SQL.getBcgogoRecommendProductIds(session, normalProductId, comparePrice, shopIds);
      return (Long) CollectionUtil.getFirst(q.list());
    } finally {
      release(session);
    }
  }

  public boolean isNormalProductIdRelevanceInShop(long normalProductId, long shopId, long shopProductId) {
    Session session = getSession();
    try {
      Query q = SQL.countProductByNormalProductIdAndShopId(session, normalProductId, shopId, shopProductId);
      Object o = q.uniqueResult();
      return Integer.valueOf(o.toString()) > 0;
    } finally {
      release(session);
    }
  }

  public ProductDTO getProductByNormalProductId(Long shopId, Long normalProductId) {
    Session session = getSession();
    try {
      Query q = SQL.getProductByNormalProductId(session, shopId, normalProductId);
      Product p = (Product) CollectionUtil.getFirst(q.list());
      if (p != null) return p.toDTO();
      return null;
    } finally {
      release(session);
    }
  }

  public List<ShopRegisterProduct> getShopRegisterProductList(Long shopId) {

    Session session = getSession();
    try {
      Query q = SQL.getShopRegisterProductList(session, shopId);
      return (List<ShopRegisterProduct>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Long> getCategoryParentIds(List<Long> ids) {
    Session session = getSession();
    try {
      Query q = SQL.getCategoryParentIds(session, ids);
      return (List<Long>) q.list();
    } finally {
      release(session);
    }
  }

  public List<PromotionOrderRecord> getPromotionOrderRecord(Long orderId) {
    Session session = getSession();
    try {
      Query query = SQL.getPromotionOrderRecord(session, orderId);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<PromotionOrderRecord> getPromotionOrderRecord(Long supplierShopId, Long promotionsId, Long supplierProductId) {
    Session session = getSession();
    try {
      Query query = SQL.getPromotionOrderRecord(session, supplierShopId, promotionsId, supplierProductId);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<Long> getInSalingProductForSupplyDemand(Long shopId,int startPageNo,int maxSize,boolean pEndTimFlag) {
    Session session = this.getSession();
    try {
      Query query=SQL.getInSalingProductForSupplyDemand(session,shopId,startPageNo,maxSize,pEndTimFlag);
      return query.list();
    } finally {
      release(session);
    }
  }

   public List<Long> getInSalingProductWithOutPromotion(Long shopId,int startPageNo,int maxSize) {
    Session session = this.getSession();
    try {
      Query query=SQL.getInSalingProductWithOutPromotion(session,shopId,startPageNo,maxSize);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<ProductCategory> getProductCategoryByIds(Set<Long> ids) {
    Session session = getSession();
    try {
      Query query = SQL.getProductCategoryByIds(session, ids);
      return query.list();
    } finally {
      release(session);
    }
  }


  public List<ShopRegisterProduct> getShopRegisterProductListByShopId(Long shopId) {
    Session session = getSession();
    try {
      Query query = SQL.getShopRegisterProductListByShopId(session, shopId);
      return query.list();
    } finally {
      release(session);
    }
  }

  //获取无限期的促销
  public List<Promotions> getUnlimitedPromotions(Long shopId) {
    Session session = getSession();
    try {
      Query query = SQL.getUnlimitedPromotions(session, shopId);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<Promotions> getPromotionsByRange(Long shopId, Long startTime, Long endTime) {
    Session session = getSession();
    try {
      Query query = SQL.getPromotionsByRange(session, shopId, startTime, endTime);
      return query.list();
    } finally {
      release(session);
    }
  }

  public double getPromotionOrderRecordUsedAmount(Long productId, Long promotionsId, Long shopId, Long orderId) {
    Session session = getSession();
    try {
      Query query = SQL.getPromotionOrderRecordUsedAmount(session, productId, promotionsId, shopId, orderId);
      return NumberUtil.doubleVal(query.uniqueResult());
    } finally {
      release(session);
    }
  }

  public List<ProductCategory> getThirdProductCategoryByName(Long shopId, String name) {
    Session session = getSession();
    try {
      Query query = SQL.getThirdProductCategoryByName(session, shopId, name);
      return (List<ProductCategory>) query.list();
    } finally {
      release(session);
    }
  }


  public List<NormalProductVehicleBrandModel> getNormalProductVehicleBrandModelByNormalProductId(Long... normalProductId) {
    Session session = getSession();
    try {
      Query query = SQL.getNormalProductVehicleBrandModelByNormalProductId(session, normalProductId);
      return (List<NormalProductVehicleBrandModel>) query.list();
    } finally {
      release(session);
    }
  }
  public List<ShopVehicleBrandModel> getShopVehicleBrandModelByShopId(Long shopId) {
    Session session = getSession();
    try {
      Query query = SQL.getShopVehicleBrandModelByShopId(session, shopId);
      return (List<ShopVehicleBrandModel>) query.list();
    } finally {
      release(session);
    }
  }

  public List<StandardVehicleBrand> getStandardVehicleBrandByName(String name, String firstLetter) {
    Session session = getSession();
    try {
      Query query = SQL.getStandardVehicleBrandByName(session, name, firstLetter);
      return (List<StandardVehicleBrand>) query.list();
    } finally {
      release(session);
    }
  }

  public List<StandardVehicleBrand> getStandardVehicleBrandSuggestionByName(String name, String firstLetter) {
    Session session = getSession();
    try {
      Query query = SQL.getStandardVehicleBrandSuggestionByName(session, name, firstLetter);
      return (List<StandardVehicleBrand>) query.list();
    } finally {
      release(session);
    }
  }

  public List<StandardVehicleModel> getStandardVehicleModelByName(Long standardVehicleBrandId, String name) {
    Session session = getSession();
    try {
      Query query = SQL.getStandardVehicleModelByName(session, standardVehicleBrandId, name);
      return (List<StandardVehicleModel>) query.list();
    } finally {
      release(session);
    }
  }

  public List<PromotionOrderRecord> getPromotionOrderRecordsByCondition(PromotionOrderRecordQuery query) {
    Session session = getSession();
    try {
      Query hqlQuery = SQL.getPromotionOrderRecordsByCondition(session, query);
      return (List<PromotionOrderRecord>) hqlQuery.list();
    } finally {
      release(session);
    }
  }

  public List<ProductCategoryRelation> getProductCategoryRelations(Long shopId, Long... productLocalInfoIds) {
    Session session = getSession();
    try {
      Query hqlQuery = SQL.getProductCategoryRelation(session, shopId, productLocalInfoIds);
      return (List<ProductCategoryRelation>) hqlQuery.list();
    } finally {
      release(session);
    }
  }

  public List<PromotionOrderRecord> getPromotionOrderRecordsById(Long... id) {
    Session session = getSession();
    try {
      Query hqlQuery = SQL.getPromotionOrderRecordsById(session, id);
      return (List<PromotionOrderRecord>) hqlQuery.list();
    } finally {
      release(session);
    }
  }


  public List<StandardVehicleModel> getStandardVehicleModelSuggestionByName(Long standardVehicleBrandId, String name) {
    Session session = getSession();
    try {
      Query query = SQL.getStandardVehicleModelSuggestionByName(session, standardVehicleBrandId, name);
      return (List<StandardVehicleModel>) query.list();
    } finally {
      release(session);
    }
  }

  //获取通用字典，通用字典只有一个
  public Dictionary getCommonDictionary() {
    Session session = getSession();
    try {
      Query query = SQL.getCommonDictionary(session);
      List<Dictionary> dictionaries = query.list();
      return CollectionUtil.getFirst(dictionaries);
    } finally {
      release(session);
    }
  }

  //一个车型只有一个字典
  public VehicleDictionary getVehicleDictionaryByVehicleModelId(Long vehicleModelId) {
    Session session = getSession();
    try {
      Query query = SQL.getVehicleDictionaryByVehicleModelId(session, vehicleModelId);
      List<VehicleDictionary> vehicleDictionaries = query.list();
      return CollectionUtil.getFirst(vehicleDictionaries);
    } finally {
      release(session);
    }
  }

  public List<DictionaryFaultInfo> getDictionaryFaultInfo(Long dictionaryId) {
    Session session = this.getSession();
    try {
      Query q = SQL.getDictionaryFaultInfo(session, dictionaryId);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<StandardVehicleBrand> getStandardVehicleBrandByIds(Set<Long> ids) {
    Session session = this.getSession();
    try {
      Query q = SQL.getStandardVehicleBrandByIds(session, ids);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<StandardVehicleBrand> getStandardVehicleBrandByNames(Set<String> names) {
    Session session = this.getSession();
    try {
      Query q = SQL.getNameStandardVehicleBrandByNames(session, names);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<StandardVehicleModel> getStandardVehicleModelByNames(Set<String> names) {
    Session session = this.getSession();
    try {
      Query q = SQL.getNameStandardVehicleModelByNames(session, names);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<StandardVehicleModel> getStandardVehicleModelByIds(Set<Long> ids) {
    Session session = this.getSession();
    try {
      Query q = SQL.getStandardVehicleModelByIds(session, ids);
      return q.list();
    } finally {
      release(session);
    }
  }


  public List<BcgogoProduct> getBcgogoProductDTOByPaymentType(PaymentType paymentType) {
    Session session = getSession();
    try {
      Query q = SQL.getBcgogoProductDTOByPaymentType(session,paymentType);
      return (List<BcgogoProduct>)q.list();
    } finally {
      release(session);
    }
  }

  public List<BcgogoProductProperty> getBcgogoProductPropertyByProductId(Long... productId) {
    Session session = getSession();
    try {
      Query q = SQL.getBcgogoProductPropertyByProductId(session, productId);
      return (List<BcgogoProductProperty>)q.list();
    } finally {
      release(session);
    }
  }

  public List<DictionaryFaultInfo> getCommonDictionaryFaultInfo(Set<String> codes) {
    List<DictionaryFaultInfo> dictionaryFaultInfoList = new ArrayList<DictionaryFaultInfo>();
    if (CollectionUtils.isEmpty(codes)) {
      return dictionaryFaultInfoList;
    }
    Session session = this.getSession();
    try {
      Query q = SQL.getCommonDictionaryFaultInfo(session, codes);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<DictionaryFaultInfo> getDictionaryFaultInfoByBrandIdAndCodes(Long brandId, Set<String> codes) {
    List<DictionaryFaultInfo> dictionaryFaultInfoList = new ArrayList<DictionaryFaultInfo>();
    if (brandId == null || CollectionUtils.isEmpty(codes)) {
      return dictionaryFaultInfoList;
    }
    Session session = this.getSession();
    try {
      Query q = SQL.getDictionaryFaultInfoByBrandIdAndCodes(session, brandId, codes);
      return q.list();
    } finally {
      release(session);
    }
  }

  public List<NormalProduct> getAllNormalProductByCondition(ProductSearchCondition searchCondition) {
    Session session = getSession();

    try {
      Query q = SQL.getAllNormalProductByCondition(session, searchCondition);
      return (List<NormalProduct>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Long> getCommodityAdProductIds(Pager pager,Long... shopIds) {
    Session session = getSession();
    try {
      Query q = SQL.getCommodityAdProductIds(session,pager,shopIds);
      return (List<Long>) q.list();
    } finally {
      release(session);
    }
  }

  public List<Long> getAdShopIdByShopArea(Long province,Long city,Long region) {
    Session session = getSession();
    try {
      Query q = SQL.getAdShopIdByShopArea(session,province,city,region);
      return (List<Long>) q.list();
    } finally {
      release(session);
    }
  }

  public int countCommodityAdProduct(Long... adShopIds) {
    Session session = getSession();
    try {
      Query q = SQL.countCommodityAdProduct(session,adShopIds);
      return NumberUtil.intValue(q.uniqueResult());
    } finally {
      release(session);
    }
  }

  public List<Long> getLackAutoPreBuyProductId(Long shopId,Long startDate,int autoPreBuyAmount) {
    Session session = getSession();
    try {
      Query query = SQL.getLackAutoPreBuyProductId(session,shopId,startDate,autoPreBuyAmount);
      return query.list();
    } finally {
      release(session);
    }
  }

  public DictionaryFaultInfo getDictionaryFaultInfoDTOByFaultCode(String faultCode) {
    Session session = getSession();
    try {
      Query query = SQL.getDictionaryFaultInfoDTOByFaultCode(session,faultCode);
      return (DictionaryFaultInfo)query.uniqueResult();
    } finally {
      release(session);
    }
  }

}
