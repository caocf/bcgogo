package com.bcgogo.config.service;

import com.bcgogo.AbstractTest;
import com.bcgogo.config.cache.BcgogoConcurrentController;
import com.bcgogo.config.util.MemCacheAdapter;
import com.bcgogo.constant.MemcachePrefix;
import com.bcgogo.enums.ConcurrentScene;
import com.bcgogo.utils.StringUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 12-8-15
 * Time: 下午8:00
 */
public class BcgogoConcurrentControllerTest extends AbstractTest {
  @Before
  public void setupTest(){
    BcgogoConcurrentController.CHECK_LOCK_INTERVAL = 20L;
    BcgogoConcurrentController.CHECK_LOCK_EXPIRY = 300L;
    BcgogoConcurrentController.RELEASE_LOCK_EXPIRY = 300L;
  }

  @Test
  public void testLockAndReleaseString(){
    String specialStr = "会  员\r\n0\r0\n1";
    cleanStringKeys("member001", specialStr, "会员003");
    //加锁后直接从memCache取, 应不为空
    boolean lockSuccess = BcgogoConcurrentController.lock(ConcurrentScene.MEMBER, "member001");
    Assert.assertTrue(lockSuccess);
    Object lock = MemCacheAdapter.get(getKey(ConcurrentScene.MEMBER.getName(), "member001"));
    Assert.assertNotNull(lock);

    //中文String+特殊符号（空格，换行）是否正常
    lockSuccess = BcgogoConcurrentController.lock(ConcurrentScene.MEMBER, specialStr);
    Assert.assertTrue(lockSuccess);
    lock = MemCacheAdapter.get(getKey(ConcurrentScene.MEMBER.getName(), specialStr));
    Assert.assertNotNull(lock);

    //再次加锁则失败
    lockSuccess = BcgogoConcurrentController.lock(ConcurrentScene.MEMBER, "member001");
    Assert.assertFalse(lockSuccess);

    lockSuccess = BcgogoConcurrentController.lock(ConcurrentScene.MEMBER, specialStr);
    Assert.assertFalse(lockSuccess);

    //解锁英文String
    boolean releaseSuccess = BcgogoConcurrentController.release(ConcurrentScene.MEMBER, "member001");
    Assert.assertTrue(releaseSuccess);
    lock = MemCacheAdapter.get(getKey(ConcurrentScene.MEMBER.getName(), "member001"));
    Assert.assertNull(lock);

    //解锁中文String
    releaseSuccess = BcgogoConcurrentController.release(ConcurrentScene.MEMBER, specialStr);
    Assert.assertTrue(releaseSuccess);
    lock = MemCacheAdapter.get(getKey(ConcurrentScene.MEMBER.getName(), specialStr));
    Assert.assertNull(lock);

    //解未加的锁
    releaseSuccess = BcgogoConcurrentController.release(ConcurrentScene.MEMBER, "会员003");
    Assert.assertTrue(releaseSuccess);
    cleanStringKeys("member001", specialStr, "会员003");
  }

  private void cleanStringKeys(String... keys){
    for(String key:keys){
      BcgogoConcurrentController.release(ConcurrentScene.MEMBER, key);
    }
  }

  @Test
  public void testLockAndReleaseLong(){
    cleanLongKeys(1111L);
    //加锁Long型key, 直接取出查看是否成功.
    boolean lockSuccess = BcgogoConcurrentController.lock(ConcurrentScene.MEMBER, 1111L);
    Assert.assertTrue(lockSuccess);
    Object key = MemCacheAdapter.get(getKey(ConcurrentScene.MEMBER.getName(), String.valueOf(1111L)));
    Assert.assertNotNull(key);

    //再次加同样锁, 应失败
    lockSuccess = BcgogoConcurrentController.lock(ConcurrentScene.MEMBER, 1111L);
    Assert.assertFalse(lockSuccess);

    //解锁, 直接取出查看是否为空
    boolean releaseSuccess = BcgogoConcurrentController.release(ConcurrentScene.MEMBER, 1111L);
    Assert.assertTrue(releaseSuccess);
    key = MemCacheAdapter.get(getKey(ConcurrentScene.MEMBER.getName(), String.valueOf(1111L)));
    Assert.assertNull(key);

    //解锁已为空的锁
    releaseSuccess = BcgogoConcurrentController.release(ConcurrentScene.MEMBER, 1111L);
    Assert.assertTrue(releaseSuccess);
    cleanLongKeys(1111L);
  }

  private void cleanLongKeys(Long... keys){
    for(Long key: keys){
      BcgogoConcurrentController.release(ConcurrentScene.MEMBER, key);
    }
  }

  @Test
  public void testLockAndReleaseList(){
    List list = new ArrayList();
    list.add("member00001");
    list.add("member00002");
    list.add("会员111");
    list.add("会员222");
    list.add(183289472L);
    list.add(3894972934L);

    List anotherList = new ArrayList();
    anotherList.add("member00009");
    anotherList.add("member00008");
    anotherList.add("member00007");
    anotherList.add("member00002");

    //先解掉用例中所有锁, 以免多次运行时出现问题
    List allList = new ArrayList();
    allList.addAll(list);
    allList.addAll(anotherList);
    BcgogoConcurrentController.release(ConcurrentScene.MEMBER, list);

    Object lock = null;

    boolean lockSuccess = BcgogoConcurrentController.lock(ConcurrentScene.MEMBER, list);
    Assert.assertTrue(lockSuccess);
    for(int i=0; i<list.size(); i++){
      Object obj = list.get(i);
      lock = MemCacheAdapter.get(getKey(ConcurrentScene.MEMBER.getName(), String.valueOf(obj)));
      Assert.assertNotNull(lock);
    }

    //再锁同样List, 应失败
    lockSuccess = BcgogoConcurrentController.lock(ConcurrentScene.MEMBER, list);
    Assert.assertFalse(lockSuccess);

    //解锁同样List, 应成功
    boolean releaseSuccess = BcgogoConcurrentController.release(ConcurrentScene.MEMBER, list);
    Assert.assertTrue(releaseSuccess);
    for(int i=0; i<list.size(); i++){
      Object obj = list.get(i);
      lock = MemCacheAdapter.get(getKey(ConcurrentScene.MEMBER.getName(), String.valueOf(obj)));
      Assert.assertNull(lock);
    }

    lockSuccess = BcgogoConcurrentController.lock(ConcurrentScene.MEMBER, list);

    //加锁list后再加锁anotherList(其中包含list中的某些数据), 则会失败.
    lockSuccess = BcgogoConcurrentController.lock(ConcurrentScene.MEMBER, anotherList);
    Assert.assertFalse(lockSuccess);

    //失败后应不影响已加过锁的list中所有key
    for(int i=0; i<list.size(); i++){
      Object obj = list.get(i);
      lock = MemCacheAdapter.get(getKey(ConcurrentScene.MEMBER.getName(), String.valueOf(obj)));
      Assert.assertNotNull(lock);
    }

    //anotherList中的锁不应加上(除非此锁在list中已加过)
    outer: for(int i=0; i<anotherList.size(); i++){
      for(int j = 0; j<list.size(); j++){
        if(anotherList.get(i).equals(list.get(j))){
          continue outer;
        }
      }
      Object obj = anotherList.get(i);
      lock = MemCacheAdapter.get(getKey(ConcurrentScene.MEMBER.getName(), String.valueOf(obj)));
      Assert.assertNull(lock);
    }
    BcgogoConcurrentController.release(ConcurrentScene.MEMBER, list);
  }

  private static String getKey(String name, String key) {
    return MemcachePrefix.concurrentLock.getValue() + StringUtil.truncValue(name) +
        StringUtil.truncValue(key).replaceAll("\\s+|\\r|\\n", "_");
  }

}
