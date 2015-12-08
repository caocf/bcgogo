package com.bcgogo.pojo;

import com.bcgogo.pojo.util.BinaryUtil;
import com.bcgogo.service.filter.OBDCharsetCodecFactory;
import com.bcgogo.service.handler.OBDClientIoHandler;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-9-18
 * Time: 下午4:05
 */
public class OBDMocker {
  private static final Logger LOG = LoggerFactory.getLogger(OBDMocker.class);

//  private static String SERVER_IP = "127.0.0.1";
  private static String SERVER_IP = "g1.bcgogo.com";
  private static int SERVER_PORT = 60113;
  private static IoSession session;

  public OBDMocker() {
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

  public void driveLogTest() throws UnsupportedEncodingException, InterruptedException {
    LOG.info("logging...");
//    String loginMsg = "~*HQ,8636000652,V1,000033,V,3116.1355,N,12044.0039,E,000.00,000,010113,FFFBDFFF,E5,3000,460,01,17695,48516,C6#Q~";
//    String loginHexString = "7E2A48512C383633363030303635322C56312C3039313030322C562C333131362E313335352C4E2C31323034342E303033392C452C3030302E30302C3030302C3232303931352C46464642444646462C46442C333030302C3436302C30312C31373639352C34383531352C433623257E";
//    session.write(BinaryUtil.hexString2Byte(loginHexString));
    LOG.info("行程开始");
    String startHexString = "7E2A48512C383633363030303635322C56392C3030303033332C562C333131362E313335352C4E2C31323034342E303033392C452C3030302E30302C3030302C3031303131332C46464642444646462C45352C333030302C3436302C30312C31373639352C34383531362C433623517E";
    session.write(BinaryUtil.hexString2Byte(startHexString));
    LOG.info("上传车况");
//    //基本类型
    String hexString = "7E24863600065217085522091531161350C6120440030C000000FFFBDFFFFD41300052000000A6630021064B0001CC01451FBD83000001CB7E";
//    session.write(BinaryUtil.hexString2Byte(hexString));
//    //01
    for (int i = 0; i < 1; i++) {
      hexString = "7E24863600065207215325091531247900C6120374190E022266FFFBDFFFFD41300052000000E163042B214B0101CC01451FCBE500420047C2010007E100042D000000057B0000000C159600000D2D0000001F0000000042364C0000440000000046000000004C00000000880000000189000000248A000000008B000000008CF07E";
         session.write(BinaryUtil.hexString2Byte(hexString));
//      hexString = "7E24863600065209574209101532022120C6119358930E000000FFFBDFFFFD413000520000010D6307035B070101CC0134331CA7038E0047C201000"+"7E"+"100043304000005810400000C0AEA00000D00E800001F02B4000042361D0000447FFF0000465FFF00004C04FF0000880000000089000000008A000000008B000080002C0E7E";
//      session.write(BinaryUtil.hexString2Byte(hexString));
//      hexString = "7E24863600065209101522091531161340C6120440120E003159FFFBDFFFFD41300052000000A6630025044B0101CC01451FBD8300090047C2010007E100044400000005490000000C169400000D0E0000001F000000004236B30000440000000046000000004C00000000880000000389000002B78A000000008B0000000002287E";
//      session.write(BinaryUtil.hexString2Byte(hexString));
//      hexString = "7E24863600065209103522091531161070C6120440330E003168FFFBDFFFFD41300052000000A663002007480101CC01451FBD8300080047C2010007E1000457000000054B0000000C18DD00000D0F0000001F0000000042367C0000440000000046000000004C00000000880000000389000000E58A000000008B0000000004E17E";
//      session.write(BinaryUtil.hexString2Byte(hexString));
//      hexString = "7E24863600065209103522091531161070C6120440330E003168FFFBDFFFFD41300052000000A663002007480101CC01451FBD8300080047C2010007E1000457000000054B0000000C18DD00000D0F0000001F0000000042367C0000440000000046000000004C00000000880000000389000000E58A000000008B0000000004E17E";
//      session.write(BinaryUtil.hexString2Byte(hexString));
//      hexString = "7E24863600065209111522091531160900C6120440040E000000FFFBDFFFFD41300052000000A663002008490101CC01451FBD8300D10047C2010007E1000459000000054F0000000C000000000D000000001F000000004233490000440000000046000000004C00000000880000000189000000008A000000008B0000000006057E";
//      session.write(BinaryUtil.hexString2Byte(hexString));
    }
//    LOG.info("行程结束");
//    String endHexString = "7E2A48512C383633363030303635322C5631302C3134313030392C562C333230322E323532312C4E2C31313933352E393238302C452C3030302E30302C3030302C3039313031352C46464646464246462C46442C333030302C3436302C30312C31333336332C373333352C43362C302C332C302C302C313436382C302C302C302C3023077E";
//    String endHexString = "7E2A48512C383633363030303635322C5631302C3039313433312C412C333131362E313937302C4E2C31323034342E313334372C452C3030302E31302C3030302C3232303931352C46464646464246462C46442C333030302C3436302C30312C31373639352C31323530352C43362C3535382C312C32382C362C3131352C3137362C302C302C3023217E";
//    String endHexString = "302C3123277E7E2A48512C383633363030303635322C5631302C3039313433312C412C333131362E313937302C4E2C31323034342E313334372C452C3030302E31302C3030302C3232303931352C46464646464246462C46442C333030302C3436302C30312C31373639352C31323530352C43362C3535382C312C32382C362C3131352C3137362C302C302C3023217E";
//    session.write(BinaryUtil.hexString2Byte(endHexString));
    LOG.info("updateGsmVehicleData finish ");
  }

}
