package com.bcgogo.txn.bcgogoListener.orderListener;

import com.bcgogo.config.dto.OperationLogDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.enums.ObjectTypes;
import com.bcgogo.enums.OperationTypes;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.RemindEventType;
import com.bcgogo.listener.BcgogoEventListener;
import com.bcgogo.search.service.IItemIndexService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.bcgogoListener.orderEvent.StatementAccountEvent;
import com.bcgogo.txn.dto.StatementAccountOrderDTO;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.txn.service.solr.ICustomerOrSupplierSolrWriteService;
import com.bcgogo.txn.service.solr.IOrderSolrWriterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-6-27
 * Time: 下午12:05
 */
public class StatementAccountListener extends BcgogoEventListener {
  public static final Logger LOG = LoggerFactory.getLogger(StatementAccountListener.class);

  private StatementAccountEvent statementAccountEvent;

  public StatementAccountEvent getStatementAccountEvent() {
    return statementAccountEvent;
  }

  public void setStatementAccountEvent(StatementAccountEvent statementAccountEvent) {
    this.statementAccountEvent = statementAccountEvent;
  }

  public StatementAccountListener(StatementAccountEvent statementAccountEvent) {
    super();
    this.statementAccountEvent = statementAccountEvent;
  }

  @Override
  public void run() {
    StatementAccountOrderDTO statementAccountOrderDTO = this.getStatementAccountEvent().getStatementAccountOrderDTO();
    if (statementAccountOrderDTO == null) {
      LOG.error("StatementAccountListener.run(), 对账单 statementAccountOrderDTO 为空");
      return;
    }
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    ICustomerOrSupplierSolrWriteService supplierSolrWriteService = ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class);
    IOrderSolrWriterService orderSolrWriterService = ServiceManager.getService(IOrderSolrWriterService.class);
    IItemIndexService itemIndexService = ServiceManager.getService(IItemIndexService.class);
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    try {
      ShopDTO shopDTO = configService.getShopById(statementAccountOrderDTO.getShopId());

      txnService.updateRemindCountInMemcacheByTypeAndShopId(RemindEventType.DEBT, statementAccountOrderDTO.getShopId());

      //重做客户和供应商索引
      if (OrderTypes.CUSTOMER_STATEMENT_ACCOUNT.equals(statementAccountOrderDTO.getOrderType())) {
        supplierSolrWriteService.reindexCustomerByCustomerId(statementAccountOrderDTO.getCustomerOrSupplierId());
      } else if (OrderTypes.SUPPLIER_STATEMENT_ACCOUNT.equals(statementAccountOrderDTO.getOrderType())) {
        supplierSolrWriteService.reindexSupplierBySupplierId(statementAccountOrderDTO.getCustomerOrSupplierId());
      }
      //更新对账单关联单据的信息
      List<StatementAccountOrderDTO> statementAccountOrderDTOList = statementAccountOrderDTO.getOrderDTOList();
      for (StatementAccountOrderDTO orderDTO : statementAccountOrderDTOList) {
        Long orderId = orderDTO.getOrderId();
        OrderTypes orderTypes = orderDTO.getOrderType();
        orderSolrWriterService.reCreateOrderSolrIndex(shopDTO, orderTypes, orderId);
        if (statementAccountOrderDTO.getOrderType() == OrderTypes.CUSTOMER_STATEMENT_ACCOUNT) {
          //保存对账操作记录
          if(orderDTO.getOrderType() == OrderTypes.SALE) {
            txnService.saveOperationLogTxnService(new OperationLogDTO(statementAccountOrderDTO.getShopId(),
                statementAccountOrderDTO.getSalesManId(), orderDTO.getOrderId(), ObjectTypes.SALE_ORDER, OperationTypes.STATEMENT_ACCOUNT));
          } else if(orderDTO.getOrderType() == OrderTypes.SALE_RETURN) {
            txnService.saveOperationLogTxnService(new OperationLogDTO(statementAccountOrderDTO.getShopId(),
                statementAccountOrderDTO.getSalesManId(), orderDTO.getOrderId(), ObjectTypes.SALE_RETURN_ORDER, OperationTypes.STATEMENT_ACCOUNT));
          } else if(orderDTO.getOrderType() == OrderTypes.REPAIR) {
            txnService.saveOperationLogTxnService(new OperationLogDTO(statementAccountOrderDTO.getShopId(),
                statementAccountOrderDTO.getSalesManId(), orderDTO.getOrderId(), ObjectTypes.REPAIR_ORDER, OperationTypes.STATEMENT_ACCOUNT));
          } else if(orderDTO.getOrderType() == OrderTypes.WASH_BEAUTY) {
            txnService.saveOperationLogTxnService(new OperationLogDTO(statementAccountOrderDTO.getShopId(),
                statementAccountOrderDTO.getSalesManId(), orderDTO.getOrderId(), ObjectTypes.WASH_ORDER, OperationTypes.STATEMENT_ACCOUNT));
          }
          itemIndexService.updateItemIndexArrearsAndPaymentTime(orderId, 0D, statementAccountOrderDTO.getPaymentTime());
        } else if (orderDTO.getOrderType() == OrderTypes.INVENTORY) {
          txnService.saveOperationLogTxnService(new OperationLogDTO(statementAccountOrderDTO.getShopId(),
              statementAccountOrderDTO.getSalesManId(), orderDTO.getOrderId(), ObjectTypes.INVENTORY_ORDER, OperationTypes.STATEMENT_ACCOUNT));
        } else if(orderDTO.getOrderType() == OrderTypes.RETURN) {
          txnService.saveOperationLogTxnService(new OperationLogDTO(statementAccountOrderDTO.getShopId(),
              statementAccountOrderDTO.getSalesManId(), orderDTO.getOrderId(), ObjectTypes.PURCHASE_RETURN_ORDER, OperationTypes.STATEMENT_ACCOUNT));
        }
      }
      //对账单自己的索引
      orderSolrWriterService.reCreateOrderSolrIndex(shopDTO, statementAccountOrderDTO.getOrderType(), statementAccountOrderDTO.getId());
    } catch (Exception e) {
      LOG.error("StatementAccountListener.run() Exception;" + e.getMessage(), e);
    }finally {
      statementAccountEvent.setOrderFlag(true);
    }

  }

}
