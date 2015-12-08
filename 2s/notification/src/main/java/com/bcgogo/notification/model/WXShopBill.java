package com.bcgogo.notification.model;

import com.bcgogo.enums.DeletedType;
import com.bcgogo.enums.SystemType;
import com.bcgogo.enums.sms.SmsSendScene;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.wx.WXShopBillDTO;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 14-12-17
 * Time: 下午1:48
 */
@Entity
@Table(name = "wx_shop_bill")
public class WXShopBill extends LongIdentifier {
  private Long shopId;
  private Long msgId;
  private Long vestDate;
  private SmsSendScene scene;
  private Integer amount;
  private Double total;
  private String operator;
  private DeletedType deleted= DeletedType.FALSE;

  public WXShopBillDTO toDTO(){
    WXShopBillDTO billDTO=new WXShopBillDTO();
    billDTO.setId(getId());
    billDTO.setShopId(getShopId());
    billDTO.setMsgId(getMsgId());
    billDTO.setVestDate(getVestDate());
    billDTO.setScene(getScene());
    billDTO.setAmount(getAmount());
    billDTO.setTotal(getTotal());
    billDTO.setOperator(getOperator());
    billDTO.setDeleted(getDeleted());
    return billDTO;
  }

   public void fromDTO(WXShopBillDTO billDTO){
    this.setId(billDTO.getId());
    this.setShopId(billDTO.getShopId());
    this.setMsgId(billDTO.getMsgId());
    this.setVestDate(billDTO.getVestDate());
    this.setScene(billDTO.getScene());
     this.setAmount(billDTO.getAmount());
    this.setTotal(billDTO.getTotal());
    this.setOperator(billDTO.getOperator());
    this.setDeleted(billDTO.getDeleted());
  }

  @Column(name = "shop_id")
  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  @Column(name = "msg_id")
  public Long getMsgId() {
    return msgId;
  }

  public void setMsgId(Long msgId) {
    this.msgId = msgId;
  }

  @Column(name = "vest_date")
  public Long getVestDate() {
    return vestDate;
  }

  public void setVestDate(Long vestDate) {
    this.vestDate = vestDate;
  }

  @Column(name = "scene")
  @Enumerated(EnumType.STRING)
  public SmsSendScene getScene() {
    return scene;
  }

  public void setScene(SmsSendScene scene) {
    this.scene = scene;
  }

  @Column(name = "amount")
  public Integer getAmount() {
    return amount;
  }

  public void setAmount(Integer amount) {
    this.amount = amount;
  }



  @Column(name = "total")
  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  @Column(name = "operator")
  public String getOperator() {
    return operator;
  }

  public void setOperator(String operator) {
    this.operator = operator;
  }

  @Column(name = "deleted")
  @Enumerated(EnumType.STRING)
  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }


}
