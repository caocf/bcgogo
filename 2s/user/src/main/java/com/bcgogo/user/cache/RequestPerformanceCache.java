package com.bcgogo.user.cache;

import com.bcgogo.enums.user.UrlMonitorTypeEnum;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.RequestPerformanceMonitorDTO;
import com.bcgogo.user.model.SystemMonitor.UrlMonitorConfig;
import com.bcgogo.user.service.IRequestMonitorService;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Hashtable;
import java.util.List;

/**
 * 请求性能监控本地缓存
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 13-2-6
 * Time: 下午3:35
 * To change this template use File | Settings | File Templates.
 */
public class RequestPerformanceCache {
  private static final Logger LOG = LoggerFactory.getLogger(RequestPerformanceCache.class);

  //系统性能监控缓存
  private static Hashtable<String, RequestPerformanceMonitorDTO> requestPerformanceMonitorDTOHashTable = new Hashtable<String, RequestPerformanceMonitorDTO>();

  //默认tomcat
  private static final String DEFAULT_TOMCAT_CONFIG = "default_node";

  //url配置表
  private static Hashtable<String, UrlMonitorConfig> urlMonitorConfigHashTable = new Hashtable<String, UrlMonitorConfig>();

  //是否第一次访问
  private static  boolean FIRST_REQUEST = true;

  /**
   * 保存或更新系统性能监控数据
   * @param requestPerformanceMonitorDTO
   */
  public static void saveOrUpdateRequestMonitor(RequestPerformanceMonitorDTO requestPerformanceMonitorDTO) {

    if (StringUtil.isEmpty(requestPerformanceMonitorDTO.getNode())) {
      requestPerformanceMonitorDTO.setNode(DEFAULT_TOMCAT_CONFIG);
    }
    String key = requestPerformanceMonitorDTO.getUrl() + requestPerformanceMonitorDTO.getNode() + requestPerformanceMonitorDTO.getYear() + requestPerformanceMonitorDTO.getMonth() + requestPerformanceMonitorDTO.getDay();
    if (requestPerformanceMonitorDTOHashTable.containsKey(key)) {
      RequestPerformanceMonitorDTO keyValue = requestPerformanceMonitorDTOHashTable.get(key);
      keyValue = keyValue.calculate(requestPerformanceMonitorDTO);
      requestPerformanceMonitorDTOHashTable.remove(key);
      requestPerformanceMonitorDTOHashTable.put(key, keyValue);
    } else {
      requestPerformanceMonitorDTOHashTable.put(key, requestPerformanceMonitorDTO);
    }
  }

  /**
   * 保存性能监控数据到db并清空缓存
   */
  public static void saveAndClearHashTable() {

    try {
      if (CollectionUtils.isNotEmpty(requestPerformanceMonitorDTOHashTable.values())) {
        IRequestMonitorService requestMonitorService = ServiceManager.getService(IRequestMonitorService.class);
        requestMonitorService.saveOrUpdateRequestMonitorDTO(requestPerformanceMonitorDTOHashTable.values());
      }
    } catch (Exception e) {
      LOG.error("RequestPerformanceCache.saveAndClearHashTable,time:" + DateUtil.convertDateLongToString(System.currentTimeMillis(), DateUtil.DATE_STRING_FORMAT_ALL));
      LOG.error(e.getMessage(), e);
    } finally {
      requestPerformanceMonitorDTOHashTable.clear();
      requestPerformanceMonitorDTOHashTable = new Hashtable<String, RequestPerformanceMonitorDTO>();
    }
  }

  /**
   * 重置url监控配置
   */
  public static void resetUrlMonitorConfigHashTable() {

    IRequestMonitorService requestMonitorService = ServiceManager.getService(IRequestMonitorService.class);
    List<UrlMonitorConfig> urlMonitorConfigList = requestMonitorService.getUrlMonitorConfig(null);

    if (CollectionUtils.isEmpty(urlMonitorConfigList)) {
      LOG.error("RequestPerformanceCache.resetUrlMonitorConfigHashTable,url配置列表为空,time:" + DateUtil.convertDateLongToString(System.currentTimeMillis(), DateUtil.DATE_STRING_FORMAT_ALL));
      return;
    }
    urlMonitorConfigHashTable.clear();
    urlMonitorConfigHashTable = new Hashtable<String, UrlMonitorConfig>();
    for (UrlMonitorConfig urlMonitorConfig : urlMonitorConfigList) {
      urlMonitorConfigHashTable.put(urlMonitorConfig.getUrl(), urlMonitorConfig);
    }
  }

  public static boolean isIncludeUrl(String url) {

    if (FIRST_REQUEST) {
      RequestPerformanceCache.resetUrlMonitorConfigHashTable();
      FIRST_REQUEST = false;
    }

    if (urlMonitorConfigHashTable.containsKey(url)) {
      UrlMonitorConfig urlMonitorConfig = urlMonitorConfigHashTable.get(url);
      return urlMonitorConfig.getUrlMonitorTypeEnum() == UrlMonitorTypeEnum.INCLUDE;
    }
    return false;
  }
  public static Hashtable<String, UrlMonitorConfig> getUrlMonitorConfigHashTable() {
    return urlMonitorConfigHashTable;
  }

  public static Hashtable<String, RequestPerformanceMonitorDTO> getRequestPerformanceMonitorDTOHashTable() {
    return requestPerformanceMonitorDTOHashTable;
  }

  public static void setRequestPerformanceMonitorDTOHashTable(Hashtable<String, RequestPerformanceMonitorDTO> requestPerformanceMonitorDTOHashTable) {
    RequestPerformanceCache.requestPerformanceMonitorDTOHashTable = requestPerformanceMonitorDTOHashTable;
  }
}

