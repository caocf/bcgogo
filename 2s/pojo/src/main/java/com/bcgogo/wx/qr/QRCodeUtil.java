//package com.bcgogo.wx.qr;
//
///**
// * Created by IntelliJ IDEA.
// * User: ndong
// * Date: 14-9-5
// * Time: 下午3:00
// * To change this template use File | Settings | File Templates.
// */
//import java.awt.Color;
//import java.awt.Graphics2D;
//import java.awt.image.BufferedImage;
//import java.io.BufferedInputStream;
//import java.io.File;
//import java.io.IOException;
//import java.net.URL;
//
//import javax.imageio.ImageIO;
//
////import com.swetake.util.Qrcode;
////import jp.sourceforge.qrcode.QRCodeDecoder;
////import jp.sourceforge.qrcode.data.QRCodeImage;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//public class QRCodeUtil {
//  private static final Logger LOG = LoggerFactory.getLogger(QRCodeUtil.class);
//
//
//  public static final int DEFAULT_HEIGHT=140;
//  public static final int DEFAULT_WIDTH=140;
//
//
//  /**
//   * 生成二维码(QRCode)图片
//   * @param content
//   * @param imgPath
//   */
//  public static void encoderQRCode(String content, String imgPath) {
//    try {
//
//      Qrcode qrcodeHandler = new Qrcode();
//      qrcodeHandler.setQrcodeErrorCorrect('M');
//      qrcodeHandler.setQrcodeEncodeMode('B');
//      qrcodeHandler.setQrcodeVersion(7);
//      byte[] contentBytes = content.getBytes("gbk");
//      BufferedImage bufImg = new BufferedImage(DEFAULT_WIDTH, DEFAULT_HEIGHT,BufferedImage.TYPE_INT_RGB);
//      Graphics2D gs = bufImg.createGraphics();
//      gs.setBackground(Color.WHITE);
//      gs.clearRect(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT);
//      // 设定图像颜色 > BLACK
//      gs.setColor(Color.BLACK);
//      // 设置偏移量 不设置可能导致解析出错
//      int pixoff = 2;
//      // 输出内容 > 二维码
//      if (contentBytes.length > 0 && contentBytes.length < 120) {
//        boolean[][] codeOut = qrcodeHandler.calQrcode(contentBytes);
//        for (int i = 0; i < codeOut.length; i++) {
//          for (int j = 0; j < codeOut.length; j++) {
//            if (codeOut[j][i]) {
//              gs.fillRect(j * 3 + pixoff, i * 3 + pixoff, 3, 3);
//            }
//          }
//        }
//      }else {
//        System.err.println("QRCode content bytes length = "
//          + contentBytes.length + " not in [ 0,120 ]. ");
//      }
//      gs.dispose();
//      bufImg.flush();
//      // 生成二维码QRCode图片
//      File imgFile = new File(imgPath);
//      ImageIO.write(bufImg, "png", imgFile);
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
//
//  }
//
//  public static String decoderQRCode(String imgUrl) throws IOException {
//    URL url = new URL(imgUrl);
//    BufferedInputStream bis = new BufferedInputStream(url.openStream());
//    return decoderQRCode(ImageIO.read(bis));
//  }
//
//  public static String decoderPathQRCode(String imgPath) throws IOException {
//    File imageFile = new File(imgPath);
//    return decoderQRCode(ImageIO.read(imageFile));
//  }
//
//  /**
//   * 解码二维码
//   * @param bufImg
//   * @return
//   */
//  public static String decoderQRCode(BufferedImage bufImg) {
//    String decodedData = null;
//    try {
//      J2SEImage image = new J2SEImage(bufImg);
//      QRCodeDecoder decoder = new QRCodeDecoder();
//      decodedData = new String(decoder.decode(image));
//      // try {
//      // System.out.println(new String(decodedData.getBytes("gb2312"),
//      // "gb2312"));
//      // } catch (Exception e) {
//      // // TODO: handle exception
//      // }
//    }catch (Exception e) {
//      LOG.error(e.getMessage(),e);
//    }
//    return decodedData;
//  }
//
//  static class J2SEImage implements QRCodeImage {
//    BufferedImage bufImg;
//
//    public J2SEImage(BufferedImage bufImg) {
//      this.bufImg = bufImg;
//    }
//
//    public int getWidth() {
//      return bufImg.getWidth();
//    }
//
//    public int getHeight() {
//      return bufImg.getHeight();
//    }
//
//    public int getPixel(int x, int y) {
//      return bufImg.getRGB(x, y);
//    }
//
//  }
//
//  /**
//   * @param args the command line arguments
//   */
//  public static void main(String[] args) throws IOException {
//    /******************* demo1 ********************/
////    String imgPath = "c:/QRCode.png";
////    String imgPath1 = "d:/scene-1.png";
////    String content = "I am admin,open the door!";
////
////    QRCodeUtil.encoderQRCode(content, imgPath);
////    System.out.println("encoder QRcode success");
////    String decoderContent = QRCodeUtil.decoderQRCode(imgPath);
////    System.out.println("解析结果如下：");
////    System.out.println(decoderContent);
////    System.out.println("========decoder success!!!");
//
////   String result=decoderQRCode("http://mmbiz.qpic.cn/mmbiz/nqU6mYZLg8MgSIMtLOkmy1MGu89EiahMoelKC3iabJwAYcUdNY1ZVB8ibiawnqoSQnvWZ6Ou9iaOhiaRKNUibPZicF5zXg/0") ;
////     System.out.println(result);
//
//  }
//}
