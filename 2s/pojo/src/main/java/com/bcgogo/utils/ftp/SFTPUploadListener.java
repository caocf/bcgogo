package com.bcgogo.utils.ftp;

/**
 * SFTP文件上传监听器
 * sftp是Secure File Transfer Protocol的缩写，安全文件传送协议。可以为传输文件提供一种安全的加密方法
 *
 * Author: ndong
 * Date: 2015-4-2
 * Time: 11:52
 */

import com.bcgogo.listener.BcgogoEventListener;
import com.jcraft.jsch.*;
import org.slf4j.*;
import org.slf4j.Logger;

import java.io.File;

public class SFTPUploadListener extends BcgogoEventListener {
  public static final Logger LOG = LoggerFactory.getLogger(SFTPUploadListener.class);

  private String host, username, password, localFile, remoteFile;
  private int port;
  private static ChannelSftp sftp = null;

  public SFTPUploadListener(String host, String username, String password, int port, String localFile, String remoteFile) {
    this.host = host;
    this.username = username;
    this.password = password;
    this.port = port;
    this.localFile = localFile;
    this.remoteFile = remoteFile;
  }

  /**
   * Disconnect with server
   */
  public void disconnect() {
    if (this.sftp == null) {
      return;
    }
    if (this.sftp.isConnected()) {
      this.sftp.disconnect();
    } else if (this.sftp.isClosed()) {
      LOG.info("sftp is closed already");
    }
  }

  /**
   * upload  the files to the server
   */
  @Override
  public void run() {
    try {
      LOG.info("SFTPUploadListener run start...");
      sftp = SFTPHelper.createChannel(host, username, password, port);
      LOG.info("localFile={},remoteFile={}", localFile, remoteFile);
      File file = new File(localFile);
      if (!file.isFile()) {
        LOG.info("localFile doesn't exist");
        return;
      }
      sftp.put(localFile, remoteFile, new SFTPMonitor(), ChannelSftp.OVERWRITE);
      LOG.info("upload done");
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }finally {
      if(sftp!=null){
        sftp.disconnect();
      }
    }
  }

}
