package com.bcgogo.notification.model;

import com.bcgogo.enums.DeletedType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.wx.WXArticleDTO;
import com.bcgogo.wx.WXMsgDTO;
import com.bcgogo.wx.WXMsgStatus;
import com.bcgogo.wx.message.WXMCategory;

import javax.persistence.*;

/**
 * 微信消息记录.
 * User: ndong
 * Date: 14-9-4
 * Time: 下午2:14
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "wx_msg")
public class WXMsg extends LongIdentifier{
  private String msgId;//微信那边返回的Id ,不一定是数值型,这里用string
  private String content;//消息的正文
  private Long sendTime;//消息发送时间
  private WXMCategory category;//消息分类
  private WXMsgStatus status;//消息状态
  private String remark;//备注
  private String title;//标题
  private String description;//描述
  private String url;//消息的连接
  private String picUrl;//消息图片的url
  private Long fromShopId;
  private Long submitReviewTime;//提交审核的时间
  private String wxArticleTemplateId;//提交审核的关联模板Id
  private Long userId;//谁发的操作人Id
  private String userName;//操作人名称
  private String mediaId;  //上传到微信素材空间后获取
  private DeletedType deleted=DeletedType.FALSE;



  public WXMsgDTO toDTO(){
    WXMsgDTO msgDTO=new WXMsgDTO();
    msgDTO.setId(getId());
    msgDTO.setFromShopId(getFromShopId());
    msgDTO.setUserId(getUserId());
    msgDTO.setUserName(getUserName());
    msgDTO.setMsgId(getMsgId());
    msgDTO.setContent(getContent());
    msgDTO.setSendTime(getSendTime());
    msgDTO.setStatus(getStatus());
    msgDTO.setCategory(getCategory());
    msgDTO.setRemark(getRemark());
    msgDTO.setTitle(getTitle());
    msgDTO.setDescription(getDescription());
    msgDTO.setSubmitReviewTime(getSubmitReviewTime());
    msgDTO.setUrl(getUrl());
    msgDTO.setPicUrl(getPicUrl());
    msgDTO.setSubmitReviewTime(getSubmitReviewTime());
    msgDTO.setDeleted(getDeleted());
    return msgDTO;
  }

  public void fromDTO(WXMsgDTO msgDTO){
    this.setFromShopId(msgDTO.getFromShopId());
    this.setUserId(msgDTO.getUserId());
    this.setUserName(msgDTO.getUserName());
    this.setMsgId(msgDTO.getMsgId());
    this.setContent(msgDTO.getContent());
    this.setSendTime(msgDTO.getSendTime());
    this.setStatus(msgDTO.getStatus());
    this.setCategory(msgDTO.getCategory());
    this.setRemark(msgDTO.getRemark());
    this.setTitle(msgDTO.getTitle());
    this.setMediaId(msgDTO.getMediaId());
    this.setSubmitReviewTime(msgDTO.getSubmitReviewTime());
    this.setDescription(msgDTO.getDescription());
    this.setUrl(msgDTO.getUrl());
    this.setPicUrl(msgDTO.getPicUrl());
    this.setId(msgDTO.getId());
    this.setDeleted(msgDTO.getDeleted());
  }

  //发送之前生成或者更新微信消息
  public void fromWxArticleDTO(WXArticleDTO dto) {
    this.setUserId(dto.getUserId());
    this.setUserName(dto.getUserName());
    this.setTitle(dto.getTitle());
    this.setFromShopId(dto.getFromShopId());
    this.setUrl(dto.getUrl());
    this.setPicUrl(dto.getPicUrl());
    this.setSendTime(dto.getSendTime());
    this.setSubmitReviewTime(dto.getSubmitReviewTime());
    this.setDescription(dto.getDescription());
  }

  @Column(name = "msg_id")
  public String getMsgId() {
    return msgId;
  }

  public void setMsgId(String msgId) {
    this.msgId = msgId;
  }

  @Column(name = "content")
  public String getContent() {
    return content;
  }


  public void setContent(String content) {
    this.content = content;
  }

  @Column(name = "send_time")
  public Long getSendTime() {
    return sendTime;
  }

  public void setSendTime(Long sendTime) {
    this.sendTime = sendTime;
  }

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  public WXMsgStatus getStatus() {
    return status;
  }

  public void setStatus(WXMsgStatus status) {
    this.status = status;
  }

  @Column(name = "category")
  @Enumerated(EnumType.STRING)
  public WXMCategory getCategory() {
    return category;
  }

  public void setCategory(WXMCategory category) {
    this.category = category;
  }

  @Column(name = "remark")
  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  @Column(name = "deleted")
  @Enumerated(EnumType.STRING)
  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }

  @Column(name = "title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @Column(name = "description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Column(name = "url")
  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  @Column(name = "pic_url")
  public String getPicUrl() {
    return picUrl;
  }

  public void setPicUrl(String picUrl) {
    this.picUrl = picUrl;
  }

  @Column(name = "from_shop_id")
  public Long getFromShopId() {
    return fromShopId;
  }

  public void setFromShopId(Long fromShopId) {
    this.fromShopId = fromShopId;
  }

  @Column(name = "submit_review_time")
  public Long getSubmitReviewTime() {
    return submitReviewTime;
  }

  public void setSubmitReviewTime(Long submitReviewTime) {
    this.submitReviewTime = submitReviewTime;
  }

  @Column(name = "wx_article_template_id")
  public String getWxArticleTemplateId() {
    return wxArticleTemplateId;
  }

  public void setWxArticleTemplateId(String wxArticleTemplateId) {
    this.wxArticleTemplateId = wxArticleTemplateId;
  }

  @Column(name = "user_id")
  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  @Column(name = "user_name")
  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  @Column(name = "media_id")
  public String getMediaId() {
    return mediaId;
  }

  public void setMediaId(String mediaId) {
    this.mediaId = mediaId;
  }

}
