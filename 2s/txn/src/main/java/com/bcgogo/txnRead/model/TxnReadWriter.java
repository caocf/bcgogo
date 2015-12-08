package com.bcgogo.txnRead.model;

import com.bcgogo.service.GenericWriterDao;
import org.springframework.transaction.support.ResourceTransactionManager;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 13-6-24
 * Time: 上午11:40
 */
public class TxnReadWriter extends GenericWriterDao {

  public TxnReadWriter(ResourceTransactionManager transactionManager) {
    super(transactionManager);
  }

}
