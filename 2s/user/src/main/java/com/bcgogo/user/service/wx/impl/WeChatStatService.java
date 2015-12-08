package com.bcgogo.user.service.wx.impl;

import com.bcgogo.user.model.UserDaoManager;
import com.bcgogo.user.model.UserWriter;
import com.bcgogo.user.service.wx.IWeChatStatService;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;
import com.bcgogo.wx.WXAccountStatDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-1-20
 * Time: 15:11
 */
@Component
public class WeChatStatService implements IWeChatStatService {

  public static final Logger LOG = LoggerFactory.getLogger(WeChatStatService.class);
  @Autowired
  private UserDaoManager daoManager;

  @Override
  public List<WXAccountStatDTO> getWXAccountStatDTO() {
    UserWriter writer = daoManager.getWriter();
    List<Object[]> objs = writer.getWXAccountStat();
    if (CollectionUtil.isEmpty(objs)) return null;
    List<WXAccountStatDTO> accountStatDTOs = new ArrayList<WXAccountStatDTO>();
    for (Object[] obj : objs) {
      WXAccountStatDTO accountStatDTO = new WXAccountStatDTO();
      accountStatDTO.setId(NumberUtil.longValue(obj[0]));
      accountStatDTO.setName(StringUtil.valueOf(obj[1]));
      accountStatDTO.setUserNum(NumberUtil.intValue(obj[2]));
      accountStatDTOs.add(accountStatDTO);
    }
    return accountStatDTOs;
  }

  private Map<Long, Integer> getWXUserGrowth() {
    UserWriter writer = daoManager.getWriter();
    List<Object[]> objs = writer.getWXUserGrowth();
    if (CollectionUtil.isEmpty(objs)) return null;
    Map<Long, Integer> growthMap = new HashMap<Long, Integer>();
    for (Object[] obj : objs) {
      Long id=NumberUtil.longValue(obj[0]);
      if(id==null) continue;
      growthMap.put(id, NumberUtil.intValue(obj[1]));
    }
    return growthMap;
  }

  @Override
  public String getWXAccountStatStr() {
    List<WXAccountStatDTO> accountStatDTOs = getWXAccountStatDTO();
    if (CollectionUtil.isEmpty(accountStatDTOs)) return "no data...";
    Map<Long, Integer> growthMap = getWXUserGrowth();
    for(WXAccountStatDTO statDTO:accountStatDTOs){
      statDTO.setUserGrowth(growthMap.get(statDTO.getId()));
    }
    StringBuilder sb = new StringBuilder("name--userNum--growth\n");
    int total = 0;
    for (int i = 0; i < accountStatDTOs.size(); i++) {
      WXAccountStatDTO accountStatDTO = accountStatDTOs.get(i);
      sb.append(i + 1).append(".").append(accountStatDTO.getName()).append(" ")
        .append(accountStatDTO.getUserNum()).append(" ")
        .append(accountStatDTO.getUserGrowth()).append("\n");
      total += NumberUtil.intValue(accountStatDTO.getUserNum());
    }
    sb.append("total:").append(total);
    return sb.toString();
  }

}
