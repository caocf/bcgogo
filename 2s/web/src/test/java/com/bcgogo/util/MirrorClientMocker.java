package com.bcgogo.util;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.api.ApiTrafficPackage;
import com.bcgogo.api.PFileBlock;
import com.bcgogo.api.gsm.GSMRegisterDTO;
import com.bcgogo.api.response.*;
import com.bcgogo.config.util.AppConstant;
import com.bcgogo.constant.GSMConstant;
import com.bcgogo.enums.app.MessageCode;
import com.bcgogo.etl.common.XConstant;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.Coordinate;
import com.bcgogo.user.service.app.IAppUserService;
import com.bcgogo.utils.*;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * 后视镜客户端
 * Author: ndong
 * Date: 2015-4-9
 * Time: 14:15
 */
public class MirrorClientMocker {
  public static final Logger LOG = LoggerFactory.getLogger(MirrorClientMocker.class);


  private static String sessionId;
  private static String appUserNo;
  private static String imei;
  private static String domain;
  private static String domain_apix;

  static String model = "";

  static {
    if (StringUtil.isEmpty(model)) {
      domain = "http://192.168.1.248:8080";
      domain_apix = "http://192.168.1.248:8080";
//      imei = "864881020017222";
      imei = "006824200000061";
    } else {
//        imei = "864881022133399" ; //debe09b5d99b152d7e1de3c8072beda9
        imei = "864881022131054" ;
      domain = "http://221.6.167.67:48080";
      domain_apix = "http://42.121.98.170:8080";
    }
  }

  private static String url_product_list = domain + "/api/product/list";;
  private static String url_get_app_vehicle = domain + "/api/vehicle/gsmUserGetAppVehicle";
  private static String url_get_driveLog_detail = domain + "/api/driveLog/mirror/detail/";
  private static String url_save_gsm_vehicle = domain + "/api/vehicle/saveGsmVehicle";
  private static String url_upload_file = domain + "/api/vehicle/impact/uploadVideo";
  private static String url_upload_init = domain + "/api/vehicle/impact/uploadInit";
  private static String url_video_list = domain + "/api/vehicle/impact/videoList";
  private static String url_impact_collect = domain + "/api/vehicle/impact/collect";
  private static String url_logout = domain + "/api/logout";
  private static String url_vehicle_violate_public = domain + "/api/violateRegulations/queryVehicleViolateRegulation/city/JS_NT/hphm/èE552UQ/hpzl/02/engineno/27695230728609/classno/WDDLJ5FB9EA131371/registno/NULL";
  private static String url_vehicle_violate = domain + "/api/violateRegulations/mirror";
  private static String url_vehicle_violate_city = domain + "/api/violateRegulations/mirror/area/list";
  private static String url_get_violate_regulations_city = domain + "/api/violateRegulations/juhe/condition/NULL/NULL/{juheCityCodes}";
  private static String url_get_brand_model_by_keywords = domain + "/api/vehicle/brandModel/NULL/{type}/{brandId}/v2";
  private static String url_bcgogo_new_version = domain + "/api/bcgogoNewVersion/platform/WINCE/appVersion/1.0.4/platformVersion/NULL/mobileModel/NULL";
  private static String url_gsm_user_qr_download = domain + "/api/gsm/userinfo/qr/download";

  private static String url_wx_user_send = domain + "/web/mirror/wx/msg/send";
  private static String url_rescue_sos = domain + "/api/vehicle/rescue/sos";


  @BeforeClass
  public static void setUp() throws Exception {
    LOG.info("mirrorLogin...");
    String loginUrl = domain + "/api/plat/login/" + imei;
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

  @Test
  public void tempTest() throws IOException {
    String url = domain + "/api/register/gsm/register";
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("mobile", "13646203695");
    HttpResponse response = HttpUtils.sendPUT(url, parameters);
    LOG.info("content:{}", JsonUtil.objectToJson(response));
  }


  //  @Test
  public void gsmAllocateAppUser() {
    String imei = EncryptionUtil.computeMD5Improved(UUID.randomUUID().toString());
    IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
    GSMRegisterDTO gsmRegisterDTO = new GSMRegisterDTO();
    gsmRegisterDTO.setImei(imei);
    appUserService.gsmAllocateAppUser(gsmRegisterDTO);
  }



  /**
   * 车辆信息
   *
   * @throws IOException
   */
  @Test
  public void getAppVehicle() throws IOException {
    HttpResponse response = sendGET(url_get_app_vehicle);
    ApiResponse apiResponse = (ApiResponse) JsonUtil.jsonToObject(response.getContent(), ApiResponse.class);
    LOG.info("车辆信息 getAppVehicle result:{}", response.getContent());
    Assert.assertEquals(MessageCode.SINGLE_APP_VEHICLE_SUCCESS.getCode(), apiResponse.getMsgCode());
  }

  @Test
   public void productList() throws IOException {
     HttpResponse response = sendGET(url_product_list);
    ApiTrafficPackage apiResponse = (ApiTrafficPackage) JsonUtil.jsonToObject(response.getContent(), ApiTrafficPackage.class);
     LOG.info("车辆信息 getAppVehicle cookie:{}", response.getCookie());
     LOG.info("车辆信息 getAppVehicle result:{}", response.getContent());
     Assert.assertEquals(MessageCode.APP_TRAFFIC_SUCCESS.getCode(), apiResponse.getMsgCode());
   }


  /**
   * 下载行车日志详情
   *
   * @throws IOException
   */
  @Test
  public void getDriveLogDetail() throws IOException {
    url_get_driveLog_detail += 10000010200159917L;
    HttpResponse response = sendGET(url_get_driveLog_detail);
    ApiResponse apiResponse = (ApiResponse) JsonUtil.jsonToObject(response.getContent(), ApiResponse.class);
    LOG.info("车辆信息 getAppVehicle result:{}", response.getContent());
    Assert.assertEquals(MessageCode.DRIVE_LOG_DETAIL_SUCCESS.getCode(), apiResponse.getMsgCode());
  }

  /**
   * 删除行车日志
   *
   * @throws IOException
   */
//  @Test
//  public void deleteDriveLogDetail() throws IOException {
//    url_delete_driveLog += 10000010193519257L;
//    HttpResponse response = sendGET(url_delete_driveLog);
//    ApiResponse apiResponse = (ApiResponse) JsonUtil.jsonToObject(response.getContent(), ApiResponse.class);
//    LOG.info("车辆信息 getAppVehicle result:{}", response.getContent());
//    Assert.assertEquals(MessageCode.SUCCESS.getCode(), apiResponse.getMsgCode());
//  }
  @Test
  public void getViolateRegulationsCity() throws IOException {
    url_get_violate_regulations_city = url_get_violate_regulations_city.replace("{juheCityCodes}", "NULL");
    HttpResponse response = sendGET(url_get_violate_regulations_city);
    ApiResultResponse apiResponse = (ApiResultResponse) JsonUtil.jsonToObject(response.getContent(), ApiResultResponse.class);
    LOG.info("车辆信息 getAppVehicle result:{}", response.getContent());
    Assert.assertEquals(MessageCode.JUHE_VIOLATE_CONDITION_REGULATIONS_SEARCH_SUCCESS.getCode(), apiResponse.getMsgCode());
  }

  /**
   * 品牌下拉
   *
   * @throws IOException
   */
  @Test
  public void getBrandModelByKeywords() throws IOException {
    //车牌
    String url = url_get_brand_model_by_keywords.replace("{type}", "brand").replace("{brandId}", "NULL");
    HttpResponse response = sendGET(url);
    VehicleResponse apiResponse = (VehicleResponse) JsonUtil.jsonToObject(response.getContent(), VehicleResponse.class);
    LOG.info("车辆品牌信息 getAppVehicle result:{}", response.getContent());
    Assert.assertEquals(MessageCode.APP_VEHICLE_BRAND_MODEL_KEYWORD_SUCCESS.getCode(), apiResponse.getMsgCode());
    //车型
    Long brandId = 10000010039L;
    url = url_get_brand_model_by_keywords.replace("{type}", "NULL").replace("{brandId}", StringUtil.valueOf(brandId));
    response = sendGET(url);
    apiResponse = (VehicleResponse) JsonUtil.jsonToObject(response.getContent(), VehicleResponse.class);
    LOG.info("车型信息 getAppVehicle result:{}", response.getContent());
    Assert.assertEquals(MessageCode.APP_VEHICLE_BRAND_MODEL_KEYWORD_SUCCESS.getCode(), apiResponse.getMsgCode());

  }

  /**
   * 车辆信息
   *
   * @throws IOException
   */
  @Test
  public void updateGsmVehicle() throws IOException {
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("vehicleNo", "苏E552UQ");
    parameters.put("vehicleVin", "LSGPC54R8CF064000");
    parameters.put("engineNo", "120660580");
    parameters.put("registNo", "");
    parameters.put("juheCityName", "苏州市");
    parameters.put("juheCityCode", "JS_SZ");
//    parameters.put("oilPrice", "7.01");
    parameters.put("nextExamineTime", System.currentTimeMillis());
    parameters.put("nextInsuranceTime", System.currentTimeMillis());
    HttpResponse response = sendPUT(url_save_gsm_vehicle, parameters);
    LOG.info("车辆信息 getAppVehicle result:{}", response.getContent());
  }


  /**
   * 获取碰撞视频列表
   *
   * @throws IOException
   */
  @Test
  public void getVideoList() throws IOException {
    HttpResponse response = sendGET(url_video_list);
    ApiResponse apiResponse = (ApiResponse) JsonUtil.jsonToObject(response.getContent(), ApiResponse.class);
    LOG.info("getVideoList result:{}", response.getContent());
    Assert.assertEquals(MessageCode.SUCCESS.getCode(), apiResponse.getMsgCode());
  }

  /**
   * 违章
   *
   * @throws IOException
   */
  @Test
  public void queryVehicleViolateRegulation() throws IOException {
    HttpResponse response = sendGET(url_vehicle_violate);
    ApiResponse apiResponse = (ApiResponse) JsonUtil.jsonToObject(response.getContent(), ApiResponse.class);
    LOG.info("queryVehicleViolateRegulation result:{}", response.getContent());
    Assert.assertEquals(MessageCode.VEHICLE_VIOLATE_REGULATION_QUERY_SUCCESS.getCode(), apiResponse.getMsgCode());
  }

  /**
   * 违章
   *
   * @throws IOException
   */
  @Test
  public void queryPublicVehicleViolateRegulation() throws IOException {
    Map<String, Object> dataMap = getBaseGsmVehicleData();
    HttpResponse response = sendGET(url_vehicle_violate_public);
    ApiVehicleViolateRegulationResponse apiResponse = (ApiVehicleViolateRegulationResponse) JsonUtil.jsonToObject(response.getContent(), ApiVehicleViolateRegulationResponse.class);
    LOG.info("queryPublicVehicleViolateRegulation result:{}", response.getContent());
    Assert.assertEquals(MessageCode.VEHICLE_VIOLATE_REGULATION_QUERY_SUCCESS.getCode(), apiResponse.getMsgCode());
  }

  /**
   * 检测新版本
   *
   * @throws IOException
   */
  @Test
  public void bcgogoNewVersionTest() throws IOException {
    HttpResponse response = sendGET(url_bcgogo_new_version);
    ApiResponse apiResponse = (ApiResponse) JsonUtil.jsonToObject(response.getContent(), ApiResponse.class);
    LOG.info("bcgogoNewVersion result:{}", response.getContent());
    Assert.assertEquals(MessageCode.UPGRADE_TESTING_SUCCESS.getCode(), apiResponse.getMsgCode());
  }

  /**
   * 查询违章城市
   *
   * @throws IOException
   */
  @Test
  public void queryVehicleViolateRegulationCity() throws IOException {
    HttpResponse response = sendGET(url_vehicle_violate_city);
    ApiResponse apiResponse = (ApiResponse) JsonUtil.jsonToObject(response.getContent(), ApiResponse.class);
    LOG.info("bcgogoNewVersion result:{}", response.getContent());
    Assert.assertEquals(MessageCode.JUHE_VIOLATE_CONDITION_REGULATIONS_SEARCH_SUCCESS.getCode(), apiResponse.getMsgCode());
  }


//  @Test
//    public void downloadGsmUserQRCode() throws IOException {
//      HttpResponse response = sendGET(url_gsm_user_qr_download);
//  //    ApiResponse apiResponse = (ApiResponse) JsonUtil.jsonToObject(response.getContent(), ApiResponse.class);
//  //    LOG.info("queryVehicleViolateRegulation result:{}", response.getContent());
//  //    Assert.assertEquals(MessageCode.SUCCESS.getCode(), apiResponse.getMsgCode());
//    }


  @Test
  public void downloadQr() throws IOException {
    try {
      URL url = new URL(url_gsm_user_qr_download);
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      con.setRequestMethod("GET");
      con.setDoInput(true);
      con.setDoOutput(true);
      con.setUseCaches(false); // post方式不能使用缓存
      // 设置请求头信息
      con.setRequestProperty("Connection", "Keep-Alive");
      con.setRequestProperty("Charset", "UTF-8");
      //设置Cookie
      con.setRequestProperty("Cookie", sessionId);
      InputStream is = con.getInputStream();
      OutputStream os = new FileOutputStream("c:\\qr.jpg");
      int len;
      while ((len = is.read()) != -1) {
        System.out.println("len =" + len);
        os.write(len);
      }
      os.close();
      is.close();
    } finally {

    }

  }


  private void uploadFile(String uuid) throws IOException {
    LOG.info("uploadFile...");
    String path = "c:\\test\\test.mp4";
    File file = new File(path);
    long len = file.length();
    long blockLen = 1 * 1024 * 1024L;
    //1.uploadInit
    long blockNumber = len / blockLen;
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("uuid", uuid);
    parameters.put("blockNumber", String.valueOf(blockNumber));
    parameters.put("totalLength", "5");
    parameters.put("crc", EncryptionUtil.computeMD5Improved("2222222"));
    HttpResponse response = sendPUT(url_upload_init, parameters);
    ApiResponse apiResponse = (ApiResponse) JsonUtil.jsonToObject(response.getContent(), ApiResponse.class);
    LOG.info("uploadInit result:{}", response.getContent());
    Assert.assertEquals(MessageCode.SUCCESS.getCode(), apiResponse.getMsgCode());
    //2. upload video
    long start = 0;
    for (int i = 0; i < blockNumber; i++) {
      long offset = blockLen;
      if (start >= len) {
        break;
      }
      if (i == (blockNumber - 1)) {
        offset = file.length() - start;
      }
      apiResponse = uploadVideo(uuid, i, i, start, offset);
      Assert.assertEquals(MessageCode.SUCCESS.getCode(), apiResponse.getMsgCode());
      start += offset;
    }
  }

  public ApiResponse uploadVideo(String uuid, int seq1, int seq2, long start, long offset) throws IOException {
    PFileBlock block = new PFileBlock();
    block.setUuid(uuid);
    block.setSeq1(seq1);
    block.setSeq2(seq2);
    block.setLength(Long.valueOf(offset));
    block.setTimestamp(System.currentTimeMillis());

    String path = "c:\\test\\test.mp4";
    File file = new File(path);
    URL urlObj = new URL(url_upload_file);
    HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();
    con.setRequestMethod("POST");
    con.setDoInput(true);
    con.setDoOutput(true);
    con.setUseCaches(false); // post方式不能使用缓存
    // 设置请求头信息
    con.setRequestProperty("Connection", "Keep-Alive");
    con.setRequestProperty("Charset", "UTF-8");
    //设置Cookie
    con.setRequestProperty("Cookie", sessionId);
    // 设置边界
    String BOUNDARY = "----------" + System.currentTimeMillis();
    con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
    // 请求正文信息
    String Enter = "\r\n";
    StringBuilder sb = new StringBuilder();
    sb.append("--").append(BOUNDARY).append(Enter);
    sb.append("Content-Type:application/octet-stream" + Enter);
    sb.append("Content-Disposition: form-data;name=\"file\";filename=\"" + file.getName() + "\"" + Enter + Enter);
    DataOutputStream out = null;
    DataInputStream dis = null;
    RandomAccessFile fileReader = null;
    try {
      byte[] tBytes = new byte[NumberUtil.intValue(offset)];
      fileReader = new RandomAccessFile(file, "rw");
      fileReader.seek(start);
      fileReader.read(tBytes, 0, NumberUtil.intValue(offset));
      out = new DataOutputStream(con.getOutputStream());
      // 输出表头
      out.writeBytes(sb.toString());
      // 把文件已流文件的方式 推入到url中
      dis = new DataInputStream(new FileInputStream(file));
      out.write(tBytes, 0, tBytes.length);
      //上传参数
      block.setCrc(FileUtil.calcCRC32CheckSum(tBytes));
      String params = Enter + "--" + BOUNDARY + Enter
        + "Content-Type: text/plain" + Enter
        + "Content-Disposition: form-data; name=\"param\"" + Enter + Enter
        + JsonUtil.objectCHToJson(block) +
        Enter + "--" + BOUNDARY + "--";
      out.writeBytes(params);
    } finally {
      if (out != null) {
        out.flush();
        out.close();
      }
      if (fileReader != null) {
        fileReader.close();
      }
      if (dis != null) {
        dis.close();
      }
    }
    InputStream in = con.getInputStream();
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
    StringBuffer temp = new StringBuffer();
    String line = bufferedReader.readLine();
    while (line != null) {
      temp.append(line);
      line = bufferedReader.readLine();
    }
    bufferedReader.close();
    return (ApiResponse) JsonUtil.jsonToObject(temp.toString(), ApiResponse.class);
  }

  /**
   * ****************************************      apix      *****************************************************
   */


  @Test
  public void apixTest() throws IOException {
    String url = domain_apix + "/apix/connect/test";
    HttpResponse response = HttpUtils.sendGet(url);
    ApiResponse apiResponse = (ApiResponse) JsonUtil.jsonToObject(response.getContent(), ApiResponse.class);
    Assert.assertEquals(MessageCode.SUCCESS.getCode(), apiResponse.getMsgCode());
  }

//  @Test
//  public void apixMirrorLogin() throws IOException {
//    String url = AppConstant.URL_APIX_TEST;
//    Map<String, Object> dataMap = getBaseGsmVehicleData();
//    dataMap.put("vehicleStatus", GSMConstant.FIRE_UP);
//    HttpResponse response = HttpUtils.sendGet(url);
////       HttpResponse response = HttpUtils.sendPUT(url,dataMap);
////    HttpResponse response = sendPUT(url,dataMap);
//    ApiResponse apiResponse = (ApiResponse) JsonUtil.jsonToObject(response.getContent(), ApiResponse.class);
//    LOG.info("bcgogoNewVersion result:{}", response.getContent());
//  }

  @Test
  public void saveDriveLog() throws IOException {
    String url = XConstant.URL_OPEN_SAVE_DRIVE_LOG;
    Map<String, Object> dataMap = getBaseGsmVehicleData();
    dataMap.put("vehicleNo", "苏E2323");
    HttpResponse response = HttpUtils.sendPost(url, dataMap);
    LOG.info("saveDriveLog result:{}", response.getContent());
  }


  @Test
  public void testPushMessage() throws IOException {
    String url = "https://shop.bcgogo.com/web/client?method=getMessages";
    Map<String, Object> dataMap = new HashMap<String, Object>();
    dataMap.put("shopId", "10000010125362440");
    dataMap.put("userNo", "13777389417");
    dataMap.put("apiVersion", "1.0");
    HttpResponse response = HttpUtils.sendPost(url, dataMap);
    LOG.info("testPushMessage result:{}", response.getContent());
  }

  @Test
  public void testGetPrompt() throws IOException {
    String url = "https://shop.bcgogo.com/web/client?method=getPrompt";
    Map<String, Object> dataMap = new HashMap<String, Object>();
    dataMap.put("shopId", "10000010125362440");
    dataMap.put("userNo", "13777389417");
    dataMap.put("apiVersion", "1.0");
    HttpResponse response = HttpUtils.sendPost(url, dataMap);
    LOG.info("testPushMessage result:{}", response.getContent());
  }


  /**
   * 上传车况信息到apix
   *
   * @throws IOException
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
    String url_gsm_vehicle_data = domain_apix + "/apix/gsm/vehicle/data";
    HttpResponse response = sendPUT(url_gsm_vehicle_data, dataMap);
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
    dataMap.put("curMil", "186");
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

  /**
   * 车辆碰撞
   *
   * @throws IOException
   */
  @Test
  public void impactMocker() throws IOException {
    Map<String, Object> impact = new HashMap<String, Object>();
    String impactUUID = UUID.randomUUID().toString();
    impact.put("uuid", impactUUID);
    impact.put("appUserNo", appUserNo);
    impact.put("uploadTime", System.currentTimeMillis());
    Map<String, Object> gvData0 = getBaseGsmVehicleData();
    gvData0.put("rdtc", "C0032,U0564");
    Map<String, Object> gvData1 = getBaseGsmVehicleData();
    gvData1.put("rdtc", "U0022");
    List<Map<String, Object>> gvDataList = new ArrayList<Map<String, Object>>();
    gvDataList.add(gvData0);
    gvDataList.add(gvData1);
    impact.put("data", gvDataList.toArray());


    String path = "c:\\test\\test.jpg";
    File file = new File(path);
    URL urlObj = new URL(url_impact_collect);
    HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();
    con.setRequestMethod("POST");
    con.setDoInput(true);
    con.setDoOutput(true);
    con.setUseCaches(false); // post方式不能使用缓存
    // 设置请求头信息
    con.setRequestProperty("Connection", "Keep-Alive");
    con.setRequestProperty("Charset", "UTF-8");
    //设置Cookie
    con.setRequestProperty("Cookie", sessionId);
    // 设置边界
    String BOUNDARY = "----------" + System.currentTimeMillis();
    String Enter = "\r\n";
    con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
    // 请求正文信息
    StringBuilder sb = new StringBuilder();
    sb.append("--").append(BOUNDARY).append(Enter);
    sb.append("Content-Type:application/octet-stream" + Enter);
    sb.append("Content-Disposition: form-data;name=\"file\";filename=\"" + file.getName() + "\"" + Enter + Enter);
    DataOutputStream out = null;
    DataInputStream dis = null;
    RandomAccessFile fileReader = null;
    try {
      int start = 0;
      long offset = file.length();
      byte[] tBytes = new byte[NumberUtil.intValue(offset)];
      fileReader = new RandomAccessFile(file, "rw");
      fileReader.seek(start);
      fileReader.read(tBytes, 0, NumberUtil.intValue(offset));
      out = new DataOutputStream(con.getOutputStream());
      // 输出表头
      out.writeBytes(sb.toString());
      // 把文件已流文件的方式 推入到url中
      dis = new DataInputStream(new FileInputStream(file));
      out.write(tBytes, 0, tBytes.length);
      String params = Enter + "--" + BOUNDARY + Enter
        + "Content-Type: text/plain" + Enter
        + "Content-Disposition: form-data; name=\"param\"" + Enter + Enter
        + JsonUtil.objectCHToJson(impact) +
        Enter + "--" + BOUNDARY + "--";
      out.writeBytes(params);
    } finally {
      if (out != null) {
        out.flush();
        out.close();
      }
      if (fileReader != null) {
        fileReader.close();
      }
      if (dis != null) {
        dis.close();
      }
    }
    InputStream in = con.getInputStream();
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
    StringBuffer temp = new StringBuffer();
    String line = bufferedReader.readLine();
    while (line != null) {
      temp.append(line);
      line = bufferedReader.readLine();
    }
    bufferedReader.close();
    ApiResponse apiResponse = JsonUtil.fromJson(temp.toString(), ApiResponse.class);
    Assert.assertEquals(MessageCode.IMPACT_DATA_SAVE_SUCCESS.getCode(), apiResponse.getMsgCode());
    //上传碰撞信息
    uploadFile(impactUUID);
  }


//  @Test
//  public void generationDriveLog() throws IOException, InterruptedException {
//    uploadGsmVehicleData();
//    LOG.info("generationDriveLog");
//    HttpUtils.sendGet(domain + "/web/tc/generationDriveLog");
//  }

  @Test
  public void startPushMessageScheduleWork() throws IOException, InterruptedException {
    updateGsmVehicle();
    HttpUtils.sendGet(domain + "/web/tc/startPushMessageScheduleWork");
  }

  @Test
  public void pushTalkPacket() throws IOException, InterruptedException {
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("openId", "ovjSts-ublkv2I4__xoU8Nt0f3iw");
    parameters.put("appUserNo", appUserNo);
    HttpUtils.sendPost(domain + "/web/tc/pushTalkPacket", parameters);
  }

  @Test
  public void wxPushTalkPacket() throws IOException, InterruptedException {
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put("openId", appUserNo);
    parameters.put("appUserNo", appUserNo);
    parameters.put("content", "hi");
    HttpUtils.sendPost(url_wx_user_send, parameters);
  }

  @Test
  public void rescue() throws Exception {
    Map<String, Object> parameters = getBaseGsmVehicleData();
    parameters.put("rdtc", "U0022");
    HttpResponse response = sendPUT(url_rescue_sos, parameters);
    ApiResponse apiResponse = (ApiResponse) JsonUtil.jsonToObject(response.getContent(), ApiResponse.class);
    Assert.assertEquals(MessageCode.SUCCESS.getCode(), apiResponse.getMsgCode());
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

  private HttpResponse sendPOST(String urlString, Map<String, Object> parameters) throws IOException {
    Map<String, String> propertys = new HashMap<String, String>();
    propertys.put("Cookie", sessionId);
    return HttpUtils.sendPost(urlString, parameters, propertys);
  }

  String placeNoteSrc = "31.298548,120.741578,1397220562000|31.299512,120.741570,1397220572000|31.300488,120.741557,1397220582000|31.300603,120.741537,1397220584000|31.300702,120.741515,1397220586000|31.300765,120.741505,1397220588000|31.300930,120.741555,1397220597000|31.301017,120.741565,1397220599000|31.302283,120.741585,1397220615000|31.305992,120.740848,1397220645000|31.309207,120.739943,1397220675000|31.310338,120.739957,1397220705000|31.310340,120.739952,1397220735000|31.311153,120.739930,1397220765000|31.314243,120.739892,1397220795000|31.316888,120.739845,1397220825000|31.320840,120.739752,1397220855000|31.321165,120.739742,1397220858000|31.321790,120.739710,1397220865000|31.321913,120.739692,1397220868000|31.321938,120.739693,1397220871000|31.321888,120.739740,1397220885000|31.321892,120.739740,1397220914000|31.321922,120.739735,1397220915000|31.322252,120.739737,1397220921000|31.322725,120.739723,1397220926000|31.325275,120.739692,1397220945000|31.327270,120.739637,1397220975000|31.328542,120.739678,1397221005000|31.329593,120.742320,1397221035000|31.330182,120.747333,1397221065000|31.330623,120.751282,1397221095000|31.330618,120.751320,1397221125000|31.330607,120.751407,1397221155000|31.330918,120.754340,1397221177000|31.331068,120.755793,1397221185000|31.331500,120.759808,1397221211000|31.331505,120.759843,1397221215000|31.331508,120.759873,1397221227000|31.331677,120.761520,1397221245000|31.332162,120.766105,1397221275000|31.332687,120.770952,1397221305000|31.333065,120.774278,1397221335000|31.333682,120.780080,1397221365000|31.334092,120.783998,1397221395000|31.334227,120.785270,1397221425000|31.334770,120.788588,1397221445000|31.335070,120.790193,1397221455000|31.335248,120.791162,1397221485000|31.335232,120.791157,1397221515000|31.335420,120.792298,1397221545000|31.336275,120.797518,1397221575000|31.337177,120.802787,1397221605000|31.337497,120.806417,1397221635000|31.336660,120.804210,1397221665000|31.337903,120.802873,1397221695000|31.341393,120.801798,1397221725000|31.345762,120.800380,1397221755000|31.348947,120.799358,1397221785000|31.350855,120.798583,1397221815000|31.354127,120.797527,1397221845000|31.356475,120.796895,1397221875000|31.357428,120.796633,1397221905000|31.357505,120.796600,1397221935000|31.357940,120.796497,1397221965000|31.358272,120.796377,1397221995000|31.358317,120.796343,1397222025000|31.358407,120.796318,1397222039000|31.358500,120.796288,1397222042000|31.358895,120.796162,1397222055000|31.358955,120.796138,1397222061000|31.359025,120.796123,1397222085000|31.359030,120.796147,1397222115000|31.359487,120.796000,1397222145000|31.361550,120.795355,1397222175000|31.363130,120.795995,1397222205000|31.363488,120.799033,1397222235000|31.363525,120.798938,1397222265000|31.363558,120.798903,1397222295000|31.363518,120.798900,1397222315000|31.363517,120.798900,1397222316000|31.363513,120.798902,1397222317000|31.363512,120.798902,1397222318000|31.363515,120.798902,1397222319000|31.363520,120.798898,1397222320000|31.363527,120.798903,1397222321000|31.363525,120.798905,1397222322000|31.363522,120.798905,1397222323000|31.363522,120.798908,1397222324000|31.363523,120.798907,1397222325000|31.363518,120.798893,1397222349000|31.363517,120.798895,1397222350000|31.363515,120.798895,1397222351000|31.363513,120.798893,1397222352000|31.363512,120.798893,1397222353000|31.363512,120.798893,1397222354000|31.363510,120.798893,1397222355000|31.363510,120.798893,1397222356000|31.363508,120.798893,1397222357000|31.363508,120.798893,1397222358000|31.363507,120.798893,1397222359000|31.363507,120.798892,1397222360000|31.363507,120.798892,1397222361000|31.363507,120.798892,1397222362000|31.363505,120.798892,1397222363000|31.363505,120.798892,1397222364000|31.363505,120.798890,1397222365000|31.363503,120.798892,1397222366000|31.363490,120.798890,1397222377000|31.363488,120.798890,1397222378000|31.363488,120.798888,1397222379000|31.363488,120.798888,1397222380000|31.363487,120.798888,1397222381000|31.363487,120.798888,1397222382000|31.363485,120.798888,1397222383000|31.363485,120.798888,1397222384000|31.363487,120.798887,1397222385000|31.363487,120.798887,1397222386000|31.363488,120.798887,1397222387000|31.363488,120.798885,1397222388000|31.363490,120.798885,1397222389000|31.363492,120.798883,1397222390000|31.363492,120.798883,1397222391000|31.363493,120.798883,1397222392000|31.363493,120.798883,1397222393000|31.363495,120.798883,1397222394000|31.363495,120.798883,1397222395000|31.363497,120.798883,1397222396000|31.363497,120.798883,1397222397000|31.363497,120.798883,1397222426000|31.363497,120.798883,1397222456000|31.363497,120.798883,1397222486000|31.363497,120.798883,1397222516000|31.363497,120.798883,1397222546000|31.363497,120.798883,1397222576000|31.363497,120.798883,1397222606000|31.363497,120.798883,1397222636000|" +
    "31.363497,120.798883,1397222666000|31.363497,120.798883,1397222690000|31.363497,120.798883,1397222690000|";
//  String placeNoteSrc = "1,1.2,1397220562000|2,2.2,1397220562000|3,3.2,1397220562000|31.298548,120.741578,1397220562000|31.299512,120.741570,1397220572000|31.300488,120.741557,1397220582000|31.300603,120.741537,1397220584000|31.300702,120.741515,1397220586000|31.300765,120.741505,1397220588000|31.300930,120.741555,1397220597000|31.301017,120.741565,1397220599000|31.302283,120.741585,1397220615000|31.305992,120.740848,1397220645000|31.309207,120.739943,1397220675000|31.310338,120.739957,1397220705000|31.310340,120.739952,1397220735000|31.311153,120.739930,1397220765000|31.314243,120.739892,1397220795000|31.316888,120.739845,1397220825000|31.320840,120.739752,1397220855000|31.321165,120.739742,1397220858000|31.321790,120.739710,1397220865000|31.321913,120.739692,1397220868000|31.321938,120.739693,1397220871000|31.321888,120.739740,1397220885000|31.321892,120.739740,1397220914000|31.321922,120.739735,1397220915000|31.322252,120.739737,1397220921000|31.322725,120.739723,1397220926000|31.325275,120.739692,1397220945000|31.327270,120.739637,1397220975000|31.328542,120.739678,1397221005000|31.329593,120.742320,1397221035000|31.330182,120.747333,1397221065000|31.330623,120.751282,1397221095000|31.330618,120.751320,1397221125000|31.330607,120.751407,1397221155000|31.330918,120.754340,1397221177000|31.331068,120.755793,1397221185000|31.331500,120.759808,1397221211000|31.331505,120.759843,1397221215000|31.331508,120.759873,1397221227000|31.331677,120.761520,1397221245000|31.332162,120.766105,1397221275000|31.332687,120.770952,1397221305000|31.333065,120.774278,1397221335000|31.333682,120.780080,1397221365000|31.334092,120.783998,1397221395000|31.334227,120.785270,1397221425000|31.334770,120.788588,1397221445000|31.335070,120.790193,1397221455000|31.335248,120.791162,1397221485000|31.335232,120.791157,1397221515000|31.335420,120.792298,1397221545000|31.336275,120.797518,1397221575000|31.337177,120.802787,1397221605000|31.337497,120.806417,1397221635000|31.336660,120.804210,1397221665000|31.337903,120.802873,1397221695000|31.341393,120.801798,1397221725000|31.345762,120.800380,1397221755000|31.348947,120.799358,1397221785000|31.350855,120.798583,1397221815000|31.354127,120.797527,1397221845000|31.356475,120.796895,1397221875000|31.357428,120.796633,1397221905000|31.357505,120.796600,1397221935000|31.357940,120.796497,1397221965000|31.358272,120.796377,1397221995000|31.358317,120.796343,1397222025000|31.358407,120.796318,1397222039000|31.358500,120.796288,1397222042000|31.358895,120.796162,1397222055000|31.358955,120.796138,1397222061000|31.359025,120.796123,1397222085000|31.359030,120.796147,1397222115000|31.359487,120.796000,1397222145000|31.361550,120.795355,1397222175000|31.363130,120.795995,1397222205000|31.363488,120.799033,1397222235000|31.363525,120.798938,1397222265000|31.363558,120.798903,1397222295000|31.363518,120.798900,1397222315000|31.363517,120.798900,1397222316000|31.363513,120.798902,1397222317000|31.363512,120.798902,1397222318000|31.363515,120.798902,1397222319000|31.363520,120.798898,1397222320000|31.363527,120.798903,1397222321000|31.363525,120.798905,1397222322000|31.363522,120.798905,1397222323000|31.363522,120.798908,1397222324000|31.363523,120.798907,1397222325000|31.363518,120.798893,1397222349000|31.363517,120.798895,1397222350000|31.363515,120.798895,1397222351000|31.363513,120.798893,1397222352000|31.363512,120.798893,1397222353000|31.363512,120.798893,1397222354000|31.363510,120.798893,1397222355000|31.363510,120.798893,1397222356000|31.363508,120.798893,1397222357000|31.363508,120.798893,1397222358000|31.363507,120.798893,1397222359000|31.363507,120.798892,1397222360000|31.363507,120.798892,1397222361000|31.363507,120.798892,1397222362000|31.363505,120.798892,1397222363000|31.363505,120.798892,1397222364000|31.363505,120.798890,1397222365000|31.363503,120.798892,1397222366000|31.363490,120.798890,1397222377000|31.363488,120.798890,1397222378000|31.363488,120.798888,1397222379000|31.363488,120.798888,1397222380000|31.363487,120.798888,1397222381000|31.363487,120.798888,1397222382000|31.363485,120.798888,1397222383000|31.363485,120.798888,1397222384000|31.363487,120.798887,1397222385000|31.363487,120.798887,1397222386000|31.363488,120.798887,1397222387000|31.363488,120.798885,1397222388000|31.363490,120.798885,1397222389000|31.363492,120.798883,1397222390000|31.363492,120.798883,1397222391000|31.363493,120.798883,1397222392000|31.363493,120.798883,1397222393000|31.363495,120.798883,1397222394000|31.363495,120.798883,1397222395000|31.363497,120.798883,1397222396000|31.363497,120.798883,1397222397000|31.363497,120.798883,1397222426000|31.363497,120.798883,1397222456000|31.363497,120.798883,1397222486000|31.363497,120.798883,1397222516000|31.363497,120.798883,1397222546000|31.363497,120.798883,1397222576000|31.363497,120.798883,1397222606000|31.363497,120.798883,1397222636000|31.363497,120.798883,1397222666000|31.363497,120.798883,1397222690000|31.363497,120.798883,1397222690000|139,139.2,1397220562000|";


}
