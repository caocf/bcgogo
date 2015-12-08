package com.bcgogo.api;

import java.io.Serializable;

/**
 * order item
 * Created with IntelliJ IDEA.
 * User: lw
 * Date: 13-8-19
 * Time: 下午4:56
 * To change this template use File | Settings | File Templates.
 */
public class AppOrderItemDTO  implements Serializable {
  private String content;//内容        String
  private String type;//类型           String
  private Double amount;//金额         double

  public final static String itemTypeService = "服务内容";
  public final static String itemTypeProduct = "商品";


  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }
}
