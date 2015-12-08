package com.bcgogo.productRead.service;

import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.config.VehicleSelectBrandModel;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.model.*;
import com.bcgogo.product.standardVehicleBrandModel.ShopVehicleBrandModelDTO;
import com.bcgogo.product.standardVehicleBrandModel.StandardVehicleBrandDTO;
import com.bcgogo.product.standardVehicleBrandModel.StandardVehicleModelDTO;
import com.bcgogo.service.ServiceManager;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bcgogo.productRead.model.ProductReadDaoManager;
import com.bcgogo.productRead.model.ProductReadReader;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-6-17
 * Time: 下午1:18
 * To change this template use File | Settings | File Templates.
 */
@Service
public class ProductReadService implements IProductReadService {
  private static final Logger LOG = LoggerFactory.getLogger(ProductReadService.class);

  @Autowired
  private ProductReadDaoManager productReadDaoManager;

  /**
   * 获取店铺一定数量的产品信息
   *
   * @param shopId
   * @param start
   * @param rows
   * @return
   */
  @Override
  public List<ProductDTO> getProductDTOList(Long shopId, int start, int rows){
    ProductReadReader productReader = productReadDaoManager.getReader();

    List<ProductDTO> productDTOList = new ArrayList<ProductDTO>();
    List<Product> productList = productReader.getProducts(shopId, start, rows);
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
  }

  @Override
  public List<ProductDTO> getProductDTOListByProductLocalInfoIds(Long shopId, Set<Long> productLocalInfoIds) {
    List<ProductDTO> productDTOList = new ArrayList<ProductDTO>();
    ProductReadReader productReader = productReadDaoManager.getReader();
    List<Object[]> list = productReader.getProductDTListByProductLocalInfoIds(shopId, productLocalInfoIds);

    if (CollectionUtils.isNotEmpty(list)) {
      for (Object[] objects : list) {
        if (objects != null && objects.length == 2) {
          Product product = (Product) objects[0];
          ProductLocalInfo productLocalInfo = (ProductLocalInfo) objects[1];
          ProductDTO productDTO = product.toDTO();
          productDTO.setProductLocalInfoDTO(productLocalInfo.toDTO());
          productDTOList.add(productDTO);
        }
      }
    }
    return productDTOList;
  }


  /**
   * 获取店铺注册的商品 商品信息拿的是最新的商品信息
   * @param shopId
   * @return
   */
  @Override
  public List<ProductDTO> getShopRegisterProductList(Long shopId) {
    ProductReadReader productReader = productReadDaoManager.getReader();
    List<ProductDTO> productDTOList = new ArrayList<ProductDTO>();

    List<ShopRegisterProduct> shopRegisterProductList = productReader.getShopRegisterProductList(shopId);
    if (CollectionUtils.isEmpty(shopRegisterProductList)) {
      return productDTOList;
    }

    Set<Long> productIdSet = new HashSet<Long>();
    for (ShopRegisterProduct shopRegisterProduct : shopRegisterProductList) {
      productIdSet.add(shopRegisterProduct.getProductLocalInfoId());
    }

    productDTOList.addAll(this.getProductDTOListByProductLocalInfoIds(shopId, productIdSet));

    return productDTOList;
  }
  /**
   * 根据id获取商品分类（经营范围）
   * @param productCategoryIds
   * @return
   */
  @Override
  public List<ProductCategory> getCategoryListByIds(List<Long> productCategoryIds) {

    ProductReadReader productReader = productReadDaoManager.getReader();
    List<ProductCategory> productCategories = productReader.getCategoryListByIds(productCategoryIds);
    return productCategories;
  }


  /**
   * 根据店铺获取该店铺下的标准车辆品牌、车型
   *
   * @param shopId
   * @return
   */
  private List<ShopVehicleBrandModelDTO> getShopVehicleBrandModelByShopId(Long shopId) {
    List<ShopVehicleBrandModelDTO> shopVehicleBrandModelDTOList = new ArrayList<ShopVehicleBrandModelDTO>();

    if (shopId == null) {
      return shopVehicleBrandModelDTOList;
    }

    //   * 如果注册时选择的是全部车型 这把标准车辆品牌、车型返回回去 此时shopVehicleBrandModelDTO没有id
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    ShopDTO shopDTO = configService.getShopById(shopId);
    if (shopDTO.getShopSelectBrandModel() == VehicleSelectBrandModel.ALL_MODEL) {
      shopVehicleBrandModelDTOList.addAll(this.getAllStandardVehicleBrandModel());

      if (CollectionUtils.isNotEmpty(shopVehicleBrandModelDTOList)) {
        for (ShopVehicleBrandModelDTO vehicleBrandModelDTO : shopVehicleBrandModelDTOList) {
          vehicleBrandModelDTO.setShopId(shopId);
        }
      }
      return shopVehicleBrandModelDTOList;
    }

    ProductReadReader productReader = productReadDaoManager.getReader();
    List<ShopVehicleBrandModel> shopVehicleBrandModelList = productReader.getShopVehicleBrandModelByShopId(shopId);

    if (CollectionUtils.isEmpty(shopVehicleBrandModelList)) {
      return shopVehicleBrandModelDTOList;
    }

    for (ShopVehicleBrandModel shopVehicleBrandModel : shopVehicleBrandModelList) {
      shopVehicleBrandModelDTOList.add(shopVehicleBrandModel.toDTO());
    }

    return shopVehicleBrandModelDTOList;
  }

  @Override
  public Map<String, String> joinShopVehicleBrandModelStr(Long shopId) {
    List<ShopVehicleBrandModelDTO> shopVehicleBrandModelDTOList = getShopVehicleBrandModelByShopId(shopId);
    if(CollectionUtils.isNotEmpty(shopVehicleBrandModelDTOList)){
      Map<String,String> result = new HashMap<String, String>();
      String vehicleModelStr = null;
      for(ShopVehicleBrandModelDTO shopVehicleBrandModelDTO:shopVehicleBrandModelDTOList){
        vehicleModelStr = result.get(shopVehicleBrandModelDTO.getBrandName());
        if(StringUtils.isNotBlank(vehicleModelStr)){
          vehicleModelStr+=" "+shopVehicleBrandModelDTO.getModelName();
        }else{
          vehicleModelStr = shopVehicleBrandModelDTO.getModelName();
        }
        result.put(shopVehicleBrandModelDTO.getBrandName(),vehicleModelStr);
      }
      return result;
    }
    return null;
  }

  /**
   * 获取所有标准车辆品牌、车型
   *
   * @return
   */
  private List<ShopVehicleBrandModelDTO> getAllStandardVehicleBrandModel() {

    //返回类型
    List<ShopVehicleBrandModelDTO> shopVehicleBrandModelDTOList = new ArrayList<ShopVehicleBrandModelDTO>();

    //所有车辆品牌
    List<StandardVehicleBrandDTO> standardVehicleBrandDTOList = this.getAllStandardVehicleBrand();

    //所有车型
    List<StandardVehicleModelDTO> standardVehicleModelDTOList = this.getAllStandardVehicleModel();

    if(CollectionUtils.isEmpty(standardVehicleBrandDTOList) || CollectionUtils.isEmpty(standardVehicleModelDTOList)){
      return shopVehicleBrandModelDTOList;
    }
    Map<Long,StandardVehicleBrandDTO> vehicleBrandDTOMap = new HashMap<Long,StandardVehicleBrandDTO>();
    for(StandardVehicleBrandDTO vehicleBrandDTO : standardVehicleBrandDTOList){
      vehicleBrandDTOMap.put(vehicleBrandDTO.getId(),vehicleBrandDTO);
    }

    //封装
    for(StandardVehicleModelDTO vehicleModelDTO : standardVehicleModelDTOList){
      StandardVehicleBrandDTO standardVehicleBrandDTO = vehicleBrandDTOMap.get(vehicleModelDTO.getStandardVehicleBrandId());

      ShopVehicleBrandModelDTO vehicleBrandModelDTO = new ShopVehicleBrandModelDTO();

      vehicleBrandModelDTO.setBrandId(standardVehicleBrandDTO.getId());
      vehicleBrandModelDTO.setBrandName(standardVehicleBrandDTO.getName());
      vehicleBrandModelDTO.setFirstLetter(standardVehicleBrandDTO.getFirstLetter());

      vehicleBrandModelDTO.setModelId(vehicleModelDTO.getId());
      vehicleBrandModelDTO.setModelName(vehicleModelDTO.getName());

      shopVehicleBrandModelDTOList.add(vehicleBrandModelDTO);
    }
    return shopVehicleBrandModelDTOList;
  }

  /**
   * 根据名称或者首字母获取车辆品牌
   *
   * @return
   */
  private List<StandardVehicleBrandDTO> getAllStandardVehicleBrand() {
    ProductReadReader productReader = productReadDaoManager.getReader();

    List<StandardVehicleBrand> standardVehicleBrands = productReader.getAllStandardVehicleBrand();
    List<StandardVehicleBrandDTO> standardVehicleBrandDTOList = new ArrayList<StandardVehicleBrandDTO>();
    if (CollectionUtils.isNotEmpty(standardVehicleBrands)) {
      for (StandardVehicleBrand standardVehicleBrand : standardVehicleBrands) {
        standardVehicleBrandDTOList.add(standardVehicleBrand.toDTO());
      }
    }
    return standardVehicleBrandDTOList;
  }
  /**
   * 根据名称或者standardVehicleBrandId获取车型
   *
   * @return
   */
  private List<StandardVehicleModelDTO> getAllStandardVehicleModel() {
    ProductReadReader productReader = productReadDaoManager.getReader();

    List<StandardVehicleModel> standardVehicleModels = productReader.getAllStandardVehicleModel();
    List<StandardVehicleModelDTO> standardVehicleModelDTOs = new ArrayList<StandardVehicleModelDTO>();
    if (CollectionUtils.isNotEmpty(standardVehicleModels)) {
      for (StandardVehicleModel standardVehicleModel : standardVehicleModels) {
        standardVehicleModelDTOs.add(standardVehicleModel.toDTO());
      }
    }
    return standardVehicleModelDTOs;
  }
}
