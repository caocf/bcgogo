package com.bcgogo.product;

import com.bcgogo.enums.Product.ProductRelevanceStatus;
import com.bcgogo.enums.ProductModifyFields;
import com.bcgogo.enums.ProductModifyTables;
import com.bcgogo.product.dto.ProductDTO;
import com.bcgogo.txn.dto.ProductModifyLogDTO;
import com.bcgogo.utils.ArrayUtil;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 14-2-27
 * Time: 上午10:49
 * To change this template use File | Settings | File Templates.
 */
public class ProductRelevanceHelper {

  public static Boolean existRelevanceProperty(ProductModifyLogDTO... modifyLogDTOs){
    if(ArrayUtil.isEmpty(modifyLogDTOs)) return false;
    for(ProductModifyLogDTO modifyLogDTO:modifyLogDTOs){
      if(!ProductModifyTables.PRODUCT.equals(modifyLogDTO.getTableName())&&!ProductModifyTables.PRODUCT_LOCAL_INFO.equals(modifyLogDTO.getTableName())){
        continue;
      }
      if(ProductModifyFields.commodityCode.equals(modifyLogDTO.getFieldName())||ProductModifyFields.name.equals(modifyLogDTO.getFieldName())||ProductModifyFields.brand.equals(modifyLogDTO.getFieldName())
        ||ProductModifyFields.spec.equals(modifyLogDTO.getFieldName())||ProductModifyFields.model.equals(modifyLogDTO.getFieldName())||ProductModifyFields.productVehicleBrand.equals(modifyLogDTO.getFieldName())
        ||ProductModifyFields.productVehicleModel.equals(modifyLogDTO.getFieldName())||ProductModifyFields.sellUnit.equals(modifyLogDTO.getFieldName())||ProductModifyFields.storageUnit.equals(modifyLogDTO.getFieldName()))
        return true;
    }
    return false;
  }

  public static Boolean existRelevancePropertyModify(ProductRelevanceStatus relevanceStatus,ProductModifyLogDTO... modifyLogDTOs){
    return (ProductRelevanceStatus.YES.equals(relevanceStatus)
      ||ProductRelevanceStatus.UN_CHECKED.equals(relevanceStatus))&&existRelevanceProperty(modifyLogDTOs);
  }
}
