package com.bcgogo.txn.service.app;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.api.AppShopCommentDTO;
import com.bcgogo.api.response.ApiPageListResponse;
import com.bcgogo.common.Pager;
import com.bcgogo.enums.app.MessageCode;
import com.bcgogo.enums.app.ValidateMsg;
import com.bcgogo.enums.supplierComment.CommentRecordType;
import com.bcgogo.txn.model.TxnDaoManager;
import com.bcgogo.txn.model.TxnWriter;
import com.bcgogo.txn.model.supplierComment.CommentRecord;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 14-2-19
 * Time: 下午8:29
 */
@Component
public class AppShopCommentService implements IAppShopCommentService {

  @Autowired
  private TxnDaoManager txnDaoManager;
  @Override
  public ApiResponse getAppShopCommentRecord(Long shopId, int pageNo, int pageSize) throws Exception {
    if (shopId == null) {
      return MessageCode.toApiResponse(MessageCode.SHOP_COMMENT_RECORD_LIST_FAIL, ValidateMsg.SHOP_ID_IS_NULL);
    }
    TxnWriter writer = txnDaoManager.getWriter();
    ApiPageListResponse<AppShopCommentDTO> response = new ApiPageListResponse<AppShopCommentDTO>(MessageCode.toApiResponse(MessageCode.SHOP_COMMENT_RECORD_LIST_SUCCESS));
    int count = writer.countAppToShopCommentRecord(shopId, CommentRecordType.APP_TO_SHOP);
    Pager pager = new Pager(count, pageNo, pageSize);
    if (count > 0) {
      List<AppShopCommentDTO> appShopCommentDTOs = getAppShopCommentDTOs(shopId, pager);
      response.setResults(appShopCommentDTOs);
    }
    response.setPager(pager);
    return response;
  }

  @Override
  public List<AppShopCommentDTO> getAppShopCommentDTOs(Long shopId, Pager pager) {
    List<AppShopCommentDTO> appShopCommentDTOs = new ArrayList<AppShopCommentDTO>();
    if(shopId == null || pager == null){
      return  appShopCommentDTOs;
    }
    TxnWriter writer = txnDaoManager.getWriter();
    List<CommentRecord> commentRecords = writer.getCommentRecordByShopIdAndCommentRecordType(shopId, pager, CommentRecordType.APP_TO_SHOP);
    if(CollectionUtils.isNotEmpty(commentRecords)){
      for(CommentRecord commentRecord : commentRecords){
        AppShopCommentDTO appShopCommentDTO = commentRecord.toAppShopCommentDTO();
        appShopCommentDTOs.add(appShopCommentDTO);
      }
    }
    return appShopCommentDTOs;
  }

  @Override
  public int countAppShopComments(Long shopId) {
    if (shopId != null) {
      TxnWriter writer = txnDaoManager.getWriter();
      return writer.countAppToShopCommentRecord(shopId, CommentRecordType.APP_TO_SHOP);
    }
    return 0;
  }


}
