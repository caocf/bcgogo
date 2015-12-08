package com.bcgogo.driving;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-10-19
 * Time: 17:21
 */
@Component
public class SpringUtil implements ApplicationContextAware {
  /**
   * 当前IOC
   */
   @Autowired
  private static ApplicationContext applicationContext;

  /**
   * 设置当前上下文环境，此方法由spring自动装配
   */
  @Override
  public void setApplicationContext(ApplicationContext arg0)
    throws BeansException {
    applicationContext = arg0;
  }

  /**
   * 从当前IOC获取bean
   *
   * @param id bean的id
   * @return
   */
  public static Object getObject(String id) {
    return applicationContext.getBean(id);
  }

}
