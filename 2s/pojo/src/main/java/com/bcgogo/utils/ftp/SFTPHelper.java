package com.bcgogo.utils.ftp;

import com.bcgogo.thread.ThreadPool;
import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-4-2
 * Time: 13:11
 */
public class SFTPHelper {
  private static final Logger LOG = LoggerFactory.getLogger(SFTPHelper.class);

  private static ChannelSftp sftp = null;

  public static void notifySFTPUploadListener(String host, String username, String password, int port, String localFile, String remoteFile) throws JSchException {
    LOG.info("create SFTP thread...");
    Executor executor = ThreadPool.getInstance();
    executor.execute(new SFTPUploadListener(host, username, password, port, localFile, remoteFile));
    LOG.info("create SFTP thread done");
  }

  /**
   * connect server via sftp
   *
   * @param host
   * @param username
   * @param password
   * @param port
   * @return
   * @throws JSchException
   */
  public static synchronized ChannelSftp createChannel(String host, String username, String password, int port) throws JSchException {
    LOG.info("createChannel,host={},port={}",host,port);
    if (sftp != null&&sftp.isConnected()) {
      LOG.info("channelSftp existed");
      return sftp;
    }
    LOG.info("create channel");
    JSch jsch = new JSch();
    jsch.getSession(username, host, port);
    Session sshSession = jsch.getSession(username, host, port);
    sshSession.setPassword(password);
    Properties sshConfig = new Properties();
    sshConfig.put("StrictHostKeyChecking", "no");
    sshSession.setConfig(sshConfig);
    sshSession.connect();
    LOG.info("Session connected.Opening Channel...");
    Channel channel = sshSession.openChannel("sftp");
    channel.connect();
    sftp = (ChannelSftp) channel;
    LOG.info("Connect to {}:{} success", host, port);
    return sftp;
  }

}
