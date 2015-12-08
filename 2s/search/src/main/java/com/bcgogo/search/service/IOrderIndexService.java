package com.bcgogo.search.service;

import com.bcgogo.enums.OrderTypes;
import com.bcgogo.search.dto.OrderIndexDTO;
import com.bcgogo.txn.dto.MemberCardOrderDTO;
import com.bcgogo.txn.dto.MemberCardReturnDTO;

import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: wjl
 * Date: 12-4-10
 * Time:2:05
 * To change this template use File | Settings | File Templates.
 */
public interface IOrderIndexService {
  /**
   * OrderIndexSOLR
   *
   * @param coll
   * @throws Exception
   * @author wjl
   */
  public void addOrderIndexToSolr(Collection<OrderIndexDTO> coll) throws Exception;

  /**
   * @param shopId
   * @param q
   * @param start
   * @param rows
   * @return
   * @throws Exception
   */
  public List<OrderIndexDTO> getOrderIndexByServiceWork(Long shopId, String q, Long startTime, Long endTime, int start, int rows) throws Exception;

  /**
   * from get from solr
   * @param shopId
   * @param orderId
   * @return
   * @throws Exception
   */
  public List<OrderIndexDTO> getByOrderId(long shopId, Long orderId) throws Exception;

  /**
   * @param shopId
   * @param orderId
   * @return
   * @throws Exception
   */
  public OrderIndexDTO getOrderIndexByOrderId(Long shopId, Long orderId) throws Exception;

  /**
   * for order reindex
   * 获得orderId
   * @param shopId 可以为null 代表所有shop
   * @param startId
   * @return
   */
  List<OrderIndexDTO> getOrderIndexForReindex(Long shopId, long startId,  long endId) throws Exception;

  public Long getOrderIndexMaxId(Long shopId, long startId, int pageSize) throws Exception;

  /**
   * clear order core
   *
   * @throws Exception
   */
  public void deleteOrderFromSolr(OrderTypes orderType, Long shopId) throws Exception;


  public int getOrderIndexSizeByServiceWorker(Long shopId,String assistantName, Long startTime, Long endTime)throws Exception;

  /**
   * 保存购卡单的同时把信息保存到orderIndex和itemIndex (在memberCardOrder保存成功后才保存，所以要判断id)
   *
   * @param memberCardOrderDTO
   * @throws Exception
   */
  public void saveOrderIndexAndItemIndexOfMemberCardOrder(MemberCardOrderDTO memberCardOrderDTO) throws Exception;

  public Long getRepairOrderIndexMaxId(Long shopId, long startId, int pageSize) throws Exception;

  void saveOrderIndexAndItemIndexOfMemberCardReturn(MemberCardReturnDTO memberCardReturnDTO) throws Exception;

  void saveOrUpdateOrderIndex(OrderIndexDTO orderIndexDTO) throws Exception;
}
