package com.bcgogo.etl.model;

import com.bcgogo.api.XAppUserLoginInfoDTO;
import com.bcgogo.enums.app.AppPlatform;
import com.bcgogo.enums.app.AppUserType;
import com.bcgogo.enums.app.ImageVersion;
import com.bcgogo.enums.user.Status;
import com.bcgogo.etl.model.mongodb.XNumberLong;
import com.bcgogo.utils.NumberUtil;
import org.bson.BsonInt64;
import org.bson.BsonObjectId;
import org.bson.BsonTimestamp;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-8-12
 * Time: 下午5:34
 */
public class XAppUserLoginInfoTest {
   private BsonObjectId _id;
  private BsonInt64 lastUpdate;

  public BsonObjectId get_id() {
    return _id;
  }

  public void set_id(BsonObjectId _id) {
    this._id = _id;
  }

  public BsonInt64 getLastUpdate() {
    return lastUpdate;
  }

  public void setLastUpdate(BsonInt64 lastUpdate) {
    this.lastUpdate = lastUpdate;
  }
}
