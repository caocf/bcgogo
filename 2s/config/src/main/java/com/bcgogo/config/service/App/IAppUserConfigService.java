package com.bcgogo.config.service.App;

import com.bcgogo.config.dto.juhe.JuheCityOilPriceDTO;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 14-2-12
 * Time: 上午10:36
 */
public interface IAppUserConfigService {

  /**
   * 根据车牌号前缀第一个汉字获取所在省的油价
   */
  JuheCityOilPriceDTO getJuheCityOilPriceDTOByFirstCarNo(String firstCarNo);
}
