package com.bcgogo.driving.model.mongodb;


import com.bcgogo.pojox.common.Assert;
import com.bcgogo.pojox.util.CommonUtil;
import com.bcgogo.pojox.util.NumberUtil;
import com.bcgogo.pojox.util.PropUtil;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


public final class MongoFactory {
  private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(MongoFactory.class);
  private static MongoDatabase db;
  private static String DB_NAME;

  private static String USER_NAME;
  private static String USER_PASS;

  private static String MONGO_DB_IP;
  private static int MONGO_DB_PORT;


  public static String getDBPropFile() throws IOException {
    LOG.info("mongo start getDBPropFile");
    String mongoDir = PropUtil.getLPath();
    mongoDir = mongoDir + "mongodb.properties";
    LOG.info("path.mongodb.properties:{}", mongoDir);
    return mongoDir;
  }

  static {
    try {
      String file = getDBPropFile();
      DB_NAME = PropUtil.readPropertyFile("DB_NAME", file);
      USER_NAME = PropUtil.readPropertyFile("USER_NAME", file);
      USER_PASS = PropUtil.readPropertyFile("USER_PASS", file);
      MONGO_DB_IP = PropUtil.readPropertyFile("MONGO_DB_IP", file);
      Assert.notEmpty(DB_NAME);
      Assert.notEmpty(MONGO_DB_IP);
      MONGO_DB_PORT = NumberUtil.intValue(PropUtil.readPropertyFile("MONGO_DB_PORT", file));
    } catch (IOException e) {
      LOG.error(e.getMessage(), e);
    }

  }


  public synchronized static MongoDatabase instance() {
    if (db == null) {
      StringBuilder sb = new StringBuilder();
      sb.append("mongodb://")
        .append(USER_NAME).append(":")
        .append(USER_PASS).append("@")
        .append(MONGO_DB_IP).append(":")
        .append(MONGO_DB_PORT).append("/?authSource=")
        .append(DB_NAME)
        .append("&authMechanism=SCRAM-SHA-1");
      MongoClientURI uri = new MongoClientURI(sb.toString());
      LOG.info("mongo connect prop:{}", uri.getURI());
      MongoClient mongoClient = new MongoClient(uri);
      db = mongoClient.getDatabase(DB_NAME);
    }
    return db;
  }


}
