package com.bcgogo.pojo;

import com.bcgogo.pojo.constants.Constant;
import com.bcgogo.pojo.enums.PushMessageType;
import com.bcgogo.pojo.message.MQLoginMessageDTO;
import com.bcgogo.pojo.message.MQMessageDTO;
import com.bcgogo.pojo.message.MQMessageItemDTO;
import com.bcgogo.pojo.protocol.MMsgProtocol;
import com.bcgogo.pojo.response.ApiMirrorLoginResponse;
import com.bcgogo.pojo.response.ApiResponse;
import com.bcgogo.pojo.response.HttpResponse;
import com.bcgogo.pojo.util.HttpUtils;
import com.bcgogo.pojo.util.JsonUtil;
import com.bcgogo.service.SocketSessionManager;
import com.bcgogo.service.filter.CharsetCodecFactory;
import com.bcgogo.service.handler.MinaClientIoHandler;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-6-5
 * Time: 13:19
 */
public class MinaClientMocker {
  public static final Logger LOG = LoggerFactory.getLogger(MinaClientMocker.class);


  private String SERVER_IP;
  private int SERVER_PORT;
  public NioSocketConnector connector;
  private String imei;
  private String appUserNo;
  private String sessionId;


  public MinaClientMocker(String imei, String ip, int port) throws IOException {
    this.imei = imei;
    this.SERVER_IP = ip;
    this.SERVER_PORT = port;
    createNioSocketConnector(imei);
    sessionId = getSessionId(imei);
  }

  public String getSessionId(String imei) throws IOException {
    LOG.info("mirror msg Login...");
    String loginUrl = Constant.URL_MIRROR_LOGIN.replace("{IMEI}", imei);
    HttpResponse response = HttpUtils.sendGet(loginUrl);
    LOG.info("登录mirrorLogin result:{}", response.getContent());
    ApiMirrorLoginResponse apiResponse = (ApiMirrorLoginResponse) JsonUtil.jsonToObject(response.getContent(), ApiMirrorLoginResponse.class);
     appUserNo = apiResponse.getAppUserDTO().getUserNo();
    sessionId = response.getCookie();
    return (sessionId.split(";")[0]).replace("JSESSIONID=", "");
  }

  public void createNioSocketConnector(String id) {
    //Create TCP/IP connection
    connector = new NioSocketConnector();
    //创建接受数据的过滤器
    DefaultIoFilterChainBuilder chain = connector.getFilterChain();
    //      chain.addLast("myChain", new ProtocolCodecFilter(new JMessageProtocolCodecFactory(Charset.forName("UTF-8"))));
    chain.addLast("myChain", new ProtocolCodecFilter(new CharsetCodecFactory()));
    //服务器的消息处理器：一个 SimpleMinaClientHandler 对象
    connector.setHandler(new MinaClientIoHandler());
    //set connect timeout
    connector.setConnectTimeoutMillis(30 * 1000);
    //连接到服务器：
    LOG.info("connect to nio socket,ip:{},port:{}",SERVER_IP,SERVER_PORT);
    ConnectFuture future = connector.connect(new InetSocketAddress(SERVER_IP, SERVER_PORT));
    LOG.info("connect finish");
    // 等待连接创建完成
    future.awaitUninterruptibly();
    IoSession session = future.getSession();
    SocketSessionManager.addSession(id, session);
  }

  public void login() throws UnsupportedEncodingException {
    LOG.info("do login,name:{},pass:{}",imei,sessionId);
    IoSession session = SocketSessionManager.getSessionById(imei);
    MQLoginMessageDTO loginMessageDTO = new MQLoginMessageDTO();
    loginMessageDTO.setName(appUserNo);
    loginMessageDTO.setPass(sessionId);
    String msg = JsonUtil.objectToJson(loginMessageDTO);
    MMsgProtocol protocol = new MMsgProtocol(Constant.MIRROR_MSG_LOGIN, msg.getBytes("UTF-8"));
    session.write(protocol.toProtocol());
    LOG.info("login finish ");
  }

  public void sendTalk() throws UnsupportedEncodingException {
    IoSession session = SocketSessionManager.getSessionById(imei);
    MQMessageDTO messageDTO = new MQMessageDTO();
    messageDTO.setAppUserNo(appUserNo);
    messageDTO.setSendTime(System.currentTimeMillis());
    MQMessageItemDTO itemDTO=new MQMessageItemDTO();
    itemDTO.setMsgId(555555444111111L);
    itemDTO.setTitle("talk");
    itemDTO.setContent("你好,I am from mirror,time="+System.currentTimeMillis());
    itemDTO.setFromUserName(appUserNo);
    itemDTO.setToUserName(Constant.openId);
    itemDTO.setCreateTime(System.currentTimeMillis());
    itemDTO.setType(0);
    List<MQMessageItemDTO> itemDTOs=new ArrayList<MQMessageItemDTO>();
    itemDTOs.add(itemDTO);
    messageDTO.setItemDTOs(itemDTOs);
    String msg = JsonUtil.objectToJson(messageDTO);
    MMsgProtocol protocol = new MMsgProtocol(Constant.MIRROR_MSG_DATA, msg.getBytes("UTF-8"));
    session.write(protocol.toProtocol());
    LOG.info("sendTalk finish ");
  }

  public void quit() {
    MMsgProtocol protocol = new MMsgProtocol(Constant.MIRROR_MSG_QUIT, null);
    IoSession session = SocketSessionManager.getSessionById(imei);
    session.write(protocol.toProtocol());
    connector.dispose();
  }

}
