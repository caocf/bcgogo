package com.bcgogo.product.service;

import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.dto.ProductVehicleDTO;
import com.bcgogo.user.dto.VehicleDTO;

import java.util.HashMap;
import java.util.List;

/**
 * 处理产品模块SOLR相关逻辑
 * Created by IntelliJ IDEA.
 * User: wjl
 * Date: 12-2-25
 * To change this template use File | Settings | File Templates.
 */
public interface IProductSolrService {

  /**
   * 将产品加入SOLR
   *
   * @param productDTOList
   * @param isBasicDate 是否为标准库产品
   * @throws Exception
   * @author wjl
   */
  public void addProductForSolr(List<ProductDTO> productDTOList, boolean isBasicDate) throws Exception;

  /**
   * 将车型加入SOLR
   * 1.车型车辆品牌  下拉建议   doc_type:vehicle_suggestion
   * @param vehicleDTOList 需要加入SOLR的车型集合
   * @throws Exception
   * @author wjl
   */
  public void addVehicleForSearch(List<VehicleDTO> vehicleDTOList) throws Exception;

  /**
   * 将数据重新reindex
   *
   * @param shopId
   * @throws Exception
   */
  public void reindexProductForSolr(Long shopId) throws Exception;//zhouxiaochen 2012-1-15

  /**
   * reindex车型的首字母
   *
   * @throws Exception
   */
  public void reindexVehicleLetter() throws Exception;

}
