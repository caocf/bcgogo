package com.bcgogo.driving.dao;

import com.bcgogo.driving.model.GsmTBoxData;
import com.bcgogo.driving.model.GsmVehicleData;
import com.bcgogo.driving.model.mongodb.MongoFactory;
import com.bcgogo.pojox.api.GsmVehicleDataCondition;
import com.bcgogo.pojox.util.JsonUtil;
import com.bcgogo.pojox.util.StringUtil;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-11-17
 * Time: 上午11:19
 */
@Repository
public class XGsmTBoxDataDao extends XBaseDao<GsmTBoxData> {

  public List<GsmTBoxData> getGsmTBoxData(GsmVehicleDataCondition condition) {
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
      cursor = MongoFactory.instance().getCollection(DocConstant.DOCUMENT_GSM_TBOX_DATA).find(filter).sort(sort).iterator();
    } else {
      cursor = MongoFactory.instance().getCollection(DocConstant.DOCUMENT_GSM_TBOX_DATA).find(filter).iterator();
    }
    List<GsmTBoxData> dataList = new ArrayList<GsmTBoxData>();
    while (cursor.hasNext()) {
      GsmTBoxData data = JsonUtil.jsonToObj(cursor.next().toJson(), GsmTBoxData.class);
      dataList.add(data);
    }
    return dataList;
  }

}
