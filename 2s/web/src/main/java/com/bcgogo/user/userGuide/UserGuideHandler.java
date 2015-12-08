package com.bcgogo.user.userGuide;

import com.bcgogo.common.Pair;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.cache.UserGuideCacheManager;
import com.bcgogo.user.dto.userGuide.UserGuideDTO;
import com.bcgogo.user.dto.userGuide.UserGuideStepDTO;
import com.bcgogo.user.service.IUserGuideService;
import com.bcgogo.user.userGuide.validator.UserGuideValidator;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-3-7
 * Time: 下午4:24
 */
@Component
public class UserGuideHandler {
  @Autowired
  private UserGuideValidator validator;
  public static final Logger LOG = LoggerFactory.getLogger(UserGuideHandler.class);


  /**
   * 获取next step
   */
  public UserGuideDTO getNextStepByShopIdStepName(long shopVersionId, long shopId, long userId, String flowName, String stepName, String... excludeFlowName) {
    UserGuideDTO userGuideDTO = new UserGuideDTO();
    Set<String> excludeFlowNameSet = new HashSet<String>();
    if (!ArrayUtils.isEmpty(excludeFlowName)) {
      Collections.addAll(excludeFlowNameSet, excludeFlowName);
    }
    try {
      if (StringUtil.isNotEmpty(stepName)) {
        userGuideDTO = validateCurrentStep(shopVersionId, shopId, userId, flowName, stepName, excludeFlowNameSet);
        long times = 0;
        List<Pair<String, String>> stack = new ArrayList<Pair<String, String>>();
        while (userGuideDTO != null && !userGuideDTO.isValidatorSuccess()) {
          times++;
          stack.add(new Pair<String, String>(userGuideDTO.getCurrentStep().getFlowName(), userGuideDTO.getCurrentStep().getName()));
          if (times > 100) {
            throw new Exception("UserGuideHandler timeout exception! stack" + stack.toString());
          }
          userGuideDTO = getUserNextFlow(shopVersionId, shopId, userId, userGuideDTO, true, excludeFlowNameSet);
        }
        if (userGuideDTO != null) {
          userGuideDTO.setContinueGuide(!userGuideDTO.getCurrentFlow().getName().equals(flowName));
        }
        return (userGuideDTO == null || !userGuideDTO.isValidatorSuccess()) ? null : userGuideDTO;
      }
    } catch (Exception e) {
      LOG.warn("UserGuideService.getNextStepByRequest,stepName:" + stepName + ",flowName:" + flowName);
      LOG.error(e.getMessage(), e);
    }
    return null;
  }

  //获取经过Validator的当前step
  public UserGuideDTO getHandledCurrentUserGuideStep(long shopVersionId, long shopId, long userId, String... excludeFlowName) throws Exception {
    UserGuideDTO userGuideDTO = new UserGuideDTO();
    IUserGuideService userGuideService = ServiceManager.getService(IUserGuideService.class);
    Pair<UserGuideStepDTO, Map<String, UserGuideStepDTO>> pair = userGuideService.getCurrentUserGuideStep(shopVersionId, userId, excludeFlowName);
    if (pair == null) {
      return null;
    }
    userGuideDTO.setCurrentFlow(UserGuideCacheManager.getCurrentUserGuideFlowByName(shopVersionId, pair.getKey().getFlowName()));
    userGuideDTO.setCurrentStep(pair.getKey());
    userGuideDTO.setNextStepList(new ArrayList<UserGuideStepDTO>(pair.getValue().values()));
    userGuideDTO = validator.validate(shopVersionId, shopId, userId, userGuideDTO, false, excludeFlowName);
    Set<String> excludeFlowNameSet = new HashSet<String>();
    if (!ArrayUtils.isEmpty(excludeFlowName)) {
      Collections.addAll(excludeFlowNameSet, excludeFlowName);
    }
    long times = 0;
    List<Pair<String, String>> stack = new ArrayList<Pair<String, String>>();
    while (userGuideDTO != null && !userGuideDTO.isValidatorSuccess()) {
      times++;
      stack.add(new Pair<String, String>(userGuideDTO.getCurrentStep().getFlowName(), userGuideDTO.getCurrentStep().getName()));
      if (times > 100) {
        throw new Exception("UserGuideHandler timeout exception! stack" + stack.toString());
      }
      userGuideDTO = getUserNextFlow(shopVersionId, shopId, userId, userGuideDTO, false, excludeFlowNameSet);
    }
    return (userGuideDTO == null || !userGuideDTO.isValidatorSuccess()) ? null : userGuideDTO;
  }

  private UserGuideDTO validateCurrentStep(long shopVersionId, long shopId, long userId, String flowName, String stepName, Set<String> excludeFlowNameSet) throws Exception {
    UserGuideDTO userGuideDTO = new UserGuideDTO();
    Pair<UserGuideStepDTO, Map<String, UserGuideStepDTO>> pair = UserGuideCacheManager.getUserGuideStepByName(stepName);
    if (pair == null) {
      LOG.warn("user guide step is empty,step name is {}.", stepName);
      return null;
    }
    userGuideDTO.setCurrentFlow(UserGuideCacheManager.getCurrentUserGuideFlowByName(shopVersionId, pair.getKey().getFlowName()));
    userGuideDTO.setCurrentStep(pair.getKey());
    userGuideDTO.setNextStepList(new ArrayList<UserGuideStepDTO>(pair.getValue().values()));
    userGuideDTO = validator.validate(shopVersionId, shopId, userId, userGuideDTO, true, excludeFlowNameSet.toArray(new String[excludeFlowNameSet.size()]));
    return userGuideDTO;
  }

  //当前step不满足条件 获取下一个flow
  private UserGuideDTO getUserNextFlow(long shopVersionId, long shopId, long userId, UserGuideDTO userGuideDTO, boolean needNext, Set<String> excludeFlowNameSet) throws Exception {
    IUserGuideService userGuideService = ServiceManager.getService(IUserGuideService.class);
    Pair<UserGuideStepDTO, Map<String, UserGuideStepDTO>> pair = userGuideService.getCurrentUserGuideStep(shopVersionId, userId, excludeFlowNameSet.toArray(new String[excludeFlowNameSet.size()]));
    if (pair == null) {
      return null;
    }
    userGuideDTO.setNextStep(pair.getKey());
    userGuideDTO.setCurrentFlow(UserGuideCacheManager.getCurrentUserGuideFlowByName(shopVersionId, pair.getKey().getFlowName()));
    userGuideDTO.setCurrentStep(pair.getKey());
    userGuideDTO.setNextStepList(new ArrayList<UserGuideStepDTO>(pair.getValue().values()));
    userGuideDTO.setValidatorSuccess(true);
    userGuideDTO = validator.validate(shopVersionId, shopId, userId, userGuideDTO, false, excludeFlowNameSet.toArray(new String[excludeFlowNameSet.size()]));
    if (!userGuideDTO.isValidatorSuccess()) excludeFlowNameSet.add(userGuideDTO.getCurrentFlow().getName());
    return userGuideDTO;
  }
}
