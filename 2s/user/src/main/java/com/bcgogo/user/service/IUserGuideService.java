package com.bcgogo.user.service;

import com.bcgogo.common.Pair;
import com.bcgogo.common.Result;
import com.bcgogo.user.dto.userGuide.UserGuideDTO;
import com.bcgogo.user.dto.userGuide.UserGuideStepDTO;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-2-28
 * Time: 上午10:36
 */
public interface IUserGuideService {
  Pair<UserGuideStepDTO, Map<String, UserGuideStepDTO>> getCurrentUserGuideStep(long shopVersionId, long userId, String... excludeFlowName) throws Exception;

  void updateCurrentUserGuideStepFinished(long userId, String flowName, String currentStep) throws Exception;

  void updateCurrentUserGuideFlowFinished(long userId, String flowName) throws Exception;

  Result skipUserGuideFlow(long shopVersionId, long userId, String name) throws Exception;

  Result finishedUserGuideFlow(long shopVersionId, long userId, String name) throws Exception;

  /**
   * used by test
   *
   * @param list step list
   */
  void saveUserGuideStepList(List<UserGuideStepDTO> list);

  //更一个新用户添加userGuide
  void addUserGuideToNewUser(Long userId);

  //以后不再提醒
  void notRemind(Long shopId, Long userId, String flowName);
}
