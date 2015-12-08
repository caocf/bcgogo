package com.bcgogo.user.userGuide.validator;

import com.bcgogo.enums.txn.message.ReceiverStatus;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.messageCenter.INoticeService;
import com.bcgogo.user.dto.userGuide.UserGuideDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-3-9
 * Time: 上午12:28
 * To change this template use File | Settings | File Templates.
 */
@Component
public class ContractMessageNoticeValidator extends AbstractValidator {
  private static final Logger LOG = LoggerFactory.getLogger(ContractMessageNoticeValidator.class);

  @Override
  protected UserGuideDTO validate(long shopVersionId, long shopId, long userId, UserGuideDTO userGuideDTO, boolean needNext, String... excludeFlowName) throws Exception {
    //前三步需要检查
    if ("CONTRACT_MESSAGE_NOTICE_BEGIN,CONTRACT_MESSAGE_NOTICE_ENTER_MESSAGE".contains(userGuideDTO.getCurrentStep().getName()) && !hasNotices(shopId, userId)) {
      userGuideDTO.setValidatorSuccess(false);
      return userGuideDTO;
    }
    if (!needNext) return userGuideDTO;
    return defaultStep(shopVersionId, userId, userGuideDTO, shopId, excludeFlowName);
  }

  /**
   * 新手指引 需要重新定义  目前 新手指引已经被关闭
   * @param shopId
   * @param userId
   * @return
   */
  private boolean hasNotices(long shopId, long userId) {
    return false;
  }

}
