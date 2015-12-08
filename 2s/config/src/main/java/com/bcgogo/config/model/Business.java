package com.bcgogo.config.model;

import com.bcgogo.model.LongIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by IntelliJ IDEA.
 * User: MZDong
 * Date: 11-11-25
 * Time: 下午2:36
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "business")
public class Business extends LongIdentifier {
  public Business(){
  }

  private Long type;
  private String content;
  private Long no;
  private Long parentNo;
  private Long state;
  private String memo;

  @Column(name = "type")
  public Long getType() {
    return type;
  }

  public void setType(Long type) {
    this.type = type;
  }

  @Column(name = "content", length = 50)
  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  @Column(name = "no")
  public Long getNo() {
    return no;
  }

  public void setNo(Long no) {
    this.no = no;
  }

  @Column(name = "parent_no")
  public Long getParentNo() {
    return parentNo;
  }

  public void setParentNo(Long parentNo) {
    this.parentNo = parentNo;
  }

  @Column(name = "state")
  public Long getState() {
    return state;
  }

  public void setState(Long state) {
    this.state = state;
  }

  @Column(name = "memo", length = 500)
  public String getMemo() {
    return memo;
  }

  public void setMemo(String memo) {
    this.memo = memo;
  }
}