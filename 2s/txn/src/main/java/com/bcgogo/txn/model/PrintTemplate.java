package com.bcgogo.txn.model;

import com.bcgogo.enums.OrderTypes;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.txn.dto.PrintTemplateDTO;
import org.apache.commons.lang.StringUtils;
import org.hibernate.CallbackException;
import org.hibernate.Session;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-6-12
 * Time: 上午10:39
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "print_template")
public class PrintTemplate extends LongIdentifier {

  private String name;
  private String orderType;
  private OrderTypes orderTypeEnum;
  private byte[] templateHtml;

  @Column(name = "template_html")
  @Lob
  public byte[] getTemplateHtml() {
    return templateHtml;
  }

  public void setTemplateHtml(byte[] templateHtml) {
    this.templateHtml = templateHtml;
  }

  @Column(name = "order_type")
  public String getOrderType() {
    return orderType;
  }

  public void setOrderType(String orderType) {
    this.orderType = orderType;
  }

  @Column(name = "order_type_enum")
  @Enumerated(EnumType.STRING)
  public OrderTypes getOrderTypeEnum() {
    return orderTypeEnum;
  }

  public void setOrderTypeEnum(OrderTypes orderTypeEnum) {
    this.orderTypeEnum = orderTypeEnum;
  }

  @Column(name = "name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public PrintTemplateDTO toDTO(){
    PrintTemplateDTO printTemplateDTO = new PrintTemplateDTO();
    printTemplateDTO.setId(getId());
    printTemplateDTO.setName(getName());
    printTemplateDTO.setOrderType(getOrderTypeEnum());
    printTemplateDTO.setTemplateHtml(getTemplateHtml());
    return printTemplateDTO;
  }

}
