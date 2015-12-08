package com.bcgogo.txn;

import com.bcgogo.common.StringUtil;
import com.bcgogo.common.WebUtil;
import com.bcgogo.config.dto.ImportRecordDTO;
import com.bcgogo.config.dto.ImportResult;
import com.bcgogo.config.service.IImportService;
import com.bcgogo.config.service.excelimport.ImportContext;
import com.bcgogo.constant.ImportConstants;
import com.bcgogo.constant.LogicResource;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.service.importexcel.order.OrderImportConstants;
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
import org.springframework.web.bind.annotation.ResponseBody;
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
 * User: ndong
 * Date: 12-4-10
 * Time: 上午11:19
 * To change this template use File | Settings | File Templates.
 */

@Controller
@RequestMapping("/importOrder.do")
public class ImportOrderController extends AbstractTxnController{

  private static final Logger LOG = LoggerFactory.getLogger(ImportOrderController.class);



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
    IPrivilegeService privilegeService = ServiceManager.getService(IPrivilegeService.class);
    Map result = new HashMap();
    try {
      MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
      MultipartFile multipartFile = multipartRequest.getFile("selectfile");
      String fileName = multipartFile.getOriginalFilename();
      if(!validateOrderType(fileName)){           //todo 不起作用
        response.setHeader("Charset","UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter writer = response.getWriter();
        result.put("orderImportMsg","上传文件名无法识别，请使用标准模板名！");
        String resultStr = JsonUtil.mapToJson(result);
        writer.write(resultStr);
        writer.flush();
        writer.close();
        return;
      }
      InputStream inputStream = multipartFile.getInputStream();
      if (inputStream.available() > ImportConstants.UPLOAD_FILE_MAX_SIZE) {
        PrintWriter writer = response.getWriter();
        writer.write("上传文件大小不能超过" + NumberUtil.byteToMillion(ImportConstants.UPLOAD_FILE_MAX_SIZE) + "M！");
        writer.flush();
        writer.close();
        return;
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
      ImportRecordDTO importRecordDTO = new ImportRecordDTO();
      importRecordDTO.setShopId(WebUtil.getShopId(request));
      importRecordDTO.setStatus(ImportConstants.Status.STATUS_WAITING);
      importRecordDTO.setType(ImportConstants.Type.TYPE_ORDER);
      importRecordDTO.setFileName(fileName);
      importRecordDTO.setFileContent(fileContent);
      importRecordDTO = importService.createImportRecord(importRecordDTO);
      if (importRecordDTO == null || importRecordDTO.getId() == null) {
        result.put("message", "保存导入记录出错！");
      } else {
        result.put("importRecordId", String.valueOf(importRecordDTO.getId()));
      }
      result.put("systemFieldList", getOrderFieldList(PrivilegeRequestProxy.verifierShopVersionResourceProxy(request, LogicResource.WEB_VERSION_WHOLESALERS)));
      String resultStr = JsonUtil.mapToJson(result);
      response.setHeader("Charset","UTF-8");
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
  @RequestMapping(params = "method=importOrderFromExcel")
  @ResponseBody
  public void importOrderFromExcel(HttpServletRequest request, HttpServletResponse response)  {
    ImportContext importContext = new ImportContext();
    String result = null;
    importContext.setShopId((Long) request.getSession().getAttribute("shopId"));
    String importRecordIds = request.getParameter("importRecordId");
    List<Long> importRecordIdlist = NumberUtil.parseLongValues(importRecordIds);
    Map<String, String> fieldMapping = JsonUtil.jsonToStringMap(request.getParameter("fieldMapping"));
    if (importRecordIdlist == null || importRecordIdlist.isEmpty() || fieldMapping == null || fieldMapping.isEmpty()) {
      result= ImportConstants.MESSAGE_NEED_FILE_OR_MAPPING;
    }
    importContext.setImportRecordIdList(importRecordIdlist);
    importContext.setFieldMapping(fieldMapping);
    importContext.setType(ImportConstants.Type.TYPE_ORDER);
    ImportResult importResult = null;
    try {
      importResult = txnService.importOrderFromExcel(importContext);
    } catch (Exception e) {
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
      LOG.error("/importOrder.do");
      LOG.error("method=importOrderFromExcel");
      LOG.error("单据导入出错");
      LOG.error(e.getMessage(), e);
    } catch (Exception e){
      LOG.error("/importOrder.do");
      LOG.error("method=importOrderFromExcel");
      LOG.error("单据导入出错");
      LOG.error(e.getMessage(), e);
    }
  }

  public boolean validateOrderType(String file){
    if(StringUtil.isEmpty(file)||file.split("\\.").length!=2){
      return false;
    }
    String fileName=file.split("\\.")[0];
    List<String> orderTypes=new ArrayList<String>();
    orderTypes.add("入库单");
    orderTypes.add("销售单");
    orderTypes.add("施工单");
    orderTypes.add("洗车美容");
    if(orderTypes.contains(fileName)){
      return true;
    }else {
      return false;
    }
  }

  private List<String> getOrderFieldList(boolean isWholesalers) {
    List<String> list = new ArrayList<String>();
    for (String str : OrderImportConstants.fieldList) {
      String[] strArray = str.split("_");
      if (isWholesalers) {
        if(ArrayUtil.contains(OrderImportConstants.WHOLESALER_EXCLUDE_FIELDS, strArray[1])){
          continue;
        }
      }
      list.add(str);
    }
    return list;
  }
}
