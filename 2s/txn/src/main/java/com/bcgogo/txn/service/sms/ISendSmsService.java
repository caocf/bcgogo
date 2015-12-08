package com.bcgogo.txn.service.sms;

import com.bcgogo.common.Result;
import com.bcgogo.enums.notification.SmsChannel;
import com.bcgogo.notification.dto.SmsDTO;
import com.bcgogo.notification.dto.SmsIndexDTO;
import com.bcgogo.user.dto.ContactDTO;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 14-2-11
 * Time: 下午5:10
 * To change this template use File | Settings | File Templates.
 */
public interface ISendSmsService {

  public void generateSmsContactBySpecialId(Long shopId,SmsDTO smsDTO);

  public SmsIndexDTO prepareForSaveSmsIndex(Long shopId,Long smsId);

  public void prepareForSaveSms(Long shopId,SmsDTO smsDTO) throws Exception;

     /**
   *
   * shopId userId(必须)
   * appFlag smsFlag至少设置一种发送方式(必须)
   * @param result
   * @param smsDTO
   * @return
   * @throws Exception
   */
  public Result sendSms(Result result,SmsDTO smsDTO) throws Exception;

  /**
   *
   * @param shopId(必须)
   * @param userId(必须)
   * @param content(必须)
   * @param appFlag 发app消息flag
   * @param smsFlag  发短信flag
   * @param templateFlag  发短信通道
   * @param contactDTOs contactId和mobile至少有一个，contactId没有时根据mobile生成自定义联系人
   * @return
   * @throws Exception
   */
  public Result sendSms(Long shopId,Long userId,String content,Boolean appFlag,Boolean smsFlag,Boolean templateFlag,ContactDTO... contactDTOs) throws Exception;

  Result sendSms(Long shopId, Long userId, String content, Boolean appFlag, Boolean smsFlag, Boolean templateFlag, String appUserNo, String mobile) throws Exception;
}
