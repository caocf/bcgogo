package com.bcgogo.pojo.pool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-7-30
 * Time: 13:51
 */


public class JdbcBatchInsert {
  public static void main(String args[]) {
    Connection conn = null;
    Statement st = null;
    ResultSet rs = null;
    String url = "jdbc:mysql://localhost:3306/";
    String db = "komal";
    String driver = "com.mysql.jdbc.Driver";
    String user = "root";
    String pass = "root";
    try {
      Class.forName(driver);
      conn = DriverManager.getConnection(url + db, user, pass);
      conn.setAutoCommit(false);// Disables auto-commit.
      st = conn.createStatement();
      st.addBatch("INSERT INTO person VALUES('4','Komal')");
      st.addBatch("INSERT INTO person VALUES('5','Ajay')");
      st.addBatch("INSERT INTO person VALUES('6','Santosh')");
      st.executeBatch();
      String sql = "select * from person";
      rs = st.executeQuery(sql);
      System.out.println("No  \tName");
      while (rs.next()) {
        System.out.print(rs.getString(1) + "   \t");
        System.out.println(rs.getString(2));
      }
      rs.close();
      st.close();
      conn.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
