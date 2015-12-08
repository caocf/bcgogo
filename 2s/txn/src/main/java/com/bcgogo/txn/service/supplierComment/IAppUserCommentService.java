package com.bcgogo.txn.service.supplierComment;

import com.bcgogo.api.ShopOrderCommentDTO;
import com.bcgogo.common.Pager;
import com.bcgogo.common.Result;
import com.bcgogo.common.Sort;
import com.bcgogo.txn.dto.supplierComment.AppUserCommentRecordDTO;
import com.bcgogo.txn.dto.supplierComment.AppUserCommentSearchResultDTO;
import com.bcgogo.txn.dto.supplierComment.CommentRecordDTO;

/**
 * 手机端用户评价店铺
 * Created with IntelliJ IDEA.
 * User: lw
 * Date: 13-8-23
 * Time: 下午5:07
 * To change this template use File | Settings | File Templates.
 */
public interface IAppUserCommentService {

  /**
   * 手机端用户评价单据
   * @param shopOrderCommentDTO
   * @return
   */
  public Result validateAndSaveAppUserCommentShop(ShopOrderCommentDTO shopOrderCommentDTO);

  void saveOrUpdateWXUserCommentRecord(AppUserCommentRecordDTO commentRecordDTO) ;

  /**
   * 根据单据id和
   * @param commentatorShopId
   * @param orderId
   * @return
   */
  public AppUserCommentRecordDTO getAppUserCommentRecordByOrderId(Long  commentatorShopId,Long orderId);

  /**
   * 根据shopId得到该店铺的评论
   */

  public AppUserCommentSearchResultDTO getAppUserCommentRecordByShopId(Long shopId, Pager pager, Sort sort);

  /**
   * 依据shopId和keyword查询评价记录
   * @param shopId
   * @param pager
   * @param commentRecordDTO
   * @return
   */

  public AppUserCommentSearchResultDTO getAppCommentRecordByShopIdAndKeyword(Long shopId,Pager pager,CommentRecordDTO commentRecordDTO) throws Exception;
}
