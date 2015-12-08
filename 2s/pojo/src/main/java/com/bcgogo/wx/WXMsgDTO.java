package com.bcgogo.wx;

import com.bcgogo.enums.DeletedType;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.wx.WXArticleDTO;
import com.bcgogo.wx.WXMsgStatus;
import com.bcgogo.wx.message.WXMCategory;
import com.bcgogo.wx.message.template.WXKWMsgTemplate;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 14-9-16
 * Time: 下午6:14
 * To change this template use File | Settings | File Templates.
 */
public class WXMsgDTO {
  private String msgId;
  private String openId;
  private String content;
  private Long sendTime;
  private String sendTimeStr;
  private WXMsgStatus status;
  private WXMCategory category;
  private String categoryStr;
  private String remark;
  private String title;//标题
  private String description;//描述
  private String url;//消息的连接
  private String picUrl;//消息图片的url
  private Long id;
  private String idStr;
  private DeletedType deleted=DeletedType.FALSE;
  private String statusName; //状态名称
  private String receiverCount; //送达人数
  private String receivers;//收信人
  private Long fromShopId;
  private String fromShopName;
  private String mediaId;  //上传到微信素材空间后获取;
  private Long userId;//谁发的操作人Id
  private String userName;//操作人名称
  private Long submitReviewTime;//提交审核的时间
  private String submitReviewTimeStr;

  //发送之前生成或者更新微信消息
  public void fromWxArticleDTO(WXArticleDTO dto) {
    this.setId(dto.getWxMsgLocalId());
    this.setUserId(dto.getUserId());
    this.setUserName(dto.getUserName());
    this.setTitle(dto.getTitle());
    this.setFromShopId(dto.getFromShopId());
    this.setPicUrl(dto.getPicUrl());
    this.setSendTime(dto.getSendTime());
    this.setSubmitReviewTime(dto.getSubmitReviewTime());
    this.setDescription(dto.getDescription());
  }

  public String getMediaId() {
    return mediaId;
  }

  public void setMediaId(String mediaId) {
    this.mediaId = mediaId;
  }

  public Long getFromShopId() {
    return fromShopId;
  }

  public void setFromShopId(Long fromShopId) {
    this.fromShopId = fromShopId;
  }

  public String getFromShopName() {
    return fromShopName;
  }

  public void setFromShopName(String fromShopName) {
    this.fromShopName = fromShopName;
  }

  public String getReceivers() {
    return receivers;
  }

  public void setReceivers(String receivers) {
    this.receivers = receivers;
  }

  public String getReceiverCount() {
    return receiverCount;
  }

  public void setReceiverCount(String receiverCount) {
    this.receiverCount = receiverCount;
  }
  public String getStime() {
    return stime;
  }

  public void setStime(String stime) {
    this.stime = stime;
  }

  private String stime;

  public String getStatusName() {
    return statusName;
  }

  public void setStatusName(String statusName) {
    this.statusName = statusName;
  }

  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
    if(id != null){
      setIdStr(id.toString());
    }else {
      setIdStr("");
    }
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getPicUrl() {
    return picUrl;
  }

  public void setPicUrl(String picUrl) {
    this.picUrl = picUrl;
  }

  public String getMsgId() {
    return msgId;
  }

  public void setMsgId(String msgId) {
    this.msgId = msgId;
  }

  public String getOpenId() {
    return openId;
  }

  public void setOpenId(String openId) {
    this.openId = openId;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Long getSendTime() {
    return sendTime;
  }

  public void setSendTime(Long sendTime) {
    this.sendTime = sendTime;
    this.sendTimeStr= DateUtil.convertDateLongToDateString(DateUtil.DEFAULT,sendTime);
  }

  public String getSendTimeStr() {
    return sendTimeStr;
  }

  public void setSendTimeStr(String sendTimeStr) {
    this.sendTimeStr = sendTimeStr;
  }

  public WXMsgStatus getStatus() {
    return status;
  }

  public void setStatus(WXMsgStatus status) {
    this.status = status;
  }

  public WXMCategory getCategory() {
    return category;
  }

  public void setCategory(WXMCategory category) {
    this.category = category;
    String categoryStr=null;
    switch (category){
      case SERVICE:
        categoryStr="演示消息";
        break;
      case TEMPLATE:
        categoryStr="消费账单";
        break;
      case MASS:
        categoryStr="群发消息";
        break;
    }
    setCategoryStr(categoryStr);
  }

  public String getCategoryStr() {
    return categoryStr;
  }

  public void setCategoryStr(String categoryStr) {
    this.categoryStr = categoryStr;
  }

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public Long getSubmitReviewTime() {
    return submitReviewTime;
  }

  public void setSubmitReviewTime(Long submitReviewTime) {
    this.submitReviewTime = submitReviewTime;
    this.setSubmitReviewTimeStr(DateUtil.convertDateLongToDateString(DateUtil.ALL,submitReviewTime));
  }

  public String getSubmitReviewTimeStr() {
    return submitReviewTimeStr;
  }

  public void setSubmitReviewTimeStr(String submitReviewTimeStr) {
    this.submitReviewTimeStr = submitReviewTimeStr;
  }

  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }
}
