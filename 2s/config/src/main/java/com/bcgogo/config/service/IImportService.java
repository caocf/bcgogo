package com.bcgogo.config.service;

import com.bcgogo.config.dto.ImportRecordDTO;
import com.bcgogo.config.dto.ImportResult;
import com.bcgogo.config.service.excelimport.ImportContext;
import com.bcgogo.exception.BcgogoException;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-3-30
 * Time: 下午4:58
 * To change this template use File | Settings | File Templates.
 */
public interface IImportService {

  /**
   * 从db中取出文件，并解析文件内容
   *
   * @param importContext
   * @return
   * @throws BcgogoException
   */
  public boolean parseData(ImportContext importContext) throws BcgogoException;

  /**
   * 获取excel中的头部信息
   *
   * @param importContext
   * @return
   * @throws BcgogoException
   */
  public List<String> parseHead(ImportContext importContext) throws BcgogoException;

  public ImportRecordDTO createImportRecord(ImportRecordDTO importRecordDTO) throws BcgogoException, BcgogoException;

  public boolean remarkImportRecordSuccess(List<Long> importRecordIds) throws BcgogoException;

  public boolean remarkImportRecordFail(List<Long> importRecordIds) throws BcgogoException;

  public List<ImportRecordDTO> getImportRecordList(List<Long> importRecordIdList, Long shopId, String status, String type) throws BcgogoException;

  public boolean directParseData(ImportContext importContext) throws BcgogoException;

}
