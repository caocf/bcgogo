package com.bcgogo.txn.model;

import com.bcgogo.common.Pager;
import com.bcgogo.enums.FaultAlertType;
import com.bcgogo.enums.YesNo;
import com.bcgogo.enums.shop.ShopKind;
import com.bcgogo.enums.txn.Status;
import com.bcgogo.enums.txn.pushMessage.PushMessageType;
import com.bcgogo.service.GenericReaderDao;
import com.bcgogo.txn.dto.pushMessage.faultCode.FaultInfoSearchConditionDTO;
import com.bcgogo.txn.dto.pushMessage.faultCode.FaultInfoToShopDTO;
import com.bcgogo.txn.model.app.AppUserBill;
import com.bcgogo.txn.model.pushMessage.faultCode.FaultInfoToShop;
import com.bcgogo.txn.model.sql.AppSQL;
import com.bcgogo.txn.model.sql.MessageSQL;
import com.bcgogo.utils.NumberUtil;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.List;


public class TxnReader extends GenericReaderDao {

  public TxnReader(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  public List<AppUserBill> getAppUserBillListByUserNo(String appUserNo, int pageSize, int currentPage) {
    Session session = getSession();
    try {
      Query query = AppSQL.getAppUserBillListByUserNo(session, appUserNo, pageSize, currentPage);
      return query.list();
    } finally {
      release(session);
    }
  }

  public int countAppUserBillListByUserNo(String appUserNo) {
    Session session = getSession();
    try {
      Query query = AppSQL.countAppUserBillListByUserNo(session, appUserNo);
      Long o = (Long) query.uniqueResult();
      return o == null ? 0 : o.intValue();
    } finally {
      release(session);
    }
  }

  public int countShopFaultInfoList(FaultInfoSearchConditionDTO searchCondition) {
    Session session = getSession();
    try {
      Query query = MessageSQL.countShopFaultInfoList(session, searchCondition);
      Long o = (Long) query.uniqueResult();
      return o == null ? 0 : o.intValue();
    } finally {
      release(session);
    }
  }

  public List<FaultInfoToShop> searchShopFaultInfoList(FaultInfoSearchConditionDTO searchCondition) {
    Session session = getSession();
    try {
      Query query = MessageSQL.searchShopFaultInfoList(session, searchCondition);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<String> getShopFaultInfoVehicleNoSuggestion(Long shopId, String keyword) {
    Session session = getSession();
    try {
      Query query = MessageSQL.getShopFaultInfoVehicleNoSuggestion(session, shopId,keyword);
      return query.list();
    } finally {
      release(session);
    }
  }

  public List<String> getShopFaultInfoMobileSuggestion(Long shopId, String keyword) {
    Session session = getSession();
    try {
      Query query = MessageSQL.getShopFaultInfoMobileSuggestion(session, shopId,keyword);
      return query.list();
    } finally {
      release(session);
    }
  }

  public int countRemindMileageCustomerRemind(Long shopId) {
    Session session = this.getSession();
    try {
      Query q = SQL.countRemindMileageCustomerRemind(session, shopId);
      return ((Long) q.uniqueResult()).intValue();
    } finally {
      release(session);
    }
  }

  public List<RemindEvent> getRemindMileageCustomerRemind(Long shopId, Pager pager) {
    Session session = this.getSession();
    try {
      Query q = SQL.getRemindMileageCustomerRemind(session, shopId, pager);
      return q.list();
    } finally {
      release(session);
    }
  }

  //代办事项-故障查询提醒
  public List<FaultInfoToShopDTO> searchShopFaultInfoList_(FaultInfoSearchConditionDTO searchCondition) {
    Session session = this.getSession();
    try {
      Query query = MessageSQL.searchShopFaultInfoList_(session,searchCondition);
      List<Object[]> result = query.list();
      List<FaultInfoToShopDTO> faultInfoToShopDTOList = new ArrayList<FaultInfoToShopDTO>();
      if (CollectionUtils.isNotEmpty(result)) {
        for (Object[] objects : result) {
          FaultInfoToShopDTO faultInfoToShopDTO = new FaultInfoToShopDTO();
          faultInfoToShopDTO.setId(NumberUtil.longValue(objects[0]));
          faultInfoToShopDTO.setShopId(NumberUtil.longValue(objects[1]));
          faultInfoToShopDTO.setAppVehicleFaultInfoId(NumberUtil.longValue(objects[2]));
          faultInfoToShopDTO.setFaultCodeInfoId(NumberUtil.longValue(objects[3]));
          faultInfoToShopDTO.setFaultCode(StringUtil.valueOf(objects[4]));
          faultInfoToShopDTO.setFaultCodeCategory(StringUtil.valueOf(objects[5]));
          faultInfoToShopDTO.setFaultCodeDescription(StringUtil.valueOf(objects[6]));
          faultInfoToShopDTO.setAppUserNo(StringUtil.valueOf(objects[7]));
          faultInfoToShopDTO.setAppVehicleId(NumberUtil.longValue(objects[8]));
          faultInfoToShopDTO.setVehicleNo(StringUtil.valueOf(objects[9]));
          faultInfoToShopDTO.setVehicleBrand(StringUtil.valueOf(objects[10]));
          faultInfoToShopDTO.setVehicleModel(StringUtil.valueOf(objects[11]));
          faultInfoToShopDTO.setMobile(StringUtil.valueOf(objects[12]));
          faultInfoToShopDTO.setFaultCodeReportTime(NumberUtil.longValue(objects[13]));
          faultInfoToShopDTO.setIsSendMessage(YesNo.valueOf(StringUtil.valueOf(objects[14])));
          faultInfoToShopDTO.setIsCreateAppointOrder(YesNo.valueOf(StringUtil.valueOf(objects[15])));
          faultInfoToShopDTO.setStatus(Status.valueOf(StringUtil.valueOf(objects[16])));
          faultInfoToShopDTO.setAppointOrderId(NumberUtil.longValue(objects[17]));
          faultInfoToShopDTO.setFaultAlertType(FaultAlertType.valueOf(StringUtil.valueOf(objects[18])));
          faultInfoToShopDTO.setLon(StringUtil.valueOf(objects[19]));
          faultInfoToShopDTO.setLat(StringUtil.valueOf(objects[20]));
          faultInfoToShopDTOList.add(faultInfoToShopDTO);
        }
      }
      return faultInfoToShopDTOList;
    } finally {
      release(session);
    }
  }

  public int countShopFaultInfoList_(FaultInfoSearchConditionDTO searchCondition) {
    Session session = getSession();
    try {
      Query query = MessageSQL.countShopFaultInfoList_(session, searchCondition);
      return NumberUtil.intValue(query.uniqueResult());
    } finally {
      release(session);
    }
  }

  public List<FaultInfoToShop> getShopFaultInfoByFaultCode(FaultInfoSearchConditionDTO searchCondition) {
    Session session = this.getSession();
    try {
      Query q = SQL.getShopFaultInfoByFaultCode(session, searchCondition);
      return q.list();
    } finally {
      release(session);
    }
  }



}

