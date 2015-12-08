package com.bcgogo.utils;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

/**
 * 二维码生成和解析工具
 * Created with IntelliJ IDEA.
 * User: zoujianhong
 * Date: 13-7-20
 * Time: 下午1:57
 */
public class RQUtil {
  private static final Logger LOG = LoggerFactory.getLogger(RQUtil.class);
  private static final int BLACK = 0xff000000;
  private static final int WHITE = 0xFFFFFFFF;

  /**
   * 生成二维码
   *
   * @param str    内容
   * @param height 高度（px）
   */
  public static BufferedImage getRQ(String str, Integer height) {
    if (height == null || height < 100) {
      height = 200;
    }
    try {
      // 编码设置编码方式为：utf-8，
      Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
      hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
      BitMatrix bitMatrix = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, height, height, hints);
      return toBufferedImage(bitMatrix);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * 写到文件中
   *
   * @param str  内容
   * @param height 高
   * @param file   File
   * @throws java.io.IOException
   */
  public static void getRQWriteFile(String str, Integer height, File file)
      throws IOException {
    BufferedImage image = getRQ(str, height);
    ImageIO.write(image, "png", file);
  }
  /**
   * 写到InputStream中
   *
   * @param str  内容  shopId,shopName
   * @param height 高
   * @throws java.io.IOException
   */
  public static byte[] getRQImageByte(String str, Integer height) throws IOException {
    BufferedImage image = getRQ(str, height);
//    image.flush();
    ByteArrayOutputStream bs = new ByteArrayOutputStream();
    ImageIO.write(image, "png", ImageIO.createImageOutputStream(bs));
    return bs.toByteArray();
  }

  /**
   * 转换成图片
   *
   * @param bitMatrix 位矩阵
   * @return BufferedImage
   */
  private static BufferedImage toBufferedImage(BitMatrix bitMatrix) {
    int width = bitMatrix.getWidth();
    int height = bitMatrix.getHeight();
    BufferedImage image = new BufferedImage(width, height,
        BufferedImage.TYPE_INT_ARGB);
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        image.setRGB(x, y, bitMatrix.get(x, y) ? BLACK : WHITE);
      }
    }
    return image;
  }

  /**
   * 解码 各类型条码
   */
  public static String decodeRQ(File file) {
    BufferedImage image;
    try {
      if (file == null || !file.exists()) {
        throw new Exception(" File not found:" + (file != null ? file.getPath() : ""));
      }
      image = ImageIO.read(file);
      LuminanceSource source = new BufferedImageLuminanceSource(image);
      BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
      Result result;
      // 解码设置编码方式为：utf-8，
      Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
      hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
      result = new MultiFormatReader().decode(bitmap, hints);
      return result.getText();

    } catch (Exception e) {
      LOG.error(e.getMessage(),e);
    }

    return null;
  }

  public static void main(String[] args) throws Exception {
    File file = new File("C:/b1.jpg");
        RQUtil.getRQWriteFile("苏州统购信息科技有限公司", 200, file);
    RQUtil.getRQWriteFile("10000010001040009", 200, file);
    System.out.println("-----成生成功----");
    System.out.println();
    String s = RQUtil.decodeRQ(file);
    System.out.println("-----解析成功----");
    System.out.println(s);
  }


}
