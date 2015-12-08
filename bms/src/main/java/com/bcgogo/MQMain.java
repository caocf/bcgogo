package com.bcgogo;

import com.bcgogo.service.broker.IBcgogoBroker;
import com.bcgogo.service.broker.PrintBroker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-6-8
 * Time: 13:31
 */
public class MQMain {
  private static final Logger LOG = LoggerFactory.getLogger(MQMain.class);

   public static void main(String[] args) {

     try {
         IBcgogoBroker broker = new PrintBroker();
         broker.start();
     } catch (Exception e) {
       LOG.error(e.getMessage(), e);
     }
   }
}
