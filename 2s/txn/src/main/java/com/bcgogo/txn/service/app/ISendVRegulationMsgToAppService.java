package com.bcgogo.txn.service.app;

/**
 * User: lw
 * Date: 14-4-29
 * Time: 下午3:24
 */
public interface ISendVRegulationMsgToAppService {

  public void sendVRegulationMsgToApp()  throws Exception;

  public void sendVRegulationMsgToYiFaWXUser()  throws Exception;

}
