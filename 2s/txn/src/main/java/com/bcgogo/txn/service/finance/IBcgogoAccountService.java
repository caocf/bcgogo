package com.bcgogo.txn.service.finance;

import com.bcgogo.common.Result;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.txn.dto.finance.*;
import com.bcgogo.txn.model.TxnWriter;
import com.bcgogo.txn.model.finance.HardwareSoftwareAccount;

import java.util.Map;

/**
 * User: ZhangJuntao
 * Date: 13-3-19
 * Time: 下午5:29
 */
public interface IBcgogoAccountService {
  /**
   * 查询
   *
   * @param condition BcgogoReceivableSearchCondition
   * @throws com.bcgogo.exception.BcgogoException
   *
   */
  Result searchHardwareSoftwareAccountResult(AccountSearchCondition condition) throws BcgogoException;

  HardwareSoftwareAccountDTO getHardwareSoftwareAccountByShopId(long shopId);

  HardwareSoftwareAccountDTO checkSoftwareAccountByShopId(Long shopId, Double total);

  void updateHardwareSoftwareAccount(HardwareSoftwareAccountDTO dto) throws BcgogoException;

  void updateHardwareSoftwareAccount(HardwareSoftwareAccountDTO dto,TxnWriter writer) throws BcgogoException;

  Map<String,Object> countHardwareSoftwareAccount(AccountSearchCondition condition);

}
