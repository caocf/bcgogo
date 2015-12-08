package com.bcgogo.user.service.appPay.aliPay;


import com.bcgogo.appPay.aliPay.AliPayConstant;
import com.bcgogo.appPay.aliPay.utils.AliPayRSAUtil;
import com.bcgogo.appPay.aliPay.utils.AliPaySignUtil;
import com.bcgogo.config.util.MemCacheAdapter;
import com.bcgogo.enums.IncomeType;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.CouponDTO;
import com.bcgogo.user.model.ConsumingRecord;
import com.bcgogo.user.service.IConsumingService;
import com.bcgogo.user.service.ICouponService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Map;

/**
 * Created by IntelliJ IDEA
 * User: ztyu
 * Date: 2015/12/2
 * Time: 14:19.
 */

@Component
public class AliPayService implements IAliPayService {

    private static final Logger LOG = LoggerFactory.getLogger(AliPayService.class);

    @Override
    public String createPrepareOrder(Map map) {

        String orderInfo = AliPaySignUtil.orderInfo(map);
        LOG.info(orderInfo);
        //对订单做RSA签名
        String test = AliPayConstant.ALI_PRIVATE_KEY.replaceAll("/","");
        String sign = AliPayRSAUtil.sign(orderInfo , AliPayConstant.ALI_PRIVATE_KEY , AliPayConstant.ALI_ECODING );

        MemCacheAdapter.add(map.get(AliPayConstant.ALI_RECEIPT_NO).toString() , map ,
                new Date(System.currentTimeMillis() + AliPayConstant.ALI_PAY_TIME *1000L ));
        LOG.info(map.get(AliPayConstant.ALI_RECEIPT_NO).toString());
        return orderInfo + "&sign=\"" + sign + "\"&"+AliPayConstant.ALI_SIGN_TYPE;
    }

    @Override
    public void notifySuccess(Map map) {

        Map orderMap = (Map) MemCacheAdapter.get(map.get(AliPayConstant.ALI_NOTIFY_RECEIPT_NO).toString());

        //生成订单
        ConsumingRecord consumingRecord = new ConsumingRecord();
        consumingRecord.setAppUserNo(orderMap.get(AliPayConstant.ALI_APPUSER_NO).toString());
        consumingRecord.setOrderStatus(OrderStatus.APP_ORDER_SUCCESS);
        consumingRecord.setConsumerTime(System.currentTimeMillis());
        consumingRecord.setSumMoney(Double.valueOf(orderMap.get(AliPayConstant.ALI_PRODUCT_PRICE).toString()));
        consumingRecord.setIncomeType(IncomeType.EXPENSES);
        consumingRecord.setAdminStatus(OrderStatus.ADMIN_ORDER_SUBMIT);
        consumingRecord.setCoupon(Double.valueOf(orderMap.get(AliPayConstant.ALI_COUPON).toString()));
        consumingRecord.setProductId(Long.valueOf(orderMap.get(AliPayConstant.ALI_PRODUCT_ID).toString()));
        consumingRecord.setProduct(orderMap.get(AliPayConstant.ALI_PRODUCT).toString());
        consumingRecord.setOrderTypes(OrderTypes.APP_ONLINE_ORDER);
        consumingRecord.setReceiptNo(orderMap.get(AliPayConstant.ALI_RECEIPT_NO).toString());

        ICouponService couponService = ServiceManager.getService(ICouponService.class);
        CouponDTO couponDTO = couponService.getCoupon(consumingRecord.getAppUserNo());
        LOG.info(couponDTO.getBalance().toString());
        couponDTO.setBalance(couponDTO.getBalance() - consumingRecord.getCoupon());
        couponService.saveOrUpdateCoupon(couponDTO);
        LOG.info(couponDTO.getBalance().toString());

        IConsumingService consumingService = ServiceManager.getService(IConsumingService.class);
        consumingService.saveOnsitePay(consumingRecord);

        MemCacheAdapter.delete(orderMap.get(AliPayConstant.ALI_RECEIPT_NO).toString());
    }
}
