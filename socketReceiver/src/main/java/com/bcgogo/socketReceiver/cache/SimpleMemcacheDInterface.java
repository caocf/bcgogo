package com.bcgogo.socketReceiver.cache;

import net.rubyeye.xmemcached.GetsResponse;
import net.rubyeye.xmemcached.exception.MemcachedException;

import java.util.Date;
import java.util.concurrent.TimeoutException;

/**
 * Created with IntelliJ IDEA.
 * User: Jimuchen
 * Date: 14-3-5
 * Time: 下午8:07
 * To change this template use File | Settings | File Templates.
 */
public abstract interface SimpleMemcacheDInterface
{
  public abstract <T> GetsResponse<T> getResponse(String paramString);

  public abstract boolean add(String paramString, Object paramObject, Date paramDate);

  public abstract boolean cas(String paramString, Object paramObject, int paramInt, long paramLong);

  public abstract boolean delete(String paramString);

  public abstract boolean flushAll()
      throws TimeoutException, InterruptedException, MemcachedException;

  public abstract Object get(String paramString);

  public abstract boolean set(String paramString, Object paramObject, Date paramDate);

  public abstract long incr(String paramString);
}