package com.bcgogo.txn.service;

import com.bcgogo.txn.dto.RepairOrderDTO;
import com.bcgogo.txn.dto.SalesOrderDTO;
import com.bcgogo.txn.dto.WashBeautyOrderDTO;
import com.bcgogo.wx.WXArticleDTO;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 14-12-18
 * Time: 下午1:57
 */
public interface IWXTxnService {


  void sendConsumeMsg(SalesOrderDTO salesOrderDTO);

  void sendConsumeMsg(RepairOrderDTO repairOrderDTO );

  void sendConsumeMsg(WashBeautyOrderDTO washBeautyOrderDTO);

  void doWXRemindEvent() throws Exception;

}
