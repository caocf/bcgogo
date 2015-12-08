package com.bcgogo.wx.user;

import com.bcgogo.api.WXFanDTO;
import com.bcgogo.enums.DeletedType;

import java.util.ArrayList;
import java.util.List;

/**
 * 微信粉丝信息
 * User: ndong
 * Date: 14-8-29
 * Time: 上午11:04
 * To change this template use File | Settings | File Templates.
 */
public class WXUserDTO {
  private Long id;
  private Long shopId;
  private String openid;
  private String publicNo;  //关注的公共号openId
  private String mobile;
  private String subscribe;
  private String nickname;
  private String name;
  private String sex;
  private String city;
  private String country;
  private String province;
  private String language;
  private String headimgurl;
  private String subscribe_time;
  private String unionid;
  private String remark;
  private DeletedType deleted;
  private String idStr;
  private String publicName;//公众号名称
  private List<WXFanDTO> wxFanDTOs = new ArrayList<WXFanDTO>();

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public List<WXFanDTO> getWxFanDTOs() {
    return wxFanDTOs;
  }

  public void setWxFanDTOs(List<WXFanDTO> wxFanDTOs) {
    this.wxFanDTOs = wxFanDTOs;
  }

  public String getPublicName() {
    return publicName;
  }

  public void setPublicName(String publicName) {
    this.publicName = publicName;
  }

  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
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

  public String getOpenid() {
    return openid;
  }

  public void setOpenid(String openid) {
    this.openid = openid;
  }

  public String getPublicNo() {
    return publicNo;
  }

  public void setPublicNo(String publicNo) {
    this.publicNo = publicNo;
  }

  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }

  public String getSubscribe() {
    return subscribe;
  }

  public void setSubscribe(String subscribe) {
    this.subscribe = subscribe;
  }

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getSex() {
    return sex;
  }

  public void setSex(String sex) {
    this.sex = sex;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getProvince() {
    return province;
  }

  public void setProvince(String province) {
    this.province = province;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public String getHeadimgurl() {
    return headimgurl;
  }

  public void setHeadimgurl(String headimgurl) {
    this.headimgurl = headimgurl;
  }

  public String getSubscribe_time() {
    return subscribe_time;
  }

  public void setSubscribe_time(String subscribe_time) {
    this.subscribe_time = subscribe_time;
  }

  public String getUnionid() {
    return unionid;
  }

  public void setUnionid(String unionid) {
    this.unionid = unionid;
  }

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  public DeletedType getDeleted() {
    return deleted;
  }

  public void setDeleted(DeletedType deleted) {
    this.deleted = deleted;
  }
}
