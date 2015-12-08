package com.bcgogo.notification.util;


import com.bcgogo.config.util.MemCacheAdapter;
import com.bcgogo.constant.MemcachePrefix;
import com.bcgogo.notification.dto.SensitiveWordsDTO;
import com.bcgogo.notification.service.SensitiveWordsService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.utils.NumberUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by IntelliJ IDEA.
 * User: zhangchuanlong
 * Date: 12-8-8
 * Time: 下午4:39
 * 短信敏感词本地缓存
 */
public class LocalCacheManager {
  private static final Logger LOG = LoggerFactory.getLogger(LocalCacheManager.class);
  public static Long SYNC_INTERVAL = 30000L;

  private static Map<String, SensitiveWordsDTO> sensitiveWordsCache = new HashMap();

  /**
   * @description only for unit test
   */
  public static void clearSensitiveWordsCache() {
    sensitiveWordsCache.clear();
  }


  /**
   * 将敏感词放到本地缓存
   *
   * @param sensitiveWordsDTO SensitiveWordsDTO
   */
  public static void setSensitiveWords(SensitiveWordsDTO sensitiveWordsDTO) {
    sensitiveWordsCache.put(MemcachePrefix.sensitiveWords.getValue(), sensitiveWordsDTO);
  }

  /**
   * 从本地缓存获取敏感词
   *
   * @return
   */
  public static SensitiveWordsDTO getSensitiveWords() {
    SensitiveWordsService sensitiveWordsService = ServiceManager.getService(SensitiveWordsService.class);
    //从本地缓存读取敏感词
    SensitiveWordsDTO sensitiveWordsDTO = sensitiveWordsCache.get(MemcachePrefix.sensitiveWords.getValue());
    //如果没读到敏感词，从数据库中重新加载到本地缓存中
    if (sensitiveWordsDTO == null) {
      sensitiveWordsDTO = sensitiveWordsService.getSensitiveWordFromDB();
      if (sensitiveWordsDTO == null) return sensitiveWordsDTO;
      sensitiveWordsDTO.setSyncTime(System.currentTimeMillis());
      setSensitiveWords(sensitiveWordsDTO);
      return sensitiveWordsDTO;
    }
    if (sensitiveWordsDTO.getSyncTime() == null || System.currentTimeMillis() - sensitiveWordsDTO.getSyncTime() >= SYNC_INTERVAL) {
      //更新 localCache
      String value = (String) MemCacheAdapter.get(MemcachePrefix.sensitiveWordsFlag.getValue());
      if (sensitiveWordsDTO.getSyncTime() == null || NumberUtil.longValue(value, System.currentTimeMillis()) > sensitiveWordsDTO.getSyncTime()) {
        sensitiveWordsDTO = freshLocalSensitiveWords();
      } else {
        // 更新 localCache syncTime
        sensitiveWordsDTO.setSyncTime(System.currentTimeMillis());
        setSensitiveWords(sensitiveWordsDTO);
      }
    }
    return sensitiveWordsDTO;
  }


  /**
   * 刷新本地敏感词缓存
   *
   * @return SensitiveWordsDTO
   */
  public static SensitiveWordsDTO freshLocalSensitiveWords() {
    SensitiveWordsService sensitiveWordsService = ServiceManager.getService(SensitiveWordsService.class);
    //如果没读到敏感词，从数据库中重新加载到本地缓存中
    SensitiveWordsDTO sensitiveWordsDTO = sensitiveWordsService.getSensitiveWordFromDB();
    if (sensitiveWordsDTO != null) {
      sensitiveWordsDTO.setSyncTime(System.currentTimeMillis());
      setSensitiveWords(sensitiveWordsDTO);
    }
    return sensitiveWordsDTO;
  }
}

