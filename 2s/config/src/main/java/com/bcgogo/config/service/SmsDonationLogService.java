package com.bcgogo.config.service;

import com.bcgogo.config.model.ConfigDaoManager;
import com.bcgogo.config.model.ConfigWriter;
import com.bcgogo.config.model.SmsDonationLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 13-2-28
 * Time: 下午5:23
 */
@Component
public class SmsDonationLogService implements ISmsDonationLogService{
  private static final Logger LOG = LoggerFactory.getLogger(SmsDonationLogService.class);
  @Autowired
  private ConfigDaoManager configDaoManager;

  @Override
  public SmsDonationLog createSmsDonationLog(SmsDonationLog smsDonationLog) {
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try{
      writer.save(smsDonationLog);
      writer.commit(status);
    }catch(Exception e){
      LOG.error("创建短信赠送记录时出错, smsDonationLog:{}", smsDonationLog);
      LOG.error(e.getMessage(), e);
    }finally{
      writer.rollback(status);
    }
    return null;
  }

  @Override
  public List<SmsDonationLog> getSmsDonationLogByShopId(Long shopId) {
    ConfigWriter writer = configDaoManager.getWriter();
   return writer.getSmsDonationLogByShopId(shopId);
  }
}
