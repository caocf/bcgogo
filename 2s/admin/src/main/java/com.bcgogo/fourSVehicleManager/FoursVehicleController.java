package com.bcgogo.fourSVehicleManager;

import com.bcgogo.common.Result;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.ShopInternalVehicleGroupDTO;
import com.bcgogo.user.service.intenernalVehicle.IInternalVehicleService;
import com.bcgogo.common.Pager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by XinyuQiu on 14-12-10.
 */
@Controller
@RequestMapping("/fourSVehicle.do")
public class FoursVehicleController {
  private static final Logger LOG = LoggerFactory.getLogger(FoursVehicleController.class);
  //跳转到4S店车辆管理主界面
  @RequestMapping(params = "method=initFourSVehiclePage")
  public String initFourSVehiclePage() {
    return "/fourSVehicle/fourSVehicleMainPage";
  }

  @ResponseBody
  @RequestMapping(params = "method=shopVehicleList")
  public Object shopVehicleList(HttpServletRequest request,Integer page,Integer rows) {
    Map<String,Object> result = new HashMap<String, Object>();
    try{
      IInternalVehicleService internalVehicleService  = ServiceManager.getService(IInternalVehicleService.class);


      int total = internalVehicleService.countShopInternalVehicleGroupByShopId();
      Pager pager = new Pager(total,page,rows);
      result.put("total",total);
      List<ShopInternalVehicleGroupDTO> rowsList = new ArrayList<ShopInternalVehicleGroupDTO>();
      if(total>0){
        rowsList = internalVehicleService.getShopInternalVehicleGroupDTOs(pager);
      }
      result.put("rows",rowsList);
    }catch (Exception e){
      LOG.error(e.getMessage(),e);
    }

    return result;
  }

  @ResponseBody
  @RequestMapping(params = "method=saveOrUpdateShopVehicles")
  public Object saveOrUpdateShopVehicles(HttpServletRequest request,ShopInternalVehicleGroupDTO shopInternalVehicleGroupDTO){
   try{
     IInternalVehicleService internalVehicleService  = ServiceManager.getService(IInternalVehicleService.class);
    Result result = internalVehicleService.saveOrUpdateShopInternalVehicles(shopInternalVehicleGroupDTO);
     return result;
   }catch (Exception e){
     LOG.error(e.getMessage(),e);
     return new Result("网络异常",false);
   }


  }
}
