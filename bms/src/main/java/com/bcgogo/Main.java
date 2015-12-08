package com.bcgogo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;


/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-2-10
 * Time: 18:01
 */
public class Main {

  private static final Logger LOG = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) {
    ApplicationContext context = new GenericXmlApplicationContext("classpath:applicationContext.xml");
  }

}
