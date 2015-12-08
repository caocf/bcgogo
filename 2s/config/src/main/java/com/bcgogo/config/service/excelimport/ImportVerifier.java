package com.bcgogo.config.service.excelimport;

import com.bcgogo.user.dto.ValidateImportDataDTO;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-4-7
 * Time: 下午4:53
 * To change this template use File | Settings | File Templates.
 */
public interface ImportVerifier {

  public String verify(Map<String, Object> data, Map<String, String> fieldMapping,ValidateImportDataDTO validateImportDataDTO) throws ExcelImportException;

}
