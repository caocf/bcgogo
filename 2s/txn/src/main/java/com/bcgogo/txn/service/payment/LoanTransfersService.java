package com.bcgogo.txn.service.payment;

import com.bcgogo.common.Pager;
import com.bcgogo.enums.payment.LoanTransfersStatus;
import com.bcgogo.payment.chinapay.ChinaPay;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.LoanTransfersDTO;
import com.bcgogo.txn.model.LoanTransfers;
import com.bcgogo.txn.model.TxnDaoManager;
import com.bcgogo.txn.model.TxnWriter;
import com.bcgogo.user.service.permission.IShopVersionService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-10-23
 * Time: 上午11:20
 * 货款转账 service
 */
@Component
public class LoanTransfersService implements ILoanTransfersService {
  private static final Logger LOG = LoggerFactory.getLogger(LoanTransfersService.class);
  @Autowired
  private TxnDaoManager txnDaoManager;

  @Override
  public LoanTransfersDTO createLoanTransfers(LoanTransfersDTO loanTransfersDTO) {
    if (loanTransfersDTO == null) return null;
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      LoanTransfers loanTransfers = new LoanTransfers(loanTransfersDTO);
      writer.save(loanTransfers);
      writer.commit(status);
      loanTransfersDTO.setId(loanTransfers.getId());
      return loanTransfersDTO;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public LoanTransfersDTO updateLoanTransfers(LoanTransfersDTO loanTransfersDTO) {
    if (loanTransfersDTO == null) return null;
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    try {
      if (loanTransfersDTO.getId() == null) {
        return createLoanTransfers(loanTransfersDTO);
      }
      LoanTransfers loanTransfers = writer.getById(LoanTransfers.class, loanTransfersDTO.getId());
      loanTransfers.fromDTO(loanTransfersDTO);
      writer.update(loanTransfers);
      writer.commit(status);
      return loanTransfersDTO;
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public LoanTransfersDTO handleLoanTransfersByTransfersNumber(String transfersNumber, String payStatus) {
    if (StringUtils.isBlank(transfersNumber)) return null;
    TxnWriter writer = txnDaoManager.getWriter();
    LoanTransfersDTO loanTransfersDTO = writer.getLoanTransfersByTransfersNumber(transfersNumber);
    if (ChinaPay.PAY_STAT_SUCCESS.equals(payStatus)) {
      loanTransfersDTO.setStatus(LoanTransfersStatus.LOAN_SUCCESS);
    } else {
      loanTransfersDTO.setStatus(LoanTransfersStatus.LOAN_FAIL);
    }
    loanTransfersDTO.setPayTime(System.currentTimeMillis());
    return this.updateLoanTransfers(loanTransfersDTO);
  }

  @Override
  public List<LoanTransfersDTO> getLoanTransfersByShopId(Long shopId, Pager pager) {
    if (shopId==null) return null;
    TxnWriter writer = txnDaoManager.getWriter();
    List<LoanTransfers> loanTransfersList = writer.getLoanTransfersByShopId(shopId, pager);
    List<LoanTransfersDTO> loanTransfersDTOList = new ArrayList<LoanTransfersDTO>();
    IShopVersionService shopVersionService = ServiceManager.getService(IShopVersionService.class);
    if (CollectionUtils.isEmpty(loanTransfersList)) return loanTransfersDTOList;
    LoanTransfersDTO loanTransfersDTO = null;
    for (LoanTransfers loanTransfers : loanTransfersList) {
      loanTransfersDTO = loanTransfers.toDTO();
      if(loanTransfers.getShopVersionId()!=null){
        loanTransfersDTO.setShopVersionValue(shopVersionService.getShopVersionById(loanTransfers.getShopVersionId()).getValue());
      }
      loanTransfersDTOList.add(loanTransfersDTO);
    }
    return loanTransfersDTOList;
  }

  @Override
  public int countLoanTransfersByShopId(Long shopId) {
    if (shopId==null) return 0;
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.countLoanTransfersByShopId(shopId);
  }

  @Override
  public Double sumLoanTransfersTotalAmountByShopId(Long shopId) {
    if (shopId==null) return 0d;
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.sumLoanTransfersTotalAmountByShopId(shopId);
  }

  @Override
  public List<Long> getLoanTransfersIdsByStatus(Long shopId, int start, int pageSize, Long loanTransferTime) {
    TxnWriter writer = txnDaoManager.getWriter();
    return writer.getLoanTransfersIdsByStatus(shopId, start, pageSize, loanTransferTime);
  }

  @Override
  public List<LoanTransfersDTO> getLoanTransfersIdsById(Long... ids) {
    TxnWriter writer = txnDaoManager.getWriter();
    List<LoanTransfers> loanTransfersList = writer.getLoanTransfersIdsById(ids);
    List<LoanTransfersDTO> loanTransfersDTOList = new ArrayList<LoanTransfersDTO>();
    if (CollectionUtils.isEmpty(loanTransfersList)) return loanTransfersDTOList;
    for (LoanTransfers loanTransfers : loanTransfersList) {
      loanTransfersDTOList.add(loanTransfers.toDTO());
    }
    return loanTransfersDTOList;
  }
}
