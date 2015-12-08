package com.bcgogo.payment.service;

import com.bcgogo.payment.PaymentException;
import com.bcgogo.payment.dto.ChinapayDTO;
import com.bcgogo.payment.dto.TransactionDTO;

public interface IChinapayService {
  /**
   * 银联交易方法
   *
   * @param referenceId 引用ID(短信充值单方支付时表示：充值单ID)
   * @param amount      支付金额，单位：分  不超过12位长度
   * @param payerId     支付人ID(短信充值单支付时表示：店面ID)
   * @param desc        订单描述 长度不超过256位
   * @param bgRetUrl    后台controller 如("loanTransfers.do?method=saveLoanComplete")
   * @param pageRetUrl  前台controller
   * @return 成功返回ChinapayDTO 失败返回null
   */
  public ChinapayDTO pay(Long referenceId, long amount, long payerId, String desc, String bgRetUrl, String pageRetUrl);

  /**
   * 后台交易接收
   *
   * @param chinapayDTO 前端通过fromForm方法，可将request中的信息封装成ChinapayDTO对象
   * @return 成功返回TransactionDTO 失败返回null
   */
  public TransactionDTO receive(ChinapayDTO chinapayDTO);

  /**
   * 后台交易接收
   *
   * @param chinapayDTO 前端通过fromForm方法，可将request中的信息封装成ChinapayDTO对象
   * @return 成功返回TransactionDTO 失败返回null
   */
  public ChinapayDTO pgReceive(ChinapayDTO chinapayDTO);

  /**
   * 主动chack银联
   * ResponseCode=0，返回成功信息 (PayStat： 1111未支付，1001支付成功，其余失败，（4位数字）); ResponseCode=其他，返回message错误信息
   *
   * @param referenceId 单据号
   * @param amount      金额
   * @param payerId     shopId
   * @param desc        描述
   * @return 成功返回TransactionDTO对象，失败返回null
   * @throws PaymentException 异常向外抛
   */
  ChinapayDTO bgReceiveCheck(Long referenceId, long amount, long payerId, String desc) throws PaymentException;
}
