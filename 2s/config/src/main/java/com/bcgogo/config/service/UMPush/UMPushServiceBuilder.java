package com.bcgogo.config.service.UMPush;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by XinyuQiu on 14-4-29.
 */
public class UMPushServiceBuilder {

  private int pooledMax = 1;
  private ExecutorService executor = null;
  private UMPushDelegate pushDelegate = null;

  public UMPushService buildUnicastService() {
    return new UMUnicastPushService(executor,pushDelegate);
  }

  public UMPushServiceBuilder setMaxPoolSize(int maxConnections) {
    this.pooledMax = maxConnections;
    this.executor = Executors.newFixedThreadPool(maxConnections);
    return this;
  }

  public UMPushServiceBuilder setDelegate(UMPushDelegateAdapter pushDelegate) {
    this.pushDelegate = pushDelegate;
    return this;
  }
}
