package com.bcgogo.user.service;

import com.bcgogo.common.Pager;
import com.bcgogo.config.dto.TrafficPackageDTO;
import com.bcgogo.config.service.IShopService;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.ConsumingRecordDTO;
import com.bcgogo.user.dto.ConsumingAdminDTO;
import com.bcgogo.user.dto.ConsumingDetailsDTO;
import com.bcgogo.user.dto.ConsumingPageDTO;
import com.bcgogo.user.model.ConsumingRecord;
import com.bcgogo.user.dto.CouponConsumeRecordDTO;
import com.bcgogo.user.model.Coupon;
import com.bcgogo.user.model.UserDaoManager;
import com.bcgogo.user.model.UserWriter;
import com.bcgogo.utils.CollectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Author : ztyu
 * Date: 2015/11/3
 * Time: 11:52
 */
@Service
@Transactional
public class ConsumingService implements IConsumingService {

    private final static Logger LOG = LoggerFactory.getLogger(ConsumingService.class);

    @Autowired
    private UserDaoManager userDaoManager;


    @Override
    public void saveOnsitePay(ConsumingRecord consumingRecord) {
        UserWriter userWriter = userDaoManager.getWriter();
        Object status = userWriter.begin();
        try {
            userWriter.save(consumingRecord);
            userWriter.commit(status);
        }finally {
            userWriter.rollback(status);
        }
    }

    @Override
    public List<ConsumingRecordDTO> findConsumingRecordByShopId(long shopId , int start , int size) {
        UserWriter userWriter = userDaoManager.getWriter();

        List<ConsumingRecordDTO> consumingRecords = userWriter.findConsumingRecordByShopId(shopId, (start - 1) * size, size);
        return consumingRecords;
    }

    @Override
    public Long findConsumingRecordCountByShopId(long shopId) {

        UserWriter userWriter = userDaoManager.getWriter();
        return userWriter.findConsumingRecordCountByShopId(shopId);
    }

    @Override
    public  List<String> countConsumingRecordAndSumCoupon(Long startTime, Long endTime,CouponConsumeRecordDTO couponConsumeRecordDTO){
        UserWriter userWriter = userDaoManager.getWriter();
        return userWriter.countConsumingRecordAndSumCoupon(startTime, endTime, couponConsumeRecordDTO);
    }

    @Override
    public List<CouponConsumeRecordDTO> getConsumingRecordListByPagerTimeArrayType(Long startTime, Long endTime,CouponConsumeRecordDTO couponConsumeRecordDTO,String arrayType, Pager pager){
        UserWriter userWriter = userDaoManager.getWriter();
        return userWriter.getConsumingRecordListByPagerTimeArrayType(startTime, endTime, couponConsumeRecordDTO, arrayType, pager);
    }

    @Override
    public CouponConsumeRecordDTO getCouponConsumeRecordById(Long id){
        UserWriter writer = userDaoManager.getWriter();
        ConsumingRecord consumingRecord= CollectionUtil.getFirst(writer.getCouponConsumeRecordById(id));
        return consumingRecord==null?null:consumingRecord.toCouponConsumeRecordDTO();
    }

    @Override
    public CouponConsumeRecordDTO getCouponConsumeRecordByShopIdAndId(Long shopId,Long id){
        UserWriter writer = userDaoManager.getWriter();
        ConsumingRecord consumingRecord= CollectionUtil.getFirst(writer.getCouponConsumeRecordByShopIdAndId(shopId, id));
        return consumingRecord==null?null:consumingRecord.toCouponConsumeRecordDTO();
    }

    /**
     * 将couponConsumeRecordDTO带有的单据信息，保存到数据库consuming_record表中
     * @param shopId
     * @param couponConsumeRecordDTO  保存有单据相关信息的代金券交易记录DTO
     */
    @Override
    public void updateConsumingRecordFromOrderInfo(Long shopId,CouponConsumeRecordDTO couponConsumeRecordDTO){
        UserWriter userWriter = userDaoManager.getWriter();
        Object status = userWriter.begin();
        try {
            userWriter.updateConsumingRecordFromOrderInfo(shopId, couponConsumeRecordDTO);
            userWriter.commit(status);
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            userWriter.rollback(status);
        }
    }

    @Override
    public List<ConsumingPageDTO> getConsumingRecord(String appUserNo, long dateTime, int count) {
        UserWriter userWriter = userDaoManager.getWriter();
        return userWriter.getConsumingRecord(appUserNo,dateTime,count);
    }

    @Override
    public ConsumingDetailsDTO getConsumingRecord(long consumingId,  File file) {

        UserWriter userWriter = userDaoManager.getWriter();
        ConsumingDetailsDTO consumingDetailsDTO = new ConsumingDetailsDTO();
        ConsumingRecord consumingRecord = userWriter.getConsumingRecord(consumingId);
        consumingDetailsDTO.setOrderCreatedTime(consumingRecord.getCreationDate());
        consumingDetailsDTO.setConsumingTime(consumingRecord.getConsumerTime());
        consumingDetailsDTO.setSumMoney(consumingRecord.getSumMoney());
        consumingDetailsDTO.setCoupon(consumingRecord.getCoupon());
        consumingDetailsDTO.setPayMoney(consumingRecord.getSumMoney()-consumingRecord.getCoupon());
        consumingDetailsDTO.setReceiptNo(consumingRecord.getReceiptNo());
        consumingDetailsDTO.setOrderStatus(consumingRecord.getOrderStatus().getName());

        IShopService shopService = ServiceManager.getService(IShopService.class);
        List<TrafficPackageDTO> trafficPackageDTOs = shopService.getTrafficPackage(file);
        for (TrafficPackageDTO trafficPackageDTO : trafficPackageDTOs){
            if (trafficPackageDTO.getProductId() == consumingRecord.getProductId()){
                consumingDetailsDTO.setProduct(trafficPackageDTO.getName());
                consumingDetailsDTO.setPictureUrl(trafficPackageDTO.getPictureUrl());
                consumingDetailsDTO.setProductSalary(trafficPackageDTO.getPrice());
                break;
            }
        }
        return consumingDetailsDTO;
    }

    public long toOrderListCount(String customerName , String telNumber, String vehicleNumber,
                                 String orderNumber,String goodsName,String orderStatus){
        UserWriter userWriter = userDaoManager.getWriter();
        return userWriter.toOrderListCount(customerName,telNumber, vehicleNumber ,orderNumber ,goodsName,orderStatus);
    }

    public List<ConsumingAdminDTO> toOrderList( String customerName , String telNumber, String vehicleNumber,
                                                String orderNumber,String goodsName,String orderStatus,Pager pager){

        UserWriter userWriter = userDaoManager.getWriter();
        return userWriter.toOrderList(customerName,telNumber, vehicleNumber ,orderNumber ,goodsName,orderStatus,pager);
    }

    public void updateCouponBalance(Coupon coupon){
        UserWriter userWriter = userDaoManager.getWriter();
        Object status = userWriter.begin();
        try {
            userWriter.updateCouponBalance(coupon);
            userWriter.commit(status);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            userWriter.rollback(status);
        }
    }

    /**
     * 代金券消费记录作废
     * @param consumingRecordId
     * @param shopId
     */
    @Override
    public void consumingRecordRepeal(Long shopId,Long consumingRecordId){
        UserWriter userWriter = userDaoManager.getWriter();
        CouponConsumeRecordDTO couponConsumeRecordDTO;
        if(null!=consumingRecordId){
            couponConsumeRecordDTO=getCouponConsumeRecordByShopIdAndId(shopId,consumingRecordId);
        }
        else{
            couponConsumeRecordDTO=new CouponConsumeRecordDTO();
        }
        String appUserNo=couponConsumeRecordDTO.getAppUserNo();
        //获取代金券coupon
        Coupon coupon=userWriter.getCoupon(appUserNo);
        //更新代金券消费记录中的orderStatus和coupon字段
        //更新代金券中的balance字段
        if(null!=couponConsumeRecordDTO.getCoupon()&&null!=coupon) {
            //返回代金券消费记录中的代金券
            coupon.setBalance(coupon.getBalance()+couponConsumeRecordDTO.getCoupon());
            //代金券消费记录作废
            couponConsumeRecordDTO.setCoupon(new Double(0));
            couponConsumeRecordDTO.setOrderStatus(OrderStatus.REPEAL);

            Object status = userWriter.begin();
            try{
                userWriter.updateCouponBalance(coupon);
                userWriter.updateConsumingRecordFromOrderInfo(couponConsumeRecordDTO.getShopId(),couponConsumeRecordDTO);
                userWriter.commit(status);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                userWriter.rollback(status);
            }
        }
    }

    @Override
    public List<CouponConsumeRecordDTO> getOverdueConsumingRecord(Long overdueTime, int start, int size) {
        UserWriter userWriter = userDaoManager.getWriter();
        List<ConsumingRecord> consumingRecords=userWriter.getOverdueConsumingRecord(overdueTime, start, size);
        List<CouponConsumeRecordDTO> couponConsumeRecordDTOs=new ArrayList<CouponConsumeRecordDTO>();
        for(ConsumingRecord record:consumingRecords){
            couponConsumeRecordDTOs.add(record.toCouponConsumeRecordDTO());
        }
        return couponConsumeRecordDTOs;
    }

    @Override
    public void updateAdminStatus(String receiptNo) {
        UserWriter userWriter = userDaoManager.getWriter();
        userWriter.updateAdminStatus(receiptNo);
    }

}
