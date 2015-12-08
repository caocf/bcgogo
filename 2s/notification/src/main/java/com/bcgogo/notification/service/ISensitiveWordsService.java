package com.bcgogo.notification.service;

import com.bcgogo.common.Pager;
import com.bcgogo.notification.dto.FailedSmsJobDTO;
import com.bcgogo.notification.dto.SensitiveWordsDTO;

import java.io.IOException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhangchuanlong
 * Date: 12-8-21
 * Time: 上午9:14
 * <p/>
 * 短信敏感词service
 */
public interface ISensitiveWordsService {
  /**
   * 分页查询发送失败短信
   */
  public List<FailedSmsJobDTO> getFailedSmsJobDTOs(FailedSmsJobDTO failedSmsJobDTO, Pager pager);

  public void formFailedSmsJob(List<FailedSmsJobDTO> failedSmsJobDTOs);

  /**
   * 发送失败短信总条数
   */
  public int getCountFailedSmsJob(FailedSmsJobDTO failedSmsJobDTO);

  /**
   * 保存敏感词
   */
  public SensitiveWordsDTO saveSensitiveWords(SensitiveWordsDTO sensitiveWordsDTO);

  /**
   * 从本地缓存中读取敏感词进行查询功能
   */
  public List<String> getSensitiveWords(String word) throws IOException;

  /**
   * 从本地缓存中读取敏感词
   */
  public List<String> getSensitiveWordList() throws IOException;

  /**
   * 从数据库中获得 SensitiveWordsDTO
   */
  public SensitiveWordsDTO getSensitiveWordFromDB();

  /**
   * 读取本地敏感词进行分页查询
   */
  public List<String> getSensitiveWords(String word, Pager pager) throws IOException;

  /**
   * 保存或更新敏感词
   */
  public void saveOrUpdateSensitiveWords(SensitiveWordsDTO sensitiveWordsDTO) throws IOException;

  /**
   * 上传时更新敏感词
   */
  public SensitiveWordsDTO updateSensitiveWords(SensitiveWordsDTO sensitiveWordsDTO) throws IOException;

  /**
   * 更新单个关键词
   */
  public SensitiveWordsDTO updateSensitiveWordsByWord(String newWord, String oldWord) throws IOException;

  /**
   * 删除敏感词
   */
  public SensitiveWordsDTO removeSensitiveWord(String word) throws IOException;

  /**
   * 验证敏感词
   */
  public String validateSensitiveWord(String smsMessage);

  /**
   * 从memcache获得敏感词
   *
   * @return SensitiveWordsDTO
   */
  public SensitiveWordsDTO getSensitiveWordsDTO();

  /**
   * 刷新memcache
   */
  public SensitiveWordsDTO freshSensitiveWords();

  /**
   * 添加敏感词时，敏感词重复验证
   */
  public String verifyDuplicateSensitiveWord(String word);

  /**
   * 将失败短信放到smsJob中
   */
  public String saveSmsJobFromFailedSms(Long id, String receiveMobile, String smsContent);

  void clearSensitiveWord() throws IOException;
}
