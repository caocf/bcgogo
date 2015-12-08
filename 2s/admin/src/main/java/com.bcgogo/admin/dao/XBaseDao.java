package com.bcgogo.admin.dao;

import com.bcgogo.common.Assert;
import com.bcgogo.etl.model.mongodb.*;
import com.bcgogo.utils.JsonUtil;
import com.mongodb.client.FindIterable;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-10-13
 * Time: 下午4:49
 */
public class XBaseDao<T extends XLongIdentifier> {

  public void save(T t) {
    Document doc = Document.parse(JsonUtil.objectToJson(t));
    MongoFactory.instance().getCollection(t.getClass().getSimpleName()).insertOne(doc);
  }


  public T getById(Class<T> clazz, String id) {
    String collectionName = clazz.getSimpleName();
    Document filter = new Document("_id", new ObjectId(id));
    FindIterable<Document> iterable = MongoFactory.instance().getCollection(collectionName).find(filter);
    if (iterable != null && iterable.first() != null) {
      Document document = iterable.first();
      return JsonUtil.jsonToObj(document.toJson(), clazz);
    }
    return null;
  }

  public void update(T t) {
    String id = t.get_id().get$oid();
    Assert.notEmpty(id);
    Document filter = new Document("_id", new ObjectId(id));
    t.beforeUpdate();
    Document update = Document.parse(JsonUtil.objectToJson(t));
    MongoFactory.instance().getCollection(t.getClass().getSimpleName()).updateOne(filter, new Document("$set", update));
  }

  public void saveOrUpdate(T t) {
    if (t.get_id() == null) {
      save(t);
    } else {
      update(t);
    }
  }

  public void delete(T t) {
  }
}
