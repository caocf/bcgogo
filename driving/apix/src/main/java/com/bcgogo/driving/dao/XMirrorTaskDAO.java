package com.bcgogo.driving.dao;

import com.bcgogo.driving.model.XAppUserLoginInfo;
import com.bcgogo.driving.model.XMirrorTask;
import com.bcgogo.driving.model.mongodb.MongoFactory;
import com.bcgogo.pojox.util.JsonUtil;
import com.bcgogo.pojox.util.StringUtil;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: LiTao
 * Date: 15-10-29
 * Time: 上午11:34
 * 后视镜任务DAO
 */
@Component
public class XMirrorTaskDAO extends XBaseDao<XMirrorTask> {
  /**
   * 通过imei号获取status为非 X_MIRROR_TASK_SUCCESS 状态的后视镜任务列表，存入数组
   * @param imei 设备的imei号
   * @return 后视镜任务数组
   */
  public List<XMirrorTask> getXMirrorTaskListByImei(String imei){
    Document filter = new Document("imei",imei);
    filter.append("status",TaskConstant.STATUS_UNTREATED);  //只获取尚未处理的
    MongoCursor<Document> cursor = MongoFactory.instance().getCollection(DocConstant.DOCUMENT_X_MIRROR_TASK).find(filter).iterator();
    List<XMirrorTask> taskList = new ArrayList<XMirrorTask>();
    while(cursor.hasNext()){
      XMirrorTask xMirrorTask=JsonUtil.jsonToObj(cursor.next().toJson(),XMirrorTask.class);
      taskList.add(xMirrorTask);
    }
    return taskList;
  }

  /**
   * 通过IMEI和Tid获取后视镜任务(XMirrorTask)列表
   *
   * @param imei 设备ID (IMEI)
   * @param tid 任务类型
   * @return 返回后视镜任务(XMirrorTask)列表
   */
  public List<XMirrorTask> getXMirrorTasksByImeiAndTid(String imei,String tid){
    Document filter = new Document("imei",imei);
    filter.append("status",TaskConstant.STATUS_UNTREATED);  //只获取尚未处理的
    filter.append("tid",tid);  //任务类型
    MongoCursor<Document> cursor = MongoFactory.instance().getCollection(DocConstant.DOCUMENT_X_MIRROR_TASK).find(filter).iterator();
    List<XMirrorTask> taskList = new ArrayList<XMirrorTask>();
    while(cursor.hasNext()){
      XMirrorTask xMirrorTask=JsonUtil.jsonToObj(cursor.next().toJson(),XMirrorTask.class);
      taskList.add(xMirrorTask);
    }
    return taskList;
  }

  /**
   * 将一个 xMirrorTask 存入数据库
   * @param xMirrorTask 一条后视镜任务
   * @return
   */
  public void addXMirrorTask(XMirrorTask xMirrorTask){
    if(xMirrorTask.getStatus()==null||"".equals(xMirrorTask.getStatus())){
      xMirrorTask.setStatus(TaskConstant.STATUS_UNTREATED); //还未处理(值为"0")
    }
    if(StringUtil.isEmpty(xMirrorTask.getTid())){
      xMirrorTask.setTid(TaskConstant.TID_UNKNOWN_TYPE); //若没有指定任务类型，则设置为未知类型(值为"0")；
    }
    if(StringUtil.isEmpty(xMirrorTask.getFilePath())){
      xMirrorTask.setFilePath("");
    }
    this.save(xMirrorTask);
//    return false;
  }

  /**
   * 根据id更新一条后视镜任务的状态
   * @param id 需要更新的任务id
   * @param status 更新为此状态
   * @return 成功返回true，失败返回false
   */
  public boolean updateXMirrorTaskStatus(String id,String status){
    Document filter=new Document("_id", new ObjectId(id));
    Document update=new Document("status",status);
    UpdateResult updateResult=MongoFactory.instance().getCollection(DocConstant.DOCUMENT_X_MIRROR_TASK).updateOne(filter, new Document("$set", update));
    if (updateResult.wasAcknowledged()){
      return true;
    }
    return false;
  }

  /**
   * 根据id更新一条后视镜任务
   * @param id 需要更新的任务id
   * @param param 更新为此param
   * @return 成功返回true，失败返回false
   */
  public boolean updateXMirrorTaskParam(String id,String param){
    Document filter=new Document("_id", new ObjectId(id));
    Document update=new Document("param",param);
    UpdateResult updateResult=MongoFactory.instance().getCollection(DocConstant.DOCUMENT_X_MIRROR_TASK).updateOne(filter, new Document("$set", update));
    if (updateResult.wasAcknowledged()){
      return true;
    }
    return false;
  }

  /**
   * 根据id更新一条后视镜任务的filePath
   * @param id 需要更新的任务id
   * @param filePath 更新为此filePath
   * @return 成功返回true，失败返回false
   */
  public boolean updateXMirrorTaskFilePath(String id,String filePath){
    Document filter=new Document("_id", new ObjectId(id));
    Document update=new Document("filePath",filePath);
    UpdateResult updateResult=MongoFactory.instance().getCollection(DocConstant.DOCUMENT_X_MIRROR_TASK).updateOne(filter, new Document("$set", update));
    if (updateResult.wasAcknowledged()){
      return true;
    }
    return false;
  }

  public static void main(String[]args){
    XMirrorTask xMirrorTask=new XMirrorTask();
    xMirrorTask.setTid("1");
    xMirrorTask.setParam("0010");
    xMirrorTask.setImei("123456789abcdef");
    XMirrorTaskDAO dao=new XMirrorTaskDAO();
    dao.addXMirrorTask(xMirrorTask);
    List<XMirrorTask> list=dao.getXMirrorTaskListByImei("123456789abcdef");
    System.out.println(list.size());
    System.out.println(list.get(0).toString());
    dao.updateXMirrorTaskStatus("5631e76bfa232e12c4767a1f","1");
    System.out.println(list.get(0).toString());
  }
}