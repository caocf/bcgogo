package com.bcgogo.driving.service.listener;

import java.util.EventListener;

/**
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 12-4-10
 * Time: 下午4:55
 * To change this template use File | Settings | File Templates.
 */
public abstract class BcgogoEventListener implements EventListener,Runnable{
  public BcgogoEventListener(){}
	public abstract void run() ;
}
