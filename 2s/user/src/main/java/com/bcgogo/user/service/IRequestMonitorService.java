package com.bcgogo.user.service;

import com.bcgogo.user.dto.*;
import com.bcgogo.user.model.SystemMonitor.UrlMonitorConfig;

import java.text.ParseException;
import java.util.Collection;
import java.util.List;

/**
 * 系统性能、功能监控专用接口
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 13-2-6
 * Time: 下午2:59
 * To change this template use File | Settings | File Templates.
 */
public interface IRequestMonitorService {

  /**
   * 批量保存监控数据
   *
   * @param requestMonitorDTOCollection
   */
  public void saveOrUpdateRequestMonitorDTO(Collection<RequestPerformanceMonitorDTO> requestMonitorDTOCollection);


  /**
   * 保存客户端信息
   * @param sessionId
   * @param ip
   * @param browser
   * @param finger
   * @param userDTO
   */
  public void saveOrUpdateUserMonitorInfo(String sessionId,String ip,String browser, String finger,UserDTO userDTO);

  /**
   * 保存用户登出时信息
   * @param sessionId
   * @param userName
   */
  public void saveOrUpdateUserLogOutInfo(String ip,String sessionId, String userName,String finger);

  List<UserLoginLogDTO> getUserClientLoginLogDTO(Long startDateTime,Long endDateTime);

  UserClientInfoDTO getUserClientInfoByUserNo(Long shopId, String userNo,String finger);

  void saveOrUpdateUserClientInfoDTO(UserClientInfoDTO clientInfoDTO);

  void calcDeviceFingerScore() throws ParseException;

  /**
   * 获取监控配置url
   * @return
   */
  public List<UrlMonitorConfig> getUrlMonitorConfig(String url);

  /**
   * 从请求中保存监控数据
   * @param shopId
   * @param userNo
   * @param url
   */
  public void saveUrlMonitorStatFromRequest(Long shopId,String userNo, String url);


  /**
   * 保存请求监控数据
   * @param begin
   * @param end
   * @param url
   */
  public void saveOrUpdateRequestMonitor(Long lastRequestTime,Long begin, long end, String url, String configValue);

  /**
   * 从MemCache中获得系统功能使用监控数据保存到db中
   */
  public void saveUrlMonitorStatFromMemCache();

  /**
   * 保存或者更新urlMonitorConfig
   * @param urlMonitorConfig
   */
  public void saveOrUpdateUrlMonitorConfig(UrlMonitorConfig urlMonitorConfig);

  /**
   * 批量保存监控数据
   * @param urlMonitorStatDTOList
   */
  public void saveOrUpdateUrlMonitorStat(List<UrlMonitorStatDTO> urlMonitorStatDTOList);

}
