package com.bcgogo.sms;

import com.bcgogo.AbstractTest;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.util.MemCacheAdapter;
import com.bcgogo.constant.MemcachePrefix;
import com.bcgogo.enums.sms.SenderType;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.exception.PageException;
import com.bcgogo.notification.dto.FailedSmsJobDTO;
import com.bcgogo.notification.model.FailedSmsJob;
import com.bcgogo.notification.model.NotificationWriter;
import com.bcgogo.notification.model.SensitiveWords;
import com.bcgogo.notification.service.SensitiveWordsService;
import com.bcgogo.notification.util.LocalCacheManager;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.utils.StringUtil;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.ui.ModelMap;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: zhangchuanlong
 * Date: 12-8-13
 * Time: 下午2:39
 */
public class FailedSmsControllerTest extends AbstractTest {
  private FailedSmsController failedSmsController;
  public ModelMap model;

  @Before
  public void setUp() throws Exception {
    failedSmsController = new FailedSmsController();
    failedSmsController.setSensitiveWordsService(ServiceManager.getService(SensitiveWordsService.class));
    model = new ModelMap();
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
  }

  @After
  public void after() throws Exception {
    MemCacheAdapter.delete(MemcachePrefix.sensitiveWords.getValue());
  }


  /**
   * 获取失败短信列表
   *
   * @throws com.bcgogo.exception.BcgogoException
   *
   */
  @Test
  public void testListFailedSms() throws BcgogoException, IOException {
    /*无数据时*/
    failedSmsController.listFailedSms(model, response, 1, null, null, null);
    List<FailedSmsJobDTO> failedSmsJobDTONoData = (List<FailedSmsJobDTO>) model.get("failedSmsJobDTOList");
    Assert.assertNull(failedSmsJobDTONoData);
    /*创建一个shop*/
    ShopDTO shopDTO = createShop();
    /*创建失败短信,页数小于pageSize=10*/
    createFailedSms(shopDTO.getId(), 9);
    /*输入数据不满足条件*/
    failedSmsController.listFailedSms(model, response, 1, "ddd", "tttt", "kkk");
    List<FailedSmsJobDTO> failedSmsJobDTOs = (List<FailedSmsJobDTO>) model.get("failedSmsJobDTOList");
    Assert.assertNull(failedSmsJobDTOs);
    /*有查询条件时测*/
    failedSmsController.listFailedSms(model, response, 1, "统购", "1", "content");
    List<FailedSmsJobDTO> failedSmsJobDTOSearch = (List<FailedSmsJobDTO>) model.get("failedSmsJobDTOList");
    Assert.assertEquals(9, failedSmsJobDTOSearch.size());
    Assert.assertEquals(SenderType.Shop, failedSmsJobDTOSearch.get(0).getSender());
    Assert.assertEquals("content0", failedSmsJobDTOSearch.get(0).getContent());
    Assert.assertEquals("15805556120", failedSmsJobDTOSearch.get(0).getReceiveMobile());
    Assert.assertEquals("110", failedSmsJobDTOSearch.get(0).getSmsId());
    Assert.assertEquals("failed", failedSmsJobDTOSearch.get(0).getStatus());
    Assert.assertEquals("reseaon0", failedSmsJobDTOSearch.get(0).getReponseReason());
  }

  /**
   * 保存敏感词
   */
  @Test
  public void testSaveSensitiveWord() throws IOException {
    String url = failedSmsController.saveSensitiveWord(request, "我们");
    Assert.assertEquals("/sms/sensitiveWordsList", url);
  }

  /**
   * 更新敏感词
   *
   * @throws java.io.IOException
   */
  @Test
  public void testUpdateSensitiveWord() throws IOException {
    String url = failedSmsController.saveSensitiveWord(request, "我们");
    Assert.assertEquals("/sms/sensitiveWordsList", url);
    String urlEdit = failedSmsController.updateSensitveWord(request, "你们", "我们");
    Assert.assertEquals("/sms/sensitiveWordsList", urlEdit);
  }

  /**
   * 删除敏感词
   *
   * @throws java.io.IOException
   */
  @Test
  public void testDeleteSensitiveWord() throws IOException {
    String url = failedSmsController.saveSensitiveWord(request, "我们");
    Assert.assertEquals("/sms/sensitiveWordsList", url);
    String urlDelete = failedSmsController.deleteSensitiveWord(request, "我们");
    Assert.assertEquals("/sms/sensitiveWordsList", urlDelete);
  }

  /**
   * 验证敏感词重复
   *
   * @throws java.io.IOException
   */
  @Test
  public void testValidateAddSensitiveWord() throws IOException {
    String url = failedSmsController.saveSensitiveWord(request, "我们");
    Assert.assertEquals("/sms/sensitiveWordsList", url);
    failedSmsController.validateAddSensitiveWord(model, request, response, "我们");
    Assert.assertEquals("failed", model.get("result"));
  }

  /**
   * 发送短信
   *
   * @throws com.bcgogo.exception.BcgogoException
   *
   */
  @Test
  public void tesTsendSmsJob() throws Exception {
    /*创建一个shop*/
    ShopDTO shopDTO = createShop();
    /*创建失败短信*/
    List<FailedSmsJob> failedSmsJobs = createFailedSms(shopDTO.getId(), 1);
    failedSmsController.sendSmsJob(model, response, failedSmsJobs.get(0).getId(), "15805556125", "欠费欠费");
    Assert.assertEquals("success", model.get("result"));
  }

  /**
   * 上传敏感词
   */
  @Test
  public void testUploadSensitiveWords() throws IOException {
    MockMultipartHttpServletRequest request = new MockMultipartHttpServletRequest();
    Set<String> sensitiveWords = new HashSet<String>();
    sensitiveWords.add("中国");
    sensitiveWords.add("美国");
    byte[] wordsData = StringUtil.getListStringToByte(sensitiveWords);
    InputStream is = new ByteArrayInputStream(wordsData);
    MockMultipartFile multipartFile = new MockMultipartFile("sensitiveWords", null, null, is);
    request.addFile(multipartFile);
    request.setMethod("POST");
    request.setContentType("multipart/form-data");
    request.addHeader("Content-type", "multipart/form-data");
    String url = failedSmsController.uploadSensitiveWords(request);
    Assert.assertEquals("sms/uploadSensitiveWords", url);
  }

  private void deleteCacheAndDB() {
    NotificationWriter writer = notificationDaoManager.getWriter();
    Object status = writer.begin();
    List<SensitiveWords> sensitiveWordsList = writer.getSensitiveWords();
    if (org.apache.commons.collections.CollectionUtils.isNotEmpty(sensitiveWordsList)) {
      try {
        for (SensitiveWords sensitiveWords : sensitiveWordsList) {
          writer.delete(SensitiveWords.class, sensitiveWords.getId());
        }
        writer.commit(status);
      } finally {
        writer.rollback(status);
      }
    }
    LocalCacheManager.clearSensitiveWordsCache();
  }

  @Test
  public void testGetSensitiveWords() throws IOException, PageException {
    deleteCacheAndDB();
    /*无数据时*/
    failedSmsController.getSensitiveWords(model, response, 1, null);
    List<String> sensitiveWordsNoData = (List<String>) model.get("sensitiveWords");
    /*总数据不超过页大小！2条数据，pageSize=10*/
    MockMultipartHttpServletRequest request = new MockMultipartHttpServletRequest();
    InputStream is = new ByteArrayInputStream("中国".getBytes());
    MockMultipartFile multipartFile = new MockMultipartFile("sensitiveWords", null, null, is);
    request.addFile(multipartFile);
    request.setMethod("POST");
    request.setContentType("multipart/form-data");
    request.addHeader("Content-type", "multipart/form-data");
    String url = failedSmsController.uploadSensitiveWords(request);
    Assert.assertNull(sensitiveWordsNoData);
    /*输入数据不满足条件*/
    Assert.assertEquals("sms/uploadSensitiveWords", url);
    failedSmsController.getSensitiveWords(model, response, 1, "日本");
    List<String> sensitiveWordsNoSearch = (List<String>) model.get("sensitiveWords");
    Assert.assertNull(sensitiveWordsNoSearch);
    /*有数据，有查询时*/
    Assert.assertEquals("sms/uploadSensitiveWords", url);
    failedSmsController.getSensitiveWords(model, response, 1, "中");
    List<String> sensitiveWords = (List<String>) model.get("sensitiveWords");
    Assert.assertEquals(1, sensitiveWords.size());
    Assert.assertEquals("中国", sensitiveWords.get(0));

  }

  /*
  * 1.创建失败短信
  * 2.分页查询
  */
  public static List<FailedSmsJob> createFailedSms(Long shopId, int n) {
    List<FailedSmsJob> failedSmsJobs = new ArrayList<FailedSmsJob>();
    for (int i = 0; i < n; i++) {
      FailedSmsJob failedSmsJob = new FailedSmsJob();
      failedSmsJob.setSender(SenderType.Shop);
      failedSmsJob.setContent("content" + i);
      failedSmsJob.setShopId(shopId);
      failedSmsJob.setReceiveMobile("1580555612" + i);
      failedSmsJob.setUserId(1L);
      failedSmsJob.setStartTime(System.currentTimeMillis());
      failedSmsJob.setLastSendTime(System.currentTimeMillis());
      failedSmsJob.setStartTime(System.currentTimeMillis());
      failedSmsJob.setSmsId("11" + i);
      failedSmsJob.setStatus("failed");
      failedSmsJob.setType(11);
      failedSmsJob.setReponseReason("reseaon" + i);
      Object status = notificationWriter.begin();
      try {
        notificationWriter.save(failedSmsJob);
        notificationWriter.commit(status);
        failedSmsJobs.add(failedSmsJob);
      } finally {
        notificationWriter.rollback(status);
      }
    }
    return failedSmsJobs;
  }

}
