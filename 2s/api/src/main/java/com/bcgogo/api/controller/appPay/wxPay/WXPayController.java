package com.bcgogo.api.controller.appPay.wxPay;

import com.bcgogo.api.AppWXPrepareOrderDTO;
import com.bcgogo.appPay.wxPay.WXPayConstant;
import com.bcgogo.appPay.wxPay.WXPrepareOrder;
import com.bcgogo.config.dto.TrafficPackageDTO;
import com.bcgogo.config.service.IShopService;
import com.bcgogo.enums.app.MessageCode;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.service.ICouponService;
import com.bcgogo.user.service.appPay.wxPay.IWXPayService;
import com.bcgogo.user.service.utils.SessionUtil;
import com.bcgogo.utils.PropUtil;
import com.bcgogo.utils.XMLParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA
 * User: ztyu
 * Date: 2015/11/30
 * Time: 9:52.
 */
@Controller
public class WXPayController {

    private static final Logger LOG = LoggerFactory.getLogger(WXPayController.class);

    @ResponseBody
    @RequestMapping(value = "/user/{userId}/prepay/wx" , method = RequestMethod.POST)
    public AppWXPrepareOrderDTO createPrepareOrder(HttpServletResponse response , HttpServletRequest request ,
                                                 @PathVariable String userId ,@RequestParam String productId , @RequestParam String coupon){

        IWXPayService payService = ServiceManager.getService(IWXPayService.class);
        AppWXPrepareOrderDTO prepareOrderDTO = new AppWXPrepareOrderDTO();

        if (productId == null || productId == "0"){
            prepareOrderDTO.setApiResponse(MessageCode.toApiResponse(MessageCode.WX_PREPARE_ORDER_EXCEPTION));
            return prepareOrderDTO;
        }
        try {

            String appUserNo = SessionUtil.getAppUserNo(request , response);
            //查询商品文件
            File f = new File(PropUtil.getProductPath());
            IShopService shopService = ServiceManager.getService(IShopService.class);
            List<TrafficPackageDTO> trafficPackageDTOs = shopService.getTrafficPackage(f);
            for (TrafficPackageDTO trafficPackageDTO : trafficPackageDTOs){
                if (trafficPackageDTO.getProductId() == Long.valueOf(productId)){

                    LOG.info(trafficPackageDTO.getProductId()+"");
                    //检测代金券使用是否合法
                    ICouponService couponService = ServiceManager.getService(ICouponService.class);
                    double userCoupon = couponService.getCoupon(appUserNo).getBalance();
                    if (Double.valueOf(coupon) > userCoupon || Double.valueOf(coupon) > trafficPackageDTO.getDisCount()){
                        prepareOrderDTO.setApiResponse(MessageCode.toApiResponse(MessageCode.WX_PREPARE_ORDER_EXCEPTION));
                        return prepareOrderDTO;
                    }

                    int money = (int) (trafficPackageDTO.getPrice()*100)  - (int) (Double.valueOf(coupon)*100) ;

                    Map<String , String> map = new HashMap<String, String>();
                    map.put(WXPayConstant.ORDER_USER , appUserNo);
                    map.put(WXPayConstant.ORDER_COUPON , coupon);
                    map.put(WXPayConstant.ORDER_SUM_MONEY , trafficPackageDTO.getPrice()+"");
                    map.put(WXPayConstant.ORDER_PRODUCT_ID ,productId);
                    map.put(WXPayConstant.ORDER_PRODUCT , trafficPackageDTO.getName());

                    WXPrepareOrder prepareOrder = payService.createPrepareOrder(map, String.valueOf(money));
                    if (prepareOrder == null){
                        prepareOrderDTO.setApiResponse(MessageCode.toApiResponse(MessageCode.WX_PREPARE_ORDER_FAIL));
                        return prepareOrderDTO;
                    }

                    prepareOrderDTO.setData(prepareOrder);
                    prepareOrderDTO.setApiResponse(MessageCode.toApiResponse(MessageCode.WX_PREPARE_ORDER_SUCCESS));
                    return prepareOrderDTO;
                }
            }
            prepareOrderDTO.setApiResponse(MessageCode.toApiResponse(MessageCode.APP_COUPON_FAIL));
            return prepareOrderDTO;
        }catch (Exception e){
            LOG.error(e.getMessage());
            prepareOrderDTO.setApiResponse(MessageCode.toApiResponse(MessageCode.WX_PREPARE_ORDER_EXCEPTION));
            return prepareOrderDTO;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/user/wxprepayOrder" , method = RequestMethod.POST )
    public void wxPaySuccess(@RequestBody String notifyStr , HttpServletResponse response){
        try {
            Map map = XMLParser.parseXml(notifyStr);
            if (map.get(WXPayConstant.PREPARE_ORDER_RESULT_CODE).equals(WXPayConstant.WXPAY_SUCCESS)
                    && map.get(WXPayConstant.PREPARE_ORDER_RETURN_CODE).equals(WXPayConstant.WXPAY_SUCCESS) ){
                IWXPayService payService = ServiceManager.getService(IWXPayService.class);
                payService.wxPaySuccess(map);
                response.getWriter().write(WXPayConstant.NOTIFY_SUCCESS);
            }
            response.getWriter().write(WXPayConstant.NOTIFY_FAIL);
        }catch (Exception e){
            LOG.error(e.getMessage());
        }
    }
}
