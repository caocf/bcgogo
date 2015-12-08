package com.bcgogo.etl.dao;

import com.bcgogo.enums.app.AppUserType;
import com.bcgogo.etl.model.XAppUserLoginInfo;
import com.bcgogo.etl.model.mongodb.MongoFactory;
import com.bcgogo.utils.JsonUtil;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-8-10
 * Time: 上午11:48
 */
@Component
public class XAppUserLoginInfoDao extends BaseDao<XAppUserLoginInfo> {

 public static void main(String[] args){
   String json="{ " +
     "\"_id\" : { \"$oid\" : \"55c8a44b5fd12a4380dfe5c4\" }, " +
     "\"appUserNo\" : \"356824200008005\", " +
     "\"loginTime\" : { \"$numberLong\" : \"1439212601412\" }, " +
     "\"sessionId\" : \"F3YwGFxT1439212600454OkZyCPv8.app\", " +
     "\"sessionCreateTime\" : { \"$numberLong\" : \"1439212601412\" }, " +
     "\"status\" : \"active\", " +
     "\"appUserType\" : \"MIRROR\", " +
     "\"created\" : { \"$numberLong\" : \"1439212607155\" }," +
     " \"last_update\" : { \"$numberLong\" : \"1439212607155\" }" +
     " }";
   XAppUserLoginInfo loginInfo= JsonUtil.jsonToObj(json, XAppUserLoginInfo.class);
   System.out.println("good");
 }



  public void updateSessionId(String sessionId, String id) {
    Document filter = new Document("_id", new ObjectId(id));
    Document update = new Document("sessionId", sessionId);
    MongoFactory.instance().getCollection(DocConstant.DOCUMENT_X_APP_USER_LOGIN_INFO).updateOne(filter, new Document("$set", update));
  }

  public XAppUserLoginInfo getAppUserLoginInfoByUserNo(String appUserNo, AppUserType appUserType) {
    Document filter = new Document("appUserNo", appUserNo).append("appUserType", appUserType.toString());
    Document document = MongoFactory.instance().getCollection(DocConstant.DOCUMENT_X_APP_USER_LOGIN_INFO).find(filter).first();
    return document != null ? JsonUtil.jsonToObj(document.toJson(), XAppUserLoginInfo.class) : null;
  }

  public XAppUserLoginInfo getAppUserLoginInfoBySessionId(String sessionId) {
    Document filter = new Document("sessionId", sessionId);
    Document document = MongoFactory.instance().getCollection(DocConstant.DOCUMENT_X_APP_USER_LOGIN_INFO).find(filter).first();
    return document != null ? JsonUtil.jsonToObj(document.toJson(), XAppUserLoginInfo.class) : null;
  }

}
