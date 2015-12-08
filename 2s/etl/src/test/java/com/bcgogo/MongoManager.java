package com.bcgogo;

import com.bcgogo.api.GsmVehicleDataCondition;
import com.bcgogo.constant.GSMConstant;
import com.bcgogo.etl.dao.DocConstant;
import com.bcgogo.etl.model.GsmVehicleData;
import com.bcgogo.etl.model.mongodb.MongoFactory;
import com.bcgogo.etl.model.mongodb.XNumberLong;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.JsonUtil;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;
import com.mongodb.BasicDBList;
import com.mongodb.QueryOperators;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-8-13
 * Time: 上午11:19
 */
public class MongoManager {
  public static final Logger LOG = LoggerFactory.getLogger(MongoManager.class);

  //  private static String appUserNo = "356824200008005";
//  private static String appUserNo = "dcf6c34822fd852ea1a5ff8cfed4c014";      //小邓
  private static String appUserNo = "201b91988ab1996243855ccbbfd1f996";    //浙江

  @BeforeClass
  public static void setUp() throws Exception {

  }


  @AfterClass
  public static void after() throws Exception {

  }

  @Test
  public void saveGsmVehicleData() {
    GsmVehicleData data = new GsmVehicleData();
//    data.setAppUserNo("asdfsddddddddddddddddddddddddd");
    data.setUploadTime(new XNumberLong(1382505918566L));
    BaseMongoMocker<GsmVehicleData> mongoMocker = new BaseMongoMocker<GsmVehicleData>();
    mongoMocker.save(data);
  }

  @Test
  public void getLastGsmVehicleDataList() {
    Document filter = new Document("appUserNo", appUserNo);
    filter.append("vehicleStatus", new Document().append(QueryOperators.NE, GSMConstant.AFTER_CUTOFF));
    Document sort = new Document("uploadTime", -1);
    MongoCursor<Document> cursor = BaseMongoMocker.instance().getCollection(DocConstant.DOCUMENT_GSM_VEHICLE_DATA).find(filter).sort(sort).limit(1).iterator();
    printData(cursor);
  }

  @Test
  public void getLastGsmVehicleData() {
    GsmVehicleData data = getLastGsmVehicleData(appUserNo, null);
    LOG.info("data:{}", JsonUtil.objectToJson(data));
  }


  private GsmVehicleData getLastGsmVehicleData(String appUserNo, String imei) {
    if (StringUtil.isEmpty(appUserNo) && StringUtil.isEmpty(imei)) {
      return null;
    }
    Document filter = new Document();
    if (StringUtil.isNotEmpty(appUserNo)) {
      filter.append("appUserNo",appUserNo);
    }
    if (StringUtil.isNotEmpty(imei)) {
      filter.append("imei", "imei");
    }
    BasicDBList basicDBList = new BasicDBList();
    basicDBList.add(GSMConstant.FIRE_UP);
    basicDBList.add(GSMConstant.CUTOFF);
    basicDBList.add(GSMConstant.DRIVING);
//    filter.append("vehicleStatus", new Document().append(QueryOperators.IN, basicDBList));
//     filter.append("vehicleStatus",new Document().append(QueryOperators.NE, GSMConstant.AFTER_CUTOFF));
    Document sort = new Document("uploadTime", -1);
    Document document = BaseMongoMocker.instance().getCollection(DocConstant.DOCUMENT_GSM_VEHICLE_DATA).find(filter).sort(sort).first();
    if (document == null) return null;
    return JsonUtil.jsonToObj(document.toJson(), GsmVehicleData.class);
  }

  @Test
  public void queryGsmVehicleDataByAppUserNo() {
    GsmVehicleDataCondition condition = new GsmVehicleDataCondition();
    condition.setAppUserNo(appUserNo);
    condition.setLimit(50);
    queryGsmVehicleData(condition);
    LOG.info("getLastGsmVehicleData finish");

  }


  private void queryGsmVehicleData(GsmVehicleDataCondition condition) {
    Document filter = new Document();
    if (StringUtil.isNotEmpty(condition.getAppUserNo())) {
      filter.append("appUserNo", condition.getAppUserNo());
    }
    if (StringUtil.isNotEmpty(condition.getUuid())) {
      filter.append("uuid", condition.getUuid());
    }
    Document sort = new Document("uploadTime", -1);
    MongoCursor<Document> cursor = BaseMongoMocker.instance().getCollection(DocConstant.DOCUMENT_GSM_VEHICLE_DATA).find(filter).sort(sort).limit(condition.getLimit()).iterator();
    printData(cursor);
    LOG.info("getLastGsmVehicleData finish");

  }

  private void printData(MongoCursor<Document> cursor) {
    StringBuilder sb = new StringBuilder();
    sb
      .append("|")
      .append("id").append(" | ")
      .append("uuid").append(" | ")
//      .append("appUserNo").append("                             ")
      .append("上传时间").append(" | ")
      .append("上传服务器时间").append(" | ")
      .append("补报").append(" | ")
      .append("车况").append(" | ")
      .append("当前里程").append(" | ")
      .append("故障").append(" | ")
      .append("lon").append(" | ")
      .append("lat").append(" | ")
      .append("\n");
    while (cursor.hasNext()) {
      GsmVehicleData data = JsonUtil.jsonToObj(cursor.next().toJson(), GsmVehicleData.class);
      String uploadTime = DateUtil.convertDateLongToString(NumberUtil.longValue(data.getUploadTime().get$numberLong()), DateUtil.ALL);
      String uploadServerTime = DateUtil.convertDateLongToString(NumberUtil.longValue(data.getUploadServerTime().get$numberLong()), DateUtil.ALL);
      sb
        .append("|")
        .append(data.get_id().get$oid()).append(" | ")
        .append(data.getUuid()).append(" | ")
//        .append(data.getAppUserNo()).append(" ")
        .append(uploadTime).append(" | ")
        .append(uploadServerTime).append(" | ")
        .append(data.getDttpe()).append(" | ")
        .append(data.getVehicleStatus()).append(" | ")
        .append(data.getCurMil()).append(" | ")
        .append(data.getRdtc()).append(" | ")
        .append(data.getLon()).append(" | ")
        .append(data.getLat()).append(" | ")
        .append("\n");
    }
    System.out.println(sb.toString());
  }


}
