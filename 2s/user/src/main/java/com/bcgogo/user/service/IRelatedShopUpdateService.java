package com.bcgogo.user.service;

import com.bcgogo.common.Result;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.enums.ExeStatus;
import com.bcgogo.user.dto.RelatedShopUpdateTaskDTO;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-8-8
 * Time: 下午3:13
 */
@Component
public interface IRelatedShopUpdateService {

  boolean isNeedToCreateTask(ShopDTO lastShopDTO, ShopDTO newShopDTO);

  //创建Task
  RelatedShopUpdateTaskDTO createRelatedShopUpdateTask(Long shopId) throws Exception;

  RelatedShopUpdateTaskDTO getFirstRelatedShopUpdateTaskDTO(ExeStatus exeStatus);

  void updateRelatedShopUpdateTaskStatus(RelatedShopUpdateTaskDTO relatedShopUpdateTaskDTO, ExeStatus start);

  //初始化
  Result initShopCustomerSupplierSync() throws Exception;

}
