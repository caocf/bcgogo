package com.bcgogo.mocker;

import com.bcgogo.pojox.api.ApiMirrorLoginResponse;
import com.bcgogo.pojox.api.ApiResponse;
import com.bcgogo.pojox.api.VehicleResponse;
import com.bcgogo.pojox.api.response.HttpResponse;
import com.bcgogo.pojox.config.Coordinate;
import com.bcgogo.pojox.constant.GSMConstant;
import com.bcgogo.pojox.enums.app.MessageCode;
import com.bcgogo.pojox.util.DateUtil;
import com.bcgogo.pojox.util.HttpUtils;
import com.bcgogo.pojox.util.JsonUtil;
import com.bcgogo.pojox.util.StringUtil;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 15-10-14
 * Time: 上午9:37
 */
public class MirrorClientMocker {

  public static final Logger LOG = LoggerFactory.getLogger(MirrorClientMocker.class);

  private static String sessionId;
  private static String appUserNo;
  private static String domain_driving;
  private static String imei;
  private static String domain;

  static String model = "sssss";

  static {
    if (StringUtil.isEmpty(model)) {
      domain = "http://192.168.1.248:8080";
      domain_driving = "http://127.0.0.1:8080";
      imei = "864881020017222";
    } else {
      imei = "864881020150805"; //升铺店测试
//      imei = "864881022129389"; //浙CHN196
      domain = "http://221.6.167.67:48080";
      domain_driving = "http://42.121.98.170:8080";
    }
  }

  private static String url_logout = domain + "/api/logout";
  private static String url_gsm_user_qr_code = domain + "/api/gsm/userinfo/qr";


  @BeforeClass
  public static void setUp() throws Exception {
    LOG.info("mirrorLogin...");
    String loginUrl = domain + "/api/mirror/login/" + imei;
    HttpResponse response = HttpUtils.sendGet(loginUrl);
    LOG.info("登录mirrorLogin result:{}", response.getContent());
    ApiMirrorLoginResponse apiResponse = (ApiMirrorLoginResponse) JsonUtil.jsonToObject(response.getContent(), ApiMirrorLoginResponse.class);
    appUserNo = apiResponse.getAppUserDTO().getUserNo();
    sessionId = response.getCookie();
    Assert.assertNotNull(sessionId);
    Assert.assertEquals(MessageCode.LOGIN_SUCCESS.getCode(), apiResponse.getMsgCode());
  }


  @AfterClass
  public static void after() throws Exception {
    LOG.info("logout");
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("userNo", appUserNo);
    HttpResponse response = sendPUT(url_logout, parameters);
  }

  /**
   * 获取后视镜二维码
   *
   * @throws IOException
   */
  @Test
  public void getGsmUserQRCode() throws IOException {
    HttpResponse response = sendGET(url_gsm_user_qr_code);
    ApiResponse apiResponse = (ApiResponse) JsonUtil.jsonToObject(response.getContent(), ApiResponse.class);
    LOG.info("url_gsm_user_qr_code result:{}", response.getContent());
    Assert.assertEquals(MessageCode.SUCCESS.getCode(), apiResponse.getMsgCode());
  }



  /**
   * 上传车况信息到apix
   *
   * @throws java.io.IOException
   */
  @Test
  public void uploadGsmVehicleData() throws IOException, InterruptedException {
    LOG.info("uploadGsmVehicleData");
    String[] placeNotes = placeNoteSrc.split("\\|");
    Coordinate[] coordinates = new Coordinate[placeNotes.length];
    for (int i = 0; i < placeNotes.length; i++) {
      String placeNote = placeNotes[i];
      Coordinate coordinate = new Coordinate(placeNote.split(",")[0], placeNote.split(",")[1]);
      coordinates[i] = coordinate;
    }
    //点火
    Map<String, Object> dataMap = getBaseGsmVehicleData();
    dataMap.put("vehicleStatus", GSMConstant.FIRE_UP);
    dataMap.put("lon", coordinates[0].getLng());
    dataMap.put("lat", coordinates[0].getLat());
    dataMap.put("curMil", "142");
    dataMap.put("rOilMass", "31");
    String url_gsm_vehicle_data = domain_driving + "/apix/gsm/vehicle/data";
    HttpResponse response =sendPUT(url_gsm_vehicle_data, dataMap);
    ApiResponse apiResponse = (VehicleResponse) JsonUtil.jsonToObject(response.getContent(), VehicleResponse.class);
    Assert.assertEquals(MessageCode.VEHICLE_DATA_SUCCESS.getCode(), apiResponse.getMsgCode());
    //行驶中
    for (int i = 1; i < (coordinates.length - 1); i++) {
      Coordinate coordinate = coordinates[i];
      dataMap.put("vehicleStatus", GSMConstant.DRIVING);
      dataMap.put("lon", coordinates[i].getLng());
      dataMap.put("lat", coordinates[i].getLat());
      dataMap.put("uploadTime", String.valueOf(System.currentTimeMillis()));
      sendPUT(url_gsm_vehicle_data, dataMap);
    }
    //熄火
    dataMap.put("vehicleStatus", GSMConstant.CUTOFF);
    dataMap.put("lon", coordinates[coordinates.length - 1].getLng());
    dataMap.put("lat", coordinates[coordinates.length - 1].getLat());
    dataMap.put("curMil", "151");
    String endTime = String.valueOf(System.currentTimeMillis() + DateUtil.HOUR_MILLION_SECONDS + DateUtil.HOUR_MILLION_SECONDS / 2);
    dataMap.put("uploadTime", endTime);
    dataMap.put("rOilMass", "43.5");
    dataMap.put("rdtc", "P0205");
    response = sendPUT(url_gsm_vehicle_data, dataMap);
    //熄火后，上传的信息
    Map<String, Object> tDataMap = new HashMap<String, Object>();
    tDataMap.put("vehicleStatus", GSMConstant.AFTER_CUTOFF);
    tDataMap.put("door", "10000");
    tDataMap.put("uuid", dataMap.get("uuid"));
    response = sendPUT(url_gsm_vehicle_data, tDataMap);
    LOG.info("车辆信息 getAppVehicle result:{}", response.getContent());
  }


  private Map<String, Object> getBaseGsmVehicleData() {
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("uuid", UUID.randomUUID().toString());
    parameters.put("curMil", "200");
    parameters.put("door", "01000");
    parameters.put("uploadTime", String.valueOf(System.currentTimeMillis()));
    parameters.put("dttpe", "0");

    parameters.put("spwr", "12.53");
    parameters.put("rpm", "2000");
    parameters.put("vss", "90");
    parameters.put("wbtm", "100");

    parameters.put("voltageForOxygenSensor", "22");
    parameters.put("throttlePosition", "22");


    parameters.put("lon", "120.741578");
    parameters.put("lonDir", "E");
    parameters.put("lat", "31.298548");
    parameters.put("latDir", "N");
    parameters.put("gpsSpeed", "");
    parameters.put("gpsHeading", "000");
    parameters.put("gpsDataValidity", "A");
    parameters.put("vehicleStatus", GSMConstant.DRIVING);

    parameters.put("aOilWear", "9");
    parameters.put("rOilMass", "30");
    parameters.put("gx", "20");
    parameters.put("gy", "25");
    parameters.put("gz", "23");
    return parameters;
  }


  private static HttpResponse sendPUT(String urlString, Map<String, Object> parameters) throws IOException {
    Map<String, String> propertys = new HashMap<String, String>();
    propertys.put("Cookie", sessionId);
    return HttpUtils.sendPUT(urlString, parameters, propertys);
  }

  private HttpResponse sendGET(String urlString) throws IOException {
     Map<String, String> propertys = new HashMap<String, String>();
     propertys.put("Cookie", sessionId);
     return HttpUtils.sendGet(urlString, null, propertys);
   }

  String placeNoteSrc = "31.298548,120.741578,1397220562000|31.299512,120.741570,1397220572000|31.300488,120.741557,1397220582000|31.300603,120.741537,1397220584000|31.300702,120.741515,1397220586000|31.300765,120.741505,1397220588000|31.300930,120.741555,1397220597000|31.301017,120.741565,1397220599000|31.302283,120.741585,1397220615000|31.305992,120.740848,1397220645000|31.309207,120.739943,1397220675000|31.310338,120.739957,1397220705000|31.310340,120.739952,1397220735000|31.311153,120.739930,1397220765000|31.314243,120.739892,1397220795000|31.316888,120.739845,1397220825000|31.320840,120.739752,1397220855000|31.321165,120.739742,1397220858000|31.321790,120.739710,1397220865000|31.321913,120.739692,1397220868000|31.321938,120.739693,1397220871000|31.321888,120.739740,1397220885000|31.321892,120.739740,1397220914000|31.321922,120.739735,1397220915000|31.322252,120.739737,1397220921000|31.322725,120.739723,1397220926000|31.325275,120.739692,1397220945000|31.327270,120.739637,1397220975000|31.328542,120.739678,1397221005000|31.329593,120.742320,1397221035000|31.330182,120.747333,1397221065000|31.330623,120.751282,1397221095000|31.330618,120.751320,1397221125000|31.330607,120.751407,1397221155000|31.330918,120.754340,1397221177000|31.331068,120.755793,1397221185000|31.331500,120.759808,1397221211000|31.331505,120.759843,1397221215000|31.331508,120.759873,1397221227000|31.331677,120.761520,1397221245000|31.332162,120.766105,1397221275000|31.332687,120.770952,1397221305000|31.333065,120.774278,1397221335000|31.333682,120.780080,1397221365000|31.334092,120.783998,1397221395000|31.334227,120.785270,1397221425000|31.334770,120.788588,1397221445000|31.335070,120.790193,1397221455000|31.335248,120.791162,1397221485000|31.335232,120.791157,1397221515000|31.335420,120.792298,1397221545000|31.336275,120.797518,1397221575000|31.337177,120.802787,1397221605000|31.337497,120.806417,1397221635000|31.336660,120.804210,1397221665000|31.337903,120.802873,1397221695000|31.341393,120.801798,1397221725000|31.345762,120.800380,1397221755000|31.348947,120.799358,1397221785000|31.350855,120.798583,1397221815000|31.354127,120.797527,1397221845000|31.356475,120.796895,1397221875000|31.357428,120.796633,1397221905000|31.357505,120.796600,1397221935000|31.357940,120.796497,1397221965000|31.358272,120.796377,1397221995000|31.358317,120.796343,1397222025000|31.358407,120.796318,1397222039000|31.358500,120.796288,1397222042000|31.358895,120.796162,1397222055000|31.358955,120.796138,1397222061000|31.359025,120.796123,1397222085000|31.359030,120.796147,1397222115000|31.359487,120.796000,1397222145000|31.361550,120.795355,1397222175000|31.363130,120.795995,1397222205000|31.363488,120.799033,1397222235000|31.363525,120.798938,1397222265000|31.363558,120.798903,1397222295000|31.363518,120.798900,1397222315000|31.363517,120.798900,1397222316000|31.363513,120.798902,1397222317000|31.363512,120.798902,1397222318000|31.363515,120.798902,1397222319000|31.363520,120.798898,1397222320000|31.363527,120.798903,1397222321000|31.363525,120.798905,1397222322000|31.363522,120.798905,1397222323000|31.363522,120.798908,1397222324000|31.363523,120.798907,1397222325000|31.363518,120.798893,1397222349000|31.363517,120.798895,1397222350000|31.363515,120.798895,1397222351000|31.363513,120.798893,1397222352000|31.363512,120.798893,1397222353000|31.363512,120.798893,1397222354000|31.363510,120.798893,1397222355000|31.363510,120.798893,1397222356000|31.363508,120.798893,1397222357000|31.363508,120.798893,1397222358000|31.363507,120.798893,1397222359000|31.363507,120.798892,1397222360000|31.363507,120.798892,1397222361000|31.363507,120.798892,1397222362000|31.363505,120.798892,1397222363000|31.363505,120.798892,1397222364000|31.363505,120.798890,1397222365000|31.363503,120.798892,1397222366000|31.363490,120.798890,1397222377000|31.363488,120.798890,1397222378000|31.363488,120.798888,1397222379000|31.363488,120.798888,1397222380000|31.363487,120.798888,1397222381000|31.363487,120.798888,1397222382000|31.363485,120.798888,1397222383000|31.363485,120.798888,1397222384000|31.363487,120.798887,1397222385000|31.363487,120.798887,1397222386000|31.363488,120.798887,1397222387000|31.363488,120.798885,1397222388000|31.363490,120.798885,1397222389000|31.363492,120.798883,1397222390000|31.363492,120.798883,1397222391000|31.363493,120.798883,1397222392000|31.363493,120.798883,1397222393000|31.363495,120.798883,1397222394000|31.363495,120.798883,1397222395000|31.363497,120.798883,1397222396000|31.363497,120.798883,1397222397000|31.363497,120.798883,1397222426000|31.363497,120.798883,1397222456000|31.363497,120.798883,1397222486000|31.363497,120.798883,1397222516000|31.363497,120.798883,1397222546000|31.363497,120.798883,1397222576000|31.363497,120.798883,1397222606000|31.363497,120.798883,1397222636000|" +
    "31.363497,120.798883,1397222666000|31.363497,120.798883,1397222690000|31.363497,120.798883,1397222690000|";



  @Test
  public void toBe() throws IOException {
    appUserNo="de7a0773c246a8284d62b1d9a442e760";
    String url_gsm_vehicle_data = (domain_driving + "/driving/toBe");
     Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("uuid", UUID.randomUUID().toString());
    HttpResponse httpResponse = HttpUtils.sendPUT(url_gsm_vehicle_data,parameters);
    LOG.info("result:{}", httpResponse.getContent());
  }



}
