package com.bcgogo.pojox.common.logback;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-10-20
 * Time: 下午12:23
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

public class LogBackConfigWatcher extends TimerTask {
  protected final Logger log = LoggerFactory.getLogger(getClass());
  private long lastReloaded;
  private String configLocation;

  LogBackConfigWatcher(String configLocation) {
    this.configLocation = configLocation;
  }

  public void run() {
    try {
      File configFile = ResourceUtils.getFile(this.configLocation);
      if ((configFile == null) || (!configFile.exists()) || (!configFile.isFile())) {
        this.log.info("logback override Config location {} is invalid or does not exist", configFile == null ? null : configFile.getAbsolutePath());

        return;
      }
      if (shouldReconfigure(configFile)) {
        this.log.debug("logback reconfiguration needed");
        LogbackConfigurer.initLogging(configFile);
        this.lastReloaded = System.currentTimeMillis();
        this.log.info("logback reconfiguration performed from resource '{}'", configFile.getAbsolutePath());
      }
    } catch (FileNotFoundException e) {
    } catch (Exception e) {
      this.log.warn("unexpected error while trying to re-configure logback with resource: '" + this.configLocation + "'", e.getMessage());
    }
  }

  protected boolean shouldReconfigure(File file) {
    long configLastModified = file.lastModified();
    this.log.trace("last modified='{}', last reloaded='{}'", Long.valueOf(configLastModified), Long.valueOf(this.lastReloaded));
    if (this.lastReloaded == 0L) {
      this.lastReloaded = configLastModified;
      return false;
    }
    return configLastModified > this.lastReloaded;
  }
}
