package com.bcgogo;

import com.bcgogo.common.Assert;
import com.bcgogo.etl.model.mongodb.MongoFactory;
import com.bcgogo.etl.model.mongodb.XLongIdentifier;
import com.bcgogo.utils.JsonUtil;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-8-13
 * Time: 上午11:24
 */
public class BaseMongoMocker<T extends XLongIdentifier> {

  private static MongoDatabase db;

  public static MongoDatabase instance() {
    if (db == null) {
      MongoClientURI uri = new MongoClientURI("mongodb://bcgogow:bcgogo0512@42.121.98.170:27017/?authSource=etl&authMechanism=SCRAM-SHA-1");
      MongoClient mongoClient = new MongoClient(uri);
      db = mongoClient.getDatabase("etl");
    }
    return db;
  }

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
