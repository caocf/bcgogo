package com.bcgogo.utils;

import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.product.dto.ProductLocalInfoDTO;
import com.bcgogo.txn.dto.BcgogoOrderItemDto;
import com.bcgogo.txn.dto.ProductHistoryDTO;
import org.apache.commons.lang.StringUtils;

/**
 * Created by IntelliJ IDEA.
 * User: Qiuxinyu
 * Date: 12-5-22
 * Time: 下午3:28
 * To change this template use File | Settings | File Templates.
 */
public class UnitUtil {

  //存在两个单位，单位换算比例，且使用的是库存大单位的时候return true
  public static boolean isStorageUnit(String unit, ProductDTO productDTO) {
    if (productDTO!=null && StringUtils.isNotBlank(productDTO.getStorageUnit())
        && StringUtils.isNotBlank(productDTO.getSellUnit())
        && !productDTO.getStorageUnit().equals(productDTO.getSellUnit())
        && productDTO.getStorageUnit().equals(unit)
        && productDTO.getRate() != null && !(new Long(0l).equals(productDTO.getRate()))) {
      return true;
    } else {
      return false;
    }
  }

    //存在两个单位，单位换算比例，且使用的是库存大单位的时候return true
  public static boolean isStorageUnit(String unit, ProductLocalInfoDTO productLocalInfoDTO) {
    if(productLocalInfoDTO == null){
      return  false;
    }
    if (StringUtils.isNotBlank(productLocalInfoDTO.getStorageUnit())
        && StringUtils.isNotBlank(productLocalInfoDTO.getSellUnit())
        && !productLocalInfoDTO.getStorageUnit().equals(productLocalInfoDTO.getSellUnit())
        && productLocalInfoDTO.getStorageUnit().equals(unit)
        && productLocalInfoDTO.getRate() != null && !(new Long(0l).equals(productLocalInfoDTO.getRate()))) {
      return true;
    } else {
      return false;
    }
  }

  public static boolean isStorageUnit(String unit, ProductHistoryDTO productHistoryDTO) {
    if (StringUtils.isNotBlank(productHistoryDTO.getStorageUnit())
        && StringUtils.isNotBlank(productHistoryDTO.getSellUnit())
        && !productHistoryDTO.getStorageUnit().equals(productHistoryDTO.getSellUnit())
        && productHistoryDTO.getStorageUnit().equals(unit)
        && productHistoryDTO.getRate() != null && !(new Long(0l).equals(productHistoryDTO.getRate()))) {
      return true;
    } else {
      return false;
    }
  }

  //存在两个单位，单位换算比例，且使用的是库存大单位的时候return true
  public static boolean isStorageUnit(String unit, BcgogoOrderItemDto itemDto) {
    if (itemDto!=null &&StringUtils.isNotBlank(itemDto.getStorageUnit())
        && StringUtils.isNotBlank(itemDto.getSellUnit())
        && !itemDto.getStorageUnit().equals(itemDto.getSellUnit())
        && itemDto.getStorageUnit().equals(unit)
        && itemDto.getRate() != null && !(new Long(0l).equals(itemDto.getRate()))) {
      return true;
    } else {
      return false;
    }
  }

//     UNIT_IS_Blank ="0";                  //库存单位，销售单位均为空时
//     UNIT_IS_PRODUCT_STOAGE_UNIT = "1";//同时存在库存单位，销售单位，且使用库存大单位状态
//     UNIT_IS_PRODUCT_SELL_UNIT = "2";//同时存在库存单位，销售单位，且使用销售小单位
//     UNIT_STATUS_ERROR ="3"         //用户使用单位异常
  @Deprecated
  public static String getUnitStatus(String unit, ProductDTO productDTO) {
    if (StringUtils.isBlank(productDTO.getStorageUnit())
        && StringUtils.isBlank(productDTO.getSellUnit())
        && productDTO.getRate() == null) {
      return TxnConstant.UnitStatus.UNIT_IS_BLANK;
    } else if (StringUtils.isNotBlank(productDTO.getStorageUnit())
        && StringUtils.isNotBlank(productDTO.getSellUnit())
        && !productDTO.getStorageUnit().equals(productDTO.getSellUnit())
        && productDTO.getStorageUnit().equals(unit)
        && productDTO.getRate() != null && !(new Long(0l).equals(productDTO.getRate()))) {
      return TxnConstant.UnitStatus.UNIT_IS_PRODUCT_STOAGE_UNIT;
    } else if (StringUtils.isNotBlank(productDTO.getStorageUnit())
        && StringUtils.isNotBlank(productDTO.getSellUnit())
        && !productDTO.getStorageUnit().equals(productDTO.getSellUnit())
        && productDTO.getSellUnit().equals(unit)
        && productDTO.getRate() != null && !(new Long(0l).equals(productDTO.getRate()))){
      return TxnConstant.UnitStatus.UNIT_IS_PRODUCT_SELL_UNIT;
    }else {
      return TxnConstant.UnitStatus.UNIT_STATUS_ERROR;
    }
  }

  /**
   *
   * @param productLocalInfoDTO  需要判断的productLocalInfoDTO
   * @param formProductDTO        前台传过来的productDTO
   * @return
   */
  public static boolean isAddFirstUnit(ProductLocalInfoDTO productLocalInfoDTO, ProductDTO formProductDTO) {
    boolean isAddFirstUnit = false;
    if(productLocalInfoDTO != null && formProductDTO != null){
       if(StringUtil.isEmpty(productLocalInfoDTO.getSellUnit())
           && StringUtil.isEmpty(productLocalInfoDTO.getStorageUnit())
           && StringUtil.isNotEmpty(formProductDTO.getSellUnit())
           && StringUtil.isNotEmpty(formProductDTO.getStorageUnit())){
         isAddFirstUnit = true;
       }
    }
    return isAddFirstUnit;
  }
}