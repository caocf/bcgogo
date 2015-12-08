package com.bcgogo.config.cache;

import com.bcgogo.config.util.MemCacheAdapter;
import com.bcgogo.constant.MemcachePrefix;
import com.bcgogo.enums.ConcurrentScene;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 12-8-15
 * Time: 下午5:43
 * 并发处理机制
 */
public class BcgogoConcurrentController {
  private static final Logger LOG = LoggerFactory.getLogger(BcgogoConcurrentController.class);

  public static final Long LOCK_EXPIRY = 60000L;            // memCache加锁默认时间
  public static  Long CHECK_LOCK_INTERVAL = 200L;       // 如果被锁, 每隔多久再查一次
  public static  Long CHECK_LOCK_EXPIRY = 2000L;      // 如果被锁, 隔多久LOG超时异常
  public static  Long RELEASE_LOCK_INTERVAL = 20L;    // 尝试解锁的间隔时间
  public static  Long RELEASE_LOCK_EXPIRY = 500L;    // 解锁的最长尝试时间


  public static boolean lock(ConcurrentScene scene, String key){
    if(scene == null || StringUtils.isBlank(key))
      return false;
    String memKey = getKey(scene.getName(), key);
    LOG.debug("并发机制加锁:{}", memKey);

    Long beginTime = System.currentTimeMillis();
    boolean lockSuccess = MemCacheAdapter.lockWithoutWait(memKey, LOCK_EXPIRY);

      //如果加锁未成功, 继续尝试, 直到超时
    if (!lockSuccess) {
      LOG.warn("并发机制加锁: {} 时未成功, 将再次尝试.", memKey);
      try {
        Object lock = MemCacheAdapter.get(memKey);
        //如果锁存在, 则等待
        while (lock != null) {
          Long recheckCost = System.currentTimeMillis() - beginTime;
          if (recheckCost > CHECK_LOCK_EXPIRY) {
            LOG.error("并发机制加锁: {} 时超时, 已等待 {} ms. 加锁不成功.", memKey, recheckCost);
            lockSuccess = false;
            return lockSuccess;
          }
          Thread.sleep(CHECK_LOCK_INTERVAL);
          lock = MemCacheAdapter.get(memKey);
        }
      } catch (InterruptedException e) {
        LOG.error("并发机制加锁:{} 等待时出错", memKey);
        LOG.error(e.getMessage(), e);
      }
      lockSuccess = MemCacheAdapter.lockWithoutWait(memKey, LOCK_EXPIRY);
    }

    LOG.debug("并发机制加锁:{}, 结果:{}", memKey, lockSuccess);
    return lockSuccess;
  }

  public static boolean lock(ConcurrentScene scene, Long key){
    if(scene == null || key == null){
      return false;
    }
    return lock(scene, String.valueOf(key));
  }

  /**
   * Lock传入的List中的所有key. 由于Java的泛型擦除问题, 无法重载实现参数为List<String>和List<Long>的方法
   * @param scene
   * @param keys List中的元素需实现toString()方法.
   * @return 是否全部成功加锁
   */
  public static boolean lock(ConcurrentScene scene, List keys){
    if(scene == null || CollectionUtils.isEmpty(keys)){
      return false;
    }
    CollectionUtil.removeNullElements(keys);
    Object[] keysArry = keys.toArray();
    String keyString = ArrayUtils.toString(keysArry, "");
    LOG.debug("并发机制加锁:{}", keyString);
    Long beginTime = System.currentTimeMillis();

    //先检查现有的memCache中是否存在keys中的锁,如果有则等待, 直至超时.
    boolean hasLockedKey = checkExistLockedKey(scene, keys);
    while (hasLockedKey) {
      Long recheckCost = System.currentTimeMillis() - beginTime;
      if (recheckCost > CHECK_LOCK_EXPIRY) {
        LOG.error("并发机制加锁: {} 时超时, 已等待 {} ms. 加锁不成功.", keyString, recheckCost);
        return false;
      }
      try {
        Thread.sleep(CHECK_LOCK_INTERVAL);
      } catch (InterruptedException e) {
        LOG.error("并发机制加锁:{} 等待时出错", keyString);
      }
      hasLockedKey = checkExistLockedKey(scene, keys);
    }

    boolean allLocked = true;
    for(int i=0;i<keys.size();i++){
      Object key = keys.get(i);
      if(key!=null && !lock(scene, key.toString())){
        allLocked = false;
        break;
      }
    }
    //如果加锁失败, 解除List中加过的锁.
    if(!allLocked){
      for(int i=0;i<keys.size();i++){
        Object key = keys.get(i);
        if(key != null)
          release(scene, key.toString());
      }
    }
    LOG.debug("并发机制加锁:{}, 结果:{}", keyString, allLocked);
    return allLocked;
  }

  private static boolean checkExistLockedKey(ConcurrentScene scene, List keys) {
    boolean hasLockedKey = false;
    for(int i=0;i<keys.size();i++){
      Object key = keys.get(i);
      if(key!=null && MemCacheAdapter.get(getKey(scene.getName(), String.valueOf(key)))!=null){
        hasLockedKey = true;
        break;
      }
    }
    return hasLockedKey;
  }

  public static boolean release(ConcurrentScene scene, String key){
    if(scene == null || StringUtils.isBlank(key)){
      return false;
    }
    String memKey = getKey(scene.getName(), key);
    LOG.debug("并发机制解锁:{}", memKey);
    Long beginTime = System.currentTimeMillis();
    Object lock = MemCacheAdapter.get(memKey);
    if(lock == null){
      return true;
    }
    boolean releaseSuccess = MemCacheAdapter.unlock(memKey);
    try{
      while(!releaseSuccess){
        Long recheckCost = System.currentTimeMillis() - beginTime;
        if(recheckCost > RELEASE_LOCK_EXPIRY){
          LOG.error("并发机制解锁: {} 时超时, 已等待 {} ms.", memKey, recheckCost);
          releaseSuccess = false;
          return releaseSuccess;
        }
        Thread.sleep(RELEASE_LOCK_INTERVAL);
        releaseSuccess = MemCacheAdapter.unlock(memKey);
      }
    }catch(InterruptedException e){
      LOG.error("并发机制解锁: {} 时出错", memKey);
      LOG.error(e.getMessage(), e);
    }
    LOG.debug("并发机制解锁:{}, 结果:{}", memKey, releaseSuccess);
    return releaseSuccess;
  }

  public static boolean release(ConcurrentScene scene, Long key){
    if(scene == null || key == null){
      return false;
    }
    return release(scene, String.valueOf(key));
  }

  public static boolean release(ConcurrentScene scene, List keys){
    if(scene == null || CollectionUtils.isEmpty(keys)){
      return false;
    }
    CollectionUtil.removeNullElements(keys);

    boolean allReleased = true;
    for(int i=0;i<keys.size();i++){
      Object key = keys.get(i);
      if(key!=null && !release(scene, key.toString())){            //key存在 且解锁不成功的情况下不阻止其他Key解锁
        allReleased = false;
      }
    }
    return allReleased;
  }

  private static String getKey(String name, String key) {
    return MemcachePrefix.concurrentLock.getValue() + StringUtil.truncValue(name) +
        StringUtil.truncValue(key).replaceAll("\\s+|\\r|\\n", "_");
  }

}
