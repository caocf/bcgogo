package com.bcgogo.web.init;

import com.bcgogo.common.Result;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.model.UserDaoManager;
import com.bcgogo.user.model.UserWriter;
import com.bcgogo.user.model.permission.User;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.EncryptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 13-8-28
 * Time: 下午1:25
 * 用户初始化 controller
 */
@Controller
@RequestMapping("/userInit.do")
public class UserInitController {
  private static final Logger LOG = LoggerFactory.getLogger(UserInitController.class);


  /**
   * user表 password 初始化成 1
   */
  @RequestMapping(params = "method=initPasswordForDevelopers")
  @ResponseBody
  public Object initPasswordForDevelopers() throws Exception {
    try {
      UserDaoManager userDaoManager = ServiceManager.getService(UserDaoManager.class);
      UserWriter writer = userDaoManager.getWriter();
      List<User> userList = writer.getAllShopUser();
      Object status = writer.begin();
      try {
        if (CollectionUtil.isNotEmpty(userList)) {
          for (User user : userList) {
            user.setPassword(EncryptionUtil.encryptPassword("1", user.getShopId()));
            writer.update(user);
          }
          writer.commit(status);
        }
      } finally {
        writer.rollback(status);
      }
      return new Result("初始化成功", true);
    } catch (Exception e) {
      LOG.error("method=initPasswordForDevelopers" + e.getMessage(), e);
      return new Result("初始化失败", false);
    }
  }
}
