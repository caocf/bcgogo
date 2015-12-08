package com.bcgogo.payment;

import com.bcgogo.AbstractTest;
import com.bcgogo.admin.LoanTransfersController;
import com.bcgogo.common.Pager;
import com.bcgogo.payment.dto.ChinapayDTO;
import com.bcgogo.txn.dto.LoanTransfersDTO;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: hans
 * Date: 12-10-29
 * Time: 下午5:18
 * 汇款转账 Unit Test (注重测试 一个流程)
 */
public class LoanTransfersTest extends AbstractTest {
  LoanTransfersController loanTransfersController = new LoanTransfersController();
  @Before
  public void setUp() throws Exception {
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
  }

  @After
  public void tearDown() throws Exception {
    request = null;
    response = null;
  }

  //货款转账流程测试
  @Test//(timeout = 30000L)  //半分钟
  public void saveAndShowLoanTest() throws Exception {
    Long shopId = createShop();
    //show test
    String url = loanTransfersController.showPage(request, response);
    Assert.assertEquals("/admin/loanTransfers/startTransfer", url);
    List<LoanTransfersDTO> loanTransfersDTOList = (List<LoanTransfersDTO>) request.getAttribute("loanTransfersDTOList");
    Assert.assertEquals(0, loanTransfersDTOList.size());

    //save loan transfers
    LoanTransfersDTO loanTransfersDTO = new LoanTransfersDTO();
    loanTransfersDTO.setAmount(100d);
    loanTransfersDTO.setMemo("test");
    url = loanTransfersController.saveLoan(request, response, loanTransfersDTO);
    //测试url跳转准确
    Assert.assertEquals("/admin/loanTransfers/transferring", url);
    ChinapayDTO chinapayDTO = (ChinapayDTO) request.getAttribute("chinaPayDTOUnitTest");
    chinapayDTO.setPayStat("1001");    //手动设置 china pay 返回值
    chinapayDTO.setId(null);
    chinapayService.receive(chinapayDTO);  // 银联后台 返回
    chinapayDTO.setId(null);
    url = loanTransfersController.saveLoanComplete(request, response,chinapayDTO);
    Assert.assertEquals("/admin/loanTransfers/transferred", url);

    url = loanTransfersController.showPage(request, response);
    //show test
    Pager pager = (Pager) request.getAttribute("pager");
    loanTransfersDTOList = (List<LoanTransfersDTO>) request.getAttribute("loanTransfersDTOList");
    Double totalAmount = Double.valueOf((String) request.getAttribute("totalAmount"));
    Assert.assertEquals(100d, totalAmount, 0.001);
    Assert.assertEquals(1, loanTransfersDTOList.size());
    Assert.assertEquals(1, pager.getTotalRows());
  }

}
