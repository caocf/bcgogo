package com.bcgogo.config.util;

import com.bcgogo.cache.SimpleMemcacheDInterface;
import com.bcgogo.cache.XmemcachedWrapper;
import com.bcgogo.common.CommonUtil;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.utils.ConfigConstant;
import com.bcgogo.utils.PropUtil;
import com.bcgogo.utils.ShopConstant;
import com.bcgogo.utils.StringUtil;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.command.BinaryCommandFactory;
import net.rubyeye.xmemcached.command.TextCommandFactory;
import net.rubyeye.xmemcached.impl.KetamaMemcachedSessionLocator;
import net.rubyeye.xmemcached.utils.AddrUtil;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;
import org.hibernate.cache.CacheException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by IntelliJ IDEA.
 * User: caiweili
 * Date: 5/15/12
 * Time: 10:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class MemCacheAdapter {
  private static int MAX_KEY_LEN = 255;
  private static final Logger LOG = LoggerFactory.getLogger(MemCacheAdapter.class);
  private static final AtomicBoolean _initialize = new AtomicBoolean(true);
  private static CountDownLatch _initialized = new CountDownLatch(1);
  private static XmemcachedWrapper _xmemcached;
  private static SimpleMemcacheDInterface _memcached;
  private static int _defaultExpirationTime = 3600;
  private static String[] servers;

  public static void stop() {
    if (_xmemcached != null) {
      LOG.debug("shutdown xmemcached client");
      try {
        _xmemcached.shutdown();
      } catch (IOException ignore) {
      }
      _initialized = new CountDownLatch(1);
      _initialize.set(true);
      _xmemcached = null;
    }
  }

  private static void init() {
    if (_initialize.compareAndSet(true, false)) {
      try {
        servers = getMemcachedServers();
        LOG.info("list of memcached servers=[{}]", StringUtils.join(servers, ", "));
        String maxConn = System.getProperty("MEMCACHED_MAX_CONN");
        if (maxConn == null) {
          maxConn = "100";
        }
        String socketTimeout = System.getProperty("MEMCACHED_SOCKET_TIMEOUT");
        int socketTO = socketTimeout != null ? Integer.valueOf(socketTimeout) : 3000;

        LOG.info("System.getProperty(\"MEMCACHED_SOCKET_TIMEOUT\")=[{}]", socketTO);
        String connectTimeout = System.getProperty("MEMCACHED_CONNECT_TIMEOUT");
        int connectTO = connectTimeout != null ? Integer.valueOf(connectTimeout) : 10;

        LOG.info("System.getProperty(\"MEMCACHED_CONNECT_TIMEOUT\")=[{}]", connectTO);
        MAX_KEY_LEN = 250;

        initMemcached(servers, Integer.valueOf(maxConn), socketTO, connectTO);

      } finally {
        _initialized.countDown(); // End critical section always
      }
      LOG.info("initialized memcached client");
//      LOG.warn("AOP_Memcache init:{}ms",System.currentTimeMillis() - begin);
    } else {
      try {
        _initialized.await();
      } catch (InterruptedException ignore) {
      }
    }

  }

  private static String[] getMemcachedServers() {
    try {
      String path = PropUtil.getLPath();
      if (StringUtil.isEmpty(path)) {
        LOG.info("getMemcachedServers from devMode");
        String[] result = new String[1];
        result[0] = "localhost:11211";
        return result;
      } else {
        path += "memcached.properties";
        LOG.debug("getMemcachedServers-getLPath:{}", path);
        String value = PropUtil.readPropertyFile("memcached.ips", path);
        LOG.info("memcached.ips:{}", value);
        if (StringUtil.isNotEmpty(value)) {
          LOG.info("read from memcached.properties success");
          return value.split(",");
        }
      }
      String[] result = new String[1];
      result[0] = "localhost:11211";
      return result;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  private static void initMemcached(String[] servers, int maxCon, int socketTO, int connectTO) {
    if (_xmemcached == null) {
      long begin = System.currentTimeMillis();
      LOG.info("initialize xmemcached client");
      MemcachedClientBuilder builder =
        new XMemcachedClientBuilder(AddrUtil.getAddresses(StringUtils.join(servers, " ")));
      builder.setSessionLocator(new KetamaMemcachedSessionLocator());
      LOG.info("use binary memcached prototol.");
      //zoujianhong 由于memcached版本原因，操作系统是windows时使用TextCommandFactory，否则使用BinaryCommandFactory
      if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
        builder.setCommandFactory(new TextCommandFactory());
      } else {
        builder.setCommandFactory(new BinaryCommandFactory());
      }
      builder.setConnectionPoolSize(maxCon);
      builder.getConfiguration().setSoTimeout(socketTO);
      try {
        _xmemcached = new XmemcachedWrapper(builder.build());
        _xmemcached.setConnectTimeout(connectTO);
        _memcached = _xmemcached;
      } catch (IOException e) {
        LOG.error(e.getMessage(), e);
        if (_xmemcached != null) {
          try {
            _xmemcached.shutdown();
          } catch (IOException e1) {
            LOG.error(e1.getMessage(), e1);
          }
          _xmemcached = null;
        }
      }
//      LOG.warn("AOP_Memcache initMemcached:{}ms",System.currentTimeMillis() - begin);
    }
  }

  /**
   * @param lock   key to push
   * @param expiry in milliseconds;
   * @return true if lock obtained.
   */
  public static boolean lockWithoutWait(String lock, long expiry) {
    init();
    long now = System.currentTimeMillis();
    Object value = now + "";
    Date expires = new Date(now + expiry);
    return _memcached.add(lock, value, expires);
  }

  public static boolean unlock(String lock) {
    init();
    return _memcached.delete(lock);
  }

  public static boolean set(String key, Object val) {
    return set(key, val, null);
  }

  public static boolean set(Long date, String key, Object val) {
    return set(key, val, new Date(date));
  }

  public static boolean set(String key, Object val, Date date) {
    long begin = System.currentTimeMillis();
    init();
    if (key == null) {
      return false;
    }
    if (date == null) {
      date = new Date(System.currentTimeMillis() + (_defaultExpirationTime * 1000L));
    }
    key = getKey(key);
    boolean setval = _memcached.set(key, val, date);
    if (!setval) {
      LOG.warn("can not set key=[{}], val=[{}]", key, val);
    }
//    LOG.info("AOP_Memcache set:{}ms",System.currentTimeMillis() - begin);
    return setval;
  }

  public static boolean add(String key, Object val) {
    return set(key, val, null);
  }

  public static boolean add(String key, Object val, Date date) {
    init();
    if (key == null) {
      return false;
    }
    if (date == null) {
      date = new Date(System.currentTimeMillis() + (_defaultExpirationTime * 1000L));
    }
    key = getKey(key);
    boolean setval = _memcached.add(key, val, date);
    if (!setval) {
      LOG.warn("can not add key=[{}], val=[{}]", key, val);
    }
    return setval;
  }

  public static Object get(String key) throws CacheException {
    long begin = System.currentTimeMillis();
    init();
    if (key == null) {
      return null;
    }
    key = getKey(key);
//      LOG.debug("memcache key is " + key + ".");
    Object v = _memcached.get(key);
    if (v == null) {
      LOG.trace("get() cache miss key=[{}]", key);
    }
    if (v instanceof String) {
      v = ((String) v).trim();
    }
//    LOG.info("AOP_Memcache get,key:{}耗时:{}ms",key,System.currentTimeMillis() - begin);
    return v;
  }

  @SuppressWarnings({"ThrowableInstanceNeverThrown"})
  public static boolean delete(String key) throws CacheException {
    init();

    if (key == null) {
      return false;
    }
    key = getKey(key);
    boolean v = _memcached.delete(key);
    if (!v) {
      LOG.debug("delete() of non-existant key=[{}]", key);
      LOG.trace("delete() failed key=[" + key + "]",
        new IllegalArgumentException("delete() called on non-existant key"));
    }
    return v;
  }

  private static String getKey(String key) {
    if (key.length() >= MAX_KEY_LEN) {
      try {
        MessageDigest mac = MessageDigest.getInstance("SHA-256");
        mac.update(key.getBytes());
        String origkey = key;
        key = new String(Hex.encodeHex(mac.digest()));
        LOG.debug("key too long. Using hash key=[{}], orig key=[{}]", key, origkey);
      } catch (NoSuchAlgorithmException e) {
        LOG.error("can't find MessageDigest", e);
        throw new RuntimeException(e);
      }
    }
    return key;
  }

  public static void flushAll() throws Exception {
    init();
    _memcached.flushAll();
  }

}

