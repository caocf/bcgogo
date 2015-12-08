package com.bcgogo.pwd;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 12-12-20
 * Time: 下午3:21
 */
public class User {

  private Long id;
  private Long shopId;
  private String password;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}

class UserRowMapper implements RowMapper{

  public Object mapRow(ResultSet resultSet, int i) throws SQLException {
    User user = new User();
    user.setId(resultSet.getLong("id"));
    user.setShopId(resultSet.getLong("shop_id"));
    user.setPassword(resultSet.getString("password"));
    return user;
  }
}
