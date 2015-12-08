package com.bcgogo;

import com.bcgogo.config.ConfigServiceFactory;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.ConfigService;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IShopService;
import com.bcgogo.enums.shop.ShopState;
import com.bcgogo.enums.shop.ShopStatus;
import com.bcgogo.enums.txn.finance.ChargeType;
import com.bcgogo.enums.txn.finance.PaymentType;
import com.bcgogo.product.BcgogoProductDTO;
import com.bcgogo.product.BcgogoProductPropertyDTO;
import com.bcgogo.product.ProductServiceFactory;
import com.bcgogo.product.service.BcgogoProductService;
import com.bcgogo.product.service.IBcgogoProductService;
import com.bcgogo.search.SearchServiceFactory;
import com.bcgogo.search.client.SolrClientHelper;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.TxnServiceFactory;
import com.bcgogo.user.UserServiceFactory;
import com.bcgogo.util.h2.H2EventListener;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.ShopConstant;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AbstractTest {

  @BeforeClass
  public static void init() throws Exception {
    H2EventListener.startServer.set(false);

    Map<String, String> jpaProperties = new HashMap<String, String>();
    System.setProperty("unit.test", "true");
    System.setProperty("solr.solr.home", "../search/src/test/resources/solr");
    jpaProperties.put("bcgogo.dataSource.url",
        "jdbc:h2:mem:test_bcgogo;DB_CLOSE_DELAY=-1;TRACE_LEVEL_SYSTEM_OUT=2" +
            ";DATABASE_EVENT_LISTENER='com.bcgogo.util.h2.H2EventListener'");

    SimpleNamingContextBuilder.emptyActivatedContextBuilder();
    new ConfigServiceFactory(jpaProperties);
    new ProductServiceFactory(jpaProperties);
    new SearchServiceFactory(jpaProperties);
    new UserServiceFactory(jpaProperties);
    new TxnServiceFactory(jpaProperties);

    ServiceManager.getService(ConfigService.class).setConfig("RecentChangedProductExpirationTime", "10", ShopConstant.BC_SHOP_ID);

    SolrClientHelper.unitTestClear();
  }

  @AfterClass
  public static void terminate() {
  }

  public Long createShop() throws Exception {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    ShopDTO shopDTO = new ShopDTO();
    shopDTO.setAccount("test");
    shopDTO.setName("test");
    shopDTO.setShortname("shortName");
    shopDTO.setShopState(ShopState.ACTIVE);
    shopDTO.setShopStatus(ShopStatus.REGISTERED_PAID);
//    shopDTO.setLandline("67773331");
    shopDTO.setMobile("13945678901");
    ServiceManager.getService(IShopService.class).createShop(shopDTO);
    return shopDTO.getId();
  }

  public static final Double softPrice=999D;

  public Long createRegisteredTrialShop() throws Exception {
    IBcgogoProductService bcgogoProductService = ServiceManager.getService(IBcgogoProductService.class);
    BcgogoProductDTO bcgogoProductDTO = new BcgogoProductDTO();
    bcgogoProductDTO.setDefaultPrice(softPrice);
    bcgogoProductDTO.setName("汽配高级");
    bcgogoProductDTO.setPaymentType(PaymentType.SOFTWARE);
    bcgogoProductDTO.setText("汽配高级");
    bcgogoProductDTO.setUnit("套");

    List<BcgogoProductPropertyDTO> bcgogoProductPropertyDTOList = new ArrayList<BcgogoProductPropertyDTO>();
    BcgogoProductPropertyDTO bcgogoProductPropertyDTO = new BcgogoProductPropertyDTO();
    bcgogoProductPropertyDTO.setKind("汽配高级");
    bcgogoProductPropertyDTO.setPrice(softPrice);
    bcgogoProductPropertyDTO.setType("");
    bcgogoProductPropertyDTOList.add(bcgogoProductPropertyDTO);
    bcgogoProductDTO.setPropertyDTOList(bcgogoProductPropertyDTOList);
    bcgogoProductService.saveBcgogoProductDTO(bcgogoProductDTO);

    ShopDTO shopDTO = new ShopDTO();
    shopDTO.setAccount("test");
    shopDTO.setName("test");
    shopDTO.setChargeType(ChargeType.ONE_TIME);
    shopDTO.setShopStatus(ShopStatus.REGISTERED_TRIAL);
    shopDTO.setSoftPrice(softPrice);
    shopDTO.setAgentId("1");
    shopDTO.setTrialStartTime(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DEFAULT, "2013-03-23 9:00"));
    shopDTO.setTrialEndTime(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_DEFAULT, "2013-04-22 9:00"));
    shopDTO.setShopVersionId(bcgogoProductDTO.getPropertyDTOList().get(0).getId());
    ServiceManager.getService(IShopService.class).createShop(shopDTO);

    return shopDTO.getId();
  }


}
