package com.bcgogo.txn.dto;

import com.bcgogo.api.AppUserCustomerDTO;
import com.bcgogo.enums.app.AppointOrderStatus;
import com.bcgogo.enums.app.AppointWay;
import com.bcgogo.utils.ArrayUtil;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-9-11
 * Time: 下午2:54
 */
public class AppointOrderSearchCondition implements Serializable {
  private static final Logger LOG = LoggerFactory.getLogger(AppointOrderSearchCondition.class);

  public String[] getCustomers() {
    return customers;
  }

  public void setCustomers(String[] customers) {
    this.customers = customers;
  }

  private enum Scene {
    //AppointOrderStatus.PENDING
    CLIENT_NEW_ORDER,
    //AppointOrderStatus.ACCEPTED and AppointOrderStatus.TO_DO_REPAIR
    CLIENT_HANDLED_ORDER,
    //appoint_time between(闭区间)  Long currentTime = System.currentTimeMillis(), upTime = currentTime + intervals[1], downTime = currentTime + intervals[0];  in AppointOrderStatus.PENDING and AppointOrderStatus.ACCEPTED
    CLIENT_OVERDUE_AND_SOON_ORDER,
  }

  private String customerSearchWord; //客户名 手机号
  private String[] customers;
  private String vehicleNo;//车牌号
  private String receiptNo;
  private Long createTimeStart;//      预约单创建时间
  private String createTimeStartStr;
  private Long createTimeEnd;//      预约单创建时间
  private String createTimeEndStr;
  private Long appointTimeStart;//     预约时间
  private String appointTimeStartStr;
  private Long appointTimeEnd;//     预约时间
  private String appointTimeEndStr;
  private Long[] serviceCategoryIds;//本店服务Id
  private AppointWay appointWay;//预约方式
  private AppointOrderStatus[] appointOrderStatus;//单据状态
  private Long[] customerIds;//客户Id如果前台下拉选择的则查改客户
  private String[] appUserNos;

  private Long shopId;//查询店铺的id
  private Scene scene;
  //在线退货前台传过来的pager分页  与单据查询中心不兼容
  private int maxRows = 15;
  private int startPageNo = 1;

  public void setClientNewOrderScene() {
    this.scene = Scene.CLIENT_NEW_ORDER;
  }

  public void setClientHandledOrderScene() {
    this.scene = Scene.CLIENT_HANDLED_ORDER;
  }

  public void setClientOverdueAndSoonOrderOrderScene() {
    this.scene = Scene.CLIENT_OVERDUE_AND_SOON_ORDER;
  }

  public boolean isClientNewOrderScene() {
    return this.scene == Scene.CLIENT_NEW_ORDER;
  }

  public boolean isClientHandledOrderScene() {
    return this.scene == Scene.CLIENT_HANDLED_ORDER;
  }

  public boolean isClientOverdueAndSoonOrderOrderScene() {
    return this.scene == Scene.CLIENT_OVERDUE_AND_SOON_ORDER;
  }

  //only used by spring framework
  public void setScene(Scene scene) {
    this.scene = scene;
  }

  public String getCustomerSearchWord() {
    return customerSearchWord;
  }

  public void setCustomerSearchWord(String customerSearchWord) {
    this.customerSearchWord = customerSearchWord;
  }

  public String getReceiptNo() {
    return receiptNo;
  }

  public void setReceiptNo(String receiptNo) {
    this.receiptNo = receiptNo;
  }

  public Long getCreateTimeStart() {
    return createTimeStart;
  }

  public void setCreateTimeStart(Long createTimeStart) {
    this.createTimeStart = createTimeStart;
  }

  public String getCreateTimeStartStr() {
    return createTimeStartStr;
  }

  public void setCreateTimeStartStr(String createTimeStartStr) {
    this.createTimeStartStr = createTimeStartStr;
    if (StringUtils.isNotEmpty(createTimeStartStr)) {
      try {
        createTimeStart = DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, createTimeStartStr);
      } catch (Exception e) {
        LOG.error(e.getMessage());
      }
    } else {
      createTimeStart = null;
    }
  }

  public Long getCreateTimeEnd() {
    return createTimeEnd;
  }

  public void setCreateTimeEnd(Long createTimeEnd) {
    this.createTimeEnd = createTimeEnd;
  }

  public String getCreateTimeEndStr() {
    return createTimeEndStr;
  }

  public void setCreateTimeEndStr(String createTimeEndStr) {
    this.createTimeEndStr = createTimeEndStr;

    if (StringUtils.isNotEmpty(createTimeEndStr)) {
      try {
        createTimeEnd = DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, createTimeEndStr);
        createTimeEnd += (24 * 3600 * 1000 - 1);   //结束日期应在当天末。
      } catch (Exception e) {
        LOG.error(e.getMessage());
      }
    } else {
      createTimeEnd = null;
    }
  }

  public Long getAppointTimeStart() {
    return appointTimeStart;
  }

  public void setAppointTimeStart(Long appointTimeStart) {
    this.appointTimeStart = appointTimeStart;
  }

  public String getAppointTimeStartStr() {
    return appointTimeStartStr;
  }

  public void setAppointTimeStartStr(String appointTimeStartStr) {
    this.appointTimeStartStr = appointTimeStartStr;
    if (StringUtils.isNotEmpty(appointTimeStartStr)) {
      try {
        appointTimeStart = DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, appointTimeStartStr);
      } catch (Exception e) {
        LOG.error(e.getMessage());
      }
    } else {
      appointTimeStart = null;
    }
  }

  public Long getAppointTimeEnd() {
    return appointTimeEnd;
  }

  public void setAppointTimeEnd(Long appointTimeEnd) {
    this.appointTimeEnd = appointTimeEnd;
  }

  public String getAppointTimeEndStr() {
    return appointTimeEndStr;
  }

  public void setAppointTimeEndStr(String appointTimeEndStr) {
    this.appointTimeEndStr = appointTimeEndStr;
    if (StringUtils.isNotEmpty(appointTimeEndStr)) {
      try {
        appointTimeEnd = DateUtil.convertDateStringToDateLong(DateUtil.YEAR_MONTH_DATE, appointTimeEndStr);
        appointTimeEnd += (24 * 3600 * 1000 - 1);   //结束日期应在当天末。
      } catch (Exception e) {
        LOG.error(e.getMessage());
      }
    } else {
      appointTimeEnd = null;
    }
  }

  public Long[] getServiceCategoryIds() {
    return serviceCategoryIds;
  }

  public void setServiceCategoryIds(Long[] serviceCategoryIds) {
    this.serviceCategoryIds = serviceCategoryIds;
  }

  public AppointWay getAppointWay() {
    return appointWay;
  }

  public void setAppointWay(AppointWay appointWay) {
    this.appointWay = appointWay;
  }

  public AppointOrderStatus[] getAppointOrderStatus() {
    return appointOrderStatus;
  }

  public void setAppointOrderStatus(AppointOrderStatus[] appointOrderStatus) {
    this.appointOrderStatus = appointOrderStatus;
  }

  public Long[] getCustomerIds() {
    return customerIds;
  }

  public void setCustomerIds(Long[] customerIds) {
    this.customerIds = customerIds;
  }

  public int getStartPageNo() {
    return startPageNo;
  }

  public void setStartPageNo(int startPageNo) {
    this.startPageNo = startPageNo;
  }

  public int getMaxRows() {
    return maxRows;
  }

  public void setMaxRows(int maxRows) {
    this.maxRows = maxRows;
  }

  public Long getShopId() {
    return shopId;
  }

  public void setShopId(Long shopId) {
    this.shopId = shopId;
  }

  public String getVehicleNo() {
    return vehicleNo;
  }

  public void setVehicleNo(String vehicleNo) {
    this.vehicleNo = vehicleNo;
  }

  public String[] getAppUserNos() {
    return appUserNos;
  }

  public void setAppUserNos(String[] appUserNos) {
    this.appUserNos = appUserNos;
  }

  public void setAppUserNosFromAppUserCustomers(List<AppUserCustomerDTO> appUserCustomerDTOList) {
    if (CollectionUtil.isNotEmpty(appUserCustomerDTOList)) {
      Set<String> appUserNos = new HashSet<String>();
//      Set<String> customers = new HashSet<String>();
       for(AppUserCustomerDTO appUserCustomerDTO: appUserCustomerDTOList){
         if(StringUtils.isNotBlank(appUserCustomerDTO.getAppUserNo())) {
           appUserNos.add(appUserCustomerDTO.getAppUserNo());
         }
//         if (appUserCustomerDTO.getAppUserDTO() != null && StringUtils.isNotBlank(appUserCustomerDTO.getAppUserDTO().getName())) {
//           customers.add(appUserCustomerDTO.getAppUserDTO().getName());
//         }
       }
      if (CollectionUtil.isNotEmpty(appUserNos)) {
        setAppUserNos(appUserNos.toArray(new String[appUserNos.size()]));
      }
//      if (CollectionUtil.isNotEmpty(customers)) {
//        setCustomers(customers.toArray(new String[customers.size()]));
//      }
    }
  }
}
