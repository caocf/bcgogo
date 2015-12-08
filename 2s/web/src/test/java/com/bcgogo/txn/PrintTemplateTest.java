package com.bcgogo.txn;

import com.bcgogo.AbstractTest;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.PrintTemplateDTO;
import com.bcgogo.txn.dto.ShopPrintTemplateDTO;
import com.bcgogo.txn.service.IPrintService;
import com.bcgogo.txn.service.ITxnService;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-6-18
 * Time: 下午9:24
 * To change this template use File | Settings | File Templates.
 */
public class PrintTemplateTest extends AbstractTest{

  private ITxnService txnService;
  private IPrintService printService;

  @Before
  public void setUp() throws Exception {

    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
    txnService = ServiceManager.getService(ITxnService.class);
    printService = ServiceManager.getService(IPrintService.class);
  }

  @Test
  public void testCreatePrintTemplate() throws  Exception
  {
      Long shopId = createShop();
      request.getSession().setAttribute("shopId", shopId);
      PrintTemplateDTO printTemplateDTO = new PrintTemplateDTO();
      String shopName = "凹凸曼大战怪兽";
//      String orderType = "采购单";
      String templateName="采购巨无霸";
      byte[] bytes = new byte[]{0,1,2,4};
      printTemplateDTO.setName(templateName);
      printTemplateDTO.setOrderType(OrderTypes.PURCHASE);
      printTemplateDTO.setTemplateHtml(bytes);
      printService.createPrintTemplate(shopId, printTemplateDTO);
      PrintTemplateDTO printTemplateDTO2 = printService.getSinglePrintTemplateDTOByShopIdAndType(shopId, OrderTypes.PURCHASE);
      Assert.assertEquals(shopId,printTemplateDTO2.getShopId());
      Assert.assertEquals(templateName,printTemplateDTO2.getName());
  }

  @Test
  public void testCreateOrUpdateShopPrintTemplate() throws Exception
  {
      Long shopId = createShop();
      request.getSession().setAttribute("shopId", shopId);
      PrintTemplateDTO printTemplateDTO = new PrintTemplateDTO();
      String shopName = "凹凸曼大战怪兽";
      OrderTypes orderType = OrderTypes.PURCHASE;
      String templateName="采购巨无霸";
      byte[] bytes = new byte[]{0,1,2,4};
      printTemplateDTO.setName(templateName);
      printTemplateDTO.setOrderType(orderType);
      printTemplateDTO.setTemplateHtml(bytes);
      printService.createPrintTemplate(shopId, printTemplateDTO);
      PrintTemplateDTO printTemplateDTO2 = printService.getSinglePrintTemplateDTOByShopIdAndType(shopId, orderType);
      ShopPrintTemplateDTO shopPrintTemplateDTO = new ShopPrintTemplateDTO();
      shopPrintTemplateDTO.setOrderType(orderType);
      shopPrintTemplateDTO.setShopId(shopId);
      shopPrintTemplateDTO.setTemplateId(printTemplateDTO2.getId());
      ShopPrintTemplateDTO shopPrintTemplateDTO2 = printService.getSingleShopPrintTemplateDTOByShopIdAndType(shopId, orderType);
      Assert.assertEquals(shopId,shopPrintTemplateDTO2.getShopId());
      Assert.assertEquals(printTemplateDTO2.getId(),shopPrintTemplateDTO2.getTemplateId());

  }

}
