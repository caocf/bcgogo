package com.bcgogo.pinyin;

import com.bcgogo.pinyin.model.Product;
import com.bcgogo.pinyin.util.PinyinUtil;
import com.bcgogo.test.DataSourceTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 13-4-19
 * Time: 上午11:53
 */
@Component
public class ProductDao {
  private Logger LOG = LoggerFactory.getLogger(DataSourceTest.class);
  
  @Autowired
  private JdbcTemplate jdbcTemplate245;

  public void testConnectionA(){
    String sql = "select id, name, brand, model, spec, product_vehicle_brand, product_vehicle_model " +
        "from product.product order by id";
    List<Product> products = jdbcTemplate245.query(sql, new RowMapper<Product>() {
      public Product mapRow(ResultSet resultSet, int i) throws SQLException {
        Product product = new Product();
        product.setId(resultSet.getLong(1));
        product.setName(resultSet.getString(2));
        product.setBrand(resultSet.getString(3));
        product.setModel(resultSet.getString(4));
        product.setSpec(resultSet.getString(5));
        product.setProductVehicleBrand(resultSet.getString(6));
        product.setProductVehicleModel(resultSet.getString(7));
        return product;
      }
    });
    LOG.info("正在测试连接A: 得到结果: {}", products.size());
    Map<String, String[]> homophones = PinyinUtil.getAllHomophoneWords();
    int count = 0;
    int tokenCount = 0;
    Set<String> originNames = new HashSet<String>();
    Set<String> tokenNames = new HashSet<String>();
    for(Product product : products){
      product.putFieldsInSet(originNames);
      product.putTokensInSet(tokenNames);
    }
    for(String word: originNames){
      if(PinyinUtil.containHomophoneWord(word, homophones)){
        System.out.println(word);
        count++;
      }
    }
    for(String token: tokenNames){
      if(PinyinUtil.containHomophoneWord(token, homophones)){
        System.out.println(token);
        tokenCount++;
      }
    }
    System.out.println("多音字字段/不重复字段总数/商品数量："+ count + "/" + originNames.size() + "/" + products.size());
    System.out.println("多音字分词/不重复分词总数/商品数量：" + tokenCount +"/"+ tokenNames.size() + "/" + products.size());
  }

  public static void main(String[] args) {
    GenericXmlApplicationContext context = new GenericXmlApplicationContext();
		context.setValidating(false);
		context.load("classpath:applicationContext.xml");
		context.refresh();
		ProductDao dao = (ProductDao)context.getBean(ProductDao.class);
    dao.testConnectionA();

//    Calendar c = Calendar.getInstance();
//    c.set(2013, 2, 1, 0, 0, 0);
//    System.out.println(c.getTimeInMillis());
  }
}
