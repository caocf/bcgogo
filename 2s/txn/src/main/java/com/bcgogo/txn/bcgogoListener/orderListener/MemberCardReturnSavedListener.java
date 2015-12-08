package com.bcgogo.txn.bcgogoListener.orderListener;

import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.bcgogoListener.orderEvent.MemberCardReturnSavedEvent;
import com.bcgogo.txn.dto.MemberCardReturnDTO;
import com.bcgogo.txn.service.solr.ICustomerOrSupplierSolrWriteService;
import com.bcgogo.txn.service.solr.IOrderSolrWriterService;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 12-10-18
 * Time: 上午11:56
 */
public class MemberCardReturnSavedListener extends OrderSavedListener {

  private MemberCardReturnSavedEvent memberCardReturnSavedEvent;

  public MemberCardReturnSavedEvent getMemberCardReturnSavedEvent() {
    return memberCardReturnSavedEvent;
  }

  public void setMemberCardOrderSavedEvent(MemberCardReturnSavedEvent memberCardReturnSavedEvent) {
    this.memberCardReturnSavedEvent = memberCardReturnSavedEvent;
  }

  public MemberCardReturnSavedListener(MemberCardReturnSavedEvent memberCardReturnSavedEvent) {
    super();
    this.memberCardReturnSavedEvent = memberCardReturnSavedEvent;
  }

  public void run() {
    MemberCardReturnDTO memberCardReturnDTO = this.getMemberCardReturnSavedEvent().getMemberCardReturnDTO();
    //reindex order
    ServiceManager.getService(IOrderSolrWriterService.class).reCreateOrderSolrIndex(ServiceManager.getService(IConfigService.class).getShopById(memberCardReturnDTO.getShopId()), OrderTypes.MEMBER_RETURN_CARD, memberCardReturnDTO.getId());

    //重建客户索引
    ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerByCustomerId(memberCardReturnDTO.getCustomerId());

    //会员卡退卡不计入营业统计
    businessStatByOrder(memberCardReturnDTO, false, false,memberCardReturnDTO.getReturnDate());
    memberCardReturnSavedEvent.setOrderFlag(true);
  }
}