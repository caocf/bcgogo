package com.bcgogo.txn.service.exportExcel;

/**
 * Created with IntelliJ IDEA.
 * User: jinyuan
 * Date: 13-8-7
 * Time: 上午1:15
 * To change this template use File | Settings | File Templates.
 */
public enum  FileDownloadStatus {
  WAITTING("未下载"),
  DOWNLOAD("已下载");
  String status;

  public String getStatus() {
    return status;
  }

  private FileDownloadStatus(String status) {
    this.status = status;
  }
}
