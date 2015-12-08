package com.bcgogo.api;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-5-5
 * Time: 10:24
 */
public class PFileBlock {
  private String uuid;
  private int seq1;//当前开始块序号，从0开始标记，如果含有多块数据，尾块序号在seq2中
  private int seq2;
  private Long length;
  private Long timestamp;
  private Long crc;

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public int getSeq1() {
    return seq1;
  }

  public void setSeq1(int seq1) {
    this.seq1 = seq1;
  }

  public int getSeq2() {
    return seq2;
  }

  public void setSeq2(int seq2) {
    this.seq2 = seq2;
  }

  public Long getLength() {
    return length;
  }

  public void setLength(Long length) {
    this.length = length;
  }

  public Long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Long timestamp) {
    this.timestamp = timestamp;
  }

  public Long getCrc() {
    return crc;
  }

  public void setCrc(Long crc) {
    this.crc = crc;
  }
}
