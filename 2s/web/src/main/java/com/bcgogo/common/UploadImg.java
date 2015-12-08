package com.bcgogo.common;

/**
 * Created by IntelliJ IDEA.
 * User: zhangchuanlong
 * Date: 12-7-4
 * Time: 上午10:57
 * To change this template use File | Settings | File Templates.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class UploadImg {
    private static final Logger LOG = LoggerFactory.getLogger(UploadImg.class);
  public static BufferedImage getResizeImg(InputStream inputStream, int width, int height,
                                           boolean isResize) {

    BufferedImage srcimg, tag;
    tag = null;
    int new_w; // 压缩后大小
    int new_h;
    boolean Resize;// 是否等比缩放

    try {
      srcimg = ImageIO.read(inputStream);
      if (isResize) {// 等比缩放
        LOG.debug("图片等比缩放");
        double rate1 = ((double) srcimg.getWidth(null)) / (double) width + 0.1;
        double rate2 = ((double) srcimg.getHeight(null)) / (double) height + 0.1;
        double rate = rate1 > rate2 ? rate1 : rate2;
        new_w = (int) (((double) srcimg.getWidth(null)) / rate);
        new_h = (int) (((double) srcimg.getHeight(null)) / rate);
      } else {
        new_w = width;
        new_h = height;
      }
      Image image = srcimg.getScaledInstance(new_w, new_h,
          Image.SCALE_DEFAULT);
      tag = new BufferedImage(new_w, new_h, BufferedImage.TYPE_INT_RGB);
      Graphics g = tag.getGraphics();
      g.drawImage(image, 0, 0, null); // 绘制缩小后的图
      g.dispose();
      return tag;

    } catch (Exception e) {
      LOG.error("等比缩放错误！"+e.getMessage(),e);
    }
    return tag;
  }

  public static byte[] getBytesOfIma(BufferedImage bi) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    BufferedImage tag = bi;
    try {
      if(tag!=null)  {
             ImageIO.write(tag, "jpg", baos);
      }
    } catch (IOException e) {
      LOG.error(e.getMessage(),e);
    }
    byte[] bytes = baos.toByteArray();
    return bytes;
  }

}
