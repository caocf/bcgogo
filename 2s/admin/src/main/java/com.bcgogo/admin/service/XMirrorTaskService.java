package com.bcgogo.admin.service;

import com.bcgogo.admin.dao.TaskConstant;
import com.bcgogo.admin.dao.XMirrorTaskDAO;
import com.bcgogo.admin.model.XMirrorTask;
import com.bcgogo.admin.model.XMirrorTaskDTO;
import com.bcgogo.admin.model.XMirrorTaskForDevDTO;
import com.bcgogo.admin.model.XMirrorTaskForWebDTO;
import com.bcgogo.common.Pager;
import com.bcgogo.exception.PageException;
import com.bcgogo.utils.ObjectUtil;
import com.bcgogo.utils.StringUtil;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: LiTao
 * Date: 15-10-29
 * Time: 下午7:13
 * 处理后视镜任务的相关service实现类
 */
@Component
public class XMirrorTaskService implements IXMirrorTaskService {
  @Autowired
  private XMirrorTaskDAO xMirrorTaskDAO;

  @Override
  public List<XMirrorTaskForWebDTO> getXMirrorTaskListPage(String imei,Pager pager) throws PageException {
    if(StringUtil.isEmpty(imei)){
      imei="";
    }
    if(pager==null){
      pager=new Pager(1);
    }
    List<XMirrorTask> taskList=xMirrorTaskDAO.getXMirrorTaskListPage(imei,pager);
    List<XMirrorTaskForWebDTO> dtoList=new ArrayList<XMirrorTaskForWebDTO>();
    for(XMirrorTask task:taskList){
      dtoList.add(task.toWebDTO());
    }
    return dtoList;
  }

  @Override
  public int getXMirrorTaskListSize(String imei){
    return xMirrorTaskDAO.getXMirrorTaskListSize(imei);
  }

  @Override
  public List<XMirrorTaskForDevDTO> getXMirrorTasksByImei(String imei) {
    List<XMirrorTask> taskList=xMirrorTaskDAO.getXMirrorTaskListByImei(imei);
    List<XMirrorTaskForDevDTO> taskDTOList=new ArrayList<XMirrorTaskForDevDTO>();
    for(XMirrorTask task:taskList){
      taskDTOList.add(task.toDevDTO());
    }
    return taskDTOList;
  }

  @Override
  public List<XMirrorTaskDTO> getXMirrorTasksByImeiAndTid(String imei, String tid) {
    List<XMirrorTask> taskList=xMirrorTaskDAO.getXMirrorTasksByImeiAndTid(imei,tid);
    List<XMirrorTaskDTO> taskDTOList=new ArrayList<XMirrorTaskDTO>();
    for(XMirrorTask task:taskList){
      taskDTOList.add(task.toDTO());
    }
    return taskDTOList;
  }

  @Override
  public boolean updateXMirrorTaskStatus(String id,String imei, String result) {
    XMirrorTask task=xMirrorTaskDAO.getById(XMirrorTask.class,id);
    if(task==null){
      return false ;
    }
    if(!task.getImei().equals(imei)){
      return false ;
    }
    if(TaskConstant.STATUS_SUCCESS.equals(task.getStatus())) {
      return true;
    }
    String status= TaskConstant.STATUS_UNTREATED;
    if(TaskConstant.RESULT_SUCCESS.equals(result)){
      status=TaskConstant.STATUS_SUCCESS;
    }
    else if(TaskConstant.RESULT_FAILED.equals(result)) {
      status=TaskConstant.STATUS_FAILED;
    }
    return xMirrorTaskDAO.updateXMirrorTaskStatus(id,status);
  }

  @Override
  public boolean addXMirrorTask(XMirrorTaskDTO xMirrorTaskDTO) {
    XMirrorTask task;
    if(xMirrorTaskDTO==null){
      return false;
    }
    task=new XMirrorTask();
    task.fromDTO(xMirrorTaskDTO);
    List<XMirrorTask> taskList=xMirrorTaskDAO.getXMirrorTasksByImeiAndTid(task.getImei(),task.getTid());
    if(!taskList.isEmpty()&&TaskConstant.STATUS_UNTREATED.equals(taskList.get(0).getStatus())){
      if(TaskConstant.TID_UPLOAD_LOG.equals(task.getTid())){
        return true;
      }
      if(TaskConstant.TID_CALIBRATE_OBD.equals(task.getTid())){
        String id=taskList.get(0).get_id().get$oid();
        xMirrorTaskDAO.updateXMirrorTaskParam(id,task.getParam());
        return true;
      }
    }

    xMirrorTaskDAO.addXMirrorTask(task);
    return true;
  }

  @Override
  public XMirrorTask getXMirrorTaskById(String id) {
    if(!ObjectId.isValid(id)){
      return null;
    }
    return xMirrorTaskDAO.getById(XMirrorTask.class,id);
  }

  @Override
  public boolean logUpload(String id, String filePath, MultipartFile ufile) throws IOException {
    XMirrorTask task=getXMirrorTaskById(id);
    OutputStream writer;
    Long currentTimeMillis=System.currentTimeMillis();
    if(task==null){
      return false ;
    }
    String filenName=task.getImei()+"_"+task.getParam()+"_"+currentTimeMillis+".log";
    String file=filePath+"/"+filenName;
    File log=new File(file);
    //文件不存在，创建文件
    if (!log.exists()) {
      log.createNewFile();
    }
    //写入文件
    writer=new FileOutputStream(log);
    writer.write(ufile.getBytes());
    xMirrorTaskDAO.updateXMirrorTaskFilePath(id,file);
    return true;
  }
}
