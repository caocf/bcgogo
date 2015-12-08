package com.bcgogo.customer;

import com.bcgogo.common.WebUtil;
import com.bcgogo.config.dto.ImportRecordDTO;
import com.bcgogo.config.dto.ImportResult;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IImportService;
import com.bcgogo.config.service.excelimport.ImportContext;
import com.bcgogo.constant.ImportConstants;
import com.bcgogo.constant.LogicResource;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.solr.ICustomerOrSupplierSolrWriteService;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.user.service.excelimport.customer.CustomerImportConstants;
import com.bcgogo.user.service.permission.IPrivilegeService;
import com.bcgogo.user.verifier.PrivilegeRequestProxy;
import com.bcgogo.utils.ArrayUtil;
import com.bcgogo.utils.IOUtil;
import com.bcgogo.utils.JsonUtil;
import com.bcgogo.utils.NumberUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 客户导入功能
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-4-10
 * Time: 上午11:19
 * To change this template use File | Settings | File Templates.
 */

@Controller
@RequestMapping("/importCustomer.do")
public class ImportCustomerController {

  private static final Logger LOG = LoggerFactory.getLogger(ImportCustomerController.class);

  /**
   * 导入前：
   * 1.读取excel文件表头
   * 2.保存导入记录（文件）到数据库
   *
   * @param request
   * @return
   */
  @RequestMapping(params = "method=uploadExcel")
  public void getExcelHeader(HttpServletRequest request, HttpServletResponse response) {
    IImportService importService = ServiceManager.getService(IImportService.class);
    Map result = new HashMap();
    try {
      MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
      MultipartFile multipartFile = multipartRequest.getFile("selectfile");
      String fileName = multipartFile.getOriginalFilename();
      InputStream inputStream = multipartFile.getInputStream();
      if (inputStream.available() > ImportConstants.UPLOAD_FILE_MAX_SIZE) {
        PrintWriter writer = response.getWriter();
        writer.write("上传文件大小不能超过" + NumberUtil.byteToMillion(ImportConstants.UPLOAD_FILE_MAX_SIZE) + "M！");
        writer.flush();
        writer.close();
      }
      byte[] fileContent = IOUtil.readFromStream(inputStream);
      //先读取表头
      ImportContext importContext = new ImportContext();
      importContext.setFileContent(fileContent);
      importContext.setFileName(fileName);
      List<String> headList = importService.parseHead(importContext);
      if (headList == null || headList.isEmpty()) {
        result.put("message", "解析导入文件头部失败！");
      } else {
        result.put("headList", headList);
      }

      //再存入数据库
      Long shopId = (Long) request.getSession().getAttribute("shopId");
      ImportRecordDTO importRecordDTO = new ImportRecordDTO();
      importRecordDTO.setShopId(shopId);
      importRecordDTO.setStatus(ImportConstants.Status.STATUS_WAITING);
      importRecordDTO.setType(ImportConstants.Type.TYPE_CUSTOMER);
      importRecordDTO.setFileName(fileName);
      importRecordDTO.setFileContent(fileContent);
      importRecordDTO = importService.createImportRecord(importRecordDTO);
      if (importRecordDTO == null || importRecordDTO.getId() == null) {
        result.put("message", "保存导入记录出错！");
      } else {
        result.put("importRecordId", String.valueOf(importRecordDTO.getId()));
      }

      ShopDTO shop = ServiceManager.getService(IConfigService.class).getShopById(shopId);
      IPrivilegeService privilegeService = ServiceManager.getService(IPrivilegeService.class);
      result.put("systemFieldList", getCustomerFieldList(PrivilegeRequestProxy.verifierShopVersionResourceProxy(request, LogicResource.WEB_VERSION_WHOLESALERS)));

      String resultStr = JsonUtil.mapToJson(result);
      response.setHeader("Charset", "UTF-8");
      response.setContentType("text/html;charset=UTF-8");
      PrintWriter writer = response.getWriter();
      writer.write(resultStr);
      writer.flush();
      writer.close();
    } catch (Exception e) {
      LOG.error("上传文件出现异常！");
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * 从excel文件导入客户数据到本地数据库
   *
   * @return
   */
  @RequestMapping(params = "method=importCustomerFromExcel")
  public void importCustomerFromExcel(HttpServletRequest request, HttpServletResponse response) throws Exception {
    ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
    String result = null;
    ImportContext importContext = new ImportContext();
    importContext.setShopId((Long) request.getSession().getAttribute("shopId"));
    String importRecordIds = request.getParameter("importRecordId");
    List<Long> importRecordIdlist = NumberUtil.parseLongValues(importRecordIds);
    Map<String, String> fieldMapping = JsonUtil.jsonToStringMap(request.getParameter("fieldMapping"));
    if (importRecordIdlist == null || importRecordIdlist.isEmpty() || fieldMapping == null || fieldMapping.isEmpty()) {
      result = ImportConstants.MESSAGE_NEED_FILE_OR_MAPPING;
    } else {
      importContext.setImportRecordIdList(importRecordIdlist);
      importContext.setFieldMapping(fieldMapping);
      importContext.setType(ImportConstants.Type.TYPE_CUSTOMER);
      ImportResult importResult = null;
      try {
        importResult = customerService.importCustomerFromExcel(importContext);
        if (importResult.isSuccess()) {
          ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerIndexList(WebUtil.getShopId(request), 2000);
        }
      } catch (BcgogoException e) {
        LOG.error(e.getMessage(), e);
      }
      result = JsonUtil.objectToJson(importResult);
      PrintWriter writer = null;
      try {
        writer = response.getWriter();
        writer.write(result);
        writer.flush();
        writer.close();
      } catch (IOException e) {
        LOG.error(e.getMessage(), e);
      }
    }
  }

  private List<String> getCustomerFieldList(boolean isWholesalers) {
    List<String> list = new ArrayList<String>();
    for (String str : CustomerImportConstants.fieldList) {
      String[] strArray = str.split("_");
      if (isWholesalers) {
        if (ArrayUtil.contains(CustomerImportConstants.WHOLESALER_EXCLUDE_FIELDS, strArray[1])) {
          continue;
        }
      }
      list.add(str);
    }
    return list;
  }
}
