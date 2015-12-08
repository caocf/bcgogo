package com.bcgogo.easyUIDemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by XinyuQiu on 14-12-10.
 */
@Controller
@RequestMapping("/easyUIDemo.do")
public class EasyUIDemoController {

  private static final Logger LOG = LoggerFactory.getLogger(EasyUIDemoController.class);
  @RequestMapping(params = "method=demo")
  public String demo() {
    return "/easyUIDemo/demo";
  }

  @RequestMapping(params = "method=demoMain")
  public String demoMain() {
    return "/easyUIDemo/demoMain";
  }
  @RequestMapping(params = "method=datagrid")
  public String datagrid() {
    return "/easyUIDemo/datagrid";
  }

  @RequestMapping(params = "method=users")
  @ResponseBody
  public Object getUsers() {
    Map<String,Object> result = new HashMap<String, Object>();
    List<Map<String,String>> list = new ArrayList<Map<String,String>>();
    for(int i=0;i<10;i++){
      Map<String,String> user1 = new HashMap<String, String>();
      user1.put("code",""+i);
      user1.put("name","name"+i);
      user1.put("addr","addr"+i);
      user1.put("col4","col4"+i);
      user1.put("firstname","firstname"+i);
      user1.put("lastname","lastname"+i);
      user1.put("phone","phone"+i);
      user1.put("email","email"+i);
      list.add(user1);
    }
    result.put("total",10);
    result.put("rows",list);
    return result;
  }
}
