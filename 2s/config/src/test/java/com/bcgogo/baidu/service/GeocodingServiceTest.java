//package com.bcgogo.baidu.service;
//
//import com.bcgogo.AbstractTest;
//import com.bcgogo.user.Coordinate;
//import com.bcgogo.baidu.model.geocoder.GeocoderResponse;
//import com.bcgogo.enums.baidu.GeocoderStatus;
//import com.bcgogo.service.ServiceManager;
//import com.bcgogo.utils.NumberUtil;
//import org.junit.After;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//
///**
// * GeocodingService Tester.
// *
// * @author zhangjuntao
// * @version 1.0
// * @since <pre>8,6, 2013</pre>
// */
//public class GeocodingServiceTest extends AbstractTest {
//
//  @Before
//  public void before() throws Exception {
//  }
//
//  @After
//  public void after() throws Exception {
//  }
//
//  /**
//   * Method: addressResolution(String address, String city, String key)
//   */
//  @Test
//  public void testAddressResolution() throws Exception {
//    IGeocodingService service = ServiceManager.getService(IGeocodingService.class);
//
//    GeocoderResponse response = service.addressToCoordinate("苏州", "苏州");
//
//    Assert.assertEquals(31.3d, NumberUtil.round(Double.valueOf(response.getResult().getLocation().getLat()), 1), 0.01);
//    Assert.assertEquals(GeocoderStatus.OK, response.getStatus());
//
//    response = service.addressToCoordinate("bcgogo-test", "苏州");
//    Assert.assertEquals(GeocoderStatus.OK, response.getStatus());
//    Assert.assertNull(response.getResult().getLocation());
//  }
//
//  /**
//   * Method: reverseAddressResolution(Location location, String key)
//   */
//  @Test
//  public void testReverseAddressResolution() throws Exception {
//    IGeocodingService service = ServiceManager.getService(IGeocodingService.class);
//
//    GeocoderResponse response = service.coordinateToAddress(new Coordinate("31.3", "120.6"));
//    Assert.assertEquals(GeocoderStatus.OK, response.getStatus());
//    Assert.assertEquals("苏州市", response.getResult().getAddressComponent().getCity());
//    Assert.assertEquals("江苏省", response.getResult().getAddressComponent().getProvince());
//
//    response = service.coordinateToAddress(new Coordinate("0", "0"));
//    Assert.assertEquals("", response.getResult().getAddressComponent().getCity());
//  }
//
//}
