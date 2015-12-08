package com.bcgogo.api.controller;

import com.bcgogo.api.AppConsumingDTO;
import com.bcgogo.api.AppConsumingDetailsDTO;
import com.bcgogo.api.AppConsumingPagerDTO;
import com.bcgogo.enums.IncomeType;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.app.MessageCode;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.ConsumingDTO;
import com.bcgogo.user.dto.ConsumingPageDTO;
import com.bcgogo.user.dto.CouponDTO;
import com.bcgogo.user.model.ConsumingRecord;
import com.bcgogo.user.service.IConsumingService;
import com.bcgogo.user.service.ICouponService;
import com.bcgogo.user.service.utils.ReceiptNoUtil;
import com.bcgogo.user.service.utils.SessionUtil;
import com.bcgogo.utils.PropUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

/**
 * 消费记录
 * Created by IntelliJ IDEA.
 * Author : ztyu
 * Date: 2015/11/2
 * Time: 17:11
 */
@Controller
public class ConsumingController {


    private static final Logger LOG = LoggerFactory.getLogger(ConsumingController.class);


    /**
     * 商城消费确认
      * @param request
     * @param response
     * @param userId
     * @param consumingRecord
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/user/{userId}/coupon/consumer",method = RequestMethod.PUT)
    public AppConsumingDTO consumingRecordCommit( HttpServletRequest request , HttpServletResponse response,
                                              @PathVariable String userId, @RequestBody ConsumingRecord consumingRecord){
        AppConsumingDTO appConsumingDTO = new AppConsumingDTO();

        try {
            String appUserNo = SessionUtil.getAppUserNo(request, response);

            ICouponService couponService = ServiceManager.getService(ICouponService.class);
            double couponTotal = couponService.getCoupon(appUserNo).getBalance();
            if (consumingRecord.getCoupon() > couponTotal){
                appConsumingDTO.setApiResponse(MessageCode.toApiResponse(MessageCode.APP_MALL_COUPON_FAIL));
                return appConsumingDTO;
            }
            if (consumingRecord.getShopId() == 0 && consumingRecord.getProductId() == 0 &&
                    consumingRecord.getOrderTypes() == null ){
                appConsumingDTO.setApiResponse(MessageCode.toApiResponse(MessageCode.APP_MALL_EXCEPTION));
                return appConsumingDTO;
            }
            if (consumingRecord.getShopId() != 0 && consumingRecord.getProductId() != 0){
                    appConsumingDTO.setApiResponse(MessageCode.toApiResponse(MessageCode.APP_MALL_EXCEPTION));
                    return appConsumingDTO;
            }

            if (consumingRecord.getOrderTypes() == OrderTypes.APP_ONLINE_ORDER){
                if (consumingRecord.getSumMoney() == 0 || consumingRecord.getSumMoney() < consumingRecord.getCoupon()){
                    appConsumingDTO.setApiResponse(MessageCode.toApiResponse(MessageCode.APP_MALL_EXCEPTION));
                    return appConsumingDTO;
                }
                consumingRecord.setAdminStatus(OrderStatus.ADMIN_ORDER_SUBMIT);
                consumingRecord.setOrderStatus(OrderStatus.APP_ORDER_SUCCESS);
            }

            consumingRecord.setAppUserNo(appUserNo);
            consumingRecord.setConsumerTime(System.currentTimeMillis());
            consumingRecord.setIncomeType(IncomeType.EXPENSES);
            consumingRecord.setSumMoney(consumingRecord.getCoupon());
            consumingRecord.setOrderStatus(OrderStatus.REPAIR_DISPATCH);

            consumingRecord.setReceiptNo(ReceiptNoUtil.createReceiptNo());
            CouponDTO couponDTO = couponService.getCoupon(appUserNo);
            couponDTO.setBalance(couponDTO.getBalance() - consumingRecord.getCoupon());
            couponService.saveOrUpdateCoupon(couponDTO);
            IConsumingService consumingService = ServiceManager.getService(IConsumingService.class);
            consumingService.saveOnsitePay(consumingRecord);

            ConsumingDTO consumingDTO = new ConsumingDTO();
            consumingDTO.setCoupon(consumingRecord.getCoupon());
            consumingDTO.setConsumingTime(consumingRecord.getConsumerTime());

            appConsumingDTO.setData(consumingDTO);
            appConsumingDTO.setApiResponse(MessageCode.toApiResponse(MessageCode.APP_MALL_SUCCESS));

            return appConsumingDTO;
        }catch (Exception e){
            LOG.error(e.getMessage());
            appConsumingDTO.setApiResponse(MessageCode.toApiResponse(MessageCode.APP_COUPON_EXCEPTION));
            return appConsumingDTO;
        }
    }


    /**
     * 获取消费列表
     * @param request
     * @param response
     * @param userId
     * @param datetime
     * @param count
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/user/{userId}/consuming/history/{datetime}/limit/{count}" , method = RequestMethod.GET)
    public AppConsumingPagerDTO getConsumingRecord(HttpServletRequest request ,HttpServletResponse response,
                                                   @PathVariable String userId , @PathVariable long datetime ,@PathVariable int count){

        AppConsumingPagerDTO appConsumingPagerDTO = new AppConsumingPagerDTO();
        if(datetime == 0 || count == 0 ){
            appConsumingPagerDTO.setApiResponse(MessageCode.toApiResponse(MessageCode.APP_CONSUMING_PARAM_FAIL));
            return appConsumingPagerDTO;
        }
        try {
            String appUserNo = SessionUtil.getAppUserNo(request,response);
            IConsumingService consumingService = ServiceManager.getService(IConsumingService.class);
            List<ConsumingPageDTO> consumingPageDTOs = consumingService.getConsumingRecord(appUserNo , datetime , count);
            appConsumingPagerDTO.setData(consumingPageDTOs);
            appConsumingPagerDTO.setApiResponse(MessageCode.toApiResponse(MessageCode.APP_CONSUMING_SUCCESS));
            return appConsumingPagerDTO;
        }catch (Exception e){
            LOG.error(e.getMessage());
            appConsumingPagerDTO.setApiResponse(MessageCode.toApiResponse(MessageCode.APP_CONSUMING_EXCEPTION));
            return appConsumingPagerDTO;
        }
    }

    /**
     * 在线订单明细
     * @param request
     * @param response
     * @param userId
     * @param consumingId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/user/{userId}/order/{consumingId}" , method = RequestMethod.GET)
    public AppConsumingDetailsDTO getConsumingDetails(HttpServletRequest request ,HttpServletResponse response,
                                                      @PathVariable String userId ,@PathVariable long consumingId){

        AppConsumingDetailsDTO appConsumingDetailsDTO = new AppConsumingDetailsDTO();

        if (consumingId == 0){
            appConsumingDetailsDTO.setApiResponse(MessageCode.toApiResponse(MessageCode.APP_CONSUMING_DETAILS_PARAM_FAIL));
            return appConsumingDetailsDTO;
        }

        try {
            IConsumingService consumingService = ServiceManager.getService(IConsumingService.class);
            File f = new File(PropUtil.getProductPath());
            appConsumingDetailsDTO.setData(consumingService.getConsumingRecord(consumingId,f));
            appConsumingDetailsDTO.setApiResponse(MessageCode.toApiResponse(MessageCode.APP_CONSUMING_DETAILS_SUCCESS));
            return appConsumingDetailsDTO;
        } catch (Exception e){
            LOG.error(e.getMessage());
            appConsumingDetailsDTO.setApiResponse(MessageCode.toApiResponse(MessageCode.APP_CONSUMING_DETAILS_EXCEPTION));
            return appConsumingDetailsDTO;
        }
    }

}
