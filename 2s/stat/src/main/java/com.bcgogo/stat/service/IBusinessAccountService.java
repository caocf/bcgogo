package com.bcgogo.stat.service;

import com.bcgogo.enums.stat.businessAccountStat.BusinessCategoryStatType;
import com.bcgogo.enums.stat.businessAccountStat.CalculateType;
import com.bcgogo.enums.stat.businessAccountStat.MoneyCategory;
import com.bcgogo.stat.dto.BusinessAccountDTO;
import com.bcgogo.stat.dto.BusinessAccountSearchConditionDTO;
import com.bcgogo.stat.dto.BusinessCategoryDTO;
import com.bcgogo.stat.dto.BusinessCategoryStatDTO;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Li jinlong
 * Date: 12-9-19
 * Time: 下午5:26
 * To change this template use File | Settings | File Templates.
 */
public interface IBusinessAccountService {

  /**
   * 根据查询条件统计营业记账数量
   * @param shopId
   * @param searchConditionDTO
   * @return
   */
  public List<String> countBusinessAccountsBySearchCondition(Long shopId,BusinessAccountSearchConditionDTO searchConditionDTO);

  /**
   * 保存营业记账
   * @param businessAccountDTO
   * @return
   * @throws Exception
   */
  public BusinessAccountDTO saveBusinessAccount(BusinessAccountDTO businessAccountDTO) throws Exception;

  /**
   * 根据查询条件取得营业记账
   * @param shopId
   * @return
   */
  public List<BusinessAccountDTO> getBusinessAccountsBySearchCondition(Long shopId, BusinessAccountSearchConditionDTO searchConditionDTO);

  /**
   * 更新营业记账
   * @param businessAccountDTO
   * @throws Exception
   */
  public void updateBusinessAccount(BusinessAccountDTO businessAccountDTO) throws Exception;

  /**
   * 根据营业记账ID取得营业记账
   * @param id
   * @return
   */
  public BusinessAccountDTO getBusinessAccountById(Long id);

  /**
   * 根据查询条件统计金额
   * @param shopId
   * @return
   */
  public Double getSumBySearchCondition(Long shopId, BusinessAccountSearchConditionDTO searchConditionDTO);

  /**
   *  根据 店面ID和 营业类型 取得营业分类
   * @param shopId
   * @param itemType
   * @return
   */
  public List<BusinessCategoryDTO> getBusinessCategoryByItemType(Long shopId, String itemType);

  /**
   * 根据营业记账ID 逻辑删除营业记账
   * @param id
   * @return
   */
  public BusinessAccountDTO deleteBusinessAccountById(Long id);


  /**
   * 获取营业外记账统计
   * @param shopId
   * @param businessCategoryId
   * @param statYear
   * @param statMonth
   * @param statDay
   * @param statType
   * @param moneyCategory
   * @return
   */
  public BusinessCategoryStatDTO getBusinessCategoryStat(Long shopId,Long businessCategoryId,Long statYear,Long statMonth,Long statDay,BusinessCategoryStatType statType,MoneyCategory moneyCategory);

  /**
   * 营业外记账统计
   * @param businessAccountDTO
   * @param calculateType
   */
  public void businessCategoryStatByDTO(BusinessAccountDTO businessAccountDTO,CalculateType calculateType);

  /**
   * 保存营业外分类的business_category
   * @param accountDTO
   */
  public void saveOrUpdateBusinessCategoryFromDTO(BusinessAccountDTO accountDTO);

  /**
   * 营业统计获取营业外记账统计数据
   * @param shopId
   * @param statYear
   * @param statMonth
   * @param statDay
   */
  public List<BusinessCategoryStatDTO> getBusinessCategoryStatForBusinessStat(Long shopId,Long statYear,Long statMonth,Long statDay);

  public List<BusinessCategoryDTO> getBusinessCategoryLikeItemName(Long shopId,String name);


  /**
   * 营业外记账员工业绩统计
   * @param statShopId
   */
  public void assistantStatBusinessAccountStat(Long statShopId);
}


