package com.bcgogo.socketReceiver.cache;
import net.rubyeye.xmemcached.*;
import net.rubyeye.xmemcached.auth.AuthInfo;
import net.rubyeye.xmemcached.exception.MemcachedException;
import net.rubyeye.xmemcached.networking.Connector;
import net.rubyeye.xmemcached.transcoders.Transcoder;
import net.rubyeye.xmemcached.utils.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
/**
 * Created with IntelliJ IDEA.
 * User: Jimuchen
 * Date: 14-3-5
 * Time: 下午8:08
 * To change this template use File | Settings | File Templates.
 */
public class XmemcachedWrapper implements SimpleMemcacheDInterface {
  private static final Logger LOG = LoggerFactory.getLogger(XmemcachedWrapper.class);
  private MemcachedClient _client;

  public <T> GetsResponse<T> getResponse(String key) {
    try {
      net.rubyeye.xmemcached.GetsResponse item = this._client.gets(key);
      if (item == null) {
        return null;
      }
      return new GetsResponse(item.getCas(), item.getValue());
    } catch (MemcachedException ex) {
      return null;
    } catch (InterruptedException e) {
      return null;
    } catch (TimeoutException e) {
    }
    return null;
  }

  public boolean add(String key, Object value, Date expires) {
    long now = System.currentTimeMillis();
    long expiry = expires.getTime() - now;
    try {
      return this._client.add(key, (int) (expiry / 1000L), value);
    } catch (TimeoutException e) {
      return true;
    } catch (InterruptedException e) {
      return true;
    } catch (MemcachedException e) {
    }
    return true;
  }

  public XmemcachedWrapper(MemcachedClient client) {
    this._client = client;
  }

  public void setMergeFactor(int i) {
    this._client.setMergeFactor(i);
  }

  public long getConnectTimeout() {
    return this._client.getConnectTimeout();
  }

  public void setConnectTimeout(long l) {
    this._client.setConnectTimeout(l);
  }

  public Connector getConnector() {
    return this._client.getConnector();
  }

  public void setOptimizeGet(boolean b) {
    this._client.setOptimizeGet(b);
  }

  public void setOptimizeMergeBuffer(boolean b) {
    this._client.setOptimizeMergeBuffer(b);
  }

  public boolean isShutdown() {
    return this._client.isShutdown();
  }

  public void addServer(String s, int i) throws IOException {
    this._client.addServer(s, i);
  }

  public void addServer(InetSocketAddress inetSocketAddress) throws IOException {
    this._client.addServer(inetSocketAddress);
  }

  public void addServer(String s) throws IOException {
    this._client.addServer(s);
  }

  public List<String> getServersDescription() {
    return this._client.getServersDescription();
  }

  public void removeServer(String s) {
    this._client.removeServer(s);
  }

  public <T> T get(String key, long timeout, Transcoder<T> transcoder)
      throws TimeoutException, InterruptedException, MemcachedException {
    return this._client.get(key, timeout, transcoder);
  }

  public Object get(String key, long l) throws TimeoutException, InterruptedException, MemcachedException {
    return this._client.get(key, l);
  }

  public <T> T get(String key, Transcoder<T> tTranscoder) throws TimeoutException, InterruptedException, MemcachedException {
    return this._client.get(key, tTranscoder);
  }

  public Object get(String s) {
    try {
      return this._client.get(s);
    } catch (Exception e) {
      LOG.info(e.getMessage());
    }
    return null;
  }

  public <T> net.rubyeye.xmemcached.GetsResponse<T> gets(String key, long l, Transcoder<T> tTranscoder) throws TimeoutException, InterruptedException, MemcachedException {
    return this._client.gets(key, l, tTranscoder);
  }

  public <T> net.rubyeye.xmemcached.GetsResponse<T> gets(String s) {
    try {
      return this._client.gets(s);
    } catch (Exception e) {
      LOG.info(e.getMessage());
    }
    return null;
  }

  public <T> net.rubyeye.xmemcached.GetsResponse<T> gets(String key, long l) throws TimeoutException, InterruptedException, MemcachedException {
    return this._client.gets(key, l);
  }

  public <T> net.rubyeye.xmemcached.GetsResponse<T> gets(String key, Transcoder transcoder) {
    try {
      return this._client.gets(key, transcoder);
    } catch (Exception e) {
      LOG.info(e.getMessage());
    }
    return null;
  }

  public <T> Map<String, T> get(Collection<String> strings, long l, Transcoder<T> tTranscoder) throws TimeoutException, InterruptedException, MemcachedException {
    return this._client.get(strings, l, tTranscoder);
  }

  public <T> Map<String, T> get(Collection<String> strings, Transcoder<T> tTranscoder) throws TimeoutException, InterruptedException, MemcachedException {
    return this._client.get(strings, tTranscoder);
  }

  public <T> Map<String, T> get(Collection<String> strings) throws TimeoutException, InterruptedException, MemcachedException {
    return this._client.get(strings);
  }

  public <T> Map<String, T> get(Collection<String> strings, long l) throws TimeoutException, InterruptedException, MemcachedException {
    return this._client.get(strings, l);
  }

  public <T> Map<String, net.rubyeye.xmemcached.GetsResponse<T>> gets(Collection<String> strings, long l, Transcoder<T> tTranscoder) throws TimeoutException, InterruptedException, MemcachedException {
    return this._client.gets(strings, l, tTranscoder);
  }

  public <T> Map<String, net.rubyeye.xmemcached.GetsResponse<T>> gets(Collection<String> strings) throws TimeoutException, InterruptedException, MemcachedException {
    return this._client.gets(strings);
  }

  public <T> Map<String, net.rubyeye.xmemcached.GetsResponse<T>> gets(Collection<String> strings, long l) throws TimeoutException, InterruptedException, MemcachedException {
    return this._client.gets(strings, l);
  }

  public <T> Map<String, net.rubyeye.xmemcached.GetsResponse<T>> gets(Collection<String> strings, Transcoder<T> tTranscoder) throws TimeoutException, InterruptedException, MemcachedException {
    return this._client.gets(strings, tTranscoder);
  }

  public <T> boolean set(String key, int i, T t, Transcoder<T> tTranscoder, long l) throws TimeoutException, InterruptedException, MemcachedException {
    return this._client.set(key, i, t, tTranscoder, l);
  }

  public boolean set(String key, int i, Object o) {
    try {
      return this._client.set(key, i, o);
    } catch (Exception e) {
      LOG.info(e.getMessage());
    }
    return false;
  }

  public boolean set(String key, int i, Object o, Date date) throws TimeoutException, InterruptedException, MemcachedException {
    return this._client.set(key, (int) (date.getTime() / 1000L), o);
  }

  public boolean set(String key, int i, Object o, long l) throws TimeoutException, InterruptedException, MemcachedException {
    return this._client.set(key, i, o, l);
  }

  public <T> boolean set(String key, int i, T t, Transcoder<T> tTranscoder) throws TimeoutException, InterruptedException, MemcachedException {
    return this._client.set(key, i, t, tTranscoder);
  }

  public void setWithNoReply(String key, int i, Object o) throws InterruptedException, MemcachedException {
    this._client.setWithNoReply(key, i, o);
  }

  public <T> void setWithNoReply(String key, int i, T t, Transcoder<T> tTranscoder) throws InterruptedException, MemcachedException {
    this._client.setWithNoReply(key, i, t, tTranscoder);
  }

  public <T> boolean add(String key, int i, T t, Transcoder<T> tTranscoder, long l) throws TimeoutException, InterruptedException, MemcachedException {
    return this._client.add(key, i, t, tTranscoder, l);
  }

  public boolean add(String key, int i, Object o) {
    try {
      return this._client.add(key, i, o);
    } catch (Exception e) {
      LOG.info(e.getMessage());
    }
    return false;
  }

  public boolean add(String key, int i, Object o, long l) throws TimeoutException, InterruptedException, MemcachedException {
    return this._client.add(key, i, o, l);
  }

  public <T> boolean add(String key, int i, T t, Transcoder<T> tTranscoder) throws TimeoutException, InterruptedException, MemcachedException {
    return this._client.add(key, i, t, tTranscoder);
  }

  public void addWithNoReply(String key, int i, Object o) throws InterruptedException, MemcachedException {
    this._client.addWithNoReply(key, i, o);
  }

  public <T> void addWithNoReply(String key, int i, T t, Transcoder<T> tTranscoder) throws InterruptedException, MemcachedException {
    this._client.addWithNoReply(key, i, t, tTranscoder);
  }

  public <T> boolean replace(String key, int i, T t, Transcoder<T> tTranscoder, long l) throws TimeoutException, InterruptedException, MemcachedException {
    return this._client.replace(key, i, t, tTranscoder, l);
  }

  public boolean replace(String key, int i, Object o) throws TimeoutException, InterruptedException, MemcachedException {
    return this._client.replace(key, i, o);
  }

  public boolean replace(String key, int i, Object o, long l) throws TimeoutException, InterruptedException, MemcachedException {
    return this._client.replace(key, i, o, l);
  }

  public <T> boolean replace(String key, int i, T t, Transcoder<T> tTranscoder) throws TimeoutException, InterruptedException, MemcachedException {
    return this._client.replace(key, i, t, tTranscoder);
  }

  public void replaceWithNoReply(String key, int i, Object o) throws InterruptedException, MemcachedException {
    this._client.replaceWithNoReply(key, i, o);
  }

  public <T> void replaceWithNoReply(String key, int i, T t, Transcoder<T> tTranscoder) throws InterruptedException, MemcachedException {
    this._client.replaceWithNoReply(key, i, t, tTranscoder);
  }

  public boolean append(String key, Object o) throws TimeoutException, InterruptedException, MemcachedException {
    return this._client.append(key, o);
  }

  public boolean append(String key, Object o, long l) throws TimeoutException, InterruptedException, MemcachedException {
    return this._client.append(key, o, l);
  }

  public void appendWithNoReply(String key, Object o) throws InterruptedException, MemcachedException {
    this._client.appendWithNoReply(key, o);
  }

  public boolean prepend(String key, Object o) throws TimeoutException, InterruptedException, MemcachedException {
    return this._client.prepend(key, o);
  }

  public boolean prepend(String key, Object o, long l) throws TimeoutException, InterruptedException, MemcachedException {
    return this._client.prepend(key, o, l);
  }

  public void prependWithNoReply(String key, Object o) throws InterruptedException, MemcachedException {
    this._client.prependWithNoReply(key, o);
  }

  public boolean cas(String key, int i, Object o, long l) {
    try {
      return this._client.cas(key, i, o, l);
    } catch (Exception e) {
      LOG.info(e.getMessage());
    }
    return false;
  }

  public <T> boolean cas(String key, int i, T t, Transcoder<T> tTranscoder, long l, long l1) throws TimeoutException, InterruptedException, MemcachedException {
    return this._client.cas(key, i, t, tTranscoder, l, l1);
  }

  public boolean cas(String key, int i, Object o, long l, long l1) throws TimeoutException, InterruptedException, MemcachedException {
    return this._client.cas(key, i, o, l, l1);
  }

  public <T> boolean cas(String key, int i, T t, Transcoder<T> tTranscoder, long l) throws TimeoutException, InterruptedException, MemcachedException {
    return this._client.cas(key, i, t, tTranscoder, l);
  }

  public <T> boolean cas(String key, int i, CASOperation<T> tcasOperation, Transcoder<T> tTranscoder) throws TimeoutException, InterruptedException, MemcachedException {
    return this._client.cas(key, i, tcasOperation, tTranscoder);
  }

  public <T> boolean cas(String key, int i, net.rubyeye.xmemcached.GetsResponse<T> tGetsResponse, CASOperation<T> tcasOperation, Transcoder<T> tTranscoder) throws TimeoutException, InterruptedException, MemcachedException {
    return this._client.cas(key, i, tGetsResponse, tcasOperation, tTranscoder);
  }

  public <T> boolean cas(String key, int i, net.rubyeye.xmemcached.GetsResponse<T> tGetsResponse, CASOperation<T> tcasOperation) throws TimeoutException, InterruptedException, MemcachedException {
    return this._client.cas(key, i, tGetsResponse, tcasOperation);
  }

  public <T> boolean cas(String key, net.rubyeye.xmemcached.GetsResponse<T> tGetsResponse, CASOperation<T> tcasOperation) throws TimeoutException, InterruptedException, MemcachedException {
    return this._client.cas(key, tGetsResponse, tcasOperation);
  }

  public <T> boolean cas(String key, int i, CASOperation<T> tcasOperation) {
    try {
      return this._client.cas(key, i, tcasOperation);
    } catch (Exception e) {
      LOG.info(e.getMessage());
    }
    return false;
  }

  public boolean cas(String key, Object value, int expires, long casUnique) {
    final Object finalVal = value;
    try {
      return this._client.cas(key, expires, new CASOperation() {
        public int getMaxTries() {
          return 10;
        }

        public Object getNewValue(long casunique, Object currentValue) {
          return finalVal;
        }
      });
    } catch (TimeoutException e) {
      return true;
    } catch (InterruptedException e) {
      return true;
    } catch (MemcachedException e) {
    }
    return true;
  }

  public <T> boolean cas(String key, CASOperation<T> tcasOperation) throws TimeoutException, InterruptedException, MemcachedException {
    return this._client.cas(key, tcasOperation);
  }

  public <T> void casWithNoReply(String key, net.rubyeye.xmemcached.GetsResponse<T> tGetsResponse, CASOperation<T> tcasOperation) throws TimeoutException, InterruptedException, MemcachedException {
    this._client.casWithNoReply(key, tGetsResponse, tcasOperation);
  }

  public <T> void casWithNoReply(String key, int i, net.rubyeye.xmemcached.GetsResponse<T> tGetsResponse, CASOperation<T> tcasOperation) throws TimeoutException, InterruptedException, MemcachedException {
    this._client.casWithNoReply(key, i, tGetsResponse, tcasOperation);
  }

  public <T> void casWithNoReply(String key, int i, CASOperation<T> tcasOperation) throws TimeoutException, InterruptedException, MemcachedException {
    this._client.casWithNoReply(key, i, tcasOperation);
  }

  public <T> void casWithNoReply(String key, CASOperation<T> tcasOperation) throws TimeoutException, InterruptedException, MemcachedException {
    this._client.casWithNoReply(key, tcasOperation);
  }

  public Map<InetSocketAddress, String> getVersions() throws TimeoutException, InterruptedException, MemcachedException {
    return this._client.getVersions();
  }

  public long incr(String key) {
    try {
      return this._client.incr(key, 1L, 1L);
    } catch (Exception ignore) {
    }
    return -1L;
  }

  public long incr(String key, long l) throws TimeoutException, InterruptedException, MemcachedException {
    return this._client.incr(key, l);
  }

  public long incr(String key, long l, long l1) throws TimeoutException, InterruptedException, MemcachedException {
    return this._client.incr(key, l, l1);
  }

  public long incr(String key, long l, long l1, long l2) throws TimeoutException, InterruptedException, MemcachedException {
    return this._client.incr(key, l, l1, l2);
  }

  public long decr(String key, long l) throws TimeoutException, InterruptedException, MemcachedException {
    return this._client.decr(key, l);
  }

  public long decr(String key, long l, long l1) throws TimeoutException, InterruptedException, MemcachedException {
    return this._client.decr(key, l, l1);
  }

  public long decr(String key, long l, long l1, long l2) throws TimeoutException, InterruptedException, MemcachedException {
    return this._client.decr(key, l, l1, l2);
  }

  public boolean flushAll() throws TimeoutException, InterruptedException, MemcachedException {
    this._client.flushAll();
    return false;
  }

  public void flushAllWithNoReply() throws InterruptedException, MemcachedException {
    this._client.flushAllWithNoReply();
  }

  public void flushAll(long l) throws TimeoutException, InterruptedException, MemcachedException {
    this._client.flushAll(l);
  }

  public void flushAll(InetSocketAddress inetSocketAddress) throws MemcachedException, InterruptedException, TimeoutException {
    this._client.flushAll(inetSocketAddress);
  }

  public void flushAllWithNoReply(InetSocketAddress inetSocketAddress) throws MemcachedException, InterruptedException {
    this._client.flushAllWithNoReply(inetSocketAddress);
  }

  public void flushAll(InetSocketAddress inetSocketAddress, long l) throws MemcachedException, InterruptedException, TimeoutException {
    this._client.flushAll(inetSocketAddress, l);
  }

  public Map<String, String> stats(InetSocketAddress inetSocketAddress) throws MemcachedException, InterruptedException, TimeoutException {
    return this._client.stats(inetSocketAddress);
  }

  public Map<String, String> stats(InetSocketAddress inetSocketAddress, long l) throws MemcachedException, InterruptedException, TimeoutException {
    return this._client.stats(inetSocketAddress, l);
  }

  public Map<InetSocketAddress, Map<String, String>> getStats(long l) throws MemcachedException, InterruptedException, TimeoutException {
    return this._client.getStats(l);
  }

  public Map<InetSocketAddress, Map<String, String>> getStats() throws MemcachedException, InterruptedException, TimeoutException {
    return this._client.getStats();
  }

  public Map<InetSocketAddress, Map<String, String>> getStatsByItem(String s) throws MemcachedException, InterruptedException, TimeoutException {
    return this._client.getStatsByItem(s);
  }

  public void shutdown() throws IOException {
    this._client.shutdown();
  }

  public boolean delete(String s) {
    try {
      return this._client.delete(s);
    } catch (Exception e) {
      LOG.info(e.getMessage());
    }
    return false;
  }

  public boolean set(String key, Object value, Date expiry) {
    try {
      return this._client.set(key, (int) (expiry.getTime() / 1000L), value);
    } catch (TimeoutException e) {
      return false;
    } catch (InterruptedException e) {
      return false;
    } catch (MemcachedException e) {
    }
    return false;
  }

  public Transcoder getTranscoder() {
    return this._client.getTranscoder();
  }

  public void setTranscoder(Transcoder transcoder) {
    this._client.setTranscoder(transcoder);
  }

  public Map<InetSocketAddress, Map<String, String>> getStatsByItem(String key, long l) throws MemcachedException, InterruptedException, TimeoutException {
    return this._client.getStatsByItem(key, l);
  }

  public long getOpTimeout() {
    return this._client.getOpTimeout();
  }

  public void setOpTimeout(long l) {
    this._client.setOpTimeout(l);
  }

  public Map<InetSocketAddress, String> getVersions(long l) throws TimeoutException, InterruptedException, MemcachedException {
    return this._client.getVersions(l);
  }

  public Collection<InetSocketAddress> getAvaliableServers() {
    return this._client.getAvaliableServers();
  }

  public void addServer(String key, int i, int i1) throws IOException {
    this._client.addServer(key, i, i1);
  }

  public void addServer(InetSocketAddress inetSocketAddress, int i) throws IOException {
    this._client.addServer(inetSocketAddress, i);
  }

  public void deleteWithNoReply(String s) throws InterruptedException, MemcachedException {
    this._client.deleteWithNoReply(s);
  }

  public void incrWithNoReply(String key, long l) throws InterruptedException, MemcachedException {
    this._client.incrWithNoReply(key, l);
  }

  public void decrWithNoReply(String key, long l) throws InterruptedException, MemcachedException {
    this._client.decrWithNoReply(key, l);
  }

  public void setLoggingLevelVerbosity(InetSocketAddress inetSocketAddress, int i) throws TimeoutException, InterruptedException, MemcachedException {
    this._client.setLoggingLevelVerbosity(inetSocketAddress, i);
  }

  public void setLoggingLevelVerbosityWithNoReply(InetSocketAddress inetSocketAddress, int i) throws InterruptedException, MemcachedException {
    this._client.setLoggingLevelVerbosityWithNoReply(inetSocketAddress, i);
  }

  public void addStateListener(MemcachedClientStateListener memcachedClientStateListener) {
    this._client.addStateListener(memcachedClientStateListener);
  }

  public void removeStateListener(MemcachedClientStateListener memcachedClientStateListener) {
    this._client.removeStateListener(memcachedClientStateListener);
  }

  public Collection<MemcachedClientStateListener> getStateListeners() {
    return this._client.getStateListeners();
  }

  public void flushAllWithNoReply(int i) throws InterruptedException, MemcachedException {
    this._client.flushAllWithNoReply(i);
  }

  public void flushAll(int i, long l) throws TimeoutException, InterruptedException, MemcachedException {
    this._client.flushAll(i, l);
  }

  public void flushAllWithNoReply(InetSocketAddress inetSocketAddress, int i) throws MemcachedException, InterruptedException {
    this._client.flushAllWithNoReply(inetSocketAddress, i);
  }

  public void flushAll(InetSocketAddress inetSocketAddress, long l, int i) throws MemcachedException, InterruptedException, TimeoutException {
    this._client.flushAll(inetSocketAddress, l, i);
  }

  public void setHealSessionInterval(long l) {
    this._client.setHealSessionInterval(l);
  }

  public long getHealSessionInterval() {
    return this._client.getHealSessionInterval();
  }

  public Protocol getProtocol() {
    return this._client.getProtocol();
  }

  public void setPrimitiveAsString(boolean b) {
    this._client.setPrimitiveAsString(b);
  }

  public void setConnectionPoolSize(int i) {
    this._client.setConnectionPoolSize(i);
  }

  public void setEnableHeartBeat(boolean b) {
    this._client.setEnableHeartBeat(b);
  }

  public void setSanitizeKeys(boolean b) {
    this._client.setSanitizeKeys(b);
  }

  public boolean isSanitizeKeys() {
    return this._client.isSanitizeKeys();
  }

  public Counter getCounter(String s) {
    return this._client.getCounter(s);
  }

  public Counter getCounter(String key, long l) {
    return this._client.getCounter(key, l);
  }

  public KeyIterator getKeyIterator(InetSocketAddress inetSocketAddress) throws MemcachedException, InterruptedException, TimeoutException {
    return this._client.getKeyIterator(inetSocketAddress);
  }

  public void setAuthInfoMap(Map<InetSocketAddress, AuthInfo> inetSocketAddressAuthInfoMap) {
    this._client.setAuthInfoMap(inetSocketAddressAuthInfoMap);
  }

  public Map<InetSocketAddress, AuthInfo> getAuthInfoMap() {
    return this._client.getAuthInfoMap();
  }

  public String getName() {
    return this._client.getName();
  }

  public void setName(String s) {
    this._client.setName(s);
  }
}