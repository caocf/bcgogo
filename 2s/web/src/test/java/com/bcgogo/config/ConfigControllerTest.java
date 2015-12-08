package com.bcgogo.config;

import com.bcgogo.AbstractTest;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.ConfigService;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IShopService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.UserDTO;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.utils.JsonUtil;
import com.bcgogo.utils.ShopConstant;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.ui.ModelMap;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: zhangchuanlong
 * Date: 12-3-30
 * Time: 下午2:25
 * To change this template use File | Settings | File Templates.
 */
public class ConfigControllerTest extends AbstractTest {

  @Before
  public void setUp() throws Exception {
    configController = new ConfigController();
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
  }
  //      店面管理员重名单元测试
  @Test
  public void  testCheckStoreManager() throws Exception
  {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    ShopDTO shopDTO=new ShopDTO();
    shopDTO.setId(10022020202562l);
    shopDTO.setAccount("6224021");
    shopDTO.setAddress("安徽省");
    shopDTO.setAgent("张三");
    shopDTO.setAgentId("120202020");
    shopDTO.setBank("建设银行");
    shopDTO.setEmail("www.224422@qq.com");
    shopDTO.setBusinessScope("个体经营");
    shopDTO.setContact("张三");
    shopDTO.setFax("0512-125565656");
    shopDTO.setName("统购车业");
    shopDTO.setStoreManager("张传龙");
    ServiceManager.getService(IShopService.class).createShop(shopDTO);
    shopDTO=configService.getStoreManager("张传龙") ;
    Assert.assertEquals("张传龙", shopDTO.getStoreManager());
  }

  @Test
  public  void testSaveShop() throws  Exception
  {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    ModelMap model=new ModelMap();
    ShopDTO shopDTO=new ShopDTO();
    MockMultipartHttpServletRequest request = new MockMultipartHttpServletRequest() ;
    //获得ServletContext路径
    String serverPath = request.getSession().getServletContext().getRealPath("/") + "file\\";
    File fileDir = new File(serverPath);
    if(!fileDir.exists()){
      if(!fileDir.mkdir())
        throw new Exception("目录不存在，创建失败！");
    }
    /*查找文件，如果不存在，就创建*/
    File file = new File(serverPath+"/temp.jpg");
    if(!file.exists())
    {
      if(!file.createNewFile())
        throw new Exception("文件不存在，创建失败！");
    }
    final FileInputStream fis = new FileInputStream(file.getAbsolutePath());
    MockMultipartFile multipartFile = new MockMultipartFile("input_fileLoad","temp.jpg","image/jpg",fis);
    request.addFile(multipartFile);
    request.setMethod("POST");
    request.setContentType("multipart/form-data");
    request.addHeader("Content-type", "multipart/form-data");
    shopDTO.setId(123546565659l);
    shopDTO.setAccount("6224021");
    shopDTO.setAddress("安徽省");
    shopDTO.setAgent("张三");
    shopDTO.setAgentId("12020202569");
    shopDTO.setBank("建设银行");
    shopDTO.setEmail("www.224422@qq.com");
    shopDTO.setBusinessScope("个体经营");
    shopDTO.setContact("张三");
    shopDTO.setFax("0512-125565656");
    shopDTO.setName("统购车业1");
    shopDTO.setStoreManager("张传龙");

    request.getSession().setAttribute("regShop",shopDTO);
    request.setParameter("operationMode","个体经营");
    request.setParameter("businessHours","12:00~24:00");
    request.setParameter("established","2012-02-25");
    request.setParameter("qualification","修车，洗车");
    request.setParameter("personnel","10");
    request.setParameter("area","江苏省苏州市相城区");
    request.setParameter("businessScope","个体经营");
    request.setParameter("relatedBusiness","个体经营");
    request.setParameter("feature","洗车");
    request.setParameter("memo","修车");
    configController.SaveShop(model,request,new MockHttpServletResponse());
    String jsonStr = (String) model.get("jsonStr");
    Map<String,String> jsonMap = new HashMap();
    jsonMap.put(configController.result,configController.registerSuccess);
    String resultStr = JsonUtil.mapToJson(jsonMap);
    assertEquals(jsonStr,resultStr);
  }

  //无文件上传的时候
  @Test
  public  void testSaveShop2() throws  Exception
  {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    ModelMap model=new ModelMap();
    ShopDTO shopDTO=new ShopDTO();

    request.setMethod("POST");
    request.setContentType("multipart/form-data");
    request.addHeader("Content-type", "multipart/form-data");
    shopDTO.setId(123546565659l);
    shopDTO.setAccount("6224021");
    shopDTO.setAddress("安徽省");
    shopDTO.setAgent("李四");
    shopDTO.setAgentId("12020202569");
    shopDTO.setBank("建设银行");
    shopDTO.setEmail("www.224422@qq.com");
    shopDTO.setBusinessScope("个体经营");
    shopDTO.setContact("张三");
    shopDTO.setFax("0512-125565656");
    shopDTO.setName("统购车业1");
    shopDTO.setStoreManager("张传龙");

    request.getSession().setAttribute("regShop",shopDTO);
    request.setParameter("operationMode","个体经营");
    request.setParameter("businessHours","12:00~24:00");
    request.setParameter("established","2012-02-25");
    request.setParameter("qualification","修车，洗车");
    request.setParameter("personnel","10");
    request.setParameter("area","江苏省苏州市相城区");
    request.setParameter("businessScope","个体经营");
    request.setParameter("relatedBusiness","个体经营");
    request.setParameter("feature","洗车");
    request.setParameter("memo","修车");
    configController.SaveShop(model,request,response);
    String jsonStr = (String) model.get("jsonStr");
    Map<String,String> jsonMap = new HashMap();
    jsonMap.put(configController.result,configController.registerSuccess);
    String resultStr = JsonUtil.mapToJson(jsonMap);
    assertEquals(jsonStr,resultStr);
  }
  @Test
  public void checkStoreManagerMobile()  throws Exception
  {
    IUserService userService= ServiceManager.getService(IUserService.class);
    ModelMap model=new ModelMap();
    UserDTO userDTO = new UserDTO();
    userDTO.setName("123");
    userDTO.setPassword("1234534545");
    userDTO.setEmail("aaa@ssdsd.com");
    userDTO.setLastTime(5L);
    userDTO.setLoginTimes(3L);
    userDTO.setMemo("aaaaaaaaaaaa");
    userDTO.setUserName("zhuyj");
    userDTO.setUserNo("3");
    userDTO.setQq("1245453434");
    userDTO.setMobile("434343342123");
    userDTO.setShopId(1L);
    userService.createUser(userDTO);
    request.setParameter("mobile","434343342123");
    configController.checkStoreManagerMobile(model,request,response);
    String jsonStr = (String) model.get("jsonStr");
    assertEquals(jsonStr,"\"true\"");
  }

  @Test
  public void checkStoreManagerMobile2() throws Exception {
    ModelMap model = new ModelMap();
    initUser();
    request.setParameter("mobile", "15851654173");
    configController.checkStoreManagerMobile(model, request, response);
    String jsonStr = (String) model.get("jsonStr");
    assertEquals(jsonStr, "\"true\"");
  }

}
