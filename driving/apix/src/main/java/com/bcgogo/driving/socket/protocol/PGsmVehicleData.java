package com.bcgogo.driving.socket.protocol;

import com.bcgogo.pojox.api.GsmVehicleDataDTO;
import com.bcgogo.pojox.constant.GSMConstant;
import com.bcgogo.pojox.constant.XConstant;

import com.bcgogo.pojox.util.DateUtil;
import com.bcgogo.pojox.util.JsonUtil;
import com.bcgogo.pojox.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

/**
 * 鹏奥达--定时上传.
 * Author: ndong
 * Date: 15-9-17
 * Time: 上午9:43
 */
public class PGsmVehicleData implements Serializable {
  private static final Logger LOG = LoggerFactory.getLogger(PGsmVehicleData.class);

  private String uuid;
  private String appUserNo;
  private String imei;  //设备id
  private String timestamp;//时间戳
  private String date;//日期
  private String lat; //纬度
  private String bddl; //备电电量
  private String lon;   //经度
  private String heading; //速度方向
  private String v_data; //车辆状态
  private String alert_status;//用户报警标识
  private String alert_flag; //报警设置标识
  private String alert_flag_status; //报警设置状态
  private String mil_flag;//里程标识
  private Integer curMil;  //当前里程
  private String a_speed_flag;//加速度标识
  private String a_speed;//加速度值
  private String data_type;//数据类型
  private String country;  //国家代码
  private String supplier; //运营商代码
  private String base_station;//基站号
  private String dist_id;  //小区ID
  private String height;//海拔
  private String extend;//扩展数据
  private String count; //记录号
  private String rdtc;  //故障码
  private Integer wbtm; //水箱温度
  private Integer rpm;  //发动机转速
  private Integer rpf;  //发动机负荷
  private Integer vss;  //车辆速度

  private Integer bOilWear;  //百公里油耗
  private Integer mOilWear;  //每小时油耗
  private Integer rOilMass;   //剩余油量
  private Integer mile;  //里程


  public static void main(String[] args) throws UnsupportedEncodingException {
//    String hexString = "24863600065213552815091531161350C6120440030C000000FFFBDFFFED41300052000000A6630205024E0101CC01451FBD8300000047C20133445566043344556605AA0000000C4E2000000DAA0000001F334455664230303030443344556646334455664C33445566880000000089000000028A000000008B0000000003";
    String hexString = "24863600065209574209101532022120C6119358930E000000FFFBDFFFFD413000520000010D6307035B070101CC0134331CA7038E0047C2010007E100043304000005810400000C0AEA00000D00E800001F02B4000042361D0000447FFF0000465FFF00004C04FF0000880000000089000000008A000000008B000080002C0E";
    PGsmVehicleData data = new PGsmVehicleData(hexString);

    System.out.println(hexString);
    System.out.println(JsonUtil.objectToJson(data));
//    String resp="7E2A48512C323134313130303239392C56342C56312C3230313530393130303833343333236F7E";
//    System.out.println(BinaryUtil.hexString2String(resp));


  }

  public PGsmVehicleData(String hexString) throws UnsupportedEncodingException {
    this.imei = hexString.substring(2, 12);
    this.timestamp = hexString.substring(12, 18);
    this.date = hexString.substring(18, 24);
    String tmp = hexString.substring(24, 32);
    this.lat = String.valueOf(Double.valueOf(tmp.substring(0, 2)) + Double.valueOf(tmp.substring(2, tmp.length())) / Math.pow(10, tmp.length() - 4) / 60);
    this.bddl = hexString.substring(32, 34);
    tmp = hexString.substring(34, 43);
    this.lon = String.valueOf(Double.valueOf(tmp.substring(0, 3)) + Double.valueOf(tmp.substring(3, tmp.length())) / Math.pow(10, tmp.length() - 5) / 60);   //todo 44位
    this.heading = hexString.substring(44, 50);
    this.v_data = hexString.substring(50, 58);
    this.alert_status = hexString.substring(58, 60);
    this.alert_flag = hexString.substring(60, 62);
    this.alert_flag_status = hexString.substring(62, 66);
    this.mil_flag = hexString.substring(66, 68);
//    this.curMil = Integer.parseInt(hexString.substring(68, 76), 16);
    this.a_speed_flag = hexString.substring(76, 78);
    this.a_speed = hexString.substring(78, 86);
    this.data_type = hexString.substring(86, 88);
    this.country = hexString.substring(88, 92);
    this.supplier = hexString.substring(92, 94);
    this.base_station = hexString.substring(94, 98);
    this.dist_id = hexString.substring(98, 102);
    this.height = hexString.substring(102, 106);
    this.extend = hexString.substring(106, hexString.length() - 2);
    this.count = hexString.substring(hexString.length() - 2, hexString.length());
    if ("01".equals(data_type)) {
//      this.rdtc = BinaryUtil.hexString2String(this.extend.substring(8, 16)); //故障代码
      this.rpf = Integer.parseInt(this.extend.substring(18, 20), 16) * 100 / 255; //发动机负荷
      this.wbtm = Integer.parseInt(this.extend.substring(28, 30), 16);
      this.rpm = (Integer.parseInt(this.extend.substring(38, 40), 16) * 256 + Integer.parseInt(this.extend.substring(40, 42), 16)) / 4;  //发动机转速
      this.vss = Integer.parseInt(this.extend.substring(48, 50), 16);

      this.mOilWear = Integer.parseInt(this.extend.substring(108, 116), 16);
      this.bOilWear = Integer.parseInt(this.extend.substring(118, 126), 16);
      this.curMil = Integer.parseInt(this.extend.substring(128, 136), 16);
      this.rOilMass = Integer.parseInt(this.extend.substring(138, 146), 16);
    } else if ("02".equals(data_type)) {
      LOG.info("GPS+故障数据");
    } else if ("03".equals(data_type)) {
      LOG.info("GPS+车身状态数据");
    } else if ("00".equals(data_type)) {
      LOG.info("基本定位数据");
    } else {
      LOG.info("位置定时数据");
    }

  }

  public GsmVehicleDataDTO toGVDataDTO() {
    GsmVehicleDataDTO dataDTO = new GsmVehicleDataDTO();
    dataDTO.setUuid(getUuid());
    dataDTO.setAppUserNo(getAppUserNo());
    dataDTO.setVehicleStatus(GSMConstant.DRIVING);
    dataDTO.setLat(getLat());
    dataDTO.setLon(getLon());
    dataDTO.setCurMil(StringUtil.valueOf(getCurMil()));
    dataDTO.setMile(String.valueOf(getMile()));
    dataDTO.setRdtc(getRdtc());
    dataDTO.setRpm(StringUtil.valueOf(getRpm()));
    dataDTO.setRpf(String.valueOf(getRpf()));
    dataDTO.setVss(StringUtil.valueOf(getVss()));
    dataDTO.setWbtm(String.valueOf(getWbtm()));
    try {
      Long uploadTime = DateUtil.convertDateStringToDateLong("ddMMyyHHmmss", getDate() + getTimestamp());
//      if (uploadTime != null) {
//        Long diffTime = System.currentTimeMillis() - uploadTime;
//        if (diffTime > XConstant.ERROR_DELAY_UPLOAD_TIME || diffTime < XConstant.ERROR_EARLIER_UPLOAD_TIME) {
//          uploadTime = System.currentTimeMillis();
//        }
//      } else {
        uploadTime = System.currentTimeMillis();
//      }
      dataDTO.setUploadTime(uploadTime);
      dataDTO.setUploadServerTime(System.currentTimeMillis());
    } catch (Exception e) {
    }
    dataDTO.setrOilMass(StringUtil.valueOf(getrOilMass()));
    dataDTO.setbOilWear(getbOilWear());
    dataDTO.setmOilWear(getmOilWear());
    dataDTO.setGpsHeading(getHeading());
    dataDTO.setCountry(getCountry());
    dataDTO.setSupplier(getSupplier());
    dataDTO.setBase_station(getBase_station());
    dataDTO.setDist_id(getDist_id());
    return dataDTO;
  }

  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getImei() {
    return imei;
  }

  public void setImei(String imei) {
    this.imei = imei;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getLat() {
    return lat;
  }

  public void setLat(String lat) {
    this.lat = lat;
  }

  public String getBddl() {
    return bddl;
  }

  public void setBddl(String bddl) {
    this.bddl = bddl;
  }

  public String getLon() {
    return lon;
  }

  public void setLon(String lon) {
    this.lon = lon;
  }

  public String getHeading() {
    return heading;
  }

  public void setHeading(String heading) {
    this.heading = heading;
  }

  public String getV_data() {
    return v_data;
  }

  public void setV_data(String v_data) {
    this.v_data = v_data;
  }

  public String getAlert_status() {
    return alert_status;
  }

  public void setAlert_status(String alert_status) {
    this.alert_status = alert_status;
  }

  public String getAlert_flag() {
    return alert_flag;
  }

  public void setAlert_flag(String alert_flag) {
    this.alert_flag = alert_flag;
  }

  public String getAlert_flag_status() {
    return alert_flag_status;
  }

  public void setAlert_flag_status(String alert_flag_status) {
    this.alert_flag_status = alert_flag_status;
  }

  public String getMil_flag() {
    return mil_flag;
  }

  public void setMil_flag(String mil_flag) {
    this.mil_flag = mil_flag;
  }


  public String getA_speed_flag() {
    return a_speed_flag;
  }

  public void setA_speed_flag(String a_speed_flag) {
    this.a_speed_flag = a_speed_flag;
  }

  public String getA_speed() {
    return a_speed;
  }

  public void setA_speed(String a_speed) {
    this.a_speed = a_speed;
  }

  public String getData_type() {
    return data_type;
  }

  public void setData_type(String data_type) {
    this.data_type = data_type;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getSupplier() {
    return supplier;
  }

  public void setSupplier(String supplier) {
    this.supplier = supplier;
  }

  public String getBase_station() {
    return base_station;
  }

  public void setBase_station(String base_station) {
    this.base_station = base_station;
  }

  public String getDist_id() {
    return dist_id;
  }

  public void setDist_id(String dist_id) {
    this.dist_id = dist_id;
  }

  public String getHeight() {
    return height;
  }

  public void setHeight(String height) {
    this.height = height;
  }

  public String getExtend() {
    return extend;
  }

  public void setExtend(String extend) {
    this.extend = extend;
  }

  public String getCount() {
    return count;
  }

  public void setCount(String count) {
    this.count = count;
  }

  public Integer getCurMil() {
    return curMil;
  }

  public void setCurMil(Integer curMil) {
    this.curMil = curMil;
  }

  public String getRdtc() {
    return rdtc;
  }

  public void setRdtc(String rdtc) {
    this.rdtc = rdtc;
  }

  public Integer getWbtm() {
    return wbtm;
  }

  public void setWbtm(Integer wbtm) {
    this.wbtm = wbtm;
  }

  public Integer getRpm() {
    return rpm;
  }

  public void setRpm(Integer rpm) {
    this.rpm = rpm;
  }

  public Integer getRpf() {
    return rpf;
  }

  public void setRpf(Integer rpf) {
    this.rpf = rpf;
  }

  public Integer getVss() {
    return vss;
  }

  public void setVss(Integer vss) {
    this.vss = vss;
  }

  public Integer getbOilWear() {
    return bOilWear;
  }

  public void setbOilWear(Integer bOilWear) {
    this.bOilWear = bOilWear;
  }

  public Integer getmOilWear() {
    return mOilWear;
  }

  public void setmOilWear(Integer mOilWear) {
    this.mOilWear = mOilWear;
  }

  public Integer getrOilMass() {
    return rOilMass;
  }

  public void setrOilMass(Integer rOilMass) {
    this.rOilMass = rOilMass;
  }

  public Integer getMile() {
    return mile;
  }

  public void setMile(Integer mile) {
    this.mile = mile;
  }
}
