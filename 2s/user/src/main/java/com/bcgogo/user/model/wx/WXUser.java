package com.bcgogo.user.model.wx;

import com.bcgogo.enums.DeletedType;
import com.bcgogo.model.LongIdentifier;
import com.bcgogo.wx.user.WXUserDTO;

import javax.persistence.*;

/**
 * 微信粉丝信息
 * User: ndong
 * Date: 14-8-29
 * Time: 上午11:02
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "wx_user")
public class WXUser extends LongIdentifier{
  private String subscribe;
  private String nickName;
  private String name;
  private String mobile;
  private String sex;
  private String city;
  private String country;
  private String province;
  private String language;
  private String headImgUrl;
  private String subscribeTime;
  private String remark;
  private String openId;
  private String publicNo;  //关注的公共号openId
  private String unionId;
  private DeletedType deleted=DeletedType.FALSE;

  public void fromDTO(WXUserDTO userDTO){
    this.setId(userDTO.getId());
    this.setOpenId(userDTO.getOpenid());
    this.setPublicNo(userDTO.getPublicNo());
    this.setOpenId(userDTO.getOpenid());
    this.setMobile(userDTO.getMobile());
    this.setNickName(userDTO.getNickname());
    this.setName(userDTO.getName());
    this.setSex(userDTO.getSex());
    this.setLanguage(userDTO.getLanguage());
    this.setHeadImgUrl(userDTO.getHeadimgurl());
    this.setSubscribe(userDTO.getSubscribe());
    this.setSubscribeTime(userDTO.getSubscribe_time());
    this.setCountry(userDTO.getCountry());
    this.setProvince(userDTO.getProvince());
    this.setCity(userDTO.getCity());
    this.setUnionId(userDTO.getUnionid());
    this.setRemark(userDTO.getRemark());
    this.setDeleted(userDTO.getDeleted());
  }

  public WXUserDTO toDTO(){
    WXUserDTO userDTO=new WXUserDTO();
    userDTO.setId(getId());
    userDTO.setOpenid(this.getOpenId());
    userDTO.setPublicNo(this.getPublicNo());
    userDTO.setMobile(this.getMobile());
    userDTO.setNickname(this.getNickName());
    userDTO.setName(this.getName());
    userDTO.setSex(this.getSex());
    userDTO.setLanguage(this.getLanguage());
    userDTO.setHeadimgurl(this.getHeadImgUrl());
    userDTO.setSubscribe(this.getSubscribe());
    userDTO.setSubscribe_time(this.getSubscribeTime());
    userDTO.setCountry(this.getCountry());
    userDTO.setProvince(this.getProvince());
    userDTO.setCity(this.getCity());
    userDTO.setUnionid(this.getUnionId());
    userDTO.setRemark(this.getRemark());
    userDTO.setDeleted(getDeleted());
    return userDTO;
  }

  @Column(name = "open_id")
  public String getOpenId() {
    return openId;
  }

  public void setOpenId(String openId) {
    this.openId = openId;
  }

  @Column(name = "public_no")
  public String getPublicNo() {
    return publicNo;
  }

  public void setPublicNo(String publicNo) {
    this.publicNo = publicNo;
  }

  @Column(name = "subscribe")
  public String getSubscribe() {
    return subscribe;
  }

  public void setSubscribe(String subscribe) {
    this.subscribe = subscribe;
  }

  @Column(name = "nick_name")
  public String getNickName() {
    return nickName;
  }

  public void setNickName(String nickName) {
    this.nickName = nickName;
  }

  @Column(name = "name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name = "mobile")
  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  @Column(name = "sex")
  public String getSex() {
    return sex;
  }

  public void setSex(String sex) {
    this.sex = sex;
  }

  @Column(name = "city")
  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  @Column(name = "country")
  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  @Column(name = "province")
  public String getProvince() {
    return province;
  }

  public void setProvince(String province) {
    this.province = province;
  }

  @Column(name = "language")
  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  @Column(name = "head_img_url")
  public String getHeadImgUrl() {
    return headImgUrl;
  }

  public void setHeadImgUrl(String headImgUrl) {
    this.headImgUrl = headImgUrl;
  }

  @Column(name = "subscribe_time")
  public String getSubscribeTime() {
    return subscribeTime;
  }

  public void setSubscribeTime(String subscribeTime) {
    this.subscribeTime = subscribeTime;
  }

  @Column(name = "union_id")
  public String getUnionId() {
    return unionId;
  }

  public void setUnionId(String unionId) {
    this.unionId = unionId;
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


}
