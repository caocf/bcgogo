package com.bcgogo.etl.dao;

import com.bcgogo.etl.model.XConfig;
import com.bcgogo.etl.model.mongodb.MongoFactory;
import com.bcgogo.utils.JsonUtil;
import com.bcgogo.utils.StringUtil;
import org.bson.Document;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-8-10
 * Time: 下午3:17
 */
@Component
public class XConfigDao extends BaseDao<XConfig> {

  public String getConfig(String name) {
    if (StringUtil.isEmpty(name)) return null;
    Document filter = new Document("name", name);
    Document document = MongoFactory.instance().getCollection(DocConstant.DOCUMENT_X_CONFIG).find(filter).first();
    XConfig config = document != null ? JsonUtil.jsonToObj(document.toJson(), XConfig.class) : null;
    return config != null ? config.getValue() : null;
  }

  public static void main(String[]args){
    XConfigDao configDao=new XConfigDao();
      String val=configDao.getConfig("DOMAIN_OPEN");
    System.out.println(val);
    System.out.println(val);
  }

}
