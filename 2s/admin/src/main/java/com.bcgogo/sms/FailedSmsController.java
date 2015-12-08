package com.bcgogo.sms;

import com.bcgogo.common.Pager;
import com.bcgogo.exception.PageException;
import com.bcgogo.notification.client.lianyu.LianYuSmsParam;
import com.bcgogo.notification.dto.FailedSmsJobDTO;
import com.bcgogo.notification.dto.SensitiveWordsDTO;
import com.bcgogo.notification.service.ISensitiveWordsService;
import com.bcgogo.notification.service.SensitiveWordsService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.util.PopMessage;
import com.bcgogo.utils.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: zhangchuanlong
 * Date: 12-8-6
 * Time: 下午4:54
 */
@Controller
@RequestMapping("/sms.do")
public class FailedSmsController {
  private static final Logger LOG = LoggerFactory.getLogger(FailedSmsController.class);
  @Autowired
  private SensitiveWordsService sensitiveWordsService;

  public void setSensitiveWordsService(SensitiveWordsService sensitiveWordsService) {
    this.sensitiveWordsService = sensitiveWordsService;
  }

  @RequestMapping(params = "method=initFailedSmsPage")
  public String failedSms() {
    return "/sms/failedSms";
  }

  /**
   * 短信发送页面初始化
   *
   *
   */
  @RequestMapping(params = "method=failedSmsList")
  public void listFailedSms(ModelMap model, HttpServletResponse response, Integer startPageNo, String shopName, String mobile, String content) throws PageException {
    FailedSmsJobDTO failedSmsJobDTO = new FailedSmsJobDTO();
    /*手机号码*/
    failedSmsJobDTO.setReceiveMobile(mobile);
    /*短信内容*/
    failedSmsJobDTO.setContent(content);
    /*店面名称*/
    failedSmsJobDTO.setName(shopName);
    /*总数*/
    int total = sensitiveWordsService.getCountFailedSmsJob(failedSmsJobDTO);
    /*分页*/
    Pager pager = new Pager(total, NumberUtil.intValue(String.valueOf(startPageNo), 1));
    /*失败短信分页查询list*/
    List<FailedSmsJobDTO> failedSmsJobDTOList = sensitiveWordsService.getFailedSmsJobDTOs(failedSmsJobDTO, pager);
    String jsonStr = "";
    /*JSON*/
    jsonStr = JsonUtil.listToJson(failedSmsJobDTOList);
    jsonStr = jsonStr.substring(0, jsonStr.length() - 1);
    if (!"[".equals(jsonStr.trim())) {
      jsonStr = jsonStr + "," + pager.toJson().substring(1, pager.toJson().length());
    } else {
      jsonStr = pager.toJson();
    }
    try {
      PrintWriter writer = response.getWriter();
      writer.write(jsonStr);
      writer.close();
    } catch (Exception e) {
      LOG.debug("/sms.do");
      LOG.debug("method=failedSmsList");
      LOG.error(e.getMessage(), e);
    }
    model.addAttribute("failedSmsJobDTOList", failedSmsJobDTOList);
  }

  /**
   * 短信敏感词上传页面 初始化
   * @return 跳转页面
   */
  @RequestMapping(params = "method=initUploadSensitiveWordsPage")
  public String initUploadSensitiveWordsPage() {
    return "sms/uploadSensitiveWords";
  }

  /**
   * 短信敏感词上传
   */
  @RequestMapping(params = "method=uploadSensitiveWords")
  public String uploadSensitiveWords(HttpServletRequest request) throws IOException {
    SensitiveWordsDTO sensitiveWordsDTO = new SensitiveWordsDTO();
    if (request instanceof MultipartHttpServletRequest) {
      MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
      MultipartFile multipartFile = multipartHttpServletRequest.getFile("sensitiveWords");
      if (multipartFile == null) return "sms/uploadSensitiveWords";
      InputStream is = multipartFile.getInputStream();
      /*获得上传敏感词列表*/
      Set<String> sensitiveWords = IOUtil.getStringsFromInputStream(is);
      byte[] wordsData = StringUtil.getListStringToByte(sensitiveWords);
      /*转为二进制*/
      sensitiveWordsDTO.setWords(wordsData);
    }
    try {
      //上传敏感词
      sensitiveWordsService.saveOrUpdateSensitiveWords(sensitiveWordsDTO);
    } catch (Exception e) {
      LOG.debug("/sms.do");
      LOG.debug("method=uploadSensitiveWords");
      LOG.error(e.getMessage(), e);
    }
    return "sms/uploadSensitiveWords";
  }

  /**
   * 敏感词查询显示列表
   */
  @RequestMapping(params = "method=getSensitiveWords")
  public void getSensitiveWords(ModelMap model, HttpServletResponse response, int startPageNo, String word) throws IOException, PageException {
    //查询总条目
    List<String> sensitiveWordsTotal = sensitiveWordsService.getSensitiveWords(word);
    String jsonStr = "";
    int pageSize = 20;    //分页
    int total = 0;
    if (CollectionUtils.isNotEmpty(sensitiveWordsTotal)) {
      total = sensitiveWordsTotal.size();
    }
    Pager pager = new Pager(total, NumberUtil.intValue(String.valueOf(startPageNo), 1), pageSize);
    List<String> sensitiveWords = null;
    if (CollectionUtils.isNotEmpty(sensitiveWordsTotal)) {
      /*分页查询敏感词*/
      sensitiveWords = sensitiveWordsService.getSensitiveWords(word, pager);
    }
    //敏感词JSON
    jsonStr = JsonUtil.listToJson(sensitiveWords);
    jsonStr = jsonStr.substring(0, jsonStr.length() - 1);
    if (!"[".equals(jsonStr.trim())) {
      jsonStr = jsonStr + "," + pager.toJson().substring(1, pager.toJson().length());
    } else {
      jsonStr = pager.toJson();
    }
    model.addAttribute("sensitiveWords", sensitiveWords);

    try {
      PrintWriter writer = response.getWriter();
      writer.write(jsonStr);
      writer.close();
    } catch (Exception e) {
      LOG.debug("/sms.do");
      LOG.debug("method=getSensitiveWords");
      LOG.error(e.getMessage(), e);
    }
  }

  @RequestMapping(params = "method=initSensitiveWordPage")
  public String initSensitiveWordPage() {
    return "/sms/sensitiveWordsList";
  }

  /**
   * 添加单个敏感词
   *
   * @param request
   * @return
   * @throws java.io.IOException
   */
  @RequestMapping(params = "method=saveSensitiveWord")
  public String saveSensitiveWord(HttpServletRequest request, String word) throws IOException {
    SensitiveWordsDTO sensitiveWordsDTO = new SensitiveWordsDTO();
    if (!StringUtil.isEmpty(word)) {
//      sensitiveWordsDTO.setWords(RegexUtils.format(word).getBytes());
      sensitiveWordsDTO.setWords(word.getBytes());
    }
    try {
      /*添加敏感词*/
      sensitiveWordsService.saveOrUpdateSensitiveWords(sensitiveWordsDTO);
    } catch (Exception e) {
      LOG.debug("/sms.do");
      LOG.debug("method=saveSensitiveWord");
      LOG.error(e.getMessage(), e);
    }
    return initSensitiveWordPage();
  }

  /**
   * 修改单个敏感词
   *
   * @param request
   * @throws java.io.IOException
   */
  @RequestMapping(params = "method=updateSensitiveWord")
  public String updateSensitveWord(HttpServletRequest request, String newWord, String oldWord) throws IOException {
    try {
      /*修改敏感词*/
      sensitiveWordsService.updateSensitiveWordsByWord(newWord, oldWord);
    } catch (Exception e) {
      LOG.debug("/sms.do");
      LOG.debug("method=updateSensitiveWord");
      LOG.error(e.getMessage(), e);
    }
    return initSensitiveWordPage();
  }

  @RequestMapping(params = "method=clearSensitiveWord")
  public String clearSensitiveWord(HttpServletRequest request) throws IOException {
    try {
      ServiceManager.getService(ISensitiveWordsService.class).clearSensitiveWord();
    } catch (Exception e) {
      LOG.debug("/sms.do");
      LOG.debug("method=clearSensitiveWord");
      LOG.error(e.getMessage(), e);
    }
    return initSensitiveWordPage();
  }

  /**
   * 删除敏感词
   *
   * @param request
   * @throws java.io.IOException
   */
  @RequestMapping(params = "method=deleteSensitiveWord")
  public String deleteSensitiveWord(HttpServletRequest request, String word) throws IOException {
    try {
      /*删除敏感词*/
      sensitiveWordsService.removeSensitiveWord(word);
    } catch (Exception e) {
      LOG.debug("/sms.do");
      LOG.debug("method=deleteSensitiveWord");
      LOG.error(e.getMessage(), e);
    }
    return initSensitiveWordPage();
  }

  /**
   * 添加敏感词时，敏感词重复验证
   *
   * @param request
   * @param response
   */
  @RequestMapping(params = "method=validateAddSensitiveWord")
  public void validateAddSensitiveWord(ModelMap model, HttpServletRequest request, HttpServletResponse response, String word) {
    /*验证敏感词*/
    String result = sensitiveWordsService.verifyDuplicateSensitiveWord(word);
    String jsonStr = "{\"result\"" + ":\"" + result + "\"}";
    try {
      PrintWriter writer = response.getWriter();
      writer.write(jsonStr);
      writer.close();
    } catch (Exception e) {
      LOG.debug("/sms.do");
      LOG.debug("method=validateAddSensitiveWord");
      LOG.error(e.getMessage(), e);
    }
    model.addAttribute("result", result);//用做单元测试
  }

  /**
   * 发送失败短信
   */
  @RequestMapping(params = "method=sendSmsJob")
  public void sendSmsJob(ModelMap model, HttpServletResponse response, Long id, String receiveMobile, String smsContent) {
    try {
      /*发送并验证敏感词*/
      String result = sensitiveWordsService.saveSmsJobFromFailedSms(id, receiveMobile, smsContent);
      /*消息提示类*/
      PopMessage message = new PopMessage();
      message.setMessage(result);
      String jsonStr = JsonUtil.objectToJson(message);
      PrintWriter writer = response.getWriter();
      writer.write(jsonStr);
      writer.close();
      model.addAttribute("result", result);
    } catch (Exception e) {
      LOG.debug("/sms.do");
      LOG.debug("method=sendSmsJob");
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * 屏蔽词检测
   */
  @RequestMapping(params = "method=testStopWords")
  public String testStopWords(HttpServletRequest request, String content,ModelMap model) {
    try {
      LianYuSmsParam smsSendParam = new LianYuSmsParam();
      smsSendParam.setUserName(SmsConstant.SmsLianYuConstant.userName);
      smsSendParam.setPassword(SmsConstant.SmsLianYuConstant.password);
      smsSendParam.setContent(content);
      String result = lianYuInterface(SmsConstant.SmsLianYuConstant.detectionStopWords, smsSendParam);
      String code = XMLParser.getRootElement(result, "resultcode");
      String message = XMLParser.getRootElement(result, "errordescription");
      model.addAttribute("message",message);
      model.addAttribute("content",content);
    } catch (Exception e) {
      LOG.debug("/sms.do");
      LOG.debug("method=testStopWords");
      LOG.error(e.getMessage(), e);
    }
    return "/sms/stopWordsTest";
  }

  /**
   * 跳转到屏蔽词检测界面
   */
  @RequestMapping(params = "method=toTestStopWords")
  public String toTestStopWords() {
    return "/sms/stopWordsTest";
  }

  public static String lianYuInterface(String name, LianYuSmsParam smsSendParam) {
    String result="";
    try {
      HttpClient client = new HttpClient();
      client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
      PostMethod post = new PostMethod(name);
      NameValuePair username = new NameValuePair("username", smsSendParam.getUserName());
      NameValuePair password = new NameValuePair("password", smsSendParam.getPassword());
      NameValuePair content = new NameValuePair("content", smsSendParam.getContent());
      post.setRequestBody(new NameValuePair[]{username, password, content});
      client.executeMethod(post);
      result = post.getResponseBodyAsString();
      post.releaseConnection();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return result;
  }

  /**
   * 余额查询
   */
  @RequestMapping(params = "method=findBalance")
  public String findBalance(HttpServletRequest request, String content,ModelMap model) {
    try {
      LianYuSmsParam smsSendParam = new LianYuSmsParam();
      smsSendParam.setUserName(SmsConstant.SmsLianYuConstant.userName);
      smsSendParam.setPassword(SmsConstant.SmsLianYuConstant.password);
      String result = lianYuInterface(SmsConstant.SmsLianYuConstant.balanceInquery, smsSendParam);
      String code = XMLParser.getRootElement(result, "resultcode");
      String mes = XMLParser.getRootElement(result, "smsbalancenum");
      model.addAttribute("message","联逾剩余可发短信"+mes+"条");
      model.addAttribute("content",content);
    } catch (Exception e) {
      LOG.debug("/sms.do");
      LOG.debug("method=findBalance");
      LOG.error(e.getMessage(), e);
    }
    return "/sms/stopWordsTest";
  }

}


