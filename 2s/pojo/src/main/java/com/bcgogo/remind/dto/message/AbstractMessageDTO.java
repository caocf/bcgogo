package com.bcgogo.remind.dto.message;

import com.bcgogo.enums.txn.message.Status;

/**
 * Created with IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-1-22
 * Time: 下午3:24
 */
public abstract class AbstractMessageDTO {
  private Long id;
  private String idStr;
  private Status status;
  private String content;
  private String contentText;

  public String getContentText() {
    return contentText;
  }

  public void setContentText(String contentText) {
    this.contentText = contentText;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
    if (id != null) {
      this.setIdStr(id.toString());
    }
  }


  public String getIdStr() {
    return idStr;
  }

  public void setIdStr(String idStr) {
    this.idStr = idStr;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }
}
