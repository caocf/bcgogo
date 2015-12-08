package com.bcgogo.web.init;

import com.bcgogo.config.service.IBaseStationService;
import com.bcgogo.user.service.IConsumingService;
import com.bcgogo.user.service.app.IAppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.*;

/**
 * Created by Administrator on 2015/10/23.
 */
@Controller
public class Test {

    @Autowired
    private IBaseStationService baseStationService;


    public double test() throws IOException {
        Map map = new HashMap();
        map.put("mcc",460);
        map.put("mnc",13824);
        map.put("lac",0);
        map.put("ci",291);
//        map.put("mcc",1);
//        map.put("mnc",1);
//        map.put("lac",1);
//        map.put("ci",1);
        return baseStationService.findStationByMncAndLacAndCi(map).getLon();
    }


    @Autowired
    private IConsumingService consumingService;
    @Autowired
    private IAppUserService appUserService;
//    @RequestMapping("/yyyyy" )
//    @ResponseBody
//    public List<ConsumingRecordDTO> test1(){
//
//        ConsumingRecordDTO consumingRecordDTO = new ConsumingRecordDTO();
//        List<ConsumingRecordDTO> list = new ArrayList<ConsumingRecordDTO>();
//        List<ConsumingRecord> consumingRecords = consumingService.findConsumingRecordByShopId(4, 0, 5);
//        Set<String> userIdSet = new HashSet<String>();
//        for (ConsumingRecord consumingRecord :  consumingRecords){
//            userIdSet.add(consumingRecord.getAppUserNo());
//        }
//
//        Map<String,AppUser> userMap = appUserService.getAppUserByUserId(userIdSet);
//        Map<String,AppVehicle> vehicleMap = appUserService.getAppVehicleByUserId(userIdSet);
//
//        for (ConsumingRecord consumingRecord :consumingRecords){
//            consumingRecordDTO.setCoupon(consumingRecord.getCoupon());
//            consumingRecordDTO.setCustomerName(userMap.get(consumingRecord.getAppUserNo()).getName());
//            consumingRecordDTO.setOrderId(consumingRecord.getOrderId());
//            consumingRecordDTO.setOrderType(consumingRecord.getOrderTypes().getName());
//            consumingRecordDTO.setTime(consumingRecord.getConsumerTime());
//            consumingRecordDTO.setVehicleNo(vehicleMap.get(consumingRecord.getAppUserNo()).getVehicleNo());
//            consumingRecordDTO.setUserName(userMap.get(consumingRecord.getAppUserNo()).getName());
//            list.add(consumingRecordDTO);
//        }
//
//        return list;
//    }


}
