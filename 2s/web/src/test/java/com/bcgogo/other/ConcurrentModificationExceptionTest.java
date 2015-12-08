package com.bcgogo.other;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-6-25
 * Time: 上午9:05
 * 上述代码中有①②③三处注释，
 * 情况1：下面我们让②③都注释上，代码运行后不出错
 * 情况2：注释掉①，②③不注释，代码运行后也不出错
 * 情况3：①②③不注释，运行代码后出错，比较一下就知道了区别了，这种情况中，map被中有2个value为空的数据，所以map会被remove2次。
 * 原因：对map进行修改操作是，下一次的修改会被认为是对原对象的修改，而其实被修改的对象已经不是原对象了，所以会造成当前修改异常java.util.ConcurrentModificationException。
 */
public class ConcurrentModificationExceptionTest {
  public static void main(String[] args) {
    Map<Integer, String> map = new HashMap<Integer, String>();
    map.put(1, "a"); //①
    map.put(2, "b");  //②
    map.put(3, "a"); //③
    ConcurrentModificationExceptionTest md = new ConcurrentModificationExceptionTest();
    md.clearMap(map);
    for (Map.Entry<Integer, String> entry : map.entrySet()) {
      System.out.println(entry.getKey() + "==>" + entry.getValue());
    }
  }

  private void clearMap(Map<Integer, String> map) {
    for (Map.Entry<Integer, String> entry : map.entrySet()) {
      if (entry.getValue().equals("a")) {
        map.remove(entry.getKey());
      }
    }
  }

}
