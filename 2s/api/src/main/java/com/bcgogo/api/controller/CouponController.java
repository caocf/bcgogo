package com.bcgogo.api.controller;

import com.bcgogo.api.response.ApiCouponResponse;
import com.bcgogo.api.response.ApiIsSharedResponse;
import com.bcgogo.api.response.ApiRecommendResponse;
import com.bcgogo.enums.IncomeType;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.enums.app.MessageCode;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.model.ConsumingRecord;
import com.bcgogo.user.model.Coupon;
import com.bcgogo.user.service.ConsumingService;
import com.bcgogo.user.service.IConsumingService;
import com.bcgogo.user.service.ICouponService;
import com.bcgogo.user.service.app.IAppUserService;
import com.bcgogo.user.service.utils.SessionUtil;
import com.bcgogo.utils.StopWatchUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by IntelliJ IDEA
 * User: ztyu
 * Date: 2015/11/11
 * Time: 20:42.
 */
@Controller
public class CouponController {

    private final static Logger LOG = LoggerFactory.getLogger(ConsumingService.class);

    /**
     * 获取用户代金券总额
     * @param request
     * @param response
     * @param userId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/user/{userId}/coupon/total" , method = RequestMethod.GET)
    public ApiCouponResponse getCoupon(HttpServletRequest request , HttpServletResponse response , @PathVariable String userId)  {
        StopWatchUtil sw = new StopWatchUtil("获取用户代金券总额", "start");
        ApiCouponResponse apiCouponResponse = new ApiCouponResponse();
        try {
            String appUserNo = SessionUtil.getAppUserNo(request, response);
            ICouponService couponService = ServiceManager.getService(ICouponService.class);
            apiCouponResponse.setApiResponse(MessageCode.toApiResponse(MessageCode.APP_COUPON_SUCCESS));
            apiCouponResponse.setData(couponService.getCoupon(appUserNo).getBalance());
        }catch (Exception e){
            LOG.error(e.getMessage());
            apiCouponResponse.setApiResponse(MessageCode.toApiResponse(MessageCode.APP_COUPON_EXCEPTION));
        }
        sw.stopAndPrintLog();
        return apiCouponResponse;
    }

    /**
     * 查询用户是否已经推荐过   1 没有推荐 0 推荐过
     * @param request
     * @param response
     * @param userId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/user/{userId}/recommend" , method = RequestMethod.GET)
    public ApiRecommendResponse getRecommendPhone( HttpServletRequest request ,HttpServletResponse response ,@PathVariable String userId){

        ApiRecommendResponse apiRecommendResponse = new ApiRecommendResponse();
        try {
            String appUserNo = SessionUtil.getAppUserNo(request,response);
            ICouponService couponService = ServiceManager.getService(ICouponService.class);
            if (couponService.getRecommendPhone(appUserNo) != null && couponService.getRecommendPhone(appUserNo) != 0){
                apiRecommendResponse.setData(couponService.getRecommendPhone(appUserNo));    //已经推荐过
                apiRecommendResponse.setApiResponse(MessageCode.toApiResponse(MessageCode.APP_GET_RECOMMEND_SUCCESS));
                return apiRecommendResponse;
            }
            apiRecommendResponse.setApiResponse(MessageCode.toApiResponse(MessageCode.APP_GET_RECOMMEND_SUCCESS));
            return apiRecommendResponse;
        }catch (Exception e){
            LOG.error(e.getMessage());
            apiRecommendResponse.setApiResponse(MessageCode.toApiResponse(MessageCode.APP_RECOMMEND_EXCEPTION));
            return apiRecommendResponse;
        }
    }

    /**
     * 保存推荐人信息，双方互加200代金券
     * @param request
     * @param response
     * @param userId
     * @param phone
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/user/{userId}/recommend" , method = RequestMethod.POST)
    public ApiRecommendResponse saveRecommendPhone( HttpServletRequest request , HttpServletResponse response,
                                                    @PathVariable String userId , @RequestParam long phone){
        ApiRecommendResponse apiRecommendResponse = new ApiRecommendResponse();
        try {
            String appUserNo = SessionUtil.getAppUserNo(request , response);
            ICouponService couponService = ServiceManager.getService(ICouponService.class);
            double coupon = (couponService.getCoupon(appUserNo).getBalance() == null? 0 :couponService.getCoupon(appUserNo).getBalance()) ;
            if (couponService.getRecommendPhone(appUserNo) != null && couponService.getRecommendPhone(appUserNo) != 0){
                apiRecommendResponse.setData(0);
                apiRecommendResponse.setApiResponse(MessageCode.toApiResponse(MessageCode.APP_RECOMMEND_FAIL));
                return apiRecommendResponse;
            }

            IAppUserService appUserService = ServiceManager.getService(IAppUserService.class);
            if (appUserService.getAppUserByPhone(phone) == null){
                apiRecommendResponse.setApiResponse(MessageCode.toApiResponse(MessageCode.APP_PHONE_RECOMMEND_FAIL));
                return apiRecommendResponse;
            }

            /*保存代金券*/
            couponService.saveRecommendPhone(appUserNo,phone ,coupon+200D);
            String recommendAppUserNo = appUserService.getAppUserByPhone(phone).getAppUserNo();
            double recommendCoupon = (couponService.getCoupon(recommendAppUserNo).getBalance() == null ? 0: couponService.getCoupon(recommendAppUserNo).getBalance());
            couponService.saveCoupon(recommendAppUserNo , recommendCoupon+200D);

            /*生成交易记录*/
            IConsumingService consumingService = ServiceManager.getService(IConsumingService.class);
            ConsumingRecord userRecord = new ConsumingRecord();
            userRecord.setAppUserNo(appUserNo);
            userRecord.setCoupon(200D);
            userRecord.setOrderTypes(OrderTypes.APP_GIVE_ORDER);
            userRecord.setConsumerTime(System.currentTimeMillis());
            userRecord.setProduct("推荐礼包");
            userRecord.setIncomeType(IncomeType.INCOME);
            userRecord.setOrderStatus(OrderStatus.APP_ORDER_SUCCESS);
            userRecord.setSumMoney(200D);
            consumingService.saveOnsitePay(userRecord);
            ConsumingRecord recommendRecord = new ConsumingRecord(userRecord);
            recommendRecord.setAppUserNo(recommendAppUserNo);
            consumingService.saveOnsitePay(recommendRecord);

            apiRecommendResponse.setData(1);
            apiRecommendResponse.setApiResponse(MessageCode.toApiResponse(MessageCode.APP_RECOMMEND_SUCCESS));
            return apiRecommendResponse;

        }catch (Exception e){
            e.getMessage();
            LOG.error(e.getMessage());
            apiRecommendResponse.setApiResponse(MessageCode.toApiResponse(MessageCode.APP_RECOMMEND_EXCEPTION));
            return apiRecommendResponse;
        }
    }

    /**
     * 保存微信分享并发放代金券
     * @param request
     * @param response
     * @param userId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/user/{userId}/WXshare" , method = RequestMethod.GET)
    public ApiIsSharedResponse saveIsShared ( HttpServletRequest request , HttpServletResponse response , @PathVariable String userId){

        ApiIsSharedResponse apiIsSharedResponse = new ApiIsSharedResponse();

        try {
            String appUserNo = SessionUtil.getAppUserNo(request , response);
            ICouponService couponService = ServiceManager.getService(ICouponService.class);
            if (couponService.getIsShared(appUserNo) != null && couponService.getIsShared(appUserNo) == 1){
                apiIsSharedResponse.setData(0);
                apiIsSharedResponse.setApiResponse(MessageCode.toApiResponse(MessageCode.APP_SHARED_FAIL));
                return apiIsSharedResponse;
            }

            /*保存代金券*/
            double coupon = (couponService.getCoupon(appUserNo).getBalance() == null ? 0: couponService.getCoupon(appUserNo).getBalance());
            couponService.saveIsShared(appUserNo , 1, coupon+200D);

            /*生成交易记录*/
            ConsumingRecord consumingRecord = new ConsumingRecord();
            consumingRecord.setAppUserNo(appUserNo);
            consumingRecord.setCoupon(200D);
            consumingRecord.setOrderTypes(OrderTypes.APP_GIVE_ORDER);
            consumingRecord.setConsumerTime(System.currentTimeMillis());
            consumingRecord.setProduct("分享礼包");
            consumingRecord.setIncomeType(IncomeType.INCOME);
            consumingRecord.setOrderStatus(OrderStatus.APP_ORDER_SUCCESS);
            consumingRecord.setSumMoney(200D);
            IConsumingService consumingService = ServiceManager.getService(IConsumingService.class);
            consumingService.saveOnsitePay(consumingRecord);

            apiIsSharedResponse.setData(1);
            apiIsSharedResponse.setApiResponse(MessageCode.toApiResponse(MessageCode.APP_SHARED_SUCCESS));
            return apiIsSharedResponse;

        }catch (Exception e){
            LOG.error(e.getMessage());
            apiIsSharedResponse.setApiResponse(MessageCode.toApiResponse(MessageCode.APP_SHARED_EXCEPTION));
            return apiIsSharedResponse;
        }

    }

}
