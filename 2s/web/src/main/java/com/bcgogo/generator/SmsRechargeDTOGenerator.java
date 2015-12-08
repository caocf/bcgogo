package com.bcgogo.generator;

import com.bcgogo.config.dto.ShopBalanceDTO;
import com.bcgogo.constant.SmsRechargeConstants;
import com.bcgogo.enums.txn.finance.BcgogoReceivableStatus;
import com.bcgogo.txn.dto.SmsRechargeDTO;
import com.bcgogo.utils.NumberUtil;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;

/**
 * 短信充值时，生成充值记录
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-3-6
 * Time: 上午10:51
 * To change this template use File | Settings | File Templates.
 */

@Component
public class SmsRechargeDTOGenerator {

    public SmsRechargeDTO generate(HttpServletRequest request, ShopBalanceDTO shopBalanceDTO){
        SmsRechargeDTO smsRechargeDTO = new SmsRechargeDTO();
        smsRechargeDTO.setRechargeAmount(NumberUtil.doubleValue(request.getParameter("rechargeamount"), 0));
        smsRechargeDTO.setShopId((Long) request.getSession().getAttribute("shopId"));
        smsRechargeDTO.setRechargeTime(Calendar.getInstance().getTimeInMillis());
        smsRechargeDTO.setUserId((Long) request.getSession().getAttribute("userId"));
        smsRechargeDTO.setState(SmsRechargeConstants.RechargeState.RECHARGE_STATE_INIT);
        if(shopBalanceDTO != null){
            smsRechargeDTO.setSmsBalance(shopBalanceDTO.getSmsBalance());
        }
        smsRechargeDTO.setStatus(BcgogoReceivableStatus.TO_BE_PAID);
        smsRechargeDTO.setPresentAmount(NumberUtil.doubleValue(request.getParameter("presentAmount"), 0));
        return smsRechargeDTO;
    }

}
