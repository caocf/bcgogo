package com.bcgogo.socketReceiver;

import com.bcgogo.socketReceiver.enums.GsmPointType;
import com.bcgogo.socketReceiver.model.GsmObdStatus;
import com.bcgogo.socketReceiver.model.GsmPoint;
import com.bcgogo.socketReceiver.rmi.IBcgogoApiSocketRmiServer;
import com.bcgogo.socketReceiver.service.IGsmObdStatusService;
import com.bcgogo.socketReceiver.util.NumberUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;


public class Main {
  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) throws Exception {
    ApplicationContext context = new GenericXmlApplicationContext("classpath:applicationContext.xml");
//    GsmMessageReceivedParser service = (GsmMessageReceivedParser) context.getBean("gsmMessageReceivedParser");
//    service.parser("#356823032255122#1584521547#1#0000#AUT#1#26280E58#11354.7034,E,2232.6869,N,000.45,345#090511#064052##");
//    MemCacheAdapter.add("test", "12345");
//    System.out.println(MemCacheAdapter.get("test"));

//    GsmObdStatus status = new GsmObdStatus();
//    status.setName(String.valueOf(System.currentTimeMillis()));
//    IGsmObdStatusService service = (IGsmObdStatusService) context.getBean("gsmObdStatusService");
//    service.save(status);

//    IBcgogoApiSocketRmiServer server = (IBcgogoApiSocketRmiServer)context.getBean("socketRmiServer");
//    GsmPoint gsmPoint = new GsmPoint();
//    gsmPoint.setGsmPointType(GsmPointType.JX);
//    gsmPoint.setEmi("356824206007887");
//   gsmPoint.setLat(NumberUtil.convertGPSLat("3106.4072"));
//    gsmPoint.setLon(NumberUtil.convertGPSLot("12123.8814"));
//    gsmPoint.setUploadTime(1395555214292L);
//    server.sendAlert("356824206007887", "3106.4072","12123.8814", GsmPointType.JX.name(), "1395555214292") ;
  }

}
