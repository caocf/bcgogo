package com.bcgogo.product.service;

import com.bcgogo.product.standardVehicleBrandModel.ShopVehicleBrandModelDTO;
import com.bcgogo.product.standardVehicleBrandModel.StandardVehicleBrandDTO;
import com.bcgogo.product.standardVehicleBrandModel.StandardVehicleModelDTO;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 标准车辆品牌（车型）专用接口
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-7-22
 * Time: 下午5:16
 * To change this template use File | Settings | File Templates.
 */
public interface IStandardBrandModelService {
  /**
   * 根据店铺 车辆品牌名字、首字母获取该店铺下的标准车辆品牌、车型
   * @param shopId
   * @return
   */
  public List<ShopVehicleBrandModelDTO> getShopVehicleBrandModelByShopId(Long shopId);

  /**
   * 根据名称或者首字母获取车辆品牌
   * @param name
   * @param firstLetter
   * @return
   */
  public List<StandardVehicleBrandDTO> getStandardVehicleBrandByName(String name,String firstLetter);

  public List<StandardVehicleBrandDTO> getStandardVehicleBrandSuggestionByName(String name,String firstLetter);

    /**
   * 根据名称或者standardVehicleBrandId获取车型
   *
   * @param name
   * @param standardVehicleBrandId
   * @param name 名称
   * @return
   */
  public List<StandardVehicleModelDTO> getStandardVehicleModelByName(Long standardVehicleBrandId,String name);

  public List<StandardVehicleModelDTO> getStandardVehicleModelSuggestionByName(Long standardVehicleBrandId,String name);

  /**
   * 获取所有标准车辆品牌、车型
   * @return
   */
  public List<ShopVehicleBrandModelDTO> getAllStandardVehicleBrandModel();

  Map<Long,StandardVehicleBrandDTO> getStandardVehicleBrandMapByIds(Set<Long> ids);

  Map<String,StandardVehicleBrandDTO> getNameStandardVehicleBrandMapByIds(Set<Long> ids);
  Map<String,StandardVehicleBrandDTO> getNameStandardVehicleBrandMapByNames(Set<String> names);
  StandardVehicleBrandDTO getStandardVehicleBrandDTOByName(String name);

  Map<String,StandardVehicleModelDTO> getNameStandardVehicleModelMapByNames(Set<String> names);

  List<StandardVehicleModelDTO> getStandardVehicleModelListByIds(Set<Long> ids);
}
