package com.bcgogo.report.service;

import com.bcgogo.AbstractTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: Xiao Jian
 * Date: 12-1-6
 */
public class ReportServiceTest extends AbstractTest {
  private static final Logger LOG = LoggerFactory.getLogger(ReportServiceTest.class);

 /* @Test
  public void testCountShopCarRepair() throws Exception {
    IReportService reportService = ServiceManager.getService(IReportService.class);

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    try {
      long startTime = simpleDateFormat.parse("2012-01-06 00:00:00").getTime();
      long endTime = simpleDateFormat.parse("2012-01-06 00:00:00").getTime();

      double total = reportService.countShopCarRepairIncome(10000010001000000L, startTime, endTime);
      LOG.info(String.valueOf(total));
      assertEquals(0.0d, total);
    } catch (Exception e) {
      LOG.info(e.getMessage());
    }

  }  */
}
