package com.bcgogo.notification.smsSend;

import com.bcgogo.exception.BcgogoException;
import com.bcgogo.notification.model.SmsJob;
import com.bcgogo.notification.service.INotificationService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.utils.RegexUtils;
import com.bcgogo.utils.SmsConstant;
import com.bcgogo.utils.StringUtil;
import com.bcgogo.utils.UserConstant;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-4-17
 * Time: 下午3:44
 * sms 工具类
 */
public class SmsUtil {
  private static final Logger LOG = LoggerFactory.getLogger(SmsUtil.class);


  public static int calculateSmsDTONum(String content,String shopName){
    content+=shopName+"【"+"】";
    return calculateSmsNum(content,null);
  }

  //计算短信条数
  public static int calculateSmsNum(String content, String mobiles) {
    if (StringUtil.isEmpty(content)) {
      return 0;
    }
    int num=0;
    if(StringUtil.isNotEmpty(mobiles)){
      String[] receiveMobiles = mobiles.split(",");
       num = receiveMobiles.length;
    }else {
      num=1;
    }
    int length = content.length();
    int smsCount = (int) Math.ceil(length / (SmsConstant.SMS_UNIT_LENGTH * 1.0));
    return smsCount * num;
  }

  //把 mobles组合成num个一组；
  public static String[] groupingSmsMobile(String mobiles, int num) {
    if (num <= 0 || mobiles == null) return null;
    String[] receiveMobiles = mobiles.split(",");
    int length = receiveMobiles.length;
    String[] sendMobiles = new String[(int) Math.ceil(length / (1.0 * num))];
    if ((length / (num * 1.0)) <= 1.0) {
      sendMobiles[0] = mobiles;
      return sendMobiles;
    }
    for (int i = 0, j = -1; i < length; i++) {
      if (i % num == 0) {
        sendMobiles[++j] = "";
      }
      sendMobiles[j] += receiveMobiles[i];
      if ((i + 1) % num != 0 && (i + 1) != length) {
        sendMobiles[j] += ",";
      }
    }
    return sendMobiles;
  }

  public static List<String[]> groupingMobilesToList(String mobiles, int num) {
    if (num <= 0 || mobiles == null) return null;
    List<String[]> sendMobilesList = new ArrayList<String[]>();
    List<String> tempList = new ArrayList<String>();
    String[] receiveMobiles = mobiles.split(",");
    int length = receiveMobiles.length;
    for (int i = 0; i < length; i++) {
      if (tempList.size() == num) {
        sendMobilesList.add(tempList.toArray(new String[tempList.size()]));
        tempList = new ArrayList<String>();
      }
      if (receiveMobiles[i].length() == 11) {
        tempList.add(receiveMobiles[i]);
      } else {
        LOG.warn("Message send by YiMei mobile[{}] is illegal.", receiveMobiles[i]);
      }
      if (i + 1 == length) {
        sendMobilesList.add(tempList.toArray(new String[tempList.size()]));
        break;
      }
    }
    return sendMobilesList;
  }

  public static String filterMobiles(String mobiles) {
    if (StringUtils.isBlank(mobiles)) return mobiles;
    String[] mobileArray = mobiles.split(",");
    StringBuilder builder = new StringBuilder();
    for (String mobile : mobileArray) {
      if (!RegexUtils.isMobile(mobile)) continue;
      builder.append(mobile).append(",");
    }
    return builder.toString();
  }

  //保存短信返回内容
  public static String accumulateSmsJobResponseReasons(String original, String now) {
    String responseReason = original == null ? now : (original + ";" + now);
    if (!StringUtil.isEmpty(responseReason) && responseReason.length() > 500)
      responseReason = StringUtil.subString(responseReason, 0, 500);
    return responseReason;
  }

}
