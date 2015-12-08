package com.bcgogo.txn.service;

import com.bcgogo.config.model.MergeRecord;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.exception.PageException;
import com.bcgogo.user.dto.CustomerDTO;
import com.bcgogo.user.dto.SupplierDTO;
import com.bcgogo.user.merge.*;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-1-11
 * Time: 上午5:23
 * To change this template use File | Settings | File Templates.
 */
public interface IMergeService {

  MergeResult mergeCustomerHandler(MergeResult<CustomerDTO,MergeCustomerSnap> mergeResult, Long parentId,Long[] childIds) throws Exception ;

  MergeResult mergeSupplierHandler(MergeResult<SupplierDTO,MergeSupplierSnap> mergeResult,Long parentId, Long[] chilIds) throws Exception;

  List getCustomerOrSupplierOrders(Long shopId,OrderTypes orderType,Long[] customerOrSupplierIds);

  MergeResult validateMergeCustomer(MergeResult<CustomerDTO,MergeCustomerSnap> mergeResult,String parentId, String[] chilIdStrs);

  MergeResult validateMergeSupplier(MergeResult<SupplierDTO,MergeSupplierSnap> mergeResult,String parentIdStr, String[] chilIdStrs) throws BcgogoException;

   List<Object> getMergeRecords(MergeRecordDTO mergeRecordIndex) throws PageException;

  SearchMergeResult<MergeRecord> getMergeSnap(SearchMergeResult<MergeRecord> result,Long parentId,Long childId);

}
