package com.bcgogo.admin.config.shop;

import com.bcgogo.common.Result;
import com.bcgogo.config.cache.AreaCacheManager;
import com.bcgogo.config.dto.AreaDTO;
import com.bcgogo.config.dto.RecommendTreeDTO;
import com.bcgogo.config.dto.image.DataImageRelationDTO;
import com.bcgogo.config.dto.image.ImageInfoDTO;
import com.bcgogo.config.model.ShopAdArea;
import com.bcgogo.config.service.IAreaService;
import com.bcgogo.config.service.IRecommendShopService;
import com.bcgogo.config.service.image.ImageService;
import com.bcgogo.config.upyun.UpYunManager;
import com.bcgogo.config.util.ConfigUtils;
import com.bcgogo.enums.config.DataType;
import com.bcgogo.enums.config.ImageType;
import com.bcgogo.product.service.IProductService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.user.dto.Node;
import com.bcgogo.user.dto.permission.ModuleDTO;
import com.bcgogo.util.WebUtil;
import com.bcgogo.utils.CollectionUtil;
import com.bcgogo.utils.ConfigConstant;
import com.bcgogo.utils.JsonUtil;
import com.bcgogo.utils.NumberUtil;
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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by XinyuQiu on 14-7-28.
 */
@Controller
@RequestMapping("/shopAd.do")
public class ShopAdController {
  private static final Logger LOG = LoggerFactory.getLogger(ShopAdController.class);

  /**
   * 获得该店铺的主营车型
   *
   */
  @RequestMapping(params = "method=getShopAdAreaScopeByShopId")
  @ResponseBody
  public Object getShopAdAreaScopeByShopId(HttpServletRequest request, Long shopId) {
    Node node = null;
    try {
      IAreaService areaService = ServiceManager.getService(IAreaService.class);
      node = areaService.getShopAdAreaScopeByShopId(shopId);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      node = new Node();
    }
    return node;
  }

  /**
   * 获得选中的主营车型
   *
   * @return CheckNode
   */
  @RequestMapping(params = "method=getShopAdAreaScope")
  @ResponseBody
  public Object getShopAdAreaScope(HttpServletRequest request, Long shopId) {
    Node node = null;
    try {
      IAreaService areaService = ServiceManager.getService(IAreaService.class);
      Set<Long> ids = new HashSet<Long>(NumberUtil.parseLongValues(request.getParameter("ids")));
      if (CollectionUtil.isEmpty(ids)){
        return areaService.getShopAdAreaScopeByShopId(shopId);
      }
      node = areaService.getCheckedShopAdAreaScope(ids);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      node = new Node();
    }
    return node;
  }

  /**
   * 获得选中的广告类目
   */
  @RequestMapping(params = "method=getShopRecommend")
  @ResponseBody
  public Object getShopRecommend(HttpServletRequest request,Long shopId){
    Node node = null;
    try {
      IAreaService areaService = ServiceManager.getService(IAreaService.class);
      Set<Long> ids = new HashSet<Long>(NumberUtil.parseLongValues(request.getParameter("ids")));
      if (CollectionUtil.isEmpty(ids)){
        return areaService.getShopRecommendScopeByShopId(shopId);
      }
      node = areaService.getCheckedShopRecommendScope(ids);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      node = new Node();
    }
    return node;
  }


  /**
   * 新增或修改广告类目
   */
  @RequestMapping(params = "method=saveOrUpdateShopRecommend")
  @ResponseBody
  public Object saveOrUpdateShopRecommend(HttpServletRequest request,RecommendTreeDTO recommendTreeDTO){

    Result result ;
    IRecommendShopService recommendShopService = ServiceManager.getService(IRecommendShopService.class);
    try {
      result =  recommendShopService.validateSaveOrUpdateRecommendTreeDTO(recommendTreeDTO);
      if(result != null && result.isSuccess()){
        recommendShopService.saveOrUpdateShopRecommend(recommendTreeDTO);
      }
      result = new Result();
      result.setData(recommendTreeDTO);
      return result;
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      result = new Result("网络异常",false);
    }
    return result;
  }

  /**
   * 删除广告类目
   */
  @RequestMapping(params = "method=deleteShopRecommend")
  @ResponseBody
  public Object deleteShopRecommend(HttpServletRequest request,RecommendTreeDTO recommendTreeDTO){
    Result result ;
    IRecommendShopService recommendShopService = ServiceManager.getService(IRecommendShopService.class);
    try {
      result =  recommendShopService.validateDeleteRecommendTreeDTO(recommendTreeDTO);
      if(result != null && result.isSuccess()){
        recommendShopService.deleteShopRecommend(recommendTreeDTO);
        result = new Result();
        result.setData(recommendTreeDTO);
        return result;
      }else {
        return result;
      }
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      result = new Result("网络异常",false);
    }
    return result;
  }

  /**
   * 删除广告类目
   */
  @RequestMapping(params = "method=addRecommendImg")
  public void addRecommendImg(HttpServletRequest request,HttpServletResponse response, Long nodeId,
                                @RequestParam("file")MultipartFile file){


    Long shopId = null;
    response.setContentType("text/html");
    PrintWriter printWriter = null;
    ImageService imageService = ServiceManager.getService(ImageService.class);
    IRecommendShopService recommendShopService = ServiceManager.getService(IRecommendShopService.class);
    try {
      Map<String, Object> result = new HashMap<String, Object>();
      shopId = WebUtil.getShopId(request);
      if (shopId == null) throw new Exception("shop id is null!");

      String imagePath = UpYunManager.getInstance().generateUploadImagePath(ConfigConstant.CONFIG_SHOP_ID,file.getOriginalFilename());
      if (UpYunManager.getInstance().writeFile(imagePath, file.getBytes(), true, UpYunManager.getInstance().generateDefaultUpYunParams())) {
        Set<ImageType> imageTypes = new HashSet<ImageType>();
        imageTypes.add(ImageType.RECOMMEND_TREE);
        DataImageRelationDTO dataImageRelationDTO = new DataImageRelationDTO(ConfigConstant.CONFIG_SHOP_ID,nodeId, DataType.RECOMMEND_TREE, ImageType.RECOMMEND_TREE, 1);
        dataImageRelationDTO.setImageInfoDTO(new ImageInfoDTO(ConfigConstant.CONFIG_SHOP_ID, imagePath));
        imageService.saveOrUpdateDataImageDTOs(ConfigConstant.CONFIG_SHOP_ID,imageTypes, DataType.RECOMMEND_TREE,nodeId,dataImageRelationDTO);
        recommendShopService.updateShopRecommendImgInfo(dataImageRelationDTO);
        result.put("success", true);
        result.put("imagePath", imagePath);
        result.put("imageURL", ConfigUtils.getUpYunDomainUrl()+imagePath);
      } else {
        result.put("success", false);
      }
      printWriter= response.getWriter();
      printWriter.write(JsonUtil.mapToJson(result));
    } catch (Exception e) {
      LOG.error("/shopAd.do?method=addRecommendImg");
      LOG.error(e.getMessage(), e);
    } finally {
      printWriter.flush();
      printWriter.close();
    }


  }



}
