package com.bcgogo.admin.obd;

import com.bcgogo.api.*;
import com.bcgogo.common.Result;
import com.bcgogo.config.dto.ImportRecordDTO;
import com.bcgogo.config.dto.ImportResult;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.excelimport.BcgogoExcelParser;
import com.bcgogo.config.service.excelimport.ImportContext;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.constant.ImportConstants;
import com.bcgogo.enums.importData.ImportType;
import com.bcgogo.enums.shop.ShopKind;
import com.bcgogo.enums.user.DepartmentResponsibility;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.UserDTO;
import com.bcgogo.user.dto.permission.UserSearchCondition;
import com.bcgogo.user.permission.UserSearchResult;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.user.service.excelimport.obd.ObdImportConstants;
import com.bcgogo.user.service.excelimport.obd.ObdImporter;
import com.bcgogo.user.service.obd.IObdManagerService;
import com.bcgogo.user.service.permission.IObdManagerPermissionService;
import com.bcgogo.user.service.permission.IUserCacheService;
import com.bcgogo.util.WebUtil;
import com.bcgogo.utils.BGIOUtil;
import com.bcgogo.utils.DateUtil;
import com.bcgogo.utils.JsonUtil;
import com.bcgogo.utils.NumberUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by XinyuQiu on 14-6-19.
 */
@Controller
@RequestMapping("/obdManage.do")
public class ObdManagerController {

  private static final Logger LOG = LoggerFactory.getLogger(ObdManagerController.class);

  @Autowired
  private BcgogoExcelParser bcgogoExcelParser;

  @Autowired
  private ObdImporter obdImporter;

  //OBD库存查看
  @RequestMapping(params = "method=getObdSimBySearchCondition")
  @ResponseBody
  public Object getObdSimBySearchCondition(HttpServletRequest request, HttpServletResponse response,
                                           ObdSimSearchCondition condition){
    ObdSimSearchResult result ;
    IObdManagerService obdManagerService = ServiceManager.getService(IObdManagerService.class);
    IObdManagerPermissionService obdManagerPermissionService = ServiceManager.getService(IObdManagerPermissionService.class);
    try{
      if(condition == null){
        condition = new ObdSimSearchCondition();
      }
      condition.generateSearchInfo();
      Long userId = WebUtil.getUserId(request);
      ObdOperationPermissionDTO permissionDTO = obdManagerPermissionService.getObdOperationPermissionDTO(WebUtil.getUserGroupId(request));
      condition.setAdmin(permissionDTO.isAdmin());
      Set<Long> userIds = new HashSet<Long>();
      userIds.add(WebUtil.getUserId(request));
      if(!condition.isAdmin()){
        IUserService userService = ServiceManager.getService(IUserService.class);
        UserDTO userDTO = userService.getUserByUserId(userId);

        if(userDTO!=null && userDTO.getDepartmentId() != null && DepartmentResponsibility.LEADER.equals(userDTO.getDepartmentResponsibility())){
          IUserCacheService userCacheService = ServiceManager.getService(IUserCacheService.class);
          UserSearchCondition userSearchCondition = new UserSearchCondition();
          userSearchCondition.setHasPager(false);
          userSearchCondition.setShopId(WebUtil.getShopId(request));
          userSearchCondition.setDepartmentId(userDTO.getDepartmentId());
          UserSearchResult userSearchResult = userCacheService.getUsersByDepartmentId(userSearchCondition);
          if(userSearchResult != null && CollectionUtils.isNotEmpty(userSearchResult.getResults())){
            for(UserDTO memberUserDTO : userSearchResult.getResults()){
              if(memberUserDTO != null && memberUserDTO.getId() != null){
                userIds.add(memberUserDTO.getId());
              }
            }
          }
        }
      }
      condition.setUserIds(userIds);
      result = new ObdSimSearchResult();
      int totalObdSimCount = obdManagerService.countObdSimBindDTO(condition);
      result.setTotals(totalObdSimCount);
      if(totalObdSimCount>0){
        List<ObdSimBindDTO> obdSimBindDTOs = obdManagerService.searchObdSimBindDTO(condition);
        if(CollectionUtils.isNotEmpty(obdSimBindDTOs)){
          for(ObdSimBindDTO obdSimBindDTO : obdSimBindDTOs){
            obdSimBindDTO.setPermissionDTO(permissionDTO);

          }
        }
        result.setResults(obdSimBindDTOs);
      }
    }catch (Exception e){
      result = new ObdSimSearchResult();
      result.setSuccess(false);
      LOG.error("method=getObdSimBySearchCondition"+e.getMessage(), e);
    }
    return result;
  }

  @RequestMapping(params = "method=uploadObdInventory")
  public void uploadObdInventory(HttpServletRequest request,HttpServletResponse response) {
    response.setContentType("text/html");
    PrintWriter printWriter = null;
    IObdManagerService obdManagerService = ServiceManager.getService(IObdManagerService.class);
    ImportType importType = ImportType.OBD_INVENTORY;
    ImportResult importResult = new ImportResult();
    Map<String,Object> result = new HashMap<String, Object>();
    try {
      printWriter= response.getWriter();
      MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
      MultipartFile multipartFile = multipartRequest.getFile("file");
      String fileName = multipartFile.getOriginalFilename();
      InputStream inputStream = multipartFile.getInputStream();
      byte[] fileContent = BGIOUtil.readFromStream(inputStream);
      //先读取表头
      ImportContext importContext = new ImportContext();
      importContext.setShopId(WebUtil.getShopId(request));
      importContext.setUserId(WebUtil.getUserId(request));
      importContext.setUserName(WebUtil.getUserName(request));

      importContext.setFileContent(fileContent);
      importContext.setFileName(fileName);
      List<String> headList = bcgogoExcelParser.parseHead(importContext.getFileContent(), importContext.getVersion());
      //todo 校验头文件
      //方法验证头内容,传入参数importType
      if(!obdImporter.validateExcelHeadContext(headList))
      {
        importResult.setSuccess(false);
        importResult.setMessage(ImportConstants.getHeadListErrorInfo(importType, fileName.split("\\.")[0]));
      } else {

        ImportRecordDTO importRecordDTO = new ImportRecordDTO();
        importRecordDTO.setStatus(ImportConstants.Status.STATUS_WAITING);
        importRecordDTO.setType(ImportType.OBD_INVENTORY.name());
        importRecordDTO.setFileName(fileName);
        importRecordDTO.setFileContent(fileContent);

        List<ImportRecordDTO> importRecordDTOList = new ArrayList<ImportRecordDTO>();
        importRecordDTOList.add(importRecordDTO);
        importContext.setImportRecordDTOList(importRecordDTOList);
        importContext.setType(ImportType.OBD_INVENTORY.name());
        importContext.setFieldMapping(ObdImportConstants.filedMap);
        importResult = obdManagerService.importOBDInventoryFromExcel(importContext);
      }
      result.put("success",importResult.isSuccess());
      result.put("importResult",importResult);
      printWriter.write(JsonUtil.mapToJson(result));
    } catch (Exception e) {
      LOG.error("/obdManage.do?method=uploadObdInventory");
      LOG.error(e.getMessage(), e);
      importResult.setMessage("网络异常");
      importResult.setSuccess(false);
      result.put("success",importResult.isSuccess());
      result.put("importResult",importResult);
      if(printWriter != null){
        printWriter.write(JsonUtil.mapToJson(result));
      }
    } finally {
      if(printWriter!=null){
        printWriter.flush();
        printWriter.close();
      }
    }
  }

  //single obd sim edit
  @RequestMapping(params = "method=updateSingleObdSim")
  @ResponseBody
  public Object updateSingleObdSim(HttpServletRequest request,HttpServletResponse response,ObdSimBindDTO obdSimBindDTO){
    Result result = new Result();
    IObdManagerService obdManagerService = ServiceManager.getService(IObdManagerService.class);
    try{
      addOperationInfo(obdSimBindDTO,request);
      if(StringUtils.isNotBlank(obdSimBindDTO.getUseDateStr())){
        obdSimBindDTO.setUseDate(DateUtil.convertDateStringToDateLong(DateUtil.DATE_STRING_FORMAT_YEAR_MON,obdSimBindDTO.getUseDateStr()));
      }
      result = obdManagerService.updateSingleObdSim(obdSimBindDTO);
    }catch (Exception e){
      result.setMsg(false,"网络异常，更新失败，请查看后台日志！");
      LOG.error("method=uploadObdInventory"+e.getMessage(), e);
    }
    return result;
  }

  //single obd sim edit
  @RequestMapping(params = "method=updateMultiObdSim")
  @ResponseBody
  public Object updateMultiObdSim(HttpServletRequest request,HttpServletResponse response,MultiObdSimUpdateDTO multiObdSimUpdateDTO){
    Result result = new Result();

    IObdManagerService obdManagerService = ServiceManager.getService(IObdManagerService.class);

    try {
      multiObdSimUpdateDTO.generateUpdateInfo();
      addOperationInfo(multiObdSimUpdateDTO.getNewObdSimBindDTO(), request);
      result = obdManagerService.updateMultiObdSim(multiObdSimUpdateDTO);
    } catch (Exception e) {
      result.setMsg(false, "网络异常，更新失败，请查看后台日志！");
      LOG.error("method=updateMultiObdSim" + e.getMessage(), e);
    }
    return result;
  }

  //OBD库存查看
  @RequestMapping(params = "method=getObdSimOperationLog")
  @ResponseBody
  public Object getObdSimOperationLog(HttpServletRequest request, HttpServletResponse response,
                                      OBDSimOperationLogDTOSearchCondition condition){
    OBDSimOperationLogDTOSearchResult result ;
    IObdManagerService obdManagerService = ServiceManager.getService(IObdManagerService.class);
    try{

      result = new OBDSimOperationLogDTOSearchResult();
      int totalObdSimLogCount = obdManagerService.countObdSimOperationLogDTOs(condition);
      result.setTotals(totalObdSimLogCount);
      if(totalObdSimLogCount>0){
        List<OBDSimOperationLogDTO> obdSimOperationLogDTOs = obdManagerService.getObdSimOperationLogDTOs(condition);
        result.setResults(obdSimOperationLogDTOs);
      }
    }catch (Exception e){
      result = new OBDSimOperationLogDTOSearchResult();
      result.setSuccess(false);
      LOG.error("method=getObdSimOperationLog"+e.getMessage(), e);
    }
    return result;
  }

  //OBD sim 拆分
  @RequestMapping(params = "method=splitObdSim")
  @ResponseBody
  public Object splitObdSim(HttpServletRequest request, HttpServletResponse response,
                            ObdSimBindDTO obdSimBindDTO){
    Result result = new Result();
    IObdManagerService obdManagerService = ServiceManager.getService(IObdManagerService.class);
    try{
      addOperationInfo(obdSimBindDTO,request);
      result = obdManagerService.splitObdSimBind(obdSimBindDTO);
    }catch (Exception e){
      result.setMsg(false, "网络异常");
      LOG.error("method=splitObdSim"+e.getMessage(), e);
    }
    return result;
  }

  private void addOperationInfo(ObdSimBindDTO obdSimBindDTO,HttpServletRequest request){
    if(obdSimBindDTO != null && request != null){
      obdSimBindDTO.setOperateShopId(WebUtil.getShopId(request));
      obdSimBindDTO.setOperateUserId(WebUtil.getUserId(request));
      obdSimBindDTO.setOperateUserName(WebUtil.getUserName(request));
    }
  }

  //OBD imei 下拉建议
  @RequestMapping(params = "method=getImeiSuggestion")
  @ResponseBody
  public Object getImeiSuggestion(HttpServletRequest request, HttpServletResponse response,ObdImeiSuggestion suggestion){
    Map<String,Object> resultMap = new HashMap<String, Object>();
    IObdManagerService obdManagerService = ServiceManager.getService(IObdManagerService.class);
    try{
      suggestion.generateSearchInfo();
      int total = obdManagerService.countObdImeiSuggestion(suggestion);
      List<ObdSimBindDTO> obdSimBindDTOs = new ArrayList<ObdSimBindDTO>();
      if(total>0){
        obdSimBindDTOs = obdManagerService.getObdImeiSuggestion(suggestion);
      }
      resultMap.put("success",true);
      resultMap.put("results",obdSimBindDTOs);
      resultMap.put("totals",total);
    }catch (Exception e){
      resultMap.put("success",false);
      resultMap.put("msg","网络异常");
      LOG.error("method=getImeiSuggestion"+e.getMessage(), e);
    }
    return resultMap;
  }

  //OBD imei 下拉建议
  @RequestMapping(params = "method=getObdVersionSuggestion")
  @ResponseBody
  public Object getObdVersionSuggestion(HttpServletRequest request, HttpServletResponse response,ObdVersionSuggestion suggestion){
    Map<String,Object> resultMap = new HashMap<String, Object>();
    IObdManagerService obdManagerService = ServiceManager.getService(IObdManagerService.class);
    try{
      suggestion.generateSearchInfo();
      int total = obdManagerService.countObdVersionSuggestion(suggestion);
      List<ObdSimBindDTO> obdSimBindDTOs = new ArrayList<ObdSimBindDTO>();
      if(total>0){
        obdSimBindDTOs = obdManagerService.getObdVersionSuggestion(suggestion);
      }
      resultMap.put("success",true);
      resultMap.put("results",obdSimBindDTOs);
      resultMap.put("totals",total);
    }catch (Exception e){
      resultMap.put("success",false);
      resultMap.put("msg","网络异常");
      LOG.error("method=getImeiSuggestion"+e.getMessage(), e);
    }
    return resultMap;
  }

  //OBD SIM mobile 下拉建议
  @RequestMapping(params = "method=getSimMobileSuggestion")
  @ResponseBody
  public Object getSimMobileSuggestion(HttpServletRequest request, HttpServletResponse response,SimMobileSuggestion suggestion){
    Map<String,Object> resultMap = new HashMap<String, Object>();
    IObdManagerService obdManagerService = ServiceManager.getService(IObdManagerService.class);
    try{
      suggestion.generateSearchInfo();
      int total = obdManagerService.countObdSimMobileSuggestion(suggestion);
      List<ObdSimBindDTO> obdSimBindDTOs = new ArrayList<ObdSimBindDTO>();
      if(total>0){
        obdSimBindDTOs = obdManagerService.getObdSimMobileSuggestion(suggestion);
      }
      resultMap.put("success",true);
      resultMap.put("results",obdSimBindDTOs);
      resultMap.put("totals",total);
    }catch (Exception e){
      resultMap.put("success",false);
      resultMap.put("msg","网络异常");
      LOG.error("method=getSimMobileSuggestion"+e.getMessage(), e);
    }
    return resultMap;
  }

  //OBD 组装
  @RequestMapping(params = "method=combineObdSim")
  @ResponseBody
  public Object combineObdSim(HttpServletRequest request, HttpServletResponse response, ObdSimBindDTO obdSimBindDTO) {
    Result result = new Result();
    IObdManagerService obdManagerService = ServiceManager.getService(IObdManagerService.class);
    try {
      addOperationInfo(obdSimBindDTO, request);
      result = obdManagerService.combineObdSim(obdSimBindDTO);
    } catch (Exception e) {
      result.setMsg(false, "网络异常");
      LOG.error("method=combineObdSim" + e.getMessage(), e);
    }
    return result;
  }


  //OBD 删除
  @RequestMapping(params = "method=deleteObdSim")
  @ResponseBody
  public Object deleteObdSim(HttpServletRequest request, HttpServletResponse response, ObdSimBindDTO obdSimBindDTO) {
    Result result = new Result();
    IObdManagerService obdManagerService = ServiceManager.getService(IObdManagerService.class);
    try {
      addOperationInfo(obdSimBindDTO, request);
      result = obdManagerService.deleteObdSim(obdSimBindDTO);
    } catch (Exception e) {
      result.setMsg(false, "网络异常");
      LOG.error("method=deleteObdSim" + e.getMessage(), e);
    }
    return result;
  }

  //获取OBD SIM操作权限
  @RequestMapping(params = "method=getOBDSimOperation")
  @ResponseBody
  public Object getOBDSimOperation(HttpServletRequest request, HttpServletResponse response) {
    Result result = new Result();
    IObdManagerPermissionService obdManagerPermissionService = ServiceManager.getService(IObdManagerPermissionService.class);
    try {
      ObdOperationPermissionDTO permissionDTO = obdManagerPermissionService.getObdOperationPermissionDTO(WebUtil.getUserGroupId(request));

      result.setData(permissionDTO);
    } catch (Exception e) {
      result.setMsg(false, "网络异常");
      LOG.error("method=getOBDSimOperation" + e.getMessage(), e);
    }
    return result;
  }

  //OBD 出库 店铺选择
  @RequestMapping(params = "method=getShopNameSelection")
  @ResponseBody
  public Object getShopNameSelection(HttpServletRequest request, HttpServletResponse response,ShopNameSuggestion suggestion){
    Map<String,Object> resultMap = new HashMap<String, Object>();
    IObdManagerService obdManagerService = ServiceManager.getService(IObdManagerService.class);
    try{
      Long foursShopVersion = NumberUtil.longValue(ConfigUtils.getFourSShopVersions());
      Set<Long> shopVersions = new HashSet<Long>();
      shopVersions.add(foursShopVersion);
//      suggestion.setShopVersionIds(shopVersions);  可以出库到所有类型的店铺
      Set<ShopKind> shopKinds = new HashSet<ShopKind>();
      shopKinds.add(ShopKind.OFFICIAL);
      suggestion.setShopKinds(shopKinds);
      int total = obdManagerService.countObdOutStorageShopNameSuggestion(suggestion);
      List<ShopDTO> shopDTOs = new ArrayList<ShopDTO>();
      if(total>0){
        shopDTOs = obdManagerService.getObdOutStorageShopNameSuggestion(suggestion);
      }
      resultMap.put("success",true);
      resultMap.put("results",shopDTOs);
      resultMap.put("totals",total);
    }catch (Exception e){
      resultMap.put("success",false);
      resultMap.put("msg","网络异常");
      LOG.error("method=getShopNameSuggestion"+e.getMessage(), e);
    }
    return resultMap;
  }

  //OBD 出库 代理商选择
  @RequestMapping(params = "method=getAgentNameSelection")
  @ResponseBody
  public Object getAgentNameSelection(HttpServletRequest request, HttpServletResponse response,AgentNameSuggestion suggestion){
    Map<String,Object> resultMap = new HashMap<String, Object>();
    IObdManagerService obdManagerService = ServiceManager.getService(IObdManagerService.class);
    try{
      Set<Long> agentDepartmentIds = ConfigUtils.getAgentDepartmentIdStr();
      suggestion.setDepartmentIds(agentDepartmentIds);
      suggestion.setShopId(WebUtil.getShopId(request));
      int total = obdManagerService.countAgentNameSuggestion(suggestion);
      List<UserDTO> userDTOs = new ArrayList<UserDTO>();
      if(total>0){
        userDTOs = obdManagerService.getAgentNameSuggestion(suggestion);
      }
      resultMap.put("success",true);
      resultMap.put("results",userDTOs);
      resultMap.put("totals",total);
    }catch (Exception e){
      resultMap.put("success",false);
      resultMap.put("msg","网络异常");
      LOG.error("method=getShopNameSuggestion"+e.getMessage(), e);
    }
    return resultMap;
  }

  //OBD 出库 员工选择
  @RequestMapping(params = "method=getStaffNameSelection")
  @ResponseBody
  public Object getStaffNameSelection(HttpServletRequest request, HttpServletResponse response,AgentNameSuggestion suggestion){
    Map<String,Object> resultMap = new HashMap<String, Object>();
    IObdManagerService obdManagerService = ServiceManager.getService(IObdManagerService.class);
    try{
      Set<Long> agentDepartmentIds = ConfigUtils.getAgentDepartmentIdStr();
      suggestion.setDepartmentIds(agentDepartmentIds);
      suggestion.setShopId(WebUtil.getShopId(request));
      int total = obdManagerService.countStaffNameSuggestion(suggestion);
      List<UserDTO> userDTOs = new ArrayList<UserDTO>();
      if(total>0){
        userDTOs = obdManagerService.getStaffNameSuggestion(suggestion);
      }
      resultMap.put("success",true);
      resultMap.put("results",userDTOs);
      resultMap.put("totals",total);
    }catch (Exception e){
      resultMap.put("success",false);
      resultMap.put("msg","网络异常");
      LOG.error("method=getStaffNameSelection"+e.getMessage(), e);
    }
    return resultMap;
  }

  //OBD 出库
  @RequestMapping(params = "method=obdSimOutStorage")
  @ResponseBody
  public Object obdSimOutStorage(HttpServletRequest request, HttpServletResponse response,ObdSimOutStorageDTO outStorageDTO){
    Result result = new Result();
    IObdManagerService obdManagerService = ServiceManager.getService(IObdManagerService.class);
    try{
      outStorageDTO.generateInfo();
      outStorageDTO.setOperationName(WebUtil.getUserName(request));
      outStorageDTO.setOperationShopId(WebUtil.getShopId(request));
      outStorageDTO.setOperationUserId(WebUtil.getUserId(request));
      result = obdManagerService.obdSimOutStorage(outStorageDTO);
    }catch (Exception e){
      result.setMsg(false, "网络异常");
      LOG.error("method=obdSimOutStorage" + e.getMessage(), e);
    }
    return result;
  }

  //OBD 销售
  @RequestMapping(params = "method=obdSimSell")
  @ResponseBody
  public Object obdSimSell(HttpServletRequest request, HttpServletResponse response,ObdSimOutStorageDTO outStorageDTO){
    Result result = new Result();
    IObdManagerService obdManagerService = ServiceManager.getService(IObdManagerService.class);
    try{
      outStorageDTO.generateInfo();
      outStorageDTO.setOperationName(WebUtil.getUserName(request));
      outStorageDTO.setOperationShopId(WebUtil.getShopId(request));
      outStorageDTO.setOperationUserId(WebUtil.getUserId(request));
      result = obdManagerService.obdSimSell(outStorageDTO);
    }catch (Exception e){
      result.setMsg(false, "网络异常");
      LOG.error("method=obdSimSell" + e.getMessage(), e);
    }
    return result;
  }

  //OBD 归还
  @RequestMapping(params = "method=obdSimReturn")
  @ResponseBody
  public Object obdSimReturn(HttpServletRequest request, HttpServletResponse response,ObdSimReturnDTO obdSimReturnDTO){
    Result result = new Result();
    IObdManagerService obdManagerService = ServiceManager.getService(IObdManagerService.class);
    try{
      obdSimReturnDTO.generateInfo();
      obdSimReturnDTO.setOperationName(WebUtil.getUserName(request));
      obdSimReturnDTO.setOperationShopId(WebUtil.getShopId(request));
      obdSimReturnDTO.setOperationUserId(WebUtil.getUserId(request));
      result = obdManagerService.obdSimReturn(obdSimReturnDTO);
    }catch (Exception e){
      result.setMsg(false, "网络异常");
      LOG.error("method=obdSimReturn" + e.getMessage(), e);
    }
    return result;
  }
}
