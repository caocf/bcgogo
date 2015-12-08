package com.bcgogo.utils;

import liquibase.util.MD5Util;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import java.io.*;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;

/**
 * 加密工具类
 * User: Jimuchen
 * Date: 12-10-11
 * Time: 上午12:05
 */
public class EncryptionUtil {
  private static final Logger LOG = LoggerFactory.getLogger(EncryptionUtil.class);

  /**
   * 使用MD5加密，原先代码中直接使用了Liquibase提供的MD5Util。
   * @param source
   * @return
   */
  public static String computeMD5(String source) {
    return MD5Util.computeMD5(source);
  }

  /**
   * used in app user
   * MD5Util提供的方法得到的结果长度并不是标准的32位，因为byte小于16时转为16进制字符时Java默认不带前缀0
   * 例： 0x0d 输出为 d 而不是 0d
   * 但是，此方法基本不会用到，因为数据库中的密码已经使用MD5Util生成，为保持一致性，加密MD5时仍使用computeMD5方法
   * @param source
   * @return
   */
  public static String computeMD5Improved(String source){
    MessageDigest md;
    try{
      md = MessageDigest.getInstance("MD5");
      md.update(source.getBytes());
    }catch(Exception e){
      LOG.error(e.getMessage(), e);
      throw new RuntimeException(e);
    }
    byte byteData[] = md.digest();
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < byteData.length; i++) {
      sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
    }
    return sb.toString();
  }

  /**
   * 使用SHA-256加密
   * @param source
   * @return
   */
  public static String computeSHA256(String source){
    MessageDigest md;
    try{
      md = MessageDigest.getInstance("SHA-256");
      md.update(source.getBytes());
    }catch(Exception e){
      LOG.error(e.getMessage(), e);
      throw new RuntimeException(e);
    }
    byte byteData[] = md.digest();

    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < byteData.length; i++) {
      sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
    }
    return sb.toString();
  }

  /**
   * 原始的密码先使用MD5加密，与shopId组合后，再使用SHA256加密，得到数据库中存储的加密值
   * @param pwd
   * @return
   */
  public static String encryptPassword(String pwd, Long shopId) throws IllegalArgumentException{
    if(StringUtils.isEmpty(pwd)){
      throw new IllegalArgumentException("创建密码时密码为空！");
    }
    if(shopId == null){
      throw new IllegalArgumentException("创建密码时shopId为空！");
    }
    return computeSHA256(computeMD5(pwd) + shopId);
  }


  // 将 str 进行 BASE64 编码
  public static String getBASE64(String str) {
    if (str == null) return null;
    return (new BASE64Encoder()).encode(str.getBytes());
  }

  // 将 BASE64 编码的字符串 str 进行解码
  public static String decodeBase64(String str) {
    if (str == null) return null;
    BASE64Decoder decoder = new BASE64Decoder();
    try {
      byte[] b = decoder.decodeBuffer(str);
      return new String(b);
    } catch (Exception e) {
      return null;
    }
  }

  /******************************************下面是非对称加密 function **********************************************************************************************************/

  /**
   * 获取私钥
   * @param filename
   * @return
   * @throws Exception
   */
  public static PrivateKey getPrivateKey(String filename)throws Exception {
    File f = new File(filename);
    FileInputStream fis = new FileInputStream(f);
    DataInputStream dis = new DataInputStream(fis);
    byte[] keyBytes = new byte[(int)f.length()];
    dis.readFully(keyBytes);
    dis.close();
    PKCS8EncodedKeySpec spec =new PKCS8EncodedKeySpec(keyBytes);
    KeyFactory kf = KeyFactory.getInstance("RSA");
    return kf.generatePrivate(spec);
  }

  /**
   * 获取公钥
   * @param filename
   * @return
   * @throws Exception
   */
  public static PublicKey getPublicKey(String filename) throws Exception {
    File f = new File(filename);
    FileInputStream fis = new FileInputStream(f);
    DataInputStream dis = new DataInputStream(fis);
    byte[] keyBytes = new byte[(int)f.length()];
    dis.readFully(keyBytes);
    dis.close();
    X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
    KeyFactory kf = KeyFactory.getInstance("RSA");
    return kf.generatePublic(spec);
  }

  /**
   * 非对称加密 sample
   */
  public void asymmetricEncryptionSample() throws Exception, IOException, NoSuchPaddingException {
    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
    SecureRandom secureRandom = new SecureRandom(new Date().toString().getBytes());
    keyPairGenerator.initialize(1024, secureRandom);
    KeyPair keyPair = keyPairGenerator.genKeyPair();
    //生成公钥
    String publicKeyFilename = "D:/publicKeyFile";
    byte[] publicKeyBytes = keyPair.getPublic().getEncoded();
    FileOutputStream fos = new FileOutputStream(publicKeyFilename);
    fos.write(publicKeyBytes);
    fos.close();
    //生成私钥
    String privateKeyFilename = "D:/privateKeyFile";
    byte[] privateKeyBytes = keyPair.getPrivate().getEncoded();
    fos = new FileOutputStream(privateKeyFilename);
    fos.write(privateKeyBytes);
    fos.close();

    String input = "thisIsMyPassword$7788";
    Cipher cipher = Cipher.getInstance("RSA");
    RSAPublicKey pubKey = (RSAPublicKey) getPublicKey("d:/publicKeyFile");
    RSAPrivateKey privKey = (RSAPrivateKey) getPrivateKey("d:/privateKeyFile");

    //使用公钥加密
    cipher.init(Cipher.ENCRYPT_MODE, pubKey);
    byte[] cipherText = cipher.doFinal(input.getBytes());
    System.out.println("cipher: " + new String(cipherText));

    //用私钥解密
    cipher.init(Cipher.DECRYPT_MODE, privKey);
    byte[] plainText = cipher.doFinal(cipherText);
    System.out.println("plain : " + new String(plainText));
  }


  /******************************************下面是对称加密 function **********************************************************************************************************/

  //密钥算法 DESede即三重DES加密算法，也被称为3DES或者Triple DES。使用三(或两)个不同的密钥对数据块进行三次(或两次)DES加密
  private static final String KEY_ALGORITHM = "DESede";
  //填充方式
  private static final String DEFAULT_CIPHER_ALGORITHM = "DESede/ECB/ISO10126Padding";

  /**
   * 初始化密钥
   *
   * @return byte[] 密钥
   * @throws Exception
   */
  public static byte[] generateSecretKey() throws Exception{
    //返回生成指定算法的秘密密钥的 KeyGenerator 对象
    KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM);
    //初始化此密钥生成器，使其具有确定的密钥大小
    kg.init(168);
    //生成一个密钥
    SecretKey  secretKey = kg.generateKey();
    return secretKey.getEncoded();
  }

  /**
   * 转换密钥
   *
   * @param key   二进制密钥
   * @return Key  密钥
   * @throws Exception
   */
  private static Key toKey(byte[] key) throws Exception{
    //实例化DES密钥规则
    DESedeKeySpec dks = new DESedeKeySpec(key);
    //实例化密钥工厂
    SecretKeyFactory skf = SecretKeyFactory.getInstance(KEY_ALGORITHM);
    //生成密钥
    SecretKey  secretKey = skf.generateSecret(dks);
    return secretKey;
  }

  /**
   * 加密
   * @param data  待加密数据
   * @param key   密钥
   * @return byte[]   加密数据
   * @throws Exception
   */
  public static byte[] encrypt(byte[] data,Key key) throws Exception{
    return encrypt(data, key,DEFAULT_CIPHER_ALGORITHM);
  }

  /**
   * 加密
   *
   * @param data  待加密数据
   * @param key   二进制密钥
   * @return byte[]   加密数据
   * @throws Exception
   */
  public static byte[] encrypt(byte[] data,byte[] key) throws Exception{
    return encrypt(data, key,DEFAULT_CIPHER_ALGORITHM);
  }


  /**
   * 加密
   *
   * @param data  待加密数据
   * @param key   二进制密钥
   * @param cipherAlgorithm   加密算法/工作模式/填充方式
   * @return byte[]   加密数据
   * @throws Exception
   */
  public static byte[] encrypt(byte[] data,byte[] key,String cipherAlgorithm) throws Exception{
    //还原密钥
    Key k = toKey(key);
    return encrypt(data, k, cipherAlgorithm);
  }

  /**
   * 加密
   *
   * @param data  待加密数据
   * @param key   密钥
   * @param cipherAlgorithm   加密算法/工作模式/填充方式
   * @return byte[]   加密数据
   * @throws Exception
   */
  public static byte[] encrypt(byte[] data,Key key,String cipherAlgorithm) throws Exception{
    //实例化
    Cipher cipher = Cipher.getInstance(cipherAlgorithm);
    //使用密钥初始化，设置为加密模式
    cipher.init(Cipher.ENCRYPT_MODE, key);
    //执行操作
    return cipher.doFinal(data);
  }



  /**
   * 解密
   * @param data  待解密数据
   * @param key   二进制密钥
   * @return byte[]   解密数据
   * @throws Exception
   */
  public static byte[] decrypt(byte[] data,byte[] key) throws Exception{
    return decrypt(data, key,DEFAULT_CIPHER_ALGORITHM);
  }

  /**
   * 解密
   *
   * @param data  待解密数据
   * @param key   密钥
   * @return byte[]   解密数据
   * @throws Exception
   */
  public static byte[] decrypt(byte[] data,Key key) throws Exception{
    return decrypt(data, key,DEFAULT_CIPHER_ALGORITHM);
  }

  /**
   * 解密
   *
   * @param data  待解密数据
   * @param key   二进制密钥
   * @param cipherAlgorithm   加密算法/工作模式/填充方式
   * @return byte[]   解密数据
   * @throws Exception
   */
  public static byte[] decrypt(byte[] data,byte[] key,String cipherAlgorithm) throws Exception{
    //还原密钥
    Key k = toKey(key);
    return decrypt(data, k, cipherAlgorithm);
  }

  /**
   * 解密
   *
   * @param data  待解密数据
   * @param key   密钥
   * @param cipherAlgorithm   加密算法/工作模式/填充方式
   * @return byte[]   解密数据
   * @throws Exception
   */
  public static byte[] decrypt(byte[] data,Key key,String cipherAlgorithm) throws Exception{
    //实例化
    Cipher cipher = Cipher.getInstance(cipherAlgorithm);
    //使用密钥初始化，设置为解密模式
    cipher.init(Cipher.DECRYPT_MODE, key);
    //执行操作
    return cipher.doFinal(data);
  }

  public static String  showByteArray(byte[] data){
    if(null == data){
      return null;
    }
    StringBuilder sb = new StringBuilder("{");
    for(byte b:data){
      sb.append(b).append(",");
    }
    sb.deleteCharAt(sb.length()-1);
    sb.append("}");
    return sb.toString();
  }


  /**
   * 对称加密 sample
   */
  public static void symmetricEncryptionSample() throws Exception, IOException, NoSuchPaddingException {
    byte[] key = generateSecretKey();
    System.out.println("key："+ showByteArray(key));

    String data ="4f9b69507594e740d35f676c871acb67 ";
    System.out.println("加密前数据: string:"+data);
    System.out.println("加密前byte数据："+ showByteArray(data.getBytes()));
    byte[] encryptData = encrypt(data.getBytes(), key);
    System.out.println("2222: string:"+new String(encryptData));
    byte[] decryptData = decrypt(encryptData, key);
    System.out.println("加密后byte数据："+ showByteArray(decryptData));
    System.out.println("解密后数据: string:"+new String(decryptData));
  }




  /**
   * write keyFile to disk
   */
  private void initSecretKeyFile(){
    try {
      byte[] sKey = EncryptionUtil.generateSecretKey();
      String sKeyPathName="c:/s_k_file";
      FileOutputStream fos = new FileOutputStream(sKeyPathName);
      fos.write(sKey);
      fos.close();
      sKey = EncryptionUtil.generateSecretKey();
    } catch (Exception e) {
      LOG.error(e.getMessage(),e);
    }

  }


  public static void main(String[] args) throws Exception {
    //asymmetric encryption  sample
    symmetricEncryptionSample();

  }

}
