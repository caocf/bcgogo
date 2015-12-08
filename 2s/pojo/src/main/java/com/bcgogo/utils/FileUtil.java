package com.bcgogo.utils;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 14-10-16
 * Time: 上午10:39
 */
public class FileUtil extends FileUtils{

  public static final Logger LOG = LoggerFactory.getLogger(FileUtil.class);

  /**
   * 计算文件 CRC32 校验值
   *
   * @param fileName
   * @return
   * @throws IOException
   */
  public static Long calcCRC32CheckSum(String fileName) throws IOException {
    CheckedInputStream cis = null;
    try {
      cis = new CheckedInputStream(new FileInputStream(fileName), new CRC32());
      byte[] buf = new byte[1024];
      while (cis.read(buf) >= 0) {
      }
      return cis.getChecksum().getValue();
    } finally {
      if (cis != null)
        cis.close();
    }
  }

  /**
   * 计算文件 CRC32 校验值
   *
   * @param fileBytes
   * @return
   * @throws IOException
   */
  public static Long calcCRC32CheckSum(byte[] fileBytes) throws IOException {
    CheckedInputStream cis = null;
    try {
      cis = new CheckedInputStream(new ByteArrayInputStream(fileBytes), new CRC32());
      byte[] buf = new byte[1024];
      while (cis.read(buf) >= 0) {
      }
      return cis.getChecksum().getValue();
    } finally {
      if (cis != null)
        cis.close();
    }
  }


  public static byte[] readUrlDate(String url) throws IOException {
    InputStream in = new URL(url).openStream();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try {
      IOUtil.copy(in, out);
      return out.toByteArray();
    } finally {
      IOUtil.closeQuietly(in);
    }
  }

  /**
   * 下载网络文件
   *
   * @param url
   * @throws IOException
   */
  public static void download(String url, String path) throws IOException {
    if (StringUtil.isEmpty(url)) return;
    InputStream in = new URL(url).openStream();
    try {
      OutputStream out = new FileOutputStream(path);
      IOUtil.copy(in, out);
    } finally {
      IOUtil.closeQuietly(in);
    }
  }


  public static File getFileFromBytes(byte[] b, String outputFile) throws IOException {
    BufferedOutputStream stream = null;
    File file = null;
    try {
      file = new File(outputFile);
      FileOutputStream fstream = new FileOutputStream(file);
      stream = new BufferedOutputStream(fstream);
      stream.write(b);
    } finally {
      if (stream != null) {
        try {
          stream.close();
        } catch (IOException e1) {
          e1.printStackTrace();
        }
      }
    }
    return file;
  }

  public static String getOSDisk() {
    return System.getProperty("os.name").toLowerCase().startsWith("win") ? "\\" : "/";
  }

}
