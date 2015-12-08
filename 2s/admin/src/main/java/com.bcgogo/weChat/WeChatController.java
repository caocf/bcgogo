package com.bcgogo.weChat;

import com.bcgogo.common.ListResult;
import com.bcgogo.common.Pager;
import com.bcgogo.common.PagingListResult;
import com.bcgogo.common.Result;
import com.bcgogo.config.cache.AreaCacheManager;
import com.bcgogo.config.dto.AreaDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.dto.image.DataImageRelationDTO;
import com.bcgogo.config.dto.image.ImageInfoDTO;
import com.bcgogo.config.service.ConfigService;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IRecommendShopService;
import com.bcgogo.config.service.image.ImageService;
import com.bcgogo.config.upyun.UpYunManager;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.enums.DeletedType;
import com.bcgogo.enums.config.DataType;
import com.bcgogo.enums.config.ImageType;
import com.bcgogo.enums.sms.SmsSendScene;
import com.bcgogo.exception.PageException;
import com.bcgogo.notification.model.WXMsg;
import com.bcgogo.notification.service.IWXService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.model.wx.WXArticleTemplate;
import com.bcgogo.user.model.wx.WXShopAccount;
import com.bcgogo.user.model.wx.WXUserVehicle;
import com.bcgogo.user.service.wx.*;
import com.bcgogo.user.service.wx.impl.WXAccountService;
import com.bcgogo.user.service.wx.impl.WXArticleService;
import com.bcgogo.util.WebUtil;
import com.bcgogo.utils.*;
import com.bcgogo.wx.*;
import com.bcgogo.wx.qr.WXQRCodeDTO;
import com.bcgogo.wx.qr.WXQRCodeSearchCondition;
import com.bcgogo.wx.user.*;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: zhangjie
 * Date: 14-09-23
 * Time:
 */
@Controller
@RequestMapping("/weChat.do")
public class WeChatController {
  private static final Logger LOG = LoggerFactory.getLogger(WeChatController.class);
  @Autowired
  private WXArticleService wxArticleService;
  @Autowired
  private IConfigService configService;
  @Autowired
  private IWXUserService wxUserService;
  @Autowired
  private IWXService wxService;
  @Autowired
  private IWXAccountService accountService;

  //跳转到微信管理主界面
  @RequestMapping(params = "method=initWeChatPage")
  public String initWeChatPage() {
    return "/weChat/weChatList";
  }

  //跳转待审核列表主界面
  @RequestMapping(params = "method=toAdultList")
  public String initAdultPage() {
    return "/weChat/weChatAdultList";
  }

  //跳转到添加微信素材界面
  @RequestMapping(params = "method=toAddWeChat")
  public String toAddWeChat() {
    return "/weChat/addWeChat";
  }

  //跳转到上传微信素材界面
  @RequestMapping(params = "method=toUploadWeChat")
  public String toUpLoadWeChat(String id, ModelMap model) {
    try {
      /*删除素材*/
      WXArticleTemplate wxArticleTemplate = wxArticleService.getWXArticleTemplateById(id);
      model.addAttribute("wxArticleTemplate", wxArticleTemplate);
    } catch (Exception e) {
      LOG.debug("/weChat.do");
      LOG.debug("method=toUploadWeChat");
      LOG.error(e.getMessage(), e);
    }
    return "/weChat/upLoadWeChat";
  }


  //跳转到查看待审核界面
  @RequestMapping(params = "method=toFindAdult")
  public String toFindAdult(String id, ModelMap model) {
    try {
      /*查询素材*/
      WXMsgDTO wxMsg = wxService.getWXMsgDTOById(id);
      model.addAttribute("wxMsg", wxMsg);
      if (wxMsg.getFromShopId() != null) {
        ShopDTO shopDTO = configService.getShopById(wxMsg.getFromShopId());
        model.addAttribute("shopDTO", shopDTO);
        model.addAttribute("mass_count", NumberUtil.intValue(wxService.countShopMonthSuccessMassMsg(wxMsg.getFromShopId())));
      }
    } catch (Exception e) {
      LOG.debug("/weChat.do");
      LOG.debug("method=toFindAdult");
      LOG.error(e.getMessage(), e);
    }
    return "/weChat/findWXMsg";
  }

  /**
   * 微信素材列表页面初始化
   */
  @RequestMapping(params = "method=weChatList")
  public void listWeChat(ModelMap model, HttpServletResponse response, Integer startPageNo, String title, String description) throws PageException {
    WXArticleTemplateDTO wxArticleDTO = new WXArticleTemplateDTO();
    /*标题*/
    wxArticleDTO.setTitle(title);
    /*正文*/
    wxArticleDTO.setDescription(description);
    /*总数*/
    int total = wxArticleService.getCountWXArticleJob(wxArticleDTO);
    /*分页*/
    Pager pager = new Pager(total, NumberUtil.intValue(String.valueOf(startPageNo), 1));
    /*失败短信分页查询list*/
    List<WXArticleTemplateDTO> wxArticleDTOList = wxArticleService.getWXArticleJobs(wxArticleDTO, pager);
    String jsonStr = "";
    /*JSON*/
    jsonStr = JsonUtil.listToJson(wxArticleDTOList);
    jsonStr = jsonStr.substring(0, jsonStr.length() - 1);
    if (!"[".equals(jsonStr.trim())) {
      jsonStr = jsonStr + "," + pager.toJson().substring(1, pager.toJson().length());
    } else {
      jsonStr = pager.toJson();
    }
    try {
      PrintWriter writer = response.getWriter();
      writer.write(jsonStr);
      writer.close();
    } catch (Exception e) {
      LOG.debug("/weChat.do");
      LOG.debug("method=weChatList");
      LOG.error(e.getMessage(), e);
    }
    model.addAttribute("wxArticleDTOList", wxArticleDTOList);
  }

  /**
   * 删除微信公共模板(逻辑删除)
   *
   * @param request
   * @throws java.io.IOException
   */
  @RequestMapping(params = "method=deleteWeChat")
  public String deleteWeChat(HttpServletRequest request, String id) throws IOException {
    try {
      /*删除素材*/
      WXArticleTemplate wxArticle = wxArticleService.getWXArticleTemplateById(id);
      if (null != wxArticle) {
        wxArticle.setDeleted(DeletedType.TRUE);
        wxArticleService.deleteWeChat(wxArticle);
      }
    } catch (Exception e) {
      LOG.debug("/weChat.do");
      LOG.debug("method=deleteWeChat");
      LOG.error(e.getMessage(), e);
    }
    return initWeChatPage();
  }

  /**
   * 跳转到修改微信素材界面
   */
  @RequestMapping(params = "method=toModifyWeChat")
  public String modifyWeChat(String id, ModelMap model) {
    try {
      /*删除素材*/
      WXArticleTemplate wxArticleTemplate = wxArticleService.getWXArticleTemplateById(id);
      model.addAttribute("wxArticleTemplate", wxArticleTemplate);
    } catch (Exception e) {
      LOG.debug("/weChat.do");
      LOG.debug("method=modifyWeChat");
      LOG.error(e.getMessage(), e);
    }
    return "/weChat/modifyWeChatPublicTemplateUpload";

  }


  /**
   * 跳转到修改微信待审核修改界面
   */
  @RequestMapping(params = "method=toModifyAudit")
  public String toModifyAudit(String id, ModelMap model) {
    try {
      WXMsgDTO wxMsg = wxService.getWXMsgDTOById(id);
      model.addAttribute("wxMsg", wxMsg);
      if (wxMsg.getFromShopId() != null) {
        ShopDTO shopDTO = configService.getShopById(wxMsg.getFromShopId());
        model.addAttribute("shopDTO", shopDTO);
        model.addAttribute("mass_count", NumberUtil.intValue(wxService.countShopMonthSuccessMassMsg(wxMsg.getFromShopId())));
      }
    } catch (Exception e) {
      LOG.debug("/weChat.do");
      LOG.debug("method=toModifyAudit");
      LOG.error(e.getMessage(), e);
    }
    return "/weChat/modifyWXMsg";

  }

  /**
   * 修改微信素材
   *
   * @param request
   */
  @RequestMapping(params = "method=modifyWeChat")
  public String modifyWeChat(HttpServletRequest request, WXArticleTemplateDTO wxArticleTemplateDTO) {
    WXArticleTemplate wxArticle = wxArticleService.getWXArticleTemplateById(wxArticleTemplateDTO.getIdStr());
    //转型为MultipartHttpRequest(重点的所在)
    MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
    //  获得第1张图片（根据前台的name名称得到上传的文件）
    MultipartFile imgFile1 = multipartRequest.getFile("pic");

    //定义一个数组，用于保存可上传的文件类型
    List fileTypes = new ArrayList();
    fileTypes.add("jpg");
    fileTypes.add("jpeg");
    fileTypes.add("bmp");
    fileTypes.add("gif");

    if (!(imgFile1.getOriginalFilename() == null || "".equals(imgFile1.getOriginalFilename()))) {
      PrintWriter printWriter = null;
      ImageService imageService = ServiceManager.getService(ImageService.class);
      IRecommendShopService recommendShopService = ServiceManager.getService(IRecommendShopService.class);
      try {
        Map<String, Object> result = new HashMap<String, Object>();
        String imagePath = UpYunManager.getInstance().generateUploadImagePath(ConfigConstant.CONFIG_SHOP_ID, imgFile1.getOriginalFilename());
        if (UpYunManager.getInstance().writeFile(imagePath, imgFile1.getBytes(), true, UpYunManager.getInstance().generateDefaultUpYunParams())) {
          Set<ImageType> imageTypes = new HashSet<ImageType>();
          imageTypes.add(ImageType.WX_ARTICLE_IMAGE);
          DataImageRelationDTO dataImageRelationDTO = new DataImageRelationDTO(ConfigConstant.CONFIG_SHOP_ID, wxArticle.getId(), DataType.SHOP_ADVERT, ImageType.WX_ARTICLE_IMAGE, 1);
          dataImageRelationDTO.setImageInfoDTO(new ImageInfoDTO(ConfigConstant.CONFIG_SHOP_ID, imagePath));
          imageService.saveOrUpdateDataImageDTOs(ConfigConstant.CONFIG_SHOP_ID, imageTypes, DataType.SHOP_ADVERT, wxArticle.getId(), dataImageRelationDTO);
          recommendShopService.updateShopRecommendImgInfo(dataImageRelationDTO);
          result.put("success", true);
          result.put("imagePath", imagePath);
          result.put("imageURL", ConfigUtils.getUpYunDomainUrl() + imagePath);
        } else {
          result.put("success", false);
        }
        wxArticle.setPicUrl(ConfigUtils.getUpYunDomainUrl() + imagePath);
        wxArticle.setTitle(wxArticleTemplateDTO.getTitle());
        wxArticle.setDescription(wxArticleTemplateDTO.getDescription());
        wxArticleService.modifyWeChat(wxArticle);
      } catch (Exception e) {
        LOG.error("/weChat.do?method=saveWeChatPic");
        LOG.error(e.getMessage(), e);
      } finally {
        if (null != printWriter) {
          printWriter.flush();
          printWriter.close();
        }
      }
    } else {
      wxArticle.setTitle(wxArticleTemplateDTO.getTitle());
      wxArticle.setDescription(wxArticleTemplateDTO.getDescription());
      wxArticleService.modifyWeChat(wxArticle);
    }
    return initWeChatPage();
  }

  /**
   * 修改微信素材
   *
   * @param request
   */
  @RequestMapping(params = "method=modifyAudit")
  public String modifyAudit(HttpServletRequest request, WXMsgDTO wXMsgDTO) {
    WXMsg wxMsg = wxService.getWXMsgById(wXMsgDTO.getId());
    //转型为MultipartHttpRequest(重点的所在)
    MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
    //  获得第1张图片（根据前台的name名称得到上传的文件）
    MultipartFile imgFile1 = multipartRequest.getFile("pic");

    //定义一个数组，用于保存可上传的文件类型
    List fileTypes = new ArrayList();
    fileTypes.add("jpg");
    fileTypes.add("jpeg");
    fileTypes.add("bmp");
    fileTypes.add("gif");

    if (!(imgFile1.getOriginalFilename() == null || "".equals(imgFile1.getOriginalFilename()))) {
      PrintWriter printWriter = null;
      ImageService imageService = ServiceManager.getService(ImageService.class);
      IRecommendShopService recommendShopService = ServiceManager.getService(IRecommendShopService.class);
      try {
        Map<String, Object> result = new HashMap<String, Object>();
        String imagePath = UpYunManager.getInstance().generateUploadImagePath(ConfigConstant.CONFIG_SHOP_ID, imgFile1.getOriginalFilename());
        if (UpYunManager.getInstance().writeFile(imagePath, imgFile1.getBytes(), true, UpYunManager.getInstance().generateDefaultUpYunParams())) {
          Set<ImageType> imageTypes = new HashSet<ImageType>();
          imageTypes.add(ImageType.WX_AUDIT_IMAGE);
          DataImageRelationDTO dataImageRelationDTO = new DataImageRelationDTO(ConfigConstant.CONFIG_SHOP_ID, wxMsg.getId(), DataType.SHOP_ADVERT, ImageType.WX_AUDIT_IMAGE, 1);
          dataImageRelationDTO.setImageInfoDTO(new ImageInfoDTO(ConfigConstant.CONFIG_SHOP_ID, imagePath));
          imageService.saveOrUpdateDataImageDTOs(ConfigConstant.CONFIG_SHOP_ID, imageTypes, DataType.SHOP_ADVERT, wxMsg.getId(), dataImageRelationDTO);
          recommendShopService.updateShopRecommendImgInfo(dataImageRelationDTO);
          result.put("success", true);
          result.put("imagePath", imagePath);
          result.put("imageURL", ConfigUtils.getUpYunDomainUrl() + imagePath);
        } else {
          result.put("success", false);
        }
        wxMsg.setPicUrl(ConfigUtils.getUpYunDomainUrl() + imagePath);
        wxMsg.setTitle(wXMsgDTO.getTitle());
        wxMsg.setDescription(wXMsgDTO.getDescription());
        wxService.modifyAudit(wxMsg);
      } catch (Exception e) {
        LOG.error("/weChat.do?method=saveWeChatPic");
        LOG.error(e.getMessage(), e);
      } finally {
        if (null != printWriter) {
          printWriter.flush();
          printWriter.close();
        }
      }
    } else {
      wxMsg.setTitle(wXMsgDTO.getTitle());
      wxMsg.setDescription(wXMsgDTO.getDescription());
      wxService.modifyAudit(wxMsg);
    }
    return initAdultPage();
  }

  /**
   * 添加微信素材
   *
   * @param request
   */
  @RequestMapping(params = "method=saveWeChat")
  public String saveWeChat(HttpServletRequest request, WXArticleTemplateDTO wxArticleTemplateDTO) {
    WXArticleTemplate wxArticle = new WXArticleTemplate();
    try {
      wxArticle.setTitle(wxArticleTemplateDTO.getTitle());
      wxArticle.setDescription(wxArticleTemplateDTO.getDescription());
      wxArticleService.saveWeChat(wxArticle);
    } catch (Exception e) {
      LOG.error("/weChat.do?method=saveWeChat");
      LOG.error(e.getMessage(), e);
    }
    return initWeChatPage();
  }

  /**
   * 上传微信素材图片
   *
   * @param request
   */
  @RequestMapping(params = "method=saveWeChatPic")
  public String saveWeChatPic(HttpServletRequest request, WXArticleTemplateDTO wxArticleTemplateDTO) {
    WXArticleTemplate wxArticleTemplate = wxArticleService.getWXArticleTemplateById(wxArticleTemplateDTO.getId().toString());
    //转型为MultipartHttpRequest(重点的所在)
    MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
    //  获得第1张图片（根据前台的name名称得到上传的文件）
    MultipartFile imgFile1 = multipartRequest.getFile("pic");

    //定义一个数组，用于保存可上传的文件类型
    List fileTypes = new ArrayList();
    fileTypes.add("jpg");
    fileTypes.add("jpeg");
    fileTypes.add("bmp");
    fileTypes.add("gif");

    //保存第一张图片
    if (!(imgFile1.getOriginalFilename() == null || "".equals(imgFile1.getOriginalFilename()))) {
      PrintWriter printWriter = null;
      ImageService imageService = ServiceManager.getService(ImageService.class);
      IRecommendShopService recommendShopService = ServiceManager.getService(IRecommendShopService.class);
      try {
        Map<String, Object> result = new HashMap<String, Object>();
        String imagePath = UpYunManager.getInstance().generateUploadImagePath(ConfigConstant.CONFIG_SHOP_ID, imgFile1.getOriginalFilename());
        if (UpYunManager.getInstance().writeFile(imagePath, imgFile1.getBytes(), true, UpYunManager.getInstance().generateDefaultUpYunParams())) {
          Set<ImageType> imageTypes = new HashSet<ImageType>();
          imageTypes.add(ImageType.WX_ARTICLE_IMAGE);
          DataImageRelationDTO dataImageRelationDTO = new DataImageRelationDTO(ConfigConstant.CONFIG_SHOP_ID, wxArticleTemplate.getId(), DataType.SHOP_ADVERT, ImageType.WX_ARTICLE_IMAGE, 1);
          dataImageRelationDTO.setImageInfoDTO(new ImageInfoDTO(ConfigConstant.CONFIG_SHOP_ID, imagePath));
          imageService.saveOrUpdateDataImageDTOs(ConfigConstant.CONFIG_SHOP_ID, imageTypes, DataType.SHOP_ADVERT, wxArticleTemplate.getId(), dataImageRelationDTO);
          recommendShopService.updateShopRecommendImgInfo(dataImageRelationDTO);
          result.put("success", true);
          result.put("imagePath", imagePath);
          result.put("imageURL", ConfigUtils.getUpYunDomainUrl() + imagePath);
        } else {
          result.put("success", false);
        }
//        printWriter= response.getWriter();
//        printWriter.write(JsonUtil.mapToJson(result));
        wxArticleTemplate.setPicUrl(ConfigUtils.getUpYunDomainUrl() + imagePath);
        wxArticleService.saveWeChat(wxArticleTemplate);
      } catch (Exception e) {
        LOG.error("/weChat.do?method=saveWeChatPic");
        LOG.error(e.getMessage(), e);
      } finally {
        if (null != printWriter) {
          printWriter.flush();
          printWriter.close();
        }
      }
      //------------------------------------------------------------------
    }
    return initWeChatPage();
  }


  /**
   * 通过传入页面读取到的文件，处理后保存到本地磁盘，并返回一个已经创建好的File
   *
   * @param imgFile   从页面中读取到的文件
   * @param typeName  商品的分类名称
   * @param brandName 商品的品牌名称
   * @param fileTypes 允许的文件扩展名集合
   * @return
   */
  private File getFile(MultipartFile imgFile, String typeName, String brandName, List fileTypes) {
    String fileName = imgFile.getOriginalFilename();
    //获取上传文件类型的扩展名,先得到.的位置，再截取从.的下一个位置到文件的最后，最后得到扩展名
    String ext = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
    //对扩展名进行小写转换
    ext = ext.toLowerCase();

    File file = null;
    if (fileTypes.contains(ext)) {                      //如果扩展名属于允许上传的类型，则创建文件
      file = this.creatFolder(typeName, brandName, fileName);
      try {
        imgFile.transferTo(file);                   //保存上传的文件
      } catch (IllegalStateException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return file;
  }

  /**
   * 检测与创建一级、二级文件夹、文件名
   * 这里我通过传入的两个字符串来做一级文件夹和二级文件夹名称
   * 通过此种办法我们可以做到根据用户的选择保存到相应的文件夹下
   */
  private File creatFolder(String typeName, String brandName, String fileName) {
//    File file = null;
//    typeName = typeName.replaceAll("/", "");               //去掉"/"
//    typeName = typeName.replaceAll(" ", "");               //替换半角空格
//    typeName = typeName.replaceAll(" ", "");               //替换全角空格
//
//    brandName = brandName.replaceAll("/", "");             //去掉"/"
//    brandName = brandName.replaceAll(" ", "");             //替换半角空格
//    brandName = brandName.replaceAll(" ", "");             //替换全角空格

    File firstFolder = new File("c:/", fileName);         //一级文件夹
//    if(firstFolder.exists()) {                             //如果一级文件夹存在，则检测二级文件夹
//      File secondFolder = new File(firstFolder,brandName);
//      if(secondFolder.exists()) {                        //如果二级文件夹也存在，则创建文件
//        file = new File(secondFolder,fileName);
//      }else {                                            //如果二级文件夹不存在，则创建二级文件夹
//        secondFolder.mkdir();
//        file = new File(secondFolder,fileName);        //创建完二级文件夹后，再合建文件
//      }
//    }else {                                                //如果一级不存在，则创建一级文件夹
//      firstFolder.mkdir();
//      File secondFolder = new File(firstFolder,brandName);
//      if(secondFolder.exists()) {                        //如果二级文件夹也存在，则创建文件
//        file = new File(secondFolder,fileName);
//      }else {                                            //如果二级文件夹不存在，则创建二级文件夹
//        secondFolder.mkdir();
//        file = new File(secondFolder,fileName);
//      }
//    }
    return firstFolder;
  }
  // ----------------------------------------待审核部分代码开始-------------------------------------------------------

  /**
   * 微信素材列表页面初始化
   */
  @RequestMapping(params = "method=initAudit")
  public void listAdult(ModelMap model, HttpServletResponse response, HttpServletRequest request,
                        Integer startPageNo, String title, String description) {
    try {
      WXMsgDTO wxMsgDTO = new WXMsgDTO();
      wxMsgDTO.setTitle(title);
      Long shopId = WebUtil.getShopId(request);
      wxMsgDTO.setFromShopId(shopId);
      wxMsgDTO.setDescription(description);
      int total = wxService.getCountAdultJob(wxMsgDTO, "aduitting");
      Pager pager = new Pager(total, NumberUtil.intValue(String.valueOf(startPageNo), 1));
      List<WXMsgDTO> wxMsgDTOList = wxService.getAdultJobs(wxMsgDTO, pager, "aduitting");
      if (CollectionUtil.isNotEmpty(wxMsgDTOList)) {
        for (WXMsgDTO msgDTO : wxMsgDTOList) {
          ShopDTO shopDTO = configService.getShopById(msgDTO.getFromShopId());
          if (shopDTO == null) {
            LOG.error("wx:wx_msg is exception,msgId is {}", msgDTO.getId());
            continue;
          }
          msgDTO.setFromShopName(shopDTO.getName());
        }
      }
      String jsonStr = "";
      /*JSON*/
      jsonStr = JsonUtil.listToJson(wxMsgDTOList);
      jsonStr = jsonStr.substring(0, jsonStr.length() - 1);
      if (!"[".equals(jsonStr.trim())) {
        jsonStr = jsonStr + "," + pager.toJson().substring(1, pager.toJson().length());
      } else {
        jsonStr = pager.toJson();
      }

      PrintWriter writer = response.getWriter();
      writer.write(jsonStr);
      writer.close();
    } catch (Exception e) {
      LOG.debug("/weChat.do");
      LOG.debug("method=initAudit");
      LOG.error(e.getMessage(), e);
    }
  }

  @RequestMapping(params = "method=toWXOperator")
  public String toWXOperator() {
    return "/weChat/wx_operator";
  }

  private Result validateOperate(String pass) {
    Result result = new Result();
    if (StringUtil.isEmpty(pass)) {
      return result.LogErrorMsg("请输入操作密码。");
    }
    if (!"654321".equals(pass)) {
      return result.LogErrorMsg("请输入操作密码不正确，请重新输入。");
    }
    return result;
  }

  @RequestMapping(params = "method=clearMemCache")
  @ResponseBody
  public Object clearMemCache(String pass) {
    try {
      Result result = validateOperate(pass);
      if (!result.isSuccess()) return result;
      return WXHelper.clearMemCache(null, WXHelper.getDefaultPublicNo());
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return e.getMessage();
    }
  }


  @RequestMapping(params = "method=reCreateMenu")
  @ResponseBody
  public Object reCreateMenu(String publicNo) {
    try {
      if (StringUtil.isEmpty(publicNo)) {
        return new Result(false, "public is null");
      }
      IWXAccountService accountService = ServiceManager.getService(WXAccountService.class);
      WXAccountDTO accountDTO = accountService.getWXAccountDTOByPublicNo(publicNo);
      if (accountDTO == null) {
        return new Result(false, "公共号不存在");
      }
      return WXHelper.createMenu(accountDTO.getPublicNo());
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return new Result(false, e.getMessage());
    }
  }

  @RequestMapping(params = "method=synUserDTOList")
  @ResponseBody
  public Object synUserDTOList(String pass) {
    try {
      Result result = validateOperate(pass);
      if (!result.isSuccess()) return result;
      wxUserService.synALLWXUserDTOs();
      result.setMsg("同步微信用户成功。");
      return result;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return new Result(false, e.getMessage());
    }
  }

  @RequestMapping(params = "method=buildWXImageLib")
  @ResponseBody
  public Object buildWXImageLib(HttpServletRequest request, String pass) {
    StringBuffer sb = new StringBuffer();
    try {
      Result result = validateOperate(pass);
      if (!result.isSuccess()) return result;
      boolean res = wxUserService.buildWXImageLib(WXConstant.IMAGE_FILE_LIB_BILL);
      if (res) sb.append(WXConstant.IMAGE_FILE_LIB_BILL + "图片，创建成功\n");
      res = wxUserService.buildWXImageLib(WXConstant.IMAGE_FILE_LIB_MEMBER);
      if (res) sb.append(WXConstant.IMAGE_FILE_LIB_MEMBER + "图片，创建成功\n");
      res = wxUserService.buildWXImageLib(WXConstant.IMAGE_FILE_LIB_VEHICLE);
      if (res) sb.append(WXConstant.IMAGE_FILE_LIB_VEHICLE + "图片，创建成功\n");
      result.setMsg(sb.toString());
      return result;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return new Result(false, "生成图库异常。");
    }
  }

  @RequestMapping(params = "/encryptAllAccount")
  @ResponseBody
  public void encryptAllAccount() {
    try {
      wxUserService.encryptAllAccount();
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * 审核通过,正式发送
   */
  @RequestMapping(params = "method=sendShopMassNewsMsg")
  @ResponseBody
  public Object sendShopMassNewsMsg(Long id, String description) {
    try {
      Result result = new Result();
      WXMsg wxMsg = wxService.getAuditingMsgById(id);
      if (wxMsg == null) return result.LogErrorMsg("素材不存在，或已经发送。");
      if (StringUtil.isEmpty(description)) return result.LogErrorMsg("素材描述不应为空");
      if (StringUtil.isEmpty(wxMsg.getPicUrl())) {
        return result.LogErrorMsg("素材图片不存在。");
      }
      Long shopId = wxMsg.getFromShopId();
      int count = wxService.countShopMonthSuccessMassMsg(shopId);
      if (count > 4) {
        return result.LogErrorMsg("该店铺本月已群发" + count + "条信息，超过系统上限！");
      }
      WXArticleDTO articleDTO = new WXArticleDTO();
      articleDTO.setUserId(wxMsg.getUserId());
      articleDTO.setUserName(wxMsg.getUserName());
      articleDTO.setTitle(wxMsg.getTitle());
      articleDTO.setDescription(description);
      articleDTO.setPicUrl(wxMsg.getPicUrl());
      articleDTO.setUrl(wxMsg.getUrl());
      articleDTO.setWxMsgLocalId(wxMsg.getId());
      result = wxUserService.sendShopMassNewsMsg(shopId, articleDTO);
      return result;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  /**
   * 审核不通过
   */
  @RequestMapping(params = "method=notThroughAudit")
  @ResponseBody
  public Object notThroughAudit(Long id, String reason) {
    try {
      Result result = new Result();
      WXMsg wxMsg = wxService.getAuditingMsgById(id);
      if (wxMsg == null) return result.LogErrorMsg("素材不存在，或已经发送。");
      if (StringUtil.isEmpty(reason)) return result.LogErrorMsg("请填写审核不通过的理由");
      wxService.saveOrUpdateWXMsg(wxMsg.toDTO());
      return result;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  // ----------------------------------------待审核部分代码结束-------------------------------------------------------


  // ----------------------------------------微信用户部分代码开始-------------------------------------------------------
  //跳转到微信用户主界面
  @RequestMapping(params = "method=initWxUser")
  public String initWxUser() {
    return "/weChat/wxUserList";
  }

  /**
   * 微信User列表页面初始化
   */
  @RequestMapping(params = "method=wxUserList")
  public void listWxUser(ModelMap model, HttpServletResponse response, Integer startPageNo, WXUserSearchCondition condition) throws PageException {
    /*总数*/
    int total = wxUserService.countWxUserBySearchCondition(condition);
    /*分页*/
    Pager pager = new Pager(total, NumberUtil.intValue(String.valueOf(startPageNo), 1));
    /*失败短信分页查询list*/
    List<WXUserDTO> wxUserDTOList = wxUserService.getWxUserDTOsBySearchCondition(condition, pager);
    String jsonStr = "";
    /*JSON*/
    jsonStr = JsonUtil.listToJson(wxUserDTOList);
    jsonStr = jsonStr.substring(0, jsonStr.length() - 1);
    if (!"[".equals(jsonStr.trim())) {
      jsonStr = jsonStr + "," + pager.toJson().substring(1, pager.toJson().length());
    } else {
      jsonStr = pager.toJson();
    }
    try {
      PrintWriter writer = response.getWriter();
      writer.write(jsonStr);
      writer.close();
    } catch (Exception e) {
      LOG.debug("/weChat.do");
      LOG.debug("method=wxUserList");
      LOG.error(e.getMessage(), e);
    }
    model.addAttribute("wxUserDTOList", wxUserDTOList);
  }


  /**
   * 微信WXUserVehicle列表页面初始化
   */
  @RequestMapping(params = "method=initWxUserVehicleList")
  public String listWXUserVehicle(ModelMap model, Integer startPageNo, String openId) throws PageException {
    WXUserDTO wxUserDTO = new WXUserDTO();
    /*openid*/
    wxUserDTO.setOpenid(openId);
    /*总数*/
    int total = wxArticleService.getCountWXUserVehicleJob(wxUserDTO);
    /*分页*/
//    Pager pager = new Pager(total, NumberUtil.intValue(String.valueOf(startPageNo), 1));
    /*失败短信分页查询list*/
    List<WXUserVehicleDTO> wXUserVehicleDTOs = wxArticleService.getWXUserVehicleJobs(wxUserDTO);
    if (wXUserVehicleDTOs != null) {
      for (WXUserVehicleDTO wxUserVehicleDTO : wXUserVehicleDTOs) {
        if (wxUserVehicleDTO.getProvince() != null) {
          AreaDTO areaDTOP = AreaCacheManager.getAreaDTOByNo(wxUserVehicleDTO.getProvince());
          AreaDTO areaDTOC = AreaCacheManager.getAreaDTOByNo(wxUserVehicleDTO.getCity());
          wxUserVehicleDTO.setProvinceAndCity(areaDTOP.getName() + " " + areaDTOC.getName());
        }
      }
    }
    model.addAttribute("wXUserVehicleDTOs", wXUserVehicleDTOs);
    model.addAttribute("openId", openId);
    return "/weChat/wxUserVehicleList";
  }

  //跳转到添加WXUserVehicle界面
  @RequestMapping(params = "method=toAddUserVehicle")
  public String toAddUserVehicle(String openId, ModelMap model) {
    model.addAttribute("openId", openId);
    return "/weChat/addWXUserVehicle";
  }


  //添加WXUserVehicle
  @RequestMapping(params = "method=addUserVehicle")
  public String addUserVehicle(WXUserVehicle wXUserVehicle, ModelMap model) {
    try {
      wxArticleService.saveWXUserVehicle(wXUserVehicle);
    } catch (Exception e) {
      LOG.debug("/weChat.do");
      LOG.debug("method=addUserVehicle");
      LOG.error(e.getMessage(), e);
    }
    try {
      return listWXUserVehicle(model, 0, wXUserVehicle.getOpenId());
    } catch (PageException e) {
      e.printStackTrace();
    }
    return null;
  }

  //跳转到修改WXUserVehicle界面
  @RequestMapping(params = "method=toModifyVehicle")
  public String toModifyVehicle(String id, ModelMap model) {
    WXUserVehicle wXUserVehicle = wxArticleService.getWXUserVehicleById(id);
    WXUserVehicleDTO wXUserVehicleDTO = wXUserVehicle.toDTO();
//    model.addAttribute("id", wXUserVehicle.getId());
//    model.addAttribute("openId", wXUserVehicle.getOpenId());
    model.addAttribute("wXUserVehicleDTO", wXUserVehicleDTO);
    return "/weChat/modifyWXUserVehicle";
  }

  //修改WXUserVehicle
  @RequestMapping(params = "method=modifyVehicle")
  public String modifyVehicle(WXUserVehicleDTO wXUserVehicleDTO, ModelMap model) {
    WXUserVehicle wXUserVehicle = wxArticleService.getWXUserVehicleById(wXUserVehicleDTO.getIdStr());
    wXUserVehicle.setVehicleNo(wXUserVehicleDTO.getVehicleNo());
    wXUserVehicle.setVin(wXUserVehicleDTO.getVin());
    wXUserVehicle.setEngineNo(wXUserVehicleDTO.getEngineNo());
    wXUserVehicle.setProvince(wXUserVehicleDTO.getProvince());
    wXUserVehicle.setCity(wXUserVehicleDTO.getCity());
    wxArticleService.modifyWXUserVehicle(wXUserVehicle);
    try {
      return listWXUserVehicle(model, 0, wXUserVehicle.getOpenId());
    } catch (PageException e) {
      e.printStackTrace();
    }
    return null;
  }

  //删除WXUserVehicle
  @RequestMapping(params = "method=deleteVehicle")
  public String deleteVehicle(String id, ModelMap model) {
    WXUserVehicle wXUserVehicle = wxArticleService.getWXUserVehicleById(id);
    wXUserVehicle.setDeleted(DeletedType.TRUE);
    wxArticleService.deleteWXUserVehicle(wXUserVehicle);
    try {
      return listWXUserVehicle(model, 0, wXUserVehicle.getOpenId());
    } catch (PageException e) {
      e.printStackTrace();
    }
    return null;
  }


  // ----------------------------------------微信用户部分代码结束-------------------------------------------------------


  @RequestMapping(params = "method=toAccountManager")
  public String toAccountManager() {
    return "/weChat/wxAccountManager";
  }

  @RequestMapping(params = "method=getWXAccount")
  @ResponseBody
  public Object getWXAccount(WXShopAccountSearchCondition condition) {
    try {
      Pager pager = new Pager(accountService.countWXAccount(condition), condition.getCurrentPage(), condition.getPageSize());
      if (pager.getTotalRows() <= 0) {
        return new Result();
      }
      condition.setPager(pager);
      List<WXAccountDTO> accountDTOs = accountService.getWXAccountDTO(condition);
      IConfigService configService = ServiceManager.getService(ConfigService.class);
      for (WXAccountDTO accountDTO : accountDTOs) {
        if (accountDTO.getShopId().equals(1L)) {
          accountDTO.setShopNames("缺省");
          continue;
        }
        List<WXShopAccountDTO> shopAccountDTOs = accountService.getWXShopAccountDTO(null, accountDTO.getId());
        if (CollectionUtil.isNotEmpty(shopAccountDTOs)) {
          StringBuilder sb = new StringBuilder();
          for (WXShopAccountDTO shopAccountDTO : shopAccountDTOs) {
            ShopDTO shopDTO = configService.getShopById(shopAccountDTO.getShopId());
            sb.append(shopDTO != null ? (shopDTO.getName() + ",") : "");
          }
          if (sb.length() > 0) {
            accountDTO.setShopNames(sb.substring(0, sb.length() - 1));
          }
        }
      }
      ListResult<WXAccountDTO> listResult = new PagingListResult<WXAccountDTO>(accountDTOs, true, pager);
      return listResult;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return new Result(e.getMessage(), false);
    }
  }

  @RequestMapping(params = "method=toWXShopAccountManager")
  public String toWXShopAccountManager() {
    return "/weChat/wxShopAccountManager";
  }

  @RequestMapping(params = "method=saveOrUpdateWXShopAccount")
  @ResponseBody
  public Result saveOrUpdateWXShopAccount(Long id, Double money, String expireDateStr) {
    try {
      Result result = new Result();
      if (id == null) return result.LogErrorMsg("账户异常");
      //update shopAccount
      WXShopAccountDTO shopAccountDTO = accountService.getWXShopAccountDTOById(id);
      Double balance = NumberUtil.add(shopAccountDTO.getBalance(), money);
      shopAccountDTO.setBalance(balance);
      Long expireDate = DateUtil.convertDateStringToDateLong(DateUtil.DEFAULT, expireDateStr);
      shopAccountDTO.setExpireDate(DateUtil.getEndOfDate(expireDate));
      accountService.saveOrUpdateWXShopAccountDTO(shopAccountDTO);
      //save wxShopBill
      WXShopBillDTO billDTO = new WXShopBillDTO();
      billDTO.setShopId(shopAccountDTO.getShopId());
      billDTO.setVestDate(System.currentTimeMillis());
      billDTO.setScene(SmsSendScene.WX_RECHARGE);
      billDTO.setTotal(money);
      wxService.saveOrUpdateWXShopBill(billDTO);
      return result;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return new Result("保存异常。", false);
    }
  }

  @ResponseBody
  @RequestMapping(params = "method=getWXShopAccount")
  public Object shopVehicleList(HttpServletRequest request, Integer page, Integer rows, WXShopAccountSearchCondition condition) {
    Map<String, Object> result = new HashMap<String, Object>();
    try {
      int total = accountService.countWXShopAccount(condition);
      Pager pager = new Pager(total, page, rows);
      condition.setPager(pager);
      result.put("total", total);
      List<WXShopAccountDTO> shopAccountDTOs = accountService.getWXShopAccountDTO(condition);
      if (CollectionUtil.isNotEmpty(shopAccountDTOs)) {
        for (WXShopAccountDTO shopAccountDTO : shopAccountDTOs) {
          WXAccountDTO accountDTO = accountService.getWXAccountDTOById(shopAccountDTO.getAccountId());
          shopAccountDTO.setAccountName(accountDTO != null ? accountDTO.getName() : null);
          ShopDTO shopDTO = configService.getShopById(shopAccountDTO.getShopId());
          shopAccountDTO.setShopName(shopDTO != null ? shopDTO.getName() : null);
        }
      }
      result.put("rows", shopAccountDTOs);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
    return result;
  }

  @RequestMapping(params = "method=getWXAccountDetail")
  @ResponseBody
  public Object getWXAccountDetail(Long accountId) {
    try {
      WXAccountDTO accountDTO = accountService.getWXAccountDTOById(accountId);
      byte[] appSecretByte = accountDTO.getAppSecretByte();
      String appSecret = new String(EncryptionUtil.decrypt(appSecretByte, WXHelper.getSecretKey()));
      accountDTO.setSecret(appSecret);
      List<WXShopAccountDTO> shopAccountDTOs = accountService.getWXShopAccountDTO(null, accountDTO.getId());
      if (CollectionUtil.isNotEmpty(shopAccountDTOs)) {
        accountDTO.setShopAccountDTOs(shopAccountDTOs.toArray(new WXShopAccountDTO[shopAccountDTOs.size()]));
        for (WXShopAccountDTO shopAccountDTO : shopAccountDTOs) {
          ShopDTO shopDTO = configService.getShopById(shopAccountDTO.getShopId());
          shopAccountDTO.setShopName(shopDTO != null ? shopDTO.getName() : null);
        }
      }
      return accountDTO;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return null;
    }
  }

  private Result validateSaveOrUpdateWXAccount(WXAccountDTO accountDTO) {
    Result result = new Result();
    if (StringUtil.isEmpty(accountDTO.getName())) {
      return result.LogErrorMsg("公共号名不能为空。");
    }
    if (StringUtil.isEmpty(accountDTO.getPublicNo())) {
      return result.LogErrorMsg("PUBLIC_NO 不能为空。");
    }
    if (StringUtil.isEmpty(accountDTO.getAppId())) {
      return result.LogErrorMsg("APP_ID 不能为空。");
    }
    if (StringUtil.isEmpty(accountDTO.getSecret())) {
      return result.LogErrorMsg("SECRET 不能为空。");
    }
    return result;
  }

  private void initWXShopAccount(WXAccountDTO accountDTO) {
    Set<Long> shopIdSet = new HashSet<Long>();
    List<WXShopAccountDTO> shopAccountDTOs = accountService.getWXShopAccountDTO(null, accountDTO.getId());
    if (CollectionUtil.isNotEmpty(shopAccountDTOs)) {
      for (WXShopAccountDTO shopAccountDTO : shopAccountDTOs) {
        shopIdSet.add(shopAccountDTO.getShopId());
      }
    }
    WXShopAccountDTO[] accountDTOs = accountDTO.getShopAccountDTOs();
    if (ArrayUtil.isNotEmpty(accountDTOs)) {
      for (WXShopAccountDTO shopAccountDTO : accountDTOs) {
        shopIdSet.add(shopAccountDTO.getShopId());
      }
    }
    //delete WXShopAccount
    for (Long shopId : shopIdSet) {
      WXShopAccountDTO shopAccountDTO = CollectionUtil.getFirst(accountService.getWXShopAccountDTO(shopId, null));
      if (shopAccountDTO == null) continue;
      shopAccountDTO.setDeleted(DeletedType.TRUE);
      accountService.saveOrUpdateWXShopAccountDTO(shopAccountDTO);
    }
    //delete qr_code
    for (Long shopId : shopIdSet) {
      WXQRCodeSearchCondition condition = new WXQRCodeSearchCondition();
      condition.setShopId(shopId);
      WXQRCodeDTO codeDTO = CollectionUtil.getFirst(wxUserService.getWXQRCodeDTO(condition));
      if (codeDTO == null) continue;
      codeDTO.setShopId(null);
      wxUserService.saveOrUpdateWXQRCodeDTOs(codeDTO);
    }
    //delete ShopWXUser
    for (Long shopId : shopIdSet) {
      List<ShopWXUserDTO> shopWXUserDTOs = wxUserService.getShopWXUserByShopId(shopId);
      if (CollectionUtil.isEmpty(shopWXUserDTOs)) continue;
      for (ShopWXUserDTO shopWXUserDTO : shopWXUserDTOs) {
        shopWXUserDTO.setDeleted(DeletedType.TRUE);
        wxUserService.saveOrUpdateShopWXUser(shopWXUserDTO);
      }
    }
  }

  @RequestMapping(params = "method=saveOrUpdateWXAccount")
  @ResponseBody
  public Object saveOrUpdateWXAccount(WXAccountDTO accountDTO) {
    try {
      Result result = validateSaveOrUpdateWXAccount(accountDTO);
      if (!result.isSuccess()) return result;
      //update wxAccount
      byte[] encryptData = EncryptionUtil.encrypt(accountDTO.getSecret().getBytes(), WXHelper.getSecretKey());
      accountDTO.setAppSecretBlob(Hibernate.createBlob(encryptData));
      accountDTO.setAccountType(accountDTO.getAccountType() == null ? WXAccountType.YIFA : accountDTO.getAccountType());
      accountService.saveOrUpdateWXAccount(accountDTO);
      //init data
      initWXShopAccount(accountDTO);
      //save WXUser
      wxUserService.synUserDTOList(accountDTO.getPublicNo());
      //save shop account from page
      WXShopAccountDTO[] shopAccountDTOs = accountDTO.getShopAccountDTOs();
      if (ArrayUtil.isNotEmpty(shopAccountDTOs)) {
        for (WXShopAccountDTO shopAccountDTO : shopAccountDTOs) {
          //save WXShopAccount
          WXShopAccount shopAccount = accountService.getWXShopAccountByShopId(shopAccountDTO.getShopId());
          shopAccountDTO.setId(shopAccount != null ? shopAccount.getId() : null);
          shopAccountDTO.setAccountId(accountDTO.getId());
          shopAccountDTO.setExpireDate(DateUtil.getStartTimeOfMonth(1));
          shopAccountDTO.setBalance(WXConstant.DEFAULT_WX_GIFT_TOTAL);
          accountService.saveOrUpdateWXShopAccountDTO(shopAccountDTO);
          //save ShopWXUser
          List<WXUserDTO> userDTOs = wxUserService.getWXUserDTOByPublicNo(accountDTO.getPublicNo());
          if (CollectionUtil.isEmpty(userDTOs)) continue;
          for (WXUserDTO userDTO : userDTOs) {
            ShopWXUserDTO shopWXUserDTO = new ShopWXUserDTO();
            shopWXUserDTO.setShopId(shopAccountDTO.getShopId());
            shopWXUserDTO.setOpenId(userDTO.getOpenid());
            shopWXUserDTO.setDeleted(DeletedType.FALSE);
            wxUserService.saveOrUpdateShopWXUser(shopWXUserDTO);
          }
        }
      }
      return result;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      return null;
    }
  }


}


