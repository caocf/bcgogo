package com.bcgogo.user.userGuide.validator;

import com.bcgogo.config.service.IApplyService;
import com.bcgogo.enums.shop.InviteCountStatus;
import com.bcgogo.enums.shop.InviteStatus;
import com.bcgogo.enums.shop.InviteType;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.userGuide.UserGuideDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-3-8
 * Time: 上午10:05
 */
@Component
public class SupplierApplyGuideValidator extends AbstractValidator {
  private static final Logger LOG = LoggerFactory.getLogger(SupplierApplyGuideValidator.class);

  @Override
  protected UserGuideDTO validate(long shopVersionId, long shopId, long userId, UserGuideDTO userGuideDTO, boolean needNext, String... excludeFlowName) throws Exception {
    //前三步需要检查
    if ("SUPPLIER_APPLY_GUIDE_BEGIN,SUPPLIER_APPLY_GUIDE_ENTER_MESSAGE".contains(userGuideDTO.getCurrentStep().getName()) && !hasSupplierApplies(shopId)) {
      userGuideDTO.setValidatorSuccess(false);
      return userGuideDTO;
    }
    if (!needNext) return userGuideDTO;
    return defaultStep(shopVersionId, userId, userGuideDTO, shopId, excludeFlowName);
  }

  private boolean hasSupplierApplies(long shopId) {
    IApplyService applyService = ServiceManager.getService(IApplyService.class);
//    Map<InviteCountStatus, Integer> applyMap = applyService.countShopRelationInvites(shopId);
    return false;
  }

}
