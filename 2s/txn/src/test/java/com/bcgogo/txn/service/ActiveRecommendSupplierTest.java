package com.bcgogo.txn.service;

import com.bcgogo.AbstractTest;
import com.bcgogo.service.ServiceManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * User: ZhangJuntao
 * Date: 13-5-13
 * Time: 下午5:58
 */
public class ActiveRecommendSupplierTest extends AbstractTest {
  private ActiveRecommendSupplierService activeRecommendSupplierService;

  @Before
  public void setUp() throws Exception {
    activeRecommendSupplierService = ServiceManager.getService(ActiveRecommendSupplierService.class);
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void obtainActiveRecommendSupplierByProductIdTest() throws Exception {
    Long customerId = this.createShop();
    Long supplierId1 = this.createShop();
    Long supplierId2 = this.createShop();




  }


}
