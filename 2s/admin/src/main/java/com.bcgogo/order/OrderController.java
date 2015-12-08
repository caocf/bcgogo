package com.bcgogo.order;

import com.bcgogo.common.Pager;
import com.bcgogo.user.dto.ConsumingAdminDTO;
import com.bcgogo.user.service.IConsumingService;
import com.bcgogo.utils.JsonUtil;
import com.bcgogo.utils.NumberUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA
 * User: ztyu
 * Date: 2015/11/17
 * Time: 16:16.
 */
@Controller
@RequestMapping("/order.do")
public class OrderController {

    private static final Logger LOG = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private IConsumingService consumingService;

    @RequestMapping(params = "method=toOrder")
    public String toOrder(){
        return "/order/orderList";
    }

    @RequestMapping(params = "method=toOrderList")
    public void toOrderList(ModelMap model, HttpServletResponse response,
                            String customerName , String telNumber, String vehicleNumber,
                            String orderNumber,String goodsName,String orderStatus, Integer startPageNo ){

        List<ConsumingAdminDTO> consumingAdminDTOs = new ArrayList<ConsumingAdminDTO>();
        try {
            int total = (int) consumingService.toOrderListCount(customerName,telNumber, vehicleNumber ,orderNumber ,goodsName,orderStatus);
//            startPageNo = 1;
            Pager pager = new Pager(total ,NumberUtil.intValue(String.valueOf(startPageNo) ,1));
            consumingAdminDTOs = consumingService.toOrderList(customerName,telNumber, vehicleNumber ,orderNumber ,goodsName,orderStatus,pager);
            /*JSON*/
            String jsonStr = "";
            jsonStr = JsonUtil.listToJson(consumingAdminDTOs);
            jsonStr = jsonStr.substring(0, jsonStr.length() - 1);
            if (!"[".equals(jsonStr.trim())) {
                jsonStr = jsonStr + "," + pager.toJson().substring(1, pager.toJson().length());
            } else {
                jsonStr = pager.toJson();
            }
            PrintWriter writer = response.getWriter();
            writer.write(jsonStr);
            writer.close();
        }catch (Exception e){
            LOG.debug("/order.do");
            LOG.debug("method=toOrderList");
            LOG.error(e.getMessage(), e);
        }
        model.addAttribute("consumingAdminDTOs", consumingAdminDTOs);
    }

    @RequestMapping(params = "method=confirmOrder")
    public void test(ModelMap model, HttpServletResponse response,String orderId) throws IOException {
        LOG.info(orderId);
        String jsonStr = "";
        PrintWriter writer = response.getWriter();
        try {
            consumingService.updateAdminStatus(orderId);
            jsonStr = "{\"isSuccess\":\"true\"}";
            writer.write(jsonStr);
            writer.close();
        }catch (Exception e){
            LOG.error(e.getMessage());
            jsonStr = "{\"isSuccess\":\"true\"}";
            writer.write(jsonStr);
            writer.close();
        }
    }
}
