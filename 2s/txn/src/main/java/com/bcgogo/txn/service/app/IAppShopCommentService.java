package com.bcgogo.txn.service.app;

import com.bcgogo.api.ApiResponse;
import com.bcgogo.api.AppShopCommentDTO;
import com.bcgogo.common.Pager;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 14-2-19
 * Time: 下午8:28
 */
public interface IAppShopCommentService {

  ApiResponse getAppShopCommentRecord(Long shopId,int pageNo,int pageSize) throws Exception;

  List<AppShopCommentDTO> getAppShopCommentDTOs(Long shopId, Pager pager);

  int countAppShopComments(Long shopId);
}
