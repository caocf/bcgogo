//package com.bcgogo.util;
//
//import com.bcgogo.constant.AppConstant;
//import com.bcgogo.utils.FileUtil;
//import com.bcgogo.utils.NumberUtil;
//import org.junit.Test;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.*;
//import java.util.Arrays;
//import java.util.Comparator;
//import java.util.UUID;
//
///**
// * Created by IntelliJ IDEA.
// * Author: ndong
// * Date: 2015-5-14
// * Time: 10:10
// */
//public class UploadTest {
//
//  @Test
//  public void testRandomAccessFile() throws IOException {
//    //创建文件
//    String path = "c:\\test\\";
//    String uuid = UUID.randomUUID().toString();
//    File videoPath = new File(path + uuid);
//    videoPath.mkdir();
//
//    //1---------------------------------分割视频 ------------------------------------
//    String srcFilePath = path + "test.mp4";
//    File srcFile = new File(srcFilePath);
//    long srcLen = srcFile.length();
//    long blockLen = 10 * 1024;
//    long start = 0;
//    long blockNumber = srcLen/blockLen+1;
//    for (int i = 0; i < blockNumber; i++) {
//      long len = blockLen;
//      if (i == (blockNumber - 1)) {
//        len = srcFile.length() - start;
//      }
//      File tempFile = new File(videoPath.getPath() + FileUtil.getOSDisk() + i);
//      tempFile.createNewFile();
//      OutputStream out = new FileOutputStream(tempFile);
//      RandomAccessFile fileReader = null;
//      try {
//        byte[] tBytes = new byte[NumberUtil.intValue(len)];
//        fileReader = new RandomAccessFile(srcFile, "r");
//        fileReader.seek(start);
//        fileReader.read(tBytes, 0, NumberUtil.intValue(len));
//        // 输出表头
//        out.write(tBytes, 0, tBytes.length);
//      } finally {
//        if (fileReader != null) {
//          fileReader.close();
//        }
//        if (out != null) {
//          out.close();
//        }
//      }
//      start += len;
//    }
//
//    //2---------------------------------组装视频 ------------------------------------
//    //排序文件块
//    String[] blockFiles = videoPath.list();
//    Arrays.sort(blockFiles, new Comparator<String>() {
//      @Override
//      public int compare(String o1, String o2) {
//        return NumberUtil.subtract(o1, o2).intValue();
//      }
//    });
//    //创建视频文件
//    File videoFile = new File(videoPath.getPath() + FileUtil.getOSDisk() + uuid + ".txt");
//    videoFile.createNewFile();
//    //组装文件
//    RandomAccessFile fileWrite = null;
//    RandomAccessFile fileReader = null;
//    try {
//      start = 0;
//      int fLen = 0;
//      byte[] buf = new byte[1024 * 10];
//      fileWrite = new RandomAccessFile(videoFile, "rw");
//      for (int i = 0; i < blockFiles.length; i++) {
//        fileWrite.seek(start);
//        File tmpFile = new File(videoPath + FileUtil.getOSDisk() + blockFiles[i]);
//        fileReader = new RandomAccessFile(tmpFile, "rw");
//        // 写入
//        while ((fLen = fileReader.read(buf)) != -1) {
//          fileWrite.write(buf, 0, fLen);
//        }
//        start += tmpFile.length();
//      }
//    } finally {
//      if (fileWrite != null) {
//        fileWrite.close();
//      }
//      if (fileReader != null) {
//        fileReader.close();
//      }
//    }
//
//
//  }
//}
