package com.bcgogo.productRead.model;

import com.bcgogo.service.GenericWriterDao;
import org.springframework.transaction.support.ResourceTransactionManager;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 13-6-24
 * Time: 上午11:40
 */
public class ProductReadWriter extends GenericWriterDao {

  public ProductReadWriter(ResourceTransactionManager transactionManager) {
    super(transactionManager);
  }

}
