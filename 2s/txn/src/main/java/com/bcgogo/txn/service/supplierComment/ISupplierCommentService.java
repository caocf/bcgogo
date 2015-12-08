package com.bcgogo.txn.service.supplierComment;

import com.bcgogo.common.Pager;
import com.bcgogo.common.Result;
import com.bcgogo.common.Sort;
import com.bcgogo.config.dto.ApplyShopSearchCondition;
import com.bcgogo.enums.supplierComment.CommentStatus;
import com.bcgogo.txn.dto.supplierComment.AppUserCommentRecordDTO;
import com.bcgogo.txn.dto.supplierComment.CommentRecordDTO;
import com.bcgogo.txn.dto.supplierComment.CommentStatDTO;
import com.bcgogo.txn.dto.supplierComment.SupplierCommentRecordDTO;
import com.bcgogo.txn.model.supplierComment.CommentRecord;
import com.bcgogo.user.dto.SupplierDTO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 供应商点评专用service
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-3-13
 * Time: 下午5:36
 * To change this template use File | Settings | File Templates.
 */
public interface ISupplierCommentService {

  /**
   * 保存或者更新供应商评价记录
   * @param supplierCommentRecordDTO
   */
  public void saveOrUpdateSupplierCommentRecord(SupplierCommentRecordDTO supplierCommentRecordDTO);

  /**
   * 保存手机端评价记录
   * @param commentRecord
   */
  public void saveOrUpdateCommentRecord(CommentRecord commentRecord);

  /**
   * 在评价之前进行校验:
   * 1.是否所有项目都已打分
   * 2.是否是在线采购单
   * 3.采购单是否已经评价过
   * @param supplierCommentRecordDTO
   * @return
   */
  public Result validateBeforeSupplierComment(SupplierCommentRecordDTO supplierCommentRecordDTO);


  /**
   * 根据采购单id和shopId获得采购单评价记录
   * @param orderId
   * @param customerShopId
   * @return
   */
  public SupplierCommentRecordDTO getCommentRecordByOrderId(Long orderId,Long customerShopId);

  /**
   * 根据供应商评价记录id追加内容
   * @param supplierCommentRecordDTO
   */
  public Result addSupplierCommentContent(SupplierCommentRecordDTO supplierCommentRecordDTO);

  /**
   * 根据店铺id统计该店铺的点评数据
   * @param shopId
   * @throws Exception
   */
  public void commentStatByShopId(Long shopId,Long shopVersionId) throws Exception;


  /**
   * 根据供应商店铺id和点评记录状态来获得点评记录
   * @param supplierShopId
   * @param commentStatus
   * @return
   */
  public List<CommentRecord> getCommentRecordByShopId(Long supplierShopId, CommentStatus commentStatus);

  /**
   * 根据供应商店铺id获得该店铺的点评统计值
   * @param supplierShopId
   * @return
   */
  public CommentStatDTO getCommentStatByShopId(Long supplierShopId);

  CommentStatDTO getShopCommentStat(Long supplierShopId);
  /**
   * 保存或者更新供应商点评统计数据
   *
   * @param commentStatDTO
   */
  public void saveOrUpdateSupplierCommentStat(CommentStatDTO commentStatDTO);

  /**
   * 根据某个店铺关联的供应商id获得该供应商的店铺id
   * @param supplierId
   * @return
   */
  public Long getSupplierShopIdBySupplierId(Long supplierId);


  /**
   * 根据供应商店铺id统计record记录条数
   * @param shopId
   * @param commentStatus
   * @return
   */
  public int countSupplierCommentRecord(Long shopId, CommentStatus commentStatus);

  /**
   * 分页获得点评记录
   * @param shopId
   * @param commentStatus
   * @param pager
   * @param sort
   * @return
   */
  public List<SupplierCommentRecordDTO> getSupplierCommentByPager(Long shopId, CommentStatus commentStatus,Pager pager,Sort sort);


  /**
   * 根据供应商 获得供应商评分统计值
   * @param supplierShopIds
   * @return
   */
  public Map<Long,CommentStatDTO> getCommentStatByShopIds(Collection<Long> supplierShopIds);


  /**
   * 设置每个供应商的评价分数
   * @param supplierDTOList
   * @param shopSearchConditions
   */
  public void setSupplierCommentStat(Collection<SupplierDTO> supplierDTOList,Collection<ApplyShopSearchCondition> shopSearchConditions);

  /**
   * 依据shopId和搜索关键字查询评价记录条数
   * @param shopId
   * @param commentRecordDTO
   * @return
   */
  public int countSupplierCommentRecordByKeyword(Long shopId,CommentRecordDTO commentRecordDTO);


  /**
   * 依据shopId和搜索关键字获得差评\中评\好评个数
   * @param shopId
   * @param commentRecordDTO
   * @param commentType
   * @return
   */
  public int countCommentTypeRecordByKeyword(Long shopId,CommentRecordDTO commentRecordDTO,String commentType);







}
