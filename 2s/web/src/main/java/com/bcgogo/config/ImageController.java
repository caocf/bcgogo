package com.bcgogo.config;

import com.bcgogo.common.Result;
import com.bcgogo.common.WebUtil;
import com.bcgogo.config.dto.image.DataImageRelationDTO;
import com.bcgogo.config.dto.image.ImageErrorLogDTO;
import com.bcgogo.config.dto.image.ImageInfoDTO;
import com.bcgogo.config.dto.upYun.UpYunFilePolicyDTO;
import com.bcgogo.config.dto.upYun.UpYunPolicyDTO;
import com.bcgogo.config.service.image.IImageService;
import com.bcgogo.config.upyun.UpYunManager;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.config.util.ImageUtils;
import com.bcgogo.enums.config.DataType;
import com.bcgogo.enums.config.ImageScene;
import com.bcgogo.enums.config.ImageType;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * @author
 */
@Controller
@RequestMapping("/upYun.do")
public class ImageController {
  private static final Logger LOG = LoggerFactory.getLogger(ImageController.class);

  @RequestMapping(params = "method=upYunSample")
  public String toUpYunSample(HttpServletRequest request,ModelMap modelMap) {
    try{
      Long shopId = WebUtil.getShopId(request);
      modelMap.addAttribute("upYunFileDTO",UpYunManager.getInstance().generateDefaultUpYunFileDTO(shopId));
    }catch (Exception e){
      LOG.error("/upYun.do?method=upYunSample");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }

    return "config/upYunSample";
  }

  @RequestMapping(params = "method=saveUEditorInfo")
  public String saveUEditorInfo(HttpServletRequest request,ModelMap modelMap,String uEditorInfo) {
    try{
      Long shopId = WebUtil.getShopId(request);
      //<h2 style="font-size: 32px; font-weight: bold; border-bottom: 2px solid rgb(204, 204, 204); padding: 0px 4px 0px 0px; text-align: center; margin: 0px 0px 20px;">飒飒</h2><p><br/></p><h1 style="font-size: 32px; font-weight: bold; border-bottom: 2px solid rgb(204, 204, 204); padding: 0px 4px 0px 0px; text-align: center; margin: 0px 0px 20px;" label="标题居中"><img src="http://bcgogo-test.b0.upaiyun.com/2013/08/02/10000010006490549/160538-9eb2f0ea5c7c7b504ef79045980d9681.jpg"/></h1><p><br/></p><h1 style="font-size: 32px; font-weight: bold; border-bottom: 2px solid rgb(204, 204, 204); padding: 0px 4px 0px 0px; text-align: center; margin: 0px 0px 20px;" label="标题居中"><img src="http://bcgogo-test.b0.upaiyun.com/2013/08/02/10000010006490549/162432-9eb2f0ea5c7c7b504ef79045980d9681.jpg" style=""/></h1><h1 style="font-size: 32px; font-weight: bold; border-bottom: 2px solid rgb(204, 204, 204); padding: 0px 4px 0px 0px; text-align: center; margin: 0px 0px 20px;" label="标题居中"><img src="http://bcgogo-test.b0.upaiyun.com/2013/08/02/10000010006490549/162433-3a661174ec4f32c09825fce664392119.jpg" style=""/></h1><h1 style="font-size: 32px; font-weight: bold; border-bottom: 2px solid rgb(204, 204, 204); padding: 0px 4px 0px 0px; text-align: center; margin: 0px 0px 20px;" label="标题居中"><img src="http://bcgogo-test.b0.upaiyun.com/2013/08/02/10000010006490549/162434-a5f0a84d1ae722312f33672022bc3db9.jpg" style=""/></h1><h1 style="font-size: 32px; font-weight: bold; border-bottom: 2px solid rgb(204, 204, 204); padding: 0px 4px 0px 0px; text-align: center; margin: 0px 0px 20px;" label="标题居中"><img src="http://bcgogo-test.b0.upaiyun.com/2013/08/02/10000010006490549/162436-d28bf5e2b9d568f391ec32a00b41d6f0.jpg" style=""/></h1><h1 style="font-size: 32px; font-weight: bold; border-bottom: 2px solid rgb(204, 204, 204); padding: 0px 4px 0px 0px; text-align: center; margin: 0px 0px 20px;" label="标题居中"><img src="http://bcgogo-test.b0.upaiyun.com/2013/08/02/10000010006490549/162437-81e5c079bdf71835606124a1fd83401b.jpg" style=""/></h1><p><br/></p>
      List<String> imageUrlList = StringUtil.getImgStr(uEditorInfo);
      if(CollectionUtils.isNotEmpty(imageUrlList)){
        ImageInfoDTO imageInfoDTO = null;
        DataImageRelationDTO dataImageRelationDTO = null;
        String imageUrl = null;
        for(int i=0;i<imageUrlList.size();i++){
          imageUrl = imageUrlList.get(i);
          dataImageRelationDTO = new DataImageRelationDTO(shopId, null, DataType.PRODUCT, ImageType.PRODUCT_DESCRIPTION_IMAGE,i+1);
          dataImageRelationDTO.setImageInfoDTO(new ImageInfoDTO(shopId,imageUrl.replaceAll(ConfigUtils.getUpYunDomainUrl(),"")));

          uEditorInfo = uEditorInfo.replaceAll(imageUrlList.get(i), ImageUtils.ImageSrcPlaceHolder+i);
        }
        for(int i=0;i<imageUrlList.size();i++){
          uEditorInfo = uEditorInfo.replaceAll(ImageUtils.ImageSrcPlaceHolder+i,imageUrlList.get(i));
        }
      }

      modelMap.addAttribute("uEditorInfo",uEditorInfo);
    }catch (Exception e){
      LOG.error("/upYun.do?method=saveUEditorInfo");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }

    return "config/upYunSample";
  }

  @RequestMapping(params = "method=saveImageErrorLog")
  @ResponseBody
  public Object saveImageErrorLog(HttpServletRequest request,ImageErrorLogDTO imageErrorLogDTO) {
    Long shopId=null;
    try {
      shopId=WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shop id is null!");

      imageErrorLogDTO.setShopId(shopId);
      IImageService imageService = ServiceManager.getService(IImageService.class);
      imageService.saveImageErrorLogDTO(imageErrorLogDTO);
      return new Result();
    } catch (Exception e) {
      LOG.error("/upYun.do?method=saveImageErrorLog");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }

    return null;
  }
}
