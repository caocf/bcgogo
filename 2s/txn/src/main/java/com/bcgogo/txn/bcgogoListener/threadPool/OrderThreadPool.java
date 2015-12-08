package com.bcgogo.txn.bcgogoListener.threadPool;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 12-4-10
 * Time: 下午4:48
 * To change this template use File | Settings | File Templates.
 */
public class OrderThreadPool {
  private static Executor pool = Executors.newFixedThreadPool(50);
  private OrderThreadPool(){}

  public static Executor getInstance(){
        if(pool == null){
          synchronized (OrderThreadPool.class){
            if(pool == null){
              pool = Executors.newFixedThreadPool(50);
            }
          }
        }
        return pool;
  }
}
