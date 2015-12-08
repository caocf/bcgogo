package com.bcgogo.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 12-8-17
 * Time: 下午3:10
 */
@Component
public class DataSourceTest {
  private Logger LOG = LoggerFactory.getLogger(DataSourceTest.class);

  @Autowired
  private JdbcTemplate jdbcTemplateA;

  @Autowired
  private JdbcTemplate jdbcTemplateB;

  public void testConnectionA(){
    long result = jdbcTemplateA.queryForLong("select id from config.area order by id desc limit 1");
    LOG.info("正在测试连接A: 得到结果: {}", result);
  }

  public void testConnectionB(){
    long result = jdbcTemplateB.queryForLong("select id from config.area order by id desc limit 1");
    LOG.info("正在测试连接B: 得到结果: {}", result);
  }


  public static void main(String[] args) {
    GenericXmlApplicationContext context = new GenericXmlApplicationContext();
		context.setValidating(false);
		context.load("classpath:applicationContext.xml");
		context.refresh();
		DataSourceTest test = (DataSourceTest)context.getBean(DataSourceTest.class);
    test.testConnectionA();
    test.testConnectionB();
  }
}
