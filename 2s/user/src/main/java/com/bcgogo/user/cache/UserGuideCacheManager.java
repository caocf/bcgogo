package com.bcgogo.user.cache;

import com.bcgogo.common.Pair;
import com.bcgogo.config.util.MemCacheAdapter;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.userGuide.UserGuideFlowDTO;
import com.bcgogo.user.dto.userGuide.UserGuideFlowsCached;
import com.bcgogo.user.dto.userGuide.UserGuideStepDTO;
import com.bcgogo.user.model.UserDaoManager;
import com.bcgogo.user.model.userGuide.UserGuideFlow;
import com.bcgogo.user.model.userGuide.UserGuideHistory;
import com.bcgogo.user.model.userGuide.UserGuideStep;
import com.bcgogo.utils.ArrayUtil;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-2-28
 * Time: 下午1:24
 */
public class UserGuideCacheManager {
  private static final Logger LOG = LoggerFactory.getLogger(UserGuideCacheManager.class);
  //step
  //key->name
  //value->self,children
  private static Map<String, Pair<UserGuideStepDTO, Map<String, UserGuideStepDTO>>>
      userGuideStepCached = new Hashtable<String, Pair<UserGuideStepDTO, Map<String, UserGuideStepDTO>>>();
  private static UserGuideFlowsCached userGuideFlowCached = new UserGuideFlowsCached();

  public static Long SYNC_INTERVAL = 600000L;

  /**
   * 获得当前step&children step
   *
   * @param stepName
   * @return
   * @throws Exception
   */
  public static Pair<UserGuideStepDTO, Map<String, UserGuideStepDTO>> getUserGuideStepByName(String stepName) throws Exception {
    if (StringUtils.isBlank(stepName)) throw new Exception("stepName is empty!");
    //初始化 userGuideStepCached
    if (MapUtils.isEmpty(userGuideStepCached)) {
      rebuildUserGuideStep();
    }
    return userGuideStepCached.get(stepName);
  }

  /**
   * 获得前一个 step
   *
   * @param stepName
   * @return
   * @throws Exception
   */
  public static UserGuideStepDTO getPreviousUserGuideStepByName(String stepName) throws Exception {
    Pair<UserGuideStepDTO, Map<String, UserGuideStepDTO>> pair = getUserGuideStepByName(stepName);
    if (pair == null) {
      LOG.warn("get UserGuideStep by name is null");
      return null;
    }
    UserGuideStepDTO self = pair.getKey();
    if (self == null || StringUtils.isBlank(self.getPreviousStep())) {
      LOG.warn("get UserGuideStep by name pair:{} is null.", pair);
      return null;
    }
    pair = getUserGuideStepByName(self.getPreviousStep());
    if (pair == null) {
      LOG.warn("get UserGuideStep by Previous name is null");
      return null;
    }
    return pair.getKey();
  }

  private static UserGuideFlowDTO getUserGuideFlow(long shopVersionId, String name) {
    //初始化 userGuideFlowCached
    if (MapUtils.isEmpty(userGuideFlowCached.getUserGuideFlows())) {
      rebuildUserGuideFlows();
    }
    UserGuideFlowDTO flowDTO = userGuideFlowCached.getUserGuideFlows().get(name);
    if (flowDTO == null) return null;
    if (flowDTO.getShopVersions().contains(String.valueOf(shopVersionId))) {
      return flowDTO;
    } else {
      if (StringUtils.isBlank(flowDTO.getNextFlowName())) return null;
      return getUserGuideFlow(shopVersionId, flowDTO.getNextFlowName());
    }
  }

  /**
   * current flow
   *
   * @param name
   * @return
   * @throws Exception
   */
  public static UserGuideFlowDTO getCurrentUserGuideFlowByName(long shopVersionId, String name) throws Exception {
    if (StringUtils.isBlank(name)) return null;
    name = name.trim();
    UserGuideFlowDTO flowDTO = getUserGuideFlow(shopVersionId, name);
    if (flowDTO == null) {
      LOG.warn("user guide flow name:{} is empty!", name);
      return flowDTO;
    }
    //检查是否需要更新
    if (System.currentTimeMillis() - userGuideFlowCached.getSyncTime() >= SYNC_INTERVAL) {
      Long value = (Long) MemCacheAdapter.get(userGuideFlowCached.assembleKey());
      if (value == null || value > userGuideFlowCached.getSyncTime()) {
        if (value == null) LOG.warn("memCache flow key is null,rebuild user guide.");
        //todo memCache 标记操作 先放在此处 等有了 setCurrentUserGuideFlow and setCurrentUserGuideStep 放入其中
        MemCacheAdapter.set(userGuideFlowCached.assembleKey(), System.currentTimeMillis());
        rebuildUserGuideFlows();
        flowDTO = getUserGuideFlow(shopVersionId, name);
        if (flowDTO == null) LOG.warn("新手指引{}:配置出错！", name);
      }
    }
    return flowDTO;
  }

  /**
   * Next Flow
   *
   * @param shopVersionId
   * @param name
   * @return
   * @throws Exception
   */
  public static UserGuideFlowDTO getNextUserGuideFlowByName(long shopVersionId, String name) throws Exception {
    UserGuideFlowDTO currentFlowDTO = getCurrentUserGuideFlowByName(shopVersionId, name);
    if (currentFlowDTO != null && StringUtil.isNotEmpty(currentFlowDTO.getNextFlowName())) {
      if (currentFlowDTO.getShopVersions().contains(String.valueOf(shopVersionId))) {
        return getCurrentUserGuideFlowByName(shopVersionId, currentFlowDTO.getNextFlowName());
      } else {
        return getNextUserGuideFlowByName(shopVersionId, currentFlowDTO.getNextFlowName());
      }
    } else {
      return null;
    }
  }

  /**
   * Previous Flow
   *
   * @param shopVersionId
   * @param name
   * @return
   * @throws Exception
   */
  public static UserGuideFlowDTO getPreviousUserGuideFlowByName(long shopVersionId, String name) throws Exception {
    UserGuideFlowDTO currentFlowDTO = getCurrentUserGuideFlowByName(shopVersionId, name);
    if (currentFlowDTO != null && StringUtil.isNotEmpty(currentFlowDTO.getPreviousFlowName())) {
      if (currentFlowDTO.getShopVersions().contains(String.valueOf(shopVersionId))) {
        return getCurrentUserGuideFlowByName(shopVersionId, currentFlowDTO.getNextFlowName());
      } else {
        return getPreviousUserGuideFlowByName(shopVersionId, currentFlowDTO.getNextFlowName());
      }
    }
    return null;
  }

  private static void rebuildUserGuideFlows() {
    if (LOG.isDebugEnabled()) LOG.debug("rebuild flow type:{} ......");
    userGuideFlowCached.getUserGuideFlows().clear();
    List<UserGuideFlow> userGuideFlows = ServiceManager.getService(UserDaoManager.class).getWriter().getAllUserGuideFlows();
    UserGuideFlowDTO flowDTO;
    if (CollectionUtil.isNotEmpty(userGuideFlows)) {
      for (UserGuideFlow userGuideFlow : userGuideFlows) {
        flowDTO = userGuideFlow.toDTO();
        userGuideFlowCached.getUserGuideFlows().put(userGuideFlow.getName(), flowDTO);
      }
    }
    userGuideFlowCached.setSyncTime(System.currentTimeMillis());
    rebuildUserGuideStep();
  }

  private static void rebuildUserGuideStep() {
    if (LOG.isDebugEnabled()) LOG.debug("rebuild step type......");
    userGuideStepCached.clear();
    List<UserGuideStep> userGuideStepList = ServiceManager.getService(UserDaoManager.class).getWriter().getUserGuideStepByName();
    Pair<UserGuideStepDTO, Map<String, UserGuideStepDTO>> pair;
    Map<String, UserGuideStepDTO> map;
    for (UserGuideStep userGuideStep : userGuideStepList) {
      if (userGuideStep == null) continue;
      pair = userGuideStepCached.get(userGuideStep.getName());
      if (pair == null) {
        pair = new Pair<UserGuideStepDTO, Map<String, UserGuideStepDTO>>();
        pair.setKey(userGuideStep.toDTO());
        map = new HashMap<String, UserGuideStepDTO>();
        pair.setValue(map);
        userGuideStepCached.put(userGuideStep.getName(), pair);
      }
    }
    for (UserGuideStep userGuideStep : userGuideStepList) {
      if (userGuideStep == null) continue;
      if (StringUtils.isBlank(userGuideStep.getPreviousStep())) continue;
      pair = userGuideStepCached.get(userGuideStep.getPreviousStep());
      if (pair != null) {
        pair.getValue().put(userGuideStep.getName(), userGuideStep.toDTO());
      }
    }
  }

  public static UserGuideHistory getLatestUserGuideFlow(long shopVersionId, List<UserGuideHistory> historyList, String... excludeFlowName) throws Exception {
    if (CollectionUtil.isEmpty(historyList)) return null;
    if (!ArrayUtil.isEmpty(excludeFlowName)) {
      Iterator<UserGuideHistory> iterator = historyList.iterator();
      UserGuideHistory history;
      while (iterator.hasNext()) {
        history = iterator.next();
        for (String str : excludeFlowName) {
          if (history.getFlowName().equals(str)){
            iterator.remove();
            break;
          }
        }
      }
    }
    if (CollectionUtil.isEmpty(historyList)) return null;
    if (MapUtils.isEmpty(userGuideFlowCached.getUserGuideFlows())) {
      rebuildUserGuideFlows();
    }
    Map<String, UserGuideFlowDTO> map = userGuideFlowCached.getUserGuideFlows();
    UserGuideFlowDTO flowDTO = map.get("CONTRACT_SUPPLIER_GUIDE"); //head flow
    while (flowDTO != null) {
      for (UserGuideHistory history : historyList) {
        if (flowDTO.getName().equals(history.getFlowName())) {
          return history;
        }
      }
      flowDTO = map.get(flowDTO.getNextFlowName());
    }
    return null;
  }

  public static void unitTest() {
    String unitTest = System.getProperty("unit.test");
    if (unitTest != null && unitTest.equals("true")) {
      rebuildUserGuideFlows();
    }
  }
}