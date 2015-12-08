package com.bcgogo.driving.dao;

import com.bcgogo.driving.model.GsmVehicleData;
import com.bcgogo.driving.model.mongodb.MongoFactory;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.util.JSON;
import org.bson.Document;

/**
 * Created by Luffy.Liu on 2015/8/7.
 */
public class XGsmDriveLogDao extends XBaseDao<GsmVehicleData>{
    //添加点火信号
    public boolean addAccOnData(GsmVehicleData dt){
        Document filter = new Document("uuid",dt.getUuid());
        Document update = new Document("accOn",Document.parse(JSON.serialize(dt))).append("appUserNo",dt.getAppUserNo())
                .append("uuid",dt.getUuid());
        UpdateOptions opts = new UpdateOptions();
        opts.upsert(true);
        UpdateResult res = MongoFactory.instance().getCollection(DocConstant.DOCUMENT_GSM_DRIVE_LOG).updateOne(filter,update,opts);
        if (res.wasAcknowledged()){
            return true;
        }
        return false;
    }
    //添加熄火信号
    public boolean addAccOffData(GsmVehicleData dt){
        Document filter = new Document("uuid",dt.getUuid());
        Document update = new Document("accOff",Document.parse(JSON.serialize(dt)));
        update.append("appUserNo", dt.getAppUserNo()).append("uuid", dt.getUuid());
        UpdateOptions opts = new UpdateOptions();
        opts.upsert(true);
        UpdateResult res =  MongoFactory.instance().getCollection(DocConstant.DOCUMENT_GSM_DRIVE_LOG).updateOne(filter,update,opts);
        if (res.wasAcknowledged()){
            return true;
        }
        return false;
    }
    //更新最新一条记录
    public boolean updateLastData(GsmVehicleData dt){
        Document filter = new Document("uuid",dt.getUuid());
        Document update = new Document("lastData",Document.parse(JSON.serialize(dt)));
        update.append("appUserNo", dt.getAppUserNo())
                .append("uuid", dt.getUuid()).append("lastTime", System.currentTimeMillis());;
        UpdateOptions opts = new UpdateOptions();
        opts.upsert(true);
        UpdateResult res = MongoFactory.instance().getCollection(DocConstant.DOCUMENT_GSM_DRIVE_LOG).updateOne(filter,update,opts);
        if (res.wasAcknowledged()){
            return true;
        }
        return false;
    }

}
