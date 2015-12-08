package com.bcgogo.admin;

import com.bcgogo.config.upyun.UpYunManager;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.util.WebUtil;
import com.bcgogo.utils.ConfigConstant;
import com.bcgogo.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author
 */
@Controller
@RequestMapping("/upYun.do")
public class ImageController {
  private static final Logger LOG = LoggerFactory.getLogger(ImageController.class);

  @RequestMapping(params = "method=getUpYunFileDTO")
  @ResponseBody
  public Object toUpYunSample(HttpServletRequest request) {
    try{
      Long shopId = WebUtil.getShopId(request);
      return UpYunManager.getInstance().generateDefaultUpYunFileDTO(shopId);
    }catch (Exception e){
      LOG.error("/upYun.do?method=getUpYunFileDTO");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    }

    return null;
  }

  @RequestMapping(params = "method=saveImage")
  public void saveImage(HttpServletRequest request,HttpServletResponse response,Long imageShopId,@RequestParam("file")MultipartFile file) {
    Long shopId = null;
    response.setContentType("text/html");
    PrintWriter printWriter = null;
    try {
      Map<String, Object> result = new HashMap<String, Object>();
      shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shop id is null!");

      String imagePath = UpYunManager.getInstance().generateUploadImagePath((imageShopId==null? ConfigConstant.CONFIG_SHOP_ID:imageShopId),file.getOriginalFilename());
      if (UpYunManager.getInstance().writeFile(imagePath, file.getBytes(), true, UpYunManager.getInstance().generateDefaultUpYunParams())) {
        result.put("success", true);
        result.put("imagePath", imagePath);
        result.put("imageURL", ConfigUtils.getUpYunDomainUrl()+imagePath);
      } else {
        result.put("success", false);
      }
      printWriter= response.getWriter();
      printWriter.write(JsonUtil.mapToJson(result));
    } catch (Exception e) {
      LOG.error("/upYun.do?method=saveImage");
      LOG.debug("shopId:" + request.getSession().getAttribute("shopId") + ",userId:" + request.getSession().getAttribute("userId"));
      LOG.error(e.getMessage(), e);
    } finally {
      printWriter.flush();
      printWriter.close();
    }
  }
}
