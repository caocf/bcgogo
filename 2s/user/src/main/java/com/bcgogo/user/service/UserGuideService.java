package com.bcgogo.user.service;

import com.bcgogo.BooleanEnum;
import com.bcgogo.common.Pair;
import com.bcgogo.common.Result;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.util.MemCacheAdapter;
import com.bcgogo.constant.MemcachePrefix;
import com.bcgogo.enums.YesNo;
import com.bcgogo.enums.user.UserType;
import com.bcgogo.enums.user.userGuide.Status;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.cache.UserGuideCacheManager;
import com.bcgogo.user.dto.userGuide.UserGuideStepDTO;
import com.bcgogo.user.model.UserDaoManager;
import com.bcgogo.user.model.UserWriter;
import com.bcgogo.user.model.permission.User;
import com.bcgogo.user.model.userGuide.UserGuideFlow;
import com.bcgogo.user.model.userGuide.UserGuideHistory;
import com.bcgogo.user.model.userGuide.UserGuideStep;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-2-28
 * Time: 上午11:12
 */
@Component
public class UserGuideService implements IUserGuideService {
  @Autowired
  private UserDaoManager userDaoManager;
  public static final Logger LOG = LoggerFactory.getLogger(UserGuideService.class);

  public Pair<UserGuideStepDTO, Map<String, UserGuideStepDTO>> getCurrentUserGuideStep(long shopVersionId, long userId, String... excludeFlowName) throws Exception {
    //获得memcache的history数据
    String key = getKey(userId);
    List<UserGuideHistory> historyList = getWaitingUserGuideHistory(userId);
    UserGuideHistory history;
    //为空从数据库中拿
    UserWriter writer = userDaoManager.getWriter();
    if (CollectionUtil.isEmpty(historyList)) {
      Object status = writer.begin();
      try {
        updateUserFinishUserGuide(userId, writer);
        writer.commit(status);
      } finally {
        writer.rollback(status);
      }
      MemCacheAdapter.delete(key);
      return null;
    } else {
      //遍历出finish的flow
      Iterator<UserGuideHistory> iterator = historyList.iterator();
      while (iterator.hasNext()) {
        history = iterator.next();
        if (Status.WAITING != history.getStatus()) iterator.remove();
      }
      if (CollectionUtil.isEmpty(historyList)) {
        Object status = writer.begin();
        try {
          updateUserFinishUserGuide(userId, writer);
          writer.commit(status);
        } finally {
          writer.rollback(status);
        }
        return null;
      }
    }
    history = UserGuideCacheManager.getLatestUserGuideFlow(shopVersionId, historyList, excludeFlowName);
    if (history == null) return null;
    return UserGuideCacheManager.getUserGuideStepByName(history.getCurrentStep());
  }

  public void updateCurrentUserGuideStepFinished(long userId, String flowName, String currentStep) throws Exception {
    if (StringUtil.isEmpty(flowName)) {
      LOG.warn("flowName is null.");
      return;
    }
    if (StringUtil.isEmpty(currentStep)) {
      LOG.warn("stepName is null.");
      return;
    }
    Pair<UserGuideStepDTO, Map<String, UserGuideStepDTO>> pair = UserGuideCacheManager.getUserGuideStepByName(currentStep);
    if (pair == null) {
      LOG.warn("user guide step is empty,step name is {}.", currentStep);
      return;
    }
    UserGuideStepDTO stepDTO = pair.getKey();
    String memCacheKey = getKey(userId);
    List<UserGuideHistory> historyList = getWaitingUserGuideHistory(userId);
    //该用户已经完成指引
    if (CollectionUtil.isEmpty(historyList)) {
      LOG.warn("user[id:{}] has finished user guild.", userId);
      MemCacheAdapter.delete(memCacheKey);
      return;
    }
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      UserGuideHistory history = writer.getUserGuideHistoryByFlowName(userId, flowName);
      if (history == null) return;
      //该用户 进入某流程的最后一步
      if (BooleanEnum.TRUE == stepDTO.getTail()) {
        if (CollectionUtil.isNotEmpty(historyList)) {
          Iterator<UserGuideHistory> iterator = historyList.iterator();
          while (iterator.hasNext()) {
            if (NumberUtil.isEqual(iterator.next().getId(), history.getId())) {
              iterator.remove();
              break;
            }
          }
        }
        history.setStatus(Status.FINISHED);
        //如果history全部finish
        if (CollectionUtil.isEmpty(historyList)) {
          updateUserFinishUserGuide(history.getUserId(), writer);
          MemCacheAdapter.delete(memCacheKey);
        } else {
          MemCacheAdapter.set(memCacheKey, historyList);
        }
      } else {
        if (MapUtils.isEmpty(pair.getValue())) {
          LOG.warn("user guide step should has child but is empty,step name is {}.", currentStep);
          return;
        }
        StringBuilder steps = new StringBuilder();
        //todo 多个下一步
        for (UserGuideStepDTO userGuideStepDTO : pair.getValue().values()) {
          steps.append(userGuideStepDTO.getName()).append(",");
        }
        history.setCurrentStep(StringUtil.subString(steps.toString()));
        Iterator<UserGuideHistory> iterator = historyList.iterator();
        while (iterator.hasNext()) {
          if (NumberUtil.isEqual(iterator.next().getId(), history.getId())) {
            iterator.remove();
            break;
          }
        }
        historyList.add(history);
        MemCacheAdapter.set(memCacheKey, historyList);
      }
      writer.update(history);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void updateCurrentUserGuideFlowFinished(long userId, String flowName) throws Exception {
    if (StringUtil.isEmpty(flowName)) {
      LOG.warn("flowName is null.");
      return;
    }
    String memCacheKey = getKey(userId);
    List<UserGuideHistory> historyList = getWaitingUserGuideHistory(userId);
    //该用户已经完成指引
    if (CollectionUtil.isEmpty(historyList)) {
      LOG.warn("user[id:{}] has finished user guild.", userId);
      MemCacheAdapter.delete(memCacheKey);
      return;
    }
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      UserGuideHistory history = writer.getUserGuideHistoryByFlowName(userId, flowName);
      if (history == null) return;
      //该用户 进入某流程的最后一步
      if (CollectionUtil.isNotEmpty(historyList)) {
        Iterator<UserGuideHistory> iterator = historyList.iterator();
        while (iterator.hasNext()) {
          if (NumberUtil.isEqual(iterator.next().getId(), history.getId())) {
            iterator.remove();
            break;
          }
        }
      }
      history.setStatus(Status.FINISHED);
      //如果history全部finish
      if (CollectionUtil.isEmpty(historyList)) {
        updateUserFinishUserGuide(history.getUserId(), writer);
        MemCacheAdapter.delete(memCacheKey);
      } else {
        MemCacheAdapter.set(memCacheKey, historyList);
      }
      writer.update(history);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }


  public Result skipUserGuideFlow(long shopVersionId, long userId, String name) throws Exception {
    UserDaoManager userDaoManager = ServiceManager.getService(UserDaoManager.class);
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      UserGuideHistory history = writer.getUserGuideHistoryByFlowName(userId, name);
      if (history == null) return new Result("跳出指引失败", "引导历史为空", false);
      history.setStatus(Status.SKIPPED);
      writer.update(history);
      writer.commit(status);
      removeUserGuideFlowByName(userId, name);
    } finally {
      writer.rollback(status);
    }
    return new Result(true);
  }


  public Result finishedUserGuideFlow(long shopVersionId, long userId, String name) throws Exception {
    UserDaoManager userDaoManager = ServiceManager.getService(UserDaoManager.class);
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      UserGuideHistory history = writer.getUserGuideHistoryByFlowName(userId, name);
      if (history == null) return new Result("跳出指引失败", "引导历史为空", false);
      history.setStatus(Status.FINISHED);
      writer.update(history);
      writer.commit(status);
      removeUserGuideFlowByName(userId, name);
    } finally {
      writer.rollback(status);
    }
    return new Result(true);
  }

  private void removeUserGuideFlowByName(long userId, String name) {
    List<UserGuideHistory> historyList = getWaitingUserGuideHistory(userId);
    String memCacheKey = getKey(userId);
    Iterator<UserGuideHistory> iterator = historyList.iterator();
    while (iterator.hasNext()) {
      UserGuideHistory history = iterator.next();
      if (history.getFlowName().equals(name)) iterator.remove();
    }
    MemCacheAdapter.set(memCacheKey, historyList);
  }

  private void unitTest(long userId) {
    UserDaoManager userDaoManager = ServiceManager.getService(UserDaoManager.class);
    String unitTest = System.getProperty("unit.test");
    if (unitTest != null && unitTest.equals("true")) {
      UserWriter writer = userDaoManager.getWriter();
      String memCacheKey = getKey(userId);
      MemCacheAdapter.set(memCacheKey, writer.getWaitingUserGuideHistory(userId));
    }
  }

  private List<UserGuideHistory> getWaitingUserGuideHistory(long userId) {
    UserDaoManager userDaoManager = ServiceManager.getService(UserDaoManager.class);
    String memCacheKey = getKey(userId);
    List<UserGuideHistory> historyList = (List<UserGuideHistory>) MemCacheAdapter.get(memCacheKey);
    if (CollectionUtil.isEmpty(historyList)) {
      UserWriter writer = userDaoManager.getWriter();
      historyList = writer.getWaitingUserGuideHistory(userId);
      MemCacheAdapter.set(memCacheKey, historyList);
    }
    return historyList;
  }

  private void updateUserFinishUserGuide(long userId, UserWriter writer) {
    LOG.info("user:{} has finish user guide.", userId);
    User user = writer.getById(User.class, userId);
    user.setFinishUserGuide(YesNo.YES);
    writer.update(user);
  }

  private String getKey(long userId) {
    return MemcachePrefix.userGuide.getValue() + String.valueOf(userId);
  }
  @Override
  public void saveUserGuideStepList(List<UserGuideStepDTO> list) {
    if (CollectionUtil.isEmpty(list)) return;
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      for (UserGuideStepDTO dto : list) {
        UserGuideStep userGuideStep = new UserGuideStep();
        userGuideStep.fromDTO(dto);
        writer.saveOrUpdate(userGuideStep);
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void addUserGuideToNewUser(Long userId) {
    if (userId == null) {
      return;
    }
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      List<UserGuideFlow> userGuideFlows = writer.getAllUserGuideFlows();
      IConfigService configService = ServiceManager.getService(IConfigService.class);
      User user = writer.getById(User.class, userId);
      if (UserType.SYSTEM_CREATE.equals(user.getUserType())) {
        ShopDTO shopDTO = configService.getShopById(user.getShopId());
        if (shopDTO != null && shopDTO.getShopVersionId() != null) {
          UserGuideHistory history;
          boolean flag = false;
          for (UserGuideFlow userGuideFlow : userGuideFlows) {
            if (StringUtil.isEmpty(userGuideFlow.getShopVersions())) {
              continue;
            }
            if (userGuideFlow.getShopVersions().contains(String.valueOf(shopDTO.getShopVersionId()))) {
              history = writer.getUserGuideHistoryByFlowName(user.getId(), userGuideFlow.getName());
              if (history != null) {
                continue;
              }
              history = new UserGuideHistory();
              history.setStatus(com.bcgogo.enums.user.userGuide.Status.WAITING);
              history.setUserId(user.getId());
              history.setCurrentStep(userGuideFlow.getFirstStepName());
              history.setFlowName(userGuideFlow.getName());
              flag = true;
              writer.save(history);
            }
          }
          if (flag) {
            user.setHasUserGuide(YesNo.YES);
            user.setFinishUserGuide(YesNo.NO);
            writer.update(user);
            writer.commit(status);
          }
        }
      }
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void notRemind(Long shopId, Long userId, String flowName) {
    if (userId == null) {
        return;
      }
      UserWriter writer = userDaoManager.getWriter();
      Object status = writer.begin();
      try {
        User user = writer.getById(User.class, userId);
        if(user != null){
          user.setFinishUserGuide(YesNo.YES);
          writer.update(user);
          List<UserGuideHistory> userGuideHistories = writer.getWaitingUserGuideHistory(userId);
          List<UserGuideHistory> cacheUserGuideHistory = new ArrayList<UserGuideHistory>();
          if(CollectionUtil.isNotEmpty(userGuideHistories)){
            for(UserGuideHistory userGuideHistory : userGuideHistories){
              if(StringUtil.isNotEmpty(flowName) && flowName.equals(userGuideHistory.getFlowName())){
                cacheUserGuideHistory.add(userGuideHistory);
              } else {
                userGuideHistory.setStatus(Status.SKIPPED);
                writer.update(userGuideHistory);
              }
            }
          }
          writer.commit(status);
          String memCacheKey = getKey(userId);
          MemCacheAdapter.set(memCacheKey, cacheUserGuideHistory);
        }
      } finally {
        writer.rollback(status);
      }
  }
}
