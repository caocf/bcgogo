package com.bcgogo.utils;

import junit.framework.Assert;
import org.junit.Test;
import sun.misc.BASE64Encoder;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 12-10-11
 * Time: 上午12:31
 */
public class EncryptionUtilTest {
  String pwdMd456 = "25cf8b51c773f3f8dc8b4be867a9a2";
  String pwdMd111111 = "96e79218965eb72c92a549dd5a33112";

  String pwd456 = "456";
  String pwd111111 = "111111";

  String pwdLong = "noMatterHowLongThePasswordIsTheLengthOfEncryptionResultIsAlways64";
  String pwdHz = "汉字测试";

  @Test
  public void testComputeSHA256() throws Exception{
    Assert.assertEquals(pwdMd456, EncryptionUtil.computeMD5(pwd456));
    Assert.assertEquals(pwdMd111111, EncryptionUtil.computeMD5(pwd111111));

    Assert.assertEquals(EncryptionUtil.encryptPassword(pwd456, 0L), EncryptionUtil.computeSHA256(pwdMd456 + 0L));
    Assert.assertEquals(EncryptionUtil.encryptPassword(pwd111111, 0L), EncryptionUtil.computeSHA256(pwdMd111111 + 0L));
  }

  @Test
  public void testSHA256Length() throws Exception{
    System.out.println(EncryptionUtil.encryptPassword(pwd456, 0L));
    System.out.println(EncryptionUtil.encryptPassword(pwd111111, 0L));
    System.out.println(EncryptionUtil.encryptPassword(pwdLong, 0L));
    System.out.println(EncryptionUtil.encryptPassword(pwdHz, 0L));

    Assert.assertEquals(64, EncryptionUtil.encryptPassword(pwd456, 0L).length());
    Assert.assertEquals(64, EncryptionUtil.encryptPassword(pwdLong, 0L).length());
    Assert.assertEquals(64, EncryptionUtil.encryptPassword(pwdHz, 0L).length());
  }

  @Test
  public void testMD5Length(){
    System.out.println(EncryptionUtil.computeMD5(pwd456) + " : " + EncryptionUtil.computeMD5(pwd456).length());   //30
    System.out.println(EncryptionUtil.computeMD5(pwdLong) +" : "+EncryptionUtil.computeMD5(pwdLong).length());    //29
    System.out.println(EncryptionUtil.computeMD5(pwd111111) + " : " + EncryptionUtil.computeMD5(pwd111111).length());   //31

    System.out.println(EncryptionUtil.computeMD5Improved(pwd456) + " : " + EncryptionUtil.computeMD5Improved(pwd456).length());   //32
    System.out.println(EncryptionUtil.computeMD5Improved(pwdLong) +" : "+EncryptionUtil.computeMD5Improved(pwdLong).length());    //32
    System.out.println(EncryptionUtil.computeMD5Improved(pwd111111) + " : " + EncryptionUtil.computeMD5Improved(pwd111111).length());   //32

    Assert.assertEquals(32, EncryptionUtil.computeMD5Improved(pwd456).length());
    Assert.assertEquals(32, EncryptionUtil.computeMD5Improved(pwdLong).length());
    Assert.assertEquals(32, EncryptionUtil.computeMD5Improved(pwdHz).length());
  }

  @Test
  public void testBase64(){
    String content = "{\"save-key\":\"{filemd5}-{year}{mon}{day}{hour}{min}{sec}-{random}{.suffix}\",\"expiration\":1372134767930,\"bucket\":\"dawngate-space1\",\"return-url\":\"http://localhost:8080/web/customer.do?method\\u003dcustomerdata\",\"content-length-rang\":\"1024,10240000\",\"allow-file-type\":\"jpg,jpeg,gif,png\",\"xGmkerlThumbnail\":\"150X130\"}";
    String encoded = new BASE64Encoder().encode(content.getBytes());

  }
}
