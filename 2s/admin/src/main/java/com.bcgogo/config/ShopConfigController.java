package com.bcgogo.config;

import com.bcgogo.common.Pager;
import com.bcgogo.config.dto.ShopConfigDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.model.ShopConfig;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IShopConfigService;
import com.bcgogo.enums.ShopConfigScene;
import com.bcgogo.enums.ShopConfigStatus;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.utils.JsonUtil;
import com.bcgogo.utils.NumberUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-8-9
 * Time: 下午10:57
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/shopConfig.do")
public class ShopConfigController {
  private static final Logger LOG = LoggerFactory.getLogger(ShopConfigController.class);

  @RequestMapping(params = "method=shopIndividuation")
  public String shopIndividuation(ModelMap modelMap,HttpServletRequest request,HttpServletResponse response)
  {
    request.setAttribute("scene", prepareShopConfigSceneMap());
    return "/shopIndividuation/shopIndividuation";
  }

  public Map<ShopConfigScene,String> prepareShopConfigSceneMap()
  {
    Map<ShopConfigScene,String> shopConfigSceneStringMap = new HashMap<ShopConfigScene, String>();
    for(ShopConfigScene scene : ShopConfigScene.values())
    {
      shopConfigSceneStringMap.put(scene,scene.getScene());
    }

    return shopConfigSceneStringMap;
  }

  @RequestMapping(params = "method=getShopConfigBySceneAndShop")
  public void getShopConfigBySceneAndShop(HttpServletRequest request,HttpServletResponse response,Integer startPageNo, Integer maxRows,ShopConfigScene scene) throws Exception
  {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    Long shopId = null;
    if(StringUtils.isNotBlank(request.getParameter("shopId")))
    {
      shopId = Long.valueOf(request.getParameter("shopId"));
    }

    String shopName = request.getParameter("shopName");

    PrintWriter out = response.getWriter();

    String jsonStr = "";

    ShopDTO shopDTO = null;
    try{
      if(null == shopId && StringUtils.isNotBlank(shopName))
      {
        shopDTO = configService.getShopByName(shopName);
        shopId = null!=shopDTO?shopDTO.getId():null;
      }

      IShopConfigService shopConfigService = ServiceManager.getService(IShopConfigService.class);

      List<ShopConfigDTO> shopConfigDTOs = shopConfigService.searchShopConfigDTOByShopAndScene(shopId,scene,startPageNo,maxRows);

      if(CollectionUtils.isNotEmpty(shopConfigDTOs))
      {
        for(ShopConfigDTO shopConfigDTO : shopConfigDTOs)
        {
          shopDTO = configService.getShopById(shopConfigDTO.getShopId());
          if(shopDTO==null){
            LOG.error("shopDTO[{}] is null!",shopConfigDTO.getShopId());
          }else{
            shopConfigDTO.setShopName(shopDTO.getName());
          }
        }
      }
      request.setAttribute("shopConfigDTOs",shopConfigDTOs);
      int totalRows = shopConfigService.countShopConfigByScene(shopId,scene);

      Pager pager = new Pager(totalRows, NumberUtil.intValue(String.valueOf(startPageNo), 1));
      jsonStr = JsonUtil.listToJson(shopConfigDTOs);
      jsonStr = jsonStr.substring(0, jsonStr.length() - 1);
      if (!"[".equals(jsonStr.trim())) {
        jsonStr = jsonStr + "," + pager.toJson().substring(1, pager.toJson().length());
      } else {
        jsonStr = pager.toJson();
      }

      out.write(jsonStr);
    }catch(Exception e){
      LOG.error(e.getMessage(),e);
    }finally {
      out.close();
    }

  }

  @RequestMapping(params = "method=changeConfigSwitch")
  public void changeConfigSwitch(HttpServletRequest request,HttpServletResponse response,Long shopId,ShopConfigScene scene,ShopConfigStatus status) throws Exception
  {
    IShopConfigService shopConfigService = ServiceManager.getService(IShopConfigService.class);
    PrintWriter out = response.getWriter();
    String jsonStr = "";
    try{
      ShopConfig shopConfig = shopConfigService.setShopConfig(shopId,scene,status);

      if(null != shopConfig && status == shopConfig.getStatus())
      {
        jsonStr = "success";
      }
      else
      {
        jsonStr = "error";
      }
      Map<String,String> map = new HashMap<String, String>();
      map.put("resu",jsonStr);

      request.setAttribute("shopConfig",shopConfig);

      out.print(JsonUtil.mapToJson(map));
    }catch (Exception e){
      LOG.error("method=changeConfigSwitch");
      LOG.error("jsonStr",jsonStr);
      LOG.error("scene",scene.getScene());
      LOG.error("status",status.getStatus());
      LOG.error(e.getMessage(),e);
    }finally {
      out.flush();
      out.close();
    }
  }

  @RequestMapping(params = "method = checkShop")
  public void checkShop(HttpServletRequest request,HttpServletResponse response,String shopName) throws Exception
  {
    IConfigService configService = ServiceManager.getService(IConfigService.class);

    PrintWriter out = response.getWriter();

    String jsonStr = "";
    try{
      ShopDTO shopDTO = configService.getShopByName(shopName);

      if(null == shopDTO)
      {
        jsonStr="error";
      }
      else
      {
        jsonStr=shopDTO.getId().toString();
      }

      Map<String,String> map = new HashMap<String, String>();

      request.setAttribute("jsonStr",jsonStr);

      map.put("resu",jsonStr);

      out.write(JsonUtil.mapToJson(map));

    }catch (Exception e){
      LOG.error(e.getMessage(),e);
    }finally {
      out.close();
    }
  }

  @RequestMapping(params = "method=addShopConfig")
  public String addShopConfig(HttpServletRequest request,HttpServletResponse response)
  {
    ShopConfigDTO shopConfigDTO = new ShopConfigDTO();
    request.setAttribute("shopConfigDTO",shopConfigDTO);
    request.setAttribute("scene", prepareShopConfigSceneMap());
    return "/shopIndividuation/addShopConfig";
  }

  @RequestMapping(params = "method=checkShopExistAndShopConfigExist")
  public void checkShopExistAndShopConfigExist(HttpServletRequest request,HttpServletResponse response,String shopName,ShopConfigScene scene) throws Exception
  {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    IShopConfigService shopConfigService = ServiceManager.getService(IShopConfigService.class);
    PrintWriter out = response.getWriter();

    String jsonStr = "";
    try{
      ShopDTO shopDTO = configService.getShopByName(shopName);

      if(null == shopDTO)
      {
        jsonStr="noShop";
      }
      else
      {
        List<ShopConfigDTO> shopConfigDTOs = shopConfigService.searchShopConfigDTOByShopAndScene(shopDTO.getId(),scene,1,10);

        if(CollectionUtils.isNotEmpty(shopConfigDTOs))
        {
          jsonStr="hasShopConfig";
        }
        else
        {
          jsonStr = shopDTO.getId().toString();
        }
      }

      request.setAttribute("jsonStr",jsonStr);

      Map<String,String> map = new HashMap<String, String>();

      map.put("resu",jsonStr);

      out.write(JsonUtil.mapToJson(map));

    }catch (Exception e){
      LOG.error(e.getMessage(),e);
    }finally {
      out.close();
    }
  }

  @RequestMapping(params = "method=saveShopConfig")
  public void saveShopConfig(HttpServletRequest request,HttpServletResponse response,ShopConfigDTO shopConfigDTO) throws Exception
  {
    IShopConfigService shopConfigService = ServiceManager.getService(IShopConfigService.class);
    PrintWriter out = response.getWriter();
    String jsonStr = "";

    try{
      ShopConfig shopConfig = shopConfigService.setShopConfig(shopConfigDTO.getShopId(),shopConfigDTO.getScene(),shopConfigDTO.getStatus());

      if(null == shopConfig.getId())
      {
        jsonStr = "error";
      }
      else
      {
        jsonStr = "success";
      }

      Map<String,String> map = new HashMap<String, String>();

      request.setAttribute("shopConfig",shopConfig);

      map.put("resu",jsonStr);

      out.write(JsonUtil.mapToJson(map));
    }catch (Exception e){
      e.printStackTrace();
      LOG.error(e.getMessage(),e);
    }finally {
      out.close();
    }
  }
}
