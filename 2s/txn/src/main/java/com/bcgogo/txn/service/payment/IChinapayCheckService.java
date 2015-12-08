package com.bcgogo.txn.service.payment;

import com.bcgogo.enums.payment.ChinaPayScene;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-6-8
 * Time: 下午7:16
 * check china pay
 */
public interface IChinapayCheckService {
  /**
   * 根据 具体场景 单个 ReferenceId check china pay
   * @param id    ReferenceId 如(SmsRecharge id or LoanTransfers id)
   * @param scene 支付场景
   */
  void checkChinaPayByReferenceId(long id, ChinaPayScene scene);

  /**
   * 根据 time check china pay 所有场景
   * @param shopId shop id can be null
   * @param dateTime scope[dateTime-now]
   */
  void checkChinaPayByShopIdAndTime(Long shopId, long dateTime);

}
