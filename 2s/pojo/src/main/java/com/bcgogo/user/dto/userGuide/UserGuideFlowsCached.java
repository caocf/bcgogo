package com.bcgogo.user.dto.userGuide;

import com.bcgogo.cache.Cacheable;
import com.bcgogo.constant.MemcachePrefix;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-3-4
 * Time: 上午11:59
 */
public class UserGuideFlowsCached implements Serializable, Cacheable {
  private Map<String, UserGuideFlowDTO> userGuideFlows = new Hashtable<String, UserGuideFlowDTO>();
  private Long syncTime;

  public Map<String, UserGuideFlowDTO> getUserGuideFlows() {
    return userGuideFlows;
  }

  public void setUserGuideFlows(Map<String, UserGuideFlowDTO> userGuideFlows) {
    this.userGuideFlows = userGuideFlows;
  }

  public Long getSyncTime() {
    return syncTime;
  }

  public void setSyncTime(Long syncTime) {
    this.syncTime = syncTime;
  }

  @Override
  public String assembleKey() {
    return MemcachePrefix.userGuideFlows.getValue();
  }
}
