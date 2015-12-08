package com.bcgogo.txn.model.message;

import com.bcgogo.enums.txn.message.Status;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import java.util.Enumeration;

/**
 * Created with IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 13-1-22
 * Time: 下午3:11
 */

@MappedSuperclass
public abstract class AbstractMessage extends LongIdentifier {
  private String content;  //内容
  private Status status;   //消息状态

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  @Column(name = "content")
  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

}
