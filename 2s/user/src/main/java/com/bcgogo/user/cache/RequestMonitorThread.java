package com.bcgogo.user.cache;

import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.RequestPerformanceMonitorDTO;
import com.bcgogo.user.dto.UrlMonitorStatDTO;
import com.bcgogo.user.service.IRequestMonitorService;

import java.util.Collection;
import java.util.List;

/**
 * 启动新线程保存监控数据
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-2-28
 * Time: 下午3:39
 * To change this template use File | Settings | File Templates.
 */
public class RequestMonitorThread implements Runnable {
  private Collection<RequestPerformanceMonitorDTO> requestPerformanceMonitorDTOCollection;
  private List<UrlMonitorStatDTO> urlMonitorStatDTOList;
  private boolean saveUrlMonthStatFromMemCache;

  public boolean isSaveUrlMonthStatFromMemCache() {
    return saveUrlMonthStatFromMemCache;
  }

  public void setSaveUrlMonthStatFromMemCache(boolean saveUrlMonthStatFromMemCache) {
    this.saveUrlMonthStatFromMemCache = saveUrlMonthStatFromMemCache;
  }

  public Collection<RequestPerformanceMonitorDTO> getRequestPerformanceMonitorDTOCollection() {
    return requestPerformanceMonitorDTOCollection;
  }

  public void setRequestPerformanceMonitorDTOCollection(Collection<RequestPerformanceMonitorDTO> requestPerformanceMonitorDTOCollection) {
    this.requestPerformanceMonitorDTOCollection = requestPerformanceMonitorDTOCollection;
  }

  public List<UrlMonitorStatDTO> getUrlMonitorStatDTOList() {
    return urlMonitorStatDTOList;
  }

  public void setUrlMonitorStatDTOList(List<UrlMonitorStatDTO> urlMonitorStatDTOList) {
    this.urlMonitorStatDTOList = urlMonitorStatDTOList;
  }

  public RequestMonitorThread(Collection<RequestPerformanceMonitorDTO> collection,List<UrlMonitorStatDTO> list,boolean isSaveUrlMonthStatFromMemCache) {
    this.requestPerformanceMonitorDTOCollection = collection;
    this.urlMonitorStatDTOList = list;
    this.saveUrlMonthStatFromMemCache = isSaveUrlMonthStatFromMemCache;
  }

  @Override
  public void run() {
    IRequestMonitorService requestMonitorService = ServiceManager.getService(IRequestMonitorService.class);
    requestMonitorService.saveOrUpdateRequestMonitorDTO(this.getRequestPerformanceMonitorDTOCollection());
    requestMonitorService.saveOrUpdateUrlMonitorStat(this.getUrlMonitorStatDTOList());
    if(isSaveUrlMonthStatFromMemCache()){
      requestMonitorService.saveUrlMonitorStatFromMemCache();
    }
  }
}
