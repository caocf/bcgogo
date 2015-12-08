package com.bcgogo.etl.dao;

import com.bcgogo.common.Assert;
import com.bcgogo.etl.model.GsmVehicleData;
import com.bcgogo.etl.model.XAppUserLoginInfoTest;
import com.bcgogo.etl.model.XConfig;
import com.bcgogo.etl.model.mongodb.MongoFactory;
import com.bcgogo.etl.model.mongodb.XLongIdentifier;
import com.bcgogo.etl.model.mongodb.XObjectId;
import com.bcgogo.utils.JsonUtil;
import com.bcgogo.utils.PropUtil;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: ndong
 * Date: 15-08-08
 * Time: 下午6:05
 * To change this template use File | Settings | File Templates.
 */
public class BaseDao<T extends XLongIdentifier> {


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

  /*
  public static MongoCollection<Document> getCollection(String col) {
      return instance().getCollection(col);
  }

  //计算条数
  public static List<String> distinct(String col, String key) {
      List<String> list = new ArrayList<String>();
      try {
          List<String> ret = instance().getCollection(col).distinct(key);
          if (ret != null) {
              list.addAll(ret);
          }
      } catch (Exception e) {
          logger.error(e.getMessage());
      }
      return list;
  }

  //计算条数
  public static long count(String col, Bson query) {
      long count = 0;
      try {
          count = instance().getCollection(col).count(query);
      } catch (Exception e) {
          logger.error(e.getMessage());
      }
      return count;
  }

  //删除记录
  public static boolean deleteOne(String col, Bson query) {
      if (col.isEmpty()) {
          logger.error("collection name is null");
          return false;
      }
      try {
          instance().getCollection(col).deleteOne(query);
      } catch (Exception e) {
          logger.error(e.getMessage());
          return false;
      }
      return true;
  }

  //更新记录,返回修改后的新纪录
  public static DBObject findAndModify(String col, Bson query, Bson update) {
      DBObject result = null;
      if (col.isEmpty()) {
          logger.error("collection name is null");
          return result;
      }
      try {//DBObject query,DBObject fields,DBObject sort,boolean remove,DBObject update,boolean returnNew,boolean upsert
          DBObject defaults = new BasicDBObject();
          result = instance().getCollection(col).findAndModify(query, defaults, defaults, false, update, true, false);
      } catch (Exception e) {
          logger.error(e.getMessage());
      }
      return result;
  }

  //更新记录
  public static boolean updateOne(String col, Bson query, Bson update) {
      if (col.isEmpty()) {
          logger.error("collection name is null");
          return false;
      }
      try {
          instance().getCollection(col).updateOne(query, update);
      } catch (Exception e) {
          logger.error(e.getMessage());
          return false;
      }
      return true;
  }

  //更新记录
  public static boolean updateMany(String col, Bson query, Bson update, boolean upsert, boolean multi) {
      if (col.isEmpty()) {
          logger.error("collection name is null");
          return false;
      }
      BasicDBObject set = new BasicDBObject("$set", update);
      try {
          instance().getCollection(col).updateMany(query, set, upsert, multi);
      } catch (Exception e) {
          logger.error(e.getMessage());
          return false;
      }
      return true;
  }

  //更新记录
  public static boolean update(String col, DBObject query, DBObject update, boolean multi) {
      if (col.isEmpty()) {
          logger.error("collection name is null");
          return false;
      }
      BasicDBObject set = new BasicDBObject("$set", update);
      try {
          instance().getCollection(col).update(query, set, false, multi);
      } catch (Exception e) {
          logger.error(e.getMessage());
          return false;
      }
      return true;
  }

  //添加记录
  public static boolean insert(String col, DBObject obj) {
      if (col.isEmpty()) {
          logger.error("collection name is null");
          return false;
      }
      try {
          instance().getCollection(col).insert(obj);
      } catch (Exception e) {
          logger.error(e.getMessage());
          return false;
      }
      return true;
  }

  //分组统计查询
  public static DBObject group(String col, DBObject key, DBObject cond, DBObject initial, String reduce) {
      DBObject obj = null;
      if (col.isEmpty()) {
          logger.error("collection name is null");
          return obj;
      }
      try {
          obj = instance().getCollection(col).group(key, cond, initial, reduce);
      } catch (Exception e) {
          logger.error(e.getMessage());
      }
      return obj;
  }

  //指定条件查询单个
  public static DBObject findOne(String col, DBObject query, DBObject keys, DBObject sort) {
      DBObject obj = null;
      if (col.isEmpty()) {
          logger.error("collection name is null");
          return obj;
      }
      try {
          obj = instance().getCollection(col).findOne(query, keys, sort);
      } catch (Exception e) {
          logger.error(e.getMessage());
      }
      return obj;
  }

  //指定条件查询单个
  public static DBObject findOne(String col, DBObject query, DBObject keys) {
      DBObject obj = null;
      if (col.isEmpty()) {
          logger.error("collection name is null");
          return obj;
      }
      try {
          obj = instance().getCollection(col).findOne(query, keys);
      } catch (Exception e) {
          logger.error(e.getMessage());
      }
      return obj;
  }

  //指定条件查询单个
  public static DBObject findOne(String col, DBObject query) {
      DBObject obj = null;
      if (col.isEmpty()) {
          logger.error("collection name is null");
          return obj;
      }
      try {
          obj = instance().getCollection(col).findOne(query);
      } catch (Exception e) {
          logger.error(e.getMessage());
      }
      return obj;
  }

  //指定条件查询
  public static List<DBObject> find(String col, DBObject query) {
      List<DBObject> list = new ArrayList<DBObject>();
      if (col.isEmpty()) {
          logger.error("collection name is null");
          return list;
      }
      try (DBCursor cursor = instance().getCollection(col).find(query)) {
          while (cursor.hasNext()) {
              list.add(cursor.next());
          }
      } catch (Exception e) {
          logger.error(e.getMessage());
      }
      return list;
  }

  //指定条件查询
  public static List<DBObject> find(String col, DBObject query, DBObject keys) {
      List<DBObject> list = new ArrayList<DBObject>();
      if (col.isEmpty()) {
          logger.error("collection name is null");
          return list;
      }
      try (DBCursor cursor = instance().getCollection(col).find(query, keys)) {
          while (cursor.hasNext()) {
              list.add(cursor.next());
          }
      } catch (Exception e) {
          logger.error(e.getMessage());
      }
      return list;
  }

  //指定条件查询
  public static List<DBObject> find(String col, DBObject query, DBObject keys, DBObject sort) {
      List<DBObject> list = new ArrayList<DBObject>();
      if (col.isEmpty()) {
          logger.error("collection name is null");
          return list;
      }
      try (DBCursor cursor = instance().getCollection(col).find(query, keys).sort(sort)) {
          while (cursor.hasNext()) {
              list.add(cursor.next());
          }
      } catch (Exception e) {
          logger.error(e.getMessage());
      }
      return list;
  }

  //分页查询
  public static List<DBObject> find(String col, DBObject query, int skip, int limit) {
      List<DBObject> list = new ArrayList<DBObject>();
      if (col.isEmpty()) {
          logger.error("collection name is null");
          return list;
      }
      try (DBCursor cursor = instance().getCollection(col).find(query).skip(skip).limit(limit)) {
          while (cursor.hasNext()) {
              list.add(cursor.next());
          }
      } catch (Exception e) {
          logger.error(e.getMessage());
      }
      return list;
  }

  //分页查询
  public static List<DBObject> find(String col, DBObject query, DBObject keys, int skip, int limit) {
      List<DBObject> list = new ArrayList<DBObject>();
      if (col.isEmpty()) {
          logger.error("collection name is null");
          return list;
      }
      try (DBCursor cursor = instance().getCollection(col).find(query, keys).skip(skip).limit(limit)) {
          while (cursor.hasNext()) {
              list.add(cursor.next());
          }
      } catch (Exception e) {
          logger.error(e.getMessage());
      }
      return list;
  }

  //分页查询
  public static List<DBObject> find(String col, DBObject query, DBObject keys, DBObject sort, int skip, int limit) {
      List<DBObject> list = new ArrayList<DBObject>();
      if (col.isEmpty()) {
          logger.error("collection name is null");
          return list;
      }
      try (DBCursor cursor = instance().getCollection(col).find(query, keys).sort(sort).skip(skip).limit(limit)) {
          while (cursor.hasNext()) {
              list.add(cursor.next());
          }
      } catch (Exception e) {
          logger.error(e.getMessage());
      }
      return list;
  }*/


  public static void main(String[] args) throws IOException {
    String id = "55cb17c2fcd8b84068b93334";
    MongoClientURI uri = new MongoClientURI("mongodb://bcgogow:bcgogo0512@42.121.98.170:27017/?authSource=etl&authMechanism=SCRAM-SHA-1");
    MongoClient mongoClient = new MongoClient(uri);
//    MongoClient mongoClient = new MongoClient("42.121.98.170", 27017);
    MongoDatabase db = mongoClient.getDatabase("etl");
    Document filter = new Document("_id", new ObjectId(id));
//    db.getCollection("etl").insertOne(filter);
    Document document = db.getCollection("XAppUserLoginInfo").find(filter).first();
    document.toJson();
    XAppUserLoginInfoTest test = db.getCollection("XAppUserLoginInfo").find(filter,XAppUserLoginInfoTest.class).first();
    System.out.println("finished");

  }


}
