package com.bcgogo.txn.service.payment;

import com.bcgogo.enums.payment.ChinaPayScene;
import com.bcgogo.payment.PaymentException;
import com.bcgogo.payment.chinapay.ChinaPay;
import com.bcgogo.payment.dto.ChinapayDTO;
import com.bcgogo.payment.service.IChinapayService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.LoanTransfersDTO;
import com.bcgogo.txn.dto.SmsRechargeDTO;
import com.bcgogo.txn.service.ISmsRechargeService;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.utils.NumberUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-6-8
 * Time: 下午7:01
 * check china pay
 */
@Component
public class ChinapayCheckService implements IChinapayCheckService {
  private static final Logger LOG = LoggerFactory.getLogger(ChinapayCheckService.class);
  private static final Long timeout = 300000L; //5分钟
  ILoanTransfersService loanTransfersService;
  ITxnService txnService;
  ISmsRechargeService smsRechargeService;
  IChinapayService chinapayService;

  public ILoanTransfersService getLoanTransfersService() {
    if (loanTransfersService == null) {
      loanTransfersService = ServiceManager.getService(ILoanTransfersService.class);
    }
    return loanTransfersService;
  }

  public ITxnService getTxnService() {
    if (txnService == null) {
      txnService = ServiceManager.getService(ITxnService.class);
    }
    return txnService;
  }

  public ISmsRechargeService getSmsRechargeService() {
    if (smsRechargeService == null) {
      smsRechargeService = ServiceManager.getService(ISmsRechargeService.class);
    }
    return smsRechargeService;
  }

  public IChinapayService getChinapayService() {
    if (chinapayService == null) {
      chinapayService = ServiceManager.getService(IChinapayService.class);
    }
    return chinapayService;
  }

  @Override
  public void checkChinaPayByShopIdAndTime(Long shopId, long dateTime) {
    int pageSize = 100;
    this.checkLoanTransfers(shopId, pageSize, dateTime);
    this.checkSmsRecharge(shopId, pageSize, dateTime);
  }

  @Override
  public void checkChinaPayByReferenceId(long id, ChinaPayScene scene) {
    try {
      if (scene == null) throw new Exception("ChinaPayScene is null");
      switch (scene) {
        case LONA_TRANSFERS:
          checkLoanTransfersImplementor(id);
          break;
        case SMS_RECHARGE:
          checkSmsRechargeImplementor(id);
          break;
        default:
          LOG.error("[ChinaPayScene:{}] is not found!", scene);
      }
    } catch (Exception e) {
      LOG.error("method=checkChinaPayByReferenceId , check 失败！", e);
    }
  }

  //短信充值
  private void checkSmsRecharge(Long shopId, int pageSize, Long loanTransferTime) {
    int start = 0;
    long startTime = System.currentTimeMillis();
    long endTime = System.currentTimeMillis();
    while (true) {
      List<Long> ids = getTxnService().getSmsRechargesByStatus(shopId, start, pageSize, loanTransferTime);
      if (CollectionUtils.isEmpty(ids)) break;
      if (endTime - startTime > timeout) {
        LOG.error("china pay check sms recharge time out!");
      }
      checkSmsRechargeImplementor(ids.toArray(new Long[ids.size()]));
      endTime = System.currentTimeMillis();
    }
  }

  private void checkSmsRechargeImplementor(Long... ids) {
    List<SmsRechargeDTO> smsRechargeDTOList = getTxnService().getSmsRechargesByIds(ids);
    ChinapayDTO chinapayDTO;
    for (SmsRechargeDTO srDTO : smsRechargeDTOList) {
      LOG.info("ChinaPay before query data<SmsRechargeDTO> details " + srDTO.toString());
      try {
        chinapayDTO = getChinapayService().bgReceiveCheck(srDTO.getId(), NumberUtil.yuanToFen(srDTO.getRechargeAmount()), srDTO.getShopId(), ChinaPayScene.SMS_RECHARGE.getValue());
        //更新SmsRecharge表
        if (chinapayDTO != null && chinapayDTO.getPayStat().equals(ChinaPay.PAY_STAT_SUCCESS)) {
          LOG.warn("ChinapayQuery SUCCESS! RechargeId is" + srDTO.getId());
          getSmsRechargeService().completeSmsRecharge(srDTO.getId());
        }else{
          getSmsRechargeService().failSmsRecharge(srDTO.getId());
          LOG.warn("ChinapayQuery fail! RechargeId is" + srDTO.getId());
        }
      } catch (Exception e) {
        LOG.error(e.getMessage(), e);
      }
      LOG.info("ChinaPay after query data<SmsRechargeDTO> details " + srDTO.toString());
    }
  }


  //货款转账
  private void checkLoanTransfers(Long shopId, int pageSize, Long loanTransferTime) {
    int start = 0;
    long startTime = System.currentTimeMillis();
    long endTime = System.currentTimeMillis();
    while (true) {
      List<Long> ids = getLoanTransfersService().getLoanTransfersIdsByStatus(shopId, start, pageSize, loanTransferTime);
      if (CollectionUtils.isEmpty(ids)) break;
      if (endTime - startTime > timeout) {
        LOG.error("china pay check loan transfers time out!");
      }
      checkLoanTransfersImplementor(ids.toArray(new Long[ids.size()]));
      endTime=System.currentTimeMillis();
    }
  }

  private void checkLoanTransfersImplementor(Long... ids) {
    IChinapayService chinapayService = ServiceManager.getService(IChinapayService.class);
    List<LoanTransfersDTO> loanTransfersDTOList = getLoanTransfersService().getLoanTransfersIdsById(ids);
    ChinapayDTO chinapayDTO = null;
    for (LoanTransfersDTO dto : loanTransfersDTOList) {
      LOG.info("ChinaPay before query data details [LoanTransfersDTO:{}].", dto.toString());
      try {
        chinapayDTO = chinapayService.bgReceiveCheck(dto.getId(), NumberUtil.yuanToFen(dto.getAmount()), dto.getShopId(), ChinaPayScene.LONA_TRANSFERS.getValue());
        String payStat="";
        if (chinapayDTO != null) {
          payStat = chinapayDTO.getPayStat();
        }
        getLoanTransfersService().handleLoanTransfersByTransfersNumber(dto.getTransfersNumber(), payStat);
      } catch (PaymentException e) {
        LOG.error(e.getMessage(), e);
      }
      LOG.info("ChinaPay after query data details [LoanTransfersDTO:{}].", dto.toString());
    }
  }

  //  /**
//   * 根据时间间隔查找 银联未 给出返回的状态的 短信充值数据 (RechargeState.RECHARGE_STATE_COMMIT)
//   *
//   * @param dateTime 时间间隔
//   */
//  private void checkSmsRechargeByTimePeriod(Long dateTime) {
//    //得到需要查找的记录PaymentServiceJob
//    if (dateTime == null) return;
//    ITxnService txnService = ServiceManager.getService(ITxnService.class);
//    ISmsRechargeService smsRechargeService = ServiceManager.getService(ISmsRechargeService.class);
//    IChinapayService chinapayService = ServiceManager.getService(IChinapayService.class);
//
//    RechargeSearchDTO rechargeSearchDTO = new RechargeSearchDTO();
//    rechargeSearchDTO.setSmsRechargeState(SmsRechargeConstants.RechargeState.RECHARGE_STATE_COMMIT);
//    rechargeSearchDTO.setTimePeriod(dateTime);
//
//    int totalRows = txnService.countSmsRechargesByConditions(rechargeSearchDTO);
//    Pager pager = null;
//    try {
//      pager = new Pager(totalRows, 1, 100);
//    } catch (PageException e) {
//      LOG.error(e.getMessage(), e);
//    }
//    rechargeSearchDTO.setPager(pager);
//    List<SmsRechargeDTO> smsRechargeDTOList = txnService.getSmsRechargesByConditions(rechargeSearchDTO);
//    //check chinaPay
//    ChinapayDTO chinapayDTO = null;
//    while (CollectionUtils.isNotEmpty(smsRechargeDTOList)) {
//      for (SmsRechargeDTO srDTO : smsRechargeDTOList) {
//        LOG.info("ChinaPay before query data<SmsRechargeDTO> details " + srDTO.toString());
//        try {
//          chinapayDTO = chinapayService.bgReceiveCheck(srDTO.getId(), NumberUtil.yuanToFen(srDTO.getRechargeAmount()), srDTO.getShopId(), ChinaPayScene.SMS_RECHARGE.getValue());
//          //更新SmsRecharge表
//          if (chinapayDTO != null && chinapayDTO.getPayStat().equals(ChinaPay.PAY_STAT_SUCCESS)) {
//            LOG.warn("ChinapayQuery SUCCESS! RechargeId is" + srDTO.getId());
//            smsRechargeService.completeSmsRecharge(srDTO.getId());
//          }
//        } catch (Exception e) {
//          LOG.error(e.getMessage(), e);
//        }
//        LOG.info("ChinaPay after query data<SmsRechargeDTO> details " + srDTO.toString());
//      }
//      if (!pager.hasNextPage()) {
//        break;
//      }
//      pager.gotoNextPage();
//      smsRechargeDTOList = txnService.getSmsRechargesByConditions(rechargeSearchDTO);
//    }
//  }

}
