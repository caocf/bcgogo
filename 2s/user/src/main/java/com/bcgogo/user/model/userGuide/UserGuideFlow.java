package com.bcgogo.user.model.userGuide;

import com.bcgogo.BooleanEnum;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.user.dto.userGuide.UserGuideFlowDTO;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-2-28
 * Time: 上午9:32
 */
@Entity
@Table(name = "user_guide_flow")
public class UserGuideFlow extends LongIdentifier {
  private String name;
  private String previousFlowName;
  private String nextFlowName;
  private String shopVersions;
  private BooleanEnum isEnabled;
  private String firstStepName;
  private BooleanEnum isHead;
  private BooleanEnum isTail;

  public UserGuideFlowDTO toDTO() {
    UserGuideFlowDTO dto = new UserGuideFlowDTO();
    dto.setId(this.getId());
    dto.setName(this.getName());
    dto.setPreviousFlowName(this.getPreviousFlowName());
    dto.setNextFlowName(this.getNextFlowName());
    dto.setShopVersions(this.getShopVersions());
    dto.setEnabled(this.getEnabled());
    dto.setHead(this.getHead());
    dto.setTail(this.getTail());
    return dto;
  }

  public void fromDTO(UserGuideFlowDTO dto) {
    this.setId(dto.getId());
    this.setName(dto.getName());
    this.setPreviousFlowName(dto.getPreviousFlowName());
    this.setNextFlowName(dto.getNextFlowName());
    this.setShopVersions(dto.getShopVersions());
    this.setEnabled(dto.getEnabled());
    this.setHead(dto.getHead());
    this.setTail(dto.getTail());
  }


  @Column(name = "name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "previous_flow_name")
  public String getPreviousFlowName() {
    return previousFlowName;
  }

  public void setPreviousFlowName(String previousFlowName) {
    this.previousFlowName = previousFlowName;
  }

  @Column(name = "next_flow_name")
  public String getNextFlowName() {
    return nextFlowName;
  }

  public void setNextFlowName(String nextFlowName) {
    this.nextFlowName = nextFlowName;
  }

  @Column(name = "shop_versions")
  public String getShopVersions() {
    return shopVersions;
  }

  public void setShopVersions(String shopVersions) {
    this.shopVersions = shopVersions;
  }

  @Column(name = "is_enabled")
  @Enumerated(EnumType.STRING)
  public BooleanEnum getEnabled() {
    return isEnabled;
  }

  public void setEnabled(BooleanEnum enabled) {
    isEnabled = enabled;
  }

  @Column(name = "first_step_name")
  public String getFirstStepName() {
    return firstStepName;
  }

  public void setFirstStepName(String firstStepName) {
    this.firstStepName = firstStepName;
  }


  @Enumerated(EnumType.STRING)
  @Column(name = "is_head")
  public BooleanEnum getHead() {
    return isHead;
  }

  public void setHead(BooleanEnum head) {
    isHead = head;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "is_tail")
  public BooleanEnum getTail() {
    return isTail;
  }

  public void setTail(BooleanEnum tail) {
    isTail = tail;
  }
}
