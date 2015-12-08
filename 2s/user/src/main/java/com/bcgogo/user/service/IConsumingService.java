package com.bcgogo.user.service;

import com.bcgogo.common.Pager;
import com.bcgogo.txn.dto.ConsumingRecordDTO;
import com.bcgogo.user.dto.ConsumingAdminDTO;
import com.bcgogo.user.dto.ConsumingDetailsDTO;
import com.bcgogo.user.dto.ConsumingPageDTO;
import com.bcgogo.user.model.ConsumingRecord;
import com.bcgogo.user.dto.CouponConsumeRecordDTO;

import java.io.File;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Author : ztyu
 * Date: 2015/11/3
 * Time: 11:51
 */
public interface IConsumingService {

    public void saveOnsitePay(ConsumingRecord consumingRecord);

    public List<ConsumingRecordDTO> findConsumingRecordByShopId(long shopId , int start , int size);

    public Long findConsumingRecordCountByShopId(long shopId);

    /**
     * 代金券交易总数和代金券金额总和
     * @param startTime 开始时间
     * @param endTime 截止时间
     * @return  包含代金券交易总数和代金券金额总和的object数组
     * 第0个为交易记录总数，第1个为金额总和
     */
    public List<String> countConsumingRecordAndSumCoupon(Long startTime, Long endTime,CouponConsumeRecordDTO couponConsumeRecordDTO);

    /**
     * 获取代金券交易记录
     * @param startTime 开始时间
     * @param endTime 截止时间
     * @param arrayType 排序方式
     * @param pager 分页信息
     * @return  返回代金券交易记录列表
     */
    public List<CouponConsumeRecordDTO> getConsumingRecordListByPagerTimeArrayType(Long startTime, Long endTime,CouponConsumeRecordDTO couponConsumeRecordDTO,String arrayType, Pager pager);

    /**
     * 通过代金券消费记录id查询对应的记录列表
     * @param id
     * @return 代金券消费记录列表
     */
    public CouponConsumeRecordDTO getCouponConsumeRecordById(Long id);

    /**
     * 单据操作过程中，保存对应的代金券交易记录到数据库
     * @param shopId
     * @param couponConsumeRecordDTO  保存有单据相关信息的代金券交易记录DTO
     * @throws java.text.ParseException
     */
    public void updateConsumingRecordFromOrderInfo(Long shopId,CouponConsumeRecordDTO couponConsumeRecordDTO);

    List<ConsumingPageDTO> getConsumingRecord(String appUserNo , long dateTime , int count);

    ConsumingDetailsDTO getConsumingRecord(long consumingId ,  File file);

    long toOrderListCount(String customerName , String telNumber, String vehicleNumber,
                          String orderNumber,String goodsName,String orderStatus);

    List<ConsumingAdminDTO> toOrderList(String customerName , String telNumber, String vehicleNumber,
                                        String orderNumber,String goodsName,String orderStatus,Pager pager);

    void updateAdminStatus (String receiptNo);

    public CouponConsumeRecordDTO getCouponConsumeRecordByShopIdAndId(Long shopId,Long id);

    /**
     * 将代金券消费记录作废
     * @param shopId
     * @param consumingRecordId
     */
    public void consumingRecordRepeal(Long shopId,Long consumingRecordId);

    /**
     * 获取逾期未处理的空白单据(代金券消费记录)
     * @param overdueTime
     * @param start
     * @param size
     * @return
     */
    public List<CouponConsumeRecordDTO> getOverdueConsumingRecord(Long overdueTime, int start, int size);
}
