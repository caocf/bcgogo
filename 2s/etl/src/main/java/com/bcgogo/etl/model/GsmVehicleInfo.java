package com.bcgogo.etl.model;


import com.bcgogo.enums.etl.GsmVehicleStatus;
import com.bcgogo.etl.GsmVehicleInfoDTO;
import com.bcgogo.model.LongIdentifier;

import javax.persistence.*;

/**
 * User: ZhangJuntao
 * Date: 14-3-5
 * Time: 下午4:45
 */
@Entity
@Table(name = "gsm_vehicle_info")
public class GsmVehicleInfo extends LongIdentifier {

  public GsmVehicleInfoDTO toDTO() {
    GsmVehicleInfoDTO dto = new GsmVehicleInfoDTO();
    dto.setId(getId());
    dto.setEmi(getEmi());
    dto.setAppUserNo(getAppUserNo());
    dto.setState(getState());
    dto.setAppPassword(getAppPassword());
    dto.setLoad(getLoad());
    dto.setEct(getEct());
    dto.setShrtft1(getShrtft1());
    dto.setLongft1(getLongft1());
    dto.setMap(getMap());
    dto.setRpm(getRpm());
    dto.setVss(getVss());
    dto.setSparkAdv(getSparkAdv());
    dto.setIat(getIat());
    dto.setMaf(getMaf());
    dto.setTps(getTps());
    dto.setMilDist(getMilDist());
    dto.setFuelLvl(getFuelLvl());
    dto.setBaro(getBaro());
    dto.setVpwr(getVpwr());
    dto.setIfe(getIfe());
    dto.setRuntime(getRuntime());
    dto.setAdMil(getAdMil());
    dto.setAdFeh(getAdFeh());
    dto.setSpwr(getSpwr());
    dto.setRdtcn(getRdtcn());
    dto.setRdtc(getRdtc());
    dto.setGsmVehicleStatus(getGsmVehicleStatus());
    dto.setOrgInfo(getOrgInfo());
    dto.setMaxr(getMaxr());
    dto.setMaxs(getMaxs());
    dto.setBadh(getBadh());
    dto.setCacafe(getCacafe());
    dto.setCactfe(getCactfe());
    dto.setCactrfe(getCactrfe());
    dto.setTrmil(getTrmil());
    dto.setDemil(getDemil());
    dto.setDrit(getDrit());
    dto.setIdlet(getIdlet());
    dto.setRpdtc(getRpdtc());
    dto.setUploadTime(getUploadTime());
    dto.setUploadServerTime(getUploadServerTime());
    return dto;
  }

  private String emi;        //EMI 356823034064670
  private String appUserNo; //
  private Integer state;     //0
  private String appPassword; //2468
  private String load;  //发动机负荷              36.1%
  private String ect;  //发动机水温                99逤
  private String shrtft1;  //短时燃油修正         9.4%
  private String longft1;  //长时燃油修正         -6.3%
  private String map;  //进气歧管绝对压力          39.0kPa
  private String rpm;  //发动机转速                2000
  private String vss;  //车辆速度                  90km/h
  private String sparkAdv;  //1 号汽缸点火提前角   4.0
  private String iat;  //进气温度                 54逤
  private String maf;  //空气流量                 3.55g/s
  private String tps;  //节气门绝对位置           12.2%
  private String milDist;  //mil（故障灯） 亮起后的行驶距离   5376km
  private String fuelLvl;  //燃油量输入          65.1%
  private String baro;  //大气压力               101kPa
  private String vpwr;  //控制模块电压           13.81V
  private String ife;  //瞬时油耗               0.337ml/s
  private String runtime;  //发动机运行时间     4130s
  private String adMil;  //总里程               122.6km
  private String adFeh;  //百公里油耗              1.102l/100km
  private String spwr;  //电瓶电压                 12.53V
  private Integer rdtcn;  //故障码数量            009
  private String rdtc;  //故障代码   U0021&B0090&C0032&P0006&P0007&P1233&P0008&P0009&U0022
  private GsmVehicleStatus gsmVehicleStatus = GsmVehicleStatus.UN_HANDLE;
  private String orgInfo;   //原始信息
  private String maxr;//最大转速
  private String maxs;//行程最大车速
  private String badh;//急加减速次数
  private String cacafe;//计算平均油耗
  private String cactfe;//计算总耗油量
  private String cactrfe;//计算单程耗油量
  private String trmil;//单次里程
  private String demil;//设备安装后的行驶里程
  private String drit;//驾驶时间
  private String idlet;//怠速时间
  private String rpdtc;//车辆未决故障码
  private Long uploadTime;//数据时间戳，设备发给我们的信息
  private Long uploadServerTime;//数据上传时服务器时间


  @Column(name = "emi")
  public String getEmi() {
    return emi;
  }

  public void setEmi(String emi) {
    this.emi = emi;
  }

  @Column(name = "app_user_no")
  public String getAppUserNo() {
    return appUserNo;
  }

  public void setAppUserNo(String appUserNo) {
    this.appUserNo = appUserNo;
  }

  @Column(name = "state")
  public Integer getState() {
    return state;
  }

  public void setState(Integer state) {
    this.state = state;
  }

  @Column(name = "app_password")
  public String getAppPassword() {
    return appPassword;
  }

  public void setAppPassword(String appPassword) {
    this.appPassword = appPassword;
  }

  @Column(name = "gvi_load")
  public String getLoad() {
    return load;
  }

  public void setLoad(String load) {
    this.load = load;
  }

  @Column(name = "ect")
  public String getEct() {
    return ect;
  }

  public void setEct(String ect) {
    this.ect = ect;
  }

  @Column(name = "shrtft1")
  public String getShrtft1() {
    return shrtft1;
  }

  public void setShrtft1(String shrtft1) {
    this.shrtft1 = shrtft1;
  }

  @Column(name = "longft1")
  public String getLongft1() {
    return longft1;
  }

  public void setLongft1(String longft1) {
    this.longft1 = longft1;
  }

  @Column(name = "map")
  public String getMap() {
    return map;
  }

  public void setMap(String map) {
    this.map = map;
  }

  @Column(name = "rpm")
  public String getRpm() {
    return rpm;
  }

  public void setRpm(String rpm) {
    this.rpm = rpm;
  }

  @Column(name = "vss")
  public String getVss() {
    return vss;
  }

  public void setVss(String vss) {
    this.vss = vss;
  }

  @Column(name = "spark_adv")
  public String getSparkAdv() {
    return sparkAdv;
  }

  public void setSparkAdv(String sparkAdv) {
    this.sparkAdv = sparkAdv;
  }

  @Column(name = "iat")
  public String getIat() {
    return iat;
  }

  public void setIat(String iat) {
    this.iat = iat;
  }

  @Column(name = "maf")
  public String getMaf() {
    return maf;
  }

  public void setMaf(String maf) {
    this.maf = maf;
  }

  @Column(name = "tps")
  public String getTps() {
    return tps;
  }

  public void setTps(String tps) {
    this.tps = tps;
  }

  @Column(name = "mil_dist")
  public String getMilDist() {
    return milDist;
  }

  public void setMilDist(String milDist) {
    this.milDist = milDist;
  }

  @Column(name = "fuel_lvl")
  public String getFuelLvl() {
    return fuelLvl;
  }

  public void setFuelLvl(String fuelLvl) {
    this.fuelLvl = fuelLvl;
  }

  @Column(name = "baro")
  public String getBaro() {
    return baro;
  }

  public void setBaro(String baro) {
    this.baro = baro;
  }

  @Column(name = "vpwr")
  public String getVpwr() {
    return vpwr;
  }

  public void setVpwr(String vpwr) {
    this.vpwr = vpwr;
  }

  @Column(name = "ife")
  public String getIfe() {
    return ife;
  }

  public void setIfe(String ife) {
    this.ife = ife;
  }

  @Column(name = "runtime")
  public String getRuntime() {
    return runtime;
  }

  public void setRuntime(String runtime) {
    this.runtime = runtime;
  }

  @Column(name = "ad_mil")
  public String getAdMil() {
    return adMil;
  }

  public void setAdMil(String adMil) {
    this.adMil = adMil;
  }

  @Column(name = "ad_feh")
  public String getAdFeh() {
    return adFeh;
  }

  public void setAdFeh(String adFeh) {
    this.adFeh = adFeh;
  }

  @Column(name = "spwr")
  public String getSpwr() {
    return spwr;
  }

  public void setSpwr(String spwr) {
    this.spwr = spwr;
  }

  @Column(name = "rdtcn")
  public Integer getRdtcn() {
    return rdtcn;
  }

  public void setRdtcn(Integer rdtcn) {
    this.rdtcn = rdtcn;
  }

  @Column(name = "rdtc")
  public String getRdtc() {
    return rdtc;
  }

  public void setRdtc(String rdtc) {
    this.rdtc = rdtc;
  }

  @Column(name = "org_info", length = 1000)
  public String getOrgInfo() {
    return orgInfo;
  }

  public void setOrgInfo(String orgInfo) {
    this.orgInfo = orgInfo;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "gsm_vehicle_status")
  public GsmVehicleStatus getGsmVehicleStatus() {
    return gsmVehicleStatus;
  }

  public void setGsmVehicleStatus(GsmVehicleStatus gsmVehicleStatus) {
    this.gsmVehicleStatus = gsmVehicleStatus;
  }

  @Column(name = "max_r")
  public String getMaxr() {
    return maxr;
  }

  public void setMaxr(String maxr) {
    this.maxr = maxr;
  }

  @Column(name = "max_s")
  public String getMaxs() {
    return maxs;
  }

  public void setMaxs(String maxs) {
    this.maxs = maxs;
  }

  @Column(name = "bad_h")
  public String getBadh() {
    return badh;
  }

  public void setBadh(String badh) {
    this.badh = badh;
  }

  @Column(name = "cac_afe")
  public String getCacafe() {
    return cacafe;
  }

  public void setCacafe(String cacafe) {
    this.cacafe = cacafe;
  }

  @Column(name = "cac_tfe")
  public String getCactfe() {
    return cactfe;
  }

  public void setCactfe(String cactfe) {
    this.cactfe = cactfe;
  }

  @Column(name = "cac_trfe")
  public String getCactrfe() {
    return cactrfe;
  }

  public void setCactrfe(String cactrfe) {
    this.cactrfe = cactrfe;
  }

  @Column(name = "tr_mil")
  public String getTrmil() {
    return trmil;
  }

  public void setTrmil(String trmil) {
    this.trmil = trmil;
  }

  @Column(name = "de_mil")
  public String getDemil() {
    return demil;
  }

  public void setDemil(String demil) {
    this.demil = demil;
  }

  @Column(name = "drit")
  public String getDrit() {
    return drit;
  }

  public void setDrit(String drit) {
    this.drit = drit;
  }

  @Column(name = "idlet")
  public String getIdlet() {
    return idlet;
  }

  public void setIdlet(String idlet) {
    this.idlet = idlet;
  }

  @Column(name = "rpdtc")
  public String getRpdtc() {
    return rpdtc;
  }

  public void setRpdtc(String rpdtc) {
    this.rpdtc = rpdtc;
  }

  @Column(name = "upload_time")
  public Long getUploadTime() {
    return uploadTime;
  }

  public void setUploadTime(Long uploadTime) {
    this.uploadTime = uploadTime;
  }

  @Column(name = "upload_server_time")
  public Long getUploadServerTime() {
    return uploadServerTime;
  }

  public void setUploadServerTime(Long uploadServerTime) {
    this.uploadServerTime = uploadServerTime;
  }
}