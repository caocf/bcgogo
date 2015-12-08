package com.bcgogo.pojox.ext;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-4-21
 * Time: 09:13
 */

import com.bcgogo.pojox.util.NumberUtil;
import org.apache.commons.fileupload.ProgressListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUploadListener implements ProgressListener {
  private static final Logger LOG = LoggerFactory.getLogger(FileUploadListener.class);
  private String uuid;

  public FileUploadListener(String uuid) {
    this.uuid = uuid;
  }

  /**
   * @param bytesRead     已经上传多少字节
   * @param contentLength 一共多少字节
   * @param items         正在上传第几个文件
   */
  @Override
  public void update(long bytesRead, long contentLength, int items) {
    double percentage = ((double) bytesRead / (double) contentLength);
    LOG.info (" uuid:{},uploadFile progress:{}",uuid , NumberUtil.round(percentage * 100) + "%");
  }

}
