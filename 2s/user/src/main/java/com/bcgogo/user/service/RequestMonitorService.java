package com.bcgogo.user.service;

import com.bcgogo.config.model.Shop;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.util.MemCacheAdapter;
import com.bcgogo.constant.MemcachePrefix;
import com.bcgogo.enums.user.UrlMonitorTypeEnum;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.cache.RequestMonitorThread;
import com.bcgogo.user.cache.RequestPerformanceCache;
import com.bcgogo.user.dto.*;
import com.bcgogo.user.model.SystemMonitor.*;
import com.bcgogo.user.model.UserDaoManager;
import com.bcgogo.user.model.UserWriter;
import com.bcgogo.user.model.permission.User;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.JsonUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.*;

/**
 * 系统性能监控
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 13-2-6
 * Time: 下午3:27
 * To change this template use File | Settings | File Templates.
 */
@Component
public class RequestMonitorService implements IRequestMonitorService {

  private static final Logger LOG = LoggerFactory.getLogger(RequestMonitorService.class);

  @Autowired
  private UserDaoManager userDaoManager;


  public void saveOrUpdateRequestMonitorDTO(Collection<RequestPerformanceMonitorDTO> requestMonitorDTOCollection) {
    if (CollectionUtils.isEmpty(requestMonitorDTOCollection)) {
      return;
    }

    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {

      for (RequestPerformanceMonitorDTO requestPerformanceMonitorDTO : requestMonitorDTOCollection) {
        RequestPerformanceMonitor requestPerformanceMonitor = new RequestPerformanceMonitor();
        requestPerformanceMonitor = requestPerformanceMonitor.fromDTO(requestPerformanceMonitorDTO);
        writer.save(requestPerformanceMonitor);
      }
      writer.flush();
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  public void saveOrUpdateUserMonitorInfo(String sessionId, String ip, String browser, String finger, UserDTO userDTO) {
    UserClientInfoDTO userClientInfoDTO = null;
    UserLoginLog userLoginLog = null;
    try {
      Long shopId = userDTO.getShopId();
      String userNo = userDTO.getUserNo();

      userClientInfoDTO = this.getUserClientInfoByUserNo(shopId, userNo, finger);
      if (userClientInfoDTO == null) {
        userClientInfoDTO = new UserClientInfoDTO();
      }
      userClientInfoDTO.setShopId(shopId);
      userClientInfoDTO.setUserNo(userNo);
      userClientInfoDTO.setBrowser(browser);
      userClientInfoDTO.setOs(browser);
      userClientInfoDTO.setFinger(finger);

      userLoginLog = this.getUserLoginLogBySessionIdUserNo(sessionId, userNo, finger);
      userLoginLog.setShopId(shopId);
      userLoginLog.setUserNo(userNo);
      userLoginLog.setLoginTime(System.currentTimeMillis());
      userLoginLog.setLoginIP(ip);
      userLoginLog.setSessionId(sessionId);
      userLoginLog.setFinger(finger);
    } catch (Exception e) {
      LOG.error("RequestMonitorService.saveOrUpdateUserMonitorInfo");
      LOG.error(e.getMessage(), e);
      return;
    }


    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    try {
      UserClientInfo userClientInfo = new UserClientInfo(userClientInfoDTO);
      if (userClientInfo.getId() != null) {
        userClientInfo = writer.getById(UserClientInfo.class, userClientInfo.getId());
        userClientInfo.fromDTO(userClientInfoDTO);
        writer.update(userClientInfo);
      } else {
        writer.save(userClientInfo);
      }

      if (userLoginLog.getId() != null) {
        writer.update(userLoginLog);
      } else {
        writer.save(userLoginLog);
      }

      writer.commit(status);
    } finally {
      writer.rollback(status);
    }


  }

  public UserClientInfo getUserClientInfoById(Long id) {
    if (id == null) return null;
    UserWriter writer = userDaoManager.getWriter();
    return writer.getById(UserClientInfo.class, id);
  }

  @Override
  public void saveOrUpdateUserClientInfoDTO(UserClientInfoDTO clientInfoDTO) {
    UserWriter writer = userDaoManager.getWriter();
    Object status = writer.begin();
    UserClientInfo clientInfo = null;
    try {
      if (clientInfoDTO.getId() != null) {
        clientInfo = getUserClientInfoById(clientInfoDTO.getId());
      } else {
        clientInfo = new UserClientInfo();
      }
      clientInfo.fromDTO(clientInfoDTO);
      writer.saveOrUpdate(clientInfo);
      writer.commit(status);
      clientInfoDTO.setId(clientInfo.getId());
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public UserClientInfoDTO getUserClientInfoByUserNo(Long shopId, String userNo, String finger) {
    UserWriter userWriter = userDaoManager.getWriter();
    UserClientInfo userClientInfo = userWriter.getUserClientInfoByUserNo(shopId, userNo, finger);
    return userClientInfo == null ? null : userClientInfo.toDTO();
  }

  public List<UserClientInfoDTO> getUserClientInfoByFinger(String finger) {
    UserWriter userWriter = userDaoManager.getWriter();
    List<UserClientInfo> clientInfos = userWriter.getUserClientInfoByFinger(finger);
    if (CollectionUtil.isEmpty(clientInfos)) return null;
    List<UserClientInfoDTO> clientInfoDTOs = new ArrayList<UserClientInfoDTO>();
    for (UserClientInfo clientInfo : clientInfos) {
      clientInfoDTOs.add(clientInfo.toDTO());
    }
    return clientInfoDTOs;
  }

  /**
   * 根据客户端登录情况，计算出设备finger和shopId关联关系的得分
   * 一般情况一天得10分，超过90分可确定出设备
   * 总分为100分
   *
   * @throws ParseException
   */
  @Override
  public void calcDeviceFingerScore() throws ParseException {
    //取当天的登录记录
    Long startDateTime = DateUtil.getStartTimeOfToday();
    Long endDateTime = System.currentTimeMillis();
    List<UserLoginLogDTO> userLoginLogDTOs = getUserClientLoginLogDTO(startDateTime, endDateTime);
    if (CollectionUtil.isEmpty(userLoginLogDTOs)) return;
    //先计算加分的情况
    addingDeviceFingerScore(userLoginLogDTOs);
    //下面计算减分的情况
    reduceDeviceFingerScore(userLoginLogDTOs);
  }

  /**
   * 计算加分的情况
   *
   * @param userLoginLogDTOs
   */
  private void addingDeviceFingerScore(List<UserLoginLogDTO> userLoginLogDTOs) {
    //按设备finger+userNo分组
    Map<String, List<UserLoginLogDTO>> userNoMap = new HashMap<String, List<UserLoginLogDTO>>();
    for (UserLoginLogDTO userLoginLogDTO : userLoginLogDTOs) {
      if (StringUtil.isEmpty(userLoginLogDTO.getFinger())) {
        continue;
      }
      String key = userLoginLogDTO.getFinger() + userLoginLogDTO.getUserNo();
      List<UserLoginLogDTO> loginLogDTOs = userNoMap.get(key);
      if (CollectionUtil.isEmpty(loginLogDTOs)) {
        loginLogDTOs = new ArrayList<UserLoginLogDTO>();
        userNoMap.put(key, loginLogDTOs);
      }
      loginLogDTOs.add(userLoginLogDTO);
    }
    //计算得分
    for (String key : userNoMap.keySet()) {
      List<UserLoginLogDTO> loginLogDTOs = userNoMap.get(key);
      int score = 8 + (loginLogDTOs.size() >= 2 ? 2 : 1);  //连续登录一天=8分，一天登录一次得1分,2次以上按2分算
      UserLoginLogDTO loginLogDTO = CollectionUtil.getFirst(loginLogDTOs);
      UserClientInfoDTO clientInfoDTO = getUserClientInfoByUserNo(loginLogDTO.getShopId(), loginLogDTO.getUserNo(), loginLogDTO.getFinger());
      if (clientInfoDTO == null) continue;
      int total = score + (clientInfoDTO.getScore() == null ? 0 : clientInfoDTO.getScore());
      clientInfoDTO.setScore(total > 100 ? 100 : total);
      saveOrUpdateUserClientInfoDTO(clientInfoDTO);
    }
  }

  /**
   * 出现一台设备多个店铺登录,减掉50分
   *
   * @param userLoginLogDTOs
   */
  private void reduceDeviceFingerScore(List<UserLoginLogDTO> userLoginLogDTOs) {
    //按设备finger分组
    Map<String, List<UserLoginLogDTO>> fingerMap = new HashMap<String, List<UserLoginLogDTO>>();
    for (UserLoginLogDTO userLoginLogDTO : userLoginLogDTOs) {
      if (StringUtil.isEmpty(userLoginLogDTO.getFinger())) {
        continue;
      }
      List<UserLoginLogDTO> loginLogDTOs = fingerMap.get(userLoginLogDTO.getFinger());
      if (CollectionUtil.isEmpty(loginLogDTOs)) {
        loginLogDTOs = new ArrayList<UserLoginLogDTO>();
        fingerMap.put(userLoginLogDTO.getFinger(), loginLogDTOs);
      }
      loginLogDTOs.add(userLoginLogDTO);
    }
    Set<String> fingerSet = new HashSet<String>();
    for (String finger : fingerMap.keySet()) {
      List<UserLoginLogDTO> loginLogDTOs = fingerMap.get(finger);
      Long shopId = null;
      for (UserLoginLogDTO loginLogDTO : loginLogDTOs) {
        if (shopId == null) {
          shopId = loginLogDTO.getShopId();
          continue;
        }
        //出现一个设备多个店铺登录情况
        if (!shopId.equals(loginLogDTO.getShopId())) {
          for (UserLoginLogDTO tempDTO : loginLogDTOs) {
            if (fingerSet.contains(tempDTO.getFinger())) {
              continue;
            }
            fingerSet.add(tempDTO.getFinger());
          }
        }
      }
    }
    for (String finger : fingerSet) {
      List<UserClientInfoDTO> clientInfoDTOs = getUserClientInfoByFinger(finger);
      for (UserClientInfoDTO clientInfoDTO : clientInfoDTOs) {
        int total = (clientInfoDTO.getScore() == null ? 0 : clientInfoDTO.getScore()) - 50;
        clientInfoDTO.setScore(total < 0 ? 0 : total);
        saveOrUpdateUserClientInfoDTO(clientInfoDTO);
      }
    }
  }

  public UserLoginLog getUserLoginLogBySessionIdUserNo(String sessionId, String userName, String finger) {
    UserWriter userWriter = userDaoManager.getWriter();
    List<UserLoginLog> userLoginLogList = userWriter.getUserLoginLogByUserNo(sessionId, userName, finger);
    UserLoginLog userLoginLog = null;
    if (CollectionUtils.isEmpty(userLoginLogList)) {
      userLoginLog = new UserLoginLog();
      userLoginLog.setSessionId(sessionId);
      userLoginLog.setUserNo(userName);
    } else {
      userLoginLog = userLoginLogList.get(0);
    }
    return userLoginLog;
  }

  @Override
  public List<UserLoginLogDTO> getUserClientLoginLogDTO(Long startDateTime, Long endDateTime) {
    UserWriter userWriter = userDaoManager.getWriter();
    List<UserLoginLog> userLoginLogs = userWriter.getUserClientLoginLog(startDateTime, endDateTime);
    if (CollectionUtil.isEmpty(userLoginLogs)) return null;
    List<UserLoginLogDTO> userLoginLogDTOs = new ArrayList<UserLoginLogDTO>();
    for (UserLoginLog userLoginLog : userLoginLogs) {
      userLoginLogDTOs.add(userLoginLog.toDTO());
    }
    return userLoginLogDTOs;
  }

  /**
   * 保存用户登出时信息
   *
   * @param sessionId
   * @param userName
   */
  public void saveOrUpdateUserLogOutInfo(String ip, String sessionId, String userName, String finger) {
    UserWriter userWriter = userDaoManager.getWriter();
    UserLoginLog userLoginLog = this.getUserLoginLogBySessionIdUserNo(sessionId, userName, finger);

    userLoginLog.setLoginIP(ip);
    List<User> userList = userWriter.getUserByUserInfo(userName);
    if (CollectionUtils.isNotEmpty(userList)) {
      userLoginLog.setShopId(userList.get(0).getShopId());
    }
    userLoginLog.setLogoutTime(System.currentTimeMillis());
    Object status = userWriter.begin();
    try {

      if (userLoginLog.getId() == null) {
        userWriter.save(userLoginLog);
      } else {
        userWriter.update(userLoginLog);
      }
      userWriter.commit(status);
    } finally {
      userWriter.rollback(status);
    }
  }

  /**
   * 获取监控url配置
   *
   * @return
   */
  public List<UrlMonitorConfig> getUrlMonitorConfig(String url) {
    UserWriter userWriter = userDaoManager.getWriter();
    return userWriter.getUrlMonitorConfig(url);
  }

  /**
   * 从请求中保存监控数据
   *
   * @param shopId
   * @param userNo
   * @param url
   */
  public void saveUrlMonitorStatFromRequest(Long shopId, String userNo, String url) {
    LOG.debug("请求监控数据开始保存,shopId:" + shopId + ",userNo:" + userNo + ",url;" + url);

    if (!RequestPerformanceCache.isIncludeUrl(url)) {
      LOG.debug("请求监控不包含此url,shopId:" + shopId + ",userNo:" + userNo + ",url;" + url);
      return;
    }

    Long statTime = System.currentTimeMillis();

    if (shopId == null || StringUtil.isEmpty(userNo) || StringUtil.isEmpty(url)) {
      LOG.warn("RequestMonitorService.saveUrlMonitorStatFromRequest,shop is null or userNo is null,or url is null,shopId:" + shopId + ",userNo:" + userNo + ",url:" + url);
      return;
    }
    String key = getKeyByShopIdUserNoUrl(shopId, userNo, url);
    UrlMonitorStatDTO urlMonitorStatDTO = (UrlMonitorStatDTO) MemCacheAdapter.get(key);
    if (urlMonitorStatDTO == null) {
      LOG.debug("Memcache不包含此key,key:" + key);
      urlMonitorStatDTO = new UrlMonitorStatDTO();
      urlMonitorStatDTO.setShopId(shopId);
      urlMonitorStatDTO.setUserNo(userNo);
      urlMonitorStatDTO.setUrl(url);
      urlMonitorStatDTO.setYear(DateUtil.getYear(statTime));
      urlMonitorStatDTO.setMonth(DateUtil.getMonth(statTime));
      urlMonitorStatDTO.setDay(DateUtil.getDay(statTime));
      urlMonitorStatDTO.setStatTime(statTime);
      urlMonitorStatDTO.setCount(urlMonitorStatDTO.getCount() + 1);

      LOG.debug("当天不存在的监控key:" + key + "被放入memcache,urlMonitorStatDTO:" + urlMonitorStatDTO.toString());
      MemCacheAdapter.set(key, urlMonitorStatDTO, new Date(System.currentTimeMillis() + DateUtil.DAY_MILLION_SECONDS));
      LOG.debug("当天不存在的memcache开始获得key");
      UrlMonitorStatDTO result = (UrlMonitorStatDTO) MemCacheAdapter.get(key);
      if (result == null) {
        LOG.debug("当天不存在的memcache拿不到这个key:" + key);
      } else {
        LOG.debug("当天不存在的memcache拿到了这个key:" + key + ",result:" + result.toString());
      }

    } else {
      if (DateUtil.isSameDay(urlMonitorStatDTO.getStatTime(), statTime)) {
        urlMonitorStatDTO.setCount(urlMonitorStatDTO.getCount() + 1);
        urlMonitorStatDTO.setStatTime(statTime);
        LOG.debug("当天的监控key:" + key + "被放入memcache,urlMonitorStatDTO:" + urlMonitorStatDTO.toString());
        MemCacheAdapter.set(key, urlMonitorStatDTO, new Date(System.currentTimeMillis() + DateUtil.DAY_MILLION_SECONDS));
        LOG.debug("当天的,memcache开始获得key");
        UrlMonitorStatDTO result = (UrlMonitorStatDTO) MemCacheAdapter.get(key);
        if (result == null) {
          LOG.debug("当天的,memcache拿不到这个key:" + key);
        } else {
          LOG.debug("当天的,memcache拿到了这个key:" + key + ",result:" + result.toString());
        }

      } else {

        LOG.debug("Memcache包含此key,不是当天的,key:" + key);
        LOG.debug("隔天统计数据:" + urlMonitorStatDTO.toString());

        List<UrlMonitorStatDTO> urlMonitorStatDTOList = new ArrayList<UrlMonitorStatDTO>();
        urlMonitorStatDTOList.add(urlMonitorStatDTO);
        RequestMonitorThread requestMonitorThread = new RequestMonitorThread(null, urlMonitorStatDTOList, false);
        requestMonitorThread.run();

        MemCacheAdapter.delete(key);
        UrlMonitorStatDTO monitorStatDTO = new UrlMonitorStatDTO();
        monitorStatDTO.setShopId(shopId);
        monitorStatDTO.setUserNo(userNo);
        monitorStatDTO.setUrl(url);
        monitorStatDTO.setYear(DateUtil.getYear(statTime));
        monitorStatDTO.setMonth(DateUtil.getMonth(statTime));
        monitorStatDTO.setDay(DateUtil.getDay(statTime));
        monitorStatDTO.setStatTime(statTime);
        monitorStatDTO.setCount(monitorStatDTO.getCount() + 1);
        LOG.debug("不是当天的key:" + key + "被放入memcache,monitorStatDTO:" + monitorStatDTO.toString());

        MemCacheAdapter.set(key, monitorStatDTO, new Date(System.currentTimeMillis() + DateUtil.DAY_MILLION_SECONDS));

        LOG.debug("不是当天的,memcache开始获得key");
        UrlMonitorStatDTO result = (UrlMonitorStatDTO) MemCacheAdapter.get(key);
        if (result == null) {
          LOG.debug("不是当天的,memcache拿不到这个key:" + key);
        } else {
          LOG.debug("不是当天的,memcache拿到了这个key:" + key + ",result:" + result.toString());
        }

      }
    }
  }

  public String getKeyByShopIdUserNoUrl(Long shopId, String userNo, String url) {
    return MemcachePrefix.systemUrlMonitorStat.getValue() + url + userNo + shopId;
  }

  public void saveOrUpdateUrlMonitorStat(List<UrlMonitorStatDTO> urlMonitorStatDTOList) {
    if (CollectionUtils.isEmpty(urlMonitorStatDTOList)) {
      return;
    }
    UserWriter userWriter = userDaoManager.getWriter();
    Object status = userWriter.begin();
    try {

      for (UrlMonitorStatDTO urlMonitorStatDTO : urlMonitorStatDTOList) {
        if (urlMonitorStatDTO.getId() == null) {
          UrlMonitorStat urlMonitorStat = new UrlMonitorStat();
          urlMonitorStat.fromDTO(urlMonitorStatDTO);
          userWriter.save(urlMonitorStat);
        } else {
          UrlMonitorStat urlMonitorStat = userWriter.getById(UrlMonitorStat.class, urlMonitorStatDTO.getId());
          urlMonitorStat.fromDTO(urlMonitorStatDTO);
          userWriter.update(urlMonitorStat);
        }
      }
      userWriter.commit(status);
    } finally {
      userWriter.rollback(status);
    }
  }

  /**
   * 保存请求监控数据
   *
   * @param begin
   * @param end
   * @param url
   */
  public void saveOrUpdateRequestMonitor(Long lastRequestTime, Long begin, long end, String url, String configValue) {
    if (!DateUtil.isSameDay(lastRequestTime, begin)) {
      Hashtable<String, RequestPerformanceMonitorDTO> monitorDTOHashTable = RequestPerformanceCache.getRequestPerformanceMonitorDTOHashTable();
      RequestPerformanceCache.setRequestPerformanceMonitorDTOHashTable(new Hashtable<String, RequestPerformanceMonitorDTO>());

      RequestMonitorThread requestMonitorThread = new RequestMonitorThread(monitorDTOHashTable.values(), null, false);
      requestMonitorThread.run();
    }


    RequestPerformanceMonitorDTO requestPerformanceMonitorDTO = new RequestPerformanceMonitorDTO();

    requestPerformanceMonitorDTO.setStartTime(begin);
    requestPerformanceMonitorDTO.setEndTime(end);
    try {
      requestPerformanceMonitorDTO.setRequestSection(configValue);
    } catch (Exception e) {
      LOG.error("RequestPerformanceFilter.doFilter");
      LOG.error(e.getMessage(), e);
      requestPerformanceMonitorDTO.setStatUrl(false);
    }

    if (requestPerformanceMonitorDTO.isStatUrl()) {
      requestPerformanceMonitorDTO.setNode(System.getProperty("node"));
      requestPerformanceMonitorDTO.setYear(DateUtil.getYear(end));
      requestPerformanceMonitorDTO.setMonth(DateUtil.getMonth(end));
      requestPerformanceMonitorDTO.setDay(DateUtil.getDay(end));
      requestPerformanceMonitorDTO.setUrl(url);
      RequestPerformanceCache.saveOrUpdateRequestMonitor(requestPerformanceMonitorDTO);
    }
  }


  public void saveUrlMonitorStatFromMemCache() {

    IRequestMonitorService requestMonitorService = ServiceManager.getService(IRequestMonitorService.class);
    List<UrlMonitorConfig> urlMonitorConfigs = requestMonitorService.getUrlMonitorConfig(null);
    LOG.debug("系统开始获得urlMoitorConfig;");
    if (CollectionUtils.isEmpty(urlMonitorConfigs)) {
      LOG.debug("系统urlMoitorConfig为空，返回");

      return;
    }

    LOG.debug("系统urlMoitorConfig:" + JsonUtil.listToJson(urlMonitorConfigs));

    List<UrlMonitorConfig> urlMonitorConfigList = new ArrayList<UrlMonitorConfig>();
    for (UrlMonitorConfig urlMonitorConfig : urlMonitorConfigs) {
      if (urlMonitorConfig.getUrlMonitorTypeEnum() == UrlMonitorTypeEnum.INCLUDE) {
        LOG.debug("url被放入list中:" + urlMonitorConfig.getUrl());
        urlMonitorConfigList.add(urlMonitorConfig);
      }
    }

    if (CollectionUtils.isEmpty(urlMonitorConfigList)) {
      return;
    }
    LOG.debug("系统urlMonitorConfigLists:" + JsonUtil.listToJson(urlMonitorConfigList));

    IConfigService configService = ServiceManager.getService(IConfigService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    List<Shop> shopList = configService.getShop();
    if (CollectionUtils.isEmpty(shopList)) {
      return;
    }
    Long shopId = null;
    List<UserDTO> userDTOList = null;
    for (Shop shop : shopList) {
      List<UrlMonitorStatDTO> urlMonitorStatDTOList = new ArrayList<UrlMonitorStatDTO>();
      shopId = shop.getId();
      LOG.debug("店铺:" + shopId + "开始统计监控数据");
      userDTOList = userService.getAllUserByShopId(shopId);
      if (CollectionUtil.isEmpty(userDTOList)) {
        continue;
      }
      LOG.debug("店铺:" + shopId + "用户列表" + JsonUtil.listToJson(userDTOList));
      for (UserDTO userDTO : userDTOList) {
        for (UrlMonitorConfig urlMonitorConfig : urlMonitorConfigList) {
          LOG.debug("店铺:" + shopId + ",userNo:" + userDTO.getUserNo() + ",url:" + urlMonitorConfig.getUrl() + "获得key");
          String key = getKeyByShopIdUserNoUrl(shopId, userDTO.getUserNo(), urlMonitorConfig.getUrl());
          LOG.debug("统计开始获取系统监控key:" + key);
          UrlMonitorStatDTO urlMonitorStatDTO = (UrlMonitorStatDTO) MemCacheAdapter.get(key);


          if (urlMonitorStatDTO == null) {
            LOG.debug("统计key:" + key + "没有拿到统计");
            continue;
          }
          LOG.debug("统计获得key:" + key);
          MemCacheAdapter.delete(key);
          LOG.debug("统计保存监控数据:" + urlMonitorStatDTO.toString());
          urlMonitorStatDTOList.add(urlMonitorStatDTO);
        }
      }

      LOG.debug("统计保存店铺监控数据:" + JsonUtil.listToJson(urlMonitorStatDTOList));
      this.saveOrUpdateUrlMonitorStat(urlMonitorStatDTOList);
    }

  }


  /**
   * 保存或者更新urlMonitorConfig
   *
   * @param urlMonitorConfig
   */
  public void saveOrUpdateUrlMonitorConfig(UrlMonitorConfig urlMonitorConfig) {

    UserWriter userWriter = userDaoManager.getWriter();

    List<UrlMonitorConfig> urlMonitorConfigList = this.getUrlMonitorConfig(urlMonitorConfig.getUrl());


    Object status = userWriter.begin();
    try {

      if (CollectionUtils.isNotEmpty(urlMonitorConfigList)) {
        UrlMonitorConfig monitorConfig = urlMonitorConfigList.get(0);
        monitorConfig.setUrlMonitorTypeEnum(urlMonitorConfig.getUrlMonitorTypeEnum());
        userWriter.update(monitorConfig);
      } else {
        userWriter.save(urlMonitorConfig);
      }
      userWriter.commit(status);
    } finally {
      userWriter.rollback(status);
    }
  }

}
