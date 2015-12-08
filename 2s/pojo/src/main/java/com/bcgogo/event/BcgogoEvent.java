package com.bcgogo.event;

import java.util.EventObject;

/**
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 12-4-10
 * Time: 下午4:52
 * To change this template use File | Settings | File Templates.
 * Comment:从EventOject继承，定义BcgogoEvent
 */
  public class BcgogoEvent extends EventObject {
    public BcgogoEvent(Object arg0) {
      super(arg0);
    }
  }
