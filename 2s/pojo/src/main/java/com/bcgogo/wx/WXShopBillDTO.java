package com.bcgogo.wx;

import com.bcgogo.enums.DeletedType;
import com.bcgogo.enums.sms.SmsSendScene;
import com.bcgogo.utils.DateUtil;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 14-12-18
 * Time: 下午2:10
 */
public class WXShopBillDTO {
  private Long id;
  private Long shopId;
  private Long msgId;
  private Long vestDate;
  private String vestDateStr;
  private SmsSendScene scene;
  private Integer amount;
  private String sceneStr;
  private Double total;
  private String operator;
  private DeletedType deleted= DeletedType.FALSE;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public Long getMsgId() {
    return msgId;
  }

  public void setMsgId(Long msgId) {
    this.msgId = msgId;
  }

  public Long getVestDate() {
    return vestDate;
  }

  public void setVestDate(Long vestDate) {
    this.vestDate = vestDate;
    this.setVestDateStr(DateUtil.convertDateLongToDateString(DateUtil.ALL,vestDate));
  }

  public String getVestDateStr() {
    return vestDateStr;
  }

  public void setVestDateStr(String vestDateStr) {
    this.vestDateStr = vestDateStr;
  }

  public SmsSendScene getScene() {
    return scene;
  }

  public void setScene(SmsSendScene scene) {
    this.scene = scene;
    this.setSceneStr(scene.getName());
  }

  public String getSceneStr() {
    return sceneStr;
  }

  public void setSceneStr(String sceneStr) {
    this.sceneStr = sceneStr;
  }

  public Integer getAmount() {
    return amount;
  }

  public void setAmount(Integer amount) {
    this.amount = amount;
  }

  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  public String getOperator() {
    return operator;
  }

  public void setOperator(String operator) {
    this.operator = operator;
  }

  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }
}
