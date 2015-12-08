package com.bcgogo.txnRead.model;

import com.bcgogo.enums.txn.preBuyOrder.BusinessChanceType;
import com.bcgogo.service.GenericReaderDao;
import com.bcgogo.txn.dto.SalesStatDTO;
import com.bcgogo.txn.model.PreBuyOrderItem;
import com.bcgogo.txn.model.SalesStat;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.List;


public class TxnReadReader extends GenericReaderDao {

  public TxnReadReader(SessionFactory sessionFactory) {
    super(sessionFactory);
  }
  /**
   * @param shopId
   * @return
   */
  public List<PreBuyOrderItem> getValidPreBuyOrderItemByShopId(Long shopId,BusinessChanceType... businessChanceType) {

    Session session = getSession();
    try {
      Query q = SQL.getValidPreBuyOrderItemByShopId(session, shopId,businessChanceType);
      return (List<PreBuyOrderItem>) q.list();
    } finally {
      release(session);
    }
  }

  public List<SalesStatDTO> getLastWeekSalesByShopId(Long shopId, Long startTime, Long endTime) {
    Session session = getSession();
    try {
      Query q = SQL.getLastWeekSalesByShopId(session, shopId, startTime, endTime);
      List list = q.list();
      List<SalesStatDTO> salesStatDTOList = new ArrayList<SalesStatDTO>();

      if (CollectionUtils.isNotEmpty(list)) {
        for (int i = 0; i < list.size(); i++) {
          Object[] array = (Object[]) list.get(i);
          SalesStatDTO salesStatDTO = new SalesStatDTO();
          salesStatDTO.setProductId((Long) array[0]);
          salesStatDTO.setAmount((Double) array[1]);
          salesStatDTOList.add(salesStatDTO);
        }
      }
      return salesStatDTOList;
    } finally {
      release(session);
    }
  }

  public List<SalesStatDTO> getLastWeekSalesChangeByShopId(Long shopId, Long startTime, Long endTime) {
    Session session = getSession();
    try {
      Query q = SQL.getLastWeekSalesChangeByShopId(session, shopId, startTime, endTime);
      List list = q.list();
      List<SalesStatDTO> salesStatDTOList = new ArrayList<SalesStatDTO>();

      if (CollectionUtils.isNotEmpty(list)) {
        for (int i = 0; i < list.size(); i++) {
          Object[] array = (Object[]) list.get(i);
          SalesStatDTO salesStatDTO = new SalesStatDTO();
          salesStatDTO.setProductId((Long) array[0]);
          salesStatDTO.setAmount((Double) array[1]);
          salesStatDTOList.add(salesStatDTO);
        }
      }
      return salesStatDTOList;
    } finally {
      release(session);
    }
  }

    public SalesStat getLatestSalesStatBeforeTime(Long shopId, Long productId, long startTimeOfTimeDay) {
    Session session = getSession();
    try {
      Query q = SQL.getLatestSalesStatBeforeTime(session, shopId, productId, startTimeOfTimeDay);
      return (SalesStat) q.uniqueResult();
    } finally {
      release(session);
    }
  }

}
