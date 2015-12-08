package com.bcgogo.notification.service;

import com.bcgogo.common.Pager;
import com.bcgogo.notification.model.WXMsg;
import com.bcgogo.notification.model.WXMsgReceiver;
import com.bcgogo.notification.model.WXSendStatusReport;
import com.bcgogo.notification.model.WXShopBill;
import com.bcgogo.wx.WXMsgDTO;
import com.bcgogo.wx.WXMsgReceiverDTO;
import com.bcgogo.wx.WXSendStatusReportDTO;
import com.bcgogo.wx.WXShopBillDTO;

import java.text.ParseException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 14-12-17
 * Time: 下午5:08
 */
public interface IWXService {

  WXMsg getWXMsgById(Long id);

  WXMsgDTO getWXMsgDTOById(String id);

  WXMsgDTO getWXMsgDTOById(Long msgId);

  WXMsgDTO getWXMsgDTOByMsgId(String msgId);

  WXMsgDTO getWXMsgByIdAndShopId(Long shopId,Long msgId);

  void saveOrUpdateWXMsg(WXMsgDTO msgDTO);

    WXSendStatusReportDTO getWXSendStatusReportDTOByMsgId(String msgId);

  WXSendStatusReport getWXSendStatusReportByMsgId(String msgId);

  int countShopMonthWXMassMsg(Long shopId) throws ParseException;

  int countShopMonthSuccessMassMsg(Long shopId) throws ParseException;

  WXMsg getAuditingMsgById(Long msgId);

  List<WXMsgDTO> getShopWXMsgRecord(Long shopId,Pager pager);

  int countShopWXMsgRecord(Long shopId);


  void saveOrUpdateWXMsgReceiver(WXMsgReceiverDTO... receiverDTOs);

  void saveOrUpdateWXSendStatusReport(WXSendStatusReportDTO reportDTO);

  WXMsgReceiver getWXMsgReceiverById(Long id);

   int getCountAdultJob(WXMsgDTO wxMsgDTO,String type);

  List<WXMsgDTO> getAdultJobs(WXMsgDTO wxMsgDTO, Pager pager,String type);

   int getCountWXMsgReceiverById(Long msgLocalId);

  List<WXMsgReceiver> getWXMsgReceiverByMsgLocalId(Long msgLocalId);

  void modifyAudit(WXMsg wXMsg);

  void saveWXMsgReceiver(WXMsgReceiver... wxMsgReceiver);

  void saveWXMsgReceiver(String openId,Long msgId);

  WXShopBill getWXShopBillById(Long id);

  void saveOrUpdateWXShopBill(WXShopBillDTO billDTO);

  int countWXShopBill(Long shopId);

  List<WXShopBillDTO> getWXShopBill(Long shopId,Pager pager);

  Object getWXShopBillStat(Long shopId);


}
