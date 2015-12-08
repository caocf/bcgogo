package com.bcgogo.admin;

import com.bcgogo.admin.dao.TaskConstant;
import com.bcgogo.admin.model.XMirrorTask;
import com.bcgogo.admin.model.XMirrorTaskDTO;
import com.bcgogo.admin.model.XMirrorTaskForWebDTO;
import com.bcgogo.admin.service.IXMirrorTaskService;
import com.bcgogo.common.Pager;
import com.bcgogo.exception.PageException;
import com.bcgogo.utils.JsonUtil;
import com.bcgogo.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: LiTao
 * Date: 15-10-30
 * Time: 下午5:37
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/mirrorTask.do")
public class MirrorTaskController {
  private static final Logger LOG = LoggerFactory.getLogger(MirrorTaskController.class);
  @Autowired
  private IXMirrorTaskService mirrorTaskService;
  @RequestMapping(params = "method=toTaskList")
  public String mirrorTaskListPage(){
    int i=0;
    return "/pages/mirrorTaskList";//待修改
  }

  @RequestMapping(params ="method=toAddTask")
  public String addTaskPage(){
    return "/pages/addMirrorTask";
  }

  @RequestMapping(params = "method=addtask", method = RequestMethod.POST)
  public String addTask(HttpServletRequest request, HttpServletResponse response, XMirrorTaskDTO xMirrorTaskDTO) {
    String tatget;
    try{
      if(StringUtil.isEmpty(xMirrorTaskDTO.getImei())||StringUtil.isEmpty(xMirrorTaskDTO.getParam())){
        return "/pages/addMirrorTask";
      }
      if(xMirrorTaskDTO.getImei().length()< TaskConstant.IMEI_LENGTH){
        return "/pages/addMirrorTask";
      }
      if("1".equals(xMirrorTaskDTO.getTid())){
        if(xMirrorTaskDTO.getParam().length()!=4){
          return "/pages/addMirrorTask";
        }
      }
      if(mirrorTaskService.addXMirrorTask(xMirrorTaskDTO)){
        //tatget="/pages/success";
        tatget="/pages/mirrorTaskList"; //待修改
      }
      else{
        tatget="/pages/addMirrorTask";
      }
    }catch (Exception e){
      tatget="/pages/addMirrorTask";
      LOG.error(e.getMessage(), e);
    }
    return tatget;
  }

  @RequestMapping(params="method=mirrorTaskList")
  public void mirrorTaskList(ModelMap model, HttpServletResponse response, HttpServletRequest request,
                             Integer startPageNo, String imei) throws PageException {

    //XMirrorTaskForWebDTO webDTO = new XMirrorTaskForWebDTO();

    int total=mirrorTaskService.getXMirrorTaskListSize(imei);
    Pager pager;
    if(startPageNo==null){
      pager=new Pager(total,1,10);
    }
    else {
      pager=new Pager(total,startPageNo.intValue(),10);
    }
    List<XMirrorTaskForWebDTO> dtoList=mirrorTaskService.getXMirrorTaskListPage(imei,pager);

    String jsonStr = "";
    //JSON
    jsonStr = JsonUtil.listToJson(dtoList);
    jsonStr = jsonStr.substring(0, jsonStr.length() - 1);
    if (!"[".equals(jsonStr.trim())) {
      jsonStr = jsonStr + "," + pager.toJson().substring(1, pager.toJson().length());
    }
    else {
      jsonStr = pager.toJson();
    }
    try{
      PrintWriter writer = response.getWriter();
      writer.write(jsonStr);
      writer.close();
     // mirrorTaskService
    }catch (Exception e){
      LOG.error(e.getMessage(), e);
    }
    model.addAttribute("dtoList", dtoList);
  }

}
