package com.bcgogo.backEndManagement;

import com.bcgogo.AbstractTest;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IShopService;
import com.bcgogo.service.ServiceManager;
import junit.framework.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.ui.ModelMap;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by IntelliJ IDEA.
 * User: 张传龙
 * Date: 12-4-7
 * Time: 上午11:42
 * To change this template use File | Settings | File Templates.
 */
public class BackEndShopControllerTest extends AbstractTest {

  private BackEndShopController backEndShopControll = new BackEndShopController();

  @Test
  public void testSaveShop() throws Exception {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    ShopDTO shopDTO = new ShopDTO();
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
    shopDTO = configService.getShopById(10022020202562l);

    ModelMap model = new ModelMap();
    MockMultipartHttpServletRequest request = new MockMultipartHttpServletRequest();
    //获得ServletContext路径
    String serverPath = request.getSession().getServletContext().getRealPath("/") + "file\\";
    File fileDir = new File(serverPath);
    if (!fileDir.exists()) {
      if (!fileDir.mkdir())
        throw new Exception("目录不存在，创建失败！");
    }
    /*查找文件，如果不存在，就创建*/
    File file = new File(serverPath + "/temp.jpg");
    if (!file.exists()) {
      if (!file.createNewFile())
        throw new Exception("文件不存在，创建失败！");
    }
    final FileInputStream fis = new FileInputStream(file.getAbsolutePath());
    MockMultipartFile multipartFile = new MockMultipartFile("input_fileLoad", "temp.jpg", "image/jpg", fis);

    request.addFile(multipartFile);
    request.setMethod("POST");
    request.setContentType("multipart/form-data");
    request.addHeader("Content-type", "multipart/form-data");
    request.setParameter("shopId", shopDTO.getId() + "");

    String url = backEndShopControll.updateShopPhoto(model, request);
    Assert.assertEquals("/backEndManagement/shopaudit", url);
  }
}
