package com.bcgogo.config;

import com.bcgogo.common.WebUtil;
import com.bcgogo.config.dto.ImportRecordDTO;
import com.bcgogo.config.dto.ImportResult;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IImportService;
import com.bcgogo.config.service.excelimport.ImportContext;
import com.bcgogo.constant.ImportConstants;
import com.bcgogo.constant.LogicResource;
import com.bcgogo.enums.importData.ImportType;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.stat.dto.SupplierRecordDTO;
import com.bcgogo.txn.service.ITxnService;
import com.bcgogo.txn.service.importexcel.InventoryImportConstants;
import com.bcgogo.txn.service.importexcel.memberService.MemberServiceImportConstants;
import com.bcgogo.txn.service.importexcel.order.OrderImportConstants;
import com.bcgogo.txn.service.solr.ICustomerOrSupplierSolrWriteService;
import com.bcgogo.txn.service.solr.IOrderSolrWriterService;
import com.bcgogo.user.service.ICustomerService;
import com.bcgogo.user.service.ISupplierService;
import com.bcgogo.user.service.excelimport.customer.CustomerImportConstants;
import com.bcgogo.user.service.excelimport.supplier.SupplierImportConstants;
import com.bcgogo.user.service.permission.IPrivilegeService;
import com.bcgogo.user.service.utils.BcgogoShopLogicResourceUtils;
import com.bcgogo.user.verifier.PrivilegeRequestProxy;
import com.bcgogo.utils.ArrayUtil;
import com.bcgogo.utils.IOUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 导入功能
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-3-30
 * Time: 下午2:42
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/import.do")
public class ImportExcelController {
  private static final Logger LOG = LoggerFactory.getLogger(ImportExcelController.class);
  
  /**
   * 进度导入页面
   * @param request
   * @return
   */
  @RequestMapping(params = "method=openImportPage")
  public String openImportPage(HttpServletRequest request){

    Map<String, String> map = ImportConstants.getImportTypes(request.getLocale());

    IPrivilegeService privilegeService = ServiceManager.getService(IPrivilegeService.class);
    if(!PrivilegeRequestProxy.verifierShopVersionResourceProxy(request, LogicResource.WEB_VERSION_MEMBER_STORED_VALUE)){
      map.remove(ImportConstants.Type.TYPE_MEMBER_SERVICE);
    }
    request.setAttribute("importType", map);

    return "/config/newImportexcel";
  }

  /**
   * 进度简单导入页面
   * @param request
   * @return
   */
  @RequestMapping(params = "method=openSimpleImportPage")
  public String openSimpleImportPage(HttpServletRequest request){

    Map<String, String> map = ImportConstants.getImportTypes();

    request.setAttribute("importType", map);

    return "/config/importSimpleExcel";
  }

  @RequestMapping(params = "method=simpleImportExcelData")
  @ResponseBody
  public Object simpleImportExcelData(HttpServletRequest request,HttpServletResponse response)
  {
    Map result = new HashMap();
    IImportService importService = ServiceManager.getService(IImportService.class);
    IPrivilegeService privilegeService = ServiceManager.getService(IPrivilegeService.class);
    try{
      MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;

      String importTypeStr = request.getParameter("importType");
      //先判断 importType 有没有值，根据类型来选择策略
      if(StringUtils.isBlank(importTypeStr))
      {
        result.put("result","error");
        result.put("errorMsg",ImportConstants.NO_IMPORT_TYPE);
        return result;
      }

      ImportType importType = null;

      try{
        importType = ImportType.valueOf(importTypeStr);
      }catch (IllegalArgumentException e){
        result.put("result","error");
        result.put("errorMsg",ImportConstants.TRANSFORM_IMPORT_TYPE_ERROR);
        return result;
      }

      MultipartFile multipartFile = multipartRequest.getFile("selectfile");
      String fileName = multipartFile.getOriginalFilename();

      if(!validateExcelFileName(importType,fileName.split("\\.")[0]))
      {
        result.put("result","error");
        result.put("errorMsg",ImportConstants.getFileNameErrorInfo(importType));
        return result;
      }

      InputStream inputStream = multipartFile.getInputStream();
      if (inputStream.available() > ImportConstants.UPLOAD_FILE_MAX_SIZE) {
        result.put("result","error");
        result.put("errorMsg", ImportConstants.FILE_TOO_BIG);
        return result;
      }
      byte[] fileContent = IOUtil.readFromStream(inputStream);
      //先读取表头
      ImportContext importContext = new ImportContext();
      importContext.setFileContent(fileContent);
      importContext.setFileName(fileName);
      importContext.setShopId((Long) request.getSession().getAttribute("shopId"));
      List<String> headList = importService.parseHead(importContext);
      if (headList == null || headList.isEmpty()) {
        result.put("result","error");
        result.put("errorMsg", "解析导入文件头部失败！");
        return result;
      }

      //方法验证头内容,传入参数importType
      if(!validateExcelHeadContext(importType,headList,fileName.split("\\.")[0],BcgogoShopLogicResourceUtils.isWholesalers(WebUtil.getShopVersionId(request))))
      {
        result.put("result","error");
        result.put("errorMsg", ImportConstants.getHeadListErrorInfo(importType,fileName.split("\\.")[0]));
        return result;
      }

      Long shopId = (Long) request.getSession().getAttribute("shopId");
      ShopDTO shop = ServiceManager.getService(IConfigService.class).getShopById(shopId);
      ImportRecordDTO importRecordDTO = new ImportRecordDTO();
      importRecordDTO.setShopId(shopId);
      importRecordDTO.setStatus(ImportConstants.Status.STATUS_WAITING);
      importRecordDTO.setType(ImportConstants.getImportConstantsType(importType));
      importRecordDTO.setFileName(fileName);
      importRecordDTO.setFileContent(fileContent);

      List<ImportRecordDTO> importRecordDTOList = new ArrayList<ImportRecordDTO>();
      importRecordDTOList.add(importRecordDTO);


      importContext.setImportRecordDTOList(importRecordDTOList);
      importContext.setType(ImportConstants.getImportConstantsType(importType));

      importContext.setFieldMapping(getFiledMapping(importType,fileName.split("\\.")[0], PrivilegeRequestProxy.verifierShopVersionResourceProxy(request, LogicResource.WEB_VERSION_WHOLESALERS)));
      if(StringUtils.isNotEmpty(request.getParameter("importToDefault"))) {
        importContext.setImportToDefault(true);
      }
      ImportResult importResult = importDataFromExcel(importContext,request);

      return importResult;
    }catch (Exception e){
      LOG.error(e.getMessage(), e);
      result.put("result","error");
      result.put("errorMsg", "导入异常，请联系客服!");
      return result;
    }
  }


  //校验传入的文件的名称
  private boolean validateExcelFileName(ImportType importType,String fileName)
  {
    if(ImportType.CUSTOMER.equals(importType) && ImportConstants.IMPORT_CUSTOMER_FILE_NAME.equals(fileName))
    {
      return true;
    }
    if(ImportType.SUPPLIER.equals(importType) && ImportConstants.IMPORT_SUPPLIER_FILE_NAME.equals(fileName))
    {
      return true;
    }
    if(ImportType.INVENTORY.equals(importType) && ImportConstants.IMPORT_INVENTORY_FILE_NAME.equals(fileName))
    {
      return true;
    }
    if(ImportType.MEMBER_SERVICE.equals(importType) && ImportConstants.IMPORT_MEMBER_SERVICE_FILE_NAME.equals(fileName))
    {
      return true;
    }
    if(ImportType.ORDER.equals(importType))
    {
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
    //此处处理单据逻辑

    return false;
  }


  //校验头部信息是否和默认的吻合
  private boolean validateExcelHeadContext(ImportType importType,List<String> headList,String filedName,boolean isWholesalers)
  {
    Map<String,String> map =  getHeadMapping(importType,filedName,isWholesalers);
    if(CollectionUtils.isEmpty(headList) || map.keySet().size() != headList.size())
    {
      return false;
    }
    for(String str : headList)
    {
      if(map.get(str) == null)
      {
        return false;
      }
    }

    return true;
  }


  //导入数据
  private ImportResult importDataFromExcel(ImportContext importContext,HttpServletRequest request) throws Exception
  {
    ITxnService txnService = ServiceManager.getService(ITxnService.class);
    ImportResult importResult = null;
    if(ImportConstants.getImportConstantsType(ImportType.CUSTOMER).equals(importContext.getType()))
    {
      ICustomerService customerService = ServiceManager.getService(ICustomerService.class);
      importResult = customerService.simpleImportCustomerFromExcel(importContext);
      if(importResult.isSuccess()) {
        ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexCustomerIndexList(importContext.getShopId(), 2000);
      }
    }
    else if(ImportConstants.getImportConstantsType(ImportType.SUPPLIER).equals(importContext.getType()))
    {
      ISupplierService supplierService = ServiceManager.getService(ISupplierService.class);
      Map<String,List<SupplierRecordDTO>> supplierRecordDTOMap = new HashMap<String,List<SupplierRecordDTO>>();
      importResult = supplierService.simpleImportSupplierFromExcel(supplierRecordDTOMap, importContext);
      List<SupplierRecordDTO> supplierRecordDTOList = supplierRecordDTOMap.get("supplierRecordDTOList");

      txnService.importSupplierRecord(WebUtil.getShopId(request),supplierRecordDTOList);

      if(importResult.isSuccess())
      {
        ServiceManager.getService(ICustomerOrSupplierSolrWriteService.class).reindexSupplierIndexList(WebUtil.getShopId(request), 2000);
      }
    }
    else if(ImportConstants.getImportConstantsType(ImportType.INVENTORY).equals(importContext.getType()))
    {
      importResult = txnService.simpleImportInventoryFromExcel(importContext);
    }
    else if(ImportConstants.getImportConstantsType(ImportType.MEMBER_SERVICE).equals(importContext.getType()))
    {
      importResult = txnService.simpleImportMemberServiceFromExcel(importContext);

      if(importResult.isSuccess())
      {
        IOrderSolrWriterService orderSolrWriterService = ServiceManager.getService(IOrderSolrWriterService.class);

        orderSolrWriterService.reCreateRepairServiceSolrIndex(importContext.getShopId(),2000);
      }
    }
    else if(ImportConstants.getImportConstantsType(ImportType.ORDER).equals(importContext.getType()))
    {
      importResult = txnService.simpleImportOrderFromExcel(importContext);
    }
    return importResult;
  }

  //获取对应关系 （name:name_desc）
  private Map<String, String> getFiledMapping(ImportType importType,String filedName,boolean isWholesalers)
  {
    Map<String, String> map = new HashMap<String, String>();
    if(ImportType.CUSTOMER.equals(importType))
    {
      for(String str : CustomerImportConstants.fieldList)
      {

        String[] strArray = str.split("_");
        if(isWholesalers)
        {
          if(CustomerImportConstants.FieldName.MEMBER_DISCOUNT_DESC.equals(strArray[1]))
          {
            continue;
          }
          if(CustomerImportConstants.FieldName.TYPE_DESC.equals(strArray[1]))
          {
            continue;
          }
          if(CustomerImportConstants.FieldName.BALANCE_DESC.equals(strArray[1]))
          {
            continue;
          }
          if(CustomerImportConstants.FieldName.ACCUMULATE_POINTS_DESC.equals(strArray[1]))
          {
            continue;
          }
          if(CustomerImportConstants.FieldName.JOIN_DATE_DESC.equals(strArray[1]))
          {
            continue;
          }
          if(CustomerImportConstants.FieldName.DEADLINE_DESC.equals(strArray[1]))
          {
            continue;
          }
          if(CustomerImportConstants.FieldName.MEMBER_DISCOUNT_DESC.equals(strArray[1]))
          {
            continue;
          }

          if(CustomerImportConstants.FieldName.VEHICLE_LICENCE_NO_DESC.equals(strArray[1]))
          {
            continue;
          }
          if(CustomerImportConstants.FieldName.VEHICLE_BRAND_DESC.equals(strArray[1]))
          {
            continue;
          }
          if(CustomerImportConstants.FieldName.VEHICLE_MODEL_DESC.equals(strArray[1]))
          {
            continue;
          }
          if(CustomerImportConstants.FieldName.VEHICLE_YEAR_DESC.equals(strArray[1]))
          {
            continue;
          }
          if(CustomerImportConstants.FieldName.VEHICLE_ENGINE_DESC.equals(strArray[1]))
          {
            continue;
          }
          if(CustomerImportConstants.FieldName.VEHICLE_ENGINE_NO_DESC.equals(strArray[1]))
          {
            continue;
          }
          if(CustomerImportConstants.FieldName.VEHICLE_CHASSIS_NUMBER_DESC.equals(strArray[1]))
          {
            continue;
          }
        }  else {
          if(ArrayUtil.contains(CustomerImportConstants.REPAIR_CUSTOMER_EXCLUDE_FIELDS, strArray[1])) {
            continue;
          }
        }

        map.put(strArray[0],strArray[1]);
      }
    }
    else if(ImportType.SUPPLIER.equals(importType))
    {
      for(String str : SupplierImportConstants.fieldList)
      {
        String[] strArray = str.split("_");
        map.put(strArray[0],strArray[1]);
      }
    }
    else if(ImportType.INVENTORY.equals(importType))
    {
      for(String str : InventoryImportConstants.fieldList)
      {
        String[] strArray = str.split("_");
        map.put(strArray[0],strArray[1]);
      }
    }
    else if(ImportType.MEMBER_SERVICE.equals(importType))
    {
      for(String str : MemberServiceImportConstants.fieldList)
      {
        String[] strArray = str.split("_");
        map.put(strArray[0],strArray[1]);
      }
    }
    else if(ImportType.ORDER.equals(importType))
    {
      if("施工单".equals(filedName))
      {
        for(String str : OrderImportConstants.fieldList)
        {
          String[] strArray = str.split("_");
          map.put(strArray[0],strArray[1]);
        }
      }
      if("入库单".equals(filedName))
      {
        for(String str : OrderImportConstants.inventoryFieldList)
        {
          String[] strArray = str.split("_");
          map.put(strArray[0],strArray[1]);
        }
      }
      if("销售单".equals(filedName))
      {
        for(String str : OrderImportConstants.saleFieldList)
        {
          String[] strArray = str.split("_");
          map.put(strArray[0],strArray[1]);
        }
      }

      if("洗车美容".equals(filedName))
      {
        for(String str : OrderImportConstants.washFieldList)
        {
          String[] strArray = str.split("_");
          map.put(strArray[0],strArray[1]);
        }
      }
    }


    return map;
  }

  //获取对应关系 （name_desc:name_desc）;用于校验
  private Map<String, String> getHeadMapping(ImportType importType, String filedName, boolean isWholesalers) {
    Map<String, String> map = new HashMap<String, String>();
    if (ImportType.CUSTOMER.equals(importType)) {
      for (String str : CustomerImportConstants.fieldList) {
        String[] strArray = str.split("_");
        if (isWholesalers) {
          if(ArrayUtil.contains(CustomerImportConstants.WHOLESALER_EXCLUDE_FIELDS, strArray[1])){
            continue;
          }
        } else {
          if(ArrayUtil.contains(CustomerImportConstants.REPAIR_CUSTOMER_EXCLUDE_FIELDS, strArray[1])) {
            continue;
          }
        }
        map.put(strArray[1], strArray[1]);
      }
    }
    if (ImportType.SUPPLIER.equals(importType)) {
      for (String str : SupplierImportConstants.fieldList) {
        String[] strArray = str.split("_");
        map.put(strArray[1], strArray[1]);
      }
    }
    if (ImportType.INVENTORY.equals(importType)) {
      for (String str : InventoryImportConstants.fieldList) {
        String[] strArray = str.split("_");
        map.put(strArray[1], strArray[1]);
      }
    }
    if (ImportType.MEMBER_SERVICE.equals(importType)) {
      for (String str : MemberServiceImportConstants.fieldList) {
        String[] strArray = str.split("_");
        map.put(strArray[1], strArray[1]);
      }
    }
    if (ImportType.ORDER.equals(importType)) {
      if ("施工单".equals(filedName)) {
        for (String str : OrderImportConstants.fieldList) {
          String[] strArray = str.split("_");
          if (isWholesalers) {
            if(ArrayUtil.contains(OrderImportConstants.WHOLESALER_EXCLUDE_FIELDS, strArray[1])){
              continue;
            }
          }
          map.put(strArray[1], strArray[1]);
        }
      }
      if ("入库单".equals(filedName)) {
        for (String str : OrderImportConstants.inventoryFieldList) {
          String[] strArray = str.split("_");
          if (isWholesalers) {
            if(ArrayUtil.contains(OrderImportConstants.WHOLESALER_EXCLUDE_FIELDS, strArray[1])){
              continue;
            }
          }
          map.put(strArray[1], strArray[1]);
        }
      }
      if ("销售单".equals(filedName)) {
        for (String str : OrderImportConstants.saleFieldList) {
          String[] strArray = str.split("_");
          if (isWholesalers) {
            if(ArrayUtil.contains(OrderImportConstants.WHOLESALER_EXCLUDE_FIELDS, strArray[1])){
              continue;
            }
          }
          map.put(strArray[1], strArray[1]);
        }
      }

      if ("洗车美容".equals(filedName)) {
        for (String str : OrderImportConstants.washFieldList) {
          String[] strArray = str.split("_");
          if (isWholesalers) {
            if(ArrayUtil.contains(OrderImportConstants.WHOLESALER_EXCLUDE_FIELDS, strArray[1])){
              continue;
            }
          }
          map.put(strArray[1], strArray[1]);
        }
      }
    }
    return map;
  }
}
