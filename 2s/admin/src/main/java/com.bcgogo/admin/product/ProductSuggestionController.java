package com.bcgogo.admin.product;

import com.bcgogo.search.dto.SearchConditionDTO;
import com.bcgogo.search.service.vehicle.ISearchVehicleService;
import com.bcgogo.service.ServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Zhangjuntao
 * Date: 13-8-5
 * Time: 上午10:50
 */
@Controller
@RequestMapping("/productSuggestion.do")
public class ProductSuggestionController {
  private static final Logger LOG = LoggerFactory.getLogger(ProductSuggestionController.class);

  /**
   * 车辆信息
   * @Related to ProductController searchBrandSuggestion
   */
  @RequestMapping(params = "method=searchBrandSuggestion")
  public void searchBrandSuggestion(ModelMap model, HttpServletRequest request, HttpServletResponse response,
                                    String searchWord, String searchField, String brandValue, String modelValue,
                                    String yearValue, String engineValue) {
    ISearchVehicleService searchVehicleService = ServiceManager.getService(ISearchVehicleService.class);
    String jsonStr = "[]";
    StringBuilder sb = new StringBuilder("[");
    try {
      SearchConditionDTO searchConditionDTO = new SearchConditionDTO();
      searchConditionDTO.setSearchWord(searchWord);
      searchConditionDTO.setSearchField(searchField);
      searchConditionDTO.setVehicleBrand(brandValue);
      searchConditionDTO.setVehicleModel(modelValue);
      searchConditionDTO.setVehicleYear(yearValue);
      searchConditionDTO.setVehicleEngine(engineValue);
      List<String> searchList = searchVehicleService.getVehicleSuggestionListByKeywords(searchConditionDTO);
      if (searchList != null && searchList.size() > 0) {
        for (String str : searchList) {
          sb.append("{\"name\":\"").append(str).append("\"},");
        }
        sb.replace(sb.length() - 1, sb.length(), "]");
        jsonStr = sb.toString();
      }
      PrintWriter writer = response.getWriter();
      writer.write(jsonStr);
      writer.close();
    } catch (Exception e) {
      LOG.debug("/product.do");
      LOG.debug("method=searchBrandSuggestion");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.debug("brandValue" + brandValue + ",modelValue:" + modelValue + ",engineValue:" + engineValue + ",yearValue:" + yearValue);
      LOG.debug("searchWord:" + searchWord + ",searchField:" + searchField);
      LOG.error(e.getMessage(), e);
    }
  }
}
