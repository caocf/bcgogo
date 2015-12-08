package com.bcgogo.admin.model;

/**
 * Created by IntelliJ IDEA.
 * User: LiTao
 * Date: 15-10-30
 * Time: 上午11:19
 * To change this template use File | Settings | File Templates.
 */
public class XMirrorTaskForDevDTO {
  private String id;         //id
  private int tid;    //任务类型
  private String param;     //参数

  public XMirrorTaskForDevDTO() {
    super();
  }

  public XMirrorTaskForDevDTO(String id, int tid, String param) {
    this.id = id;
    this.tid = tid;
    this.param = param;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public int getTid() {
    return tid;
  }

  public void setTid(int tid) {
    this.tid = tid;
  }

  public String getParam() {
    return param;
  }

  public void setParam(String param) {
    this.param = param;
  }
}
