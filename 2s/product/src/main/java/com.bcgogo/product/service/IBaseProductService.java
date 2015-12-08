package com.bcgogo.product.service;

import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.user.dto.VehicleDTO;

import java.util.List;

/**
 * 产品模块基本CRUD操作
 * Created by IntelliJ IDEA.
 * User: wjl
 * Date: 12-2-25
 * To change this template use File | Settings | File Templates.
 */
public interface IBaseProductService {

  /**
   * * 添加车型公共方法
   * 条件：车型ID至少有一个为空，且车型不能为“全部”与"多款"
   *
   * @param brandId
   * @param modelId
   * @param yearId
   * @param engineId
   * @param brand
   * @param model
   * @param year
   * @param engine
   * @return
   * @throws Exception
   */
  public Long[] saveVehicle(Long brandId, Long modelId, Long yearId, Long engineId, String brand, String model, String year,
                            String engine) throws Exception;

  /**
   * 将车型添加到数据库（不存入SOLR）
   * 只用于某些特殊功能(如数据导入)，不是添加车型的完整操作。
   *
   * @param brand
   * @param model
   * @param year
   * @param engine
   * @return
   * @throws Exception
   * @author wjl
   */
  public VehicleDTO addVehicleToDB(String brand, String model, String year, String engine) throws Exception;

  /**
   * 将产品添加到数据库(不存入SOLR)
   *
   * @param shopId
   * @param productName
   * @param brand
   * @param spec
   * @param model
   * @return
   * @throws Exception
   * @author wjl
   */
  public ProductDTO addProductToDB(Long shopId, String productName, String brand, String spec, String model) throws Exception;

  public List<VehicleDTO> getVehicleDTOListByBrand() throws Exception;

  public List<VehicleDTO> getVehicleDTOListByModel() throws Exception;

  public List<VehicleDTO> getVehicleDTOListByYear() throws Exception;

  public List<VehicleDTO> getVehicleDTOListByEngine() throws Exception;
}
