package com.bcgogo.user.dto.userGuide;

import com.bcgogo.BooleanEnum;
import com.bcgogo.cache.Cacheable;
import com.bcgogo.constant.MemcachePrefix;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-2-28
 * Time: 上午10:53
 */
public class UserGuideFlowDTO implements Serializable {
  private Long id;
  private String name;
  private String previousFlowName;
  private String nextFlowName;
  private String shopVersions;
  private BooleanEnum isEnabled;
  private String firstStepName;
  private BooleanEnum isHead;
  private BooleanEnum isTail;


  public BooleanEnum getEnabled() {
    return isEnabled;
  }

  public void setEnabled(BooleanEnum enabled) {
    isEnabled = enabled;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPreviousFlowName() {
    return previousFlowName;
  }

  public void setPreviousFlowName(String previousFlowName) {
    this.previousFlowName = previousFlowName;
  }

  public String getNextFlowName() {
    return nextFlowName;
  }

  public void setNextFlowName(String nextFlowName) {
    this.nextFlowName = nextFlowName;
  }

  public String getShopVersions() {
    return shopVersions;
  }

  public void setShopVersions(String shopVersions) {
    this.shopVersions = shopVersions;
  }

  public String getFirstStepName() {
    return firstStepName;
  }

  public void setFirstStepName(String firstStepName) {
    this.firstStepName = firstStepName;
  }

  public BooleanEnum getHead() {
    return isHead;
  }

  public void setHead(BooleanEnum head) {
    isHead = head;
  }

  public BooleanEnum getTail() {
    return isTail;
  }

  public void setTail(BooleanEnum tail) {
    isTail = tail;
  }

}
