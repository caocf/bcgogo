package com.bcgogo.txn.service;

import com.bcgogo.enums.OrderTypes;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.exception.PageException;
import com.bcgogo.search.dto.DraftOrderSearchDTO;
import com.bcgogo.txn.dto.DraftOrderDTO;
import com.bcgogo.txn.model.DraftOrder;

import java.text.ParseException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 12-9-8
 * Time: 上午5:15
 * To change this template use File | Settings | File Templates.
 */
public interface IDraftOrderService {

  public List<DraftOrderDTO> getDraftOrders(DraftOrderSearchDTO draftOrderSearchDTO) throws PageException, ParseException;

  public DraftOrder lazyLoadDraftOrderId(Long shopId,Long draftOrderId);

  public List<DraftOrder> getDraftOrdersByCustomerOrSupplierId(Long shopId,Long customerId);

  public DraftOrderDTO getOrderByDraftOrderId(Long shopId,Long shopVersionId,Long draftOrderId) throws Exception;

  public DraftOrderDTO saveOrUpdateDraftOrder(DraftOrderDTO draftOrderDTO) throws BcgogoException;

  public int countDraftOrders(DraftOrderSearchDTO draftOrderSearchDTO) throws Exception;

  public List countDraftOrderByOrderType(DraftOrderSearchDTO draftOrderSearchDTO) throws Exception;

  public DraftOrder deleteDraftOrder(Long shopId,Long draftOrderId) throws BcgogoException;

  public DraftOrder deleteDraftOrderByTxnOrderId(Long shopId,Long txnOrderId) throws BcgogoException;


  public DraftOrderDTO getDraftOrderByVechicle(Long shopId,Long shopVersionId,String vechicle) throws Exception;

  public DraftOrderDTO getDraftOrderByTxnOrderId(Long shopId,Long shopVersionId,Long txnOrderId) throws Exception;

  public List<DraftOrder> getDraftOrder(Long shopId,List<OrderTypes> orderTypesList,Long customerId);

  public List<DraftOrder> deleteDraftOrderList(List<DraftOrder> draftOrderList) throws BcgogoException;

}
