package com.bcgogo.mq;

import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.MapUtils;
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

  public static void addSession(Long id, IoSession session) {
    addSession(StringUtil.valueOf(id), session);
  }

  public static void addSession(String id, IoSession session) {
    LOG.info("mq:addSession id:{}", id);
    if (StringUtils.isBlank(id)) {
      return;
    }
    id = id.trim();
    String fromUserName = String.valueOf(session.getAttribute("fromUserName"));
    IoSession existSession = getSessionByFromUserName(fromUserName);
    if (existSession != null) {
      LOG.info("id:{},mq:fromUserName:{} is exist,will remove",existSession.getId(),fromUserName);
      sessionMap.remove(existSession.getId());
      existSession.close(true);
    }
    sessionMap.put(id, session);
    LOG.info("mq:addSession id:{},fromUserName:{},success", id, session.getAttribute("fromUserName"));
  }

  public static void removeSession(Long id) {
    removeSession(StringUtil.valueOf(id));
  }

  public static void removeSession(String id) {
    if (StringUtils.isBlank(id)) {
      return;
    }
    id = id.trim();
    if (sessionMap.get(id) != null) {
      sessionMap.remove(id);
    }
  }

  public static IoSession getSessionById(Long id) {
    return getSessionById(StringUtil.valueOf(id));
  }

  public static IoSession getSessionById(String id) {
    if (StringUtils.isBlank(id)) {
      return null;
    }
    return sessionMap.get(id);
  }

  public static Map<String, IoSession> getSessionMap() {
    return sessionMap;
  }

  public static IoSession getSessionByFromUserName(String fromUserName) {
    Map<String, IoSession> sessionMap = getSessionMap();
    if (MapUtils.isEmpty(sessionMap)) return null;
    IoSession session = null;
    for (String key : sessionMap.keySet()) {
      session = sessionMap.get(key);
      if (fromUserName.equals(session.getAttribute("fromUserName"))) {
        return session;
      }
    }
    return null;
  }

}
