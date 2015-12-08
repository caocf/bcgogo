package com.bcgogo.finance;

import com.bcgogo.AbstractTest;
import com.bcgogo.common.Result;
import com.bcgogo.enums.txn.finance.*;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.product.BcgogoProductDTO;
import com.bcgogo.product.BcgogoProductPropertyDTO;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.finance.BcgogoReceivableDTO;
import com.bcgogo.txn.dto.finance.BcgogoReceivableRecordDTO;
import com.bcgogo.txn.dto.finance.BcgogoReceivableSearchCondition;
import com.bcgogo.txn.model.TxnDaoManager;
import com.bcgogo.txn.model.TxnWriter;
import com.bcgogo.txn.model.finance.InstalmentPlanAlgorithm;
import com.bcgogo.txn.service.finance.BcgogoReceivableService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 13-3-23
 * Time: 下午2:50
 */
public class BcgogoReceivableTest extends AbstractTest {

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void softwareOfflinePayInstalmentPlanTest() throws Exception {
    BcgogoReceivableService bcgogoReceivableService = ServiceManager.getService(BcgogoReceivableService.class);
    //创建shop
    long shopId = createRegisteredTrialShop();

    //创建 SoftwareReceivable
    bcgogoReceivableService.createSoftwareReceivable(shopId, 0l, "", BuyChannels.BACKGROUND_ENTRY);

    //获得bcgogoReceivableOrderRecordRelationId
    List<BcgogoReceivableRecordDTO> dtoList = searchBcgogoReceivableToBePaidTest();

    long bcgogoReceivableOrderRecordRelationId = dtoList.get(0).getBcgogoReceivableOrderRecordRelationId();
    //首次付款
    BcgogoReceivableDTO dto = new BcgogoReceivableDTO();
    dto.setShopId(shopId);
    dto.setInstalmentPlanAlgorithmId(createInstalmentPlanAlgorithm());
    dto.setBcgogoReceivableOrderRecordRelationId(bcgogoReceivableOrderRecordRelationId);
    dto.setTotalAmount(softPrice);
    dto.setPaidAmount(449D);
    dto.setPaymentTime(System.currentTimeMillis());
    dto.setPaymentMethod(PaymentMethod.DOOR_CHARGE);
    dto.setReceivableMethod(ReceivableMethod.INSTALLMENT);
    bcgogoReceivableService.softwareReceivable(dto);

    dtoList = searchBcgogoReceivableToBePaidTest();
    Assert.assertEquals(1, dtoList.size());
    bcgogoReceivableOrderRecordRelationId = dtoList.get(0).getBcgogoReceivableOrderRecordRelationId();


    //第二次付款
    dto = new BcgogoReceivableDTO();
    dto.setShopId(shopId);
    dto.setInstalmentPlanAlgorithmId(createInstalmentPlanAlgorithm());
    dto.setBcgogoReceivableOrderRecordRelationId(bcgogoReceivableOrderRecordRelationId);
    dto.setTotalAmount(softPrice - 449D);
    dto.setPaidAmount(70.48D);
    dto.setPaymentTime(System.currentTimeMillis());
    dto.setPaymentMethod(PaymentMethod.DOOR_CHARGE);
    dto.setReceivableMethod(ReceivableMethod.INSTALLMENT);
    bcgogoReceivableService.instalmentReceivable(dto);

    dtoList = searchBcgogoReceivableToBePaidTest();
    Assert.assertEquals(1, dtoList.size());
    bcgogoReceivableOrderRecordRelationId = dtoList.get(0).getBcgogoReceivableOrderRecordRelationId();


    //第三次付款
    dto.setTotalAmount(softPrice - 449D - 70.48D);
    dto.setPaidAmount(79.92d + 39.96D);
    dto.setBcgogoReceivableOrderRecordRelationId(bcgogoReceivableOrderRecordRelationId);
    bcgogoReceivableService.instalmentReceivable(dto);

    dtoList = searchBcgogoReceivableToBePaidTest();
    Assert.assertEquals(1, dtoList.size());
    bcgogoReceivableOrderRecordRelationId = dtoList.get(0).getBcgogoReceivableOrderRecordRelationId();


    //全部付清
    dto.setBcgogoReceivableOrderRecordRelationId(bcgogoReceivableOrderRecordRelationId);
    dto.setTotalAmount(softPrice - 449D - 70.48D - (79.92d + 39.96D));
    dto.setPaidAmount(dto.getTotalAmount());
    bcgogoReceivableService.instalmentReceivable(dto);

    dtoList = searchBcgogoReceivableToBePaidTest();
    Assert.assertEquals(0, dtoList.size());

    dtoList = searchBcgogoReceivablePendingReviewTest();
    Assert.assertEquals(4, dtoList.size());
  }

  private List<BcgogoReceivableRecordDTO> searchBcgogoReceivableToBePaidTest() throws BcgogoException {
    BcgogoReceivableService bcgogoReceivableService = ServiceManager.getService(BcgogoReceivableService.class);
    BcgogoReceivableSearchCondition condition = new BcgogoReceivableSearchCondition();
    condition.setLimit(100);
    condition.setStatus(BcgogoReceivableStatus.TO_BE_PAID);
    condition.setReceivableStatuses(new String[]{BcgogoReceivableStatus.TO_BE_PAID.name()});
    Result result = bcgogoReceivableService.searchBcgogoReceivableResult(condition,false);
    return (List<BcgogoReceivableRecordDTO>) result.getData();
  }

  private List<BcgogoReceivableRecordDTO> searchBcgogoReceivablePendingReviewTest() throws BcgogoException {
    BcgogoReceivableService bcgogoReceivableService = ServiceManager.getService(BcgogoReceivableService.class);
    BcgogoReceivableSearchCondition condition = new BcgogoReceivableSearchCondition();
    condition.setLimit(100);
    condition.setStatus(BcgogoReceivableStatus.PENDING_REVIEW);
    condition.setReceivableStatuses(new String[]{BcgogoReceivableStatus.PENDING_REVIEW.name()});
    Result result = bcgogoReceivableService.searchBcgogoReceivableResult(condition,false);
    return (List<BcgogoReceivableRecordDTO>) result.getData();
  }

  private Long createInstalmentPlanAlgorithm() {
    TxnDaoManager txnDaoManager = ServiceManager.getService(TxnDaoManager.class);
    TxnWriter writer = txnDaoManager.getWriter();
    Object status = writer.begin();
    InstalmentPlanAlgorithm algorithm;
    try {
      algorithm = new InstalmentPlanAlgorithm();
      algorithm.setName("6期");
      algorithm.setPeriods(6);
      algorithm.setPeriodsMonthRate(1d);
      algorithm.setTerminallyRatio("0.2,0.16,0.16,0.16,0.16,0.16");
      writer.save(algorithm);

      algorithm = new InstalmentPlanAlgorithm();
      algorithm.setName("12期");
      algorithm.setPeriods(12);
      algorithm.setPeriodsMonthRate(1d);
      algorithm.setTerminallyRatio("0.12,0.08,0.08,0.08,0.08,0.08,0.08,0.08,0.08,0.08,0.08,0.08");
      writer.save(algorithm);

      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
    return algorithm.getId();
  }

}
