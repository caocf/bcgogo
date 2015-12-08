package com.bcgogo.user.userGuide.validator;

import com.bcgogo.BooleanEnum;
import com.bcgogo.common.Pair;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.userGuide.UserGuideDTO;
import com.bcgogo.user.dto.userGuide.UserGuideStepDTO;
import com.bcgogo.user.service.IUserGuideService;
import com.bcgogo.user.userGuide.UserGuideHandler;
import com.bcgogo.utils.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-3-8
 * Time: 上午10:07
 */
public abstract class AbstractValidator {
  protected abstract UserGuideDTO validate(long shopVersionId, long shopId, long userId, UserGuideDTO userGuideDTO, boolean needNext, String... excludeFlowName) throws Exception;

  protected UserGuideDTO defaultStep(long shopVersionId, long userId, UserGuideDTO userGuideDTO, long shopId, String... excludeFlowName) throws Exception {
    IUserGuideService userGuideService = ServiceManager.getService(IUserGuideService.class);
    if (BooleanEnum.TRUE == userGuideDTO.getCurrentStep().getTail()) {
      userGuideService.updateCurrentUserGuideStepFinished(userId, userGuideDTO.getCurrentFlow().getName(), userGuideDTO.getCurrentStep().getName());
      Pair<UserGuideStepDTO, Map<String, UserGuideStepDTO>> pair = userGuideService.getCurrentUserGuideStep(shopVersionId, userId, excludeFlowName);
      if (pair == null) {
        return null;
      }
      userGuideDTO.setValidatorCount(userGuideDTO.getValidatorCount() + 1);
      if (userGuideDTO.getValidatorCount() > 30) throw new Exception("递归异常");
      userGuideDTO.setCurrentStep(pair.getKey());
      userGuideDTO = ServiceManager.getService(UserGuideValidator.class).validate(shopVersionId, shopId, userId, userGuideDTO, true, excludeFlowName);
    } else {
      //分叉 上面单独处理
      if (CollectionUtil.isNotEmpty(userGuideDTO.getNextStepList())) {
        userGuideDTO.setNextStep(userGuideDTO.getNextStepList().get(0));
        //更新当前step history
        userGuideService.updateCurrentUserGuideStepFinished(userId, userGuideDTO.getCurrentFlow().getName(), userGuideDTO.getCurrentStep().getName());
      }
    }
    return userGuideDTO;
  }

}
