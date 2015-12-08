package com.bcgogo.product.service;

import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.config.VehicleSelectBrandModel;
import com.bcgogo.product.dto.PingyinInfo;
import com.bcgogo.product.model.*;
import com.bcgogo.product.standardVehicleBrandModel.ShopVehicleBrandModelDTO;
import com.bcgogo.product.standardVehicleBrandModel.StandardVehicleBrandDTO;
import com.bcgogo.product.standardVehicleBrandModel.StandardVehicleModelDTO;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.PinyinUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 标准车辆品牌（车型）专用接口
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-7-22
 * Time: 下午5:17
 * To change this template use File | Settings | File Templates.
 */
@Component
public class StandardBrandModelService implements IStandardBrandModelService {
  @Autowired
  private ProductDaoManager productDaoManager;

  private static final Logger LOG = LoggerFactory.getLogger(StandardBrandModelService.class);

  /**
   * 根据店铺获取该店铺下的标准车辆品牌、车型
   *
   * 如果注册时选择的是全部车型 这把标准车辆品牌、车型返回回去 此时shopVehicleBrandModelDTO没有id
   * @param shopId
   * @return
   */
  public List<ShopVehicleBrandModelDTO> getShopVehicleBrandModelByShopId(Long shopId) {
    List<ShopVehicleBrandModelDTO> shopVehicleBrandModelDTOList = new ArrayList<ShopVehicleBrandModelDTO>();

    if (shopId == null) {
      return shopVehicleBrandModelDTOList;
    }

    //   * 如果注册时选择的是全部车型 这把标准车辆品牌、车型返回回去 此时shopVehicleBrandModelDTO没有id
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    ShopDTO shopDTO = configService.getShopById(shopId);
    if (shopDTO.getShopSelectBrandModel() == VehicleSelectBrandModel.ALL_MODEL) {
      shopVehicleBrandModelDTOList = this.getAllStandardVehicleBrandModel();

      if (CollectionUtils.isNotEmpty(shopVehicleBrandModelDTOList)) {
        for (ShopVehicleBrandModelDTO vehicleBrandModelDTO : shopVehicleBrandModelDTOList) {
          vehicleBrandModelDTO.setShopId(shopId);
        }
      }
      return shopVehicleBrandModelDTOList;
    }

    ProductWriter writer = productDaoManager.getWriter();
    List<ShopVehicleBrandModel> shopVehicleBrandModelList = writer.getShopVehicleBrandModelByShopId(shopId);

    if (CollectionUtils.isEmpty(shopVehicleBrandModelList)) {
      return shopVehicleBrandModelDTOList;
    }

    for (ShopVehicleBrandModel shopVehicleBrandModel : shopVehicleBrandModelList) {
      shopVehicleBrandModelDTOList.add(shopVehicleBrandModel.toDTO());
    }

    return shopVehicleBrandModelDTOList;
  }

  /**
   * 根据名称或者首字母获取车辆品牌
   *
   * @param name
   * @param firstLetter
   * @return
   */
  public List<StandardVehicleBrandDTO> getStandardVehicleBrandByName(String name, String firstLetter) {
    ProductWriter writer = productDaoManager.getWriter();

    List<StandardVehicleBrand> standardVehicleBrands = writer.getStandardVehicleBrandByName(name, firstLetter);
    List<StandardVehicleBrandDTO> standardVehicleBrandDTOList = new ArrayList<StandardVehicleBrandDTO>();
    if (CollectionUtils.isNotEmpty(standardVehicleBrands)) {
      for (StandardVehicleBrand standardVehicleBrand : standardVehicleBrands) {
        standardVehicleBrandDTOList.add(standardVehicleBrand.toDTO());
      }
    }
    return standardVehicleBrandDTOList;
  }

  @Override
  public List<StandardVehicleBrandDTO> getStandardVehicleBrandSuggestionByName(String name, String firstLetter) {
    ProductWriter writer = productDaoManager.getWriter();

    List<StandardVehicleBrand> standardVehicleBrands = writer.getStandardVehicleBrandSuggestionByName(name, firstLetter);
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
   * @param name
   * @param standardVehicleBrandId
   * @param name 名称
   * @return
   */
  public List<StandardVehicleModelDTO> getStandardVehicleModelByName(Long standardVehicleBrandId,String name) {
    ProductWriter writer = productDaoManager.getWriter();

    List<StandardVehicleModel> standardVehicleModels = writer.getStandardVehicleModelByName(standardVehicleBrandId, name);
    List<StandardVehicleModelDTO> standardVehicleModelDTOs = new ArrayList<StandardVehicleModelDTO>();
    if (CollectionUtils.isNotEmpty(standardVehicleModels)) {
      for (StandardVehicleModel standardVehicleModel : standardVehicleModels) {
        standardVehicleModelDTOs.add(standardVehicleModel.toDTO());
      }
    }
    return standardVehicleModelDTOs;
  }

  public List<StandardVehicleModelDTO> getStandardVehicleModelSuggestionByName(Long standardVehicleBrandId,String name) {
    ProductWriter writer = productDaoManager.getWriter();

    List<StandardVehicleModel> standardVehicleModels = writer.getStandardVehicleModelSuggestionByName(standardVehicleBrandId, name);
    List<StandardVehicleModelDTO> standardVehicleModelDTOs = new ArrayList<StandardVehicleModelDTO>();
    if (CollectionUtils.isNotEmpty(standardVehicleModels)) {
      for (StandardVehicleModel standardVehicleModel : standardVehicleModels) {
        standardVehicleModelDTOs.add(standardVehicleModel.toDTO());
      }
    }
    return standardVehicleModelDTOs;
  }

  /**
   * 获取所有标准车辆品牌、车型
   *
   * @return
   */
  public List<ShopVehicleBrandModelDTO> getAllStandardVehicleBrandModel() {

    //返回类型
    List<ShopVehicleBrandModelDTO> shopVehicleBrandModelDTOList = new ArrayList<ShopVehicleBrandModelDTO>();

    //所有车辆品牌
    List<StandardVehicleBrandDTO> standardVehicleBrandDTOList = this.getStandardVehicleBrandByName(null,null);

    //所有车型
    List<StandardVehicleModelDTO> standardVehicleModelDTOList = this.getStandardVehicleModelByName(null,null);

    if(CollectionUtils.isEmpty(standardVehicleBrandDTOList) || CollectionUtils.isEmpty(standardVehicleModelDTOList)){
      return shopVehicleBrandModelDTOList;
    }
    Map<Long,StandardVehicleBrandDTO> vehicleBrandDTOMap = new HashMap<Long,StandardVehicleBrandDTO>();
    for(StandardVehicleBrandDTO vehicleBrandDTO : standardVehicleBrandDTOList){
      vehicleBrandDTOMap.put(vehicleBrandDTO.getId(),vehicleBrandDTO);
    }

    //封装
    for(StandardVehicleModelDTO vehicleModelDTO : standardVehicleModelDTOList){
      StandardVehicleBrandDTO vehicleBrandDTO = vehicleBrandDTOMap.get(vehicleModelDTO.getStandardVehicleBrandId());

      ShopVehicleBrandModelDTO vehicleBrandModelDTO = new ShopVehicleBrandModelDTO();
      PingyinInfo brandPingyinInfo = PinyinUtil.getPingyinInfo(vehicleBrandDTO.getName());
      vehicleBrandModelDTO.setBrandId(vehicleBrandDTO.getId());
      vehicleBrandModelDTO.setBrandName(vehicleBrandDTO.getName());
      vehicleBrandModelDTO.setBrandPy(brandPingyinInfo.pingyin);
      vehicleBrandModelDTO.setBrandFl(brandPingyinInfo.firstLetters);
      vehicleBrandModelDTO.setFirstLetter(vehicleBrandDTO.getFirstLetter());

      vehicleBrandModelDTO.setModelId(vehicleModelDTO.getId());
      PingyinInfo modelPingyinInfo = PinyinUtil.getPingyinInfo(vehicleModelDTO.getName());
      vehicleBrandModelDTO.setModelName(vehicleModelDTO.getName());
      vehicleBrandModelDTO.setModelPy(modelPingyinInfo.pingyin);
      vehicleBrandModelDTO.setModelFl(modelPingyinInfo.firstLetters);

      shopVehicleBrandModelDTOList.add(vehicleBrandModelDTO);
    }
    return shopVehicleBrandModelDTOList;
  }

  @Override
  public Map<Long, StandardVehicleBrandDTO> getStandardVehicleBrandMapByIds(Set<Long> ids) {
    Map<Long,StandardVehicleBrandDTO> vehicleBrandDTOMap = new HashMap<Long,StandardVehicleBrandDTO>();
    if (CollectionUtils.isEmpty(ids)) return vehicleBrandDTOMap;
    ProductWriter writer = productDaoManager.getWriter();
    List<StandardVehicleBrand> brands  = writer.getStandardVehicleBrandByIds(ids);
    for(StandardVehicleBrand brand : brands){
      vehicleBrandDTOMap.put(brand.getId(),brand.toDTO());
    }
    return vehicleBrandDTOMap;
  }

  @Override
  public Map<String, StandardVehicleBrandDTO> getNameStandardVehicleBrandMapByIds(Set<Long> ids) {
    Map<String,StandardVehicleBrandDTO> vehicleBrandDTOMap = new HashMap<String,StandardVehicleBrandDTO>();
    if (CollectionUtils.isEmpty(ids)) return vehicleBrandDTOMap;
    ProductWriter writer = productDaoManager.getWriter();
    List<StandardVehicleBrand> brands  = writer.getStandardVehicleBrandByIds(ids);
    for(StandardVehicleBrand brand : brands){
      vehicleBrandDTOMap.put(brand.getName(),brand.toDTO());
    }
    return vehicleBrandDTOMap;
  }

  @Override
  public Map<String, StandardVehicleBrandDTO> getNameStandardVehicleBrandMapByNames(Set<String> names) {
    Map<String,StandardVehicleBrandDTO> vehicleBrandDTOMap = new HashMap<String,StandardVehicleBrandDTO>();
    if (CollectionUtils.isEmpty(names)) return vehicleBrandDTOMap;
    ProductWriter writer = productDaoManager.getWriter();
    List<StandardVehicleBrand> brands  = writer.getStandardVehicleBrandByNames(names);
    for(StandardVehicleBrand brand : brands){
      vehicleBrandDTOMap.put(brand.getName(),brand.toDTO());
    }
    return vehicleBrandDTOMap;
  }

  @Override
  public StandardVehicleBrandDTO getStandardVehicleBrandDTOByName(String name) {
    if (StringUtils.isEmpty(name)) return null;
    ProductWriter writer = productDaoManager.getWriter();
    Set<String> names = new HashSet<String>();
    names.add(name);
    StandardVehicleBrand standardVehicleBrand  = CollectionUtil.getFirst(writer.getStandardVehicleBrandByNames(names));
    if(standardVehicleBrand != null){
      return standardVehicleBrand.toDTO();
    }
    return null;
  }


  @Override
  public Map<String, StandardVehicleModelDTO> getNameStandardVehicleModelMapByNames(Set<String> names) {
    Map<String,StandardVehicleModelDTO> map = new HashMap<String,StandardVehicleModelDTO>();
    if (CollectionUtils.isEmpty(names)) return map;
    ProductWriter writer = productDaoManager.getWriter();
    List<StandardVehicleModel> models  = writer.getStandardVehicleModelByNames(names);
    for(StandardVehicleModel model : models){
      map.put(model.getName(), model.toDTO());
    }
    return map;
  }

  @Override
  public List<StandardVehicleModelDTO> getStandardVehicleModelListByIds(Set<Long> ids) {
    List<StandardVehicleModelDTO> standardVehicleModelDTOList = new ArrayList<StandardVehicleModelDTO>();
    if (CollectionUtils.isEmpty(ids)) return standardVehicleModelDTOList;
    ProductWriter writer = productDaoManager.getWriter();
    List<StandardVehicleModel> standardVehicleModels = writer.getStandardVehicleModelByIds(ids);
    if(CollectionUtils.isNotEmpty(standardVehicleModels)) {
      for(StandardVehicleModel standardVehicleModel : standardVehicleModels) {
        standardVehicleModelDTOList.add(standardVehicleModel.toDTO());
      }
    }
    return  standardVehicleModelDTOList;
  }
}
