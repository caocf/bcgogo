package com.bcgogo.schedule;

import com.bcgogo.util.spring.hibernate.HibernateUtil;
import org.hibernate.SessionFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-3-1
 * Time: 下午6:00
 * To change this template use File | Settings | File Templates.
 */
public abstract class BcgogoQuartzJobBean extends QuartzJobBean {
  private static final Logger LOG = LoggerFactory.getLogger(BcgogoQuartzJobBean.class);

  /**
   * 根据系统级参数判断当前主机是否是schedule执行机
   *
   * @param jobExecutionContext
   * @throws JobExecutionException
   */
  @Override
  protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    String isScheduleOn = System.getProperty("is.schedule.on");
    if (isScheduleOn != null && isScheduleOn.equals("true")) {
      executeJob(jobExecutionContext);
    }
  }

  protected void debugResourceLeak() {
    //Now some cleanup/debug stuff:
    try {
      if (TransactionSynchronizationManager.isSynchronizationActive()) {
        LOG.error("TransactionSynchronizationManager has active synchronizations");
      }
      Map map = TransactionSynchronizationManager.getResourceMap();
      if (map.size() > 0) {
        LOG.error("TransactionSynchronizationManager has active resources/sessions: " + map.size());
        ArrayList unbindKeys = new ArrayList(map.size());
        Set<Map.Entry> entrySet = map.entrySet();
        for (Map.Entry entry : entrySet) {
          Object key = entry.getKey();
          if (key instanceof SessionFactory) {
            Object value = entry.getValue();
            if (value instanceof SessionHolder) {
              LOG.error("TransactionSynchronizationManager has active sessionfactory : " + key +
                  " and session holders with sessions " + value);
              unbindKeys.add(key);
            } else {
              LOG.error("TransactionSynchronizationManager has active sessionfactory sessions, " + key +
                  " but value not SessionHolder: " + value);
            }
          } else {
            LOG.error("TransactionSynchronizationManager has active non sessionfactory resources: " + key);
          }
        }

        //Now clear/unbind
        //Have to do this as we cannot call unbind in the loop above (ConcurrentModification of Map)
        if (true) {
          for (Object key : unbindKeys) {
            SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager.unbindResource(key);
            HibernateUtil.releaseSession(sessionHolder.getSession(), (SessionFactory) key);
            sessionHolder.clear();
          }
        }
      }

      //Not Sure if this is correct but force safety check!!!!
      // Even if we remove resource (session factories) synchronization may still be active. And future threads
      // will bind resource to thread if sync is active. This would cause problems when (same thread object in future
      // requests) session is opened without binding to thread. The app code assumes the session is not bound, but
      // internally since sync is on, the session will get bound to the thread. But app will not  unbind (as the app
      // code did not explicitly call bind)
      if (true) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
          if (TransactionSynchronizationManager.getResourceMap() == null ||
              TransactionSynchronizationManager.getResourceMap().size() == 0) {
            LOG.error("TransactionSynchronizationManager still has synchronization with no " +
                "active resources, force clearing");
            TransactionSynchronizationManager.clear();
          } else {
            LOG.error("TransactionSynchronizationManager still has synchronization with " +
                TransactionSynchronizationManager.getResourceMap().size() +
                "active resources, force clearing");
            TransactionSynchronizationManager.clear();
          }
        }
      }


    } catch (Exception e) {
      //DO nothing just log and move on.
      LOG.error("failed while checking resource leak", e);
    } finally {
    }
  }

  abstract protected void executeJob(JobExecutionContext jobExecutionContext) throws JobExecutionException;
}
