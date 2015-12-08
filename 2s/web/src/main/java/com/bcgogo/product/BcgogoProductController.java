package com.bcgogo.product;

import com.bcgogo.common.Result;
import com.bcgogo.common.WebUtil;
import com.bcgogo.config.dto.image.DataImageRelationDTO;
import com.bcgogo.config.dto.image.ImageInfoDTO;
import com.bcgogo.config.service.image.IImageService;
import com.bcgogo.enums.Product.BcgogoProductScene;
import com.bcgogo.enums.config.DataType;
import com.bcgogo.enums.config.ImageScene;
import com.bcgogo.enums.config.ImageType;
import com.bcgogo.enums.txn.finance.PaymentType;
import com.bcgogo.product.service.IBcgogoProductService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.txn.dto.finance.BcgogoReceivableSearchCondition;
import com.bcgogo.txn.service.finance.IBcgogoReceivableService;
import com.bcgogo.user.dto.permission.ShopVersionDTO;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@RequestMapping("/bcgogoProduct.do")
public class BcgogoProductController {
  private static final Logger LOG = LoggerFactory.getLogger(BcgogoProductController.class);

  @RequestMapping(params = "method=bcgogoProductDetail")
  public String bcgogoProductDetail(ModelMap modelMap, HttpServletRequest request,Long bcgogoProductId) {
    Long shopId = null;
    IBcgogoProductService bcgogoProductService = ServiceManager.getService(IBcgogoProductService.class);
    IImageService imageService = ServiceManager.getService(IImageService.class);
    try {
      shopId = WebUtil.getShopId(request);
      ShopVersionDTO shopVersionDTO = WebUtil.getShopVersion(request);
      List<BcgogoProductDTO> bcgogoProductDTOList = bcgogoProductService.getBcgogoProductDTOByPaymentType(PaymentType.HARDWARE,false);
      Iterator<BcgogoProductDTO> iterator = bcgogoProductDTOList.iterator();
      BcgogoProductDTO bcgogoProductDTO = null;
      Map<Long,BcgogoProductDTO> bcgogoProductDTOMap = new HashMap<Long, BcgogoProductDTO>();
      while (iterator.hasNext()){
        bcgogoProductDTO = iterator.next();
        if(bcgogoProductDTO.getShowToShopVersions().indexOf(shopVersionDTO.getId().toString())>-1){
          bcgogoProductDTOMap.put(bcgogoProductDTO.getId(),bcgogoProductDTO);
        }else{
          iterator.remove();
        }
      }

      bcgogoProductDTO =  bcgogoProductDTOMap.get(bcgogoProductId);
      modelMap.addAttribute("bcgogoProductDTO",bcgogoProductDTO);
      if(bcgogoProductDTO!=null && bcgogoProductDTO.getId().equals(100000003l)){
        BcgogoProductDTO attachedBcgogoProductDTO = bcgogoProductDTOMap.get(100000001l);
        for(BcgogoProductPropertyDTO productPropertyDTO:attachedBcgogoProductDTO.getPropertyDTOList()){
          if(productPropertyDTO.getId().equals(100000001l)){
            modelMap.addAttribute("attachedBcgogoProductPropertyDTO", productPropertyDTO);
          }
        }
        modelMap.addAttribute("attachedBcgogoProductDTO", attachedBcgogoProductDTO);
      }

      List<ImageScene> imageSceneList = new ArrayList<ImageScene>();
      imageSceneList.add(ImageScene.BCGOGO_PRODUCT_LIST_IMAGE_SMALL);
      imageSceneList.add(ImageScene.BCGOGO_PRODUCT_INFO_IMAGE_SMALL);
      imageSceneList.add(ImageScene.BCGOGO_PRODUCT_INFO_IMAGE_BIG);
      imageService.addImageToBcgogoProductDTO(imageSceneList,bcgogoProductDTOList.toArray(new BcgogoProductDTO[bcgogoProductDTOList.size()]));

      modelMap.addAttribute("bcgogoProductDTOList", bcgogoProductDTOList);

    } catch (Exception e) {
      LOG.error("bcgogoProduct.do?method=bcgogoProductDetail,shopId:" + shopId);
      LOG.error(e.getMessage(), e);
    }
    return "/autoaccessoryonline/payOnline/bcgogoProduct";
  }

  @RequestMapping (params = "method=saveProductImageRelation")
  @ResponseBody
  public Object saveProductImageRelation(HttpServletRequest request,Long productLocalInfoId,String imagePath){
    Long shopId = WebUtil.getShopId(request);
    IImageService imageService = ServiceManager.getService(IImageService.class);
    Result result = new Result();
    try{
      if(shopId==null) throw new Exception("shopId is null!");
      if(productLocalInfoId==null) throw new Exception("productLocalInfoId is null!");
      if(StringUtils.isBlank(imagePath)) throw new Exception("imagePath is null!");
      DataImageRelationDTO dataImageRelationDTO = new DataImageRelationDTO(shopId, productLocalInfoId, DataType.PRODUCT, ImageType.PRODUCT_MAIN_IMAGE,0);
      dataImageRelationDTO.setImageInfoDTO(new ImageInfoDTO(shopId,imagePath));
      Set<ImageType> imageTypeSet = new HashSet<ImageType>();
      imageTypeSet.add(ImageType.PRODUCT_MAIN_IMAGE);
      imageService.saveOrUpdateDataImageDTOs(shopId,imageTypeSet,DataType.PRODUCT,productLocalInfoId,dataImageRelationDTO);
      result.setData(dataImageRelationDTO);
      return result;
    } catch (Exception e){
      LOG.error("product.do?method=saveProductImageRelation,shopId:{}," + e.getMessage(), new Object[]{shopId, e});
      result.setSuccess(false);
      return result;
    }
  }
}
