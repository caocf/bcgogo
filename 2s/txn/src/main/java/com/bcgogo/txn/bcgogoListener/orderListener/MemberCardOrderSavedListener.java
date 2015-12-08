package com.bcgogo.txn.bcgogoListener.orderListener;

import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.bcgogoListener.orderEvent.MemberCardOrderSavedEvent;
import com.bcgogo.txn.dto.MemberCardOrderDTO;
import com.bcgogo.txn.service.solr.ICustomerOrSupplierSolrWriteService;
import com.bcgogo.txn.service.solr.IOrderSolrWriterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by IntelliJ IDEA.
 * User: liuWei
 * Date: 12-4-12
 * Time: 下午1:34
 */
public class MemberCardOrderSavedListener extends OrderSavedListener {
  public static final Logger LOG = LoggerFactory.getLogger(MemberCardOrderSavedListener.class);

  public MemberCardOrderSavedEvent getMemberCardOrderSavedEvent() {
    return memberCardOrderSavedEvent;
  }

  public void setMemberCardOrderSavedEvent(MemberCardOrderSavedEvent memberCardOrderSavedEvent) {
    this.memberCardOrderSavedEvent = memberCardOrderSavedEvent;
  }

  private MemberCardOrderSavedEvent memberCardOrderSavedEvent;


  public MemberCardOrderSavedListener(MemberCardOrderSavedEvent memberCardOrderSavedEvent) {
    super();
    this.memberCardOrderSavedEvent = memberCardOrderSavedEvent;
  }

  public void run() {
     MemberCardOrderDTO memberCardOrderDTO = this.getMemberCardOrderSavedEvent().getMemberCardOrderDTO();
     //会员卡购卡续卡不算入营业额 因此 isBusinessStat为false
    businessStatByOrder(memberCardOrderDTO,false,false,memberCardOrderDTO.getVestDate());
    reCreateSolrIndex(memberCardOrderDTO);

    memberCardOrderSavedEvent.setOrderFlag(true);
  }

  private void reCreateSolrIndex(MemberCardOrderDTO memberCardOrderDTO) {
    //reindex order solr
    ServiceManager.getService(IOrderSolrWriterService.class).reCreateOrderSolrIndex(ServiceManager.getService(IConfigService.class).getShopById(memberCardOrderDTO.getShopId()), OrderTypes.MEMBER_BUY_CARD, memberCardOrderDTO.getId());
    //reindex customer in solr
    ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerByCustomerId(memberCardOrderDTO.getCustomerId());
  }
}
