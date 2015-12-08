package com.bcgogo.config.service;

import com.bcgogo.config.dto.ImportRecordDTO;
import com.bcgogo.config.dto.ImportResult;
import com.bcgogo.config.model.ConfigDaoManager;
import com.bcgogo.config.model.ConfigWriter;
import com.bcgogo.config.model.ImportRecord;
import com.bcgogo.config.service.excelimport.BcgogoExcelParser;
import com.bcgogo.config.service.excelimport.ExcelImportUtil;
import com.bcgogo.config.service.excelimport.ImportContext;
import com.bcgogo.constant.ImportConstants;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * excel数据导入服务类
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-3-30
 * Time: 下午5:22
 * To change this template use File | Settings | File Templates.
 */
@Component
public class ImportService implements IImportService {

  private static final Logger LOG = LoggerFactory.getLogger(ImportService.class);

  /**
   * 客户数据导入
   *
   * @param importContext
   * @return
   * @throws BcgogoException
   */
  @Override
  public boolean parseData(ImportContext importContext) throws BcgogoException {
    //获取待导入的记录列表（可能有多个）
    List<ImportRecordDTO> importRecordDTOList = getImportRecordList(importContext.getImportRecordIdList(), importContext.getShopId(), ImportConstants.Status.STATUS_WAITING, importContext.getType());
    if (CollectionUtils.isEmpty(importRecordDTOList)) {
      return false;
    }
    List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
    List<Map<String, Object>> singleDataList = null;
    List<Map<String, Object>> finalSingleDataList = new ArrayList<Map<String, Object>>();
    Map<String,Object> files=new HashMap<String,Object>();
    for (ImportRecordDTO importRecordDTO : importRecordDTOList) {
      if (importRecordDTO == null || importRecordDTO.getFileContent() == null || StringUtil.isEmpty(importRecordDTO.getFileName())) {
        continue;
      }
      singleDataList = bcgogoExcelParser.parseData(importRecordDTO.getFileContent(), ExcelImportUtil.getVersion(importRecordDTO.getFileName()));
      for(int i=0;i<singleDataList.size();i++){
        if(singleDataList.get(i).size()==0)
        {
          continue;
        }

        singleDataList.get(i).put("fileName",importRecordDTO.getFileName().split("\\.")[0]);
        finalSingleDataList.add(singleDataList.get(i));
      }
      dataList.addAll(finalSingleDataList);
    }
    importContext.setDataList(dataList);
    return true;
  }

  /**
   * 解析EXCEL文件表头
   *
   * @param importContext
   * @return
   * @throws BcgogoException
   */
  @Override
  public List<String> parseHead(ImportContext importContext) throws BcgogoException {
    return bcgogoExcelParser.parseHead(importContext.getFileContent(), importContext.getVersion());
  }

  /**
   * 保存导入记录
   *
   * @param importRecordDTO
   * @return
   */
  public ImportRecordDTO createImportRecord(ImportRecordDTO importRecordDTO) throws BcgogoException {
    ConfigWriter writer = configDaoManager.getWriter();
    ImportRecord importRecord = new ImportRecord(importRecordDTO);
    Object status = writer.begin();
    try {
      writer.save(importRecord);
      writer.commit(status);
      importRecordDTO.setId(importRecord.getId());
    } finally {
      writer.rollback(status);
    }
    return importRecordDTO;
  }

  /**
   * 把导入记录标记为导入成功
   * @param importRecordIds
   * @return
   * @throws BcgogoException
   */
  @Override
  public boolean remarkImportRecordSuccess(List<Long> importRecordIds) throws BcgogoException {
    if(importRecordIds == null || importRecordIds.isEmpty()){
      return false;
    }
    ConfigWriter writer = configDaoManager.getWriter();
    Object transaction = writer.begin();
    try{
      ImportRecord importRecord = null;
      for(Long importRecordId : importRecordIds){
        if(importRecordId == null){
          continue;
        }
        importRecord = writer.getById(ImportRecord.class, importRecordId);
        importRecord.setStatus(ImportConstants.Status.STATUS_SUCCESS);
        writer.update(importRecord);
      }
      writer.commit(transaction);
      return true;
    }finally {
      writer.rollback(transaction);
    }
  }

  /**
   * 把导入记录标记为导入失败
   * @param importRecordIds
   * @return
   * @throws BcgogoException
   */
  @Override
  public boolean remarkImportRecordFail(List<Long> importRecordIds) throws BcgogoException {
    if(importRecordIds == null || importRecordIds.isEmpty()){
      return false;
    }
    ConfigWriter writer = configDaoManager.getWriter();
    Object transaction = writer.begin();
    try{
      ImportRecord importRecord = null;
      for(Long importRecordId : importRecordIds){
        if(importRecordId == null){
          continue;
        }
        importRecord = writer.getById(ImportRecord.class, importRecordId);
        importRecord.setStatus(ImportConstants.Status.STATUS_FAIL);
        writer.update(importRecord);
      }
      writer.commit(transaction);
      return true;
    }finally {
      writer.rollback(transaction);
    }
  }

  /**
   * 获取导入记录
   *
   * @param importRecordIdList
   * @param shopId
   * @return
   */
  @Override
  public List<ImportRecordDTO> getImportRecordList(List<Long> importRecordIdList, Long shopId, String status, String type) throws BcgogoException {
    ConfigWriter writer = configDaoManager.getWriter();
    return writer.getImportRecordList(importRecordIdList, shopId, status, type);
  }

  @Autowired
  private ConfigDaoManager configDaoManager;

  @Autowired
  private BcgogoExcelParser bcgogoExcelParser;

  /**
   *
   * @param importContext
   * @return
   * @throws BcgogoException
   */
  @Override
  public boolean directParseData(ImportContext importContext) throws BcgogoException {
    if(null == importContext)
    {
      return false;
    }
    //获取待导入的记录列表（可能有多个）
    List<ImportRecordDTO> importRecordDTOList = importContext.getImportRecordDTOList();
    if (CollectionUtils.isEmpty(importRecordDTOList)) {
      return false;
    }
    List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
    List<Map<String, Object>> singleDataList = null;
    List<Map<String, Object>> finalSingleDataList = new ArrayList<Map<String, Object>>();
    Map<String,Object> files=new HashMap<String,Object>();
    for (ImportRecordDTO importRecordDTO : importRecordDTOList) {
      if (importRecordDTO == null || importRecordDTO.getFileContent() == null || StringUtil.isEmpty(importRecordDTO.getFileName())) {
        continue;
      }
      singleDataList = bcgogoExcelParser.parseData(importRecordDTO.getFileContent(), ExcelImportUtil.getVersion(importRecordDTO.getFileName()));

      for(int i=0;i<singleDataList.size();i++){
        if(singleDataList.get(i).keySet().size()==0)
        {
          continue;
        }
        singleDataList.get(i).put("fileName",importRecordDTO.getFileName().split("\\.")[0]);
        finalSingleDataList.add(singleDataList.get(i));
      }
      dataList.addAll(finalSingleDataList);
    }
    importContext.setDataList(dataList);
    return true;
  }
}
