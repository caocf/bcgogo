package com.bcgogo.pojox.common;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 14-10-24
 * Time: 下午3:15
 */
public class ThreadPool {

   private static Executor pool = Executors.newFixedThreadPool(300);
  private ThreadPool(){}

  public static Executor getInstance(){
        if(pool == null){
          synchronized (ThreadPool.class){
            if(pool == null){
              pool = Executors.newFixedThreadPool(300);
            }
          }
        }
        return pool;
  }
}
