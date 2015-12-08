package com.bcgogo.cache;

import com.bcgogo.utils.StringUtil;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-1-3
 * Time: 上午3:49
 * To change this template use File | Settings | File Templates.
 */
public class UserDataLinkedStack<T> {

  //  缓存用户信息的数量
  public static final int USER_NUM_LIMIT=1000;
  private  Node topNode;  //引用链表中的第一个节点
  private Node lastNode;

  public UserDataLinkedStack(){
    clear();
  }


  private Node getTopNode() {
    return topNode;
  }

  private void setTopNode(Node topNode) {
    this.topNode = topNode;
  }

  private  boolean isEmpty(){
    return  topNode == null;
  }

  private  void clear(){
    topNode = null;
  }

  public T get(String key){
    Node point = getTopNode();
    while(point != null){
      if(point.key!=null&&point.key.equals(key)){
        return point.data;
      }
      point = point.next;
    }
    return null;
  }

  /**
   * 此方法同push的区别在于限制stack的长度
   * @param key
   * @param newEntry
   */
  public synchronized void put(String key,T newEntry){
    if(!push(key,newEntry)){
      return;
    }
    //控制stack的长度
    int count=0;
    Node point = getTopNode();
    while(point != null){
      count++;
      point = point.next;
    }
    if(count>USER_NUM_LIMIT){
       point=null;
    }
  }

  public boolean push(String key,T  newEntry){
    if(StringUtil.isEmpty(key)||newEntry==null){
      return false;
    }
    Node  newNode = new Node(newEntry);
    newNode.key=key;
    newNode.next = topNode;
    topNode =   newNode;
    return true;
  }

  private  T pop(){
    T top = null;
    if(!isEmpty()){
      top = topNode.getData();
      topNode = topNode.getNextNode();
    }
    return  top;
  }

  public void display(){
    Node point = topNode;
    System.out.println("从顶端元素开始:");
    while(point != null){
      System.out.println(point.getData());
      point = point.next;
    }
  }

  private  class  Node{
    private String key;
    private T data;   //栈的元素
    private Node next; //指向下一个节点的连接

    private  Node(T data){
      this.data = data;
      next = null;
    }

    public String getKey() {
      return key;
    }

    public void setKey(String key) {
      this.key = key;
    }

    private  T getData(){
      return  data;
    }

    private Node getNextNode(){
      return next;
    }
  }

}
