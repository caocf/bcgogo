package com.bcgogo.api;

/**
 * 碰撞视频文件上传前初始化参数
 * Author: ndong
 * Date: 2015-5-5
 * Time: 11:11
 */
public class PFile {
  private String uuid;
  private Long blockNumber;
  private Long totalLength;
  private String crc;

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public Long getBlockNumber() {
    return blockNumber;
  }

  public void setBlockNumber(Long blockNumber) {
    this.blockNumber = blockNumber;
  }

  public Long getTotalLength() {
    return totalLength;
  }

  public void setTotalLength(Long totalLength) {
    this.totalLength = totalLength;
  }

  public String getCrc() {
    return crc;
  }

  public void setCrc(String crc) {
    this.crc = crc;
  }
}
