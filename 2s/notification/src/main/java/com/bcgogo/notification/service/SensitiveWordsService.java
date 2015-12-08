package com.bcgogo.notification.service;

import com.bcgogo.common.Pager;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.ConfigService;
import com.bcgogo.config.util.MemCacheAdapter;
import com.bcgogo.constant.MemcachePrefix;
import com.bcgogo.notification.dto.FailedSmsJobDTO;
import com.bcgogo.notification.dto.SensitiveWordsDTO;
import com.bcgogo.notification.model.*;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.ShopConstant;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: WWW
 * Date: 12-8-21
 * Time: 上午9:16
 */
@Component
public class SensitiveWordsService implements ISensitiveWordsService {
  private static final Logger LOG = LoggerFactory.getLogger(NotificationService.class);

  @Autowired
  private NotificationDaoManager notificationDaoManager;


  /**
   * 保存或更新敏感词
   *
   * @param sensitiveWordsDTO SensitiveWordsDTO
   * @throws java.io.IOException
   */
  @Override
  public void saveOrUpdateSensitiveWords(SensitiveWordsDTO sensitiveWordsDTO) throws IOException {
    if (this.getSensitiveWordFromDB() == null) {
      /*添加敏感词*/
      this.saveSensitiveWords(sensitiveWordsDTO);
    } else {
      /*更新敏感词*/
      this.updateSensitiveWords(sensitiveWordsDTO);
    }
  }

  /**
   * 保存敏感词
   *
   * @param sensitiveWordsDTO SensitiveWordsDTO
   * @return SensitiveWordsDTO
   */
  @Override
  public SensitiveWordsDTO saveSensitiveWords(SensitiveWordsDTO sensitiveWordsDTO) {
    NotificationWriter writer = notificationDaoManager.getWriter();
    Object status = writer.begin();
    try {
      SensitiveWords sensitiveWords = new SensitiveWords(sensitiveWordsDTO);
      writer.save(sensitiveWords);
      writer.commit(status);
      sensitiveWordsDTO = sensitiveWords.toDTO();
      freshSensitiveWords();
      return sensitiveWordsDTO;
    } finally {
      writer.rollback(status);
    }
  }


  /**
   * 上传时更新敏感词
   *
   * @param sensitiveWordsDTO SensitiveWordsDTO
   * @return SensitiveWordsDTO
   */
  @Override
  public SensitiveWordsDTO updateSensitiveWords(SensitiveWordsDTO sensitiveWordsDTO) throws IOException {
    NotificationWriter writer = notificationDaoManager.getWriter();
    Object status = writer.begin();
    try {
      //从本地缓存读取敏感词列表
      List<String> stringList = this.getSensitiveWordList();
      Set<String> stringSet;
      if (CollectionUtils.isNotEmpty(stringList)) {
        stringSet = CollectionUtil.listToSet(stringList);
      } else {
        stringSet = new HashSet<String>();
      }
      //将上传来的新敏感词添加到原敏感词列表中
      stringSet.addAll(sensitiveWordsDTO.getLetters());
      byte[] wordsData = StringUtil.stringSetToByteArray(stringSet);
      List<SensitiveWords> sensitiveWordsList = writer.getSensitiveWords();
      if (CollectionUtils.isEmpty(sensitiveWordsList)) {
        return null;
      }
      SensitiveWords sensitiveWords = sensitiveWordsList.get(0);
      sensitiveWords.setWords(wordsData);
      //更新敏感词
      writer.update(sensitiveWords);
      writer.commit(status);
      //更新本地缓存
      sensitiveWordsDTO = sensitiveWords.toDTO();
      freshSensitiveWords();
      return sensitiveWordsDTO;
    } finally {
      writer.rollback(status);
    }
  }


  /**
   * 从本地缓存中读取敏感词进行查询功能
   *
   * @return List
   */
  @Override
  public List<String> getSensitiveWords(String word) throws IOException {
    //获取敏感词列表
    List<String> sensitiveWords = getSensitiveWordList();
    if (StringUtils.isBlank(word)) return sensitiveWords;
    List<String> searchSensitiveWords = new ArrayList<String>();
    if (CollectionUtils.isEmpty(sensitiveWords)) return searchSensitiveWords;
    for (String s : sensitiveWords) {
      if (s.contains(word)) {
        searchSensitiveWords.add(s);
      }
    }
    return searchSensitiveWords;
  }

  /**
   * 从本地缓存中读取敏感词进行查询功能
   *
   * @return List
   */
  @Override
  public List<String> getSensitiveWordList() throws IOException {
    SensitiveWordsDTO sensitiveWordsDTO = (SensitiveWordsDTO) MemCacheAdapter.get(MemcachePrefix.sensitiveWords.getValue());
    if (sensitiveWordsDTO == null) {
      sensitiveWordsDTO = getSensitiveWordFromDB();
      if (sensitiveWordsDTO != null) MemCacheAdapter.set(MemcachePrefix.sensitiveWords.getValue(), sensitiveWordsDTO);
    }
    return sensitiveWordsDTO != null ? sensitiveWordsDTO.getLetters() : new ArrayList<String>();
  }

  /**
   * 从数据库中获得 SensitiveWordsDTO
   *
   * @return
   */
  @Override
  public SensitiveWordsDTO getSensitiveWordFromDB() {
    NotificationWriter writer = notificationDaoManager.getWriter();
    List<SensitiveWords> words = writer.getSensitiveWords();
    if (CollectionUtils.isEmpty(words)) {
      return null;
    }
    SensitiveWords sensitiveWord = words.get(0);
    return sensitiveWord.toDTO();
  }

  /**
   * 读取本地敏感词进行分页查询
   *
   * @return
   */
  @Override
  public List<String> getSensitiveWords(String word, Pager pager) throws IOException {
    List<String> sensitiveWords = getSensitiveWordList();
    if (StringUtils.isBlank(word)) {
      return sensitiveWords.subList(pager.getRowStart(), pager.getRowEnd());
    }
    List<String> searchSensitiveWords = new ArrayList<String>();
    if (CollectionUtils.isEmpty(sensitiveWords)) return searchSensitiveWords;
    for (String s : sensitiveWords) {
      /*查询*/
      if (s.contains(word)) {
        searchSensitiveWords.add(s);
      }
    }
    /*分页，索引越界由pager控制*/
    return searchSensitiveWords.subList(pager.getRowStart(), pager.getRowEnd());
  }


  /**
   * 更新单个关键词
   *
   * @param newWord
   * @param oldWord
   * @return
   * @throws java.io.IOException
   */
  @Override
  public SensitiveWordsDTO updateSensitiveWordsByWord(String newWord, String oldWord) throws IOException {
    NotificationWriter writer = notificationDaoManager.getWriter();
    Object status = writer.begin();
    try {/*从本地缓存读取敏感词列表*/
      List<String> stringList = this.getSensitiveWordList();
      if (stringList == null) {
        return null;
      }
      Set<String> stringSet = CollectionUtil.listToSet(stringList);
      String editWord = StringUtil.getListStringToString(stringSet);
      String newEditWord = null;
      /*用新的敏感词替换旧的敏感词*/
      if (!stringSet.contains(newWord)) {
        newEditWord = editWord.replace(oldWord + " ", newWord + " ");
      } else {
        newEditWord = editWord;
      }
      byte[] wordsData = newEditWord.getBytes();
      SensitiveWords sensitiveWords = writer.getSensitiveWords().get(0);
      sensitiveWords.setWords(wordsData);
      /*更新敏感词*/
      writer.update(sensitiveWords);
      writer.commit(status);
      SensitiveWordsDTO sensitiveWordsDTO = sensitiveWords.toDTO();
      freshSensitiveWords();
      return sensitiveWordsDTO;
    } finally {
      writer.rollback(status);
    }
  }


  /**
   * 删除敏感词
   *
   * @param word
   * @return
   * @throws java.io.IOException
   */
  @Override
  public SensitiveWordsDTO removeSensitiveWord(String word) throws IOException {
    NotificationWriter writer = notificationDaoManager.getWriter();
    Object status = writer.begin();
    try {
      //从本地缓存读取敏感词列表
      List<String> stringList = this.getSensitiveWordList();
      if (CollectionUtils.isNotEmpty(stringList)) stringList.remove(word);
      byte[] wordsData = StringUtil.getListStringToByte(stringList);
      SensitiveWords sensitiveWords = writer.getSensitiveWords().get(0);
      sensitiveWords.setWords(wordsData);
      //更新敏感词
      writer.update(sensitiveWords);
      writer.commit(status);
      //修改本地缓存
      SensitiveWordsDTO sensitiveWordsDTO = sensitiveWords.toDTO();
      freshSensitiveWords();
      return sensitiveWordsDTO;
    } finally {
      writer.rollback(status);
    }
  }

  /**
   * 验证敏感词
   *
   * @param smsMessage String
   */
  @Override
  public String validateSensitiveWord(String smsMessage) {
    //获取memcache里敏感词
    SensitiveWordsDTO sensitiveWordsDTOMem = this.getSensitiveWordsDTO();
    if(sensitiveWordsDTOMem == null){
      return null;
    }
    List<String> sensitiveWords = sensitiveWordsDTOMem.getLetters();
    String resultString = null;
    for (String s : sensitiveWords) {
      if (smsMessage.contains(s)) {
        resultString = resultString==null?"":resultString;
        resultString += s + ";";
      }
    }
    return resultString;
  }

  /**
   * 从memcache获得敏感词
   *
   * @return SensitiveWordsDTO
   */
  @Override
  public SensitiveWordsDTO getSensitiveWordsDTO() {
    /*从memcache获得敏感词*/
    SensitiveWordsDTO sensitiveWordsDTO = (SensitiveWordsDTO) MemCacheAdapter.get(MemcachePrefix.sensitiveWords.getValue());
    /*如果memcache没有敏感词，则从数据中重新读取一次载入memcache中*/
    if (sensitiveWordsDTO == null) {
      sensitiveWordsDTO = this.getSensitiveWordFromDB();
      if (sensitiveWordsDTO == null) {
        return null;
      }
      sensitiveWordsDTO.setSyncTime(System.currentTimeMillis());
      MemCacheAdapter.set(MemcachePrefix.sensitiveWords.getValue(), sensitiveWordsDTO);
    }
    return sensitiveWordsDTO;
  }

  /**
   * 刷新memcache
   *
   * @return
   */
  @Override
  public SensitiveWordsDTO freshSensitiveWords() {
    /*从数据中读取敏感词*/
    SensitiveWordsDTO sensitiveWordsDTO = this.getSensitiveWordFromDB();
    /*更新memcache*/
    sensitiveWordsDTO.setSyncTime(System.currentTimeMillis());
    MemCacheAdapter.set(MemcachePrefix.sensitiveWords.getValue(), sensitiveWordsDTO);
    return sensitiveWordsDTO;
  }


  /**
   * 添加敏感词时，敏感词重复验证
   *
   * @param word String
   */
  public String verifyDuplicateSensitiveWord(String word) {
    //获取MemCache里敏感词
    SensitiveWordsDTO wordsDTO = this.getSensitiveWordsDTO();
    if (wordsDTO != null) {
      List<String> sensitiveWords = wordsDTO.getLetters();
      if (sensitiveWords == null) sensitiveWords = new ArrayList<String>();
      for (String s : sensitiveWords) {
        if (word.equals(s)) {
          return "failed";
        }
      }
    }
    return "success";
  }


  /**
   * 分页查询发送失败短信
   *
   * @param failedSmsJobDTO FailedSmsJobDTO
   * @param pager           Pager
   */
  @Override
  public List<FailedSmsJobDTO> getFailedSmsJobDTOs(FailedSmsJobDTO failedSmsJobDTO, Pager pager) {
    NotificationWriter writer = notificationDaoManager.getWriter();
    ConfigService configService = ServiceManager.getService(ConfigService.class);
    //根据shop名称查询shopID
    List<ShopDTO> shops = configService.getShopByObscureName(failedSmsJobDTO.getName());
    List<Long> shopIds = new ArrayList<Long>();
    if (CollectionUtils.isNotEmpty(shops)) {
      for (ShopDTO p : shops) {
        shopIds.add(p.getId());
      }
    }
    //根据shopID分页查询
    List<FailedSmsJob> failedSmsJobs = writer.getFailedSmsJobs(failedSmsJobDTO, pager, shopIds);
    if (CollectionUtils.isEmpty(failedSmsJobs))
      return null;
    List<FailedSmsJobDTO> failedSmsJobDTOs = new ArrayList<FailedSmsJobDTO>();

    for (FailedSmsJob f : failedSmsJobs) {
      if (f == null) continue;            //f为空跳过
      FailedSmsJobDTO failedSmsJobDto = f.toDTO();
      failedSmsJobDTOs.add(failedSmsJobDto);
    }
    formFailedSmsJob(failedSmsJobDTOs);
    return failedSmsJobDTOs;
  }

  /**
   * 将shop名称附加到failedSmsJobDTOs
   *
   * @param failedSmsJobDTOs List<FailedSmsJobDTO>
   */
  public void formFailedSmsJob(List<FailedSmsJobDTO> failedSmsJobDTOs) {
    ConfigService configService = ServiceManager.getService(ConfigService.class);
    if (CollectionUtils.isEmpty(failedSmsJobDTOs))
      return;
    for (FailedSmsJobDTO failedSmsJobDTO : failedSmsJobDTOs) {
      ShopDTO shopDTO = null;
      if (failedSmsJobDTO.getShopId() != null) {
        if (failedSmsJobDTO.getShopId() == ShopConstant.BC_SHOP_ID) {
          failedSmsJobDTO.setName("BCGOGO_SYSTEM");
        } else {
          shopDTO = configService.getShopById(failedSmsJobDTO.getShopId());
          if (shopDTO == null) continue;
          //将shop名称附加到 failedSmsJobDTOs
          failedSmsJobDTO.setName(shopDTO.getName());
        }
      }
    }
  }


  /**
   * 发送失败短信总条数
   *
   * @return int
   */
  @Override
  public int getCountFailedSmsJob(FailedSmsJobDTO failedSmsJobDTO) {
    ConfigService configService = ServiceManager.getService(ConfigService.class);
    List<ShopDTO> shops = configService.getShopByObscureName(failedSmsJobDTO.getName());
    List<Long> shopIds = new ArrayList<Long>();
    if (CollectionUtils.isNotEmpty(shops)) {
      for (ShopDTO p : shops) {
        shopIds.add(p.getId());
      }
    }
    NotificationWriter writer = notificationDaoManager.getWriter();
    return writer.getCountFailedSmsJob(failedSmsJobDTO, shopIds);
  }

  /**
   * 将失败短信放到smsJob中
   *
   * @param id
   * @param receiveMobile
   * @param smsContent
   */
  @Override
  public String saveSmsJobFromFailedSms(Long id, String receiveMobile, String smsContent) {
    NotificationWriter writer = notificationDaoManager.getWriter();
    Object status = writer.begin();
    try {
      String resultString = this.validateSensitiveWord(smsContent);
      //如果验证失败，则返回失败信息
      if (resultString != null) {
        return resultString;
      }
      FailedSmsJob failedSmsJob = writer.getById(FailedSmsJob.class, id);
      failedSmsJob.setReceiveMobile(receiveMobile);
      failedSmsJob.setContent(smsContent);
      SmsJob smsJob = failedSmsJob.toSmsJob();
      smsJob.setReponseReason(null);
      smsJob.setStatus(null);
      //保存到SmsJob中
      writer.save(smsJob);
      //删除失败短信
      writer.delete(failedSmsJob);
      writer.commit(status);
      return resultString == null ? "success" : resultString;
    } catch(Exception e){
      LOG.error("saveSmsJobFromFailedSms", e);
      return "验证敏感词失败";
    } finally{
      writer.rollback(status);
    }
  }

  public void clearSensitiveWord() throws IOException {
    NotificationWriter writer = notificationDaoManager.getWriter();
    Object status = writer.begin();
    try {
      List<SensitiveWords> sensitiveWordsList = writer.getSensitiveWords();
      if (CollectionUtils.isEmpty(sensitiveWordsList)) {
        return;
      }
      SensitiveWords sensitiveWords = sensitiveWordsList.get(0);
      sensitiveWords.setWords(null);
      //更新敏感词
      writer.update(sensitiveWords);
      writer.commit(status);
      freshSensitiveWords();
    } finally {
      writer.rollback(status);
    }
  }
}
