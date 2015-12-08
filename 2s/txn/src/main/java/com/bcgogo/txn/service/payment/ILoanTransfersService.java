package com.bcgogo.txn.service.payment;

import com.bcgogo.common.Pager;
import com.bcgogo.txn.dto.LoanTransfersDTO;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-10-23
 * Time: 上午11:20
 * 货款转账 service
 */
public interface ILoanTransfersService {
  //创建一条货款转账
  LoanTransfersDTO createLoanTransfers(LoanTransfersDTO loanTransfersDTO);

  //更新货款转账信息
  LoanTransfersDTO updateLoanTransfers(LoanTransfersDTO loanTransfersDTO);

  //根据货款转账号 处理该账单
  LoanTransfersDTO handleLoanTransfersByTransfersNumber(String transfersNumber, String payStatus);

  /**
   * 根据shopId 获得 LoanTransfersDTO List，根据更新时间倒序
   *
   *
   * @param shopId 店面id
   * @param pager
   * @return List<LoanTransfersDTO>
   */
  List<LoanTransfersDTO> getLoanTransfersByShopId(Long shopId, Pager pager);

  //count loan transfers
  int countLoanTransfersByShopId(Long shopId);

  //sum total amount
  Double sumLoanTransfersTotalAmountByShopId(Long shopId);

  //根据page大小 查找出所有 LoanTransfersStatus为 LOAN_IN("转账中")
  List<Long> getLoanTransfersIdsByStatus(Long shopId, int start, int pageSize, Long loanTransferTime);

  //根据 id 获得  LoanTransfersDTO
  List<LoanTransfersDTO> getLoanTransfersIdsById(Long... ids);
}
