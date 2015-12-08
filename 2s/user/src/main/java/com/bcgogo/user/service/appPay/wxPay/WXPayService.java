package com.bcgogo.user.service.appPay.wxPay;

import com.bcgogo.appPay.wxPay.WXPayConstant;
import com.bcgogo.appPay.wxPay.WXPrepareOrder;
import com.bcgogo.appPay.wxPay.utils.WXPaySignUtil;
import com.bcgogo.config.util.MemCacheAdapter;
import com.bcgogo.enums.IncomeType;
import com.bcgogo.enums.OrderStatus;
import com.bcgogo.enums.OrderTypes;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.CouponDTO;
import com.bcgogo.user.model.ConsumingRecord;
import com.bcgogo.user.service.IConsumingService;
import com.bcgogo.user.service.ICouponService;
import com.bcgogo.user.service.utils.ReceiptNoUtil;
import com.bcgogo.utils.XMLParser;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by IntelliJ IDEA
 * User: ztyu
 * Date: 2015/11/27
 * Time: 14:37.
 */
@Component
public class WXPayService  implements  IWXPayService{

    private static final Logger LOG = LoggerFactory.getLogger(WXPayService.class);


    @Override
    public WXPrepareOrder createPrepareOrder(Map orderMap, String money) {
        try{
            URL url = new URL(WXPayConstant.PREPARE_ORDER_URL);
            URLConnection urlConnection = url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("content-type", "application/xml");
            // 得到请求的输出流对象
            OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream() , "utf-8");
            // 把数据写入请求的Body

            String productStr = new String(orderMap.get(WXPayConstant.ORDER_PRODUCT).toString().getBytes("utf-8"));
            String receiptNo = ReceiptNoUtil.createReceiptNo();
            String postXML = WXPaySignUtil.postXML(money, productStr, receiptNo);
            out.write(postXML);
            out.flush();
            out.close();

            // 从服务器读取响应
            InputStream inputStream = urlConnection.getInputStream();
            String body = IOUtils.toString(inputStream, WXPayConstant.CHARACTER_ENCODING);

            Map map = XMLParser.parseXml(body);

            WXPrepareOrder prepareOrder = new WXPrepareOrder();

            LOG.info(map.get("result_code").toString());

            if (map.get(WXPayConstant.PREPARE_ORDER_RESULT_CODE).equals(WXPayConstant.WXPAY_SUCCESS)
                    && map.get(WXPayConstant.PREPARE_ORDER_RETURN_CODE).equals(WXPayConstant.WXPAY_SUCCESS) ){
                prepareOrder.setNonceStr(WXPaySignUtil.getCharAndNumr(20));
                prepareOrder.setPrepayId(map.get("prepay_id").toString());
                prepareOrder.setTimeStamp(String.valueOf(System.currentTimeMillis()/1000));

                /**生成第二次签名**/
                SortedMap<Object,Object> parameters = new TreeMap<Object,Object>();
                parameters.put("appid", WXPayConstant.APP_ID);
                parameters.put("partnerid", WXPayConstant.MCH_ID);
                parameters.put("prepayid", map.get("prepay_id"));
                parameters.put("package", WXPayConstant.PACKAGE);
                parameters.put("noncestr",prepareOrder.getNonceStr());
                parameters.put("timestamp",prepareOrder.getTimeStamp());

                MemCacheAdapter.add(receiptNo, orderMap, new Date(System.currentTimeMillis()+WXPayConstant.PREPARE_ORDER_TIME*1000L));
                prepareOrder.setSign(WXPaySignUtil.createSign(WXPayConstant.CHARACTER_ENCODING, parameters));
            }
            return prepareOrder;
        }catch(Exception e){
            LOG.error(e.getMessage());
            return null;
        }
    }

    @Override
    public void wxPaySuccess(Map map) {
        if (MemCacheAdapter.get(map.get(WXPayConstant.ORDER_RECEIPT_NO).toString()) != null){
            Map orderMap = (Map) MemCacheAdapter.get(map.get(WXPayConstant.ORDER_RECEIPT_NO).toString());

            //生成订单
            ConsumingRecord consumingRecord = new ConsumingRecord();
            consumingRecord.setAppUserNo(orderMap.get(WXPayConstant.ORDER_USER).toString());
            consumingRecord.setOrderStatus(OrderStatus.APP_ORDER_SUCCESS);
            consumingRecord.setConsumerTime(System.currentTimeMillis());
            consumingRecord.setSumMoney(Double.valueOf(orderMap.get(WXPayConstant.ORDER_SUM_MONEY).toString()));
            consumingRecord.setIncomeType(IncomeType.EXPENSES);
            consumingRecord.setAdminStatus(OrderStatus.ADMIN_ORDER_SUBMIT);
            consumingRecord.setCoupon(Double.valueOf(orderMap.get(WXPayConstant.ORDER_COUPON).toString()));
            consumingRecord.setProductId(Long.valueOf(orderMap.get(WXPayConstant.ORDER_PRODUCT_ID).toString()));
            consumingRecord.setProduct(orderMap.get(WXPayConstant.ORDER_PRODUCT).toString());
            consumingRecord.setOrderTypes(OrderTypes.APP_ONLINE_ORDER);
            consumingRecord.setReceiptNo(map.get(WXPayConstant.ORDER_RECEIPT_NO).toString());

            LOG.info("*********************************开始扣除代金券");
            ICouponService couponService = ServiceManager.getService(ICouponService.class);
            CouponDTO couponDTO = couponService.getCoupon(consumingRecord.getAppUserNo());
            LOG.info("***********************************现有代金券"+couponDTO.getBalance().toString());
            couponDTO.setBalance(couponDTO.getBalance() - consumingRecord.getCoupon());
            LOG.info("***********************************所剩代金券"+couponDTO.getBalance().toString());
            couponService.saveOrUpdateCoupon(couponDTO);


            IConsumingService consumingService = ServiceManager.getService(IConsumingService.class);
            consumingService.saveOnsitePay(consumingRecord);

            MemCacheAdapter.delete(map.get(WXPayConstant.ORDER_RECEIPT_NO).toString());
        }
    }


}
