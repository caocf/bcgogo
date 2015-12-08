package com.bcgogo.api.controller.appPay.aliPay;

import com.bcgogo.api.AppAliPrepareOrderDTO;
import com.bcgogo.appPay.aliPay.AliPayConstant;
import com.bcgogo.config.dto.TrafficPackageDTO;
import com.bcgogo.config.service.IShopService;
import com.bcgogo.enums.app.MessageCode;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.service.ICouponService;
import com.bcgogo.user.service.appPay.aliPay.IAliPayService;
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
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA
 * User: ztyu
 * Date: 2015/12/2
 * Time: 14:33.
 */
@Controller
public class AlipayController {

    private static final Logger LOG = LoggerFactory.getLogger(AlipayController.class);

    /**
     * 生成sign返回客户端
     * @param response
     * @param request
     * @param userId
     * @param productId
     * @param coupon
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/user/{userId}/prepay/alipay" , method = RequestMethod.POST)
    public AppAliPrepareOrderDTO createPrepareOrder(HttpServletResponse response ,HttpServletRequest request ,
                                                    @PathVariable String userId , @RequestParam String productId ,@RequestParam String coupon){
        LOG.info(coupon);
        IAliPayService payService = ServiceManager.getService(IAliPayService.class);
        AppAliPrepareOrderDTO prepareOrderDTO = new AppAliPrepareOrderDTO();
        if (productId == null || productId == "0"){
            prepareOrderDTO.setApiResponse(MessageCode.toApiResponse(MessageCode.WX_PREPARE_ORDER_EXCEPTION));
            return prepareOrderDTO;
        }
        try {

            String appUserNo = SessionUtil.getAppUserNo(request, response);
            //查询商品文件
            File f = new File(PropUtil.getProductPath());
            IShopService shopService = ServiceManager.getService(IShopService.class);
            List<TrafficPackageDTO> trafficPackageDTOs = shopService.getTrafficPackage(f);
            for (TrafficPackageDTO trafficPackageDTO : trafficPackageDTOs){
                if (trafficPackageDTO.getProductId() == Long.valueOf(productId)){

                    //检测代金券使用是否合法
                    ICouponService couponService = ServiceManager.getService(ICouponService.class);
                    double userCoupon = couponService.getCoupon(appUserNo).getBalance();
                    if (Double.valueOf(coupon) > userCoupon || Double.valueOf(coupon) > trafficPackageDTO.getDisCount()){
                        prepareOrderDTO.setApiResponse(MessageCode.toApiResponse(MessageCode.WX_PREPARE_ORDER_EXCEPTION));
                        return prepareOrderDTO;
                    }

                    double money = trafficPackageDTO.getPrice()  - Double.valueOf(coupon) ;
                    DecimalFormat df = new DecimalFormat("0.00");
                    String moneyStr = df.format(money);
                    Map<String , String> map = new HashMap<String, String>();
                    map.put(AliPayConstant.ALI_APPUSER_NO , appUserNo);
                    map.put(AliPayConstant.ALI_PRODUCT , trafficPackageDTO.getName());
                    map.put(AliPayConstant.ALI_PRODUCT_ID,productId);
                    map.put(AliPayConstant.ALI_PRODUCT_PRICE,String.valueOf(trafficPackageDTO.getPrice()));
                    map.put(AliPayConstant.ALI_COUPON,coupon);
                    map.put(AliPayConstant.ALI_PRICE , moneyStr);
                    map.put(AliPayConstant.ALI_PRODUCT_DES , trafficPackageDTO.getIntroduce());
                    map.put(AliPayConstant.ALI_RECEIPT_NO , ReceiptNoUtil.createReceiptNo());

                    prepareOrderDTO.setData(payService.createPrepareOrder(map));
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
    @RequestMapping( value = "/user/aliprepayOrder" , method = RequestMethod.POST)
    public void AlipayNotifyUrl(HttpServletRequest request,HttpServletResponse response ,@RequestBody String notifyStr) throws IOException {


        String[] array = notifyStr.split("&");
        Map map = new HashMap();
        for (String str : array){
            String[] arr = str.split("=");
            map.put(arr[0] , arr[1]);
        }
        if (map.get("trade_status").toString().equals("TRADE_SUCCESS")){
            IAliPayService payService = ServiceManager.getService(IAliPayService.class);
            payService.notifySuccess(map);
        }
        response.getWriter().write("success");

    }
}
