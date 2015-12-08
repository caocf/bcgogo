package com.bcgogo.txn.service.importexcel.memberService;

import com.bcgogo.config.dto.ImportResult;
import com.bcgogo.config.service.excelimport.BcgogoExcelDataImporter;
import com.bcgogo.config.service.excelimport.CheckResult;
import com.bcgogo.config.service.excelimport.ExcelImportConstants;
import com.bcgogo.config.service.excelimport.ImportContext;
import com.bcgogo.constant.ImportConstants;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.model.Service;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.user.dto.MemberDTO;
import com.bcgogo.user.dto.MemberServiceDTO;
import com.bcgogo.user.dto.ValidateImportDataDTO;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-9-5
 * Time: 下午6:00
 * To change this template use File | Settings | File Templates.
 */
@Component
public class MemberServiceImporter extends BcgogoExcelDataImporter{
    private static final Logger LOG = LoggerFactory.getLogger(MemberServiceImporter.class);

  /**
   * 执行客户数据导入
   *
   * @param importContext
   * @return
   * @throws Exception
   */
  @Override
  public ImportResult importData(ImportContext importContext) throws Exception {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    ImportResult importResult = new ImportResult();
    List<Map<String, Object>> dataList = importContext.getDataList();
    importResult.setTotalCount(dataList.size());
    List<MemberServiceDTO> memberServiceDTOList = new ArrayList<MemberServiceDTO>();
    MemberServiceDTO memberServiceDTO = null;
    int successCount = 0;
    int failCount = 0;
    StringBuffer messageBuffer = new StringBuffer();
    for (int index = 0; index < dataList.size(); index++) {
      Map<String, Object> data = dataList.get(index);
      if (data == null || data.isEmpty()) {
        continue;
      }
      memberServiceDTO = memberServiceInfoDTOGenerator.generate(data, importContext.getFieldMapping(), importContext.getShopId());
      if (memberServiceDTO != null) {
        memberServiceDTOList.add(memberServiceDTO);
      }
      if (memberServiceDTOList.size() >= BATCH_SAVE_SIZE || index >= dataList.size() - 1) {
        try {
          txnService.batchCreateMemberServiceAndService(memberServiceDTOList);
          successCount += memberServiceDTOList.size();
        } catch (BcgogoException e) {
          LOG.error("批量保存会员服务数据时发生异常 : " + e.getMessage(), e);
          failCount += memberServiceDTOList.size();
          messageBuffer.append(e.getMessage());
        } finally {
          memberServiceDTOList.clear();
        }
      }
    }

    //更新导入记录状态
    remarkImportRecordStatus(importContext.getImportRecordIdList(), importContext.getShopId(), successCount, ImportConstants.Type.TYPE_MEMBER_SERVICE);

    importResult.setFailCount(failCount);
    importResult.setSuccessCount(successCount);
    importResult.setMessage(messageBuffer.toString());
    return importResult;
  }

  /**
   * 执行客户数据校验
   *
   * @param importContext
   * @return
   * @throws BcgogoException
   */
  @Override
  public CheckResult checkData(ImportContext importContext) throws BcgogoException {
    CheckResult checkResult = new CheckResult();
    List<Map<String, Object>> dataList = importContext.getDataList();
    if (dataList == null || dataList.isEmpty()) {
      checkResult.setMessage(ExcelImportConstants.CheckResultMessage.EMPTY_DATA_CONTENT);
      return checkResult;
    }
    Map<String, String> fieldMapping = importContext.getFieldMapping();
    if (fieldMapping == null || fieldMapping.isEmpty()) {
      checkResult.setMessage(ExcelImportConstants.CheckResultMessage.EMPTY_FIELD_MAPPING);
      return checkResult;
    }
    List<Map<String, Object>> failDataList = new ArrayList<Map<String, Object>>();
    checkResult.setFailDataList(failDataList);
    String fieldCheckResult = null;
    Map<String,MemberDTO> memberDTOMap = ServiceManager.getService(IUserService.class).getMemberMap(importContext.getShopId());
    ValidateImportDataDTO validateImportDataDTO = new ValidateImportDataDTO();
    validateImportDataDTO.setMemberDTOMap(memberDTOMap);
    int headLineNum = 2;

    for(int index = 0;index < dataList.size();index++ ){
      Map<String,Object> data = dataList.get(index);
      if (data == null) {
        continue;
      }
      if (data.isEmpty()) {
        data.put("message", "第" + String.valueOf(index + headLineNum) +"行" + ExcelImportConstants.CheckResultMessage.EMPTY_DATA_ITEM_CONTENT);
        failDataList.add(data);
        if (!StringUtil.isEmpty(checkResult.getMessage())) {
          checkResult.setMessage(checkResult.getMessage() + "第" + String.valueOf(index + headLineNum) + "行" + ExcelImportConstants.CheckResultMessage.EMPTY_DATA_ITEM_CONTENT);
        }else{
          checkResult.setMessage("第" + String.valueOf(index + headLineNum) + "行" + ExcelImportConstants.CheckResultMessage.EMPTY_DATA_ITEM_CONTENT);
        }

        continue;
      }
      //校验库存信息名
      fieldCheckResult = memberServiceImportVerifier.verify(data, fieldMapping,validateImportDataDTO);
      if (!StringUtil.isEmpty(fieldCheckResult)) {
        data.put("message", "第" + String.valueOf(index + headLineNum) +"行"+fieldCheckResult);
        failDataList.add(data);
        if (!StringUtil.isEmpty(checkResult.getMessage())) {
          checkResult.setMessage( checkResult.getMessage() +"第" + String.valueOf(index + headLineNum) +"行" + fieldCheckResult );
        }else{
          checkResult.setMessage( "第" + String.valueOf(index + headLineNum) +"行" + fieldCheckResult );
        }
        continue;
      }
    }

    return checkResult;
  }

  @Autowired
  private MemberServiceInfoDTOGenerator memberServiceInfoDTOGenerator;

  @Autowired
  private MemberServiceImportVerifier memberServiceImportVerifier;
}
