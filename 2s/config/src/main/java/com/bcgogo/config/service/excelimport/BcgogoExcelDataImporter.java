package com.bcgogo.config.service.excelimport;

import com.bcgogo.config.dto.ImportResult;
import com.bcgogo.config.service.IImportService;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.service.ServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-4-5
 * Time: 下午3:12
 * To change this template use File | Settings | File Templates.
 */
public abstract class BcgogoExcelDataImporter {

  private static final Logger LOG = LoggerFactory.getLogger(BcgogoExcelDataImporter.class);

  protected static final int BATCH_SAVE_SIZE = 100;

  public abstract ImportResult importData(ImportContext importContext) throws Exception;

  public abstract CheckResult checkData(ImportContext importContext) throws BcgogoException;

  protected void remarkImportRecordStatus(List<Long> importRecordIds, Long shopId, int successCount, String type){
    IImportService importService = ServiceManager.getService(IImportService.class);
    if(successCount > 0){
      try {
        importService.remarkImportRecordSuccess(importRecordIds);
      } catch (BcgogoException e) {
        LOG.error("更新导入记录为成功状态时出现异常！");
        LOG.error("ImportRecordId : " + importRecordIds.toString());
        LOG.error("信息：" + e.getMessage(), e);
      }
    }else{
      try {
        importService.remarkImportRecordFail(importRecordIds);
      } catch (BcgogoException e) {
        LOG.error("更新导入记录为成功状态时出现异常！");
        LOG.error("ImportRecordId : " + importRecordIds.toString());
        LOG.error("信息：" + e.getMessage(), e);
      }
    }
  }

}
