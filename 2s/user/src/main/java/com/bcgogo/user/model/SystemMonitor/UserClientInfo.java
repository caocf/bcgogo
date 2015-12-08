package com.bcgogo.user.model.SystemMonitor;

import com.bcgogo.model.LongIdentifier;
import com.bcgogo.user.dto.UserClientInfoDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 用户客户端信息
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 13-2-19
 * Time: 下午3:40
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "user_client_info")
public class UserClientInfo extends LongIdentifier {
  private Long shopId;
  private String userNo;
  private String browser;//浏览器
  private String os;//操作系统
  private String cpu;//cpu
  private String finger;//客户端的指纹
  private Integer score;//finger准确率得分.共100分

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "browser")
  public String getBrowser() {
    return browser;
  }

  public void setBrowser(String browser) {
    this.browser = browser;
  }

  @Column(name = "user_no")
  public String getUserNo() {
    return userNo;
  }

  public void setUserNo(String userNo) {
    this.userNo = userNo;
  }

  @Column(name = "os")
  public String getOs() {
    return os;
  }

  public void setOs(String os) {
    this.os = os;
  }

  @Column(name = "cpu")
  public String getCpu() {
    return cpu;
  }

  public void setCpu(String cpu) {
    this.cpu = cpu;
  }

  @Column(name = "finger")
  public String getFinger() {
    return finger;
  }

  public void setFinger(String finger) {
    this.finger = finger;
  }

  @Column(name = "score")
  public Integer getScore() {
    return score;
  }

  public void setScore(Integer score) {
    this.score = score;
  }

  public UserClientInfo(){}

  public UserClientInfo(UserClientInfoDTO userClientInfoDTO) {
    this.setId(userClientInfoDTO.getId());
    this.setShopId(userClientInfoDTO.getShopId());
    this.setUserNo(userClientInfoDTO.getUserNo());
    this.setBrowser(userClientInfoDTO.getBrowser());
    this.setOs(userClientInfoDTO.getOs());
    this.setCpu(userClientInfoDTO.getCpu());
    this.setFinger(userClientInfoDTO.getFinger());
  }

  public UserClientInfoDTO toDTO() {
    UserClientInfoDTO userClientInfoDTO = new UserClientInfoDTO();
    userClientInfoDTO.setId(getId());
    userClientInfoDTO.setShopId(getShopId());
    userClientInfoDTO.setUserNo(getUserNo());
    userClientInfoDTO.setBrowser(getBrowser());
    userClientInfoDTO.setOs(getOs());
    userClientInfoDTO.setCpu(getCpu());
    userClientInfoDTO.setFinger(getFinger());
    userClientInfoDTO.setScore(getScore());
    return userClientInfoDTO;
  }

  public UserClientInfo fromDTO(UserClientInfoDTO userClientInfoDTO) {
    this.setId(userClientInfoDTO.getId());
    this.setShopId(userClientInfoDTO.getShopId());
    this.setUserNo(userClientInfoDTO.getUserNo());
    this.setBrowser(userClientInfoDTO.getBrowser());
    this.setOs(userClientInfoDTO.getOs());
    this.setCpu(userClientInfoDTO.getCpu());
    this.setFinger(userClientInfoDTO.getFinger());
    this.setScore(userClientInfoDTO.getScore());
    return this;
  }
}
