package com.bcgogo.notification.service;

import com.bcgogo.common.Pager;
import com.bcgogo.notification.model.*;
import com.bcgogo.utils.*;
import com.bcgogo.wx.*;
import com.bcgogo.wx.message.WXMCategory;
import com.bcgogo.wx.WXMsgDTO;
import com.bcgogo.wx.WXMsgReceiverDTO;
import com.bcgogo.wx.user.WXMsgSearchCondition;
import com.bcgogo.wx.WXSendStatusReportDTO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 14-12-17
 * Time: 下午5:08
 */
@Component
public class WXService implements IWXService {

  @Autowired
  private NotificationDaoManager daoManager;


  @Override
  public WXMsgDTO getWXMsgDTOById(String id) {
    return getWXMsgDTOById(NumberUtil.longValue(id));
  }

  @Override
  public WXMsgDTO getWXMsgByIdAndShopId(Long shopId, Long msgId) {
    NotificationWriter writer = daoManager.getWriter();
    WXMsg wxMsg = writer.getWXMsgByIdAndShopId(shopId, msgId);
    return wxMsg != null ? wxMsg.toDTO() : null;
  }

  /**
   * 注意区分 getWXMsgById
   *
   * @param msgId 微信平台返回的msgId
   * @return
   */
  @Override
  public WXMsgDTO getWXMsgDTOByMsgId(String msgId) {
    NotificationWriter writer = daoManager.getWriter();
    WXMsg wxMsg = writer.getWXMsgByMsgId(msgId);
    return wxMsg != null ? wxMsg.toDTO() : null;
  }

  @Override
  public void saveOrUpdateWXMsg(WXMsgDTO msgDTO) {
    NotificationWriter writer = daoManager.getWriter();
    Object status = writer.begin();
    WXMsg msg = null;
    try {
      if (msgDTO.getId() != null) {
        msg = getWXMsgById(msgDTO.getId());
      } else {
        msg = new WXMsg();
      }
      msg.fromDTO(msgDTO);
      writer.saveOrUpdate(msg);
      writer.commit(status);
      msgDTO.setId(msg.getId());
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public WXMsgDTO getWXMsgDTOById(Long id) {
    WXMsg wxMsg = getWXMsgById(id);
    return wxMsg != null ? wxMsg.toDTO() : null;
  }

  @Override
  public WXMsg getWXMsgById(Long id) {
    NotificationWriter writer = daoManager.getWriter();
    return writer.getWXMsgById(id);
  }

  /**
   * 查询审核中的素材
   *
   * @param id
   * @return
   */
  @Override
  public WXMsg getAuditingMsgById(Long id) {
    NotificationWriter writer = daoManager.getWriter();
    return writer.getAuditingMsgById(id);
  }

  //待审核数量获取
  @Override
  public int getCountAdultJob(WXMsgDTO wxMsgDTO, String type) {
    NotificationWriter writer = daoManager.getWriter();
    return writer.getCountAdultJob(wxMsgDTO, type);

  }

  //待审核列表
  @Override
  public List<WXMsgDTO> getAdultJobs(WXMsgDTO wxMsgDTO, Pager pager, String type) {
    NotificationWriter writer = daoManager.getWriter();
    //根据shopID分页查询
    List<WXMsg> wxMsgs = writer.getAdultJobs(wxMsgDTO, pager, type);
    if (CollectionUtils.isEmpty(wxMsgs))
      return new ArrayList<WXMsgDTO>();
    List<WXMsgDTO> wxMsgDTOs = new ArrayList<WXMsgDTO>();
    for (WXMsg w : wxMsgs) {
      if (w == null) continue;            //f为空跳过
      WXMsgDTO toWxMsgDTO = w.toDTO();
      wxMsgDTOs.add(toWxMsgDTO);
    }
    return wxMsgDTOs;
  }

  //待审核数量获取
  @Override
  public int getCountWXMsgReceiverById(Long msgLocalId) {
    NotificationWriter writer = daoManager.getWriter();
    return writer.getCountWXMsgReceiverByMsgLocalId(msgLocalId);

  }

  @Override
  public List<WXMsgReceiver> getWXMsgReceiverByMsgLocalId(Long msgLocalId) {
    NotificationWriter writer = daoManager.getWriter();
    return writer.getWXMsgReceiverByMsgLocalId(msgLocalId);
  }

  //修改微信待审核
  public void modifyAudit(WXMsg wXMsg) {
    NotificationWriter writer = daoManager.getWriter();
    Object status = writer.begin();
    try {
      writer.update(wXMsg);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public List<WXMsgDTO> getShopWXMsgRecord(Long shopId, Pager pager) {
    NotificationWriter writer = daoManager.getWriter();
    WXMsgSearchCondition condition = new WXMsgSearchCondition();
    condition.setShopId(shopId);
    condition.setPager(pager);
    WXMCategory[] categoryList = {WXMCategory.MASS, WXMCategory.SERVICE, WXMCategory.TEMPLATE};
    condition.setCategoryList(categoryList);
    List<WXMsg> msgList = writer.getMsg(condition);
    if (CollectionUtil.isEmpty(msgList)) return null;
    List<WXMsgDTO> msgDTOList = new ArrayList<WXMsgDTO>();
    for (WXMsg msg : msgList) {
      msgDTOList.add(msg.toDTO());
    }
    return msgDTOList;
  }

  @Override
  public int countShopWXMsgRecord(Long shopId) {
    NotificationWriter writer = daoManager.getWriter();
    WXMsgSearchCondition condition = new WXMsgSearchCondition();
    condition.setShopId(shopId);
    WXMCategory[] categoryList = {WXMCategory.MASS, WXMCategory.SERVICE, WXMCategory.TEMPLATE};
    condition.setCategoryList(categoryList);
    return writer.countMsg(condition);
  }


  /**
   * @param msgId 微信平台返回的msgId
   * @return
   */
  @Override
  public WXSendStatusReportDTO getWXSendStatusReportDTOByMsgId(String msgId) {
    WXSendStatusReport report = getWXSendStatusReportByMsgId(msgId);
    return report != null ? report.toDTO() : null;
  }

  /**
   * @param msgId 微信平台返回的msgId
   * @return
   */
  @Override
  public WXSendStatusReport getWXSendStatusReportByMsgId(String msgId) {
    if (StringUtil.isEmpty(msgId)) return null;
    NotificationWriter writer = daoManager.getWriter();
    return writer.getWXSendStatusReportByMsgId(msgId);
  }


  /**
   * 获取店铺当月群发消息数量
   *
   * @param shopId
   * @return
   * @throws java.text.ParseException
   */
  @Override
  public int countShopMonthWXMassMsg(Long shopId) throws ParseException {
    NotificationWriter writer = daoManager.getWriter();
    WXMsgSearchCondition condition = new WXMsgSearchCondition();
    condition.setShopId(shopId);
    condition.setStartSendTime(DateUtil.getStartTimeOfMonth(0));
    condition.setEndSendTime(DateUtil.getEndTimeOfToday());
    WXMCategory[] categories = {WXMCategory.MASS};
    condition.setCategoryList(categories);
    WXMsgStatus[] statusList = {WXMsgStatus.AUDITING, WXMsgStatus.AUDITING_FAILED, WXMsgStatus.SENT, WXMsgStatus.SUCCESS};
    condition.setStatusList(statusList);
    return writer.countMsg(condition);
  }

  @Override
  public int countShopMonthSuccessMassMsg(Long shopId) throws ParseException {
    NotificationWriter writer = daoManager.getWriter();
    WXMsgSearchCondition condition = new WXMsgSearchCondition();
    condition.setShopId(shopId);
    condition.setStartSendTime(DateUtil.getStartTimeOfMonth(0));
    condition.setEndSendTime(DateUtil.getEndTimeOfToday());
    WXMCategory[] categories = {WXMCategory.MASS};
    condition.setCategoryList(categories);
    WXMsgStatus[] statusList = {WXMsgStatus.SUCCESS};
    condition.setStatusList(statusList);
    return writer.countMsg(condition);
  }


  @Override
  public void saveOrUpdateWXMsgReceiver(WXMsgReceiverDTO... receiverDTOs) {
    if (ArrayUtil.isEmpty(receiverDTOs)) return;
    NotificationWriter writer = daoManager.getWriter();
    Object status = writer.begin();
    try {
      for (WXMsgReceiverDTO receiverDTO : receiverDTOs) {
        WXMsgReceiver receiver = null;
        if (receiverDTO.getId() != null) {
          receiver = getWXMsgReceiverById(receiverDTO.getId());
        } else {
          receiver = new WXMsgReceiver();
        }
        receiver.fromDTO(receiverDTO);
        writer.saveOrUpdate(receiver);
        receiverDTO.setId(receiver.getId());
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void saveOrUpdateWXSendStatusReport(WXSendStatusReportDTO reportDTO) {
    NotificationWriter writer = daoManager.getWriter();
    Object status = writer.begin();
    WXSendStatusReport report = null;
    try {
      if (reportDTO.getId() != null) {
        report = getWXSendStatusReportByMsgId(reportDTO.getMsgId());
      } else {
        report = new WXSendStatusReport();
      }
      report.fromDTO(reportDTO);
      writer.saveOrUpdate(report);
      writer.commit(status);
      reportDTO.setId(report.getId());
    } finally {
      writer.rollback(status);
    }
  }


  @Override
  public WXMsgReceiver getWXMsgReceiverById(Long id) {
    NotificationWriter writer = daoManager.getWriter();
    return writer.getById(WXMsgReceiver.class, id);
  }


  @Override
  public void saveWXMsgReceiver(String openId, Long msgId) {
    NotificationWriter writer = daoManager.getWriter();
    Object status = writer.begin();
    try {
      WXMsgReceiver receiver = new WXMsgReceiver();
      receiver.setOpenId(openId);
      receiver.setMsgId(msgId);
      writer.save(receiver);
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public void saveWXMsgReceiver(WXMsgReceiver... receivers) {
    if (ArrayUtil.isEmpty(receivers)) return;
    ;
    NotificationWriter writer = daoManager.getWriter();
    Object status = writer.begin();
    try {
      for (WXMsgReceiver receiver : receivers) {
        writer.save(receiver);
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  /**
   * @param id 本地数据库生成的id
   * @return
   */
  @Override
  public WXShopBill getWXShopBillById(Long id) {
    NotificationWriter writer = daoManager.getWriter();
    return writer.getById(WXShopBill.class, id);
  }

  @Override
  public void saveOrUpdateWXShopBill(WXShopBillDTO billDTO) {
    NotificationWriter writer = daoManager.getWriter();
    Object status = writer.begin();
    WXShopBill bill = null;
    try {
      if (billDTO.getId() != null) {
        bill = getWXShopBillById(billDTO.getId());
      } else {
        bill = new WXShopBill();
      }
      bill.fromDTO(billDTO);
      writer.saveOrUpdate(bill);
      writer.commit(status);
      billDTO.setId(bill.getId());
    } finally {
      writer.rollback(status);
    }
  }

  @Override
  public int countWXShopBill(Long shopId) {
    if (shopId == null) return 0;
    NotificationWriter writer = daoManager.getWriter();
    return writer.countWXShopBill(shopId);

  }

  @Override
  public List<WXShopBillDTO> getWXShopBill(Long shopId, Pager pager) {
    if (shopId == null) return null;
    NotificationWriter writer = daoManager.getWriter();
    List<WXShopBill> bills = writer.getWXShopBill(shopId, pager);
    List<WXShopBillDTO> billDTOs = new ArrayList<WXShopBillDTO>();
    for (WXShopBill bill : bills) {
      billDTOs.add(bill.toDTO());
    }
    return billDTOs;
  }


  @Override
  public Object getWXShopBillStat(Long shopId) {
    NotificationWriter writer = daoManager.getWriter();
    return writer.getWXShopBillStat(shopId);
  }


}
