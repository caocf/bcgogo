package com.bcgogo.api.response;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.config.dto.AppUpdateAnnounceDTO;

/**
 * User: ZhangJuntao
 * Date: 13-8-19
 * Time: 下午2:35
 */
public class ApiUpgradeTestingResponse extends ApiResponse {
  public static final String FORCE = "force";     //强制
  public static final String ALERT = "alert";    //提醒
  public static final String NORMAL = "normal";  //正常

  private String url;
  private String action;
  private String description;


  public ApiUpgradeTestingResponse() {
    super();
  }

  public ApiUpgradeTestingResponse(ApiResponse response) {
    super(response);
  }

  public void setAppUpdateAnnounceDTO(AppUpdateAnnounceDTO appUpdateAnnounceDTO) {
    if (appUpdateAnnounceDTO != null) {
         switch (appUpdateAnnounceDTO.getAppUpdateType()){
           case FORCE:
             setForceAction();
             break;
           case ALERT:
             setAlertAction();
             break;
           case NORMAL:
             setNormalAction();
             break;
           default:
             setAlertAction();
             break;
         }
        setDescription(appUpdateAnnounceDTO.getDescription());
    }
  }

  public void setForceAction() {
    this.setAction(FORCE);
  }

  public void setNormalAction() {
    this.setAction(NORMAL);
  }

  public void setAlertAction() {
    this.setAction(ALERT);
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getAction() {
    return action;
  }

  private void setAction(String action) {
    this.action = action;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }



}
