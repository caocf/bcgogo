package com.bcgogo.notification.client;

import com.bcgogo.notification.client.lianyu.LianYuHttpSmsClient;
import com.bcgogo.notification.client.lianyu.LianYuSmsClient;
import com.bcgogo.notification.client.yimei.YimeiHttpSmsClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: ZhangJuntao
 * Date: 13-5-7
 * Time: 下午4:05
 */
public class ClientFactory {
  private final static Map<BcgogoClientCore, SmsClient> _clients = new ConcurrentHashMap<BcgogoClientCore, SmsClient>();
  private final static Map<BcgogoClientCore, LianYuSmsClient> _client_lianyu = new ConcurrentHashMap<BcgogoClientCore, LianYuSmsClient>();

  static {
//    _clients.put(BcgogoClientCore.YimeiEmbeddedSmsClient, new YimeiEmbeddedSmsClient());
    _clients.put(BcgogoClientCore.YimeiHttpSmsClient, new YimeiHttpSmsClient());
    _client_lianyu.put(BcgogoClientCore.LianYuHttpSmsClient, new LianYuHttpSmsClient());
  }

  public enum BcgogoClientCore {
    YimeiEmbeddedSmsClient,
    YimeiHttpSmsClient,
    LianYuHttpSmsClient
  }

  public static SmsClient getYimeiEmbeddedSmsClient() {
    return _clients.get(BcgogoClientCore.YimeiEmbeddedSmsClient);
  }

  public static SmsClient getYimeiHttpSmsClient() {
    return _clients.get(BcgogoClientCore.YimeiHttpSmsClient);
  }

  public static LianYuSmsClient getLianYuHttpSmsClient() {
    return _client_lianyu.get(BcgogoClientCore.LianYuHttpSmsClient);
  }

  }
