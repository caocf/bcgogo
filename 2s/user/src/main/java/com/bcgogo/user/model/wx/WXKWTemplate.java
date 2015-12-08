package com.bcgogo.user.model.wx;

import com.bcgogo.enums.DeletedType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.wx.message.template.WXKWMsgTemplate;
import com.bcgogo.wx.user.WXKWTemplateDTO;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 14-10-11
 * Time: 下午4:10
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "wx_kw_template")
public class WXKWTemplate extends LongIdentifier{
  private String publicNo;
  private String title;
  private String templateId;
  private String first;
  private String remark;
  private String topColor;
  private String firstColor;
  private String keyword1Color;
  private String keyword2Color;
  private String keyword3Color;
  private String remarkColor;
  private DeletedType deleted=DeletedType.FALSE;

  public WXKWTemplateDTO toDTO(){
     WXKWTemplateDTO templateDTO=new WXKWTemplateDTO();
    templateDTO.setId(getId());
    templateDTO.setPublicNo(getPublicNo());
    templateDTO.setTitle(getTitle());
    templateDTO.setTemplateId(getTemplateId());
    templateDTO.setFirst(getFirst());
    templateDTO.setRemark(getRemark());
    templateDTO.setTopColor(getTopColor());
    templateDTO.setFirstColor(getFirstColor());
    templateDTO.setKeyword1Color(getKeyword1Color());
    templateDTO.setKeyword2Color(getKeyword2Color());
    templateDTO.setKeyword3Color(getKeyword3Color());
    templateDTO.setRemarkColor(getRemarkColor());
    templateDTO.setDeleted(getDeleted());
    return templateDTO;
  }

  @Column(name = "public_no")
  public String getPublicNo() {
    return publicNo;
  }

  public void setPublicNo(String publicNo) {
    this.publicNo = publicNo;
  }

  @Column(name = "title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @Column(name = "template_id")
  public String getTemplateId() {
    return templateId;
  }

  public void setTemplateId(String templateId) {
    this.templateId = templateId;
  }

  @Column(name = "first")
  public String getFirst() {
    return first;
  }

  public void setFirst(String first) {
    this.first = first;
  }

  @Column(name = "remark")
  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  @Column(name = "top_color")
  public String getTopColor() {
    return topColor;
  }

  public void setTopColor(String topColor) {
    this.topColor = topColor;
  }

  @Column(name = "first_color")
  public String getFirstColor() {
    return firstColor;
  }

  public void setFirstColor(String firstColor) {
    this.firstColor = firstColor;
  }

  @Column(name = "keyword1_color")
  public String getKeyword1Color() {
    return keyword1Color;
  }

  public void setKeyword1Color(String keyword1Color) {
    this.keyword1Color = keyword1Color;
  }

  @Column(name = "keyword2_color")
  public String getKeyword2Color() {
    return keyword2Color;
  }

  public void setKeyword2Color(String keyword2Color) {
    this.keyword2Color = keyword2Color;
  }

  @Column(name = "keyword3_color")
  public String getKeyword3Color() {
    return keyword3Color;
  }

  public void setKeyword3Color(String keyword3Color) {
    this.keyword3Color = keyword3Color;
  }

  @Column(name = "remark_color")
  public String getRemarkColor() {
    return remarkColor;
  }

  public void setRemarkColor(String remarkColor) {
    this.remarkColor = remarkColor;
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
