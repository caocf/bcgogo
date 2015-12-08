package com.bcgogo.config.dto;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: MZDong
 * Date: 11-11-25
 * Time: 下午2:43
 * To change this template use File | Settings | File Templates.
 */
public class BusinessDTO implements Serializable {
  public BusinessDTO(){
  }

  private Long type;
  private String content;
  private Long no;
  private Long parentNo;
  private Long state;
  private String memo;

  public Long getType() {
    return type;
  }

  public void setType(Long type) {
    this.type = type;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Long getNo() {
    return no;
  }

  public void setNo(Long no) {
    this.no = no;
  }

  public Long getParentNo() {
    return parentNo;
  }

  public void setParentNo(Long parentNo) {
    this.parentNo = parentNo;
  }

  public Long getState() {
    return state;
  }

  public void setState(Long state) {
    this.state = state;
  }

  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }

}
