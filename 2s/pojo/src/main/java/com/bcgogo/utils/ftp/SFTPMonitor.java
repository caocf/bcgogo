package com.bcgogo.utils.ftp;

import com.jcraft.jsch.SftpProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 检测文件上传状态和上传进度
 * Author: ndong
 * Date: 2015-4-2
 * Time: 13:23
 */
public class SFTPMonitor implements SftpProgressMonitor {
  public static final Logger LOG = LoggerFactory.getLogger(SFTPMonitor.class);

  private long transfered;

  @Override
  public boolean count(long count) {
    transfered = transfered + count;
    LOG.info("currently transferred total size: {} bytes" + transfered);
    return true;
  }

  @Override
  public void end() {
    LOG.info("sftp Transferring done");
  }

  @Override
  public void init(int op, String src, String dest, long max) {
    LOG.info("sftp Transferring begin");
  }
}
