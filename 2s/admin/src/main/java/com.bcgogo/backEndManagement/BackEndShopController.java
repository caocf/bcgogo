package com.bcgogo.backEndManagement;

import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.model.Business;
import com.bcgogo.config.model.Shop;
import com.bcgogo.config.model.ShopBusiness;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IShopService;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.model.TemBlance;
import com.bcgogo.txn.service.ITemBalance;
import com.bcgogo.user.dto.UserDTO;
import com.bcgogo.user.service.IUserService;
import com.bcgogo.util.ShopRequest;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: MZDong
 * Date: 11-11-25
 * Time: 下午9:24
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/beshop.do")
public class BackEndShopController implements Serializable {
  private static final Logger LOG = LoggerFactory.getLogger(BackEndShopController.class);
  private static final long serialVersionUID = 1L;

  @RequestMapping(params = "method=shoplist")
  public String shopList(ModelMap model) {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    List<Shop> shoplist = configService.getShop();

    model.addAttribute("shoplist", shoplist);

    return "/backEndManagement/shoplist";
  }

  //已注册
  @RequestMapping(params = "method=shoplist1")
  public String shopList1(ModelMap model, HttpServletRequest request) {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    int pageNo = 1;
    int pageSize = 25;
    String pageNoStr = request.getParameter("pageNo");

    if (pageNoStr != null) {
      pageNo = Integer.parseInt(pageNoStr);
    }
    List<Shop> shoplist = configService.getShopByState(pageNo - 1, pageSize);
    int count = configService.countShopByState();
    int pageCount = count % pageSize == 0 ? (count / pageSize) : (count / pageSize + 1);

    request.setAttribute("pageCount", pageCount);
    request.setAttribute("pageNo", pageNo);
    model.addAttribute("shoplist", shoplist);

    return "/backEndManagement/shoplist1";
  }

  //短信充值
  @RequestMapping(params = "method=smsRecharge")
  public String smsRecharge(ModelMap model, HttpServletRequest request) {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    int pageNo = 1;
    int pageSize = 25;
    String pageNoStr = request.getParameter("pageNo");

    if (pageNoStr != null) {
      pageNo = Integer.parseInt(pageNoStr);
    }
    List<Shop> shoplist = configService.getShopByState(pageNo - 1, pageSize);
    int count = configService.countShopByState();
    int pageCount = count % pageSize == 0 ? (count / pageSize) : (count / pageSize + 1);

    request.setAttribute("pageCount", pageCount);
    request.setAttribute("pageNo", pageNo);
    model.addAttribute("shoplist", shoplist);

    return "/backEndManagement/smsRecharge";
  }

  //待注册
  @RequestMapping(params = "method=shoplist2")
  public String shopList2(ModelMap model, HttpServletRequest request) {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    int pageNo = 1;
    int pageSize = 25;
    String pageNoStr = request.getParameter("pageNo");

    if (pageNoStr != null) {
      pageNo = Integer.parseInt(pageNoStr);
    }
    List<Shop> shoplist = configService.getShopByState1(pageNo - 1, pageSize);
    int count = configService.countShopByState1();
    int pageCount = count % pageSize == 0 ? (count / pageSize) : (count / pageSize + 1);
    request.setAttribute("count", count);
    request.setAttribute("pageCount", pageCount);
    request.setAttribute("pageNo", pageNo);
    model.addAttribute("shoplist", shoplist);

    return "/backEndManagement/shoplist2";
  }

  @RequestMapping(params = "method=getSms")
  public String getSms(HttpServletRequest request) {
    ITemBalance iTemBalance = ServiceManager.getService(ITemBalance.class);

    int pageNo = 1;
    int pageSize = 6;
    String pageNoStr = request.getParameter("pageNo");
    if (pageNoStr != null) {
      pageNo = Integer.parseInt(pageNoStr);
    }
    List<TemBlance> smsAndShopDTOList = iTemBalance.getSmsByShopId(pageNo - 1, pageSize);
    int count = iTemBalance.coutSms();
    int pageCount = count % pageSize == 0 ? (count / pageSize) : (count / pageSize + 1);
    request.setAttribute("count", count);
    request.setAttribute("pageCount", pageCount);
    request.setAttribute("smsAndShopDTOList", smsAndShopDTOList);
    request.setAttribute("pageNo", pageNo);
    return "/backEndManagement/backRecharge";
  }

  @RequestMapping(params = "method=deleteBlance")
  public void deleteBlance(HttpServletRequest request, HttpServletResponse response) {

    ITemBalance iTemBalance = ServiceManager.getService(ITemBalance.class);
    Long id = Long.parseLong(request.getParameter("id"));
    iTemBalance.deleteBlance(id);
    String s = "success";
    try {
      PrintWriter writer = response.getWriter();
      writer.write(s);
      writer.close();
    } catch (Exception e) {
      LOG.info(e.getMessage());
    }
  }


  @RequestMapping(params = "method=shopaudit")
  public String shopAudit(ModelMap model, HttpServletRequest request) {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    List<Business> businessDTOList = configService.getBusinessList("2");
    model.addAttribute("businessDTOList2", businessDTOList);
    model.addAttribute("businessDTOList10", configService.getBusinessList("10"));
    model.addAttribute("businessDTOList34", configService.getBusinessList("34"));
    String shopId = request.getParameter("shopId");
    ShopDTO shopDTO = configService.getShopById(Long.parseLong(shopId));
//    model.addAttribute("shopDTO", shopDTO);
    request.setAttribute("shopDTO", shopDTO);
    List<ShopBusiness> shopBusinessList = configService.getShopBusinessList(Long.parseLong(request.getParameter("shopId")));
    if (shopBusinessList != null && shopBusinessList.size() > 0) {
      String value = "";
      for (ShopBusiness shopBusiness : shopBusinessList) {
        if (shopBusiness.getBusinessId() == 33L || shopBusiness.getBusinessId() == 35L) {
          value += "," + shopBusiness.getBusinessId() + shopBusiness.getMemo();
        } else {
          value += "," + shopBusiness.getBusinessId();
        }
      }
      if (!value.isEmpty()) {
        model.addAttribute("value", value.substring(1));
      }
    }
    return "/backEndManagement/shopaudit";
  }


//  @ResponseBody
  @RequestMapping(params = "method=getShopPhoto")
  public void getPhotoByShopId(String shopId, final HttpServletResponse response, HttpServletRequest request) throws IOException, SQLException {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    if("".equals(shopId) || shopId==null){
      shopId = request.getParameter("shopId");
      if(shopId==null ||"".equals(shopId)){
//        return;
      }
    }
    ShopDTO shopDTO = configService.getShopById(Long.parseLong(shopId));
    byte[] data = shopDTO.getAttachment();
//    return data;
    if (data != null) {
      response.setContentType("image/jpg");
      response.setCharacterEncoding("UTF-8");
      OutputStream outputSream = response.getOutputStream();
      InputStream in = new ByteArrayInputStream(data);
      int len = 0;
      byte[] buf = new byte[1024];
      while ((len = in.read(buf, 0, 1024)) != -1) {
        outputSream.write(buf, 0, len);
      }
      outputSream.close();
    }
//    return null;
  }


  @Deprecated
  //修改店铺图片
  @RequestMapping(params = "method=updateShopPhoto")
  public String updateShopPhoto(ModelMap model, HttpServletRequest request) {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
    String shopIdStr = request.getParameter("shopId");
    request.setAttribute("shopId", shopIdStr);
    try {
      if (StringUtils.isNotBlank(shopIdStr)) {
        Long shopId = Long.parseLong(shopIdStr);
        ShopDTO shopDTO = configService.getShopById(shopId);
        MultipartFile multipartFile = multipartRequest.getFile("input_fileLoad");
        InputStream is = multipartFile.getInputStream();
        byte[] studentPhotoData = configService.InputStreamToByte(is);
        String multipartFileToString = multipartFile.getOriginalFilename();
        shopDTO.setPhoto(multipartFileToString);
        shopDTO.setAttachment(studentPhotoData);
        ServiceManager.getService(IShopService.class).updateShop(shopDTO);
      }
    } catch (BcgogoException e) {
      LOG.error("Shop 信息更新失败");
      LOG.error(e.getMessage(),e);
    } catch (Exception ex) {
      LOG.error("request to shopDTO error");
    }

    return shopAudit(model, request);
  }


  @Deprecated
  //更新店铺信息
  @RequestMapping(params = "method=updateShop")
  public String updateShop(ModelMap model, HttpServletRequest request) {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    IUserService userService = ServiceManager.getService(IUserService.class);
    String shopIdStr = request.getParameter("shopId");
    ShopRequest shopRequest = new ShopRequest();
    try {
      if (shopIdStr != null && !"".equals(shopIdStr)) {
        Long shopId = Long.parseLong(shopIdStr);
        ShopDTO shopDTO = configService.getShopById(shopId);
        if (shopDTO != null) {
          String oldShopManagerMobile = shopDTO.getStoreManagerMobile();
          shopDTO = shopRequest.shopDTORequest(request, shopDTO);  //update DTO
          ServiceManager.getService(IShopService.class).updateShop(shopDTO);

          //user 存在时更新user
          List<UserDTO> userDTOList = userService.getUserByShopIDAndMobile(shopId, oldShopManagerMobile);
          if (userDTOList != null && userDTOList.size() > 0) {
            UserDTO userDTO = userDTOList.get(0);
            userDTO.setEmail(shopDTO.getEmail());
            userDTO.setQq(shopDTO.getQq());
            userDTO.setName(shopDTO.getStoreManager());
            userDTO.setUserName(shopDTO.getLegalRep());
            userDTO.setMobile(shopDTO.getStoreManagerMobile());
            userService.updateUser(userDTO);
          }
        }
      }
    } catch (BcgogoException e) {
      LOG.error("Shop 信息更新失败");
      LOG.error(e.getMessage(),e);
    } catch (Exception ex) {
      LOG.error("request to shopDTO error");
    }

    return shopAudit(model, request);
  }

}