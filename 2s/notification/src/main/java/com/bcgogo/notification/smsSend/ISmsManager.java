package com.bcgogo.notification.smsSend;

import com.bcgogo.notification.dto.SmsSendDTO;
import com.bcgogo.notification.dto.SmsSendResult;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-3-30
 * Time: 上午9:09
 * To change this template use File | Settings | File Templates.
 */
public interface ISmsManager {
    public SmsSendResult sendSms(SmsSendDTO smsSendDTO) throws SmsException;

    public SmsSendResult sendSms(SmsSendDTO smsSendDTO, String name) throws SmsException;

}
