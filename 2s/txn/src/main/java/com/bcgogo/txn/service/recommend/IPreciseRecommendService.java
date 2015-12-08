package com.bcgogo.txn.service.recommend;

import com.bcgogo.common.Pager;
import com.bcgogo.config.dto.ApplyShopSearchCondition;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.enums.DeletedType;
import com.bcgogo.enums.shop.ShopKind;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.productManage.ProductSearchCondition;
import com.bcgogo.txn.dto.PreBuyOrderItemDTO;
import com.bcgogo.txn.dto.recommend.ProductRecommendDTO;
import com.bcgogo.txn.dto.recommend.SalesInventoryWeekStatDTO;
import com.bcgogo.txn.dto.recommend.ShopProductMatchResultDTO;
import com.bcgogo.txn.model.recommend.ProductRecommend;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.SupplierDTO;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 精准推荐
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-6-19
 * Time: 上午10:39
 * To change this template use File | Settings | File Templates.
 */
public interface IPreciseRecommendService {
  /**
   * 1.统计所有汽修版的店铺上周 销售量和入库量总和 并按品名和品牌去重
   * 2.遍历所有汽修店铺 找到销量前十的商品、注册填写的商品、经营范围的商品 用品名 品牌去重
   * 3.用品名 品牌去匹配所有汽修版总和的统计值 如果匹配 作为展示结果保存数据
   */
  public void salesInventoryMonthStat();

  /**
   * 根据店铺id获取上周商品的销售量和入库量
   * @param shopId
   * @return
   */
  public Map<String, SalesInventoryWeekStatDTO> getSalesInventoryWeekStatByShopId(Long shopId, ShopKind shopKind);


  /**
   * 获取上一周某个店铺销量前十的商品
   * @param shopId
   * @return
   */
  public List<ProductDTO> getLastMonthTopTenSalesByShopId(Long shopId);


  /**
   * 根据shopId获取第二大类的经营范围的合计
   * @param shopIdSet
   * @return
   */
  public Map<Long,String> getSecondCategoryByShopId(Set<Long> shopIdSet);


  /**
   * 推荐客户 推荐供应商 获取经营范围
   * @param applyShopSearchConditionList
   * @return
   */
  public List<ApplyShopSearchCondition> getShopBusinessScopeForApply(List<ApplyShopSearchCondition> applyShopSearchConditionList);

  /**
   * 获取本店推荐客户或者供应商
   * @param shopId
   * @param pager
   * @return
   */
  public List<ShopDTO> getRecommendShopByShopId(Long shopId,Pager pager);

  /**
   * 汽配版获取推荐求购信息
   * @param shopId
   * @param deletedType
   * @param pager
   * @return
   */
  public List<PreBuyOrderItemDTO> getWholesalerProductRecommendByPager(Long shopId,DeletedType deletedType,Pager pager);



  /**
   * 汽修版获取推荐商品数量
   * @param shopId
   * @param deletedType
   * @return
   */
  public int countProductRecommendByShopId(Long shopId,DeletedType deletedType);


  /**
   * 汽配版获取推荐商品数量
   * @param shopId
   * @param deletedType
   * @return
   */
  public int countWholesalerProductRecommendByShopId(Long shopId,DeletedType deletedType);

  /**
   * 供求中心首页获取上周销量信息(汽配版)
   * @param shopId
   * @return
   */
  public int countLastWeekSalesInventoryStatByShopId(Long shopId,int weekOfYear);

    /**
   * 供求中心首页获取上周销量信息(汽配版)
   * @param shopId
   * @return
   */
  public List<ShopProductMatchResultDTO> getLastMonthSalesInventoryStatByShopId(Long shopId,int statYear,int statMonth,int statDay,Pager pager);

    /**
   * 获取客户或者供应商的二级分类（关联供应商专用）
   * @param customerDTO
   * @param supplierDTO
   * @return
   */

  public void getCustomerSupplierBusinessScope(CustomerDTO customerDTO,SupplierDTO supplierDTO);

  /**
   * 获取客户或者供应商的二级分类（新增客户或者供应商专用）
   * @param customerDTO
   * @param supplierDTO
   * @return
   */
  public void getCustomerSupplierBusinessScopeForAdd(CustomerDTO customerDTO, SupplierDTO supplierDTO);


  /**
   * 批量设置客户或者供应商的经营范围
   * @param customerDTOList
   * @param supplierDTOList
   */
  public void setCustomerSupplierBusinessScope(List<CustomerDTO> customerDTOList,List<SupplierDTO> supplierDTOList);

  List<ProductDTO> getRecommendProductDetailDTOs(ProductSearchCondition condition) throws Exception;

  List<ProductDTO> getSalesAmountByShopIdProductIdTime(Long shopId, Long startTime, Long endTime, Long... productId);

  Map<Long,Double> getSalesAmountMapByShopIdProductIdTime(Long shopId, Long startTime, Long endTime, Long... productId);
}
