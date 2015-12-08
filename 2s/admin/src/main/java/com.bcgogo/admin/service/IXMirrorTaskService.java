package com.bcgogo.admin.service;

import com.bcgogo.admin.model.XMirrorTask;
import com.bcgogo.admin.model.XMirrorTaskDTO;
import com.bcgogo.admin.model.XMirrorTaskForDevDTO;
import com.bcgogo.admin.model.XMirrorTaskForWebDTO;
import com.bcgogo.common.Pager;
import com.bcgogo.exception.PageException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: LiTao
 * Date: 15-10-29
 * Time: 下午5:49
 * 处理后视镜任务的相关service
 */
public interface IXMirrorTaskService {
  /**
   * 通过imei号获取一页的后视镜任务列表，存入数组
   * @param imei 设备的imei号
   * @param pager 存放分页的相关参数(总数,页数,每页长度)
   * @return 后视镜任务数组
   */
  public List<XMirrorTaskForWebDTO> getXMirrorTaskListPage(String imei,Pager pager) throws PageException;
  /**
   * 通过imei号获取后视镜任务列表长度，
   * @param imei 设备的imei号
   * @return 后视镜任务数组长度
   */
  public int getXMirrorTaskListSize(String imei);

  /**
   * 获取车辆设备自定义任务(设备),即通过IMEI获取后视镜任务(XMirrorTask)列表
   *
   * @param imei 设备ID (IMEI)
   * @return 返回后视镜任务(XMirrorTask)列表
   */
  public List<XMirrorTaskForDevDTO> getXMirrorTasksByImei(String imei);

  /**
   * 获取车辆设备自定义任务(设备),即通过IMEI获取后视镜任务(XMirrorTask)列表
   *
   *
   * @param imei 设备ID (IMEI)
   * @param tid 任务类型
   * @return 返回后视镜任务(XMirrorTask)列表
   */
  public List<XMirrorTaskDTO> getXMirrorTasksByImeiAndTid(String imei, String tid);

  /**
   * 汇报自定义任务执行情况(设备)，即更改指定id的任务状态(status)
   * @param id 后视镜任务id
   * @param result 任务处理结果
   * @return 更新成功返回true 更新失败返回false
   */
  public boolean updateXMirrorTaskStatus(String id, String imei, String result);

  /**
   * 添加后视镜任务
   * @param xMirrorTaskDTO
   * @return 成功返回true 失败返回false
   */
  public boolean addXMirrorTask(XMirrorTaskDTO xMirrorTaskDTO);

  /**
   * 通过id获取后视镜任务对象
   * @param id 后视镜任务id
   * @return 返回获取到的后视镜任务对象
   */
  public XMirrorTask getXMirrorTaskById(String id);

  /**
   * 将指定id的后视镜任务log文件，上传到指定目录下
   * @param id 后视镜任务id
   * @param filePath 指定保存的目录
   * @param ufile log文件
   * @return 成功返回true，失败返回false
   */
  public boolean logUpload(String id, String filePath, MultipartFile ufile) throws IOException;
}
