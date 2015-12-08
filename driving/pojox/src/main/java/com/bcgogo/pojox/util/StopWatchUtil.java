package com.bcgogo.pojox.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-7-5
 * Time: 下午2:25
 */
public class StopWatchUtil {
  private StopWatch stopWatch;
  private long lastSplitTime = 0;
  private static final Logger LOG = LoggerFactory.getLogger(StopWatchUtil.class);
  private int step = 1;

  public StopWatchUtil() {
    stopWatch = new StopWatch();
    stopWatch.start("Step " + step);
  }

  public StopWatchUtil(String swName){
    stopWatch = new StopWatch(swName);
    stopWatch.start("Step " + step);
  }

  /**
   * 创建Stopwatch, 赋第一个任务名称并开始计时
   * @param swName  stopWatch名称
   * @param taskName  第一个任务名称
   */
  public StopWatchUtil(String swName, String taskName){
    stopWatch = new StopWatch(swName);
    stopWatch.start(taskName);
    LOG.debug("StopWatchUtil start,name:{}",swName);
  }

  /**
   * 结束上个任务，开始下个任务(taskName)
   * @param taskName 下个任务名称
   */
  public void stopAndStart(String taskName){
    stopWatch.stop();
    stopWatch.start(taskName);
    LOG.debug("StopWatchUtil-stopAndStart,taskName:{}",taskName);
  }

  public void stopAndStart(){
    stopWatch.stop();
    stopWatch.start("Step " + ++step);
  }

  /**
   * 结束任务，打印StopWatch信息
   */
  public void stopAndPrintLog(){
    stopWatch.stop();
    LOG.debug(stopWatch.toString());
  }

  public void stopAndPrintWarnLog(){
    stopWatch.stop();
    LOG.debug(stopWatch.toString());
  }

//  public void split() {
//    if (lastSplitTime == 0) {
//      lastSplitTime = super.getStartTime();
//    } else {
//      lastSplitTime = super.getSplitTime() + super.getStartTime();
//    }
//    super.split();
//  }
//
//  public void getExecutionTime(String logInfo) {
//    if (lastSplitTime == 0) {
//      lastSplitTime = super.getStartTime();
//    } else {
//      lastSplitTime = super.getSplitTime() + super.getStartTime();
//    }
//    super.split();
//    LOG.debug("StopWatchUtil step" + logInfo + ":{} ms", super.getSplitTime() + super.getStartTime() - lastSplitTime);
//    step++;
//  }



}
