package com.bcgogo.pojox.common.logback;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-10-20
 * Time: 下午12:20
 */

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.joran.spi.JoranException;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;
import org.springframework.util.SystemPropertyUtils;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.FileNotFoundException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class LogBackContextListener
  implements ServletContextListener {
  private static final String LOGBACK_CONFIG_FILE = "logback-config-file";
  private boolean _logBackStarted = false;
  private ScheduledExecutorService _timer;
  private ScheduledFuture _timerFuture;

  public void contextInitialized(ServletContextEvent event) {
    if (System.getProperty("disable.logback") != null) {
      return;
    }
    String location = event.getServletContext().getInitParameter("logback-config-file");
    if (location != null) {
      try {
        if (!ResourceUtils.isUrl(location)) {
          location = SystemPropertyUtils.resolvePlaceholders(location);
          location = WebUtils.getRealPath(event.getServletContext(), location);
        }
        event.getServletContext().log("Initializing Logback from [" + location + "]");
        LogbackConfigurer.initLogging(location);
        this._logBackStarted = true;
      } catch (FileNotFoundException ex) {
        event.getServletContext().log("Invalid logbackConfigLocation [" + location + "]", ex);
      } catch (JoranException e) {
        event.getServletContext().log("Unexpected error during logback initialization " + location, e);
      }
    }
    this._timer = Executors.newSingleThreadScheduledExecutor();
    this._timerFuture = this._timer.scheduleWithFixedDelay(new LogBackConfigWatcher(location), 5000L, 5000L, TimeUnit.MILLISECONDS);
  }

  public void contextDestroyed(ServletContextEvent event) {
    try {
      if (this._logBackStarted) {
        if (this._timerFuture != null) this._timerFuture.cancel(true);
        if (this._timer != null) {
          event.getServletContext().log("Shutdown LogbackConfigWatcher timer");
          this._timer.shutdown();
          int count = 0;
          while ((!this._timer.isShutdown()) && (count++ < 10))
            try {
              this._timer.awaitTermination(500L, TimeUnit.MILLISECONDS);
            } catch (InterruptedException ignore) {
            }
          event.getServletContext().log("LogbackConfigWatcher timer stopped.");
        }
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        event.getServletContext().log("stopping LoggerContext");
        lc.stop();
        event.getServletContext().log("LoggerContext stopped.");
      }
    } catch (RuntimeException e) {
      event.getServletContext().log("Unexpected error during logback shutdown", e);
    } finally {
      this._timerFuture = null;
      this._timer = null;
      this._logBackStarted = false;
    }
  }
}
