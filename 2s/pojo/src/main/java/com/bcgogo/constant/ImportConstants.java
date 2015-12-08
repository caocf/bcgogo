package com.bcgogo.constant;

import com.bcgogo.enums.importData.ImportType;
import com.bcgogo.utils.BcgogoI18N;
import com.bcgogo.utils.NumberUtil;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-3-30
 * Time: 下午6:33
 * To change this template use File | Settings | File Templates.
 */
public class ImportConstants {

  /* 上传文件最大不能超过16M */
  public static final int UPLOAD_FILE_MAX_SIZE = 16777216;

  public static final String MESSAGE_NEED_FILE_OR_MAPPING = "缺少文件或者字段映射！";

  /**
   * 导入状态
   */
  public class Status {
    /* 等待导入 */
    public static final String STATUS_WAITING = "waiting";
    /* 已成功导入 */
    public static final String STATUS_SUCCESS = "success";
    /* 导入失败 */
    public static final String STATUS_FAIL = "fail";
  }

  /**
   * 导入类型
   */
  public class Type {
    /* 客户 */
    public static final String TYPE_CUSTOMER = "customer";
    public static final String TYPE_DESC_CUSTOMER = "客户";
    /* 供应桑 */
    public static final String TYPE_SUPPLIER = "supplier";
    public static final String TYPE_DESC_SUPPLIER = "供应商";
    /* 库存 */
    public static final String TYPE_INVENTORY = "inventory";
    public static final String TYPE_DESC_INVENTORY = "库存";
    /*会员服务*/
    public static final String TYPE_MEMBER_SERVICE = "memberService";
    public static final String TYPE_DESC_MEMBER_SERVICE = "会员服务";
    /* 客户 */
    public static final String TYPE_ORDER = "order";
    public static final String TYPE_DESC_ORDER = "单据";
  }

  public static Map<String, String> importTypes = new HashMap<String, String>();

  public static Map<String, String> getImportTypes(Locale locale){
    importTypes.put(Type.TYPE_CUSTOMER, BcgogoI18N.getMessageByKey("import.type.customer", locale));
    importTypes.put(Type.TYPE_SUPPLIER, BcgogoI18N.getMessageByKey("import.type.supplier", locale));
    importTypes.put(Type.TYPE_INVENTORY, BcgogoI18N.getMessageByKey("import.type.inventory", locale));
    importTypes.put(Type.TYPE_MEMBER_SERVICE,BcgogoI18N.getMessageByKey("import.type.memberService", locale));
    importTypes.put(Type.TYPE_ORDER,BcgogoI18N.getMessageByKey("import.type.order", locale));
    return importTypes;
  }


  public class ExcelVersion {
    public static final String EXCEL_VERSION_2003_SUFFIX = ".xls";
    public static final String EXCEL_VERSION_2007_SUFFIX = ".xlsx";

    public static final String EXCEL_VERSION_2003 = "2003";
    public static final String EXCEL_VERSION_2007 = "2007";
  }

  public static String IMPORT_CUSTOMER_FILE_NAME = "BCGOGO客户导入";
  public static String IMPORT_SUPPLIER_FILE_NAME = "BCGOGO供应商导入";
  public static String IMPORT_INVENTORY_FILE_NAME = "BCGOGO库存导入";
  public static String IMPORT_MEMBER_SERVICE_FILE_NAME = "BCGOGO会员服务导入";
  public static String IMPORT_ORDER_FILE_NAME = "BCGOGO单据导入";

  public static String NO_IMPORT_TYPE = "没有选择上传类型或者上传类型不正确";
  public static String FILE_TOO_BIG = "上传文件大小不能超过" + NumberUtil.byteToMillion(ImportConstants.UPLOAD_FILE_MAX_SIZE) + "M！";
  public static String TRANSFORM_IMPORT_TYPE_ERROR = "上传类型在系统中不存在";
  public static String IMPORT_CUSTOMER_FIle_NAME_ERROR = "请上传名称是'BCGOGO客户导入'的excel文件";
  public static String IMPORT_SUPPLIER_FIle_NAME_ERROR = "请上传名称是'BCGOGO供应商导入'的excel文件";
  public static String IMPORT_INVENTORY_FIle_NAME_ERROR = "请上传名称是'BCGOGO库存导入'的excel文件";
  public static String IMPORT_MEMBER_SERVICE_FIle_NAME_ERROR = "请上传名称是'BCGOGO会员服务导入'的excel文件";
  public static String IMPORT_ORDER_FIle_NAME_ERROR = "选择单据上传时请上传名称是'入库单'、'销售单'、'施工单'或者'洗车美容'的excel文件";

  public static String IMPORT_CUSTOMER_EXCEL_HEAD_LIST_ERROR = "客户上传的excel的头信息和从系统下载的excel文件头信息不吻合";
  public static String IMPORT_SUPPLIER_EXCEL_HEAD_LIST_ERROR = "供应商上传的excel的头信息和从系统下载的excel文件头信息不吻合";
  public static String IMPORT_INVENTORY_EXCEL_HEAD_LIST_ERROR = "库存上传的excel的头信息和从系统下载的excel文件头信息不吻合";
  public static String IMPORT_MEMBER_SERVICE_EXCEL_HEAD_LIST_ERROR = "会员服务上传的excel的头信息和从系统下载的excel文件头信息不吻合";
  public static String IMPORT_ORDER_EXCEL_HEAD_LIST_ERROR = "单据上传的excel的头信息和从系统下载的excel文件头信息不吻合";
  public static String IMPORT_OBD_EXCEL_HEAD_LIST_ERROR = "OBD库存导入的excel的头信息和从系统下载的excel文件头信息不吻合";


  public static String getImportConstantsType(ImportType importType)
  {
    if(ImportType.CUSTOMER.equals(importType))
    {
      return ImportConstants.Type.TYPE_CUSTOMER;
    }
    else if(ImportType.SUPPLIER.equals(importType))
    {
      return ImportConstants.Type.TYPE_SUPPLIER;
    }
    else if(ImportType.INVENTORY.equals(importType))
    {
      return ImportConstants.Type.TYPE_INVENTORY;
    }
    else if(ImportType.ORDER.equals(importType))
    {
      return ImportConstants.Type.TYPE_ORDER;
    }
    else if(ImportType.MEMBER_SERVICE.equals(importType))
    {
      return ImportConstants.Type.TYPE_MEMBER_SERVICE;
    }
    return null;
  }

  public static Map getImportTypes()
  {
    ImportType[] importTypes = ImportType.values();

    Map map = new HashMap();

    for(ImportType type : importTypes)
    {
      map.put(type.toString(),type.getType());
    }

    return map;
  }

  public static String getFileNameErrorInfo(ImportType importType)
  {
    if(ImportType.CUSTOMER.equals(importType))
    {
      return ImportConstants.IMPORT_CUSTOMER_FIle_NAME_ERROR;
    }
    else if(ImportType.SUPPLIER.equals(importType))
    {
      return ImportConstants.IMPORT_SUPPLIER_FIle_NAME_ERROR;
    }
    else if(ImportType.INVENTORY.equals(importType))
    {
      return ImportConstants.IMPORT_INVENTORY_FIle_NAME_ERROR;
    }
    else if(ImportType.MEMBER_SERVICE.equals(importType))
    {
      return ImportConstants.IMPORT_MEMBER_SERVICE_FIle_NAME_ERROR;
    }
    else if(ImportType.ORDER.equals(importType))
    {
      return ImportConstants.IMPORT_ORDER_FIle_NAME_ERROR;
    }
    //单据另外处理

    return ImportConstants.TRANSFORM_IMPORT_TYPE_ERROR;
  }

  public static String getHeadListErrorInfo(ImportType importType,String filedName)
  {
    if(ImportType.CUSTOMER.equals(importType))
    {
      return ImportConstants.IMPORT_CUSTOMER_EXCEL_HEAD_LIST_ERROR;
    }
    else if(ImportType.SUPPLIER.equals(importType))
    {
      return ImportConstants.IMPORT_SUPPLIER_EXCEL_HEAD_LIST_ERROR;
    }
    else if(ImportType.INVENTORY.equals(importType))
    {
      return ImportConstants.IMPORT_INVENTORY_EXCEL_HEAD_LIST_ERROR;
    }
    else if(ImportType.MEMBER_SERVICE.equals(importType))
    {
      return ImportConstants.IMPORT_MEMBER_SERVICE_EXCEL_HEAD_LIST_ERROR;
    }
    else if(ImportType.ORDER.equals(importType))
    {
      return filedName+ImportConstants.IMPORT_ORDER_EXCEL_HEAD_LIST_ERROR;
    }else if(ImportType.OBD_INVENTORY.equals(importType)){
      return ImportConstants.IMPORT_OBD_EXCEL_HEAD_LIST_ERROR;
    }

    return ImportConstants.TRANSFORM_IMPORT_TYPE_ERROR;
  }

}
