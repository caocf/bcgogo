package com.bcgogo.config.service;

import com.bcgogo.AbstractTest;
import com.bcgogo.config.dto.ImportRecordDTO;
import com.bcgogo.constant.ImportConstants;
import com.bcgogo.service.ServiceManager;
import junit.framework.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-4-18
 * Time: 下午4:26
 * To change this template use File | Settings | File Templates.
 */
public class ImportServiceTest extends AbstractTest{

  @Test
  public void testImportService() throws Exception {
    Long shopId = createShop();
    IImportService importService = ServiceManager.getService(IImportService.class);

    //1.测试生成一条导入记录
    ImportRecordDTO importRecordDTO = new ImportRecordDTO();
    importRecordDTO.setStatus(ImportConstants.Status.STATUS_WAITING);
    importRecordDTO.setFileName("测试文件.xls");
    importRecordDTO.setType(ImportConstants.Type.TYPE_CUSTOMER);
    importRecordDTO.setShopId(shopId);
    importRecordDTO = importService.createImportRecord(importRecordDTO);
    Assert.assertNotNull(importRecordDTO.getId());

    List<Long> importRecordIds = new ArrayList<Long>();
    importRecordIds.add(importRecordDTO.getId());

    //2.测试查询导入记录
    List<ImportRecordDTO> importRecordDTOList = importService.getImportRecordList(importRecordIds, shopId, ImportConstants.Status.STATUS_WAITING, ImportConstants.Type.TYPE_CUSTOMER);
    Assert.assertNotNull(importRecordDTOList);
    Assert.assertEquals(1, importRecordDTOList.size());
    ImportRecordDTO importRecordDTO1 = importRecordDTOList.get(0);
    Assert.assertEquals(shopId, importRecordDTO1.getShopId());
    Assert.assertEquals(ImportConstants.Status.STATUS_WAITING, importRecordDTO1.getStatus());
    Assert.assertEquals(ImportConstants.Type.TYPE_CUSTOMER, importRecordDTO1.getType());
    Assert.assertEquals("测试文件.xls", importRecordDTO1.getFileName());

    //3.测试更新导入记录为“成功”
    importService.remarkImportRecordSuccess(importRecordIds);
    importRecordDTOList = importService.getImportRecordList(importRecordIds, shopId, ImportConstants.Status.STATUS_SUCCESS, ImportConstants.Type.TYPE_CUSTOMER);
    Assert.assertNotNull(importRecordDTOList);
    Assert.assertEquals(1, importRecordDTOList.size());
    ImportRecordDTO importRecordDTO2 = importRecordDTOList.get(0);
    Assert.assertEquals(shopId, importRecordDTO2.getShopId());
    Assert.assertEquals(ImportConstants.Status.STATUS_SUCCESS, importRecordDTO2.getStatus());
    Assert.assertEquals(ImportConstants.Type.TYPE_CUSTOMER, importRecordDTO2.getType());
    Assert.assertEquals("测试文件.xls", importRecordDTO2.getFileName());

    //4.测试更新导入记录为“失败”
    importService.remarkImportRecordFail(importRecordIds);
    importRecordDTOList = importService.getImportRecordList(importRecordIds, shopId, ImportConstants.Status.STATUS_FAIL, ImportConstants.Type.TYPE_CUSTOMER);
    Assert.assertNotNull(importRecordDTOList);
    Assert.assertEquals(1, importRecordDTOList.size());
    ImportRecordDTO importRecordDTO3 = importRecordDTOList.get(0);
    Assert.assertEquals(shopId, importRecordDTO3.getShopId());
    Assert.assertEquals(ImportConstants.Status.STATUS_FAIL, importRecordDTO3.getStatus());
    Assert.assertEquals(ImportConstants.Type.TYPE_CUSTOMER, importRecordDTO3.getType());
    Assert.assertEquals("测试文件.xls", importRecordDTO3.getFileName());

  }

}
