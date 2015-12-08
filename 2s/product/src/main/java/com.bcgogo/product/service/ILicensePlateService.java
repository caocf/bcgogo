package com.bcgogo.product.service;

import com.bcgogo.config.dto.AreaDTO;

import java.util.Map;

/**
 * User: ZhangJuntao
 * Date: 13-11-11
 * Time: 下午1:24
 */
public interface ILicensePlateService {
  AreaDTO getAreaDTOByLicenseNo(String licenseNo);

  /**
   * @param licenseNo vehicleNo array
   * @return Map<String, AreaDTO>
   */
  Map<String, AreaDTO> getAreaMapByLicenseNo(String... licenseNo);
}
