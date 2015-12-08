package com.bcgogo.pwd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * 批量更新目标服务器中user表的所有密码
 * User: Jimuchen
 * Date: 12-12-20
 * Time: 下午3:06
 */
@Component
public class PasswordBatchChange {
  private Logger LOG = LoggerFactory.getLogger(PasswordBatchChange.class);

  @Autowired
  @Qualifier("jdbcTemplateA")
  private JdbcTemplate connA;

  public void testConnectionA(){
    long result = connA.queryForLong("select id from bcuser.user order by id desc limit 1");
    LOG.info("正在测试连接A: 得到结果: {}", result);
  }

  public void updatePwd(String originPwd){
    final List<User> users = connA.query("select * from user", new UserRowMapper());
    for(User u: users){
      System.out.println(u.getId() + ":" +u.getShopId() + ":" + u.getPassword());
      String pwd = EncryptionUtil.encryptPassword(originPwd, u.getShopId());
      connA.update("update user set password = ? where id = ?", pwd, u.getId());
    }
//    connA.batchUpdate("update user set password=? where id=?",  new BatchPreparedStatementSetter() {
//      public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
//        preparedStatement.setString(1, EncryptionUtil.encryptPassword(users.get(i).getPassword(), users.get(i).getShopId()));
//        preparedStatement.setLong(2, users.get(i).getId());
//      }
//
//      public int getBatchSize() {
//        return users.size();
//      }
//    });

  }

  /**
   * 将目标数据库中所有用户密码置为指定值
   * @param args
   */
  public static void main(String[] args) {
    GenericXmlApplicationContext context = new GenericXmlApplicationContext();
		context.setValidating(false);
		context.load("classpath:applicationContext.xml");
		context.refresh();
		PasswordBatchChange test = (PasswordBatchChange)context.getBean(PasswordBatchChange.class);
    test.updatePwd("111111");
  }
}
