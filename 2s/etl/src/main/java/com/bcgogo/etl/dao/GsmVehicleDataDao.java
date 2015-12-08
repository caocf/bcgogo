package com.bcgogo.etl.dao;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-8-7
 * Time: 上午9:13
 */

import com.bcgogo.api.GsmVehicleDataCondition;
import com.bcgogo.constant.GSMConstant;
import com.bcgogo.etl.model.EtlDaoManager;
import com.bcgogo.etl.model.GsmVehicleData;
import com.bcgogo.etl.model.IllegalCity;
import com.bcgogo.etl.model.mongodb.MongoFactory;
import com.bcgogo.utils.JsonUtil;
import com.bcgogo.utils.StringUtil;
import com.mongodb.BasicDBList;
import com.mongodb.QueryOperators;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GsmVehicleDataDao extends BaseDao<GsmVehicleData> {
  @Autowired
  private EtlDaoManager etlDaoManager;

  public List<GsmVehicleData> getGsmVehicleDataDTO(GsmVehicleDataCondition condition) {
    Document filter = new Document();
    if (StringUtil.isNotEmpty(condition.getUuid())) {
      filter.append("uuid", condition.getUuid());
    }
    if (StringUtil.isNotEmpty(condition.getImei())) {
      filter.append("imei", condition.getImei());
    }
    if (StringUtil.isNotEmpty(condition.getVehicleStatus())) {
      filter.append("vehicleStatus", condition.getVehicleStatus());
    }
    MongoCursor<Document> cursor = null;
    if (StringUtil.isNotEmpty(condition.getOrderBy())) {
      String[] orderBy = condition.getOrderBy().split(",");
      Document sort = new Document(orderBy[0], "desc".equals(orderBy[1]) ? -1 : 1);
      cursor = MongoFactory.instance().getCollection(DocConstant.DOCUMENT_GSM_VEHICLE_DATA).find(filter).sort(sort).iterator();
    } else {
      cursor = MongoFactory.instance().getCollection(DocConstant.DOCUMENT_GSM_VEHICLE_DATA).find(filter).iterator();
    }
    List<GsmVehicleData> dataList = new ArrayList<GsmVehicleData>();
    while (cursor.hasNext()) {
      GsmVehicleData data = JsonUtil.jsonToObj(cursor.next().toJson(), GsmVehicleData.class);
      dataList.add(data);
    }
    return dataList;
  }

  public GsmVehicleData getLastGsmVehicleData(String appUserNo, String imei) {
    if (StringUtil.isEmpty(appUserNo) && StringUtil.isEmpty(imei)) {
      return null;
    }
    Document filter = new Document();
    if (StringUtil.isNotEmpty(appUserNo)) {
      filter.append("appUserNo", appUserNo);
    }
    if (StringUtil.isNotEmpty(imei)) {
      filter.append("imei", imei);
    }
    BasicDBList basicDBList = new BasicDBList();
    basicDBList.add(GSMConstant.FIRE_UP);
    basicDBList.add(GSMConstant.CUTOFF);
    basicDBList.add(GSMConstant.DRIVING);
    filter.append("vehicleStatus", new Document().append(QueryOperators.IN, basicDBList));
//     filter.append("vehicleStatus",new Document().append(QueryOperators.NE, GSMConstant.AFTER_CUTOFF));
    Document sort = new Document("uploadTime", -1);
    Document document = MongoFactory.instance().getCollection(DocConstant.DOCUMENT_GSM_VEHICLE_DATA).find(filter).sort(sort).first();
    if (document == null) return null;
    return JsonUtil.jsonToObj(document.toJson(), GsmVehicleData.class);
  }

  public GsmVehicleData getGsmVehicleDataByUUidAndUpdateTime(String uuid, long upLoadTime) {
    Document filter = new Document("uuid", uuid);
//    filter.append("uploadTime",new Document().append(QueryOperators.LTE, upLoadTime));
    Document sort = new Document("uploadTime", -1);
    Document document = MongoFactory.instance().getCollection(DocConstant.DOCUMENT_GSM_VEHICLE_DATA).find(filter).sort(sort).first();
    if (document == null) return null;
    return JsonUtil.jsonToObj(document.toJson(), GsmVehicleData.class);
  }


  public IllegalCity getIllegalCityByAppUserNo(String appUserNo) {

    Document filter = new Document("appUserNo", appUserNo);
//    Document sort = new Document("uploadTime", -1);
    Document document = MongoFactory.instance().getCollection(DocConstant.DOCUMENT_ILLEGAL_CITY).find(filter).first();
    if (document == null) return null;
    return JsonUtil.jsonToObj(document.toJson(), IllegalCity.class);
  }


  public List<GsmVehicleData> getGsmVehicleDataByGpsCityStatus() {
    Document filter = new Document();
    filter.append("gpsCityStatus", "UN_HANDLE");
    MongoCursor<Document> cursor = MongoFactory.instance().getCollection(DocConstant.DOCUMENT_GSM_VEHICLE_DATA).find(filter).iterator();
    List<GsmVehicleData> dataList = new ArrayList<GsmVehicleData>();
    while (cursor.hasNext()) {
      GsmVehicleData data = JsonUtil.jsonToObj(cursor.next().toJson(), GsmVehicleData.class);
      dataList.add(data);
    }
    return dataList;
  }


}
