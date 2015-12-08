package com.bcgogo.driving.controller;

import ch.qos.logback.core.joran.spi.ElementSelector;
import com.bcgogo.driving.dao.TaskConstant;
import com.bcgogo.driving.dao.XConfigDao;
import com.bcgogo.driving.model.XMirrorTask;
import com.bcgogo.driving.service.IXMirrorTaskService;
import com.bcgogo.pojox.api.ApiMirrorTaskResponse;
import com.bcgogo.pojox.api.ParamDTO;
import com.bcgogo.pojox.api.VersionDTO;
import com.bcgogo.pojox.util.JsonUtil;
import com.bcgogo.pojox.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: LiTao
 * Date: 15-10-30
 * Time: 上午9:40
 * 后视镜任务Controller
 */
@Controller
public class XMirrorTaskController {
  private static final Logger LOG = LoggerFactory.getLogger(XMirrorTaskController.class);
  @Autowired
  private IXMirrorTaskService mirrorTaskService;

  /**
   * 获取后视镜任务列表
   * 通过vehicleId（imei）获取到后视镜任务的列表，列表存放在taskResponse的data中
   * taskResponse会自动转换成json格式的字符串，如下
   * result:	{code:0,msg:"",data:[{id:"",tid:2,param:""},{..}..]}
   * @param request
   * @param response
   * @param vehicleId 即imei号
   * @return 返回一个ApiMirrorTaskResponse的对象taskResponse，包含错误码code，错误信息msg，返回的数据data
   */
  @ResponseBody
  @RequestMapping(value = "/dev/{dev}/vehicle/{vehicleId}/task", method = RequestMethod.GET)
  public ApiMirrorTaskResponse getMirrorTasks(HttpServletRequest request, HttpServletResponse response,@PathVariable String vehicleId) {
    ApiMirrorTaskResponse taskResponse;
    try{
      String imei=vehicleId;
      if(StringUtil.isEmpty(imei)||imei.length()< TaskConstant.IMEI_LENGTH){
        taskResponse=new ApiMirrorTaskResponse(1,"无效的IMEI",null);
      }
      else{
      taskResponse=new ApiMirrorTaskResponse(0,"",mirrorTaskService.getXMirrorTasksByImei(imei));
      }
    }catch (Exception e){
      taskResponse=new ApiMirrorTaskResponse(-1,"操作失败",null);
      LOG.error(e.getMessage(), e);
    }
    return taskResponse;
  }

  /**
   * 汇报任务执行结果，返回taskResponse
   * taskResponse会自动转换成json格式的字符串，如下
   * result:	{code:0,msg:"",data:[{id:"",tid:2,param:""},{..}..]}
   * @param request
   * @param response
   * @param paramDTO 接收执行结果的对象
   * @param id 后视镜任务在数据库中的唯一标识id
   * @param vehicleId imei号
   * @return 返回一个ApiMirrorTaskResponse的对象taskResponse，包含错误码code，错误信息msg，返回的数据data
   */
  @ResponseBody
  @RequestMapping(value = "/dev/{dev}/vehicle/{vehicleId}/task/{id}",method = RequestMethod.PUT)
  public ApiMirrorTaskResponse reportTaskResult(HttpServletRequest request, HttpServletResponse response,
                                                @RequestBody ParamDTO paramDTO,
                                                @PathVariable String id,@PathVariable String vehicleId){
    ApiMirrorTaskResponse taskResponse=new ApiMirrorTaskResponse();
    try{
     // String param=request.getParameter("param");
      String result;
      if(paramDTO!=null){
        //paramDTO= JsonUtil.jsonToObj(param,ParamDTO.class);
        result=""+paramDTO.getResult();
      }
      else{
        result=request.getParameter("result");
      }

      if(StringUtil.isEmpty(id)||StringUtil.isEmpty(vehicleId)||StringUtil.isEmpty(result)){
        taskResponse=new ApiMirrorTaskResponse(2,"无效的请求",null);
      }
      else if(mirrorTaskService.getXMirrorTaskById(id)==null){
        taskResponse=new ApiMirrorTaskResponse(3,"找不到对应的记录",null);
      }
      else if(mirrorTaskService.updateXMirrorTaskStatus(id,vehicleId,result)){
        taskResponse=new ApiMirrorTaskResponse(0,"",null);
      }
      else{
        taskResponse=new ApiMirrorTaskResponse(-1,"操作失败",null);
      }
    }catch(Exception e){
      taskResponse=new ApiMirrorTaskResponse(-1,"操作失败",null);
      LOG.error(e.getMessage(), e);
    }
    return taskResponse;
  }

  /**
   * 动态绑定SIM卡
   * @param request
   * @param response
   * @return 返回一个ApiMirrorTaskResponse的对象taskResponse，包含错误码code，错误信息msg，返回的数据data
   */
  @ResponseBody
  @RequestMapping(value="/dev/{dev}/vehicle/{vehicleId}/sim/{iccid}")
  public ApiMirrorTaskResponse BindWithSim(HttpServletRequest request, HttpServletResponse response){
    ApiMirrorTaskResponse taskResponse=new ApiMirrorTaskResponse();
    taskResponse=new ApiMirrorTaskResponse(0,"",null);
    return taskResponse;
  }

  /**
   * log文件上传
   * log文件路径保存在XConfig表中，name为"mirrorTaskLogFilePath"
   * @param request
   * @param response
   * @param uFile 上传的文件
   * @param param 包含执行结果信息的json字符串
   * @param id 后视镜任务在数据库中的唯一标识id
   * @param vehicleId imei号
   * @return 返回一个ApiMirrorTaskResponse的对象taskResponse，包含错误码code，错误信息msg，返回的数据data
   * taskResponse会自动转换成json格式的字符串，如下
   * result:	{code:0,msg:"",data:[{id:"",tid:2,param:""},{..}..]}
   */
  @ResponseBody
  @RequestMapping(value="/dev/{dev}/vehicle/{vehicleId}/task/{id}/upload",method = RequestMethod.POST)
  public ApiMirrorTaskResponse LogUpload(HttpServletRequest request, HttpServletResponse response,
                                         @RequestParam(value="file", required=false) MultipartFile uFile,String param,
                                         @PathVariable String id,@PathVariable String vehicleId){
    ApiMirrorTaskResponse taskResponse=new ApiMirrorTaskResponse();
    ParamDTO paramDTO;
    XConfigDao xConfigDao=new XConfigDao();
    try{
//      String param=request.getParameter("param");
      if (StringUtil.isEmpty(vehicleId)||StringUtil.isEmpty(id)) {
        taskResponse=new ApiMirrorTaskResponse(2,"无效的请求",null);
        return taskResponse;
      }
      if (uFile.isEmpty()) {
        taskResponse=new ApiMirrorTaskResponse(4,"未接收到log文件",null);
        return taskResponse;
      }
      XMirrorTask task=mirrorTaskService.getXMirrorTaskById(id);
      if(task==null||!task.getImei().equals(vehicleId)){
        taskResponse=new ApiMirrorTaskResponse(3,"找不到对应的记录",null);
        return taskResponse;
      }
      //检查param是否为空
      if(!StringUtil.isEmpty(param)){
        paramDTO=JsonUtil.jsonToObj(param,ParamDTO.class);
      }else {
        paramDTO=new ParamDTO(-1,-1,null);
      }
      String result=""+paramDTO.getResult();
      //创建文件夹
      String filePath= xConfigDao.getConfig("mirrorTaskLogFilePath");
      File dFile = new File(filePath);
      if (!dFile.isDirectory()) {
        dFile.mkdir();
      }
      if(mirrorTaskService.logUpload(id,filePath,uFile)){
        if(mirrorTaskService.updateXMirrorTaskStatus(id,vehicleId,result)){
          taskResponse=new ApiMirrorTaskResponse(0,"",null);
          return taskResponse;
        }
      }
      taskResponse=new ApiMirrorTaskResponse(-1,"操作失败",null);
    }catch (Exception e){
      taskResponse=new ApiMirrorTaskResponse(-1,"操作失败",null);
      LOG.error(e.getMessage(), e);
    }

    return taskResponse;
  }

  /**
   * Version 版本号
   * @param request
   * @param response
   * @return
   */
  @ResponseBody
  @RequestMapping(value="/dev/{dev}/vehicle/{vehicleId}/version")
  public ApiMirrorTaskResponse putVersion(HttpServletRequest request, HttpServletResponse response, @RequestBody VersionDTO versionInfo){
    ApiMirrorTaskResponse taskResponse=new ApiMirrorTaskResponse();
    taskResponse=new ApiMirrorTaskResponse(0,"",null);
    LOG.info(JsonUtil.objectToJson(versionInfo));
    return taskResponse;
  }
}
