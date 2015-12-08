package com.bcgogo.mocker;

import com.bcgogo.obd.mina.OBDCharsetCodecFactory;
import com.bcgogo.obd.mina.OBDClientIoHandler;
import com.bcgogo.pojox.api.response.HttpResponse;
import com.bcgogo.pojox.util.BinaryUtil;
import com.bcgogo.pojox.util.DateUtil;
import com.bcgogo.pojox.util.HttpUtils;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.bson.types.Binary;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-10-20
 * Time: 下午4:17
 */
public class POBDClientMocker {

  private static final Logger LOG = LoggerFactory.getLogger(POBDClientMocker.class);
  private static IoSession session;

  @BeforeClass
  public static void setUp() throws Exception {
    String SERVER_IP = "g1.bcgogo.com";
//    SERVER_IP = "127.0.0.1";
    int SERVER_PORT = 60113;
    NioSocketConnector connector = new NioSocketConnector();
    DefaultIoFilterChainBuilder chain = connector.getFilterChain();
    chain.addLast("myChain", new ProtocolCodecFilter(new OBDCharsetCodecFactory()));
    connector.setHandler(new OBDClientIoHandler());
    connector.setConnectTimeoutMillis(30 * 1000);
    //连接到服务器：
    LOG.info("connect to nio socket,ip:{},port:{}", SERVER_IP, SERVER_PORT);
    ConnectFuture future = connector.connect(new InetSocketAddress(SERVER_IP, SERVER_PORT));
    LOG.info("connect finish");
    // 等待连接创建完成
    future.awaitUninterruptibly();
    session = future.getSession();
  }

  @AfterClass
  public static void after() throws Exception {

  }

  /**
   * 获取后视镜二维码
   *
   * @throws java.io.IOException
   */
  @Test
  public void toBe() throws IOException {
//    String url = "http://127.0.0.1:8080/apix/toBe";
//    HttpResponse response = HttpUtils.sendGet(url);
//    LOG.info("httpResponse result:{}", response.getContent());
    LOG.debug("{}",BinaryUtil.hexString2String("38363336303034333438"));
  }


  @Test
  public void driveLogTest() throws UnsupportedEncodingException, InterruptedException {
    LOG.info("logging...");
    String imei = "8886004345";
//    String imei = "8636000652";
    String hex_imei = BinaryUtil.toHexString(imei);
    String hex_time = BinaryUtil.toHexString(DateUtil.convertDateLongToDateString("HHmmss", System.currentTimeMillis()));
    String hex_date = BinaryUtil.toHexString(DateUtil.convertDateLongToDateString("ddMMyyy", System.currentTimeMillis()));
    String hex_lat =BinaryUtil.toHexString("3118.1254");
    LOG.info("行程开始");
    String startHexString = "7E2A48512C" + hex_imei + "2C56392C" + hex_time + "2C562C"+hex_lat+"2C4E2C31323034342E303033392C452C3030302E30302C3030302C" + hex_date + "2C46464642444646462C45352C333030302C3436302C30312C31373639352C34383531362C433623517E";
    session.write(BinaryUtil.hexString2Byte(startHexString));
    LOG.info("上传车况");
    String hexString;
    for (int i = 0; i < 1; i++) {
      hexString = "7E24" + imei + "07215325091531247900C6120374190E022266FFFBDFFFFD41300052000000E163042B214B0101CC01451FCBE500420047C2010007E100042D000000057B0000000C159600000D2D0000001F0000000042364C0000440000000046000000004C00000000880000000189000000248A000000008B000000008CF07E7E2A48512C383633363030303635322C5631302C3039313433312C412C333131362E313937302C4E2C31323034342E313334372C452C3030302E31302C3030302C3232303931352C46464646464246462C46442C333030302C3436302C30312C31373639352C31323530352C43362C3535382C312C32382C362C3131352C3137362C302C302C3023217E";
      session.write(BinaryUtil.hexString2Byte(hexString));
      hexString = "7E24" + imei + "07215325091531247900C6120374190E022266FFFBDFFFFD41300052000000E163042B214B0101CC01451FCBE500420047C2010007E100042D000000057B0000000C159600000D2D0000001F0000000042364C0000440000000046000000004C00000000880000000189000000248A000000008B000000008CF07E7E2A48512C383633363030303635322C5631302C3039313433312C412C333131362E313937302C4E2C31323034342E313334372C452C3030302E31302C3030302C3232303931352C46464646464246462C46442C333030302C3436302C30312C31373639352C31323530352C43362C3535382C312C32382C362C3131352C3137362C302C302C3023217E";
//      session.write(BinaryUtil.hexString2Byte(hexString));
    }
    LOG.info("行程结束");
    hex_time = BinaryUtil.toHexString(DateUtil.convertDateLongToDateString("HHmmss", System.currentTimeMillis() + 1000 * 60 * 10));
    String endHexString = "7E2A48512C" + hex_imei + "2C5631302C" + hex_time + "2C412C333131362E313235342C4E2C31323034332E393731312C452C3030302E32302C3030302C" + hex_date + "2C46464646464246462C46442C333030302C3436302C30312C31373639352C34383531362C43362C313038362C322C35302C382C3235342C3233342C332C302C3023127E";
    session.write(BinaryUtil.hexString2Byte(endHexString));
    LOG.info("updateGsmVehicleData finish ");
    Thread.sleep(2000);
  }

}
