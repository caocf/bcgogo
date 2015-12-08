package com.bcgogo.customer;

import com.bcgogo.common.WebUtil;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.stat.dto.CostStatConditionDTO;
import com.bcgogo.stat.dto.VehicleServeMonthStatDTO;
import com.bcgogo.txn.service.IVehicleStatService;
import com.bcgogo.utils.DateUtil;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 成本统计controller
 * User: Jimuchen
 * Date: 12-10-27
 * Time: 上午2:03
 * To change this template use File | Settings | File Templates.
 */

@Controller
@RequestMapping("/vehicleStat.do")
public class VehicleStatController {
  private static final Logger LOG = LoggerFactory.getLogger(VehicleStatController.class);

  private static final int TOP_LIMIT = 10;

  @RequestMapping(params = "method=carStatistics")
  public String getCarStat(Model model, HttpServletRequest request, CostStatConditionDTO conditionDTO){
    Long shopId = WebUtil.getShopId(request);
    if(shopId==null){
      return "/";
    }
    int beginYear = 2012;
    int endYear =DateUtil.getYear(System.currentTimeMillis());
    if(endYear == beginYear){
      conditionDTO.setAllYearOptions(new String[]{beginYear+"年"});
    }else if(endYear>beginYear){
      String[] allYearOptions = new String[0];
      while(beginYear<=endYear){
        allYearOptions = (String[])ArrayUtils.add(allYearOptions, beginYear+"年");
        beginYear++;
      }
      conditionDTO.setAllYearOptions(allYearOptions);
    }

    int year = DateUtil.getCurrentYear();
    int month = DateUtil.getCurrentMonth();
    boolean allYear = false;
    if(conditionDTO == null){
      conditionDTO = new CostStatConditionDTO();
    }
    if(conditionDTO.getAllYear() == null){
      conditionDTO.setAllYear(false);
    }else{
      allYear = conditionDTO.getAllYear();
    }
    if(conditionDTO.getYear() != null){
      year = conditionDTO.getYear();
    }else{
      conditionDTO.setYear(year);
    }
    if(conditionDTO.getMonth() != null){
      month = conditionDTO.getMonth();
    }else{
      conditionDTO.setMonth(month);
    }
    try{
      IVehicleStatService vehicleStatService = ServiceManager.getService(IVehicleStatService.class);
      List<VehicleServeMonthStatDTO> result = vehicleStatService.queryTopVehicleServeMonthStat(shopId, year, month, allYear, TOP_LIMIT);
      int timesTotal = vehicleStatService.queryVehicleServeTotal(shopId, year, month, allYear);

      VehicleServeMonthStatDTO other = new VehicleServeMonthStatDTO();
      int topTimesTotal = 0;
      for(VehicleServeMonthStatDTO statDTO : result){
        topTimesTotal += statDTO.getTotalTimes();
      }
      other.setTotalTimes(timesTotal - topTimesTotal);
      other.setBrand("其他");

      model.addAttribute("vehicleServeMonthStatDTOs", result);
      model.addAttribute("other", other);
      model.addAttribute("total", timesTotal);

      return "/customer/carStatistics";
    } catch (Exception e) {
      LOG.error("VehicleStatController.getCarStat, shopId:" + shopId);
      LOG.error(e.getMessage(),e);
    }
    return "/customer/carStatistics";
  }

}
