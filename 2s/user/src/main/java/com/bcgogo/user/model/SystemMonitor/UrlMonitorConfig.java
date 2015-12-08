package com.bcgogo.user.model.SystemMonitor;

import com.bcgogo.enums.user.UrlMonitorTypeEnum;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.*;

/**
 * 系统监控url配置表
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-2-20
 * Time: 下午2:40
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "url_monitor_config")
public class UrlMonitorConfig extends LongIdentifier {
  private UrlMonitorTypeEnum urlMonitorTypeEnum;//系统url监控类型
  private String url; //需要配置的url

  public UrlMonitorConfig() {

  }

  @Column(name = "url_monitor_type_enum")
  @Enumerated(EnumType.STRING)
  public UrlMonitorTypeEnum getUrlMonitorTypeEnum() {
    return urlMonitorTypeEnum;
  }

  public void setUrlMonitorTypeEnum(UrlMonitorTypeEnum urlMonitorTypeEnum) {
    this.urlMonitorTypeEnum = urlMonitorTypeEnum;
  }

  @Column(name = "url")
  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

}
