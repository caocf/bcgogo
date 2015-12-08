package com.bcgogo.txn.bcgogoListener.orderEvent;

import com.bcgogo.event.BcgogoEvent;
import com.bcgogo.txn.dto.StatementAccountOrderDTO;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-6-27
 * Time: 上午11:53
 */
public class StatementAccountEvent extends BcgogoEvent {
  private boolean orderFlag = false, mainFlag = false;
  private StatementAccountOrderDTO statementAccountOrderDTO;

  public StatementAccountEvent(StatementAccountOrderDTO statementAccountOrderDTO) {
    super(statementAccountOrderDTO);
    this.statementAccountOrderDTO =  statementAccountOrderDTO;
  }

  public StatementAccountOrderDTO getStatementAccountOrderDTO() {
    return statementAccountOrderDTO;
  }

  public void setStatementAccountOrderDTO(StatementAccountOrderDTO statementAccountOrderDTO) {
    this.statementAccountOrderDTO = statementAccountOrderDTO;
  }

  public boolean isOrderFlag() {
    return orderFlag;
  }

  public void setOrderFlag(boolean orderFlag) {
    this.orderFlag = orderFlag;
  }

  public boolean isMainFlag() {
    return mainFlag;
  }

  public void setMainFlag(boolean mainFlag) {
    this.mainFlag = mainFlag;
  }

  public boolean mockFlag() {
      return !(orderFlag && mainFlag);
    }
}
