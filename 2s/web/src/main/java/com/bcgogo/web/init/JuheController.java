package com.bcgogo.web.init;

import com.bcgogo.common.Result;
import com.bcgogo.config.cache.AreaCacheManager;
import com.bcgogo.config.dto.AreaDTO;
import com.bcgogo.config.dto.juhe.ViolateRegulationCitySearchConditionResponse;
import com.bcgogo.config.service.IJuheService;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.http.BcgogoHttpRequest;
import com.bcgogo.http.BcgogoHttpResponse;
import com.bcgogo.product.model.Licenseplate;
import com.bcgogo.product.model.ProductDaoManager;
import com.bcgogo.product.model.ProductWriter;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * User: ZhangJuntao
 * Date: 13-10-23
 * Time: 下午2:12
 */
@Controller
@RequestMapping("/juhe.do")
public class JuheController {
  private static final Logger LOG = LoggerFactory.getLogger(JuheController.class);

  @RequestMapping(params = "method=initJuheViolateRegulationCitySearchCondition")
  @ResponseBody
  public Object init(HttpServletRequest request) {
    try {
      BcgogoHttpRequest bcgogoHttpRequest =new BcgogoHttpRequest();
      BcgogoHttpResponse bcgogoHttpResponse = bcgogoHttpRequest.sendGet("http://v.juhe.cn/wz/citys?key="+ ConfigUtils.getJuheViolateRegulationKey());
      LOG.info(bcgogoHttpResponse.getContent());
      ViolateRegulationCitySearchConditionResponse response = JsonUtil.fromJson(bcgogoHttpResponse.getContent(), ViolateRegulationCitySearchConditionResponse.class);
      if (response.isSuccess()) {
        ServiceManager.getService(IJuheService.class).initJuheViolateRegulationCitySearchCondition(response.getResult());
        return new Result("初始化成功", true);
      }
      return new Result(response.getReason(), true);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return new Result("初始化失败", false);
    }
  }

  private void test() {
    ProductDaoManager productDaoManager = ServiceManager.getService(ProductDaoManager.class);
    ProductWriter writer = productDaoManager.getWriter();
    Object status = writer.begin();
    try {
      List<AreaDTO> areaDTOs = AreaCacheManager.getChildAreaDTOListByParentNo(1001l);
      for (AreaDTO areaDTO : areaDTOs) {
        Licenseplate licenseplate = new Licenseplate();
        licenseplate.setAreaNo(areaDTO.getNo());
        licenseplate.setAreaName("北京市");
        licenseplate.setCarno("京A");
        licenseplate.setAreaFirstcarno("京");
        licenseplate.setAreaFirstname("J");
        writer.save(licenseplate);
      }
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

}
