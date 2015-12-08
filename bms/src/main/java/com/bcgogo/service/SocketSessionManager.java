package com.bcgogo.service;

import org.apache.commons.lang.StringUtils;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Jimuchen
 * Date: 14-3-7
 * Time: 上午10:33
 * To change this template use File | Settings | File Templates.
 */
public class SocketSessionManager {
  private static final Logger LOG = LoggerFactory.getLogger(SocketSessionManager.class);

  private static final Map<String, IoSession> sessionMap = new ConcurrentHashMap<String, IoSession>();

  public static void addSession(String id, IoSession session){
    if(StringUtils.isBlank(id)){
      return;
    }
    id = id.trim();
    if(sessionMap.get(id) == null){
      sessionMap.put(id, session);
    }else{
      if(sessionMap.get(id).getId() != session.getId()){
        sessionMap.put(id, session);
      }
    }
  }

  public static void removeSession(String imei){
    if(StringUtils.isBlank(imei)){
      return;
    }
    imei = imei.trim();
    if(sessionMap.get(imei) != null){
      sessionMap.remove(imei);
    }
  }

  public static IoSession getSessionById(String id){
    if(StringUtils.isBlank(id)){
      return null;
    }
    return sessionMap.get(id);
  }

  public static Map<String, IoSession> getSessionMap() {
    return sessionMap;
  }

}
