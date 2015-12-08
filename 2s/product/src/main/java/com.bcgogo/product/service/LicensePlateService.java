package com.bcgogo.product.service;

import com.bcgogo.config.cache.AreaCacheManager;
import com.bcgogo.config.dto.AreaDTO;
import com.bcgogo.product.model.Licenseplate;
import com.bcgogo.product.model.ProductDaoManager;
import com.bcgogo.product.model.ProductReader;
import com.bcgogo.utils.ArrayUtil;
import com.bcgogo.utils.RegexUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * User: ZhangJuntao
 * Date: 13-11-11
 * Time: 下午1:24
 */
@Component
public class LicensePlateService implements ILicensePlateService {
  @Autowired
  private ProductDaoManager productDaoManager;

  @Override
  public AreaDTO getAreaDTOByLicenseNo(String licenseNo) {
    ProductReader reader = productDaoManager.getReader();
    if (RegexUtils.isNotVehicleNo(licenseNo)) {
      return null;
    }
    Licenseplate licenseplate = reader.getLicenseplateByCarno(licenseNo.substring(0, 2));
    return licenseplate == null ? null : AreaCacheManager.getAreaDTOByNo(licenseplate.getAreaNo());
  }

  @Override
  public Map<String, AreaDTO> getAreaMapByLicenseNo(String... licenseNo) {
    Map<String, AreaDTO> areaDTOMap = new HashMap<String, AreaDTO>();
    if (ArrayUtil.isEmpty(licenseNo)) return areaDTOMap;
    ProductReader reader = productDaoManager.getReader();
    Licenseplate licenseplate;
    AreaDTO areaDTO;
    for (String str : licenseNo) {
      if (RegexUtils.isNotVehicleNo(str)) {
        continue;
      }
      licenseplate = reader.getLicenseplateByCarno(str.substring(0, 2));
      if (licenseplate == null) continue;
      areaDTO = AreaCacheManager.getAreaDTOByNo(licenseplate.getAreaNo());
      if (areaDTO != null)
        areaDTOMap.put(str, areaDTO);
    }
    return areaDTOMap;
  }

}
