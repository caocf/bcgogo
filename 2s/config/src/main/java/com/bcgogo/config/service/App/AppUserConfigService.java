package com.bcgogo.config.service.App;

import com.bcgogo.config.dto.juhe.JuheCityOilPriceDTO;
import com.bcgogo.config.model.ConfigDaoManager;
import com.bcgogo.config.model.ConfigWriter;
import com.bcgogo.config.model.JuheCityOilPrice;
import com.bcgogo.service.ServiceManager;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 14-2-12
 * Time: 上午10:36
 */
@Component
public class AppUserConfigService implements IAppUserConfigService {

  @Autowired
  private ConfigDaoManager configDaoManager;

  @Override
  public JuheCityOilPriceDTO getJuheCityOilPriceDTOByFirstCarNo(String firstCarNo) {
    if (StringUtils.isNotBlank(firstCarNo)) {
      ConfigWriter configWriter = configDaoManager.getWriter();
      JuheCityOilPrice juheCityOilPrice = configWriter.getJuheCityOilPriceByFirstCarNo(firstCarNo);
      if (juheCityOilPrice != null) {
        return juheCityOilPrice.toDTO();
      }
    }
    return null;
  }
}
