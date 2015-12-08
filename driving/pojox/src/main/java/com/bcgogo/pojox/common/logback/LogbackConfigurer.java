package com.bcgogo.pojox.common.logback;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-10-21
 * Time: 11:02
 */

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.status.ErrorStatus;
import ch.qos.logback.core.status.InfoStatus;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Handler;
import java.util.logging.LogManager;

import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.util.ResourceUtils;
import org.springframework.util.SystemPropertyUtils;

public class LogbackConfigurer {
  public static void initLogging(String location)
    throws FileNotFoundException, JoranException {
    String resolvedLocation = SystemPropertyUtils.resolvePlaceholders(location);
    initLogging(ResourceUtils.getFile(resolvedLocation));
  }

  public static void initLogging(File file) throws FileNotFoundException, JoranException {
    org.slf4j.Logger logger = LoggerFactory.getLogger(LogBackContextListener.class);
    LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
    if ((file == null) || (!file.exists()) || (!file.isFile())) {
      logger.info("logback override Config location {} is invalid or does not exist", file == null ? null : file.getAbsolutePath());

      return;
    }
    try {
      logger.info("Starting to load new logger config");
      JoranConfigurator jc = new JoranConfigurator();
      jc.setContext(lc);
      lc.reset();
      jc.doConfigure(file);
      lc.getStatusManager().add(new InfoStatus("done resetting the logging context", LogbackConfigurer.class));

      java.util.logging.Logger rootLogger = LogManager.getLogManager().getLogger("");
      Handler[] handlers = rootLogger.getHandlers();
      for (Handler handler : handlers) {
        rootLogger.removeHandler(handler);
      }
      SLF4JBridgeHandler.install();
      logger.info("New logger config initialized");
    } catch (JoranException re) {
      lc.getStatusManager().add(new ErrorStatus("done resetting the logging context", LogbackConfigurer.class, re));

      throw re;
    }
  }
}
