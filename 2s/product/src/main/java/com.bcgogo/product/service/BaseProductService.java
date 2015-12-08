package com.bcgogo.product.service;

import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.model.*;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.VehicleDTO;
import com.bcgogo.utils.PinyinUtil;
import com.bcgogo.utils.SearchConstant;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 产品模块基本CRUD操作
 * Created by IntelliJ IDEA.
 * User: wjl
 * Date: 12-2-25
 * Time: 下午1:54
 * To change this template use File | Settings | File Templates.
 */
@Component
public class BaseProductService implements IBaseProductService {
  @Autowired
  private ProductDaoManager productDaoManager;

  /**
   * 将车型添加到数据库（不存入SOLR）
   *
   * @param brand
   * @param model
   * @param year
   * @param engine
   * @return
   * @throws Exception
   */
  @Override
  public VehicleDTO addVehicleToDB(String brand, String model, String year, String engine) throws Exception {
    ProductWriter writer = productDaoManager.getWriter();
    Object status = writer.begin();
    Long bid = null, mid = null, yid = null, eid = null;
    try {
      VehicleDTO vehicleDTO = new VehicleDTO();
      if (!StringUtils.isBlank(brand)) {
        bid = saveBrand(brand, writer);
        vehicleDTO.setId(bid);
      }
      if (!StringUtils.isBlank(model)) {
        mid = saveModel(bid, model, writer);
        vehicleDTO.setId(mid);
      }
      if (!StringUtils.isBlank(year)) {
        yid = saveYear(bid, mid, year, writer);
        vehicleDTO.setId(yid);
      }
      if (!StringUtils.isBlank(engine)) {
        eid = saveEngine(bid, mid, yid, engine, writer);
        vehicleDTO.setId(eid);
      }
      writer.commit(status);
      vehicleDTO.setBrand(brand);
      vehicleDTO.setModel(model);
      vehicleDTO.setYear(year);
      vehicleDTO.setEngine(engine);
      vehicleDTO.setVirtualBrandId(bid);
      vehicleDTO.setVirtualModelId(mid);
      vehicleDTO.setVirtualYearId(yid);
      vehicleDTO.setVirtualEngineId(eid);

      vehicleDTO.setBrandPinYin(PinyinUtil.converterToFirstSpell(brand).toLowerCase());
      vehicleDTO.setModelPinYin(PinyinUtil.converterToFirstSpell(model).toLowerCase());
      return vehicleDTO;
    } finally {
      writer.rollback(status);
    }
  }

  /**
   * 保存车辆品牌  该品牌已存在，返回ID；不存在，新增后返回ID
   *
   * @param brand  车辆品牌
   * @param writer
   * @return
   * @author wjl
   */
  public Long saveBrand(String brand, ProductWriter writer) throws Exception {
    Brand b = writer.getBrandByName(brand);
    if (b != null) {
      return b.getId();
    } else {
      Brand newBrand = new Brand();
      newBrand.setName(brand);
      newBrand.setFirstLetter(PinyinUtil.getFirstLetter(brand).toLowerCase());
      writer.save(newBrand);
      return newBrand.getId();
    }
  }

  /**
   * 保存车型  该车型已存在，返回ID；不存在，新增后返回ID
   *
   * @param brandId 所属品牌的ID
   * @param model   车型名称
   * @param writer
   * @return
   * @author wjl
   */
  public Long saveModel(Long brandId, String model, ProductWriter writer) throws Exception {
    Model m = writer.getModelByName(brandId, model);
    if (m != null) {
      return m.getId();
    } else {
      Model newModel = new Model();
      newModel.setName(model);
      newModel.setBrandId(brandId);
      newModel.setFirstLetter(PinyinUtil.getFirstLetter(model).toLowerCase());
      writer.save(newModel);
      return newModel.getId();
    }
  }

  /**
   * 保存车辆年代  该年代已存在，返回ID；不存在，新增后返回ID
   *
   * @param brandId 所属车辆品牌ID
   * @param modelId 所属车型ID
   * @param year    年代
   * @param writer
   * @return
   * @author wjl
   */
  public Long saveYear(Long brandId, Long modelId, String year, ProductWriter writer) throws Exception {
//    Integer intYear = (year == null || "".equals(year) ? null : Integer.parseInt(year));
    Integer intYear = NumberUtils.toInt(year);
    Year y = writer.getYearByName(intYear, modelId, brandId);
    if (y != null) {
      return y.getId();
    } else {
      Year newYear = new Year();
      newYear.setBrandId(brandId);
      newYear.setModelId(modelId);
      newYear.setYear(intYear);
      writer.save(newYear);
      return newYear.getId();
    }
  }

  /**
   * 保存排量  该排量已存在，返回ID；不存在，新增后返回ID
   *
   * @param brandId 所属车辆品牌ID
   * @param modelId 所属车型ID
   * @param yearId  所属年代ID
   * @param engine  排量值
   * @param writer
   * @return
   * @author wjl
   */
  public Long saveEngine(Long brandId, Long modelId, Long yearId, String engine, ProductWriter writer) throws Exception {
    Engine e = writer.getEngineByName(engine, yearId, modelId, brandId);
    if (e != null) {
      return e.getId();
    } else {
      Engine newEngine = new Engine();
      newEngine.setBrandId(brandId);
      newEngine.setModelId(modelId);
      newEngine.setYearId(yearId);
      newEngine.setEngine(engine);
      writer.save(newEngine);
      return newEngine.getId();
    }
  }

  /**
   * 添加车型公共方法
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
   * @author wjl
   */
  @Override
  public Long[] saveVehicle(Long brandId, Long modelId, Long yearId, Long engineId, String brand, String model, String year,
                            String engine) throws Exception {
    if ((brandId == null || modelId == null || yearId == null || engineId == null)
        && (!StringUtils.isBlank(brand)) && (!StringUtils.isBlank(model)) &&
        !SearchConstant.PRODUCT_PRODUCTSTATUS_ALL_VALUE.equals(brand) &&
        !SearchConstant.PRODUCT_PRODUCTSTATUS_MULTIPLE_VALUE.equals(brand)) {

      VehicleDTO vehicleDTO = addVehicleToDB(brand, model, year, engine);
      List<VehicleDTO> vehicleDTOList=new ArrayList<VehicleDTO>();
      vehicleDTOList.add(vehicleDTO);
//
      ServiceManager.getService(IProductSolrService.class).addVehicleForSearch(vehicleDTOList);

      return new Long[]{vehicleDTO.getVirtualBrandId(), vehicleDTO.getVirtualModelId(),
          vehicleDTO.getVirtualYearId(), vehicleDTO.getVirtualEngineId()};
    }
    return new Long[]{brandId, modelId, yearId, engineId};
  }

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
  @Override
  public ProductDTO addProductToDB(Long shopId, String productName, String brand, String spec, String model) throws Exception {
    ProductDTO productDTO = new ProductDTO();
    productDTO.setName("".equals(productName) ? null : productName);
    productDTO.setBrand("".equals(brand) ? null : brand);
    productDTO.setSpec("".equals(spec) ? null : spec);
    productDTO.setModel("".equals(model) ? null : model);

    productDTO.setFirstLetter(PinyinUtil.getFirstLetter(productName));
    productDTO.setFirstLetterCombination(PinyinUtil.converterToFirstSpell(productName));
    productDTO.setProductVehicleStatus(SearchConstant.PRODUCT_PRODUCTSTATUS_NULL);
    productDTO.setShopId(shopId);
    ProductDTO pDTO = ServiceManager.getService(IProductService.class).createProduct(productDTO);
    return pDTO;
  }

  @Override
  public List<VehicleDTO> getVehicleDTOListByBrand() throws Exception {
    ProductWriter writer = productDaoManager.getWriter();
    List<VehicleDTO> vehicleDTOList = writer.getVehicleDTOListByBrand();
    if(CollectionUtils.isNotEmpty(vehicleDTOList)){
      for(VehicleDTO vehicleDTO : vehicleDTOList){
        if(StringUtils.isNotBlank(vehicleDTO.getBrand()) && StringUtils.isBlank(vehicleDTO.getBrandPinYin())){
          vehicleDTO.setBrandPinYin(PinyinUtil.converterToFirstSpell(vehicleDTO.getBrand()).toLowerCase());
        }
      }
    }
    return vehicleDTOList;
  }

  @Override
  public List<VehicleDTO> getVehicleDTOListByModel() throws Exception {
    ProductWriter writer = productDaoManager.getWriter();
    List<VehicleDTO> vehicleDTOList = writer.getVehicleDTOListByModel();
    if(CollectionUtils.isNotEmpty(vehicleDTOList)){
      for(VehicleDTO vehicleDTO : vehicleDTOList){
        if(StringUtils.isNotBlank(vehicleDTO.getBrand()) && StringUtils.isBlank(vehicleDTO.getBrandPinYin())){
          vehicleDTO.setBrandPinYin(PinyinUtil.converterToFirstSpell(vehicleDTO.getBrand()).toLowerCase());
        }
        if(StringUtils.isNotBlank(vehicleDTO.getModel()) && StringUtils.isBlank(vehicleDTO.getModelPinYin())){
          vehicleDTO.setModelPinYin(PinyinUtil.converterToFirstSpell(vehicleDTO.getModel()).toLowerCase());
        }
      }
    }
    return vehicleDTOList;
  }

  @Override
  public List<VehicleDTO> getVehicleDTOListByYear() throws Exception {
    ProductWriter writer = productDaoManager.getWriter();
    List<VehicleDTO> vehicleDTOList = writer.getVehicleDTOListByYear();
    if(CollectionUtils.isNotEmpty(vehicleDTOList)){
      for(VehicleDTO vehicleDTO : vehicleDTOList){
        if(StringUtils.isNotBlank(vehicleDTO.getBrand()) && StringUtils.isBlank(vehicleDTO.getBrandPinYin())){
          vehicleDTO.setBrandPinYin(PinyinUtil.converterToFirstSpell(vehicleDTO.getBrand()).toLowerCase());
        }
        if(StringUtils.isNotBlank(vehicleDTO.getModel()) && StringUtils.isBlank(vehicleDTO.getModelPinYin())){
          vehicleDTO.setModelPinYin(PinyinUtil.converterToFirstSpell(vehicleDTO.getModel()).toLowerCase());
        }
      }
    }
    return vehicleDTOList;
  }

  @Override
  public List<VehicleDTO> getVehicleDTOListByEngine() throws Exception {
    ProductWriter writer = productDaoManager.getWriter();
    List<VehicleDTO> vehicleDTOList = writer.getVehicleDTOListByEngine();
    if(CollectionUtils.isNotEmpty(vehicleDTOList)){
      for(VehicleDTO vehicleDTO : vehicleDTOList){
        if(StringUtils.isNotBlank(vehicleDTO.getBrand()) && StringUtils.isBlank(vehicleDTO.getBrandPinYin())){
          vehicleDTO.setBrandPinYin(PinyinUtil.converterToFirstSpell(vehicleDTO.getBrand()).toLowerCase());
        }
        if(StringUtils.isNotBlank(vehicleDTO.getModel()) && StringUtils.isBlank(vehicleDTO.getModelPinYin())){
          vehicleDTO.setModelPinYin(PinyinUtil.converterToFirstSpell(vehicleDTO.getModel()).toLowerCase());
        }
      }
    }
    return vehicleDTOList;
  }
}
