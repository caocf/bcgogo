package com.bcgogo.wx;

import com.bcgogo.wx.article.CustomArticle;
import com.bcgogo.wx.article.NewsArticle;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 14-9-25
 * Time: 上午11:31
 * To change this template use File | Settings | File Templates.
 */
public class WXArticleDTO extends WXArticleTemplateDTO{

  //待审核或者审核不通过的wxMsg的Id 不是wxMsg里的wxMsgId
  private Long wxMsgLocalId;
  //wxMsg 里的msgId 微信给的
  private String wxMsgId;
  //收信人组，目前只支持所有和测试组（ALL_FANS，TEST_FANS），以后可以扩展
  private String wxReceiverGroupType;
  //收件人的openId
  private String[] receiverOpenIds;
  private Long[] receiverUserIds;
  //关联WxArticleTemplate 的Id
  private Long wxArticleTemplateId;
  //用户自定义上传的图片文件
  private MultipartFile imgFile ;
  //从那个店铺发的
  private Long fromShopId;
  //谁发的操作人Id
  private Long userId;
  //操作人名称
  private String userName;
  //提交审核时间
  private Long submitReviewTime;
  //发送时间
  private Long sendTime;

  //上传到微信素材空间后获取
  private String mediaId;
   //发送的微信公共号
  private String publicNo;
  //文章作者
  private String author;

  public NewsArticle toNewsArticle(){
    NewsArticle article=new NewsArticle();
    article.setTitle(this.getTitle());
    article.setContent(this.getDescription());
    article.setDigest(this.getDescription());
    article.setThumb_media_id(getMediaId());
    article.setAuthor(getAuthor());
    return article;
  }

 //客服消息图文消息素材
  public CustomArticle toCustomArticle(){
    CustomArticle article=new CustomArticle();
    article.setTitle(this.getTitle());
    article.setDescription(this.getDescription());
    article.setPicurl(this.getPicUrl());
    article.setUrl(this.getUrl());
    return article;
  }


  public Long getWxMsgLocalId() {
    return wxMsgLocalId;
  }

  public void setWxMsgLocalId(Long wxMsgLocalId) {
    this.wxMsgLocalId = wxMsgLocalId;
  }

  public String getWxMsgId() {
    return wxMsgId;
  }

  public void setWxMsgId(String wxMsgId) {
    this.wxMsgId = wxMsgId;
  }

  public String getMediaId() {
    return mediaId;
  }

  public void setMediaId(String mediaId) {
    this.mediaId = mediaId;
  }

  public String getWxReceiverGroupType() {
    return wxReceiverGroupType;
  }

  public void setWxReceiverGroupType(String wxReceiverGroupType) {
    this.wxReceiverGroupType = wxReceiverGroupType;
  }

  public String[] getReceiverOpenIds() {
    return receiverOpenIds;
  }

  public void setReceiverOpenIds(String[] receiverOpenIds) {
    this.receiverOpenIds = receiverOpenIds;
  }

  public Long getWxArticleTemplateId() {
    return wxArticleTemplateId;
  }

  public void setWxArticleTemplateId(Long wxArticleTemplateId) {
    this.wxArticleTemplateId = wxArticleTemplateId;
  }

  public MultipartFile getImgFile() {
    return imgFile;
  }

  public void setImgFile(MultipartFile imgFile) {
    this.imgFile = imgFile;
  }

  public Long getFromShopId() {
    return fromShopId;
  }

  public void setFromShopId(Long fromShopId) {
    this.fromShopId = fromShopId;
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
  }

  public Long getSendTime() {
    return sendTime;
  }

  public void setSendTime(Long sendTime) {
    this.sendTime = sendTime;
  }

  public Long[] getReceiverUserIds() {
    return receiverUserIds;
  }

  public void setReceiverUserIds(Long[] receiverUserIds) {
    this.receiverUserIds = receiverUserIds;
  }

  public String getPublicNo() {
    return publicNo;
  }

  public void setPublicNo(String publicNo) {
    this.publicNo = publicNo;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }



}
